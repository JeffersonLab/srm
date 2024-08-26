package org.jlab.srm.persistence.entity.view;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.jlab.srm.persistence.entity.Status;

/**
 * @author ryans
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "COMPONENT_STATUS_2", schema = "SRM_OWNER")
public class ComponentStatus implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "COMPONENT_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger componentId;

  /*@Basic(optional = false)
  @Column(name = "STATUS_ID", nullable = false, precision = 22, scale = 0)
  @NotNull
  private BigInteger statusId; */
  @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID", nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Status status;

  public ComponentStatus() {}

  public ComponentStatus(BigInteger componentId) {
    this.componentId = componentId;
  }

  public BigInteger getComponentId() {
    return componentId;
  }

  public void setComponentId(BigInteger componentId) {
    this.componentId = componentId;
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
    if (!(object instanceof ComponentStatus)) {
      return false;
    }
    ComponentStatus other = (ComponentStatus) object;
    return (this.componentId != null || other.componentId == null)
        && (this.componentId == null || this.componentId.equals(other.componentId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.ComponentStatus[ componentId=" + componentId + " ]";
  }
}
