package org.jlab.srm.presentation.params;

import org.jlab.srm.business.params.ReadinessParams;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ReadinessUrlParamHandler implements UrlParamHandler<ReadinessParams> {

    private final HttpServletRequest request;
    private final BeamDestinationFacade destinationFacade;
    private final CategoryFacade categoryFacade;
    private final SystemFacade systemFacade;
    private final RegionFacade regionFacade;
    private final ResponsibleGroupFacade groupFacade;
    private final StatusFacade statusFacade;

    public ReadinessUrlParamHandler(HttpServletRequest request,
                                    BeamDestinationFacade destinationFacade,
                                    CategoryFacade categoryFacade, SystemFacade systemFacade,
                                    RegionFacade regionFacade, ResponsibleGroupFacade groupFacade,
                                    StatusFacade statusFacade) {
        this.request = request;
        this.destinationFacade = destinationFacade;
        this.categoryFacade = categoryFacade;
        this.systemFacade = systemFacade;
        this.regionFacade = regionFacade;
        this.groupFacade = groupFacade;
        this.statusFacade = statusFacade;
    }

    @Override
    public ReadinessParams convert() {
        BigInteger[] destinationIdArray = ParamConverter.convertBigIntegerArray(request,
                "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
        BigInteger[] statusIdArray = ParamConverter.convertBigIntegerArray(request,
                "statusId");

        ReadinessParams params = new ReadinessParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setStatusIdArray(statusIdArray);

        return params;
    }

    @Override
    public void validate(ReadinessParams params) {
        // No rules (any number is allowed, and null is allowed
    }

    @Override
    public void store(ReadinessParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("readinessDestinationId[]",
                params.getDestinationIdArray() == null ? new BigInteger[0] : params.getDestinationIdArray());
        session.setAttribute("readinessCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("readinessSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("readinessRegionId[]", new BigInteger[]{params.getRegionId()});
        session.setAttribute("readinessGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("readinessStatusId[]",
                params.getStatusIdArray() == null ? new BigInteger[0] : params.getStatusIdArray());
    }

    @Override
    public ReadinessParams defaults() {
        return new ReadinessParams(); // All fields null is the default
    }

    @Override
    public ReadinessParams materialize() {
        ReadinessParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute("readinessDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute("readinessCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("readinessSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("readinessRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("readinessGroupId[]");
        BigInteger[] statusIdArray = (BigInteger[]) session.getAttribute("readinessStatusId[]");

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

        if (statusIdArray == null) {
            statusIdArray = defaultValues.getStatusIdArray();
        }

        ReadinessParams params = new ReadinessParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setStatusIdArray(statusIdArray);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(ReadinessParams params) {
        List<String> filters = new ArrayList<>();

        List<BeamDestination> destinationList = null;
        Category category = null;
        SystemEntity system = null;
        Region region = null;
        ResponsibleGroup group = null;
        List<Status> statusList = null;

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

        if (statusList != null && !statusList.isEmpty()) {
            String sublist = "\"" + statusList.get(0).getName() + "\"";

            for (int i = 1; i < statusList.size(); i++) {
                Status status = statusList.get(i);
                sublist = sublist + ", \"" + status.getName() + "\"";
            }

            filters.add("Status " + sublist);
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
    public void redirect(HttpServletResponse response, ReadinessParams params) throws IOException {
        ParamBuilder builder = new ParamBuilder();

        builder.add("destinationId", params.getDestinationIdArray());
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("qualified", "");
        builder.add("statusId", params.getStatusIdArray());

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
