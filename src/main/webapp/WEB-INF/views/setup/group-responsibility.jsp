<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group Responsibility"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            .dialog textarea {
                width: 20em;
                height: 8em;
            }

            #update-checklistUrl,
            #create-checklistUrl {
                width: 20em;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/group-responsibility-setup.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <c:choose>
            <c:when test="${selectedSystem eq null}">
            <h2 class="page-header-title"><c:out value="${title}"/></h2> <span style="font-weight: bold;">(<span
                class="required-field"></span> required)
                        <form class="filter-form" action="group-responsibility" method="get">
                            <div id="filter-form-panel">
                                <fieldset>
                                    <jsp:include page="/WEB-INF/includes/group-responsibility-setup-form.jsp"/>
                                </fieldset>
                            </div>
                            <input type="submit" style="margin-top: 1em;" value="Apply"/>
                        </form>                        
                        <div class="message-box">Select a System to continue</div>
                    </c:when>                                
                    <c:otherwise>
                        <s:filter-flyout-widget requiredMessage="true" clearButton="true">
                            <form class="filter-form" action="group-responsibility" method="get">
                                <div id="filter-form-panel">
                                    <fieldset>
                                        <legend>Filter</legend>
                                        <jsp:include page="/WEB-INF/includes/group-responsibility-setup-form.jsp"/>
                                    </fieldset>
                                </div>
                                <input type="submit" class="filter-form-submit-button" value="Apply"/>
                            </form>
                        </s:filter-flyout-widget>
                        <h2 class="page-header-title"><c:out value="${title}"/></h2>
                        <div class="message-box"><c:out value="${selectionMessage}"/></div>
                        <s:editable-row-table-controls/>
                        <table id="responsibility-table"
                               class="data-table stripped-table uniselect-table editable-row-table">
                            <thead>
                                <tr>
                                    <th></th>
                                    <th>Order</th>
                                    <th>Group</th>
                                    <th>Checklist Required</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="responsibility" items="${selectedSystem.groupResponsibilityList}">
                                    <tr data-group-responsibility-id="${responsibility.groupResponsibilityId}"
                                        data-group-id="${responsibility.group.groupId}">
                                        <td class="drag-handle"><span class="ui-icon ui-icon-carat-2-n-s"></span></td>
                                        <td><c:out value="${responsibility.weight}"/></td>
                                        <td><c:out value="${responsibility.group.name}"/></td>
                                        <td><c:out value="${responsibility.checklistRequired ? 'Yes' : 'No'}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <s:editable-row-table-dialog>
                    <div style="float: right; font-weight: bold;">(<span class="required-field"></span> required)</div>
                    <form id="row-form">
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    System:
                                </div>
                                <div class="li-value">
                                    <span class="system-placeholder"></span>
                                </div>
                            </li>                          
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="group-select">Group</label>
                                </div>
                                <div class="li-value">
                                    <select id="group-select" name="groupId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${groupList}" var="group">
                                            <option value="${group.groupId}"><c:out value="${group.name}"/></option>
                                        </c:forEach>
                                    </select> 
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="create-checklist-required-select">Checklist Required</label>
                                </div>
                                <div class="li-value">
                                    <select id="create-checklist-required-select" name="checklistRequired">
                                        <option value="">&nbsp;</option>
                                        <option value="Y">Yes</option>
                                        <option value="N">No</option>
                                    </select> 
                                </div>
                            </li>                                                 
                        </ul>
                        <input type="hidden" id="updateGroupResponsibilityId" name="updateGroupResponsibilityId"
                               value=""/>
                    </form>
                </s:editable-row-table-dialog>
        </section>
    </jsp:body>
</s:setup-page>