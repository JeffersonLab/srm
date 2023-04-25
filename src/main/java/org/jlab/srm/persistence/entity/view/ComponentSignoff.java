package org.jlab.srm.persistence.entity.view;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author ryans
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "COMPONENT_SIGNOFF", schema = "HCO_OWNER")
@IdClass(ComponentSignoffKey.class)
public class ComponentSignoff implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SYSTEM_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger systemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID", nullable = false, precision = 22, scale = 0)
    @Id
    private BigInteger componentId;
    @Basic(optional = false)
    @Column(name = "GROUP_ID", nullable = false, precision = 22, scale = 0)
    @NotNull
    @Id
    private BigInteger groupId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger statusId;

    public ComponentSignoff() {
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

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public BigInteger getStatusId() {
        return statusId;
    }

    public void setStatusId(BigInteger statusId) {
        this.statusId = statusId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.componentId);
        hash = 11 * hash + Objects.hashCode(this.groupId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponentSignoff other = (ComponentSignoff) obj;
        if (!Objects.equals(this.componentId, other.componentId)) {
            return false;
        }
        return Objects.equals(this.groupId, other.groupId);
    }

    @Override
    public String toString() {
        return "ComponentSignoff{" + "systemId=" + systemId + ", componentId=" + componentId +
                ", groupId=" + groupId + ", statusId=" + statusId + '}';
    }
}
