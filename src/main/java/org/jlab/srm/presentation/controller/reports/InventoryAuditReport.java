package org.jlab.srm.presentation.controller.reports;

import org.jlab.srm.business.params.InventoryAuditParams;
import org.jlab.srm.business.session.ApplicationRevisionInfoFacade;
import org.jlab.srm.persistence.entity.ApplicationRevisionInfo;
import org.jlab.srm.presentation.params.InventoryAuditUrlParamHandler;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "InventoryActivityReport", urlPatterns = {"/reports/inventory-activity"})
public class InventoryAuditReport extends HttpServlet {

    @EJB
    ApplicationRevisionInfoFacade revisionFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        InventoryAuditUrlParamHandler paramHandler = new InventoryAuditUrlParamHandler(request);

        InventoryAuditParams params;

        if (paramHandler.qualified()) {
            params = paramHandler.convert();
            paramHandler.validate(params);
            paramHandler.store(params);
        } else {
            params = paramHandler.materialize();
            paramHandler.redirect(response, params);
            return;
        }

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 100;


        List<ApplicationRevisionInfo> transactionList = revisionFacade.filterList(params.getStart(), params.getEnd(), offset, maxPerPage);
        Long totalRecords = revisionFacade.countFilterList(params.getStart(), params.getEnd());

        revisionFacade.loadStaff(transactionList);

        Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage = "All Transactions ";

        String filters = paramHandler.message(params);

        if (filters.length() > 0) {
            selectionMessage = filters;
        }

        if (paginator.getTotalRecords() < maxPerPage && offset == 0) {
            selectionMessage = selectionMessage + " {" + formatter.format(
                    paginator.getTotalRecords()) + "}";
        } else {
            selectionMessage = selectionMessage + " {"
                    + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords()) + "}";
        }

        request.setAttribute("transactionList", transactionList);
        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("paginator", paginator);
        request.setAttribute("now", new Date());

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/reports/inventory-activity.jsp").forward(request, response);
    }
}
