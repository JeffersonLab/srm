package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.BeamDestinationFacade;
import org.jlab.hco.persistence.entity.BeamDestination;
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
@WebServlet(name = "BeamDestinationList", urlPatterns = {"/setup/destination-list"})
public class BeamDestinationList extends HttpServlet {

    @EJB
    BeamDestinationFacade destinationFacade;

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
        List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective("weight"));

        request.setAttribute("destinationList", destinationList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/destination-list.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BigInteger[] targetArray = ParamConverter.convertBigIntegerArray(request, "target");

        destinationFacade.setTarget(targetArray);

        response.sendRedirect(response.encodeRedirectURL("destination-list"));
    }
}
