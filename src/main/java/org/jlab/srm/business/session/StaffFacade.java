package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Staff;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class StaffFacade extends AbstractFacade<Staff> {

    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public StaffFacade() {
        super(Staff.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<Staff> search(String term, Integer maxResults) {
        TypedQuery<Staff> q = em.createQuery("select s from Staff s where upper(username) like :term order by username asc", Staff.class);

        if (term == null) {
            term = "";
        }

        q.setParameter("term", "%" + term.toUpperCase() + "%");

        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }

        return q.getResultList();
    }

    @PermitAll
    public Staff findByUsername(String username) {
        TypedQuery<Staff> q = em.createQuery("select s from Staff s where username = :username", Staff.class);

        q.setParameter("username", username);

        Staff staff = null;

        List<Staff> resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            staff = resultList.get(0);
        }

        return staff;
    }

    @PermitAll
    public Long count(String term) {
        TypedQuery<Long> q = em.createQuery("select count(*) from Staff s where upper(username) like :term", Long.class);

        q.setParameter("term", "%" + term.toUpperCase() + "%");

        return q.getSingleResult();
    }
}
