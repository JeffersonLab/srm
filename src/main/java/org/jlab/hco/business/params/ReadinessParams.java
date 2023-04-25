package org.jlab.hco.business.params;

import java.math.BigInteger;

public class ReadinessParams {
    private BigInteger[] destinationIdArray;
    private BigInteger categoryId;
    private BigInteger systemId;
    private BigInteger regionId;
    private BigInteger groupId;
    private BigInteger[] statusIdArray;

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

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public BigInteger[] getStatusIdArray() {
        return statusIdArray;
    }

    public void setStatusIdArray(BigInteger[] statusIdArray) {
        this.statusIdArray = statusIdArray;
    }
}
