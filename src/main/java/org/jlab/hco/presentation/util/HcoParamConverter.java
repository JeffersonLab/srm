package org.jlab.hco.presentation.util;

import org.jlab.hco.persistence.enumeration.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
public final class HcoParamConverter {

    private HcoParamConverter() {
        // No one can instantiate due to private visibility
    }

    public static DataSource convertDataSource(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        DataSource source = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            source = DataSource.valueOf(valueStr);
        }

        return source;
    }

    public static MaskingRequestStatus convertMaskingRequestStatus(HttpServletRequest request,
                                                                   String name) {
        String valueStr = request.getParameter(name);
        MaskingRequestStatus status = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            status = MaskingRequestStatus.valueOf(valueStr);
        }

        return status;
    }

    public static AllChangeType convertAllChangeType(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        AllChangeType type = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            type = AllChangeType.valueOf(valueStr);
        }

        return type;
    }

    public static AllChangeType[] convertAllChangeTypeArray(HttpServletRequest request, String name) {
        String[] valueStrArray = request.getParameterValues(name);
        List<AllChangeType> valueList = new ArrayList<>();

        if (valueStrArray != null && valueStrArray.length > 0) {
            for (String valueStr : valueStrArray) {
                if (valueStr != null && !valueStr.isEmpty()) {
                    AllChangeType value = AllChangeType.valueOf(valueStr);
                    valueList.add(value);
                }
            }
        }

        if (valueList.isEmpty()) {
            return null;
        } else {
            return valueList.toArray(new AllChangeType[]{});
        }
    }

    public static SignoffChangeType convertChangeType(HttpServletRequest request, String name) {
        String valueStr = request.getParameter(name);
        SignoffChangeType type = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            type = SignoffChangeType.valueOf(valueStr);
        }

        return type;
    }


    public static Date convertJLabDate(HttpServletRequest request, String name) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            value = format.parse(valueStr);
        }

        return value;
    }

    public static Date convertISO8601Date(HttpServletRequest request, String name) throws
            ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date value = null;

        String valueStr = request.getParameter(name);

        if (valueStr != null && !valueStr.isEmpty()) {
            value = format.parse(valueStr);
        }

        return value;
    }

    public static HcoNodeType convertHcoNodeType(HttpServletRequest request,
                                                 String name) {
        String valueStr = request.getParameter(name);
        HcoNodeType value = null;

        if (valueStr != null && !valueStr.isEmpty()) {
            value = HcoNodeType.valueOf(valueStr);
        }

        return value;
    }
}
