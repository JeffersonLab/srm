package org.jlab.hco.business.params;

import java.math.BigInteger;
import java.util.Date;

public class SignoffParams {
    private BigInteger[] destinationIdArray;
    private BigInteger categoryId;
    private BigInteger systemId;
    private BigInteger[] regionIdArray;
    private BigInteger groupId;
    private BigInteger[] statusIdArray;
    private Boolean readyTurn;
    private String componentName;
    private Date minLastModified;
    private Date maxLastModified;

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

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public Boolean isReadyTurn() {
        return readyTurn;
    }

    public void setReadyTurn(Boolean readyTurn) {
        this.readyTurn = readyTurn;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Date getMinLastModified() {
        return minLastModified;
    }

    public void setMinLastModified(Date minLastModified) {
        this.minLastModified = minLastModified;
    }

    public Date getMaxLastModified() {
        return maxLastModified;
    }

    public void setMaxLastModified(Date maxLastModified) {
        this.maxLastModified = maxLastModified;
    }

    public BigInteger[] getRegionIdArray() {
        return regionIdArray;
    }

    public void setRegionIdArray(BigInteger[] regionIdArray) {
        this.regionIdArray = regionIdArray;
    }

    public BigInteger[] getStatusIdArray() {
        return statusIdArray;
    }

    public void setStatusIdArray(BigInteger[] statusIdArray) {
        this.statusIdArray = statusIdArray;
    }
}
