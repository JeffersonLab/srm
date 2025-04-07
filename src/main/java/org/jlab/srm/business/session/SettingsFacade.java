package org.jlab.srm.business.session;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.srm.persistence.entity.Settings;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles("srm-admin")
public class SettingsFacade extends AbstractFacade<Settings> {
  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public SettingsFacade() {
    super(Settings.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public Settings findSettings() {
    return find(BigInteger.ONE);
  }

  @RolesAllowed("srm-admin")
  public void updateGoalDate(Date goalDate) {
    Settings settings = findSettings();

    settings.setGoalDate(goalDate);
  }

  @RolesAllowed("srm-admin")
  public void setAutoEmail(boolean autoEmail) {
    Settings settings = findSettings();

    settings.setAutoEmail(autoEmail);
  }
}
