package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.*;
import org.jlab.hco.persistence.entity.*;
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
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "SavedSignoffController", urlPatterns
        = {"/setup/saved-signoff"})
public class SavedSignoffController extends HttpServlet {

    @EJB
    private SavedSignoffFacade savedSignoffFacade;
    @EJB
    private StatusFacade statusFacade;
    @EJB
    private SystemFacade systemFacade;
    @EJB
    private ResponsibleGroupFacade groupFacade;
    @EJB
    private RegionFacade regionFacade;
    @EJB
    private SavedSignoffTypeFacade typeFacade;

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

        BigInteger typeId = ParamConverter.convertBigInteger(request, "typeId");
        BigInteger systemId = ParamConverter.convertBigInteger(request,
                "systemId");
        BigInteger groupId
                = ParamConverter.convertBigInteger(request, "groupId");

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 100;

        List<SavedSignoff> signoffList
                = savedSignoffFacade.filterList(typeId, systemId, groupId,
                offset, maxPerPage);
        Long totalRecords = savedSignoffFacade.countFilterList(typeId, systemId,
                groupId);
        List<Status> statusList = statusFacade.findAll(
                new AbstractFacade.OrderDirective("statusId"));
        List<SystemEntity> systemList = systemFacade.findAllHco();
        List<ResponsibleGroup> groupList = groupFacade.findAll(
                new AbstractFacade.OrderDirective("name"));
        List<Region> regionList = regionFacade.findAll(
                new AbstractFacade.OrderDirective("weight"));
        List<SavedSignoffType> typeList = typeFacade.findAll(
                new AbstractFacade.OrderDirective("weight"));

        SavedSignoffType selectedType = null;
        SystemEntity selectedSystem = null;
        ResponsibleGroup selectedGroup = null;

        if (typeId != null) {
            selectedType = typeFacade.find(typeId);
        }

        if (systemId != null) {
            selectedSystem = systemFacade.find(systemId);
        }

        if (groupId != null) {
            selectedGroup = groupFacade.find(groupId);
        }

        Paginator paginator = new Paginator(totalRecords.intValue(), offset,
                maxPerPage);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Saved Signoffs ";

        String filters = FilterSelectionMessage.getSavedSignoffMessage(
                selectedType, selectedSystem,
                selectedGroup);

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

        request.setAttribute("paginator", paginator);
        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("signoffList", signoffList);
        request.setAttribute("statusList", statusList);
        request.setAttribute("systemList", systemList);
        request.setAttribute("groupList", groupList);
        request.setAttribute("regionList", regionList);
        request.setAttribute("typeList", typeList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/saved-signoff.jsp").forward(request,
                response);
    }
}
