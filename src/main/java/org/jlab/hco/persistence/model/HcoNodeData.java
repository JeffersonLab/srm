package org.jlab.hco.persistence.model;

import org.jlab.hco.persistence.entity.Status;
import org.jlab.hco.persistence.enumeration.HcoNodeType;

import java.math.BigInteger;

/**
 * @author ryans
 */
public class HcoNodeData {
    private HcoNodeType type;
    private BigInteger id;
    private String name;
    private Status status;
    private boolean lazyChildren;

    public HcoNodeData(HcoNodeType type, BigInteger id, String name, Status status, boolean lazyChildren) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.status = status;
        this.lazyChildren = lazyChildren;
    }

    public String getName() {
        return name;
    }

    public BigInteger getId() {
        return id;
    }

    public HcoNodeType getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isLazyChildren() {
        return lazyChildren;
    }
}
