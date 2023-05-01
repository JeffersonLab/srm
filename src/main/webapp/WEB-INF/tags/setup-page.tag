<%@tag description="The Setup Page Template Tag" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="title" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<t:page title="${title}" category="Setup">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/setup.css"/>
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
                        <ul>
                            <li${'/setup/category-tree' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/setup/category-tree">Category Tree</a></li>
                            <li${'/setup/component-list' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/setup/component-list">Component List</a>
                            </li>
                            <li${'/setup/component-participation' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/setup/component-participation">Component
                                Part.</a></li>
                            <li${'/setup/subsystem-participation' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/setup/subsystem-participation">Subsystem
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
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:page>
