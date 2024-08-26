package org.jlab.srm.presentation.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.persistence.entity.Component;

/**
 * @author ryans
 */
@WebServlet(
    name = "SearchComponent",
    urlPatterns = {"/ajax/search-component"})
@Deprecated
public class SearchComponent extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(SearchComponent.class.getName());
  @EJB ComponentFacade componentFacade;

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

    List<Component> componentList = null;

    try {
      String term = request.getParameter("term");

      componentList = componentFacade.search(term, 25);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to perform component search", e);
      errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    response.setContentType("application/json");

    PrintWriter pw = response.getWriter();

    JsonObjectBuilder json = Json.createObjectBuilder();

    if (errorReason == null) {
      JsonArrayBuilder staffJsonArray = Json.createArrayBuilder();
      if (componentList != null) {
        for (Component component : componentList) {
          JsonObjectBuilder staffJson = Json.createObjectBuilder();
          staffJson.add("id", component.getComponentId());
          staffJson.add("label", component.getName());
          staffJson.add("value", component.getName());
          staffJsonArray.add(staffJson);
        }
      }
      json.add("records", staffJsonArray);
    } else {
      json.add("error", errorReason);
    }

    pw.write(json.build().toString());

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      LOGGER.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
