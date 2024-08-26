package org.jlab.srm.presentation.controller.setup.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.persistence.util.HcoSqlUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "AddComponent",
    urlPatterns = {"/setup/ajax/add-component"})
public class AddComponent extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(AddComponent.class.getName());

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
      String name = request.getParameter("name");
      BigInteger systemId = ParamConverter.convertBigInteger(request, "system-id");
      BigInteger regionId = ParamConverter.convertBigInteger(request, "region-id");
      Boolean masked = ParamConverter.convertYNBoolean(request, "masked");
      String maskedReason = request.getParameter("maskedReason");
      componentFacade.addNew(name, systemId, regionId, masked, maskedReason);
    } catch (EJBAccessException e) {
      LOGGER.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      LOGGER.log(Level.WARNING, "Unable to add component", e);
      errorReason = e.getMessage();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to add component", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      Throwable integrityCause =
          HcoSqlUtil.findCause(SQLIntegrityConstraintViolationException.class, e);

      if (integrityCause != null) {
        SQLIntegrityConstraintViolationException dbException =
            (SQLIntegrityConstraintViolationException) integrityCause;

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
