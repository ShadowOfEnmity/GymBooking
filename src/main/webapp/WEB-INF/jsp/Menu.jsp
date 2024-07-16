<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<ul class="horizontal-menu">
  <li><a href="${pageContext.request.contextPath}/home">Main</a></li>
  <li><a href="${pageContext.request.contextPath}/training-session">Create training</a></li>
  <c:if test="${sessionScope.user.role eq 'ADMIN'}">
  <li><a href="${pageContext.request.contextPath}/gym">Create gym</a></li>
  </c:if>
  <li><a href="${pageContext.request.contextPath}/gyms">Gyms</a></li>
  <li><a href="${pageContext.request.contextPath}/user">Edit user</a></li>
  <li><a href="${pageContext.request.contextPath}/training-sessions">Training-sessions</a></li>
  <li><a href="${pageContext.request.contextPath}/bookings">Bookings</a></li>
  <li><a href="${pageContext.request.contextPath}/new-booking">Booking</a></li>
  <li><a href="${pageContext.request.contextPath}/logout">Log out</a></li>
</ul>
