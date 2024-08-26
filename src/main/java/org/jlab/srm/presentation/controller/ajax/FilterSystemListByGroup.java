package org.jlab.srm.presentation.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "FilterSystemListByGroup",
    urlPatterns = {"/ajax/filter-system-list-by-group"})
public class FilterSystemListByGroup extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(FilterSystemListByGroup.class.getName());
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
      BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

      systemList = systemFacade.findByGroup(groupId);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to filter system list", e);
      errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    response.setContentType("application/json");

    PrintWriter pw = response.getWriter();

    JsonObjectBuilder json = Json.createObjectBuilder();

    if (errorReason == null) {
      JsonArrayBuilder optionJsonArray = Json.createArrayBuilder();
      if (systemList != null) {
        for (SystemEntity system : systemList) {
          JsonObjectBuilder systemJson = Json.createObjectBuilder();
          systemJson.add("name", system.getName());
          systemJson.add("value", system.getSystemId());
          optionJsonArray.add(systemJson);
        }
      }
      json.add("status", "success");
      json.add("optionList", optionJsonArray);
    } else {
      json.add("status", "error");
      json.add("errorReason", errorReason);
    }

    pw.write(json.build().toString());

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      LOGGER.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
