package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The readiness page's Recent Activity feed and its "Show more" button, which appends the entries
 * /ajax/recent-activity gives. Each test signs off its own component in system 141 for group 4
 * (Alignment) seven times, each with its own comment, so each is its own entry, newest first. No
 * request here marks the signoff as needing attention, which would post to the real JLab logbook.
 */
class RecentActivityIT {

  private static final String FILTERS = "&systemId=141&groupId=4";
  private static final String CHECKED = "50";
  private static final int SIGNOFFS = 7;

  /** Entries the page starts with, and entries a request for more gives */
  private static final int PAGE_MAX = 5;

  private static final int MORE_MAX = 10;

  private String componentId;

  /** The signoffs' comments, newest first */
  private final List<String> comments = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @BeforeEach
  void signOffComponent() throws Exception {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    String token = Srm.token(Srm.ADMIN);
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

    for (int i = 1; i <= SIGNOFFS; i++) {
      String comment = name + " signoff " + i;
      HttpResponse<String> response =
          Srm.post(
              "/ajax/batch-signoff",
              token,
              List.of(
                  Map.entry("componentId[]", componentId),
                  Map.entry("groupId[]", "4"),
                  Map.entry("statusId", CHECKED),
                  Map.entry("comment", comment),
                  Map.entry("needsAttention", "false")));

      assertEquals(200, response.statusCode(), response.body());
      assertEquals("Success", Jsoup.parse(response.body()).select(".status").text());

      comments.add(0, comment);
    }
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
  void pageShowsNewestEntriesAndOffersMore() throws Exception {
    HttpResponse<String> response = Srm.getPage("/readiness?qualified=" + FILTERS, null);

    assertEquals(200, response.statusCode(), response.body());

    Document page = Jsoup.parse(response.body());

    assertEquals(comments.subList(0, PAGE_MAX), comments(page));
    assertEquals("true", page.select("#activity-table tbody").attr("data-more"));
    assertEquals(1, page.select("#show-more-button").size());
    assertEquals(1, page.select("#more-link").size());
  }

  @Test
  void pageWithoutMoreEntriesHasNoButton() throws Exception {
    // Region 2 has none of this test's activity, and less than a page of anyone's
    HttpResponse<String> response = Srm.getPage("/readiness?qualified=&regionId=2" + FILTERS, null);

    assertEquals(200, response.statusCode(), response.body());

    Document page = Jsoup.parse(response.body());
    int entries = comments(page).size();

    assertTrue(entries <= PAGE_MAX, "entries: " + entries);
    assertEquals(0, page.select("#show-more-button").size());
  }

  @Test
  void moreContinuesAfterThePage() throws Exception {
    Document more = more(FILTERS + "&offset=" + PAGE_MAX);
    List<String> next = comments(more);

    assertTrue(next.size() <= MORE_MAX, "entries: " + next.size());
    assertEquals(comments.subList(PAGE_MAX, SIGNOFFS), next.subList(0, SIGNOFFS - PAGE_MAX));
  }

  @Test
  void moreEndsWithTheLastEntry() throws Exception {
    List<String> all = new ArrayList<>();
    boolean hasMore = true;

    for (int requests = 0; hasMore; requests++) {
      assertTrue(requests < 1000, "no end after " + all.size() + " entries");

      Document more = more(FILTERS + "&offset=" + all.size());
      List<String> entries = comments(more);

      hasMore = "true".equals(more.select("tbody").attr("data-more"));

      if (hasMore) {
        assertEquals(MORE_MAX, entries.size());
      } else {
        assertTrue(entries.size() <= MORE_MAX, "entries: " + entries.size());
      }

      all.addAll(entries);
    }

    assertEquals(comments, all.subList(0, SIGNOFFS));

    Document past = more(FILTERS + "&offset=" + all.size());

    assertEquals(List.of(), comments(past));
    assertEquals("false", past.select("tbody").attr("data-more"));
  }

  @Test
  void moreUsesThePagesFilters() throws Exception {
    List<String> otherRegion = comments(more("&regionId=2" + FILTERS));

    for (String comment : comments) {
      assertFalse(otherRegion.contains(comment), otherRegion.toString());
    }
  }

  @Test
  void badOffsetIsRejected() throws Exception {
    assertEquals(400, Srm.get("/ajax/recent-activity?offset=-1", null).statusCode());
    assertEquals(400, Srm.get("/ajax/recent-activity?offset=x", null).statusCode());
  }

  /** The rows /ajax/recent-activity gives, in a table so the parser keeps them. */
  private static Document more(String parameters) throws Exception {
    HttpResponse<String> response = Srm.get("/ajax/recent-activity?qualified=" + parameters, null);

    assertEquals(200, response.statusCode(), response.body());

    return Jsoup.parse("<table>" + response.body() + "</table>");
  }

  /** The entries' comments, in order. */
  private static List<String> comments(Document document) {
    return document.select("tr.middle-row td").stream().map(Element::text).toList();
  }
}
