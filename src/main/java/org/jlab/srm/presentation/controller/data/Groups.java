package org.jlab.srm.presentation.controller.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.enumeration.Include;

/**
 * @author ryans
 */
@WebServlet(
    name = "Groups",
    urlPatterns = {"/data/groups"})
public class Groups extends HttpServlet {

  private static final Logger logger = Logger.getLogger(Groups.class.getName());
  @EJB ResponsibleGroupFacade groupFacade;

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
    String errorReason = null;
    List<ResponsibleGroup> groupList = null;
    String jsonp = null;
    String accept = request.getHeader("Accept");
    boolean plaintextFormat = false;
    /*Look at Accept header and if we find text/plain before application/json then we'll do text/plain.  Otherwise we default to application/json*/
    if (accept != null) {
      String[] tokens = accept.split(",");

      for (String token : tokens) {
        if (token != null) {
          if (token.startsWith("text/plain")) {
            plaintextFormat = true;
            break;
          } else if (token.startsWith("application/json")) {
            break;
          }
        }
      }
    }

    /*We can override the HTTP header with a URL parameter*/
    String acceptOverride = request.getParameter("accept");

    if ("plain".equals(acceptOverride)) {
      plaintextFormat = true;
    }

    try {
      BigInteger systemId = ParamConverter.convertBigInteger(request, "system_id");
      Include archived = convertInclude(request, "archived");
      jsonp = request.getParameter("jsonp");

      groupList = groupFacade.findWithLeaders(systemId, archived);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unable to obtain group list", e);
      errorReason = e.getClass().getSimpleName();
    }

    PrintWriter pw = response.getWriter();

    if (plaintextFormat) {
      response.setContentType("text/plain");

      if (errorReason == null) {
        if (groupList != null) {
          for (ResponsibleGroup group : groupList) {
            pw.write(group.getName());
            pw.write(" - ");
            pw.write(group.getGroupId().toString());
            pw.println();
          }
        }
      } else {
        pw.write("Unable to service request");
        pw.println();
        pw.write(errorReason);
      }

    } else {
      response.setContentType("application/json");

      JsonObjectBuilder json = Json.createObjectBuilder();

      if (errorReason == null) {
        JsonArrayBuilder itemJsonArray = Json.createArrayBuilder();
        if (groupList != null) {
          for (ResponsibleGroup group : groupList) {
            JsonObjectBuilder itemJson = Json.createObjectBuilder();
            itemJson.add("id", group.getGroupId());
            itemJson.add("name", group.getName());
            itemJson.add("archived", group.isArchived());
            JsonArrayBuilder emailArray = Json.createArrayBuilder();
            UserAuthorizationService userService = UserAuthorizationService.getInstance();
            List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
            if (userList != null) {
              for (User leader : userList) {
                emailArray.add(leader.getUsername() + "@jlab.org");
              }
            }
            itemJson.add("leader_emails", emailArray);

            itemJsonArray.add(itemJson);
          }
        }
        json.add("stat", "ok");
        json.add("data", itemJsonArray);
      } else {
        json.add("stat", "fail");
        json.add("error", errorReason);
      }

      String jsonStr = json.build().toString();

      if (jsonp != null) {
        jsonStr = "jsonp" + "(" + jsonStr + ");";
      }

      pw.write(jsonStr);
    }

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      logger.log(Level.SEVERE, "PrintWriter Error");
    }
  }

  public static Include convertInclude(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    Include result = null;

    if (value != null && !value.isBlank()) {
      result = Include.valueOf(value);
    }

    return result;
  }
}
