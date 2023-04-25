package org.jlab.hco.persistence.entity.view;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ryans
 */
@Entity
@Table(name = "ALL_CATEGORIES", schema = "HCO_OWNER")
public class AllCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CATEGORY_ID", nullable = false)
    private BigInteger categoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    private BigInteger weight;
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "CATEGORY_ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private AllCategory parent;

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public AllCategory getParent() {
        return parent;
    }


}
