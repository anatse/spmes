<%-- 
    Document   : welcome
    Created on : Apr 9, 2014, 11:20:38 AM
    Author     : ASementsov
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <title><spring:message code="login.title"/></title>
        <style>
            .error {
                padding: 15px;
                margin-bottom: 20px;
                border: 1px solid transparent;
                border-radius: 4px;
                color: #a94442;
                background-color: #f2dede;
                border-color: #ebccd1;
            }

            .msg {
                padding: 15px;
                margin-bottom: 20px;
                border: 1px solid transparent;
                border-radius: 4px;
                color: #31708f;
                background-color: #d9edf7;
                border-color: #bce8f1;
            }

            #login-box {
                width: 300px;
                padding: 20px;
                margin: 100px auto;
                background: #fff;
                -webkit-border-radius: 2px;
                -moz-border-radius: 2px;
                border: 1px solid #000;
            }
        </style>
        <script>
        function focus() {
            document.getElementById('username').focus();
        }
        </script>
    </head>
    <body onload='focus();'>
        <h1><spring:message code="login.form.title"/></h1>
        <div id="login-box">
            <h3><spring:message code="login.box.title"/></h3>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>
            <c:if test="${not empty msg}">
                <div class="msg">${msg}</div>
            </c:if>

            <c:url value="/login" var="loginUrl"/>
            <form action="${loginUrl}" method="post">       
                <table>
                    <tr>
                        <td>
                            <label for="username"><spring:message code="username.label"/></label>
                        </td>
                        <td>
                            <input type="text" id="username" name="username"/>	
                        </td>
                    </tr>
                    
                    <tr>
                        <td>
                            <label for="password"><spring:message code="password.label"/></label>
                        </td>
                        <td>
                            <input type="password" id="password" name="password"/>
                        </td>
                    </tr>
                </table>

                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>

                <button type="submit" class="btn"><spring:message code="loginButton.label"/></button>
            </form>
        </div>
    </body>
</html>