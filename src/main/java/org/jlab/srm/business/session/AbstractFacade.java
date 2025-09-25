package org.jlab.srm.business.session;

import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.ResponsibleGroup;

/**
 * @author ryans
 */
@DeclareRoles({"srm-admin", "cc"})
public abstract class AbstractFacade<T> {

  @Resource SessionContext context;
  private final Class<T> entityClass;

  public AbstractFacade(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected abstract EntityManager getEntityManager();

  public void create(T entity) {
    getEntityManager().persist(entity);
  }

  public void edit(T entity) {
    getEntityManager().merge(entity);
  }

  public void remove(T entity) {
    getEntityManager().remove(getEntityManager().merge(entity));
  }

  @PermitAll
  public T find(Object id) {
    return getEntityManager().find(entityClass, id);
  }

  @PermitAll
  public List<T> findAll() {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
    cq.select(cq.from(entityClass));
    return getEntityManager().createQuery(cq).getResultList();
  }

  @PermitAll
  public List<T> findAll(OrderDirective... directives) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(entityClass);
    Root<T> root = cq.from(entityClass);
    cq.select(root);
    List<Order> orders = new ArrayList<Order>();
    for (OrderDirective ob : directives) {
      Order o;

      Path p = root.get(ob.field);

      if (ob.asc) {
        o = cb.asc(p);
      } else {
        o = cb.desc(p);
      }

      orders.add(o);
    }
    cq.orderBy(orders);
    return getEntityManager().createQuery(cq).getResultList();
  }

  @PermitAll
  public List<T> findRange(int[] range) {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
    cq.select(cq.from(entityClass));
    TypedQuery<T> q = getEntityManager().createQuery(cq);
    q.setMaxResults(range[1] - range[0]);
    q.setFirstResult(range[0]);
    return q.getResultList();
  }

  @PermitAll
  public long count() {
    CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
    Root<T> rt = cq.from(entityClass);
    cq.select(getEntityManager().getCriteriaBuilder().count(rt));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected String checkAuthenticated() {
    String username = context.getCallerPrincipal().getName();
    if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
      throw new EJBAccessException("You must be authenticated to perform the requested operation");
    }

    return username;
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected void checkAdminOrGroupLeader(String username, ResponsibleGroup group, String message) {
    boolean isAdminOrLeader = isAdminOrGroupLeader(username, group);

    if (!isAdminOrLeader) {
      throw new EJBAccessException(message);
    }
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected void checkAdminOrGroupLeader(String username, ResponsibleGroup group) {
    checkAdminOrGroupLeader(
        username, group, "You must be an admin or group leader to perform the requested operation");
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected void checkAdminOrCrewChief(String message) {
    boolean isAdminOrCrewChief = isAdminOrCrewChief();

    if (!isAdminOrCrewChief) {
      throw new EJBAccessException(message);
    }
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected void checkAdminOrCrewChief() {
    checkAdminOrCrewChief("You must be an admin or crew chief to perform the requested operation");
  }

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  protected void checkAdminOrBranchAdmin(String username, Category branchRoot) {
    boolean isAdminOrBranchAdmin = isAdminOrBranchAdmin(username, branchRoot);

    if (!isAdminOrBranchAdmin) {
      throw new EJBAccessException(
          "You must be an admin or branch admin to perform the requested operation");
    }
  }

  @PermitAll
  public boolean isCrewChief() {
    return context.isCallerInRole("cc");
  }

  @PermitAll
  public boolean isAdminOrCrewChief() {
    boolean isAdmin = context.isCallerInRole("srm-admin");
    boolean isCrewChief = context.isCallerInRole("cc");

    return isAdmin || isCrewChief;
  }

  protected boolean isAdminOrGroupLeader(String username, ResponsibleGroup group) {
    boolean isAdminOrLeader = false;

    boolean isAdmin = context.isCallerInRole("srm-admin");
    if (isAdmin) {
      isAdminOrLeader = true;
    } else {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
      boolean isLeader = false;
      for (User leader : userList) {
        if (username.equals(leader.getUsername())) { // leader.getUsername() might be null
          isLeader = true;
          break;
        }
      }
      if (isLeader) {
        isAdminOrLeader = true;
      }
    }

    return isAdminOrLeader;
  }

  @PermitAll
  public boolean isAdminOrGroupLeader(String username, BigInteger groupId) {
    if (username == null || groupId == null) {
      return false;
    }

    ResponsibleGroup group = getEntityManager().find(ResponsibleGroup.class, groupId);

    if (group == null) {
      return false;
    }

    return isAdminOrGroupLeader(username, group);
  }

  @PermitAll
  public boolean isAdminOrBranchAdmin(Category branchRoot) {
    String username = context.getCallerPrincipal().getName();
    if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
      return false;
    }

    return isAdminOrBranchAdmin(username, branchRoot);
  }

  protected boolean isAdminOrBranchAdmin(String username, Category branchRoot) {
    boolean isAdminOrBranchAdmin = false;

    boolean isAdmin = context.isCallerInRole("srm-admin");
    if (isAdmin) {
      isAdminOrBranchAdmin = true;
    } else {
      boolean isBranchAdmin = false;
      if (branchRoot != null) {
        switch (branchRoot.getName()) {
          case "Hall A":
            isBranchAdmin = context.isCallerInRole("halead");
            break;
          case "Hall B":
            isBranchAdmin = context.isCallerInRole("hblead");
            break;
          case "Hall C":
            isBranchAdmin = context.isCallerInRole("hclead");
            break;
          case "Hall D":
            isBranchAdmin = context.isCallerInRole("hdlead");
            break;
          case "LERF":
            isBranchAdmin = context.isCallerInRole("lerfadm");
            break;
          case "Cryo":
            isBranchAdmin = context.isCallerInRole("cryoadm");
            break;
          case "CMTF":
            isBranchAdmin = context.isCallerInRole("cmtfadm");
            break;
          case "VTA":
            isBranchAdmin = context.isCallerInRole("vtaadm");
            break;
        }
      }
      if (isBranchAdmin) {
        isAdminOrBranchAdmin = true;
      }
    }

    return isAdminOrBranchAdmin;
  }

  public static class OrderDirective {

    private final String field;
    private final boolean asc;

    public OrderDirective(String field) {
      this(field, true);
    }

    public OrderDirective(String field, boolean asc) {
      this.field = field;
      this.asc = asc;
    }

    public String getField() {
      return field;
    }

    public boolean isAsc() {
      return asc;
    }
  }
}
