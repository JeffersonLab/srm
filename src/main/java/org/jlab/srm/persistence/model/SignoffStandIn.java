package org.jlab.srm.persistence.model;

import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;

import java.math.BigInteger;
import java.util.Date;

/**
 * In order to avoid the overhead of JPA pulling in the group responsibility
 * OneToMany relationships in the GroupSignoff entity we created this stand-in
 * class.
 *
 * @author ryans
 */
public class SignoffStandIn {

    private final BigInteger groupSignoffId;
    private final BigInteger componentId;
    private final BigInteger groupId;
    private final String modifiedBy;
    private final Date modifiedDate;
    private final SignoffChangeType changeType;
    private final String comments;
    private final Status status;

    public SignoffStandIn(BigInteger groupSignoffId, BigInteger componentId, BigInteger groupId, String modifiedBy, Date modifiedDate, SignoffChangeType changeType, String comments, Status status) {
        this.groupSignoffId = groupSignoffId;
        this.componentId = componentId;
        this.groupId = groupId;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
        this.changeType = changeType;
        this.comments = comments;
        this.status = status;
    }

    public BigInteger getGroupSignoffId() {
        return groupSignoffId;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public String getComments() {
        return comments;
    }

    public Status getStatus() {
        return status;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public SignoffChangeType getChangeType() {
        return changeType;
    }
}
