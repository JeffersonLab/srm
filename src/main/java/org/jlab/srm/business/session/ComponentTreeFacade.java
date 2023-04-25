package org.jlab.srm.business.session;

import oracle.jdbc.OracleTypes;
import org.jlab.srm.business.util.PartitionList;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.entity.view.ComponentStatus;
import org.jlab.srm.persistence.enumeration.HcoNodeType;
import org.jlab.srm.persistence.model.HcoNodeData;
import org.jlab.srm.persistence.model.TreeNode;
import org.jlab.srm.presentation.util.HcoFunctions;
import org.jlab.smoothness.business.util.IOUtil;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@Stateless
public class ComponentTreeFacade {

    private static final Logger logger = Logger.getLogger(
            ComponentTreeFacade.class.getName());
    @Resource(mappedName = "jdbc/srm")
    DataSource ds;
    @EJB
    CategoryFacade categoryFacade;
    @EJB
    ComponentFacade componentFacade;
    @EJB
    GroupSignoffFacade signoffFacade;
    @EJB
    SystemFacade systemFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    /**
     * In order to filter by category and system we first check if specific
     * system is specified and return that as the one and only system. Otherwise
     * if category is specified then grab all system IDs which are underneath
     * that category.
     *
     * @param categoryId Top most category
     * @param systemId   Specific system
     * @return
     */
    @PermitAll
    public BigInteger[] getSystemIdArray(BigInteger categoryId, BigInteger systemId) {
        List<BigInteger> systemIdList = new ArrayList<>();

        if (systemId != null) {
            systemIdList.add(systemId);
        } else if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            for (SystemEntity se : systemList) {
                systemIdList.add(se.getSystemId());
            }
        }

        BigInteger[] systemIdArray = systemIdList.toArray(new BigInteger[0]);

