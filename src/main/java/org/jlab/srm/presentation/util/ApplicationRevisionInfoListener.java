package org.jlab.srm.presentation.util;

import org.hibernate.envers.RevisionListener;
import org.jlab.smoothness.presentation.filter.AuditContext;
import org.jlab.srm.persistence.entity.ApplicationRevisionInfo;

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

    if (context == null || "srm-admin".equals(context.getExtra("effectiveRole"))) {
      ip = "localhost";
      username = "srm-admin";
    } else {
      ip = context.getIp();
      username = context.getUsername();
    }

    revisionInfo.setAddress(ip);
    revisionInfo.setUsername(username);
  }
}
