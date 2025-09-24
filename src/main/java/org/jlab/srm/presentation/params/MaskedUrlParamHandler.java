package org.jlab.srm.presentation.params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.srm.business.params.MaskedParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;

public class MaskedUrlParamHandler implements UrlParamHandler<MaskedParams> {

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;
  private final CategoryFacade categoryFacade;
  private final SystemFacade systemFacade;
  private final BeamDestinationFacade destinationFacade;
  private final RegionFacade regionFacade;
  private final ResponsibleGroupFacade groupFacade;

  public MaskedUrlParamHandler(
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
  public MaskedParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
    String reason = request.getParameter("reason");

    MaskedParams params = new MaskedParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);
    params.setReason(reason);

    return params;
  }

  @Override
  public void validate(MaskedParams params) {
    // No rules
  }

  @Override
  public void store(MaskedParams params) {
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
  }

  @Override
  public MaskedParams defaults() {
    MaskedParams defaultParams = new MaskedParams();

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);

    return defaultParams;
  }

  @Override
  public MaskedParams materialize() {
    MaskedParams defaultValues = defaults();

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

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger regionId = defaultValues.getRegionId();
    BigInteger groupId = defaultValues.getGroupId();
    String reason = defaultValues.getReason();

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

    MaskedParams params = new MaskedParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setRegionId(regionId);
    params.setGroupId(groupId);
    params.setReason(reason);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(MaskedParams params) {
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
  public void redirect(HttpServletResponse response, MaskedParams params) throws IOException {
    ParamBuilder builder = new ParamBuilder();

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
    builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
    builder.add("reason", params.getReason());
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
