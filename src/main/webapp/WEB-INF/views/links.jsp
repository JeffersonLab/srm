<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Links"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <c:if test="${not empty generalLinks}">
                <h3>General</h3>
                <ul>
                    <c:forEach items="${generalLinks}" var="link">
                        <li>
                            <a href="${fn:escapeXml(link.url)}"><c:out value="${link.label}"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${not empty sketchLinks}">
                <h3>Beamline Sketches</h3>
                <ul>
                    <c:forEach items="${sketchLinks}" var="link">
                        <li>
                            <a href="${fn:escapeXml(link.url)}"><c:out value="${link.label}"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${empty generalLinks and empty sketchLinks}">
                <p>No links. Admins add them in the LINKS_GENERAL_CSV and LINKS_SKETCH_CSV settings on
                    the Setup tab.</p>
            </c:if>
        </section>
    </jsp:body>
</s:page>
