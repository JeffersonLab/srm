package org.jlab.srm.presentation.controller.reports;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.srm.business.params.OverallStatusParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.ComponentSignoffFacade.StatusCount;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.params.OverallStatusUrlParamHandler;
import org.jlab.srm.presentation.util.FilterSelectionMessage;

/**
 * @author ryans
 */
@WebServlet(
    name = "Overall Status",
    urlPatterns = {"/reports/overall-status"})
public class OverallStatus extends HttpServlet {

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

    OverallStatusUrlParamHandler paramHandler =
        new OverallStatusUrlParamHandler(request, defaultDestinationIdArray);

    OverallStatusParams params;

    if (paramHandler.qualified()) {
      params = paramHandler.convert();
      paramHandler.validate(params);
      paramHandler.store(params);
    } else {
      params = paramHandler.materialize();
      paramHandler.redirect(response, params);
      return;
    }

    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
    List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);

    List<BeamDestination> selectedDestinationList = null;
    Category selectedCategory = null;
    SystemEntity selectedSystem = null;
    Region selectedRegion = null;
    ResponsibleGroup selectedGroup = null;

    List<StatusCount> statusCountList =
        reportFacade.getStatusCount(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId());
    Long maskedCount =
        componentFacade.countMasked(
            params.getDestinationIdArray(),
            null,
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
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

    if (params.getGroupId() != null) {
      selectedGroup = groupFacade.find(params.getGroupId());
    }

    int readyCount = 0;
    int checkedCount = 0;
    int notReadyCount = 0;

    for (StatusCount sc : statusCountList) {
      switch (sc.getStatus().getStatusId().intValue()) {
        case 1:
          readyCount = sc.getCount();
          break;
        case 50:
          checkedCount = sc.getCount();
          break;
        case 100:
          notReadyCount = sc.getCount();
          break;
      }
    }

    /*Put in records with zero counts if there are any*/
    statusCountList = new ArrayList<>();
    statusCountList.add(new StatusCount(Status.FROM_ID(BigInteger.valueOf(1L)), readyCount));
    statusCountList.add(new StatusCount(Status.FROM_ID(BigInteger.valueOf(50L)), checkedCount));
    statusCountList.add(new StatusCount(Status.FROM_ID(BigInteger.valueOf(100L)), notReadyCount));

    int totalCount = readyCount + checkedCount + notReadyCount;

    List<Map.Entry<String, Object>> footnoteList =
        FilterSelectionMessage.getOverallStatusFootnoteList(
            selectedDestinationList,
            selectedCategory,
            selectedSystem,
            selectedRegion,
            selectedGroup);

    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("footnoteList", footnoteList);
    request.setAttribute("maskedCount", maskedCount);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("groupList", groupList);
    request.setAttribute("statusCountList", statusCountList);
    request.setAttribute("readyCount", readyCount);
    request.setAttribute("checkedCount", checkedCount);
    request.setAttribute("notReadyCount", notReadyCount);
    request.setAttribute("totalCount", totalCount);
    request.setAttribute("now", new Date());

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/overall-status.jsp")
        .forward(request, response);
  }
}
