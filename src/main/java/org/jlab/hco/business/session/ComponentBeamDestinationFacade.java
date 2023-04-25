package org.jlab.hco.business.session;

import org.jlab.hco.persistence.entity.BeamDestination;
import org.jlab.hco.persistence.entity.Category;
import org.jlab.hco.persistence.entity.Component;
import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"hcoadm", "halead", "hblead", "hclead", "hdlead", "lerfadm", "cryoadm"})
public class ComponentBeamDestinationFacade extends AbstractFacade<BeamDestination> {

    @EJB
    ComponentFacade componentFacade;
    @EJB
    BeamDestinationFacade destinationFacade;
    @EJB
    CategoryFacade categoryFacade;
    @PersistenceContext(unitName = "hcoPU")
    private EntityManager em;

    public ComponentBeamDestinationFacade() {
        super(BeamDestination.class);
    }

    @PermitAll
    public void toggle(BigInteger componentId, BigInteger destinationId) throws UserFriendlyException {
        String username = checkAuthenticated();

        if (componentId == null) {
            throw new UserFriendlyException("componentId must not be null");
        }

        Component component = componentFacade.find(componentId);

        if (component == null) {
            throw new UserFriendlyException("Could not find component with ID: " + componentId);
        }

        Category branchRoot = categoryFacade.findBranchRoot(component.getSystem().getCategory());

        checkAdminOrBranchAdmin(username, branchRoot);

        if (exists(destinationId, componentId)) {
            delete(destinationId, componentId);
        } else {
            insert(destinationId, componentId);
        }
    }

    private boolean exists(BigInteger destinationId, BigInteger componentId) {
        Query q = em.createNativeQuery("select count(*) from component_beam_destination where beam_destination_id = :destinationId and component_id = :componentId");

        q.setParameter("destinationId", destinationId);
        q.setParameter("componentId", componentId);

        long count = ((Number) q.getSingleResult()).longValue();

        boolean exists = count > 0;

        return exists;
    }

    private void delete(BigInteger destinationId, BigInteger componentId) {
        Query q = em.createNativeQuery("delete from component_beam_destination where beam_destination_id = :destinationId and component_id = :componentId");

        q.setParameter("destinationId", destinationId);
        q.setParameter("componentId", componentId);

        q.executeUpdate();
    }

    private void insert(BigInteger destinationId, BigInteger componentId) {
        Query q = em.createNativeQuery("insert into component_beam_destination (beam_destination_id, component_id) values(:destinationId, :componentId)");

        q.setParameter("destinationId", destinationId);
        q.setParameter("componentId", componentId);

        q.executeUpdate();
    }

    @RolesAllowed("hcoadm")
    public void checkAll(BigInteger componentId) {
        Component component = componentFacade.find(componentId);
        List<BeamDestination> destinations = destinationFacade.findAll();
        component.setBeamDestinationList(destinations);
    }

    @RolesAllowed("hcoadm")
    public void uncheckAll(BigInteger componentId) {
        Component component = componentFacade.find(componentId);
        component.setBeamDestinationList(new ArrayList<BeamDestination>());
    }

    @RolesAllowed("hcoadm")
    public void copy(BigInteger fromComponentId, BigInteger toComponentId) {
        Component fromComponent = componentFacade.find(fromComponentId);
        Component toComponent = componentFacade.find(toComponentId);
        List<BeamDestination> destinations = fromComponent.getBeamDestinationList();
        toComponent.setBeamDestinationList(destinations);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
