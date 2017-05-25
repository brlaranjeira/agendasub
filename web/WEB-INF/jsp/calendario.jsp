<%-- 
    Document   : calendario
    Created on : Apr 28, 2015, 4:13:01 PM
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
        <jwr:style src="/bundles/calendario.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        Ver semana do dia: <input class="data" name="datacalendario"/>
        <table id="table-calendario">
            <tr>
                <td class="td-horario">Horário</td>
                <c:forEach items="${datas}" var="dt" varStatus="i">
                    <c:set var="lastDay" value="${dt}"/>
                    <th>${diasSemana[i.index]}<br/>${dt}</th>
                </c:forEach>
            </tr>
            <c:forEach items="${horas}" var="hr" varStatus="hrIdx">
                <c:if test="${hrIdx.index ne 0}">
                    <tr>
                        <td class="td-horario">
                            <fmt:formatNumber value="${horas[hrIdx.index-1]}" pattern="00"/>-<fmt:formatNumber value="${hr}" pattern="00"/>
                        </td>
                        <c:forEach items="${datas}" var="dt" varStatus="dtIdx">
                            <td class="calendarioCelula loading" dt="${dt}" hrIni="${horas[hrIdx.index-1]}" hrFim="${hr}"></td>
                        </c:forEach>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
        <div id="div-calendario-prevnext" dt="${lastDay}">
            <div id="div-calendario-prev" class="div-calendario-btn" pn="p"></div>
            <div id="div-calendario-prevnext-text">Visualizar outra semana</div>
            <div id="div-calendario-next" class="div-calendario-btn" pn="n"></div>
        </div>
    </body>
    <jwr:script src="/bundles/calendario.js"/>
</html>
