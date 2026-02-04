package org.jlab.srm.presentation.controller.excel;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import org.jlab.srm.business.params.SignoffReportParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.model.SignoffReportRecord;
import org.jlab.srm.presentation.params.SignoffReportUrlParamHandler;

/**
 * @author ryans
 */
@WebServlet(
    name = "ExcelSignoffReport",
    urlPatterns = {"/excel/signoff.xlsx"})
public class ExcelSignoffReport extends HttpServlet {

  @EJB ExcelSignoffReportFacade excelFacade;
  @EJB BeamDestinationFacade destinationFacade;
  @EJB SystemFacade systemFacade;
  @EJB RegionFacade regionFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB ComponentSignoffFacade signoffFacade;
  @EJB StatusFacade statusFacade;
  @EJB CategoryFacade categoryFacade;

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

    SignoffReportUrlParamHandler paramHandler =
        new SignoffReportUrlParamHandler(
            request,
            defaultDestinationIdArray,
            destinationFacade,
            categoryFacade,
            systemFacade,
            regionFacade,
            groupFacade);

    SignoffReportParams params;

    try {
      params = paramHandler.convert();
      paramHandler.validate(params);
      paramHandler.store(params);
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    int offset = 0;
    int maxPerPage = Integer.MAX_VALUE;

    List<SignoffReportRecord> recordList =
        signoffFacade.filterSignoffReportRecordList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            params.getStatusId(),
            params.isReadyTurn(),
            params.isMasked(),
            params.getComponentName(),
            offset,
            maxPerPage);
    Long totalRecords =
        signoffFacade.countSignoffReportRecordList(
            params.getDestinationIdArray(),
            params.getCategoryId(),
            params.getSystemId(),
            params.getRegionId(),
            params.getGroupId(),
            params.getStatusId(),
            params.isReadyTurn(),
            params.isMasked(),
            params.getComponentName());

    String selectionMessage = "All Signoffs ";

    String filters = paramHandler.message(params);

    if (filters.length() > 0) {
      selectionMessage = filters;
    }

    DecimalFormat formatter = new DecimalFormat("###,###");

    selectionMessage = selectionMessage + " {" + formatter.format(totalRecords) + "}";

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("content-disposition", "attachment;filename=\"signoff.xlsx\"");

    excelFacade.export(response.getOutputStream(), recordList, selectionMessage);
  }
}
