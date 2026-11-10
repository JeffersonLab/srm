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
import org.jlab.srm.business.session.BeamDestinationFacade;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.enumeration.Facility;

/**
 * @author ryans
 */
@WebServlet(
    name = "BeamDestinationList",
    urlPatterns = {"/setup/destination-list"})
public class BeamDestinationList extends HttpServlet {

  @EJB BeamDestinationFacade destinationFacade;

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
    Facility facility;

    try {
      facility = facility(request);
    } catch (IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<BeamDestination> destinationList = destinationFacade.findByFacility(facility);

    request.setAttribute("facilityList", Facility.values());
    request.setAttribute("selectedFacility", facility);
    request.setAttribute("destinationList", destinationList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/destination-list.jsp")
        .forward(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Facility facility;

    try {
      facility = facility(request);
    } catch (IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    BigInteger[] targetArray = ParamConverter.convertBigIntegerArray(request, "target");

    // The form lists only the filtered facility's destinations, so leave the others' targets
    destinationFacade.setTarget(facility, targetArray);

    String url = "destination-list";

    if (facility != null) {
      url = url + "?facility=" + facility.name();
    }

    response.sendRedirect(response.encodeRedirectURL(url));
  }

  /**
   * Reads the facility parameter.
   *
   * @param request servlet request
   * @return the facility, or null if the parameter is missing or empty
   * @throws IllegalArgumentException if the parameter names no facility
   */
  private static Facility facility(HttpServletRequest request) {
    String value = request.getParameter("facility");

    if (value == null || value.isBlank()) {
      return null;
    }

    return Facility.valueOf(value);
  }
}
