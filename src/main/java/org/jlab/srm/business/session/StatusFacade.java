package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Status;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class StatusFacade extends AbstractFacade<Status> {
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public StatusFacade() {
        super(Status.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public List<Status> findMultiple(BigInteger[] statusIdArray) {
        TypedQuery<Status> q = em.createQuery("select a from Status a where a.statusId in :statusIdArray", Status.class);

        q.setParameter("statusIdArray", Arrays.asList(statusIdArray));

        return q.getResultList();
    }

}
