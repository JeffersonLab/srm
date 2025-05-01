package org.jlab.srm.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;
import org.jlab.smoothness.persistence.view.User;

/**
 * Note: we name this class ResponsibleGroup instead of Group because group is a reserved word in
 * SQL.
 *
 * @author ryans
 */
@Entity
@Table(name = "RESPONSIBLE_GROUP", schema = "SRM_OWNER")
public class ResponsibleGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "GroupId", sequenceName = "GROUP_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GroupId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "GROUP_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger groupId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(nullable = false, length = 128)
  private String name;

  @Size(max = 1024)
  @Column(length = 1024)
  private String description;

  @NotNull
  @Basic(optional = false)
  @Column(name = "GOAL_PERCENT", nullable = false)
  private int goalPercent;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", fetch = FetchType.LAZY)
  private List<GroupResponsibility> groupResponsibilityList;

  @NotNull
  @Column(name = "LEADER_WORKGROUP", nullable = false, length = 64)
  private String leaderWorkgroup;

  @Basic
  @Column(name = "ARCHIVED_YN", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean archived;

  @Transient private List<User> leaders;

  public ResponsibleGroup() {}

  public ResponsibleGroup(BigInteger groupId) {
    this.groupId = groupId;
  }

  public ResponsibleGroup(BigInteger groupId, String name) {
    this.groupId = groupId;
    this.name = name;
  }

  public BigInteger getGroupId() {
    return groupId;
  }

  public void setGroupId(BigInteger groupId) {
    this.groupId = groupId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getGoalPercent() {
    return goalPercent;
  }

  public void setGoalPercent(int goalPercent) {
    this.goalPercent = goalPercent;
  }

  public List<GroupResponsibility> getGroupResponsibilityList() {
    return groupResponsibilityList;
  }

  public void setGroupResponsibilityList(List<GroupResponsibility> groupResponsibilityList) {
    this.groupResponsibilityList = groupResponsibilityList;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public String getLeaderWorkgroup() {
    return leaderWorkgroup;
  }

  public void setLeaderWorkgroup(String leaderWorkgroup) {
    this.leaderWorkgroup = leaderWorkgroup;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (groupId != null ? groupId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ResponsibleGroup)) {
      return false;
    }
    ResponsibleGroup other = (ResponsibleGroup) object;
    return (this.groupId != null || other.groupId == null)
        && (this.groupId == null || this.groupId.equals(other.groupId));
  }

  @Override
  public String toString() {
    return "org.jlab.srm.persistence.entity.ResponsibleGroup[ groupId=" + groupId + " ]";
  }

  public void setLeaders(List<User> leaders) {
    this.leaders = leaders;
  }

  public List<User> getLeaders() {
    return leaders;
  }
}
