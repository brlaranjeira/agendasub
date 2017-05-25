<%-- 
    Document   : forbidden
    Created on : Apr 8, 2015, 4:37:14 PM
    Author     : SSI-Bruno
--%>


<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jwr" uri="http://jawr.net/tags" %>
<%@include file="../../jspf/definitions.jspf" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/errorpages.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitações de Substituição</title>
    </head>
    <body>
        <div id="divForbidden"></div>
        <div id="forbidden-message">
            <h1>Acesso negado.</h1>
            <c:choose>
                <c:when test="${not empty username}">
                    Você está logado como <strong>${username}</strong> e não possui permissão para acessar esta página.<br/>
                </c:when>
                <c:otherwise>
                    Você não possui permissão para acessar esta página.<br/>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty message}">
                Mensagem do sistema:<br/>
                <strong>${message}<strong><br/>
            </c:if>
                    
            Clique <a href="index.htm">AQUI</a> para voltar.
            
        </div>
    </body>
</html>
