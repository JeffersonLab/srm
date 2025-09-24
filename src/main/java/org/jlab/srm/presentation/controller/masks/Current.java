package org.jlab.srm.presentation.controller.masks;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.params.CurrentMasksParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.params.CurrentMasksUrlParamHandler;
import org.jlab.srm.presentation.util.FilterSelectionMessage;

/**
 * @author ryans
 */
@WebServlet(
    name = "CurrentMasks",
    urlPatterns = {"/masks/current"})
public class Current extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB ComponentFacade componentFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB RegionFacade regionFacade;
  @EJB StatusFacade statusFacade;

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

    CurrentMasksUrlParamHandler paramHandler =
        new CurrentMasksUrlParamHandler(request, defaultDestinationIdArray);

    CurrentMasksParams params;

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

    Category categoryRoot = categoryFacade.findBranch(null, null);
    List<SystemEntity> systemList = systemFacade.findAllWithCategory();
    List<SystemEntity> systemListFiltered =
        systemFacade.findWithCategory(params.getCategoryId(), null, null, null, true, false);
    List<Component> componentList =
        componentFacade.filterList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            BigInteger.ONE,
            null,
            true,
            params.isUnpowered(),
            params.getComponentName(),
            params.getStatusIdArray(),
            true,
            offset,
            maxPerPage);
    Long totalRecords =
        componentFacade.countFilterList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            BigInteger.ONE,
            null,
            true,
            params.isUnpowered(),
            params.getComponentName(),
            params.getStatusIdArray());
    List<ResponsibleGroup> groupList = groupFacade.filterList(null, 0, Integer.MAX_VALUE);
    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
    List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    List<BeamDestination> selectedDestinationList = null;
    Category selectedCategory = null;
    SystemEntity selectedSystem = null;
    Region selectedRegion = null;
    ResponsibleGroup selectedGroup = null;
    List<Status> selectedStatusList = null;

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

    if (params.getStatusIdArray() != null) {
      selectedStatusList = statusFacade.findMultiple(params.getStatusIdArray());
    }

    statusList.remove(Status.NOT_APPLICABLE);
    statusList.remove(Status.NOT_READY);
    statusList.remove(Status.CHECKED);
    statusList.remove(Status.READY);

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Components ";

    String filters =
        FilterSelectionMessage.getMessage(
            selectedDestinationList,
            selectedCategory,
            selectedSystem,
            selectedRegion,
            selectedGroup,
            selectedStatusList,
            null,
            params.isUnpowered(),
            params.getComponentName(),
            null,
            null);

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

    boolean editable = componentFacade.isAdminOrCrewChief();

    request.setAttribute("editable", editable);
    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("statusList", statusList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("systemListFiltered", systemListFiltered);
    request.setAttribute("componentList", componentList);
    request.setAttribute("paginator", paginator);
    request.setAttribute("groupList", groupList);
    request.setAttribute("regionList", regionList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/masks/current.jsp")
        .forward(request, response);
  }
}
