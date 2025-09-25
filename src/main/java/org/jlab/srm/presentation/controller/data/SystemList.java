package org.jlab.srm.presentation.controller.data;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "SystemList",
    urlPatterns = {"/data/system-list"})
@Deprecated
public class SystemList extends HttpServlet {

  private static final Logger logger = Logger.getLogger(SystemList.class.getName());
  @EJB SystemFacade systemFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String errorReason = null;

    List<SystemEntity> systemList = null;

    try {
      systemList = systemFacade.findAllWithCategory();

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to obtain system list", e);
      errorReason = e.getClass().getSimpleName();
    }

    response.setContentType("application/json");

    PrintWriter pw = response.getWriter();

    JsonObjectBuilder json = Json.createObjectBuilder();

    if (errorReason == null) {
      JsonArrayBuilder itemJsonArray = Json.createArrayBuilder();
      if (systemList != null) {
        for (SystemEntity system : systemList) {
          JsonObjectBuilder itemJson = Json.createObjectBuilder();
          itemJson.add("id", system.getSystemId());
          itemJson.add("name", system.getName());
          itemJson.add("category", system.getCategory().getName());

          itemJsonArray.add(itemJson);
        }
      }
      json.add("systems", itemJsonArray);
    } else {
      json.add("error", errorReason);
    }

    pw.write(json.build().toString());

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      logger.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
