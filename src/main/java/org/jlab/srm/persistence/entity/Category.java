package org.jlab.srm.persistence.entity;

import org.hibernate.envers.Audited;
import org.jlab.srm.persistence.model.Node;

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
@Audited
@Table(schema = "SRM_OWNER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"NAME"})})
@NamedQueries({
        @NamedQuery(name = "Category.findRoot", query = "SELECT c FROM Category c WHERE c.categoryId = 0")})
public class Category implements Serializable, Comparable<Category>, Node {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "CategoryId", sequenceName = "CATEGORY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CategoryId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CATEGORY_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger categoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 128)
    private String name;
    private BigInteger weight;
    @OneToMany(mappedBy = "parentId")
    @OrderBy("weight ASC, name ASC")
    private List<Category> categoryList;
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "CATEGORY_ID")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    //@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)    
    private Category parentId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category", fetch = FetchType.LAZY)
    @OrderBy("weight ASC, name ASC")
    private List<SystemEntity> systemList;

    public Category() {
    }

    public Category(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public Category(BigInteger categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
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

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public Category getParentId() {
        return parentId;
    }

    public void setParentId(Category parentId) {
        this.parentId = parentId;
    }

    public List<SystemEntity> getSystemList() {
        return systemList;
    }

    public void setSystemList(List<SystemEntity> systemList) {
        this.systemList = systemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        return (this.categoryId != null || other.categoryId == null) && (this.categoryId == null || this.categoryId.equals(other.categoryId));
    }

    @Override
    public String toString() {
        return "org.jlab.hco.persistence.entity.Category[ categoryId=" + categoryId + " ]";
    }

    @Override
    public int compareTo(Category c) {
        return getName().compareTo(c.getName()); //TODO: look at weight as well
    }

    @Override
    public BigInteger getId() {
        return getCategoryId();
    }

    @Override
    public List<? extends Node> getChildren() {
        return getCategoryList();
    }

}
