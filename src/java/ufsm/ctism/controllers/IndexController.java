/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ufsm.ctism.service.UsuarioService;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.SessionUtils;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class IndexController {

    @Autowired
    UsuarioService usuarioService;

    /**
     * Controller para redirecionamento de uma requisiçao para index.htm,
     * direcionando para form.htm, caso esteja logado, ou login.htm, caso
     * contrário
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/index.htm")
    public String index(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {
        try {
            String userUid = request.getSession().getAttribute(SessionUtils.USER_UID).toString();
            String myIndex = "login";
            Integer permissions = usuarioService.getPermissionsAndSetToModel(userUid, model);
            if ((permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0) {
                myIndex = "form";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_SSI) > 0) {
                myIndex = "relatorios";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) > 0) {
                myIndex = "deferir";
            } else if ((permissions & GrupoUsuarios.PERMISSAO_ESTAGIARIOS) > 0) {
                myIndex = "paramim";
            }
            response.sendRedirect(myIndex + ".htm");
            return myIndex;
        } catch (NullPointerException ex) {
            response.sendRedirect("login.htm");
            return "login";
        }
    }
}
