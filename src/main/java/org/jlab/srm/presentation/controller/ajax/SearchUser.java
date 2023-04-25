package org.jlab.srm.presentation.controller.ajax;

import org.jlab.srm.business.session.StaffFacade;
import org.jlab.srm.persistence.entity.Staff;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "SearchUser", urlPatterns = {"/ajax/search-user"})
public class SearchUser extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            SearchUser.class.getName());
    @EJB
    StaffFacade staffFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorReason = null;

        List<Staff> staffList = null;
        Long totalRecords = null;

        try {
            String term = request.getParameter("term");
            Integer max = ParamConverter.convertInteger(request, "max");

            staffList = staffFacade.search(term, max);

            if (max != null) {
                totalRecords = staffFacade.count(term);
            } else {
                totalRecords = Long.valueOf(staffList.size());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to perform user search", e);
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        response.setContentType("application/json");

        PrintWriter pw = response.getWriter();

        JsonObjectBuilder json = Json.createObjectBuilder();

        if (errorReason == null) {
            JsonArrayBuilder staffJsonArray = Json.createArrayBuilder();
            if (staffList != null) {
                for (Staff staff : staffList) {
                    JsonObjectBuilder staffJson = Json.createObjectBuilder();
                    staffJson.add("id", staff.getStaffId());
                    staffJson.add("label", staff.getUsername()); // username might be null (though we know it can't be if username term search is required)
                    staffJson.add("value", staff.getUsername());
                    staffJson.add("username", staff.getUsername());
                    staffJson.add("first", staff.getFirstname() == null ? "" : staff.getFirstname());
                    staffJson.add("last", staff.getLastname() == null ? "" : staff.getLastname());
                    staffJsonArray.add(staffJson);
                }
            }
            json.add("records", staffJsonArray);
            json.add("total_records", totalRecords);
        } else {
            json.add("error", errorReason);
        }

        pw.write(json.build().toString());

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            LOGGER.log(Level.SEVERE, "PrintWriter Error");
        }
    }
}
