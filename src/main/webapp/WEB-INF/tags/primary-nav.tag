<%@tag description="Primary Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<ul>
    <li${'/readiness' eq currentPath ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/readiness">Readiness</a></li>
    <li${'/signoff' eq currentPath ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/signoff">Signoff</a></li>
    <li${fn:startsWith(currentPath, '/masks') ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/masks/current">Masks</a></li>
    <li${fn:startsWith(currentPath, '/checklists') ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/checklists">Checklists</a></li>
    <li${'/links' eq currentPath ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/links">Links</a></li>
    <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/reports/overall-status">Reports</a></li>
    <c:if test="${pageContext.request.isUserInRole('srm-admin') || pageContext.request.isUserInRole('halead') || pageContext.request.isUserInRole('hblead') || pageContext.request.isUserInRole('hclead') || pageContext.request.isUserInRole('hdlead') || pageContext.request.isUserInRole('lerfadm') || pageContext.request.isUserInRole('cryoadm') || pageContext.request.isUserInRole('cmtfadm') || pageContext.request.isUserInRole('vtaadm')}">
        <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/settings">Setup</a></li>
    </c:if>
    <li${'/help' eq currentPath ? ' class="current-primary"' : ''}><a
            href="${pageContext.request.contextPath}/help">Help</a></li>
</ul>