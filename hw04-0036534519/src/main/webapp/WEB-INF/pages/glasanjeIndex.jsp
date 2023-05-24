<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Glasanje</title>
    </head>

    <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
        <h1>${pageContext.request.getAttribute("pollTitle")}</h1>
        <p>${pageContext.request.getAttribute("pollMessage")}</p>
        <ol>
            <c:forEach var="option" items="${options}">
                <li><a href="${pageContext.request.contextPath}/servleti/glasanje-glasaj?id=${option.id}&pollID=${pageContext.request.getAttribute("pollID")}">
                        ${option.optionTitle}
                </a></li>
            </c:forEach>
        </ol>
    </body>
</html>
