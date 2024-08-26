package org.jlab.srm.business.params;

import java.math.BigInteger;

public class GroupResponsibilityReportParams {
  private BigInteger[] destinationIdArray;
  private BigInteger categoryId;
  private BigInteger systemId;
  private BigInteger groupId;
  private Boolean checklistRequired;
  private Boolean checklistMissing;

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

  public BigInteger getGroupId() {
    return groupId;
  }

  public void setGroupId(BigInteger groupId) {
    this.groupId = groupId;
  }

  public Boolean isChecklistRequired() {
    return checklistRequired;
  }

  public void setChecklistRequired(Boolean checklistRequired) {
    this.checklistRequired = checklistRequired;
  }

  public Boolean isChecklistMissing() {
    return checklistMissing;
  }

  public void setChecklistMissing(Boolean checklistMissing) {
    this.checklistMissing = checklistMissing;
  }
}
