package org.jlab.srm.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.filter.AuditContext;
import org.jlab.srm.business.params.ReadinessParams;
import org.jlab.srm.business.params.RecentActivityParams;
import org.jlab.srm.business.service.RecentActivityService;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.model.SignoffActivityCompressedRecord;
import org.jlab.srm.presentation.params.ReadinessUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "Readiness",
    urlPatterns = {"/readiness"})
public class Readiness extends HttpServlet {

  @EJB ComponentTreeFacade treeFacade;
  @EJB BeamDestinationFacade destinationFacade;
  @EJB RegionFacade regionFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB StatusFacade statusFacade;
  @EJB ComponentFacade componentFacade;
  @EJB SignoffActivityFacade signoffActivityFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;

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
    ReadinessUrlParamHandler paramHandler =
        new ReadinessUrlParamHandler(
            request,
            destinationFacade,
            categoryFacade,
            systemFacade,
            regionFacade,
            groupFacade,
            statusFacade);

    ReadinessParams params;

    if (paramHandler.qualified()) {
      params = paramHandler.convert();
      paramHandler.validate(params);
      paramHandler.store(params);
    } else {
      params = paramHandler.materialize();
      paramHandler.redirect(response, params);
      return;
    }

    // Just to ensure people won't see expired masks
    AuditContext ctx = AuditContext.getCurrentInstance();
    ctx.putExtra("effectiveRole", "srm-admin");
    try {
      componentFacade.expireMasks();
    } finally {
      ctx.putExtra("effectiveRole", null);
    }

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);

    List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective("weight"));
    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
    List<ResponsibleGroup> groupList = groupFacade.filterList(null, 0, Integer.MAX_VALUE);
    List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));
    Long maskedCount =
        componentFacade.countMasked(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            null,
            params.getStatusIdArray());
    Long componentCount =
        componentFacade.countFilterList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            BigInteger.ONE,
            null,
            null,
            null,
            null,
            params.getStatusIdArray());

    RecentActivityParams recentParams =
        new RecentActivityParams(
            IOUtil.removeNullValues(params.getDestinationIdArray(), BigInteger.class),
            treeFacade.getSystemIdArray(params.getCategoryId(), params.getSystemId()),
            params.getRegionId(),
            params.getGroupId(),
            params.getStatusIdArray());
    RecentActivityService recentService = new RecentActivityService();

    List<SignoffActivityCompressedRecord> signoffActivityList;

    try {
      signoffActivityList = recentService.filterListCompressedFast(recentParams, 0, 5);
    } catch (SQLException e) {
      throw new ServletException("Unable to query for recent activity", e);
    }

    // Signoff is different than filter list (N/A is okay for signoff).
    List<Status> signoffStatusList = new ArrayList<>(statusList);

    signoffStatusList.remove(Status.MASKED);
    signoffStatusList.remove(Status.MASKED_CC);
    signoffStatusList.remove(Status.MASKED_ADMIN);

    statusList.remove(Status.NOT_APPLICABLE);

    String targetCsv = destinationFacade.filterTargetCsv(destinationList);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Components ";

    String filters = paramHandler.message(params);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    selectionMessage = selectionMessage + " {" + formatter.format(componentCount) + "}";

    request.setAttribute("signoffActivityList", signoffActivityList);
    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("componentCount", componentCount);
    request.setAttribute("maskedCount", maskedCount);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("groupList", groupList);
    request.setAttribute("statusList", statusList);
    request.setAttribute("signoffStatusList", signoffStatusList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/readiness.jsp")
        .forward(request, response);
  }
}
