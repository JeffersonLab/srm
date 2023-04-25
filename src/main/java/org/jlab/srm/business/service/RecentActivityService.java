package org.jlab.srm.business.service;

import org.jlab.srm.business.params.RecentActivityParams;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.jlab.srm.persistence.model.SignoffActivityCompressedRecord;
import org.jlab.srm.persistence.params.RecentActivityQueryParamHandler;
import org.jlab.srm.persistence.util.HcoSqlUtil;
import org.jlab.smoothness.business.util.IOUtil;

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
public class RecentActivityService {

    private static final Logger LOGGER = Logger.getLogger(
            RecentActivityService.class.getName());

    public List<SignoffActivityCompressedRecord> filterListCompressedFast(
            RecentActivityParams params, int offset, int max) throws SQLException {
        List<SignoffActivityCompressedRecord> recordList = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "with compressed as (select "
                + "count(group_signoff_history_id) as component_count, "
                + "max(group_signoff_history_id) as first_history_id, "
                + "max(a.component_id) as first_component_id, "
                + "trunc(modified_date, 'MI') as modified_date, modified_by, "
                + "a.system_id, group_id, comments, change_type, "
                + "status_id from group_signoff_history a inner join component b on a.component_id = b.component_id ";

        // NOTE: We filter in inner query BEFORE group by;  We get wrong result if filter in outer query
        RecentActivityQueryParamHandler handler = new RecentActivityQueryParamHandler(params);

        String where = handler.getSqlWhereClause();

        sql = sql + where;

        sql = sql + "group by "
                + "trunc(modified_date, 'MI'), modified_by, a.system_id, "
                + "group_id, comments, change_type, status_id) "
                + "select c.*, r.name as group_name, s.name as systemName, "
                + "c2.name as componentName, c.modified_username, c.modified_username, c.modified_username, c2.unpowered_yn "
                + "from compressed c inner join responsible_group r on c.group_id = r.group_id "
                + "inner join system s on c.system_id = s.system_id inner join component c2 on "
                + "c.first_component_id = c2.component_id ";

        sql = sql + "order by first_history_id desc ";

        // Limit number of records (pagination)
        sql = "select * from (select z.*, ROWNUM rnum from ("
                + sql + ") z where ROWNUM <= " + (offset + max) + ") where rnum > " + offset;

        LOGGER.log(Level.FINEST, "Query: {0}", sql);

        try {
            con = HcoSqlUtil.getConnection();

            stmt = con.prepareStatement(sql);

            handler.assignParameterValues(stmt);

            rs = stmt.executeQuery();

            while (rs.next()) {
                BigInteger componentCount = rs.getBigDecimal(1).toBigIntegerExact();
                BigInteger firstHistoryId = rs.getBigDecimal(2).toBigIntegerExact();
                BigInteger firstComponentId = rs.getBigDecimal(3).toBigIntegerExact();
                Date modifiedDate = rs.getDate(4);
                BigInteger modifiedBy = rs.getBigDecimal(5).toBigIntegerExact();
                BigInteger systemId = rs.getBigDecimal(6).toBigIntegerExact();
                BigInteger groupId = rs.getBigDecimal(7).toBigIntegerExact();
                String comments = rs.getString(8);
                String changeTypeStr = rs.getString(9);
                SignoffChangeType changeType = SignoffChangeType.valueOf(changeTypeStr);
                BigInteger statusId = rs.getBigDecimal(10).toBigIntegerExact();
                Status status = Status.FROM_ID(statusId);
                String groupName = rs.getString(11);
                String systemName = rs.getString(12);
                String componentName = rs.getString(13);
                String username = rs.getString(14);
                String lastname = rs.getString(15);
                String firstname = rs.getString(16);
                String unpoweredStr = rs.getString(17);
                boolean unpowered = "Y".equals(unpoweredStr);

                SignoffActivityCompressedRecord record = new SignoffActivityCompressedRecord();

                record.setComponentCount(componentCount);
                record.setGroupSignoffHistoryId(firstHistoryId);
                record.setFirstComponentId(firstComponentId);
                record.setModifiedDate(modifiedDate);
                record.setModifiedBy(modifiedBy);
                record.setSystemId(systemId);
                record.setGroupId(groupId);
                record.setComments(comments);
                record.setChangeType(changeType);
                record.setStatusId(statusId);
                record.setStatusName(status.getName());
                record.setGroupName(groupName);
                record.setSystemName(systemName);
                record.setFirstComponentName(componentName);
                record.setUsername(username);
                record.setLastname(lastname);
                record.setFirstname(firstname);
                record.setFirstUnpowered(unpowered);

                recordList.add(record);
            }

        } finally {
            IOUtil.close(rs, stmt, con);
        }

        return recordList;
    }
}
