package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** The pages and data anyone may read, and the setup pages only admins may. */
class PagesIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/",
        "/readiness",
        "/signoff",
        "/reports/component",
        "/reports/component/detail?componentId=2763",
        "/reports/signoff",
        "/reports/overall-status",
        "/reports/group-status",
        "/reports/group-responsibility",
        "/reports/group-leader",
        "/reports/signoff-activity",
        "/reports/signoff-summary",
        "/reports/all-activity",
        "/masks/current",
        "/masks/requests",
        "/masks/candidates",
        "/system-detail?systemId=141",
        "/group-detail?groupId=4",
        "/checklists",
        "/links"
      })
  void anyoneReadsPage(String path) throws Exception {
    assertRendered(Srm.getPage(path, null));
  }

  @ParameterizedTest
  @CsvSource({
    "/system-detail, 400",
    "/system-detail?systemId=, 400",
    "/system-detail?systemId=abc, 400",
    "/system-detail?systemId=999999, 404",
    "/group-detail, 400",
    "/group-detail?groupId=, 400",
    "/group-detail?groupId=abc, 400",
    "/group-detail?groupId=999999, 404"
  })
  void detailPageRejectsMissingOrUnknownId(String path, int status) throws Exception {
    HttpResponse<String> response = Srm.get(path, null);

    assertEquals(status, response.statusCode(), path + ": " + response.body());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/setup/component-list",
        "/setup/category-tree",
        "/setup/group-list",
        "/setup/group-responsibility",
        "/setup/system-participation",
        "/reports/inventory-activity/component-audit"
      })
  void onlyAdminReadsSetupPage(String path) throws Exception {
    assertRendered(Srm.getPage(path, Srm.token(Srm.ADMIN)));

    assertEquals(403, Srm.getPage(path, Srm.token(Srm.USER)).statusCode());

    HttpResponse<String> anonymous = Srm.getPage(path, null);
    assertEquals(302, anonymous.statusCode());
    assertTrue(
        anonymous
            .headers()
            .firstValue("Location")
            .orElseThrow()
            .contains("/realms/test-realm/protocol/openid-connect/auth"),
        "not sent to Keycloak's login: " + anonymous.headers().map());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/data/systems", "/data/groups", "/data/regions", "/data/destinations"})
  void anyoneReadsDataList(String path) throws Exception {
    JsonObject result = data(path);

    assertFalse(result.getJsonArray("data").isEmpty(), result.toString());
  }

  @Test
  void anyoneReadsCategoryTree() throws Exception {
    JsonObject root = data("/data/categories").getJsonObject("data");

    assertEquals("JLAB", root.getString("name"));
    assertFalse(root.getJsonArray("children").isEmpty(), root.toString());
  }

  @Test
  void componentsFilterByName() throws Exception {
    JsonArray components = data("/data/components?q=0L03").getJsonArray("data");

    assertEquals(1, components.size(), components.toString());
    assertEquals(2763, components.getJsonObject(0).getInt("id"));
    assertEquals("0L03", components.getJsonObject(0).getString("name"));
  }

  @Test
  void componentsPage() throws Exception {
    JsonObject result = data("/data/components?max=5&offset=0");

    assertEquals(5, result.getJsonArray("data").size(), result.toString());
    assertTrue(result.getInt("total_records") > 5, result.toString());
  }

  private static JsonObject data(String path) throws Exception {
    HttpResponse<String> response = Srm.get(path, null);

    assertEquals(200, response.statusCode(), response.body());

    JsonObject result = Srm.json(response);

    assertEquals("ok", result.getString("stat"), result.toString());

    return result;
  }

  /** A JSP can fail after the 200 status is sent, leaving the page cut short. */
  private static void assertRendered(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.uri() + ": " + response.body());
    assertTrue(
        response.body().stripTrailing().endsWith("</html>"),
        response.uri() + " cut short: ..." + tail(response.body()));
  }

  private static String tail(String body) {
    return body.substring(Math.max(0, body.length() - 500));
  }
}
