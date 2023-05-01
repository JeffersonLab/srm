<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Inventory Activity"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">   
        <style type="text/css">
            .table-cell-list {
                list-style: none;
                margin: 0;
                padding: 0;
            }

            .table-cell-list-item {
                margin: 1em 0;
            }

            .changes-header {
                width: 250px;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">              
        <script type="text/javascript">
            $(document).on("click", ".default-clear-panel", function () {
                $("#start").val('');
                $("#end").val('');
                $("#range").val('custom').trigger('change');
                return false;
            });
            $(document).on("click", ".default-reset-panel", function () {
                $("#start").val('');
                $("#end").val('');
                $("#range").val('1day').trigger('change');
                return false;
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
                <form id="filter-form" method="get" action="inventory-activity">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Time</legend>
                            <s:date-range datetime="${true}" sevenAmOffset="${true}"/>
                        </fieldset>
                    </div>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <ul class="bracket-horizontal-nav">
                <li>Transactions&nbsp;</li>
                <li>
                    <a href="${pageContext.request.contextPath}/reports/inventory-activity/component-audit">Component</a>&nbsp;
                </li>
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity/system-audit">System</a>&nbsp;
                </li>
                <li><a href="${pageContext.request.contextPath}/reports/inventory-activity/category-audit">Category</a>&nbsp;
                </li>
            </ul>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div class="chart-wrap-backdrop">
                <c:if test="${fn:length(transactionList) > 0}">
                    <table class="data-table stripped-table">
                        <thead>
                        <tr>
                            <th title="Transaction ID">Trans. ID</th>
                            <th>Modified Date</th>
                            <th>Modified By</th>
                            <th>Computer</th>
                            <th class="changes-header">Changes</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${transactionList}" var="revision">
                            <tr>
                                <td><c:out value="${revision.id}"/></td>
                                <td><fmt:formatDate value="${revision.revisionDate}" pattern="dd-MMM-yyyy HH:mm"/></td>
                                <td><c:out
                                        value="${revision.user != null ? s:formatUser(revision.user) : revision.username}"/></td>
                                <td><c:out value="${srm:getHostnameFromIp(revision.address)}"/></td>
                                <td>
                                    <c:if test="${fn:length(revision.changeList) > 0}">
                                        <ul class="table-cell-list">
                                            <c:forEach items="${revision.changeList}" var="change">
                                                <li class="table-cell-list-item">
                                                    <a title="${change.entityClass.simpleName} Audit"
                                                       href="${pageContext.request.contextPath}/reports/inventory-activity/${change.entityClass.simpleName eq 'Component' ? 'component-audit?componentId' : change.entityClass.simpleName eq 'System' ? 'system-audit?systemId' : 'category-audit?categoryId'}=${change.entityId}"><c:out
                                                            value="${change.type} ${change.entityClass.simpleName} ${change.entityName}"/></a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="paginator-button-panel">
                        <button id="previous-button" type="button" data-offset="${paginator.previousOffset}"
                                value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                        </button>
                        <button id="next-button" type="button" data-offset="${paginator.nextOffset}"
                                value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                        </button>
                    </div>
                </c:if>
            </div>
            <div id="report-generated-date">Generated: <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                       value="${now}"/></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:reports-page>