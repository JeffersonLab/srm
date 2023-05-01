package org.jlab.srm.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * @author ryans
 */
@Entity
@Table(schema = "SRM_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"NAME"})})
public class Application implements Serializable {

    /* We cache all possible values for ease of use and performance */
    @Transient
    public static final Application READINESS = new Application(BigInteger.valueOf(1L), "Readiness");
    @Transient
    public static final Application DOWNTIME = new Application(BigInteger.valueOf(2L), "Downtime");
    @Transient
    public static final Application PROBLEM = new Application(BigInteger.valueOf(3L), "Problem Report");
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "APPLICATION_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger applicationId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    @JoinTable(name = "SYSTEM_APPLICATION", joinColumns = {
            @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "SYSTEM_ID", referencedColumnName = "SYSTEM_ID", nullable = false)})
    @ManyToMany
    private List<SystemEntity> systemList;

    public Application() {
    }

    public Application(BigInteger applicationId, String name) {
        this.applicationId = applicationId;
        this.name = name;
    }

    public static Application FROM_ID(BigInteger id) {
        Application status;
        switch (id.intValue()) {
            case 1:
                status = Application.READINESS;
                break;
            case 2:
                status = Application.DOWNTIME;
                break;
            case 3:
                status = Application.PROBLEM;
                break;
            default:
                throw new IllegalArgumentException("ApplicationId must be one of 1, 2, 3");
        }
        return status;
    }

    public BigInteger getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(BigInteger applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SystemEntity> getSystemList() {
        return systemList;
    }

    public void setSystemList(List<SystemEntity> systemList) {
        this.systemList = systemList;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.applicationId != null ? this.applicationId.hashCode() : 0);
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
        final Application other = (Application) obj;
        return Objects.equals(this.applicationId, other.applicationId);
    }
}
