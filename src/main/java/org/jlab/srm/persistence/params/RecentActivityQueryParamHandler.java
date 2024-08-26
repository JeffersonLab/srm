package org.jlab.srm.persistence.params;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jlab.srm.business.params.RecentActivityParams;

/**
 * @author ryans
 */
public final class RecentActivityQueryParamHandler implements QueryParamHandler {

  private final RecentActivityParams params;

  public RecentActivityQueryParamHandler(RecentActivityParams params) {
    this.params = params;
  }

  @Override
  public String getSqlWhereClause() {
    String filter = "";

    List<String> filters = new ArrayList<>();

    if (params.getDestinationIdArray() != null && params.getDestinationIdArray().length > 0) {
      String subFilter =
          "a.component_id in (select component_id from component_beam_destination where beam_destination_id in (?";
      for (int i = 1; i < params.getDestinationIdArray().length; i++) {
        subFilter = subFilter + ",?";
      }
      subFilter = subFilter + ")) ";
      filters.add(subFilter);
    }

    if (params.getSystemIdArray() != null && params.getSystemIdArray().length > 0) {
      String subFilter = "b.system_id in (?";
      for (int i = 1; i < params.getSystemIdArray().length; i++) {
        subFilter = subFilter + ",?";
      }
      subFilter = subFilter + ") ";
      filters.add(subFilter);
    }

    if (params.getRegionId() != null) {
      filters.add("b.region_id = ?");
    }

    if (params.getGroupId() != null) {
      filters.add("a.group_id = ?");
    }

    if (params.getStatusIdArray() != null && params.getStatusIdArray().length > 0) {
      String subFilter = "a.status_id in (?";
      for (int i = 1; i < params.getStatusIdArray().length; i++) {
        subFilter = subFilter + ",?";
      }
      subFilter = subFilter + ") ";
      filters.add(subFilter);
    }

    if (!filters.isEmpty()) {
      filter = "where " + filters.get(0) + " ";

      if (filters.size() > 1) {
        for (int i = 1; i < filters.size(); i++) {
          filter = filter + "and " + filters.get(i) + " ";
        }
      }
    }

    return filter;
  }

  @Override
  public void assignParameterValues(PreparedStatement stmt) throws SQLException {
    int i = 1;

    if (params.getDestinationIdArray() != null) {
      for (BigInteger destinationId : params.getDestinationIdArray()) {
        stmt.setBigDecimal(i++, new BigDecimal(destinationId));
      }
    }

    if (params.getSystemIdArray() != null) {
      for (BigInteger systemId : params.getSystemIdArray()) {
        stmt.setBigDecimal(i++, new BigDecimal(systemId));
      }
    }

    if (params.getRegionId() != null) {
      stmt.setBigDecimal(i++, new BigDecimal(params.getRegionId()));
    }

    if (params.getGroupId() != null) {
      stmt.setBigDecimal(i++, new BigDecimal(params.getGroupId()));
    }

    if (params.getStatusIdArray() != null) {
      for (BigInteger statusId : params.getStatusIdArray()) {
        stmt.setBigDecimal(i++, new BigDecimal(statusId));
      }
    }
  }
}
