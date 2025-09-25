<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Status List"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <table class="data-table stripped-table">
                <thead>
                <tr>
                    <th>Order</th>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${statusList}" var="status" varStatus="iterator">
                    <tr>
                        <td><c:out value="${iterator.count}"/></td>
                        <td><c:out value="${status.name}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </jsp:body>
</s:setup-page>