package org.jlab.srm.presentation.controller.setup;

import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.SystemEntity;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "CategoryTree", urlPatterns = {"/setup/category-tree"})
public class CategoryTree extends HttpServlet {

    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Category root = categoryFacade.findRootWithChildren();
        List<SystemEntity> systemList = systemFacade.findAllWithCategory();
        request.setAttribute("root", root);
        request.setAttribute("systemList", systemList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/category-tree.jsp").forward(request, response);
    }
}
