package org.jlab.srm.presentation.util;

import java.text.SimpleDateFormat;
import java.util.*;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.enumeration.DataSource;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;

/**
 * @author ryans
 */
public final class FilterSelectionMessage {

  private FilterSelectionMessage() {
    // Private constructor
  }

  public static String getTrendReportMessage(Date start, Date end) {

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    return "Daily Ready Signoff Count from "
        + formatter.format(start)
        + " to "
        + formatter.format(end);
  }

  public static List<Map.Entry<String, Object>> getOverallStatusFootnoteList(
      List<BeamDestination> selectedDestinationList,
      Category selectedCategory,
      SystemEntity selectedSystem,
      Region selectedRegion,
      ResponsibleGroup selectedGroup) {
    ArrayList<Map.Entry<String, Object>> footnoteList = new ArrayList<>();

    if (selectedDestinationList != null && !selectedDestinationList.isEmpty()) {
      String sublist = selectedDestinationList.get(0).getName();

      for (int i = 1; i < selectedDestinationList.size(); i++) {
        BeamDestination destination = selectedDestinationList.get(i);
        sublist = sublist + ", " + destination.getName();
      }

      Map.Entry<String, Object> entry =
          new AbstractMap.SimpleEntry<>("Beam Destination", selectedDestinationList);

      footnoteList.add(entry);
    }

    if (selectedCategory != null) {
      Map.Entry<String, Object> entry =
          new AbstractMap.SimpleEntry<>("Category", selectedCategory.getName());

      footnoteList.add(entry);
    }

    if (selectedSystem != null) {
      Map.Entry<String, Object> entry =
          new AbstractMap.SimpleEntry<>("System", selectedSystem.getName());

      footnoteList.add(entry);
    }

    if (selectedRegion != null) {
      Map.Entry<String, Object> entry =
          new AbstractMap.SimpleEntry<>("Region", selectedRegion.getName());

      footnoteList.add(entry);
    }

    if (selectedGroup != null) {
      Map.Entry<String, Object> entry =
          new AbstractMap.SimpleEntry<>("Group", selectedGroup.getName());

      footnoteList.add(entry);
    }

    return footnoteList;
  }

  public static String getMessage(
      List<BeamDestination> destinationList,
      Category category,
      SystemEntity system,
      Region region,
      ResponsibleGroup group,
      List<Status> statusList,
      Boolean masked,
      Boolean unpowered,
      String componentName,
      String reason,
      DataSource source) {

    List<String> filters = new ArrayList<>();

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

    if (statusList != null && !statusList.isEmpty()) {
      String sublist = "\"" + statusList.get(0).getName() + "\"";

      for (int i = 1; i < statusList.size(); i++) {
        Status status = statusList.get(i);
        sublist = sublist + ", \"" + status.getName() + "\"";
      }

      filters.add("Status " + sublist);
    }

    if (source != null) {
      filters.add("Source \"" + source + "\"");
    }

    if (masked != null) {
      filters.add("Masked \"" + (masked ? "Yes" : "No") + "\"");
    }

    if (unpowered != null) {
      filters.add("Unpowered \"" + (unpowered ? "Yes" : "No") + "\"");
    }

    if (componentName != null && !componentName.isEmpty()) {
      filters.add("Component Name \"" + componentName + "\"");
    }

    if (reason != null && !reason.isEmpty()) {
      filters.add("Reason \"" + reason + "\"");
    }

    String message = "";

    if (!filters.isEmpty()) {
      for (String filter : filters) {
        message += " " + filter + " and";
      }

      // Remove trailing " and"
      message = message.substring(0, message.length() - 4);
    }

    return message;
  }

