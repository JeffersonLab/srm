package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.net.http.HttpResponse;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The readiness page's include and exclude beam destination filters, on the test data: destination
 * 13 (1R Dumplette) has 28 of the components, among them the only component of system 44, and 27 of
 * system 141's 52.
 */
class ReadinessIT {

  private static final String DESTINATION = "13";
  private static final Pattern COUNT = Pattern.compile("class=\"message-box\">[^{]*\\{([\\d,]+)}");

  @BeforeAll
  static void awaitReady() throws Exception {
    Srm.awaitReady();
  }

  @Test
  void excludedDestinationSubtractsFromComponentCount() throws Exception {
    long all = componentCount("");
    long included = componentCount("&destinationId=" + DESTINATION);
    long excluded = componentCount("&excludeDestinationId=" + DESTINATION);

    assertTrue(included > 0);
    assertTrue(excluded > 0);
    assertEquals(all, included + excluded);
    assertEquals(
        0,
        componentCount("&destinationId=" + DESTINATION + "&excludeDestinationId=" + DESTINATION));
  }

  @Test
  void excludedDestinationIsInSelectionMessage() throws Exception {
    String body = page("&excludeDestinationId=" + DESTINATION);

    assertTrue(body.contains("All Components excluding Beam Destination &#034;1R Dumplette"), body);
    assertTrue(
        Pattern.compile(
                "<option value=\"" + DESTINATION + "\" selected=\"selected\">\\s*1R Dumplette")
            .matcher(body)
            .find(),
        "exclude select does not keep the selection");
  }

  @Test
  void excludedDestinationPrunesTree() throws Exception {
    Set<String> included = systemNodes("&destinationId=" + DESTINATION);
    Set<String> excluded = systemNodes("&excludeDestinationId=" + DESTINATION);

    assertTrue(included.contains("node-SYSTEM-44"), included.toString());
    assertFalse(excluded.contains("node-SYSTEM-44"), excluded.toString());
    assertTrue(included.contains("node-SYSTEM-141"), included.toString());
    assertTrue(excluded.contains("node-SYSTEM-141"), excluded.toString());

    int all = children("node-SYSTEM-141", "").size();
    int includedComponents = children("node-SYSTEM-141", "&destinationId=" + DESTINATION).size();
    int excludedComponents =
        children("node-SYSTEM-141", "&excludeDestinationId=" + DESTINATION).size();

    assertTrue(includedComponents > 0);
    assertTrue(excludedComponents > 0);
    assertEquals(all, includedComponents + excludedComponents);
  }

  private static String page(String filters) throws Exception {
    HttpResponse<String> response = Srm.getPage("/readiness?qualified=" + filters, null);

    assertEquals(200, response.statusCode(), response.body());

    return response.body();
  }

  private static long componentCount(String filters) throws Exception {
    String body = page(filters);
    Matcher matcher = COUNT.matcher(body);

    assertTrue(matcher.find(), "no component count in: " + body);

    return Long.parseLong(matcher.group(1).replace(",", ""));
  }

  /** The tree's nodes under a node (or the root, for null), as /get-children gives them. */
  private static JsonArray children(String nodeId, String filters) throws Exception {
    String path = "/get-children?" + (nodeId == null ? "" : "id=" + nodeId) + filters;
    HttpResponse<String> response = Srm.get(path, null);

    assertEquals(200, response.statusCode(), response.body());

    try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
      return reader.readArray();
    }
  }

  /** The system nodes of the whole tree, expanding every category. */
  private static Set<String> systemNodes(String filters) throws Exception {
    Set<String> systems = new HashSet<>();
    Deque<String> categories = new ArrayDeque<>();

    for (JsonObject root : children(null, filters).getValuesAs(JsonObject.class)) {
      categories.push(root.getString("id"));
    }

    while (!categories.isEmpty()) {
      for (JsonObject node : children(categories.pop(), filters).getValuesAs(JsonObject.class)) {
        if ("SYSTEM".equals(node.getString("type"))) {
          systems.add(node.getString("id"));
        } else if (node.getBoolean("children")) {
          categories.push(node.getString("id"));
        }
      }
    }

    return systems;
  }
}
