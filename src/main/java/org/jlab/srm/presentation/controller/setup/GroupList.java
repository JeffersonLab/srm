package org.jlab.srm.presentation.controller.setup;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.enumeration.Include;

/**
 * @author ryans
 */
@WebServlet(
    name = "GroupList",
    urlPatterns = {"/setup/group-list"})
public class GroupList extends HttpServlet {

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
    List<ResponsibleGroup> groupList = groupFacade.findAllWithLeaderList(Include.YES);

    request.setAttribute("groupList", groupList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/group-list.jsp")
        .forward(request, response);
  }
}
