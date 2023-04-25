package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Workgroup;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ryans
 */
@Stateless
public class WorkgroupFacade extends AbstractFacade<Workgroup> {
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public WorkgroupFacade() {
        super(Workgroup.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
