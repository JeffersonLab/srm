package org.jlab.srm.presentation.util;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.jlab.srm.persistence.enumeration.AllChangeType;
import org.jlab.srm.persistence.enumeration.DataSource;
import org.jlab.srm.persistence.enumeration.HcoNodeType;
import org.jlab.srm.persistence.enumeration.MaskingRequestStatus;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.junit.jupiter.api.Test;

class HcoParamConverterTest {

  /** A request that answers only getParameter and getParameterValues, from the given map. */
  private static HttpServletRequest request(Map<String, String[]> params) {
    return (HttpServletRequest)
        Proxy.newProxyInstance(
            HttpServletRequest.class.getClassLoader(),
            new Class<?>[] {HttpServletRequest.class},
            (proxy, method, args) -> {
              String[] values = params.get((String) args[0]);
              switch (method.getName()) {
                case "getParameter":
                  return values == null || values.length == 0 ? null : values[0];
                case "getParameterValues":
                  return values;
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }

  private static HttpServletRequest request(String name, String... values) {
    return request(Map.of(name, values));
  }

  private static Date date(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month, day);
    return cal.getTime();
  }

  @Test
  void missingOrEmptyParametersAreNull() throws ParseException {
    HttpServletRequest missing = request(Map.of());
    HttpServletRequest empty = request("p", "");

    for (HttpServletRequest r : new HttpServletRequest[] {missing, empty}) {
      assertNull(HcoParamConverter.convertDataSource(r, "p"));
      assertNull(HcoParamConverter.convertMaskingRequestStatus(r, "p"));
      assertNull(HcoParamConverter.convertAllChangeType(r, "p"));
      assertNull(HcoParamConverter.convertAllChangeTypeArray(r, "p"));
      assertNull(HcoParamConverter.convertChangeType(r, "p"));
      assertNull(HcoParamConverter.convertJLabDate(r, "p"));
      assertNull(HcoParamConverter.convertISO8601Date(r, "p"));
      assertNull(HcoParamConverter.convertHcoNodeType(r, "p"));
    }
  }

  @Test
  void convertsEnums() {
    assertEquals(DataSource.LED, HcoParamConverter.convertDataSource(request("p", "LED"), "p"));
    assertEquals(
        MaskingRequestStatus.PENDING,
        HcoParamConverter.convertMaskingRequestStatus(request("p", "PENDING"), "p"));
    assertEquals(
        AllChangeType.ADD_SYSTEM,
        HcoParamConverter.convertAllChangeType(request("p", "ADD_SYSTEM"), "p"));
    assertEquals(
        SignoffChangeType.CASCADE,
        HcoParamConverter.convertChangeType(request("p", "CASCADE"), "p"));
    assertEquals(
        HcoNodeType.GROUP, HcoParamConverter.convertHcoNodeType(request("p", "GROUP"), "p"));
  }

  @Test
  void unknownEnumValueIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HcoParamConverter.convertDataSource(request("p", "led"), "p"));
  }

  @Test
  void allChangeTypeArraySkipsEmptyValues() {
    AllChangeType[] types =
        HcoParamConverter.convertAllChangeTypeArray(request("p", "MASK", "", "UNMASK"), "p");

    assertArrayEquals(new AllChangeType[] {AllChangeType.MASK, AllChangeType.UNMASK}, types);
    assertNull(HcoParamConverter.convertAllChangeTypeArray(request("p", "", ""), "p"));
  }

  @Test
  void convertsDates() throws ParseException {
    assertEquals(
        date(2025, Calendar.MARCH, 7),
        HcoParamConverter.convertJLabDate(request("p", "07-Mar-2025"), "p"));
    assertEquals(
        date(2025, Calendar.MARCH, 7),
        HcoParamConverter.convertISO8601Date(request("p", "2025-03-07"), "p"));
  }

  @Test
  void malformedDateIsRejected() {
    assertThrows(
        ParseException.class,
        () -> HcoParamConverter.convertJLabDate(request("p", "2025-03-07"), "p"));
  }
}
