package org.jlab.hco.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CHECKLIST_HISTORY", schema = "HCO_OWNER")
public class ChecklistHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ChecklistHistoryId", sequenceName = "CHECKLIST_HISTORY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChecklistHistoryId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHECKLIST_HISTORY_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger checklistHistoryId;
    @NotNull
    @JoinColumn(name = "CHECKLIST_ID", referencedColumnName = "CHECKLIST_ID")
    @ManyToOne(optional = false)
    private Checklist checklist;
    @Column(name = "BODY_HTML")
    @Lob
    private String bodyHtml;
    @Size(max = 64)
    @Column(length = 64)
    private String author;
    @JoinColumn(name = "MODIFIED_BY", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MODIFIED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 1024)
    @Column(length = 1024)
    private String comments;

    public ChecklistHistory() {
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public Staff getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Staff modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigInteger getChecklistHistoryId() {
        return checklistHistoryId;
    }

    public void setChecklistHistoryId(BigInteger checklistHistoryId) {
        this.checklistHistoryId = checklistHistoryId;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.checklistHistoryId != null ? this.checklistHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChecklistHistory other = (ChecklistHistory) obj;
        return Objects.equals(this.checklistHistoryId, other.checklistHistoryId);
    }

    @Override
    public String toString() {
        return "ChecklistHistory{" + "checklistHistoryId=" + checklistHistoryId + '}';
    }
}
