package org.jlab.srm.presentation.params;

import static org.jlab.srm.presentation.params.OverallStatusUrlParamHandlerTest.ids;
import static org.jlab.srm.presentation.params.OverallStatusUrlParamHandlerTest.request;
import static org.jlab.srm.presentation.params.OverallStatusUrlParamHandlerTest.session;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.Map;
import org.jlab.srm.business.params.GroupStatusParams;
import org.junit.jupiter.api.Test;

/** The group status report's URL parameters and their session storage; no facades are needed. */
class GroupStatusUrlParamHandlerTest {

  private static final BigInteger[] NONE = new BigInteger[0];

  private static GroupStatusUrlParamHandler handler(HttpServletRequest request) {
    return new GroupStatusUrlParamHandler(request, ids(6));
  }

  @Test
  void convertsIncludedAndExcludedDestinations() {
    GroupStatusParams params =
        handler(
                request(
                    Map.of(
                        "destinationId", new String[] {"6"},
                        "excludeDestinationId", new String[] {"13", "28"},
                        "chart", new String[] {"bar"},
                        "qualified", new String[] {""}),
                    session()))
            .convert();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertArrayEquals(ids(13, 28), params.getExcludeDestinationIdArray());
    assertEquals("bar", params.getChart());
  }

  @Test
  void convertsNoExcludedDestinationsToNull() {
    GroupStatusParams params =
        handler(request(Map.of("destinationId", new String[] {"6"}), session())).convert();

    assertNull(params.getExcludeDestinationIdArray());
  }

  @Test
  void storesAndMaterializesExcludedDestinations() {
    HttpSession session = session();
    GroupStatusUrlParamHandler handler =
        handler(
            request(
                Map.of(
                    "destinationId", new String[] {"6"},
                    "excludeDestinationId", new String[] {"13", "28"}),
                session));

    handler.store(handler.convert());

    GroupStatusParams params = handler(request(Map.of(), session)).materialize();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertArrayEquals(ids(13, 28), params.getExcludeDestinationIdArray());
  }

  @Test
  void storesNoExcludedDestinationsAsEmpty() {
    HttpSession session = session();
    GroupStatusUrlParamHandler handler = handler(request(Map.of(), session));

    handler.store(handler.convert());

    assertArrayEquals(NONE, handler.materialize().getExcludeDestinationIdArray());
  }

  @Test
  void materializesDefaultsWhenNothingStored() {
    GroupStatusParams params = handler(request(Map.of(), session())).materialize();

    assertArrayEquals(ids(6), params.getDestinationIdArray());
    assertNull(params.getExcludeDestinationIdArray());
    assertEquals("table", params.getChart());
  }

  /** The two status reports share their filters through the session. */
  @Test
  void sharesExcludedDestinationsWithOverallStatus() {
    HttpSession session = session();
    GroupStatusUrlParamHandler handler =
        handler(request(Map.of("excludeDestinationId", new String[] {"13"}), session));

    handler.store(handler.convert());

    assertArrayEquals(
        ids(13),
        new OverallStatusUrlParamHandler(request(Map.of(), session), ids(6))
            .materialize()
            .getExcludeDestinationIdArray());
  }
}
