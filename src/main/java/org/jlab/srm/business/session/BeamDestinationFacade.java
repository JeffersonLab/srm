package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.BeamDestination;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class BeamDestinationFacade extends AbstractFacade<BeamDestination> {

    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public BeamDestinationFacade() {
        super(BeamDestination.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @RolesAllowed("srm-admin")
    public void setTarget(BigInteger[] targetArray) {
        Query q = em.createQuery("update BeamDestination a set a.targetYn = 'N'");
        q.executeUpdate();

        if (targetArray != null && targetArray.length > 0) {
            q = em.createQuery("update BeamDestination a set a.targetYn = 'Y' where a.beamDestinationId in :destinationIdArray");
            q.setParameter("destinationIdArray", Arrays.asList(targetArray));
            q.executeUpdate();
        }
    }

    @PermitAll
    public List<BeamDestination> filterTargetList(List<BeamDestination> destinationList) {
        List<BeamDestination> targetList = new ArrayList<>();

        if (destinationList != null) {
            for (BeamDestination destination : destinationList) {
                if (destination.isTarget()) {
                    targetList.add(destination);
                }
            }
        }

        return targetList;
    }

    @PermitAll
    public String filterTargetCsv(List<BeamDestination> destinationList) {
        String csv = "";
        List<BeamDestination> targetList = filterTargetList(destinationList);

        if (targetList != null && !targetList.isEmpty()) {
            csv = targetList.get(0).getBeamDestinationId().toString();

            for (int i = 1; i < targetList.size(); i++) {
                csv = csv + "," + targetList.get(i).getBeamDestinationId().toString();
            }
        }

        return csv;
    }

    @PermitAll
    public String toCsv(List<BeamDestination> destinationList) {
        String csv = "";

        if (destinationList != null && !destinationList.isEmpty()) {
            csv = destinationList.get(0).getBeamDestinationId().toString();

            for (int i = 1; i < destinationList.size(); i++) {
                csv = csv + "," + destinationList.get(i).getBeamDestinationId().toString();
            }
        }

        return csv;
    }

    @PermitAll
    public BigInteger[] toIdArray(List<BeamDestination> destinationList) {
        List<BigInteger> destinationIdList = new ArrayList<>();

        if (destinationList != null) {
            for (BeamDestination destination : destinationList) {
                if (destination != null) {
                    destinationIdList.add(destination.getBeamDestinationId());
                }
            }
        }

        return destinationIdList.toArray(new BigInteger[0]);
    }

    @PermitAll
    public BeamDestination findTarget() {
        TypedQuery<BeamDestination> q = em.createQuery("select a from BeamDestination a where a.targetYn = 'Y'", BeamDestination.class);

        BeamDestination target = null;

        List<BeamDestination> destinationList = q.getResultList();

        if (destinationList != null && !destinationList.isEmpty()) {
            target = destinationList.get(0);
        }

        return target;
    }

    @PermitAll
    public List<BeamDestination> getFilteredDestinationList(BigInteger[] destinationIdArray) {
        List<BeamDestination> destinationList;

        if (destinationIdArray == null) {
            destinationList = findAll(new OrderDirective("weight"));
        } else {
            TypedQuery<BeamDestination> q = em.createQuery("select b from BeamDestination b where b.beamDestinationId in :destinationList order by weight asc", BeamDestination.class);
            q.setParameter("destinationList", Arrays.asList(destinationIdArray));
            destinationList = q.getResultList();
        }

        return destinationList;
    }

    @PermitAll
    public List<BeamDestination> filterList(BigInteger componentId) {
        String sql = "select b from BeamDestination b order by weight asc";

        if (componentId != null) {
            sql = "select b from BeamDestination b inner join b.componentList c where c.componentId = " + componentId + " order by b.weight asc";
        }

        TypedQuery<BeamDestination> q = em.createQuery(sql, BeamDestination.class);

        return q.getResultList();
    }

    @PermitAll
    public List<BeamDestination> findMultiple(BigInteger[] destinationIdArray) {
        TypedQuery<BeamDestination> q = em.createQuery("select a from BeamDestination a where a.beamDestinationId in :destinationIdArray", BeamDestination.class);

        q.setParameter("destinationIdArray", Arrays.asList(destinationIdArray));

        return q.getResultList();
    }
}
