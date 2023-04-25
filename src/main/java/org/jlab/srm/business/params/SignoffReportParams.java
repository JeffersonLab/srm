package org.jlab.srm.business.params;

import java.math.BigInteger;

public class SignoffReportParams {
    private BigInteger[] destinationIdArray;
    private BigInteger categoryId;
    private BigInteger systemId;
    private BigInteger regionId;
    private BigInteger groupId;
    private BigInteger statusId;
    private Boolean readyTurn;
    private Boolean masked;
    private String componentName;

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

    public BigInteger getStatusId() {
        return statusId;
    }

    public void setStatusId(BigInteger statusId) {
        this.statusId = statusId;
    }

    public Boolean isReadyTurn() {
        return readyTurn;
    }

    public void setReadyTurn(Boolean readyTurn) {
        this.readyTurn = readyTurn;
    }

    public Boolean isMasked() {
        return masked;
    }

    public void setMasked(Boolean masked) {
        this.masked = masked;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
