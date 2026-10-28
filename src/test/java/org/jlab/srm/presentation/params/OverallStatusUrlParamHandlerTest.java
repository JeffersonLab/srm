package org.jlab.srm.presentation.params;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.jlab.srm.business.params.OverallStatusParams;
import org.junit.jupiter.api.Test;

/** The overall status report's URL parameters and their session storage; no facades are needed. */
class OverallStatusUrlParamHandlerTest {

  private static final BigInteger[] NONE = new BigInteger[0];

  /** A session that only keeps attributes. */
  static HttpSession session() {
    Map<String, Object> attributes = new HashMap<>();
    return (HttpSession)
        Proxy.newProxyInstance(
            HttpSession.class.getClassLoader(),
            new Class<?>[] {HttpSession.class},
            (proxy, method, args) -> {
              switch (method.getName()) {
                case "getAttribute":
                  return attributes.get((String) args[0]);
                case "setAttribute":
                  attributes.put((String) args[0], args[1]);
                  return null;
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }

  /** A request with the given parameters and session. */
  static HttpServletRequest request(Map<String, String[]> params, HttpSession session) {
    return (HttpServletRequest)
        Proxy.newProxyInstance(
            HttpServletRequest.class.getClassLoader(),
            new Class<?>[] {HttpServletRequest.class},
            (proxy, method, args) -> {
              switch (method.getName()) {
                case "getParameter":
                  String[] values = params.get((String) args[0]);
                  return values == null || values.length == 0 ? null : values[0];
                case "getParameterValues":
                  return params.get((String) args[0]);
                case "getSession":
                  return session;
                default:
                  throw new UnsupportedOperationException(method.getName());
              }
            });
  }

  static BigInteger[] ids(long... values) {
    BigInteger[] array = new BigInteger[values.length];
    for (int i = 0; i < values.length; i++) {
      array[i] = BigInteger.valueOf(values[i]);
    }
    return array;
  }

  private static OverallStatusUrlParamHandler handler(HttpServletRequest request) {
    return new OverallStatusUrlParamHandler(request, ids(6));
  }

  @Test
  void convertsIncludedAndExcludedDestinations() {
    OverallStatusParams params =
        handler(
                request(
                    Map.of(
                        "destinationId", new String[] {"6"},
                        "excludeDestinationId", new String[] {"13", "28"},
                        "groupId", new String[] {"4"},
                        "qualified", new String[] {""}),
                    session()))
            .convert();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertArrayEquals(ids(13, 28), params.getExcludeDestinationIdArray());
    assertEquals(BigInteger.valueOf(4), params.getGroupId());
  }

  @Test
  void convertsNoExcludedDestinationsToNull() {
    OverallStatusParams params =
        handler(request(Map.of("destinationId", new String[] {"6"}), session())).convert();

    assertNull(params.getExcludeDestinationIdArray());
  }

  @Test
  void storesAndMaterializesExcludedDestinations() {
    HttpSession session = session();
    OverallStatusUrlParamHandler handler =
        handler(
            request(
                Map.of(
                    "destinationId", new String[] {"6"},
                    "excludeDestinationId", new String[] {"13", "28"}),
                session));

    handler.store(handler.convert());

    OverallStatusParams params = handler(request(Map.of(), session)).materialize();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertArrayEquals(ids(13, 28), params.getExcludeDestinationIdArray());
  }

  @Test
  void storesNoExcludedDestinationsAsEmpty() {
    HttpSession session = session();
    OverallStatusUrlParamHandler handler = handler(request(Map.of(), session));

    handler.store(handler.convert());

    assertArrayEquals(NONE, handler.materialize().getExcludeDestinationIdArray());
  }

  @Test
  void materializesDefaultsWhenNothingStored() {
    OverallStatusParams params = handler(request(Map.of(), session())).materialize();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertNull(params.getExcludeDestinationIdArray());
  }

  /** The two status reports share their filters through the session. */
  @Test
  void sharesExcludedDestinationsWithGroupStatus() {
    HttpSession session = session();
    OverallStatusUrlParamHandler handler =
        handler(request(Map.of("excludeDestinationId", new String[] {"13"}), session));

    handler.store(handler.convert());

    assertArrayEquals(
        ids(13),
        new GroupStatusUrlParamHandler(request(Map.of(), session), ids(6))
            .materialize()
            .getExcludeDestinationIdArray());
  }
}
