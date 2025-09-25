package org.jlab.srm.presentation.controller.reports.inventoryactivity;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.session.ComponentAudFacade;
import org.jlab.srm.persistence.entity.aud.ComponentAud;

/**
 * @author ryans
 */
@WebServlet(
    name = "ComponentActivity",
    urlPatterns = {"/reports/inventory-activity/component-audit"})
public class ComponentActivity extends HttpServlet {

  @EJB ComponentAudFacade audFacade;

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

    BigInteger componentId = ParamConverter.convertBigInteger(request, "componentId");
    BigInteger revisionId = ParamConverter.convertBigInteger(request, "revisionId");

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 5;

    List<ComponentAud> revisionList = null;
    Long totalRecords = 0L;

    if (componentId != null) {
      revisionList = audFacade.filterList(componentId, revisionId, offset, maxPerPage);
      totalRecords = audFacade.countFilterList(componentId, revisionId);

      audFacade.loadStaff(revisionList);
    }

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    request.setAttribute("revisionList", revisionList);
    request.setAttribute("paginator", paginator);

    request
        .getRequestDispatcher("/WEB-INF/views/reports/inventory-activity/component-audit.jsp")
        .forward(request, response);
  }
}
