package org.jlab.srm.presentation.params;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.srm.business.params.MaskRequestParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.enumeration.MaskingRequestStatus;
import org.jlab.srm.presentation.util.HcoParamConverter;

public class MaskRequestUrlParamHandler implements UrlParamHandler<MaskRequestParams> {

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;
  private final CategoryFacade categoryFacade;
  private final SystemFacade systemFacade;
  private final BeamDestinationFacade destinationFacade;
  private final RegionFacade regionFacade;
  private final ResponsibleGroupFacade groupFacade;

  public MaskRequestUrlParamHandler(
      HttpServletRequest request,
      BigInteger[] defaultDestinationIdArray,
      BeamDestinationFacade destinationFacade,
      CategoryFacade categoryFacade,
      SystemFacade systemFacade,
      RegionFacade regionFacade,
      ResponsibleGroupFacade groupFacade) {
    this.request = request;
    this.defaultDestinationIdArray = defaultDestinationIdArray;
    this.destinationFacade = destinationFacade;
    this.categoryFacade = categoryFacade;
    this.systemFacade = systemFacade;
    this.regionFacade = regionFacade;
    this.groupFacade = groupFacade;
  }

  @Override
  public MaskRequestParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
    String reason = request.getParameter("reason");
    MaskingRequestStatus status = HcoParamConverter.convertMaskingRequestStatus(request, "status");

    MaskRequestParams params = new MaskRequestParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);
    params.setReason(reason);
    params.setStatus(status);

    return params;
  }

  @Override
  public void validate(MaskRequestParams params) {
    // No rules
  }

  @Override
  public void store(MaskRequestParams params) {
    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);

    session.setAttribute(
        "maskedDestinationId[]",
        params.getDestinationIdArray() == null
            ? new BigInteger[0]
            : params.getDestinationIdArray());
    session.setAttribute("maskedCategoryId[]", new BigInteger[] {params.getCategoryId()});
    session.setAttribute("maskedSystemId[]", new BigInteger[] {params.getSystemId()});
    session.setAttribute("maskedRegionId[]", new BigInteger[] {params.getRegionId()});
    session.setAttribute("maskedGroupId[]", new BigInteger[] {params.getGroupId()});
    session.setAttribute("maskedReason[]", new String[] {params.getReason()});
    session.setAttribute("maskedRequestStatus[]", new MaskingRequestStatus[] {params.getStatus()});
  }

  @Override
  public MaskRequestParams defaults() {
    MaskRequestParams defaultParams = new MaskRequestParams();

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);
    defaultParams.setStatus(MaskingRequestStatus.PENDING);

    return defaultParams;
  }

  @Override
  public MaskRequestParams materialize() {
    MaskRequestParams defaultValues = defaults();

    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);
    BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute("maskedDestinationId[]");
    BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute("maskedCategoryId[]");
    BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("maskedSystemId[]");
    BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("maskedRegionId[]");
    BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("maskedGroupId[]");
    String[] reasonArray = (String[]) session.getAttribute("maskedReason[]");
    MaskingRequestStatus[] statusArray =
        (MaskingRequestStatus[]) session.getAttribute("maskedRequestStatus[]");

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger regionId = defaultValues.getRegionId();
    BigInteger groupId = defaultValues.getGroupId();
    String reason = defaultValues.getReason();
    MaskingRequestStatus status = defaultValues.getStatus();

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

    if (reasonArray != null && reasonArray.length > 0) {
      reason = reasonArray[0];
    }

    if (statusArray != null && statusArray.length > 0) {
      status = statusArray[0];
    }

    MaskRequestParams params = new MaskRequestParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);
    params.setReason(reason);
    params.setStatus(status);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(MaskRequestParams params) {
    List<String> filters = new ArrayList<>();

    List<BeamDestination> destinationList = null;
    Category category = null;
    SystemEntity system = null;
    Region region = null;
    ResponsibleGroup group = null;

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

    if (params.getGroupId() != null) {
      group = groupFacade.find(params.getGroupId());
    }

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

    if (group != null) {
      filters.add("Group \"" + group.getName() + "\"");
    }

    if (params.getReason() != null && !params.getReason().isEmpty()) {
      filters.add("Reason \"" + params.getReason() + "\"");
    }

    if (params.getStatus() != null) {
      filters.add("Request Status \"" + params.getStatus() + "\"");
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
  public void redirect(HttpServletResponse response, MaskRequestParams params) throws IOException {
    ParamBuilder builder = new ParamBuilder();

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
    builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
    builder.add("reason", params.getReason());
    builder.add("status", (params.getStatus() == null) ? null : params.getStatus().name());
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
