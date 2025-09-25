package org.jlab.srm.presentation.controller.reports;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jlab.srm.business.params.ActivitySummaryParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Region;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.srm.persistence.model.ActivitySummaryRecord;
import org.jlab.srm.presentation.params.ActivitySummaryUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "Signoff Summary",
    urlPatterns = {"/reports/signoff-summary"})
public class SignoffSummary extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB RegionFacade regionFacade;
  @EJB SignoffActivityFacade signoffActivityFacade;

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
    List<BeamDestination> destinationList = destinationFacade.findAll(new OrderDirective("weight"));

    List<BeamDestination> defaultDestinationList =
        destinationFacade.filterTargetList(destinationList);

    BigInteger[] defaultDestinationIdArray = destinationFacade.toIdArray(defaultDestinationList);

    ActivitySummaryUrlParamHandler paramHandler =
        new ActivitySummaryUrlParamHandler(
            request,
            defaultDestinationIdArray,
            destinationFacade,
            categoryFacade,
            systemFacade,
            regionFacade);

    ActivitySummaryParams params;

    if (paramHandler.qualified()) {
      try {
        params = paramHandler.convert();
        paramHandler.validate(params);
        paramHandler.store(params);
      } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    } else {
      params = paramHandler.materialize();
      paramHandler.redirect(response, params);
      return;
    }

    Calendar c = Calendar.getInstance();
    // Date now = new Date();
    c.set(Calendar.MILLISECOND, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.HOUR_OF_DAY, 7);
    Date today = c.getTime();
    c.add(Calendar.DATE, -7);
    Date sevenDaysAgo = c.getTime();
    c.add(Calendar.DATE, 4);
    Date threeDaysAgo = c.getTime();
    c.add(Calendar.DATE, 2);
    Date oneDayAgo = c.getTime();

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);
    List<Region> regionList = regionFacade.findAll(new OrderDirective("weight"));
    List<ActivitySummaryRecord> activitySummaryList =
        signoffActivityFacade.findSummaryList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getStart(),
            params.getEnd());

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    ActivitySummaryRecord total = null;
    int grandTotal = 0;

    if (activitySummaryList != null) {
      int upgradeReady = 0;
      int upgradeChecked = 0;
      int downgradeChecked = 0;
      int downgradeNotReady = 0;
      int cascade = 0;
      int comment = 0;

      for (ActivitySummaryRecord r : activitySummaryList) {
        upgradeReady = upgradeReady + r.getUpgradeReadyCount();
        upgradeChecked = upgradeChecked + r.getUpgradeCheckedCount();
        downgradeChecked = downgradeChecked + r.getDowngradeCheckedCount();
        downgradeNotReady = downgradeNotReady + r.getDowngradeNotReadyCount();
        cascade = cascade + r.getCascadeCount();
        comment = comment + r.getCommentCount();
      }

      total =
          new ActivitySummaryRecord(
              BigInteger.ZERO,
              "Total",
              upgradeReady,
              upgradeChecked,
              downgradeChecked,
              downgradeNotReady,
              cascade,
              comment);
      grandTotal =
          upgradeReady + upgradeChecked + downgradeChecked + downgradeNotReady + cascade + comment;
    }

    // DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Changes ";

    String filters = paramHandler.message(params);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    // selectionMessage = selectionMessage + " {" + formatter.format(grandTotal) + "}";

    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("start", params.getStart());
    request.setAttribute("end", params.getEnd());
    request.setAttribute("total", total);
    request.setAttribute("grandTotal", grandTotal);
    request.setAttribute("activitySummaryList", activitySummaryList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("now", new Date());

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/signoff-summary.jsp")
        .forward(request, response);
  }
}
