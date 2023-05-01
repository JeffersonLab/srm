package org.jlab.srm.presentation.params;

import org.jlab.srm.business.params.SignoffReportParams;
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

public class SignoffReportUrlParamHandler implements UrlParamHandler<SignoffReportParams> {

    private final HttpServletRequest request;
    private final BigInteger[] defaultDestinationIdArray;
    private final BeamDestinationFacade destinationFacade;
    private final CategoryFacade categoryFacade;
    private final SystemFacade systemFacade;
    private final RegionFacade regionFacade;
    private final ResponsibleGroupFacade groupFacade;

    public SignoffReportUrlParamHandler(HttpServletRequest request,
                                        BigInteger[] defaultDestinationIdArray, BeamDestinationFacade destinationFacade,
                                        CategoryFacade categoryFacade, SystemFacade systemFacade, RegionFacade regionFacade,
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
    public SignoffReportParams convert() {
        BigInteger[] destinationIdArray = ParamConverter.convertBigIntegerArray(request,
                "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
        BigInteger statusId = ParamConverter.convertBigInteger(request, "statusId");

        Boolean readyTurn, masked;

        try {
            readyTurn = ParamConverter.convertYNBoolean(request, "readyTurn");
            masked = ParamConverter.convertYNBoolean(request, "masked");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse boolean");
        }

        String componentName = request.getParameter("componentName");

        SignoffReportParams params = new SignoffReportParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setStatusId(statusId);
        params.setReadyTurn(readyTurn);
        params.setMasked(masked);
        params.setComponentName(componentName);

        return params;
    }

    @Override
    public void validate(SignoffReportParams params) {
        // Nothing to validate
    }

    @Override
    public void store(SignoffReportParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("signoffReportDestinationId[]",
                params.getDestinationIdArray() == null ? new BigInteger[0] : params.getDestinationIdArray());
        session.setAttribute("signoffReportCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("signoffReportSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("signoffReportRegionId[]", new BigInteger[]{params.getRegionId()});
        session.setAttribute("signoffReportGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("signoffReportStatusId[]", new BigInteger[]{params.getStatusId()});
        session.setAttribute("signoffReportReadyTurn[]", new Boolean[]{params.isReadyTurn()});
        session.setAttribute("signoffReportMasked[]", new Boolean[]{params.isMasked()});
        session.setAttribute("signoffReportComponentName[]", new String[]{params.getComponentName()});
    }

    @Override
    public SignoffReportParams defaults() {
        SignoffReportParams defaultParams = new SignoffReportParams();

        defaultParams.setDestinationIdArray(defaultDestinationIdArray);

        // All fields null is default
        return defaultParams;
    }

    @Override
    public SignoffReportParams materialize() {
        SignoffReportParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute(
                "signoffReportDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute(
                "signoffReportCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("signoffReportSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("signoffReportRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("signoffReportGroupId[]");
        BigInteger[] statusIdArray = (BigInteger[]) session.getAttribute("signoffReportStatusId[]");
        Boolean[] readyTurnArray = (Boolean[]) session.getAttribute("signoffReportReadyTurn[]");
        Boolean[] maskedArray = (Boolean[]) session.getAttribute("signoffReportMasked[]");
        String[] componentNameArray
                = (String[]) session.getAttribute("signoffReportComponentName[]");

        BigInteger categoryId = defaultValues.getCategoryId();
        BigInteger systemId = defaultValues.getSystemId();
        BigInteger regionId = defaultValues.getRegionId();
        BigInteger groupId = defaultValues.getGroupId();
        BigInteger statusId = defaultValues.getStatusId();
        Boolean readyTurn = defaultValues.isReadyTurn();
        Boolean masked = defaultValues.isMasked();
        String componentName = defaultValues.getComponentName();

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

        if (statusIdArray != null && statusIdArray.length > 0) {
            statusId = statusIdArray[0];
        }

        if (readyTurnArray != null && readyTurnArray.length > 0) {
            readyTurn = readyTurnArray[0];
        }

        if (maskedArray != null && maskedArray.length > 0) {
            masked = maskedArray[0];
        }

        if (componentNameArray != null && componentNameArray.length > 0) {
            componentName = componentNameArray[0];
        }

        SignoffReportParams params = new SignoffReportParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setStatusId(statusId);
        params.setReadyTurn(readyTurn);
        params.setMasked(masked);
        params.setComponentName(componentName);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(SignoffReportParams params) {
        List<String> filters = new ArrayList<>();

        List<BeamDestination> destinationList = null;
        Category category = null;
        SystemEntity system = null;
        Region region = null;
        ResponsibleGroup group = null;
        Status status = null;

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

        if (params.getStatusId() != null) {
            status = Status.FROM_ID(params.getStatusId());
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

        if (status != null) {
            filters.add("Status \"" + status.getName() + "\"");
        }

        if (params.isReadyTurn() != null) {
            filters.add("Ready Turn \"" + (params.isReadyTurn() ? "Yes" : "No") + "\"");
        }

        if (params.isMasked() != null) {
            filters.add("Masked \"" + (params.isMasked() ? "Yes" : "No") + "\"");
        }

        if (params.getComponentName() != null && !params.getComponentName().trim().isEmpty()) {
            filters.add("Component \"" + params.getComponentName() + "\"");
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
     * @param params   The parameter object
     * @throws IOException If unable to redirect
     */
    @Override
    public void redirect(HttpServletResponse response, SignoffReportParams params) throws
            IOException {
        ParamBuilder builder = new ParamBuilder();

        builder.add("destinationId", params.getDestinationIdArray());
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("statusId", IOUtil.nullOrString(params.getStatusId()));
        builder.add("readyTurn", IOUtil.nullOrBoolean(params.isReadyTurn()));
        builder.add("masked", IOUtil.nullOrBoolean(params.isMasked()));
        builder.add("componentName", params.getComponentName());
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
