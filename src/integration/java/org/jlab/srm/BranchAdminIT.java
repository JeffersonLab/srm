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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Branch admins: the users with the admin role of a branch, a top-level category, which an admin
 * sets on the Setup > Category/System Tree page. Each test adds its own categories and removes them
 * after.
 */
class BranchAdminIT {

  private static final String PAGE = "/setup/category-tree";

  /** The root category, and the "Other" and top-level VTA categories in the test data */
  private static final String ROOT = "0";

  private static final String OTHER = "3";
  private static final String VTA = "900";
  private static final String VTA_WEIGHT = "16";

  /** Has vtaadm, the VTA branch's admin role in the test data (container/oracle/initdb.d) */
  private static final String VTA_ADMIN = "user3";

  /** Has uitfadm, which no branch has as its admin role, and no code names (container/keycloak) */
  private static final String UITF_ADMIN = "user2";

  private static final String UITF_ROLE = "uitfadm";

  private static final String BRANCH = "IT Branch";

  private static final String SETUP_TAB = "/setup/settings\">Setup</a>";

  private static String admin;

  private final List<String> categoryIds = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @AfterEach
  void removeAdded() throws Exception {
    // Children first: removing a category leaves its child categories without a parent
    for (int i = categoryIds.size() - 1; i >= 0; i--) {
      Srm.post(
          "/setup/ajax/remove-category",
          admin,
          List.of(Map.entry("categoryId", categoryIds.get(i))));
    }
  }

