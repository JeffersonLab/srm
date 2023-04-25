package org.jlab.hco.presentation.params;

import org.jlab.hco.business.params.ComponentSetupParams;
import org.jlab.hco.persistence.enumeration.DataSource;
import org.jlab.hco.presentation.util.HcoParamConverter;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;

public class ComponentSetupUrlParamHandler implements UrlParamHandler<ComponentSetupParams> {

    private final HttpServletRequest request;

    public ComponentSetupUrlParamHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ComponentSetupParams convert() {
        BigInteger destinationId = ParamConverter.convertBigInteger(request, "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

        String componentName = request.getParameter("componentName");

        DataSource source = HcoParamConverter.convertDataSource(request, "source");

        Boolean masked;

        try {
            masked = ParamConverter.convertYNBoolean(request, "masked");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse boolean");
        }

        Boolean unpowered;

        try {
            unpowered = ParamConverter.convertYNBoolean(request, "unpowered");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse boolean");
        }

        ComponentSetupParams params = new ComponentSetupParams();

        params.setDestinationId(destinationId);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setSource(source);
        params.setMasked(masked);
        params.setUnpowered(unpowered);
        params.setComponentName(componentName);

        return params;
    }

    @Override
    public void validate(ComponentSetupParams params) {
        // Nothing to validate
    }

    @Override
    public void store(ComponentSetupParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("setupDestinationId[]", new BigInteger[]{params.getDestinationId()});
        session.setAttribute("setupCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("setupSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("setupRegionId[]", new BigInteger[]{params.getRegionId()});
        session.setAttribute("setupGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("setupSource[]", new DataSource[]{params.getSource()});
        session.setAttribute("setupMasked[]", new Boolean[]{params.isMasked()});
        session.setAttribute("setupUnpowered[]", new Boolean[]{params.isUnpowered()});
        session.setAttribute("setupComponentName[]", new String[]{params.getComponentName()});
    }

    @Override
    public ComponentSetupParams defaults() {
        ComponentSetupParams defaultParams = new ComponentSetupParams();

        // All fields null is default
        return defaultParams;
    }

    @Override
    public ComponentSetupParams materialize() {
        ComponentSetupParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray
                = (BigInteger[]) session.getAttribute("setupDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute("setupCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("setupSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("setupRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("setupGroupId[]");
        DataSource[] sourceArray = (DataSource[]) session.getAttribute("setupSource[]");
        Boolean[] maskedArray = (Boolean[]) session.getAttribute("setupMasked[]");
        Boolean[] unpoweredArray = (Boolean[]) session.getAttribute("setupUnpowered[]");
        String[] componentNameArray = (String[]) session.getAttribute("setupComponentName[]");

        BigInteger destinationId = defaultValues.getDestinationId();
        BigInteger categoryId = defaultValues.getCategoryId();
        BigInteger systemId = defaultValues.getSystemId();
        BigInteger regionId = defaultValues.getRegionId();
        BigInteger groupId = defaultValues.getGroupId();
        DataSource source = defaultValues.getSource();
        Boolean masked = defaultValues.isMasked();
        Boolean unpowered = defaultValues.isUnpowered();
        String componentName = defaultValues.getComponentName();

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            destinationId = destinationIdArray[0];
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

        if (sourceArray != null && sourceArray.length > 0) {
            source = sourceArray[0];
        }

        if (maskedArray != null && maskedArray.length > 0) {
            masked = maskedArray[0];
        }

        if (unpoweredArray != null && unpoweredArray.length > 0) {
            unpowered = unpoweredArray[0];
        }

        if (componentNameArray != null && componentNameArray.length > 0) {
            componentName = componentNameArray[0];
        }

        ComponentSetupParams params = new ComponentSetupParams();

        params.setDestinationId(destinationId);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionId(regionId);
        params.setGroupId(groupId);
        params.setSource(source);
        params.setMasked(masked);
        params.setUnpowered(unpowered);
        params.setComponentName(componentName);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(ComponentSetupParams params) {
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
    public void redirect(HttpServletResponse response, ComponentSetupParams params) throws
            IOException {
        ParamBuilder builder = new ParamBuilder();

        builder.add("destinationId", IOUtil.nullOrString(params.getDestinationId()));
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", IOUtil.nullOrString(params.getRegionId()));
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("source", IOUtil.nullOrString(params.getSource()));
        builder.add("masked", IOUtil.nullOrBoolean(params.isMasked()));
        builder.add("unpowered", IOUtil.nullOrBoolean(params.isUnpowered()));
        builder.add("componentName", params.getComponentName());
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
