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
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Mask dialog on Masks › Candidates and the Edit Mask dialog on Masks › Current, which both
 * save with /ajax/edit-component-exception. Each test adds its own component in system 141, whose
 * responsible groups each get a Not Ready signoff, and deletes it afterwards.
 */
class EditMaskIT {

  /** RF in the Injector (container/oracle/initdb.d/05_test_data.sql) */
  private static final String SYSTEM_ID = "141";

  private static final String REGION_ID = "1";

  /** The format of the app's date pickers */
  private static final DateTimeFormatter FRIENDLY =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.US);

  private String token;
  private String componentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @BeforeEach
  void addComponent() throws Exception {
    token = Srm.token(Srm.ADMIN);
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);

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

  @Test
  void reasonMasksAsCrewChief() throws Exception {
    assertEquals(
        "Success", save("masked by EditMaskIT", LocalDateTime.now().plusWeeks(1)).status());

    assertSignoffs("Masked (Crew Chief)");
  }

  /** A blank reason used to unmask the component and set its signoffs to Checked. */
  @Test
  void blankReasonIsRefusedOnCandidate() throws Exception {
    assertSignoffs("Not Ready");

    assertEquals(new Result("Error", "Please provide a reason"), save(" ", null));

    assertSignoffs("Not Ready");
  }

  /** A blank reason used to remove the mask and set the signoffs to Checked. */
  @Test
  void blankReasonIsRefusedOnEdit() throws Exception {
    assertEquals(
        "Success", save("masked by EditMaskIT", LocalDateTime.now().plusWeeks(1)).status());

    assertEquals(new Result("Error", "Please provide a reason"), save("", null));

    assertSignoffs("Masked (Crew Chief)");
  }

  private record Result(String status, String reason) {}

  private Result save(String reason, LocalDateTime expiration) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/ajax/edit-component-exception",
            token,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("exceptionReason", reason),
                Map.entry(
                    "expiration-date",
                    expiration == null
                        ? ""
                        : FRIENDLY.format(expiration.truncatedTo(ChronoUnit.MINUTES)))));

    assertEquals(200, response.statusCode(), response.body());

    Document xml = Jsoup.parse(response.body());
    String status = xml.select(".status").text();

    return new Result(status, "Success".equals(status) ? null : xml.select(".reason").text());
  }

  /** Asserts that each group's status icon title on the component's detail page is the status. */
  private void assertSignoffs(String status) throws Exception {
    HttpResponse<String> response =
        Srm.get("/reports/component/detail?componentId=" + componentId, null);

    assertEquals(200, response.statusCode(), response.body());

    List<String> statuses =
        Jsoup.parse(response.body()).select("table.data-table td > div span.small-icon").stream()
            .map(icon -> icon.attr("title"))
            .toList();

    assertFalse(statuses.isEmpty(), "system " + SYSTEM_ID + " has no responsible groups");
    statuses.forEach(s -> assertEquals(status, s, statuses.toString()));
  }
}
