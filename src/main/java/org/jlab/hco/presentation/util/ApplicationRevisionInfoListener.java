package org.jlab.hco.presentation.util;

import org.hibernate.envers.RevisionListener;
import org.jlab.hco.persistence.entity.ApplicationRevisionInfo;
import org.jlab.smoothness.presentation.filter.AuditContext;

/**
 * @author ryans
 */
public class ApplicationRevisionInfoListener implements RevisionListener {

    @Override
    public void newRevision(Object o) {
        ApplicationRevisionInfo revisionInfo = (ApplicationRevisionInfo) o;

        AuditContext context = AuditContext.getCurrentInstance();

        String ip = null;
        String username = null;

        if (context == null || "hcoadm".equals(context.getExtra("effectiveRole"))) {
            ip = "localhost";
            username = "hcoadm";
        } else {
            ip = context.getIp();
            username = context.getUsername();
        }

        revisionInfo.setAddress(ip);
        revisionInfo.setUsername(username);
    }

}
