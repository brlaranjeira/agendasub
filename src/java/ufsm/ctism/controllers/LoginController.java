/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import java.util.Enumeration;
import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ufsm.ctism.service.*;
import ufsm.ctism.utils.SessionUtils;
import ufsm.ctism.utils.GrupoUsuarios;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;
    
    /**
     * Controller para a autenticação do login
     *
     * @param request
     * @param response
     * @param user
     * @param passwd
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login.action", method = RequestMethod.POST)
    public String authLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String user,
            @RequestParam String passwd,
            Model model) throws Exception {
        try {
            Boolean auth = usuarioService.auth(user, passwd);
            Boolean allow = usuarioService.hasSystemAccess(user);
            Integer permissions = usuarioService.getPermissionsAndSetToModel(user, model);
            if ( auth && allow ) {
                String userName = usuarioService.getByLdap(user).getNome();
                Enumeration<String> attrs = request.getSession().getAttributeNames();
                while ( attrs.hasMoreElements() ) {
                    request.getSession().removeAttribute(attrs.nextElement());
                }
                request.getSession().setAttribute(SessionUtils.USER_UID, user);
                request.getSession().setAttribute(SessionUtils.USER_FULLNAME, userName);
                String myIndex = "login";
                if ( ( permissions & GrupoUsuarios.PERMISSAO_PROFESSORES ) > 0) {
                    myIndex = "form";
                } else if ( ( permissions & GrupoUsuarios.PERMISSAO_SSI ) > 0) {
                    myIndex = "relatorios";
                } else if ( ( permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO ) > 0) {
                    myIndex = "deferir";
                } else if ( ( permissions & GrupoUsuarios.PERMISSAO_ESTAGIARIOS) > 0) {
                    myIndex = "paramim";
                }
                response.sendRedirect( myIndex + ".htm");
                return myIndex;
            }
            request.getSession().setAttribute("forbidden", !allow);
            request.getSession().setAttribute("loginError", !auth);
            response.sendRedirect("login.htm");
            return "login";
        } catch (ServiceUnavailableException ex) {
            throw new Exception(ex);
        }
        
        
    }

    /**
     * Controller para a tela de login
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/login.htm")
    public String login(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {
        Object username = request.getSession().getAttribute(SessionUtils.USER_FULLNAME);
        if (username != null) {
            model.addAttribute("login", true);
            model.addAttribute("username", username.toString());
            Integer permissions = usuarioService.getPermissionsAndSetToModel(request.getSession().getAttribute(SessionUtils.USER_UID).toString(), model);
            String myIndex = null;
            if ((permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0) {
                myIndex = "form";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_SSI) > 0) {
                myIndex = "relatorios";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) > 0) {
                myIndex = "deferir";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_ESTAGIARIOS) > 0) {
                myIndex = "paramim";
            }
            if (myIndex != null) {
                response.sendRedirect( myIndex + ".htm");
                return myIndex;
            }
        }
        if (request.getSession().getAttribute("loginError") != null) {
            model.addAttribute("loginError", request.getSession().getAttribute("loginError"));
            request.getSession().removeAttribute("loginError");
        }
        if (request.getSession().getAttribute("forbidden") != null) {
            model.addAttribute("forbidden", request.getSession().getAttribute("forbidden"));
            request.getSession().removeAttribute("forbidden");
        }
        return "login";
    }

    /**
     * Controller para ação de logout
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/logout.htm")
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {
        HttpSession session = request.getSession();
        while (session.getAttributeNames().hasMoreElements()) {
            String el = session.getAttributeNames().nextElement().toString();
            session.removeAttribute(el);
        }
        response.sendRedirect("login.htm");
        return "login";
    }
}
