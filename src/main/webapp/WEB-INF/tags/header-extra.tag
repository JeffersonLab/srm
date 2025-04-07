<%@tag description="Header Extra Tag" pageEncoding="UTF-8"%>
<form action="${pageContext.request.contextPath}/reports/component/detail">
    <input id="quick-component" class="component-autocomplete quick-autocomplete" data-application-id="1"
           type="text" name="name" placeholder="Component name"
           value="${'/reports/component/detail' eq currentPath ? param.name : ''}"/>
    <button type="submit" title="Search">→</button>
</form>