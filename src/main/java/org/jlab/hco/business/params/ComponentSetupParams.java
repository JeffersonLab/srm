package org.jlab.hco.business.params;

import org.jlab.hco.persistence.enumeration.DataSource;

import java.math.BigInteger;

public class ComponentSetupParams {
    private BigInteger destinationId;
    private BigInteger categoryId;
    private BigInteger systemId;
    private BigInteger regionId;
    private BigInteger groupId;
    private DataSource source;
    private Boolean masked;
    private Boolean exception;
    private Boolean unpowered;
    private String componentName;

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public Boolean isMasked() {
        return masked;
    }

    public void setMasked(Boolean masked) {
        this.masked = masked;
    }

    public Boolean isException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }

    public Boolean isUnpowered() {
        return unpowered;
    }

    public void setUnpowered(Boolean unpowered) {
        this.unpowered = unpowered;
    }

    public BigInteger getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(BigInteger destinationId) {
        this.destinationId = destinationId;
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
