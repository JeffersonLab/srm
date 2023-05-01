<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="hco" uri="http://jlab.org/srm/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Group Leader"/>
<t:reports-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <div>
                <c:forEach items="${groupList}" var="group">
                    <h3><c:out value="${group.name}"/></h3>
                    <c:choose>
                        <c:when test="${fn:length(group.leaders) > 0}">
                            <table class="data-table stripped-table">
                                <thead>
                                <tr>
                                    <th>Lastname</th>
                                    <th>Firstname</th>
                                    <th>Username</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${group.leaders}" var="leader">
                                    <tr>
                                        <td><c:out value="${leader.lastname}"/></td>
                                        <td><c:out value="${leader.firstname}"/></td>
                                        <td><c:out value="${leader.username}"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div>No group leaders</div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </section>
    </jsp:body>
</t:reports-page>