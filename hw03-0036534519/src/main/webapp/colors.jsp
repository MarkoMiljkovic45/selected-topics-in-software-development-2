<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<html>
    <head>
        <title>Colors</title>
    </head>
    <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
        <a style="color: white" href="${pageContext.request.contextPath}/setcolor?pickedBgCol=white">WHITE</a>
        <a style="color: red"   href="${pageContext.request.contextPath}/setcolor?pickedBgCol=red">RED</a>
        <a style="color: green" href="${pageContext.request.contextPath}/setcolor?pickedBgCol=green">GREEN</a>
        <a style="color: cyan"  href="${pageContext.request.contextPath}/setcolor?pickedBgCol=cyan">CYAN</a>
    </body>
</html>