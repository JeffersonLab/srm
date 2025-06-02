package org.jlab.srm.presentation.controller.masks;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.srm.business.params.MaskRequestParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.presentation.params.MaskRequestUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "MaskRequests",
    urlPatterns = {"/masks/requests"})
public class Requests extends HttpServlet {

  @EJB MaskingRequestFacade recordFacade;
  @EJB BeamDestinationFacade destinationFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB SystemFacade systemFacade;
  @EJB RegionFacade regionFacade;
  @EJB ResponsibleGroupFacade groupFacade;

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

    List<BeamDestination> destinationList =
        destinationFacade.findAll(new AbstractFacade.OrderDirective("weight"));

    List<BeamDestination> defaultDestinationList =
        destinationFacade.filterTargetList(destinationList);

    BigInteger[] defaultDestinationIdArray = destinationFacade.toIdArray(defaultDestinationList);

    MaskRequestUrlParamHandler paramHandler =
        new MaskRequestUrlParamHandler(
            request,
            defaultDestinationIdArray,
            destinationFacade,
            categoryFacade,
            systemFacade,
            regionFacade,
            groupFacade);

    MaskRequestParams params;

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

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    Category categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
    List<SystemEntity> systemList =
        systemFacade.findWithCategory(
            params.getCategoryId(), null, null, BigInteger.ONE, true, false);
    List<Region> regionList = regionFacade.findAll(new AbstractFacade.OrderDirective("weight"));
    List<ResponsibleGroup> groupList = groupFacade.filterList(null, 0, Integer.MAX_VALUE);
    List<MaskingRequest> recordList =
        recordFacade.find(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            params.getReason(),
            params.getStatus(),
            offset,
            maxPerPage);
    Long totalRecords =
        recordFacade.count(
            null,
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            params.getReason(),
            params.getStatus());

    String targetCsv = destinationFacade.toCsv(defaultDestinationList);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, maxPerPage);

    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Requests ";

    String filters = paramHandler.message(params);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    if (paginator.getTotalRecords() < maxPerPage && offset == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    request.setAttribute("targetCsv", targetCsv);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("recordList", recordList);
    request.setAttribute("destinationList", destinationList);
    request.setAttribute("categoryRoot", categoryRoot);
    request.setAttribute("systemList", systemList);
    request.setAttribute("regionList", regionList);
    request.setAttribute("groupList", groupList);
    request.setAttribute("paginator", paginator);
    request.setAttribute("now", new Date());

    request.getRequestDispatcher("/WEB-INF/views/masks/requests.jsp").forward(request, response);
  }
}
