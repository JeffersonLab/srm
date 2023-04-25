package org.jlab.hco.presentation.controller.data;

import org.jlab.hco.business.session.CategoryFacade;
import org.jlab.hco.persistence.entity.Category;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "Categories", urlPatterns = {"/data/categories"})
public class Categories extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            Categories.class.getName());
    @EJB
    CategoryFacade categoryFacade;

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
        String errorReason = null;
        Category category = null;
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
            BigInteger parentId = ParamConverter.convertBigInteger(request, "parent_id");
            BigInteger applicationId = ParamConverter.convertBigInteger(request, "application_id");
            jsonp = request.getParameter("jsonp");

            category = categoryFacade.findBranch(parentId, applicationId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to obtain category tree", e);
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        PrintWriter pw = response.getWriter();

        if (plaintextFormat) {
            response.setContentType("text/plain");

            List<Category> categoryList = new ArrayList<>();

            if (category != null) {
                categoryList.add(category);

                unwind(categoryList, category.getCategoryList());

                Collections.sort(categoryList);
            }

            if (errorReason == null) {
                for (Category c : categoryList) {
                    pw.write(c.getName());
                    pw.write(" - ");
                    pw.write(c.getCategoryId().toString());
                    pw.println();
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
                JsonObjectBuilder itemJson = Json.createObjectBuilder();
                if (category != null) {
                    itemJson.add("id", category.getCategoryId());
                    itemJson.add("name", category.getName());
                    if (category.getParentId() != null) {
                        itemJson.add("parent_id", category.getParentId().getCategoryId());
                    } else {
                        itemJson.add("parent_id", "");
                    }
                    itemJson.add("children", getChildrenArray(category));
                }
                json.add("stat", "ok");
                json.add("data", itemJson);
            } else {
                json.add("stat", "fail");
                json.add("error", errorReason);
            }

            String jsonStr = json.build().toString();

            if (jsonp != null) {
                jsonStr = jsonp + "(" + jsonStr + ");";
            }

            pw.write(jsonStr);
        }

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error");
        }
    }

    private JsonArrayBuilder getChildrenArray(Category category) {
        JsonArrayBuilder children = Json.createArrayBuilder();

        if (category.getCategoryList() != null) {
            for (Category child : category.getCategoryList()) {
                JsonObjectBuilder childJson = Json.createObjectBuilder();
                childJson.add("id", child.getCategoryId());
                childJson.add("name", child.getName());
                if (child.getParentId() != null) {
                    childJson.add("parent_id", child.getParentId().getCategoryId());
                } else {
                    childJson.add("parent_id", "");
                }
                childJson.add("children", getChildrenArray(child));
                children.add(childJson);
            }
        }

        return children;
    }

    private void unwind(List<Category> globalList, List<Category> currentList) {
        if (currentList != null) {
            globalList.addAll(currentList);

            for (Category c : currentList) {
                unwind(globalList, c.getCategoryList());
            }
        }
    }
}
