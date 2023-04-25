<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Signoff Summary"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">   
        <style type="text/css">
            .chart-data-table tbody td {
                text-align: right;
            }

            .chart-data-table tbody td:first-child {
                text-align: left;
            }

            .chart-data-table tfoot th {
                text-align: right;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">      
        <script type="text/javascript">
            $(document).on("click", ".default-clear-panel", function () {
                $("#destination-select").select2("val", "");
                $("#category-select").val('').trigger('change');
                $("#system-select").val('');
                $("#region-select").val('');
                $("#start").val('');
                $("#end").val('');
                $("#date-range").val('custom').trigger('change');
                return false;
            });
            $(document).on("click", ".default-reset-panel", function () {
                $select = $("#destination-select");
                $select.select2("val", $select.attr("data-current-run-id-csv").split(","));
                $("#category-select").val('').trigger('change');
                $("#system-select").val('');
                $("#region-select").val('');
                $("#start").val('');
                $("#end").val('');
                $("#date-range").val('1day').trigger('change');
                return false;
            });
            $(document).on("change", "#category-select", function () {
                var categoryId = $(this).val();
                jlab.hco.filterSystemListByCategory(categoryId);
            });
            $(function () {
                $("#destination-select").select2({
                    width: 390
                });

                $("#destination-select").closest(".li-value").css("visibility", "visible");
            });
        </script>        
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
            <s:filter-flyout-widget clearButton="true" resetButton="true">
                <form id="filter-form" method="get" action="signoff-summary">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Time</legend>
                            <s:date-range datetime="${true}" sevenAmOffset="${true}"/>
                        </fieldset>
                        <fieldset>
                            <legend>Taxonomy</legend>
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
                                                <label for="system-select">Subsystem</label>
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
                    </div>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box">
                <c:out value="${selectionMessage}"/>
            </div>
            <div class="chart-wrap-backdrop">
                <c:choose>
                    <c:when test="${fn:length(activitySummaryList) > 0}">
                        <h3>Change Count</h3>
                        <table class="data-table stripped-table chart-data-table">
                            <thead>
                            <tr>
                                <th>Change</th>
                                <th>Count</th>
                            </tr>
                            </thead>
                            <tfoot>
                            <tr>
                                <th>Total:</th>
                                <th><fmt:formatNumber value="${grandTotal}"/></th>
                            </tr>
                            </tfoot>
                            <tbody>
                            <tr>
                                <td>Upgrade Ready</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="UPGRADE"/>
                                    <c:param name="statusId" value="1"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                        value="${total.upgradeReadyCount}"/></a></td>
                            </tr>
                            <tr>
                                <td>Upgrade Checked</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="UPGRADE"/>
                                    <c:param name="statusId" value="50"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                        value="${total.upgradeCheckedCount}"/></a></td>
                            </tr>
                            <tr>
                                <td>Downgrade Checked</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="DOWNGRADE"/>
                                    <c:param name="statusId" value="50"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                        value="${total.downgradeCheckedCount}"/></a></td>
                            </tr>
                            <tr>
                                <td>Downgrade Not Ready</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="DOWNGRADE"/>
                                    <c:param name="statusId" value="100"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                        value="${total.downgradeNotReadyCount}"/></a></td>
                            </tr>
                            <tr>
                                <td>Cascade</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="CASCADE"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber value="${total.cascadeCount}"/></a>
                                </td>
                            </tr>
                            <tr>
                                <td>Comment</td>
                                <c:url var="url" value="signoff-activity">
                                    <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                        <c:param name="destinationId" value="${destinationId}"/>
                                    </c:forEach>
                                    <c:param name="categoryId" value="${param.categoryId}"/>
                                    <c:param name="systemId" value="${param.systemId}"/>
                                    <c:param name="regionId" value="${param.regionId}"/>
                                    <c:param name="start" value="${param.start}"/>
                                    <c:param name="end" value="${param.end}"/>
                                    <c:param name="change" value="COMMENT"/>
                                    <c:param name="qualified" value=""/>
                                </c:url>
                                <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber value="${total.commentCount}"/></a>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <h3>Change Count By Group</h3>
                        <table class="data-table stripped-table chart-data-table">
                            <thead>
                            <tr>
                                <th rowspan="2">Group</th>
                                <th colspan="6">Change</th>
                            </tr>
                            <tr>
                                <th>Upgrade Ready</th>
                                <th>Upgrade Checked</th>
                                <th>Downgrade Checked</th>
                                <th>Downgrade Not Ready</th>
                                <th>Cascade</th>
                                <th>Comment</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${activitySummaryList}" var="activity">
                                <tr>
                                    <td><c:out value="${activity.groupName}"/></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="UPGRADE"/>
                                        <c:param name="statusId" value="1"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.upgradeReadyCount}"/></a></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="UPGRADE"/>
                                        <c:param name="statusId" value="50"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.upgradeCheckedCount}"/></a></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="DOWNGRADE"/>
                                        <c:param name="statusId" value="50"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.downgradeCheckedCount}"/></a></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="DOWNGRADE"/>
                                        <c:param name="statusId" value="100"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.downgradeNotReadyCount}"/></a></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="CASCADE"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.cascadeCount}"/></a></td>
                                    <c:url var="url" value="signoff-activity">
                                        <c:forEach items="${paramValues.destinationId}" var="destinationId">
                                            <c:param name="destinationId" value="${destinationId}"/>
                                        </c:forEach>
                                        <c:param name="categoryId" value="${param.categoryId}"/>
                                        <c:param name="systemId" value="${param.systemId}"/>
                                        <c:param name="regionId" value="${param.regionId}"/>
                                        <c:param name="start" value="${param.start}"/>
                                        <c:param name="end" value="${param.end}"/>
                                        <c:param name="groupId" value="${activity.groupId}"/>
                                        <c:param name="change" value="COMMENT"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <td><a href="${fn:escapeXml(url)}"><fmt:formatNumber
                                            value="${activity.commentCount}"/></a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        No changes to report
                    </c:otherwise>
                </c:choose>
            </div>
            <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                       value="${now}"/></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:reports-page>