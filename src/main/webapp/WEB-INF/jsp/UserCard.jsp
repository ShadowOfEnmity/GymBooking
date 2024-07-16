<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>User: ${user.fullName}</title>
    <style>
        <%@include file="/WEB-INF/css/userCard.css" %>
    </style>
</head>
<body>
<div class="container">
    <h2>Enter user data</h2>
    <form action="${pageContext.request.contextPath}/user" method="post">
        <input hidden="hidden" name="userId" value="${requestScope.user.id}">
        <div class="form-group">
            <label for="login">Login</label>
            <input type="text" id="login" name="login" value="${requestScope.user.login}" placeholder="login"
                   required>
            <c:if test="${not empty errors && errors.get('login') != null}">
                <span style="color: red">${errors.get('login')}</span>
            </c:if>

        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="text" id="password" name="password" placeholder="password" required>
            <c:if test="${not empty errors && errors.get('Password') != null}">
                <span style="color: red">${errors.get('Password')}</span>
            </c:if>
        </div>
        <%--        <c:if test="${not empty user}">--%>
        <%--            <div class="form-group">--%>
        <%--                <label for="oldPassword">Old password</label>--%>
        <%--                <input type="text" id="oldPassword" name="oldPassword" required>--%>
        <%--            </div>--%>
        <%--        </c:if>--%>

        <c:if test="${requestScope.role.isPresent() && requestScope.role.get().name() eq 'TRAINER'}">
            <div id="trainer">
                <div class="form-group">
                    <label for="experience">Experience</label>
                    <c:choose>
                        <c:when test="${not empty requestScope.trainer}">
                            <input type="text" id="experience" name="experience"
                                   value="${requestScope.trainer.experience}" placeholder="experience" required>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="experience" name="experience" placeholder="experience" required>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="form-group">
                    <label for="specialization">Specialization</label>
                    <c:choose>
                        <c:when test="${not empty requestScope.trainer}">
                            <input type="text" id="specialization" name="specialization"
                                   value="${requestScope.trainer.specialization}" placeholder="specialization"
                                   required>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="specialization" name="specialization"
                                   placeholder="specialization"
                                   required>
                        </c:otherwise>
                    </c:choose>
                </div>

                    <%--                <div class="form-group">--%>
                    <%--                    <label for="rating">Rating</label>--%>
                    <%--                    <input type="number" id="rating" name="rating" required>--%>
                    <%--                </div>--%>


                <div class="form-group">
                    <label for="availability">Availability</label>
                    <input type="checkbox" id="availability" name="availability"
                           <c:if test="${requestScope.trainer.availability}">checked</c:if>>

                </div>
            </div>
        </c:if>
        <div class="form-group">
            <label for="role">Role:</label>
            <select id="role" name="role" required>
                <c:forEach var="item" items="${requestScope.roles}">
                    <option value="${item.name()}"
                            <c:if test="${requestScope.role.isPresent() && requestScope.role.get().name() eq item.name()}">selected</c:if> >${item.name()}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="firstName">Name</label>
            <input type="text" id="firstName" name="firstName" value="${requestScope.user.firstName}"
                   placeholder="first name" required>
            <c:if test="${not empty errors && errors.get('firstName') != null}">
                <span style="color: red">${errors.get('firstName')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name</label>
            <input type="text" id="lastName" name="lastName" value="${requestScope.user.lastName}"
                   placeholder="last name" required>
            <c:if test="${not empty errors && errors.get('lastName') != null}">
                <span style="color: red">${errors.get('lastName')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" value="${requestScope.user.phone}"
                   placeholder="+0(000) 000-00-00" required>
            <c:if test="${not empty errors && errors.get('phone') != null}">
                <span style="color: red">${errors.get('phone')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${requestScope.user.email}"
                   placeholder="example@gmail.com" required>
            <c:if test="${not empty errors && errors.get('email') != null}">
                <span style="color: red">${errors.get('email')}</span>
            </c:if>
        </div>
        <div class="form-group">
            <button type="submit">Submit</button>
        </div>

    </form>
</div>
<script>
    document.querySelector('select[name="role"]').addEventListener("change", function () {
        let selectedOption = this.value;
        let url = "${pageContext.request.contextPath}/user?role=" + encodeURIComponent(selectedOption);
        window.location.href = url;
    });
</script>
</body>
</html>
