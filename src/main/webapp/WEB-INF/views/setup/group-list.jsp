<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group List"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">       
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/group-list.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <s:editable-row-table-controls/>
            <table class="data-table stripped-table uniselect-table editable-row-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Leader Workgroup</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${groupList}" var="group">
                    <tr data-group-id="${group.groupId}" data-workgroup-id="${group.leaderWorkgroup.workgroupId}">
                        <td><a title="Group Information" class="dialog-ready"
                               data-dialog-title="Group Information: ${fn:escapeXml(group.name)}"
                               href="${pageContext.request.contextPath}/group-detail?groupId=${group.groupId}"><c:out
                                value="${group.name}"/></a></td>
                        <td><c:out value="${group.description}"/></td>
                        <td><c:out value="${group.leaderWorkgroup.name}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div id="add-dialog" class="dialog" title="Add New Group">
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="add-name">Name</label>
                            </div>
                            <div class="li-value">
                                <input type="text" name="addName" id="add-name"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="add-description">Description</label>
                            </div>
                            <div class="li-value">
                                <input type="text" name="addDescription" id="add-description"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="add-workgroup">Leader Workgroup</label>
                            </div>
                            <div class="li-value">
                                <select id="add-workgroup" name="workgroupId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${workgroupList}" var="workgroup">
                                        <option value="${workgroup.workgroupId}"><c:out
                                                value="${workgroup.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                    <div class="dialog-button-panel">
                        <button type="button" id="add-button" class="dialog-submit">Save</button>
                        <button type="button" class="dialog-close-button">Cancel</button>
                    </div>
                </form>
            </div>
            <s:editable-row-table-dialog>
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
                                <select id="row-workgroup" name="workgroupId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${workgroupList}" var="workgroup">
                                        <option value="${workgroup.workgroupId}"><c:out
                                                value="${workgroup.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </ul>
                </form>
            </s:editable-row-table-dialog>
        </section>
    </jsp:body>
</t:setup-page>       