package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Checklist;
import org.jlab.srm.persistence.entity.ChecklistHistory;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
public class ChecklistHistoryFacade extends AbstractFacade<ChecklistHistory> {
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public ChecklistHistoryFacade() {
        super(ChecklistHistory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public void newHistory(Checklist checklist) {
        ChecklistHistory history = new ChecklistHistory();

        history.setChecklist(checklist);
        history.setAuthor(checklist.getAuthor());
        history.setBodyHtml(checklist.getBodyHtml());
        history.setComments(checklist.getComments());
        history.setModifiedBy(checklist.getModifiedBy());
        history.setModifiedDate(checklist.getModifiedDate());

        createSpecial(history);
    }


    /**
     * THIS IS A HACK.  Can't get ENVERS to stop trying to version this thing.
     *
     * @param checklist
     */
    @PermitAll
    public void createSpecial(ChecklistHistory checklist) {
        Query q = em.createNativeQuery("insert into checklist_history (checklist_history_id, checklist_id, body_html, author, modified_date, modified_username, comments) values (checklist_history_id.nextval, ?, ?, ?, ?, ?, ?)");

        q.setParameter(1, checklist.getChecklist().getChecklistId());
        q.setParameter(2, checklist.getBodyHtml());
        q.setParameter(3, checklist.getAuthor());
        q.setParameter(4, checklist.getModifiedDate());
        q.setParameter(5, checklist.getModifiedBy());
        q.setParameter(6, checklist.getComments());

        q.executeUpdate();
    }

    @PermitAll
    public int countByChecklistId(BigInteger checklistId) {
        TypedQuery<Long> q = em.createQuery("select count(c) from ChecklistHistory c where c.checklist.checklistId = :checklistId", Long.class);

        q.setParameter("checklistId", checklistId);

        return q.getResultList().get(0).intValue();
    }

    @PermitAll
    public List<ChecklistHistory> findByChecklistId(BigInteger checklistId) {
        TypedQuery<ChecklistHistory> q = em.createQuery("select c from ChecklistHistory c where c.checklist.checklistId = :checklistId order by c.modifiedDate asc", ChecklistHistory.class);

        q.setParameter("checklistId", checklistId);

        return q.getResultList();
    }
}
