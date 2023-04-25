package org.jlab.hco.presentation.controller;

import org.jlab.hco.business.params.AllActivityParams;
import org.jlab.hco.business.service.AllActivityService;
import org.jlab.hco.business.session.BeamDestinationFacade;
import org.jlab.hco.business.session.ComponentSignoffFacade;
import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.business.session.SignoffActivityFacade;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
import org.jlab.hco.persistence.enumeration.AllChangeType;
import org.jlab.hco.persistence.model.AllActivityRecord;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "GroupDailyEmail", urlPatterns = {"/group-daily-email"})
public class GroupDailyEmail extends HttpServlet {

    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    ComponentSignoffFacade signoffFacade;
    @EJB
    SignoffActivityFacade activityFacade;
    @EJB
    BeamDestinationFacade destinationFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
        //Date now = new Date();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 7);
        Date today = c.getTime();
        c.add(Calendar.DATE, -7);
        Date sevenDaysAgo = c.getTime();
        c.add(Calendar.DATE, 4);
        Date threeDaysAgo = c.getTime();
        c.add(Calendar.DATE, 2);
        Date oneDayAgo = c.getTime();

        Date start = oneDayAgo;
        Date end = today;

        AllActivityService activityService = new AllActivityService();

        List<AllActivityRecord> activityList;
        Long totalRecords;

        AllActivityParams params = new AllActivityParams();

        params.setStart(start);
        params.setEnd(end);

        params.setGroupId(groupId);

        params.setStatusIdArray(new BigInteger[]{BigInteger.valueOf(50), BigInteger.valueOf(100)});
        params.setCurrentSignoffStatusIdArray(new BigInteger[]{BigInteger.valueOf(50), BigInteger.valueOf(100)});

        params.setChangeArray(new AllChangeType[]{
                AllChangeType.UPGRADE,
                AllChangeType.DOWNGRADE,
                AllChangeType.CASCADE,
                AllChangeType.COMMENT});

        int offset = 0;
        int maxPerPage = 50;

        try {
            activityList = activityService.filterList(params, offset, maxPerPage);
            totalRecords = activityService.count(params);
        } catch (SQLException e) {
            throw new ServletException("Unable to query for recent activity", e);
        }

        boolean hasMoreActivity = totalRecords > activityList.size();

        String willNotBeSentMessage = null;

        if (activityList.isEmpty()) {
            willNotBeSentMessage = "There are no new action items";
        }

        request.setAttribute("start", params.getStart());
        request.setAttribute("end", params.getEnd());
        request.setAttribute("willNotBeSentMessage", willNotBeSentMessage);
        request.setAttribute("group", group);
        request.setAttribute("dateRange", TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd()));
        request.setAttribute("totalCount", totalRecords);
        request.setAttribute("activityList", activityList);
        request.setAttribute("hasMoreActivity", hasMoreActivity);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/group-daily-email.jsp").forward(request, response);
    }
}
