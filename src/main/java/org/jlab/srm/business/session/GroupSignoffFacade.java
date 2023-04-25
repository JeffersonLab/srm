package org.jlab.srm.business.session;

import org.jlab.jlog.*;
import org.jlab.srm.business.util.EntityUtil;
import org.jlab.srm.business.util.PartitionList;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.entity.view.ComponentSignoff;
import org.jlab.srm.persistence.enumeration.SignoffChangeType;
import org.jlab.srm.persistence.model.SignoffStandIn;
import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@Stateless
public class GroupSignoffFacade extends AbstractFacade<GroupSignoff> {

    private static final Logger LOGGER = Logger.getLogger(
            GroupSignoffFacade.class.getName());
    @EJB
    ComponentFacade componentFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    GroupResponsibilityFacade groupResponsibilityFacade;
    @EJB
    GroupSignoffHistoryFacade groupSignoffHistoryFacade;
    @EJB
    StaffFacade staffFacade;
    @EJB
    SystemFacade systemFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public GroupSignoffFacade() {
        super(GroupSignoff.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<GroupSignoff> updateAndFetchGroupSignOffList(Component component) {
        return updateAndFetchGroupSignOffList(component, null);
    }

    @PermitAll
    public List<GroupSignoff> updateAndFetchGroupSignOffList(Component component, BigInteger groupId) {
        List<GroupSignoff> list = new ArrayList<>();

        if (component != null && component.getSystem().getGroupResponsibilityList() != null) {
            Map<ResponsibleGroup, GroupSignoff> groupMap
                    = EntityUtil.getGroupSignoffMap(
                    component.getGroupSignoffList());

            for (GroupResponsibility responsibility : component.getSystem().getGroupResponsibilityList()) { //Should be ordered by weight
                GroupSignoff signoff = groupMap.get(responsibility.getGroup());
                if (signoff == null) {
                    signoff = new GroupSignoff();
                    signoff.setGroupResponsibility(responsibility);
                    signoff.setComponent(component);
                    signoff.setStatus(Status.NOT_READY);
                    signoff.setModifiedDate(null);
                    signoff.setModifiedBy(null);
                    //create(signoff);
                }

                if (groupId == null
                        || signoff.getGroupResponsibility().getGroup().getGroupId().equals(
                        groupId)) {
                    list.add(signoff);
                }
            }
        }

        return list;
    }

    @PermitAll
    public void updateSignoff(BigInteger componentId, BigInteger groupId, Status status, String comment, SignoffCascadeRule cascadeRule, SignoffValidateRule validateRule, Date signoffDate, String username) throws UserFriendlyException {
        GroupSignoff signoff = findByComponentIdAndGroupId(componentId,
                groupId);

        Component component = componentFacade.find(componentId);
        SystemEntity system = component.getSystem();
        GroupResponsibility responsibility
                = groupResponsibilityFacade.findBySystemIdAndGroupId(
                system.getSystemId(), groupId);

        if (responsibility == null) {
            throw new UserFriendlyException(
                    "Cannot signoff when there is no responsibility");
        }

        if (validateRule.requiredChecklistPublished) {
            if (responsibility.isChecklistRequired() && !responsibility.isPublished()
                    && !context.isCallerInRole("hcoadm")) {
                throw new UserFriendlyException(
                        "The system checklist must be published before signoffs can be made");
            }
        }

        boolean creatingNew = false;

        if (signoff == null) {
            creatingNew = true;

            signoff = new GroupSignoff();

            signoff.setComponent(component);
            signoff.setGroupResponsibility(responsibility);

            /*signoff.setComponentId(component.getComponentId());
                 signoff.setGroupId(responsibility.getGroup().getGroupId());
                 signoff.setSystemId(component.getSystem().getSystemId());*/
        }

        Staff staff = staffFacade.findByUsername(username);

        if (staff == null) {
            throw new UserFriendlyException(
                    "Cannot find staff with username: " + username);
        }

        Status before = signoff.getStatus();

        if (before == null) {
            before = Status.NOT_READY;
        }

        if (validateRule.disallowModifyMask && (before.equals(Status.MASKED) || before.equals(Status.MASKED_CC))) {
            throw new UserFriendlyException("Cannot signoff a masked component");
        }

        if (Status.MASKED.equals(status) || Status.MASKED_CC.equals(status)) {
            throw new UserFriendlyException("Mask signoff not allowed via regular user submission");
        }

        SignoffChangeType type = Status.CHANGE(before, status);

        /*If only commenting then just make sure there is a comment; it is okay if you are not an admin and choose Ready or N/A in this case (status isn't changing)*/
        if (type == SignoffChangeType.COMMENT) {
            if (comment == null || comment.isEmpty()) {
                throw new UserFriendlyException(
                        "Cannot signoff component \"" + component.getName() + "\" for group \""
                                + responsibility.getGroup().getName()
                                + "\" without a comment since the status is not changing");
            }
        } else {
            /*We are actually changing the status*/

            /*If setting N/A make sure at least one responsible group is not N/A*/
            /*Also, you best be an Admin or Group Leader*/
            if (status.equals(Status.NOT_APPLICABLE)) {
                checkAdminOrGroupLeader(username, responsibility.getGroup(), "You must be a group leader or admin to change a signoff status to N/A");

                if (validateRule.atLeastOneNonNa) {
                    List<ComponentSignoff> signoffList
                            = componentFacade.findByComponent(componentId);

                    boolean atLeastOneNonNa = false;

                    for (ComponentSignoff s : signoffList) {
                        if (!s.getGroupId().equals(groupId) && !s.getStatusId().equals(
                                Status.NOT_APPLICABLE.getStatusId())) {
                            atLeastOneNonNa = true;
                            break;
                        }
                    }

                    if (!atLeastOneNonNa) {
                        throw new UserFriendlyException(
                                "At least one group must be something other than N/A.  Looks like you got stuck with the check!");
                    }
                }

                /*If setting to Ready you best be an Admin or the group leader*/
            } else if (status.equals(Status.READY)) {
                checkAdminOrGroupLeader(username, responsibility.getGroup(), "You must be a group leader or admin to change a signoff status to Ready");

                if (validateRule.twoStepSignoff) {
                    if (signoff.getStatus() == null
                            || signoff.getStatus().equals(Status.NOT_READY)) {
                        throw new UserFriendlyException(
                                "Cannot signoff component \"" + component.getName()
                                        + "\" for group \""
                                        + responsibility.getGroup().getName()
                                        + "\" to ready until the component is first checked");
                    }
                }
            } else {
                /*Check for downgrade, which means possible cascade on trailing signoffs*/

                boolean downgrade
                        = status.getStatusId().intValue()
                        > (signoff.getStatus() == null ? 100 : signoff.getStatus().getStatusId().intValue());
                if (downgrade) {
                    /*System.out.println("Downgrade detected!");
                         System.out.println("readCascade: " + readyCascade);
                         System.out.println("checkedCascade: " + checkedCascade);*/

                    if (cascadeRule.cascade) {
                        groupResponsibilityFacade.cascadeDowngrade(
                                component.getComponentId(),
                                responsibility.getWeight(), staff, comment, cascadeRule.readyCascade, cascadeRule.checkedCascade);
                    }
                }
            }
        }

        signoff.setStatus(status);
        signoff.setComments(comment);
        signoff.setModifiedDate(signoffDate);
        signoff.setModifiedBy(staff);
        signoff.setChangeType(type);

        if (creatingNew) {
            signoff = createSpecial(signoff);
        } else {
            edit(signoff); // May be creating new so this call is required
        }

        groupSignoffHistoryFacade.newHistory(signoff);
    }

    @PermitAll
    public Long updateSignoff(List<BigInteger> componentIdList, List<BigInteger> groupIdList,
                              Status status, String comment, boolean needsAttention, SignoffCascadeRule cascadeRule, SignoffValidateRule validateRule) throws
            UserFriendlyException {

        String username = checkAuthenticated();

        Set<BigInteger> uniqueGroupList = new HashSet<>(groupIdList);

        if (uniqueGroupList.size() > 1) {
            throw new UserFriendlyException(
                    "Cannot signoff for more than one group at a time");
        }

        if (uniqueGroupList.size() < 1) {
            throw new UserFriendlyException("No group selected");
        }

        BigInteger groupId = groupIdList.get(0);

        Date signoffDate = new Date(); // We use same date for all items in list

        for (BigInteger componentId : componentIdList) {
            //BigInteger groupId = groupIdList.get(i++);
            updateSignoff(componentId, groupId, status, comment, cascadeRule, validateRule, signoffDate, username);
        }

        Long prId = null;

        if (needsAttention) {
            prId = createOpsPr(componentIdList, groupId, comment, username);
        }

        return prId;
    }

    /**
     * HACK - Can't get ENVERS to stop auditing entity.
     *
     * @param signoff Unmanaged Entity
     * @return Managed Entity
     */
    @PermitAll
    public GroupSignoff createSpecial(GroupSignoff signoff) {
        Query q1 = em.createNativeQuery("select group_signoff_id.nextval from dual");

        BigInteger signoffId = BigInteger.valueOf(((Number) q1.getSingleResult()).longValue());

        Query q = em.createNativeQuery("insert into group_signoff (group_signoff_id, system_id, group_id, component_id, status_id, modified_by, modified_date, comments, change_type) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        /*System.out.println(signoffId);
        System.out.println(signoff.getGroupResponsibility().getSystem().getSystemId());
        System.out.println(signoff.getGroupResponsibility().getGroup().getGroupId());
        System.out.println(signoff.getComponent().getComponentId());
        System.out.println(signoff.getStatus().getStatusId());
        System.out.println(signoff.getModifiedBy().getStaffId());
        System.out.println(signoff.getModifiedDate());
        System.out.println(signoff.getComments());
        System.out.println(signoff.getChangeType().name());*/
        // signoff.getGroupId() == null !!!!
        // signoff.getComponentId() == null !!!
        // what about signoff.getSystemId()????
        q.setParameter(1, signoffId);
        q.setParameter(2, signoff.getGroupResponsibility().getSystem().getSystemId());
        q.setParameter(3, signoff.getGroupResponsibility().getGroup().getGroupId());
        q.setParameter(4, signoff.getComponent().getComponentId());
        q.setParameter(5, signoff.getStatus().getStatusId());
        q.setParameter(6, signoff.getModifiedBy().getStaffId());
        q.setParameter(7, signoff.getModifiedDate());
        q.setParameter(8, signoff.getComments());
        q.setParameter(9, signoff.getChangeType().name());

        q.executeUpdate();

        return find(signoffId);
    }

    private GroupSignoff findByComponentIdAndGroupId(BigInteger componentId, BigInteger groupId) {
        TypedQuery<GroupSignoff> q = em.createQuery(
                "SELECT g FROM GroupSignoff g WHERE g.component.componentId = :componentId AND g.groupResponsibility.group.groupId = :groupId",
                GroupSignoff.class);

        q.setParameter("componentId", componentId);
        q.setParameter("groupId", groupId);

        GroupSignoff signoff = null;

        List<GroupSignoff> results = q.getResultList();

        if (results != null && !results.isEmpty()) {
            signoff = results.get(0);
        }

        return signoff;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<SignoffStandIn> findStandInListByComponentList(List<Component> componentList) {
        List<SignoffStandIn> signoffList = new ArrayList<>();
        Query q = em.createQuery(
                "select new org.jlab.srm.persistence.model.SignoffStandIn(a.groupSignoffId, a.componentId, a.groupId, a.modifiedBy, a.modifiedDate, a.changeType, a.comments, a.status) from GroupSignoff a where a.component.componentId in :componentIdList");

        List<BigInteger> componentIdList = new ArrayList<>();

        for (Component component : componentList) {
            componentIdList.add(component.getComponentId());
        }

        long start = System.currentTimeMillis();

        //Oracle has a limit of 1000 literal items in an "IN" query so we must partition
        PartitionList<BigInteger> plist = new PartitionList<>(
                componentIdList, 1000);

        for (List<BigInteger> l : plist) {
            q.setParameter("componentIdList", l);
            signoffList.addAll(q.getResultList());
        }

        long end = System.currentTimeMillis();
        LOGGER.log(Level.FINE, "[PERFORMANCE] Fetch Group Signoff List: {0}",
                (end - start) / 1000.0f);

        return signoffList;
    }

    @PermitAll
    public Map<String, SignoffStandIn> createComponentGroupToSignoffStandInMap(
            List<SignoffStandIn> signoffList) {
        Map<String, SignoffStandIn> signoffMap
                = new HashMap<>();

        if (signoffList != null) {
            for (SignoffStandIn signoff : signoffList) {
                signoffMap.put(
                        signoff.getComponentId().toString() + ";" + signoff.getGroupId().toString(),
                        signoff);
            }
        }

        return signoffMap;
    }

    @PermitAll
    public void reassignGroupSignoffs(Component component, ResponsibleGroup group,
                                      SystemEntity newSystem) {
        String sql
                = "update group_signoff set system_id = ?1 where component_id = ?2 and group_id = ?3";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, newSystem.getSystemId());
        q.setParameter(2, component.getComponentId());
        q.setParameter(3, group.getGroupId());
        q.executeUpdate();

        /*Now do the history table*/
        sql
                = "update GroupSignoffHistory set systemId = ?1 where componentId = ?2 and groupId = ?3";
        Query q2 = em.createQuery(sql);
        q2.setParameter(1, newSystem.getSystemId());
        q2.setParameter(2, component.getComponentId());
        q2.setParameter(3, group.getGroupId());
        q2.executeUpdate();
    }

    @PermitAll
    public void deleteGroupSignoffs(Component component, SystemEntity system) {
        String sql
                = "delete from group_signoff where component_id = ?1 and system_id = ?2";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, component.getComponentId());
        q.setParameter(2, system.getSystemId());
        q.executeUpdate();
    }

    @PermitAll
    public void bulkSignoff(BigInteger systemId, Status status, String comment) throws UserFriendlyException {
        List<BigInteger> componentIdList;
        List<BigInteger> groupIdList;
        /*ID list is a waste as it is just same Group ID repeated for each component ID*/

        SystemEntity system = systemFacade.find(systemId);

        if (system == null) {
            throw new UserFriendlyException("System with ID: " + systemId + " not found");
        }

        List<Component> componentList = system.getComponentList();
        List<GroupResponsibility> responsibilityList = system.getGroupResponsibilityList();

        for (GroupResponsibility responsibility : responsibilityList) {
            componentIdList = new ArrayList<>();
            groupIdList = new ArrayList<>();

            BigInteger groupId = responsibility.getGroup().getGroupId();
            for (Component c : componentList) {
                if (!c.isMasked()) { // Ignore masked components
                    componentIdList.add(c.getComponentId());
                    groupIdList.add(groupId);
                }
            }

            this.updateSignoff(componentIdList, groupIdList, status, comment, false, new SignoffCascadeRule(), new SignoffValidateRule());
        }
    }

    private Long createOpsPr(List<BigInteger> componentIdList, BigInteger groupId, String comment, String username) throws UserFriendlyException {

        BigInteger componentId = null;

        if (componentIdList.isEmpty()) {
            throw new UserFriendlyException("Component list must not be empty");
        }

        componentId = componentIdList.get(0);

        Component component = componentFacade.find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Component with ID: " + componentId + " not found");
        }

        SystemEntity system = component.getSystem();

        if (componentIdList.size() > 1) {
            componentId = null;
        }

        String subject = "System Readiness (HCO) Update for " + system.getName();

        String logbooks = System.getenv("LOGBOOK_OPS_BOOKS_CSV");

        if (logbooks == null || logbooks.isEmpty()) {
            logbooks = "TLOG";
            LOGGER.log(Level.WARNING,
                    "Environment variable 'LOGBOOK_OPS_BOOKS_CSV' not found, using default TLOG");
        }

        ProblemReport report = new ProblemReport(ProblemReportType.OPS, true, system.getSystemId().intValue(), groupId.intValue(), componentId == null ? null : componentId.intValue());

        LogEntry entry = new LogEntry(subject, logbooks);

        entry.setProblemReport(report);

        entry.setBody(comment, Body.ContentType.TEXT);

        LogEntryAdminExtension extension = new LogEntryAdminExtension(entry);
        extension.setAuthor(username);

        Properties config = Library.getConfiguration();

        String logbookHostname = System.getenv("LOGBOOK_HOSTNAME");

        if (logbookHostname == null || logbookHostname.isEmpty()) {
            logbookHostname = "logbooktest.acc.jlab.org";
            LOGGER.log(Level.WARNING,
                    "Environment variable 'LOGBOOK_HOSTNAME' not found, using default logbooktest.acc.jlab.org");
        }

        config.setProperty("SUBMIT_URL", "https://" + logbookHostname + "/incoming");

        long logId;

        try {
            logId = entry.submitNow();
        } catch (Exception e) {
            throw new UserFriendlyException("Unable to send elog", e);
        }

        return logId;
    }

    public static class SignoffCascadeRule {

        public boolean cascade = false;
        public BigInteger readyCascade = BigInteger.ZERO;
        public BigInteger checkedCascade = BigInteger.ZERO;
    }

    public static class SignoffValidateRule {

        public boolean twoStepSignoff = false;
        public boolean atLeastOneNonNa = false;
        public boolean requiredChecklistPublished = false;
        public boolean disallowModifyMask = false;
    }
}
