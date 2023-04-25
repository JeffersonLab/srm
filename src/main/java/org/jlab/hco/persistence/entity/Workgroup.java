package org.jlab.hco.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@Entity
@Table(name = "WORKGROUP", schema = "SUPPORT")
public class Workgroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "WORKGROUP_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger workgroupId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    @JoinTable(name = "WORKGROUP_MEMBERSHIP", joinColumns = {
            @JoinColumn(name = "WORKGROUP_ID", referencedColumnName = "WORKGROUP_ID", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "STAFF_ID", referencedColumnName = "STAFF_ID", nullable = false)})
    @ManyToMany
    @OrderBy("lastname asc")
    private List<Staff> staffList;

    public Workgroup() {
    }

    public Workgroup(BigInteger workgroupId) {
        this.workgroupId = workgroupId;
    }

    public BigInteger getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(BigInteger workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workgroupId != null ? workgroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Workgroup)) {
            return false;
        }
        Workgroup other = (Workgroup) object;
        return (this.workgroupId != null || other.workgroupId == null) && (this.workgroupId == null || this.workgroupId.equals(other.workgroupId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.Workgroup[ workgroupId=" + workgroupId + " ]";
    }

}
