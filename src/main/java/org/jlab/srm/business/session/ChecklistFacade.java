package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.Checklist;
import org.jlab.srm.persistence.entity.GroupResponsibility;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author ryans
 */
@Stateless
public class ChecklistFacade extends AbstractFacade<Checklist> {

    @EJB
    GroupResponsibilityFacade responsibilityFacade;
    @EJB
    ChecklistHistoryFacade historyFacade;
    @PersistenceContext(unitName = "srmPU")
    private EntityManager em;

    public ChecklistFacade() {
        super(Checklist.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public void saveChanges(BigInteger checklistId, String author, String bodyHtml, String comments) throws UserFriendlyException {
        Checklist checklist = find(checklistId);

        if (checklist == null) {
            throw new UserFriendlyException("Checklist with ID: " + checklistId + " not found");
        }

        if (author == null || author.isEmpty()) {
            throw new UserFriendlyException("Author is required");
        }

        if (comments == null || comments.isEmpty()) {
            throw new UserFriendlyException("Revision Comments are required");
        }

        String username = checkAuthenticated();

        if (checklist.getGroupResponsibility().isPublished()) {
            checkAdminOrGroupLeader(username, checklist.getGroupResponsibility().getGroup());
        }

        bodyHtml = Jsoup.clean(bodyHtml, Safelist.basic().addAttributes(":all", "style").addTags("h1", "h2", "h3", "h4", "h5", "h6"));

        checklist.setBodyHtml(bodyHtml);

        checklist.setAuthor(author);
        checklist.setComments(comments);
        checklist.setModifiedBy(username);
        checklist.setModifiedDate(new Date());

        historyFacade.newHistory(checklist);
    }

    @PermitAll
    public BigInteger createNew(BigInteger groupId, BigInteger systemId, String author, String bodyHtml, String comments) throws UserFriendlyException {
        GroupResponsibility responsibility = responsibilityFacade.findBySystemIdAndGroupId(systemId, groupId);

        if (responsibility == null) {
            throw new UserFriendlyException("No responsibility for groupId " + groupId + " and systemId " + systemId);
        }

        if (author == null || author.isEmpty()) {
            throw new UserFriendlyException("Author is required");
        }

        /*Revision comments are NOT required on first revision */

        String username = checkAuthenticated();

        if (responsibility.isPublished()) {
            checkAdminOrGroupLeader(username, responsibility.getGroup());
        }

        if (responsibility.getChecklist() != null) {
            throw new UserFriendlyException("Checklist already exists for groupId " + groupId + " and systemId " + systemId);
        }

        Checklist checklist = new Checklist();

        bodyHtml = Jsoup.clean(bodyHtml, Safelist.basic().addAttributes(":all", "style").addTags("h1", "h2", "h3", "h4", "h5", "h6"));

        checklist.setBodyHtml(bodyHtml);

        checklist.setGroupResponsibility(responsibility);
        checklist.setAuthor(author);
        checklist.setComments(comments);
        checklist.setModifiedBy(username);
        checklist.setModifiedDate(new Date());

        checklist = createSpecial(checklist);

        historyFacade.newHistory(checklist);

        responsibility.setChecklist(checklist);

        return checklist.getChecklistId();
    }

    /**
     * THIS IS A HACK.  Can't get ENVERS to stop trying to version this thing.
     *
     * @param checklist
     * @return
     */
    @PermitAll
    public Checklist createSpecial(Checklist checklist) {
        Query q1 = em.createNativeQuery("select checklist_id.nextval from dual");

        BigInteger checklistId = BigInteger.valueOf(((Number) q1.getSingleResult()).longValue());

        Query q = em.createNativeQuery("insert into checklist (checklist_id, body_html, author, modified_date, modified_username, comments) values (?, ?, ?, ?, ?, ?)");

        q.setParameter(1, checklistId);
        q.setParameter(2, checklist.getBodyHtml());
        q.setParameter(3, checklist.getAuthor());
        q.setParameter(4, checklist.getModifiedDate());
        q.setParameter(5, checklist.getModifiedBy());
        q.setParameter(6, checklist.getComments());

        q.executeUpdate();

        return find(checklistId);
    }

    @PermitAll
    public BigInteger delete(BigInteger checklistId) throws UserFriendlyException {
        Checklist checklist = find(checklistId);

        if (checklist == null) {
            throw new UserFriendlyException("Unable to find checklist with ID: " + checklistId);
        }

        GroupResponsibility responsibility = checklist.getGroupResponsibility();

        String username = checkAuthenticated();
        checkAdminOrGroupLeader(username, responsibility.getGroup());

        BigInteger groupId = responsibility.getGroup().getGroupId();

        responsibility.setChecklist(null);
        responsibility.setPublished(false);
        responsibility.setPublishedBy(null);
        responsibility.setPublishedDate(null);

        removeSpecial(checklist);

        return groupId;
    }

    /**
     * HACK - to get ENVERS to stop trying to version delete!
     *
     * @param checklist
     */
    @PermitAll
    public void removeSpecial(Checklist checklist) {
        Query q = em.createNativeQuery("delete from checklist where checklist_id = ?");

        q.setParameter(1, checklist.getChecklistId());

        q.executeUpdate();
    }
}
