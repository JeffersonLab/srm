<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<c:set var="title" value="${checklist.groupResponsibility.group.name} ${checklist.groupResponsibility.system.name} Checklist"/>
<s:loose-page title="${title}" category="" description="Responsible Group Detail">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
                  href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/srm.css"/>
        <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/checklist.css"/>
        <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/fullpage-checklist.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/srm.js"></script>
        <script type="text/javascript">
            jlab.contextPath = '${pageContext.request.contextPath}';
        </script>
    </jsp:attribute>
    <jsp:body>
        <c:if test="${'Y' ne param.partial}">
<div id="page">
    <c:if test="${not editable}">
        <div class="banner-breadbox no-bottom-border nav-links">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/readiness"><c:out value="${initParam.appShortName}"/></a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checklists">Checklists</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checklists?groupId=${checklist.groupResponsibility.group.groupId}"><c:out
                            value="${checklist.groupResponsibility.group.name}"/></a>
                </li>
                <li>
                    <c:out value="${checklist.groupResponsibility.system.name}"/> Printable Checklist
                </li>
            </ul>
        </div>
    </c:if>
    <div id="content">
        <div id="content-liner">
        </c:if>
            <section>
                <form method="post" action="checklist">
                    <h1 class="hide-in-dialog"><c:out
                            value="${checklist.groupResponsibility.group.name.concat(' ').concat(checklist.groupResponsibility.system.name)} Checklist"/></h1>
                    <div class="dialog-content">
                        <div class="dialog-links">
                            <c:if test="${'Y' eq param.partial}">
                            <a href="${pageContext.request.contextPath}/checklist?checklistId=${checklist.checklistId}">Printer
                                Friendly</a>
                            </c:if>
                        </div>
                        <div>
                            <c:choose>
                                <c:when test="${editable}">
                                    <div style="float: right; font-weight: bold;">(<span class="required-field"></span>
                                        required)
                                    </div>
                                    <ul class="key-value-list">
                                        <li>
                                            <div class="li-key">
                                                <label class="required-field" for="author">Author</label>
                                            </div>
                                            <div class="li-value">
                                                <input type="text" id="author" name="author"
                                                       value="${param.author ne null ? fn:escapeXml(param.author) : checklist.author}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Revision Number:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${revision + 1}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                <label class="${revision == null || revision == 0 ? '' : 'required-field'}"
                                                       for="comments">Revision Comment</label>
                                            </div>
                                            <div class="li-value">
                                                <textarea id="comments" name="comments">${fn:escapeXml(param.comments)}</textarea>
                                            </div>
                                        </li>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <ul class="key-value-list">
                                        <li>
                                            <div class="li-key">
                                                Document ID:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${checklist.checklistId}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Group:
                                            </div>
                                            <div class="li-value">
                                                <span class="fullpage-only"><c:out
                                                        value="${checklist.groupResponsibility.group.name}"/></span>
                                                <a title="Group Information" class="dialog-opener dialog-only"
                                                   data-dialog-title="Group Information: ${fn:escapeXml(checklist.groupResponsibility.group.name)}"
                                                   href="${pageContext.request.contextPath}/group-detail?groupId=${checklist.groupResponsibility.group.groupId}"><c:out
                                                        value="${checklist.groupResponsibility.group.name}"/></a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                System:
                                            </div>
                                            <div class="li-value">
                                                <span class="fullpage-only"><c:out
                                                        value="${checklist.groupResponsibility.system.name}"/></span>
                                                <a title="System Information" class="dialog-opener dialog-only"
                                                   data-dialog-title="System Information: ${fn:escapeXml(checklist.groupResponsibility.system.name)}"
                                                   href="${pageContext.request.contextPath}/system-detail?systemId=${checklist.groupResponsibility.system.systemId}"><c:out
                                                        value="${checklist.groupResponsibility.system.name}"/></a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Author:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${checklist.author}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Submitted By:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${s:formatUsername(checklist.modifiedBy)}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Revision Number:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${revision}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Revision Date:
                                            </div>
                                            <div class="li-value">
                                                <fmt:formatDate pattern="${s:getFriendlyDateTimePattern()}"
                                                                value="${checklist.modifiedDate}"/>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="li-key">
                                                Revision Comment:
                                            </div>
                                            <div class="li-value">
                                                <c:out value="${checklist.comments}"/>
                                            </div>
                                        </li>
                                    </ul>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <hr/>
                        <c:choose>
                            <c:when test="${editable}">
                                        <textarea name="bodyHtml" class="checklist-body">
                                                ${param.bodyHtml ne null ? fn:escapeXml(param.bodyHtml) : checklist.bodyHtml}
                                        </textarea>
                            </c:when>
                            <c:otherwise>
                                <div class="checklist-body">
                                        ${checklist.bodyHtml}
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="message-box error-message">
                        <c:out value="${errorMessage}"/>
                    </div>
                    <c:if test="${editable}">
                        <div class="edit-controls">
                            <input type="submit" value="Save"/>
                            <a href="${pageContext.request.contextPath}/checklists?groupId=${checklist.groupResponsibility.group.groupId}">Cancel</a>
                        </div>
                        <script src="${pageContext.request.contextPath}/resources/tinymce/tinymce.min.js"></script>
                        <script>
                            tinymce.init({
                                selector: '.checklist-body',
                                plugins: "paste link textcolor template",
                                content_css: "${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/checklist.css",
                                menubar: false,
                                toolbar: "undo redo | styleselect forecolor | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link template",
                                height: 500,
                                gecko_spellcheck: true,
                                templates: [
                                    {title: 'Please Select An Option To Continue... ', description: '', content: ''},
                                    {
                                        title: 'Uncredited Control Level 1',
                                        description: 'Uncredited Control Level 1',
                                        url: 'resources/tinymce-templates/uncredited-level1.html'
                                    },
                                    {
                                        title: 'Uncredited Control Level 2',
                                        description: 'Uncredited Control Level 2',
                                        url: 'resources/tinymce-templates/uncredited-level2.html'
                                    }
                                ]
                            });
                        </script>
                    </c:if>
                    <input type="hidden" name="checklistId" value="${checklist.checklistId}"/>
                    <input type="hidden" name="groupId" value="${checklist.groupResponsibility.group.groupId}"/>
                    <input type="hidden" name="systemId" value="${checklist.groupResponsibility.system.systemId}"/>
                </form>
            </section>
        <c:if test="${'Y' ne param.partial}">
        </div>
    </div>
        </c:if>
    </jsp:body>
</s:loose-page>