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
            background-color: #327cad;
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

        .form-container button{
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

        .container {
            display: grid;
            grid-template-columns: 1fr 1fr; /* Две колонки с равной шириной */
            gap: 1px; /* Пространство между блоками */
        }

        .block {
            box-sizing: border-box; /* Учитываем padding и border в ширине блока */
            padding: 1px;
            /*border: 1px solid #ccc;*/
        }

        .submit-button {
            background-color: #327cad; /* голубой цвет кнопки отправки данных формы */
            color: #FFFFFF; /* белый цвет текста кнопки отправки данных формы */
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 5px;
        }
        .add-submit-block button{
            background-color: #5592bb;
            color: #FFFFFF;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
        }

        .add-submit-block button:hover {
            background-color: #327cad;
        }

        .outerTableFooter {
            border-top: none;
        }

        .outerTableFooter .tableFootStyle {
            padding: 3px 5px;
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
        <div>Gym</div>
        <div>Image URL</div>
        <div>Actions</div>
    </div>

    <c:forEach items="${photos}" var="photo">
        <div class="table-row">
            <form action="${pageContext.request.contextPath}/photo" method="POST">
                <div class="form-container">
                    <div><input type="hidden" name="gymId" value="${photo.gym.id}"></div>
                    <div><input type="hidden" name="photoId" value="${photo.id}"></div>
<%--                    <div><input type="hidden" name="photoId" value="${photo.alt}"></div>--%>
                    <div><input type="text" name="id" disabled value="${photo.id}"></div>
                    <div><input type="text" name="gym" disabled value="${photo.gym.name}"></div>
                    <div><input type="text" name="image" disabled value="${photo.imageUrl}"></div>
                    <div class="container">
                        <div class="block">
                            <button type="submit" name="delete" value="delete">delete</button>
                        </div>
                        <div class="block">
                                <%--                            <input type="hidden" name="buttonOpen" id="buttonValue" value="${photo.id}">--%>
                            <button type="submit" name="open" value="open">Open</button>
                        </div>
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
    <div class="add-submit-block" style="margin-top: 10px">
        <form action="${pageContext.request.contextPath}/photo" method="POST">
            <input hidden="hidden" name="gymId" value="${gymId}">
            <button type="submit" class="submit-button" value="add">Add new photo</button>
        </form>
    </div>
</div>

<%--<form action="/" method="POST"><input type="submit" class="submit-button" value="Отправить данные"></form>--%>
</body>
</html>
