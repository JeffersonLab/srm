<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<c:set var="title" value="Component Activity"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets"> 
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget requiredMessage="true">
                <form id="filter-form" method="get" action="component-audit">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="component-id">Component ID</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="component-id" name="componentId"
                                           value="${fn:escapeXml(param.componentId)}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="revision-id">Revision ID</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="revision-id" name="revisionId" value="${fn:escapeXml(param.revisionId)}"/>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title">Inventory Activity: Component <c:out value="${param.componentId}"/></h2>
            <ul class="bracket-horizontal-nav">
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity">Transactions</a>&nbsp;</li>
                <li>Component&nbsp;</li>
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity/system-audit">System</a>&nbsp;
                </li>
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity/category-audit">Category</a>&nbsp;
                </li>
            </ul>
            <c:choose>
                <c:when test="${param.componentId == null}">
                    <div class="message-box">Choose a component ID to continue</div>
                </c:when>
                <c:when test="${fn:length(revisionList) == 0}">
                    <div class="message-box">Found 0 Revisions</div>
                </c:when>
                <c:otherwise>
                    <div class="message-box">Showing Revisions <fmt:formatNumber value="${paginator.startNumber}"/> -
                        <fmt:formatNumber value="${paginator.endNumber}"/> of <fmt:formatNumber
                                value="${paginator.totalRecords}"/></div>
                    <table id="revision-table" class="data-table stripped-table">
                        <thead>
                        <tr>
                            <th>Revision #:</th>
                            <c:forEach items="${revisionList}" var="revision" varStatus="status">
                                <td>
                                    <c:out value="${status.count + paginator.offset}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tfoot>
                        <tr>
                            <th>Modified By:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.revision.user != null ? s:formatUser(entity.revision.user) : entity.revision.username}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Modified Date:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
                                                    value="${entity.revision.revisionDate}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Computer:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${srm:getHostnameFromIp(entity.revision.address)}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Revision ID:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.revision.id}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Revision Type:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.type}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        </tfoot>
                        <tbody>
                        <tr>
                            <th>Name:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.name}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>System:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.system.name}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Region:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.region.name}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Unpowered:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${('Y' eq entity.unpoweredStr) ? 'Yes' : 'No'}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Masked:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${('Y' eq entity.maskedStr) ? 'Yes' : 'No'}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Mask Type:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:if test="${!empty entity.maskTypeId}">
                                        <c:out value="${srm:getStatusById(entity.maskTypeId).maskType}"/>
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Masked By:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.maskedBy != null ? s:formatUsername(entity.maskedBy) : ''}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Masked Date:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <fmt:formatDate pattern="dd-MMM-yyyy HH:mm" value="${entity.maskedDate}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Masked Reason:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.maskedComment}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Mask Expiration:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <fmt:formatDate pattern="dd-MMM-yyyy HH:mm" value="${entity.maskExpirationDate}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Source:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.dataSource}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <th>Source ID:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.dataSourceId}"/>
                                </td>
                            </c:forEach>
                        </tr>
                        </tbody>
                    </table>
                    <div class="revision-controls">
                        <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                                value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                        </button>
                        <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                                value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </jsp:body>
</t:reports-page>
