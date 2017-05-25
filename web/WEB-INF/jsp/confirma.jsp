<%-- 
    Document   : confirma
    Created on : Sep 19, 2014, 2:03:22 PM
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
        <jwr:style src="/bundles/confirma.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        <c:set var="pattern" value="'<strong>'dd/MM/yyyy'</strong>'" />
        <c:set var="patternDataHora" value="dd/MM/yyyy HH:mm:ss" />
        <c:set var="patternData" value="dd/MM/yyyy" />
        <c:set var="patternHora" value="HH:mm" />

        <div id="div-confirma">
            
            <%--<div id="aviso-sie">
                <fmt:message key="form.confirma.aviso.sie"/>
            </div>--%>
            
            Eu, <strong>${solicitacao.professor.nome}</strong>, informo ao Departamento de Ensino que, no
            
            <c:choose>
                <c:when test="${solicitacao.datainicio eq solicitacao.datafim}">
                    dia <fmt:formatDate value="${solicitacao.datainicio}" pattern="${pattern}"/>
                </c:when>
                <c:otherwise>
                    período de <fmt:formatDate value="${solicitacao.datainicio}" pattern="${pattern}"/> a <fmt:formatDate value="${solicitacao.datafim}" pattern="${pattern}"/>
                </c:otherwise>
            </c:choose>
            , estarei afastado das atividades escolares.<br/>
            Motivos do afastamento:<br/>
            <ul>
                <c:forEach var="motivo" items="${solicitacao.motivosAfastamento}">
                    <li> ${motivo.descricao} </li>
                </c:forEach>
                <c:if test="${not empty solicitacao.outroMotivo}">
                    <li>${solicitacao.outroMotivo}</li>
                </c:if>
            </ul>
            Solicito a substituição das seguintes aulas:
            <c:set var="pattern" value="'<strong>'dd/MM/yyyy'</strong>' à's' <'strong>'HH:mm'</strong>'" />
            <c:forEach items="${aulas}" var="aula">
                <div class="aula-solicitada">
                    <p/>
                    <strong>${aula.componente.nome}</strong>,<br/>
                    no dia <fmt:formatDate value="${aula.dataAula}" pattern="${pattern}" /></strong>,
                    pelo(a) professor(a) <strong>${aula.profSubstituto.nome}</strong>.<br/>
                    Esta aula será recuperada no dia <fmt:formatDate value="${aula.dataRecuperacao}" pattern="${pattern}" /><br/>
                </div>
            </c:forEach>
            <fmt:formatDate value="${solicitacao.datainicio}" var="dtIni" pattern="dd/MM/yyyy"/>
            <fmt:formatDate value="${solicitacao.datafim}" var="dtFim" pattern="dd/MM/yyyy"/>
            <input type="hidden" name="datainicio" value="${dtIni}"/>
            <input type="hidden" name="datafim" value="${dtFim}"/>
            <c:forEach items="${solicitacao.motivosAfastamento}" var="motivo">
                <input type="hidden" name="motivo" value="${motivo.id}"/>
            </c:forEach>
            <c:forEach items="${aulas}" var="aula">
                <fmt:formatDate value="${aula.dataAula}" var="dtAula" pattern="${patternDataHora}"/>
                <fmt:formatDate value="${aula.dataRecuperacao}" var="dtRec" pattern="${patternDataHora}"/>
                <input type="hidden" name="componente" value="${aula.componente.id}"/>
                <input type="hidden" name="professor" value="${aula.profSubstituto.ldap}"/>
                <input type="hidden" name="dtaula" value="${dtAula}"/>
                <input type="hidden" name="dtrec" value="${dtRec}"/>
            </c:forEach>
            <c:if test="${not empty solicitacao.outroMotivo}">
                <input type="hidden" name="outro" value="${solicitacao.outroMotivo}"/>
            </c:if>
            <table><tr>
                <td><button id="btConfirma">Confirma</button></td>
                <td><button id="btCancela">Cancelar</button></td>
            </tr></table>
            
        </div>
    </body>
    <jwr:script src="/bundles/confirma.js"/>
</html>
