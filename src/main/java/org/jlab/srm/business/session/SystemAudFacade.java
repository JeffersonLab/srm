package org.jlab.srm.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.srm.persistence.entity.aud.SystemAud;

/**
 * @author ryans
 */
@Stateless
public class SystemAudFacade extends AbstractFacade<SystemAud> {
  @EJB ApplicationRevisionInfoFacade revisionFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public SystemAudFacade() {
    super(SystemAud.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public List<SystemAud> filterList(
      BigInteger systemId, BigInteger revisionId, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<SystemAud> cq = cb.createQuery(SystemAud.class);
    Root<SystemAud> root = cq.from(SystemAud.class);
    cq.select(root);

    List<Predicate> filters = new ArrayList<>();

    if (systemId != null) {
      filters.add(cb.equal(root.get("systemAudPK").get("systemId"), systemId));
    }

    if (revisionId != null) {
      filters.add(cb.equal(root.get("revision").get("id"), revisionId));
    }

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }
    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("revision").get("id");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);

    List<SystemAud> entityList =
        getEntityManager()
            .createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(max)
            .getResultList();

    if (entityList != null) {
      for (SystemAud entity : entityList) {
        entity.getRevision().getId(); // Tickle to load
      }
    }

    return entityList;
  }

  @PermitAll
  public Long countFilterList(BigInteger entityId, BigInteger revisionId) {
    String selectFrom = "select count(*) from SYSTEM_AUD e ";

    List<String> whereList = new ArrayList<>();

    String w;

    if (entityId != null) {
      w = "e.system_id = " + entityId;
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
  public void loadStaff(List<SystemAud> entityList) {
    if (entityList != null) {
      for (SystemAud entity : entityList) {
        revisionFacade.loadUsers(entity.getRevision());
      }
    }
  }
}
