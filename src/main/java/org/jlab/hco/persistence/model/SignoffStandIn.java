package org.jlab.hco.persistence.model;

import org.jlab.hco.persistence.entity.Staff;
import org.jlab.hco.persistence.entity.Status;
import org.jlab.hco.persistence.enumeration.SignoffChangeType;

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

    private BigInteger groupSignoffId;
    private BigInteger componentId;
    private BigInteger groupId;
    private Staff modifiedBy;
    private Date modifiedDate;
    private SignoffChangeType changeType;
    private String comments;
    private Status status;

    public SignoffStandIn(BigInteger groupSignoffId, BigInteger componentId, BigInteger groupId, Staff modifiedBy, Date modifiedDate, SignoffChangeType changeType, String comments, Status status) {
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

    public Staff getModifiedBy() {
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
