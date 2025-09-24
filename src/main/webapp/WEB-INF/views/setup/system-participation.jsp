<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="System Application Participation"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/system-participation.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">              
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/system-participation.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget>
                <form class="filter-form" method="get"
                      action="${pageContext.request.contextPath}/setup/system-participation">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="category">Category</label>
                                </div>
                                <div class="li-value">
                                    <select id="category" name="categoryId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${categoryRoot.children}" var="child">
                                            <t:hierarchical-select-option node="${child}" level="0"
                                                                          parameterName="categoryId"/>
                                        </c:forEach>
                                    </select>
                                    <span id="category-indicator" class="form-control-indicator"></span>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <c:choose>
                <c:when test="${systemList eq null}">
                    <div class="message-box">No Systems found</div>
                </c:when>
                <c:otherwise>
                    <div class="message-box">
                        <c:choose>
                            <c:when test="${selectedCategory ne null}">
                                Category &quot;<c:out value="${selectedCategory.name}"/>&quot;
                            </c:when>
                            <c:otherwise>
                                All Systems
                            </c:otherwise>
                        </c:choose>
                        {${fn:length(systemList)}}
                    </div>
                </c:otherwise>
            </c:choose>
            <c:if test="${fn:length(systemList) > 0}">
                <table class="data-table stripped-table fixed-table system-participation-table editable">
                    <thead>
                    <tr>
                        <th rowspan="2" class="system-header">System</th>
                        <th colspan="${fn:length(applicationList)}">Application</th>
                    </tr>
                    <tr>
                        <c:forEach items="${applicationList}" var="application">
                            <th class="application-header"><c:out value="${application.name}"/></th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${systemList}" var="system">
                        <tr data-system-id="${system.systemId}">
                            <th><a title="System Information" class="dialog-opener"
                                   data-dialog-title="System Information: ${fn:escapeXml(system.name)}"
                                   href="${pageContext.request.contextPath}/system-detail?systemId=${system.systemId}"><c:out
                                    value="${system.name}"/></a></th>
                            <c:forEach items="${applicationList}" var="application">
                                <td data-application-id="${application.applicationId}">
                                    <c:if test="${system.applicationList.contains(application)}">
                                        ✔
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </section>
    </jsp:body>
</s:setup-page>
