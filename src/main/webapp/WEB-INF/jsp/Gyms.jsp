<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Gyms</title>
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
            <div class="divTableHead">Name</div>
            <div class="divTableHead">Address</div>
            <div class="divTableHead">Description</div>
            <div class="divTableHead">Latitude</div>
            <div class="divTableHead">Longitude</div>
            <div class="divTableHead">Phone</div>
            <div class="divTableHead">Website</div>
            <div class="divTableHead">Gym</div>
            <div class="divTableHead">Training</div>
            <div class="divTableHead">Photos</div>
        </div>
    </div>
    <div class="divTableBody">
        <c:forEach var="item" items="${items}">
            <div class="divTableRow">
                <div class="divTableCell">${item.id}</div>
                <div class="divTableCell">${item.name}</div>
                <div class="divTableCell">${item.address}</div>
                <div class="divTableCell">${item.description}</div>
                <div class="divTableCell">${item.latitude}</div>
                <div class="divTableCell">${item.longitude}</div>
                <div class="divTableCell">${item.phone}</div>
                <div class="divTableCell">${item.website}</div>
                <div class="divTableCell"><a href="${pageContext.request.contextPath}/gym?id=${item.id}">edit</a></div>
                <div class="divTableCell"><a href="${pageContext.request.contextPath}/training-sessions?gymId=${item.id}">view</a></div>
                <div class="divTableCell"><a href="${pageContext.request.contextPath}/photos?gymId=${item.id}">view</a></div>
            </div>
        </c:forEach>
    </div>
</div>
<div class="blueTable outerTableFooter">
    <%@include file="Pagination.jsp" %>
</div>
</body>
</html>
