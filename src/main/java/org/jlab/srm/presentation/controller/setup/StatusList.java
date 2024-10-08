package org.jlab.srm.presentation.controller.setup;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.StatusFacade;
import org.jlab.srm.persistence.entity.Status;

/**
 * @author ryans
 */
@WebServlet(
    name = "StatusList",
    urlPatterns = {"/setup/status-list"})
public class StatusList extends HttpServlet {

  @EJB StatusFacade statusFacade;

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
    List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));

    request.setAttribute("statusList", statusList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/status-list.jsp")
        .forward(request, response);
  }
}
