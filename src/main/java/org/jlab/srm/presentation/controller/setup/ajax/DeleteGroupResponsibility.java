package org.jlab.srm.presentation.controller.setup.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.GroupResponsibilityFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "DeleteGroupResponsibility",
    urlPatterns = {"/setup/ajax/delete-group-responsibility"})
public class DeleteGroupResponsibility extends HttpServlet {

  private static final Logger logger = Logger.getLogger(DeleteGroupResponsibility.class.getName());

  @EJB GroupResponsibilityFacade responsibilityFacade;

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String errorReason = null;

    try {
      BigInteger groupResponsibilityId =
          ParamConverter.convertBigInteger(request, "group-responsibility-id");

      logger.log(Level.FINE, "GroupResponsibilityId: {0}", groupResponsibilityId);

      responsibilityFacade.delete(groupResponsibilityId);

    } catch (EJBAccessException e) {
      logger.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      logger.log(Level.WARNING, "Application Exception", e);
      errorReason = e.getMessage();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to delete group responsibility", e);
      errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    response.setContentType("text/xml");

    PrintWriter pw = response.getWriter();

    String xml;

    if (errorReason == null) {
      xml = "<response><span class=\"status\">Success</span></response>";
    } else {
      xml =
          "<response><span class=\"status\">Error</span><span "
              + "class=\"reason\">"
              + errorReason
              + "</span></response>";
    }

    pw.write(xml);

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      logger.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
