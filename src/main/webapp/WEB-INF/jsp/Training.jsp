<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Training-sessions</title>
    <style>
        <%@include file="/WEB-INF/css/table.css" %>
        <%@include file="/WEB-INF/css/menu.css" %>
    </style>
</head>
<body>
<%@include file="Menu.jsp" %>
<div class="divTable blueTable">
    <div class="divTableHeading">
        <div class="divTableRow">
            <div class="divTableHead">#Id</div>
            <div class="divTableHead">Gym</div>
            <%--            <c:if test="${not sessionScope.user.role eq 'Trainer'}">--%>
            <div class="divTableHead">Trainer</div>
            <%--            </c:if>--%>
            <div class="divTableHead">Type</div>
            <div class="divTableHead">Description</div>
            <div class="divTableHead">Date</div>
            <div class="divTableHead">Start time</div>
            <div class="divTableHead">Duration</div>
            <div class="divTableHead">Price</div>
            <div class="divTableHead">Capacity</div>
            <div class="divTableHead">Edit</div>
        </div>
    </div>
    <div class="divTableBody">
        <c:forEach var="item" items="${items}">
            <div class="divTableRow">
                <div class="divTableCell">${item.id}</div>

                <div class="divTableCell"><a href="${pageContext.request.contextPath}/gym?id=${item.gym.id}">${item.gym.presentation}</a></div>

                    <%--                <c:if test="${not sessionScope.user.role eq 'Trainer'}">--%>
                <div class="divTableCell"><a
                        href="${pageContext.request.contextPath}/user?userId=${item.trainer.id}">${item.trainer.presentation}</a>
                </div>
                    <%--                </c:if>--%>
                <div class="divTableCell">${item.type}</div>
                <div class="divTableCell">${item.description}</div>
                <div class="divTableCell">${item.date}</div>
                <div class="divTableCell">${item.startTime}</div>
                <div class="divTableCell">${item.duration}</div>
                <div class="divTableCell">${item.price}</div>
                <div class="divTableCell">${item.capacity}</div>
                <div class="divTableHead"><a href="${pageContext.request.contextPath}/training-session?id=${item.id}">Edit</a></div>
            </div>
        </c:forEach>
    </div>
</div>
<div class="blueTable outerTableFooter">
    <%@include file="Pagination.jsp" %>
</div>
</body>
</html>
