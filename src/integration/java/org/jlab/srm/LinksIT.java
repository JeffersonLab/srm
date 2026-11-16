package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Links tab, whose links admins edit in the LINKS_GENERAL_CSV and LINKS_SKETCH_CSV settings.
 */
class LinksIT {

  private static final String KEY = "LINKS_GENERAL_CSV";

  /** The value cell of the only row of the Settings page filtered to one key */
  private static final Pattern VALUE_CELL =
      Pattern.compile("<td>([^<]*)</td>\\s*</tr>\\s*</tbody>");

  private static String admin;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @Test
  void showsTheLinksInTheSettings() throws Exception {
    String body = linksPage();

    assertTrue(body.contains("<h3>General</h3>"), body);
    assertTrue(body.contains("<a href=\"https://logbooks.jlab.org/\">Logbooks</a>"), body);
    assertTrue(body.contains(">Hall A &amp; C Responsibilities</a>"), body);
    assertTrue(body.contains("<h3>Beamline Sketches</h3>"), body);
    assertTrue(body.contains("/zones/LINAC1/sketch\">North LINAC</a>"), body);
    assertFalse(body.contains("{CED_SERVER_URL}"), body);
  }

  @Test
  void showsAnEditedSettingAtOnce() throws Exception {
    String original = settingValue();

    try {
      editSetting(
          "https://example.org/a?x=1&y=2|<b>First</b>,javascript:alert(1)|Script,/srm/help|Help");

      String body = linksPage();

      assertTrue(
          body.contains(
              "<a href=\"https://example.org/a?x=1&amp;y=2\">&lt;b&gt;First&lt;/b&gt;</a>"),
          body);
      assertTrue(body.contains("<a href=\"/srm/help\">Help</a>"), body);
      assertFalse(body.contains("javascript:alert"), body);
      assertFalse(body.contains(">Logbooks</a>"), body);
      assertTrue(body.contains("<h3>Beamline Sketches</h3>"), body);
    } finally {
      editSetting(original);
    }

    assertEquals(original, settingValue());
  }

  private static String linksPage() throws Exception {
    HttpResponse<String> response = Srm.getPage("/links", null);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static String settingValue() throws Exception {
    HttpResponse<String> response = Srm.getPage("/setup/settings?key=" + KEY, admin);

    assertEquals(200, response.statusCode(), response.body());

    Matcher matcher = VALUE_CELL.matcher(response.body());

    assertTrue(matcher.find(), response.body());

    return matcher
        .group(1)
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&#039;", "'")
        .replace("&#034;", "\"")
        .replace("&amp;", "&");
  }

  private static void editSetting(String value) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/edit-setting",
            admin,
            List.of(Map.entry("key", KEY), Map.entry("value", value)));

    assertEquals("ok", Srm.json(response).getString("stat"), response.body());
  }
}
