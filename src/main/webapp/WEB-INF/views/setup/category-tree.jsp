<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Category/System Tree"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${cdnContextPath}/jquery-plugins/jstree/3.3.8/themes/classic/style.min.css"/>
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/readiness.css"/>
        <style type="text/css">
            .category-list {
                list-style-image: url("../resources/img/category.png");
            }

            .system-list {
                list-style-image: url("../resources/img/system.png");
            }

            #select-node-fieldset,
            #modify-category-fieldset {
                margin-bottom: 1em;
            }

            li[data-node-type='SYSTEM'] > a.jstree-anchor {
                color: blue;
                cursor: pointer;
            }

            li[data-node-type='SYSTEM'] > a:hover {
                color: red;
            }

            #open-edit-root-dialog-button {
                float: right;
            }
        </style>          
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${cdnContextPath}/jquery-plugins/jstree/3.3.8/jstree.min.js"></script>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/category-tree.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <div>
                <fieldset id="modify-category-fieldset">
                    <legend>Category/System</legend>
                    <button type="button" id="open-add-category-dialog-button">Add...</button>
                    <button type="button" id="open-edit-category-dialog-button">Edit...</button>
                    <button type="button" id="open-remove-category-dialog-button">Remove...</button>
                    <c:if test="${pageContext.request.isUserInRole('srm-admin')}">
                        <button type="button" id="open-edit-root-dialog-button">Root...</button>
                    </c:if>
                </fieldset>
                <fieldset>
                    <legend>Subsystem</legend>
                    <button type="button" id="open-add-system-dialog-button">Add...</button>
                    <button type="button" id="open-edit-system-dialog-button">Edit...</button>
                    <button type="button" id="open-remove-system-dialog-button">Remove...</button>
                </fieldset>
            </div>
            <div id="tree-widget">
                <div id="tree-nodes">
                    <div id="tree">
                        <ul class="category-list">
                            <c:set var="parent" value="${root}" scope="request"/>
                            <jsp:include page="/WEB-INF/includes/category-tree-node.jsp"/>
                        </ul>
                    </div>
                </div>
                <div id="tree-keys">
                    <fieldset>
                        <legend>Node Key</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <span class="small-icon CATEGORY"></span>
                                </div>
                                <div class="li-value">
                                    Category/System
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <span class="small-icon SYSTEM"></span>
                                </div>
                                <div class="li-value">
                                    Subsystem
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                </div>
            </div>
            <div id="node-dialog" class="dialog" title="Node Dialog">
                <form>
                    <fieldset id="select-node-fieldset">
                        <legend>Choose Target:</legend>
                        <ul class="key-value-list">
                            <li id="category-node-select">
                                <div class="li-key">
                                    <label for="category">Category</label>
                                </div>
                                <div class="li-value">
                                    <select id="category" name="category">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${root.children}" var="child">
                                            <t:hierarchical-select-option node="${child}" level="0"
                                                                          parameterName="categoryId"/>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li id="system-node-select">
                                <div class="li-key">
                                    <label for="system">Subsystem</label>
                                </div>
                                <div class="li-value">
                                    <select id="system" name="system">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${systemList}" var="system">
                                            <option value="${system.systemId}"${param.systemId eq system.systemId ? ' selected="selected"' : ''}
                                                    data-category="${system.category.name}"><c:out
                                                    value="${system.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <fieldset id="new-value-fieldset">
                        <legend>New Values:</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="category-parent">Parent</label>
                                </div>
                                <div class="li-value">
                                    <select id="category-parent" name="category-parent">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${root.children}" var="child">
                                            <t:hierarchical-select-option node="${child}" level="0"
                                                                          parameterName="categoryId"/>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="node-name">Name</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" maxlength="128" id="node-name" name="node-name"/>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <div class="dialog-button-panel">
                        <button id="SaveButton" class="dialog-submit ajax-button" type="button">Save</button>
                        <button class="dialog-close-button" type="button">Cancel</button>
                    </div>
                </form>
            </div>
            <div id="root-dialog" class="dialog" title="Rename Root">
                <form>
                    <fieldset>
                        <legend>New Values:</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="root-name">Root Name</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" maxlength="128" id="root-name" name="node-name"/>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <div class="dialog-button-panel">
                        <button id="root-save-button" class="dialog-submit ajax-button" type="button">Save</button>
                        <button class="dialog-close-button" type="button">Cancel</button>
                    </div>
                </form>
            </div>
        </section>
    </jsp:body>
</t:setup-page>