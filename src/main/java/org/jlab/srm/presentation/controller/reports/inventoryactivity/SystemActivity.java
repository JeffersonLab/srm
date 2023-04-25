package org.jlab.srm.presentation.controller.reports.inventoryactivity;

import org.jlab.srm.business.session.SystemAudFacade;
import org.jlab.srm.persistence.entity.aud.SystemAud;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

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
@WebServlet(name = "SystemActivity", urlPatterns = {"/reports/inventory-activity/system-audit"})
public class SystemActivity extends HttpServlet {

    @EJB
    SystemAudFacade audFacade;

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

        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger revisionId = ParamConverter.convertBigInteger(request, "revisionId");

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 5;

        List<SystemAud> revisionList = null;
        Long totalRecords = 0L;

        if (systemId != null) {
            revisionList = audFacade.filterList(systemId, revisionId, offset, maxPerPage);
            totalRecords = audFacade.countFilterList(systemId, revisionId);

            audFacade.loadStaff(revisionList);
        }

        Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);


        request.setAttribute("revisionList", revisionList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/reports/inventory-activity/system-audit.jsp").forward(request, response);
    }
}
