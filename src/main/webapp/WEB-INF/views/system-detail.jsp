<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="System Information: ${system.name}"/>
<s:loose-page title="${title}" category="" description="System Detail">
    <jsp:attribute name="stylesheets">
            <link rel="stylesheet" type="text/css"
                  href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
    <h2 class="hide-in-dialog">System ${system.name}</h2>
    <dl class="dialog-content">
        <dt>Description:</dt>
        <dd>
            <c:out value="${system.description eq null ? 'None' : system.description}"/>
        </dd>
        <dt>Group Responsibilities:</dt>
        <dd>
            <c:if test="${fn:length(system.groupResponsibilityList) > 0}">
                <table class="data-table stripped-table">
                    <thead>
                    <tr>
                        <th class="constrained-xx-small-column"></th>
                        <th>Group</th>
                        <th>Checklist</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="responsibility" items="${system.groupResponsibilityList}">
                        <tr>
                            <td class="right-aligned"><c:out value="${responsibility.weight}"/></td>
                            <td><a title="Group Information" class="dialog-opener"
                                   data-dialog-title="Group Information: ${fn:escapeXml(responsibility.group.name)}"
                                   href="${pageContext.request.contextPath}/group-detail?groupId=${responsibility.group.groupId}"><c:out
                                    value="${responsibility.group.name}"/></a></td>
                            <td>
                                <c:if test="${responsibility.checklist ne null and responsibility.published}">
                                    <a title="Checklist"
                                       data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                       class="dialog-opener" data-dialog-type="checklist"
                                       href="${pageContext.request.contextPath}/checklist?checklistId=${responsibility.checklist.checklistId}">Checklist</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </dd>
        <dt>Applications:</dt>
        <dd>
            <ul>
                <c:forEach var="application" items="${system.applicationList}">
                    <li><c:out value="${application.name}"/></li>
                </c:forEach>
            </ul>
        </dd>
        <dt>Components:</dt>
        <dd>
            <ul>
                <c:forEach var="component" items="${system.componentList}">
                    <li><a title="Component Information" class="dialog-opener"
                           data-dialog-title="Component Information: ${fn:escapeXml(srm:formatComponent(component))}"
                           href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}"><c:out
                            value="${srm:formatComponent(component)}"/></a></li>
                </c:forEach>
            </ul>
        </dd>
    </dl>
</section>
    </jsp:body>
</s:loose-page>