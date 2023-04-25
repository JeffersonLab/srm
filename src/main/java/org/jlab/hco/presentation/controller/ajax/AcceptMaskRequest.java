package org.jlab.hco.presentation.controller.ajax;

import org.jlab.hco.business.session.MaskingRequestFacade;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "AcceptMaskRequest", urlPatterns = {"/ajax/accept-mask-request"})
public class AcceptMaskRequest extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AcceptMaskRequest.class.getName());

    @EJB
    MaskingRequestFacade maskingRequestFacade;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorReason = null;

        try {
            BigInteger maskRequestId = ParamConverter.convertBigInteger(
                    request, "requestId");
            String reason = request.getParameter("reason");
            Date expiration = ParamConverter.convertFriendlyDateTime(request, "expiration");

            maskingRequestFacade.acceptMaskRequest(maskRequestId, reason, expiration);
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
                errorReason = "Unknown Exception of type: "
                        + e.getCausedByException().getClass().getSimpleName();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to accept masking requset", e);
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
                    .write("stat", stat); // This is unnecessary - if 200 OK then it worked
            if (errorReason != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gen.write("error", errorReason);
            }
            gen.writeEnd();
        }
    }
}
