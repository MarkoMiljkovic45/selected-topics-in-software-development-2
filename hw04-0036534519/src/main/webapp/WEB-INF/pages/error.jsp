<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<html>
  <head>
    <title>Error</title>
  </head>
  <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
    <h2>An error occurred:</h2>
    <p>${err}</p>
  </body>
</html>
