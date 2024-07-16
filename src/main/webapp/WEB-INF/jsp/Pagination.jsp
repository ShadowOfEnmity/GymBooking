<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <c:if test="${totalItems > itemsPerPage}">
    <div class="tableFootStyle">
      <div class="links">

        <c:choose>
          <c:when test="${currentPage > 1}">
            <a href="?page=${currentPage - 1}">&laquo;</a>
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
              <a href="${pageContext.request.contextPath}?page=${page}">${page}</a>
            </c:otherwise>
          </c:choose>
        </c:forEach>

        <c:choose>
          <c:when test="${currentPage < ((totalItems / itemsPerPage))}">
            <a href="?page=${currentPage + 1}">&raquo;</a>
          </c:when>
          <c:otherwise>
            <span>&raquo;</span>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:if>