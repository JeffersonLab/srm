package org.jlab.srm.persistence.model;

import org.jlab.srm.persistence.enumeration.SignoffChangeType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class SignoffActivityCompressedRecord implements Serializable {
    private BigInteger componentCount;
    private BigInteger firstHistoryId;
    private BigInteger firstComponentId;
    private String firstComponentName;
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
    private SignoffChangeType changeType;


    public SignoffChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(SignoffChangeType changeType) {
        this.changeType = changeType;
    }

    public boolean isFirstUnpowered() {
        return unpowered;
    }

    public void setFirstUnpowered(boolean unpowered) {
        this.unpowered = unpowered;
    }

    public BigInteger getFirstHistoryId() {
        return firstHistoryId;
    }

    public void setGroupSignoffHistoryId(BigInteger firstHistoryId) {
        this.firstHistoryId = firstHistoryId;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public BigInteger getFirstComponentId() {
        return firstComponentId;
    }

    public void setFirstComponentId(BigInteger firstComponentId) {
        this.firstComponentId = firstComponentId;
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

    public String getFirstComponentName() {
        return firstComponentName;
    }

    public void setFirstComponentName(String firstComponentName) {
        this.firstComponentName = firstComponentName;
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
        int hash = 0;
        hash += (firstHistoryId != null ? firstHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SignoffActivityCompressedRecord)) {
            return false;
        }
        SignoffActivityCompressedRecord other = (SignoffActivityCompressedRecord) object;
        return (this.firstHistoryId != null || other.firstHistoryId == null) && (this.firstHistoryId == null || this.firstHistoryId.equals(other.firstHistoryId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.SignoffActivity[ groupSignoffHistoryId=" + firstHistoryId + " ]";
    }

}
