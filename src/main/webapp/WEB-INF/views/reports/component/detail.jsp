<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Component -  Detail: ${component.name}"/>
<t:reports-page title="${title}">
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            .breadbox {
                padding: 10px;
                position: relative;
            }

            .breadcrumb:before {
                content: " ";
                position: absolute;
                width: 10px;
                background-color: #f0f0f0;
                display: block;
                left: -5px;
                top: 0;
                bottom: 0;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li>
                    <a href="${pageContext.request.contextPath}/reports/component">Component Search</a>
                </li>
                <li>
                    <h2 id="page-header-title"><c:out value="${component.name}"/></h2>
                </li>
            </ul>
        </div>
        <section>
            <c:choose>
                <c:when test="${component ne null}">
                    <div class="dialog-content">
                        <c:if test="${editable}">
                            <c:url var="url" value="/setup/component-list">
                                <c:param name="componentName" value="${component.name}"/>
                                <c:param name="qualified" value=""/>
                            </c:url>
                            <div style="float: right;"><a href="${url}">Modify</a></div>
                        </c:if>
                        <dl>
                            <c:if test="${component.nameAlias ne null}">
                                <dt>Alias:</dt>
                                <dd><c:out value="${component.nameAlias}"/></dd>
                            </c:if>
                            <dt>Group Signoff:</dt>
                            <dd>
                                <c:if test="${fn:length(component.system.groupResponsibilityList) > 0}">
                                    <table class="data-table stripped-table constrained-table dialog-fixed-table">
                                        <thead>
                                        <tr>
                                            <th class="constrained-xx-small-column"></th>
                                            <th style="width: 175px;">Status / Name</th>
                                            <th style="width: 175px">Date / By</th>
                                            <th>Comments</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="responsibility"
                                                   items="${component.system.groupResponsibilityList}">
                                            <tr style="vertical-align: top;">
                                                <td class="right-aligned"><c:out value="${responsibility.weight}"/></td>
                                                <td>
                                                    <div style="margin-bottom: 1em;">
                                                        <span class="small-icon ${hco:getStatusClass(signoffMap[responsibility.group].status)}-icon"
                                                              title="${signoffMap[responsibility.group].status.name}"></span>
                                                        <c:out value="${responsibility.group.name}"/>
                                                    </div>
                                                    <c:url value="/signoff" var="signoffUrl">
                                                        <c:param name="systemId"
                                                                 value="${responsibility.system.systemId}"/>
                                                        <c:param name="componentName" value="${component.name}"/>
                                                        <c:param name="groupId"
                                                                 value="${responsibility.group.groupId}"/>
                                                        <c:param name="qualified" value=""/>
                                                    </c:url>
                                                    <c:url value="/reports/signoff-activity" var="activityUrl">
                                                        <c:param name="systemId"
                                                                 value="${responsibility.system.systemId}"/>
                                                        <c:param name="componentName" value="${component.name}"/>
                                                        <c:param name="groupId"
                                                                 value="${responsibility.group.groupId}"/>
                                                        <c:param name="dialog" value="true"/>
                                                        <c:param name="qualified" value=""/>
                                                    </c:url>
                                                    <div><a title="Group Signoff" href="${signoffUrl}">Signoff</a> |
                                                        <a class="dialog-ready"
                                                           data-dialog-title="${fn:escapeXml(component.name)}: ${responsibility.group.name} Signoff Activity"
                                                           title="Signoff Activity Report" href="${activityUrl}">Activity</a>
                                                    </div>
                                                </td>
                                                <td><fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                    value="${signoffMap[responsibility.group].modifiedDate}"/>
                                                    <div style="margin-top: 1em;"><c:out
                                                            value="${s:formatUsername(signoffMap[responsibility.group].modifiedBy)}"/></div>
                                                </td>
                                                <td><c:out value="${signoffMap[responsibility.group].comments}"/></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                            </dd>
                            <dt>Group Responsibilities:</dt>
                            <dd>
                                <c:if test="${fn:length(component.system.groupResponsibilityList) > 0}">
                                    <table class="data-table stripped-table">
                                        <thead>
                                        <tr>
                                            <th class="constrained-xx-small-column"></th>
                                            <th>Name</th>
                                            <th>Checklist</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="responsibility"
                                                   items="${component.system.groupResponsibilityList}">
                                            <tr>
                                                <td class="right-aligned"><c:out value="${responsibility.weight}"/></td>
                                                <td><a title="Group Information" class="dialog-ready"
                                                       data-dialog-title="Group Information: ${fn:escapeXml(responsibility.group.name)}"
                                                       href="${pageContext.request.contextPath}/group-detail?groupId=${responsibility.group.groupId}"><c:out
                                                        value="${responsibility.group.name}"/></a></td>
                                                <td>
                                                    <c:if test="${responsibility.checklist ne null and responsibility.published}">
                                                        <a title="Checklist"
                                                           data-dialog-title="${fn:escapeXml(responsibility.group.name.concat(' ').concat(responsibility.system.name))} Checklist"
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
                            <dt>Category:</dt>
                            <dd>
                                <c:forEach items="${categoryBranch}" var="category" varStatus="status">
                                    <c:forEach begin="0" end="${status.index}">&nbsp;&nbsp;&nbsp;&nbsp;</c:forEach>
                                    <c:out value="${category.name}"/>
                                    <br/>
                                </c:forEach>
                            </dd>
                            <dt>Subsystem:</dt>
                            <dd><a title="Subsystem Information" class="dialog-ready"
                                   data-dialog-title="Subsystem Information: ${fn:escapeXml(component.system.name)}"
                                   href="${pageContext.request.contextPath}/system-detail?systemId=${component.system.systemId}"><c:out
                                    value="${component.system.name}"/></a></dd>
                            <dt>Region:</dt>
                            <dd>
                                <c:out value="${component.region.name}"/>
                                <c:if test="${not empty component.region.alias}">
                                    (<c:out value="${component.region.alias}"/>)
                                </c:if>
                            </dd>
                            <dt>Beam Destinations:</dt>
                            <dd>
                                <ul>
                                    <c:forEach var="destination" items="${component.beamDestinationList}">
                                        <li><c:out value="${destination.name}"/></li>
                                    </c:forEach>
                                </ul>
                            </dd>
                            <dt>Unpowered:</dt>
                            <dd><c:out value="${component.unpowered ? 'Yes' : 'No'}"/></dd>
                            <dt>Masked:</dt>
                            <dd><c:out value="${component.masked ? 'Yes' : 'No'}"/></dd>
                            <c:if test="${component.masked}">
                                <dt>Masked Date:</dt>
                                <dd><fmt:formatDate value="${component.maskedDate}"
                                                    pattern="${s:getFriendlyDateTimePattern()}"/></dd>
                                <dt>Masked By:</dt>
                                <dd><c:out value="${s:formatUsername(component.maskedBy)}"/></dd>
                                <dt>Masked Type:</dt>
                                <dd><c:out value="${hco:getStatusById(component.maskTypeId).maskType}"/></dd>
                                <dt>Masked Reason:</dt>
                                <dd><c:out value="${component.maskedComment}"/></dd>
                                <dt>Mask Expiration Date:</dt>
                                <dd><fmt:formatDate value="${component.maskExpirationDate}"
                                                    pattern="${s:getFriendlyDateTimePattern()}"/></dd>
                            </c:if>
                            <dt>Source:</dt>
                            <c:choose>
                                <c:when test="${component.dataSource eq 'INTERNAL'}">
                                    <dd><c:out value="${component.dataSource}"/></dd>
                                </c:when>
                                <c:otherwise>
                                    <dd><a target="_blank"
                                           href="http://${component.dataSource.hostname}/elem/${hco:truncateAndUrlEncodeCedName(component.name)}"><c:out
                                            value="${component.dataSource}"/></a></dd>
                                </c:otherwise>
                            </c:choose>
                            <dt>Added Date:</dt>
                            <dd><fmt:formatDate value="${component.addedDate}"
                                                pattern="${s:getFriendlyDateTimePattern()}"/> (<a
                                    href="${pageContext.request.contextPath}/reports/inventory-activity/component-audit?componentId=${component.componentId}">History</a>)
                            </dd>
                        </dl>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="message-box">No component found for given ID / Name</div>
                </c:otherwise>
            </c:choose>
        </section>
    </jsp:body>
</t:reports-page>

