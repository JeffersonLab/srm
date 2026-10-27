package org.jlab.srm.presentation.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Region;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.entity.SavedSignoffType;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.srm.persistence.enumeration.DataSource;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.junit.jupiter.api.Test;

class FilterSelectionMessageTest {

  private static final Category CATEGORY = new Category(BigInteger.ONE, "Magnets");
  private static final SystemEntity SYSTEM = new SystemEntity(BigInteger.ONE, "Dipoles");
  private static final Region REGION = new Region(BigInteger.ONE, "North Linac");
  private static final ResponsibleGroup GROUP = new ResponsibleGroup(BigInteger.ONE, "Ops");

  private static BeamDestination destination(String name) {
    BeamDestination destination = new BeamDestination(BigInteger.ONE);
    destination.setName(name);
    return destination;
  }

  private static Date date(int year, int month, int day, int hour, int minute) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month, day, hour, minute);
    return cal.getTime();
  }

  @Test
  void trendReportMessage() {
    assertEquals(
        "Daily Ready Signoff Count from 01-Feb-2025 to 03-Feb-2025",
        FilterSelectionMessage.getTrendReportMessage(
            date(2025, Calendar.FEBRUARY, 1, 0, 0), date(2025, Calendar.FEBRUARY, 3, 0, 0)));
  }

  @Test
  void messageWithNoFiltersIsEmpty() {
    assertEquals(
        "",
        FilterSelectionMessage.getMessage(
            null, null, null, null, null, null, null, null, null, null, null));
    assertEquals(
        "",
        FilterSelectionMessage.getMessage(
            List.of(), null, null, null, null, List.of(), null, null, "", "", null));
  }

  @Test
  void messageJoinsFiltersWithAnd() {
    String message =
        FilterSelectionMessage.getMessage(
            Arrays.asList(destination("Hall A"), destination("Hall B")),
            CATEGORY,
            SYSTEM,
            REGION,
            GROUP,
            Arrays.asList(Status.READY, Status.NOT_READY),
            true,
            false,
            "IPM",
            "maintenance",
            DataSource.CED);

    // A leading space separates the message from the text before it on the page
    assertEquals(
        " Beam Destination \"Hall A\", \"Hall B\" and Category \"Magnets\" and System"
            + " \"Dipoles\" and Region \"North Linac\" and Group \"Ops\" and Status \"Ready\","
            + " \"Not Ready\" and Source \"CED\" and Masked \"Yes\" and Unpowered \"No\" and"
            + " Component Name \"IPM\" and Reason \"maintenance\"",
        message);
  }

  @Test
  void savedSignoffMessage() {
    assertEquals("", FilterSelectionMessage.getSavedSignoffMessage(null, null, null));
    assertEquals(
        " Saved Signoff Type \"Daily\" and System \"Dipoles\" and Group \"Ops\"",
        FilterSelectionMessage.getSavedSignoffMessage(
            new SavedSignoffType(BigInteger.ONE, "Daily"), SYSTEM, GROUP));
  }

  @Test
  void activityScreenMessage() {
    String message =
        FilterSelectionMessage.getActivityScreenMessage(
            null,
            null,
            SYSTEM,
            null,
            null,
            "jdoe",
            "IPM",
            Status.CHECKED,
            SignoffChangeType.DOWNGRADE,
            date(2025, Calendar.JANUARY, 2, 7, 5),
            date(2025, Calendar.JANUARY, 3, 19, 30));

    assertEquals(
        " System \"Dipoles\" and User \"jdoe\" and Component \"IPM\" and Status \"Checked\" and"
            + " Change \"Downgrade\" and Start Date \"2025-01-02 07:05\" and End Date"
            + " \"2025-01-03 19:30\"",
        message);
  }

  @Test
  void signoffScreenMessageHasNoLeadingSpace() {
    assertEquals(
        "",
        FilterSelectionMessage.getSignoffScreenMessage(
            null, null, null, null, null, null, null, null, null, null, null));

    String message =
        FilterSelectionMessage.getSignoffScreenMessage(
            GROUP,
            CATEGORY,
            null,
            List.of(destination("Hall C")),
            Arrays.asList(REGION, new Region(BigInteger.TWO, "South Linac")),
            List.of(Status.MASKED),
            false,
            true,
            "IPM",
            null,
            null);

    assertEquals(
        "Group \"Ops\" and Category \"Magnets\" and Beam Destination \"Hall C\" and Region"
            + " \"North Linac\", \"South Linac\" and Status \"Masked (Director)\" and Ready Turn"
            + " \"No\" and Masked \"Yes\" and Name \"IPM\"",
        message);
  }

  @Test
  void overallStatusFootnotes() {
    List<BeamDestination> destinations = List.of(destination("Hall D"));

    List<Map.Entry<String, Object>> footnotes =
        FilterSelectionMessage.getOverallStatusFootnoteList(
            destinations, CATEGORY, null, REGION, GROUP);

    assertEquals(4, footnotes.size());
    assertEquals(Map.entry("Beam Destination", destinations), footnotes.get(0));
    assertEquals(Map.entry("Category", "Magnets"), footnotes.get(1));
    assertEquals(Map.entry("Region", "North Linac"), footnotes.get(2));
    assertEquals(Map.entry("Group", "Ops"), footnotes.get(3));
    assertTrue(
        FilterSelectionMessage.getOverallStatusFootnoteList(null, null, null, null, null)
            .isEmpty());
  }
}
