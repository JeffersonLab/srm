<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><c:out value="${initParam.appShortName}"/>
        - ${history.checklist.groupResponsibility.group.name.concat(' ').concat(history.checklist.groupResponsibility.system.name)}
        Checklist</title>
    <link rel="shortcut icon"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
    <c:choose>
        <c:when test="${'CDN' eq resourceLocation}">
            <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jquery-ui/1.13.2/theme/smoothness/jquery-ui.min.css"/>
            <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/${env['CDN_VERSION']}/css/smoothness.min.css"/>
        </c:when>
        <c:otherwise><!-- LOCAL -->
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-ui-1.13.2/jquery-ui.min.css"/>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/smoothness.css"/>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/checklist.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/fullpage-checklist.css"/>
</head>
<body>
<c:if test="${initParam.notification ne null}">
    <div id="notification-bar"><c:out value="${initParam.notification}"/></div>
</c:if>
<div id="page">
    <div class="banner-breadbox no-bottom-border nav-links">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/readiness"><c:out value="${initParam.appShortName}"/></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/checklists">Checklists</a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/checklists?groupId=${history.checklist.groupResponsibility.group.groupId}"><c:out
                        value="${history.checklist.groupResponsibility.group.name}"/></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/checklists/history-list?checklistId=${history.checklist.checklistId}"><c:out
                        value="${history.checklist.groupResponsibility.system.name}"/> History</a>
            </li>
            <li>
                Printable Checklist (Revision ${fn:escapeXml(param.revision)})
            </li>
        </ul>
    </div>
    <div id="content">
        <div id="content-liner">
            <section>
                <h1><c:out
                        value="${history.checklist.groupResponsibility.group.name.concat(' ').concat(history.checklist.groupResponsibility.system.name)} Checklist"/></h1>
                <div class="dialog-content">
                    <div class="dialog-links dialog-only">
                        <a href="${pageContext.request.contextPath}/checklists/revision?checklistHistoryId=${history.checklistHistoryId}&amp;revision=${fn:escapeXml(param.revision)}">Printer
                            Friendly</a>
                    </div>
                    <div>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    Document ID:
                                </div>
                                <div class="li-value">
                                    <c:out value="${history.checklist.checklistId}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Group:
                                </div>
                                <div class="li-value">
                                    <span class="fullpage-only"><c:out
                                            value="${history.checklist.groupResponsibility.group.name}"/></span>
                                    <a title="Group Information" class="dialog-opener dialog-only"
                                       data-dialog-title="Group Information: ${fn:escapeXml(history.checklist.groupResponsibility.group.name)}"
                                       href="${pageContext.request.contextPath}/group-detail?groupId=${history.checklist.groupResponsibility.group.groupId}"><c:out
                                            value="${history.checklist.groupResponsibility.group.name}"/></a>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    System:
                                </div>
                                <div class="li-value">
                                    <span class="fullpage-only"><c:out
                                            value="${history.checklist.groupResponsibility.system.name}"/></span>
                                    <a title="System Information" class="dialog-opener dialog-only"
                                       data-dialog-title="System Information: ${fn:escapeXml(history.checklist.groupResponsibility.system.name)}"
                                       href="${pageContext.request.contextPath}/system-detail?systemId=${history.checklist.groupResponsibility.system.systemId}"><c:out
                                            value="${history.checklist.groupResponsibility.system.name}"/></a>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Author:
                                </div>
                                <div class="li-value">
                                    <c:out value="${history.author}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Submitted By:
                                </div>
                                <div class="li-value">
                                    <c:out value="${s:formatUsername(history.modifiedBy)}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Revision Number:
                                </div>
                                <div class="li-value">
                                    <c:out value="${param.revision}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Revision Date:
                                </div>
                                <div class="li-value">
                                    <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                    value="${history.modifiedDate}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Revision Comment:
                                </div>
                                <div class="li-value">
                                    <c:out value="${history.comments}"/>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <hr/>
                    <div class="checklist-body">
                        ${history.bodyHtml}
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<c:choose>
    <c:when test="${'CDN' eq resourceLocation}">
        <script src="${cdnContextPath}/jquery/3.6.1.min.js"></script>
        <script src="${cdnContextPath}/jquery-ui/1.13.2/jquery-ui.min.js"></script>
        <script src="${cdnContextPath}/jquery-plugins/maskedinput/jquery.maskedinput-1.3.1.min.js"></script>
        <script src="${cdnContextPath}/jquery-plugins/timepicker/jquery-ui-timepicker-1.5.0.min.js"></script>
        <script src="${cdnContextPath}/jlab-theme/smoothness/${env['CDN_VERSION']}/js/smoothness.min.js"></script>
    </c:when>
    <c:otherwise><!-- LOCAL -->
        <script src="${pageContext.request.contextPath}/resources/js/jquery-3.6.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/jquery-ui-1.13.2/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/jquery-plugins/maskedinput/1.3.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/jquery-plugins/timepicker/1.5.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/smoothness.js"></script>
    </c:otherwise>
</c:choose>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/srm.js"></script>
<script type="text/javascript">
    jlab.contextPath = '${pageContext.request.contextPath}';
</script>
</body>
</html>