<%-- 
    Document   : paramim
    Created on : Sep 30, 2014, 5:42:57 PM
    Author     : SSI-Bruno
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jwr" uri="http://jawr.net/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../jspf/definitions.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/paramim.css" />
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        <div id="div-paramim">
            <div class="paramim-title">
                <fmt:message key="paramim.aceitar.recusar.titulo"/>
            </div>
            <div class="paramim-empty
                <c:if test="${not empty aulas}">
                    hidden
                </c:if>
            "> <fmt:message key="paramim.vazio"/> </div>
            
            <c:forEach items="${aulas}" var="aula" varStatus="i">
                <c:set var="divClass" value="aceita-recusa"/>
                <c:if test="${i.index mod 2 ne 0}">
                    <c:set var="divClass" value="${divClass} zebra"/>
                </c:if>
                <div class="${divClass}" num="${aula.id}">
                    Disciplina: <strong>${aula.componente.nome}</strong><br/>
                    Dia: <fmt:formatDate value="${aula.dataAula}" pattern="'<strong>'dd/MM/yyyy'</strong>' 'às' '<strong>'HH'h' mm'min</strong>'"/>
                    <br/>Solicitada por: <strong>${aula.solicitacao.professor.nome}</strong><br/>
                    <table class="botoesTable">
                        <td><button class="btAceita" id="${aula.id}-aceita" acao="aceitar" value="${aula.id}"><fmt:message key="paramim.aceitar"/></button></td>
                        <td><button class="btRecusa" id="${aula.id}-recusa" acao="recusar" value="${aula.id}"><fmt:message key="paramim.recusar"/></button></td>
                    </table>
                </div>
            </c:forEach>
            <div class="paramim-title">
                <fmt:message key="paramim.proximas.titulo"/>
            </div>
            <c:if test="${empty proximas}">
                <div class="paramim-empty">
                    <fmt:message key="paramim.proximas.vazio"/>
                </div>
            </c:if>
            <c:forEach items="${proximas}" var="prox" varStatus="i" >
                <c:set var="divClass" value="aula-futura"/>
                <c:if test="${i.index mod 2 ne 0}">
                    <c:set var="divClass" value="${divClass} zebra"/>
                </c:if>
                <div class="${divClass}">
                    <fmt:formatDate value="${prox.dataAula}" pattern="'<strong>'dd/MM/yyyy'</strong>, às <strong>' hh'h' mm'min</strong>'" var="currentAulaData"/>
                    <fmt:message key="paramim.aula.detalhes">
                        <fmt:param value="${prox.componente.nome}" />
                        <fmt:param value="${currentAulaData}" />
                        <fmt:param value="${prox.solicitacao.professor.nome}" />
                        <fmt:param value="${prox.situacao.descricao}" />
                    </fmt:message>
                    <br/>
                </div>
            </c:forEach>
        </div>
    </body>
    <jwr:script src="/bundles/paramim.js"/>
</html>
