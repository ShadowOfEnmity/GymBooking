<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Bookings</title>
    <style>
        <%--        <%@include file="/WEB-INF/css/userCard.css" %>--%>
        <%@include file="/WEB-INF/css/menu.css" %>
        <%@include file="/WEB-INF/css/table.css" %>
        body {
            font-family: Arial, sans-serif;
            background-color: #5592bb;
        }

        .table-container {
            display: flex;
            flex-direction: column;
            width: 80%;
            margin: 20px auto;
        }

        .table-header {
            background-color: #327cad;
            color: #FFFFFF;
            padding: 10px;
            display: flex;
            justify-content: space-between;
        }

        .table-header h2 {
            margin: 0;
        }

        .table-header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            border-bottom: 1px solid #ddd;
            background-color: #1C6EA4;
            color: #FFFFFF;
        }

        .table-row {
            display: flex;
            justify-content: space-between;
            flex-direction: row;
            align-items: center;
            padding: 10px;
            border-bottom: 1px solid #ddd;
            background-color: #1C6EA4;
            color: #FFFFFF;
        }

        .table-row:nth-child(even) {
            background-color: #f2f2f2;
        }

        .form-container {
            display: flex;
            align-items: center;
            flex: content;
        }

        .form-container input {
            padding: 8px;
            margin-right: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .form-container select {
            padding: 8px;
            margin-right: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .form-container button {
            background-color: #5592bb;
            color: #FFFFFF;
            padding: 8px 12px;
            border: none;
            cursor: pointer;
            border-radius: 4px;
        }

        .form-container button:hover {
            background-color: #1C6EA4;
        }

        div.content {
            width: 100%; /* Ширина блока */
            margin: 1px auto; /* Отступы сверху и снизу, автоматическое выравнивание по центру */
            padding: 1px; /* Внутренние отступы внутри блока */
            background-color: #1C6EA4; /* Цвет фона блока */
            /*background-color: #f0f0f0; !* Цвет фона блока *!*/
            /*border: 1px solid #ccc; !* Граница блока *!*/
            border: 1px solid #1C6EA4; /* Граница блока */
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* Тень блока */
            box-sizing: border-box; /* Учитывать границы и внутренние отступы в общую ширину блока */
        }
    </style>
</head>
<body>
<div class="table-container">
    <div class="content">
        <%@include file="Menu.jsp" %>
    </div>
    <%--    <div class="table-header">--%>
    <%--        <h2>Заголовок таблицы</h2>--%>
    <%--    </div>--%>

    <div class="table-header-row">
        <div>#Id</div>
        <div>Booking date</div>
        <div>Status</div>
        <div>Payment status</div>
        <div>Actions</div>
    </div>

    <c:forEach items="${items}" var="item">
        <div class="table-row">
            <form action="${pageContext.request.contextPath}/bookings" method="POST">
                <div class="form-container">
                    <div><input type="text" name="id" hidden="hidden" value="${item.id}"></div>
                    <div><input type="text" name="bookId" disabled value="${item.id}"></div>
                    <div><input type="date" name="bookingDate" value="${item.bookingDate}"></div>
                    <div><select name="status">
                        <c:forEach items="${statuses}" var="status">
                            <option value="${status}"
                                    <c:if test="${item.status eq status}">selected</c:if>>${status}</option>
                        </c:forEach>
                    </select></div>
                    <div><select name="paymentStatus">
                        <c:forEach items="${paymentsStatuses}" var="paymentStatus">
                            <option value="${paymentStatus}"
                                    <c:if test="${item.paymentStatus eq paymentStatus}">selected</c:if>>${paymentStatus}</option>
                        </c:forEach>
                    </select></div>
                    <div>
                        <button type="submit">Edit</button>
                    </div>
                </div>
            </form>
        </div>
    </c:forEach>
    <div>
        <div class="blueTable outerTableFooter">
            <%@include file="Pagination.jsp" %>
        </div>
    </div>
</div>

<%--<form action="/" method="POST"><input type="submit" class="submit-button" value="Отправить данные"></form>--%>
</body>
</html>
