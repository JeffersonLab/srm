package org.jlab.srm.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ryans
 */
@Entity
@Table(name = "SETTINGS", schema = "SRM_OWNER")
public class Settings implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "SETTINGS_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger settingsId;

  @Column(name = "AUTO_EMAIL_YN")
  @Size(min = 1, max = 1)
  @NotNull
  private String autoEmailYn;

  @Basic(optional = true)
  @Column(name = "GOAL_DATE", nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date goalDate;

  @Basic(optional = true)
  @Column(name = "MASK_REQUEST_EMAIL_CSV", nullable = true)
  private String maskRequestEmailCsv;

  @Basic(optional = true)
  @Column(name = "ACTIVITY_EMAIL_CSV", nullable = true)
  private String activityEmailCsv;

  public List<Address> getMaskRequestEmailAddresses() throws AddressException {
    List<Address> addressList = new ArrayList<>();

    if (maskRequestEmailCsv != null && !maskRequestEmailCsv.isEmpty()) {
      String[] tokens = maskRequestEmailCsv.split(",");

      for (String token : tokens) {
        addressList.add(new InternetAddress(token.trim()));
      }
    }

    return addressList;
  }

  public List<Address> getActivityEmailAddresses() throws AddressException {
    List<Address> addressList = new ArrayList<>();

    if (activityEmailCsv != null && !activityEmailCsv.isEmpty()) {
      String[] tokens = activityEmailCsv.split(",");

      for (String token : tokens) {
        addressList.add(new InternetAddress(token.trim()));
      }
    }

    return addressList;
  }

  public BigInteger getSettingsId() {
    return settingsId;
  }

  public void setSettingsId(BigInteger settingsId) {
    this.settingsId = settingsId;
  }

  public Date getGoalDate() {
    return goalDate;
  }

  public void setGoalDate(Date goalDate) {
    this.goalDate = goalDate;
  }

  public boolean isAutoEmail() {
    return "Y".equals(autoEmailYn);
  }

  public void setAutoEmail(boolean autoEmail) {
    this.autoEmailYn = autoEmail ? "Y" : "N";
  }
}
