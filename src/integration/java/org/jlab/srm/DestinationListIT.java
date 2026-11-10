package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The Setup > Beam Destinations page, its Facility filter, and setting the targets. */
class DestinationListIT {

  private static final String PATH = "/setup/destination-list";

  /** The targets in the test data (container/oracle/initdb.d): Hall A to Hall D */
  private static final List<String> TARGETS = List.of("6", "7", "8", "9");

  /** A UITF destination in the test data */
  private static final String UITF_DUMP = "50";

  private static String admin;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @Test
  void listsEveryFacilityWithoutFilter() throws Exception {
    String body = page(PATH);

    assertTrue(body.contains("All Facilities"), body);
    assertTrue(body.contains("Hall A"), body);
    assertTrue(body.contains("LERF 1X Dump"), body);
    assertTrue(body.contains("UITF KeV Beam Dump (IDLK501)"), body);
  }

  @Test
  void listsOnlyTheFilteredFacility() throws Exception {
    String body = page(PATH + "?facility=UITF");

    assertTrue(body.contains("Facility &quot;UITF&quot;"), body);
    assertTrue(body.contains("{5}"), body);
    assertTrue(body.contains("UITF KeV Beam Dump (IDLK501)"), body);
    assertFalse(body.contains("Hall A"), body);
    assertFalse(body.contains("LERF 1X Dump"), body);
    assertTrue(body.contains("<option value=\"UITF\" selected=\"selected\">"), body);
  }

  @Test
  void listsNothingForAFacilityWithoutDestinations() throws Exception {
    String body = page(PATH + "?facility=VTA");

    assertTrue(body.contains("{0}"), body);
    assertFalse(body.contains("Hall A"), body);
  }

  @ParameterizedTest
  @ValueSource(strings = {"cebaf", "NOPE"})
  void rejectsUnknownFacility(String facility) throws Exception {
    HttpResponse<String> response = Srm.get(PATH + "?facility=" + facility, admin);

    assertEquals(400, response.statusCode(), response.body());
  }

  @Test
  void settingTargetsInOneFacilityKeepsTheOthers() throws Exception {
    try {
      // Before the filter, the form's targets replaced every destination's, so posting the UITF
      // list would have cleared the halls
      setTargets("UITF", List.of(UITF_DUMP));

      String all = page(PATH);

      for (String target : TARGETS) {
        assertTrue(isTarget(all, target), "lost target " + target);
      }
      assertTrue(isTarget(all, UITF_DUMP), "UITF target not set");

      HttpResponse<String> redirect = setTargets("UITF", List.of());

      assertEquals(302, redirect.statusCode(), redirect.body());
      assertTrue(
          redirect.headers().firstValue("Location").orElseThrow().endsWith(PATH + "?facility=UITF"),
          "not sent back to the filtered list: " + redirect.headers().map());

      all = page(PATH);

      for (String target : TARGETS) {
        assertTrue(isTarget(all, target), "lost target " + target);
      }
      assertFalse(isTarget(all, UITF_DUMP), "UITF target not cleared");
    } finally {
      setTargets(null, TARGETS);
    }
  }

  @Test
  void settingTargetsIgnoresDestinationsOutsideTheFacility() throws Exception {
    try {
      setTargets("CEBAF", List.of("7", UITF_DUMP));

      String all = page(PATH);

      assertFalse(isTarget(all, "6"), "CEBAF target 6 kept");
      assertTrue(isTarget(all, "7"), "CEBAF target 7 not set");
      assertFalse(isTarget(all, UITF_DUMP), "UITF target set from the CEBAF list");
    } finally {
      setTargets(null, TARGETS);
    }
  }

  private static String page(String path) throws Exception {
    HttpResponse<String> response = Srm.getPage(path, admin);

    assertEquals(200, response.statusCode(), path + ": " + response.body());
    assertTrue(response.body().stripTrailing().endsWith("</html>"), path + " cut short");

    return response.body();
  }

  private static HttpResponse<String> setTargets(String facility, List<String> targets)
      throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();

    form.add(Map.entry("facility", facility == null ? "" : facility));

    for (String target : targets) {
      form.add(Map.entry("target", target));
    }

    return Srm.post(PATH, admin, form);
  }

  private static boolean isTarget(String body, String destinationId) {
    return body.contains("value=\"" + destinationId + "\" checked=\"checked\"");
  }
}
