package org.jlab.hco.persistence.entity.aud;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ryans
 */
@Embeddable
public class CategoryAudPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "CATEGORY_ID", nullable = false)
    private BigInteger categoryId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "REV", nullable = false)
    private BigInteger rev;

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public BigInteger getRev() {
        return rev;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.categoryId != null ? this.categoryId.hashCode() : 0);
        hash = 23 * hash + (this.rev != null ? this.rev.hashCode() : 0);
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
        final CategoryAudPK other = (CategoryAudPK) obj;
        if (this.categoryId != other.categoryId && (this.categoryId == null || !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        return this.rev == other.rev || (this.rev != null && this.rev.equals(other.rev));
    }
}
