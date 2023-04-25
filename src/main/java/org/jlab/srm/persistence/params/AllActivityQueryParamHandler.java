package org.jlab.srm.persistence.params;

import org.jlab.srm.business.params.AllActivityParams;
import org.jlab.srm.persistence.enumeration.AllChangeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryans
 */
public final class AllActivityQueryParamHandler implements QueryParamHandler {

    private final AllActivityParams params;

    public AllActivityQueryParamHandler(AllActivityParams params) {
        this.params = params;
    }

    @Override
    public String getSqlWhereClause() {
        String filter = "";

        List<String> filters = new ArrayList<>();

        if (params.getStart() != null) {
            filters.add("modified_date >= ?");
        }

        if (params.getEnd() != null) {
            filters.add("modified_date < ?");
        }

        if (params.getDestinationIdArray() != null && params.getDestinationIdArray().length > 0) {
            String subFilter = "component_id in (select component_id from component_beam_destination where beam_destination_id in (?";
            for (int i = 1; i < params.getDestinationIdArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ")) ";
            filters.add(subFilter);
        }

        if (params.getSystemIdArray() != null && params.getSystemIdArray().length > 0) {
            String subFilter = "system_id in (?";
            for (int i = 1; i < params.getSystemIdArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ") ";
            filters.add(subFilter);
        }

        if (params.getRegionId() != null) {
            filters.add("region_id = ?");
        }

        /**
         * Two cases: (1) signoff_activity and group_id is not null (some group
         * did the signoff) (2) inventory_activity and group_id is null (a
         * component/system/category changed)
         *
         * If first case then just check if signoff was done by group (group_id
         * = ?) If second case then check if group is responsible for system in
         * question (note: category changes are IGNORED by group filter as
         * written)
         */
        if (params.getGroupId() != null) {
            filters.add("((group_id is null and system_id in (select system_id from group_responsibility where group_id = ?)) or group_id = ?)");
        }

        if (params.getUsername() != null && !params.getUsername().isEmpty()) {
            filters.add("username = ?");
        }

        if (params.getComponentName() != null && !params.getComponentName().isEmpty()) {
            filters.add("component_name like ?");
        }

        if (params.getChangeArray() != null && params.getChangeArray().length > 0) {
            String subFilter = "change_type in (?";
            for (int i = 1; i < params.getChangeArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ") ";
            filters.add(subFilter);
        }

        /**
         * Signoff status filter
         *
         * Two cases: (1) signoff_activity and status_id is not null (some group
         * did the signoff) (2) inventory_activity and status_id is null (a
         * component/system/category changed)
         *
         * If first case then just check if signoffs include status (status_id
         * in ?,...) If second case then need "or status_id is null" so that
         * inventory items are included
         */
        if (params.getStatusIdArray() != null && params.getStatusIdArray().length > 0) {
            String subFilter = "(status_id is null or status_id in (?";
            for (int i = 1; i < params.getStatusIdArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ")) ";
            filters.add(subFilter);
        }

        /**
         * Component/Node status filter
         *
         * This allows us to filter out everything but Not Ready or Checked
         * components for example (so we can ignore activity on components that
         * are now Ready)
         *
         * NOTE: System and Category changes are ignored when the component
         * status filter is used as written
         */
        if (params.getCurrentComponentStatusIdArray() != null && params.getCurrentComponentStatusIdArray().length > 0) {
            String subFilter = "component_id in (select component_id from component_status_2 where status_id in (?";
            for (int i = 1; i < params.getCurrentComponentStatusIdArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ")) ";
            filters.add(subFilter);
        }

        /**
         * Current group signoff status filter
         *
         * This allows us to filter out everything but Not Ready or Checked
         * group signoff for example (so we can ignore activity on signoff that
         * are now Ready)
         *
         * NOTE: A group MUST be specified or the signoff is ambigous
         *
         * NOTE: System and Category changes are ignored when the component
         * status filter is used as written
         */
        if (params.getGroupId() != null && params.getCurrentSignoffStatusIdArray() != null && params.getCurrentSignoffStatusIdArray().length > 0) {
            String subFilter = "component_id in (select component_id from group_signoff where group_id = ? and status_id in (?";
            for (int i = 1; i < params.getCurrentSignoffStatusIdArray().length; i++) {
                subFilter = subFilter + ",?";
            }
            subFilter = subFilter + ")) ";
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

        int loopCounter = 1;
        do {
            if (params.getStart() != null) {
                java.sql.Date startSql = new java.sql.Date(params.getStart().getTime());
                stmt.setDate(i++, startSql);
            }

            if (params.getEnd() != null) {
                java.sql.Date endSql = new java.sql.Date(params.getEnd().getTime());
                stmt.setDate(i++, endSql);
            }

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
                stmt.setBigDecimal(i++, new BigDecimal(params.getGroupId()));
            }

            if (params.getUsername() != null && !params.getUsername().isEmpty()) {
                stmt.setString(i++, params.getUsername());
            }

            if (params.getComponentName() != null && !params.getComponentName().isEmpty()) {
                stmt.setString(i++, params.getComponentName());
            }

            if (params.getChangeArray() != null) {
                for (AllChangeType change : params.getChangeArray()) {
                    stmt.setString(i++, change.name());
                }
            }

            if (params.getStatusIdArray() != null) {
                for (BigInteger statusId : params.getStatusIdArray()) {
                    stmt.setBigDecimal(i++, new BigDecimal(statusId));
                }
            }

            if (params.getCurrentComponentStatusIdArray() != null) {
                for (BigInteger statusId : params.getCurrentComponentStatusIdArray()) {
                    stmt.setBigDecimal(i++, new BigDecimal(statusId));
                }
            }

            if (params.getGroupId() != null && params.getCurrentSignoffStatusIdArray() != null) {
                stmt.setBigDecimal(i++, new BigDecimal(params.getGroupId()));
                for (BigInteger statusId : params.getCurrentSignoffStatusIdArray()) {
                    stmt.setBigDecimal(i++, new BigDecimal(statusId));
                }
            }
            loopCounter++;
        } while (loopCounter < 3); // Loop twice since we use where clause twice
    }

}
