package org.jlab.hco.presentation.controller.setup.ajax;

import org.jlab.hco.business.session.GroupSignoffFacade;
import org.jlab.hco.persistence.entity.Status;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "AddBulkSignoff", urlPatterns = {"/setup/ajax/bulk-signoff"})
public class AddBulkSignoff extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddBulkSignoff.class.getName());
    @EJB
    GroupSignoffFacade signoffFacade;

    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
            BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
            BigInteger statusId = ParamConverter.convertBigInteger(request, "statusId");
            String comment = request.getParameter("comment");

            if (statusId == null) {
                throw new UserFriendlyException("Please select status");
            }

            signoffFacade.bulkSignoff(systemId, Status.FROM_ID(statusId), comment);
        } catch (EJBAccessException e) {
            LOGGER.log(Level.WARNING, "Auth Exception", e);
            errorReason = e.getMessage();
        } catch (UserFriendlyException e) {
            LOGGER.log(Level.WARNING, "Application Exception", e);
            errorReason = e.getMessage();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to perform batch signoff", e);
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
