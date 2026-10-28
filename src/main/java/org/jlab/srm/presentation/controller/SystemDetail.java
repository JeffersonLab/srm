package org.jlab.srm.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "SystemDetail",
    urlPatterns = {"/system-detail"})
public class SystemDetail extends HttpServlet {

  @EJB SystemFacade systemFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    BigInteger systemId;

    try {
      systemId = ParamConverter.convertBigInteger(request, "systemId");
    } catch (NumberFormatException e) {
      systemId = null;
    }

    if (systemId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    SystemEntity system = systemFacade.findWithRelatedData(systemId);

    if (system == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    request.setAttribute("system", system);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/system-detail.jsp")
        .forward(request, response);
  }
}
