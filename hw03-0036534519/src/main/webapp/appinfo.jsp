<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%!
  private String getUptime() {
    long startTime = (Long) getServletConfig().getServletContext().getAttribute("startTime");
    long durationInMillis = System.currentTimeMillis() - startTime;

    long millis = durationInMillis % 1000;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour   = (durationInMillis / (1000 * 60 * 60)) % 24;
    long days   = (durationInMillis / (1000 * 60 * 60 * 24));

    return String.format("%02d days %02d hours %02d minutes %02d seconds %d milliseconds", days, hour, minute, second, millis);
  }
%>
<html>
  <head>
    <title>App Info</title>
  </head>
  <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
    <h2>The app has been running:</h2>

    <p>
      <%= getUptime() %>
    </p>
  </body>
</html>
