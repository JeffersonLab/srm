package org.jlab.hco.persistence.enumeration;

/**
 * @author ryans
 */
public enum DataSource {
    CED("ced.acc.jlab.org"), LED("led.acc.jlab.org"), UED("ued.acc.jlab.org"), INTERNAL("accweb.acc.jlab.org");
    private String hostname;

    DataSource(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

}
