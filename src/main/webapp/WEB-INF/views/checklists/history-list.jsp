<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="hco" uri="http://jlab.org/hco/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title"
       value="Checklists > ${checklist.groupResponsibility.group.name.concat(' > ').concat(checklist.groupResponsibility.system.name)} History"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">        
    </jsp:attribute>
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li>
                    <a href="${pageContext.request.contextPath}/checklists">Checklists</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checklists?groupId=${checklist.groupResponsibility.group.groupId}"><c:out
                            value="${checklist.groupResponsibility.group.name}"/></a>
                </li>
                <li>
                    <c:out value="${checklist.groupResponsibility.system.name}"/> History
                </li>
            </ul>
        </div>
        <section>
            <div class="dialog-content">
                <table class="data-table stripped-table constrained-table">
                    <thead>
                    <tr>
                        <th>Revision Number</th>
                        <th>Revision Date</th>
                        <th>Revision Comments</th>
                        <th>Author</th>
                        <th>Submitted By</th>
                        <th>Checklist</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="history" items="${checklistHistoryList}" varStatus="status">
                        <tr>
                            <td><c:out value="${status.count}"/></td>
                            <td><fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                value="${history.modifiedDate}"/></td>
                            <td><c:out value="${history.comments}"/></td>
                            <td><c:out value="${history.author}"/></td>
                            <td><c:out value="${s:formatUsername(history.modifiedBy)}"/></td>
                            <td>
                                <a data-dialog-title="${history.checklist.groupResponsibility.group.name.concat(' ').concat(history.checklist.groupResponsibility.system.name)} Checklist (History)"
                                   class="dialog-ready" data-dialog-type="checklist"
                                   href="${pageContext.request.contextPath}/checklists/revision?checklistHistoryId=${history.checklistHistoryId}&amp;revision=${status.count}">View</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </jsp:body>
</t:page>
