package org.jlab.srm.presentation.controller.setup.ajax;

import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.xa.XAException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "EditComponent", urlPatterns = {"/setup/ajax/edit-component"})
public class EditComponent extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            EditComponent.class.getName());

    @EJB
    ComponentFacade componentFacade;

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
            BigInteger componentId = ParamConverter.convertBigInteger(
                    request, "componentId");
            BigInteger systemId = ParamConverter.convertBigInteger(
                    request, "systemId");
            String name = request.getParameter("name");
            BigInteger regionId = ParamConverter.convertBigInteger(
                    request, "regionId");
            boolean force = ParamUtil.convertAndValidateYNBoolean(request, "force", false);

            componentFacade.editComponent(systemId, componentId, name, regionId, force);
        } catch (EJBAccessException e) {
            logger.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to add component", e);
            Throwable rootCause = ExceptionUtil.getRootCause(e);

            /*System.out.println("Root Cause: " + rootCause);*/

            if (rootCause instanceof SQLException) {
                SQLException dbException = (SQLException) rootCause;

                /*System.out.println("ErrorCode: " + dbException.getErrorCode());
                System.out.println("State: " + dbException.getSQLState());
                System.out.println("Message: " + dbException.getMessage());*/

                if (dbException.getErrorCode() == 1 && "23000".equals(dbException.getSQLState()) && dbException.getMessage().contains("COMPONENT_AK1")) {
                    errorReason = "There is already a component in this system with that name";
                } else if (dbException.getErrorCode() == 2290 && "23000".equals(dbException.getSQLState()) && dbException.getMessage().contains("COMPONENT_CK4")) {
                    errorReason = "Component name cannot contain an asterisk because we use the asterisk to denote unpowered components";
                } /*else if (dbException.getErrorCode() == 2292 && "23000".equals(dbException.getSQLState()) && dbException.getMessage().contains("GROUP_SIGNOFF_FK1")) { 
                    errorReason = "Cannot move component to a new subsystem because the component has signoffs.  Select the force option if a loss of signoffs / signoff history is acceptable.";
                }*/ else {
                    errorReason = "Database exception";
                }
            } else if (rootCause instanceof XAException) { /*We're likely dealing with deferred constraint*/
                XAException txException = (XAException) rootCause;

                if (txException.getMessage().contains("GROUP_SIGNOFF_FK1")) {
                    errorReason = "Cannot reassign component to a new system because the component has signoffs.  Select the force option if a loss of signoffs / signoff history is acceptable.";
                } else {
                    errorReason = "Unable to complete database transaction";
                }

            } else {
                errorReason = "Something unexpected happened";
            }
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span></response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error");
        }
    }
}
