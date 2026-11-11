package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The signoff report's Status filter, which takes more than one status. The test data's signoffs
 * are all Not Ready, so a test adds a component to system 141 and signs off group 4 (Alignment),
 * which needs no published checklist, as Checked. No request here marks the signoff as needing
 * attention, which would post to the real JLab logbook.
 */
class SignoffReportIT {

  private static final String CHECKED = "50";
  private static final String NOT_READY = "100";

  /** The record total in the selection message: {total} or {first - last of total}. */
  private static final Pattern TOTAL = Pattern.compile("\\{(?:[\\d,]+ - [\\d,]+ of )?([\\d,]+)\\}");

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
  void statusesAreCombined() throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    String token = Srm.token(Srm.ADMIN);

    addComponent(token, name);
    signOffChecked(token);

    String component = "&componentName=" + name;
    long all = total(page(component));
    long checked = total(page(component + "&statusId=" + CHECKED));
    long notReady = total(page(component + "&statusId=" + NOT_READY));

    assertEquals(1, checked);
    assertEquals(all, checked + notReady);
    // Checked last: a report that reads only the first status leaves out the Checked signoff.
    assertEquals(all, total(page(component + "&statusId=" + NOT_READY + "&statusId=" + CHECKED)));
  }

  @Test
  void statusesAreInMessageSelectAndExcelForm() throws Exception {
    Document page = page("&statusId=" + CHECKED + "&statusId=" + NOT_READY);

    assertTrue(
        page.select(".message-box").text().startsWith("Status \"Checked\", \"Not Ready\""),
        page.select(".message-box").text());
    assertEquals(
        List.of(CHECKED, NOT_READY),
        page.select("#status-select option[selected]").eachAttr("value"));
    assertEquals(
        List.of(CHECKED, NOT_READY),
        page.select("#excel-form input[name=statusId]").eachAttr("value"));
  }

  @Test
  void excelTakesStatuses() throws Exception {
    HttpResponse<String> response =
        Srm.get("/excel/signoff.xlsx?statusId=" + CHECKED + "&statusId=" + NOT_READY, null);

    assertEquals(200, response.statusCode());
  }

  private void addComponent(String token, String name) throws Exception {
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
  }

  private void signOffChecked(String token) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/ajax/batch-signoff",
            token,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("groupId[]", "4"),
                Map.entry("statusId", CHECKED),
                Map.entry("comment", "signoff report statuses"),
                Map.entry("needsAttention", "false")));

    assertEquals(200, response.statusCode(), response.body());
    assertEquals("Success", Jsoup.parse(response.body()).select(".status").text(), response.body());
  }

  private static Document page(String filters) throws Exception {
    HttpResponse<String> response = Srm.getPage("/reports/signoff?qualified=" + filters, null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse(response.body());
  }

  private static long total(Document page) {
    String message = page.select(".message-box").text();
    Matcher matcher = TOTAL.matcher(message);

    assertTrue(matcher.find(), message);

    return Long.parseLong(matcher.group(1).replace(",", ""));
  }
}
