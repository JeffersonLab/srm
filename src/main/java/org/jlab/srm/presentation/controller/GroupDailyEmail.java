package org.jlab.srm.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.params.AllActivityParams;
import org.jlab.srm.business.service.AllActivityService;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.BeamDestinationFacade;
import org.jlab.srm.business.session.ComponentSignoffFacade;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.business.session.SignoffActivityFacade;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.enumeration.AllChangeType;
import org.jlab.srm.persistence.model.AllActivityRecord;

/**
 * @author ryans
 */
@WebServlet(
    name = "GroupDailyEmail",
    urlPatterns = {"/group-daily-email"})
public class GroupDailyEmail extends HttpServlet {

  @EJB ResponsibleGroupFacade groupFacade;
  @EJB ComponentSignoffFacade signoffFacade;
  @EJB SignoffActivityFacade activityFacade;
  @EJB BeamDestinationFacade destinationFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

    if (groupId == null) {
      throw new ServletException("groupId is required");
    }

    ResponsibleGroup group = groupFacade.find(groupId);

    Calendar c = Calendar.getInstance();
    c.set(Calendar.MILLISECOND, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.HOUR_OF_DAY, 7);
    Date today = c.getTime();
    c.add(Calendar.DATE, -1);
    Date oneDayAgo = c.getTime();

    Date start;
    Date end;

    try {
      start = ParamConverter.convertFriendlyDateTime(request, "start");
      end = ParamConverter.convertFriendlyDateTime(request, "end");
    } catch (UserFriendlyException e) {
      throw new ServletException("Unable to parse date", e);
    }

    if (start == null) {
      start = oneDayAgo;
    }

    if (end == null) {
      end = today;
    }

    List<BeamDestination> currentRunList =
        destinationFacade.filterTargetList(destinationFacade.findAll(new OrderDirective("weight")));

    AllActivityService activityService = new AllActivityService();

    List<AllActivityRecord> activityList;
    Long totalRecords;

    AllActivityParams params = new AllActivityParams();

    params.setStart(start);
    params.setEnd(end);

    params.setGroupId(groupId);

    params.setDestinationIdArray(destinationFacade.toIdArray(currentRunList));

    params.setStatusIdArray(new BigInteger[] {BigInteger.valueOf(50), BigInteger.valueOf(100)});
    params.setCurrentSignoffStatusIdArray(
        new BigInteger[] {BigInteger.valueOf(50), BigInteger.valueOf(100)});

    params.setChangeArray(
        new AllChangeType[] {
          AllChangeType.UPGRADE,
          AllChangeType.DOWNGRADE,
          AllChangeType.CASCADE,
          AllChangeType.COMMENT
        });

    int offset = 0;
    int maxPerPage = 50;

    String willNotBeSentMessage = null;

    if (currentRunList.isEmpty()) {
      // An empty destination filter would match every component, so check for no current run
      activityList = new ArrayList<>();
      totalRecords = 0L;
      willNotBeSentMessage = "No beam destination is in the current run";
    } else {
      try {
        activityList = activityService.filterList(params, offset, maxPerPage);
        totalRecords = activityService.count(params);
      } catch (SQLException e) {
        throw new ServletException("Unable to query for recent activity", e);
      }

      if (activityList.isEmpty()) {
        willNotBeSentMessage = "There are no new action items";
      }
    }

    boolean hasMoreActivity = totalRecords > activityList.size();

    request.setAttribute("start", params.getStart());
    request.setAttribute("end", params.getEnd());
    request.setAttribute("willNotBeSentMessage", willNotBeSentMessage);
    request.setAttribute("group", group);
    request.setAttribute("currentRunList", currentRunList);
    request.setAttribute(
        "dateRange", TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd()));
    request.setAttribute("totalCount", totalRecords);
    request.setAttribute("activityList", activityList);
    request.setAttribute("hasMoreActivity", hasMoreActivity);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/group-daily-email.jsp")
        .forward(request, response);
  }
}
