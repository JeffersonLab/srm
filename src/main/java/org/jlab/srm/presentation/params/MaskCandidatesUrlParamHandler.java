package org.jlab.srm.presentation.params;

import org.jlab.srm.business.params.MaskCandidatesParams;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;

public class MaskCandidatesUrlParamHandler implements UrlParamHandler<MaskCandidatesParams> {

    private final HttpServletRequest request;
    private final BigInteger[] defaultDestinationIdArray;

    public MaskCandidatesUrlParamHandler(HttpServletRequest request,
                                         BigInteger[] defaultDestinationIdArray) {
        this.request = request;
        this.defaultDestinationIdArray = defaultDestinationIdArray;
    }

    @Override
    public MaskCandidatesParams convert() {
        BigInteger[] destinationIdArray = ParamConverter.convertBigIntegerArray(request,
                "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

        String componentName = request.getParameter("componentName");

        Boolean unpowered;

        try {
            unpowered = ParamConverter.convertYNBoolean(request, "unpowered");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse boolean");
        }

        BigInteger[] statusIdArray = ParamConverter.convertBigIntegerArray(request, "statusId");

        MaskCandidatesParams params = new MaskCandidatesParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setUnpowered(unpowered);
        params.setComponentName(componentName);
        params.setStatusIdArray(statusIdArray);

        return params;
    }

    @Override
    public void validate(MaskCandidatesParams params) {
        // Nothing to validate
    }

    @Override
    public void store(MaskCandidatesParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("exceptionsDestinationId[]",
                params.getDestinationIdArray() == null ? new BigInteger[0] : params.getDestinationIdArray());
        session.setAttribute("exceptionsCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("exceptionsSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("exceptionsRegionId[]", new BigInteger[]{params.getRegionId()});
        session.setAttribute("exceptionsGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("exceptionsUnpowered[]", new Boolean[]{params.isUnpowered()});
        session.setAttribute("exceptionsComponentName[]", new String[]{params.getComponentName()});
        session.setAttribute("exceptionsStatusId[]",
                params.getStatusIdArray() == null ? new BigInteger[0] : params.getStatusIdArray());
    }

    @Override
    public MaskCandidatesParams defaults() {
        MaskCandidatesParams defaultParams = new MaskCandidatesParams();

        defaultParams.setDestinationIdArray(defaultDestinationIdArray);
        defaultParams.setStatusIdArray(new BigInteger[]{Status.NOT_READY.getStatusId(),
                Status.CHECKED.getStatusId()});

        return defaultParams;
    }

    @Override
    public MaskCandidatesParams materialize() {
        MaskCandidatesParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute(
                "exceptionsDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute("exceptionsCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("exceptionsSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("exceptionsRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("exceptionsGroupId[]");
        Boolean[] unpoweredArray = (Boolean[]) session.getAttribute("exceptionsUnpowered[]");
        String[] componentNameArray = (String[]) session.getAttribute("exceptionsComponentName[]");
        BigInteger[] statusIdArray = (BigInteger[]) session.getAttribute(
                "exceptionsStatusId[]");

        BigInteger categoryId = defaultValues.getCategoryId();
        BigInteger systemId = defaultValues.getSystemId();
        BigInteger regionId = defaultValues.getRegionId();
        BigInteger groupId = defaultValues.getGroupId();
        Boolean unpowered = defaultValues.isUnpowered();
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

        if (unpoweredArray != null && unpoweredArray.length > 0) {
            unpowered = unpoweredArray[0];
        }

        if (componentNameArray != null && componentNameArray.length > 0) {
            componentName = componentNameArray[0];
        }

        if (statusIdArray == null) {
            statusIdArray = defaultValues.getStatusIdArray();
        }

        MaskCandidatesParams params = new MaskCandidatesParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setUnpowered(unpowered);
        params.setComponentName(componentName);
        params.setStatusIdArray(statusIdArray);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(MaskCandidatesParams params) {
        return null;
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
    public void redirect(HttpServletResponse response, MaskCandidatesParams params) throws
            IOException {
        ParamBuilder builder = new ParamBuilder();

        builder.add("destinationId", params.getDestinationIdArray());
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("unpowered", IOUtil.nullOrBoolean(params.isUnpowered()));
        builder.add("componentName", params.getComponentName());
        builder.add("statusId", params.getStatusIdArray());
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
