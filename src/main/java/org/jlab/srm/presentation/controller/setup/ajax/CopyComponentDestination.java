package org.jlab.srm.presentation.controller.setup.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentBeamDestinationFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "CopyComponentDestination",
    urlPatterns = {"/grid-copy"})
@Deprecated
public class CopyComponentDestination extends HttpServlet {

  private static final Logger logger = Logger.getLogger(CopyComponentDestination.class.getName());
  @EJB ComponentBeamDestinationFacade componentBeamDestinationFacade;

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
      BigInteger toComponentId = ParamConverter.convertBigInteger(request, "toComponentId");
      BigInteger fromComponentId = ParamConverter.convertBigInteger(request, "fromComponentId");

      // componentBeamDestinationFacade.copy(fromComponentId, toComponentId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to copy", e);
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
