package org.jlab.hco.presentation.controller;

import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author ryans
 */
@WebServlet(name = "GroupDetail", urlPatterns = {"/group-detail"})
public class GroupDetail extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

        ResponsibleGroup group = groupFacade.findDetail(groupId);

        request.setAttribute("group", group);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/group-detail.jsp").forward(request, response);
    }
}
