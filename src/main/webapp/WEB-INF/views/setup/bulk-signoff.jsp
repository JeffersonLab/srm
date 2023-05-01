<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Bulk Signoff"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            body .select2-container-multi .select2-choices li {
                float: none !important;
            }

            body .select2-container-multi,
            body .select2-choices {
                min-height: 200px !important;
            }

            #comment {
                width: 575px;
                height: 100px;
            }

            .sub-key {
                vertical-align: top;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/bulk-signoff.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2> <span id="required-span"
                                                                            style="font-weight: bold;">(<span
                class="required-field"></span> required)</span>
            <form id="filter-form" action="bulk-signoff" method="get">
                <div id="filter-form-panel">
                    <fieldset>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="category-select">Category</label>
                                </div>
                                <div class="li-value">
                                    <select id="category-select" name="categoryId">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${categoryRoot.children}" var="child">
                                            <t:hierarchical-select-option node="${child}" level="0"
                                                                          parameterName="categoryId"/>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="sub-level-symbol">↳</span>
                                </div>
                                <div class="li-value" style="visibility: hidden; height: 210px;">
                                    <div class="sub-table">
                                        <div class="sub-key">
                                            <label class="required-field" for="system-select">System</label>
                                            <div class="default-selection-panel">(<a id="select-all-link"
                                                                                     href="#">All</a> | <a
                                                    id="select-none-link" href="#">None</a>)
                                            </div>
                                        </div>
                                        <div class="sub-value">
                                            <select multiple="multiple" id="system-select" name="systemId">
                                                <c:forEach items="${systemList}" var="system">
                                                    <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}>
                                                        <c:out value="${system.name}"/></option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="status-select">Status</label>
                                </div>
                                <div class="li-value">
                                    <select id="status-select">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${statusList}" var="status">
                                            <option value="${status.statusId}"><c:out value="${status.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label class="required-field" for="comment">Comment</label>
                                </div>
                                <div class="li-value">
                                    <textarea id="comment"></textarea>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                </div>
                <input id="submit-button" type="submit" style="margin-top: 1em;" value="Signoff"/>
            </form>
            <div id="bulk-div"></div>
        </section>
    </jsp:body>
</t:setup-page>