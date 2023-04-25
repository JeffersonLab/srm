package org.jlab.srm.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "GROUP_RESPONSIBILITY", schema = "SRM_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"GROUP_ID", "SYSTEM_ID"})})
public class GroupResponsibility implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "GroupResponsibilityId", sequenceName = "GROUP_RESPONSIBILITY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GroupResponsibilityId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_RESPONSIBILITY_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger groupResponsibilityId;
    @Column(name = "CHECKLIST_REQUIRED")
    @Size(min = 1, max = 1)
    @NotNull
    private String checklistRequiredStr;
    private BigInteger weight;
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID", nullable = false)
    @ManyToOne(optional = false)
    private ResponsibleGroup group;
    @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false)
    @ManyToOne(optional = false)
    private SystemEntity system;
    @Column(name = "PUBLISHED")
    @Size(min = 1, max = 1)
    @NotNull
    private String publishedStr;
    @JoinColumn(name = "PUBLISHED_BY", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = true)
    private Staff PublishedBy;
    @Basic(optional = true)
    @Column(name = "PUBLISHED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedDate;
    @JoinColumn(name = "CHECKLIST_ID", referencedColumnName = "CHECKLIST_ID", nullable = true)
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    private Checklist checklist;
    @OneToMany(mappedBy = "groupResponsibility")
    private List<GroupSignoff> groupSignoffList;

    public GroupResponsibility() {
    }

    public GroupResponsibility(BigInteger groupResponsibilityId) {
        this.groupResponsibilityId = groupResponsibilityId;
    }

    public BigInteger getGroupResponsibilityId() {
        return groupResponsibilityId;
    }

    public void setGroupResponsibilityId(BigInteger groupResponsibilityId) {
        this.groupResponsibilityId = groupResponsibilityId;
    }

    public boolean isChecklistRequired() {
        return "Y".equals(checklistRequiredStr);
    }

    public void setChecklistRequired(boolean checklistRequired) {
        this.checklistRequiredStr = checklistRequired ? "Y" : "N";
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    public ResponsibleGroup getGroup() {
        return group;
    }

    public void setGroup(ResponsibleGroup group) {
        this.group = group;
    }

    public SystemEntity getSystem() {
        return system;
    }

    public void setSystem(SystemEntity system) {
        this.system = system;
    }

    public List<GroupSignoff> getGroupSignoffList() {
        return groupSignoffList;
    }

    public void setGroupSignoffList(List<GroupSignoff> groupSignoffList) {
        this.groupSignoffList = groupSignoffList;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public boolean isPublished() {
        return "Y".equals(publishedStr);
    }

    public void setPublished(boolean published) {
        this.publishedStr = published ? "Y" : "N";
    }

    public Staff getPublishedBy() {
        return PublishedBy;
    }

    public void setPublishedBy(Staff PublishedBy) {
        this.PublishedBy = PublishedBy;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupResponsibilityId != null ? groupResponsibilityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupResponsibility)) {
            return false;
        }
        GroupResponsibility other = (GroupResponsibility) object;
        return (this.groupResponsibilityId != null || other.groupResponsibilityId == null) && (this.groupResponsibilityId == null || this.groupResponsibilityId.equals(other.groupResponsibilityId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.GroupResponsibility[ groupResponsibilityId=" + groupResponsibilityId + " ]";
    }
}
