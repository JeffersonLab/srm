<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Candidate Masks"/>
<t:masks-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/candidate-masks.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">         
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/candidate-masks.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true" resetButton="true" ribbon="false">
                <form class="filter-form" method="get" action="candidates">
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
                                                    <c:forEach items="${systemListFiltered}" var="system">
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
                                                    <c:out value="${region.name}"/></option>
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
                                        <label for="unpowered-select">Unpowered</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="unpowered-select" name="unpowered">
                                            <option value="">&nbsp;</option>
                                            <option value="Y"${param.unpowered eq 'Y' ? ' selected="selected"' : ''}>
                                                Yes
                                            </option>
                                            <option value="N"${param.unpowered eq 'N' ? ' selected="selected"' : ''}>
                                                No
                                            </option>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="component">Component</label>
                                    </div>
                                    <div class="li-value">
                                        <input id="component" class="component-autocomplete" data-application-id=""
                                               name="componentName" value="${fn:escapeXml(param.componentName)}"
                                               placeholder="name"/>
                                        (use % as wildcard)
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="status-select">Status</label>
                                        <div class="default-selection-panel">(<a id="candidate-link"
                                                                                 href="#">Candidates</a>)
                                        </div>
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
                    <input type="hidden" class="offset-input" name="offset" value="0"/>
                    <input type="hidden" name="qualified" value=""/>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <c:choose>
                <c:when test="${fn:length(componentList) == 0}">
                </c:when>
                <c:otherwise>
                    <c:if test="${editable}">
                        <s:editable-row-table-controls excludeAdd="${true}" excludeDelete="${true}"
                                                       excludeEdit="${true}" multiselect="${true}">
                            <button type="button" id="open-edit-exception-button" class="selected-row-action"
                                    disabled="disabled">Mask
                            </button>
                        </s:editable-row-table-controls>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:if test="${fn:length(componentList) > 0}">
                <table id="component-table" class="data-table outer-table stripped-table">
                    <thead>
                    <tr>
                        <th>Component</th>
                        <th>System</th>
                        <th>Region</th>
                        <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="inner-table-cell" colspan="6">
                            <div class="pane-decorator">
                                <div class="table-scroll-pane">
                                    <table class="data-table inner-table${editable ? ' multiselect-table editable-row-table' : ''} stripped-table">
                                        <tbody>
                                        <c:forEach items="${componentList}" var="component">
                                            <tr data-component-id="${component.componentId}"
                                                data-masked="${component.masked}"
                                                data-system-id="${component.system.systemId}"
                                                data-source="${component.dataSource}">
                                                <td>
                                                    <a title="Component Information" class="dialog-opener"
                                                       data-dialog-title="Component Information: ${fn:escapeXml(srm:formatComponent(component))}"
                                                       href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}">
                                                        <span class="component-name"
                                                              data-raw-name="${component.name}"><c:out
                                                                value="${srm:formatComponent(component)}"/></span>
                                                    </a>
                                                </td>
                                                <td>
                                                    <c:out value="${component.system.name}"/>
                                                </td>
                                                <td><c:out value="${component.region.name}"/></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <c:if test="${editable}">
                    <div id="multi-instructions">Hold down the control (Ctrl) or shift key when clicking to select
                        multiple. Hold down the Command (⌘) key on Mac.
                    </div>
                </c:if>
                <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                </button>
                <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                </button>
            </c:if>
            <div id="exception-dialog" class="dialog" title="Mask Component">
                <form id="mask-component-form" action="component-list" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Components:</span>
                            </div>
                            <div class="li-value">
                                <ul id="selected-row-list"></ul>
                                <span id="exception-dialog-component-count"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-component-masked-reason">Reason</label>
                            </div>
                            <div class="li-value">
                                <textarea id="mask-component-masked-reason" name="maskedReason"></textarea>
                                <span class="rows-differ-message">WARNING: One or more selected components have an existing reason that differs from the above</span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-expiration"
                                       title="How long do you want this component to be masked for?">Expiration</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="mask-expiration" class="date-time-field"
                                       placeholder="DD-MMM-YYYY hh:mm"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="mask-component-id" name="componentId"/>
                        <button type="button" id="mask-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
        </section>
    </jsp:body>
</t:masks-page>
