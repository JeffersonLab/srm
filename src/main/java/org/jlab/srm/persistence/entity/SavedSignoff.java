package org.jlab.srm.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ryans
 */
@Entity
@Table(
    name = "SAVED_SIGNOFF",
    schema = "SRM_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"SIGNOFF_NAME"})})
public class SavedSignoff implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SavedSignoffId", sequenceName = "SAVED_SIGNOFF_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SavedSignoffId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SAVED_SIGNOFF_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger savedSignoffId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "SIGNOFF_NAME", nullable = false, length = 256)
  private String signoffName;

  @Size(max = 128)
  @Column(name = "FILTER_COMPONENT_NAME", length = 128)
  private String filterComponentName;

  @Size(max = 1024)
  @Column(name = "SIGNOFF_COMMENTS", length = 1024)
  private String signoffComments;

  private BigInteger weight;

  @JoinColumn(name = "FILTER_STATUS_ID", referencedColumnName = "STATUS_ID", nullable = true)
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private Status filterStatus;

  @JoinColumn(name = "FILTER_REGION_ID", referencedColumnName = "REGION_ID", nullable = true)
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private Region region;

  @JoinColumn(name = "FILTER_SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @NotNull
  private SystemEntity system;

  @JoinColumn(name = "FILTER_GROUP_ID", referencedColumnName = "GROUP_ID", nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @NotNull
  private ResponsibleGroup group;

  @JoinColumn(name = "SIGNOFF_STATUS_ID", referencedColumnName = "STATUS_ID", nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @NotNull
  private Status signoffStatus;

  @JoinColumn(
      name = "SAVED_SIGNOFF_TYPE_ID",
      referencedColumnName = "SAVED_SIGNOFF_TYPE_ID",
      nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @NotNull
  private SavedSignoffType type;

  public SavedSignoff() {}

  public BigInteger getSavedSignoffId() {
    return savedSignoffId;
  }

  public void setSavedSignoffId(BigInteger savedSignoffId) {
    this.savedSignoffId = savedSignoffId;
  }

  public String getSignoffName() {
    return signoffName;
  }

  public void setSignoffName(String signoffName) {
    this.signoffName = signoffName;
  }

  public String getFilterComponentName() {
    return filterComponentName;
  }

  public void setFilterComponentName(String filterComponentName) {
    this.filterComponentName = filterComponentName;
  }

  public String getSignoffComments() {
    return signoffComments;
  }

  public void setSignoffComments(String signoffComments) {
    this.signoffComments = signoffComments;
  }

  public BigInteger getWeight() {
    return weight;
  }

  public void setWeight(BigInteger weight) {
    this.weight = weight;
  }

  public SystemEntity getSystem() {
    return system;
  }

  public void setSystem(SystemEntity system) {
    this.system = system;
  }

  public Status getSignoffStatus() {
    return signoffStatus;
  }

  public void setSignoffStatus(Status signoffStatus) {
    this.signoffStatus = signoffStatus;
  }

  public Status getFilterStatus() {
    return filterStatus;
  }

  public void setFilterStatus(Status filterStatus) {
    this.filterStatus = filterStatus;
  }

  public ResponsibleGroup getGroup() {
    return group;
  }

  public void setGroup(ResponsibleGroup group) {
    this.group = group;
  }

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public SavedSignoffType getType() {
    return type;
  }

  public void setType(SavedSignoffType type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (savedSignoffId != null ? savedSignoffId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SavedSignoff)) {
      return false;
    }
    SavedSignoff other = (SavedSignoff) object;
    return (this.savedSignoffId != null || other.savedSignoffId == null)
        && (this.savedSignoffId == null || this.savedSignoffId.equals(other.savedSignoffId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.SavedDowngrade[ savedDowngradeId="
        + savedSignoffId
        + " ]";
  }
}
