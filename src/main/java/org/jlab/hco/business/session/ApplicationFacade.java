package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.Application;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.SystemEntity;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"hcoadm", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class ApplicationFacade extends AbstractFacade<Application> {

    @EJB
    SystemFacade systemFacade;
    @EJB
    CategoryFacade categoryFacade;
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public ApplicationFacade() {
        super(Application.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public void toggle(BigInteger systemId, BigInteger applicationId) {
        String username = checkAuthenticated();

        SystemEntity system = systemFacade.find(systemId);

        Category branchRoot = categoryFacade.findBranchRoot(system.getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        Application application = find(applicationId);

        List<Application> applicationList = system.getApplicationList();
        List<SystemEntity> systemList = application.getSystemList();

        //TODO: change systemList to set in the entity since hibernate is deleting all and adding back in
        if (applicationList.contains(application)) {
            //applicationList.remove(application);
            systemList.remove(system); // This is owning side
        } else {
            //applicationList.add(application);
            systemList.add(system); // This is owning side
        }
    }

}
