package org.jlab.srm.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.srm.persistence.entity.*;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"srm-admin"})
public class SavedSignoffFacade extends AbstractFacade<SavedSignoff> {

  @EJB StatusFacade statusFacade;
  @EJB SystemFacade systemFacade;
  @EJB ResponsibleGroupFacade groupFacade;
  @EJB RegionFacade regionFacade;
  @EJB SavedSignoffTypeFacade typeFacade;
  @EJB GroupSignoffFacade signoffFacade;
  @EJB ComponentFacade componentFacade;

  @PersistenceContext(unitName = "srmPU")
  private EntityManager em;

  public SavedSignoffFacade() {
    super(SavedSignoff.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @RolesAllowed("srm-admin")
  public void delete(BigInteger[] savedSignoffIdArray) throws UserFriendlyException {
    if (savedSignoffIdArray == null || savedSignoffIdArray.length == 0) {
      throw new UserFriendlyException("Saved Signoff IDs cannot be empty.");
    }

    for (BigInteger savedSignoffId : savedSignoffIdArray) {

      SavedSignoff saved = find(savedSignoffId);

      if (saved == null) {
        throw new UserFriendlyException("Saved Signoff with ID " + savedSignoffId + " not found.");
      }

      removeSpecial(saved);
    }
  }

  /**
   * ENVERS hack
   *
   * @param signoff SavedSignoff
   */
  private void removeSpecial(SavedSignoff signoff) {
    Query q = em.createNativeQuery("delete from saved_signoff where saved_signoff_id = ?");

    q.setParameter(1, signoff.getSavedSignoffId());

    q.executeUpdate();
  }

  @RolesAllowed("srm-admin")
  public BigInteger add(
      BigInteger typeId,
      String signoffName,
      BigInteger signoffStatusId,
      String comments,
      BigInteger systemId,
      BigInteger groupId,
      BigInteger regionId,
      BigInteger filterStatusId,
      String componentName)
      throws UserFriendlyException {

    if (typeId == null) {
      throw new UserFriendlyException("Signoff Type must not be empty");
    }

    SavedSignoffType type = typeFacade.find(typeId);

    if (type == null) {
      throw new UserFriendlyException("Signoff Type with ID: " + typeId + " not found");
    }

    if (signoffName == null || signoffName.isEmpty()) {
      throw new UserFriendlyException("Saved Signoff Name must not be empty");
    }

    if (signoffStatusId == null) {
      throw new UserFriendlyException("Signoff Status must not be empty");
    }

    Status signoffStatus = statusFacade.find(signoffStatusId);

    if (signoffStatus == null) {
      throw new UserFriendlyException("Signoff Status with ID: " + signoffStatusId + " not found");
    }

    if (systemId == null) {
      throw new UserFriendlyException("System must not be empty");
    }

    SystemEntity system = systemFacade.find(systemId);

    if (system == null) {
      throw new UserFriendlyException("System with ID: " + systemId + " not found");
    }

    if (groupId == null) {
      throw new UserFriendlyException("Group must not be empty");
    }

    ResponsibleGroup group = groupFacade.find(groupId);

    if (group == null) {
      throw new UserFriendlyException("Group with ID: " + groupId + " not found");
    }

    Region region = null;

    if (regionId != null) {
      region = regionFacade.find(regionId);
    }

    Status filterStatus = null;

    if (filterStatusId != null) {
      filterStatus = statusFacade.find(filterStatusId);
    }

    SavedSignoff saved = new SavedSignoff();
    saved.setType(type);
    saved.setSignoffName(signoffName);
    saved.setSignoffStatus(signoffStatus);
    saved.setSignoffComments(comments);
    saved.setSystem(system);
    saved.setGroup(group);
    saved.setRegion(region);
    saved.setFilterStatus(filterStatus);
    saved.setFilterComponentName(componentName);

    saved = createSpecial(saved);

    return saved.getSavedSignoffId();
  }

  /**
   * ENVERS hack
   *
   * @param signoff
   */
  private SavedSignoff createSpecial(SavedSignoff signoff) {
    Query q1 = em.createNativeQuery("select saved_signoff_id.nextval from dual");

    BigInteger signoffId = BigInteger.valueOf(((Number) q1.getSingleResult()).longValue());

    String sql =
        "insert into saved_signoff (saved_signoff_id, saved_signoff_type_id, signoff_name, filter_group_id, filter_system_id, filter_region_id, filter_status_id, filter_component_name, signoff_status_id, signoff_comments, weight) "
            + "values (?, ?, ?, ?, ?";

    if (signoff.getRegion() != null) {
      sql = sql + ", ?";
    } else {
      sql = sql + ", null";
    }

    if (signoff.getFilterStatus() != null) {
      sql = sql + ", ?";
    } else {
      sql = sql + ", null";
    }

    if (signoff.getFilterComponentName() != null) {
      sql = sql + ", ?";
    } else {
      sql = sql + ", null";
    }

    if (signoff.getSignoffStatus() != null) {
      sql = sql + ", ?";
    } else {
      sql = sql + ", null";
    }

    if (signoff.getSignoffComments() != null) {
      sql = sql + ", ?";
    } else {
      sql = sql + ", null";
    }

    sql = sql + ", null)";

    Query q = em.createNativeQuery(sql);

    int i = 1;

    q.setParameter(i++, signoffId);
    q.setParameter(i++, signoff.getType().getSavedSignoffTypeId());
    q.setParameter(i++, signoff.getSignoffName());
    q.setParameter(i++, signoff.getGroup().getGroupId());
    q.setParameter(i++, signoff.getSystem().getSystemId());

    if (signoff.getRegion() != null) {
      q.setParameter(i++, signoff.getRegion().getRegionId());
    }

    if (signoff.getFilterStatus() != null) {
      q.setParameter(i++, signoff.getFilterStatus().getStatusId());
    }

    if (signoff.getFilterComponentName() != null) {
      q.setParameter(i++, signoff.getFilterComponentName());
    }

    if (signoff.getSignoffStatus() != null) {
      q.setParameter(i++, signoff.getSignoffStatus().getStatusId());
    }

    if (signoff.getSignoffComments() != null) {
      q.setParameter(i++, signoff.getSignoffComments());
    }

    q.executeUpdate();

    return find(signoffId);
  }

  @PermitAll
  public List<SavedSignoff> filterList(
      BigInteger typeId, BigInteger systemId, BigInteger groupId, int offset, int maxPerPage) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<SavedSignoff> cq = cb.createQuery(SavedSignoff.class);
    Root<SavedSignoff> root = cq.from(SavedSignoff.class);
    Join<SavedSignoff, SystemEntity> system = root.join("system");
    Join<SavedSignoff, ResponsibleGroup> group = root.join("group");
    cq.select(root); // .distinct(true);
    List<Predicate> filters = new ArrayList<>();
    if (typeId != null) {
      filters.add(cb.equal(root.get("type"), typeId));
    }
    if (systemId != null) {
      filters.add(cb.equal(root.get("system"), systemId));
    }
    if (groupId != null) {
      filters.add(cb.equal(root.get("group"), groupId));
    }
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p1 = system.get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    Path p2 = group.get("name");
    Order o2 = cb.asc(p2);
    orders.add(o2);
    Path p3 = root.get("savedSignoffId");
    Order o3 = cb.asc(p3);
    orders.add(o3);
    cq.orderBy(orders);
    return em.createQuery(cq).setFirstResult(offset).setMaxResults(maxPerPage).getResultList();
  }

  @PermitAll
  public Long countFilterList(BigInteger typeId, BigInteger systemId, BigInteger groupId) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    Root<SavedSignoff> root = cq.from(SavedSignoff.class);
    List<Predicate> filters = new ArrayList<>();
    if (typeId != null) {
      filters.add(cb.equal(root.get("type"), typeId));
    }
    if (systemId != null) {
      filters.add(cb.equal(root.get("system"), systemId));
    }
    if (groupId != null) {
      filters.add(cb.equal(root.get("group"), groupId));
    }
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root)); // .distinct(true);
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  @RolesAllowed("srm-admin")
  public void multipleSignoffs(BigInteger[] savedSignoffIdArray, Date maxLastModified)
      throws UserFriendlyException {
    if (savedSignoffIdArray == null || savedSignoffIdArray.length == 0) {
      throw new UserFriendlyException("Saved Signoff IDs cannot be empty.");
    }

    for (BigInteger savedSignoffId : savedSignoffIdArray) {

      SavedSignoff saved = find(savedSignoffId);

      if (saved == null) {
        throw new UserFriendlyException("Saved Signoff with ID " + savedSignoffId + " not found.");
      }

      // TODO: here is where the magic goes!!!
      System.err.println("magic happening with id: " + savedSignoffId);

      if (saved.getSystem() == null) {
        throw new UserFriendlyException("System cannot be empty");
      }

      if (saved.getGroup() == null) {
        throw new UserFriendlyException("Group cannot be empty");
      }

      BigInteger[] regionIdArray = null;
      BigInteger[] statusIdArray = null;

      if (saved.getRegion() != null) {
        regionIdArray = new BigInteger[] {saved.getRegion().getRegionId()};
      }

      if (saved.getFilterStatus() != null) {
        statusIdArray = new BigInteger[] {saved.getFilterStatus().getStatusId()};
      }

      List<Component> componentList =
          componentFacade.findWithoutSignoff(
              null,
              saved.getSystem().getSystemId(),
              regionIdArray,
              saved.getGroup().getGroupId(),
              statusIdArray,
              null,
              saved.getFilterComponentName(),
              null,
              maxLastModified);

      List<BigInteger> componentIdList = new ArrayList<>();
      List<BigInteger> groupIdList = new ArrayList<>();

      for (Component c : componentList) {
        componentIdList.add(c.getComponentId());
        groupIdList.add(saved.getGroup().getGroupId());
      }

      doSignoff(
          saved.getSignoffStatus().getStatusId(),
          saved.getSignoffComments(),
          componentIdList,
          groupIdList);
    }
  }

  private void doSignoff(
      BigInteger statusId,
      String comment,
      List<BigInteger> componentIdList,
      List<BigInteger> groupIdList)
      throws UserFriendlyException {
    boolean needsAttention = false;

    if (statusId == null) {
      throw new UserFriendlyException("statusId cannot be empty");
    }

    GroupSignoffFacade.SignoffCascadeRule cascadeRule = new GroupSignoffFacade.SignoffCascadeRule();
    cascadeRule.cascade = true;
    cascadeRule.readyCascade = Status.CHECKED.getStatusId();
    cascadeRule.checkedCascade = null;

    GroupSignoffFacade.SignoffValidateRule validateRule =
        new GroupSignoffFacade.SignoffValidateRule();
    validateRule.twoStepSignoff = true;
    validateRule.atLeastOneNonNa = true;
    validateRule.requiredChecklistPublished = true;
    validateRule.disallowModifyMask = true;

    signoffFacade.updateSignoff(
        componentIdList,
        groupIdList,
        Status.FROM_ID(statusId),
        comment,
        needsAttention,
        cascadeRule,
        validateRule);
  }
}
