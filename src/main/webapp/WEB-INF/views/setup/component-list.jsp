<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Component List"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            #add-component-form {
                margin-top: 1em;
            }

            .masked-cell {
                text-align: center;
            }

            #component-table td {
                height: 1.5em;
            }

            #component-table td:first-child {
                word-break: break-word;
            }

            #mask-component-masked-reason {
                width: 400px;
                height: 200px;
                resize: none;
            }

            .flyout-panel {
                border: 1px solid black;
                width: 400px;
                height: 150px;
                position: absolute;
                right: -455px;
                top: -106px;
                z-index: 2;
                background-color: white;
                border-radius: 0.5em;
                box-shadow: 0.5em 0.5em 0.5em #979797;
                padding: 16px;

                text-align: left;
            }

            #flyouts {
                display: none;
            }

            .flyout-handle {
                position: relative;
            }

            .flyout-panel:after {
                content: '';
                width: 0;
                height: 0;
                border-top: 20px solid transparent;
                border-bottom: 20px solid transparent;
                border-right: 20px solid white;
                top: 50%;
                margin-top: -20px;
                position: absolute;
                left: -20px;
            }

            .flyout-panel:before {
                content: '';
                width: 0;
                height: 0;
                border-top: 21px solid transparent;
                border-bottom: 21px solid transparent;
                border-right: 21px solid black;
                top: 50%;
                margin-top: -21px;
                position: absolute;
                left: -21px;
            }

            .close-bubble {
                float: right;
                min-width: inherit;
            }

            .bubble-title {
                margin-bottom: 1em;
                font-weight: bold;
                font-size: 18px;
            }

            .bubble-body {
                font-weight: normal;
                font-size: 16px;
            }

            .second-button-panel {
                margin-top: 0.5em;
            }

            #bulk-component-name {
                width: 500px;
                height: 100px;
                resize: none;
            }

            .outer-table thead th:first-child {
                min-width: 110px;
                width: 110px;
                max-width: 110px;
            }

            .inner-table tbody td:first-child {
                min-width: 105px;
                width: 105px;
                max-width: 105px;
            }

            .outer-table thead th:nth-child(2),
            .inner-table tbody td:nth-child(2) {
                min-width: 150px;
                width: 150px;
                max-width: 150px;
                word-wrap: break-word;
            }

            .outer-table thead th:nth-child(3),
            .inner-table tbody td:nth-child(3) {
                min-width: 90px;
                width: 90px;
                max-width: 90px;
                word-wrap: break-word;
            }

            .outer-table thead th:nth-child(4),
            .inner-table tbody td:nth-child(4) {
                min-width: 180px;
                width: 180px;
                max-width: 180px;
                word-wrap: break-word;
            }

            .outer-table thead th:nth-child(5),
            .inner-table tbody td:nth-child(5) {
                width: 375px;
                word-wrap: break-word;
            }
        </style>        
    </jsp:attribute>
    <jsp:attribute name="scripts">         
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/component-list.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true">
                <form id="filter-form" method="get" action="component-list">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="destination-select">Beam Destination</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="destination-select" name="destinationId">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${destinationList}" var="destination">
                                                <option value="${destination.beamDestinationId}"${param.destinationId eq destination.beamDestinationId ? ' selected="selected"' : ''}>
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
                                        <label for="source-select">Source</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="source-select" name="source">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${hco:dataSourceList()}" var="source">
                                                <option value="${source}"${param.source eq source ? ' selected="selected"' : ''}>
                                                    <c:out value="${source}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="masked-select">Masked</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="masked-select" name="masked">
                                            <option value="">&nbsp;</option>
                                            <option value="Y"${param.masked eq 'Y' ? ' selected="selected"' : ''}>Yes
                                            </option>
                                            <option value="N"${param.masked eq 'N' ? ' selected="selected"' : ''}>No
                                            </option>
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
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/> <a href="#" class="flyout-link"
                                                                    data-flyout-type="all-components-flyout">‡</a></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <s:editable-row-table-controls excludeAdd="${true}" excludeEdit="${true}" excludeDelete="${true}">
                <button type="button" id="show-add-dialog-button" class="no-selection-row-action">Add</button>
                <button type="button" id="show-bulk-add-dialog-button" class="no-selection-row-action">Bulk Add</button>
                <button type="button" class="selected-row-action" id="show-rename-dialog-button" disabled="disabled">
                    Rename
                </button>
                <button type="button" class="selected-row-action" id="delete-component-button" disabled="disabled">
                    Remove
                </button>
            </s:editable-row-table-controls>
            <div class="second-button-panel">
                <button type="button" class="selected-row-action" id="open-mask-component-button" disabled="disabled">
                    (Un)mask
                </button>
                <button type="button" class="selected-row-action" id="open-unpowered-component-button"
                        disabled="disabled">(Un)power
                </button>
                <button type="button" class="selected-row-action" id="show-system-dialog-button" disabled="disabled">
                    Edit Subsystem
                </button>
                <button type="button" class="selected-row-action" id="show-region-dialog-button" disabled="disabled">
                    Edit Region
                </button>
                <button type="button" class="selected-row-action" id="open-source-component-button" disabled="disabled">
                    Edit Source
                </button>
                <button type="button" class="selected-row-action" id="open-alias-component-button" disabled="disabled">
                    Edit Alias
                </button>
            </div>
            <c:if test="${fn:length(componentList) > 0}">
                <table id="component-table" class="data-table outer-table stripped-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Subsystem / Region</th>
                        <th>Source</th>
                        <th>Masked Date / Expiration Date</th>
                        <th>Masked Reason</th>
                        <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="inner-table-cell" colspan="6">
                            <div class="pane-decorator">
                                <div class="table-scroll-pane">
                                    <table class="data-table inner-table stripped-table uniselect-table editable-row-table">
                                        <tbody>
                                        <c:forEach items="${componentList}" var="component">
                                            <fmt:formatDate value="${component.maskExpirationDate}"
                                                            pattern="${s:getFriendlyDateTimePattern()}" var="expiration"/>
                                            <tr data-component-id="${component.componentId}"
                                                data-masked="${component.masked}" data-expiration="${expiration}"
                                                data-system-id="${component.system.systemId}"
                                                data-region-id="${component.region.regionId}"
                                                data-source="${component.dataSource}"
                                                data-source-id="${component.dataSourceId}"
                                                data-unpowered="${component.unpowered}"
                                                data-alias="${component.nameAlias}">
                                                <td>
                                                    <a title="Component Information" class="dialog-ready"
                                                       data-dialog-title="Component Information: ${fn:escapeXml(hco:formatComponent(component))}"
                                                       href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}">
                                                        <span class="component-name"
                                                              data-raw-name="${component.name}"><c:out
                                                                value="${hco:formatComponent(component)}"/></span>
                                                    </a></td>
                                                <td><c:out value="${component.system.name}"/> / <c:out
                                                        value="${component.region.name}"/></td>
                                                <td><c:out value="${component.dataSource}"/></td>
                                                <td><fmt:formatDate value="${component.maskedDate}"
                                                                    pattern="${s:getFriendlyDateTimePattern()}"/> ${component.maskExpirationDate ne null ? ' / ' : ' '}
                                                    <fmt:formatDate value="${component.maskExpirationDate}"
                                                                    pattern="${s:getFriendlyDateTimePattern()}"/></td>
                                                <td class="masked-reason"><c:out
                                                        value="${component.maskedComment}"/></td>
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
                <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                </button>
                <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                </button>
            </c:if>
            <div id="add-dialog" class="dialog" title="Add New Component">
                <form id="add-component-form" action="component-list" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="new-component-name">Name</label>
                            </div>
                            <div class="li-value">
                                <input id="new-component-name" type="text" name="name" maxlength="128"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="category-select">Category</label>
                            </div>
                            <div class="li-value">
                                <select id="new-component-category-select" name="categoryId">
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
                                        <label for="new-component-system-select">Subsystem</label>
                                    </div>
                                    <div class="sub-value">
                                        <select id="new-component-system-select" name="newSystemId">
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
                                <label for="new-component-region-select">Region</label>
                            </div>
                            <div class="li-value">
                                <select id="new-component-region-select" name="newRegionId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${regionList}" var="region">
                                        <option value="${region.regionId}"${param.newRegionId eq region.regionId ? ' selected="selected"' : ''}>
                                            <c:out value="${region.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li style="display: none;">
                            <div class="li-key">
                                <label for="new-component-masked">Masked</label>
                            </div>
                            <div class="li-value">
                                <input id="new-component-masked" name="newComponentMasked" type="checkbox"/>
                            </div>
                        </li>
                        <li style="display: none;">
                            <div class="li-key">
                                <label for="new-component-masked-reason">Masked Reason</label>
                            </div>
                            <div class="li-value">
                                <textarea id="new-component-masked-reason" name="newComponentMaskedReason"></textarea>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <button type="button" id="add-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="bulk-add-dialog" class="dialog" title="Bulk Add New Components">
                <form id="bulk-add-component-form" action="component-list" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="bulk-component-name">Names</label>
                            </div>
                            <div class="li-value">
                                <span>(newline separated)</span>
                                <textarea id="bulk-component-name"></textarea>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="category-select">Category</label>
                            </div>
                            <div class="li-value">
                                <select id="bulk-component-category-select" name="categoryId">
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
                                        <label for="bulk-component-system-select">Subsystem</label>
                                    </div>
                                    <div class="sub-value">
                                        <select id="bulk-component-system-select" name="newSystemId">
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
                                <label for="bulk-component-region-select">Region</label>
                            </div>
                            <div class="li-value">
                                <select id="bulk-component-region-select" name="newRegionId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${regionList}" var="region">
                                        <option value="${region.regionId}"${param.newRegionId eq region.regionId ? ' selected="selected"' : ''}>
                                            <c:out value="${region.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <button type="button" id="bulk-add-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="mask-dialog" class="dialog" title="Mask/Unmask Component">
                <form id="mask-component-form" action="mask-component" method="post">
                    <div class="warning-banner"><span class="warning-banner-heading">WARNING:</span> Masking a component
                        will clear any Crew Chief Exception. Unmasking a component will reset all group signoffs to Not
                        Ready.
                    </div>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="mask-component-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-component-masked">Masked</label>
                            </div>
                            <div class="li-value">
                                <input type="checkbox" id="mask-component-masked" name="masked"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-component-masked-reason">Masked Reason</label>
                            </div>
                            <div class="li-value">
                                <textarea id="mask-component-masked-reason" name="maskedReason"
                                          maxlength="512"></textarea>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-component-mask-expiration">Mask Expiration Date</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="mask-component-mask-expiration" class="date-time-field"
                                       name="maskedExpiration" placeholder="DD-MMM-YYYY hh:mm"/>
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
            <div id="unpowered-dialog" class="dialog" title="Component Unpowered State">
                <form id="unpowered-component-form" action="edit-component-unpowered" method="post">
                    <div class="warning-banner"><span class="warning-banner-heading">WARNING:</span> Changing the
                        unpowered state of a component results in all associated signoffs being reset to Not Ready.
                    </div>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="unpowered-component-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="unpowered-component-state">Unpowered</label>
                            </div>
                            <div class="li-value">
                                <input type="checkbox" id="unpowered-component-state" name="unpowered"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="unpowered-component-id" name="component-id"/>
                        <button type="button" id="unpowered-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="source-dialog" class="dialog" title="Component Source">
                <form id="source-component-form" action="edit-component-source" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="source-component-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="source-component-source">Source</label>
                            </div>
                            <div class="li-value">
                                <select id="source-component-source" name="source">
                                    <option>INTERNAL</option>
                                    <option>CED</option>
                                    <option>LED</option>
                                    <option>UED</option>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="source-component-source-id">Source ID</label>
                            </div>
                            <div class="li-value">
                                <input type="number" min="0" id="source-component-source-id" name="source-id"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="source-component-id" name="component-id"/>
                        <button type="button" id="source-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="rename-dialog" class="dialog" title="Rename Component">
                <form id="rename-component-form" action="rename-component" method="post">
                    <div class="warning-banner"><span class="warning-banner-heading">WARNING:</span> This component is
                        linked to the CED/LED. The name should not be changed unless you want to contradict the CED/LED
                    </div>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Old Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="rename-component-old-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <span>New Name:</span>
                            </div>
                            <div class="li-value">
                                <input type="text" id="rename-component-name" name="name"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="rename-component-id" name="component-id"/>
                        <button type="button" id="rename-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="system-dialog" class="dialog" title="Edit Component System">
                <form id="edit-component-form" action="edit-component-system" method="post">
                    <div class="warning-banner"><span class="warning-banner-heading">WARNING:</span> This component is
                        linked to the CED/LED. The system should not be changed unless you want to contradict the
                        CED/LED
                    </div>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="system-component-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="system-component-system">Subsystem</label>
                            </div>
                            <div class="li-value">
                                <select id="system-component-system" name="systemId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${systemList}" var="system">
                                        <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                            <c:out value="${system.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="system-component-id" name="componentId"/>
                        <button type="button" id="system-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="region-dialog" class="dialog" title="Edit Component Region">
                <form id="edit-component-region-form" action="edit-component-region" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Name:</span>
                            </div>
                            <div class="li-value">
                                <span id="region-component-name"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="region-component-region">Region</label>
                            </div>
                            <div class="li-value">
                                <select id="region-component-region" name="systemId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${regionList}" var="region">
                                        <option value="${region.regionId}"${param.regionId eq region.regionId ? ' selected="selected"' : ''}>
                                            <c:out value="${region.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="region-component-id" name="componentId"/>
                        <button type="button" id="region-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="alias-dialog" class="dialog" title="Edit Component Alias">
                <form id="edit-component-alias-form" action="edit-component-alias" method="post">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <span>Alias:</span>
                            </div>
                            <div class="li-value">
                                <input type="text" id="edit-component-alias" name="alias"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <input type="hidden" id="alias-component-id" name="component-id"/>
                        <button type="button" id="edit-alias-button" class="dialog-submit-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="flyouts">
                <div class="all-components-flyout">
                    <div class="flyout-panel">
                        <button class="close-bubble">X</button>
                        <div class="bubble-title">Note</div>
                        <div class="bubble-body">
                            This list may contain some components which do not participate in HCO. Some components only
                            participate in Downtime or Problem Reporting (or none, during staging).
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </jsp:body>
</t:setup-page>
