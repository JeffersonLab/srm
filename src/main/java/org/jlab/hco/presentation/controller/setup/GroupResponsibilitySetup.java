package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.CategoryFacade;
import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.business.session.SystemFacade;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
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
@WebServlet(name = "GroupResponsibilitySetup",
        urlPatterns = {"/setup/group-responsibility"})
public class GroupResponsibilitySetup extends HttpServlet {

    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
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

        Category categoryRoot = categoryFacade.findBranch(null, null);
        List<SystemEntity> systemList = systemFacade.findWithCategory(categoryId, null,
                null,
                null, true, false);
        List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));

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

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("systemList", systemList);
        request.setAttribute("selectedSystem", selectedSystem);
        request.setAttribute("groupList", groupList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/group-responsibility.jsp").forward(
                request, response);
    }
}
