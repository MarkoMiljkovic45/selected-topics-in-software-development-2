<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Register</title>
        
        <style>
            .error {
                color: red;
            }
        </style>
    </head>
    <body>
        <div>
            <h2>Register</h2>
            <form action="register" method="post">
                <div>
                    <label for="firstName">First name:</label>
                    <input id="firstName" type="text" name="firstName" value='<c:out value="${registerForm.firstName}"/>'>

                    <c:if test="${registerForm.hasError('firstName')}">
                        <div class="error">${registerForm.getErrorMessage('firstName')}</div>
                    </c:if>
                </div>
                <div>
                    <label for="lastName">Last name:</label>
                    <input id="lastName" type="text" name="lastName" value='<c:out value="${registerForm.lastName}"/>'>

                    <c:if test="${registerForm.hasError('lastName')}">
                        <div class="error">${registerForm.getErrorMessage('lastName')}</div>
                    </c:if>
                </div>
                <div>
                    <label for="nickname">Nickname:</label>
                    <input id="nickname" type="text" name="nickname" value='<c:out value="${registerForm.nickname}"/>'>

                    <c:if test="${registerForm.hasError('nickname')}">
                        <div class="error">${registerForm.getErrorMessage('nickname')}</div>
                    </c:if>
                </div>
                <div>
                    <label for="email">E-mail:</label>
                    <input id="email" type="email" name="email" value='<c:out value="${registerForm.email}"/>'>

                    <c:if test="${registerForm.hasError('email')}">
                        <div class="error">${registerForm.getErrorMessage('email')}</div>
                    </c:if>
                </div>
                <div>
                    <label for="password">Password:</label>
                    <input id="password" type="password" name="password" value='<c:out value="${registerForm.password}"/>'>

                    <c:if test="${registerForm.hasError('password')}">
                        <div class="error">${registerForm.getErrorMessage('password')}</div>
                    </c:if>
                </div>
                <div>
                    <input type="submit" value="Register">
                </div>
            </form>
        </div>
    </body>
</html>
