<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Component"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts">          
        <script type="text/javascript">
            $(document).on("click", ".default-clear-panel", function () {
                $("#destination-select").val(null).trigger('change');
                $("#category-select").val('').trigger('change');
                $("#system-select").val('');
                $("#region-select").val('');
                $("#group-select").val('');
                $("#status-select").val('');
                $("#unpowered-select").val('');
                $("#component").val('');
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
            <s:filter-flyout-widget clearButton="true">
                <form class="filter-form" method="get" action="component">
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
                                        <label for="status-select">Status</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="status-select" name="statusId">
                                            <option value="">&nbsp;</option>
                                            <c:forEach items="${statusList}" var="status">
                                                <option value="${status.statusId}"${param.statusId eq status.statusId ? ' selected="selected"' : ''}>
                                                    <c:out value="${status.name}"/></option>
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
                                        <input id="component" class="component-autocomplete" data-application-id="1"
                                               name="componentName" value="${fn:escapeXml(param.componentName)}"
                                               placeholder="name"/>
                                        (use % as wildcard)
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
            <div>
                <c:if test="${fn:length(componentStatusList) > 0}">
                    <table class="data-table stripped-table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>System</th>
                            <th>Region</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${componentStatusList}" var="component">
                            <tr>
                                <td><a title="Component Information" class="dialog-opener"
                                       data-dialog-title="Component Information: ${fn:escapeXml(srm:formatFakeComponent(component.name, component.unpowered))}"
                                       href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}"><c:out
                                        value="${srm:formatFakeComponent(component.name, component.unpowered)}"/></a>
                                </td>
                                <td><c:out value="${component.systemName}"/></td>
                                <td><c:out value="${component.regionName}"/></td>
                                <td>
                                    <c:url var="url" value="../signoff">
                                        <c:param name="systemId" value="${component.systemId}"/>
                                        <c:param name="componentName" value="${component.name}"/>
                                        <c:param name="systemFirst" value="Y"/>
                                        <c:param name="qualified" value=""/>
                                    </c:url>
                                    <a href="${fn:escapeXml(url)}"><c:out value="${component.statusName}"/></a>
                                </td>
                                <td>
                                    <form method="get"
                                          action="${pageContext.request.contextPath}/reports/component/detail">
                                        <input type="hidden" name="componentId" value="${component.componentId}"/>
                                        <button class="single-char-button" type="submit">&rarr;</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
                            value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                    </button>
                    <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
                            value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                    </button>
                </c:if>
            </div>
        </section>
    </jsp:body>
</t:reports-page>