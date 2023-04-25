package org.jlab.srm.persistence.entity.aud;

import org.hibernate.envers.RevisionType;
import org.jlab.srm.persistence.entity.ApplicationRevisionInfo;
import org.jlab.srm.persistence.entity.view.AllCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author ryans
 */
@Entity
@Table(name = "SYSTEM_AUD", schema = "HCO_OWNER")
public class SystemAud implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SystemAudPK systemAudPK;
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
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AllCategory category;

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

    public SystemAudPK getSystemAudPK() {
        return systemAudPK;
    }

    public void setSystemAudPK(SystemAudPK systemAudPK) {
        this.systemAudPK = systemAudPK;
    }

    public String getName() {
        return name;
    }

    public AllCategory getCategory() {
        return category;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.systemAudPK != null ? this.systemAudPK.hashCode() : 0);
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
        final SystemAud other = (SystemAud) obj;
        return Objects.equals(this.systemAudPK, other.systemAudPK);
    }
}
