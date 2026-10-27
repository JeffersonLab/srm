package org.jlab.srm.presentation.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.TimeZone;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.HcoNodeType;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.jlab.srm.persistence.model.HcoNodeData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HcoFunctionsTest {

  @Test
  void formatComponentStarsUnpowered() {
    Component component = new Component(BigInteger.ONE, "IPM1L01", null);

    assertEquals("IPM1L01", HcoFunctions.formatComponent(component));

    component.setUnpowered(true);
    assertEquals("IPM1L01★", HcoFunctions.formatComponent(component));
  }

  @Test
  void formatFakeComponentStarsUnpowered() {
    assertEquals("X", HcoFunctions.formatFakeComponent("X", false));
    assertEquals("X★", HcoFunctions.formatFakeComponent("X", true));
  }

  @Test
  void formatBoolean() {
    assertEquals("", HcoFunctions.formatBoolean(null));
    assertEquals("Yes", HcoFunctions.formatBoolean(true));
    assertEquals("No", HcoFunctions.formatBoolean(false));
  }

  @Test
  void formatChangeType() {
    assertEquals("", HcoFunctions.formatChangeType(null));
    assertEquals("Upgrade", HcoFunctions.formatChangeType(SignoffChangeType.UPGRADE));
    assertEquals("Downgrade", HcoFunctions.formatChangeType(SignoffChangeType.DOWNGRADE));
    assertEquals("Cascade", HcoFunctions.formatChangeType(SignoffChangeType.CASCADE));
    assertEquals("Comment", HcoFunctions.formatChangeType(SignoffChangeType.COMMENT));
  }

  @Test
  void getStatusById() {
    assertNull(HcoFunctions.getStatusById(null));
    assertSame(Status.CHECKED, HcoFunctions.getStatusById(50));
  }

  @ParameterizedTest
  @CsvSource({
    "0, notapplicable",
    "1, ready",
    "50, checked",
    "100, not-ready",
    "150, masked",
    "200, exception",
    "250, tragedy",
    "999, unknown"
  })
  void getStatusClass(int id, String expected) {
    Status status = new Status(BigInteger.valueOf(id), "any");

    assertEquals(expected, HcoFunctions.getStatusClass(status));
  }

  @Test
  void defaultStatusIsNotReady() {
    assertSame(Status.NOT_READY, HcoFunctions.getDefaultStatus());
  }

  @Test
  void nodeUrlOnlyForComponents() {
    HcoNodeData component =
        new HcoNodeData(HcoNodeType.COMPONENT, BigInteger.valueOf(42), "c", Status.READY, false);
    HcoNodeData system =
        new HcoNodeData(HcoNodeType.SYSTEM, BigInteger.valueOf(42), "s", Status.READY, false);

    assertEquals("reports/component/detail?componentId=42", HcoFunctions.getNodeUrl(component));
    assertNull(HcoFunctions.getNodeUrl(system));
  }

  @Test
  void localTimeAddsZoneOffset() {
    Date date = new Date(1_700_000_000_000L);
    long offset = TimeZone.getDefault().getOffset(date.getTime());

    assertEquals(date.getTime() + offset, HcoFunctions.getLocalTime(date));
  }

  @Test
  void truncateAndUrlEncodeCedName() {
    assertEquals("", HcoFunctions.truncateAndUrlEncodeCedName(null));
    assertEquals("IPM1L01", HcoFunctions.truncateAndUrlEncodeCedName("IPM1L01 (old name)"));
    assertEquals("A%26B%2FC", HcoFunctions.truncateAndUrlEncodeCedName("A&B/C"));
  }

  @Test
  void hostnameFromNullIpIsNull() {
    assertNull(HcoFunctions.getHostnameFromIp(null));
  }
}
