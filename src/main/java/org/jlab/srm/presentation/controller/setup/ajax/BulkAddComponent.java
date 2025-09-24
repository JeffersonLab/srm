package org.jlab.srm.presentation.controller.setup.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "BulkAddComponent",
    urlPatterns = {"/setup/ajax/bulk-add-component"})
public class BulkAddComponent extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(BulkAddComponent.class.getName());

  @EJB ComponentFacade componentFacade;

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
    String errorReason = null;

    try {
      String names = request.getParameter("names");
      BigInteger systemId = ParamConverter.convertBigInteger(request, "system-id");
      BigInteger regionId = ParamConverter.convertBigInteger(request, "region-id");
      componentFacade.bulkAddNew(names, systemId, regionId);
    } catch (EJBAccessException e) {
      LOGGER.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      LOGGER.log(Level.WARNING, "Unable to bulk add components", e);
      errorReason = e.getMessage();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to bulk add components", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      if (rootCause instanceof SQLException) {
        SQLException dbException = (SQLException) rootCause;

        /*System.out.println("ErrorCode: " + dbException.getErrorCode());
        System.out.println("State: " + dbException.getSQLState());
        System.out.println("Message: " + dbException.getMessage());*/

        if (dbException.getErrorCode() == 1
            && "23000".equals(dbException.getSQLState())
            && dbException.getMessage().contains("COMPONENT_AK1")) {
          errorReason = "There is already a component in this system with that name";
        } else if (dbException.getErrorCode() == 2290
            && "23000".equals(dbException.getSQLState())
            && dbException.getMessage().contains("COMPONENT_CK4")) {
          errorReason =
              "Component name cannot contain an asterisk because we use the asterisk to denote unpowered components";
        } else {
          errorReason = "Database exception";
        }
      } else {
        errorReason = "Something unexpected happened";
      }
    }

    String stat = "ok";

    if (errorReason != null) {
      stat = "fail";
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat); // This is unnecessary - if 200 OK then it worked
      if (errorReason != null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        gen.write("error", errorReason);
      }
      gen.writeEnd();
    }
  }
}
