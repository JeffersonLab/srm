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
import org.jlab.smoothness.presentation.util.Functions;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ChecklistFacade;
import org.jlab.srm.business.session.ChecklistHistoryFacade;
import org.jlab.srm.business.session.GroupResponsibilityFacade;
import org.jlab.srm.persistence.entity.Checklist;
import org.jlab.srm.persistence.entity.GroupResponsibility;

/**
 * @author ryans
 */
@WebServlet(
    name = "ChecklistController",
    urlPatterns = {"/checklist"})
public class ChecklistController extends HttpServlet {

  @EJB ChecklistFacade checklistFacade;
  @EJB GroupResponsibilityFacade responsibilityFacade;
  @EJB ChecklistHistoryFacade historyFacade;

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

    BigInteger checklistId = ParamConverter.convertBigInteger(request, "checklistId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");

    Boolean editable = null;

    try {
      editable = ParamConverter.convertYNBoolean(request, "editable");
    } catch (Exception e) {
      throw new ServletException("Unable to parse boolean", e);
    }

    if (editable == null) {
      editable = false;
    }

    setChecklist(request, checklistId, groupId, systemId);

    request.setAttribute("editable", editable);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/checklist.jsp")
        .forward(request, response);
  }

  private void setChecklist(
      HttpServletRequest request, BigInteger checklistId, BigInteger groupId, BigInteger systemId)
      throws ServletException {
    Checklist checklist;

    /* Create new if checklistId is null */
    if (checklistId == null) {
      if (groupId == null || systemId == null) {
        throw new ServletException("checklistId must not be null");
      }

      GroupResponsibility responsibility =
          responsibilityFacade.findBySystemIdAndGroupId(systemId, groupId);

      if (responsibility == null) {
        throw new ServletException(
            "No responsibility for groupId " + groupId + " and systemId " + systemId);
      }

      if (responsibility.getChecklist() != null) {
        throw new ServletException(
            "Checklist already exists for groupId " + groupId + " and systemId " + systemId);
      }

      checklist = new Checklist();
      checklist.setGroupResponsibility(responsibility);
      String username = request.getRemoteUser();
      if (username != null && !username.isEmpty()) {
        checklist.setAuthor(Functions.formatUsername(username));
      }
    } else {
      /* View / Edit existing */
      checklist = checklistFacade.find(checklistId);

      if (checklist == null) {
        throw new ServletException("Checklist with ID: " + checklistId + " not found");
      }

      int revision = historyFacade.countByChecklistId(checklistId);

      request.setAttribute("revision", revision);
    }

    request.setAttribute("checklist", checklist);
  }

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
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");

    String bodyHtml = request.getParameter("bodyHtml");
    String author = request.getParameter("author");
    String comments = request.getParameter("comments");

    try {
      /* Create new if checklistId is null */
      if (checklistId == null) {
        if (groupId == null || systemId == null) {
          throw new ServletException("checklistId must not be null");
        }
        checklistFacade.createNew(groupId, systemId, author, bodyHtml, comments);
      } else {
        /* Edit existing */
        checklistFacade.saveChanges(checklistId, author, bodyHtml, comments);
      }

      String returnUrl = request.getContextPath() + "/checklists?groupId=" + groupId;

      response.sendRedirect(response.encodeRedirectURL(returnUrl));

    } catch (UserFriendlyException e) {
      request.setAttribute("errorMessage", "Unable to save checklist: " + e.getMessage());
      request.setAttribute("editable", true);
      setChecklist(request, checklistId, groupId, systemId);
      getServletConfig()
          .getServletContext()
          .getRequestDispatcher("/WEB-INF/views/checklist.jsp")
          .forward(request, response);
    }
  }
}
