package org.jlab.srm.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import org.jlab.srm.persistence.entity.view.ComponentStatus;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;

/**
 * @author ryans
 */
@Entity
@Table(
    schema = "SRM_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class Status implements Serializable {

  /* We cache all possible values for ease of use and performance */
  @Transient
  public static final Status NOT_APPLICABLE = new Status(BigInteger.valueOf(0L), "Not Applicable");

  @Transient public static final Status READY = new Status(BigInteger.valueOf(1L), "Ready");
  @Transient public static final Status CHECKED = new Status(BigInteger.valueOf(50L), "Checked");

  @Transient
  public static final Status NOT_READY = new Status(BigInteger.valueOf(100L), "Not Ready");

  @Transient
  public static final Status MASKED = new Status(BigInteger.valueOf(150L), "Masked (Director)");

  @Transient
  public static final Status MASKED_CC =
      new Status(BigInteger.valueOf(200L), "Masked (Crew Chief)");

  @Transient
  public static final Status MASKED_ADMIN =
      new Status(BigInteger.valueOf(250L), "Masked (Administrator)");

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "STATUS_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger statusId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(nullable = false, length = 128)
  private String name;

  @OneToMany(mappedBy = "status")
  private List<GroupSignoff> groupSignoffList;

  @OneToMany(mappedBy = "status")
  private List<ComponentStatus> componentStatusList;

  public Status() {}

  public Status(BigInteger statusId, String name) {
    this.statusId = statusId;
    this.name = name;
  }

  public static SignoffChangeType CHANGE(Status before, Status after) {
    SignoffChangeType type;

    if (before.equals(after)) {
      type = SignoffChangeType.COMMENT;
    } else if (after.equals(Status.NOT_APPLICABLE)) {
      type = SignoffChangeType.UPGRADE;
    } else if (before.equals(Status.NOT_APPLICABLE)) {
      type = SignoffChangeType.DOWNGRADE;
    } else if (after.equals(Status.READY)) {
      if (before.equals(Status.NOT_APPLICABLE)) {
        type = SignoffChangeType.DOWNGRADE;
      } else {
        type = SignoffChangeType.UPGRADE;
      }
    } else if (after.equals(Status.CHECKED)) {
      if (before.equals(Status.READY)) {
        type = SignoffChangeType.DOWNGRADE;
      } else {
        type = SignoffChangeType.UPGRADE;
      }
    } else { // after Not Ready
      type = SignoffChangeType.DOWNGRADE;
    }

    return type;
  }

  public static Status FROM_ID(BigInteger id) {
    Status status;
    switch (id.intValue()) {
      case 0:
        status = Status.NOT_APPLICABLE;
        break;
      case 1:
        status = Status.READY;
        break;
      case 50:
        status = Status.CHECKED;
        break;
      case 100:
        status = Status.NOT_READY;
        break;
      case 150:
        status = Status.MASKED;
        break;
      case 200:
        status = Status.MASKED_CC;
        break;
      case 250:
        status = Status.MASKED_ADMIN;
        break;
      default:
        throw new IllegalArgumentException(
            "StatusId must be one of 0, 1, 50, 100, 150, 200, 250.  Found: " + id.intValue());
    }
    return status;
  }

  public static Status MAX(List<Status> statuses) {
    Status max = Status.READY;

    if (statuses != null && !statuses.isEmpty()) {
      for (Status status : statuses) {
        if (status.getStatusId().intValue() > max.getStatusId().intValue()) {
          max = status;
        }
      }
    }
    return max;
  }

  public BigInteger getStatusId() {
    return statusId;
  }

  public void setStatusId(BigInteger statusId) {
    this.statusId = statusId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMaskType() {
    String maskType = null;

    switch (this.statusId.intValue()) {
      case 150:
        maskType = "Director";
        break;
      case 200:
        maskType = "Crew Chief";
        break;
      case 250:
        maskType = "Administrator";
        break;
    }

    return maskType;
  }

  public List<GroupSignoff> getGroupSignoffList() {
    return groupSignoffList;
  }

  public void setGroupSignoffList(List<GroupSignoff> groupSignoffList) {
    this.groupSignoffList = groupSignoffList;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (statusId != null ? statusId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Status)) {
      return false;
    }
    Status other = (Status) object;
    return (this.statusId != null || other.statusId == null)
        && (this.statusId == null || this.statusId.equals(other.statusId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.Status[ statusId=" + statusId + " ]";
  }
}
