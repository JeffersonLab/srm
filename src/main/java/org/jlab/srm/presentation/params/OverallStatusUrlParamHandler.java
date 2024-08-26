package org.jlab.srm.presentation.params;

import java.io.IOException;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.srm.business.params.OverallStatusParams;

public class OverallStatusUrlParamHandler implements UrlParamHandler<OverallStatusParams> {

  public static final String SESSION_STATUS_REPORT_DESTINATION_ID = "statusReportDestinationId[]";
  public static final String SESSION_STATUS_REPORT_CATEGORY_ID = "statusReportCategoryId[]";
  public static final String SESSION_STATUS_REPORT_SYSTEM_ID = "statusReportSystemId[]";
  public static final String SESSION_STATUS_REPORT_REGION_ID = "statusReportRegionId[]";

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;

  public OverallStatusUrlParamHandler(
      HttpServletRequest request, BigInteger[] defaultDestinationIdArray) {
    this.request = request;
    this.defaultDestinationIdArray = defaultDestinationIdArray;
  }

  @Override
  public OverallStatusParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

    OverallStatusParams params = new OverallStatusParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);

    return params;
  }

  @Override
  public void validate(OverallStatusParams params) {
    // No rules (any number is allowed, and null is allowed
  }

  @Override
  public void store(OverallStatusParams params) {
    /* Note: We store each field individually as we want to re-use among screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);

    session.setAttribute(
        SESSION_STATUS_REPORT_DESTINATION_ID,
        params.getDestinationIdArray() == null
            ? new BigInteger[0]
            : params.getDestinationIdArray());
    session.setAttribute(
        SESSION_STATUS_REPORT_CATEGORY_ID, new BigInteger[] {params.getCategoryId()});
    session.setAttribute(SESSION_STATUS_REPORT_SYSTEM_ID, new BigInteger[] {params.getSystemId()});
    session.setAttribute(SESSION_STATUS_REPORT_REGION_ID, new BigInteger[] {params.getRegionId()});
    session.setAttribute("overallStatusReportGroupId[]", new BigInteger[] {params.getGroupId()});
  }

  @Override
  public OverallStatusParams defaults() {
    OverallStatusParams defaultParams = new OverallStatusParams();

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);

    return defaultParams;
  }

  @Override
  public OverallStatusParams materialize() {
    OverallStatusParams defaultValues = defaults();

    HttpSession session = request.getSession(true);
    BigInteger[] destinationIdArray =
        (BigInteger[]) session.getAttribute(SESSION_STATUS_REPORT_DESTINATION_ID);
    BigInteger[] categoryIdArray =
        (BigInteger[]) session.getAttribute(SESSION_STATUS_REPORT_CATEGORY_ID);
    BigInteger[] systemIdArray =
        (BigInteger[]) session.getAttribute(SESSION_STATUS_REPORT_SYSTEM_ID);
    BigInteger[] regionIdArray =
        (BigInteger[]) session.getAttribute(SESSION_STATUS_REPORT_REGION_ID);
    BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("overallStatusReportGroupId[]");

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger regionId = defaultValues.getRegionId();
    BigInteger groupId = defaultValues.getGroupId();

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

    if (groupIdArray != null && groupIdArray.length > 0) {
      groupId = groupIdArray[0];
    }

    OverallStatusParams params = new OverallStatusParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(OverallStatusParams params) {
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
  public void redirect(HttpServletResponse response, OverallStatusParams params)
      throws IOException {
    ParamBuilder builder = new ParamBuilder();

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
    builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
