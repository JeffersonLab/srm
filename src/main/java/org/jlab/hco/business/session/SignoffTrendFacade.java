package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.GroupSignoffHistory;
import org.jlab.hco.persistence.model.SignoffTrendInfo;
import org.jlab.smoothness.persistence.util.JPAUtil;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class SignoffTrendFacade extends AbstractFacade<GroupSignoffHistory> {

    @PersistenceContext(unitName = "hcoPU")
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
        String sql
                = "select day_date, signoff_ready_count(day_date) as day_count from "
                + "(SELECT (:start + (LEVEL - 1)) as day_date "
                + " FROM DUAL connect by level <= (:end - :start) + 1)";

        Query q = em.createNativeQuery(sql);

        q.setParameter("start", start, TemporalType.DATE);
        q.setParameter("end", end, TemporalType.DATE);

        return JPAUtil.getResultList(q, SignoffTrendInfo.class);
    }
}
