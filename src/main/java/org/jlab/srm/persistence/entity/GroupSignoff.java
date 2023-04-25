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
@Table(name = "GROUP_SIGNOFF", schema = "SRM_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"SYSTEM_ID", "GROUP_ID", "COMPONENT_ID"})})
public class GroupSignoff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "GroupSignoffId", sequenceName = "GROUP_SIGNOFF_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GroupSignoffId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_SIGNOFF_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger groupSignoffId;
    @Column(name = "MODIFIED_USERNAME", nullable = true, length = 512)
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MODIFIED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 1024)
    @Column(length = 1024)
    private String comments;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Status status;
    @JoinColumns({
            @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false),
            @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID", nullable = false)})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private GroupResponsibility groupResponsibility;
    @Column(name = "GROUP_ID", updatable = false, insertable = false)
    private BigInteger groupId;
    @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Component component;
    @Column(name = "COMPONENT_ID", updatable = false, insertable = false)
    private BigInteger componentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHANGE_TYPE", nullable = false, length = 24)
    @Enumerated(EnumType.STRING)
    private SignoffChangeType changeType;

    public GroupSignoff() {
    }

    public GroupSignoff(BigInteger groupSignoffId) {
        this.groupSignoffId = groupSignoffId;
    }

    public GroupSignoff(BigInteger groupSignoffId, String modifiedBy, Date modifiedDate) {
        this.groupSignoffId = groupSignoffId;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

    public SignoffChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(SignoffChangeType changeType) {
        this.changeType = changeType;
    }

    public BigInteger getGroupSignoffId() {
        return groupSignoffId;
    }

    public void setGroupSignoffId(BigInteger groupSignoffId) {
        this.groupSignoffId = groupSignoffId;
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

    public GroupResponsibility getGroupResponsibility() {
        return groupResponsibility;
    }

    public void setGroupResponsibility(GroupResponsibility groupResponsibility) {
        this.groupResponsibility = groupResponsibility;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupSignoffId != null ? groupSignoffId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupSignoff)) {
            return false;
        }
        GroupSignoff other = (GroupSignoff) object;
        return (this.groupSignoffId != null || other.groupSignoffId == null) && (this.groupSignoffId == null || this.groupSignoffId.equals(other.groupSignoffId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.GroupSignoff[ groupSignoffId=" + groupSignoffId + " ]";
    }
}
