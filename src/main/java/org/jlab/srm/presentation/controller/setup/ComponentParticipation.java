package org.jlab.srm.presentation.controller.setup;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.util.FilterSelectionMessage;

/**
 * @author ryans
 */
@WebServlet(
    name = "ComponentParticipation",
    urlPatterns = {"/setup/component-participation"})
public class ComponentParticipation extends HttpServlet {

  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB ComponentFacade componentFacade;
  @EJB BeamDestinationFacade destinationFacade;
  @EJB RegionFacade regionFacade;

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
    BigInteger destinationId = ParamConverter.convertBigInteger(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    String componentName = request.getParameter("componentName");
    BigInteger[] colDestinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "colDestinationId");

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = ParamUtil.convertAndValidateNonNegativeInt(request, "max", 10);

    Category categoryRoot = categoryFacade.findBranch(null, null);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(categoryId, null, null, null, true, false);

    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));

    BeamDestination selectedDestination = null;
    Category selectedCategory = null;
    SystemEntity selectedSystem = null;
    Region selectedRegion = null;
    List<BeamDestination> columnList = new ArrayList<>();

    List<Component> componentList =
        componentFacade.findWithDestinations(
            destinationId, categoryId, systemId, regionId, componentName, offset, maxPerPage);
    Long totalRecords =
        componentFacade.countFilterList(
            new BigInteger[] {destinationId},
            categoryId,
            systemId,
            regionId,
            null,
            null,
            null,
            null,
            null,
            componentName,
            null);

    if (destinationId != null) {
      selectedDestination = destinationFacade.find(destinationId);
    }

    if (categoryId != null) {
      selectedCategory = categoryFacade.find(categoryId);
    }

    if (systemId != null) {
      selectedSystem = systemFacade.find(systemId);
    }

    if (regionId != null) {
      selectedRegion = regionFacade.find(regionId);
    }

    if (colDestinationIdArray != null) {
      for (BigInteger id : colDestinationIdArray) {
        BeamDestination dest = destinationFacade.find(id);
        if (dest != null) {
          columnList.add(dest);
        }
      }
    }

    List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective("weight"));

    List<BeamDestination> filteredDestinationList =
        destinationFacade.getFilteredDestinationList(colDestinationIdArray);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Components ";

    List<BeamDestination> selectedDestinationList = null;

    if (selectedDestination != null) {
      selectedDestinationList = List.of(selectedDestination);
    }

    String filters =
        FilterSelectionMessage.getMessage(
            selectedDestinationList,
            selectedCategory,
            selectedSystem,
            selectedRegion,
            null,
            null,
            null,
            null,
            componentName,
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

    if (!columnList.isEmpty()) {
      selectionMessage = selectionMessage + " <Filtered Columns>";
    }

    request.setAttribute("paginator", paginator);
    request.setAttribute("selectionMessage", selectionMessage);
    // request.setAttribute("selectedSystem", selectedSystem);
    // request.setAttribute("selectedRegion", selectedRegion);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("componentList", componentList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("filteredDestinationList", filteredDestinationList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/component-participation.jsp")
        .forward(request, response);
  }
}
