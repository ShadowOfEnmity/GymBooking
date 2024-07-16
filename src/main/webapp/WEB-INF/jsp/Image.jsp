<%@ page import="java.net.URLDecoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${requestScope.alt}</title>
    <style>
        body {
            background-color: #327cad; /* Цвет фона */
            font-family: Arial, sans-serif; /* Шрифт текста */
            color: #FFFFFF; /* Цвет текста */
            margin: 0;
            padding: 0;
        }
        .container {
            width: 100%;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            text-align: center;
        }
        img {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 0 auto;
            border-radius: 8px; /* Закругление углов изображения */
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Тень под изображением */
        }
    </style>
</head>
<body>
<div class="container">
    <img src="${pageContext.request.contextPath}/image?photoId=${requestScope.photoId}" alt="${requestScope.alt}"/>
</div>
</body>
</html>
