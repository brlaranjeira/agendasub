<%-- 
    Document   : minhas
    Created on : Sep 30, 2014, 5:42:50 PM
    Author     : SSI-Bruno
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jwr" uri="http://jawr.net/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../jspf/definitions.jspf" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/minhas.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title><fmt:message key="main.titulo"/></title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        <c:set var="dataPattern" value="'<strong>'dd/MM/yyyy'</strong>, às <strong>' HH'h' mm'min</strong>'"/>
        <div id="div-radio-tipo">
            <form>
                <label id="label-radio-filtro"> Mostrar: </label>
                <input type="radio" name="tipo-filtro" tipo="dataAula" <c:if test="${tipo eq 'dataAula'}">checked</c:if> /> Aulas Futuras
                <input type="radio" name="tipo-filtro" tipo="dataRecuperacao" <c:if test="${tipo eq 'dataRecuperacao'}">checked</c:if> /> Aulas com Recuperação Futura
                <input type="radio" name="tipo-filtro" tipo="todas" <c:if test="${tipo eq 'todas'}">checked</c:if> /> Todas as aulas
                </form>
            </div>
            <div id="div-solicitacoes">
            <jsp:useBean id="timenow" class="java.util.Date"/>
            <c:choose>
                <c:when test="${not empty aulas}">
                    <c:forEach items="${aulas}" var="aula" varStatus="i">
                        <fmt:formatDate value="${aula.dataAula}" pattern="${dataPattern}" var="currentAulaData"/>
                        <fmt:formatDate value="${aula.dataRecuperacao}" pattern="${dataPattern}" var="currentAulaDataRec"/>
                        <c:set var="divClass" value="solicitacao"/>
                        <c:if test="${i.index mod 2 ne 0}">
                            <c:set var="divClass" value="${divClass} zebra"/>
                        </c:if>
                        <div class="${divClass}" componente="${aula.componente.nome}" dtaula="${currentAulaData}" dtrec="${currentAulaDataRec}" profsub="${aula.profSubstituto.nome}">
                            <table class="minhaSolicitacaoTable"><tr>
                                <col width="57px" />
                                <col/>
                                <td class="tdCancelaAula">
                                    <c:if test="${aula.dataAula gt timenow}">
                                        <div class="cancelaAula" aulaId="${aula.id}" ></div>
                                        Cancelar
                                    </c:if>
                                </td>
                                <td>
                                    <br/>
                                    <c:set var="profSubNome" value="${aula.profSubstituto.nome}"/>
                                    <c:if test="${aula.profSubstituto eq null}">
                                        <c:set var="profSubNome" value="[${aula.profSubstitutoLdap}]"/>
                                    </c:if>
                                    <fmt:message key="minhas.aula.detalhes">
                                        <fmt:param value="${aula.componente.nome}" />
                                        <fmt:param value="${currentAulaData}" />
                                        <fmt:param value="${currentAulaDataRec}" />
                                        <fmt:param value="${profSubNome}" />
                                        <fmt:param value="${aula.situacao.descricao}" />
                                    </fmt:message> <br/>
                                    <br/>
                                </td>

                                </tr></table>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <fmt:message key="minhas.vazio"/>
                </c:otherwise>
            </c:choose>

            <%--
            <c:forEach items="${matrixAulas}" var="listAulas" >
                <c:set var="situacao" value="${listAulas[0].situacao}"/>
                <h3><fmt:message key="minhas.status"/>: ${situacao.descricao}</h3><div>
                <c:forEach items="${listAulas}" var="aula" varStatus="i">
                    <c:set var="divClass" value="solicitacao"/>
                    <c:if test="${i.index mod 2 ne 0}">
                        <c:set var="divClass" value="${divClass} zebra"/>
                    </c:if>
                    <div class="${divClass}">
                        Aula de ${aula.componente.nome}.<br/>Do dia <fmt:formatDate value="${aula.dataAula}" pattern="${dataPattern}"/> <br/>
                        Solicitada para o professor ${aula.profSubstituto.nome}. Situação: <strong>${aula.situacao.descricao}</strong><p/>
                    </div>
                </c:forEach>
                </div>
            </c:forEach>
            --%>
        </div>
        <%--        Disciplina: ${aula.componente.nome}<br/>
                    Dia: <fmt:formatDate value="${aula.dataAula}" pattern="${dataPattern}" /><br/>
                    Professor substituto: ${aula.profSubstituto.nome}<br/>
                    Recuperação marcada para: <fmt:formatDate value="${aula.dataRecuperacao}" pattern="${dataPattern}" /><br/>
                    Situação: ${aula.situacao.descricao}<p/> --%>
    </body>
    <jwr:script src="/bundles/minhas.js"/>
</html>
