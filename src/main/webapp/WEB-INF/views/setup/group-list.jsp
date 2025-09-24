<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group List"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">       
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/group-list.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <s:editable-row-table-controls excludeDelete="true"/>
            <table class="data-table stripped-table uniselect-table editable-row-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Leader Workgroup</th>
                    <th>Archived</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${groupList}" var="group">
                    <tr data-group-id="${group.groupId}" data-workgroup="${group.leaderWorkgroup}" data-archived="${group.archived}">
                        <td><a title="Group Information" class="dialog-opener"
                               data-dialog-title="Group Information: ${fn:escapeXml(group.name)}"
                               href="${pageContext.request.contextPath}/group-detail?groupId=${group.groupId}"><c:out
                                value="${group.name}"/></a></td>
                        <td><c:out value="${group.description}"/></td>
                        <td><c:out value="${group.leaderWorkgroup}"/></td>
                        <td><c:out value="${group.archived}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <s:editable-row-table-dialog>
                <section>
                <form id="row-form">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="row-name">Name</label>
                            </div>
                            <div class="li-value">
                                <input type="text" name="name" id="row-name"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-description">Description</label>
                            </div>
                            <div class="li-value">
                                <input type="text" name="description" id="row-description"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-workgroup">Leader Workgroup</label>
                            </div>
                            <div class="li-value">
                                <input type="text" name="workgroup" id="row-workgroup"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-archived">Archived</label>
                            </div>
                            <div class="li-value">
                                <input type="checkbox" name="archived" id="row-archived"/>
                            </div>
                        </li>
                    </ul>
                </form>
                </section>
            </s:editable-row-table-dialog>
        </section>
    </jsp:body>
</s:setup-page>