  @Test
  void adminRoleOfBranchMakesBranchAdmin() throws Exception {
    String token = Srm.token(UITF_ADMIN);

    assertEquals(403, Srm.getPage(PAGE, token).statusCode());
    assertFalse(Srm.getPage("/readiness", token).body().contains(SETUP_TAB), "has the Setup tab");

    assertSuccess(addCategory(admin, ROOT, BRANCH, UITF_ROLE));
    String branch = categoryId(page(), BRANCH);

    assertEquals(UITF_ROLE, adminRole(page(), branch));
    assertEquals(200, Srm.getPage(PAGE, token).statusCode());
    assertEquals(
        200, Srm.getPage("/reports/inventory-activity/component-audit", token).statusCode());
    assertTrue(Srm.getPage("/readiness", token).body().contains(SETUP_TAB), "no Setup tab");

    // In their own branch only
    assertSuccess(addCategory(token, branch, "IT Branch Sub", null));
    assertNotAuthorized(addCategory(token, VTA, "IT Branch Other", null));
    assertNotAuthorized(addCategory(token, ROOT, "IT Branch Top", null));

    // Removing the role removes the branch admins
    assertSuccess(editCategory(admin, branch, ROOT, BRANCH, ""));

    assertEquals("", adminRole(page(), branch));
    assertEquals(403, Srm.getPage(PAGE, token).statusCode());
    assertEquals(403, addCategory(token, branch, "IT Branch Sub 2", null).statusCode());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/setup/category-tree",
        "/setup/component-list",
        "/reports/inventory-activity/component-audit"
      })
  void branchAdminInTestDataReadsSetupPage(String path) throws Exception {
    assertEquals(200, Srm.getPage(path, Srm.token(VTA_ADMIN)).statusCode());
  }

  @Test
  void onlyAdminChangesAdminRole() throws Exception {
    String token = Srm.token(VTA_ADMIN);

    for (String role : List.of("group3Leaders", "")) {
      HttpResponse<String> response = editCategory(token, VTA, ROOT, "VTA", role);

      assertTrue(
          response.body().contains("Only an admin can change a category&#39;s admin role")
              || response.body().contains("Only an admin can change a category's admin role"),
          response.body());
    }

    assertEquals("vtaadm", adminRole(page(), VTA));

    // Without the role, as from a branch admin's dialog, and with it unchanged
    assertSuccess(editCategory(token, VTA, ROOT, "VTA", null));
    assertSuccess(editCategory(token, VTA, ROOT, "VTA", "vtaadm"));

    assertEquals("vtaadm", adminRole(page(), VTA));
  }

  @Test
  void onlyTopLevelCategoryHasAdminRole() throws Exception {
    HttpResponse<String> response = addCategory(admin, OTHER, BRANCH, UITF_ROLE);

    assertTrue(
        response.body().contains("Only a top-level category can have an admin role"),
        response.body());

    assertSuccess(addCategory(admin, ROOT, BRANCH, UITF_ROLE));
    String branch = categoryId(page(), BRANCH);

    response = editCategory(admin, branch, OTHER, BRANCH, UITF_ROLE);

    assertTrue(
        response.body().contains("Only a top-level category can have an admin role"),
        response.body());
    assertEquals(UITF_ROLE, adminRole(page(), branch));

    // Moved under another category, it is no longer a branch
    assertSuccess(editCategory(admin, branch, OTHER, BRANCH, null));

    assertEquals("", adminRole(page(), branch));
    assertEquals(403, Srm.getPage(PAGE, Srm.token(UITF_ADMIN)).statusCode());
  }

  @Test
  void adminRoleFieldIsForAdminOnly() throws Exception {
    assertTrue(page().contains("id=\"node-admin-role\""), "admin has no Admin Role field");

    HttpResponse<String> response = Srm.getPage(PAGE, Srm.token(VTA_ADMIN));

    assertEquals(200, response.statusCode());
    assertFalse(response.body().contains("id=\"node-admin-role\""), "branch admin has the field");
  }

  /** Adds a category, with an admin role unless it is null. */
  private HttpResponse<String> addCategory(
      String token, String parent, String name, String adminRole) throws Exception {
    List<Map.Entry<String, String>> form =
        new ArrayList<>(
            List.of(
                Map.entry("parentId", parent), Map.entry("name", name), Map.entry("weight", "")));

    if (adminRole != null) {
      form.add(Map.entry("adminRole", adminRole));
    }

    HttpResponse<String> response = Srm.post("/setup/ajax/add-category", token, form);

    if (isSuccess(response)) {
      categoryIds.add(categoryId(page(), name));
    }

    return response;
  }

  /**
   * Edits a category, leaving out the admin role if it is null. The VTA category keeps its weight.
   */
  private static HttpResponse<String> editCategory(
      String token, String id, String parent, String name, String adminRole) throws Exception {
    List<Map.Entry<String, String>> form =
        new ArrayList<>(
            List.of(
                Map.entry("categoryId", id),
                Map.entry("parentId", parent),
                Map.entry("name", name),
                Map.entry("weight", VTA.equals(id) ? VTA_WEIGHT : "")));

    if (adminRole != null) {
      form.add(Map.entry("adminRole", adminRole));
    }

    return Srm.post("/setup/ajax/edit-category", token, form);
  }

  private static String page() throws Exception {
    HttpResponse<String> response = Srm.getPage(PAGE, admin);

    assertEquals(200, response.statusCode(), response.body());
    assertTrue(response.body().stripTrailing().endsWith("</html>"), "page cut short");

    return response.body();
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

  /** The admin role the tree has for a category, which the Edit dialog reads. */
  private static String adminRole(String body, String id) {
    Matcher m =
        Pattern.compile("<li id=\"node-CATEGORY-" + id + "\"[^>]*data-admin-role=\"([^\"]*)\"")
            .matcher(body);

    assertTrue(m.find(), "no category " + id);

    return m.group(1);
  }

  private static boolean isSuccess(HttpResponse<String> response) {
    return response.body().contains("<span class=\"status\">Success</span>");
  }

  private static void assertSuccess(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    assertTrue(isSuccess(response), response.body());
  }

  private static void assertNotAuthorized(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    assertTrue(response.body().contains("Not authorized"), response.body());
  }
}
