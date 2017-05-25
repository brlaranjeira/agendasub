<%-- 
    Document   : form
    Created on : Sep 18, 2014, 5:47:38 PM
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
        <%--<jwr:script src="/bundles/form.js"/>--%>
        <jwr:style src="/bundles/form.css" />
        <link rel="Shortcut Icon" type="image/ico" href="/solicita/img/logo_ctism.ico"/>
        <title><fmt:message key="main.titulo"/></title>
    </head>
    <body>
        <%@include file="../jspf/top.jspf" %>

        <div id="div-error-msg"></div>
        <form id="mainform" action="submitform.htm" method="POST">
            <fieldset class="motivo"> <legend> <fmt:message key="form.titulo.afastamento"/> </legend>
                <div id="div-motivo">
                    <fmt:message key="form.periodo"/>:
                    <input class="data" name="datainicio"
                           <c:if test="${not empty session_datainicio}">
                               <fmt:formatDate value="${session_datainicio}" var="session_datainicio" pattern="dd/MM/yyyy" />
                               value="${session_datainicio}"
                           </c:if>
                           />
                    a <input class="data" name="datafim" 
                             <c:if test="${not empty session_datafim}">
                                 <fmt:formatDate value="${session_datafim}" var="session_datafim" pattern="dd/MM/yyyy" />
                                 value="${session_datafim}"
                             </c:if>
                             /><br/>
                    <fmt:message key="form.motivos"/>:<br/>
                    <c:forEach items="${motivos}" var="motivo">
                        <input type="checkbox" name="motivo" value="${motivo.id}"
                            <c:forEach items="${session_motivos}" var="sess_motivo">
                                <c:if test="${sess_motivo eq motivo.id}">
                                    checked="true"
                                </c:if>
                            </c:forEach>
                        />${motivo.descricao}<br/>
                    </c:forEach>
                    <c:set var="outroClass" value="hiddenInput"/>    
                    <c:forEach items="${session_motivos}" var="sess_motivo">
                        <c:if test="${sess_motivo eq -1}">
                            <c:set var="outroClass" value=""/>
                        </c:if>
                    </c:forEach>
                    <input type="checkbox" name="motivo" value="-1"
                           <c:if test="${empty outroClass}">
                               checked="true"
                           </c:if>
                           /><fmt:message key="form.motivos.outro"/>:
                    <label id="outro-label" class="${outroClass}"><fmt:message key="form.motivos.outro.qual" /></label>
                    <input type="text" name="outro" class="${outroClass}"
                           <c:if test="${not empty session_outro}">
                               value="${session_outro}"
                           </c:if>
                           /> <br/>
                </div>
            </fieldset>
            <c:choose>
                <c:when test="${not empty session_componentes}">
                    <c:forEach items="${session_componentes}" varStatus="i">
                        <%@include file="../jspf/form_aula.jspf" %>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <%@include file="../jspf/form_aula.jspf" %>
                </c:otherwise>
            </c:choose>



            <div id="div-addAula"></div> 
            <br/><input type="submit" value="<fmt:message key="form.enviar"/>"/>
        </form>
    </body>
    <script type="text/javascript" src="/solicita/js/bootstrap/bootstrap.min.js" ></script>
    <jwr:script src="/bundles/form.js" />
    
</html>
