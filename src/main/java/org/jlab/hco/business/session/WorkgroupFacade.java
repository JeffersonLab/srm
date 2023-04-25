package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.Workgroup;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ryans
 */
@Stateless
public class WorkgroupFacade extends AbstractFacade<Workgroup> {
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public WorkgroupFacade() {
        super(Workgroup.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
