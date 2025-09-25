package org.jlab.srm.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.entity.view.ComponentStatusRecord;

/**
 * @author ryans
 */
@Stateless
public class ComponentStatusFacade extends AbstractFacade<ComponentStatusRecord> {

  @EJB SystemFacade systemFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public ComponentStatusFacade() {
    super(ComponentStatusRecord.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  /**
   * If status isn't involved we can query the much faster component table vs the component_status
   * view.
   *
   * @param destinationIdArray
   * @param categoryId
   * @param systemId
   * @param regionId
   * @param groupId
   * @param unpowered
   * @param componentName
   * @param offset
   * @param max
   * @return
   */
  @PermitAll
  public List<ComponentStatusRecord> filterListOptimized(
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      Boolean unpowered,
      String componentName,
      int offset,
      int max) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<BigInteger> cq = cb.createQuery(BigInteger.class);
    Root<Component> root = cq.from(Component.class);

    Expression<BigInteger> id = root.get("componentId");

    cq.select(id); // .distinct(true);
    List<Predicate> filters = new ArrayList<>();

    destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

    if (destinationIdArray != null && destinationIdArray.length > 0) {
      Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
      Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
      Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
      subquery.select(componentList.get("componentId"));
      Predicate p1 =
          subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
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
      Join<ComponentStatusRecord, SystemEntity> system = root.join("system");
      Join<SystemEntity, GroupResponsibility> responsibilities =
          system.join("groupResponsibilityList");
      filters.add(responsibilities.get("group").in(groupId));
    }
    if (unpowered != null) {
      filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
    }
    if (componentName != null && !componentName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
    }
    Join<Component, SystemEntity> system = root.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p1 = root.get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    cq.orderBy(orders);

    List<BigInteger> ids =
        em.createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();

    List<ComponentStatusRecord> resultList = null;

    if (ids != null && !ids.isEmpty()) {
      TypedQuery<ComponentStatusRecord> q =
          em.createQuery(
              "select a from ComponentStatusRecord a where a.componentId in :idList",
              ComponentStatusRecord.class);

      q.setParameter("idList", ids);

      resultList = q.getResultList();
    }

    return resultList;
  }

  @PermitAll
  public List<ComponentStatusRecord> filterList(
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      BigInteger statusId,
      Boolean unpowered,
      String componentName,
      int offset,
      int max) {
    if (statusId == null) {
      return filterListOptimized(
          destinationIdArray,
          categoryId,
          systemId,
          regionId,
          groupId,
          unpowered,
          componentName,
          offset,
          max);
    }

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ComponentStatusRecord> cq = cb.createQuery(ComponentStatusRecord.class);
    Root<ComponentStatusRecord> root = cq.from(ComponentStatusRecord.class);
    cq.select(root); // .distinct(true);
    List<Predicate> filters = new ArrayList<>();
    destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

    if (destinationIdArray != null && destinationIdArray.length > 0) {
      Join<ComponentStatusRecord, Component> component = root.join("component");
      Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
      Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
      Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
      subquery.select(componentList.get("componentId"));
      Predicate p1 =
          subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
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
      Join<ComponentStatusRecord, SystemEntity> system = root.join("system");
      Join<SystemEntity, GroupResponsibility> responsibilities =
          system.join("groupResponsibilityList");
      filters.add(responsibilities.get("group").in(groupId));
    }
    if (statusId != null) {
      filters.add(cb.equal(root.get("statusId"), statusId));
    }
    if (unpowered != null) {
      filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
    }
    if (componentName != null && !componentName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
    }
    Join<Component, SystemEntity> system = root.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p1 = root.get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    cq.orderBy(orders);
    return em.createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    // return findAll(new OrderDirective("groupSignoffHistoryId", false));
  }

  /**
   * If status isn't involved we can query the much faster component table vs the component_status
   * view.
   *
   * @param destinationIdArray
   * @param categoryId
   * @param systemId
   * @param regionId
   * @param groupId
   * @param unpowered
   * @param componentName
   * @return
   */
  @PermitAll
  public Long countFilterListOptimized(
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      Boolean unpowered,
      String componentName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    Root<Component> root = cq.from(Component.class);
    List<Predicate> filters = new ArrayList<>();
    destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

    if (destinationIdArray != null && destinationIdArray.length > 0) {
      Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
      Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
      Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
      subquery.select(componentList.get("componentId"));
      Predicate p1 =
          subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
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
      Join<ComponentStatusRecord, SystemEntity> system = root.join("system");
      Join<SystemEntity, GroupResponsibility> responsibilities =
          system.join("groupResponsibilityList");
      filters.add(responsibilities.get("group").in(groupId));
    }
    if (unpowered != null) {
      filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
    }
    if (componentName != null && !componentName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
    }
    Join<Component, SystemEntity> system = root.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root)); // .distinct(true);
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @PermitAll
  public Long countFilterList(
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      BigInteger statusId,
      Boolean unpowered,
      String componentName) {
    if (statusId == null) {
      return countFilterListOptimized(
          destinationIdArray, categoryId, systemId, regionId, groupId, unpowered, componentName);
    }

    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    Root<ComponentStatusRecord> root = cq.from(ComponentStatusRecord.class);
    List<Predicate> filters = new ArrayList<>();
    destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

    if (destinationIdArray != null && destinationIdArray.length > 0) {
      Join<ComponentStatusRecord, Component> component = root.join("component");
      Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
      Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
      Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
      subquery.select(componentList.get("componentId"));
      Predicate p1 =
          subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
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
      Join<ComponentStatusRecord, SystemEntity> system = root.join("system");
      Join<SystemEntity, GroupResponsibility> responsibilities =
          system.join("groupResponsibilityList");
      filters.add(responsibilities.get("group").in(groupId));
    }
    if (statusId != null) {
      filters.add(cb.equal(root.get("statusId"), statusId));
    }
    if (unpowered != null) {
      filters.add(cb.equal(root.get("unpoweredStr"), unpowered ? "Y" : "N"));
    }
    if (componentName != null && !componentName.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("name")), componentName.toLowerCase()));
    }
    Join<Component, SystemEntity> system = root.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root)); // .distinct(true);
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
