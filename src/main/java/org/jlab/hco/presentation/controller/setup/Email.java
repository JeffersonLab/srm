package org.jlab.hco.presentation.controller.setup;

import org.jlab.hco.business.session.AbstractFacade;
import org.jlab.hco.business.session.HcoSettingsFacade;
import org.jlab.hco.business.session.ResponsibleGroupFacade;
import org.jlab.hco.business.session.ScheduledEmailer;
import org.jlab.hco.persistence.entity.HcoSettings;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(name = "Email", urlPatterns = {"/setup/email"})
public class Email extends HttpServlet {

    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    ScheduledEmailer emailer;
    @EJB
    HcoSettingsFacade settingsFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ResponsibleGroup> groupList = groupFacade.findAll(new AbstractFacade.OrderDirective("name"));

        HcoSettings settings = settingsFacade.findSettings();

        List<Address> maskRequestAddresses = null;
        List<Address> feedbackAddresses = null;
        List<Address> activityAddresses = null;

        try {
            maskRequestAddresses = settings.getMaskRequestEmailAddresses();
            feedbackAddresses = settings.getFeedbackEmailAddresses();
            activityAddresses = settings.getActivityEmailAddresses();
        } catch (AddressException e) {
            throw new ServletException(e);
        }

        request.setAttribute("maskRequestAddresses", maskRequestAddresses);
        request.setAttribute("feedbackAddresses", feedbackAddresses);
        request.setAttribute("activityAddresses", activityAddresses);
        request.setAttribute("schedulerEnabled", emailer.isEnabled());
        request.setAttribute("groupList", groupList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/setup/email.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>Post</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean enabled;

        try {
            enabled = ParamConverter.convertYNBoolean(request, "schedulerEnabled");
        } catch (Exception e) {
            throw new ServletException("Unable to convert parameter", e);
        }

        if (enabled == null) {
            throw new ServletException("schedulerEnabled must not be empty");
        }

        emailer.setEnabled(enabled);

        response.sendRedirect(response.encodeRedirectURL("email"));
    }
}
