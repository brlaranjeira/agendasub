/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.service.MailService;
import ufsm.ctism.utils.SessionUtils;




/**
 *
 * @author SSI-Bruno
 */
@ControllerAdvice
@Controller
public class ErrorsController {

    
    @Autowired
    MailService mailService;
    
    @ExceptionHandler(Exception.class)
    public String defaultExceptionHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception) {
        request.getSession().setAttribute(SessionUtils.ERROR_EXCEPTION, exception);
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "error-pages/servererror";
    }
    
    @RequestMapping(value = "/ajax/bugtrack.action" , method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> sendBug(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String userMessage) {
        
        Map<String,Object> ret = new HashMap<>();
        
        StringBuilder sb = new StringBuilder();
        
        String userName;
        try {
            userName = request.getSession().getAttribute(SessionUtils.USER_FULLNAME).toString();
            sb.append("Usuário:\n\t" + userName + "\n");
        }catch (NullPointerException ex) {
            sb.append("Usuário não logado.\n");
        }
         sb.append("Mensagem do Usuário:\n\t" + userMessage + "\n");
         Exception fromSession = (Exception) request.getSession().getAttribute(SessionUtils.ERROR_EXCEPTION);
        if (fromSession.getMessage() == null || fromSession.getMessage().trim().isEmpty()) {
            sb.append("Sem mensagem na excessão\n");
        } else {
            sb.append("Mensagem da Exceção:\n\t" + fromSession.getMessage().trim() + "\n");
        }
        
        sb.append("Pilha:");
        for (StackTraceElement stackElement : fromSession.getStackTrace()) {
            sb.append( stackElement.toString() + "\n" );
        }
        request.getSession().removeAttribute(SessionUtils.ERROR_EXCEPTION);
        Boolean sent = mailService.sendMail("ssi@ctism.ufsm.br", "BUG", sb.toString(), Boolean.TRUE);
        ret.put("success", sent);
        response.setStatus(sent ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return ret;
    }
    
}
