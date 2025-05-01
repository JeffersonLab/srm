package org.jlab.srm.persistence.enumeration;

/**
 * @author ryans
 */
public enum DataSource {
  CED("ace.jlab.org/ced"),
  LED("led.acc.jlab.org"),
  UED("ued.acc.jlab.org"),
  INTERNAL("ace.jlab.org");
  private final String hostname;

  DataSource(String hostname) {
    this.hostname = hostname;
  }

  public String getHostname() {
    return hostname;
  }
}
