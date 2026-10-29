package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Users request masks; admins accept or deny them. Each test adds its own component, and removes it
 * with its requests afterwards.
 */
class MaskingRequestIT {

  /** RF in the Injector (container/oracle/initdb.d/05_test_data.sql) */
  private static final String SYSTEM_ID = "141";

  private static final String REGION_ID = "1";

  /** The format of the app's date pickers */
  private static final DateTimeFormatter FRIENDLY =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.US);

  private static final String SUCCESS =
      "<response><span class=\"status\">Success</span></response>";

  private String name;
  private String componentId;

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @BeforeEach
  void addComponent() throws Exception {
    name = "it-" + UUID.randomUUID().toString().substring(0, 8);

    JsonObject added =
        Srm.json(
            Srm.post(
                "/setup/ajax/add-component",
                Srm.token(Srm.ADMIN),
                List.of(
                    Map.entry("name", name),
                    Map.entry("system-id", SYSTEM_ID),
                    Map.entry("region-id", REGION_ID),
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
                  Srm.token(Srm.ADMIN),
                  List.of(Map.entry("component-id", componentId))));

      assertEquals("ok", deleted.getString("stat"), deleted.toString());
    }
  }

  @Test
  void anonymousCannotRequest() throws Exception {
    assertEquals(error("Not authorized"), request(null, "anonymous " + name, inAWeek()));
  }

  @Test
  void requestNeedsFutureExpiration() throws Exception {
    assertEquals(
        error("Expiration must be a future date"),
        request(Srm.token(Srm.USER), "past " + name, LocalDateTime.now().minusDays(1)));
  }

  @Test
  void requestEmailsMaskRequestList() throws Exception {
    String reason = "emailed " + name;

    assertEquals(SUCCESS, request(Srm.token(Srm.USER), reason, inAWeek()));

    JsonArray emails = Srm.emailsContaining(name).getJsonArray("items");

    assertEquals(1, emails.size(), emails.toString());

    JsonObject headers = emails.getJsonObject(0).getJsonObject("Content").getJsonObject("Headers");

    assertEquals("HCO Mask Request: " + name, headers.getJsonArray("Subject").getString(0));
    assertEquals("tester@example.com", headers.getJsonArray("To").getString(0));
    assertEquals(Srm.USER + "@jlab.org", headers.getJsonArray("From").getString(0));
  }

  @Test
  void requestNeedsComponent() throws Exception {
    assertEquals(
        error("No component selected"),
        requestComponents(Srm.token(Srm.USER), "none " + name, inAWeek(), List.of()));
  }

  @Test
  void requestNeedsNumericComponentId() throws Exception {
    assertEquals(
        error("Component ID must be a number"),
        requestComponents(Srm.token(Srm.USER), "bad id " + name, inAWeek(), List.of("abc")));
  }

  @Test
  void onlyOnePendingRequest() throws Exception {
    String token = Srm.token(Srm.USER);

    assertEquals(SUCCESS, request(token, "first " + name, inAWeek()));
    assertEquals(
        error("Component mask request already pending: " + name),
        request(token, "second " + name, inAWeek()));
  }

  @Test
  void userCannotAcceptOrDeny() throws Exception {
    String token = Srm.token(Srm.USER);
    String reason = "refused " + name;

    assertEquals(SUCCESS, request(token, reason, inAWeek()));

    String requestId = requestId(reason, "PENDING");

    assertNotAuthorized(
        Srm.post(
            "/ajax/accept-mask-request",
            token,
            List.of(Map.entry("requestId", requestId), Map.entry("reason", reason))));
    assertNotAuthorized(
        Srm.post("/ajax/deny-mask-request", token, List.of(Map.entry("requestId", requestId))));

    // The refused accept rolled back, so the request is still pending
    assertEquals(requestId, requestId(reason, "PENDING"));
  }

  @Test
  void adminDenies() throws Exception {
    String reason = "denied " + name;

    assertEquals(SUCCESS, request(Srm.token(Srm.USER), reason, inAWeek()));

    String requestId = requestId(reason, "PENDING");

    assertOk(
        Srm.post(
            "/ajax/deny-mask-request",
            Srm.token(Srm.ADMIN),
            List.of(Map.entry("requestId", requestId))));

    assertEquals(requestId, requestId(reason, "REJECTED"));

    // A denied request is no longer pending, so the component can be requested again
    assertEquals(SUCCESS, request(Srm.token(Srm.USER), "again " + name, inAWeek()));
  }

  @Test
  void adminAcceptsAndMasks() throws Exception {
    String reason = "accepted " + name;

    assertEquals(SUCCESS, request(Srm.token(Srm.USER), reason, inAWeek()));

    String requestId = requestId(reason, "PENDING");

    assertOk(
        Srm.post(
            "/ajax/accept-mask-request",
            Srm.token(Srm.ADMIN),
            List.of(
                Map.entry("requestId", requestId),
                Map.entry("reason", reason),
                Map.entry("expiration", FRIENDLY.format(inAWeek())))));

    assertEquals(requestId, requestId(reason, "ACCEPTED"));
    assertEquals(
        error("Component is already masked: " + name),
        request(Srm.token(Srm.USER), "masked " + name, inAWeek()));
  }

  /** Requests a mask of the component; the reply is XML, not JSON. */
  private String request(String token, String reason, LocalDateTime expiration) throws Exception {
    return requestComponents(token, reason, expiration, List.of(componentId));
  }

  /** Requests a mask of the components with the IDs, none to send no componentId[] parameter. */
  private static String requestComponents(
      String token, String reason, LocalDateTime expiration, List<String> componentIds)
      throws Exception {
    List<Map.Entry<String, String>> params = new ArrayList<>();

    params.add(Map.entry("reason", reason));
    params.add(Map.entry("expiration-date", FRIENDLY.format(expiration)));

    for (String id : componentIds) {
      params.add(Map.entry("componentId[]", id));
    }

    HttpResponse<String> response = Srm.post("/ajax/save-masking-request", token, params);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  /** Finds the one request with the reason and status on the requests page. */
  private static String requestId(String reason, String status) throws Exception {
    HttpResponse<String> page =
        Srm.get(
            "/masks/requests?reason="
                + URLEncoder.encode(reason, StandardCharsets.UTF_8)
                + "&status="
                + status
                + "&qualified=",
            null);

    assertEquals(200, page.statusCode(), page.body());

    List<String> ids = new ArrayList<>();
    Matcher m = Pattern.compile("data-request-id=\"(\\d+)\"").matcher(page.body());

    while (m.find()) {
      ids.add(m.group(1));
    }

    assertEquals(1, ids.size(), status + " requests with reason " + reason + ": " + ids);

    return ids.get(0);
  }

  private static LocalDateTime inAWeek() {
    return LocalDateTime.now().plusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
  }

  private static String error(String reason) {
    return "<response><span class=\"status\">Error</span><span class=\"reason\">"
        + reason
        + "</span></response>";
  }

  private static void assertOk(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    assertEquals("ok", Srm.json(response).getString("stat"), response.body());
  }

  private static void assertNotAuthorized(HttpResponse<String> response) {
    assertEquals(400, response.statusCode(), response.body());

    JsonObject result = Srm.json(response);

    assertEquals("fail", result.getString("stat"), response.body());
    assertTrue(result.getString("error").contains("Not authorized"), response.body());
  }
}
