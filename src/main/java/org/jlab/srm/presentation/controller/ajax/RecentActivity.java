package org.jlab.srm.presentation.controller.ajax;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.params.RecentActivityParams;
import org.jlab.srm.business.service.RecentActivityService;
import org.jlab.srm.business.session.ComponentTreeFacade;
import org.jlab.srm.persistence.model.SignoffActivityCompressedRecord;

/**
 * The next entries of the readiness page's Recent Activity feed, as the table rows the page
 * appends. Takes the page's filter parameters and the number of entries already shown.
 */
@WebServlet(
    name = "RecentActivity",
    urlPatterns = {"/ajax/recent-activity"})
public class RecentActivity extends HttpServlet {

  /** Entries per request */
  static final int MAX = 10;

  @EJB ComponentTreeFacade treeFacade;

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
    RecentActivityParams params;
    int offset;

    try {
      params =
          new RecentActivityParams(
              IOUtil.removeNullValues(
                  ParamConverter.convertBigIntegerArray(request, "destinationId"),
                  BigInteger.class),
              treeFacade.getSystemIdArray(
                  ParamConverter.convertBigInteger(request, "categoryId"),
                  ParamConverter.convertBigInteger(request, "systemId")),
              ParamConverter.convertBigInteger(request, "regionId"),
              ParamConverter.convertBigInteger(request, "groupId"),
              ParamConverter.convertBigIntegerArray(request, "statusId"));
      params.setExcludeDestinationIdArray(
          IOUtil.removeNullValues(
              ParamConverter.convertBigIntegerArray(request, "excludeDestinationId"),
              BigInteger.class));
      offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<SignoffActivityCompressedRecord> signoffActivityList;

    try {
      // One more than shown, to tell whether there are more
      signoffActivityList =
          new RecentActivityService().filterListCompressedFast(params, offset, MAX + 1);
    } catch (SQLException e) {
      throw new ServletException("Unable to query for recent activity", e);
    }

    boolean more = signoffActivityList.size() > MAX;

    if (more) {
      signoffActivityList = signoffActivityList.subList(0, MAX);
    }

    request.setAttribute("signoffActivityList", signoffActivityList);
    request.setAttribute("signoffActivityMore", more);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/readiness-activity.jsp")
        .forward(request, response);
  }
}
