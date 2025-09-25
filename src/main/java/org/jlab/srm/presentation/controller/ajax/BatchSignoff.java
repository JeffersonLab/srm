package org.jlab.srm.presentation.controller.ajax;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.GroupSignoffFacade;
import org.jlab.srm.business.session.GroupSignoffFacade.SignoffCascadeRule;
import org.jlab.srm.business.session.GroupSignoffFacade.SignoffValidateRule;
import org.jlab.srm.persistence.entity.Status;

/**
 * @author ryans
 */
@WebServlet(
    name = "BatchSignoff",
    urlPatterns = {"/ajax/batch-signoff"})
public class BatchSignoff extends HttpServlet {

  private static final Logger logger = Logger.getLogger(BatchSignoff.class.getName());
  @EJB GroupSignoffFacade signoffFacade;

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
    Long prId = null;

    try {
      BigInteger statusId = ParamConverter.convertBigInteger(request, "statusId");
      String comment = request.getParameter("comment");

      boolean needsAttention = "true".equals(request.getParameter("needsAttention"));

      if (statusId == null) {
        throw new UserFriendlyException("Please select status");
      }

      String[] componentIdArray = request.getParameterValues("componentId[]");
      String[] groupIdArray = request.getParameterValues("groupId[]");

      if (componentIdArray == null || groupIdArray == null) {
        throw new UserFriendlyException("Please select group signoff");
      }

      if (componentIdArray.length != groupIdArray.length) {
        throw new UserFriendlyException("componentId[] and groupId[] must be of same length");
      }

      List<BigInteger> componentIdList = new ArrayList<>();
      List<BigInteger> groupIdList = new ArrayList<>();

      fillInLists(componentIdList, groupIdList, componentIdArray, groupIdArray);

      BigInteger readyCascade = ParamConverter.convertBigInteger(request, "readyCascade");
      BigInteger checkedCascade = ParamConverter.convertBigInteger(request, "checkedCascade");

      SignoffCascadeRule cascadeRule = new SignoffCascadeRule();
      cascadeRule.cascade = true;
      cascadeRule.readyCascade = readyCascade;
      cascadeRule.checkedCascade = checkedCascade;

      SignoffValidateRule validateRule = new SignoffValidateRule();
      validateRule.twoStepSignoff = true;
      validateRule.atLeastOneNonNa = true;
      validateRule.requiredChecklistPublished = true;
      validateRule.disallowModifyMask = true;

      prId =
          signoffFacade.updateSignoff(
              componentIdList,
              groupIdList,
              Status.FROM_ID(statusId),
              comment,
              needsAttention,
              cascadeRule,
              validateRule);
    } catch (EJBAccessException e) {
      logger.log(Level.WARNING, "Auth Exception", e);
      errorReason = "Not authorized";
    } catch (UserFriendlyException e) {
      logger.log(Level.WARNING, "Application Exception", e);
      errorReason = e.getUserMessage();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to perform batch signoff", e);
      errorReason = e.getClass().getSimpleName();
    }

    response.setContentType("text/xml");

    PrintWriter pw = response.getWriter();

    String xml;

    if (errorReason == null) {
      xml =
          "<response><span class=\"status\">Success</span><span class=\"pr-id\">"
              + prId
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

  private void fillInLists(
      List<BigInteger> componentIdList,
      List<BigInteger> groupIdList,
      String[] componentIdArray,
      String[] groupIdArray) {
    int i = 0;
    for (String componentIdStr : componentIdArray) {
      String groupIdStr = groupIdArray[i++];

      BigInteger componentId = toBigInteger(componentIdStr);
      BigInteger groupId = toBigInteger(groupIdStr);

      componentIdList.add(componentId);
      groupIdList.add(groupId);
    }
  }

  private BigInteger toBigInteger(String value) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null");
    }

    return new BigInteger(value);
  }
}
