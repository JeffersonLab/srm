<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Saved Signoffs"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/saved-signoffs.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/saved-signoff.js"></script>
        <c:if test="${param.pop eq 'true'}">
            <script type="text/javascript">
                $(function () {
                    $("#open-add-dialog-button").click();
                    $("#system-select").val(${param.systemId});
                    $("#group-select").val(${param.groupId});
                    $("#region-select").val(${param.regionId});
                    $("#filter-status-select").val(${param.statusId});
                    $("#component").val('${param.component}');
                });
            </script>
        </c:if>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true">
                <form id="filter-form" method="get" action="saved-signoff">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="type-select">Saved Signoff Type</label>
                                </div>
                                <div class="li-value">
                                    <select id="type-select" name="typeId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${typeList}" var="type">
                                            <option value="${type.savedSignoffTypeId}"${param.typeId eq type.savedSignoffTypeId ? ' selected="selected"' : ''}>
                                                <c:out value="${type.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="system-select">Subsystem</label>
                                </div>
                                <div class="li-value">
                                    <select id="system-select" name="systemId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${systemList}" var="system">
                                            <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                                <c:out value="${system.name}"/></option>
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
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box">
                <c:out value="${selectionMessage}"/>
            </div>
            <div id="subheader-controls">
                <div id="subheader-cell1">
                    <s:editable-row-table-controls excludeAdd="${true}" excludeDelete="${true}"
                                                   excludeEdit="${true}" multiselect="${true}">
                        <button type="button" id="signoff-button" title="Ron Lauze memorial signoff button" class="selected-row-action" disabled="disabled">Signoff</button>
                        | <button type="button" id="open-add-dialog-button" class="no-selection-row-action">Add</button>
                        <button type="button" id="remove-row-button" class="selected-row-action" disabled="disabled">Remove</button>
                        <button type="button" id="select-all-button">Select All</button>
                    </s:editable-row-table-controls>
                </div>
                <div id="subheader-cell2">
                    <fieldset>
                        <legend>Load-Time Filter</legend>
                        <label>Max Last Modified Date:</label>
                        <input id="max-modified" class="date-time-field"
                               placeholder="${s:getFriendlyDateTimePlaceholder()}" type="text"/>
                        <input type="button" value="Save" style="display: none;"/>
                    </fieldset>
                </div>
            </div>
            <c:if test="${fn:length(signoffList) > 0}">
                <table id="super-table" class="data-table outer-table stripped-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Comments</th>
                        <th></th>
                        <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="inner-table-cell" colspan="6">
                            <div class="pane-decorator">
                                <div class="table-scroll-pane">
                                    <table class="data-table inner-table multiselect-table editable-row-table stripped-table">
                                        <tbody>
                    <c:forEach items="${signoffList}" var="signoff">
                        <tr data-saved-signoff-id="${signoff.savedSignoffId}"
                            data-system-id="${signoff.system.systemId}"
                            data-group-id="${signoff.group.groupId}"
                            data-region-id="${signoff.region.regionId}"
                            data-status-id="${signoff.filterStatus.statusId}"
                            data-component-name="${signoff.filterComponentName}">
                            <td><c:out value="${signoff.signoffName}"/></td>
                            <td><c:out value="${signoff.type.name}"/></td>
                            <td><c:out value="${signoff.signoffStatus.name}"/></td>
                            <td><c:out value="${signoff.signoffComments}"/></td>
                            <td><button type="button" class="preview-button">🔗</button></td>
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
                <div id="multi-instructions">Hold down the control (Ctrl) or shift key when clicking to select
                    multiple. Hold down the Command (⌘) key on Mac.
                </div>
                <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                </button>
                <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                </button>
            </c:if>
            <p><b>Tip:</b> If you want to downgrade only those signoffs which are "Ready" to "Checked" use the "Status"
                filter of "Ready" to avoid inadvertently upgrading those that are "Not Ready".</p>
            <p><b>Tip:</b> Set the Max Last Modified Date to the beginning of the SAD. This will prevent downgrading
                signoffs which were made after the start of the SAD. This is a load-time filter and is applied with all
                saved signoffs when using the "Load Selected" feature.</p>
            <p><b>Tip:</b> Be conscientious of cascade rules when downgrading.</p>
            <p><b>Note:</b> The filter options that can be saved are simplified (subset of signoff screen).</p>
            <div id="add-dialog" class="dialog" title="Add Saved Signoff">
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label class="required-field" for="add-type-select">Signoff Type</label>
                            </div>
                            <div class="li-value">
                                <select id="add-type-select" name="addTypeId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${typeList}" var="type">
                                        <option value="${type.savedSignoffTypeId}"><c:out
                                                value="${type.name}"/></option>
                                    </c:forEach>
                                </select>
                                <span class="li-value-option">
                                    <input id="autofill-checkbox" type="checkbox" checked="checked"/>
                                    <span>Autofill name and comments</span>
                                </span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label class="required-field" for="signoff-name">Signoff Name</label>
                            </div>
                            <div class="li-value">
                                <input id="signoff-name" type="text" maxlength="128" disabled="disabled"/>
                            </div>
                        </li>
                    </ul>
                    <fieldset>
                        <legend>Signoff</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="signoff-status-select">Status</label>
                                </div>
                                <div class="li-value">
                                    <select id="signoff-status-select" name="signoffStatusId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${statusList}" var="status">
                                            <c:if test="${status.name ne 'Ready'}">
                                                <option value="${status.statusId}"><c:out
                                                        value="${status.name}"/></option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                                    <span class="li-value-option">
                                        <input id="downgrade-only-checkbox" type="checkbox" checked="checked"/>
                                        <span>Downgrade Only</span>
                                    </span>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="comments">Comments</label>
                                </div>
                                <div class="li-value">
                                    <textarea id="comments" disabled="disabled"></textarea>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="add-group-select">Group</label>
                                </div>
                                <div class="li-value">
                                    <select id="add-group-select" name="addGroupId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${groupList}" var="group">
                                            <option value="${group.groupId}"><c:out value="${group.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="add-system-select">Subsystem</label>
                                </div>
                                <div class="li-value">
                                    <select id="add-system-select" name="addSystemId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${systemList}" var="system">
                                            <option value="${system.systemId}"><c:out value="${system.name}"/></option>
                                        </c:forEach>
                                    </select>
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
                                            <option value="${region.regionId}"><c:out value="${region.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="filter-status-select">Status</label>
                                </div>
                                <div class="li-value">
                                    <select id="filter-status-select" name="signoffStatusId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${statusList}" var="status">
                                            <option value="${status.statusId}"><c:out value="${status.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="component">Component Name</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="component" class="component-autocomplete"
                                           data-application-id="1" name="componentName"/> (use % as wildcard)
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <div class="dialog-button-panel">
                        <button type="button" id="add-button" class="dialog-submit ajax-button">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="edit-dialog" class="dialog" title="Edit Saved Downgrade">
                <form>
                    <div class="dialog-button-panel">
                        <button type="button" id="edit-button" class="dialog-submit">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                    <input type="hidden" id="downgradeId" name="downgradeId" value=""/>
                </form>
            </div>
        </section>
    </jsp:body>
</t:setup-page>