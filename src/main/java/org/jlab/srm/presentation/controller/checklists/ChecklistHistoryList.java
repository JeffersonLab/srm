package org.jlab.srm.presentation.controller.checklists;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ChecklistFacade;
import org.jlab.srm.business.session.ChecklistHistoryFacade;
import org.jlab.srm.persistence.entity.Checklist;
import org.jlab.srm.persistence.entity.ChecklistHistory;

/**
 * @author ryans
 */
@WebServlet(
    name = "ChecklistHistoryList",
    urlPatterns = {"/checklists/history-list"})
public class ChecklistHistoryList extends HttpServlet {

  @EJB ChecklistFacade checklistFacade;
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

    if (checklistId == null) {
      throw new ServletException("checklistId must not be empty");
    }

    Checklist checklist = checklistFacade.find(checklistId);
    List<ChecklistHistory> checklistHistoryList = historyFacade.findByChecklistId(checklistId);

    request.setAttribute("checklist", checklist);
    request.setAttribute("checklistHistoryList", checklistHistoryList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/checklists/history-list.jsp")
        .forward(request, response);
  }
}
