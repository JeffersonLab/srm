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
public class SystemAudPK implements Serializable {
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYSTEM_ID", nullable = false)
  private BigInteger systemId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "REV", nullable = false)
  private BigInteger rev;

  public BigInteger getSystemId() {
    return systemId;
  }

  public void setSystemId(BigInteger systemId) {
    this.systemId = systemId;
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
    hash = 23 * hash + (this.systemId != null ? this.systemId.hashCode() : 0);
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
    final SystemAudPK other = (SystemAudPK) obj;
    if (!Objects.equals(this.systemId, other.systemId)) {
      return false;
    }
    return Objects.equals(this.rev, other.rev);
  }
}
