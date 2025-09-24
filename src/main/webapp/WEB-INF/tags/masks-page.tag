<%@tag description="The Setup Page Template Tag" pageEncoding="UTF-8" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="title" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<s:page title="Masks - ${title}">
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
                    <h2 id="left-column-header">Masks</h2>
                    <nav id="secondary-nav">
                        <ul>
                            <li${'/masks/current' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/masks/current">Current</a></li>
                            <li${'/masks/requests' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/masks/requests">Requests</a></li>
                            <li${'/masks/candidates' eq currentPath ? ' class="current-secondary"' : ''}><a
                                    href="${pageContext.request.contextPath}/masks/candidates">Candidates</a></li>
                        </ul>
                    </nav>
                </section>
            </div>
            <div id="right-column">
                <jsp:doBody/>
            </div>
        </div>
    </jsp:body>
</s:page>
