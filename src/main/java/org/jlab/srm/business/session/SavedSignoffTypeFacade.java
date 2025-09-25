package org.jlab.srm.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.srm.persistence.entity.SavedSignoffType;

/**
 * @author ryans
 */
@Stateless
public class SavedSignoffTypeFacade extends AbstractFacade<SavedSignoffType> {
  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public SavedSignoffTypeFacade() {
    super(SavedSignoffType.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
}
