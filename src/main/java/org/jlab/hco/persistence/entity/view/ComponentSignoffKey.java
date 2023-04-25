package org.jlab.hco.persistence.entity.view;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author ryans
 */
public class ComponentSignoffKey implements Serializable {
    private BigInteger componentId;
    private BigInteger groupId;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.componentId);
        hash = 37 * hash + Objects.hashCode(this.groupId);
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
        final ComponentSignoffKey other = (ComponentSignoffKey) obj;
        if (!Objects.equals(this.componentId, other.componentId)) {
            return false;
        }
        return Objects.equals(this.groupId, other.groupId);
    }
}
