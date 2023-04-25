package org.jlab.srm.persistence.model;

import org.jlab.srm.persistence.enumeration.AllChangeType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AllActivityRecord implements Serializable {
    private BigInteger componentCount;
    private BigInteger recordId;
    private BigInteger componentId;
    private String componentName;
    private boolean unpowered;
    private BigInteger systemId;
    private BigInteger groupId;
    private BigInteger statusId;
    private BigInteger modifiedBy;
    private Date modifiedDate;
    private String comments;
    private String systemName;
    private String groupName;
    private String statusName;
    private String username;
    private String firstname;
    private String lastname;
    private AllChangeType changeType;


    public AllChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(AllChangeType changeType) {
        this.changeType = changeType;
    }

    public boolean isUnpowered() {
        return unpowered;
    }

    public void setUnpowered(boolean unpowered) {
        this.unpowered = unpowered;
    }

    public BigInteger getRecordId() {
        return recordId;
    }

    public void setRecordId(BigInteger recordId) {
        this.recordId = recordId;
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

    public BigInteger getStatusId() {
        return statusId;
    }

    public void setStatusId(BigInteger statusId) {
        this.statusId = statusId;
    }

    public BigInteger getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getModifiedDatePlusOneMinute() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(modifiedDate);
        cal.add(Calendar.MINUTE, 1);
        return cal.getTime();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public BigInteger getSystemId() {
        return systemId;
    }

    public void setSystemId(BigInteger systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
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

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public BigInteger getComponentCount() {
        return componentCount;
    }

    public void setComponentCount(BigInteger componentCount) {
        this.componentCount = componentCount;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.recordId);
        hash = 53 * hash + Objects.hashCode(this.changeType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AllActivityRecord other = (AllActivityRecord) obj;
        if (!Objects.equals(this.recordId, other.recordId)) {
            return false;
        }
        return this.changeType == other.changeType;
    }
}
