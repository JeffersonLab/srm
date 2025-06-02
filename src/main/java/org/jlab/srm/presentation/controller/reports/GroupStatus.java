package org.jlab.srm.presentation.controller.reports;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.srm.business.params.GroupStatusParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.ComponentSignoffFacade.GroupStatusCount;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.params.GroupStatusUrlParamHandler;
import org.jlab.srm.presentation.util.FilterSelectionMessage;

/**
 * @author ryans
 */
@WebServlet(
    name = "Group Status",
    urlPatterns = {"/reports/group-status"})
public class GroupStatus extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;
  @EJB SystemFacade systemFacade;
  @EJB RegionFacade regionFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB ComponentSignoffFacade reportFacade;
  @EJB ComponentFacade componentFacade;
  @EJB CategoryFacade categoryFacade;

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

    GroupStatusUrlParamHandler paramHandler =
        new GroupStatusUrlParamHandler(request, defaultDestinationIdArray);

    GroupStatusParams params;

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

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);

    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));

    List<BeamDestination> selectedDestinationList = null;
    Category selectedCategory = null;
    SystemEntity selectedSystem = null;
    Region selectedRegion = null;

    List<GroupStatusCount> groupStatusList =
        reportFacade.getGroupStatusCount(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId());
    Long maskedCount =
        componentFacade.countMasked(
            params.getDestinationIdArray(),
            null,
            params.getSystemId(),
            params.getRegionId(),
            null,
            null,
            null);

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    if (params.getDestinationIdArray() != null) {
      selectedDestinationList = destinationFacade.findMultiple(params.getDestinationIdArray());
    }

    if (params.getCategoryId() != null) {
      selectedCategory = categoryFacade.find(params.getCategoryId());
    }

    if (params.getSystemId() != null) {
      selectedSystem = systemFacade.find(params.getSystemId());
    }

    if (params.getRegionId() != null) {
      selectedRegion = regionFacade.find(params.getRegionId());
    }

    int totalCount = 0;
    for (GroupStatusCount gsc : groupStatusList) {
      totalCount = totalCount + gsc.getTotalCount();
    }

    List<Map.Entry<String, Object>> footnoteList =
        FilterSelectionMessage.getOverallStatusFootnoteList(
            selectedDestinationList, selectedCategory, selectedSystem, selectedRegion, null);

    if ("table".equals(params.getChart())) {
      request.setAttribute(
          "selectionMessage",
          FilterSelectionMessage.getActivityScreenMessage(
              selectedDestinationList,
              selectedCategory,
              selectedSystem,
              selectedRegion,
              null,
              null,
              null,
              null,
              null,
              null,
              null));
    }

    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("footnoteList", footnoteList);
    request.setAttribute("totalCount", totalCount);
    request.setAttribute("maskedCount", maskedCount);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("groupStatusList", groupStatusList);
    request.setAttribute("now", new Date());

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/group-status.jsp")
        .forward(request, response);
  }
}
