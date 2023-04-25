package org.jlab.srm.presentation.controller;

import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.SystemEntity;
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
@WebServlet(name = "SystemDetail", urlPatterns = {"/system-detail"})
public class SystemDetail extends HttpServlet {

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
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");


        SystemEntity system = systemFacade.findWithRelatedData(systemId);

        request.setAttribute("system", system);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/system-detail.jsp").forward(request, response);
    }
}
