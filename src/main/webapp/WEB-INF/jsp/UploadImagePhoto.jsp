<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload Image</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h2 {
            color: #327cad;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        .form-group input[type="file"] {
            padding: 10px;
            width: 100%;
        }

        .form-group button {
            padding: 10px 20px;
            background-color: #327cad;
            color: #fff;
            border: none;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Upload Image</h2>
    <form action="${pageContext.request.contextPath}/photo/upload-photo" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <input name="gymId" value="${requestScope.gymId}" hidden="hidden">
            <label for="gymId">gymId:</label>
            <input type="number" id="gymId" name="gymId" value="${requestScope.gymId}" disabled>
            <br/>
            <label for="alt">alternative:</label>
            <input type="text" id="alt" name="alt">
            <br/>
            <label>Select Image:</label>
            <input type="file" name="image" required>
        </div>
        <div class="form-group">
            <button type="submit">Upload</button>
        </div>
    </form>
</div>
</body>
</html>