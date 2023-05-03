<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<fmt:setLocale value="en_US" scope="session"/>
<c:set var="pathPrefix" value="${pageContext.request.contextPath}"/>
<c:if test="${param.email eq 'Y'}">
    <c:set var="pathPrefix" value="https://ace.jlab.org/srm"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>SRM - Activity Report</title>
    <link rel="shortcut icon" href="${pathPrefix}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/1.6/css/smoothness.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pathPrefix}/resources/v${initParam.releaseNumber}/css/srm.css"/>
    <style type="text/css">
        body {
            background-color: white;
        }

        #activity-table {
            border-collapse: collapse;
        }

        #activity-table td {
            vertical-align: top;
            word-wrap: break-word;
            border-left: 0;
            border-right: 0;
            padding-left: 0;
            padding-right: 0;
            padding-top: 0;
        }

        #activity-table td:first-child {
            width: 200px;
        }

        #activity-table td:nth-child(2) {
            width: 250px;
        }

        .cell-header {
            margin-top: 0;
            padding: 0.5em;
            background-color: navy;
            color: white;
            font-weight: bold;
            min-height: 1.5em;
            white-space: nowrap;
        }

        .cell-footer {
            white-space: nowrap;
        }

        tr:nth-child(2n) .cell-header {
            color: #f5f5f5;
        }

        tr td:nth-child(2) .cell-header {
            overflow: hidden;
        }

        .cell-subfield:first-child {
            margin-top: 0;
        }

        .cell-subfield {
            padding: 0.5em;
        }

        .cell-sublabel {
            font-size: 0.8em;
            color: #595959;
        }
    </style>
</head>
<body>
<h2 style="margin-left: 16px;">SRM - Activity Report</h2>
<div style="margin-left: 16px;" id="email-content" class="content-section dialog-content">
    <h3>New Outstanding Signoffs</h3>
    <h4>${fn:escapeXml(dateRange)}</h4>
    <c:if test="${fn:length(activityList) > 0}">
        <table id="activity-table" class="data-table stripped-table constrained-table">
            <thead style="display: none;">
            <tr>
                <th class="constrained-large-column">Modified</th>
                <th class="constrained-large-column">Activity</th>
                <th class="constrained-large-column">Taxonomy</th>
                <th>Comments</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${activityList}" var="activity">
                <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}" value="${activity.modifiedDate}"
                                var="formattedModifiedDate"/>
                <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}" value="${activity.modifiedDatePlusOneMinute}"
                                var="formattedModifiedDatePlusOneMinute"/>
                <tr>
                    <td>
                        <div class="cell-header">
                            <c:out value="${formattedModifiedDate}"/>
                        </div>
                        <div class="cell-subfield"><c:out
                                value="${s:formatUsername(activity.username)}"/></div>
                    </td>
                    <td>
                        <div class="cell-header">
                            <c:out value="${activity.changeType}"/>
                            <c:if test="${activity.componentCount > 1}">
                                (<c:out value="${activity.componentCount}"/>)
                            </c:if>
                        </div>
                        <c:if test="${activity.statusName ne null}">
                            <div class="cell-subfield">
                                <div class="cell-sublabel">Group:</div>
                                <c:out value="${activity.groupName}"/>
                            </div>
                            <div class="cell-subfield">
                                <div class="cell-sublabel">Status:</div>
                                <c:out value="${activity.statusName}"/>
                            </div>
                        </c:if>
                    </td>
                    <td>
                        <div class="cell-header">
                            <c:out value="${srm:formatFakeComponent(activity.componentName, activity.unpowered)}"/>
                        </div>
                        <c:if test="${activity.systemName ne null}">
                            <div class="cell-subfield">
                                <div class="cell-sublabel">System:</div>
                                <c:out value="${activity.systemName}"/>
                            </div>
                        </c:if>
                    </td>
                    <td>
                        <div class="cell-header">&nbsp;</div>
                        <div class="cell-subfield"><c:out value="${activity.comments}"/></div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <p>
            <c:if test="${hasMoreActivity}">
                <span style="font-weight: bold;">Not all results shown.</span>
            </c:if>
            <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}" var="startStr" value="${start}"/>
            <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}" var="endStr" value="${end}"/>
            <c:url var="url" context="/" value="/reports/all-activity">
                <c:param name="start" value="${startStr}"/>
                <c:param name="end" value="${endStr}"/>
                <c:param name="componentStatusId" value="50"/>
                <c:param name="componentStatusId" value="100"/>
                <c:param name="statusId" value="50"/>
                <c:param name="statusId" value="100"/>
                <c:param name="change" value="UPGRADE"/>
                <c:param name="change" value="DOWNGRADE"/>
                <c:param name="change" value="CASCADE"/>
                <c:param name="change" value="COMMENT"/>
                <c:param name="change" value="COMPONENT_SYSTEM"/>
                <c:param name="qualified" value=""/>
            </c:url>
            See <a href="${pathPrefix}${url}">full report</a>.
            <c:if test="${param.email eq 'Y'}">
                <a style="float: right;" href="${pathPrefix}/readiness">Readiness Home</a>
            </c:if>
        </p>
    </c:if>
    <div id="doNotSend" class="error-message">
        <c:out value="${willNotBeSentMessage}"/>
    </div>
</div>
</body>
</html>