package org.jlab.srm.presentation.controller.reports;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@WebServlet(
    name = "GroupLeaderReport",
    urlPatterns = {"/reports/group-leader"})
public class GroupLeaderReport extends HttpServlet {
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

    List<ResponsibleGroup> groupList = groupFacade.findAllWithLeaderList(null);

    request.setAttribute("groupList", groupList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/group-leader.jsp")
        .forward(request, response);
  }
}
