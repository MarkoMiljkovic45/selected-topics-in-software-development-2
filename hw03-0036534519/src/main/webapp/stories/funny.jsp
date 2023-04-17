<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%!
  private String getRandomColor() {
    Random rand = new Random();
    String[] colors = {"blue", "yellow", "pink"};

    return colors[rand.nextInt(colors.length)];
  }
%>
<html>
  <head>
    <title>Funny</title>
  </head>
  <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
    <p style="color: <%= getRandomColor() %>">
      Kaže zeko zmiji:<br/>
      - "Izvini što sam te neki dan zezao sto nemaš noge."<br/>
      Na to će zmija:<br/>
      - "Ma nema veze, bilo pa prošlo."<br/>
      Na to će zeko:<br/>
      - "E svaka ti čast! Evo ruka!"
    </p>
  </body>
</html>
