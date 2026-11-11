package org.jlab.srm.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.srm.persistence.enumeration.*;

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

  public static MaskingRequestStatus convertMaskingRequestStatus(
      HttpServletRequest request, String name) {
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

    if (valueStrArray != null) {
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
      return valueList.toArray(new AllChangeType[] {});
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

  public static Date convertJLabDate(HttpServletRequest request, String name)
      throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = format.parse(valueStr);
    }

    return value;
  }

  public static Date convertISO8601Date(HttpServletRequest request, String name)
      throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    Date value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = format.parse(valueStr);
    }

    return value;
  }

  public static HcoNodeType convertHcoNodeType(HttpServletRequest request, String name) {
    String valueStr = request.getParameter(name);
    HcoNodeType value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      value = HcoNodeType.valueOf(valueStr);
    }

    return value;
  }

  /**
   * Converts a category or system weight, which orders it among its siblings: lower first, and
   * those with no weight last, by name.
   *
   * @param request the request
   * @param name the parameter name
   * @return the weight, or null if the parameter is missing or blank
   * @throws UserFriendlyException if the value is not a whole number in the int range
   */
  public static BigInteger convertWeight(HttpServletRequest request, String name)
      throws UserFriendlyException {
    String valueStr = request.getParameter(name);

    if (valueStr == null || valueStr.isBlank()) {
      return null;
    }

    try {
      return BigInteger.valueOf(Integer.parseInt(valueStr.trim()));
    } catch (NumberFormatException e) {
      throw new UserFriendlyException(
          "Weight must be a whole number from " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
    }
  }
}
