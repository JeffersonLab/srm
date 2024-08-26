package org.jlab.srm.presentation.controller.reports;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.params.AllActivityParams;
import org.jlab.srm.business.service.AllActivityService;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.model.AllActivityRecord;
import org.jlab.srm.presentation.params.AllActivityUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "All Activity",
    urlPatterns = {"/reports/all-activity"})
public class AllActivity extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB RegionFacade regionFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB ComponentSignoffFacade reportFacade;
  @EJB StatusFacade statusFacade;
  @EJB SignoffActivityFacade signoffActivityFacade;
  @EJB ComponentTreeFacade treeFacade;

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
    List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective("weight"));

    List<BeamDestination> defaultDestinationList =
        destinationFacade.filterTargetList(destinationList);

    BigInteger[] defaultDestinationIdArray = destinationFacade.toIdArray(defaultDestinationList);

    AllActivityUrlParamHandler paramHandler =
        new AllActivityUrlParamHandler(
            request,
            defaultDestinationIdArray,
            destinationFacade,
            categoryFacade,
            systemFacade,
            regionFacade,
            groupFacade,
            statusFacade);

    AllActivityParams params;

    if (paramHandler.qualified()) {
      params = paramHandler.convert();
      paramHandler.validate(params);
      paramHandler.store(params);
    } else {
      params = paramHandler.materialize();
      paramHandler.redirect(response, params);
      return;
    }

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);
    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
    List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));
    List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));

    BigInteger[] systemIdArray =
        treeFacade.getSystemIdArray(params.getCategoryId(), params.getSystemId());
    params.setSystemIdArray(systemIdArray);

    AllActivityService activityService = new AllActivityService();

    List<AllActivityRecord> activityList;
    Long totalRecords;

    try {
      activityList = activityService.filterList(params, offset, maxPerPage);
      totalRecords = activityService.count(params);
    } catch (SQLException e) {
      throw new ServletException("Unable to query for recent activity", e);
    }

    statusList.remove(Status.NOT_APPLICABLE);
    statusList.remove(Status.MASKED);
    statusList.remove(Status.MASKED_CC);
    statusList.remove(Status.MASKED_ADMIN);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Activity ";

    String filters = paramHandler.message(params);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    if (paginator.getTotalRecords() < maxPerPage && offset == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("statusList", statusList);
    request.setAttribute("activityList", activityList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("groupList", groupList);
    request.setAttribute("paginator", paginator);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/all-activity.jsp")
        .forward(request, response);
  }
}
