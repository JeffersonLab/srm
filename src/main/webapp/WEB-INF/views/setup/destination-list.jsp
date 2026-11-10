<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
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
            <s:filter-flyout-widget>
                <form class="filter-form" method="get"
                      action="${pageContext.request.contextPath}/setup/destination-list">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    <label for="facility">Facility</label>
                                </div>
                                <div class="li-value">
                                    <select id="facility" name="facility">
                                        <option value="">&nbsp;</option>
                                        <c:forEach items="${facilityList}" var="facility">
                                            <option value="${facility}"${facility eq selectedFacility ? ' selected="selected"' : ''}>
                                                <c:out value="${facility}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </li>
                        </ul>
                    </fieldset>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box">
                <c:choose>
                    <c:when test="${selectedFacility ne null}">
                        Facility &quot;<c:out value="${selectedFacility}"/>&quot;
                    </c:when>
                    <c:otherwise>
                        All Facilities
                    </c:otherwise>
                </c:choose>
                {${fn:length(destinationList)}}
            </div>
            <form id="target-form" method="post" action="destination-list">
                <input type="hidden" name="facility" value="${selectedFacility}"/>
                <table class="data-table stripped-table">
                    <thead>
                    <tr>
                        <th>Order</th>
                        <th>Name</th>
                        <th>Facility</th>
                        <th>Target</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${destinationList}" var="destination" varStatus="iterator">
                        <tr>
                            <td><c:out value="${iterator.count}"/></td>
                            <td><c:out value="${destination.name}"/></td>
                            <td><c:out value="${destination.facility}"/></td>
                            <td>
                                <input type="checkbox" name="target" class="target-radio"
                                       value="${destination.beamDestinationId}"${destination.target ? ' checked="checked"' : ''}/>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="3"></td>
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
