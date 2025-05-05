package org.jlab.srm.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.util.JPAUtil;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.srm.persistence.entity.GroupResponsibility;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.enumeration.Include;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles("srm-admin")
public class ResponsibleGroupFacade extends AbstractFacade<ResponsibleGroup> {

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public ResponsibleGroupFacade() {
    super(ResponsibleGroup.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public ResponsibleGroup findDetail(BigInteger groupId) {
    ResponsibleGroup group = find(groupId);

    if (group != null) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
      group.setLeaders(userList);

      JPAUtil.initialize(group.getGroupResponsibilityList());
    }

    return group;
  }

  @PermitAll
  public ResponsibleGroup findWithLeaderList(BigInteger groupId) {
    ResponsibleGroup group = find(groupId);

    if (group != null) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
      group.setLeaders(userList);
    }

    return group;
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<ResponsibleGroup> root,
      Include includeArchived) {
    List<Predicate> filters = new ArrayList<>();

    if (includeArchived == null) {
      filters.add(cb.equal(root.get("archived"), false));
    } else if (Include.EXCLUSIVELY == includeArchived) {
      filters.add(cb.equal(root.get("archived"), true));
    } // else Include.YES, which means don't filter at all

    return filters;
  }

  @PermitAll
  public List<ResponsibleGroup> filterList(Include includeArchived, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<ResponsibleGroup> cq = cb.createQuery(ResponsibleGroup.class);
    Root<ResponsibleGroup> root = cq.from(ResponsibleGroup.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, cq, root, includeArchived);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public List<ResponsibleGroup> findAllWithLeaderList(Include includeArchived) {
    List<ResponsibleGroup> groupList = filterList(includeArchived, 0, Integer.MAX_VALUE);

    for (ResponsibleGroup group : groupList) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
      group.setLeaders(userList);
    }

    return groupList;
  }

  @RolesAllowed("srm-admin")
  public void updateGoals(List<BigInteger> groupIdList, List<Integer> percentList) {
    if (groupIdList != null) {
      for (int i = 0; i < groupIdList.size(); i++) {
        BigInteger groupId = groupIdList.get(i);
        Integer percent = percentList.get(i);
        ResponsibleGroup group = find(groupId);
        group.setGoalPercent(percent);
      }
    }
  }

  @PermitAll
  public List<ResponsibleGroup> findWithLeaders(BigInteger systemId) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<ResponsibleGroup> cq = cb.createQuery(ResponsibleGroup.class);
    Root<ResponsibleGroup> root = cq.from(ResponsibleGroup.class);
    cq.select(root);

    List<Predicate> filters = new ArrayList<>();
    List<Order> orders = new ArrayList<>();

    if (systemId != null) {
      Join<ResponsibleGroup, GroupResponsibility> responsibilities =
          root.join("groupResponsibilityList");
      filters.add(responsibilities.get("system").in(systemId));
      Path p0 = responsibilities.get("weight");
      Order o0 = cb.asc(p0);
      orders.add(o0);
    }
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    Path p1 = root.get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);

    cq.orderBy(orders);

    List<ResponsibleGroup> groupList = getEntityManager().createQuery(cq).getResultList();

    for (ResponsibleGroup group : groupList) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
      group.setLeaders(userList);
    }

    return groupList;
  }

  @SuppressWarnings("unchecked")
  @PermitAll
  public List<ResponsibleGroup> findBySystem(BigInteger systemId) {
    if (systemId == null) {
      return findAll(new OrderDirective("name"));
    }

    Query q =
        em.createNativeQuery(
            "select a.* from responsible_group a, group_responsibility b where a.group_id = b.group_id and b.system_id = :systemId and a.archived_yn = 'N' order by b.weight, a.name asc",
            ResponsibleGroup.class);

    q.setParameter("systemId", systemId);

    return q.getResultList();
  }

  @RolesAllowed("srm-admin")
  public void add(String name, String description, String leaderWorkgroup, boolean archived)
      throws UserFriendlyException {

    if (name == null || name.isEmpty()) {
      throw new UserFriendlyException("name must not be empty");
    }

    if (description == null || description.isEmpty()) {
      throw new UserFriendlyException("description must not be empty");
    }

    if (leaderWorkgroup == null) {
      throw new UserFriendlyException("leader workgroup must not be empty");
    }

    ResponsibleGroup group = new ResponsibleGroup();

    group.setName(name);
    group.setDescription(description);
    group.setLeaderWorkgroup(leaderWorkgroup);
    group.setArchived(archived);

    createSpecial(group);
  }

  /**
   * HACK - Can't get ENVERS to behave.
   *
   * @param group
   */
  private void createSpecial(ResponsibleGroup group) {
    Query q =
        em.createNativeQuery(
            "insert into responsible_group (group_id, name, description, goal_percent, leader_workgroup, archived_yn) values (group_id.nextval, ?, ?, ?, ?, ?)");

    q.setParameter(1, group.getName());
    q.setParameter(2, group.getDescription());
    q.setParameter(3, group.getGoalPercent());
    q.setParameter(4, group.getLeaderWorkgroup());
    q.setParameter(5, group.isArchived() ? "Y" : "N");

    q.executeUpdate();
  }

  @RolesAllowed("srm-admin")
  public void delete(BigInteger groupId) throws UserFriendlyException {
    if (groupId == null) {
      throw new UserFriendlyException("Group ID must not be empty");
    }

    ResponsibleGroup group = find(groupId);

    if (group == null) {
      throw new UserFriendlyException("Group with ID " + groupId + " not found");
    }

    removeSpecial(group);
  }

  /**
   * ENVERS HACK
   *
   * @param group
   */
  private void removeSpecial(ResponsibleGroup group) {
    Query q = em.createNativeQuery("delete from responsible_group where group_id = ?");

    q.setParameter(1, group.getGroupId());

    q.executeUpdate();
  }

  @RolesAllowed("srm-admin")
  public void edit(
      BigInteger groupId, String name, String description, String leaderWorkgroup, boolean archived)
      throws UserFriendlyException {

    if (groupId == null) {
      throw new UserFriendlyException("group ID must not be empty");
    }

    ResponsibleGroup group = find(groupId);

    if (group == null) {
      throw new UserFriendlyException("group with ID: " + groupId + " not found");
    }

    if (name == null || name.isEmpty()) {
      throw new UserFriendlyException("name must not be empty");
    }

    if (description == null || description.isEmpty()) {
      throw new UserFriendlyException("description must not be empty");
    }

    if (leaderWorkgroup == null) {
      throw new UserFriendlyException("leader workgroup must not be empty");
    }

    group.setName(name);
    group.setDescription(description);
    group.setLeaderWorkgroup(leaderWorkgroup);
    group.setArchived(archived);

    edit(group);
  }
}
