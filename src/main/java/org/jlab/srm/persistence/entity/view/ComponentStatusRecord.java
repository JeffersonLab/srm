package org.jlab.srm.persistence.entity.view;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.entity.SystemEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ryans
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "COMPONENT_STATUS", schema = "SRM_OWNER")
public class ComponentStatusRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger componentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    @Basic(optional = false)
    @Column(name = "STATUS_ID", nullable = false, precision = 22, scale = 0)
    @NotNull
    private BigInteger statusId;
    @Basic(optional = false)
    @Column(name = "REGION_ID", nullable = false, precision = 22, scale = 0)
    @NotNull
    private BigInteger regionId;
    @Basic(optional = false)
    @Column(name = "SYSTEM_ID", nullable = false, precision = 22, scale = 0)
    @NotNull
    private BigInteger systemId;
    @Basic(optional = true)
    @Column(name = "WEIGHT")
    private BigInteger weight;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "SYSTEM_NAME", nullable = false, length = 128)
    private String systemName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "REGION_NAME", nullable = false, length = 128)
    private String regionName;
    @Column(name = "UNPOWERED_YN")
    @Size(min = 1, max = 1)
    @NotNull
    private String unpoweredStr;
    @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID", updatable = false, insertable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Component component;
    @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", updatable = false, insertable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SystemEntity system;

    public ComponentStatusRecord() {
    }

    public ComponentStatusRecord(BigInteger componentId) {
        this.componentId = componentId;
    }

    public ComponentStatusRecord(BigInteger componentId, String name) {
        this.componentId = componentId;
        this.name = name;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public void setComponentId(BigInteger componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusName() {
        return Status.FROM_ID(statusId).getName();
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public boolean isUnpowered() {
        return "Y".equals(unpoweredStr);
    }

    public BigInteger getStatusId() {
        return statusId;
    }

    public void setStatusId(BigInteger statusId) {
        this.statusId = statusId;
    }

    public BigInteger getRegionId() {
        return regionId;
    }

    public void setRegionId(BigInteger regionId) {
        this.regionId = regionId;
    }

    public BigInteger getSystemId() {
        return systemId;
    }

    public void setSystemId(BigInteger systemId) {
        this.systemId = systemId;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentId != null ? componentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentStatusRecord)) {
            return false;
        }
        ComponentStatusRecord other = (ComponentStatusRecord) object;
        return (this.componentId != null || other.componentId == null) && (this.componentId == null || this.componentId.equals(other.componentId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.ComponentStatusRecord[ componentId=" + componentId + " ]";
    }

}
