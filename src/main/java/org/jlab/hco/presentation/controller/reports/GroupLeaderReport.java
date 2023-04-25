package org.jlab.hco.presentation.controller.reports;

import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.persistence.entity.ResponsibleGroup;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "GroupLeaderReport", urlPatterns = {"/reports/group-leader"})
public class GroupLeaderReport extends HttpServlet {
    @EJB
    ResponsibleGroupFacade groupFacade;

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

        List<ResponsibleGroup> groupList = groupFacade.findAllWithLeaderList();

        request.setAttribute("groupList", groupList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/reports/group-leader.jsp").forward(request, response);
    }
}
