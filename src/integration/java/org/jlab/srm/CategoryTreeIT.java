package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Setup > Category/System Tree page: setting the weight that orders categories and systems
 * among their siblings. Each test adds its own categories and systems under a new parent and
 * removes them after.
 */
class CategoryTreeIT {

  private static final String PAGE = "/setup/category-tree";

  /** The "Other" category in the test data (container/oracle/initdb.d) */
  private static final String OTHER = "3";

  /** The root category, and the top-level VTA category, with its weight, in the test data */
  private static final String ROOT = "0";

  private static final String VTA = "900";
  private static final String VTA_WEIGHT = "16";

  /** The VTA branch admin (vtaadm) in the test data */
  private static final String VTA_ADMIN = "user3";

  private static final String PARENT = "IT Weight Parent";

  private static String admin;

  private final List<String> categoryIds = new ArrayList<>();
  private final List<String> systemIds = new ArrayList<>();
  private String parentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @BeforeEach
  void addParent() throws Exception {
    assertSuccess(addCategory(OTHER, PARENT, ""));
    parentId = categoryId(page(), PARENT);
  }

  @AfterEach
  void removeAdded() throws Exception {
    for (String id : systemIds) {
      Srm.post("/setup/ajax/remove-system", admin, List.of(Map.entry("systemId", id)));
    }
    // Children first: removing a category leaves its child categories without a parent
    for (int i = categoryIds.size() - 1; i >= 0; i--) {
      Srm.post(
          "/setup/ajax/remove-category",
          admin,
          List.of(Map.entry("categoryId", categoryIds.get(i))));
    }
  }

  @Test
  void categoriesAreOrderedByWeightThenName() throws Exception {
    assertSuccess(addCategory(parentId, "IT Weight C", "1"));
    assertSuccess(addCategory(parentId, "IT Weight A", "2"));
    assertSuccess(addCategory(parentId, "IT Weight B", ""));
    assertSuccess(addCategory(parentId, "IT Weight D", "2"));

    String body = page();
    String a = categoryId(body, "IT Weight A");

    assertOrder(body, "IT Weight C", "IT Weight A", "IT Weight D", "IT Weight B");
    assertTrue(
        body.contains(
            "id=\"node-CATEGORY-" + a + "\" data-node-type=\"CATEGORY\" data-weight=\"2\""),
        body);

    assertSuccess(editCategory(a, parentId, "IT Weight A", "-5"));

    assertOrder(page(), "IT Weight A", "IT Weight C", "IT Weight D", "IT Weight B");

    // A blank weight clears it: last, by name
    assertSuccess(editCategory(a, parentId, "IT Weight A", ""));

    assertOrder(page(), "IT Weight C", "IT Weight D", "IT Weight A", "IT Weight B");
  }

  @Test
  void systemsAreOrderedByWeightThenName() throws Exception {
    assertSuccess(addSystem(parentId, "IT Weight Sys B", "10"));
    assertSuccess(addSystem(parentId, "IT Weight Sys A", "20"));

    String body = page();
    String a = systemId(body, "IT Weight Sys A");

    assertOrder(tree(body), "IT Weight Sys B", "IT Weight Sys A");
    assertTrue(
        Pattern.compile("value=\"" + a + "\"[^>]*data-weight=\"20\"").matcher(body).find(), body);

    assertSuccess(editSystem(a, parentId, "IT Weight Sys A", "5"));

    assertOrder(tree(page()), "IT Weight Sys A", "IT Weight Sys B");
  }

  @Test
  void malformedWeightIsRejected() throws Exception {
    HttpResponse<String> response = addCategory(parentId, "IT Weight Bad", "1.5");

    assertTrue(response.body().contains("Weight must be a whole number"), response.body());
    assertFalse(page().contains("IT Weight Bad"), "added with a malformed weight");

    assertSuccess(addSystem(parentId, "IT Weight Sys Bad", "3"));
    String id = systemId(page(), "IT Weight Sys Bad");

    response = editSystem(id, parentId, "IT Weight Sys Bad", "ten");

    assertTrue(response.body().contains("Weight must be a whole number"), response.body());
    assertTrue(
        Pattern.compile("value=\"" + id + "\"[^>]*data-weight=\"3\"").matcher(page()).find(),
        "weight changed by a malformed edit");
  }

