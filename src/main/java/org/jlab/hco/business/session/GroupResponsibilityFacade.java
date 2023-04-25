package org.jlab.hco.business.session;

import org.jlab.hco.business.session.GroupSignoffFacade.SignoffCascadeRule;
import org.jlab.hco.business.session.GroupSignoffFacade.SignoffValidateRule;
import org.jlab.hco.persistence.entity.*;
import org.jlab.hco.persistence.enumeration.SignoffChangeType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.IOUtil;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.*;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"hcoadm", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class GroupResponsibilityFacade extends AbstractFacade<GroupResponsibility> {

    @EJB
    SystemFacade systemFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    GroupSignoffFacade groupSignoffFacade;
    @EJB
    GroupSignoffHistoryFacade groupSignoffHistoryFacade;
    @EJB
    StaffFacade staffFacade;
    @EJB
    CategoryFacade categoryFacade;
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public GroupResponsibilityFacade() {
        super(GroupResponsibility.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public BigInteger createNew(BigInteger systemId, BigInteger groupId,
                                boolean checklistRequired, BigInteger weight)
            throws UserFriendlyException {
        String username = checkAuthenticated();

        if (systemId == null) {
            throw new UserFriendlyException(
                    "System ID cannot be null.");
        }

        if (groupId == null) {
            throw new UserFriendlyException(
                    "Group ID cannot be null.");
        }

        SystemEntity system = systemFacade.find(systemId);

        if (system == null) {
            throw new UserFriendlyException("System with ID " + systemId
                    + " not found.");
        }

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        GroupResponsibility responsibility = new GroupResponsibility();

        responsibility.setSystem(system);

        ResponsibleGroup group = groupFacade.find(groupId);

        if (group == null) {
            throw new UserFriendlyException("Group with ID " + groupId + " not found.");
        }

        GroupResponsibility existing = findBySystemAndGroup(systemId, groupId);

        if (existing != null) {
            throw new UserFriendlyException("The group " + existing.getGroup().getName()
                    + " already has responsibility in the system "
                    + existing.getSystem().getName() + ".");
        }

        responsibility.setGroup(group);

        responsibility.setChecklistRequired(checklistRequired);
        responsibility.setWeight(weight);

        responsibility.setPublished(false);
        /* Null not allowed */

        responsibility = createSpecial(responsibility);

        String comment = "New group responsibility assignment";

        setInitialSystemSignoffs(system, group, comment, username);

        return responsibility.getGroupResponsibilityId();
    }

    @PermitAll
    public void setInitialSystemSignoffs(SystemEntity system, ResponsibleGroup group, String comment, String username) throws UserFriendlyException {
        List<BigInteger> componentIdList = new ArrayList<>();
        List<BigInteger> groupIdList = new ArrayList<>();

        for (Component c : system.getComponentList()) {
            componentIdList.add(c.getComponentId());
            groupIdList.add(group.getGroupId());
        }

        // Only set initial if group/component exists!
        if (groupIdList.size() > 0) {
            groupSignoffFacade.updateSignoff(componentIdList, groupIdList, Status.NOT_READY, comment, false, new SignoffCascadeRule(), new SignoffValidateRule());
        }
    }

    @PermitAll
    public void setInitialComponentSignoffs(Component component, ResponsibleGroup group, String comment, String username) throws UserFriendlyException {
        List<BigInteger> componentIdList = new ArrayList<>();
        List<BigInteger> groupIdList = new ArrayList<>();

        componentIdList.add(component.getComponentId());
        groupIdList.add(group.getGroupId());

        groupSignoffFacade.updateSignoff(componentIdList, groupIdList, Status.NOT_READY, comment, false, new SignoffCascadeRule(), new SignoffValidateRule());
    }

    private GroupResponsibility createSpecial(GroupResponsibility responsibility) {
        Query q1 = em.createNativeQuery("select group_responsibility_id.nextval from dual");

        BigInteger responsibilityId = BigInteger.valueOf(((Number) q1.getSingleResult()).longValue());

        Query q = em.createNativeQuery("insert into group_responsibility (group_responsibility_id, group_id, system_id, weight, checklist_id, checklist_required, published, published_date, published_by) values (?, ?, ?, ?, null, ?, ?, null, null)");

        q.setParameter(1, responsibilityId);
        q.setParameter(2, responsibility.getGroup().getGroupId());
        q.setParameter(3, responsibility.getSystem().getSystemId());
        q.setParameter(4, responsibility.getWeight());
        q.setParameter(5, responsibility.isChecklistRequired() ? "Y" : "N");
        q.setParameter(6, responsibility.isPublished() ? "Y" : "N");
        //q.setParameter(7, responsibility.getPublishedDate());
        //q.setParameter(8, responsibility.getPublishedBy());

        q.executeUpdate();

        return find(responsibilityId);
    }

    @PermitAll
    public void order(BigInteger[] groupResponsibilityIdArray)
            throws UserFriendlyException {
        String username = checkAuthenticated();

        List<GroupResponsibility> newGroupResponsibilityList = new ArrayList<>();

        Set<SystemEntity> systemSet = new HashSet<>();

        for (int i = 0; i < groupResponsibilityIdArray.length; i++) {
            BigInteger id = groupResponsibilityIdArray[i];

            if (id == null) {
                throw new UserFriendlyException(
                        "Group Responsibility ID cannot be null.");
            }

            GroupResponsibility responsibility = find(id);

            if (responsibility == null) {
                throw new UserFriendlyException(
                        "Group Responsibilty with ID " + id + " not found.");
            }

            responsibility.setWeight(BigInteger.valueOf(i + 1L));

            systemSet.add(responsibility.getSystem());

            newGroupResponsibilityList.add(responsibility);
        }

        if (systemSet.size() > 1) {
            throw new UserFriendlyException(
                    "List of group responsibilties do not all belong to the"
                            + " same system.");
        }

        SystemEntity system = systemSet.iterator().next();

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        List<GroupResponsibility> oldGroupResponsibilityList = system.getGroupResponsibilityList();

        if (oldGroupResponsibilityList.size()
                != newGroupResponsibilityList.size()
                || !oldGroupResponsibilityList.containsAll(
                newGroupResponsibilityList)) {
            throw new UserFriendlyException("The list of responsibilties to order does"
                    + " not match the list in the database.");
        }

        system.setGroupResponsibilityList(newGroupResponsibilityList);
    }

    @PermitAll
    public GroupResponsibility findBySystemAndGroup(BigInteger systemId,
                                                    BigInteger groupId) throws UserFriendlyException {

        if (systemId == null) {
            throw new UserFriendlyException(
                    "System ID cannot be null.");
        }

        TypedQuery<GroupResponsibility> q = em.createQuery(
                "select g from GroupResponsibility g where"
                        + " g.system.systemId = :systemId and"
                        + " g.group.groupId = :groupId", GroupResponsibility.class);

        q.setParameter("systemId", systemId);
        q.setParameter("groupId", groupId);

        List<GroupResponsibility> groupResponsibilityList = q.getResultList();

        GroupResponsibility responsibility = null;

        if (groupResponsibilityList != null
                && !groupResponsibilityList.isEmpty()) {
            responsibility = groupResponsibilityList.get(0);
        }

        return responsibility;
    }

    @PermitAll
    public void delete(BigInteger groupResponsibilityId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (groupResponsibilityId == null) {
            throw new UserFriendlyException("Group Responsibility ID cannot be null.");
        }

        GroupResponsibility responsibility = find(groupResponsibilityId);

        if (responsibility == null) {
            throw new UserFriendlyException(
                    "Group Responsibilty with ID "
                            + groupResponsibilityId + " not found.");
        }

        SystemEntity system = responsibility.getSystem();

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        List<GroupResponsibility> groupResponsibilityList = system.getGroupResponsibilityList();

        groupResponsibilityList.remove(responsibility);

        removeSpecial(responsibility);

        for (int i = 0; i < groupResponsibilityList.size(); i++) {
            groupResponsibilityList.get(i).setWeight(BigInteger.valueOf(i + 1L));
        }

        system.setGroupResponsibilityList(groupResponsibilityList);
    }

    /**
     * ENVERS workaround.
     *
     * @param responsibility
     */
    private void removeSpecial(GroupResponsibility responsibility) {
        Query q = em.createNativeQuery("delete from group_responsibility where group_responsibility_id = ?");

        q.setParameter(1, responsibility.getGroupResponsibilityId());

        q.executeUpdate();
    }

    @PermitAll
    public void update(BigInteger groupResponsibilityId,
                       boolean checklistRequired) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (groupResponsibilityId == null) {
            throw new UserFriendlyException("Group Responsibility ID cannot be null.");
        }

        GroupResponsibility responsibility = find(groupResponsibilityId);

        if (responsibility == null) {
            throw new UserFriendlyException(
                    "Group Responsibilty with ID "
                            + groupResponsibilityId + " not found.");
        }

        Category branchRoot
                = categoryFacade.findBranchRoot(responsibility.getSystem().getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        responsibility.setChecklistRequired(checklistRequired);
    }

    @PermitAll
    public GroupResponsibility findBySystemIdAndGroupId(BigInteger systemId, BigInteger groupId) {
        TypedQuery<GroupResponsibility> q = em.createQuery(
                "select g from GroupResponsibility g where g.system.systemId = :systemId and g.group.groupId = :groupId",
                GroupResponsibility.class);

        q.setParameter("systemId", systemId);
        q.setParameter("groupId", groupId);

        List<GroupResponsibility> results = q.getResultList();

        GroupResponsibility responsibility = null;

        if (results != null && !results.isEmpty()) {
            responsibility = results.get(0);
        }

        return responsibility;
    }

    @PermitAll
    public boolean isSignoffAllowed(BigInteger componentId, BigInteger weight) {
        Query q = em.createNativeQuery("select previous_signoff_status(" + componentId + ","
                + weight + ") from dual");
        Number result = (Number) q.getSingleResult();
        int status = 1;

        if (result != null) {
            status = result.intValue();
        }

        return (status == 1);
    }

    private void doReadyCascade(BigInteger componentId, BigInteger weight, Staff staff,
                                String comment, BigInteger readyCascade) throws UserFriendlyException {
        /*Select all signoffs which come after this signoff and are Ready, because we need to downgrade them!*/
        TypedQuery<GroupSignoff> q = em.createQuery(
                "select g from GroupSignoff g where g.component.componentId = :componentId and g.status.statusId = 1 and g.groupResponsibility.weight > :weight",
                GroupSignoff.class);

        q.setParameter("componentId", componentId);
        q.setParameter("weight", weight);

        List<GroupSignoff> groupSignoffList = q.getResultList();

        if (groupSignoffList != null && !groupSignoffList.isEmpty()) {

            if (comment == null || comment.isEmpty()) {
                throw new UserFriendlyException(
                        "A comment must be provided when status changes cause cascading downgrades");
            }

            for (GroupSignoff signoff : groupSignoffList) {

                signoff.setStatus(Status.FROM_ID(readyCascade));
                signoff.setComments(comment);
                signoff.setModifiedDate(new Date());
                signoff.setModifiedBy(staff);
                signoff.setChangeType(SignoffChangeType.CASCADE);

                groupSignoffHistoryFacade.newHistory(signoff);
            }
        }
    }

    private void doCheckedCascade(BigInteger componentId, BigInteger weight, Staff staff,
                                  String comment, BigInteger checkedCascade) throws UserFriendlyException {
        /*Select all signoffs which come after this signoff and are Checked, because we need to downgrade them!*/
        TypedQuery<GroupSignoff> q = em.createQuery(
                "select g from GroupSignoff g where g.component.componentId = :componentId and g.status.statusId = 50 and g.groupResponsibility.weight > :weight",
                GroupSignoff.class);

        q.setParameter("componentId", componentId);
        q.setParameter("weight", weight);

        List<GroupSignoff> groupSignoffList = q.getResultList();

        if (groupSignoffList != null && !groupSignoffList.isEmpty()) {

            if (comment == null || comment.isEmpty()) {
                throw new UserFriendlyException(
                        "A comment must be provided when status changes cause cascading downgrades");
            }

            for (GroupSignoff signoff : groupSignoffList) {

                signoff.setStatus(Status.FROM_ID(checkedCascade));
                signoff.setComments(comment);
                signoff.setModifiedDate(new Date());
                signoff.setModifiedBy(staff);
                signoff.setChangeType(SignoffChangeType.CASCADE);

                groupSignoffHistoryFacade.newHistory(signoff);
            }
        }
    }

    @PermitAll
    public void cascadeDowngrade(BigInteger componentId, BigInteger weight, Staff staff,
                                 String comment, BigInteger readyCascade, BigInteger checkedCascade) throws UserFriendlyException {

        if (checkedCascade != null) {
            doCheckedCascade(componentId, weight, staff, comment, checkedCascade);
        }

        if (readyCascade != null) {
            doReadyCascade(componentId, weight, staff, comment, readyCascade);
        }
    }

    @PermitAll
    public List<GroupResponsibility> filterList(BigInteger[] destinationIdArray,
                                                BigInteger categoryId, BigInteger systemId,
                                                BigInteger groupId, Boolean checklistRequired, Boolean checklistMissing, int offset,
                                                int max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GroupResponsibility> cq = cb.createQuery(GroupResponsibility.class);
        Root<GroupResponsibility> root = cq.from(GroupResponsibility.class);
        cq.select(root);
        Join<GroupResponsibility, ResponsibleGroup> group = root.join("group");
        Join<GroupResponsibility, SystemEntity> system = root.join("system");
        Join<GroupResponsibility, Checklist> checklist = root.join("checklist", JoinType.LEFT);
        List<Predicate> filters = new ArrayList<>();
        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("system"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(system.get("systemId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("system"), systemId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("group"), groupId));
        }
        if (checklistRequired != null) {
            filters.add(cb.equal(root.get("checklistRequiredStr"), checklistRequired ? "Y" : "N"));
        }
        if (checklistMissing != null) {
            if (checklistMissing) {
                Predicate p1 = cb.equal(root.get("checklistRequiredStr"), "Y");
                Predicate p2 = cb.isNull(root.get("checklist"));
                Predicate p3 = cb.equal(cb.length(root.get("checklist")), 0);
                Predicate p4 = cb.equal(root.get("publishedStr"), "N");
                filters.add(cb.and(p1, cb.or(p2, p3, p4)));
            } else {
                Predicate p1 = cb.equal(root.get("checklistRequiredStr"), "N");
                Predicate p2 = cb.equal(root.get("checklistRequiredStr"), "Y");
                Predicate p3 = cb.isNotNull(root.get("checklist"));
                Predicate p4 = cb.gt(cb.length(root.get("checklist")), 0);
                Predicate p5 = cb.equal(root.get("publishedStr"), "Y");

                filters.add(cb.or(p1, cb.and(p2, p3, p4, p5)));
            }
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = checklist.get("modifiedDate");
        Order o0 = cb.desc(p0);
        orders.add(o0);
        Path p1 = group.get("name");
        Order o1 = cb.asc(p1);
        orders.add(o1);
        Path p2 = system.get("name");
        Order o2 = cb.asc(p2);
        orders.add(o2);
        cq.orderBy(orders);
        List<GroupResponsibility> responsibilityList
                = em.createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();

        for (GroupResponsibility responsibility : responsibilityList) {
            if (responsibility.getChecklist() != null) {
                responsibility.getChecklist().getModifiedDate();
                /*Tickle to initialize*/

            }
        }

        return responsibilityList;
    }

    @PermitAll
    public Long countFilterList(BigInteger[] destinationIdArray, BigInteger categoryId,
                                BigInteger systemId, BigInteger groupId, Boolean checklistRequired,
                                Boolean checklistMissing) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<GroupResponsibility> root = cq.from(GroupResponsibility.class);
        Join<GroupResponsibility, SystemEntity> system = root.join("system");
        List<Predicate> filters = new ArrayList<>();
        destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

        if (destinationIdArray != null && destinationIdArray.length > 0) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
            Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
            subquery.select(componentList.get("system"));
            Predicate p1 = subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(
                    destinationIdArray));
            subquery.where(p1);
            filters.add(cb.in(system.get("systemId")).value(subquery));
        }
        if (categoryId != null) {
            List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
            filters.add(root.get("system").in(systemList));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("system"), systemId));
        }
        if (groupId != null) {
            filters.add(cb.equal(root.get("group"), groupId));
        }
        if (checklistRequired != null) {
            filters.add(cb.equal(root.get("checklistRequiredStr"), checklistRequired ? "Y" : "N"));
        }
        if (checklistMissing != null) {
            if (checklistMissing) {
                Predicate p1 = cb.equal(root.get("checklistRequiredStr"), "Y");
                Predicate p2 = cb.isNull(root.get("checklist"));
                Predicate p3 = cb.equal(cb.length(root.get("checklist")), 0);
                Predicate p4 = cb.equal(root.get("publishedStr"), "N");
                filters.add(cb.and(p1, cb.or(p2, p3, p4)));
            } else {
                Predicate p1 = cb.equal(root.get("checklistRequiredStr"), "N");
                Predicate p2 = cb.equal(root.get("checklistRequiredStr"), "Y");
                Predicate p3 = cb.isNotNull(root.get("checklist"));
                Predicate p4 = cb.gt(cb.length(root.get("checklist")), 0);
                Predicate p5 = cb.equal(root.get("publishedStr"), "Y");

                filters.add(cb.or(p1, cb.and(p2, p3, p4, p5)));
            }
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public BigInteger togglePublished(BigInteger groupResponsibilityId) throws UserFriendlyException {
        if (groupResponsibilityId == null) {
            throw new UserFriendlyException("groupResponsibilityId must not be null");
        }

        GroupResponsibility responsibility = find(groupResponsibilityId);

        if (responsibility == null) {
            throw new UserFriendlyException("Group Responsibility with ID: " + groupResponsibilityId
                    + " not found");
        }

        String username = checkAuthenticated();
        checkAdminOrGroupLeader(username, responsibility.getGroup());

        if (responsibility.isPublished()) {
            responsibility.setPublished(false);
            responsibility.setPublishedBy(null);
            responsibility.setPublishedDate(null);
        } else {
            Staff staff = staffFacade.findByUsername(username);

            if (staff == null) {
                throw new UserFriendlyException("Cannot find staff with username: " + username);
            }

            responsibility.setPublished(true);
            responsibility.setPublishedBy(staff);
            responsibility.setPublishedDate(new Date());
        }

        return responsibility.getGroup().getGroupId();
    }
}
