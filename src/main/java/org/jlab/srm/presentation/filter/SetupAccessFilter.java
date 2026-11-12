package org.jlab.srm.presentation.filter;

import jakarta.ejb.EJB;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.srm.business.session.CategoryFacade;

/**
 * Lets only an admin or a branch admin use the setup pages.
 *
 * <p>The branch admin roles are data, each branch's admin role, so web.xml cannot list them: its
 * security constraint on the same URL patterns only requires a login, and this filter then checks
 * the roles. Keep the patterns here and there the same.
 */
@WebFilter(
    filterName = "SetupAccessFilter",
    urlPatterns = {"/setup/*", "/reports/inventory-activity/*"},
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class SetupAccessFilter extends HttpFilter {

  @EJB CategoryFacade categoryFacade;

  @Override
  protected void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (categoryFacade.isAdminOrAnyBranchAdmin()) {
      chain.doFilter(request, response);
    } else {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
  }
}
