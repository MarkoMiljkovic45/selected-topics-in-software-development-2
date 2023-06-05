<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>${action}</title>

    <style>
      .error {
        color: red;
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

    <c:choose>
      <c:when test="${sessionScope.userId == null}">
        <div class="error">
          You must be logged in to access this page!
        </div>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${error == null}">
            <h2>${action} a Blog Entry</h2>

            <form method="post">
              <div>
                <label for="title">Title:</label>
                <input id="title" type="text" name="title" value='<c:out value="${blogForm.title}"/>'>

                <c:if test="${blogForm.hasError('title')}">
                  <div class="error">${blogForm.getErrorMessage('title')}</div>
                </c:if>
              </div>
              <div>
                <label for="text">Text:</label>
                <textarea id="text" name="text"><c:out value="${blogForm.text}"/></textarea>

                <c:if test="${blogForm.hasError('text')}">
                  <div class="error">${blogForm.getErrorMessage('text')}</div>
                </c:if>
              </div>
              <div>
                <input type="submit" value="${action}">
              </div>
            </form>
          </c:when>
          <c:otherwise>
            <div class="error">${error}</div>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>

  </body>
</html>
