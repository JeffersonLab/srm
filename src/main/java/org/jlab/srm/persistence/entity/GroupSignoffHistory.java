package org.jlab.srm.persistence.entity;

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
@Table(name = "GROUP_SIGNOFF_HISTORY", schema = "HCO_OWNER")
public class GroupSignoffHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "GroupSignoffHistoryId", sequenceName = "GROUP_SIGNOFF_HISTORY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GroupSignoffHistoryId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_SIGNOFF_HISTORY_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger groupSignoffHistoryId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SYSTEM_ID", nullable = false)
    private BigInteger systemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_ID", nullable = false)
    private BigInteger groupId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID", nullable = false)
    private BigInteger componentId;
    @Column(name = "STATUS_ID")
    private BigInteger statusId;
    @JoinColumn(name = "MODIFIED_BY", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff modifiedBy;
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
    @Column(name = "CHANGE_TYPE", nullable = false, length = 24)
    @Enumerated(EnumType.STRING)
    private SignoffChangeType changeType;

    public GroupSignoffHistory() {
    }

    public GroupSignoffHistory(BigInteger groupSignoffHistoryId) {
        this.groupSignoffHistoryId = groupSignoffHistoryId;
    }

    public GroupSignoffHistory(BigInteger groupSignoffHistoryId, BigInteger groupId, BigInteger componentId, Staff modifiedBy, Date modifiedDate) {
        this.groupSignoffHistoryId = groupSignoffHistoryId;
        this.groupId = groupId;
        this.componentId = componentId;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

    public BigInteger getGroupSignoffHistoryId() {
        return groupSignoffHistoryId;
    }

    public void setGroupSignoffHistoryId(BigInteger groupSignoffId) {
        this.groupSignoffHistoryId = groupSignoffId;
    }

    public SignoffChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(SignoffChangeType changeType) {
        this.changeType = changeType;
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

    public Staff getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Staff modifiedBy) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupSignoffHistoryId != null ? groupSignoffHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupSignoffHistory)) {
            return false;
        }
        GroupSignoffHistory other = (GroupSignoffHistory) object;
        return (this.groupSignoffHistoryId != null || other.groupSignoffHistoryId == null) && (this.groupSignoffHistoryId == null || this.groupSignoffHistoryId.equals(other.groupSignoffHistoryId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.GroupSignoffHistory[ groupSignoffHistoryId=" + groupSignoffHistoryId + " ]";
    }

}
