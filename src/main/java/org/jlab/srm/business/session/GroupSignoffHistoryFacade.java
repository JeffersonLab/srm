package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.GroupSignoff;
import org.jlab.srm.persistence.entity.GroupSignoffHistory;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author ryans
 */
@Stateless
public class GroupSignoffHistoryFacade extends AbstractFacade<GroupSignoffHistory> {

    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public GroupSignoffHistoryFacade() {
        super(GroupSignoffHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public void newHistory(GroupSignoff signoff) {
        GroupSignoffHistory history = new GroupSignoffHistory();

        history.setSystemId(signoff.getGroupResponsibility().getSystem().getSystemId());
        history.setComponentId(signoff.getComponent().getComponentId());
        history.setGroupId(signoff.getGroupResponsibility().getGroup().getGroupId());
        history.setStatusId(signoff.getStatus().getStatusId());
        history.setComments(signoff.getComments());
        history.setModifiedBy(signoff.getModifiedBy());
        history.setModifiedDate(signoff.getModifiedDate());
        history.setChangeType(signoff.getChangeType());

        create(history);
    }

    /**
     * THIS IS A HACK. I can't get ENVERS (Hibernate auditing) to stop trying to version this
     * entity. So I'll just manually do the insertion.
     *
     * @param history the history
     */
    @Override
    @PermitAll
    public void create(GroupSignoffHistory history) {
        Query q = em.createNativeQuery("insert into group_signoff_history (group_signoff_history_id, system_id, group_id, component_id, status_id, modified_username, modified_date, comments, change_type) values (group_signoff_history_id.nextval, ?, ?, ?, ?, ?, ?, ?, ?)");

        q.setParameter(1, history.getSystemId());
        q.setParameter(2, history.getGroupId());
        q.setParameter(3, history.getComponentId());
        q.setParameter(4, history.getStatusId());
        q.setParameter(5, history.getModifiedBy());
        q.setParameter(6, history.getModifiedDate());
        q.setParameter(7, history.getComments());
        q.setParameter(8, history.getChangeType().name());

        q.executeUpdate();
    }
}
