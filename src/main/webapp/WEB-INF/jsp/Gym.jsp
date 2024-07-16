<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Gym: ${(empty requestScope.gym)?"new":requestScope.gym.name}</title>
    <style>
        <%@include file="/WEB-INF/css/userCard.css" %>
    </style>
</head>
<body>
<div class="container">
    <h2> Enter gym information</h2>
    <form action="${pageContext.request.contextPath}/gym" method="post">
        <c:if test="${not empty gym &&  not empty gym.id}">
            <input hidden="hidden" name="id" value="${gym.id}">
        </c:if>
        <div class="form-group">
            <label for="_id">Id:</label>
            <input type="text" id="_id" name="_id" value="${gym.id}" disabled required>
        </div>
        <div class="form-group">
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" value="${gym.name}" required>
            <c:if test="${not empty errors && errors.get('name') != null}">
                <span style="color: red">${errors.get('name')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="address">Address:</label>
            <input type="text" id="address" name="address" value="${gym.address}" required>
            <c:if test="${not empty errors && errors.get('address') != null}">
                <span style="color: red">${errors.get('address')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <input type="text" id="description" name="description" value="${gym.description}" required>
        </div>
        <div class="form-group">
            <label for="latitude">Latitude:</label>
            <input type="number" id="latitude" name="latitude" placeholder="-90.000000" size="10" step="0.000001" value="${gym.latitude}" required>
            <c:if test="${not empty errors && errors.get('latitude') != null}">
                <span style="color: red">${errors.get('latitude')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="longitude">Longitude:</label>
            <input type="number" id="longitude" name="longitude"  placeholder="-180.000000" size="11" step="0.000001" value="${gym.longitude}" required>
            <c:if test="${not empty errors && errors.get('longitude') != null}">
                <span style="color: red">${errors.get('longitude')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="phone">Phone:</label>
            <input type="text" id="phone" name="phone" value="${gym.phone}" required>
            <c:if test="${not empty errors && errors.get('phone') != null}">
                <span style="color: red">${errors.get('phone')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="website">Website:</label>
            <input type="url" id="website" name="website" value="${gym.website}" required>
            <c:if test="${not empty errors && errors.get('website') != null}">
                <span style="color: red">${errors.get('website')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <button type="submit">Submit</button>
        </div>
    </form>
</div>
</body>
</html>
