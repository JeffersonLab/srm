<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:choose>
    <c:when test="${param.subsystemFirst eq 'Y'}">
        <fieldset>
            <legend>Step 1:</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label for="category-select">Category</label>
                    </div>
                    <div class="li-value">
                        <select id="category-select" name="categoryId">
                            <option value="">&nbsp;</option>
                            <c:forEach items="${categoryRoot.children}" var="child">
                                <t:hierarchical-select-option node="${child}" level="0" parameterName="categoryId"/>
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
                                <label class="required-field" for="system-select">Subsystem</label>
                            </div>
                            <div class="sub-value">
                                <select id="system-select" name="systemId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${systemList}" var="system">
                                        <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                            <c:out value="${system.name}"/></option>
                                    </c:forEach>
                                </select>
                                <a href="#" class="flyout-link" data-flyout-type="all-subsystems-flyout">All</a>
                            </div>
                        </div>
                    </div>
                </li>
            </ul>
        </fieldset>
        <fieldset>
            <legend>Step 2:</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label class="required-field" for="group-select">Group</label>
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
    </c:when>
    <c:otherwise>
        <fieldset>
            <legend>Step 1:</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label class="required-field" for="group-select">Group</label>
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
        <fieldset>
            <legend>Step 2:</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label class="required-field" for="system-select">Subsystem</label>
                    </div>
                    <div class="li-value">
                        <select id="system-select" name="systemId">
                            <option value="">&nbsp;</option>
                            <c:forEach items="${systemList}" var="system">
                                <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                    <c:out value="${system.name}"/></option>
                            </c:forEach>
                        </select>
                        <a href="#" class="flyout-link" data-flyout-type="all-subsystems-flyout">All</a>
                    </div>
                </li>
            </ul>
        </fieldset>
    </c:otherwise>
</c:choose>
<fieldset>
    <legend>Step 3 (optional):</legend>
    <ul class="key-value-list">
        <li>
            <div class="li-key">
                <label for="destination-select">Beam Destination</label>
                <div class="default-selection-panel">(<a id="current-run-link" href="#">Current Run</a>)</div>
            </div>
            <div class="li-value" style="visibility: hidden;">
                <select id="destination-select" multiple="multiple" name="destinationId" style="min-height: 34px;"
                        data-current-run-id-csv="${targetCsv}">
                    <c:forEach items="${destinationList}" var="destination">
                        <option value="${destination.beamDestinationId}"${s:inArray(paramValues.destinationId, destination.beamDestinationId.toString()) ? ' selected="selected"' : ''}>
                            <c:out value="${destination.name}"/></option>
                    </c:forEach>
                </select>
            </div>
        </li>
        <li>
            <div class="li-key">
                <label for="region-select">Region</label>
            </div>
            <div class="li-value" style="visibility: hidden;">
                <select id="region-select" multiple="multiple" name="regionId" style="min-height: 34px;">
                    <c:forEach items="${regionList}" var="region">
                        <option value="${region.regionId}"${s:inArray(paramValues.regionId, region.regionId.toString()) ? ' selected="selected"' : ''}>
                            <c:out value="${region.name} (${region.alias})"/></option>
                    </c:forEach>
                </select>
            </div>
        </li>
        <li>
            <div class="li-key">
                <label for="status-select">Status</label>
                <div class="default-selection-panel">(<a id="exclude-na-link" href="#">Exclude N/A</a>)</div>
            </div>
            <div class="li-value" style="visibility: hidden;">
                <select id="status-select" multiple="multiple" name="statusId" style="min-height: 34px;">
                    <c:forEach items="${statusList}" var="status">
                        <option value="${status.statusId}"${s:inArray(statusIdArray, status.statusId) ? ' selected="selected"' : ''}>
                            <c:out value="${status.name}"/></option>
                    </c:forEach>
                </select>
            </div>
        </li>
        <li title="Whether or not the group's component signoff is the next in line to move to Ready">
            <div class="li-key">
                <label for="ready-turn-select">Ready Turn</label>
            </div>
            <div class="li-value">
                <select id="ready-turn-select" name="readyTurn">
                    <option value="">&nbsp;</option>
                    <option value="Y"${param.readyTurn eq 'Y' ? ' selected="selected"' : ''}>Yes</option>
                    <option value="N"${param.readyTurn eq 'N' ? ' selected="selected"' : ''}>No</option>
                </select>
            </div>
        </li>
        <li title="The component name">
            <div class="li-key">
                <label for="component">Component</label>
            </div>
            <div class="li-value">
                <input id="component" class="component-autocomplete" data-application-id="1" name="componentName"
                       value="${fn:escapeXml(param.componentName)}" placeholder="name"/>
                (use % as wildcard)
            </div>
        </li>
        <li title="The minimum last modified date of the selected group's signoffs">
            <div class="li-key">
                <label for="min-modified">Min Last Modified</label>
            </div>
            <div class="li-value">
                <input id="min-modified" name="minLastModified" class="date-time-field"
                       placeholder="${s:getFriendlyDateTimePlaceholder()}" value="${fn:escapeXml(param.minLastModified)}"/>
            </div>
        </li>
        <li title="The maximum last modified date of the selected group's signoffs">
            <div class="li-key">
                <label for="max-modified">Max Last Modified</label>
            </div>
            <div class="li-value">
                <input id="max-modified" name="maxLastModified" class="date-time-field"
                       placeholder="${s:getFriendlyDateTimePlaceholder()}" value="${fn:escapeXml(param.maxLastModified)}"/>
            </div>
        </li>
    </ul>
</fieldset>
<input type="hidden" name="qualified" value=""/>
<div id="signoff-parameter-panel-buttons">
    <input class="signoff-action-button" type="submit" value="Apply"/>
    <c:if test="${pageContext.request.isUserInRole('srm-admin')}">
        <input id="save-signoff-button" class="signoff-action-button" type="button" value="Save Named Signoff"/>
    </c:if>
</div>