package org.jlab.hco.presentation.params;

import org.jlab.hco.business.params.SignoffParams;
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
import java.util.Date;

public class SignoffUrlParamHandler implements UrlParamHandler<SignoffParams> {

    private final HttpServletRequest request;

    public SignoffUrlParamHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public SignoffParams convert() {
        BigInteger[] destinationIdArray = ParamConverter.convertBigIntegerArray(request, "destinationId");
        BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
        BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
        BigInteger[] regionIdArray = ParamConverter.convertBigIntegerArray(request, "regionId");
        BigInteger[] statusIdArray = ParamConverter.convertBigIntegerArray(request, "statusId");

        Boolean readyTurn;

        try {
            readyTurn = ParamConverter.convertYNBoolean(request, "readyTurn");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse boolean");
        }

        Date minLastModified, maxLastModified;

        try {
            minLastModified = ParamConverter.convertFriendlyDateTime(request, "minLastModified");
            maxLastModified = ParamConverter.convertFriendlyDateTime(request, "maxLastModified");
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date");
        }

        String componentName = request.getParameter("componentName");

        SignoffParams params = new SignoffParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionIdArray(regionIdArray);
        params.setGroupId(groupId);
        params.setStatusIdArray(statusIdArray);
        params.setReadyTurn(readyTurn);
        params.setComponentName(componentName);
        params.setMinLastModified(minLastModified);
        params.setMaxLastModified(maxLastModified);

        return params;
    }

    @Override
    public void validate(SignoffParams params) {
        // Nothing to validate
    }

    @Override
    public void store(SignoffParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("signoffDestinationId[]",
                params.getDestinationIdArray() == null ? new BigInteger[0] : params.getDestinationIdArray());
        session.setAttribute("signoffCategoryId[]", new BigInteger[]{params.getCategoryId()});
        session.setAttribute("signoffSystemId[]", new BigInteger[]{params.getSystemId()});
        session.setAttribute("signoffRegionId[]",
                params.getRegionIdArray() == null ? new BigInteger[0] : params.getRegionIdArray());
        session.setAttribute("signoffGroupId[]", new BigInteger[]{params.getGroupId()});
        session.setAttribute("signoffStatusId[]",
                params.getStatusIdArray() == null ? new BigInteger[0] : params.getStatusIdArray());
        session.setAttribute("signoffReadyTurn[]", new Boolean[]{params.isReadyTurn()});
        session.setAttribute("signoffComponentName[]", new String[]{params.getComponentName()});
        session.setAttribute("signoffMinLastModified[]", new Date[]{params.getMinLastModified()});
        session.setAttribute("signoffMaxLastModified[]", new Date[]{params.getMaxLastModified()});
    }

    @Override
    public SignoffParams defaults() {
        SignoffParams defaultParams = new SignoffParams();

        defaultParams.setStatusIdArray(new BigInteger[]{BigInteger.ONE, BigInteger.valueOf(50L),
                BigInteger.valueOf(100L)});

        return defaultParams;
    }

    @Override
    public SignoffParams materialize() {
        SignoffParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        BigInteger[] destinationIdArray = (BigInteger[]) session.getAttribute("signoffDestinationId[]");
        BigInteger[] categoryIdArray = (BigInteger[]) session.getAttribute("signoffCategoryId[]");
        BigInteger[] systemIdArray = (BigInteger[]) session.getAttribute("signoffSystemId[]");
        BigInteger[] regionIdArray = (BigInteger[]) session.getAttribute("signoffRegionId[]");
        BigInteger[] groupIdArray = (BigInteger[]) session.getAttribute("signoffGroupId[]");
        BigInteger[] statusIdArray = (BigInteger[]) session.getAttribute("signoffStatusId[]");
        Boolean[] readyTurnArray = (Boolean[]) session.getAttribute("signoffReadyTurn[]");
        String[] componentNameArray = (String[]) session.getAttribute("signoffComponentName[]");
        Date[] minLastModifiedArray = (Date[]) session.getAttribute("signoffMinLastModified[]");
        Date[] maxLastModifiedArray = (Date[]) session.getAttribute("signoffMaxLastModified[]");

        BigInteger categoryId = defaultValues.getCategoryId();
        BigInteger systemId = defaultValues.getSystemId();
        BigInteger groupId = defaultValues.getGroupId();
        Boolean readyTurn = defaultValues.isReadyTurn();
        String componentName = defaultValues.getComponentName();
        Date minLastModified = defaultValues.getMinLastModified();
        Date maxLastModified = defaultValues.getMaxLastModified();

        if (destinationIdArray == null) {
            destinationIdArray = defaultValues.getDestinationIdArray();
        }

        if (categoryIdArray != null && categoryIdArray.length > 0) {
            categoryId = categoryIdArray[0];
        }

        if (systemIdArray != null && systemIdArray.length > 0) {
            systemId = systemIdArray[0];
        }

        if (regionIdArray == null) {
            regionIdArray = defaultValues.getRegionIdArray();
        }

        if (groupIdArray != null && groupIdArray.length > 0) {
            groupId = groupIdArray[0];
        }

        if (statusIdArray == null) {
            statusIdArray = defaultValues.getStatusIdArray();
        }

        if (readyTurnArray != null && readyTurnArray.length > 0) {
            readyTurn = readyTurnArray[0];
        }

        if (componentNameArray != null && componentNameArray.length > 0) {
            componentName = componentNameArray[0];
        }

        if (minLastModifiedArray != null && minLastModifiedArray.length > 0) {
            minLastModified = minLastModifiedArray[0];
        }

        if (maxLastModifiedArray != null && maxLastModifiedArray.length > 0) {
            maxLastModified = maxLastModifiedArray[0];
        }

        SignoffParams params = new SignoffParams();

        params.setDestinationIdArray(destinationIdArray);
        params.setCategoryId(categoryId);
        params.setSystemId(systemId);
        params.setRegionIdArray(regionIdArray);
        params.setGroupId(groupId);
        params.setStatusIdArray(statusIdArray);
        params.setReadyTurn(readyTurn);
        params.setComponentName(componentName);
        params.setMinLastModified(minLastModified);
        params.setMaxLastModified(maxLastModified);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(SignoffParams params) {
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
    public void redirect(HttpServletResponse response, SignoffParams params) throws
            IOException {
        ParamBuilder builder = new ParamBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

        builder.add("destinationId", params.getDestinationIdArray());
        builder.add("categoryId", IOUtil.nullOrString(params.getCategoryId()));
        builder.add("systemId", IOUtil.nullOrString(params.getSystemId()));
        builder.add("regionId", params.getRegionIdArray());
        builder.add("groupId", IOUtil.nullOrString(params.getGroupId()));
        builder.add("statusId", params.getStatusIdArray());
        builder.add("readyTurn", IOUtil.nullOrBoolean(params.isReadyTurn()));
        builder.add("componentName", params.getComponentName());
        builder.add("minLastModified", IOUtil.nullOrFormat(params.getMinLastModified(), dateFormat));
        builder.add("maxLastModified", IOUtil.nullOrFormat(params.getMaxLastModified(), dateFormat));
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
