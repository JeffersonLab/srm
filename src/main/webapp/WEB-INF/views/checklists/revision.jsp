<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<c:set var="title" value="${history.checklist.groupResponsibility.group.name} ${history.checklist.groupResponsibility.system.name} Checklist"/>
<s:loose-page title="${title}" category="" description="Checklist">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/checklist.css"/>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/fullpage-checklist.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/srm.js"></script>
        <script type="text/javascript">
            jlab.contextPath = '${pageContext.request.contextPath}';
        </script>
    </jsp:attribute>
    <jsp:body>
        <c:if test="${'Y' ne param.partial}">
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
        </c:if>
            <section>
                <h1 class="hide-in-dialog"><c:out
                        value="${history.checklist.groupResponsibility.group.name.concat(' ').concat(history.checklist.groupResponsibility.system.name)} Checklist"/></h1>
                <div class="dialog-content">
                    <div class="dialog-links">
                        <c:if test="${'Y' eq param.partial}">
                        <a href="${pageContext.request.contextPath}/checklists/revision?checklistHistoryId=${history.checklistHistoryId}&amp;revision=${fn:escapeXml(param.revision)}">Printer
                            Friendly</a>
                        </c:if>
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
<c:if test="${'Y' ne param.partial}">
</div>
</div>
</c:if>
</jsp:body>
</s:loose-page>