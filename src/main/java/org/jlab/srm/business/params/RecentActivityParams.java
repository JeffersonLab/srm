package org.jlab.srm.business.params;

import java.math.BigInteger;

public class RecentActivityParams {

  private BigInteger[] destinationIdArray;
  private BigInteger[] systemIdArray;
  private BigInteger regionId;
  private BigInteger groupId;
  private BigInteger[] statusIdArray;

  public RecentActivityParams(
      BigInteger[] destinationIdArray,
      BigInteger[] systemIdArray,
      BigInteger regionId,
      BigInteger groupId,
      BigInteger[] statusIdArray) {
    this.destinationIdArray = destinationIdArray;
    this.systemIdArray = systemIdArray;
    this.regionId = regionId;
    this.groupId = groupId;
    this.statusIdArray = statusIdArray;
  }

  public BigInteger[] getDestinationIdArray() {
    return destinationIdArray;
  }

  public void setDestinationIdArray(BigInteger[] destinationIdArray) {
    this.destinationIdArray = destinationIdArray;
  }

  public BigInteger[] getSystemIdArray() {
    return systemIdArray;
  }

  public void setSystemIdArray(BigInteger[] systemIdArray) {
    this.systemIdArray = systemIdArray;
  }

  public BigInteger getRegionId() {
    return regionId;
  }

  public void setRegionId(BigInteger regionId) {
    this.regionId = regionId;
  }

  public BigInteger getGroupId() {
    return groupId;
  }

  public void setGroupId(BigInteger groupId) {
    this.groupId = groupId;
  }

  public BigInteger[] getStatusIdArray() {
    return statusIdArray;
  }

  public void setStatusIdArray(BigInteger[] statusIdArray) {
    this.statusIdArray = statusIdArray;
  }
}
