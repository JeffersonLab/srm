package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Group Action Report emailed daily to a group's leaders lists only the components in the
 * current run's destinations. Group 4 (Alignment) is responsible for system 141 and needs no
 * published checklist. No request here marks the signoff as needing attention, which would post to
 * the real JLab logbook.
 */
class GroupDailyEmailIT {

  private static final String SYSTEM_ID = "141";
  private static final String GROUP_ID = "4";
  private static final String CHECKED = "50";
  private static final String READY = "1";
  private static final String NOT_READY = "100";

  /** The targets in the test data (container/oracle/initdb.d): Hall A to Hall D */
  private static final List<String> TARGETS = List.of("6", "7", "8", "9");

  private static final String HALL_A = "6";

  /** 1D Spectrometer, which isn't a target in the test data */
  private static final String NOT_A_TARGET = "1";

  /** The format of the app's date pickers */
  private static final DateTimeFormatter FRIENDLY =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.US);

  private static String admin;

  private final List<String> componentIds = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @AfterEach
  void deleteComponents() throws Exception {
    for (String componentId : componentIds) {
      JsonObject deleted =
          Srm.json(
              Srm.post(
                  "/setup/ajax/delete-component",
                  admin,
                  List.of(Map.entry("component-id", componentId))));

      assertEquals("ok", deleted.getString("stat"), deleted.toString());
    }
  }

  @Test
  void listsDowngradeInCurrentRun() throws Exception {
    String name = addComponent(HALL_A);
    String comment = "downgraded by GroupDailyEmailIT " + name;

    downgrade(name, comment);

    Document email = email();

    assertEquals("DOWNGRADE", changeOf(email, comment), email.html());
    assertTrue(email.select("#current-run").text().contains("Hall A"), email.html());
    assertEquals("", email.select("#doNotSend").text(), email.html());
  }

  @Test
  void leavesOutDowngradeOutsideCurrentRun() throws Exception {
    String name = addComponent(NOT_A_TARGET);

    downgrade(name, "downgraded by GroupDailyEmailIT " + name);

    Document email = email();

    assertFalse(email.text().contains(name), email.html());
  }

  @Test
  void leavesOutDowngradeWithoutDestination() throws Exception {
    String name = addComponent(null);

    downgrade(name, "downgraded by GroupDailyEmailIT " + name);

    Document email = email();

    assertFalse(email.text().contains(name), email.html());
  }

  /** Without a current run, nothing is in it, rather than everything as in a report's filter. */
  @Test
  void sendsNothingWithoutCurrentRun() throws Exception {
    String name = addComponent(HALL_A);

    downgrade(name, "downgraded by GroupDailyEmailIT " + name);

    try {
      setTargets(List.of());

      Document email = email();

      assertFalse(email.text().contains(name), email.html());
      assertTrue(email.select("#activity-table").isEmpty(), email.html());
      assertEquals(
          "No beam destination is in the current run",
          email.select("#doNotSend").text(),
          email.html());
    } finally {
      setTargets(TARGETS);
    }
  }

  /** Adds a component to the system, in the destination unless it is null, and returns its name */
  private String addComponent(String destinationId) throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                admin,
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", SYSTEM_ID),
                    Map.entry("region-id", "1"),
                    Map.entry("masked", "N"))));

    assertEquals("ok", added.getString("stat"), added.toString());

    JsonArray found = Srm.json(Srm.get("/data/components?q=" + name, null)).getJsonArray("data");
    assertEquals(1, found.size(), found.toString());
    String id = found.getJsonObject(0).getJsonNumber("id").toString();

    componentIds.add(id);

    if (destinationId != null) {
      assertSuccess(
          Srm.post(
              "/setup/ajax/toggle-component-destination",
              admin,
              List.of(Map.entry("componentId", id), Map.entry("destinationId", destinationId))));
    }

    return name;
  }

  /** Signs the group off as Ready, then downgrades it to Not Ready */
  private void downgrade(String name, String comment) throws Exception {
    String id = componentIds.get(componentIds.size() - 1);

    signoff(id, CHECKED, "checked " + name);
    signoff(id, READY, "ready " + name);
    signoff(id, NOT_READY, comment);
  }

  private static void signoff(String componentId, String statusId, String comment)
      throws Exception {
    assertSuccess(
        Srm.post(
            "/ajax/batch-signoff",
            admin,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("groupId[]", GROUP_ID),
                Map.entry("statusId", statusId),
                Map.entry("comment", comment),
                Map.entry("needsAttention", "false"))));
  }

  private static void setTargets(List<String> targets) throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();

    form.add(Map.entry("facility", ""));

    for (String target : targets) {
      form.add(Map.entry("target", target));
    }

    HttpResponse<String> response = Srm.post("/setup/destination-list", admin, form);

    assertEquals(302, response.statusCode(), response.body());
  }

  /**
   * The group's report, for a day either side of now rather than yesterday's 7:00 to today's, so
   * that it has the test's signoffs whatever the app's time zone.
   */
  private static Document email() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    HttpResponse<String> response =
        Srm.get(
            "/group-daily-email?email=Y&groupId="
                + GROUP_ID
                + "&start="
                + encode(FRIENDLY.format(now.minusDays(1)))
                + "&end="
                + encode(FRIENDLY.format(now.plusDays(1))),
            null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse(response.body());
  }

  /** The change of the report's row with the comment */
  private static String changeOf(Document email, String comment) {
    for (Element row : email.select("#activity-table tbody tr")) {
      if (row.select("td").get(3).text().equals(comment)) {
        return row.select("td").get(1).select(".cell-header").text();
      }
    }

    throw new AssertionError("No row with comment " + comment + ": " + email.html());
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static void assertSuccess(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    assertEquals("Success", Jsoup.parse(response.body()).select(".status").text(), response.body());
  }
}
