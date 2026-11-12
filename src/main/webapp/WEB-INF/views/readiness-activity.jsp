<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<%@taglib prefix="srm" uri="http://jlab.org/srm/functions" %>
<%-- Recent Activity entries: the readiness page includes the first, and /ajax/recent-activity gives the next for it to append --%>
<tbody data-more="${signoffActivityMore ? 'true' : 'false'}">
<c:forEach items="${signoffActivityList}" var="signoff">
    <tr class="top-row">
        <fmt:formatDate pattern="dd-MMM-yyyy HH:mm" value="${signoff.modifiedDate}"
                        var="fullDate"/>
        <td>
            <span class="date-header" title="${fn:escapeXml(fullDate)}"><fmt:formatDate
                    pattern="dd MMM HH:mm" value="${signoff.modifiedDate}"/></span>
            - <span class="activity-user"
                    title="${s:formatUsername(signoff.modifiedBy)}"><c:out
                value="${signoff.modifiedBy}"/></span>
            - <c:out value="${srm:formatChangeType(signoff.changeType)}"/> - <c:out
                value="${signoff.systemName}"/>
            - <c:out
                value="${srm:formatFakeComponent(signoff.firstComponentName, signoff.firstUnpowered)}"/>
            <c:if test="${signoff.componentCount > 1}">
                + ${signoff.componentCount - 1} more
            </c:if>
        </td>
    </tr>
    <tr class="middle-row">
        <td><c:out value="${signoff.comments}"/></td>

    </tr>
    <tr class="bottom-row">
        <td><c:out value="${signoff.groupName}"/> - <c:out
                value="${signoff.statusName}"/></td>
    </tr>
</c:forEach>
</tbody>
