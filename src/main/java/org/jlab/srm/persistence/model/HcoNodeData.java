package org.jlab.srm.persistence.model;

import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.HcoNodeType;

import java.math.BigInteger;

/**
 * @author ryans
 */
public class HcoNodeData {
    private final HcoNodeType type;
    private final BigInteger id;
    private final String name;
    private final Status status;
    private final boolean lazyChildren;

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
