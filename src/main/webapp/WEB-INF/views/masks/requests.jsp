<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Mask Requests"/>
<t:masks-page title="${title}">  
    <jsp:attribute name="stylesheets">     
        <style type="text/css">
            .fullscreen .chart-data-table th:first-child,
            .fullscreen .chart-data-table th:nth-child(2),
            .fullscreen .chart-data-table th:nth-child(5),
            .fullscreen .chart-data-table th:nth-child(6) {
                width: 130px;
            }

            #mask-request-reason {
                resize: none;
                width: 250px;
                height: 100px;
            }

            #editable-row-table-control-panel button {
                vertical-align: top;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">           
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/masking-request.js"></script>
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
                <form id="filter-form" method="get" action="requests">
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
                                        <label for="reason">Reason</label>
                                    </div>
                                    <div class="li-value">
                                        <input type="text" name="reason" id="reason" value="${param.reason}"/>
                                        (use % as wildcard)
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="status">Request Status</label>
                                    </div>
                                    <div class="li-value">
                                        <select name="status" id="status">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${srm:requestStatusList()}" var="record">
                                                <option value="${record.name()}"${param.status eq record.name() ? ' selected="selected"' : ''}>
                                                    <c:out value="${record.name()}"/></option>
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
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div class="chart-wrap-backdrop">
                <c:if test="${fn:length(recordList) > 0}">
                    <c:if test="${pageContext.request.isUserInRole('srm-admin')}">
                        <s:editable-row-table-controls excludeAdd="${true}" excludeEdit="${true}"
                                                       excludeDelete="${true}">
                            <button id="open-approval-dialog-button" class="selected-row-action" disabled="disabled">
                                Accept
                            </button>
                            <button id="deny-button" class="selected-row-action" disabled="disabled">Reject</button>
                        </s:editable-row-table-controls>
                    </c:if>
                    <table id="request-table"
                           class="data-table stripped-table constrained-table chart-data-table${pageContext.request.isUserInRole('srm-admin') ? ' uniselect-table editable-row-table' : ''}">
                        <thead>
                        <tr>
                            <th class="constrained-medium-column">Response</th>
                            <th class="constrained-medium-column">Component</th>
                            <th class="constrained-medium-column">System</th>
                            <th>Reason</th>
                            <th class="constrained-medium-column">Mask Expiration Date</th>
                            <th class="constrained-medium-column">Requested By</th>
                            <th class="constrained-medium-column">Requested Date <span title="Descending">&#9660;</span>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${recordList}" var="record">
                            <tr data-request-id="${record.maskingRequestId}">
                                <td><c:out value="${record.requestStatus}"/></td>
                                <td><a title="Component Information" class="dialog-ready"
                                       data-dialog-title="Component Information: ${fn:escapeXml(srm:formatComponent(record.component))}"
                                       href="${pageContext.request.contextPath}/reports/component/detail?componentId=${record.component.componentId}"><c:out
                                        value="${srm:formatComponent(record.component)}"/></a></td>
                                <td><c:out value="${record.component.system.name}"/></td>
                                <td><c:out value="${record.requestReason}"/></td>
                                <td><fmt:formatDate value="${record.maskExpirationDate}"
                                                    pattern="${s:getFriendlyDateTimePattern()}"/></td>
                                <td><c:out value="${s:formatUsername(record.requestBy)}"/></td>
                                <td><fmt:formatDate value="${record.requestDate}"
                                                    pattern="${s:getFriendlyDateTimePattern()}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="paginator-button-panel">
                        <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                                value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                        </button>
                        <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                                value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                        </button>
                    </div>
                </c:if>
            </div>
            <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                       value="${now}"/></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
        <div class="dialog" id="approval-dialog" title="Approve">
            <form>
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="mask-request-component">Component</label>
                        </div>
                        <div class="li-value">
                            <div id="mask-request-component"></div>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="mask-request-reason">Mask Reason</label>
                        </div>
                        <div class="li-value">
                            <textarea id="mask-request-reason" maxlength="512"></textarea>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="mask-request-expiration">Mask Expiration</label>
                        </div>
                        <div class="li-value">
                            <input id="mask-request-expiration" class="date-time-field" type="text"
                                   placeholder="DD-MMM-YYYY hh:mm"/>
                        </div>
                    </li>
                </ul>
            </form>
            <div class="dialog-button-panel">
                <button type="button" id="approve-save-button" class="dialog-submit-button">Save</button>
                <button type="button" class="dialog-close-button">Cancel</button>
            </div>
        </div>
    </jsp:body>
</t:masks-page>