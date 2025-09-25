package org.jlab.srm.presentation.controller.reports.component;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.business.session.ComponentFacade;
import org.jlab.srm.business.session.GroupSignoffFacade;
import org.jlab.srm.business.util.EntityUtil;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.GroupSignoff;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@WebServlet(
    name = "ComponentDetail",
    urlPatterns = {"/reports/component/detail"})
public class ComponentDetail extends HttpServlet {

  @EJB ComponentFacade componentFacade;
  @EJB GroupSignoffFacade signoffFacade;
  @EJB CategoryFacade categoryFacade;

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
      Map<ResponsibleGroup, GroupSignoff> map =
          EntityUtil.getGroupSignoffMap(component.getGroupSignoffList());

      Category category =
          categoryFacade.findBranchReverse(component.getSystem().getSystemId(), BigInteger.ONE);

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

    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/reports/component/detail.jsp")
        .forward(request, response);
  }
}
