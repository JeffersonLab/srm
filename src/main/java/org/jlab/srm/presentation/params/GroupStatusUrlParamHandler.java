package org.jlab.srm.presentation.params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.srm.business.params.GroupStatusParams;

public class GroupStatusUrlParamHandler implements UrlParamHandler<GroupStatusParams> {

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;

  public GroupStatusUrlParamHandler(
      HttpServletRequest request, BigInteger[] defaultDestinationIdArray) {
    this.request = request;
    this.defaultDestinationIdArray = defaultDestinationIdArray;
  }

  @Override
  public GroupStatusParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    String chart = request.getParameter("chart");

    GroupStatusParams params = new GroupStatusParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setChart(chart);

    return params;
  }

  @Override
  public void validate(GroupStatusParams params) {
    // No rules (any number is allowed, and null is allowed
  }

  @Override
  public void store(GroupStatusParams params) {
    /* Note: We store each field individually as we want to re-use among screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);

    session.setAttribute(
        OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_DESTINATION_ID,
        params.getDestinationIdArray() == null
            ? new BigInteger[0]
            : params.getDestinationIdArray());
    session.setAttribute(
        OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_CATEGORY_ID,
        new BigInteger[] {params.getCategoryId()});
    session.setAttribute(
        OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_SYSTEM_ID,
        new BigInteger[] {params.getSystemId()});
    session.setAttribute(
        OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_REGION_ID,
        new BigInteger[] {params.getRegionId()});
    session.setAttribute("groupStatusReportChart[]", new String[] {params.getChart()});
  }

  @Override
  public GroupStatusParams defaults() {
    GroupStatusParams defaultParams = new GroupStatusParams();

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);

    defaultParams.setChart("table");

    return defaultParams;
  }

  @Override
  public GroupStatusParams materialize() {
    GroupStatusParams defaultValues = defaults();

    HttpSession session = request.getSession(true);
    BigInteger[] destinationIdArray =
        (BigInteger[])
            session.getAttribute(OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_DESTINATION_ID);
    BigInteger[] categoryIdArray =
        (BigInteger[])
            session.getAttribute(OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_CATEGORY_ID);
    BigInteger[] systemIdArray =
        (BigInteger[])
            session.getAttribute(OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_SYSTEM_ID);
    BigInteger[] regionIdArray =
        (BigInteger[])
            session.getAttribute(OverallStatusUrlParamHandler.SESSION_STATUS_REPORT_REGION_ID);
    String[] chartArray = (String[]) session.getAttribute("groupStatusReportChart[]");

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger regionId = defaultValues.getRegionId();
    String chart = defaultValues.getChart();

    if (destinationIdArray == null) {
      destinationIdArray = defaultValues.getDestinationIdArray();
    }

    if (categoryIdArray != null && categoryIdArray.length > 0) {
      categoryId = categoryIdArray[0];
    }

    if (systemIdArray != null && systemIdArray.length > 0) {
      systemId = systemIdArray[0];
    }

    if (regionIdArray != null && regionIdArray.length > 0) {
      regionId = regionIdArray[0];
    }

    if (chartArray != null && chartArray.length > 0) {
      chart = chartArray[0];
    }

    GroupStatusParams params = new GroupStatusParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setChart(chart);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(GroupStatusParams params) {
    return null;
  }

  /**
   * Sends a redirect response indicating the qualified URL. If calling this method from a Servlet
   * doGet method generally a return statement should immediately follow. This method is useful to
   * maintain a restful / bookmarkable URL for the user.
   *
   * @param response The Servlet response
   * @param params The parameter object
   * @throws IOException If unable to redirect
   */
  @Override
  public void redirect(HttpServletResponse response, GroupStatusParams params) throws IOException {
    ParamBuilder builder = new ParamBuilder();

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
    builder.add("chart", IOUtil.nullOrString(params.getChart()));
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
