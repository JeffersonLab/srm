package org.jlab.srm.persistence.params;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jlab.srm.business.params.RecentActivityParams;
import org.junit.jupiter.api.Test;

/** The recent activity SQL filters and their placeholders, which must match in order. */
class RecentActivityQueryParamHandlerTest {

  private static final String EXCLUDE =
      "a.component_id not in (select component_id from component_beam_destination where"
          + " beam_destination_id in (?,?))";

  private static BigInteger[] ids(long... values) {
    BigInteger[] array = new BigInteger[values.length];
    for (int i = 0; i < values.length; i++) {
      array[i] = BigInteger.valueOf(values[i]);
    }
    return array;
  }

  /** The values assigned to a statement's placeholders, by placeholder index. */
  private static Map<Integer, BigDecimal> assigned(RecentActivityQueryParamHandler handler)
      throws Exception {
    Map<Integer, BigDecimal> values = new TreeMap<>();
    PreparedStatement stmt =
        (PreparedStatement)
            Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class<?>[] {PreparedStatement.class},
                (proxy, method, args) -> {
                  if ("setBigDecimal".equals(method.getName())) {
                    values.put((Integer) args[0], (BigDecimal) args[1]);
                    return null;
                  }
                  throw new UnsupportedOperationException(method.getName());
                });

    handler.assignParameterValues(stmt);

    return values;
  }

  private static long placeholders(String sql) {
    return sql.chars().filter(c -> c == '?').count();
  }

  @Test
  void excludedDestinationsFollowIncludedOnes() throws Exception {
    RecentActivityParams params =
        new RecentActivityParams(ids(6), ids(141), BigInteger.ONE, BigInteger.TWO, ids(2, 3));
    params.setExcludeDestinationIdArray(ids(13, 28));
    RecentActivityQueryParamHandler handler = new RecentActivityQueryParamHandler(params);

    String where = handler.getSqlWhereClause();
    Map<Integer, BigDecimal> values = assigned(handler);

    assertTrue(where.contains(EXCLUDE), where);
    assertTrue(where.indexOf("beam_destination_id in (?)") < where.indexOf(EXCLUDE), where);
    assertEquals(placeholders(where), values.size(), where);
    assertEquals(
        List.of(6L, 13L, 28L, 141L, 1L, 2L, 2L, 3L),
        values.values().stream().map(BigDecimal::longValueExact).toList(),
        values.toString());
  }

  @Test
  void excludedDestinationsAlone() throws Exception {
    RecentActivityParams params = new RecentActivityParams(null, null, null, null, null);
    params.setExcludeDestinationIdArray(ids(13, 28));
    RecentActivityQueryParamHandler handler = new RecentActivityQueryParamHandler(params);

    String where = handler.getSqlWhereClause();

    assertEquals("where " + EXCLUDE + "  ", where);
    assertEquals(List.of(1, 2), List.copyOf(assigned(handler).keySet()));
  }

  @Test
  void noExcludedDestinationsLeavesSqlAlone() throws Exception {
    RecentActivityParams params = new RecentActivityParams(ids(6), null, null, null, null);
    RecentActivityQueryParamHandler handler = new RecentActivityQueryParamHandler(params);

    String where = handler.getSqlWhereClause();

    assertFalse(where.contains("not in"), where);
    assertEquals(1, assigned(handler).size());
  }
}
