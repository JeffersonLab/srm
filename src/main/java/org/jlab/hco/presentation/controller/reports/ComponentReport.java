package org.jlab.hco.presentation.controller.reports;

import org.jlab.hco.business.params.ComponentReportParams;
import org.jlab.hco.business.session.*;
import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.persistence.entity.*;
import org.jlab.hco.persistence.entity.view.ComponentStatusRecord;
import org.jlab.hco.presentation.params.ComponentReportUrlParamHandler;
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
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "Component Report", urlPatterns = {"/reports/component"})
public class ComponentReport extends HttpServlet {

    @EJB
    BeamDestinationFacade destinationFacade;
    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    RegionFacade regionFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    ComponentSignoffFacade reportFacade;
    @EJB
    StatusFacade statusFacade;
    @EJB
    ComponentStatusFacade componentStatusFacade;

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
        List<BeamDestination> destinationList = destinationFacade.findAll(
                new AbstractFacade.OrderDirective(
                        "weight"));

        List<BeamDestination> defaultDestinationList = destinationFacade.filterTargetList(
                destinationList);

        BigInteger[] defaultDestinationIdArray = destinationFacade.toIdArray(defaultDestinationList);

        ComponentReportUrlParamHandler paramHandler = new ComponentReportUrlParamHandler(request,
                defaultDestinationIdArray, destinationFacade, categoryFacade, systemFacade,
                regionFacade, groupFacade);

        ComponentReportParams params;

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
        List<SystemEntity> systemList = systemFacade.findWithCategory(params.getCategoryId(), null,
                null,
                BigInteger.ONE, true, false);
        List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
        List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));
        List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));
        List<ComponentStatusRecord> componentStatusList = componentStatusFacade.filterList(
                params.getDestinationIdArray(), params.getCategoryId(), params.getSystemId(),
                params.getRegionId(),
                params.getGroupId(), params.getStatusId(), params.getUnpowered(), params.getComponentName(), offset,
                maxPerPage);
        Long totalRecords = componentStatusFacade.countFilterList(params.getDestinationIdArray(),
                params.getCategoryId(), params.getSystemId(), params.getRegionId(),
                params.getGroupId(), params.getStatusId(), params.getUnpowered(), params.getComponentName());

        statusList.remove(Status.NOT_APPLICABLE);

        String targetCsv = destinationFacade.toCsv(defaultDestinationList);

        Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Components ";

        String filters = paramHandler.message(params);

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

        request.setAttribute("targetCsv", targetCsv);
        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("statusList", statusList);
        request.setAttribute("componentStatusList", componentStatusList);
        request.setAttribute("destinationList", destinationList);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("systemList", systemList);
        request.setAttribute("regionList", regionList);
        request.setAttribute("groupList", groupList);
        request.setAttribute("paginator", paginator);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/reports/component.jsp").forward(request, response);
    }
}
