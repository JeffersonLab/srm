package org.jlab.hco.presentation.controller.reports.inventoryactivity;

import org.jlab.hco.business.session.CategoryAudFacade;
import org.jlab.hco.persistence.entity.aud.CategoryAud;
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
@WebServlet(name = "CategoryActivity", urlPatterns = {"/reports/inventory-activity/category-audit"})
public class CategoryActivity extends HttpServlet {

    @EJB
    CategoryAudFacade audFacade;

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

        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger revisionId = ParamConverter.convertBigInteger(request, "revisionId");

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int maxPerPage = 5;

        List<CategoryAud> revisionList = null;
        Long totalRecords = 0L;

        if (categoryId != null) {
            revisionList = audFacade.filterList(categoryId, revisionId, offset, maxPerPage);
            totalRecords = audFacade.countFilterList(categoryId, revisionId);

            audFacade.loadStaff(revisionList);
        }

        Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);


        request.setAttribute("revisionList", revisionList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/reports/inventory-activity/category-audit.jsp").forward(request, response);
    }
}
