<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HCO
        - ${history.checklist.groupResponsibility.group.name.concat(' ').concat(history.checklist.groupResponsibility.system.name)}
        Checklist</title>
    <link rel="shortcut icon"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css"
          href="${cdnContextPath}/jquery-ui/1.10.3/theme/smoothness/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/hco.css"/>
    <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/1.6/css/smoothness.min.css"/>
    <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jquery-plugins/select2/3.5.2/select2.css"/>
    <link rel="stylesheet" type="text/css"
          href="${cdnContextPath}/jquery-plugins/timepicker/jquery-ui-timepicker-1.3.1.css"/>
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
    <div class="nav-links">
        <ul class="breadcrumb">
            <li>
                <a href="${pageContext.request.contextPath}/readiness">HCO</a>
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
                Printable Checklist (Revision ${param.revision})
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
                        <a href="${pageContext.request.contextPath}/checklists/revision?checklistHistoryId=${history.checklistHistoryId}&amp;revision=${param.revision}">Printer
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
                                    <a title="Group Information" class="dialog-ready dialog-only"
                                       data-dialog-title="Group Information: ${fn:escapeXml(history.checklist.groupResponsibility.group.name)}"
                                       href="${pageContext.request.contextPath}/group-detail?groupId=${history.checklist.groupResponsibility.group.groupId}"><c:out
                                            value="${history.checklist.groupResponsibility.group.name}"/></a>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    Subsystem:
                                </div>
                                <div class="li-value">
                                    <span class="fullpage-only"><c:out
                                            value="${history.checklist.groupResponsibility.system.name}"/></span>
                                    <a title="Subsystem Information" class="dialog-ready dialog-only"
                                       data-dialog-title="Subsystem Information: ${fn:escapeXml(history.checklist.groupResponsibility.system.name)}"
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
<script type="text/javascript" src="${cdnContextPath}/jquery/1.10.2.min.js"></script>
<script type="text/javascript" src="${cdnContextPath}/jquery-ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript" src="${cdnContextPath}/jquery-plugins/select2/3.5.2/select2.min.js"></script>
<script type="text/javascript"
        src="${cdnContextPath}/jquery-plugins/maskedinput/jquery.maskedinput-1.3.1.min.js"></script>
<script type="text/javascript" src="${cdnContextPath}/jquery-plugins/timepicker/jquery-ui-timepicker-1.3.1.js"></script>
<script type="text/javascript" src="${cdnContextPath}/jlab-theme/smoothness/1.6/js/smoothness.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/hco.js"></script>
<script type="text/javascript">
    jlab.contextPath = '${pageContext.request.contextPath}';
</script>
</body>
</html>