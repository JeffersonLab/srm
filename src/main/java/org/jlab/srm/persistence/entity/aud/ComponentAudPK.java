package org.jlab.srm.persistence.entity.aud;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * @author ryans
 */
@Embeddable
public class ComponentAudPK implements Serializable {
  @Basic(optional = false)
  @NotNull
  @Column(name = "COMPONENT_ID", nullable = false)
  private BigInteger componentId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "REV", nullable = false)
  private BigInteger rev;

  public ComponentAudPK() {}

  public BigInteger getComponentId() {
    return componentId;
  }

  public void setComponentId(BigInteger componentId) {
    this.componentId = componentId;
  }

  public BigInteger getRev() {
    return rev;
  }

  public void setRev(BigInteger rev) {
    this.rev = rev;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + (this.componentId != null ? this.componentId.hashCode() : 0);
    hash = 23 * hash + (this.rev != null ? this.rev.hashCode() : 0);
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
    final ComponentAudPK other = (ComponentAudPK) obj;
    if (!Objects.equals(this.componentId, other.componentId)) {
      return false;
    }
    return Objects.equals(this.rev, other.rev);
  }
}
