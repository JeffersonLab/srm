package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.entity.Status;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.srm.persistence.model.SignoffReportRecord;
import org.jlab.smoothness.business.util.IOUtil;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class ComponentSignoffFacade {

    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    SystemFacade systemFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<HotSeatRecord> getHotSeatRecordList() {
        Query q = em.createNativeQuery(
                "select a.system_id, a.group_id, count(a.group_id)"
                        + " as signoff_count"
                        + "  from component_signoff a,"
                        + " group_responsibility b where a.system_id = b.system_id"
                        + " and a.group_id = b.group_id and a.status_id in (50, 100)"
                        + " and previous_signoff_status(a.component_id, b.weight) = 1"
                        + " group by a.system_id, a.group_id order by"
                        + " signoff_count desc, a.group_id asc");

        List<Object[]> results = q.getResultList();

        List<HotSeatRecord> records = new ArrayList<>();

        if (results != null) {
            for (Object[] row : results) {
                BigInteger systemId = BigInteger.valueOf(((Number) row[0]).longValue());
                BigInteger groupId = BigInteger.valueOf(((Number) row[1]).longValue());
                int signoffCount = ((Number) row[2]).intValue();
                ResponsibleGroup group = groupFacade.find(groupId);
                SystemEntity system = systemFacade.find(systemId);
                HotSeatRecord record = new HotSeatRecord(group, system, signoffCount);
                records.add(record);
            }
        }
        return records;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<HotSeatRecord> findReadyTurnRecordByGroup(BigInteger groupId) {
        Query q = em.createNativeQuery(
                "select a.system_id, a.group_id, count(a.group_id)"
                        + " as signoff_count"
                        + "  from component_signoff a,"
                        + " group_responsibility b where a.system_id = b.system_id"
                        + " and a.group_id = b.group_id and a.status_id in (50, 100)"
                        + " and previous_signoff_status(a.component_id, b.weight) = 1"
                        + " and a.group_id = " + groupId
                        + " group by a.system_id, a.group_id order by"
                        + " signoff_count desc, a.group_id asc");

        List<Object[]> results = q.getResultList();

        List<HotSeatRecord> records = new ArrayList<>();

        if (results != null) {
            for (Object[] row : results) {
                BigInteger systemId = BigInteger.valueOf(((Number) row[0]).longValue());
                groupId = BigInteger.valueOf(((Number) row[1]).longValue());
                int signoffCount = ((Number) row[2]).intValue();
                ResponsibleGroup group = groupFacade.find(groupId);
                SystemEntity system = systemFacade.find(systemId);
                HotSeatRecord record = new HotSeatRecord(group, system, signoffCount);
                records.add(record);
            }
        }
        return records;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<StatusCount> getStatusCount(BigInteger[] destinationIdArray, BigInteger categoryId, BigInteger systemId,
                                            BigInteger regionId, BigInteger groupId) {
        List<StatusCount> records = new ArrayList<>();

        String selectFrom
                = "select a.status_id, count(a.status_id) from component_signoff a, component b";

        List<String> whereList = new ArrayList<>();

        String w;

        w = "a.component_id = b.component_id and b.masked = 'N'";
        whereList.add(w);

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);

        if (destinationIdCsv != null) {
            w
                    = "a.component_id in (select component_id from component_beam_destination where beam_destination_id in ("
                    + destinationIdCsv + "))";
            whereList.add(w);
        }

        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            String csvList = "-1";
            if (systemList != null) {
                for (SystemEntity system : systemList) {
                    csvList = csvList + "," + system.getSystemId();
                }
            }
            w = "a.system_id in (" + csvList + ")";
            whereList.add(w);
        }

        if (systemId != null) {
            w = "a.system_id = " + systemId;
            whereList.add(w);
        }

        if (regionId != null) {
            w = "a.component_id in (select component_id from component where region_id = "
                    + regionId + ")";
            whereList.add(w);
        }

        if (groupId != null) {
            w = "a.group_id = " + groupId;
            whereList.add(w);
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = "where ";
            for (String wh : whereList) {
                where = where + wh + " and ";
            }

            where = where.substring(0, where.length() - 5);
        }

        String groupBy = "group by a.status_id order by a.status_id asc";

        String query = selectFrom + " " + where + " " + groupBy;

        //Query q = em.createNativeQuery("select a.status_id, count(b.status_id) from status a, component_signoff b where a.status_id = b.status_id (+) group by a.status_id");
        Query q = em.createNativeQuery(query);

        List<Object[]> results = q.getResultList();

        for (Object[] row : results) {
            StatusCount count = new StatusCount();

            Number statusIdNum = (Number) row[0];
            Number countNum = (Number) row[1];

            count.setStatus(Status.FROM_ID(BigInteger.valueOf(statusIdNum.longValue())));
            count.setCount(countNum.intValue());

            records.add(count);
        }

        return records;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<GroupStatusCount> getGroupStatusCount(BigInteger[] destinationIdArray,
                                                      BigInteger categoryId, BigInteger systemId, BigInteger regionId) {
        List<GroupStatusCount> records = new ArrayList<>();

        List<String> whereList = new ArrayList<>();

        String w;

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);

        if (destinationIdCsv != null) {
            w
                    = "a.component_id in (select component_id from component_beam_destination where beam_destination_id in ("
                    + destinationIdCsv + "))";
            whereList.add(w);
        }

        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            String csvList = "-1";
            if (systemList != null) {
                for (SystemEntity system : systemList) {
                    csvList = csvList + "," + system.getSystemId();
                }
            }
            w = "a.system_id in (" + csvList + ")";
            whereList.add(w);
        }

        if (systemId != null) {
            w = "a.system_id = " + systemId;
            whereList.add(w);
        }

        if (regionId != null) {
            w = "a.component_id in (select component_id from component where region_id = "
                    + regionId + ")";
            whereList.add(w);
        }

        String whereExtra = "";

        if (!whereList.isEmpty()) {
            for (String wh : whereList) {
                whereExtra = whereExtra + wh + " and ";
            }

            whereExtra = whereExtra.substring(0, whereExtra.length() - 5);
        }

        if (whereExtra.length() > 0) {
            whereExtra = " and " + whereExtra;
        }

        String select = "select c.group_id, c.name, c.goal_percent, "
                + "(select count(*) from component_signoff a, component b where a.group_id = c.group_id and a.status_id = 1 and a.component_id = b.component_id and b.masked = 'N'"
                + whereExtra + ") as ready, "
                + "(select count(*) from component_signoff a, component b where a.group_id = c.group_id and a.status_id = 50 and a.component_id = b.component_id and b.masked = 'N'"
                + whereExtra + ") as checked, "
                + "(select count(*) from component_signoff a, component b where a.group_id = c.group_id and a.status_id = 100 and a.component_id = b.component_id and b.masked = 'N'"
                + whereExtra + ") as not_ready";

        String from = "from responsible_group c order by c.name asc";

        //String query = select + " " + from;
        String query = "select * from (" + select + " " + from + ") where ready > 0 or checked > 0 or not_ready > 0 order by name asc";

        Query q = em.createNativeQuery(query);

        List<Object[]> results = q.getResultList();

        for (Object[] row : results) {

            BigInteger groupId = BigInteger.valueOf(((Number) row[0]).longValue());
            String name = (String) row[1];
            int goalPercent = ((Number) row[2]).intValue();
            int ready = ((Number) row[3]).intValue();
            int checked = ((Number) row[4]).intValue();
            int notReady = ((Number) row[5]).intValue();
            int total = ready + checked + notReady;

            GroupStatusCount count
                    = new GroupStatusCount(groupId, name, goalPercent, ready, checked, notReady,
                    total);

            records.add(count);
        }

        return records;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<SignoffReportRecord> filterSignoffReportRecordList(BigInteger[] destinationIdArray,
                                                                   BigInteger categoryId, BigInteger systemId, BigInteger regionId, BigInteger groupId, BigInteger statusId,
                                                                   Boolean readyTurn, Boolean masked, String name, int offset, int max) {
        List<SignoffReportRecord> records = new ArrayList<>();

        boolean setNameParameter = false;

        String selectFrom
                = "select a.component_id, a.group_id, a.system_id, a.status_id, b.name as component_name, b.unpowered_yn, c.name as group_name, d.name as system_name, m.modified_date, m.modified_username, m.comments from component_signoff a left outer join group_signoff m on a.component_id = m.component_id and a.system_id = m.system_id and a.group_id = m.group_id, component b, responsible_group c, system d";

        List<String> whereList = new ArrayList<>();

        String w;

        w
                = "a.component_id = b.component_id and a.group_id = c.group_id and a.system_id = d.system_id";
        whereList.add(w);

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);

        if (destinationIdCsv != null) {
            w
                    = "a.component_id in (select component_id from component_beam_destination where beam_destination_id in ("
                    + destinationIdCsv + ")) ";
            whereList.add(w);
        }

        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            String csvList = "-1";
            if (systemList != null) {
                for (SystemEntity system : systemList) {
                    csvList = csvList + "," + system.getSystemId();
                }
            }
            w = "a.system_id in (" + csvList + ")";
            whereList.add(w);
        }

        if (systemId != null) {
            w = "a.system_id = " + systemId;
            whereList.add(w);
        }

        if (regionId != null) {
            w = "b.region_id = " + regionId;
            whereList.add(w);
        }

        if (groupId != null) {
            w = "a.group_id = " + groupId;
            whereList.add(w);
        }

        if (statusId != null) {
            w = "a.status_id = " + statusId;
            whereList.add(w);
        }

        if (readyTurn != null) {
            w = "(a.component_id, a.group_id) " + (readyTurn ? "in" : "not in")
                    + " (select component_id, group_id from ready_turn)";
            whereList.add(w);
        }

        if (masked != null) {
            w = "b.masked = " + (masked ? "'Y'" : "'N'");
            whereList.add(w);
        }

        if (name != null && !name.isEmpty()) {
            w = "b.name like :componentName";
            whereList.add(w);
            setNameParameter = true;
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = "where ";
            for (String wh : whereList) {
                where = where + wh + " and ";
            }

            where = where.substring(0, where.length() - 5);
        }

        String orderBy = "order by d.name asc, b.name asc, c.name asc";

        String query = selectFrom + " " + where + " " + orderBy;

        String limitedQuery = "select * from (select z.*, ROWNUM rnum from (" + query
                + ") z where ROWNUM <= " + (offset + max) + ") where rnum > " + offset;

        Query q = em.createNativeQuery(limitedQuery);

        if (setNameParameter) {
            q.setParameter("componentName", name);
        }

        List<Object[]> results = q.getResultList();

        //a.component_id, a.group_id, a.system_id, a.status_id, b.name, c.name, d.name
        for (Object[] row : results) {
            SignoffReportRecord record = new SignoffReportRecord();

            Number componentIdNum = (Number) row[0];
            Number groupIdNum = (Number) row[1];
            Number systemIdNum = (Number) row[2];
            Number statusIdNum = (Number) row[3];
            String componentName = (String) row[4];
            Character unpoweredYn = (Character) row[5];
            String groupName = (String) row[6];
            String systemName = (String) row[7];
            Date modifiedDate = (Date) row[8];
            String username = (String) row[9];
            String comments = (String) row[10];

            record.setComponentId(BigInteger.valueOf(componentIdNum.longValue()));
            record.setGroupId(BigInteger.valueOf(groupIdNum.longValue()));
            record.setSystemId(BigInteger.valueOf(systemIdNum.longValue()));
            record.setStatus(Status.FROM_ID(BigInteger.valueOf(statusIdNum.longValue())));
            record.setComponentName(componentName);
            record.setUnpowered("Y".equals(String.valueOf(unpoweredYn)));
            record.setGroupName(groupName);
            record.setSystemName(systemName);
            record.setModifiedDate(modifiedDate);
            record.setModifiedBy(username);
            record.setComments(comments);

            records.add(record);
        }

        return records;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public Long countSignoffReportRecordList(BigInteger[] destinationIdArray, BigInteger categoryId, BigInteger systemId,
                                             BigInteger regionId, BigInteger groupId, BigInteger statusId, Boolean readyTurn,
                                             Boolean masked, String name) {
        String selectFrom
                = "select count(*) from component_signoff a, component b, responsible_group c, system d";

        boolean setNameParameter = false;

        List<String> whereList = new ArrayList<>();

        String w;

        w
                = "a.component_id = b.component_id and a.group_id = c.group_id and a.system_id = d.system_id";
        whereList.add(w);

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);

        if (destinationIdCsv != null) {
            w
                    = "a.component_id in (select component_id from component_beam_destination where beam_destination_id in ("
                    + destinationIdCsv + ")) ";
            whereList.add(w);
        }

        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            String csvList = "-1";
            if (systemList != null) {
                for (SystemEntity system : systemList) {
                    csvList = csvList + "," + system.getSystemId();
                }
            }
            w = "a.system_id in (" + csvList + ")";
            whereList.add(w);
        }

        if (systemId != null) {
            w = "a.system_id = " + systemId;
            whereList.add(w);
        }

        if (regionId != null) {
            w = "b.region_id = " + regionId;
            whereList.add(w);
        }

        if (groupId != null) {
            w = "a.group_id = " + groupId;
            whereList.add(w);
        }

        if (statusId != null) {
            w = "a.status_id = " + statusId;
            whereList.add(w);
        }

        if (readyTurn != null) {
            w = "(a.component_id, a.group_id) " + (readyTurn ? "in" : "not in")
                    + " (select component_id, group_id from ready_turn)";
            whereList.add(w);
        }

        if (masked != null) {
            w = "b.masked = " + (masked ? "'Y'" : "'N'");
            whereList.add(w);
        }

        if (name != null && !name.isEmpty()) {
            w = "b.name like :componentName";
            whereList.add(w);
            setNameParameter = true;
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = "where ";
            for (String wh : whereList) {
                where = where + wh + " and ";
            }

            where = where.substring(0, where.length() - 5);
        }

        String query = selectFrom + " " + where;

        Query q = em.createNativeQuery(query);

        if (setNameParameter) {
            q.setParameter("componentName", name);
        }

        return ((Number) q.getSingleResult()).longValue();
    }

    public static class HotSeatRecord {

        private ResponsibleGroup group;
        private SystemEntity system;
        private int signoffCount;

        public HotSeatRecord(ResponsibleGroup group, SystemEntity system, int signoffCount) {
            this.group = group;
            this.system = system;
            this.signoffCount = signoffCount;
        }

        public ResponsibleGroup getGroup() {
            return group;
        }

        public void setGroup(ResponsibleGroup group) {
            this.group = group;
        }

        public SystemEntity getSystem() {
            return system;
        }

        public void setSystem(SystemEntity system) {
            this.system = system;
        }

        public int getSignoffCount() {
            return signoffCount;
        }

        public void setSignoffCount(int signoffCount) {
            this.signoffCount = signoffCount;
        }
    }

    public static class StatusCount {

        private Status status;
        private int count;

        public StatusCount() {
        }

        public StatusCount(Status status, int count) {
            this.status = status;
            this.count = count;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class GroupStatusCount {

        private final BigInteger groupId;
        private final String name;
        private final int goalPercent;
        private final int ready;
        private final int checked;
        private final int notReady;
        private final int totalCount;
        private final float readyPercent;
        private final float checkedPercent;
        private final float notReadyPercent;

        public GroupStatusCount(BigInteger groupId, String name, int goalPercent, int ready,
                                int checked, int notReady, int totalCount) {
            this.groupId = groupId;
            this.name = name;
            this.goalPercent = goalPercent;
            this.ready = ready;
            this.checked = checked;
            this.notReady = notReady;
            this.totalCount = totalCount;
            if (totalCount < 1) {
                readyPercent = 0;
                checkedPercent = 0;
                notReadyPercent = 0;
            } else {
                readyPercent = ((float) ready / totalCount * 100);
                checkedPercent = ((float) checked / totalCount * 100);
                notReadyPercent = ((float) notReady / totalCount * 100);
            }
        }

        public BigInteger getGroupId() {
            return groupId;
        }

        public String getName() {
            return name;
        }

        public int getGoalPercent() {
            return goalPercent;
        }

        public int getReady() {
            return ready;
        }

        public int getChecked() {
            return checked;
        }

        public int getNotReady() {
            return notReady;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public float getReadyPercent() {
            return readyPercent;
        }

        public float getCheckedPercent() {
            return checkedPercent;
        }

        public float getNotReadyPercent() {
            return notReadyPercent;
        }
    }
}
