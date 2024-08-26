package org.jlab.srm.presentation.controller.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.MaskingRequestFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "DenyMaskRequest",
    urlPatterns = {"/ajax/deny-mask-request"})
public class DenyMaskRequest extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(DenyMaskRequest.class.getName());

  @EJB MaskingRequestFacade maskingRequestFacade;

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
      BigInteger maskRequestId = ParamConverter.convertBigInteger(request, "requestId");
      maskingRequestFacade.denyMaskRequest(maskRequestId);
    } catch (EJBAccessException e) {
      LOGGER.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to deny masking requset", e);
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
