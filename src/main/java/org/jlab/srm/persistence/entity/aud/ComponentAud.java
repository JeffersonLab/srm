package org.jlab.srm.persistence.entity.aud;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.RevisionType;
import org.jlab.srm.persistence.entity.ApplicationRevisionInfo;
import org.jlab.srm.persistence.entity.Region;
import org.jlab.srm.persistence.entity.view.AllSystem;
import org.jlab.srm.persistence.enumeration.DataSource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

/**
 * @author ryans
 */
@Entity
@Table(name = "COMPONENT_AUD", schema = "SRM_OWNER")
public class ComponentAud implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComponentAudPK componentAudPK;
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Column(name = "REVTYPE")
    private RevisionType type;
    @JoinColumn(name = "REV", referencedColumnName = "REV", insertable = false, updatable = false, nullable = false)
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ApplicationRevisionInfo revision;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATA_SOURCE", nullable = false, length = 24)
    @Enumerated(EnumType.STRING)
    private DataSource dataSource;
    @Column(name = "DATA_SOURCE_ID")
    private BigInteger dataSourceId;
    @Column(name = "MASKED")
    @Size(min = 1, max = 1)
    @NotNull
    private String maskedStr;
    @Column(name = "MASK_TYPE_ID")
    private Integer maskTypeId;
    @Basic(optional = true)
    @Size(min = 0, max = 512)
    @Column(name = "MASKED_COMMENT", nullable = true, length = 512)
    private String maskedComment;
    @Basic(optional = true)
    @Column(name = "MASKED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date maskedDate;
    @Column(name = "MASKED_USERNAME", nullable = true, length = 64)
    private String maskedBy;
    @Basic(optional = true)
    @Column(name = "MASK_EXPIRATION_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date maskExpirationDate;
    @JoinColumn(name = "REGION_ID", referencedColumnName = "REGION_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Region region;
    @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AllSystem system;
    @Column(name = "UNPOWERED_YN")
    @Size(min = 1, max = 1)
    @NotNull
    private String unpoweredStr;

    public RevisionType getType() {
        return type;
    }

    public void setType(RevisionType type) {
        this.type = type;
    }

    public ApplicationRevisionInfo getRevision() {
        return revision;
    }

    public void setRevision(ApplicationRevisionInfo revision) {
        this.revision = revision;
    }

    public ComponentAudPK getComponentAudPK() {
        return componentAudPK;
    }

    public void setComponentAudPK(ComponentAudPK componentAudPK) {
        this.componentAudPK = componentAudPK;
    }

    public String getName() {
        return name;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public BigInteger getDataSourceId() {
        return dataSourceId;
    }

    public String getMaskedStr() {
        return maskedStr;
    }

    public String getMaskedComment() {
        return maskedComment;
    }

    public Date getMaskedDate() {
        return maskedDate;
    }

    public String getMaskedBy() {
        return maskedBy;
    }

    public Date getMaskExpirationDate() {
        return maskExpirationDate;
    }

    public void setMaskExpirationDate(Date maskExpirationDate) {
        this.maskExpirationDate = maskExpirationDate;
    }

    public Region getRegion() {
        return region;
    }

    public AllSystem getSystem() {
        return system;
    }

    public String getUnpoweredStr() {
        return unpoweredStr;
    }

    public Integer getMaskTypeId() {
        return maskTypeId;
    }

    public void setMaskTypeId(Integer maskTypeId) {
        this.maskTypeId = maskTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.componentAudPK != null ? this.componentAudPK.hashCode() : 0);
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
        final ComponentAud other = (ComponentAud) obj;
        return Objects.equals(this.componentAudPK, other.componentAudPK);
    }
}