        return systemIdArray;
    }

    @PermitAll
    public TreeNode<HcoNodeData> findRoot(BigInteger[] destinationIdArray, BigInteger categoryId, BigInteger systemId, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        Category root = categoryFacade.findRoot();

        BigInteger[] systemIdArray = getSystemIdArray(categoryId, systemId);

        String destinationIdCsv = IOUtil.toNullOrCsvForStoredProcedure(destinationIdArray);
        String systemIdCsv = IOUtil.toNullOrCsvForStoredProcedure(systemIdArray);
        String statusIdCsv = IOUtil.toNullOrCsvForStoredProcedure(statusIdArray);

        Query q = em.createNativeQuery("select filter_category_status(0, " + destinationIdCsv + ", " + systemIdCsv + ", " + regionId + ", " + groupId + ", " + statusIdCsv + ") from dual");

        Number statusIdNum = (Number) q.getSingleResult();

        BigInteger statusId = BigInteger.valueOf(statusIdNum.longValue());

        HcoNodeData data = new HcoNodeData(HcoNodeType.CATEGORY, root.getCategoryId(), root.getName(), Status.FROM_ID(statusId), true);
        TreeNode<HcoNodeData> node = new TreeNode<>(data);

        return node;
    }

    @PermitAll
    public List<TreeNode<HcoNodeData>> findChildren(HcoNodeType type, BigInteger nodeId, BigInteger[] destinationIdArray, BigInteger categoryId, BigInteger systemId, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        List<TreeNode<HcoNodeData>> children = null;

        BigInteger[] systemIdArray = getSystemIdArray(categoryId, systemId);

        switch (type) {
            case SYSTEM:
                children = findChildrenOfSystem(nodeId, destinationIdArray, regionId, groupId, statusIdArray);
                break;
            case CATEGORY:
                children = findChildrenOfCategory(nodeId, destinationIdArray, systemIdArray, regionId, groupId, statusIdArray);
                break;
            case COMPONENT:
                children = findChildrenOfComponent(nodeId, groupId); // no need to filter by statusIdArray
                break;
        }

        return children;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    private List<Category> findCategoryChildrenList(BigInteger categoryId, BigInteger[] destinationIdArray, BigInteger[] systemIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        List<BigInteger> categoryIdList = new ArrayList<>();
        Connection con = null;

        String destinationIdCsv = IOUtil.toNullOrCsv(destinationIdArray);
        String systemIdCsv = IOUtil.toNullOrCsv(systemIdArray);
        String statusIdCsv = IOUtil.toNullOrCsv(statusIdArray);

        try {
            con = ds.getConnection();

            String query = "{call filter_category_child_table(?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cstmt = con.prepareCall(query);
            if (categoryId == null) {
                cstmt.setNull(1, Types.INTEGER);
            } else {
                cstmt.setLong(1, categoryId.longValue());
            }
            if (destinationIdCsv == null) {
                cstmt.setNull(2, Types.VARCHAR);
            } else {
                cstmt.setString(2, destinationIdCsv);
            }
            if (systemIdCsv == null) {
                cstmt.setNull(3, Types.VARCHAR);
            } else {
                cstmt.setString(3, systemIdCsv);
            }
            if (regionId == null) {
                cstmt.setNull(4, Types.INTEGER);
            } else {
                cstmt.setLong(4, regionId.longValue());
            }
            if (groupId == null) {
                cstmt.setNull(5, Types.INTEGER);
            } else {
                cstmt.setLong(5, groupId.longValue());
            }
            if (statusIdCsv == null) {
                cstmt.setNull(6, Types.VARCHAR);
            } else {
                cstmt.setString(6, statusIdCsv);
            }
            cstmt.registerOutParameter(7, OracleTypes.CURSOR);
            cstmt.execute();
            ResultSet rset = (ResultSet) cstmt.getObject(7);

            while (rset.next()) {
                //System.out.println(rset.getString(1));
                categoryIdList.add(BigInteger.valueOf(rset.getLong(1)));
            }
            cstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to query database", e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Unable to close db connection");
                }
            }
        }

        List<Category> categoryList;

        if (!categoryIdList.isEmpty()) {
            Query q = em.createNativeQuery("select * from category where category_id in (:categoryIdList) order by weight asc, name asc", Category.class);
            q.setParameter("categoryIdList", categoryIdList);
            categoryList = q.getResultList();
        } else {
            categoryList = new ArrayList<>();
        }
        return categoryList;
    }

    private List<SystemEntity> findSystemChildrenList(BigInteger categoryId, BigInteger[] destinationIdArray, BigInteger[] systemIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SystemEntity> cq = cb.createQuery(SystemEntity.class);
        Root<SystemEntity> root = cq.from(SystemEntity.class);
        cq.select(root).distinct(true);

        Join<SystemEntity, Component> componentList = root.join("componentList");

        List<Predicate> filters = new ArrayList<>();
        if (categoryId != null) {
            filters.add(root.get("category").in(categoryId));
        }

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            //Join<SystemEntity, Component> components = root.join("componentList");
            //Join<Component, BeamDestination> destinations = components.join("beamDestinationList");
            //filters.add(destinations.in((Object[]) destinationIdArray));

            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> destComponentList = subqueryRoot.join("componentList");
            subquery.select(destComponentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(componentList.get("componentId")).value(subquery));
        }


        systemIdArray = IOUtil.removeNullValues(systemIdArray, BigInteger.class);

        if (systemIdArray != null && systemIdArray.length > 0) {
            filters.add(root.get("systemId").in(Arrays.asList(systemIdArray)));
        }


        if (regionId != null) {
            filters.add(componentList.get("region").in(regionId));
        }
        if (groupId != null) {
            Join<SystemEntity, GroupResponsibility> responsibilities = root.join("groupResponsibilityList");
            filters.add(responsibilities.get("group").in(groupId));
        }
        Join<SystemEntity, Application> application = root.join("applicationList");
        filters.add(cb.equal(application.get("applicationId"), 1));

        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {

            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Status> subqueryRoot = subquery.from(Status.class);
            Join<Status, ComponentStatus> statusList = subqueryRoot.join("componentStatusList");
            subquery.select(statusList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("statusId").in(Arrays.asList(
                    statusIdArray));
            subquery.where(p1);
            filters.add(cb.in(componentList.get("componentId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p1 = root.get("weight");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        Path p2 = root.get("name");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        cq.orderBy(orders);
        return em.createQuery(cq).getResultList();
    }

    private List<Component> findComponentChildrenList(BigInteger systemId, BigInteger[] destinationIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root).distinct(true);
        List<Predicate> filters = new ArrayList<>();
        filters.add(root.get("system").in(systemId));

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (regionId != null) {
            filters.add(root.get("region").in(regionId));
        }
        if (groupId != null) {
            Join<Component, SystemEntity> system = root.join("system");
            Join<SystemEntity, GroupResponsibility> responsibilities = system.join("groupResponsibilityList");
            filters.add(responsibilities.get("group").in(groupId));
        }

        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (statusIdArray != null && statusIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Status> subqueryRoot = subquery.from(Status.class);
            Join<Status, ComponentStatus> componentList = subqueryRoot.join("componentStatusList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("statusId").in(Arrays.asList(
                    statusIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();

        Path p1 = root.get("weight");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        Path p2 = root.get("name");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        cq.orderBy(orders);
        return em.createQuery(cq).getResultList();
    }

    @SuppressWarnings("unchecked")
    private Map<BigInteger, Status> findCategoryStatuses(BigInteger categoryId, BigInteger[] destinationIdArray, BigInteger[] systemIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        Map<BigInteger, Status> statusMap = new HashMap<>();

        String destinationIdCsv = IOUtil.toNullOrCsvForStoredProcedure(destinationIdArray);
        String systemIdCsv = IOUtil.toNullOrCsvForStoredProcedure(systemIdArray);
        String statusIdCsv = IOUtil.toNullOrCsvForStoredProcedure(statusIdArray);

        Query q = em.createNativeQuery("select * from table(filter_category_status_table(" + categoryId + ", " + destinationIdCsv + ", " + systemIdCsv + ", " + regionId + ", " + groupId + ", " + statusIdCsv + "))");

        long start = System.currentTimeMillis();
        List<Object[]> results = q.getResultList();
        long end = System.currentTimeMillis();
        logger.log(Level.FINE, "[PERFORMANCE] Fetch Category Statuses: {0}", (end - start) / 1000.0f);

        for (Object[] row : results) {
            Number childCatId = (Number) row[0];
            Number statusId = (Number) row[1];
            statusMap.put(BigInteger.valueOf(childCatId.longValue()), Status.FROM_ID(BigInteger.valueOf(statusId.longValue())));
        }

        return statusMap;
    }

    @SuppressWarnings("unchecked")
    private Map<BigInteger, Status> findSystemStatuses(BigInteger[] destinationIdArray, BigInteger[] systemIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        Map<BigInteger, Status> statusMap = new HashMap<>();

        String destinationIdCsv = IOUtil.toNullOrCsvForStoredProcedure(destinationIdArray);
        String systemIdCsv = IOUtil.toNullOrCsvForStoredProcedure(systemIdArray);
        String statusIdCsv = IOUtil.toNullOrCsvForStoredProcedure(statusIdArray);

        Query q = em.createNativeQuery("select * from table(filter_system_status_table(" + destinationIdCsv + ", " + systemIdCsv + ", " + regionId + ", " + groupId + ", " + statusIdCsv + "))");

        List<Object[]> results = q.getResultList();

        for (Object[] row : results) {
            Number categoryId = (Number) row[0];
            Number statusId = (Number) row[1];
            statusMap.put(BigInteger.valueOf(categoryId.longValue()), Status.FROM_ID(BigInteger.valueOf(statusId.longValue())));
        }

        return statusMap;
    }

    @SuppressWarnings("unchecked")
    private Map<BigInteger, Status> findComponentStatuses(List<Component> components, BigInteger groupId) {
        Map<BigInteger, Status> statusMap = new HashMap<>();

        if (components != null && !components.isEmpty()) {
            Query q;

            if (groupId == null) {
                q = em.createNativeQuery("select a.component_id, case when a.masked = 'Y' then a.mask_type_id else nvl((select max(b.status_id) from component_signoff b where a.component_id = b.component_id), 1) end as status_id from component a where a.component_id in :componentIdList", "ComponentStatus");
            } else {
                q = em.createNativeQuery("select a.component_id, case when a.masked = 'Y' then a.mask_type_id else nvl((select b.status_id from component_signoff b where a.component_id = b.component_id and b.group_id = :groupId), 1) end as status_id from component a where a.component_id in :componentIdList", "ComponentStatus");
                q.setParameter("groupId", groupId);
            }

            List<BigInteger> componentIdList = new ArrayList<>();

            for (Component component : components) {
                componentIdList.add(component.getComponentId());
            }

            /*Oracle has a limit of 1000 literal items in an "IN" query so we must partition*/
            PartitionList<BigInteger> plist = new PartitionList<>(componentIdList, 1000);

            for (List<BigInteger> l : plist) {
                q.setParameter("componentIdList", l);

                List<Object[]> componentStatusList = q.getResultList();

                for (Object[] record : componentStatusList) {
                    Number componentIdAsNumber = (Number) record[0];
                    Number statusIdAsNumber = (Number) record[1];

                    statusMap.put(BigInteger.valueOf(componentIdAsNumber.longValue()), Status.FROM_ID(BigInteger.valueOf(statusIdAsNumber.longValue())));
                }
            }
        }

        return statusMap;
    }

    @PermitAll
    public List<TreeNode<HcoNodeData>> findChildrenOfCategory(BigInteger categoryId, BigInteger[] destinationIdArray, BigInteger[] systemIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        List<TreeNode<HcoNodeData>> children = new ArrayList<>();

        // Find category children
        List<Category> categoryChildren = findCategoryChildrenList(categoryId, destinationIdArray, systemIdArray, regionId, groupId, statusIdArray);

        // Find category statuses
        Map<BigInteger, Status> categoryStatuses = findCategoryStatuses(categoryId, destinationIdArray, systemIdArray, regionId, groupId, statusIdArray);

        // Find system children
        List<SystemEntity> systemChildren = findSystemChildrenList(categoryId, destinationIdArray, systemIdArray, regionId, groupId, statusIdArray);

        /*for(SystemEntity entity: systemChildren)
        {
            System.out.println("Found: " + entity.getName());
        }*/
        Map<BigInteger, Status> systemStatuses = findSystemStatuses(destinationIdArray, systemIdArray, regionId, null, statusIdArray); // groupId always = null to avoid pretending status only considers one group

        // Create nodes for category children
        if (categoryChildren != null) {
            for (Category category : categoryChildren) {
                boolean lazyChildren = ((category.getCategoryList() != null) && !(category.getCategoryList().isEmpty())) || ((category.getSystemList() != null) && !(category.getSystemList().isEmpty()));
                HcoNodeData data = new HcoNodeData(HcoNodeType.CATEGORY, category.getCategoryId(), category.getName(), categoryStatuses.get(category.getCategoryId()), lazyChildren);
                TreeNode<HcoNodeData> node = new TreeNode<>(data);
                children.add(node);
            }
        }

        // Create nodes for system children
        if (systemChildren != null) {
            for (SystemEntity system : systemChildren) {
                boolean lazyChildren = (system.getComponentList() != null) && !(system.getComponentList().isEmpty());
                HcoNodeData data = new HcoNodeData(HcoNodeType.SYSTEM, system.getSystemId(), system.getName(), systemStatuses.get(system.getSystemId()), lazyChildren);
                TreeNode<HcoNodeData> node = new TreeNode<>(data);
                children.add(node);
            }
        }

        return children;
    }

    @PermitAll
    public List<TreeNode<HcoNodeData>> findChildrenOfSystem(BigInteger systemId, BigInteger[] destinationIdArray, BigInteger regionId, BigInteger groupId, BigInteger[] statusIdArray) {
        List<TreeNode<HcoNodeData>> children = new ArrayList<>();

        // Find component children
        List<Component> componentChildren = findComponentChildrenList(systemId, destinationIdArray, regionId, groupId, statusIdArray);

        // Find component statuses
        Map<BigInteger, Status> componentStatuses = findComponentStatuses(componentChildren, null); // groupId always null because we don't want to change component status by pretending component only has one group

        // Create nodes for component children
        if (componentChildren != null) {
            for (Component component : componentChildren) {
                boolean lazyChildren = (component.getSystem().getGroupResponsibilityList() != null) && !(component.getSystem().getGroupResponsibilityList().isEmpty());
                Status status = componentStatuses.get(component.getComponentId());
                if (status == null) {
                    status = Status.READY;
                }

                String name = HcoFunctions.formatComponent(component);

                HcoNodeData data = new HcoNodeData(HcoNodeType.COMPONENT, component.getComponentId(), name, status, lazyChildren);
                TreeNode<HcoNodeData> node = new TreeNode<>(data);
                children.add(node);
            }
        }

        return children;
    }

    @PermitAll
    public List<TreeNode<HcoNodeData>> findChildrenOfComponent(BigInteger componentId, BigInteger groupId) {
        List<TreeNode<HcoNodeData>> children = new ArrayList<>();

        Component component = componentFacade.find(componentId);

        List<GroupSignoff> signoffList;

        signoffList = signoffFacade.updateAndFetchGroupSignOffList(component, null); // always use null groupId so that all groups are always shown!

        for (GroupSignoff signoff : signoffList) {
            HcoNodeData data = new HcoNodeData(HcoNodeType.GROUP, signoff.getGroupResponsibility().getGroup().getGroupId(), signoff.getGroupResponsibility().getGroup().getName(), signoff.getStatus(), false);
            TreeNode<HcoNodeData> node = new TreeNode<>(data);
            children.add(node);
        }

        return children;
    }
}
