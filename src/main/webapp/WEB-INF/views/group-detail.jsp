<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HCO - Group ${group.name}</title>
    <link rel="shortcut icon"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/1.6/css/smoothness.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
</head>
<body>
<div id="page">
    <h1>Group ${group.name}</h1>
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
                                <td><a title="System Information" class="dialog-ready"
                                       data-dialog-title="System Information: ${fn:escapeXml(responsibility.system.name)}"
                                       href="${pageContext.request.contextPath}/system-detail?systemId=${responsibility.system.systemId}"><c:out
                                        value="${responsibility.system.name}"/></a></td>
                                <td class="right-aligned"><c:out value="${responsibility.weight}"/></td>
                                <td>
                                    <c:if test="${responsibility.checklist ne null and responsibility.published}">
                                        <a title="Checklist"
                                           data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                           class="dialog-ready"
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
</div>
</body>
</html>
