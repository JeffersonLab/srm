<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Signoff Activity"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            $(document).on("click", ".default-clear-panel", function () {
                $("#destination-select").select2("val", "");
                $("#category-select").val('').trigger('change');
                $("#system-select").val('');
                $("#region-select").val('');
                $("#group-select").val('');
                $("#user").val('');
                $("#component").val('');
                $("#status-select").select2("val", "");
                $("#change-select").val('');
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
                $("#group-select").val('');
                $("#user").val('');
                $("#component").val('');
                $("#status-select").select2("val", "");
                $("#change-select").val('');
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

                $("#status-select").select2({
                    width: 390
                });

                $("#destination-select").closest(".li-value").css("visibility", "visible");

                $("#status-select").closest(".li-value").css("visibility", "visible");
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true" resetButton="true">
                <form id="filter-form" method="get" action="signoff-activity">
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
                                        <label for="user">User</label>
                                    </div>
                                    <div class="li-value">
                                        <input id="user" class="username-autocomplete" name="username"
                                               value="${fn:escapeXml(param.username)}" placeholder="username"/>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="component">Component</label>
                                    </div>
                                    <div class="li-value">
                                        <input id="component" class="component-autocomplete" data-application-id="1"
                                               name="componentName" value="${fn:escapeXml(param.componentName)}"
                                               placeholder="name"/>
                                        (use % as wildcard)
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="status-select">Status</label>
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
                                <li>
                                    <div class="li-key">
                                        <label for="change-select">Change</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="change-select" name="change">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${hco:changeTypeList()}" var="change">
                                                <option value="${change}"${param.change eq change ? ' selected="selected"' : ''}>
                                                    <c:out value="${hco:formatChangeType(change)}"/></option>
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
            <div>
                <div class="dialog-content">
                    <c:if test="${fn:length(signoffActivityList) > 0}">
                        <table class="data-table stripped-table constrained-table">
                            <thead>
                            <tr>
                                <th class="constrained-medium-column">Modified Date</th>
                                <th class="constrained-medium-column">Modified By</th>
                                <th class="constrained-change-column">Change and Status</th>
                                <c:if test="${param.dialog ne 'true'}">
                                    <th class="constrained-medium-column">Component</th>
                                    <th class="constrained-medium-column">Group</th>
                                </c:if>
                                <th>Comments</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${signoffActivityList}" var="activity">
                                <tr>
                                    <td><fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                        value="${activity.modifiedDate}"/></td>
                                    <td><c:out
                                            value="${activity.lastname}, ${activity.firstname} (${activity.username})"/></td>
                                    <td><c:out value="${hco:formatChangeType(activity.changeType)}"/>; <c:out
                                            value="${activity.statusName}"/></td>
                                    <c:if test="${param.dialog ne 'true'}">
                                        <td><a title="Component Information" class="dialog-ready"
                                               data-dialog-title="Component Information: ${fn:escapeXml(hco:formatFakeComponent(activity.componentName, activity.unpowered))}"
                                               href="${pageContext.request.contextPath}/reports/component/detail?componentId=${activity.componentId}"><c:out
                                                value="${hco:formatFakeComponent(activity.componentName, activity.unpowered)}"/></a>
                                        </td>
                                        <td><c:out value="${activity.groupName}"/></td>
                                    </c:if>
                                    <td><c:out value="${activity.comments}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <c:if test="${param.dialog eq 'true' and paginator.next}">
                            <c:url var="url" value="reports/signoff-activity">
                                <c:param name="systemId" value="${param.systemId}"/>
                                <c:param name="componentName" value="${param.component}"/>
                                <c:param name="groupId" value="${param.groupId}"/>
                                <c:param name="qualified" value=""/>
                                <c:param name="offset" value="100"/>
                            </c:url>
                            <a target="_blank" href="${url}">More...</a>
                        </c:if>
                    </c:if>
                    <c:if test="${param.dialog eq 'true' and fn:length(signoffActivityList) <= 0}">
                        <div class="message-box">No History Found</div>
                    </c:if>
                </div>
                <c:if test="${fn:length(signoffActivityList) > 0}">
                    <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                            value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                    </button>
                    <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                            value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                    </button>
                </c:if>
            </div>
        </section>
    </jsp:body>
</t:reports-page>