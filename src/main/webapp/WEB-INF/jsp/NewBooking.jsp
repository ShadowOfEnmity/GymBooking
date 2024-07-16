<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Create booking</title>
    <style>
        <%@include file="/WEB-INF/css/menu.css" %>
        body {
            font-family: Arial, sans-serif;
            background-color: #327cad;
            color: #FFFFFF;
        }

        .container {
            width: 50%;
            margin: 0 auto;
            padding: 20px;
            background-color: #1C6EA4;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #5592bb;
        }

        th {
            background-color: #5592bb;
            color: #FFFFFF;
        }

        td {
            background-color: #FFFFFF;
            color: #1C6EA4;
        }

        select {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            margin-bottom: 10px;
        }

        button {
            background-color: #5592bb;
            color: #FFFFFF;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
        }

        button:hover {
            background-color: #327cad;
        }

        .tableFootStyle {
            font-size: 14px;
        }

        .tableFootStyle .links {
            text-align: right;
        }

        .tableFootStyle .links a, span {
            display: inline-block;
            background: #5592bb;
            color: #FFFFFF;
            padding: 2px 8px;
            border-radius: 5px;
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
<div class="container">
    <h2>Select training sessions</h2>
    <div class="content">
        <%@include file="Menu.jsp" %>
    </div>
    <form action="${pageContext.request.contextPath}/new-booking?userId=${requestScope.get("userId")}" id="form"
          method="post">
        <input type="hidden" name="checkboxState"
               value='${not empty requestScope.checkboxState ? requestScope.checkboxState: '{}'}'>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <%--          <th>Название</th>--%>
                <th>Presentation</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="session" items="${sessions}">
                <tr>
                    <td><c:out value="${session.id}"/></td>
                    <td><c:out value="${session.presentation}"/></td>
                    <td><input multiple type="checkbox" name="selectedSessions"
                               <c:if test="${requestScope.checkboxStateMap[session.id]}">checked</c:if>
                               value="<c:out value='${session.id}'/>">
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="outerTableFooter">
            <c:if test="${totalItems > itemsPerPage}">
                <div class="tableFootStyle">
                    <div class="links">

                        <c:choose>
                            <c:when test="${currentPage > 1}">
                                <a href="?page=${currentPage - 1}" onclick="submitCheckedList(this.href); return false;">&laquo;</a>
                            </c:when>
                            <c:otherwise>
                                <span>&laquo;</span>
                            </c:otherwise>
                        </c:choose>

                        <c:forEach var="page" begin="1" end="${(totalItems / itemsPerPage)+1}">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span>${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}?page=${page}" onclick="submitCheckedList(this.href); return false;">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${currentPage < ((totalItems / itemsPerPage))}">
                                <a href="?page=${currentPage + 1}" onclick="submitCheckedList(this.href); return false;">&raquo;</a>
                            </c:when>
                            <c:otherwise>
                                <span>&raquo;</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
        </div>
        <button type="submit">Submit</button>
    </form>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Получаем скрытый input, содержащий JSON-строку с состоянием чекбоксов
        let checkboxStateInput = document.querySelector('input[name="checkboxState"]');

        // Получаем все чекбоксы на странице
        let checkboxes = document.querySelectorAll('input[type="checkbox"]');

        // Функция для обновления JSON-строки в атрибуте value скрытого input
        function updateCheckboxState() {
            // var currentState = {};
            let jsonObject = JSON.parse(checkboxStateInput.value);

            let map = new Map();

// Заполняем Map данными из объекта JavaScript
            for (let key in jsonObject) {
                if (jsonObject.hasOwnProperty(key)) {
                    map.set(key, jsonObject[key]);
                }
            }

            checkboxes.forEach(function (checkbox) {
                // currentState[checkbox.value] = checkbox.checked;
                // jsonObject.put(checkbox.value, checkbox.checked)
                map.set(checkbox.value, checkbox.checked)
            });

            let obj = {};
            for (let [key, value] of map) {
                obj[key] = value;
            }

            checkboxStateInput.value = JSON.stringify(obj);
        }

        // Добавляем обработчик события change для каждого чекбокса
        checkboxes.forEach(function (checkbox) {
            checkbox.addEventListener('change', updateCheckboxState);
        });

        // Вызываем функцию один раз при загрузке страницы, чтобы установить начальное состояние
        updateCheckboxState();
    });

    function submitCheckedList(parameter) {

        const inputField = document.querySelector('input[name="checkboxState"]');

        // Создаем массив для хранения выбранных значений
        let selectedValues = [];

        // const mapString = inputField.value;

        // if (mapString.length === 0) {
        // Парсим значение поля в формате JSON
        const mapString = inputField.value;


        const jsonObject = JSON.parse(mapString);

        const map = new Map();
        for (const [key, value] of Object.entries(jsonObject)) {
            map.set(key, value);
        }

        map.forEach((value, key) => {
            if (value) selectedValues.push(key);
        });


        // Преобразуем массив в строку для передачи в URL
        const selectedString = selectedValues.join(',');

        // Формируем URL с параметром, который содержит выбранные значения
        const url = parameter + '&selected=' + encodeURIComponent(selectedString);

        // Переходим по сформированному URL
        window.location.href = url;

    }
</script>
</body>
</html>
