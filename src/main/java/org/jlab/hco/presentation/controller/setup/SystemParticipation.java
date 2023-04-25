package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.ApplicationFacade;
import org.jlab.hco.business.session.CategoryFacade;
import org.jlab.hco.business.session.SystemFacade;
import org.jlab.hco.persistence.entity.Application;
import org.jlab.hco.persistence.entity.Category;
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
@WebServlet(name = "SystemParticipation",
        urlPatterns = {"/setup/subsystem-participation"})
public class SystemParticipation extends HttpServlet {

    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    ApplicationFacade applicationFacade;

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

        Category selectedCategory = null;

        if (categoryId != null) {
            selectedCategory = categoryFacade.find(categoryId);
        }

        Category categoryRoot = categoryFacade.findRootWithAllDescendents();

        List<SystemEntity> systemList = systemFacade.findWithCategory(categoryId, null, null, null, true, true);

        List<Application> applicationList = applicationFacade.findAll();

        request.setAttribute("selectedCategory", selectedCategory);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("systemList", systemList);
        request.setAttribute("applicationList", applicationList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/subsystem-participation.jsp").forward(
                request, response);
    }
}
