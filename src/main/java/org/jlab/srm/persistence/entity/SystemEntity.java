package org.jlab.srm.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * Note: we name this class SystemEntity instead of System because Java has a class named
 * java.lang.System in global scope (implicitly imported) which will cause namespace headaches.
 *
 * @author ryans
 */
@Entity
@Audited
@Table(
    name = "SYSTEM",
    schema = "SRM_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class SystemEntity implements Serializable, Comparable<SystemEntity> {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SystemId", sequenceName = "SYSTEM_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SystemId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYSTEM_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger systemId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(nullable = false, length = 128)
  private String name;

  private BigInteger weight;

  @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Category category;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "system", fetch = FetchType.LAZY)
  @OrderBy("weight ASC")
  @NotAudited
  private List<GroupResponsibility> groupResponsibilityList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "system", fetch = FetchType.LAZY)
  private List<Component> componentList;

  @ManyToMany(mappedBy = "systemList", fetch = FetchType.LAZY)
  @NotAudited
  private List<Application> applicationList;

  public SystemEntity() {}

  public SystemEntity(BigInteger systemId) {
    this.systemId = systemId;
  }

  public SystemEntity(BigInteger systemId, String name) {
    this.systemId = systemId;
    this.name = name;
  }

  public BigInteger getSystemId() {
    return systemId;
  }

  public void setSystemId(BigInteger systemId) {
    this.systemId = systemId;
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

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public List<GroupResponsibility> getGroupResponsibilityList() {
    return groupResponsibilityList;
  }

  public void setGroupResponsibilityList(List<GroupResponsibility> groupResponsibilityList) {
    this.groupResponsibilityList = groupResponsibilityList;
  }

  public List<Component> getComponentList() {
    return componentList;
  }

  public void setComponentList(List<Component> componentList) {
    this.componentList = componentList;
  }

  public List<Application> getApplicationList() {
    return applicationList;
  }

  public void setApplicationList(List<Application> applicationList) {
    this.applicationList = applicationList;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (systemId != null ? systemId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SystemEntity)) {
      return false;
    }
    SystemEntity other = (SystemEntity) object;
    return (this.systemId != null || other.systemId == null)
        && (this.systemId == null || this.systemId.equals(other.systemId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.SystemEntity[ systemId=" + systemId + " ]";
  }

  @Override
  public int compareTo(SystemEntity c) {
    return getName().compareTo(c.getName()); // TODO: look at weight as well
  }
}
