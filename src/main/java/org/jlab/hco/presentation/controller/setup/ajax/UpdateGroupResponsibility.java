package org.jlab.hco.presentation.controller.setup.ajax;

import org.jlab.hco.business.session.GroupResponsibilityFacade;
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
@WebServlet(name = "UpdateGroupResponsibility", urlPatterns = {"/setup/ajax/update-group-responsibility"})
public class UpdateGroupResponsibility extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            UpdateGroupResponsibility.class.getName());

    @EJB
    GroupResponsibilityFacade responsibilityFacade;

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
            BigInteger groupResponsibilityId = ParamConverter.convertBigInteger(
                    request, "groupResponsibilityId");
            Boolean checklistRequired = ParamConverter.convertYNBoolean(request, "checklistRequired");

            logger.log(Level.FINE, "GroupResponsibilitySystemId: {0}", new Object[]{groupResponsibilityId});

            responsibilityFacade.update(groupResponsibilityId, checklistRequired);

        } catch (EJBAccessException e) {
            logger.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";
        } catch (UserFriendlyException e) {
            logger.log(Level.WARNING, "Application Exception", e);
            errorReason = e.getMessage();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to update group responsibility", e);
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