  @Test
  void branchAdminSetsTopLevelCategoryWeight() throws Exception {
    String token = Srm.token(VTA_ADMIN);

    try {
      // A top-level category's parent is the root
      assertSuccess(editCategory(token, VTA, ROOT, "VTA", "-1"));

      String body = page();

      assertTrue(
          body.contains(
              "id=\"node-CATEGORY-" + VTA + "\" data-node-type=\"CATEGORY\" data-weight=\"-1\""),
          body);
      assertOrder(tree(body), "VTA", "CEBAF");
    } finally {
      assertSuccess(editCategory(VTA, ROOT, "VTA", VTA_WEIGHT));
    }
  }

  @Test
  void onlyAdminMovesCategoryToTopLevel() throws Exception {
    String token = Srm.token(VTA_ADMIN);

    assertSuccess(addCategory(token, VTA, "IT Weight VTA Sub", "7"));
    String id = categoryId(page(), "IT Weight VTA Sub");

    HttpResponse<String> response = editCategory(token, id, ROOT, "IT Weight VTA Sub", "7");

    assertTrue(
        response.body().contains("Only an admin can move a category to the top level"),
        response.body());

    assertSuccess(editCategory(id, ROOT, "IT Weight VTA Sub", "7"));
  }

  private HttpResponse<String> addCategory(String parent, String name, String weight)
      throws Exception {
    return addCategory(admin, parent, name, weight);
  }

  private HttpResponse<String> addCategory(String token, String parent, String name, String weight)
      throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/add-category",
            token,
            List.of(
                Map.entry("parentId", parent),
                Map.entry("name", name),
                Map.entry("weight", weight)));

    if (isSuccess(response)) {
      categoryIds.add(categoryId(page(), name));
    }

    return response;
  }

  private HttpResponse<String> editCategory(String id, String parent, String name, String weight)
      throws Exception {
    return editCategory(admin, id, parent, name, weight);
  }

  private static HttpResponse<String> editCategory(
      String token, String id, String parent, String name, String weight) throws Exception {
    return Srm.post(
        "/setup/ajax/edit-category",
        token,
        List.of(
            Map.entry("categoryId", id),
            Map.entry("parentId", parent),
            Map.entry("name", name),
            Map.entry("weight", weight)));
  }

  private HttpResponse<String> addSystem(String parent, String name, String weight)
      throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/add-system",
            admin,
            List.of(
                Map.entry("parentId", parent),
                Map.entry("name", name),
                Map.entry("description", ""),
                Map.entry("weight", weight)));

    if (isSuccess(response)) {
      systemIds.add(systemId(page(), name));
    }

    return response;
  }

  private HttpResponse<String> editSystem(String id, String parent, String name, String weight)
      throws Exception {
    return Srm.post(
        "/setup/ajax/edit-system",
        admin,
        List.of(
            Map.entry("systemId", id),
            Map.entry("parentId", parent),
            Map.entry("name", name),
            Map.entry("description", ""),
            Map.entry("weight", weight)));
  }

  private static String page() throws Exception {
    HttpResponse<String> response = Srm.getPage(PAGE, admin);

    assertEquals(200, response.statusCode(), response.body());
    assertTrue(response.body().stripTrailing().endsWith("</html>"), "page cut short");

    return response.body();
  }

  /** The tree part of the page, without the dialog's lists, which are ordered by name */
  private static String tree(String body) {
    return body.substring(
        body.indexOf("<div id=\"tree\">"), body.indexOf("<div id=\"node-dialog\""));
  }

  private static String categoryId(String body, String name) {
    Matcher m =
        Pattern.compile(
                "<li id=\"node-CATEGORY-(\\d+)\"[^>]*>\\s*<a href=\"#\">"
                    + Pattern.quote(name)
                    + "</a>")
            .matcher(body);

    assertTrue(m.find(), "no category " + name);

    return m.group(1);
  }

  private static String systemId(String body, String name) {
    Matcher m =
        Pattern.compile("<option value=\"(\\d+)\"[^>]*>" + Pattern.quote(name) + "</option>")
            .matcher(body);

    assertTrue(m.find(), "no system " + name);

    return m.group(1);
  }

  private static void assertOrder(String body, String... names) {
    int last = -1;

    for (String name : names) {
      int index = body.indexOf(">" + name + "<");

      assertTrue(index > last, name + " out of order in " + List.of(names));

      last = index;
    }
  }

  private static boolean isSuccess(HttpResponse<String> response) {
    return response.body().contains("<span class=\"status\">Success</span>");
  }

  private static void assertSuccess(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    assertTrue(isSuccess(response), response.body());
  }
}
