package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
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
 * The Signoff button on Setup › Saved Signoffs, which applies the selected saved signoffs with the
 * page's Max Last Modified Date. Each saved signoff filters on the name of a component the test
 * adds, so that it changes nothing else. Group 4 (Alignment) is responsible for systems 141 and 42
 * and needs no published checklist on either. Saved signoffs never mark a signoff as needing
 * attention, which would post to the real JLab logbook.
 */
class SavedSignoffIT {

  private static final String SYSTEM_ID = "141";
  private static final String SYSTEM_NAME = "Cryomodules";

  /** BPM (SEE), where group 4 is also the first group and needs no published checklist. */
  private static final String OTHER_SYSTEM_ID = "42";

  private static final String GROUP_ID = "4";
  private static final String GROUP_NAME = "Alignment";
  private static final String READY = "1";
  private static final String CHECKED = "50";
  private static final String NOT_READY = "100";
  private static final String CEBAF_TYPE = "2";

  /** The page's date-time format (smoothness's friendly date-time). */
  private static final DateTimeFormatter FRIENDLY =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.US);

  private final List<String> componentIds = new ArrayList<>();
  private final List<String> savedSignoffIds = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @AfterEach
  void cleanUp() throws Exception {
    String token = Srm.token(Srm.ADMIN);

    if (!savedSignoffIds.isEmpty()) {
      List<Map.Entry<String, String>> form = new ArrayList<>();

      for (String id : savedSignoffIds) {
        form.add(Map.entry("saved-signoff-id-array[]", id));
      }

      assertSuccess(Srm.post("/setup/ajax/remove-saved-signoff", token, form).body());
    }

    for (String componentId : componentIds) {
      JsonObject deleted =
          Srm.json(
              Srm.post(
                  "/setup/ajax/delete-component",
                  token,
                  List.of(Map.entry("component-id", componentId))));

      assertEquals("ok", deleted.getString("stat"), deleted.toString());
    }
  }

  @Test
  void signoffLeavesSignoffsChangedAfterMaxLastModified() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String name = addComponent(token);
    String componentId = componentIds.get(0);

    assertSuccess(batchSignoff(token, componentId, CHECKED, "checked by SavedSignoffIT"));

    String savedId = addSavedSignoff(token, name, NOT_READY, "");

    // The component's signoff was changed just now, after yesterday's limit.
    assertSuccess(signoffSaved(token, List.of(savedId), LocalDateTime.now().minusDays(1)));

    assertEquals("Checked", currentStatus(componentId));
  }

  /** A saved signoff that matches no components is skipped, and the others are still applied. */
  @Test
  void signoffSkipsSavedSignoffsThatMatchNothing() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String unmatchedName = addComponent(token, SYSTEM_ID);
    String matchedName = addComponent(token, OTHER_SYSTEM_ID);

    assertSuccess(batchSignoff(token, componentIds.get(0), CHECKED, "checked by SavedSignoffIT"));
    assertSuccess(batchSignoff(token, componentIds.get(1), CHECKED, "checked by SavedSignoffIT"));

    // Only Ready signoffs, and this component's is Checked.
    String unmatchedId = addSavedSignoff(token, unmatchedName, SYSTEM_ID, NOT_READY, READY);
    String matchedId = addSavedSignoff(token, matchedName, OTHER_SYSTEM_ID, NOT_READY, "");

    assertSuccess(signoffSaved(token, List.of(unmatchedId, matchedId), null));

    assertEquals("Checked", currentStatus(componentIds.get(0)));
    assertEquals("Not Ready", currentStatus(componentIds.get(1)));
  }

  @Test
  void signoffChangesSignoffsChangedBeforeMaxLastModified() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String name = addComponent(token);
    String componentId = componentIds.get(0);

    assertSuccess(batchSignoff(token, componentId, CHECKED, "checked by SavedSignoffIT"));

    String savedId = addSavedSignoff(token, name, NOT_READY, "");

    assertSuccess(signoffSaved(token, List.of(savedId), LocalDateTime.now().plusDays(1)));

    assertEquals("Not Ready", currentStatus(componentId));
  }

  /**
   * Saved signoffs on the same system may change the same signoffs, since a downgrade cascades to
   * later groups, so a run that selects more than one per system is refused before any change.
   */
  @Test
  void signoffRefusesMoreThanOneSavedSignoffPerSystem() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String firstName = addComponent(token);
    String secondName = addComponent(token);

    assertSuccess(batchSignoff(token, componentIds.get(0), CHECKED, "checked by SavedSignoffIT"));
    assertSuccess(batchSignoff(token, componentIds.get(1), CHECKED, "checked by SavedSignoffIT"));

    String firstId = addSavedSignoff(token, firstName, NOT_READY, "");
    String secondId = addSavedSignoff(token, secondName, NOT_READY, "");

    Document body = Jsoup.parse(signoffSaved(token, List.of(firstId, secondId), null));

    assertEquals("Error", body.select(".status").text(), body.toString());
    assertEquals(
        "Only one saved signoff rule per System per bulk execution. Systems selected more than"
            + " once: "
            + SYSTEM_NAME
            + " (2)",
        body.select(".reason").text());

    assertEquals("Checked", currentStatus(componentIds.get(0)));
    assertEquals("Checked", currentStatus(componentIds.get(1)));
  }

  private String addComponent(String token) throws Exception {
    return addComponent(token, SYSTEM_ID);
  }

  private String addComponent(String token, String systemId) throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                token,
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", systemId),
                    Map.entry("region-id", "1"),
                    Map.entry("masked", "N"))));

    assertEquals("ok", added.getString("stat"), added.toString());

    JsonArray found = Srm.json(Srm.get("/data/components?q=" + name, null)).getJsonArray("data");
    assertEquals(1, found.size(), found.toString());

    componentIds.add(found.getJsonObject(0).getJsonNumber("id").toString());

    return name;
  }

  private String addSavedSignoff(
      String token, String componentName, String statusId, String filterStatusId) throws Exception {
    return addSavedSignoff(token, componentName, SYSTEM_ID, statusId, filterStatusId);
  }

  /** A saved signoff for one component; filterStatusId is "" for any status. */
  private String addSavedSignoff(
      String token, String componentName, String systemId, String statusId, String filterStatusId)
      throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/add-saved-signoff",
            token,
            List.of(
                Map.entry("typeId", CEBAF_TYPE),
                Map.entry("signoffName", "SavedSignoffIT " + componentName),
                Map.entry("signoffStatusId", statusId),
                Map.entry("comments", "saved signoff by SavedSignoffIT"),
                Map.entry("systemId", systemId),
                Map.entry("groupId", GROUP_ID),
                Map.entry("regionId", ""),
                Map.entry("filterStatusId", filterStatusId),
                Map.entry("componentName", componentName)));

    assertEquals(200, response.statusCode(), response.body());
    assertSuccess(response.body());

    String id = Jsoup.parse(response.body()).select(".saved-signoff-id").text();
    savedSignoffIds.add(id);

    return id;
  }

  /**
   * What the Signoff button posts: the selected rows and the Max Last Modified Date field, which is
   * empty when maxModified is null.
   */
  private static String signoffSaved(
      String token, List<String> savedSignoffIds, LocalDateTime maxModified) throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();

    for (String id : savedSignoffIds) {
      form.add(Map.entry("saved-signoff-id-array[]", id));
    }

    form.add(Map.entry("maxModified", maxModified == null ? "" : FRIENDLY.format(maxModified)));

    HttpResponse<String> response = Srm.post("/setup/ajax/signoff-multiple-saved", token, form);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static String batchSignoff(
      String token, String componentId, String statusId, String comment) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/ajax/batch-signoff",
            token,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("groupId[]", GROUP_ID),
                Map.entry("statusId", statusId),
                Map.entry("comment", comment),
                Map.entry("needsAttention", "false")));

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static void assertSuccess(String body) {
    assertEquals("Success", Jsoup.parse(body).select(".status").text(), body);
  }

  /** The group's status icon title on the component's detail page. */
  private static String currentStatus(String componentId) throws Exception {
    HttpResponse<String> response =
        Srm.get("/reports/component/detail?componentId=" + componentId, null);

    assertEquals(200, response.statusCode(), response.body());

    Document page = Jsoup.parse(response.body());

    for (Element div : page.select("table.data-table td > div")) {
      if (div.text().equals(GROUP_NAME)) {
        return div.select("span.small-icon").attr("title");
      }
    }

    throw new AssertionError("No " + GROUP_NAME + " row: " + response.body());
  }
}
