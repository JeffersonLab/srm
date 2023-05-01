package org.jlab.srm.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@Entity
@Table(schema = "SRM_OWNER")
public class Region implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "REGION_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger regionId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    @Size(max = 128)
    @Column(length = 128)
    private String alias;
    private BigInteger weight;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
    private List<Component> componentList;

    public Region() {
    }

    public Region(BigInteger regionId) {
        this.regionId = regionId;
    }

    public Region(BigInteger regionId, String name) {
        this.regionId = regionId;
        this.name = name;
    }

    public BigInteger getRegionId() {
        return regionId;
    }

    public void setRegionId(BigInteger regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (regionId != null ? regionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Region)) {
            return false;
        }
        Region other = (Region) object;
        return (this.regionId != null || other.regionId == null) && (this.regionId == null || this.regionId.equals(other.regionId));
    }

    @Override
    public String toString() {
        return "org.jlab.srm.persistence.entity.Region[ regionId=" + regionId + " ]";
    }

}
