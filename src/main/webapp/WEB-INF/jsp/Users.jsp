<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Users</title>
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
            <div class="divTableHead">First name</div>
            <div class="divTableHead">Last name</div>
            <div class="divTableHead">Email</div>
            <div class="divTableHead">Phone</div>
            <div class="divTableHead">Role</div>
            <div class="divTableHead">User card</div>
            <div class="divTableHead">Bookings</div>
            <div class="divTableHead">New booking</div>
        </div>
    </div>
    <div class="divTableBody">
        <c:forEach var="item" items="${items}">
            <div class="divTableRow">
                <div class="divTableCell">${item.id}</div>
                <div class="divTableCell">${item.firstName}</div>
                <div class="divTableCell">${item.lastName}</div>
                <div class="divTableCell">${item.email}</div>
                <c:if test="${not empty requestScope['email']}">
                    <span style="color: red;">${requestScope['email']}</span><br/>
                </c:if>
                <div class="divTableCell">${item.phone}</div>
                <div class="divTableCell">${item.role}</div>
<%--                <c:choose>--%>
<%--                    <c:when test="${item.role == 'TRAINER'}">--%>
<%--                        <div class="divTableCell"><a--%>
<%--                                href="${pageContext.request.contextPath}/trainer?userId=${item.id}">edit</a></div>--%>
<%--                    </c:when>--%>
<%--                    <c:otherwise>--%>
                        <div class="divTableCell"><a href="${pageContext.request.contextPath}/user?userId=${item.id}">edit</a>
                        </div>
<%--                    </c:otherwise>--%>
<%--                </c:choose>--%>
                <div class="divTableCell"><a
                        href="${pageContext.request.contextPath}/bookings?userId=${item.id}">view</a>
                </div>
                <div class="divTableCell"><a
                        href="${pageContext.request.contextPath}/new-booking?userId=${item.id}">new</a>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<div class="blueTable outerTableFooter">
    <%@include file="Pagination.jsp" %>
</div>
</body>
</html>
