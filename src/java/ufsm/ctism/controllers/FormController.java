/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.dao.*;
import ufsm.ctism.service.ComponentesService;
import ufsm.ctism.service.MotivoAfastamentoService;
import ufsm.ctism.service.UsuarioService;
import ufsm.ctism.utils.SessionUtils;
import ufsm.ctism.utils.SessionUser;
import ufsm.ctism.utils.GrupoUsuarios;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class FormController {

    @Autowired
    ComponentesService componentesService;
    @Autowired
    MotivoAfastamentoService motivoAfastamentoService;
    @Autowired
    UsuarioService usuarioService;

    /**
     * Controller para a tela do formulário de solicitações
     *
     * @param request
     * @param response
     * @param n
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/form.htm")
    public String formOpen(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "false") Boolean n,
            Model model) throws IOException, NamingException {

        HttpSession httpSession = request.getSession();
        SessionUser user = SessionUser.getUser(request);

        if (user == null) {
            model.addAttribute("forbidden", true);
            response.sendRedirect("login.htm");
            return "login";
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        Boolean allow = (permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0;
        if (!allow) {
            model.addAttribute("username", user.getNome());
            model.addAttribute("message", "Você precisa ser cadastrado como PROFESSOR para acessar esta página.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error-pages/forbidden";
        }
        try {
            if (usuarioService.userInGroup(user.getUid(), GrupoUsuarios.GRUPO_ESTAGIARIOS)) {
                response.sendRedirect("login.htm");
                return "login";
            }
        } catch (NamingException ex) {
            response.sendRedirect("login.htm");
            return "login";
        }
        String usrUid = user.getUid();
        HttpSession session = request.getSession();
        if (!n) {
            addToModel("session_datainicio", session.getAttribute(SessionUtils.FORM_DATAINICIO), model);
            addToModel("session_datafim", session.getAttribute(SessionUtils.FORM_DATAFIM), model);
            addToModel("session_motivos", session.getAttribute(SessionUtils.FORM_MOTIVOS), model);
            addToModel("session_outro", session.getAttribute(SessionUtils.FORM_MOTIVO_OUTRO), model);
            addToModel("session_componentes", session.getAttribute(SessionUtils.FORM_AULAS_COMPONENTES), model);
            addToModel("session_professores", session.getAttribute(SessionUtils.FORM_AULAS_PROFESSORES), model);
            addToModel("session_datas", session.getAttribute(SessionUtils.FORM_AULAS_DATAS), model);
            addToModel("session_rec_datas", session.getAttribute(SessionUtils.FORM_AULAS_REC_DATAS), model);
            addToModel("session_horas", session.getAttribute(SessionUtils.FORM_AULAS_HORAS), model);
            addToModel("session_rec_horas", session.getAttribute(SessionUtils.FORM_AULAS_REC_HORAS), model);
            addToModel("session_minutos", session.getAttribute(SessionUtils.FORM_AULAS_MINUTOS), model);
            addToModel("session_rec_minutos", session.getAttribute(SessionUtils.FORM_AULAS_REC_MINUTOS), model);
        } else {
            session.removeAttribute(SessionUtils.FORM_DATAINICIO);
            session.removeAttribute(SessionUtils.FORM_DATAFIM);
            session.removeAttribute(SessionUtils.FORM_MOTIVOS);
            session.removeAttribute(SessionUtils.FORM_MOTIVO_OUTRO);
            session.removeAttribute(SessionUtils.FORM_AULAS_COMPONENTES);
            session.removeAttribute(SessionUtils.FORM_AULAS_PROFESSORES);
            session.removeAttribute(SessionUtils.FORM_AULAS_DATAS);
            session.removeAttribute(SessionUtils.FORM_AULAS_REC_DATAS);
            session.removeAttribute(SessionUtils.FORM_AULAS_HORAS);
            session.removeAttribute(SessionUtils.FORM_AULAS_REC_HORAS);
            session.removeAttribute(SessionUtils.FORM_AULAS_MINUTOS);
            session.removeAttribute(SessionUtils.FORM_AULAS_REC_MINUTOS);
        }
        Collection professores, bolsistas;
        try {
            Comparator<Usuario> ordenaUsuarios = (Usuario o1, Usuario o2) -> {
                return o1.getNome().compareToIgnoreCase(o2.getNome());
            };
            Predicate<Usuario> notMe = (Usuario usuario) -> {
                return !usuario.getLdap().equals(usrUid);
            };
            professores = usuarioService.getAllFromGroup(GrupoUsuarios.GRUPO_PROFESSORES).stream()
                    .filter(notMe).sorted(ordenaUsuarios)
                    .collect(Collectors.toList());
//            estagiarios = usuarioService.getAllFromGroup(usuarioService.GRUPO_ESTAGIARIOS).stream()
//                    .filter(notMe).sorted(ordenaUsuarios)
//                    .collect(Collectors.toList());

        } catch (NamingException ex) {
            response.sendRedirect("/login.htm");
            return "login";
        }

        model.addAttribute("userName", httpSession.getAttribute(SessionUtils.USER_FULLNAME));
        model.addAttribute("componentes", componentesService.getAll());
        model.addAttribute("motivos", motivoAfastamentoService.getAll());
        model.addAttribute("user", user);
        model.addAttribute("professores", professores);
//        model.addAttribute("estagiarios",estagiarios);
        return "form";
    }

    /**
     * Controller para ação de submeter solicitação (antes de confirmada)
     *
     * @param request
     * @param response
     * @param datainicio data de inicio do afastamento
     * @param datafim data de término do afastamento
     * @param motivos motivos do afastamento (apenas dos motivos padrão)
     * @param outro outro motivo (opcional) para afastamento
     * @param componentesAulas vetor com os ids dos componentes das aulas
     * solicitadas
     * @param professoresAulas vetor com os ids dos professores substitutos
     * @param datasAulas vetor com as datas (sem hora) das aulas solicitadas
     * @param datasRec vetor com as datas (sem hora) das recuperações das aulas
     * solicitadas
     * @param horasAulas vetor com as horas (sem minutos) das aulas solicitadas
     * @param horasRec vetor com as horas (sem minutos) das recuperações das
     * aulas solicitadas
     * @param minutosAulas vetor com os minutos das aulas solicitadas
     * @param minutosRec vetor com os minutos das recuperações das aulas
     * solicitadas
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/submitform.htm", method = RequestMethod.POST)
    public String submitForm(
            HttpServletRequest request,
            HttpServletResponse response,
            //do pedido inteiro
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date datainicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date datafim,
            @RequestParam(value = "motivo") Integer[] motivos,
            @RequestParam String outro,
            //de cada aula
            @RequestParam(value = "componente") Integer[] componentesAulas,
            @RequestParam(value = "professor") String[] professoresAulas,
            @RequestParam(value = "dtaula") @DateTimeFormat(pattern = "dd/MM/yyyy") Date[] datasAulas,
            @RequestParam(value = "dtrecupera") @DateTimeFormat(pattern = "dd/MM/yyyy") Date[] datasRec,
            @RequestParam(value = "horaaula") Integer[] horasAulas,
            @RequestParam(value = "horarecupera") Integer[] horasRec,
            @RequestParam(value = "minaula") Integer[] minutosAulas,
            @RequestParam(value = "minrecupera") Integer[] minutosRec,
            Model model) throws IOException {

        HttpSession session = request.getSession();
        session.setAttribute(SessionUtils.FORM_DATAINICIO, datainicio);
        session.setAttribute(SessionUtils.FORM_DATAFIM, datafim);
        session.setAttribute(SessionUtils.FORM_MOTIVOS, motivos);
        session.setAttribute(SessionUtils.FORM_MOTIVO_OUTRO, outro);
        session.setAttribute(SessionUtils.FORM_AULAS_COMPONENTES, componentesAulas);
        session.setAttribute(SessionUtils.FORM_AULAS_PROFESSORES, professoresAulas);
        session.setAttribute(SessionUtils.FORM_AULAS_DATAS, datasAulas);
        session.setAttribute(SessionUtils.FORM_AULAS_REC_DATAS, datasRec);
        session.setAttribute(SessionUtils.FORM_AULAS_HORAS, horasAulas);
        session.setAttribute(SessionUtils.FORM_AULAS_REC_HORAS, horasRec);
        session.setAttribute(SessionUtils.FORM_AULAS_MINUTOS, minutosAulas);
        session.setAttribute(SessionUtils.FORM_AULAS_REC_MINUTOS, minutosRec);

        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("login.htm");
            return "login";
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        if ((permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0) {
            
            /**
             * VALIDACAO
             */
            if ((datafim != null && datainicio.after(datafim)) || (motivos.length == 0)
                    || (componentesAulas.length != professoresAulas.length
                    || componentesAulas.length != datasAulas.length
                    || componentesAulas.length != horasAulas.length
                    || componentesAulas.length != minutosAulas.length)
                    || componentesAulas.length != datasRec.length
                    || componentesAulas.length != horasRec.length
                    || componentesAulas.length != minutosRec.length) {
                model.addAttribute("error", true);
                response.sendRedirect("form.htm");
                return "form";
            }
            for (Integer motivo : motivos) {
                if (motivo == -1 && outro.trim().isEmpty()) {
                    model.addAttribute("error", true);
                    response.sendRedirect("form.htm");
                    return "form";
                }
            }

            /**
             * CONSTRUIR SOLICITACAO
             */
            Solicitacao solicitacao = new Solicitacao();

            datafim = datafim == null ? datainicio : datafim;
            solicitacao.setProfessorLdap(user.getUid());
            motivoAfastamentoService.getListByIds(motivos);
            Set<MotivoAfastamento> setMotivos = new HashSet<>();
            setMotivos.addAll(motivoAfastamentoService.getListByIds(motivos));
            solicitacao.setMotivosAfastamento(setMotivos);

            if (Arrays.asList(motivos).stream().anyMatch((Integer mt) -> {
                return mt == -1 && outro != null && !outro.trim().isEmpty();
            })) {
                solicitacao.setOutroMotivo(outro.trim());
            }
            solicitacao.setDatainicio(datainicio);
            solicitacao.setDatafim(datafim);
            try {
                solicitacao.setProfessor(usuarioService.getByLdap(session.getAttribute(SessionUtils.USER_UID).toString()));
            } catch (NamingException ex) {
                model.addAttribute("error", true);
                response.sendRedirect("form.htm");
                return "form";
            }

            /**
             * CONSTRUIR LISTA DE AULAS
             */
            List<AulaSolicitada> listAulas = new LinkedList<>();
            for (int i = 0; i < componentesAulas.length; i++) {

                AulaSolicitada aula = new AulaSolicitada();
                try {
                    aula.setProfSubstituto(usuarioService.getByLdap(professoresAulas[i]));
                } catch (NamingException ex) {
                    model.addAttribute("error", true);
                    response.sendRedirect("form.htm");
                    return "form";
                }
                
                aula.setComponente(componentesService.getById(componentesAulas[i]));
                Calendar cal = Calendar.getInstance();
                cal.setTime(datasAulas[i]);
                cal.add(Calendar.HOUR_OF_DAY, horasAulas[i]);
                cal.add(Calendar.MINUTE, minutosAulas[i]);
                datasAulas[i].setTime(cal.getTimeInMillis());
                aula.setDataAula(datasAulas[i]);
                cal.setTime(datasRec[i]);
                cal.add(Calendar.HOUR_OF_DAY, horasRec[i]);
                cal.add(Calendar.MINUTE, minutosRec[i]);
                datasRec[i].setTime(cal.getTimeInMillis());
                aula.setDataRecuperacao(datasRec[i]);

                listAulas.add(aula);
            }

            /**
             * CONSTRUIR MODEL
             */
            model.addAttribute("user", user);
            model.addAttribute("solicitacao", solicitacao);
            model.addAttribute("aulas", listAulas);
            return "confirma";
        }
        model.addAttribute("message","Você deve estar cadastrado como PROFESSOR para fazer esta solicitação.");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "error-pages/forbidden";
    }

    @RequestMapping(value = "/ajax/getprofsorestagiarios.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getProfsOrEstagiarios(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean estagiarios) {
        Map<String, Object> ret = new HashMap<>();
        try {
            Boolean autorized = usuarioService.userInGroup(request.getSession().getAttribute(SessionUtils.USER_UID).toString(), GrupoUsuarios.GRUPO_PROFESSORES);
            if (!autorized) {
                ret.put("success", false);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return ret;
            }
        } catch (NamingException ex) {
            ret.put("success", false);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        Comparator<Usuario> ordenaUsuarios = (Usuario o1, Usuario o2) -> {
            return o1.getNome().compareToIgnoreCase(o2.getNome());
        };
        Predicate<Usuario> notMe = (Usuario usuario) -> {
            return !usuario.getLdap().equals(request.getSession().getAttribute(SessionUtils.USER_UID));
        };
        try {
            Collection usuarios = usuarioService.getAllFromGroup(estagiarios ? GrupoUsuarios.GRUPO_ESTAGIARIOS : GrupoUsuarios.GRUPO_PROFESSORES)
                    .stream().filter(notMe).sorted(ordenaUsuarios).collect(Collectors.toList());
            ret.put("usuarios", usuarios);
            ret.put("success", true);
        } catch (NamingException ex) {
            ret.put("success", false);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        return ret;
    }

    private static void addToModel(String name, Object value, Model model) {
        if (value != null) {
            model.addAttribute(name, value);
        }
    }

}
