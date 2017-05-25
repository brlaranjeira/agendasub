/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.service.SituacaoService;
import ufsm.ctism.service.UsuarioService;
import ufsm.ctism.utils.JReportsUtils;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.MiscUtils;
import ufsm.ctism.utils.SessionUser;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class RelatoriosController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    SituacaoService situacaoService;

    /**
     * Controller para a tela de geração de relatorios
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/relatorios.htm")
    public String reports(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {
        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("login.htm");
            return "login";
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        //PERMISSAO_PROFESSORES = 1;
//    public static final Integer PERMISSAO_DEPTO_EDUCACAO = 1 << 1;
//    public static final Integer PERMISSAO_SSI = 1 << 2;
//    public static final Integer PERMISSAO_BOLSISTAS = 1 << 3;
//    public static final Integer PERMISSAO_ESTAGIARIOS
        if (((permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) == 0)
                && (((permissions & GrupoUsuarios.PERMISSAO_SSI) == 0))
                && ((permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) == 0)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error-pages/forbidden";
        }
//        Session dbSession = HibernateUtils.getInstance().getSession();
        Collection<Situacao> situacoes = situacaoService.getAll();
        Collection<Usuario> professores;
        Collection<Usuario> solicitacoesPara;
        try {
            Comparator ordenador = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((Usuario) o1).getNome().compareToIgnoreCase(((Usuario) o2).getNome());
                }
            };
            professores = (Collection) usuarioService.getAllFromGroup(GrupoUsuarios.GRUPO_PROFESSORES)
                    .stream().sorted(ordenador).collect(Collectors.toList());
            solicitacoesPara = (Collection) usuarioService.getAllFromGroups(new Integer[]{GrupoUsuarios.GRUPO_ESTAGIARIOS, GrupoUsuarios.GRUPO_PROFESSORES})
                    .stream().sorted(ordenador).collect(Collectors.toList());
        } catch (NamingException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return "index";
        }
        model.addAttribute("user", user);
        model.addAttribute("situacoes", situacoes);
        model.addAttribute("professores", professores);
        model.addAttribute("solicitacoesPara", solicitacoesPara);
        return "relatorios";
    }

    /**
     * Controller para a geração de relatórios
     *
     * @param request
     * @param response
     * @param tiporelatorio
     * @param tipofiltrodata
     * @param professor_para
     * @param professor_por
     * @param situacoes
     * @param datainicio
     * @param datafim
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = "/relatorios.action", method = RequestMethod.POST)
    @ResponseBody
    public void getReport(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String tiporelatorio,
            @RequestParam String tipofiltrodata,
            @RequestParam(required = false) String professor_para,
            @RequestParam(required = false) String professor_por,
            @RequestParam(value = "situacao") Integer[] situacoes,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date datainicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date datafim,
            Model model) throws IOException, NamingException {

        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("index.htm");
            return;
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), null);
        InputStream report = ClassLoaderResourceUtils.getResourceAsStream("/reportProfessor.jasper", this);
        Map<String, Object> params = new HashMap<>();
        if (!tiporelatorio.endsWith("mim")) {
//        if (user.getSuperUser() && !tiporelatorio.endsWith("mim")) {
            params.put("ldap", tiporelatorio.startsWith("por") ? professor_por : professor_para);
            if ( ( ( permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO ) == 0) && ( ( permissions & GrupoUsuarios.PERMISSAO_SSI ) == 0) ) {
//                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                response.sendRedirect("");
                return;
            }
        } else {
            params.put("ldap", user.getUid());
        }
        Map<String, String> ldapsToNames = new HashMap<>();
        usuarioService.getAllFromGroups(new Integer[]{GrupoUsuarios.GRUPO_PROFESSORES, GrupoUsuarios.GRUPO_ESTAGIARIOS})
                .forEach(u -> ldapsToNames.put(u.getLdap(), u.getNome()));
        params.put("ldapsToNames", ldapsToNames);
        params.put("situacaolist", Arrays.asList(situacoes));

        params.put("tipofiltrodata", tipofiltrodata);
        Calendar cal = Calendar.getInstance();
        cal.setTime(datafim);
        cal.add(Calendar.DATE, 1);
        datafim = cal.getTime();
        params.put("dataini", datainicio);
        params.put("datafim", datafim);
        params.put("prof", tiporelatorio.startsWith("por") ? "sol.id_professor" : "a.id_prof_substituto");

        File repFile = JReportsUtils.getInstance().getPDF(report, params);
        byte[] repBytes = MiscUtils.fileToByteArray(repFile);
        response.reset();
        response.setBufferSize(repBytes.length);
        response.setContentType("application/pdf");
        response.getOutputStream().write(repBytes);
    }

}
