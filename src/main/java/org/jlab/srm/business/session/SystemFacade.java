package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Application;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.persistence.util.JPAUtil;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.*;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"hcoadm", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class SystemFacade extends AbstractFacade<SystemEntity> {

    @EJB
    CategoryFacade categoryFacade;
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public SystemFacade() {
        super(SystemEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public SystemEntity findWithRelatedData(BigInteger systemId) {
        SystemEntity system = find(systemId);

        if (system != null) {
            JPAUtil.initialize(system.getGroupResponsibilityList());
            JPAUtil.initialize(system.getComponentList());
            JPAUtil.initialize(system.getApplicationList());
        }

        return system;
    }

    @PermitAll
    public SystemEntity findWithResponsibilities(BigInteger systemId) {
        SystemEntity system = find(systemId);

        if (system != null) {
            JPAUtil.initialize(system.getGroupResponsibilityList());
        }

        return system;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<SystemEntity> findAllHco() {
        Query q = em.createNativeQuery("select * from system where system_id in (select system_id from system_application where application_id = 1) order by weight asc, name asc", SystemEntity.class);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<SystemEntity> findByGroup(BigInteger groupId) {
        if (groupId == null) {
            return findAllHco();
        }

        Query q = em.createNativeQuery("select * from system where system_id in (select system_id from system_application where application_id = 1) and system_id in (select system_id from group_responsibility where group_id = :groupId) order by weight asc, name asc", SystemEntity.class);

        q.setParameter("groupId", groupId);

        return q.getResultList();
    }

    @PermitAll
    public List<SystemEntity> findAllWithCategory() {
        List<SystemEntity> systemList = findAll(new OrderDirective("name"));

        for (SystemEntity system : systemList) {
            system.getCategory().getName(); // tickle to force proxy to load
        }

        return systemList;
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<SystemEntity> findByBeamDestination(BigInteger destinationId) {
        String query = "select * from system where system_id in (select distinct system_id from component a, component_beam_destination b where a.component_id = b.component_id and beam_destination_id = :destinationId)";
        Query q = em.createNativeQuery(query, SystemEntity.class);

        q.setParameter("destinationId", destinationId);

        return q.getResultList();
    }

    @PermitAll
    public List<SystemEntity> findByComponentCategoryAndSystem(BigInteger componentId, BigInteger categoryId, BigInteger systemId, BigInteger applicationId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SystemEntity> cq = cb.createQuery(SystemEntity.class);
        Root<SystemEntity> root = cq.from(SystemEntity.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (componentId != null) {
            Join<SystemEntity, Component> components = root.join("componentList");
            filters.add(components.in(componentId));
        }
        if (categoryId != null) {
            filters.add(cb.equal(root.get("category").get("categoryId"), categoryId));
        }
        if (systemId != null) {
            filters.add(cb.equal(root.get("systemId"), systemId));
        }
        if (applicationId != null) {
            Join<SystemEntity, Application> application = root.join("applicationList");
            filters.add(cb.equal(application.get("applicationId"), applicationId));
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("name");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);

        return getEntityManager().createQuery(cq).getResultList();
    }

    @PermitAll
    public List<SystemEntity> findWithCategory(BigInteger[] categoryIdArray, BigInteger componentId, BigInteger systemId, BigInteger applicationId, boolean recurse, boolean loadApplications) {
        List<SystemEntity> systemList = new ArrayList<>();

        // Just load entire hierarchy of categories and cache in em
        categoryFacade.findAllViaCartesianProduct();

        if(categoryIdArray != null && (categoryIdArray.length == 0 || categoryIdArray[0] == null)) {
            categoryIdArray = null;
        }

        // If searching by component, or by system_id, or for all systems (no filter), or by category, but without recursion
        if (componentId != null || systemId != null || (componentId == null && systemId == null && categoryIdArray == null) || (categoryIdArray != null && !recurse)) {

            // Zero or one (first) category are used if searching by componentId or systemId
            BigInteger categoryId = categoryIdArray == null ? null : categoryIdArray[0];

            systemList = findByComponentCategoryAndSystem(componentId, categoryId, systemId, applicationId);
        } else {
            // componentId == null && systemId == null && categoryId != null && recurse == true
            // Search for systems by category using recursion

            Set<SystemEntity> systemSet = new LinkedHashSet<>(); // Prevent duplicates
            for(BigInteger categoryId: categoryIdArray) {
                if(categoryId != null) {
                    systemSet.addAll(fetchHierarchy(categoryId, applicationId));
                }
            }
            systemList.addAll(systemSet);
        }

        if (loadApplications) {
            for (SystemEntity system : systemList) {
                    JPAUtil.initialize(system.getApplicationList());
            }
        }

        return systemList;
    }

    // Note: method above is same except categoryIdArray is used.
    @PermitAll
    public List<SystemEntity> findWithCategory(BigInteger categoryId, BigInteger componentId, BigInteger systemId, BigInteger applicationId, boolean recurse, boolean loadApplications) {
        List<SystemEntity> systemList;

        // Just load entire hierarchy of categories and cache in em
        categoryFacade.findAllViaCartesianProduct();

        // If searching by component, or by system_id, or for all systems (no filter), or by category, but without recursion
        if (componentId != null || systemId != null || (componentId == null && systemId == null && categoryId == null) || (categoryId != null && !recurse)) {
            systemList = findByComponentCategoryAndSystem(componentId, categoryId, systemId, applicationId);
        } else {
            // componentId == null && systemId == null && categoryId != null && recurse == true
            // Search for systems by category using recursion
            systemList = fetchHierarchy(categoryId, applicationId);
        }

        if (loadApplications) {
            for (SystemEntity system : systemList) {
                JPAUtil.initialize(system.getApplicationList());
            }
        }

        return systemList;
    }

    @PermitAll
    public List<SystemEntity> fetchHierarchy(BigInteger categoryId, BigInteger applicationId) {
        List<SystemEntity> systemList;

        Category category = categoryFacade.find(categoryId);
        if (category != null) {
            systemList = gatherDescendents(category, applicationId);
            Collections.sort(systemList);
        } else {
            systemList = new ArrayList<>();
        }

        return systemList;
    }

    @PermitAll
    public List<SystemEntity> gatherDescendents(Category category, BigInteger applicationId) {
        List<SystemEntity> systemList;

        if (category.getSystemList() == null || category.getSystemList().isEmpty()) {
            systemList = new ArrayList<>();
        } else {
            systemList = new ArrayList<>();
            for (SystemEntity system : category.getSystemList()) {
                if (applicationId == null || system.getApplicationList().contains(Application.FROM_ID(applicationId))) {
                    systemList.add(system);
                }
            }
        }

        if (category.getCategoryList() != null && !category.getCategoryList().isEmpty()) {
            for (Category child : category.getCategoryList()) {
                systemList.addAll(gatherDescendents(child, applicationId));
            }
        }

        return systemList;
    }

    @PermitAll
    public void addNew(BigInteger parentId, String name) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (parentId == null) {
            throw new UserFriendlyException("parentId must not be null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("name must not be null");
        }

        Category parent = categoryFacade.find(parentId);

        if (parent == null) {
            throw new UserFriendlyException("Could not find parent with ID: " + parentId);
        }

        Category branchRoot = categoryFacade.findBranchRoot(parent);

        checkAdminOrBranchAdmin(username, branchRoot);

        SystemEntity s = new SystemEntity();
        s.setCategory(parent);
        s.setName(name);
        s.setWeight(BigInteger.valueOf(1000));

        create(s);
    }

    @PermitAll
    public void remove(BigInteger systemId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (systemId == null) {
            throw new UserFriendlyException("systemId must not be null");
        }

        SystemEntity system = find(systemId);

        if (system == null) {
            throw new UserFriendlyException("Could not find system with ID: " + systemId);
        }

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        this.remove(system);
    }

    @PermitAll
    public void edit(BigInteger systemId, BigInteger parentId, String name) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (parentId == null) {
            throw new UserFriendlyException("parentId must not be null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("name must not be null");
        }

        Category parent = categoryFacade.find(parentId);

        if (parent == null) {
            throw new UserFriendlyException("Could not find parent with ID: " + parentId);
        }

        Category branchRoot = categoryFacade.findBranchRoot(parent);

        checkAdminOrBranchAdmin(username, branchRoot);

        SystemEntity s = find(systemId);

        if (s == null) {
            throw new UserFriendlyException("Could not find subsystem with ID: " + systemId);
        }

        s.setCategory(parent);
        s.setName(name);
    }
}
