<%-- 
    Document   : relatorios
    Created on : Oct 6, 2014, 3:30:33 PM
    Author     : SSI-Bruno
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="jwr" uri="http://jawr.net/tags" %>
<%@include file="../jspf/definitions.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jwr:style src="/bundles/relatorios.css"/>
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title>Solicitação de Substituições</title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>
        <form id="form-relatorio" action="relatorios.action" method="POST" target="_blank">
            <div id="div-error-msg"></div>
            <div id="div-situacoes">
                <span class="report-title"><fmt:message key="relatorios.incluir.situacoes"/>:<br/></span>
                <div class="tabbed-div">
                    <c:forEach items="${situacoes}" var="situacao">
                        <input type="checkbox" value="${situacao.id}" name="situacao"/>${situacao.descricao}
                    </c:forEach><br/>
                </div>
            </div>
            <span class="report-title"><fmt:message key="relatorios.tipo"/>:<br/></span>
            <div class="tabbed-div">
                <input type="radio" name="tiporelatorio" value="por_mim"><fmt:message key="relatorios.tipo.pormim"/><br/>
                <input type="radio" name="tiporelatorio" value="para_mim"><fmt:message key="relatorios.tipo.paramim"/><br/>
                <c:if test="${isSsi or isDepEd}">
                    <input type="radio" name="tiporelatorio" value="por_prof"><fmt:message key="relatorios.tipo.por"/>:
                    <select name="professor_por">
                        <c:forEach items="${professores}" var="professor">
                            <option value="${professor.ldap}">${professor.nome}</option>
                        </c:forEach>
                    </select><br/>
                    <input type="radio" name="tiporelatorio" value="para_prof"><fmt:message key="relatorios.tipo.para"/>:
                    <select name="professor_para">
                        <c:forEach items="${solicitacoesPara}" var="solicitado">
                            <option value="${solicitado.ldap}">${solicitado.nome}</option>
                        </c:forEach>
                    </select><br/>
                </c:if>
            </div>
            <span class="report-title"><fmt:message key="relatorios.tipofiltro"/>:<br/></span>
            <div class="tabbed-div">
                <select name="tipofiltrodata">
                    <option value="dt_aula"><fmt:message key="relatorios.tipofiltro.dtaula"/></option>
                    <option value="dt_recuperacao"><fmt:message key="relatorios.tipofiltro.dtrec"/></option>
                </select><br/>
                Entre: <input name="datainicio" class="data">
                e <input name="datafim" class="data"><br/>
            </div>
            <input type="submit" value="<fmt:message key="relatorios.enviar"/>"/>
        </form>
    </body>
    <jwr:script src="/bundles/relatorios.js"/>
</html>
