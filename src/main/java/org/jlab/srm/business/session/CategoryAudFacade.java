package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.aud.CategoryAud;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class CategoryAudFacade extends AbstractFacade<CategoryAud> {
    @EJB
    ApplicationRevisionInfoFacade revisionFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public CategoryAudFacade() {
        super(CategoryAud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<CategoryAud> filterList(BigInteger categoryId, BigInteger revisionId, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CategoryAud> cq = cb.createQuery(CategoryAud.class);
        Root<CategoryAud> root = cq.from(CategoryAud.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (categoryId != null) {
            filters.add(cb.equal(root.get("categoryAudPK").get("categoryId"), categoryId));
        }

        if (revisionId != null) {
            filters.add(cb.equal(root.get("revision").get("id"), revisionId));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("revision").get("id");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);

        List<CategoryAud> entityList = getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();

        if (entityList != null) {
            for (CategoryAud entity : entityList) {
                entity.getRevision().getId(); // Tickle to load
            }
        }

        return entityList;
    }

    @PermitAll
    public Long countFilterList(BigInteger entityId, BigInteger revisionId) {
        String selectFrom = "select count(*) from CATEGORY_AUD e ";

        List<String> whereList = new ArrayList<>();

        String w;

        if (entityId != null) {
            w = "e.category_id = " + entityId;
            whereList.add(w);
        }

        if (revisionId != null) {
            w = "e.rev = " + revisionId;
            whereList.add(w);
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = "where ";
            for (String wh : whereList) {
                where = where + wh + " and ";
            }

            where = where.substring(0, where.length() - 5);
        }

        String sql = selectFrom + " " + where;
        Query q = em.createNativeQuery(sql);

        return ((Number) q.getSingleResult()).longValue();
    }

    @PermitAll
    public void loadStaff(List<CategoryAud> entityList) {
        if (entityList != null) {
            for (CategoryAud entity : entityList) {
                revisionFacade.loadStaff(entity.getRevision());
            }
        }
    }
}
