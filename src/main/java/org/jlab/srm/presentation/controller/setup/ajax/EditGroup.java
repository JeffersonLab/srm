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
import org.jlab.srm.business.session.ResponsibleGroupFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditGroup",
    urlPatterns = {"/setup/ajax/edit-group"})
public class EditGroup extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditGroup.class.getName());

  @EJB ResponsibleGroupFacade groupFacade;

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
      BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
      String leaderWorkgroup = request.getParameter("workgroup");
      String name = request.getParameter("name");
      String description = request.getParameter("description");
      Boolean archived = ParamConverter.convertYNBoolean(request, "archived");

      if (archived == null) {
        throw new UserFriendlyException("archived must not be empty");
      }

      groupFacade.edit(groupId, name, description, leaderWorkgroup, archived);
    } catch (EJBAccessException e) {
      logger.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to edit group", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);

      /*System.out.println("Root Cause: " + rootCause);*/
      if (rootCause instanceof SQLException) {
        SQLException dbException = (SQLException) rootCause;

        /*System.out.println("ErrorCode: " + dbException.getErrorCode());
        System.out.println("State: " + dbException.getSQLState());
        System.out.println("Message: " + dbException.getMessage());*/
        if (dbException.getErrorCode() == 1
            && "23000".equals(dbException.getSQLState())
            && dbException.getMessage().contains("RESPONSIBLE_GROUP_AK1")) {
          errorReason = "There is already a group with that name";
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
