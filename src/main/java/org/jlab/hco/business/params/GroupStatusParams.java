package org.jlab.hco.business.params;

import java.math.BigInteger;

public class GroupStatusParams {
    private BigInteger[] destinationIdArray;
    private BigInteger categoryId;
    private BigInteger systemId;
    private BigInteger regionId;
    private String chart;

    public BigInteger[] getDestinationIdArray() {
        return destinationIdArray;
    }

    public void setDestinationIdArray(BigInteger[] destinationIdArray) {
        this.destinationIdArray = destinationIdArray;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public BigInteger getSystemId() {
        return systemId;
    }

    public void setSystemId(BigInteger systemId) {
        this.systemId = systemId;
    }

    public BigInteger getRegionId() {
        return regionId;
    }

    public void setRegionId(BigInteger regionId) {
        this.regionId = regionId;
    }

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }
}
