package org.jlab.srm.business.session;

import org.hibernate.envers.RevisionType;
import org.jlab.srm.persistence.entity.ApplicationRevisionInfo;
import org.jlab.srm.persistence.entity.Category;
import org.jlab.srm.persistence.entity.Component;
import org.jlab.srm.persistence.entity.Staff;
import org.jlab.srm.persistence.model.AuditedEntityChange;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class ApplicationRevisionInfoFacade extends AbstractFacade<ApplicationRevisionInfo> {

    @EJB
    StaffFacade staffFacade;
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public ApplicationRevisionInfoFacade() {
        super(ApplicationRevisionInfo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<ApplicationRevisionInfo> filterList(Date modifiedStart, Date modifiedEnd, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApplicationRevisionInfo> cq = cb.createQuery(ApplicationRevisionInfo.class);
        Root<ApplicationRevisionInfo> root = cq.from(ApplicationRevisionInfo.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (modifiedStart != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("ts"), modifiedStart.getTime()));
        }

        if (modifiedEnd != null) {
            filters.add(cb.lessThan(root.get("ts"), modifiedEnd.getTime()));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("id");
        Order o0 = cb.desc(p0);
        orders.add(o0);
        cq.orderBy(orders);

        List<ApplicationRevisionInfo> revisionList = getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();

        if (revisionList != null) {
            for (ApplicationRevisionInfo revision : revisionList) {
                revision.setChangeList(findEntityChangeList(revision.getId()));
            }
        }

        return revisionList;
    }

    @PermitAll
    public Long countFilterList(Date modifiedStart, Date modifiedEnd) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ApplicationRevisionInfo> root = cq.from(ApplicationRevisionInfo.class);

        List<Predicate> filters = new ArrayList<>();

        if (modifiedStart != null) {
            filters.add(cb.greaterThanOrEqualTo(root.get("ts"), modifiedStart.getTime()));
        }

        if (modifiedEnd != null) {
            filters.add(cb.lessThan(root.get("ts"), modifiedEnd.getTime()));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @PermitAll
    public List<AuditedEntityChange> findEntityChangeList(long revision) {
        Query q = em.createNativeQuery("select 'A', component_id, name, revtype from component_aud where rev = ?1 union select 'B', system_id, name, revtype from system_aud where rev = ?2 union select 'C', category_id, name, revtype from category_aud where rev = ?3");

        q.setParameter(1, revision);
        q.setParameter(2, revision);
        q.setParameter(3, revision);

        List<Object[]> resultList = q.getResultList();

        List<AuditedEntityChange> changeList = new ArrayList<>();

        if (resultList != null) {
            for (Object[] row : resultList) {
                Class entityClass = fromCharacter(((Character) row[0]));
                BigInteger entityId = BigInteger.valueOf(((Number) row[1]).longValue());
                String entityName = (String) row[2];
                RevisionType type = fromNumber((Number) row[3]);
                changeList.add(new AuditedEntityChange(revision, type, entityId, entityName, entityClass));
            }
        }

        return changeList;
    }

    @PermitAll
    public Class fromCharacter(Character c) {
        Class entityClass = null;

        if (c != null) {
            switch (c) {
                case 'A':
                    entityClass = Component.class;
                    break;
                case 'B':
                    entityClass = System.class;
                    break;
                case 'C':
                    entityClass = Category.class;
                    break;
                default:
                    break;
            }
        }

        return entityClass;
    }

    @PermitAll
    public RevisionType fromNumber(Number n) {
        RevisionType type = null;

        if (n != null) {
            int intValue = (int) n.longValue();

            switch (intValue) {
                case 0:
                    type = RevisionType.ADD;
                    break;
                case 1:
                    type = RevisionType.MOD;
                    break;
                case 2:
                    type = RevisionType.DEL;
                    break;
            }
        }

        return type;
    }

    @PermitAll
    public void loadStaff(List<ApplicationRevisionInfo> revisionList) {
        if (revisionList != null) {
            for (ApplicationRevisionInfo revision : revisionList) {
                loadStaff(revision);
            }
        }
    }

    @PermitAll
    public void loadStaff(ApplicationRevisionInfo revision) {
        if (revision != null) {
            String username = revision.getUsername();

            if (username != null) {
                Staff staff = staffFacade.findByUsername(username);

                revision.setStaff(staff);
            }
        }
    }
}
