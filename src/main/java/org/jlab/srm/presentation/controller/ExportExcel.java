package org.jlab.srm.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.srm.business.session.ExcelFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "ExportExcel",
    urlPatterns = {"/categories-systems.xlsx"})
public class ExportExcel extends HttpServlet {

  @EJB ExcelFacade excelFacade;

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

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("content-disposition", "attachment;filename=\"categories-systems.xlsx\"");

    excelFacade.exportCategoriesAndSystems(response.getOutputStream());
  }
}
