package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.HcoSettings;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles("hcoadm")
public class HcoSettingsFacade extends AbstractFacade<HcoSettings> {
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public HcoSettingsFacade() {
        super(HcoSettings.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public HcoSettings findSettings() {
        return find(BigInteger.ONE);
    }

    @RolesAllowed("hcoadm")
    public void updateGoalDate(Date goalDate) {
        HcoSettings settings = findSettings();

        settings.setGoalDate(goalDate);
    }

    @RolesAllowed("hcoadm")
    public void setAutoEmail(boolean autoEmail) {
        HcoSettings settings = findSettings();

        settings.setAutoEmail(autoEmail);
    }
}
