package org.jlab.srm.presentation.controller.setup.ajax;

import org.jlab.srm.business.session.GroupResponsibilityFacade;
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
@WebServlet(name = "CreateGroupResponsibility", urlPatterns = {"/setup/ajax/create-group-responsibility"})
public class CreateGroupResponsibility extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            CreateGroupResponsibility.class.getName());

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

        BigInteger groupResponsibilityId = null;

        try {
            BigInteger systemId = ParamConverter.convertBigInteger(
                    request, "systemId");
            BigInteger groupId = ParamConverter.convertBigInteger(
                    request, "groupId");
            Boolean checklistRequired = ParamConverter.convertYNBoolean(request, "checklistRequired");
            BigInteger order = ParamConverter.convertBigInteger(
                    request, "order");

            logger.log(Level.FINE, "SystemId: {0}; GroupId: {1}, Order: {2}", new Object[]{systemId, groupId, order});

            groupResponsibilityId = responsibilityFacade.createNew(systemId, groupId, checklistRequired, order);

        } catch (EJBAccessException e) {
            logger.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";
        } catch (UserFriendlyException e) {
            logger.log(Level.WARNING, "Application Exception", e);
            errorReason = e.getMessage();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to create group responsibility", e);
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
            } else {
                gen.write("id", groupResponsibilityId);
            }
            gen.writeEnd();
        }
    }
}
