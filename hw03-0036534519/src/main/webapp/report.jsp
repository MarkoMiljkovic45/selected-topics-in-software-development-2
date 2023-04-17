<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<html>
  <head>
    <title>OS Usage</title>
  </head>
  <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
    <h1>OS usage</h1>

    <p>
      Here are the results of OS usage in survey that we completed.
    </p>

    <img src="${pageContext.request.contextPath}/reportImage" alt="OS-usage"/>
  </body>
</html>
