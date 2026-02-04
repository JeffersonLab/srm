package org.jlab.srm.presentation.controller.excel;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.srm.business.session.ExcelCategorySystemsFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "ExcelCategorySystems",
    urlPatterns = {"/excel/categories-systems.xlsx"})
public class ExcelCategorySystems extends HttpServlet {

  @EJB ExcelCategorySystemsFacade excelFacade;

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

    excelFacade.export(response.getOutputStream());
  }
}
