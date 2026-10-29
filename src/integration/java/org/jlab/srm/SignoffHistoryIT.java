package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignoffHistoryIT {

  private String componentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @AfterEach
  void deleteComponent() throws Exception {
    if (componentId != null) {
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
  void rapidMaskChangesRetainEveryHistoryRow() throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    String token = Srm.token(Srm.ADMIN);
    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                token,
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", "141"),
                    Map.entry("region-id", "1"),
                    Map.entry("masked", "N"))));

    assertEquals("ok", added.getString("stat"), added.toString());

    JsonArray found = Srm.json(Srm.get("/data/components?q=" + name, null)).getJsonArray("data");
    assertEquals(1, found.size(), found.toString());
    componentId = found.getJsonObject(0).getJsonNumber("id").toString();

    String unmaskComment = "rapid unmask " + name;

    // Exercise consecutive changes without the one-second workaround. Each change
    // writes signoff history for every group responsible for this component.
    for (String masked : List.of("Y", "N", "Y")) {
      HttpResponse<String> response =
          Srm.post(
              "/setup/ajax/mask-component",
              token,
              List.of(
                  Map.entry("componentId", componentId),
                  Map.entry("masked", masked),
                  Map.entry("maskedReason", unmaskComment)));

      assertEquals(200, response.statusCode(), response.body());
      assertEquals("ok", Srm.json(response).getString("stat"), response.body());
    }

    HttpResponse<String> history =
        Srm.get("/reports/signoff-activity?qualified=&componentName=" + name + "&groupId=4", null);

    assertEquals(200, history.statusCode(), history.body());

    List<Element> rows =
        Jsoup.parse(history.body()).select("table.data-table tbody tr").stream()
            .filter(
                row -> {
                  String comment = row.select("td").last().text();
                  return comment.equals("Masked") || comment.equals(unmaskComment);
                })
            .toList();

    assertEquals(3, rows.size(), history.body());
    assertEquals(
        List.of(
            "Comment; Masked (Administrator)",
            "Downgrade; Checked",
            "Comment; Masked (Administrator)"),
        rows.stream().map(row -> row.select("td").get(2).text()).toList());
  }
}
