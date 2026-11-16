package org.jlab.srm.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LinkTest {

  private static List<String> urlsAndLabels(List<Link> links) {
    return links.stream().map(link -> link.getUrl() + " " + link.getLabel()).toList();
  }

  @Test
  void readsUrlAndLabelInOrder() {
    List<Link> links =
        Link.parse(
            List.of("https://logbooks.jlab.org/|Logbooks", " https://tasklists.jlab.org/ | Tasks "),
            null);

    assertEquals(
        List.of("https://logbooks.jlab.org/ Logbooks", "https://tasklists.jlab.org/ Tasks"),
        urlsAndLabels(links));
  }

  @Test
  void labelMayContainPipe() {
    assertEquals(
        List.of("https://example.org/ A | B"),
        urlsAndLabels(Link.parse(List.of("https://example.org/|A | B"), null)));
  }

  @Test
  void labelsWithUrlWhenMissing() {
    assertEquals(
        List.of("https://example.org/ https://example.org/", "/srm /srm"),
        urlsAndLabels(Link.parse(List.of("https://example.org/", "/srm|"), null)));
  }

  @Test
  void replacesCedPlaceholder() {
    List<String> items = List.of("{CED_SERVER_URL}/zones/LINAC1/sketch|North LINAC");

    assertEquals(
        List.of("https://ced.example.org/zones/LINAC1/sketch North LINAC"),
        urlsAndLabels(Link.parse(items, "https://ced.example.org")));
    assertEquals(
        List.of("/zones/LINAC1/sketch North LINAC"), urlsAndLabels(Link.parse(items, null)));
  }

  @Test
  void keepsOtherPlaceholdersAsWritten() {
    assertEquals(
        List.of("{LOGBOOK_SERVER_URL}/entries Logbook"),
        urlsAndLabels(Link.parse(List.of("{LOGBOOK_SERVER_URL}/entries|Logbook"), "https://ced")));
  }

  @Test
  void leavesOutBlankItemsAndOtherSchemes() {
    List<Link> links =
        Link.parse(
            List.of(
                "",
                "  ",
                "|No URL",
                "javascript:alert(1)|Script",
                " JavaScript:alert(1)|Script",
                "data:text/html,x|Data",
                "HTTP://example.org/|Upper",
                "zones/a:b|Relative"),
            null);

    assertEquals(List.of("HTTP://example.org/ Upper", "zones/a:b Relative"), urlsAndLabels(links));
  }

  @Test
  void missingSettingHasNoLinks() {
    assertTrue(Link.parse(null, null).isEmpty());
  }
}
