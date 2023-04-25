package org.jlab.srm.persistence.entity.view;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Staff;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ryans
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "SIGNOFF_ACTIVITY", schema = "SRM_OWNER")
public class SignoffActivityRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_SIGNOFF_HISTORY_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger groupSignoffHistoryId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SYSTEM_ID", nullable = false)
    private BigInteger systemId;
    @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SystemEntity system;    // Used by ActivtyDetail Report
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_ID", nullable = false)
    private BigInteger groupId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID", nullable = false)
    private BigInteger componentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS_ID", nullable = false)
    private BigInteger statusId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "REGION_ID", nullable = false)
    private BigInteger regionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MODIFIED_BY", nullable = false, precision = 22, scale = 0)
    private BigInteger modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MODIFIED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 1024)
    @Column(length = 1024)
    private String comments;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "SYSTEM_NAME", nullable = false, length = 128)
    private String systemName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "COMPONENT_NAME", nullable = false, length = 128)
    private String componentName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "GROUP_NAME", nullable = false, length = 128)
    private String groupName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "STATUS_NAME", nullable = false, length = 128)
    private String statusName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "REGION_NAME", nullable = false, length = 128)
    private String regionName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "USERNAME", nullable = false, length = 256)
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "FIRSTNAME", nullable = false, length = 256)
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "LASTNAME", nullable = false, length = 256)
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHANGE_TYPE", nullable = false, length = 24)
    @Enumerated(EnumType.STRING)
    private SignoffChangeType changeType;
    @Column(name = "UNPOWERED_YN")
    @Size(min = 1, max = 1)
    @NotNull
    private String unpoweredStr;
    @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID", updatable = false, insertable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Component component;
    @JoinColumn(name = "MODIFIED_BY", referencedColumnName = "STAFF_ID", updatable = false, insertable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Staff staff;

    public SignoffActivityRecord() {
    }

    public SignoffActivityRecord(BigInteger groupSignoffHistoryId) {
        this.groupSignoffHistoryId = groupSignoffHistoryId;
    }

    public SignoffActivityRecord(BigInteger groupSignoffHistoryId, BigInteger groupId, BigInteger componentId, BigInteger modifiedBy, Date modifiedDate) {
        this.groupSignoffHistoryId = groupSignoffHistoryId;
        this.groupId = groupId;
        this.componentId = componentId;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

    public boolean isUnpowered() {
        return "Y".equals(unpoweredStr);
    }

    public SignoffChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(SignoffChangeType changeType) {
        this.changeType = changeType;
    }

    public BigInteger getGroupSignoffHistoryId() {
        return groupSignoffHistoryId;
    }

    public void setGroupSignoffHistoryId(BigInteger groupSignoffId) {
        this.groupSignoffHistoryId = groupSignoffId;
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

    public BigInteger getRegionId() {
        return regionId;
    }

    public void setRegionId(BigInteger regionId) {
        this.regionId = regionId;
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

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupSignoffHistoryId != null ? groupSignoffHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SignoffActivityRecord)) {
            return false;
        }
        SignoffActivityRecord other = (SignoffActivityRecord) object;
        return (this.groupSignoffHistoryId != null || other.groupSignoffHistoryId == null) && (this.groupSignoffHistoryId == null || this.groupSignoffHistoryId.equals(other.groupSignoffHistoryId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.SignoffActivity[ groupSignoffHistoryId=" + groupSignoffHistoryId + " ]";
    }

}
