<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Checklists${selectedGroup ne null ? ' > '.concat(selectedGroup.name) : ''}"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            $(document).on("click", ".delete-button", function () {
                var $tr = $(this).closest("tr"),
                    group = $("#group-select option:selected").text(),
                    system = $tr.find("td:nth-child(1)").text(),
                    checklistId = $(this).attr("data-checklistId");

                if (confirm('Are you sure you want to delete the checklist of group ' + group + ' and system ' + system + ' with all of its history?')) {
                    $("#delete-form-checklist-id").val(checklistId);
                    $("#delete-form").submit();
                }

                return false;
            });
            $(document).on("click", ".publish-button", function () {
                var groupResponsibilityId = $(this).attr("data-groupResponsibilityId");

                $("#publish-form-group-responsibility-id").val(groupResponsibilityId);
                $("#publish-form").submit();

                return false;
            });
        </script>        
    </jsp:attribute>
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/checklists">Checklists</a>
                </li>
                <li>
                    <form class="filter-form" method="get" action="checklists">
                        <select id="group-select" name="groupId" class="change-submit">
                            <option value="">&nbsp;</option>
                            <c:forEach items="${groupList}" var="group">
                                <option value="${group.groupId}"${param.groupId eq group.groupId ? ' selected="selected"' : ''}>
                                    <c:out value="${group.name}"/></option>
                            </c:forEach>
                        </select>
                    </form>
                </li>
            </ul>
        </div>
        <section>
            <c:choose>
                <c:when test="${selectedGroup eq null}">
                    <div class="message-box">Select a group to continue</div>
                </c:when>
                <c:when test="${fn:length(groupResponsibilityList) eq 0}">
                    <div class="message-box">No group responsibilities assigned</div>
                </c:when>
                <c:otherwise>
                    <div>
                        <c:if test="${fn:length(groupResponsibilityList) > 0}">
                            <table class="data-table stripped-table">
                                <thead>
                                <tr>
                                    <th>System</th>
                                    <th>Checklist Required</th>
                                    <th>Published</th>
                                    <th>Checklist</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${groupResponsibilityList}" var="responsibility">
                                    <tr>
                                        <td><c:out value="${responsibility.system.name}"/></td>
                                        <td><c:out value="${responsibility.checklistRequired ? 'Yes' : 'No'}"/></td>
                                        <td>
                                            <c:out value="${responsibility.published ? 'Yes' : 'No'}"/>
                                            <c:if test="${responsibility.published and responsibility.publishedBy ne null}">
                                                [by <c:out value="${s:formatUsername(responsibility.publishedBy)}"/>
                                                on <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                   value="${responsibility.publishedDate}"/>]
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${responsibility.checklist ne null}">
                                                    <a data-dialog-title="${responsibility.group.name.concat(' ').concat(responsibility.system.name)} Checklist"
                                                       class="dialog-opener" data-dialog-type="checklist"
                                                       href="${pageContext.request.contextPath}/checklist?checklistId=${responsibility.checklist.checklistId}">View</a>
                                                    |
                                                    <a href="${pageContext.request.contextPath}/checklists/history-list?checklistId=${responsibility.checklist.checklistId}">History</a>
                                                    <c:if test="${adminOrLeader or (not responsibility.published and pageContext.request.userPrincipal ne null)}">
                                                        |
                                                        <a href="${pageContext.request.contextPath}/checklist?checklistId=${responsibility.checklist.checklistId}&amp;editable=Y">Edit</a>
                                                    </c:if>
                                                    <c:if test="${adminOrLeader}">
                                                        |
                                                        <a class="delete-button" href="#"
                                                           data-checklistId="${responsibility.checklist.checklistId}">Delete</a>
                                                        |
                                                        <a class="publish-button" href="#"
                                                           data-groupResponsibilityId="${responsibility.groupResponsibilityId}">${responsibility.published ? 'Unpublish' : 'Sign &amp; Publish'}</a>
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${pageContext.request.userPrincipal ne null}">
                                                        <a href="${pageContext.request.contextPath}/checklist?groupId=${responsibility.group.groupId}&amp;systemId=${responsibility.system.systemId}&amp;editable=Y">Create</a>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
        <form id="delete-form" method="post" action="${pageContext.request.contextPath}/delete-checklist">
            <input id="delete-form-checklist-id" type="hidden" name="checklistId" value=""/>
        </form>
        <form id="publish-form" method="post" action="${pageContext.request.contextPath}/publish-checklist">
            <input id="publish-form-group-responsibility-id" type="hidden" name="groupResponsibilityId" value=""/>
        </form>
    </jsp:body>
</s:page>