package org.jlab.srm.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.srm.persistence.entity.SavedSignoffType;

/**
 * @author ryans
 */
@Stateless
public class SavedSignoffTypeFacade extends AbstractFacade<SavedSignoffType> {
  @PersistenceContext(unitName = "srmPU")
  private EntityManager em;

  public SavedSignoffTypeFacade() {
    super(SavedSignoffType.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
}
