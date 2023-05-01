<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Overall Signoff Status"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.pie.min.js"></script>
                <script src="${cdnContextPath}/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.pie.min.js"></script>
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/overall-status.js"></script>
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
                <form id="filter-form" method="get" action="overall-status">
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
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <s:chart-widget>
                <table class="chart-legend">
                    <tbody>
                    <c:forEach items="${statusCountList}" var="count">
                        <tr>
                            <th>
                                <div class="color-box"></div>
                            </th>
                            <td class="legend-label"><c:out value="${count.status.name}"/></td>
                            <td>
                                <fmt:formatNumber value="${count.count}"/>
                            </td>
                            <td>
                                (<fmt:formatNumber pattern="##0.0"
                                                   value="${totalCount eq 0 ? 0 : (count.count / totalCount * 100)}"/>%)
                            </td>
                        </tr>
                    </c:forEach>
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
            <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                       value="${now}"/></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:reports-page>