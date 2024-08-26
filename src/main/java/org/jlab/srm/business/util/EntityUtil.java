package org.jlab.srm.business.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.srm.persistence.entity.GroupSignoff;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
public final class EntityUtil {
  private EntityUtil() {
    // Cannot instantiate publicly
  }

  public static Map<ResponsibleGroup, GroupSignoff> getGroupSignoffMap(
      List<GroupSignoff> signoffList) {
    Map<ResponsibleGroup, GroupSignoff> map = new HashMap<ResponsibleGroup, GroupSignoff>();

    if (signoffList != null) {
      for (GroupSignoff signoff : signoffList) {
        map.put(signoff.getGroupResponsibility().getGroup(), signoff);
      }
    }

    return map;
  }
}
