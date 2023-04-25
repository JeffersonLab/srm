package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.SavedSignoffType;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ryans
 */
@Stateless
public class SavedSignoffTypeFacade extends AbstractFacade<SavedSignoffType> {
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public SavedSignoffTypeFacade() {
        super(SavedSignoffType.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
