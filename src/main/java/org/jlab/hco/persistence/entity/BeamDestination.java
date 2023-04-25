package org.jlab.hco.persistence.entity;

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
@Table(name = "BEAM_DESTINATION", schema = "HCO_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"NAME"})})
@NamedQueries({
        @NamedQuery(name = "BeamDestination.findAll", query = "SELECT b FROM BeamDestination b")})
public class BeamDestination implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "BEAM_DESTINATION_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger beamDestinationId;
    @Size(max = 128)
    @Column(length = 128)
    private String name;
    private BigInteger weight;
    @Column(name = "TARGET_YN")
    @Size(min = 1, max = 1)
    @NotNull
    private String targetYn;
    @JoinTable(name = "COMPONENT_BEAM_DESTINATION", joinColumns = {
            @JoinColumn(name = "BEAM_DESTINATION_ID", referencedColumnName = "BEAM_DESTINATION_ID", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID", nullable = false)})
    @ManyToMany
    private List<Component> componentList;

    public BeamDestination() {
    }

    public BeamDestination(BigInteger beamDestinationId) {
        this.beamDestinationId = beamDestinationId;
    }

    public BigInteger getBeamDestinationId() {
        return beamDestinationId;
    }

    public void setBeamDestinationId(BigInteger beamDestinationId) {
        this.beamDestinationId = beamDestinationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    public boolean isTarget() {
        return "Y".equals(targetYn);
    }

    public void setTarget(boolean target) {
        this.targetYn = target ? "Y" : "N";
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
        hash += (beamDestinationId != null ? beamDestinationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BeamDestination)) {
            return false;
        }
        BeamDestination other = (BeamDestination) object;
        return (this.beamDestinationId != null || other.beamDestinationId == null) && (this.beamDestinationId == null || this.beamDestinationId.equals(other.beamDestinationId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.BeamDestination[ beamDestinationId=" + beamDestinationId + " ]";
    }
}
