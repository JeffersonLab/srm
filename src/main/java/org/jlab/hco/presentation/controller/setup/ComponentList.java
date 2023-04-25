package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.params.ComponentSetupParams;
import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.*;
import org.jlab.hco.persistence.entity.*;
import org.jlab.hco.presentation.params.ComponentSetupUrlParamHandler;
import org.jlab.hco.presentation.util.FilterSelectionMessage;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "SetupComponentList", urlPatterns = {"/setup/component-list"})
public class ComponentList extends HttpServlet {

    @EJB
    BeamDestinationFacade destinationFacade;
    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    ComponentFacade componentFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    RegionFacade regionFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ComponentSetupUrlParamHandler paramHandler = new ComponentSetupUrlParamHandler(request);

        ComponentSetupParams params;

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

        List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective(
                "weight"));
        Category categoryRoot = categoryFacade.findBranch(null, null);
        List<SystemEntity> systemList = systemFacade.findAllWithCategory();
        List<SystemEntity> systemListFiltered
                = systemFacade.findWithCategory(params.getCategoryId(), null,
                null,
                null, true, false);
        List<Component> componentList = componentFacade.filterList(new BigInteger[]{
                        params.getDestinationId()}, params.getCategoryId(), params.getSystemId(),
                params.getRegionId(), params.getGroupId(), null, params.getSource(),
                params.isMasked(), params.isUnpowered(), params.getComponentName(), null,
                false, offset, maxPerPage);
        Long totalRecords = componentFacade.countFilterList(new BigInteger[]{
                        params.getDestinationId()}, params.getCategoryId(), params.getSystemId(),
                params.getRegionId(), params.getGroupId(), null, params.getSource(),
                params.isMasked(), params.isUnpowered(), params.getComponentName(), null);
        List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));
        List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));

        Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

        BeamDestination selectedDestination = null;
        Category selectedCategory = null;
        SystemEntity selectedSystem = null;
        Region selectedRegion = null;
        ResponsibleGroup selectedGroup = null;

        if (params.getDestinationId() != null) {
            selectedDestination = destinationFacade.find(params.getDestinationId());
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

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Components ";

        List<BeamDestination> selectedDestinationList = null;

        if (selectedDestination != null) {
            selectedDestinationList = Arrays.asList(selectedDestination);
        }

        String filters
                = FilterSelectionMessage.getMessage(selectedDestinationList,
                selectedCategory,
                selectedSystem,
                selectedRegion, selectedGroup, null, params.isMasked(),
                params.isUnpowered(), params.getComponentName(), null, params.getSource());

        if (filters.length() > 0) {
            selectionMessage = filters;
        }

        if (paginator.getTotalRecords() < maxPerPage && offset == 0) {
            selectionMessage = selectionMessage + " {" + formatter.format(
                    paginator.getTotalRecords()) + "}";
        } else {
            selectionMessage = selectionMessage + " {"
                    + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords()) + "}";
        }

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("destinationList", destinationList);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("systemList", systemList);
        request.setAttribute("systemListFiltered", systemListFiltered);
        request.setAttribute("componentList", componentList);
        request.setAttribute("paginator", paginator);
        request.setAttribute("groupList", groupList);
        request.setAttribute("regionList", regionList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/component-list.jsp").forward(request, response);
    }
}
