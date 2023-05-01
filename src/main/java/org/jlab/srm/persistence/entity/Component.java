package org.jlab.srm.persistence.entity;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.jlab.srm.persistence.enumeration.DataSource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@Entity
@Audited
@Table(schema = "SRM_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"DATA_SOURCE"})})
@NamedQueries({
        @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c"),
        @NamedQuery(name = "Component.findStatus", query = "SELECT max(a.status.statusId) FROM GroupSignoff a WHERE a.component = :component")})
@SqlResultSetMapping(name = "ComponentStatus", columns = {@ColumnResult(name = "component_id"), @ColumnResult(name = "status_id")})
public class Component implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name = "ComponentId", sequenceName = "COMPONENT_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ComponentId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger componentId;
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
    @Basic(optional = true)
    @Column(name = "ADDED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;
    @Column(name = "MASKED")
    @Size(min = 1, max = 1)
    @NotNull
    private String maskedStr;
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
    @Basic(optional = true)
    @Column(name = "WEIGHT")
    private BigInteger weight;
    @ManyToMany(mappedBy = "componentList")
    @OrderBy("weight ASC")
    @NotAudited
    private List<BeamDestination> beamDestinationList;
    @JoinColumn(name = "REGION_ID", referencedColumnName = "REGION_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Region region;
    @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private SystemEntity system;
    @OneToMany(mappedBy = "component")
    @NotAudited
    private List<GroupSignoff> groupSignoffList;
    @Column(name = "UNPOWERED_YN")
    @Size(min = 1, max = 1)
    @NotNull
    private String unpoweredStr;
    @Basic(optional = true)
    @Column(name = "MASK_TYPE_ID")
    private Integer maskTypeId;
    @Size(max = 128)
    @Column(length = 128, name = "NAME_ALIAS")
    private String nameAlias;

    public Component() {
    }

    public Component(BigInteger componentId) {
        this.componentId = componentId;
    }

    public Component(BigInteger componentId, String name, DataSource dataSource) {
        this.componentId = componentId;
        this.name = name;
        this.dataSource = dataSource;
    }

    public Integer getMaskTypeId() {
        return maskTypeId;
    }

    public void setMaskTypeId(Integer maskTypeId) {
        this.maskTypeId = maskTypeId;
    }

    public boolean isUnpowered() {
        return "Y".equals(unpoweredStr);
    }

    public void setUnpowered(boolean unpowered) {
        this.unpoweredStr = unpowered ? "Y" : "N";
    }

    public boolean isMasked() {
        return "Y".equals(maskedStr);
    }

    public void setMasked(boolean masked) {
        this.maskedStr = masked ? "Y" : "N";
    }

    public Date getMaskExpirationDate() {
        return maskExpirationDate;
    }

    public void setMaskExpirationDate(Date maskExpirationDate) {
        this.maskExpirationDate = maskExpirationDate;
    }

    public String getMaskedComment() {
        return maskedComment;
    }

    public void setMaskedComment(String maskedComment) {
        this.maskedComment = maskedComment;
    }

    public Date getMaskedDate() {
        return maskedDate;
    }

    public void setMaskedDate(Date maskedDate) {
        this.maskedDate = maskedDate;
    }

    public String getMaskedBy() {
        return maskedBy;
    }

    public void setMaskedBy(String maskedBy) {
        this.maskedBy = maskedBy;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public void setComponentId(BigInteger componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BigInteger getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(BigInteger dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    public List<BeamDestination> getBeamDestinationList() {
        return beamDestinationList;
    }

    public void setBeamDestinationList(List<BeamDestination> beamDestinationList) {
        this.beamDestinationList = beamDestinationList;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public SystemEntity getSystem() {
        return system;
    }

    public void setSystem(SystemEntity system) {
        this.system = system;
    }

    public List<GroupSignoff> getGroupSignoffList() {
        return groupSignoffList;
    }

    public void setGroupSignoffList(List<GroupSignoff> groupSignoffList) {
        this.groupSignoffList = groupSignoffList;
    }

    public String getNameAlias() {
        return nameAlias;
    }

    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentId != null ? componentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        return (this.componentId != null || other.componentId == null) && (this.componentId == null || this.componentId.equals(other.componentId));
    }

    @Override
    public String toString() {
        return "org.jlab.srm.persistence.entity.Component[ componentId=" + componentId + " ]";
    }

}
