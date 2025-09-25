package org.jlab.srm.presentation.controller.reports;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.params.GroupResponsibilityReportParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.params.GroupResponsibilityReportUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "GroupResponsibilityReport",
    urlPatterns = {"/reports/group-responsibility"})
public class GroupResponsibilityReport extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB GroupResponsibilityFacade groupResponsibilityFacade;

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
    List<BeamDestination> destinationList =
        destinationFacade.findAll(new AbstractFacade.OrderDirective("weight"));

    List<BeamDestination> defaultDestinationList =
        destinationFacade.filterTargetList(destinationList);

    BigInteger[] defaultDestinationIdArray = destinationFacade.toIdArray(defaultDestinationList);

    GroupResponsibilityReportUrlParamHandler paramHandler =
        new GroupResponsibilityReportUrlParamHandler(
            request,
            defaultDestinationIdArray,
            destinationFacade,
            categoryFacade,
            systemFacade,
            groupFacade);

    GroupResponsibilityReportParams params;

    if (paramHandler.qualified()) {
      try {
        params = paramHandler.convert();
        paramHandler.validate(params);
        paramHandler.store(params);
      } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
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
    List<ResponsibleGroup> groupList = groupFacade.filterList(null, 0, Integer.MAX_VALUE);
    List<GroupResponsibility> groupResponsibilityList =
        groupResponsibilityFacade.filterList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getGroupId(),
            params.isChecklistRequired(),
            params.isChecklistMissing(),
            offset,
            maxPerPage);
    Long totalRecords =
        groupResponsibilityFacade.countFilterList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getGroupId(),
            params.isChecklistRequired(),
            params.isChecklistMissing());

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Responsibilities ";

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
    request.setAttribute("groupResponsibilityList", groupResponsibilityList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("groupList", groupList);
    request.setAttribute("paginator", paginator);
    request.setAttribute("now", new Date());

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/group-responsibility.jsp")
        .forward(request, response);
  }
}
