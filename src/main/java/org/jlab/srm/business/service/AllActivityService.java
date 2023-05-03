package org.jlab.srm.business.service;

import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.srm.business.params.AllActivityParams;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.AllChangeType;
import org.jlab.srm.persistence.model.AllActivityRecord;
import org.jlab.srm.persistence.params.AllActivityQueryParamHandler;
import org.jlab.srm.persistence.util.HcoSqlUtil;
import org.jlab.smoothness.business.util.IOUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
public class AllActivityService {

    private static final Logger LOGGER = Logger.getLogger(AllActivityService.class.getName());

    public List<AllActivityRecord> filterList(
            AllActivityParams params, int offset, int max) throws SQLException {
        List<AllActivityRecord> recordList = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        AllActivityQueryParamHandler handler = new AllActivityQueryParamHandler(params);
        String sql = buildSharedSql(handler);

        sql = sql + "select c.*, r.name as groupname, s.name as systemname, "
                + "c2.unpowered_yn "
                + "from combined c left join responsible_group r on c.group_id = r.group_id "
                + "left join all_systems s on c.system_id = s.system_id left join all_components c2 on "
                + "c.component_id = c2.component_id ";

        sql = sql + "order by modified_date desc, record_id desc ";

        // Limit number of records (pagination)
        sql = "select * from (select z.*, ROWNUM rnum from ("
                + sql + ") z where ROWNUM <= " + (offset + max) + ") where rnum > " + offset;

        LOGGER.log(Level.FINEST, "Query: {0}", sql);

        UserAuthorizationService userService = UserAuthorizationService.getInstance();

        try {
            con = HcoSqlUtil.getConnection();

            stmt = con.prepareStatement(sql);

            handler.assignParameterValues(stmt);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Date modifiedDate = rs.getDate(1);
                String changeTypeStr = rs.getString(2);
                AllChangeType changeType = AllChangeType.valueOf(changeTypeStr);
                String username = rs.getString(3);
                BigInteger recordId = rs.getBigDecimal(4).toBigIntegerExact();
                BigInteger componentId = null;
                BigDecimal componentIdDecimal = rs.getBigDecimal(5);
                if (componentIdDecimal != null) {
                    componentId = componentIdDecimal.toBigIntegerExact();
                }
                BigInteger systemId = null;
                BigDecimal systemIdDecimal = rs.getBigDecimal(6);
                if (systemIdDecimal != null) {
                    systemId = systemIdDecimal.toBigIntegerExact();
                }
                BigInteger regionId = null; // Not used except for filtering
                BigDecimal regionIdDecimal = rs.getBigDecimal(7);
                if (regionIdDecimal != null) {
                    regionId = regionIdDecimal.toBigIntegerExact();
                }
                String comments = rs.getString(8);
                BigInteger componentCount = null;
                BigDecimal componentCountDecimal = rs.getBigDecimal(9);
                if (componentCountDecimal != null) {
                    componentCount = componentCountDecimal.toBigIntegerExact();
                }
                BigInteger groupId = null;
                BigDecimal groupIdDecimal = rs.getBigDecimal(10);
                if (groupIdDecimal != null) {
                    groupId = groupIdDecimal.toBigIntegerExact();
                }
                BigInteger statusId = null;
                Status status = null;
                BigDecimal statusIdDecimal = rs.getBigDecimal(11);
                if (statusIdDecimal != null) {
                    statusId = statusIdDecimal.toBigIntegerExact();
                    status = Status.FROM_ID(statusId);
                }

                String componentName = rs.getString(12);

                String groupName = rs.getString(13);
                String systemName = rs.getString(14);
                String unpoweredStr = rs.getString(15);
                boolean unpowered = "Y".equals(unpoweredStr);

                AllActivityRecord record = new AllActivityRecord();

                record.setComponentCount(componentCount);
                record.setRecordId(recordId);
                record.setComponentId(componentId);
                record.setModifiedDate(modifiedDate);
                //record.setModifiedBy(modifiedBy);
                record.setSystemId(systemId);
                record.setGroupId(groupId);
                record.setComments(comments);
                record.setChangeType(changeType);
                record.setStatusId(statusId);
                record.setStatusName(status == null ? null : status.getName());
                record.setGroupName(groupName);
                record.setSystemName(systemName);
                record.setComponentName(componentName);

                record.setUsername(username);

                User user = userService.getUserFromUsername(username);
                record.setLastname(user.getLastname());
                record.setFirstname(user.getFirstname());

                record.setUnpowered(unpowered);

                recordList.add(record);
            }

        } finally {
            IOUtil.close(rs, stmt, con);
        }

        return recordList;
    }

    private String buildSharedSql(AllActivityQueryParamHandler handler) {
        String sql = "with activity_subquery as (select "
                + "count(group_signoff_history_id) as component_count, "
                + "max(group_signoff_history_id) as first_history_id, "
                + "max(component_id) as first_component_id, "
                + "max(region_id) as first_region_id, "
                + "trunc(modified_date, 'MI') as modified_date, modified_username, "
                + "system_id, group_id, comments, change_type, "
                + "status_id from (select group_signoff_history_id, "
                + "component_id, modified_date, modified_username, group_id, "
                + "comments, change_type, status_id, component.system_id, "
                + "region_id, name as component_name from group_signoff_history inner join component "
                + "using(component_id)) ";
        // NOTE: We use select from subselect inner join component in order grab 
        // region_id without creating ambiguous system_id, which would be 
        // harder to deal with in dynamic where clause as it would sometimes need to be qualifed.
        // We also need component name to filter on

        // NOTE: We filter in inner query BEFORE group by;  We get wrong result if filter in outer query
        // NOTE: first_history_id, first_component_id, first_region_id are just random and do not necessarily come from the same component.
        String where = handler.getSqlWhereClause();

        sql = sql + where;

        sql = sql + "group by "
                + "trunc(modified_date, 'MI'), modified_username, system_id, "
                + "group_id, comments, change_type, status_id), "
                + "combined as ( "
                + "select * from (select modified_date, change_type, username as modified_username, record_id, component_id, inventory_activity.system_id, all_components.region_id, remark, null as component_count, null as group_id, null as status_id, name as component_name from inventory_activity left join all_components using(component_id)) "; // We select from (select...) so we can filter by group_id, status_id;  left join component for component name

        sql = sql + where;

        sql = sql + "union all "
                + "select modified_date, change_type, modified_username, first_history_id as record_id, first_component_id as component_id, activity_subquery.system_id, first_region_id as region_id, comments as remark, component_count, group_id, status_id, name as component_name from activity_subquery left join all_components on all_components.component_id = activity_subquery.first_component_id " // left join component to get name
                + ") ";

        return sql;
    }

    public long count(AllActivityParams params) throws SQLException {
        AllActivityQueryParamHandler handler = new AllActivityQueryParamHandler(params);
        String sql = buildSharedSql(handler);

        sql = sql + "select count(*) from combined";

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        long count = 0;

        try {
            con = HcoSqlUtil.getConnection();

            stmt = con.prepareStatement(sql);

            handler.assignParameterValues(stmt);

            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } finally {
            IOUtil.close(rs, stmt, con);
        }

        return count;
    }
}
