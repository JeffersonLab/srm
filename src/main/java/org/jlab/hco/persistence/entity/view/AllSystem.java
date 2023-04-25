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
@Table(name = "ALL_SYSTEMS", schema = "HCO_OWNER")
public class AllSystem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SYSTEM_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger systemId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    private BigInteger weight;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AllCategory category;

    public BigInteger getSystemId() {
        return systemId;
    }

    public String getName() {
        return name;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public AllCategory getCategory() {
        return category;
    }

}
