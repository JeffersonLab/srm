package org.jlab.srm.presentation.controller.setup.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBException;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.persistence.entity.Status;

/**
 * @author ryans
 */
@WebServlet(
    name = "MaskComponent",
    urlPatterns = {"/setup/ajax/mask-component"})
public class MaskComponent extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(MaskComponent.class.getName());

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
      BigInteger componentId = ParamConverter.convertBigInteger(request, "componentId");
      Boolean masked = ParamConverter.convertYNBoolean(request, "masked");
      String maskedReason = request.getParameter("maskedReason");
      Date expiration = ParamConverter.convertFriendlyDateTime(request, "expiration");
      componentFacade.editMasked(
          componentId,
          masked,
          maskedReason,
          expiration,
          Status.MASKED_ADMIN.getStatusId().intValue());
    } catch (EJBException e) {
      Throwable root = ExceptionUtil.getRootCause(e);
      if (e instanceof EJBAccessException || root instanceof EJBAccessException) {
        LOGGER.log(Level.WARNING, "Not authorized", e);
        errorReason = "Not authorized";
      } else if (root instanceof ConstraintViolationException) {
        LOGGER.log(Level.WARNING, "Invalid input data", e);
        errorReason = root.getMessage();
      } else {
        LOGGER.log(Level.WARNING, "Transaction rolled back", e);
        errorReason =
            "Unknown Exception of type: " + e.getCausedByException().getClass().getSimpleName();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to mask/unmask component", e);
      errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
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
