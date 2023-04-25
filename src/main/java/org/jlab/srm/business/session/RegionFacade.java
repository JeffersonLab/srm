package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Region;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class RegionFacade extends AbstractFacade<Region> {

    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public RegionFacade() {
        super(Region.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<Region> findMultiple(BigInteger[] regionIdArray) {
        TypedQuery<Region> q = em.createQuery("select r from Region r where r.regionId in :regionIdList", Region.class);

        q.setParameter("regionIdList", Arrays.asList(regionIdArray));

        return q.getResultList();
    }

    @PermitAll
    public List<Region> filterList(BigInteger systemId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Region> cq = cb.createQuery(Region.class);
        Root<Region> root = cq.from(Region.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (systemId != null) {
            Subquery<Integer> regionIdSubquery = cq.subquery(Integer.class);
            Root<Component> componentRoot = regionIdSubquery.from(Component.class);
            regionIdSubquery.select(componentRoot.<Region>get("region").get("regionId"));
            regionIdSubquery.where(cb.equal(componentRoot.get("system"), systemId));
            filters.add(cb.in(root.get("regionId")).value(regionIdSubquery));
        }
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("weight");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);

        return getEntityManager().createQuery(cq).getResultList();
    }
}
