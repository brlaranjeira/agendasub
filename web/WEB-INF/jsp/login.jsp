<%-- 
    Document   : login
    Created on : Sep 18, 2014, 4:29:39 PM
    Author     : SSI-Bruno
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jwr" uri="http://jawr.net/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../jspf/definitions.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/login.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <c:choose>
            <c:when test="${(not empty login and login) and (empty forbidden or not forbidden)}">
                <%@ include file="../jspf/top.jspf" %>
                <div id="div-logout">
                    Você está acessando como: ${username}.<br/>
                    <a href="logout.htm">Clique aqui para sair.</a><br/>
    <!--                <a href="form.htm"></a>-->
                </div>
            </c:when>
            <c:otherwise>
                <div id="ctismlogo-login"></div>
                <div id="div-login">
                    <form id="login-form" action="login.action" method="POST">
                    <c:if test="${(not empty loginError) or (not empty forbidden and forbidden)}">
                        <div id="div-error-msg">
                            <c:if test="${not empty loginError}">
                                <div id="div-error-msg">
                                    Login ou senha inválidos!<br/>
                                </div>
                            </c:if>
                            <c:if test="${not empty forbidden and forbidden}">
                                <div id="div-error-msg">
                                    Seu login não está autorizado a utilizar o sistema!<br/>
                                </div>
                            </c:if>
                        </div>
                    </c:if>
                    <%--
                    <c:choose>
                        <c:when test="${not empty loginError}">
                            <div id="div-error-msg">
                                Login ou senha inválidos!
                            </div>
                        </c:when>
                        <c:when test="${not empty forbidden and forbidden}">
                            <div id="div-error-msg">
                                Seu login não está autorizado a utilizar o sistema!
                            </div>
                        </c:when>
                    </c:choose>
                    --%>
                        Login:<br/> <input type="text" name="user"/><br/>
                        Senha:<br/> <input type="password" name="passwd"/><br/>
                        <input type="submit" value="Entrar"/>
                    </form> 
               </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>