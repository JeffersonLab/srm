package org.jlab.srm.business.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import org.jlab.srm.persistence.entity.GroupResponsibility;
import org.jlab.srm.persistence.entity.GroupSignoff;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.junit.jupiter.api.Test;

class EntityUtilTest {

  private static GroupSignoff signoff(ResponsibleGroup group) {
    GroupResponsibility responsibility = new GroupResponsibility();
    responsibility.setGroup(group);

    GroupSignoff signoff = new GroupSignoff();
    signoff.setGroupResponsibility(responsibility);
    return signoff;
  }

  @Test
  void nullListGivesEmptyMap() {
    assertTrue(EntityUtil.getGroupSignoffMap(null).isEmpty());
  }

  @Test
  void mapsEachSignoffByItsGroup() {
    ResponsibleGroup groupA = new ResponsibleGroup(BigInteger.ONE, "A");
    ResponsibleGroup groupB = new ResponsibleGroup(BigInteger.TWO, "B");
    GroupSignoff signoffA = signoff(groupA);
    GroupSignoff signoffB = signoff(groupB);

    Map<ResponsibleGroup, GroupSignoff> map =
        EntityUtil.getGroupSignoffMap(Arrays.asList(signoffA, signoffB));

    assertEquals(2, map.size());
    assertSame(signoffA, map.get(groupA));
    assertSame(signoffB, map.get(new ResponsibleGroup(BigInteger.TWO)));
  }

  @Test
  void laterSignoffForSameGroupWins() {
    GroupSignoff first = signoff(new ResponsibleGroup(BigInteger.ONE, "A"));
    GroupSignoff second = signoff(new ResponsibleGroup(BigInteger.ONE, "A"));

    Map<ResponsibleGroup, GroupSignoff> map =
        EntityUtil.getGroupSignoffMap(Arrays.asList(first, second));

    assertEquals(1, map.size());
    assertSame(second, map.values().iterator().next());
  }
}
