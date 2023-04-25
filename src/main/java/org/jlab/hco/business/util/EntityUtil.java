package org.jlab.hco.business.util;

import org.jlab.hco.persistence.entity.GroupSignoff;
import org.jlab.hco.persistence.entity.ResponsibleGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ryans
 */
public final class EntityUtil {
    private EntityUtil() {
        // Cannot instantiate publicly
    }

    public static Map<ResponsibleGroup, GroupSignoff> getGroupSignoffMap(List<GroupSignoff> signoffList) {
        Map<ResponsibleGroup, GroupSignoff> map = new HashMap<ResponsibleGroup, GroupSignoff>();

        if (signoffList != null) {
            for (GroupSignoff signoff : signoffList) {
                map.put(signoff.getGroupResponsibility().getGroup(), signoff);
            }
        }

        return map;
    }
}
