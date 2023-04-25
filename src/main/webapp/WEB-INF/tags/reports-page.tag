<%@tag description="The Setup Page Template Tag" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="title" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<t:page title="Reports - ${title}">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:body>
        <div id="two-columns">
            <div id="left-column">
                <section>
                    <h2 id="left-column-header">Reports</h2>
                    <nav id="secondary-nav">
                        <ul>
                            <li${'/reports/overall-status' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/overall-status">Overall Status</a>
                            </li>
                            <li${'/reports/group-status' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/group-status">Group Status</a></li>
                            <li${'/reports/signoff-summary' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/signoff-summary">Signoff
                                Summary</a></li>
                            <li${'/reports/signoff' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/signoff">Signoff</a></li>
                            <li${fn:startsWith(currentPath, '/reports/component') ? ' class="current-secondary"' : ''}>
                                <a href="${pageContext.request.contextPath}/reports/component">Component</a></li>
                            <li${'/reports/group-responsibility' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/group-responsibility">Group
                                Resp.</a></li>
                            <li${'/reports/group-leader' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/group-leader">Group Leader</a></li>
                            <li${'/reports/signoff-activity' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/signoff-activity">Signoff
                                Activity</a></li>
                            <li${fn:startsWith(currentPath, '/reports/inventory-activity') ? ' class="current-secondary"' : ''}>
                                <a href="${pageContext.request.contextPath}/reports/inventory-activity">Inventory
                                    Activity</a></li>
                            <li${'/reports/all-activity' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/reports/all-activity">All Activity</a></li>
                        </ul>
                    </nav>
                </section>
            </div>
            <div id="right-column">
                <jsp:doBody/>
            </div>
        </div>
    </jsp:body>
</t:page>
