package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.BeamDestination;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.srm.persistence.entity.view.SignoffActivityRecord;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.jlab.srm.persistence.model.ActivitySummaryRecord;
import org.jlab.srm.persistence.model.SignoffActivityCompressedRecord;
import org.jlab.smoothness.business.util.IOUtil;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class SignoffActivityFacade extends AbstractFacade<SignoffActivityRecord> {

    @EJB
    SystemFacade systemFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public SignoffActivityFacade() {
        super(SignoffActivityRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<SignoffActivityCompressedRecord> filterListCompressed(
            BigInteger[] destinationIdArray, BigInteger categoryId, BigInteger systemId,
            BigInteger regionId, BigInteger groupId, String username, String componentName,
            BigInteger statusId, SignoffChangeType type, Date startDate, Date endDate,
            BigInteger[] statusIdArray, int offset, int max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignoffActivityCompressedRecord> cq = cb.createQuery(
                SignoffActivityCompressedRecord.class);
        Root<SignoffActivityCompressedRecord> root = cq.from(SignoffActivityCompressedRecord.class);
        cq.select(root);//.distinct(true);
        List<Predicate> filters = new ArrayList<>();

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Join<SignoffActivityCompressedRecord, Component> component = root.join("component");
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(component.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("systemId"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("regionId"), regionId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("groupId"), groupId));
        }
        if (username != null && !username.isEmpty()) {
            filters.add(cb.equal(root.get("username"), username));
        }
        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("firstComponentName")),
                    componentName.toLowerCase()));
        }
        if (statusId != null) {
            filters.add(cb.equal(root.get("statusId"), statusId));
        }
        if (type != null) {
            filters.add(cb.equal((root.<String>get("changeType")), type));
        }
        if (startDate != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), startDate));
        }
        if (endDate != null) {
            filters.add(cb.lessThan(root.get("modifiedDate"), endDate));
        }

        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {
            filters.add(root.get("statusId").in(Arrays.asList(
                    statusIdArray)));

            /*Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Status> subqueryRoot = subquery.from(Status.class);
            Join<Status, ComponentStatus> componentList = subqueryRoot.join("componentStatusList");
            subquery.select(componentList.<BigInteger>get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("statusId").in(Arrays.asList(
                    statusIdArray));
            subquery.where(p1);
            Join<SignoffActivityCompressedRecord, Component> component = root.join("component");
            filters.add(cb.in(component.get("componentId")).value(subquery));*/
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p1 = root.get("firstHistoryId");
        Order o1 = cb.desc(p1);
        orders.add(o1);
        cq.orderBy(orders);
        return em.createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public Long countFilterListCompressed(BigInteger[] destinationIdArray, BigInteger categoryId,
                                          BigInteger systemId, BigInteger regionId, BigInteger groupId, String username,
                                          String componentName, BigInteger statusId, SignoffChangeType type, Date startDate, Date endDate,
                                          BigInteger[] statusIdArray) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<SignoffActivityCompressedRecord> root = cq.from(SignoffActivityCompressedRecord.class);
        List<Predicate> filters = new ArrayList<>();
        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Join<SignoffActivityCompressedRecord, Component> component = root.join("component");
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("firstComponentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(component.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("systemId"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("regionId"), regionId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("groupId"), groupId));
        }
        if (username != null && !username.isEmpty()) {
            filters.add(cb.equal(root.get("username"), username));
        }
        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("firstComponentName")),
                    componentName.toLowerCase()));
        }
        if (statusId != null) {
            filters.add(cb.equal(root.get("statusId"), statusId));
        }
        if (type != null) {
            filters.add(cb.equal((root.<String>get("changeType")), type));
        }
        if (startDate != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), startDate));
        }
        if (endDate != null) {
            filters.add(cb.lessThan(root.get("modifiedDate"), endDate));
        }
        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {
            filters.add(root.get("statusId").in(Arrays.asList(
                    statusIdArray)));
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root)); //.distinct(true);
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public List<SignoffActivityRecord> filterList(BigInteger[] destinationIdArray,
                                                  BigInteger categoryId, BigInteger systemId, BigInteger regionId, BigInteger groupId,
                                                  String username, String componentName, SignoffChangeType type,
                                                  Date startDate, Date endDate, BigInteger[] statusIdArray, int offset, int max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignoffActivityRecord> cq = cb.createQuery(SignoffActivityRecord.class);
        Root<SignoffActivityRecord> root = cq.from(SignoffActivityRecord.class);
        cq.select(root);//.distinct(true);
        List<Predicate> filters = new ArrayList<>();

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Join<SignoffActivityRecord, Component> component = root.join("component");
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(component.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("systemId"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("regionId"), regionId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("groupId"), groupId));
        }
        if (username != null && !username.isEmpty()) {
            filters.add(cb.equal(root.get("modifiedBy"), username));
        }
        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("componentName")),
                    componentName.toLowerCase()));
        }
        if (type != null) {
            filters.add(cb.equal((root.<String>get("changeType")), type));
        }
        if (startDate != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), startDate));
        }
        if (endDate != null) {
            filters.add(cb.lessThan(root.get("modifiedDate"), endDate));
        }
        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {

            filters.add(root.get("statusId").in(Arrays.asList(
                    statusIdArray)));

        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p1 = root.get("groupSignoffHistoryId");
        Order o1 = cb.desc(p1);
        orders.add(o1);
        cq.orderBy(orders);
        return em.createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
        //return findAll(new OrderDirective("groupSignoffHistoryId", false));
    }

    @PermitAll
    public Long countFilterList(BigInteger[] destinationIdArray, BigInteger categoryId,
                                BigInteger systemId, BigInteger regionId, BigInteger groupId, String username,
                                String componentName, SignoffChangeType type, Date startDate, Date endDate,
                                BigInteger[] statusIdArray) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<SignoffActivityRecord> root = cq.from(SignoffActivityRecord.class);
        List<Predicate> filters = new ArrayList<>();
        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Join<SignoffActivityRecord, Component> component = root.join("component");
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(component.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("systemId"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("regionId"), regionId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("groupId"), groupId));
        }
        if (username != null && !username.isEmpty()) {
            filters.add(cb.equal(root.get("modifiedBy"), username));
        }
        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("componentName")),
                    componentName.toLowerCase()));
        }
        if (type != null) {
            filters.add(cb.equal((root.<String>get("changeType")), type));
        }
        if (startDate != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), startDate));
        }
        if (endDate != null) {
            filters.add(cb.lessThan(root.get("modifiedDate"), endDate));
        }
        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {
            filters.add(root.get("statusId").in(Arrays.asList(
                    statusIdArray)));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root)); //.distinct(true);
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<ActivitySummaryRecord> findSummaryList(BigInteger[] destinationIdArray,
                                                       BigInteger categoryId, BigInteger systemId, BigInteger regionId, Date startDate,
                                                       Date endDate) {
        Query q = em.createNativeQuery("delete from tmp_activity_summary");

        q.executeUpdate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String query
                = "insert into tmp_activity_summary select b.group_id, a.change_type, a.status_id, count(*) from group_signoff_history a, responsible_group b, component c "
                + "where a.group_id = b.group_id "
                + "and a.component_id = c.component_id ";
        if (regionId != null) {
            query = query + "and c.region_id = " + regionId + " ";
        }

        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            String csvList = "-1";
            if (systemList != null) {
                for (SystemEntity system : systemList) {
                    csvList = csvList + "," + system.getSystemId();
                }
            }
            query = query + "and c.system_id in (" + csvList + ") ";
        }

        if (systemId != null) {
            query = query + "and c.system_id = " + systemId + " ";
        }

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);

        if (destinationIdCsv != null) {
            query
                    = query
                    + "and a.component_id in (select component_id from component_beam_destination where beam_destination_id in ("
                    + destinationIdCsv + ")) ";
        }

        if (startDate != null) {
            query = query + "and a.modified_date >= to_date('" + formatter.format(startDate)
                    + "', 'YYYY-MM-DD HH24:MI') ";
        }
        if (endDate != null) {
            query = query + "and a.modified_date < to_date('" + formatter.format(endDate)
                    + "', 'YYYY-MM-DD HH24:MI') ";
        }
        query = query + "group by b.group_id, change_type, status_id";

        q = em.createNativeQuery(query);

        q.executeUpdate();

        q = em.createNativeQuery(
                "select * from activity_summary where upgrade_ready_count > 0 or upgrade_checked_count > 0 or downgrade_checked_count > 0 or downgrade_not_ready_count > 0 or cascade_count > 0 or comment_count > 0");

        List<ActivitySummaryRecord> activitySummaryList = new ArrayList<ActivitySummaryRecord>();

        List<Object[]> resultList = q.getResultList();

        if (resultList != null) {
            for (Object[] row : resultList) {
                BigInteger groupId = BigInteger.valueOf(((Number) row[0]).longValue());
                String groupName = (String) row[1];
                int upgradeReadyCount = ((Number) row[2]).intValue();
                int upgradeCheckedCount = ((Number) row[3]).intValue();
                int downgradeCheckedCount = ((Number) row[4]).intValue();
                int downgradeNotReadyCount = ((Number) row[5]).intValue();
                int cascadeCount = ((Number) row[6]).intValue();
                int commentCount = ((Number) row[7]).intValue();

                ActivitySummaryRecord summary = new ActivitySummaryRecord(
                        groupId,
                        groupName, upgradeReadyCount, upgradeCheckedCount,
                        downgradeCheckedCount, downgradeNotReadyCount,
                        cascadeCount, commentCount);

                activitySummaryList.add(summary);
            }
        }

        return activitySummaryList;
    }
}
