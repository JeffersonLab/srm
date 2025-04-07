<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Beam Destination List"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            $(document).on("click", "#clear-target-button", function () {
                $('.target-radio').prop('checked', false);
                $("#target-form").submit();
            });
            $(document).on("change", ".target-radio", function () {
                $("#target-form").submit();
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <form id="target-form" method="post" action="destination-list">
                <table class="data-table stripped-table">
                    <thead>
                    <tr>
                        <th>Order</th>
                        <th>Name</th>
                        <th>Target</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${destinationList}" var="destination" varStatus="iterator">
                        <tr>
                            <td><c:out value="${iterator.count}"/></td>
                            <td><c:out value="${destination.name}"/></td>
                            <td>
                                <input type="checkbox" name="target" class="target-radio"
                                       value="${destination.beamDestinationId}"${destination.target ? ' checked="checked"' : ''}/>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="2"></td>
                        <td>
                            <button type="button" id="clear-target-button">No Target</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </section>
    </jsp:body>
</s:setup-page>
