<%@ page import="hr.fer.oprpp2.jmbag0036534519.model.PollOption" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%!
    private List<PollOption> getBestOptions(Object obj) {
        if(!(obj instanceof List)) {
            return new ArrayList<>();
        }

        List<PollOption> options = (List<PollOption>) obj;

        OptionalLong maxVotesCount = options.stream().mapToLong(PollOption::getVotesCount).max();

        if (maxVotesCount.isEmpty()) {
            return new ArrayList<>();
        }

        return options.stream().filter(option -> option.getVotesCount() == maxVotesCount.getAsLong()).toList();
    }
%>
<html>
    <head>
        <title>Rezultati Glasanja</title>
    </head>
    <body style="background-color: ${pickedBgCol == null ? "white" : pickedBgCol}">
        <h1>Rezultati glasanja</h1>
        <p>Ovo su rezultati glasanja.</p>
        <table border="1" cellspacing="0" class="rez">
            <thead><tr><th>Opcija</th><th>Broj glasova</th></tr></thead>
            <tbody>
            <c:forEach var="option" items="${options}">
                <tr><td>${option.optionTitle}</td><td>${option.votesCount}</td></tr>
            </c:forEach>
            </tbody>
        </table>

        <h2>Grafički prikaz rezultata</h2>
        <img alt="Pie-chart" src="${pageContext.request.contextPath}/servleti/glasanje-grafika?pollID=${pageContext.request.getAttribute("pollID")}" width="400" height="400" />

        <h2>Rezultati u XLS formatu</h2>
        <p>Rezultati u XLS formatu dostupni su <a href="${pageContext.request.contextPath}/servleti/glasanje-xls?pollID=${pageContext.request.getAttribute("pollID")}">ovdje</a></p>

        <h2>Razno</h2>
        <p>Primjeri pobjedničkih opcija:</p>
        <ul>
            <% for (PollOption option: getBestOptions(request.getAttribute("options"))) { %>
                <li><a href="<%= option.getOptionLink() %>" target="_blank"><%= option.getOptionTitle() %></a></li>
            <% } %>
        </ul>
    </body>
</html>
