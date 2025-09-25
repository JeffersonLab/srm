package org.jlab.srm.presentation.controller.setup.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.SavedSignoffFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "SubmitMultipleSavedSignoffs",
    urlPatterns = {"/setup/ajax/signoff-multiple-saved"})
public class SubmitMultipleSavedSignoffs extends HttpServlet {

  private static final Logger logger =
      Logger.getLogger(SubmitMultipleSavedSignoffs.class.getName());

  @EJB SavedSignoffFacade savedSignoffFacade;

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
      BigInteger[] savedSignoffIdArray =
          ParamConverter.convertBigIntegerArray(request, "saved-signoff-id-array[]");

      Date maxLastModified = ParamConverter.convertFriendlyDateTime(request, "max-modified");

      savedSignoffFacade.multipleSignoffs(savedSignoffIdArray, maxLastModified);
    } catch (EJBAccessException e) {
      logger.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      logger.log(Level.WARNING, "Application Exception", e);
      errorReason = e.getUserMessage();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to remove saved downgrade", e);
      errorReason = e.getClass().getSimpleName();
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
