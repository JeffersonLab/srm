package org.jlab.srm.presentation.params;

import org.jlab.srm.business.params.AllActivityParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.enumeration.AllChangeType;
import org.jlab.srm.presentation.util.HcoParamConverter;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllActivityUrlParamHandler implements UrlParamHandler<AllActivityParams> {

    private final HttpServletRequest request;
    private final BigInteger[] defaultDestinationIdArray;
    private final BeamDestinationFacade destinationFacade;
    private final CategoryFacade categoryFacade;
    private final SystemFacade systemFacade;
    private final RegionFacade regionFacade;
    private final ResponsibleGroupFacade groupFacade;
    private final StatusFacade statusFacade;

    public AllActivityUrlParamHandler(HttpServletRequest request,
                                      BigInteger[] defaultDestinationIdArray, BeamDestinationFacade destinationFacade,
                                      CategoryFacade categoryFacade, SystemFacade systemFacade, RegionFacade regionFacade,
                                      ResponsibleGroupFacade groupFacade, StatusFacade statusFacade) {
        this.request = request;
        this.defaultDestinationIdArray = defaultDestinationIdArray;
        this.destinationFacade = destinationFacade;
        this.categoryFacade = categoryFacade;
        this.systemFacade = systemFacade;
        this.regionFacade = regionFacade;
        this.groupFacade = groupFacade;
        this.statusFacade = statusFacade;
    }

    @Override
    public AllActivityParams convert() {
        BigInteger[] destinationIdArray = ParamConverter.convertBigIntegerArray(request,
                "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
        String username = request.getParameter("username");
        String componentName = request.getParameter("componentName");
        AllChangeType[] change = HcoParamConverter.convertAllChangeTypeArray(request, "change");
        BigInteger[] statusIdArray = ParamConverter.convertBigIntegerArray(request,
                "statusId");
        BigInteger[] currentComponentStatusIdArray = ParamConverter.convertBigIntegerArray(request,
                "componentStatusId");
        BigInteger[] currentSignoffStatusIdArray = ParamConverter.convertBigIntegerArray(request,
                "currentSignoffStatusId");

        Date start, end;

        try {
            start = ParamConverter.convertFriendlyDateTime(request, "start");
            end = ParamConverter.convertFriendlyDateTime(request, "end");
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date");
        }

        AllActivityParams params = new AllActivityParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setUsername(username);
        params.setComponentName(componentName);
        params.setChangeArray(change);
        params.setStart(start);
        params.setEnd(end);
        params.setStatusIdArray(statusIdArray);
        params.setCurrentComponentStatusIdArray(currentComponentStatusIdArray);
        params.setCurrentSignoffStatusIdArray(currentSignoffStatusIdArray);

        return params;
    }

    @Override
    public void validate(AllActivityParams params) {
        // end must not come before start

        if (params.getStart() != null && params.getEnd() != null) {
            if (params.getEnd().before(params.getStart())) {
                throw new RuntimeException("End date must not come before start date");
            }
        }
    }

    @Override
    public void store(AllActivityParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("activityDetailDestinationId[]",
                params.getDestinationIdArray() == null ? new BigInteger[0] : params.getDestinationIdArray());
        session.setAttribute("activityDetailCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("activityDetailSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("activityDetailRegionId[]", new BigInteger[]{params.getRegionId()});
        session.setAttribute("activityDetailGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("activityDetailUsername[]", new String[]{params.getUsername()});
        session.setAttribute("activityDetailComponentName[]",
                new String[]{params.getComponentName()});
        session.setAttribute("allChange[]", params.getChangeArray() == null ? new AllChangeType[0] : params.getChangeArray());
        session.setAttribute("activityDetailStart[]", new Date[]{params.getStart()});
        session.setAttribute("activityDetailEnd[]", new Date[]{params.getEnd()});
        session.setAttribute("activityDetailStatusId[]",
                params.getStatusIdArray() == null ? new BigInteger[0] : params.getStatusIdArray());
        session.setAttribute("activityDetailCurrentComponentStatusId[]",
                params.getCurrentComponentStatusIdArray() == null ? new BigInteger[0] : params.getCurrentComponentStatusIdArray());
        session.setAttribute("activityDetailCurrentSignoffStatusId[]",
                params.getCurrentSignoffStatusIdArray() == null ? new BigInteger[0] : params.getCurrentComponentStatusIdArray());
    }

    @Override
    public AllActivityParams defaults() {
        AllActivityParams defaultParams = new AllActivityParams();

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
    public AllActivityParams materialize() {
        AllActivityParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute(
                "activityDetailDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute(
                "activityDetailCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("activityDetailSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("activityDetailRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("activityDetailGroupId[]");
        String[] usernameArray = (String[]) session.getAttribute("activityDetailUsername[]");
        String[] componentNameArray = (String[]) session.getAttribute(
                "activityDetailComponentName[]");
        AllChangeType[] changeArray = (AllChangeType[]) session.getAttribute("allChange[]");
        Date[] startArray = (Date[]) session.getAttribute("activityDetailStart[]");
        Date[] endArray = (Date[]) session.getAttribute("activityDetailEnd[]");
        BigInteger[] statusIdArray = (BigInteger[]) session.getAttribute(
                "activityDetailStatusId[]");
        BigInteger[] currentComponentStatusIdArray = (BigInteger[]) session.getAttribute(
                "activityDetailCurrentComponentStatusId[]");
        BigInteger[] currentSignoffStatusIdArray = (BigInteger[]) session.getAttribute(
                "activityDetailCurrentSignoffStatusId[]");

        BigInteger categoryId = defaultValues.getCategoryId();
        BigInteger systemId = defaultValues.getSystemId();
        BigInteger regionId = defaultValues.getRegionId();
        BigInteger groupId = defaultValues.getGroupId();
        String username = defaultValues.getUsername();
        String componentName = defaultValues.getComponentName();
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

        if (groupIdArray != null && groupIdArray.length > 0) {
            groupId = groupIdArray[0];
        }

        if (usernameArray != null && usernameArray.length > 0) {
            username = usernameArray[0];
        }

        if (componentNameArray != null && componentNameArray.length > 0) {
            componentName = componentNameArray[0];
        }

        if (changeArray == null) {
            changeArray = defaultValues.getChangeArray();
        }

        if (startArray != null && startArray.length > 0) {
            start = startArray[0];
        }

        if (endArray != null && endArray.length > 0) {
            end = endArray[0];
        }

        if (statusIdArray == null) {
            statusIdArray = defaultValues.getStatusIdArray();
        }

        if (currentComponentStatusIdArray == null) {
            currentComponentStatusIdArray = defaultValues.getCurrentComponentStatusIdArray();
        }

        if (currentSignoffStatusIdArray == null) {
            currentSignoffStatusIdArray = defaultValues.getCurrentSignoffStatusIdArray();
        }

        AllActivityParams params = new AllActivityParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setUsername(username);
        params.setComponentName(componentName);
        params.setChangeArray(changeArray);
        params.setStart(start);
        params.setEnd(end);
        params.setStatusIdArray(statusIdArray);
        params.setCurrentComponentStatusIdArray(currentComponentStatusIdArray);
        params.setCurrentSignoffStatusIdArray(currentSignoffStatusIdArray);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(AllActivityParams params) {
        List<String> filters = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());
        List<BeamDestination> destinationList = null;
        Category category = null;
        SystemEntity system = null;
        Region region = null;
        ResponsibleGroup group = null;
        List<Status> statusList = null;
        List<Status> currentComponentStatusList = null;
        List<Status> currentSignoffStatusList = null;

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

        if (params.getStatusIdArray() != null) {
            statusList = statusFacade.findMultiple(params.getStatusIdArray());
        }

        if (params.getCurrentComponentStatusIdArray() != null) {
            currentComponentStatusList = statusFacade.findMultiple(params.getCurrentComponentStatusIdArray());
        }

        if (params.getCurrentSignoffStatusIdArray() != null) {
            currentSignoffStatusList = statusFacade.findMultiple(params.getCurrentSignoffStatusIdArray());
        }

        if (params.getStart() != null && params.getEnd() != null) {
            filters.add(TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd()));
        } else if (params.getStart() != null) {
            filters.add("Starting " + TimeUtil.formatSmartSingleTime(params.getStart()));
        } else if (params.getEnd() != null) {
            filters.add("Before " + TimeUtil.formatSmartSingleTime(params.getEnd()));
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
            filters.add("Subsystem \"" + system.getName() + "\"");
        }

        if (region != null) {
            filters.add("Region \"" + region.getName() + "\"");
        }

        if (group != null) {
            filters.add("Group \"" + group.getName() + "\"");
        }

        if (params.getUsername() != null && !params.getUsername().isEmpty()) {
            filters.add("User \"" + params.getUsername() + "\"");
        }

        if (params.getComponentName() != null && !params.getComponentName().isEmpty()) {
            filters.add("Component \"" + params.getComponentName() + "\"");
        }

        if (params.getChangeArray() != null && params.getChangeArray().length > 0) {
            String sublist = "\"" + params.getChangeArray()[0] + "\"";

            for (int i = 1; i < params.getChangeArray().length; i++) {
                sublist = sublist + ", \"" + params.getChangeArray()[i] + "\"";
            }

            filters.add("Change " + sublist);
        }

        if (statusList != null && !statusList.isEmpty()) {
            String sublist = "\"" + statusList.get(0).getName() + "\"";

            for (int i = 1; i < statusList.size(); i++) {
                Status status = statusList.get(i);
                sublist = sublist + ", \"" + status.getName() + "\"";
            }

            filters.add("Signoff Status " + sublist);
        }

        if (currentComponentStatusList != null && !currentComponentStatusList.isEmpty()) {
            String sublist = "\"" + currentComponentStatusList.get(0).getName() + "\"";

            for (int i = 1; i < currentComponentStatusList.size(); i++) {
                Status status = currentComponentStatusList.get(i);
                sublist = sublist + ", \"" + status.getName() + "\"";
            }

            filters.add("Current Component Status " + sublist);
        }

        if (currentSignoffStatusList != null && !currentSignoffStatusList.isEmpty()) {
            String sublist = "\"" + currentSignoffStatusList.get(0).getName() + "\"";

            for (int i = 1; i < currentSignoffStatusList.size(); i++) {
                Status status = currentSignoffStatusList.get(i);
                sublist = sublist + ", \"" + status.getName() + "\"";
            }

            filters.add("Current Signoff Status " + sublist);
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
     * Sends a redirect response indicating the qualified URL. If calling this
     * method from a Servlet doGet method generally a return statement should
     * immediately follow. This method is useful to maintain a restful /
     * bookmarkable URL for the user.
     *
     * @param response The Servlet response
     * @param params   The parameter object
     * @throws IOException If unable to redirect
     */
    @Override
    public void redirect(HttpServletResponse response, AllActivityParams params) throws
            IOException {
        ParamBuilder builder = new ParamBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

        builder.add("destinationId", params.getDestinationIdArray());
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("username", params.getUsername());
        builder.add("componentName", params.getComponentName());
        builder.add("change", params.getChangeArray());
        builder.add("start", IOUtil.nullOrFormat(params.getStart(), dateFormat));
        builder.add("end", IOUtil.nullOrFormat(params.getEnd(), dateFormat));
        builder.add("statusId", params.getStatusIdArray());
        builder.add("componentStatusId", params.getCurrentComponentStatusIdArray());
        builder.add("currentSignoffStatusId", params.getCurrentSignoffStatusIdArray());
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
