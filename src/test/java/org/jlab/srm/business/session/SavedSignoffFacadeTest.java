package org.jlab.srm.business.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.srm.persistence.entity.SavedSignoff;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.junit.jupiter.api.Test;

/** Saved signoffs applied together must be on different systems. */
class SavedSignoffFacadeTest {

  @Test
  void differentSystemsAreAllowed() {
    assertNull(SavedSignoffFacade.duplicateSystemsMessage(saved("Beam Dump", "Vacuum", "RF")));
  }

  @Test
  void namesEachSystemSelectedMoreThanOnceWithItsCount() {
    assertEquals(
        "Only one saved signoff rule per System per bulk execution. Systems selected more than"
            + " once: CLAS12 ALERT AHDC (2), Vacuum (3)",
        SavedSignoffFacade.duplicateSystemsMessage(
            saved("Vacuum", "CLAS12 ALERT AHDC", "RF", "Vacuum", "CLAS12 ALERT AHDC", "Vacuum")));
  }

  private static List<SavedSignoff> saved(String... systemNames) {
    List<SavedSignoff> list = new ArrayList<>();

    for (int i = 0; i < systemNames.length; i++) {
      SavedSignoff saved = new SavedSignoff();
      saved.setSystem(new SystemEntity(BigInteger.valueOf(i), systemNames[i]));
      list.add(saved);
    }

    return list;
  }
}
