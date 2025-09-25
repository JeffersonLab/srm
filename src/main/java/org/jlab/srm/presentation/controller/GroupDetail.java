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
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@WebServlet(
    name = "GroupDetail",
    urlPatterns = {"/group-detail"})
public class GroupDetail extends HttpServlet {
  @EJB ResponsibleGroupFacade groupFacade;

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
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

    ResponsibleGroup group = groupFacade.findDetail(groupId);

    request.setAttribute("group", group);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/group-detail.jsp")
        .forward(request, response);
  }
}
