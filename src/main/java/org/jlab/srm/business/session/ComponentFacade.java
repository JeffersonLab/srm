package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.entity.view.ComponentSignoff;
import org.jlab.srm.persistence.entity.view.ComponentStatus;
import org.jlab.srm.persistence.entity.view.ReadyTurn;
import org.jlab.srm.persistence.enumeration.DataSource;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.persistence.util.JPAUtil;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"srm-admin", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class ComponentFacade extends AbstractFacade<Component> {

    private static final Logger LOGGER = Logger.getLogger(ComponentFacade.class.getName());
    @EJB
    GroupSignoffFacade signoffFacade;
    @EJB
    CategoryFacade categoryFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    RegionFacade regionFacade;
    @EJB
    GroupSignoffHistoryFacade groupSignoffHistoryFacade;
    @EJB
    GroupSignoffFacade groupSignoffFacade;
    @EJB
    GroupResponsibilityFacade groupResponsibilityFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public ComponentFacade() {
        super(Component.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<Component> filterList(BigInteger[] destinationIdArray, BigInteger categoryId,
                                      BigInteger systemId,
                                      BigInteger regionId, BigInteger groupId, BigInteger applicationId, DataSource source,
                                      Boolean masked, Boolean unpowered, String name,
                                      BigInteger[] statusIdArray,
                                      boolean sortByMaskedDate, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root);
        Join<Component, SystemEntity> systems = root.join("system");
        Join<Component, Region> regions = root.join("region");
        //root.fetch("componentStatus");
        List<Predicate> filters = new ArrayList<>();

        if (source != null) {
            filters.add(cb.equal((root.<String>get("dataSource")), source));
        }

        if (masked != null) {
            filters.add(cb.equal((root.<String>get("maskedStr")), masked ? "Y" : "N"));
        }

        if (unpowered != null) {
            filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
        }

        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
        }

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, applicationId);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(root.get("system").in(systemId));
        }
        if (regionId != null) {
            filters.add(root.get("region").in(regionId));
        }
        if (groupId != null) {
            Join<Category, GroupResponsibility> responsibilities = systems.join(
                    "groupResponsibilityList");
            filters.add(responsibilities.get("group").in(groupId));
        }
        if (applicationId != null) {
            Join<Component, SystemEntity> system = root.join("system");
            Join<SystemEntity, Application> application = system.join("applicationList");
            filters.add(cb.equal(application.get("applicationId"), applicationId));
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
        if (sortByMaskedDate) {
            Path p0 = root.get("maskedDate");
            Order o1 = cb.desc(p0);
            orders.add(o1);
            Path p2 = root.get("name");
            Order o2 = cb.asc(p2);
            orders.add(o2);
        } else {
            Path p0 = systems.get("name");
            Order o0 = cb.asc(p0);
            orders.add(o0);
            Path p1 = regions.get("name");
            Order o1 = cb.asc(p1);
            orders.add(o1);
            Path p2 = root.get("weight");
            Order o2 = cb.asc(p2);
            orders.add(o2);
            Path p3 = root.get("name");
            Order o3 = cb.asc(p3);
            orders.add(o3);
        }
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public long countFilterList(BigInteger[] destinationIdArray, BigInteger categoryId,
                                BigInteger systemId,
                                BigInteger regionId,
                                BigInteger groupId, BigInteger applicationId, DataSource source, Boolean masked,
                                Boolean unpowered, String name, BigInteger[] statusIdArray) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Component> root = cq.from(Component.class);

        List<Predicate> filters = new ArrayList<>();

        if (source != null) {
            filters.add(cb.equal((root.<String>get("dataSource")), source));
        }

        if (masked != null) {
            filters.add(cb.equal((root.<String>get("maskedStr")), masked ? "Y" : "N"));
        }

        if (unpowered != null) {
            filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
        }

        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
        }

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, applicationId);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(root.get("system").in(systemId));
        }
        if (regionId != null) {
            filters.add(root.get("region").in(regionId));
        }
        if (groupId != null) {
            Join<Component, SystemEntity> systems = root.join("system");
            Join<Category, GroupResponsibility> responsibilities = systems.join(
                    "groupResponsibilityList");
            filters.add(responsibilities.get("group").in(groupId));
        }
        if (applicationId != null) {
            Join<Component, SystemEntity> system = root.join("system");
            Join<SystemEntity, Application> application = system.join("applicationList");
            filters.add(cb.equal(application.get("applicationId"), applicationId));
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

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public Component findDetail(BigInteger componentId) {
        Component component = find(componentId);

        if (component != null) {
            JPAUtil.initialize(component.getBeamDestinationList());

            List<GroupSignoff> signoffList = signoffFacade.updateAndFetchGroupSignOffList(component);

            em.detach(component);

            component.setGroupSignoffList(signoffList);
        }

        return component;
    }

    @PermitAll
    public Component findDetail(String name) {
        TypedQuery<Component> q = em.createQuery("select c from Component c where c.name = :name",
                Component.class);

        q.setParameter("name", name);

        List<Component> componentList = q.getResultList();

        Component component = null;

        if (componentList != null && !componentList.isEmpty()) {
            component = componentList.get(0);
        }

        if (component != null) {
            JPAUtil.initialize(component.getBeamDestinationList());

            List<GroupSignoff> signoffList = signoffFacade.updateAndFetchGroupSignOffList(component);

            em.detach(component);

            component.setGroupSignoffList(signoffList);
        }

        return component;
    }

    /**
     * For performance reasons this method assumes the group_signoff table is
     * synchronized with the group_action table. This method may give incorrect
     * results if this assumption isn't true. To ensure the tables are in sync
     * call GroupSignoff.updateAndFetchGroupSignOffList before calling this
     * method.
     *
     * @param component
     * @return Status
     */
    @PermitAll
    public Status findComponentStatus(Component component) {
        TypedQuery<BigInteger> q = em.createNamedQuery("Component.findStatus", BigInteger.class);

        q.setParameter("component", component);

        BigInteger value = q.getSingleResult();

        if (value == null) {
            value = BigInteger.valueOf(3L);
        }

        return Status.FROM_ID(value);
    }

    @PermitAll
    public List<Component> findWithDestinations(BigInteger destinationId, BigInteger categoryId,
                                                BigInteger systemId,
                                                BigInteger regionId,
                                                String componentName, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root);
        Join<Component, SystemEntity> systems = root.join("system");
        Join<Component, Region> regions = root.join("region");
        List<Predicate> filters = new ArrayList<>();
        if (destinationId != null) {
            Join<Component, BeamDestination> destinations = root.join("beamDestinationList");
            filters.add(destinations.in(destinationId));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, null);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(root.get("system").in(systemId));
        }
        if (regionId != null) {
            filters.add(root.get("region").in(regionId));
        }
        if (componentName != null && !componentName.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = systems.get("name");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        Path p1 = regions.get("name");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        Path p2 = root.get("weight");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        Path p3 = root.get("name");
        Order o3 = cb.asc(p3);
        orders.add(o3);
        cq.orderBy(orders);
        List<Component> components
                = getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();

        for (Component c : components) {
            JPAUtil.initialize(c.getBeamDestinationList());
            /* Tickle collection to initialize, yes this is very expensive */

        }

        return components;
    }

    @PermitAll
    public List<Component> findWithoutSignoff(BigInteger[] destinationIdArray, BigInteger systemId,
                                              BigInteger[] regionIdArray, BigInteger groupId, BigInteger[] statusIdArray,
                                              Boolean readyTurn,
                                              String name, Date minLastModifiedDate, Date maxLastModifiedDate) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);
        cq.select(root);
        Join<Component, SystemEntity> systems = root.join("system");
        Join<Component, Region> regions = root.join("region");
        List<Predicate> filters = new ArrayList<>();

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);
        regionIdArray = IOUtil.removeNullValues(regionIdArray, BigInteger.class);
        statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }

        if (systemId != null) {
            filters.add(cb.equal(root.<BigInteger>get("system"), systemId));
        }
        if (regionIdArray != null && regionIdArray.length > 0) {
            filters.add(root.<BigInteger>get("region").in(Arrays.asList(regionIdArray)));
        }
        if (groupId != null && statusIdArray != null && statusIdArray.length > 0) {
            Subquery<ComponentSignoff> subquery = cq.subquery(ComponentSignoff.class);
            Root<ComponentSignoff> subqueryRoot = subquery.from(ComponentSignoff.class);
            subquery.select(subqueryRoot.get("componentId"));
            Predicate p1 = cb.equal(subqueryRoot.<BigInteger>get("groupId"), groupId);
            Predicate p2 = subqueryRoot.<BigInteger>get("statusId").in(Arrays.asList(statusIdArray));
            subquery.where(cb.and(p1, p2));
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (readyTurn != null) {
            Subquery<ReadyTurn> subquery = cq.subquery(ReadyTurn.class);
            Root<ReadyTurn> subqueryRoot = subquery.from(ReadyTurn.class);
            subquery.select(subqueryRoot.get("componentId"));
            subquery.where(cb.equal(subqueryRoot.<BigInteger>get("groupId"), groupId));
            Predicate p = cb.in(root.get("componentId")).value(subquery);
            if (!readyTurn) {
                p = cb.not(p);
            }
            filters.add(p);
        }
        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("name")), name.toLowerCase()));
        }
        if (minLastModifiedDate != null) {
            Subquery<GroupSignoff> subquery = cq.subquery(GroupSignoff.class);
            Root<GroupSignoff> subqueryRoot = subquery.from(GroupSignoff.class);
            subquery.select(subqueryRoot.get("componentId"));
            Predicate p1 = cb.greaterThanOrEqualTo(subqueryRoot.get("modifiedDate"),
                    minLastModifiedDate);
            Predicate p2 = cb.equal(subqueryRoot.<BigInteger>get("groupId"), groupId);
            subquery.where(cb.and(p1, p2));
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (maxLastModifiedDate != null) {
            Subquery<GroupSignoff> subquery = cq.subquery(GroupSignoff.class);
            Root<GroupSignoff> subqueryRoot = subquery.from(GroupSignoff.class);
            subquery.select(subqueryRoot.get("componentId"));
            Predicate p1 = cb.lessThanOrEqualTo(subqueryRoot.get("modifiedDate"),
                    maxLastModifiedDate);
            Predicate p2 = cb.equal(subqueryRoot.<BigInteger>get("groupId"), groupId);
            subquery.where(cb.and(p1, p2));
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p2 = root.get("weight");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        Path p3 = root.get("name");
        Order o3 = cb.asc(p3);
        orders.add(o3);
        cq.orderBy(orders);
        List<Component> components = getEntityManager().createQuery(cq).getResultList();

        return components;
    }

    @PermitAll
    public List<Component> search(String term, int maxResults) {
        TypedQuery<Component> q = em.createQuery(
                "select c from Component c where upper(name) like :term order by name asc",
                Component.class);

        q.setParameter("term", term.toUpperCase() + "%");

        return q.setMaxResults(maxResults).getResultList();
    }

    @PermitAll
    public List<Component> findMasked(BigInteger[] destinationIdArray, BigInteger categoryId,
                                      BigInteger systemId,
                                      BigInteger regionId, BigInteger groupId, String reason, int offset, int maxPerPage) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Component> cq = cb.createQuery(Component.class);
        Root<Component> root = cq.from(Component.class);

        List<Predicate> filters = new ArrayList<>();

        filters.add(cb.equal(root.get("maskedStr"), "Y"));

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("system"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("region"), regionId));
        }
        if (groupId != null) {
            Join<Component, SystemEntity> systems = root.join("system");
            Join<Category, GroupResponsibility> responsibilities = systems.join(
                    "groupResponsibilityList");
            filters.add(cb.equal(responsibilities.get("group"), groupId));
        }
        if (reason != null && !reason.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("maskedComment")), reason.toLowerCase()));
        }

        Join<Component, SystemEntity> system = root.join("system");
        Join<SystemEntity, Application> application = system.join("applicationList");
        filters.add(cb.equal(application.get("applicationId"), 1));
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("maskedDate");
        Order o0 = cb.desc(p0);
        orders.add(o0);
        Path p1 = root.get("name");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        cq.orderBy(orders);

        cq.select(root);
        TypedQuery<Component> q
                = getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(maxPerPage);
        List<Component> componentList = q.getResultList();

        // These are EAGER fetched so don't need to worry about 'em
        /*if (componentList != null) {
         for (Component c : componentList) {
         JPAUtil.initialize(c.getSystem());
         JPAUtil.initialize(c.getRegion());
         }
         }*/
        return componentList;
    }

    @PermitAll
    public Long countMasked(BigInteger[] destinationIdArray, BigInteger categoryId,
                            BigInteger systemId,
                            BigInteger regionId,
                            BigInteger groupId, String reason, BigInteger[] statusIdArray) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Component> root = cq.from(Component.class);

        List<Predicate> filters = new ArrayList<>();

        filters.add(cb.equal(root.get("maskedStr"), "Y"));

        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            //Join<Component, BeamDestination> destinations = root.join("beamDestinationList");
            //filters.add(destinations.in((Object[]) destinationIdArray));

            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("componentId"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(root.get("componentId")).value(subquery));

        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("system"), systemId));
        }
        if (regionId != null) {
            filters.add(cb.equal(root.get("region"), regionId));
        }
        if (groupId != null) {
            Join<Component, SystemEntity> systems = root.join("system");
            Join<Category, GroupResponsibility> responsibilities = systems.join(
                    "groupResponsibilityList");
            filters.add(cb.equal(responsibilities.get("group"), groupId));
        }
        if (reason != null && !reason.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("maskedComment")), reason.toLowerCase()));
        }
        Join<Component, SystemEntity> system = root.join("system");
        Join<SystemEntity, Application> application = system.join("applicationList");
        filters.add(cb.equal(application.get("applicationId"), 1));

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

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getResultList().get(0);
    }

    @PermitAll
    public void addNew(String name, BigInteger systemId, BigInteger regionId, Boolean masked,
                       String maskedReason) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("Name must not be empty");
        }
        if (systemId == null) {
            throw new UserFriendlyException("System must not be empty");
        }
        if (regionId == null) {
            throw new UserFriendlyException("Region must not be empty");
        }
        if (masked == null) {
            masked = false;
        }

        SystemEntity system = systemFacade.find(systemId);

        if (system == null) {
            throw new UserFriendlyException("System with id: " + systemId + " not found");
        }

        Region region = regionFacade.find(regionId);

        if (region == null) {
            throw new UserFriendlyException("Region with id: " + regionId + " not found");
        }

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        // Trim off spaces before and after name
        name = name.trim();

        Component c = new Component();
        c.setName(name);
        c.setSystem(system);
        c.setRegion(region);
        c.setDataSource(DataSource.INTERNAL);
        c.setMasked(masked);
        c.setAddedDate(new Date());

        if (masked) {
            c.setMaskedComment(maskedReason);
            c.setMaskedDate(new Date());
            c.setMaskedBy(username);
        }

        // We always set Unpowered to 'N' for new components
        c.setUnpowered(false);

        create(c);

        setInitialStatus(c, system, username);
    }

    private void setInitialStatus(Component component, SystemEntity system, String username) throws UserFriendlyException {
        BigInteger componentId = component.getComponentId();
        List<GroupResponsibility> responsibilityList = system.getGroupResponsibilityList();
        String comment = "New component added";
        Date signoffDate = new Date();

        if (responsibilityList != null) {
            for (GroupResponsibility gr : responsibilityList) {
                ResponsibleGroup group = gr.getGroup();
                groupSignoffFacade.updateSignoff(componentId, group.getGroupId(), Status.NOT_READY, comment, new GroupSignoffFacade.SignoffCascadeRule(), new GroupSignoffFacade.SignoffValidateRule(), signoffDate, username);
            }
        }
    }

    @PermitAll
    public void delete(BigInteger componentId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("ComponentId must not be empty");
        }

        Component c = find(componentId);

        if (c == null) {
            throw new UserFriendlyException("Component with ID: " + componentId + " not found");
        }

        Category branchRoot = categoryFacade.findBranchRoot(c.getSystem().getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        remove(c);
    }

    @PermitAll
    public List<Component> findMustFilter(BigInteger[] categoryIdArray, BigInteger[] systemIdArray, String q, BigInteger regionId,
                                          BigInteger componentId, BigInteger destinationId, BigInteger applicationId, Integer max,
                                          Integer offset, Boolean autoWild) {
        List<Component> componentList;

        categoryIdArray = IOUtil.removeNullValues(categoryIdArray, BigInteger.class);
        systemIdArray = IOUtil.removeNullValues(systemIdArray, BigInteger.class);

        if (max == null && categoryIdArray == null && systemIdArray == null && (q == null || q.isEmpty()) && regionId == null
                && componentId
                == null && destinationId == null) {
            componentList = new ArrayList<>(); // Return empty list if no filter provided
        } else {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Component> cq = cb.createQuery(Component.class);
            Root<Component> root = cq.from(Component.class);
            cq.select(root);

            List<Predicate> filters = new ArrayList<>();

            if(categoryIdArray != null && categoryIdArray.length > 0) {
                Set<BigInteger> systemSet = new LinkedHashSet<>();
                for(BigInteger categoryId: categoryIdArray) {
                    List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, applicationId);
                    for(SystemEntity entity: systemList) {
                        systemSet.add(entity.getSystemId());
                    }
                }

                if(!systemSet.isEmpty()) {
                    if(systemIdArray != null) {
                        systemSet.addAll(Arrays.asList(systemIdArray));
                    }

                    systemIdArray = systemSet.toArray(new BigInteger[]{});
                }
            }

            if (systemIdArray != null && systemIdArray.length > 0) {
                filters.add(root.get("system").in((Object[])systemIdArray));
            }
            if (q != null && !q.isEmpty()) {
                String searchString = q.toUpperCase();

                if (autoWild == null || autoWild) {
                    searchString = "%" + searchString + "%";
                }

                Predicate p1 = cb.like(cb.upper(root.get("name")), searchString);
                Predicate p2 = cb.like(cb.upper(root.get("nameAlias")), searchString);

                filters.add(cb.or(p1, p2));
            }
            if (regionId != null) {
                filters.add(cb.equal(root.get("region"), regionId));
            }
            if (componentId != null) {
                filters.add(cb.equal(root.get("componentId"), componentId));
            }
            if (destinationId != null) {
                Join<Component, BeamDestination> destinations = root.join("beamDestinationList");
                filters.add(destinations.in(destinationId));
            }
            if (applicationId != null) {
                Join<Component, SystemEntity> system = root.join("system");
                Join<SystemEntity, Application> application = system.join("applicationList");
                filters.add(cb.equal(application.get("applicationId"), applicationId));
            }
            if (!filters.isEmpty()) {
                cq.where(cb.and(filters.toArray(new Predicate[]{})));
            }
            List<Order> orders = new ArrayList<>();
            /*Path p0 = root.get("weight");
             Order o0 = cb.asc(p0);
             orders.add(o0);*/
            Path p1 = root.get("name");
            Order o1 = cb.asc(p1);
            orders.add(o1);
            cq.orderBy(orders);

            TypedQuery<Component> query = getEntityManager().createQuery(cq);

            if (max != null) {
                query.setMaxResults(max);
            }

            if (offset != null) {
                query.setFirstResult(offset);
            }

            componentList = query.getResultList();
        }

        return componentList;
    }

    @PermitAll
    public void editMasked(BigInteger componentId, Boolean masked, String maskedReason,
                           Date expiration, int maskTypeId) throws
            UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("component must not be empty");
        }
        if (masked == null) {
            throw new UserFriendlyException("masked must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("component with id: " + componentId + " not found");
        }

        // If Crew Chief then keep going, otherwise check if Admin or Branch Admin
        if (!isCrewChief()) {
            Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
            checkAdminOrBranchAdmin(username, branchRoot);
        }

        component.setMasked(masked);

        if (masked) {
            if (expiration != null) {
                Date now = new Date();
                if (expiration.before(now)) {
                    throw new UserFriendlyException("Please provide a future expiration date");
                }
            }

            component.setMaskedComment(maskedReason);
            component.setMaskedDate(new Date());
            component.setMaskedBy(username);
            component.setMaskExpirationDate(expiration);
            component.setMaskTypeId(maskTypeId);
            String comment = "Masked";
            resetSignoffsToMasked(component, comment, username, maskTypeId);
        } else {
            component.setMaskedComment(null);
            component.setMaskedDate(null);
            component.setMaskedBy(null);
            component.setMaskExpirationDate(null);
            component.setMaskTypeId(null);
            String comment = "Auto-signoff due to unmasking";

            if (maskedReason != null && !maskedReason.isEmpty()) {
                comment = maskedReason;
            }

            if (maskTypeId == 100) {
                resetSignoffsToNotReady(component, comment, username);
            } else {
                resetSignoffsToChecked(component, comment, username);
            }

        }
    }

    /**
     * Changes a component's system while attempting to maintain signoffs and
     * signoff history.
     * <p>
     * If the old and new system are not the same then check if the old and new
     * systems share any responsible groups. If they do then update the signoffs
     * of the same groups over to the new system. This will require deferring
     * constraints to wait until the end of the transaction.
     *
     * @param component The component
     * @param newSystem The new system
     * @param username  The username
     * @throws UserFriendlyException
     */

    @PermitAll
    public void forceSystemChange(Component component, SystemEntity newSystem, String username) throws UserFriendlyException {
        SystemEntity oldSystem = component.getSystem();

        if (!oldSystem.equals(newSystem)) {
            component.setSystem(newSystem);
            /*Temporary Constraint Violation*/

            List<GroupResponsibility> oldResponsibilityList = oldSystem.getGroupResponsibilityList();
            List<GroupResponsibility> newResponsibilityList = newSystem.getGroupResponsibilityList();

            // List of groups that have a totally new responsibility and should be notified.
            List<ResponsibleGroup> groupsToNotify = new ArrayList<>();

            /*First we reassign all signoffs from groups which overlap*/
            for (GroupResponsibility newResponsibility : newResponsibilityList) {
                ResponsibleGroup group = newResponsibility.getGroup();
                /*System.out.println("Looking at new responsibility: " + newResponsibility);*/

                boolean found = false;
                for (GroupResponsibility oldResponsibility : oldResponsibilityList) {
                    /*System.out.println("Comparing with old responsibility: " + oldResponsibility);*/
                    if (group.equals(oldResponsibility.getGroup())) {
                        /*Temporary Constraint Violation to Reassign Signoffs/History*/
 /*System.out.println("Reassigning Signoff: " + newSystem.getName() + "; "
                         + component.getName() + "; " + group.getName());*/
                        signoffFacade.reassignGroupSignoffs(component, group, newSystem);
                        found = true;
                        break;
                    }
                }

                // We have a new responsibility that didn't exist before on this component
                if (!found) {
                    groupsToNotify.add(group);
                }
            }

            /*Second we delete all signoffs which do not overlap (still have old system)*/
 /*System.out.println("Deleting Old Signoff: " + oldSystem.getName() + "; "
             + component.getName());*/
            /*Delete group signoffs which don't overlap new system assignment*/
            signoffFacade.deleteGroupSignoffs(component, oldSystem);

            // Finally, notify any groups with new responsibility by logging initial NOT_READY signoffs
            for (ResponsibleGroup g : groupsToNotify) {
                String comment = "New group responsibility assignment due to component system change";

                groupResponsibilityFacade.setInitialComponentSignoffs(component, g, comment, username);
            }
        }
    }

    @PermitAll
    public void editComponent(BigInteger systemId, BigInteger componentId, String name,
                              BigInteger regionId, boolean force) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        if (name == null) {
            throw new UserFriendlyException("Name must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        if (systemId == null) {
            throw new UserFriendlyException("System must not be empty");
        }

        SystemEntity system = systemFacade.find(systemId);

        if (system == null) {
            throw new UserFriendlyException("System with ID: " + systemId + " not found");
        }

        if (regionId == null) {
            throw new UserFriendlyException("Region must not be empty");
        }

        Region region = regionFacade.find(regionId);

        if (region == null) {
            throw new UserFriendlyException("Region with ID: " + regionId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        /*Second see if user is an admin over new system*/
        branchRoot = categoryFacade.findBranchRoot(system.getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        if (force) {
            forceSystemChange(component, system, username);
        } else {
            component.setSystem(system);
        }

        component.setName(name);
        component.setRegion(region);
    }

    @PermitAll
    public List<ComponentSignoff> findByComponent(BigInteger componentId) {
        TypedQuery<ComponentSignoff> q = em.createQuery(
                "select c from ComponentSignoff c where c.componentId = :componentId",
                ComponentSignoff.class);

        q.setParameter("componentId", componentId);

        return q.getResultList();
    }

    @PermitAll
    public void editException(BigInteger[] componentIdArray, String reason, Date expirationDate) throws
            UserFriendlyException {
        String username = checkAuthenticated();

        if (componentIdArray == null || componentIdArray.length == 0) {
            throw new UserFriendlyException("component ID array must not be empty");
        }

        boolean masked = reason != null && !reason.trim().isEmpty();

        for (BigInteger componentId : componentIdArray) {
            if (componentId == null) {
                throw new UserFriendlyException("component ID must not be empty");
            }

            this.editMasked(componentId, masked, reason, expirationDate, Status.MASKED_CC.getStatusId().intValue());
        }
    }

    @PermitAll
    public void removeException(BigInteger[] componentIdArray, Status status, String comment) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentIdArray == null || componentIdArray.length == 0) {
            throw new UserFriendlyException("component ID array must not be empty");
        }

        boolean masked = false;

        for (BigInteger componentId : componentIdArray) {
            if (componentId == null) {
                throw new UserFriendlyException("component ID must not be empty");
            }

            this.editMasked(componentId, masked, comment, null, status.getStatusId().intValue());
        }
    }

    @PermitAll
    public Long countMustFilter(BigInteger[] categoryIdArray, BigInteger[] systemIdArray, String q, BigInteger regionId,
                                BigInteger componentId, BigInteger destinationId, BigInteger applicationId,
                                Boolean autoWild, Integer max) {
        Long count;

        categoryIdArray = IOUtil.removeNullValues(categoryIdArray, BigInteger.class);
        systemIdArray = IOUtil.removeNullValues(systemIdArray, BigInteger.class);

        if (max == null && categoryIdArray == null && systemIdArray == null && (q == null || q.isEmpty()) && regionId == null
                && componentId
                == null && destinationId == null) {
            count = 0L; // Return zero if no filter provided
        } else {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Component> root = cq.from(Component.class);

            List<Predicate> filters = new ArrayList<>();

            if(categoryIdArray != null && categoryIdArray.length > 0) {
                Set<BigInteger> systemSet = new LinkedHashSet<>();
                for(BigInteger categoryId: categoryIdArray) {
                    List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, applicationId);
                    for(SystemEntity entity: systemList) {
                        systemSet.add(entity.getSystemId());
                    }
                }

                if(!systemSet.isEmpty()) {
                    if(systemIdArray != null) {
                        systemSet.addAll(Arrays.asList(systemIdArray));
                    }

                    systemIdArray = systemSet.toArray(new BigInteger[]{});
                }
            }

            if (systemIdArray != null && systemIdArray.length > 0) {
                filters.add(root.get("system").in((Object[])systemIdArray));
            }
            if (q != null && !q.isEmpty()) {
                String searchString = q.toUpperCase();

                if (autoWild == null || autoWild) {
                    searchString = "%" + searchString + "%";
                }

                filters.add(cb.like(cb.upper(root.get("name")), searchString));
            }
            if (regionId != null) {
                filters.add(cb.equal(root.get("region"), regionId));
            }
            if (componentId != null) {
                filters.add(cb.equal(root.get("componentId"), componentId));
            }
            if (destinationId != null) {
                Join<Component, BeamDestination> destinations = root.join("beamDestinationList");
                filters.add(destinations.in(destinationId));
            }
            if (applicationId != null) {
                Join<Component, SystemEntity> system = root.join("system");
                Join<SystemEntity, Application> application = system.join("applicationList");
                filters.add(cb.equal(application.get("applicationId"), applicationId));
            }
            if (!filters.isEmpty()) {
                cq.where(cb.and(filters.toArray(new Predicate[]{})));
            }

            cq.select(cb.count(root));
            TypedQuery<Long> query = getEntityManager().createQuery(cq);
            count = query.getSingleResult();
        }
        return count;
    }

    @PermitAll
    public void rename(BigInteger componentId, String name) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("Name must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        // Trim off spaces before and after name
        name = name.trim();

        component.setName(name);
    }

    @PermitAll
    public void setUnpowered(BigInteger componentId, boolean unpowered) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        if (component.isUnpowered() == unpowered) {
            LOGGER.log(Level.FINEST, "Not changing unpowered state");
            return;
        }

        component.setUnpowered(unpowered);

        String comment = "Unpowered changed to " + (unpowered ? "Yes" : "No");

        //List<GroupResponsibility> responsibilityList = component.getSystem().getGroupResponsibilityList();
        resetSignoffsToNotReady(component, comment, username);
    }

    private void resetSignoffsToMasked(Component component, String comment, String username, int maskTypeId) {
        List<GroupSignoff> signoffList = component.getGroupSignoffList();

        Date modifiedDate = new Date();

        for (GroupSignoff signoff : signoffList) {
            signoff.setStatus(Status.FROM_ID(BigInteger.valueOf(maskTypeId)));
            signoff.setComments(comment);
            signoff.setModifiedDate(modifiedDate);
            signoff.setModifiedBy(username);
            signoff.setChangeType(SignoffChangeType.COMMENT);

            groupSignoffHistoryFacade.newHistory(signoff);
        }
    }

    private void resetSignoffsToNotReady(Component component, String comment, String username) {
        List<GroupSignoff> signoffList = component.getGroupSignoffList();

        Date modifiedDate = new Date();

        for (GroupSignoff signoff : signoffList) {
            switch (signoff.getStatus().getName()) {
                case "Not Applicable":
                case "Not Ready":
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.COMMENT);
                    break;
                default:
                    signoff.setStatus(Status.NOT_READY);
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.DOWNGRADE);
                    break;
            }

            groupSignoffHistoryFacade.newHistory(signoff);
        }
    }

    private void resetSignoffsToChecked(Component component, String comment, String username) {
        List<GroupSignoff> signoffList = component.getGroupSignoffList();

        Date modifiedDate = new Date();

        for (GroupSignoff signoff : signoffList) {
            switch (signoff.getStatus().getName()) {
                case "Not Applicable":
                case "Not Ready":
                    signoff.setStatus(Status.CHECKED);
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.UPGRADE);
                    break;
                case "Checked":
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.COMMENT);
                    break;
                default:
                    signoff.setStatus(Status.CHECKED);
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.DOWNGRADE);
                    break;
            }

            groupSignoffHistoryFacade.newHistory(signoff);
        }
    }

    public void setGroupSignoffToNotReady(Component component, ResponsibleGroup group, String comment, String username) {
        Date modifiedDate = new Date();

        List<GroupSignoff> signoffList = component.getGroupSignoffList();

        for (GroupSignoff signoff : signoffList) {
            switch (signoff.getStatus().getName()) {
                case "Not Applicable":
                case "Not Ready":
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.COMMENT);
                    break;
                default:
                    signoff.setStatus(Status.NOT_READY);
                    signoff.setComments(comment);
                    signoff.setModifiedDate(modifiedDate);
                    signoff.setModifiedBy(username);
                    signoff.setChangeType(SignoffChangeType.DOWNGRADE);
                    break;
            }

            groupSignoffHistoryFacade.newHistory(signoff);
        }
    }

    @PermitAll
    public void setSource(BigInteger componentId, DataSource source, BigInteger sourceId)
            throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        if (source == null) {
            throw new UserFriendlyException("DataSource must not be empty");
        }

        if (source == DataSource.INTERNAL && sourceId != null) {
            sourceId = null;
        } else if (source != DataSource.INTERNAL && sourceId == null) {
            throw new UserFriendlyException("Source ID must not be empty unless INTERNAL");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        if (component.getDataSource() == source && component.getDataSourceId() == sourceId) {
            LOGGER.log(Level.FINEST, "Not changing data source state");
            return;
        }

        component.setDataSource(source);
        component.setDataSourceId(sourceId);
    }

    @PermitAll
    public void setSystem(BigInteger componentId, BigInteger systemId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        if (systemId == null) {
            throw new UserFriendlyException("System must not be empty");
        }

        SystemEntity system = systemFacade.find(systemId);

        if (system == null) {
            throw new UserFriendlyException("System with ID: " + systemId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        if (component.getSystem().equals(system)) {
            LOGGER.log(Level.FINEST, "Not changing system");
            return;
        }

        forceSystemChange(component, system, username);
    }

    @PermitAll
    public void setRegion(BigInteger componentId, BigInteger regionId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        if (regionId == null) {
            throw new UserFriendlyException("Region must not be empty");
        }

        Region region = regionFacade.find(regionId);

        if (region == null) {
            throw new UserFriendlyException("Region with ID: " + regionId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        if (component.getRegion().equals(region)) {
            LOGGER.log(Level.FINEST, "Not changing region");
            return;
        }

        component.setRegion(region);
    }

    @PermitAll
    public void setAlias(BigInteger componentId, String alias) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("Component must not be empty");
        }

        Component component = find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with id: " + componentId + " not found");
        }

        /*First see if user is an admin over current system*/
        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());
        checkAdminOrBranchAdmin(username, branchRoot);

        component.setNameAlias(alias);
    }

    @PermitAll
    public void bulkAddNew(String names, BigInteger systemId, BigInteger regionId) throws
            UserFriendlyException {
        if (names == null || names.trim().isEmpty()) {
            throw new UserFriendlyException("Component names must not be empty");
        }

        String[] tokens = names.split("\\n");

        for (String token : tokens) {
            this.addNew(token, systemId, regionId, false, null);
        }
    }

    @PermitAll
    public void expireMasks() {
        LOGGER.log(Level.FINEST, "Checking for masks to expire...");
        TypedQuery<Component> q = em.createQuery(
                "select c from Component c where c.maskExpirationDate < function('sysdate') ", Component.class);

        List<Component> componentList = q.getResultList();

        if (componentList != null) {
            for (Component c : componentList) {
                c.setMasked(false);
                c.setMaskedComment(null);
                c.setMaskedDate(null);
                c.setMaskedBy(null);
                c.setMaskExpirationDate(null);
                String comment = "Auto-downgrade due to unmasking by expiration";
                resetSignoffsToNotReady(c, comment, "srm-admin");
                LOGGER.log(Level.FINE, "Mask expired for component: {0}", c.getName());
            }
        }
    }
}
