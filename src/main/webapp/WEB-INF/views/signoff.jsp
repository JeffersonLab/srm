<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Signoff"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/signoff.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/signoff.js"></script>
        <c:if test="${param.pop eq 'true' and pageContext.request.userPrincipal ne null}">
            <script type="text/javascript">
                $(function () {
                    var $th = $(".select-column-icon").closest("th");
                    var index = $th.index() + 1; //rowspan hides first column
                    var count = index + 1; //nth-child starts counting at 1, not 0
                    jlab.hco.selectColumn($("#signoff-table"), count);
                    jlab.hco.updateSignoffCount();
                    $("#open-edit-dialog-button").click();
                });
            </script>
        </c:if>
        <c:if test="${param.subsystemFirst eq 'Y'}">
            <script type="text/javascript">
                jlab.hco.subsystemFirst = true;
            </script>            
        </c:if>            
    </jsp:attribute>
    <jsp:body>
        <section>
            <form id="filter-form" method="get" action="signoff">
                <div id="start-with-div"><input id="start-with-checkbox" name="subsystemFirst" class="change-submit"
                                                value="Y"${param.subsystemFirst eq 'Y' ? ' checked="checked"' : ''}
                                                type="checkbox"/><label for="start-with-checkbox">Start with
                    Category/System</label></div>
                <c:choose>
                    <c:when test="${componentList eq null}">
                        <h2 id="page-header-title"><c:out value="${title}"/></h2> <span
                            style="font-weight: bold;">(<span class="required-field"></span> required)</span> <span
                            class="default-clear-panel">(<a href="#">Clear</a>)</span>
                        <jsp:include page="/WEB-INF/includes/signoff-form.jsp"/>
                    </c:when>
                    <c:otherwise>
                        <s:filter-flyout-widget requiredMessage="true" ribbon="true" clearButton="true">
                            <jsp:include page="/WEB-INF/includes/signoff-form.jsp"/>
                        </s:filter-flyout-widget>
                        <h2 id="page-header-title"><c:out value="${title}"/></h2>
                    </c:otherwise>
                </c:choose>
            </form>
            <c:choose>
                <c:when test="${componentList eq null}">
                    <div class="message-box">Select a group and subsystem to continue</div>
                </c:when>
                <c:when test="${fn:length(selectedSystem.groupResponsibilityList) == 0}">
                    <div class="message-box">No group responsibilities found in Subsystem
                        &quot;${fn:escapeXml(selectedSystem.name)}&quot;
                    </div>
                </c:when>
                <c:when test="${not pageContext.request.isUserInRole('hcoadm') and groupResponsibilityForSelected.checklistRequired and not groupResponsibilityForSelected.published}">
                    <div class="message-box">A checklist for Group &quot;${fn:escapeXml(selectedGroup.name)}&quot; and
                        Subsystem &quot;${fn:escapeXml(selectedSystem.name)}&quot; must be published before signoffs can
                        be made
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="message-box">
                        <c:out value="${selectionMessage}"/>
                    </div>
                    <c:if test="${fn:length(componentList) > 0}">
                        <c:if test="${pageContext.request.userPrincipal ne null}">
                            <div id="selection-action-panel">
                                <button type="button" id="open-edit-dialog-button" disabled="disabled">Edit Signoff
                                </button>
                                <button type="button" id="open-request-dialog-button" disabled="disabled">Request
                                    Masking
                                </button>
                                <button type="button" id="unselect-button" disabled="disabled">Unselect All</button>
                                <span>(Selected <span id="selected-count">0</span> Signoffs)</span>
                            </div>
                        </c:if>
                        <div id="table-option-panel">
                            <input form="filter-form" id="show-comments-checkbox" name="showComments" type="checkbox"
                                   value="Y"${param.showComments eq 'Y' ? ' checked="checked"' : ''}/>
                            <label for="show-comments-checkbox">Show Comments</label>
                        </div>
                        <div id="table-wrapper">
                            <table id="signoff-table"
                                   class="data-table ${pageContext.request.userPrincipal ne null ? 'editable' : ''}">
                                <thead>
                                <tr>
                                    <th id="component-header" rowspan="2">Component</th>
                                    <th colspan="${fn:length(selectedSystem.groupResponsibilityList)}">Group Signoff
                                    </th>
                                    <th id="scrollbar-header" rowspan="2"><span class="small-icon" title="Expand Table"
                                                                                id="expand-icon"></span></th>
                                </tr>
                                <tr>
                                    <c:forEach items="${selectedSystem.groupResponsibilityList}" var="responsibility"
                                               varStatus="status">
                                        <th class="group-header${selectedGroup eq responsibility.group ? ' selected-group-header' : ' unselected-group-header'}"
                                            data-group-id="${responsibility.group.groupId}">
                                            <div class="group-header-content">
                                                <c:out value="${status.count}."/>
                                                <a title="Group Information" class="dialog-ready"
                                                   data-dialog-title="Group Information: ${fn:escapeXml(responsibility.group.name)}"
                                                   href="group-detail?groupId=${responsibility.group.groupId}"><c:out
                                                        value="${responsibility.group.name}"/></a>
                                                <c:if test="${(pageContext.request.userPrincipal ne null) and (selectedGroup eq responsibility.group)}">
                                                    <span title="Select Column"
                                                          class="small-icon select-column-icon"></span>
                                                </c:if>
                                            </div>
                                            <div class="halo-holder">
                                                <div class="halo"></div>
                                            </div>
                                        </th>
                                    </c:forEach>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td id="nested-table-cell"
                                        colspan="${2 + fn:length(selectedSystem.groupResponsibilityList)}">
                                        <div id="pane-decorator">
                                            <div id="table-scroll-pane">
                                                <table id="nested-table" class="data-table stripped-table">
                                                    <tbody>
                                                    <c:forEach items="${componentList}" var="component">
                                                        <tr>
                                                            <th data-component-id="${component.componentId}"
                                                                class="row-header">
                                                                <c:choose>
                                                                    <c:when test="${component.masked and component.maskTypeId eq 150}">
                                                                        <span class="small-icon masked-icon"
                                                                              title="Masked (Director)"></span>
                                                                    </c:when>
                                                                    <c:when test="${component.masked and component.maskTypeId eq 200}">
                                                                        <span class="small-icon exception-icon"
                                                                              title="Masked (Crew Chief)"></span>
                                                                    </c:when>
                                                                    <c:when test="${component.masked and component.maskTypeId eq 250}">
                                                                        <span class="small-icon tragedy-icon"
                                                                              title="Masked (Administrator)"></span>
                                                                    </c:when>
                                                                </c:choose>
                                                                <a title="Component Information" class="dialog-ready"
                                                                   data-dialog-title="Component Information: ${fn:escapeXml(hco:formatComponent(component))}"
                                                                   href="reports/component/detail?componentId=${component.componentId}">
                                                                    <c:out value="${hco:formatComponent(component)}"/>
                                                                </a>
                                                            </th>
                                                            <c:forEach items="${selectedSystem.groupResponsibilityList}"
                                                                       var="responsibility">
                                                                <c:set var="signoff"
                                                                       value="${signoffMap[component.componentId.toString().concat(';').concat(responsibility.group.groupId.toString())]}"/>
                                                                <c:set var="status"
                                                                       value="${signoff.status ne null ? signoff.status : hco:getDefaultStatus()}"/>
                                                                <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                                value="${signoff.modifiedDate}"
                                                                                var="modifiedDate"/>
                                                                <td${selectedGroup eq responsibility.group ? ' class="selected-group-cell"' : ''}>
                                                                    <div class="cell-liner">
                                                                        <c:choose>
                                                                            <c:when test="${param.showComments eq 'Y'}">
                                                                                <div class="signoff-cell">
                                                                                    <c:url var="url"
                                                                                           value="reports/signoff-activity">
                                                                                        <c:param name="systemId"
                                                                                                 value="${component.system.systemId}"/>
                                                                                        <c:param name="componentName"
                                                                                                 value="${component.name}"/>
                                                                                        <c:param name="groupId"
                                                                                                 value="${responsibility.group.groupId}"/>
                                                                                        <c:param name="qualified"
                                                                                                 value=""/>
                                                                                        <c:param name="dialog"
                                                                                                 value="true"/>
                                                                                    </c:url>
                                                                                    <span class="small-icon ${hco:getStatusClass(status)}-icon"></span>
                                                                                    <a href="${fn:escapeXml(url)}"
                                                                                       data-dialog-title="${fn:escapeXml(hco:formatComponent(component))}: ${responsibility.group.name} Signoff History"
                                                                                       title="Click for signoff history"
                                                                                       class="small-icon dialog-ready comment-icon"></a>
                                                                                </div>
                                                                                <div class="signoff-cell">
                                                                                    <c:out value="${signoff.comments}"/>
                                                                                </div>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span data-change="${hco:formatChangeType(signoff.changeType)}"
                                                                                      data-comment="${fn:escapeXml(signoff.comments)}"
                                                                                      data-modified-by="${hco:formatStaff(signoff.modifiedBy)}"
                                                                                      data-modified-date="${modifiedDate}"
                                                                                      class="small-icon tooltip-icon ${hco:getStatusClass(status)}-icon"></span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </td>
                                                            </c:forEach>
                                                            <th class="cell-buffer"></th>
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
                        </div>
                        <c:if test="${pageContext.request.userPrincipal ne null}">
                            <a id="signoff-options-link" href="#">Downgrade Options...</a>
                            <div>Hold down the control (Ctrl) key when clicking to select multiple. Hold down the
                                Command (⌘) key on Mac.
                            </div>
                        </c:if>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <div id="signoff-dialog" class="dialog" title="Edit Group Signoff">
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">Group:</div>
                            <div class="li-value selected-group"></div>
                        </li>
                        <li>
                            <div class="li-key">Components:</div>
                            <div class="li-value">
                                <ul class="selected-component-list">

                                </ul>
                                <span class="edit-dialog-component-count"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="update-status-select">Status</label>
                            </div>
                            <div class="li-value">
                                <select id="update-status-select">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${statusList}" var="status">
                                        <option value="${status.statusId}"${param.signoffStatus eq status.name ? ' selected="selected"' : ''}>
                                            <c:out value="${status.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="comment">Comment</label>
                            </div>
                            <div class="li-value">
                                <textarea id="comment"><c:out value="${param.comments}"/></textarea>
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
            <div id="request-dialog" class="dialog" title="Request Masking">
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">Components:</div>
                            <div class="li-value">
                                <ul class="selected-component-list">

                                </ul>
                                <span class="edit-dialog-component-count"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="request-comment">Reason</label>
                            </div>
                            <div class="li-value">
                                <textarea id="request-comment" maxlength="512"></textarea>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="mask-expiration"
                                       title="How long do you want this component to be masked for?">Mask
                                    Expiration</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="mask-expiration" class="date-time-field"
                                       placeholder="DD-MMM-YYYY hh:mm"/>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <button id="request-mask-button" class="dialog-submit ajax-button" type="button">Save</button>
                        <button class="dialog-close-button" type="button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="signoff-options-dialog" class="dialog" title="Downgrade Signoff Options">
                <p style="margin-top: 0;">When downgrading a signoff any group signoffs which follow (to the right of
                    the selected group) are subject to cascade rules.
                    The behavior of cascades is based on the status of the signoffs which follow.</p>
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="readyCascade">Ready Cascades To</label>
                        </div>
                        <div class="li-value">
                            <select form="filter-form" id="readyCascade" name="readyCascade">
                                <option value="">&nbsp;</option>
                                <option value="50"${readyCascade eq '50' ? ' selected="selected"' : ''}>Checked</option>
                                <option value="100"${readyCascade eq '100' ? ' selected="selected"' : ''}>Not Ready
                                </option>
                            </select>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="checkedCascade">Checked Cascades To</label>
                        </div>
                        <div class="li-value">
                            <select form="filter-form" id="checkedCascade" name="checkedCascade">
                                <option value="">&nbsp;</option>
                                <option value="100"${checkedCascade eq '100' ? ' selected="selected"' : ''}>Not Ready
                                </option>
                            </select>
                        </div>
                    </li>
                </ul>
                <div class="dialog-button-panel">
                    <button form="filter-form" type="submit">Apply</button>
                    <button class="dialog-close-button" type="button">Cancel</button>
                </div>
            </div>
            <div id="flyouts">
                <div class="all-subsystems-flyout">
                    <div class="flyout-panel">
                        <button class="close-bubble">X</button>
                        <div class="bubble-title">Looking for all Subsystems at once?</div>
                        <div class="bubble-body">
                            <span>Try:</span>
                            <ul id="all-systems-link-list">
                                <li><a href="/hco/readiness">Readiness</a> - Hierarchical Roll-up</li>
                                <li><a href="/hco/reports/signoff">Signoff Report</a> - Tabular Summary</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </jsp:body>
</t:page>