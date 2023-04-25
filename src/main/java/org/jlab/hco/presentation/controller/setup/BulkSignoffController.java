package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.CategoryFacade;
import org.jlab.hco.business.session.StatusFacade;
import org.jlab.hco.business.session.SystemFacade;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.Status;
import org.jlab.hco.persistence.entity.SystemEntity;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "BulkSignoffController", urlPatterns = {"/setup/bulk-signoff"})
public class BulkSignoffController extends HttpServlet {

    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    StatusFacade statusFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(
                request, "systemId");

        List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));
        Category categoryRoot = categoryFacade.findBranch(null, null);
        List<SystemEntity> systemList = systemFacade.findWithCategory(categoryId, null,
                null,
                null, true, false);

        String selectionMessage = null;
        Category selectedCategory = null;
        SystemEntity selectedSystem = null;

        if (categoryId != null) {
            selectedCategory = categoryFacade.find(categoryId);
            selectionMessage = selectedCategory.getName();
        }

        if (systemId != null) {
            selectedSystem = systemFacade.findWithRelatedData(systemId);

            if (selectionMessage == null) {
                selectionMessage = selectedSystem.getName();
            } else {
                selectionMessage = selectionMessage + " > " + selectedSystem.getName();
            }
        }

        statusList.remove(Status.NOT_APPLICABLE);
        statusList.remove(Status.MASKED);
        statusList.remove(Status.MASKED_CC);
        statusList.remove(Status.MASKED_ADMIN);

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("systemList", systemList);
        request.setAttribute("selectedSystem", selectedSystem);
        request.setAttribute("statusList", statusList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/bulk-signoff.jsp").forward(
                request, response);
    }
}
