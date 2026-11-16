package org.jlab.srm.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.smoothness.business.service.SettingsService;
import org.jlab.smoothness.persistence.view.ImmutableSettings;
import org.jlab.srm.presentation.util.Link;

/**
 * The Links tab, whose links admins edit in the LINKS_GENERAL_CSV and LINKS_SKETCH_CSV settings.
 *
 * @author ryans
 */
@WebServlet(
    name = "Links",
    urlPatterns = {"/links"})
public class Links extends HttpServlet {

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
    ImmutableSettings settings = SettingsService.cachedSettings;
    String cedServerUrl = System.getenv("CED_SERVER_URL");

    request.setAttribute(
        "generalLinks", Link.parse(settings.csv("LINKS_GENERAL_CSV"), cedServerUrl));
    request.setAttribute("sketchLinks", Link.parse(settings.csv("LINKS_SKETCH_CSV"), cedServerUrl));

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/links.jsp")
        .forward(request, response);
  }
}
