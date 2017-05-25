<%-- 
    Document   : deferir
    Created on : Oct 2, 2014, 2:38:04 PM
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
        <jwr:style src="/bundles/deferir.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        <c:set var="patternDataHora" value="'<strong>'dd/MM/yyyy'</strong>' 'às' '<strong>'HH'h' mm'min</strong>'"/>
        <c:set var="patternData" value="'<strong>'dd/MM/yyyy'</strong>'"/>
        <div id="deferir-empty"
             <c:if test="${not empty aulas}" >
                 class="hidden"
             </c:if>
        > <fmt:message key="deferir.vazio"/> </div>
        <c:forEach items="${aulas}" var="aula" varStatus="i">
            <c:set var="divClass" value="deferir"/>
            <c:if test="${i.index mod 2 ne 0}">
                <c:set var="divClass" value="${divClass} zebra"/>
            </c:if>
            <div class="${divClass}" num="${aula.id}">
                <strong>${aula.solicitacao.professor.nome}</strong> solicita:<br/>
                Substituição para a aula de <strong>${aula.componente.nome}</strong> do dia <fmt:formatDate value="${aula.dataAula}" pattern="${patternDataHora}"/><br/>
                Pelo(a) professor(a):
                    <strong>
                    <c:choose>
                        <c:when test="${aula.profSubstituto ne null}">
                            ${aula.profSubstituto.nome}
                        </c:when>
                        <c:otherwise>
                            [${aula.profSubstitutoLdap}]
                        </c:otherwise>
                    </c:choose>
                    </strong>.<br/>
                A aula será recuperada no dia <fmt:formatDate value="${aula.dataRecuperacao}" pattern="${patternDataHora}"/><br/>
                O(a) professor(a) estará afastado(a) das atividades escolares
                <c:choose>
                    <c:when test="${aula.solicitacao.datainicio eq aula.solicitacao.datafim}">
                        no dia <fmt:formatDate value="${aula.solicitacao.datainicio}" pattern="${patternData}"/>
                    </c:when>
                    <c:otherwise>
                        entre os dias <fmt:formatDate value="${aula.solicitacao.datainicio}" pattern="${patternData}"/>
                        e <fmt:formatDate value="${aula.solicitacao.datafim}" pattern="${patternData}"/>
                    </c:otherwise>
                </c:choose><br/>
                Pelo(s) motivo(s):
                <c:forEach items="${aula.solicitacao.motivosAfastamento}" var="motivo" varStatus="i">
                    <c:if test="${i.index ne 0}">, </c:if> <strong>${motivo.descricao}</strong>
                </c:forEach>
                <c:if test="${not empty aula.solicitacao.outroMotivo}">
                    <c:if test="${not empty aula.solicitacao.motivosAfastamento}">, </c:if>
                    <strong><c:out value="${aula.solicitacao.outroMotivo}"/></strong>
                </c:if>
                <c:if test="${empty aula.solicitacao.outroMotivo and empty aula.solicitacao.motivosAfastamento}">
                    <strong>(Nenhum motivo cadastrado)</strong>
                </c:if>
                <table class="botoesTable"><tr>
                    <td><button class="btAceita" value="${aula.id}" acao="deferir"><fmt:message key="deferir.deferir"/></button></td>
                    <td><button class="btRecusa" value="${aula.id}" acao="indeferir"><fmt:message key="deferir.indeferir"/></button></td>
                </tr></table>
                <p/>
            </div>
        </c:forEach>
    </body>
    <jwr:script src="/bundles/deferir.js"/>
</html>
