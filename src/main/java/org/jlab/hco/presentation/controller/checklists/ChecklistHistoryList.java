package org.jlab.hco.presentation.controller.checklists;

import org.jlab.hco.business.session.ChecklistFacade;
import org.jlab.hco.business.session.ChecklistHistoryFacade;
import org.jlab.hco.persistence.entity.Checklist;
import org.jlab.hco.persistence.entity.ChecklistHistory;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "ChecklistHistoryList", urlPatterns = {"/checklists/history-list"})
public class ChecklistHistoryList extends HttpServlet {

    @EJB
    ChecklistFacade checklistFacade;
    @EJB
    ChecklistHistoryFacade historyFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/checklists/history-list.jsp").forward(request, response);
    }
}
