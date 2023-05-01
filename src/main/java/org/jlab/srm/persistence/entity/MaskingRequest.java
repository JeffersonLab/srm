package org.jlab.srm.persistence.entity;

import org.jlab.srm.persistence.enumeration.MaskingRequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ryans
 */
@Entity
@Table(name = "MASKING_REQUEST", schema = "SRM_OWNER")
public class MaskingRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "MaskingRequestId", sequenceName = "MASKING_REQUEST_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MaskingRequestId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "MASKING_REQUEST_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger maskingRequestId;
    @NotNull
    @Column(name = "REQUEST_USERNAME", nullable = false, length = 64)
    private String requestBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "REQUEST_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "REQUEST_REASON", nullable = false, length = 512)
    private String requestReason;
    @Basic(optional = false)
    @Column(name = "REQUEST_STATUS", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private MaskingRequestStatus requestStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MASK_EXPIRATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date maskExpirationDate;
    @NotNull
    @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Component component;

    public BigInteger getMaskingRequestId() {
        return maskingRequestId;
    }

    public void setMaskingRequestId(BigInteger maskingRequestId) {
        this.maskingRequestId = maskingRequestId;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public MaskingRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(MaskingRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getMaskExpirationDate() {
        return maskExpirationDate;
    }

    public void setMaskExpirationDate(Date maskExpirationDate) {
        this.maskExpirationDate = maskExpirationDate;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (maskingRequestId != null ? maskingRequestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MaskingRequest)) {
            return false;
        }
        MaskingRequest other = (MaskingRequest) object;
        return (this.maskingRequestId != null || other.maskingRequestId == null) &&
                (this.maskingRequestId == null ||
                        this.maskingRequestId.equals(other.maskingRequestId));
    }

    @Override
    public String toString() {
        return "org.jlab.srm.persistence.entity.MaskingRequest[ maskingRequestId=" + maskingRequestId +
                " ]";
    }

}
