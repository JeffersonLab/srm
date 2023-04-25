package org.jlab.hco.presentation.controller.data;

import org.jlab.hco.business.session.BeamDestinationFacade;
import org.jlab.hco.persistence.entity.BeamDestination;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "BeamDestinations", urlPatterns = {"/data/destinations"})
public class BeamDestinations extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            BeamDestinations.class.getName());
    @EJB
    BeamDestinationFacade destinationFacade;

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
        String errorReason = null;
        List<BeamDestination> destinationList = null;
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
            BigInteger componentId = ParamConverter.convertBigInteger(request, "component_id");
            jsonp = request.getParameter("jsonp");

            destinationList = destinationFacade.filterList(componentId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to obtain destination list", e);
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        PrintWriter pw = response.getWriter();

        if (plaintextFormat) {
            response.setContentType("text/plain");

            if (errorReason == null) {
                if (destinationList != null) {
                    for (BeamDestination destination : destinationList) {
                        pw.write(destination.getName());
                        pw.write(" - ");
                        pw.write(destination.getBeamDestinationId().toString());
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
                if (destinationList != null) {
                    for (BeamDestination destination : destinationList) {
                        JsonObjectBuilder itemJson = Json.createObjectBuilder();
                        itemJson.add("id", destination.getBeamDestinationId());
                        itemJson.add("name", destination.getName());
                        itemJson.add("weight", destination.getWeight());

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
                jsonStr = jsonp + "(" + jsonStr + ");";
            }

            pw.write(jsonStr);
        }

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            LOGGER.log(Level.SEVERE, "PrintWriter Error");
        }
    }
}
