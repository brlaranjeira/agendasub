<%-- 
    Document   : servererror
    Created on : Apr 10, 2015, 1:16:40 PM
    Author     : SSI-Bruno
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="jwr" uri="http://jawr.net/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/errorpages.css"/>
        <title>Solicitações de Substituição</title>
    </head>
    <body>
        <h1>Ocorreu um erro no serviço!<br/></h1>
        <c:if test="${not empty message}">
            Mensagem do servidor:<p/>
            <div id="bugServerMessage">${message}</div><p/>
        </c:if>
        Se quiser, escreva uma mensagem para o SSI, no campo abaixo, <strong>ou clique <a href="index.htm">AQUI</a> para voltar à sua página inicial.</strong>
        <form id="bugtrack" action="bugtrack.action" method="POST">
            <textarea rows="6" cols="70" name="userMessage"></textarea>
            <button type="button" id="enviaBug">Enviar!</button>
        </form>
    </body>
    <jwr:script src="/bundles/errorpages.js"/>
</html>
