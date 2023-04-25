package org.jlab.hco.persistence.entity.aud;

import org.hibernate.envers.RevisionType;
import org.jlab.hco.persistence.entity.ApplicationRevisionInfo;
import org.jlab.hco.persistence.entity.view.AllCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author ryans
 */
@Entity
@Table(name = "CATEGORY_AUD", schema = "HCO_OWNER")
public class CategoryAud implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CategoryAudPK categoryAudPK;
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
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "CATEGORY_ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private AllCategory parent;

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

    public CategoryAudPK getCategoryAudPK() {
        return categoryAudPK;
    }

    public String getName() {
        return name;
    }

    public AllCategory getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.categoryAudPK != null ? this.categoryAudPK.hashCode() : 0);
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
        final CategoryAud other = (CategoryAud) obj;
        return this.categoryAudPK == other.categoryAudPK || (this.categoryAudPK != null && this.categoryAudPK.equals(other.categoryAudPK));
    }
}
