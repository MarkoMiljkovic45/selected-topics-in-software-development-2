<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>${author.nickname}</title>
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
      <h2>${author.nickname} (${author.firstName} ${author.lastName})</h2>

      <p>Email: ${author.email}</p>

      <h3>Blogs:</h3>
      <ul>
        <c:forEach var="blog" items="${author.blogs}">
          <a href="${pageContext.request.contextPath}/servleti/author/${author.nickname}/${blog.id}">
            <li>${blog.title}</li>
          </a>
        </c:forEach>
      </ul>

      <c:if test="${sessionScope.userId == author.id}">
        <a href="${pageContext.request.contextPath}/servleti/author/${author.nickname}/new">
          <button>Create new blog</button>
        </a>
      </c:if>
    </div>
  </body>
</html>
