<%@tag description="Setup Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<ul>
    <li${'/setup/settings' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/setup/settings">Settings</a>
    </li>
    <li${'/setup/directory-cache' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/setup/directory-cache">Directory Cache</a>
    </li>
    <li${'/setup/category-tree' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/category-tree">Category Tree</a></li>
    <li${'/setup/component-list' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/component-list">Component List</a>
    </li>
    <li${'/setup/component-participation' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/component-participation">Component
        Part.</a></li>
    <li${'/setup/system-participation' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/system-participation">System
        Part.</a></li>
    <li${'/setup/group-responsibility' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/group-responsibility">Group Resp.</a>
    </li>
    <li${'/setup/bulk-signoff' eq currentPath ? ' class="current-secondary"' : ''}><a
            href="${pageContext.request.contextPath}/setup/bulk-signoff">Bulk Signoff</a></li>
    <c:if test="${pageContext.request.isUserInRole('srm-admin')}">
        <li${'/setup/region-list' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/region-list">Region List</a></li>
        <li${'/setup/group-list' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/group-list">Group List</a></li>
        <li${'/setup/destination-list' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/destination-list">Beam Dest.
            List</a></li>
        <li${'/setup/status-list' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/status-list">Status List</a></li>
        <li${'/setup/saved-signoff' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/saved-signoff">Saved Signoffs</a>
        </li>
        <li${'/setup/email' eq currentPath ? ' class="current-secondary"' : ''}><a
                href="${pageContext.request.contextPath}/setup/email">Email</a></li>
    </c:if>
</ul>