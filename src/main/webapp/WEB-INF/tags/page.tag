<%@tag description="The Site Page Template Tag" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@attribute name="title" %>
<%@attribute name="category" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<%@attribute name="secondaryNavigation" fragment="true" %>
<s:tabbed-page title="${title}" category="${category}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/hco.css"/>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/checklist.css"/>
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/hco.js"></script>
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="footnote">
        <span class="unpowered-symbol">★</span> Unpowered Component
    </jsp:attribute>
    <jsp:attribute name="headerExtra">
        <form action="${pageContext.request.contextPath}/reports/component/detail">
            <input id="quick-component" class="component-autocomplete quick-autocomplete" data-application-id="1"
                   type="text" name="name" placeholder="Component name"
                   value="${'/reports/component/detail' eq currentPath ? param.name : ''}"/>
            <button type="submit" title="Search">→</button>
        </form>
    </jsp:attribute>
    <jsp:attribute name="primaryNavigation">
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
                        <c:if test="${pageContext.request.isUserInRole('srm-admin') || pageContext.request.isUserInRole('halead') || pageContext.request.isUserInRole('hblead') || pageContext.request.isUserInRole('hclead') || pageContext.request.isUserInRole('hdlead') || pageContext.request.isUserInRole('lerfadm') || pageContext.request.isUserInRole('cryoadm')}">
                            <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}><a
                                    href="${pageContext.request.contextPath}/setup/category-tree">Setup</a></li>
                            </c:if>
                        <li${'/help' eq currentPath ? ' class="current-primary"' : ''}><a
                                href="${pageContext.request.contextPath}/help">Help</a></li>
                    </ul>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <jsp:invoke fragment="secondaryNavigation"/>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:tabbed-page>

