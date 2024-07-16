<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Training session</title>
    <style>
        <%@include file="/WEB-INF/css/userCard.css" %>
    </style>
</head>
<body>
<div class="container">
    <h2>Enter new training-session</h2>
    <form action="${pageContext.request.contextPath}/training-session" method="post">
        <c:if test="${not empty training &&  not empty training.id}">
            <input hidden="hidden" name="id" value="${training.id}">
        </c:if>
        <div class="form-group">
            <label for="gym">GYM:</label>
            <select id="gym" name="gym" required>
                <c:forEach var="gym" items="${requestScope.gyms}">
                    <option value="${gym.id}"
                            <c:if test="${not empty training.gym && training.gym.id eq gym.id}">selected</c:if>>${gym.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="trainer">Trainer:</label>
            <select id="trainer" name="trainer" required>
                <c:forEach var="trainer" items="${requestScope.trainers}">
                    <option value="${trainer.id}"
                            <c:if test="${not empty training.trainer && training.trainer.id eq trainer.id}">selected</c:if>>${trainer.user.fullName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="type">Type:</label>
            <input type="text" id="type" name="type" value="${training.type}" required>
        </div>
        <div class="form-group">
            <label for="description">Description:</label>

            <textarea type="text" id="description" name="description" style="width: 100%; height: 100%;"
                      required>${training.description}</textarea>
        </div>
        <div class="form-group">
            <label for="date">Date:</label>
            <input type="date" id="date" name="date" placeholder="yyyy-MM-dd" value="${training.date}" required>
        </div>
        <div class="form-group">
            <label for="startTime">Start time:</label>
            <input type="time" id="startTime" name="startTime" placeholder="HH:mm" value="${training.startTime}"
                   required>
        </div>
        <div class="form-group">
            <label for="duration">Duration (hour):</label>
            <input type="number" id="duration" name="duration" placeholder="0" value="${training.duration}" required>
            <c:if test="${not empty errors && errors.get('duration') != null}">
                <span style="color: red">${errors.get('duration')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="price">Price:</label>
            <input type="number" id="price" name="price" placeholder="0.00$" value="${training.price}" min="0" size="5"
                   step="0.01" required>
        </div>
        <div class="form-group">
            <label for="capacity">Capacity:</label>
            <input type="number" id="capacity" name="capacity" placeholder="0" value="${training.capacity}" required>
            <c:if test="${not empty errors && errors.get('capacity') != null}">
                <span style="color: red">${errors.get('capacity')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <button type="submit">Submit</button>
        </div>
    </form>
</div>
</body>
</html>
