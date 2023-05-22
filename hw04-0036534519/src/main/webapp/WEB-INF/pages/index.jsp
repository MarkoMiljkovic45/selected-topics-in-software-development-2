<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Home</title>
    </head>
    <body>
        <jsp:useBean id="polls" scope="request" type="java.util.List"/>
        <c:forEach var="poll" items="${polls}">
            <a href="${pageContext.request.contextPath}/servleti/glasanje?pollID=${poll.id}">
                <h3>${poll.title}</h3>
            </a>

            <p>
                ${poll.message}
            </p>
        </c:forEach>
    </body>
</html>
