package org.jlab.srm.presentation.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jlab.srm.business.session.CategoryFacade;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.*;
import org.jlab.srm.persistence.model.HcoNodeData;

/**
 * @author ryans
 */
public final class HcoFunctions {

  private static final Logger LOGGER = Logger.getLogger(HcoFunctions.class.getName());

  private HcoFunctions() {
    // cannot instantiate publicly
  }

  /**
   * Whether the user may use the setup pages, for showing the Setup tab: an admin, or the branch
   * admin of any branch.
   *
   * @return true if so; false if it cannot be determined, so that every page still renders
   */
  public static boolean isAdminOrAnyBranchAdmin() {
    try {
      CategoryFacade categoryFacade = InitialContext.doLookup("java:module/CategoryFacade");

      return categoryFacade.isAdminOrAnyBranchAdmin();
    } catch (NamingException | RuntimeException e) {
      LOGGER.log(Level.WARNING, "Unable to check for an admin or branch admin", e);
      return false;
    }
  }

  public static String formatComponent(Component component) {
    String name = component.getName();

    if (component.isUnpowered()) {
      name = name + "★";
    }

    return name;
  }

  public static String formatFakeComponent(String name, boolean unpowered) {
    if (unpowered) {
      name = name + "★";
    }

    return name;
  }

  public static String formatBoolean(Boolean value) {
    if (value == null) {
      return "";
    } else if (value) {
      return "Yes"; // true; Y; '✔'
    } else {
      return "No"; // false; N; ' '
    }
  }

  public static String formatChangeType(SignoffChangeType type) {
    String formatted = "";

    if (type != null) {
      switch (type) {
        case UPGRADE:
          formatted = "Upgrade";
          break;
        case DOWNGRADE:
          formatted = "Downgrade";
          break;
        case CASCADE:
          formatted = "Cascade";
          break;
        default: // COMMENT
          formatted = "Comment";
      }
    }

    return formatted;
  }

  public static Status getStatusById(Integer statusId) {
    Status status = null;

    if (statusId != null) {
      status = Status.FROM_ID(BigInteger.valueOf(statusId));
    }

    return status;
  }

  public static String getStatusClass(Status status) {
    String classname = null;

    switch (status.getStatusId().intValue()) {
      case 0:
        classname = "notapplicable";
        break;
      case 1:
        classname = "ready";
        break;
      case 50:
        classname = "checked";
        break;
      case 100:
        classname = "not-ready";
        break;
      case 150:
        classname = "masked";
        break;
      case 200:
        classname = "exception";
        break;
      case 250:
        classname = "tragedy";
        break;
      default:
        classname = "unknown";
    }

    return classname;
  }

  public static Status getDefaultStatus() {
    return Status.NOT_READY;
  }

  public static String getNodeUrl(HcoNodeData data) {
    String url;

    if (Objects.requireNonNull(data.getType()) == HcoNodeType.COMPONENT) {
      url = "reports/component/detail?componentId=" + data.getId();
      /*case SYSTEM:
      url = "system-detail?systemId=" + data.getId();
      break;*/
    } else {
      url = null;
    }
    return url;
  }

  public static List<DataSource> dataSourceList() {
    return Arrays.asList(DataSource.values());
  }

  public static List<AllChangeType> allChangeTypeList() {
    return Arrays.asList(AllChangeType.values());
  }

  public static List<SignoffChangeType> changeTypeList() {
    return Arrays.asList(SignoffChangeType.values());
  }

  public static List<MaskingRequestStatus> requestStatusList() {
    return Arrays.asList(MaskingRequestStatus.values());
  }

  /**
   * Returns the number of milliseconds since Jan 01 1970, but in local time, not UTC like usual.
   * This is useful because web browsers / JavaScript generally can't figure out daylight savings or
   * timezone offsets for varying points in time (they generally only know the fixed/constant offset
   * being applied on the client at present).
   *
   * @param date The date (milliseconds since Epoch in UTC)
   * @return milliseconds since Epoch in local time
   */
  public static long getLocalTime(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    long localOffset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);

    return cal.getTimeInMillis() + localOffset;
  }

  public static String truncateAndUrlEncodeCedName(String name) {
    String encoded = "";

    if (name != null) {
      String[] tokens = name.split(" ");

      String str = tokens[0];

      encoded = URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
    return encoded;
  }

  public static String getHostnameFromIp(String ip) {
    String hostname = ip;

    if (ip != null) {
      try {
        InetAddress address = InetAddress.getByName(ip);
        hostname = address.getHostName();

        if (!ip.equals(hostname)) {
          hostname = hostname + " (" + ip + ")";
        }
      } catch (UnknownHostException e) {
        // Unable to resolve... oh well, just use ip
      }
    }

    return hostname;
  }
}
