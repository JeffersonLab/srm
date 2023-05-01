package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.SystemEntity;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.persistence.util.JPAUtil;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"srm-admin", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class CategoryFacade extends AbstractFacade<Category> {

    public static final BigInteger ROOT_CATEGORY_ID = BigInteger.valueOf(0L);
    @Resource(mappedName = "jdbc/srm")
    DataSource ds;
    @EJB
    SystemFacade systemFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public CategoryFacade() {
        super(Category.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public Category findRoot() {
        // For performance reasons we use named query to ensure JPA doesn't try
        // join on parentId; also we assume root id
        TypedQuery<Category> q = em.createNamedQuery("Category.findRoot", Category.class);
        List<Category> results = q.getResultList();
        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @PermitAll
    public Category findRootWithChildren() {
        findAll(); // Load all categories into em cache
        Category root = findRoot(); // Should be cache hit
        initializeChildrenWithSystemList(root); // Recursive; should all be cache hits
        return root;
    }

    @PermitAll
    public void initializeChildrenWithSystemList(Category parent) {
        if (parent.getCategoryList() != null) {
            for (Category child : parent.getCategoryList()) {
                initializeChildrenWithSystemList(child);
            }
        }
        JPAUtil.initialize(parent.getSystemList());
    }

    @PermitAll
    @SuppressWarnings("unchecked")
    public List<Category> findCategoryDescendents(BigInteger categoryId) {
        Query q = em.createNativeQuery(
                "select * from category c start with c.parent_id = :rootId connect by prior c.category_id = c.parent_id",
                Category.class);

        q.setParameter("rootId", categoryId);

        return q.getResultList();
    }

    @PermitAll
    @SuppressWarnings("unchecked")
    public Category findBranchRoot(Category category) {
        String sql = "select * from (select * from category where category_id != 0 "
                + "connect by category_id = prior parent_id "
                + "start with category_id = :categoryId order by level desc) where rownum = 1";

        Query q = em.createNativeQuery(sql, Category.class);

        q.setParameter("categoryId", category.getCategoryId());

        List<Category> resultList = q.getResultList();

        Category branchRoot = null;

        if (resultList != null && !resultList.isEmpty()) {
            branchRoot = resultList.get(0);
        }

        return branchRoot;
    }

    @PermitAll
    public List<Category> findAllViaCartesianProduct() {
        TypedQuery<Category> q = em.createQuery(
                "select c from Category c left join fetch c.categoryList", Category.class);
        return q.getResultList();
    }

    @PermitAll
    public Category findRootWithAllDescendents() {
        findAllViaCartesianProduct(); // Load all categories into em cache via single query
        Category root = findRoot(); // Should be cache hit
        return root;
    }

    @PermitAll
    public Category findBranch(BigInteger categoryId, BigInteger applicationId) {
        Category category;

        if (categoryId == null) {
            categoryId = ROOT_CATEGORY_ID;
        }

        findAllViaCartesianProduct(); // load all categories AND their children relationships

        category = find(categoryId);

        if (applicationId != null) {
            category = pruneCategoryTree(category, applicationId);
        }

        return category;
    }

    private Category pruneCategoryTree(Category category, BigInteger applicationId) {

        if (category != null) {
            em.detach(category);
            List<BigInteger> categoryIdList = findCategoryIdList(applicationId);
            Collections.sort(categoryIdList);
            int index = Collections.binarySearch(categoryIdList, category.getCategoryId());
            if (index < 0) {
                category = null;
            } else {
                pruneCategoryDescendents(category, categoryIdList);
            }
        }

        return category;
    }

    private void pruneCategoryDescendents(Category category, List<BigInteger> categoryIdList) {
        int index;

        List<Category> childrenList = new ArrayList<Category>();

        if (category.getCategoryList() != null) {
            for (Category c : category.getCategoryList()) {
                index = Collections.binarySearch(categoryIdList, c.getCategoryId());

                if (index >= 0) {
                    childrenList.add(c);
                }
            }
        }

        em.detach(category);
        category.setCategoryList(childrenList);
        for (Category c : childrenList) {
            pruneCategoryDescendents(c, categoryIdList);
        }
    }

    @SuppressWarnings("unchecked")
    private List<BigInteger> findCategoryIdList(BigInteger applicationId) {
        Query q = em.createNativeQuery(
                "select distinct category_id from category z start with z.category_id in (select category_id from system a where system_id in (select system_id from system_application where application_id = :applicationId)) connect by prior z.parent_id = z.category_id");

        q.setParameter("applicationId", applicationId);

        List<BigInteger> categoryIdList = new ArrayList<BigInteger>();

        List<Object> resultList = q.getResultList();

        if (resultList != null) {
            for (Object row : resultList) {
                BigInteger categoryId = BigInteger.valueOf(((Number) row).longValue());
                categoryIdList.add(categoryId);
            }
        }

        return categoryIdList;
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

        Category parent = find(parentId);

        if (parent == null) {
            throw new UserFriendlyException("Could not find parent with ID: " + parentId);
        }

        Category branchRoot = findBranchRoot(parent);

        checkAdminOrBranchAdmin(username, branchRoot);

        Category c = new Category();
        c.setParentId(parent);
        c.setName(name);
        c.setWeight(BigInteger.valueOf(1000));

        create(c);
    }

    @PermitAll
    public void remove(BigInteger categoryId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (categoryId == null) {
            throw new UserFriendlyException("categoryId must not be null");
        }

        Category category = find(categoryId);

        if (category == null) {
            throw new UserFriendlyException("Could not find category with ID: " + categoryId);
        }

        Category branchRoot = findBranchRoot(category);

        checkAdminOrBranchAdmin(username, branchRoot);

        this.remove(category);
    }

    @PermitAll
    public void edit(BigInteger categoryId, BigInteger parentId, String name) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (categoryId == null) {
            throw new UserFriendlyException("categoryId must not be null");
        }

        if (parentId == null) {
            throw new UserFriendlyException("parentId must not be null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("name must not be null");
        }

        Category parent = find(parentId);

        if (parent == null) {
            throw new UserFriendlyException("Could not find parent with ID: " + parentId);
        }

        Category c = find(categoryId);

        if (c == null) {
            throw new UserFriendlyException("Could not find category with ID: " + categoryId);
        }

        if (c.equals(parent)) {
            throw new UserFriendlyException("You cannot make a category be it's own parent");
        }

        Category branchRoot = findBranchRoot(c);

        checkAdminOrBranchAdmin(username, branchRoot);

        c.setParentId(parent);
        c.setName(name);
    }

    @RolesAllowed("srm-admin")
    public void renameRoot(String name) throws UserFriendlyException {
        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("name must not be null");
        }

        Category root = this.findRoot();

        if (root == null) {
            throw new UserFriendlyException("No root exists to rename.  Looks like you're going to have to get your hands dirty.");
        }

        root.setName(name);
    }

    /**
     * Returns the bottom-most category, but loads all of it's parent categories.
     *
     * @param systemId
     * @param applicationId
     * @return
     */
    @PermitAll
    public Category findBranchReverse(BigInteger systemId, BigInteger applicationId) {
        SystemEntity system = systemFacade.find(systemId);

        Category category = system.getCategory();

        while (category.getParentId() != null) {
            category = category.getParentId();
            category.getName(); // tickle to load
        }

        return system.getCategory();
    }
}
