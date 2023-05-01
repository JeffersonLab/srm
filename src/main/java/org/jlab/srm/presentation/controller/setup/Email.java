package org.jlab.srm.presentation.controller.setup;

import org.jlab.srm.business.session.AbstractFacade;
import org.jlab.srm.business.session.SettingsFacade;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.business.session.ScheduledEmailer;
import org.jlab.srm.persistence.entity.Settings;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
    SettingsFacade settingsFacade;

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

        Settings settings = settingsFacade.findSettings();

        List<Address> maskRequestAddresses = null;
        List<Address> feedbackAddresses = null;
        List<Address> activityAddresses = null;

        try {
            maskRequestAddresses = settings.getMaskRequestEmailAddresses();

            String prefix = getServletContext().getInitParameter("appSpecificEnvPrefix");

            String feedbackCsv = System.getenv(prefix + "_FEEDBACK_TO_ADDRESS_CSV");

            feedbackAddresses = new ArrayList<>();

            for(String s: feedbackCsv.split(",")) {
                feedbackAddresses.add(new InternetAddress(s.trim()));
            }

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
