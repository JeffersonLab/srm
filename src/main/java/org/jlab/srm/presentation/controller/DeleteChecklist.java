package org.jlab.srm.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ChecklistFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "DeleteChecklist",
    urlPatterns = {"/delete-checklist"})
public class DeleteChecklist extends HttpServlet {

  @EJB ChecklistFacade checklistFacade;

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

    BigInteger checklistId = ParamConverter.convertBigInteger(request, "checklistId");

    /* Create new if checklistId is null */
    if (checklistId == null) {
      throw new ServletException("checklistId must not be null");
    }

    BigInteger groupId = null;

    try {
      groupId = checklistFacade.delete(checklistId);
    } catch (UserFriendlyException e) {
      throw new ServletException("Unable to delete checklist", e);
    }

    String returnUrl = request.getContextPath() + "/checklists?groupId=" + groupId;

    response.sendRedirect(response.encodeRedirectURL(returnUrl));
  }
}
