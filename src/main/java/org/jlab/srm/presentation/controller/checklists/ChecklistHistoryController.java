package org.jlab.srm.presentation.controller.checklists;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ChecklistFacade;
import org.jlab.srm.business.session.ChecklistHistoryFacade;
import org.jlab.srm.persistence.entity.ChecklistHistory;

/**
 * @author ryans
 */
@WebServlet(
    name = "ChecklistHistoryController",
    urlPatterns = {"/checklists/revision"})
public class ChecklistHistoryController extends HttpServlet {

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
    BigInteger checklistHistoryId = ParamConverter.convertBigInteger(request, "checklistHistoryId");

    if (checklistHistoryId == null) {
      throw new ServletException("checklistHistoryId must not be empty");
    }

    ChecklistHistory history = historyFacade.find(checklistHistoryId);

    request.setAttribute("history", history);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/checklists/revision.jsp")
        .forward(request, response);
  }
}
