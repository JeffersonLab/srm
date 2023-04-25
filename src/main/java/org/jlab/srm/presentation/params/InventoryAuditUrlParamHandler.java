package org.jlab.srm.presentation.params;

import org.jlab.srm.business.params.InventoryAuditParams;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryAuditUrlParamHandler implements UrlParamHandler<InventoryAuditParams> {

    private final HttpServletRequest request;

    public InventoryAuditUrlParamHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public InventoryAuditParams convert() {

        Date modifiedStart, modifiedEnd;

        try {
            modifiedStart = ParamConverter.convertFriendlyDateTime(request, "start");
            modifiedEnd = ParamConverter.convertFriendlyDateTime(request, "end");
        } catch (ParseException e) {
            throw new RuntimeException("Date format error", e);
        }

        InventoryAuditParams params = new InventoryAuditParams();

        params.setStart(modifiedStart);
        params.setEnd(modifiedEnd);

        return params;
    }

    @Override
    public void validate(InventoryAuditParams params) {
        // No rules
    }

    @Override
    public void store(InventoryAuditParams params) {
        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("inventoryAuditStart[]", new Date[]{params.getStart()});
        session.setAttribute("inventoryAuditEnd[]", new Date[]{params.getEnd()});
    }

    @Override
    public InventoryAuditParams defaults() {
        InventoryAuditParams defaultParams = new InventoryAuditParams();

        return defaultParams;
    }

    @Override
    public InventoryAuditParams materialize() {
        InventoryAuditParams defaultValues = defaults();

        /* Note: We store each field indivdually as we want to re-use amoung screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        Date[] startArray = (Date[]) session.getAttribute("inventoryAuditStart[]");
        Date[] endArray = (Date[]) session.getAttribute("inventoryAuditEnd[]");

        Date start = defaultValues.getStart();
        Date end = defaultValues.getEnd();

        if (startArray != null && startArray.length > 0) {
            start = startArray[0];
        }

        if (endArray != null && endArray.length > 0) {
            end = endArray[0];
        }

        InventoryAuditParams params = new InventoryAuditParams();

        params.setStart(start);
        params.setEnd(end);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(InventoryAuditParams params) {
        List<String> filters = new ArrayList<>();

        if (params.getStart() != null && params.getEnd() != null) {
            filters.add(TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd()));
        } else if (params.getStart() != null) {
            filters.add("Starting " + TimeUtil.formatSmartSingleTime(params.getStart()));
        } else if (params.getEnd() != null) {
            filters.add("Before " + TimeUtil.formatSmartSingleTime(params.getEnd()));
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
    public void redirect(HttpServletResponse response, InventoryAuditParams params) throws IOException {
        ParamBuilder builder = new ParamBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

        builder.add("start", IOUtil.nullOrFormat(params.getStart(), dateFormat));
        builder.add("end", IOUtil.nullOrFormat(params.getEnd(), dateFormat));
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(
                response.encodeRedirectURL(url));
    }
}
