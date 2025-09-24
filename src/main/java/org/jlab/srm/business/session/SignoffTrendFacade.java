package org.jlab.srm.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import org.jlab.smoothness.persistence.util.JPAUtil;
import org.jlab.srm.persistence.entity.GroupSignoffHistory;
import org.jlab.srm.persistence.model.SignoffTrendInfo;

/**
 * @author ryans
 */
@Stateless
public class SignoffTrendFacade extends AbstractFacade<GroupSignoffHistory> {

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public SignoffTrendFacade() {
    super(GroupSignoffHistory.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public List<SignoffTrendInfo> findTrendListByPeriod(Date start, Date end) {
    String sql =
        "select day_date, signoff_ready_count(day_date) as day_count from "
            + "(SELECT (:start + (LEVEL - 1)) as day_date "
            + " FROM DUAL connect by level <= (:end - :start) + 1)";

    Query q = em.createNativeQuery(sql);

    q.setParameter("start", start, TemporalType.DATE);
    q.setParameter("end", end, TemporalType.DATE);

    return JPAUtil.getResultList(q, SignoffTrendInfo.class);
  }
}
