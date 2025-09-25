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
import org.jlab.srm.business.params.GroupResponsibilityReportParams;
import org.jlab.srm.business.session.BeamDestinationFacade;
import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.business.session.ResponsibleGroupFacade;
import org.jlab.srm.business.session.SystemFacade;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.entity.SystemEntity;

public class GroupResponsibilityReportUrlParamHandler
    implements UrlParamHandler<GroupResponsibilityReportParams> {

  private final HttpServletRequest request;
  private final BigInteger[] defaultDestinationIdArray;
  private final BeamDestinationFacade destinationFacade;
  private final CategoryFacade categoryFacade;
  private final SystemFacade systemFacade;
  private final ResponsibleGroupFacade groupFacade;

  public GroupResponsibilityReportUrlParamHandler(
      HttpServletRequest request,
      BigInteger[] defaultDestinationIdArray,
      BeamDestinationFacade destinationFacade,
      CategoryFacade categoryFacade,
      SystemFacade systemFacade,
      ResponsibleGroupFacade groupFacade) {
    this.request = request;
    this.defaultDestinationIdArray = defaultDestinationIdArray;
    this.destinationFacade = destinationFacade;
    this.categoryFacade = categoryFacade;
    this.systemFacade = systemFacade;
    this.groupFacade = groupFacade;
  }

  @Override
  public GroupResponsibilityReportParams convert() {
    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

    Boolean checklistRequired, checklistMissing;

    try {
      checklistRequired = ParamConverter.convertYNBoolean(request, "checklistRequired");
      checklistMissing = ParamConverter.convertYNBoolean(request, "checklistMissing");
    } catch (Exception e) {
      throw new RuntimeException("Unable to parse boolean");
    }

    GroupResponsibilityReportParams params = new GroupResponsibilityReportParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setGroupId(groupId);
    params.setChecklistRequired(checklistRequired);
    params.setChecklistMissing(checklistMissing);

    return params;
  }

  @Override
  public void validate(GroupResponsibilityReportParams params) {
    // Nothing to validate
  }

  @Override
  public void store(GroupResponsibilityReportParams params) {
    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);

    session.setAttribute(
        "responsibilityReportDestinationId[]",
        params.getDestinationIdArray() == null
            ? new BigInteger[0]
            : params.getDestinationIdArray());
    session.setAttribute(
        "responsibilityReportCategoryId[]", new BigInteger[] {params.getCategoryId()});
    session.setAttribute("responsibilityReportSystemId[]", new BigInteger[] {params.getSystemId()});
    session.setAttribute("responsibilityReportGroupId[]", new BigInteger[] {params.getGroupId()});
    session.setAttribute(
        "responsibilityReportChecklistRequired[]", new Boolean[] {params.isChecklistRequired()});
    session.setAttribute(
        "responsibilityReportChecklistMissing[]", new Boolean[] {params.isChecklistMissing()});
  }

  @Override
  public GroupResponsibilityReportParams defaults() {
    GroupResponsibilityReportParams defaultParams = new GroupResponsibilityReportParams();

    defaultParams.setDestinationIdArray(defaultDestinationIdArray);

    // All fields null is default
    return defaultParams;
  }

  @Override
  public GroupResponsibilityReportParams materialize() {
    GroupResponsibilityReportParams defaultValues = defaults();

    /* Note: We store each field indivdually as we want to re-use amoung screens*/
    /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
    /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
    HttpSession session = request.getSession(true);
    BigInteger[] destinationIdArray =
        (BigInteger[]) session.getAttribute("responsibilityReportDestinationId[]");
    BigInteger[] categoryIdArray =
        (BigInteger[]) session.getAttribute("responsibilityReportCategoryId[]");
    BigInteger[] systemIdArray =
        (BigInteger[]) session.getAttribute("responsibilityReportSystemId[]");
    BigInteger[] groupIdArray =
        (BigInteger[]) session.getAttribute("responsibilityReportGroupId[]");
    Boolean[] checklistRequiredArray =
        (Boolean[]) session.getAttribute("responsibilityReportChecklistRequired[]");
    Boolean[] checklistMissingArray =
        (Boolean[]) session.getAttribute("responsibilityReportChecklistMissing[]");

    BigInteger categoryId = defaultValues.getCategoryId();
    BigInteger systemId = defaultValues.getSystemId();
    BigInteger groupId = defaultValues.getGroupId();
    Boolean checklistRequired = defaultValues.isChecklistRequired();
    Boolean checklistMissing = defaultValues.isChecklistMissing();

    if (destinationIdArray == null) {
      destinationIdArray = defaultValues.getDestinationIdArray();
    }

    if (categoryIdArray != null && categoryIdArray.length > 0) {
      categoryId = categoryIdArray[0];
    }

    if (systemIdArray != null && systemIdArray.length > 0) {
      systemId = systemIdArray[0];
    }

    if (groupIdArray != null && groupIdArray.length > 0) {
      groupId = groupIdArray[0];
    }

    if (checklistRequiredArray != null && checklistRequiredArray.length > 0) {
      checklistRequired = checklistRequiredArray[0];
    }

    if (checklistMissingArray != null && checklistMissingArray.length > 0) {
      checklistMissing = checklistMissingArray[0];
    }

    GroupResponsibilityReportParams params = new GroupResponsibilityReportParams();

    params.setDestinationIdArray(destinationIdArray);
    params.setCategoryId(categoryId);
    params.setSystemId(systemId);
    params.setGroupId(groupId);
    params.setChecklistRequired(checklistRequired);
    params.setChecklistMissing(checklistMissing);

    return params;
  }

  @Override
  public boolean qualified() {
    return request.getParameter("qualified") != null;
  }

  @Override
  public String message(GroupResponsibilityReportParams params) {
    List<String> filters = new ArrayList<>();

    List<BeamDestination> destinationList = null;
    Category category = null;
    SystemEntity system = null;
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

    if (group != null) {
      filters.add("Group \"" + group.getName() + "\"");
    }

    if (params.isChecklistRequired() != null) {
      filters.add("Checklist Required \"" + (params.isChecklistRequired() ? "Yes" : "No") + "\"");
    }

    if (params.isChecklistMissing() != null) {
      filters.add("Checklist Missing \"" + (params.isChecklistMissing() ? "Yes" : "No") + "\"");
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
  public void redirect(HttpServletResponse response, GroupResponsibilityReportParams params)
      throws IOException {
    ParamBuilder builder = new ParamBuilder();

    builder.add("destinationId", params.getDestinationIdArray());
    builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
    builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
    builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
    builder.add("checklistRequired", IOUtil.nullOrBoolean(params.isChecklistRequired()));
    builder.add("checklistMissing", IOUtil.nullOrBoolean(params.isChecklistMissing()));
    builder.add("qualified", "");

    String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

    response.sendRedirect(response.encodeRedirectURL(url));
  }
}
