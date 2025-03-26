<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group Responsibility"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts">            
        <script type="text/javascript">
            $(document).on("click", ".default-clear-panel", function () {
                $("#destination-select").val(null).trigger('change');
                $("#category-select").val('').trigger('change');
                $("#group-select").val('');
                $("#system-select").val('');
                $("#checklist-required-select").val('');
                $("#checklist-missing-select").val('');
                return false;
            });
            $(document).on("change", "#category-select", function () {
                var categoryId = $(this).val();
                jlab.srm.filterSystemListByCategory(categoryId);
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
            <s:filter-flyout-widget clearButton="true">
                <form class="filter-form" method="get" action="group-responsibility">
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
                                    <label for="checklist-required-select">Checklist Required</label>
                                </div>
                                <div class="li-value">
                                    <select id="checklist-required-select" name="checklistRequired">
                                        <option value="">&nbsp;</option>
                                        <option value="Y"${param.checklistRequired eq 'Y' ? ' selected="selected"' : ''}>
                                            Yes
                                        </option>
                                        <option value="N"${param.checklistRequired eq 'N' ? ' selected="selected"' : ''}>
                                            No
                                        </option>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="checklist-missing-select">Checklist Missing</label>
                                </div>
                                <div class="li-value">
                                    <select id="checklist-missing-select" name="checklistMissing">
                                        <option value="">&nbsp;</option>
                                        <option value="Y"${param.checklistMissing eq 'Y' ? ' selected="selected"' : ''}>
                                            Yes
                                        </option>
                                        <option value="N"${param.checklistMissing eq 'N' ? ' selected="selected"' : ''}>
                                            No
                                        </option>
                                    </select>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <c:if test="${fn:length(groupResponsibilityList) > 0}">
                <div class="chart-wrap-backdrop">
                    <table class="data-table stripped-table chart-data-table">
                        <thead>
                        <tr>
                            <th>Group</th>
                            <th>System</th>
                            <th>Checklist Required</th>
                            <th>Checklist</th>
                            <th>Checklist Published</th>
                            <th>Checklist Modified Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${groupResponsibilityList}" var="responsibility">
                            <tr>
                                <td><c:out value="${responsibility.group.name}"/></td>
                                <td><c:out value="${responsibility.system.name}"/></td>
                                <td><c:out value="${responsibility.checklistRequired ? 'Yes' : 'No'}"/></td>
                                <td>
                                    <c:if test="${responsibility.checklist ne null}">
                                        <a data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                           class="dialog-ready" data-dialog-type="checklist"
                                           href="${pageContext.request.contextPath}/checklist?checklistId=${responsibility.checklist.checklistId}">View</a>
                                    </c:if>
                                </td>
                                <td>
                                    <c:out value="${responsibility.published ? 'Yes' : 'No'}"/>
                                    <c:if test="${responsibility.publishedBy ne null}">
                                        [by <c:out value="${s:formatUsername(responsibility.publishedBy)}"/>
                                        on <fmt:formatDate pattern="${s:getFriendlyDatePattern()}"
                                                           value="${responsibility.publishedDate}"/>]
                                    </c:if>
                                </td>
                                <td><fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                    value="${responsibility.checklist.modifiedDate}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="paginator-button-panel">
                    <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                            value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                    </button>
                    <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                            value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                    </button>
                </div>
            </c:if>
            <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                       value="${now}"/></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:reports-page>