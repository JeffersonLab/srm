package org.jlab.srm.presentation.controller.setup;

import jakarta.ejb.EJB;
import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jlab.smoothness.business.service.SettingsService;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.AbstractFacade;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.business.session.ScheduledEmailer;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@WebServlet(
    name = "Email",
    urlPatterns = {"/setup/email"})
public class Email extends HttpServlet {

  @EJB ResponsibleGroupFacade groupFacade;
  @EJB ScheduledEmailer emailer;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<ResponsibleGroup> groupList =
        groupFacade.findAll(new AbstractFacade.OrderDirective("name"));

    List<Address> maskRequestAddresses = null;
    List<Address> feedbackAddresses = null;
    List<Address> activityAddresses = null;

    try {
      List<String> maskAddressList = SettingsService.cachedSettings.csv("EMAIL_MASK_REQUEST_LIST");
      maskRequestAddresses = new ArrayList<>();
      for (String maskAddress : maskAddressList) {
        maskRequestAddresses.add(new InternetAddress(maskAddress));
      }

      String prefix = getServletContext().getInitParameter("appSpecificEnvPrefix");

      String feedbackCsv = System.getenv(prefix + "_FEEDBACK_TO_ADDRESS_CSV");

      feedbackAddresses = new ArrayList<>();

      if (feedbackCsv != null) {
        for (String s : feedbackCsv.split(",")) {
          feedbackAddresses.add(new InternetAddress(s.trim()));
        }
      }

      List<String> activityAddressesList =
          SettingsService.cachedSettings.csv("EMAIL_ACTIVITY_LIST");
      activityAddresses = new ArrayList<>();

      for (String activityAddress : activityAddressesList) {
        activityAddresses.add(new InternetAddress(activityAddress.trim()));
      }

    } catch (AddressException e) {
      throw new ServletException(e);
    }

    request.setAttribute("maskRequestAddresses", maskRequestAddresses);
    request.setAttribute("feedbackAddresses", feedbackAddresses);
    request.setAttribute("activityAddresses", activityAddresses);
    request.setAttribute("schedulerEnabled", emailer.isEnabled());
    request.setAttribute("groupList", groupList);

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/setup/email.jsp")
        .forward(request, response);
  }

  /**
   * Handles the HTTP <code>Post</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
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
