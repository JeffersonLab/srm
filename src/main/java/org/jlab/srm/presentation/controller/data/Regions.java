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
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.RegionFacade;
import org.jlab.srm.persistence.entity.Region;

/**
 * @author ryans
 */
@WebServlet(
    name = "Regions",
    urlPatterns = {"/data/regions"})
public class Regions extends HttpServlet {

  private static final Logger logger = Logger.getLogger(Regions.class.getName());
  @EJB RegionFacade regionFacade;

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
    List<Region> regionList = null;
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
      BigInteger systemId = ParamConverter.convertBigInteger(request, "system_id");
      jsonp = request.getParameter("jsonp");

      regionList = regionFacade.filterList(systemId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to obtain region list", e);
      errorReason = e.getClass().getSimpleName();
    }

    PrintWriter pw = response.getWriter();

    if (plaintextFormat) {
      response.setContentType("text/plain");

      if (errorReason == null) {
        if (regionList != null) {
          for (Region region : regionList) {
            pw.write(region.getName());
            pw.write(" - ");
            pw.write(region.getRegionId().toString());
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
        if (regionList != null) {
          for (Region region : regionList) {
            JsonObjectBuilder itemJson = Json.createObjectBuilder();
            itemJson.add("id", region.getRegionId());
            itemJson.add("name", region.getName());
            itemJson.add("aliases", region.getAlias());

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
