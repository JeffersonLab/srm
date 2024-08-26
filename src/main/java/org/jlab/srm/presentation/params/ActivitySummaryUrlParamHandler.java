package org.jlab.srm.presentation.params;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.srm.business.params.ActivitySummaryParams;
import org.jlab.srm.business.session.BeamDestinationFacade;
import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.business.session.RegionFacade;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Region;
import org.jlab.srm.persistence.entity.SystemEntity;

public class ActivitySummaryUrlParamHandler implements UrlParamHandler<ActivitySummaryParams> {

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;
  private final BeamDestinationFacade destinationFacade;
  private final CategoryFacade categoryFacade;
  private final SystemFacade systemFacade;
  private final RegionFacade regionFacade;

  public ActivitySummaryUrlParamHandler(
      HttpServletRequest request,
      BigInteger[] defaultDestinationIdArray,
      BeamDestinationFacade destinationFacade,
      CategoryFacade categoryFacade,
      SystemFacade systemFacade,
      RegionFacade regionFacade) {
    this.request = request;
    this.defaultDestinationIdArray = defaultDestinationIdArray;
    this.destinationFacade = destinationFacade;
    this.categoryFacade = categoryFacade;
    this.systemFacade = systemFacade;
    this.regionFacade = regionFacade;
  }

  @Override
  public ActivitySummaryParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");

    Date start, end;

    try {
      start = ParamConverter.convertFriendlyDateTime(request, "start");
      end = ParamConverter.convertFriendlyDateTime(request, "end");
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse date");
    }

    ActivitySummaryParams params = new ActivitySummaryParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setStart(start);
    params.setEnd(end);

    return params;
  }

  @Override
  public void validate(ActivitySummaryParams params) {
    // end must not come before start

    if (params.getStart() != null && params.getEnd() != null) {
      if (params.getEnd().before(params.getStart())) {
        throw new RuntimeException("End date must not come before start date");
      }
    }
  }

  @Override
  public void store(ActivitySummaryParams params) {
    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);

    session.setAttribute(
        "activitySummaryDestinationId[]",
        params.getDestinationIdArray() == null
            ? new BigInteger[0]
            : params.getDestinationIdArray());
    session.setAttribute("activitySummaryCategoryId[]", new BigInteger[] {params.getCategoryId()});
    session.setAttribute("activitySummarySystemId[]", new BigInteger[] {params.getSystemId()});
    session.setAttribute("activitySummaryRegionId[]", new BigInteger[] {params.getRegionId()});
    session.setAttribute("activitySummaryStart[]", new Date[] {params.getStart()});
    session.setAttribute("activitySummaryEnd[]", new Date[] {params.getEnd()});
  }

  @Override
  public ActivitySummaryParams defaults() {
    ActivitySummaryParams defaultParams = new ActivitySummaryParams();

    Calendar c = Calendar.getInstance();
    c.set(Calendar.MILLISECOND, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.HOUR_OF_DAY, 7);
    Date today = c.getTime();
    c.add(Calendar.DATE, -1);
    Date yesterday = c.getTime();

    defaultParams.setStart(yesterday);
    defaultParams.setEnd(today);

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);

    return defaultParams;
  }

  @Override
  public ActivitySummaryParams materialize() {
    ActivitySummaryParams defaultValues = defaults();

    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);
    BigInteger[] destinationIdArray =
        (BigInteger[]) session.getAttribute("activitySummaryDestinationId[]");
    BigInteger[] categoryIdArray =
        (BigInteger[]) session.getAttribute("activitySummaryCategoryId[]");
    BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("activitySummarySystemId[]");
    BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("activitySummaryRegionId[]");
    Date[] startArray = (Date[]) session.getAttribute("activitySummaryStart[]");
    Date[] endArray = (Date[]) session.getAttribute("activitySummaryEnd[]");

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger regionId = defaultValues.getRegionId();
    Date start = defaultValues.getStart();
    Date end = defaultValues.getEnd();

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

    if (startArray != null && startArray.length > 0) {
      start = startArray[0];
    }

    if (endArray != null && endArray.length > 0) {
      end = endArray[0];
    }

    ActivitySummaryParams params = new ActivitySummaryParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setStart(start);
    params.setEnd(end);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(ActivitySummaryParams params) {
    List<String> filters = new ArrayList<>();
    // SimpleDateFormat formatter = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());
    List<BeamDestination> destinationList = null;
    Category category = null;
    SystemEntity system = null;
    Region region = null;

    if (params.getDestinationIdArray() != null) {
      destinationList = destinationFacade.findMultiple(params.getDestinationIdArray());
    }

    if (params.getCategoryId() != null) {
      category = categoryFacade.find(params.getCategoryId());
    }

    if (params.getSystemId() != null) {
      system = systemFacade.find(params.getSystemId());
    }

    if (params.getRegionId() != null) {
      region = regionFacade.find(params.getRegionId());
    }

    if (params.getStart() != null && params.getEnd() != null) {
      filters.add(TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd()));
    } else if (params.getStart() != null) {
      filters.add("Starting " + TimeUtil.formatSmartSingleTime(params.getStart()));
    } else if (params.getEnd() != null) {
      filters.add("Before " + TimeUtil.formatSmartSingleTime(params.getEnd()));
    }

    /*if (params.getStart() != null) {
        filters.add("Start Date \"" + formatter.format(params.getStart()) + "\"");
    }

    if (params.getEnd() != null) {
        filters.add("End Date \"" + formatter.format(params.getEnd()) + "\"");
    }        */

    if (destinationList != null && !destinationList.isEmpty()) {
      String sublist = "\"" + destinationList.get(0).getName() + "\"";

      for (int i = 1; i < destinationList.size(); i++) {
        BeamDestination destination = destinationList.get(i);
        sublist = sublist + ", \"" + destination.getName() + "\"";
      }

      filters.add("Beam Destination " + sublist);
    }

    if (category != null) {
      filters.add("Category \"" + category.getName() + "\"");
    }

    if (system != null) {
      filters.add("System \"" + system.getName() + "\"");
    }

    if (region != null) {
      filters.add("Region \"" + region.getName() + "\"");
    }

    String message = "";

    if (!filters.isEmpty()) {
      message = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        message += " and " + filter;
      }
    }

    return message;
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
  public void redirect(HttpServletResponse response, ActivitySummaryParams params)
      throws IOException {
    ParamBuilder builder = new ParamBuilder();

    SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
    builder.add("start", IOUtil.nullOrFormat(params.getStart(), dateFormat));
    builder.add("end", IOUtil.nullOrFormat(params.getEnd(), dateFormat));
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
