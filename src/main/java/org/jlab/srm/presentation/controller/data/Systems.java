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
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.SystemEntity;

/**
 * @author ryans
 */
@WebServlet(
    name = "Systems",
    urlPatterns = {"/data/systems"})
public class Systems extends HttpServlet {

  private static final Logger logger = Logger.getLogger(Systems.class.getName());
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
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String errorReason = null;
    List<SystemEntity> systemList = null;
    String jsonp = null;
    String accept = request.getHeader("Accept");
    boolean plaintextFormat = false;
    /*Look at Accept header and if we find text/plain before application/json then we'll do text/plain.  Otherwise we default to application/json*/
    if (accept != null) {
      String[] tokens = accept.split(",");

      for (String token : tokens) {
        if (token != null) {
          if (token.startsWith("text/plain")) {
            plaintextFormat = true;
            break;
          } else if (token.startsWith("application/json")) {
            break;
          }
        }
      }
    }

    /*We can override the HTTP header with a URL parameter*/
    String acceptOverride = request.getParameter("accept");

    if ("plain".equals(acceptOverride)) {
      plaintextFormat = true;
    }

    try {
      BigInteger[] categoryIdArray = ParamConverter.convertBigIntegerArray(request, "category_id");
      BigInteger componentId = ParamConverter.convertBigInteger(request, "component_id");
      BigInteger systemId = ParamConverter.convertBigInteger(request, "system_id");
      BigInteger applicationId = ParamConverter.convertBigInteger(request, "application_id");
      jsonp = request.getParameter("jsonp");

      systemList =
          systemFacade.findWithCategory(
              categoryIdArray, componentId, systemId, applicationId, true, false);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to obtain system list", e);
      errorReason = e.getClass().getSimpleName();
    }

    PrintWriter pw = response.getWriter();

    if (plaintextFormat) {
      response.setContentType("text/plain");

      if (errorReason == null) {
        if (systemList != null) {
          for (SystemEntity system : systemList) {
            pw.write(system.getName());
            pw.write(" - ");
            pw.write(system.getSystemId().toString());
            pw.println();
          }
        }
      } else {
        pw.write("Unable to service request");
        pw.println();
        pw.write(errorReason);
      }

    } else {
      response.setContentType("application/json");

      JsonObjectBuilder json = Json.createObjectBuilder();

      if (errorReason == null) {
        JsonArrayBuilder itemJsonArray = Json.createArrayBuilder();
        if (systemList != null) {
          for (SystemEntity system : systemList) {
            JsonObjectBuilder itemJson = Json.createObjectBuilder();
            itemJson.add("id", system.getSystemId());
            itemJson.add("name", system.getName());
            itemJson.add("category_id", system.getCategory().getCategoryId());

            itemJsonArray.add(itemJson);
          }
        }
        json.add("stat", "ok");
        json.add("data", itemJsonArray);
      } else {
        json.add("stat", "fail");
        json.add("error", errorReason);
      }

      String jsonStr = json.build().toString();

      if (jsonp != null) {
        jsonStr = "jsonp" + "(" + jsonStr + ");";
      }

      pw.write(jsonStr);
    }

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      logger.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
