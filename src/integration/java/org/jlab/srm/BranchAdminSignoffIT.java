package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Signoffs to Ready and N/A by a branch admin, who may make them for any group on the components of
 * their own branch, as an admin or the group's leader may. Each test adds its own system under the
 * VTA category, with two responsible groups that need no checklist, and its own components, and
 * removes them after. No request here marks the signoff as needing attention, which would post to
 * the real JLab logbook.
 */
class BranchAdminSignoffIT {

  /** The top-level VTA category in the test data, whose admin role is vtaadm */
  private static final String VTA = "900";

  /** Has vtaadm, and leads neither group */
  private static final String VTA_ADMIN = "user3";

  /** Has uitfadm, which is no branch's admin role */
  private static final String OTHER_USER = "user2";

  /** Alignment, the group signed off for, and a second, as one group must stay other than N/A */
  private static final String GROUP_ID = "4";

  private static final String GROUP_NAME = "Alignment";
  private static final String SECOND_GROUP_ID = "1";

  /** The Cryomodules system in the test data, in the CEBAF branch; Alignment is responsible */
  private static final String CEBAF_SYSTEM_ID = "141";

  private static final String NOT_APPLICABLE = "0";
  private static final String READY = "1";
  private static final String CHECKED = "50";

  private static String admin;

  private final List<String> componentIds = new ArrayList<>();
  private String systemId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @BeforeEach
  void addVtaSystem() throws Exception {
    String name = "IT Signoff " + UUID.randomUUID().toString().substring(0, 8);

    HttpResponse<String> added =
        Srm.post(
            "/setup/ajax/add-system",
            admin,
            List.of(
                Map.entry("parentId", VTA),
                Map.entry("name", name),
                Map.entry("description", ""),
                Map.entry("weight", "")));

    assertTrue(added.body().contains("<span class=\"status\">Success</span>"), added.body());

    HttpResponse<String> page = Srm.getPage("/setup/category-tree", admin);
    Matcher m =
        Pattern.compile("<option value=\"(\\d+)\"[^>]*>" + Pattern.quote(name) + "</option>")
            .matcher(page.body());

    assertTrue(m.find(), "no system " + name);
    systemId = m.group(1);

    // Only a system that participates in the SRM application has component statuses
    HttpResponse<String> participating =
        Srm.post(
            "/setup/ajax/toggle-system-participation",
            admin,
            List.of(Map.entry("systemId", systemId), Map.entry("applicationId", "1")));

    assertTrue(
        participating.body().contains("<span class=\"status\">Success</span>"),
        participating.body());

    int order = 1;

    for (String groupId : List.of(GROUP_ID, SECOND_GROUP_ID)) {
      JsonObject created =
          Srm.json(
              Srm.post(
                  "/setup/ajax/create-group-responsibility",
                  admin,
                  List.of(
                      Map.entry("systemId", systemId),
                      Map.entry("groupId", groupId),
                      Map.entry("checklistRequired", "N"),
                      Map.entry("order", String.valueOf(order++)))));

      assertEquals("ok", created.getString("stat"), created.toString());
    }
  }

  @AfterEach
  void removeAdded() throws Exception {
    for (String componentId : componentIds) {
      Srm.post(
          "/setup/ajax/delete-component", admin, List.of(Map.entry("component-id", componentId)));
    }

    if (systemId != null) {
      Srm.post("/setup/ajax/remove-system", admin, List.of(Map.entry("systemId", systemId)));
    }
  }

  @Test
  void branchAdminSignsOffComponentInBranchAsReady() throws Exception {
    String token = Srm.token(VTA_ADMIN);
    String id = addComponent(systemId);

    assertSuccess(signoff(token, id, CHECKED));
    assertSuccess(signoff(token, id, READY));

    assertEquals("Ready", currentStatus(id));
  }

  @Test
  void branchAdminSignsOffComponentInBranchAsNotApplicable() throws Exception {
    String id = addComponent(systemId);

    assertSuccess(signoff(Srm.token(VTA_ADMIN), id, NOT_APPLICABLE));

    assertEquals("Not Applicable", currentStatus(id));
  }

  @Test
  void branchAdminCannotSignOffComponentInAnotherBranchAsReady() throws Exception {
    String token = Srm.token(VTA_ADMIN);
    String id = addComponent(CEBAF_SYSTEM_ID);

    // Anyone logged in may check a component
    assertSuccess(signoff(token, id, CHECKED));

    assertNotAuthorized(signoff(token, id, READY));
    assertNotAuthorized(signoff(token, id, NOT_APPLICABLE));

    assertEquals("Checked", currentStatus(id));
  }

  @Test
  void userWithoutBranchCannotSignOffComponentAsReady() throws Exception {
    String token = Srm.token(OTHER_USER);
    String id = addComponent(systemId);

    assertSuccess(signoff(token, id, CHECKED));

    assertNotAuthorized(signoff(token, id, READY));
    assertNotAuthorized(signoff(token, id, NOT_APPLICABLE));

    assertEquals("Checked", currentStatus(id));
  }

  private String addComponent(String system) throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                admin,
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", system),
                    Map.entry("region-id", "1"),
                    Map.entry("masked", "N"))));

    assertEquals("ok", added.getString("stat"), added.toString());

    JsonArray found = Srm.json(Srm.get("/data/components?q=" + name, null)).getJsonArray("data");
    assertEquals(1, found.size(), found.toString());
    String id = found.getJsonObject(0).getJsonNumber("id").toString();

    componentIds.add(id);

    return id;
  }

  /** Signs off the component for Alignment as the signoff page does, with its rules. */
  private static String signoff(String token, String componentId, String statusId)
      throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/ajax/batch-signoff",
            token,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("groupId[]", GROUP_ID),
                Map.entry("statusId", statusId),
                Map.entry("comment", "branch admin signoff test"),
                Map.entry("needsAttention", "false")));

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static void assertSuccess(String body) {
    assertEquals("Success", Jsoup.parse(body).select(".status").text(), body);
  }

  private static void assertNotAuthorized(String body) {
    assertEquals("Not authorized", Jsoup.parse(body).select(".reason").text(), body);
  }

  /** Alignment's status icon title on the component's detail page. */
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
