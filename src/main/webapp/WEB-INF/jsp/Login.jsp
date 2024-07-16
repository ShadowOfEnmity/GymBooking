<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
    <style>
        <%@include file="/WEB-INF/css/userCard.css" %>
        .outer-div {
            display: flex; /* Используем flexbox для расположения элементов в строку */
            /*border: 1px solid #000; !* Пример рамки для внешнего div (можно удалить) *!*/
            /*padding: 10px; !* Пример отступов (можно изменить) *!*/
        }

        /* Стили для внутренних div */
        /*.inner-div {*/
            /*width: 50%; !* Делим на два равные части *!*/
            /*border: 1px solid #f00; !* Пример рамки для внутренних div (можно удалить) *!*/
            /*padding: 10px; !* Пример отступов (можно изменить) *!*/
            /*box-sizing: border-box; !* Учитываем рамки и отступы в общую ширину *!*/
        /*}*/

        .block {
            width: 50%; /* каждый блок будет занимать половину ширины обрамляющего блока */
            display: inline-block; /* размещение блоков в один ряд */
            height: 100px; /* задаем высоту блоков */
            background-color: lightblue; /* цвет фона блоков */
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Authentication</h2>
    <form id="form" action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label for="login">Login</label>
            <input type="text" id="login" name="login" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div class="form-group">
            <c:if test="${requestScope.error}">
                <span style="color: red">"Incorrect login or password"</span>
            </c:if>
        </div>
        <div class="form-group" style="width: 100%">
                <div class="inner-div">
                    <button type="submit">Submit</button>
                </div>
                <div class="inner-div"><a href="${pageContext.request.contextPath}/user">Registration</a></div>
        </div>
    </form>
</div>
</body>
</html>
