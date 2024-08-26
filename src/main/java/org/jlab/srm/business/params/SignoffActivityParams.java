package org.jlab.srm.business.params;

import java.math.BigInteger;
import java.util.Date;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;

public class SignoffActivityParams {
  private BigInteger[] destinationIdArray;
  private BigInteger categoryId;
  private BigInteger systemId;
  private BigInteger regionId;
  private BigInteger groupId;
  private String username;
  private String componentName;
  private SignoffChangeType change;
  private Date start;
  private Date end;
  private BigInteger[] statusIdArray;

  public BigInteger getGroupId() {
    return groupId;
  }

  public void setGroupId(BigInteger groupId) {
    this.groupId = groupId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getComponentName() {
    return componentName;
  }

  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  public BigInteger[] getDestinationIdArray() {
    return destinationIdArray;
  }

  public void setDestinationIdArray(BigInteger[] destinationIdArray) {
    this.destinationIdArray = destinationIdArray;
  }

  public BigInteger getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(BigInteger categoryId) {
    this.categoryId = categoryId;
  }

  public BigInteger getSystemId() {
    return systemId;
  }

  public void setSystemId(BigInteger systemId) {
    this.systemId = systemId;
  }

  public BigInteger getRegionId() {
    return regionId;
  }

  public void setRegionId(BigInteger regionId) {
    this.regionId = regionId;
  }

  public SignoffChangeType getChange() {
    return change;
  }

  public void setChange(SignoffChangeType change) {
    this.change = change;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public BigInteger[] getStatusIdArray() {
    return statusIdArray;
  }

  public void setStatusIdArray(BigInteger[] statusIdArray) {
    this.statusIdArray = statusIdArray;
  }
}
