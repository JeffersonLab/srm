<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Component Beam Destination Participation"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">      
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/component-participation.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">                      
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/component-participation.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true">
                <form id="filter-form" action="component-participation" method="get">
                    <fieldset class="content-filter">
                        <legend>Component Filter (Rows)</legend>
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
                                                <c:out value="${region.name}"/></option>
                                        </c:forEach>
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
                    <fieldset class="display-filter">
                        <legend>Beam Destination Filter (Columns)</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="col-destination-select">Beam Destination</label>
                                </div>
                                <div class="li-value">
                                    <select id="col-destination-select" name="colDestinationId" multiple="multiple">
                                        <c:forEach items="${destinationList}" var="destination">
                                            <option value="${destination.beamDestinationId}"${s:inArray(paramValues.colDestinationId, destination.beamDestinationId.toString()) ? ' selected="selected"' : ''}>
                                                <c:out value="${destination.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="hidden" id="max-input" name="max" value="${param.max}"/>
                    <input type="submit" id="filter-form-submit-button" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <c:if test="${fn:length(componentList) > 0}">
                <div class="carousel-table-wrapper">
                    <div class="horizontal-pagination-buttons">
                        <span id="destinationSelectionMessage">Beam Destinations:</span>
                        <button id="horizontal-previous-button" type="button">&larr; Previous</button>
                        <button id="horizontal-next-button" type="button">Next &rarr;</button>
                    </div>
                    <table class="data-table stripped-table component-destination-table editable">
                        <thead>
                        <tr>
                            <th style="display: none;" class="component-header"></th>
                            <c:forEach items="${filteredDestinationList}" var="destination">
                                <th style="display: none;" class="destination-header">
                                    <div class="destination-name-box" title="${fn:escapeXml(destination.name)}"><c:out
                                            value="${destination.name}"/></div>
                                </th>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${componentList}" var="component">
                            <tr data-component-id="${component.componentId}">
                                <th style="display: none;">
                                    <div class="component-name-box">
                                        <a title="${fn:escapeXml(srm:formatComponent(component))}" class="dialog-ready"
                                           data-dialog-title="Component Information: ${fn:escapeXml(srm:formatComponent(component))}"
                                           href="${pageContext.request.contextPath}/reports/component/detail?componentId=${component.componentId}">
                                            <c:out value="${srm:formatComponent(component)}"/>
                                        </a>
                                    </div>
                                </th>
                                <c:forEach items="${filteredDestinationList}" var="destination">
                                    <td style="display: none;" data-destination-id="${destination.beamDestinationId}">
                                        <c:if test="${component.beamDestinationList.contains(destination)}">
                                            ✔
                                        </c:if>
                                    </td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <span>Showing Components <fmt:formatNumber value="${paginator.startNumber}"/> - <fmt:formatNumber
                        value="${paginator.endNumber}"/> of <fmt:formatNumber value="${paginator.totalRecords}"/></span>
                (<select id="vertical-record-max-selector">
                <option ${(param.max eq null || param.max eq '' || param.max eq '10') ? 'selected="selected"' : ''}>10
                </option>
                <option ${param.max eq '100' ? 'selected="selected"' : ''}>100</option>
                <option ${param.max eq '1000' ? 'selected="selected"' : ''}>1000</option>
                </select> at a time)
                <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>&uarr; Previous
                </button>
                <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next &darr;
                </button>
            </c:if>
        </section>
    </jsp:body>
</t:setup-page>
