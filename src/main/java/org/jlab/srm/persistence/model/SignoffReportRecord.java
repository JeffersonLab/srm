package org.jlab.srm.persistence.model;

import org.jlab.srm.persistence.entity.Status;

import java.math.BigInteger;
import java.util.Date;

/**
 * In order to handle scenario where a group_signoff record is implicit due to
 * the group_responsibility table, but isn't actually present we use the
 * component_signoff view and this model object.
 *
 * @author ryans
 */
public class SignoffReportRecord {

    private BigInteger componentId;
    private BigInteger groupId;
    private BigInteger systemId;
    private String modifiedBy;
    private Date modifiedDate;
    private String comments;
    private Status status;
    private String componentName;
    private String groupName;
    private String systemName;
    private boolean unpowered;

    public SignoffReportRecord() {

    }

    public SignoffReportRecord(BigInteger componentId, BigInteger groupId, BigInteger systemId, String modifiedBy, Date modifiedDate, String comments, Status status, String componentName, String groupName, String systemName) {
        this.componentId = componentId;
        this.groupId = groupId;
        this.systemId = systemId;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
        this.comments = comments;
        this.status = status;
        this.componentName = componentName;
        this.groupName = groupName;
        this.systemName = systemName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public void setComponentId(BigInteger componentId) {
        this.componentId = componentId;
    }

    public BigInteger getSystemId() {
        return systemId;
    }

    public void setSystemId(BigInteger systemId) {
        this.systemId = systemId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public boolean isUnpowered() {
        return unpowered;
    }

    public void setUnpowered(boolean unpowered) {
        this.unpowered = unpowered;
    }
}
