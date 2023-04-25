package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.RegionFacade;
import org.jlab.hco.persistence.entity.Region;

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
@WebServlet(name = "RegionList", urlPatterns = {"/setup/region-list"})
public class RegionList extends HttpServlet {

    @EJB
    RegionFacade regionFacade;

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
        List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));

        request.setAttribute("regionList", regionList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/region-list.jsp").forward(request, response);
    }
}
