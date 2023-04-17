<%@ page import="hr.fer.oprpp2.servlets.voting.model.BandEntry" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.OptionalInt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%!
    private List<BandEntry> getBestBands(Object obj) {
        if(!(obj instanceof List)) {
            return new ArrayList<>();
        }

        List<BandEntry> bands = (List<BandEntry>) obj;

        OptionalInt maxVotesCount = bands.stream().mapToInt(BandEntry::getVoteCount).max();

        if (maxVotesCount.isEmpty()) {
            return new ArrayList<>();
        }

        return bands.stream().filter(band -> band.getVoteCount() == maxVotesCount.getAsInt()).toList();
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
            <thead><tr><th>Bend</th><th>Broj glasova</th></tr></thead>
            <tbody>
            <c:forEach var="band" items="${bands}">
                <tr><td>${band.name}</td><td>${band.voteCount}</td></tr>
            </c:forEach>
            </tbody>
        </table>

        <h2>Grafički prikaz rezultata</h2>
        <img alt="Pie-chart" src="${pageContext.request.contextPath}/glasanje-grafika" width="400" height="400" />

        <h2>Rezultati u XLS formatu</h2>
        <p>Rezultati u XLS formatu dostupni su <a href="${pageContext.request.contextPath}/glasanje-xls">ovdje</a></p>

        <h2>Razno</h2>
        <p>Primjeri pjesama pobjedničkih bendova:</p>
        <ul>
            <% for (BandEntry band: getBestBands(request.getAttribute("bands"))) { %>
                <li><a href="<%= band.getRepresentativeUrl() %>" target="_blank"><%= band.getName() %></a></li>
            <% } %>
        </ul>
    </body>
</html>
