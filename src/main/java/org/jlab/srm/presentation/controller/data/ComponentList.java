package org.jlab.srm.presentation.controller.data;

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
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.persistence.entity.Component;

/**
 * @author ryans
 */
@WebServlet(
    name = "DataComponentList",
    urlPatterns = {"/data/component-list"})
@Deprecated
public class ComponentList extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ComponentList.class.getName());
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
      BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
      BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");

      componentList =
          componentFacade.filterList(
              null,
              null,
              systemId,
              regionId,
              null,
              BigInteger.ONE,
              null,
              null,
              null,
              null,
              null,
              false,
              0,
              10000);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to obtain component list", e);
      errorReason = e.getClass().getSimpleName();
    }

    response.setContentType("application/json");

    PrintWriter pw = response.getWriter();

    JsonObjectBuilder json = Json.createObjectBuilder();

    if (errorReason == null) {
      JsonArrayBuilder itemJsonArray = Json.createArrayBuilder();
      if (componentList != null) {
        for (Component component : componentList) {
          JsonObjectBuilder itemJson = Json.createObjectBuilder();
          itemJson.add("id", component.getComponentId());
          itemJson.add("name", component.getName());
          itemJson.add("region", component.getRegion().getName());
          itemJsonArray.add(itemJson);
        }
      }
      json.add("components", itemJsonArray);
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
