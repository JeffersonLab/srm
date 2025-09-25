package org.jlab.srm.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Checklist", schema = "SRM_OWNER")
public class Checklist implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Access(
      value =
          AccessType
              .PROPERTY) /*Hibernate will initialize lazy proxy when accessing ID without this (HHH-3718)*/
  @SequenceGenerator(name = "ChecklistId", sequenceName = "CHECKLIST_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChecklistId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "CHECKLIST_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger checklistId;

  @Column(name = "BODY_HTML")
  @Lob
  private String bodyHtml;

  @Size(max = 64)
  @Column(length = 64)
  private String author;

  @Column(name = "MODIFIED_USERNAME", nullable = true, length = 512)
  private String modifiedBy;

  @Basic(optional = false)
  @NotNull
  @Column(name = "MODIFIED_DATE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Size(max = 1024)
  @Column(length = 1024)
  private String comments;

  @OneToOne(optional = true, mappedBy = "checklist")
  private GroupResponsibility groupResponsibility;

  public Checklist() {}

  public String getBodyHtml() {
    return bodyHtml;
  }

  public void setBodyHtml(String bodyHtml) {
    this.bodyHtml = bodyHtml;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public BigInteger getChecklistId() {
    return checklistId;
  }

  public void setChecklistId(BigInteger checklistId) {
    this.checklistId = checklistId;
  }

  public GroupResponsibility getGroupResponsibility() {
    return groupResponsibility;
  }

  public void setGroupResponsibility(GroupResponsibility groupResponsibility) {
    this.groupResponsibility = groupResponsibility;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + (this.checklistId != null ? this.checklistId.hashCode() : 0);
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
    final Checklist other = (Checklist) obj;
    return Objects.equals(this.checklistId, other.checklistId);
  }

  @Override
  public String toString() {
    return "Checklist{" + "checklistId=" + checklistId + '}';
  }
}
