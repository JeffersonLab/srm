package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The overall status and group status reports' include and exclude beam destination filters, on the
 * test data: destination 13 (1R Dumplette) has 28 of the components, among them the only component
 * of system 44.
 */
class StatusReportsIT {

  private static final String DESTINATION = "13";
  private static final String SYSTEM = "44";

  /** A status's count in the overall status chart legend. */
  private static final Pattern STATUS_COUNT =
      Pattern.compile("legend-label\">([^<]+)</td>\\s*<td>\\s*([\\d,]+)");

  /** A group's signoff total in the group status table: the link without a statusId. */
  private static final Pattern GROUP_TOTAL =
      Pattern.compile("groupId=(\\d+)&amp;masked=N&amp;qualified=\">([\\d,]+)</a>");

  private static final Pattern EXCLUDE_SELECTED =
      Pattern.compile(
          "name=\"excludeDestinationId\"[\\s\\S]*?<option value=\""
              + DESTINATION
              + "\" selected=\"selected\">\\s*1R Dumplette");

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @Test
  void excludedDestinationSubtractsFromOverallStatus() throws Exception {
    Map<String, Long> all = statusCounts("");
    Map<String, Long> included = statusCounts("&destinationId=" + DESTINATION);
    Map<String, Long> excluded = statusCounts("&excludeDestinationId=" + DESTINATION);

    assertEquals(3, all.size(), all.toString());
    assertTrue(total(included) > 0, included.toString());
    assertTrue(total(excluded) > 0, excluded.toString());

    for (String status : all.keySet()) {
      assertEquals(all.get(status), included.get(status) + excluded.get(status), status);
    }

    assertEquals(
        0,
        total(
            statusCounts(
                "&destinationId=" + DESTINATION + "&excludeDestinationId=" + DESTINATION)));
  }

  @Test
  void excludedDestinationEmptiesItsOnlySystemOnOverallStatus() throws Exception {
    assertTrue(total(statusCounts("&systemId=" + SYSTEM)) > 0);
    assertEquals(
        0, total(statusCounts("&systemId=" + SYSTEM + "&excludeDestinationId=" + DESTINATION)));
  }

  @Test
  void excludedDestinationIsInOverallStatusFootnoteAndSelect() throws Exception {
    String body = page("overall-status", "&excludeDestinationId=" + DESTINATION);

    assertTrue(body.contains("Excluding Beam Destination: "), body);
    assertTrue(EXCLUDE_SELECTED.matcher(body).find(), "exclude select does not keep the selection");
    assertFalse(page("overall-status", "").contains("Excluding Beam Destination"));
  }

  @Test
  void excludedDestinationSubtractsFromGroupStatus() throws Exception {
    Map<String, Long> all = groupTotals("");
    Map<String, Long> included = groupTotals("&destinationId=" + DESTINATION);
    Map<String, Long> excluded = groupTotals("&excludeDestinationId=" + DESTINATION);

    assertFalse(all.isEmpty());
    assertFalse(included.isEmpty());
    assertFalse(excluded.isEmpty());

    for (String group : all.keySet()) {
      assertEquals(
          all.get(group),
          included.getOrDefault(group, 0L) + excluded.getOrDefault(group, 0L),
          "group " + group);
    }

    assertTrue(
        groupTotals("&destinationId=" + DESTINATION + "&excludeDestinationId=" + DESTINATION)
            .isEmpty());
  }

  @Test
  void excludedDestinationEmptiesItsOnlySystemOnGroupStatus() throws Exception {
    assertFalse(groupTotals("&systemId=" + SYSTEM).isEmpty());
    assertTrue(
        groupTotals("&systemId=" + SYSTEM + "&excludeDestinationId=" + DESTINATION).isEmpty());
  }

  @Test
  void excludedDestinationIsInGroupStatusMessageFootnoteAndSelect() throws Exception {
    String table = page("group-status", "&chart=table&excludeDestinationId=" + DESTINATION);
    String bar = page("group-status", "&chart=bar&excludeDestinationId=" + DESTINATION);

    assertTrue(table.contains("excluding Beam Destination &#034;1R Dumplette"), table);
    assertTrue(
        EXCLUDE_SELECTED.matcher(table).find(), "exclude select does not keep the selection");
    assertTrue(bar.contains("Excluding Beam Destination: "), bar);
    assertFalse(page("group-status", "&chart=table").contains("excluding Beam Destination"));
  }

  private static String page(String report, String filters) throws Exception {
    HttpResponse<String> response =
        Srm.getPage("/reports/" + report + "?qualified=" + filters, null);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static Map<String, Long> statusCounts(String filters) throws Exception {
    return counts(STATUS_COUNT, page("overall-status", filters));
  }

  private static Map<String, Long> groupTotals(String filters) throws Exception {
    return counts(GROUP_TOTAL, page("group-status", "&chart=table" + filters));
  }

  private static Map<String, Long> counts(Pattern pattern, String body) {
    Map<String, Long> counts = new LinkedHashMap<>();
    Matcher matcher = pattern.matcher(body);

    while (matcher.find()) {
      counts.put(matcher.group(1), Long.parseLong(matcher.group(2).replace(",", "")));
    }

    return counts;
  }

  private static long total(Map<String, Long> counts) {
    return counts.values().stream().mapToLong(Long::longValue).sum();
  }
}
