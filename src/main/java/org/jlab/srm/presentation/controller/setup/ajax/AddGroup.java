package org.jlab.srm.presentation.controller.setup.ajax;

import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.smoothness.business.util.ExceptionUtil;

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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "AddGroup", urlPatterns = {"/setup/ajax/add-group"})
public class AddGroup extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            AddGroup.class.getName());

    @EJB
    ResponsibleGroupFacade groupFacade;

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
            String leaderWorkgroup = request.getParameter("leaderWorkgroup");
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            groupFacade.add(name, description, leaderWorkgroup);
        } catch (EJBAccessException e) {
            logger.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to add group", e);
            Throwable rootCause = ExceptionUtil.getRootCause(e);

            /*System.out.println("Root Cause: " + rootCause);*/
            if (rootCause instanceof SQLException) {
                SQLException dbException = (SQLException) rootCause;

                /*System.out.println("ErrorCode: " + dbException.getErrorCode());
                System.out.println("State: " + dbException.getSQLState());
                System.out.println("Message: " + dbException.getMessage());*/
                if (dbException.getErrorCode() == 1 && "23000".equals(dbException.getSQLState())
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
