<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
      <title>Trigonometric</title>
    </head>
    <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
        <p>Evo traženih rezultata.</p>

        <table border="1">
            <tr><th>Broj</th><th>Njegov sinus</th><th>Njegov kosinus</th></tr>
            <c:forEach var="trigonometric" items="${results}">
            <tr><td>${trigonometric.arg}</td><td>${trigonometric.sin}</td><td>${trigonometric.cos}</td></tr>
            </c:forEach>
        </table>
    </body>
</html>
