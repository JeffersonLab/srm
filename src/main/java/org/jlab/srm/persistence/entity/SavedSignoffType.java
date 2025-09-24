package org.jlab.srm.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ryans
 */
@Entity
@Table(
    name = "SAVED_SIGNOFF_TYPE",
    schema = "SRM_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class SavedSignoffType implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "SAVED_SIGNOFF_TYPE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger savedSignoffTypeId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(nullable = false, length = 128)
  private String name;

  private BigInteger weight;

  public SavedSignoffType() {}

  public SavedSignoffType(BigInteger savedSignoffTypeId) {
    this.savedSignoffTypeId = savedSignoffTypeId;
  }

  public SavedSignoffType(BigInteger savedSignoffTypeId, String name) {
    this.savedSignoffTypeId = savedSignoffTypeId;
    this.name = name;
  }

  public BigInteger getSavedSignoffTypeId() {
    return savedSignoffTypeId;
  }

  public void setSavedSignoffTypeId(BigInteger savedSignoffTypeId) {
    this.savedSignoffTypeId = savedSignoffTypeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
    hash += (savedSignoffTypeId != null ? savedSignoffTypeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SavedSignoffType)) {
      return false;
    }
    SavedSignoffType other = (SavedSignoffType) object;
    return (this.savedSignoffTypeId != null || other.savedSignoffTypeId == null)
        && (this.savedSignoffTypeId == null
            || this.savedSignoffTypeId.equals(other.savedSignoffTypeId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.SavedSignoffType[ savedSignoffTypeId="
        + savedSignoffTypeId
        + " ]";
  }
}
