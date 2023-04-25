package org.jlab.hco.presentation.controller.reports.component;

import org.jlab.hco.business.session.CategoryFacade;
import org.jlab.hco.business.session.ComponentFacade;
import org.jlab.hco.business.session.GroupSignoffFacade;
import org.jlab.hco.business.util.EntityUtil;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.Component;
import org.jlab.hco.persistence.entity.GroupSignoff;
import org.jlab.hco.persistence.entity.ResponsibleGroup;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ryans
 */
@WebServlet(name = "ComponentDetail", urlPatterns = {"/reports/component/detail"})
public class ComponentDetail extends HttpServlet {

    @EJB
    ComponentFacade componentFacade;
    @EJB
    GroupSignoffFacade signoffFacade;
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
        BigInteger componentId = ParamConverter.convertBigInteger(request, "componentId");
        String name = request.getParameter("name");

        Component component = null;
        boolean editable = false;

        if (componentId != null) {
            component = componentFacade.findDetail(componentId);
        } else if (name != null && !name.trim().isEmpty()) {
            component = componentFacade.findDetail(name);
        }

        if (component != null) {
            Map<ResponsibleGroup, GroupSignoff> map = EntityUtil.getGroupSignoffMap(
                    component.getGroupSignoffList());

            Category category
                    = categoryFacade.findBranchReverse(component.getSystem().getSystemId(),
                    BigInteger.ONE);

            List<Category> categoryBranch = new ArrayList<>();
            categoryBranch.add(category);

            Category cat = category;
            int i = 0;
            while (cat.getParentId() != null) {
                cat = cat.getParentId();
                categoryBranch.add(cat);
                i++;

                if (i > 20) {
                    System.err.println("Category hierarchy depth too great!");
                    break;
                }
            }

            Collections.reverse(categoryBranch);

            Category branchRoot = categoryFacade.findBranchRoot(category);
            editable = componentFacade.isAdminOrBranchAdmin(branchRoot);

            request.setAttribute("component", component);
            request.setAttribute("editable", editable);
            request.setAttribute("signoffMap", map);
            request.setAttribute("categoryBranch", categoryBranch);
        }

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/reports/component/detail.jsp").forward(request, response);
    }
}
