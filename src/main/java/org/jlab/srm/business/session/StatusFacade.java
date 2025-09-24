package org.jlab.srm.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.jlab.srm.persistence.entity.Status;

/**
 * @author ryans
 */
@Stateless
public class StatusFacade extends AbstractFacade<Status> {
  @PersistenceContext(unitName = "webappPU")
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
    TypedQuery<Status> q =
        em.createQuery("select a from Status a where a.statusId in :statusIdArray", Status.class);

    q.setParameter("statusIdArray", Arrays.asList(statusIdArray));

    return q.getResultList();
  }
}
