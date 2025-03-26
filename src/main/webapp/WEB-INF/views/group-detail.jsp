<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Group Information: ${group.name}"/>
<t:loose-page title="${title}" category="" description="Responsible Group Detail">
    <jsp:attribute name="stylesheets">
        <c:choose>
            <c:when test="${'NONE' eq resourceLocation}">
            </c:when>
            <c:when test="${'CDN' eq resourceLocation}">
                <link rel="stylesheet" type="text/css" href="//${env[initParam.appSpecificEnvPrefix.concat('_CDN_SERVER')]}/jquery-ui/1.14.1/theme/smoothness/jquery-ui.min.css"/>
                <link rel="stylesheet" type="text/css" href="//${env[initParam.appSpecificEnvPrefix.concat('_CDN_SERVER')]}/jlab-theme/smoothness/${env[initParam.appSpecificEnvPrefix.concat('_SMOOTHNESS_VERSION')]}/css/smoothness.min.css"/>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-ui-1.14.1/jquery-ui.min.css"/>
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/smoothness.css"/>
            </c:otherwise>
        </c:choose>
            <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
<section>
    <h2 class="hide-in-dialog"><c:out value="${title}"/></h2>
    <div class="dialog-content">
        <dl>
            <dt>Description:</dt>
            <dd>
                <c:out value="${group.description}"/>
            </dd>
            <dt>Leaders:</dt>
            <dd>
                <ul>
                    <c:forEach var="leader" items="${group.leaders}">
                        <li><c:out value="${s:formatUser(leader)}"/></li>
                    </c:forEach>
                </ul>
            </dd>
            <dt>Group Responsibilities:</dt>
            <dd>
                <c:if test="${fn:length(group.groupResponsibilityList) > 0}">
                    <table class="data-table stripped-table">
                        <thead>
                        <tr>
                            <th>System</th>
                            <th>Order</th>
                            <th>Checklist</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="responsibility" items="${group.groupResponsibilityList}">
                            <tr>
                                <td><a title="System Information" class="dialog-opener"
                                       data-dialog-title="System Information: ${fn:escapeXml(responsibility.system.name)}"
                                       href="${pageContext.request.contextPath}/system-detail?systemId=${responsibility.system.systemId}"><c:out
                                        value="${responsibility.system.name}"/></a></td>
                                <td class="right-aligned"><c:out value="${responsibility.weight}"/></td>
                                <td>
                                    <c:if test="${responsibility.checklist ne null and responsibility.published}">
                                        <a title="Checklist"
                                           data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                           class="dialog-opener"
                                           data-dialog-title="${fn:escapeXml(responsibility.group.name)} ${fn:escapeXml(responsibility.system.name)} Checklist"
                                           href="${pageContext.request.contextPath}/checklist?checklistId=${responsibility.checklist.checklistId}">Checklist</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </dd>
        </dl>
    </div>
</section>
    </jsp:body>
</t:loose-page>
