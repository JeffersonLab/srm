package org.jlab.srm.presentation.controller.setup.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.SQLException;
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
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.SavedSignoffFacade;

/**
 * @author ryans
 */
@WebServlet(
    name = "AddSavedDowngrade",
    urlPatterns = {"/setup/ajax/add-saved-signoff"})
public class AddSavedSignoff extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AddSavedSignoff.class.getName());

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

    BigInteger savedSignoffId = null;

    try {
      BigInteger typeId = ParamConverter.convertBigInteger(request, "typeId");
      String signoffName = request.getParameter("signoffName");
      BigInteger signoffStatusId = ParamConverter.convertBigInteger(request, "signoffStatusId");
      String comments = request.getParameter("comments");
      BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
      BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
      BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
      BigInteger filterStatusId = ParamConverter.convertBigInteger(request, "filterStatusId");
      String componentName = request.getParameter("componentName");

      savedSignoffId =
          savedSignoffFacade.add(
              typeId,
              signoffName,
              signoffStatusId,
              comments,
              systemId,
              groupId,
              regionId,
              filterStatusId,
              componentName);

    } catch (EJBAccessException e) {
      logger.log(Level.WARNING, "Not authorized", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      logger.log(Level.WARNING, "Application Exception", e);
      errorReason = e.getMessage();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to add saved signoff", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      if (rootCause instanceof SQLException) {
        SQLException dbException = (SQLException) rootCause;

        if (dbException.getErrorCode() == 1 && "23000".equals(dbException.getSQLState())) {
          errorReason = "There is already a saved signoff with that name";
        } else {
          errorReason = "Database exception";
        }
      } else {
        errorReason = "Something unexpected happened";
      }
    }

    response.setContentType("text/xml");

    PrintWriter pw = response.getWriter();

    String xml;

    if (errorReason == null) {
      xml =
          "<response><span class=\"status\">Success</span><span class=\"saved-signoff-id\">"
              + savedSignoffId
              + "</span></response>";
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
