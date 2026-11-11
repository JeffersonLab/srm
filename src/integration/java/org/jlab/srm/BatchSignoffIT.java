package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Batch signoffs from the signoff page, which post repeated componentId[] and groupId[] pairs.
 * Group 4 (Alignment) is responsible for system 141 and needs no published checklist. No request
 * here marks the signoff as needing attention, which would post to the real JLab logbook.
 */
class BatchSignoffIT {

  private static final String SYSTEM_ID = "141";
  private static final String GROUP_ID = "4";
  private static final String GROUP_NAME = "Alignment";
  private static final String CHECKED = "50";
  private static final String READY = "1";

  /** The history row that adding a component writes for each responsible group. */
  private static final String ADDED = "Comment; Not Ready";

  private final List<String> componentIds = new ArrayList<>();
  private final List<String> componentNames = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @AfterEach
  void deleteComponents() throws Exception {
    for (String componentId : componentIds) {
      JsonObject deleted =
          Srm.json(
              Srm.post(
                  "/setup/ajax/delete-component",
                  Srm.token(Srm.ADMIN),
                  List.of(Map.entry("component-id", componentId))));

      assertEquals("ok", deleted.getString("stat"), deleted.toString());
    }
  }

  @Test
  void singlePairSetsStatusAndHistory() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String id = addComponent(token);

    assertSuccess(batchSignoff(token, List.of(id), CHECKED, "single " + id));

    assertEquals("Checked", currentStatus(id));
    assertEquals(List.of("Upgrade; Checked", ADDED), history(0));
  }

  @Test
  void severalComponentsAreEachSignedOff() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String first = addComponent(token);
    String second = addComponent(token);

    assertSuccess(batchSignoff(token, List.of(first, second), CHECKED, "several"));

    assertEquals("Checked", currentStatus(first));
    assertEquals("Checked", currentStatus(second));
    assertEquals(List.of("Upgrade; Checked", ADDED), history(0));
    assertEquals(List.of("Upgrade; Checked", ADDED), history(1));
  }

  @Test
  void repeatedPairIsSignedOffOnce() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String id = addComponent(token);

    assertSuccess(batchSignoff(token, List.of(id, id), CHECKED, "repeated " + id));

    assertEquals("Checked", currentStatus(id));
    assertEquals(List.of("Upgrade; Checked", ADDED), history(0));
  }

  @Test
  void failedBatchChangesNothing() throws Exception {
    String token = Srm.token(Srm.ADMIN);
    String checked = addComponent(token);
    String notReady = addComponent(token);

    assertSuccess(batchSignoff(token, List.of(checked), CHECKED, "first step"));

    // Ready needs the Checked step first, so the second component fails after the first
    // component's signoff and history are written in the same transaction.
    String body = batchSignoff(token, List.of(checked, notReady), READY, "too soon");

    assertEquals(
        "Cannot signoff component \""
            + componentNames.get(1)
            + "\" for group \""
            + GROUP_NAME
            + "\" to ready until the component is first checked",
        reason(body),
        body);
    assertEquals("Checked", currentStatus(checked));
    assertEquals("Not Ready", currentStatus(notReady));
    assertEquals(List.of("Upgrade; Checked", ADDED), history(0));
    assertEquals(List.of(ADDED), history(1));
  }

  private String addComponent(String token) throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                token,
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
    componentNames.add(name);

    return id;
  }

  private static String batchSignoff(
      String token, List<String> componentIds, String statusId, String comment) throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();

    for (String componentId : componentIds) {
      form.add(Map.entry("componentId[]", componentId));
      form.add(Map.entry("groupId[]", GROUP_ID));
    }

    form.add(Map.entry("statusId", statusId));
    form.add(Map.entry("comment", comment));
    form.add(Map.entry("needsAttention", "false"));

    HttpResponse<String> response = Srm.post("/ajax/batch-signoff", token, form);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static void assertSuccess(String body) {
    assertEquals("Success", Jsoup.parse(body).select(".status").text(), body);
  }

  private static String reason(String body) {
    return Jsoup.parse(body).select(".reason").text();
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

  /**
   * The group's signoff history for the test's nth component, as "Change; Status", newest first.
   */
  private List<String> history(int index) throws Exception {
    HttpResponse<String> response =
        Srm.get(
            "/reports/signoff-activity?qualified=&componentName="
                + componentNames.get(index)
                + "&groupId="
                + GROUP_ID,
            null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse(response.body()).select("table.data-table tbody tr").stream()
        .map(row -> row.select("td").get(2).text())
        .toList();
  }
}
