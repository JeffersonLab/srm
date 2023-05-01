<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HCO - System ${system.name}</title>
    <link rel="shortcut icon"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/1.6/css/smoothness.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
</head>
<body>
<div id="page">
    <h1>System ${system.name}</h1>
    <dl class="dialog-content">
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
                            <td><a title="Group Information" class="dialog-ready"
                                   data-dialog-title="Group Information: ${fn:escapeXml(responsibility.group.name)}"
                                   href="${pageContext.request.contextPath}/group-detail?groupId=${responsibility.group.groupId}"><c:out
                                    value="${responsibility.group.name}"/></a></td>
                            <td>
                                <c:if test="${responsibility.checklist ne null and responsibility.published}">
                                    <a title="Checklist"
                                       data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                       class="dialog-ready" data-dialog-type="checklist"
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
                    <li><a title="Component Information" class="dialog-ready"
                           data-dialog-title="Component Information: ${fn:escapeXml(srm:formatComponent(component))}"
                           href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}"><c:out
                            value="${srm:formatComponent(component)}"/></a></li>
                </c:forEach>
            </ul>
        </dd>
    </dl>
</div>
</body>
</html>
