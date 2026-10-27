package org.jlab.srm.persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class StatusTest {

  private static Status status(int id) {
    return Status.FROM_ID(BigInteger.valueOf(id));
  }

  @Test
  void fromIdReturnsConstants() {
    assertSame(Status.NOT_APPLICABLE, status(0));
    assertSame(Status.READY, status(1));
    assertSame(Status.CHECKED, status(50));
    assertSame(Status.NOT_READY, status(100));
    assertSame(Status.MASKED, status(150));
    assertSame(Status.MASKED_CC, status(200));
    assertSame(Status.MASKED_ADMIN, status(250));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 2, 99, 300})
  void fromIdRejectsUnknownId(int id) {
    assertThrows(IllegalArgumentException.class, () -> status(id));
  }

  @Test
  void maxOfNullOrEmptyIsReady() {
    assertSame(Status.READY, Status.MAX(null));
    assertSame(Status.READY, Status.MAX(Collections.emptyList()));
  }

  @Test
  void maxReturnsHighestId() {
    List<Status> statuses = Arrays.asList(Status.READY, Status.NOT_READY, Status.CHECKED);

    assertSame(Status.NOT_READY, Status.MAX(statuses));
    assertSame(
        Status.MASKED_ADMIN, Status.MAX(Arrays.asList(Status.MASKED_ADMIN, Status.NOT_READY)));
  }

  @Test
  void maxIgnoresNotApplicableBelowReady() {
    assertSame(Status.READY, Status.MAX(Collections.singletonList(Status.NOT_APPLICABLE)));
  }

  @ParameterizedTest(name = "{0} -> {1} is {2}")
  @CsvSource({
    "1, 1, COMMENT",
    "100, 100, COMMENT",
    "1, 0, UPGRADE",
    "100, 0, UPGRADE",
    "0, 1, DOWNGRADE",
    "0, 100, DOWNGRADE",
    "100, 1, UPGRADE",
    "50, 1, UPGRADE",
    "1, 50, DOWNGRADE",
    "100, 50, UPGRADE",
    "1, 100, DOWNGRADE",
    "50, 100, DOWNGRADE"
  })
  void change(int before, int after, SignoffChangeType expected) {
    assertEquals(expected, Status.CHANGE(status(before), status(after)));
  }

  @Test
  void maskType() {
    assertEquals("Director", Status.MASKED.getMaskType());
    assertEquals("Crew Chief", Status.MASKED_CC.getMaskType());
    assertEquals("Administrator", Status.MASKED_ADMIN.getMaskType());
    assertNull(Status.READY.getMaskType());
    assertNull(Status.NOT_READY.getMaskType());
  }

  @Test
  void equalityIsById() {
    Status copy = new Status(BigInteger.valueOf(1), "Different name");

    assertEquals(Status.READY, copy);
    assertEquals(Status.READY.hashCode(), copy.hashCode());
    assertNotEquals(Status.READY, Status.CHECKED);
    assertNotEquals(Status.READY, "Ready");
  }
}
