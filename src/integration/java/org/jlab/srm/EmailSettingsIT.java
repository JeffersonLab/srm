package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The settings that turn the daily emails on and off, whose edits tell the ScheduledEmailer to
 * start or stop its timer, as Setup › Email shows.
 */
class EmailSettingsIT {

  /** The heading on Setup › Email that says whether the daily emails' timer is running */
  private static final Pattern SCHEDULED =
      Pattern.compile("Scheduled Daily Emails:\\s*(YES|NO)\\s*</h3>");

  private static String admin;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
    admin = Srm.token(Srm.ADMIN);
  }

  @Test
  void editingLeaderEmailsStartsAndStopsTheSchedule() throws Exception {
    assertEquals(
        "NO",
        scheduled(),
        "Both EMAIL_LEADERS_ENABLED and EMAIL_ACTIVITY_ENABLED should be off, as seeded");

    try {
      editSetting("EMAIL_LEADERS_ENABLED", "Y");

      assertEquals("YES", scheduled());
    } finally {
      editSetting("EMAIL_LEADERS_ENABLED", "N");
    }

    assertEquals("NO", scheduled());
  }

  private static String scheduled() throws Exception {
    HttpResponse<String> response = Srm.getPage("/setup/email", admin);

    assertEquals(200, response.statusCode(), response.body());

    Matcher matcher = SCHEDULED.matcher(response.body());

    assertTrue(matcher.find(), response.body());

    return matcher.group(1);
  }

  private static void editSetting(String key, String value) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/setup/ajax/edit-setting",
            admin,
            List.of(Map.entry("key", key), Map.entry("value", value)));

    assertEquals("ok", Srm.json(response).getString("stat"), response.body());
  }
}
