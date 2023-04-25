package org.jlab.hco.presentation.controller.ajax;

import org.jlab.hco.business.session.MaskingRequestFacade;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "SaveMaskingRequest", urlPatterns = {"/ajax/save-masking-request"})
public class SaveMaskingRequest extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SaveMaskingRequest.class.getName());
    @EJB
    MaskingRequestFacade requestFacade;

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
            String reason = request.getParameter("reason");
            Date expirationDate = ParamConverter.convertFriendlyDateTime(request, "expiration-date");

            if (reason == null) {
                throw new UserFriendlyException("Please provide a reason");
            }

            if (expirationDate == null) {
                throw new UserFriendlyException("Please provide an expiration date");
            }

            String[] componentIdArray = request.getParameterValues("componentId[]");

            List<BigInteger> componentIdList = new ArrayList<>();

            fillInLists(componentIdList, componentIdArray);

            requestFacade.createRequest(componentIdList, reason, expirationDate);
        } catch (EJBAccessException e) {
            LOGGER.log(Level.WARNING, "Auth Exception", e);
            errorReason = e.getMessage();
        } catch (UserFriendlyException e) {
            LOGGER.log(Level.WARNING, "Application Exception", e);
            errorReason = e.getMessage();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to create masking request", e);
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
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
            LOGGER.log(Level.SEVERE, "PrintWriter Error");
        }
    }

    private void fillInLists(List<BigInteger> componentIdList, String[] componentIdArray) {

        for (String componentIdStr : componentIdArray) {

            BigInteger componentId = toBigInteger(componentIdStr);

            componentIdList.add(componentId);
        }
    }

    private BigInteger toBigInteger(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return new BigInteger(value);
    }
}
