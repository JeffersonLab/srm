package org.jlab.srm.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.GroupResponsibilityFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "PublishChecklist",
    urlPatterns = {"/publish-checklist"})
public class PublishChecklist extends HttpServlet {

  @EJB GroupResponsibilityFacade groupResponsibilityFacade;

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    BigInteger groupResponsibilityId =
        ParamConverter.convertBigInteger(request, "groupResponsibilityId");

    if (groupResponsibilityId == null) {
      throw new ServletException("groupResponsiblityId must not be null");
    }

    BigInteger groupId = null;

    try {
      groupId = groupResponsibilityFacade.togglePublished(groupResponsibilityId);
    } catch (UserFriendlyException e) {
      throw new ServletException("Unable to toggle published attribute", e);
    }

    String returnUrl = request.getContextPath() + "/checklists?groupId=" + groupId;

    response.sendRedirect(response.encodeRedirectURL(returnUrl));
  }
}
