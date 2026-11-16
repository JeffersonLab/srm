package org.jlab.srm.presentation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A link on the Links tab, read from a CSV setting such as LINKS_GENERAL_CSV.
 *
 * <p>Each item of the setting is a URL and a label separated with the pipe symbol, as in
 * HELP_DOC_CSV. A URL may start with {@value #CED_PLACEHOLDER}, which is replaced with the
 * CED_SERVER_URL environment variable. Only that variable may be named, so that a setting cannot
 * show the value of any other.
 */
public final class Link {

  /** Replaced, at the start of a URL, with the CED_SERVER_URL environment variable. */
  public static final String CED_PLACEHOLDER = "{CED_SERVER_URL}";

  private final String url;
  private final String label;

  public Link(String url, String label) {
    this.url = url;
    this.label = label;
  }

  public String getUrl() {
    return url;
  }

  public String getLabel() {
    return label;
  }

  /**
   * Reads the links from a CSV setting's items, in order.
   *
   * <p>An item without a label is labeled with its URL. Blank items, items without a URL, and items
   * whose URL has a scheme other than http or https, such as javascript:, are left out.
   *
   * @param items the setting's items, or null if it is missing
   * @param cedServerUrl the CED_SERVER_URL environment variable, or null if it is not set
   * @return the links
   */
  public static List<Link> parse(List<String> items, String cedServerUrl) {
    List<Link> links = new ArrayList<>();

    if (items == null) {
      return links;
    }

    for (String item : items) {
      String[] parts = item.split("\\|", 2);
      String url = parts[0].trim();
      String label = parts.length > 1 ? parts[1].trim() : "";

      if (url.startsWith(CED_PLACEHOLDER)) {
        url = (cedServerUrl == null ? "" : cedServerUrl) + url.substring(CED_PLACEHOLDER.length());
      }

      if (url.isEmpty() || !isAllowedScheme(url)) {
        continue;
      }

      links.add(new Link(url, label.isEmpty() ? url : label));
    }

    return links;
  }

  /** Whether the URL is relative or uses http or https. */
  private static boolean isAllowedScheme(String url) {
    int colon = url.indexOf(':');
    int slash = url.indexOf('/');

    if (colon < 0 || (slash >= 0 && slash < colon)) {
      return true;
    }

    String scheme = url.substring(0, colon).toLowerCase(Locale.ROOT);

    return scheme.equals("http") || scheme.equals("https");
  }
}
