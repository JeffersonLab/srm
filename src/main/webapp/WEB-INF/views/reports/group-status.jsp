<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group Signoff Status"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">       
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/group-status.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.stack.min.js"></script>
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.stack.min.js"></script>
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/js/jquery.flot.stackpercent.js"></script>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/js/jquery.flot.tooltip.js"></script>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/js/jquery.sparkline.min.js"></script>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/group-status.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <div id="report-page-actions">
                <button id="fullscreen-button">Full Screen</button>
                <div id="export-widget">
                    <button id="export-menu-button">Export</button>
                    <ul id="export-menu">
                        <li id="image-menu-item">Image</li>
                        <li id="print-menu-item">Print</li>
                    </ul>
                </div>
            </div>
            <s:filter-flyout-widget clearButton="true">
                <form class="filter-form" method="get" action="group-status">
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
                                                <label for="system-select">system</label>
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
                            </ul>
                        </fieldset>
                        <fieldset>
                            <legend>Display</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="chart">Chart</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="chart" name="chart">
                                            <option value="bar"${param.chart eq 'bar' ? ' selected="selected"' : ''}>
                                                Bar
                                            </option>
                                            <option value="table"${param.chart eq 'table' ? ' selected="selected"' : ''}>
                                                Table
                                            </option>
                                        </select>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box">
                <c:out value="${selectionMessage}"/>
            </div>
            <div>
                <c:if test="${param.chart eq 'bar'}">
                    <s:chart-widget>
                        <table class="chart-legend">
                            <tbody>
                            <tr>
                                <th>
                                    <div class="color-box" style="background-color: green;"></div>
                                </th>
                                <td class="legend-label">Ready</td>
                            </tr>
                            <tr>
                                <th>
                                    <div class="color-box" style="background-color: yellow;"></div>
                                </th>
                                <td class="legend-label">Checked</td>
                            </tr>
                            <tr>
                                <th>
                                    <div class="color-box" style="background-color: red;"></div>
                                </th>
                                <td class="legend-label">Not Ready</td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="chart-footnote">
                            <c:if test="${!empty footnoteList}">
                                <ul>
                                    <c:forEach items="${footnoteList}" var="note">
                                        <c:choose>
                                            <c:when test="${'ArrayList' eq note.value.getClass().getSimpleName()}">
                                                <li><b><c:out value="${note.key}"/>: </b>
                                                    <ul>
                                                        <c:forEach items="${note.value}" var="v">
                                                            <li><c:out value="${v.name}"/></li>
                                                        </c:forEach>
                                                    </ul>
                                                </li>
                                            </c:when>
                                            <c:otherwise>
                                                <li><b><c:out value="${note.key}"/>: </b><c:out value="${note.value}"/></li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </ul>
                            </c:if>
                        </div>
                    </s:chart-widget>
                </c:if>
                <div class="chart-wrap-backdrop"${param.chart eq 'bar' ? ' style="display: none;"' : ''}>
                    <c:choose>
                        <c:when test="${fn:length(groupStatusList) > 0}">
                            <table id="bar-chart-data-table" class="data-table stripped-table chart-data-table">
                                <thead>
                                <tr>
                                    <th rowspan="2">Group</th>
                                    <th rowspan="2">Signoff Total</th>
                                    <th colspan="3">Signoff Count</th>
                                    <th colspan="3">Signoff Percent</th>
                                    <th rowspan="2"></th>
                                </tr>
                                <tr>
                                    <th>Ready</th>
                                    <th>Checked</th>
                                    <th>Not Ready</th>
                                    <th>Ready</th>
                                    <th>Checked</th>
                                    <th>Not Ready</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${groupStatusList}" var="status">
                                    <tr>
                                        <td>
                                            <c:out value="${status.name}"/>
                                        </td>
                                        <td>
                                            <c:url var="url" value="signoff">
                                                <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                                    <c:param name="destinationId" value="${destinationId}"/>
                                                </c:forEach>
                                                <c:param name="categoryId" value="${param.categoryId}"/>
                                                <c:param name="systemId" value="${param.systemId}"/>
                                                <c:param name="regionId" value="${param.regionId}"/>
                                                <c:param name="groupId" value="${status.groupId}"/>
                                                <c:param name="masked" value="N"/>
                                                <c:param name="qualified" value=""/>
                                            </c:url>
                                            <a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                                    value="${status.totalCount}"/></a>
                                        </td>
                                        <td>
                                            <c:url var="url" value="signoff">
                                                <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                                    <c:param name="destinationId" value="${destinationId}"/>
                                                </c:forEach>
                                                <c:param name="categoryId" value="${param.categoryId}"/>
                                                <c:param name="systemId" value="${param.systemId}"/>
                                                <c:param name="regionId" value="${param.regionId}"/>
                                                <c:param name="groupId" value="${status.groupId}"/>
                                                <c:param name="masked" value="N"/>
                                                <c:param name="statusId" value="1"/>
                                                <c:param name="qualified" value=""/>
                                            </c:url>
                                            <a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                                    value="${status.ready}"/></a>
                                        </td>
                                        <td>
                                            <c:url var="url" value="signoff">
                                                <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                                    <c:param name="destinationId" value="${destinationId}"/>
                                                </c:forEach>
                                                <c:param name="categoryId" value="${param.categoryId}"/>
                                                <c:param name="systemId" value="${param.systemId}"/>
                                                <c:param name="regionId" value="${param.regionId}"/>
                                                <c:param name="groupId" value="${status.groupId}"/>
                                                <c:param name="masked" value="N"/>
                                                <c:param name="statusId" value="50"/>
                                                <c:param name="qualified" value=""/>
                                            </c:url>
                                            <a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                                    value="${status.checked}"/></a>
                                        </td>
                                        <td>
                                            <c:url var="url" value="signoff">
                                                <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                                    <c:param name="destinationId" value="${destinationId}"/>
                                                </c:forEach>
                                                <c:param name="categoryId" value="${param.categoryId}"/>
                                                <c:param name="systemId" value="${param.systemId}"/>
                                                <c:param name="regionId" value="${param.regionId}"/>
                                                <c:param name="groupId" value="${status.groupId}"/>
                                                <c:param name="masked" value="N"/>
                                                <c:param name="statusId" value="100"/>
                                                <c:param name="qualified" value=""/>
                                            </c:url>
                                            <a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                                    value="${status.notReady}"/></a>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${status.readyPercent}" pattern="##0.0"/>%
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${status.checkedPercent}" pattern="##0.0"/>%
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${status.notReadyPercent}" pattern="##0.0"/>%
                                        </td>
                                        <td>
                                            <span class="sparkline"
                                                  data-graph="${status.ready}, ${status.checked}, ${status.notReady}"></span>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div>No groups signoffs match the selected criteria</div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                           value="${now}"/></div>
            </div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:reports-page>