  public static String getSavedSignoffMessage(
      SavedSignoffType type, SystemEntity system, ResponsibleGroup group) {

    List<String> filters = new ArrayList<>();

    if (type != null) {
      filters.add("Saved Signoff Type \"" + type.getName() + "\"");
    }

    if (system != null) {
      filters.add("System \"" + system.getName() + "\"");
    }

    if (group != null) {
      filters.add("Group \"" + group.getName() + "\"");
    }

    String message = "";

    if (!filters.isEmpty()) {
      for (String filter : filters) {
        message += " " + filter + " and";
      }

      // Remove trailing " and"
      message = message.substring(0, message.length() - 4);
    }

    return message;
  }

  public static String getActivityScreenMessage(
      List<BeamDestination> destinationList,
      Category category,
      SystemEntity system,
      Region region,
      ResponsibleGroup group,
      String username,
      String componentName,
      Status status,
      SignoffChangeType change,
      Date start,
      Date end) {
    List<String> filters = new ArrayList<>();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

    if (username != null && !username.isEmpty()) {
      filters.add("User \"" + username + "\"");
    }

    if (componentName != null && !componentName.isEmpty()) {
      filters.add("Component \"" + componentName + "\"");
    }

    if (status != null) {
      filters.add("Status \"" + status.getName() + "\"");
    }

    if (change != null) {
      filters.add("Change \"" + HcoFunctions.formatChangeType(change) + "\"");
    }

    if (start != null) {
      filters.add("Start Date \"" + formatter.format(start) + "\"");
    }

    if (end != null) {
      filters.add("End Date \"" + formatter.format(end) + "\"");
    }

    String message = "";

    if (!filters.isEmpty()) {
      for (String filter : filters) {
        message += " " + filter + " and";
      }

      // Remove trailing " and"
      message = message.substring(0, message.length() - 4);
    }

    return message;
  }

  public static String getSignoffScreenMessage(
      ResponsibleGroup group,
      Category category,
      SystemEntity system,
      List<BeamDestination> destinationList,
      List<Region> regionList,
      List<Status> statusList,
      Boolean readyTurn,
      Boolean masked,
      String componentName,
      Date minLastModifiedDate,
      Date maxLastModifiedDate) {

    List<String> filters = new ArrayList<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

    if (group != null) {
      filters.add("Group \"" + group.getName() + "\"");
    }

    if (category != null) {
      filters.add("Category \"" + category.getName() + "\"");
    }

    if (system != null) {
      filters.add("System \"" + system.getName() + "\"");
    }

    if (destinationList != null && !destinationList.isEmpty()) {
      String sublist = "\"" + destinationList.get(0).getName() + "\"";

      for (int i = 1; i < destinationList.size(); i++) {
        BeamDestination destination = destinationList.get(i);
        sublist = sublist + ", \"" + destination.getName() + "\"";
      }

      filters.add("Beam Destination " + sublist);
    }

    if (regionList != null && !regionList.isEmpty()) {
      String sublist = "\"" + regionList.get(0).getName() + "\"";

      for (int i = 1; i < regionList.size(); i++) {
        Region region = regionList.get(i);
        sublist = sublist + ", \"" + region.getName() + "\"";
      }

      filters.add("Region " + sublist);
    }

    if (statusList != null && !statusList.isEmpty()) {
      String sublist = "\"" + statusList.get(0).getName() + "\"";

      for (int i = 1; i < statusList.size(); i++) {
        Status status = statusList.get(i);
        sublist = sublist + ", \"" + status.getName() + "\"";
      }

      filters.add("Status " + sublist);
    }

    if (readyTurn != null) {
      filters.add("Ready Turn \"" + (readyTurn ? "Yes" : "No") + "\"");
    }

    if (masked != null) {
      filters.add("Masked \"" + (masked ? "Yes" : "No") + "\"");
    }

    if (componentName != null && !componentName.isEmpty()) {
      filters.add("Name \"" + componentName + "\"");
    }

    if (minLastModifiedDate != null) {
      filters.add("Min Last Modified Date \"" + dateFormat.format(minLastModifiedDate) + "\"");
    }

    if (maxLastModifiedDate != null) {
      filters.add("Max Last Modified Date \"" + dateFormat.format(maxLastModifiedDate) + "\"");
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
}
