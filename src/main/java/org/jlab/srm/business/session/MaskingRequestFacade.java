package org.jlab.srm.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.enumeration.MaskingRequestStatus;

/**
 * @author ryans
 */
@Stateless
public class MaskingRequestFacade extends AbstractFacade<MaskingRequest> {

  private static final Logger LOGGER = Logger.getLogger(MaskingRequestFacade.class.getName());

  @EJB ComponentFacade componentFacade;
  @EJB SystemFacade systemFacade;
  @EJB CategoryFacade categoryFacade;
  @EJB EmailFacade emailFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public MaskingRequestFacade() {
    super(MaskingRequest.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public void createRequest(List<BigInteger> componentIdList, String reason, Date expirationDate)
      throws UserFriendlyException {

    String username = checkAuthenticated();

    Date requestDate = new Date();

    if (reason == null || reason.trim().isEmpty()) {
      throw new UserFriendlyException("Reason must not be empty");
    }

    Date now = new Date();
    if (expirationDate == null || expirationDate.before(now)) {
      throw new UserFriendlyException("Expiration must be a future date");
    }

    List<String> componentNameList = new ArrayList<>();

    if (componentIdList != null && !componentIdList.isEmpty()) {
      for (BigInteger componentId : componentIdList) {
        Component c = componentFacade.find(componentId);

        if (c != null) {
          if (c.isMasked()) {
            throw new UserFriendlyException("Component is already masked: " + c.getName());
          }

          if (isRequestAlreadyPending(c)) {
            throw new UserFriendlyException(
                "Component mask request already pending: " + c.getName());
          }

          MaskingRequest request = new MaskingRequest();

          request.setComponent(c);
          request.setRequestReason(reason);
          request.setMaskExpirationDate(expirationDate);
          request.setRequestDate(requestDate);
          request.setRequestBy(username);
          request.setRequestStatus(MaskingRequestStatus.PENDING);

          create(request);

          componentNameList.add(c.getName());
        } else {
          throw new UserFriendlyException("Component with ID not found: " + componentId);
        }
      }

      String proxyServer = System.getenv("FRONTEND_SERVER_URL");

      String subject = componentNameList.get(0);

      for (int i = 1; i < componentNameList.size(); i++) {
        subject = subject + ", " + componentNameList.get(i);
      }

      String body =
          reason + "\n\n\n" + proxyServer + "/srm/masks/requests?status=PENDING&qualified=";
      emailFacade.sendMaskRequestEmail(subject, body);

    } else {
      throw new UserFriendlyException("No component selected");
    }
  }

  @PermitAll
  public List<MaskingRequest> find(
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      String reason,
      MaskingRequestStatus status,
      int offset,
      int maxPerPage) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<MaskingRequest> cq = cb.createQuery(MaskingRequest.class);
    Root<MaskingRequest> root = cq.from(MaskingRequest.class);
    Join<MaskingRequest, Component> component = root.join("component");

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
      filters.add(cb.in(component.get("componentId")).value(subquery));
    }
    if (categoryId != null) {
      List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
      filters.add(component.get("system").in(systemList));
    }
    if (systemId != null) {
      filters.add(cb.equal(component.get("system"), systemId));
    }
    if (regionId != null) {
      filters.add(cb.equal(component.get("region"), regionId));
    }
    if (groupId != null) {
      Join<Component, SystemEntity> systems = component.join("system");
      Join<Category, GroupResponsibility> responsibilities =
          systems.join("groupResponsibilityList");
      filters.add(cb.equal(responsibilities.get("group"), groupId));
    }
    if (reason != null && !reason.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("requestReason")), reason.toLowerCase()));
    }

    if (status != null) {
      filters.add(cb.equal(root.get("requestStatus"), status));
    }

    /*Join<Component, SystemEntity> system = root.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));*/
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("requestDate");
    Order o0 = cb.desc(p0);
    orders.add(o0);
    Path p1 = component.get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    cq.orderBy(orders);

    cq.select(root);
    TypedQuery<MaskingRequest> q =
        getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(maxPerPage);
    List<MaskingRequest> recordList = q.getResultList();

    // These are EAGER fetched so don't need to worry about 'em
    /*if (componentList != null) {
    for (Component c : componentList) {
    JPAUtil.initialize(c.getSystem());
    JPAUtil.initialize(c.getRegion());
    }
    }*/
    return recordList;
  }

  @PermitAll
  public Long count(
      BigInteger componentId,
      BigInteger[] destinationIdArray,
      BigInteger categoryId,
      BigInteger systemId,
      BigInteger regionId,
      BigInteger groupId,
      String reason,
      MaskingRequestStatus status) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<MaskingRequest> root = cq.from(MaskingRequest.class);
    Join<MaskingRequest, Component> component = root.join("component");

    List<Predicate> filters = new ArrayList<>();

    destinationIdArray = IOUtil.removeNullValues(destinationIdArray, BigInteger.class);

    if (destinationIdArray != null && destinationIdArray.length > 0) {
      // Join<Component, BeamDestination> destinations = root.join("beamDestinationList");
      // filters.add(destinations.in((Object[]) destinationIdArray));

      Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
      Root<BeamDestination> subqueryRoot = subquery.from(BeamDestination.class);
      Join<BeamDestination, Component> componentList = subqueryRoot.join("componentList");
      subquery.select(componentList.get("componentId"));
      Predicate p1 =
          subqueryRoot.<BigInteger>get("beamDestinationId").in(Arrays.asList(destinationIdArray));
      subquery.where(p1);
      filters.add(cb.in(component.get("componentId")).value(subquery));
    }
    if (componentId != null) {
      filters.add(cb.equal(component.get("componentId"), componentId));
    }
    if (categoryId != null) {
      List<SystemEntity> systemList = systemFacade.fetchHierarchy(categoryId, BigInteger.ONE);
      filters.add(component.get("system").in(systemList));
    }
    if (systemId != null) {
      filters.add(cb.equal(component.get("system"), systemId));
    }
    if (regionId != null) {
      filters.add(cb.equal(component.get("region"), regionId));
    }
    if (groupId != null) {
      Join<Component, SystemEntity> systems = component.join("system");
      Join<Category, GroupResponsibility> responsibilities =
          systems.join("groupResponsibilityList");
      filters.add(cb.equal(responsibilities.get("group"), groupId));
    }
    if (reason != null && !reason.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("requestReason")), reason.toLowerCase()));
    }

    if (status != null) {
      filters.add(cb.equal(root.get("requestStatus"), status));
    }

    /*Join<Component, SystemEntity> system = component.join("system");
    Join<SystemEntity, Application> application = system.join("applicationList");
    filters.add(cb.equal(application.get("applicationId"), 1));*/

    /*statusIdArray = IOUtil.removeNullValues(statusIdArray, BigInteger.class);

    if (statusIdArray != null && statusIdArray.length > 0) {
        Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
        Root<Status> subqueryRoot = subquery.from(Status.class);
        Join<Status, ComponentStatus> componentList = subqueryRoot.join("componentStatusList");
        subquery.select(componentList.<BigInteger>get("componentId"));
        Predicate p1 = subqueryRoot.<BigInteger>get("statusId").in(Arrays.asList(
                statusIdArray));
        subquery.where(p1);
        filters.add(cb.in(root.get("componentId")).value(subquery));
    }             */
    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getResultList().get(0);
  }

  @PermitAll
  public void denyMaskRequest(BigInteger maskRequestId) throws UserFriendlyException {
    String username = checkAuthenticated();

    if (maskRequestId == null) {
      throw new UserFriendlyException("masking request ID must not be empty");
    }

    MaskingRequest request = find(maskRequestId);

    if (request == null) {
      throw new UserFriendlyException("masking request not found for given ID: " + maskRequestId);
    }

    Component component = request.getComponent();

    Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());

    checkAdminOrBranchAdmin(username, branchRoot);

    request.setRequestStatus(MaskingRequestStatus.REJECTED);
  }

  @PermitAll
  public void acceptMaskRequest(BigInteger maskRequestId, String reason, Date expiration)
      throws UserFriendlyException {
    if (maskRequestId == null) {
      throw new UserFriendlyException("masking request ID must not be empty");
    }

    MaskingRequest request = find(maskRequestId);

    if (request == null) {
      throw new UserFriendlyException("masking request not found for given ID: " + maskRequestId);
    }

    request.setRequestStatus(MaskingRequestStatus.ACCEPTED);

    Component component = request.getComponent();

    // if (component.isMasked()) {
    //    throw new UserFriendlyException("Component " + component.getName() + " is already
    // masked");
    // }

    if (expiration != null) {
      Date now = new Date();
      if (expiration.before(now)) {
        throw new UserFriendlyException("Please provide a future expiration date");
      }
    }

    // If user isn't authorized this method will catch it
    componentFacade.editMasked(
        component.getComponentId(),
        true,
        reason,
        expiration,
        Status.MASKED.getStatusId().intValue());
  }

  @PermitAll
  public boolean isRequestAlreadyPending(Component c) {
    long count =
        count(c.getComponentId(), null, null, null, null, null, null, MaskingRequestStatus.PENDING);

    return count > 0;
  }
}
