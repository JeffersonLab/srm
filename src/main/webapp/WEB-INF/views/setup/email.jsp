<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Email"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            #activity-table {
                border-collapse: collapse;
            }

            #activity-table td {
                vertical-align: top;
                word-wrap: break-word;
                border-left: 0;
                border-right: 0;
                padding-left: 0;
                padding-right: 0;
                padding-top: 0;
            }

            #activity-table td:first-child {
                width: 200px;
            }

            #activity-table td:nth-child(2) {
                width: 250px;
            }

            .cell-header {
                margin-top: 0;
                padding: 0.5em;
                background-color: navy;
                color: white;
                font-weight: bold;
                min-height: 1.5em;
                white-space: nowrap;
            }

            .cell-footer {
                white-space: nowrap;
            }

            tr:nth-child(2n) .cell-header {
                color: #f5f5f5;
            }

            tr td:nth-child(2) .cell-header {
                overflow: hidden;
            }

            .cell-subfield:first-child {
                margin-top: 0;
            }

            .cell-subfield {
                padding: 0.5em;
            }

            .cell-sublabel {
                font-size: 0.8em;
                color: #595959;
            }

            .email-section {
                background-color: black;
                color: white;
                padding: 1em;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/email.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <h3 class="email-section">On Demand Emails</h3>
            <h4>Mask Request Emails</h4>
            <p>Each time a mask is requested an email is sent to the list of interested parties. This recipient list
                consists of:</p>
            <code>
                <ul>
                    <c:forEach items="${maskRequestAddresses}" var="address">
                        <li><c:out value="${address}"/></li>
                    </c:forEach>
                </ul>
                <c:if test="${empty maskRequestAddresses}">
                    None
                </c:if>
            </code>
            <h4>Feedback Emails</h4>
            <p>An email is sent to interested parties when feedback is submitted via the Help tab. The recipient list
                consists of:</p>
            <code>
                <ul>
                    <c:forEach items="${feedbackAddresses}" var="address">
                        <li><c:out value="${address}"/></li>
                    </c:forEach>
                </ul>
                <c:if test="${empty feedbackAddresses}">
                    None
                </c:if>
            </code>
            <h3 class="email-section">Scheduled Daily Emails:
                    ${schedulerEnabled ? ' YES' : ' NO'}
            </h3>
            <h4>Activity Emails</h4>
            <p>Activty report emails are scheduled for 7:00 AM, are sent to interested parties, and report notable
                activity from the previous day. The recipient list consists of:</p>
            <code>
                <ul>
                    <c:forEach items="${activityAddresses}" var="address">
                        <li><c:out value="${address}"/></li>
                    </c:forEach>
                </ul>
                <c:if test="${empty activityAddresses}">
                    None
                </c:if>
            </code>
            <fieldset style="display:inline-block;">
                <a style="margin-right: 10px;" data-dialog-title="HCO - Activity Report" class="dialog-opener"
                   href="${pageContext.request.contextPath}/activity-daily-email">Preview</a>
                <button id="activity-report-on-demand-button" type="button">Send Now</button>
            </fieldset>
            <h4>Group Leader Action Needed Emails</h4>
            <p>Group action report emails are scheduled for 7:00 AM, are sent to group leaders, and report the group's
                new outstanding action items from the previous day</p>
            <table class="data-table stripped-table">
                <thead>
                <tr>
                    <th>Group</th>
                    <th>Preview</th>
                    <th>On Demand</th>
                </tr>
                </thead>
                <tfoot>
                <tr>
                    <th></th>
                    <th></th>
                    <th>
                        <button id="send-all-button" type="button">Send To All</button>
                    </th>
                </tr>
                </tfoot>
                <tbody>
                <c:forEach items="${groupList}" var="group">
                    <tr>
                        <td><a title="Group Information" class="dialog-opener"
                               data-dialog-title="Group Information: ${fn:escapeXml(group.name)}"
                               href="${pageContext.request.contextPath}/group-detail?groupId=${group.groupId}"><c:out
                                value="${group.name}"/></a></td>
                        <td><a data-dialog-title="HCO - Group Action Report" class="dialog-opener"
                               href="${pageContext.request.contextPath}/group-daily-email?groupId=${group.groupId}">Preview</a>
                        </td>
                        <td>
                            <button class="on-demand-button" type="button" data-group-id="${group.groupId}">Send Now
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </jsp:body>
</s:setup-page>