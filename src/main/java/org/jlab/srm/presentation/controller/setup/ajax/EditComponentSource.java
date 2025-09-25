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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.persistence.enumeration.DataSource;
import org.jlab.srm.presentation.util.HcoParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "EditComponentSource",
    urlPatterns = {"/setup/ajax/edit-component-source"})
public class EditComponentSource extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(EditComponentSource.class.getName());
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
      BigInteger componentId = ParamConverter.convertBigInteger(request, "component-id");

      DataSource source = HcoParamConverter.convertDataSource(request, "source");

      BigInteger sourceId = ParamConverter.convertBigInteger(request, "source-id");

      componentFacade.setSource(componentId, source, sourceId);
    } catch (EJBAccessException e) {
      LOGGER.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      LOGGER.log(Level.WARNING, "Unable to edit component source", e);
      errorReason = e.getMessage();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to edit component source", e);
      errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    String stat = "ok";

    if (errorReason != null) {
      stat = "fail";
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject()
          .write(
              "stat",
              stat); // This is unnecessary - if 200 OK then it worked (however some clients like
      // CURL make it difficult to get both status and repsone body)
      if (errorReason != null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        gen.write("error", errorReason);
      }
      gen.writeEnd();
    }
  }
}
