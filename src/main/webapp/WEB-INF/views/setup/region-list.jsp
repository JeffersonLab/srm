<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Region List"/>
<t:setup-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            /*var jlab = jlab || {};
            jlab.editableRowTable = jlab.editableRowTable || {};
            jlab.editableRowTable.entity = 'Region';
            jlab.editableRowTable.width = 400;
            jlab.editableRowTable.height = 300;
            $(document).on("table-row-add", function() {
                alert('add row');
            });
            $(document).on("table-row-edit", function() {
                alert('edit row');
            });*/
        </script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <table class="data-table stripped-table">
                <thead>
                <tr>
                    <th>Order</th>
                    <th>Name</th>
                    <th>Alias</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${regionList}" var="region" varStatus="iterator">
                    <tr>
                        <td><c:out value="${iterator.count}"/></td>
                        <td><c:out value="${region.name}"/></td>
                        <td><c:out value="${region.alias}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
        <s:editable-row-table-dialog>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label for="row-name">Name</label>
                    </div>
                    <div class="li-value">
                        <input type="text" id="row-name"/>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="row-alias">Alias</label>
                    </div>
                    <div class="li-value">
                        <input type="text" id="row-alias"/>
                    </div>
                </li>
            </ul>
        </s:editable-row-table-dialog>
    </jsp:body>
</t:setup-page>