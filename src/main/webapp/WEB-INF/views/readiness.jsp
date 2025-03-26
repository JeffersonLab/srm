<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<c:set var="title" value="Readiness"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jquery-plugins/jstree/3.3.8/themes/classic/style.min.css"/>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jstree/3.3.8/themes/classic/style.min.css"/>
            </c:otherwise>
        </c:choose>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/readiness.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <script src="${cdnContextPath}/jquery-plugins/jstree/3.3.8/jstree.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script src="${pageContext.request.contextPath}/resources/jstree/3.3.8/jstree.min.js"></script>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/readiness.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <div id="report-page-actions">
                <div id="export-widget">
                    <button id="export-menu-button">Export</button>
                    <ul id="export-menu">
                        <li id="system-excel-menu-item">System Excel</li>
                    </ul>
                </div>
            </div>
            <s:filter-flyout-widget ribbon="true" clearButton="true">
                <form class="filter-form" action="readiness" method="get">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="destination-select">Beam Destination</label>
                                        <div class="default-selection-panel">(<a id="current-run-link" href="#">Current
                                            Run</a>)
                                        </div>
                                    </div>
                                    <div class="li-value" style="visibility: hidden;">
                                        <select id="destination-select" multiple="multiple" name="destinationId"
                                                style="min-height: 34px;" data-current-run-id-csv="${targetCsv}">
                                            <c:forEach items="${destinationList}" var="destination">
                                                <option value="${destination.beamDestinationId}"${s:inArray(paramValues.destinationId, destination.beamDestinationId.toString()) ? ' selected="selected"' : ''}>
                                                    <c:out value="${destination.name}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="category-select">Category</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="category-select" name="categoryId">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${categoryRoot.children}" var="child">
                                                <t:hierarchical-select-option node="${child}" level="0"
                                                                              parameterName="categoryId"/>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <span class="sub-level-symbol">↳</span>
                                    </div>
                                    <div class="li-value">
                                        <div class="sub-table">
                                            <div class="sub-key">
                                                <label for="system-select">System</label>
                                            </div>
                                            <div class="sub-value">
                                                <select id="system-select" name="systemId">
                                                    <option value="">&nbsp;</option>
                                                    <c:forEach items="${systemList}" var="system">
                                                        <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                                            <c:out value="${system.name}"/></option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="region-select">Region</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="region-select" name="regionId">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${regionList}" var="region">
                                                <option value="${region.regionId}"${param.regionId eq region.regionId ? ' selected="selected"' : ''}>
                                                    <c:out value="${region.name} (${region.alias})"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="group-select">Group</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="group-select" name="groupId">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${groupList}" var="group">
                                                <option value="${group.groupId}"${param.groupId eq group.groupId ? ' selected="selected"' : ''}>
                                                    <c:out value="${group.name}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="status-select">Status</label>
                                    </div>
                                    <div class="li-value" style="visibility: hidden;">
                                        <select id="status-select" multiple="multiple" name="statusId"
                                                style="min-height: 34px;">
                                            <c:forEach items="${statusList}" var="status">
                                                <option value="${status.statusId}"${s:inArray(paramValues.statusId, status.statusId.toString()) ? ' selected="selected"' : ''}>
                                                    <c:out value="${status.name}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" name="qualified" value=""/>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box">
                <c:out value="${selectionMessage}"/>
                <c:if test="${maskedCount > 0}">
                    <c:url var="maskedUrl" value="/masks/current">
                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                            <c:param name="destinationId" value="${destinationId}"/>
                        </c:forEach>
                        <c:param name="categoryId" value="${param.categoryId}"/>
                        <c:param name="systemId" value="${param.systemId}"/>
                        <c:param name="regionId" value="${param.regionId}"/>
                        <c:param name="groupId" value="${param.groupId}"/>
                        <c:forEach items="${paramValues.statusId}" var="statusId">
                            <c:param name="statusId" value="${statusId}"/>
                        </c:forEach>
                        <c:param name="qualified" value=""/>
                    </c:url>
                    (<a title="Masked Component Report" href="${fn:escapeXml(maskedUrl)}"><fmt:formatNumber
                        value="${maskedCount}"/> Masked</a>)
                </c:if>
            </div>
            <button id="expand-all-button" data-count="${componentCount}"
                    title="${componentCount <= 100 ? 'Expand all categories and systems' : 'Filter to no more than 100 components first...'}" ${componentCount <= 100 ? '' : 'disabled="disabled"'}>
                Expand All
            </button>
            <button id="collapse-all-button">Collapse All</button>
            <div id="tree-widget">
                <div id="tree-nodes">
                    <div class="tree status-tree">
                    </div>
                </div>
                <div id="tree-keys">
                    <fieldset>
                        <legend>Status Key</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <span class="small-icon notapplicable-icon"></span>
                                </div>
                                <div class="li-value">
                                    Not Applicable
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                </div>
                                <div class="li-value">
                                    <hr style="margin: 0;"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon ready-icon"></span>
                                </div>
                                <div class="li-value">
                                    Ready
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon checked-icon"></span>
                                </div>
                                <div class="li-value">
                                    Checked
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon not-ready-icon"></span>
                                </div>
                                <div class="li-value">
                                    Not Ready
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                </div>
                                <div class="li-value">
                                    <hr style="margin: 0;"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon masked-icon"></span>
                                </div>
                                <div class="li-value">
                                    Masked
                                    <div>(Director)</div>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon exception-icon"></span>
                                </div>
                                <div class="li-value">
                                    Masked
                                    <div>(Crew Chief)</div>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon tragedy-icon"></span>
                                </div>
                                <div class="li-value">
                                    Masked
                                    <div>(Administrator)</div>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <fieldset>
                        <legend>Node Key</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <span class="small-icon CATEGORY"></span>
                                </div>
                                <div class="li-value">
                                    Category
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon SYSTEM"></span>
                                </div>
                                <div class="li-value">
                                    System
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon COMPONENT"></span>
                                </div>
                                <div class="li-value">
                                    Component
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon GROUP"></span>
                                </div>
                                <div class="li-value">
                                    Group
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                </div>
                <div id="activity-feed">
                    <h3>Recent Activity</h3>
                    <c:choose>
                        <c:when test="${signoffActivityList.size() > 0}">
                            <table id="activity-table">
                                <tbody>
                                <c:forEach items="${signoffActivityList}" var="signoff">
                                    <tr class="top-row">
                                        <fmt:formatDate pattern="dd-MMM-yyyy HH:mm" value="${signoff.modifiedDate}"
                                                        var="fullDate"/>
                                        <td>
                                            <span class="date-header" title="${fn:escapeXml(fullDate)}"><fmt:formatDate
                                                    pattern="dd MMM HH:mm" value="${signoff.modifiedDate}"/></span>
                                            - <span class="activity-user"
                                                    title="${s:formatUsername(signoff.modifiedBy)}"><c:out
                                                value="${signoff.modifiedBy}"/></span>
                                            - <c:out value="${srm:formatChangeType(signoff.changeType)}"/> - <c:out
                                                value="${signoff.systemName}"/>
                                            - <c:out
                                                value="${srm:formatFakeComponent(signoff.firstComponentName, signoff.firstUnpowered)}"/>
                                            <c:if test="${signoff.componentCount > 1}">
                                                + ${signoff.componentCount - 1} more
                                            </c:if>
                                        </td>
                                    </tr>
                                    <tr class="middle-row">
                                        <td><c:out value="${signoff.comments}"/></td>

                                    </tr>
                                    <tr class="bottom-row">
                                        <td><c:out value="${signoff.groupName}"/> - <c:out
                                                value="${signoff.statusName}"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                            <c:url var="url" value="/reports/signoff-activity">
                                <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                    <c:param name="destinationId" value="${destinationId}"/>
                                </c:forEach>
                                <c:param name="categoryId" value="${param.categoryId}"/>
                                <c:param name="systemId" value="${param.systemId}"/>
                                <c:param name="regionId" value="${param.regionId}"/>
                                <c:param name="groupId" value="${param.groupId}"/>
                                <c:forEach items="${paramValues.statusId}" var="statusId">
                                    <c:param name="statusId" value="${statusId}"/>
                                </c:forEach>
                                <c:param name="qualified" value=""/>
                            </c:url>
                            <a id="more-link" href="${url}">More -&gt;</a>
                        </c:when>
                        <c:otherwise>
                            <div class="message-box">None</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div id="group-dialog" class="dialog" title="Edit Group Signoff">
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">Group:</div>
                            <div class="li-value group-name"></div>
                        </li>
                        <li>
                            <div class="li-key">Component:</div>
                            <div class="li-value component-name"></div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="update-status-select">Status</label>
                            </div>
                            <div class="li-value">
                                <select id="update-status-select">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${signoffStatusList}" var="status">
                                        <option value="${status.statusId}"><c:out value="${status.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="comment">Comment</label>
                            </div>
                            <div class="li-value">
                                <textarea id="comment"></textarea>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="needs-attention">Needs Attention</label>
                                <div>(Create OPS-PR)</div>
                            </div>
                            <div class="li-value">
                                <input id="needs-attention" type="checkbox">
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <button id="updateButton" class="dialog-submit ajax-button" type="button">Save</button>
                        <button class="dialog-close-button" type="button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="ops-pr-dialog" class="dialog" title="OPS-PR Created">
                <div><a id="ops-pr-link" href="#">OPS-PR</a></div>
                <div class="dialog-button-panel">
                    <button id="ops-pr-ok-button" type="button">OK</button>
                </div>
            </div>
            <form id="excel-form" method="get" action="${pageContext.request.contextPath}/categories-systems.xlsx">
                <button id="excel" type="submit" style="display: none;">Excel</button>
            </form>
        </section>
    </jsp:body>
</t:page>
