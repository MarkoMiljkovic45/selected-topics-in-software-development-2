<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<html>
    <head>
        <title>Home</title>
    </head>
    <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
        <div>
            <a href="colors.jsp">Background color chooser</a>
        </div>

        <div>
            <a href="${pageContext.request.contextPath}/trigonometric?a=0&b=90">Trigonometric</a>
        </div>

        <div>
            <form action="trigonometric" method="GET">
                Početni kut:<br><input type="number" name="a" min="0" max="360" step="1" value="0"><br>
                Završni kut:<br><input type="number" name="b" min="0" max="360" step="1" value="360"><br>
                <input type="submit" value="Tabeliraj"><input type="reset" value="Reset">
            </form>
        </div>

        <div>
            <a href="stories/funny.jsp">Funny story</a>
        </div>

        <div>
            <a href="report.jsp">OS usage report</a>
        </div>

        <div>
            <a href="${pageContext.request.contextPath}/powers?a=1&b=100&n=3">Generate xls</a>
        </div>
    </body>
</html>
