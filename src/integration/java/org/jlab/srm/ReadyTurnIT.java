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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Whose turn it is: the Signoff report's Ready Turn filter. A group's Not Ready or Checked signoff
 * is in its turn when the groups before it are Ready or Not Applicable. Each test adds a component
 * to system 141, whose groups sign off in the order Alignment (4), Cryo (12), group 13, group 15,
 * group 14, and signs off its first groups as the admin (Ready only after Checked). No request here
 * marks a signoff as needing attention, which would post to the real JLab logbook.
 */
class ReadyTurnIT {

  private static final String NOT_APPLICABLE = "0";
  private static final String READY = "1";
  private static final String CHECKED = "50";

  private static final String ALIGNMENT = "4";
  private static final String CRYO = "12";
  private static final String THIRD = "13";
  private static final String FOURTH = "15";

  /** The record total in the selection message: {total} or {first - last of total}. */
  private static final Pattern TOTAL = Pattern.compile("\\{(?:[\\d,]+ - [\\d,]+ of )?([\\d,]+)\\}");

  private String token;
  private String name;
  private String componentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @BeforeEach
  void addComponent() throws Exception {
    token = Srm.token(Srm.ADMIN);
    name = "it-" + UUID.randomUUID().toString().substring(0, 8);

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
  void firstGroupsTurnWhenNoneSignedOff() throws Exception {
    assertReadyTurn(ALIGNMENT);
    assertNotReadyTurn(CRYO);
  }

  @Test
  void nextGroupsTurnWhenOnlyEarlierGroupIsNotApplicable() throws Exception {
    signOff(ALIGNMENT, NOT_APPLICABLE);

    assertReadyTurn(CRYO);
    assertNotReadyTurn(THIRD);
  }

  @Test
  void nextGroupsTurnWhenEveryEarlierGroupIsNotApplicable() throws Exception {
    signOff(ALIGNMENT, NOT_APPLICABLE);
    signOff(CRYO, NOT_APPLICABLE);

    assertReadyTurn(THIRD);
    assertNotReadyTurn(FOURTH);
  }

  @Test
  void nextGroupsTurnWhenEarlierGroupsAreReadyOrNotApplicable() throws Exception {
    signOff(ALIGNMENT, CHECKED);
    signOff(ALIGNMENT, READY);
    signOff(CRYO, NOT_APPLICABLE);

    assertReadyTurn(THIRD);
    assertNotReadyTurn(FOURTH);
  }

  private void assertReadyTurn(String groupId) throws Exception {
    assertEquals(1, readyTurnCount(groupId), "group " + groupId + "'s turn");
  }

  private void assertNotReadyTurn(String groupId) throws Exception {
    assertEquals(0, readyTurnCount(groupId), "not group " + groupId + "'s turn");
  }

  private void signOff(String groupId, String statusId) throws Exception {
    HttpResponse<String> response =
        Srm.post(
            "/ajax/batch-signoff",
            token,
            List.of(
                Map.entry("componentId[]", componentId),
                Map.entry("groupId[]", groupId),
                Map.entry("statusId", statusId),
                Map.entry("comment", "ready turn"),
                Map.entry("needsAttention", "false")));

    assertEquals(200, response.statusCode(), response.body());
    assertEquals("Success", Jsoup.parse(response.body()).select(".status").text(), response.body());
  }

  /** How many of the component's signoffs for the group the Signoff report lists as Ready Turn. */
  private long readyTurnCount(String groupId) throws Exception {
    HttpResponse<String> response =
        Srm.getPage(
            "/reports/signoff?qualified=&readyTurn=Y&componentName=" + name + "&groupId=" + groupId,
            null);

    assertEquals(200, response.statusCode(), response.body());

    String message = Jsoup.parse(response.body()).select(".message-box").text();
    Matcher matcher = TOTAL.matcher(message);

    assertTrue(matcher.find(), message);

    return Long.parseLong(matcher.group(1).replace(",", ""));
  }
}
