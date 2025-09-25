package org.jlab.srm.presentation.controller.setup;

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
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.business.session.StatusFacade;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "BulkSignoffController",
    urlPatterns = {"/setup/bulk-signoff"})
public class BulkSignoffController extends HttpServlet {

  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB StatusFacade statusFacade;

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
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");

    List<Status> statusList = statusFacade.findAll(new OrderDirective("statusId"));
    Category categoryRoot = categoryFacade.findBranch(null, null);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(categoryId, null, null, null, true, false);

    String selectionMessage = null;
    Category selectedCategory = null;
    SystemEntity selectedSystem = null;

    if (categoryId != null) {
      selectedCategory = categoryFacade.find(categoryId);
      selectionMessage = selectedCategory.getName();
    }

    if (systemId != null) {
      selectedSystem = systemFacade.findWithRelatedData(systemId);

      if (selectionMessage == null) {
        selectionMessage = selectedSystem.getName();
      } else {
        selectionMessage = selectionMessage + " > " + selectedSystem.getName();
      }
    }

    statusList.remove(Status.NOT_APPLICABLE);
    statusList.remove(Status.MASKED);
    statusList.remove(Status.MASKED_CC);
    statusList.remove(Status.MASKED_ADMIN);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("selectedSystem", selectedSystem);
    request.setAttribute("statusList", statusList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/bulk-signoff.jsp")
        .forward(request, response);
  }
}
