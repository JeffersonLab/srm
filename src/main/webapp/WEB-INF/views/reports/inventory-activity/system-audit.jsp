<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<c:set var="title" value="System Activity"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets"> 
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget requiredMessage="true">
                <form id="filter-form" method="get" action="system-audit">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="system-id">System ID</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="system-id" name="systemId" value="${param.systemId}"/>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="revision-id">Revision ID</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="revision-id" name="revisionId" value="${param.revisionId}"/>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title">Inventory Activity: Subsystem <c:out value="${param.systemId}"/></h2>
            <ul class="bracket-horizontal-nav">
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity">Transactions</a>&nbsp;</li>
                <li>
                    <a href="${pageContext.request.contextPath}/reports/inventory-activity/component-audit">Component</a>&nbsp;
                </li>
                <li>Subsystem&nbsp;</li>
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity/category-audit">Category</a>&nbsp;
                </li>
            </ul>
            <c:choose>
                <c:when test="${param.systemId == null}">
                    <div class="message-box">Choose a system ID to continue</div>
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
                                    <c:out value="${hco:getHostnameFromIp(entity.revision.address)}"/>
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
                            <th>Category:</th>
                            <c:forEach items="${revisionList}" var="entity">
                                <td>
                                    <c:out value="${entity.category.name}"/>
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
