package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The (Un)mask dialog on Setup › Component List. Each test adds its own component in system 141,
 * whose responsible groups each get a signoff, and deletes it afterwards.
 */
class MaskComponentIT {

  /** RF in the Injector (container/oracle/initdb.d/05_test_data.sql) */
  private static final String SYSTEM_ID = "141";

  private static final String REGION_ID = "1";

  /** The format of the app's date pickers */
  private static final DateTimeFormatter FRIENDLY =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.US);

  private String token;
  private String name;
  private String componentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @BeforeEach
  void addComponent() throws Exception {
    token = Srm.token(Srm.ADMIN);
    name = "it-" + UUID.randomUUID().toString().substring(0, 8);

    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                token,
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", SYSTEM_ID),
                    Map.entry("region-id", REGION_ID),
                    Map.entry("masked", "N"))));

    assertEquals("ok", added.getString("stat"), added.toString());

    JsonArray found = Srm.json(Srm.get("/data/components?q=" + name, null)).getJsonArray("data");

    assertEquals(1, found.size(), found.toString());

    componentId = found.getJsonObject(0).getJsonNumber("id").toString();
  }

  @AfterEach
  void deleteComponent() throws Exception {
    if (componentId != null) {
      JsonObject deleted =
          Srm.json(
              Srm.post(
                  "/setup/ajax/delete-component",
                  token,
                  List.of(Map.entry("component-id", componentId))));

      assertEquals("ok", deleted.getString("stat"), deleted.toString());
    }
  }

  /** The dialog warns that unmasking resets all group signoffs to Not Ready. */
  @Test
  void unmaskResetsSignoffsToNotReady() throws Exception {
    mask("Y", "masked by MaskComponentIT", LocalDateTime.now().plusWeeks(1));

    List<String> masked = statuses();

    assertFalse(masked.isEmpty(), "system " + SYSTEM_ID + " has no responsible groups");
    masked.forEach(status -> assertEquals("Masked (Administrator)", status, masked.toString()));

    mask("N", "", null);

    List<String> unmasked = statuses();

    assertEquals(masked.size(), unmasked.size(), unmasked.toString());
    unmasked.forEach(status -> assertEquals("Not Ready", status, unmasked.toString()));

    // Group 4 (Alignment) is one of the system's groups
    assertEquals(
        List.of(
            "Downgrade; Not Ready; Auto-downgrade due to unmasking",
            "Comment; Masked (Administrator); Masked",
            "Comment; Not Ready; New component added"),
        history("4"));
  }

  @Test
  void unmaskReasonIsTheHistoryComment() throws Exception {
    mask("Y", "masked by MaskComponentIT", LocalDateTime.now().plusWeeks(1));
    mask("N", "unmasked by MaskComponentIT", null);

    assertEquals("Downgrade; Not Ready; unmasked by MaskComponentIT", history("4").get(0));
  }

  private void mask(String masked, String reason, LocalDateTime expiration) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/mask-component",
            token,
            List.of(
                Map.entry("componentId", componentId),
                Map.entry("masked", masked),
                Map.entry("maskedReason", reason),
                Map.entry(
                    "expiration",
                    expiration == null
                        ? ""
                        : FRIENDLY.format(expiration.truncatedTo(ChronoUnit.MINUTES)))));

    assertEquals(200, response.statusCode(), response.body());
    assertEquals("ok", Srm.json(response).getString("stat"), response.body());
  }

  /** Each group's status icon title on the component's detail page. */
  private List<String> statuses() throws Exception {
    HttpResponse<String> response =
        Srm.get("/reports/component/detail?componentId=" + componentId, null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse(response.body()).select("table.data-table td > div span.small-icon").stream()
        .map(icon -> icon.attr("title"))
        .toList();
  }

  /** The group's signoff history for the component, as "Change; Status; Comment", newest first. */
  private List<String> history(String groupId) throws Exception {
    HttpResponse<String> response =
        Srm.get(
            "/reports/signoff-activity?qualified=&componentName=" + name + "&groupId=" + groupId,
            null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse(response.body()).select("table.data-table tbody tr").stream()
        .map(row -> row.select("td").get(2).text() + "; " + row.select("td").last().text())
        .toList();
  }
}
