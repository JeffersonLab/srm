package org.jlab.srm.presentation.controller;

import org.jlab.srm.business.session.ExcelFacade;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ryans
 */
@WebServlet(name = "ExportExcel", urlPatterns = {"/categories-systems.xlsx"})
public class ExportExcel extends HttpServlet {

    @EJB
    ExcelFacade excelFacade;

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

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("content-disposition", "attachment;filename=\"categories-subsystems.xlsx\"");

        excelFacade.exportCategoriesAndSubsystems(response.getOutputStream());
    }
}
