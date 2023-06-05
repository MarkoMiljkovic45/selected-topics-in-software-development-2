<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Home</title>

    <style>
      .error {
        color: red;
      }
    </style>
  </head>
  <body>
    <div>
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

      <c:if test="${sessionScope.userId == null}">
        <div>
          <h2>Login</h2>
          <form action="main" method="post">
            <div>
              <label for="nickname">Nickname:</label>
              <input id="nickname" type="text" name="nickname" value='<c:out value="${loginForm.nickname}"/>'>

              <c:if test="${loginFrom.hasError('nickname')}">
                <div class="error">${loginForm.getErrorMessage('nickname')}</div>
              </c:if>
            </div>
            <div>
              <label for="password">Password:</label>
              <input id="password" type="password" name="password" value='<c:out value="${loginForm.password}"/>'>

              <c:if test="${loginFrom.hasError('password')}">
                <div class="error">${loginForm.getErrorMessage('password')}</div>
              </c:if>
            </div>

            <c:if test="${error != null}">
              <div class="error">${error}</div>
            </c:if>

            <div>
              <input type="submit" value="Login">
            </div>
          </form>
          <div>
            <span>Don't have an account?</span>
            <a href="<%=request.getContextPath()%>/servleti/register">Register here!</a>
          </div>
        </div>
      </c:if>
      <div>
        <h2>Registered authors</h2>
        <ul>
          <c:forEach var="user" items="${users}">
            <li>
              <a href="${pageContext.request.contextPath}/servleti/author/${user.nickname}">
                ${user.nickname} (${user.firstName} ${user.lastName})
              </a>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </body>
</html>
