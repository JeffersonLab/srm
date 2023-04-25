package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade.OrderDirective;
import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.business.session.WorkgroupFacade;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
import org.jlab.hco.persistence.entity.Workgroup;

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
@WebServlet(name = "GroupList", urlPatterns = {"/setup/group-list"})
public class GroupList extends HttpServlet {

    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    WorkgroupFacade workgroupFacade;

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
        List<Workgroup> workgroupList = workgroupFacade.findAll(new OrderDirective("name"));

        request.setAttribute("groupList", groupList);
        request.setAttribute("workgroupList", workgroupList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/group-list.jsp").forward(request, response);
    }
}
