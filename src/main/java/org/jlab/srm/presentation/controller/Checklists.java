package org.jlab.srm.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.GroupResponsibilityFacade;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.GroupResponsibility;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@WebServlet(
    name = "Checklists",
    urlPatterns = {"/checklists"})
public class Checklists extends HttpServlet {

  @EJB SystemFacade systemFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB GroupResponsibilityFacade groupResponsibilityFacade;

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
    try {
      BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

      List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));

      boolean adminOrLeader = false;
      List<GroupResponsibility> groupResponsibilityList = null;
      ResponsibleGroup selectedGroup = null;

      if (groupId != null) {
        selectedGroup = groupFacade.find(groupId);
        groupResponsibilityList =
            groupResponsibilityFacade.filterList(null, null, null, groupId, null, null, 0, 1000);

        String username = request.getRemoteUser();

        adminOrLeader =
            groupResponsibilityFacade.isAdminOrGroupLeader(username, selectedGroup.getGroupId());
      }

      request.setAttribute("adminOrLeader", adminOrLeader);
      request.setAttribute("groupResponsibilityList", groupResponsibilityList);
      request.setAttribute("selectedGroup", selectedGroup);
      request.setAttribute("groupList", groupList);

      getServletConfig()
          .getServletContext()
          .getRequestDispatcher("/WEB-INF/views/checklists.jsp")
          .forward(request, response);
    } catch (Exception e) {
      throw new ServletException("Unable to display checklists", e);
    }
  }
}
