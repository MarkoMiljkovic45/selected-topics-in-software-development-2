<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>${blogEntry.title}</title>

        <style>
            .comment {
                border: solid black 1px;
            }
        </style>
    </head>
    <body>
        <c:choose>
            <c:when test="${sessionScope.userId == null}">
                <header>Not logged in</header>
            </c:when>
            <c:otherwise>
                <header>
                    <span>${sessionScope.userFirstName} ${sessionScope.userLastName}</span>
                    <a href="${pageContext.request.contextPath}/servleti/logout"><button>Logout</button></a>
                </header>
            </c:otherwise>
        </c:choose>

        <div>
            <h2>${blogEntry.title}</h2>
            <p>${blogEntry.text}</p>

            <c:if test="${sessionScope.userId == blogEntry.creator.id}">
                <a href="${pageContext.request.contextPath}/servleti/author/${blogEntry.creator.nickname}/edit?id=${blogEntry.id}">
                    <button>Edit</button>
                </a>
            </c:if>

            <h3>Comments:</h3>

            <c:forEach var="comment" items="${blogEntry.comments}">
                <div class="comment">
                    <h4>${comment.usersEMail}</h4>
                    <p>Posted on: ${comment.postedOn}</p>
                    <p>${comment.message}</p>
                </div>
            </c:forEach>

            <c:if test="${sessionScope.userId != null}">
                <form method="post">
                    <label for="message">Leave a comment:</label>
                    <textarea id="message" name="message"></textarea>
                    <input type="submit" value="Post">
                </form>
            </c:if>
        </div>
    </body>
</html>
