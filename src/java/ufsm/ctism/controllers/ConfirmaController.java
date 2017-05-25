/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Componente;
import ufsm.ctism.dao.MotivoAfastamento;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.dao.Solicitacao;
import ufsm.ctism.service.*;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.SessionUser;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class ConfirmaController {

    @Value("${msg.internal.error}")
    private String internalErrorMessage;
    @Value("${msg.solicitacao.success}")
    private String successMessage;
    @Value("${mail.ola}")
    private String mailOla;
    @Value("${depto.ensino.mail}")
    private String deptoEnsinoMail;
    @Value("${mail.extrato.assunto}")
    private String extratoAssunto;
    @Value("${mail.extrato.head}")
    private String extratoHead;
    @Value("${mail.extrato.head.deped}")
    private String extratoHeadDepEd;
    @Value("${mail.extrato.dias.sing}")
    private String extratoDiasSing;
    @Value("${mail.extrato.dias.mult}")
    private String extratoDiasMult;
    @Value("${mail.extrato.motivos}")
    private String extratoMotivos;
    @Value("${mail.extrato.aulas}")
    private String extratoAulas;
    @Value("${mail.extrato.aula.detalhes}")
    private String extratoAulaDetalhe;
    @Value("${mail.acessar.app}")
    private String mailAcessarApp;
    @Value("${mode.debug}")
    Boolean debug;

    @Autowired
    MotivoAfastamentoService motivoAfastamentoService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    SolicitacaoService solicitacaoService;
    @Autowired
    ComponentesService componentesService;
    @Autowired
    MailService mailService;

    /**
     * Controller para a ação de confirmar uma solicitação ou não
     *
     * @param request
     * @param response
     * @param datainicio data de inicio do afastamento
     * @param datafim data de término do afastamento
     * @param motivos vetor com os ids dos motivos do afastamento
     * @param componentes vetor com os ids dos componente das aulas solicitadas
     * @param professores vetor com os ids dos professores substitutos
     * @param dtAulas vetor com as datas (objetos com data e horário) das aulas
     * solicitadas
     * @param dtRecs vetor com as datas (objetos com data e horário) das
     * recuperações das aulas solicitadas
     * @param outro outro motivo (opcional) para afastamento
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/ajax/confirma.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> confirma(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date datainicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date datafim,
            @RequestParam(required = false, value = "motivo") Integer[] motivos,
            @RequestParam(value = "componente") Integer[] componentes,
            @RequestParam(value = "professor") String[] professores,
            @RequestParam(value = "dtaula") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date[] dtAulas,
            @RequestParam(value = "dtrec") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date[] dtRecs,
            @RequestParam(required = false) String outro) throws IOException {

        Map<String, Object> ret = new HashMap<>();
        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            ret.put("success", false);
            ret.put("msg", "Você não está logado!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ret;
        }
        Integer permission = usuarioService.getPermissionsAndSetToModel(user.getUid(), null);
        if ((permission & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0) {
            StringBuilder mailToSolicitante = new StringBuilder();
            StringBuilder mailToDepEd = new StringBuilder();
            mailToSolicitante.append(mailOla.replace("{0}", user.getNome()) + "\n");
            mailToSolicitante.append(extratoHead.replace("{0}", Integer.toString(componentes.length))).append(" ");
            mailToDepEd.append(extratoHeadDepEd
                    .replace("{0}", user.getNome())
                    .replace("{1}", Integer.toString(componentes.length))).append(" ");
            if (datainicio.compareTo(datafim) == 0) {
                Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> {
                    sb.append(extratoDiasSing.replace("{0}", new SimpleDateFormat("dd/MM/yyyy").format(datainicio)));
                });
            } else {
                String str = extratoDiasMult
                        .replace("{0}", new SimpleDateFormat("dd/MM/yyyy").format(datainicio))
                        .replace("{1}", new SimpleDateFormat("dd/MM/yyyy").format(datafim));
                Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> {
                    sb.append(str);
                });
            }
            Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> {sb.append(" ").append(extratoMotivos);});

            if (motivos != null && motivos.length > 0) {
                motivoAfastamentoService.getListByIds(motivos).forEach((MotivoAfastamento motivo) -> {
                    Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> sb.append("\n\t*").append(motivo.getDescricao()));
                });
            }
            

            if (outro != null && !outro.trim().isEmpty()) {
                Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> {
                    try {
                        sb.append("\n\t*").append(URLDecoder.decode(outro, "UTF-8").trim());
                    } catch (UnsupportedEncodingException skip) {
                    }
                });
            }
            Solicitacao solicitacao = new Solicitacao();
            solicitacao.setDatainicio(datainicio);
            solicitacao.setDatafim(datafim);
            Set<MotivoAfastamento> setMotivos = new HashSet<>();
            if (motivos != null && motivos.length > 0) {
                setMotivos.addAll(motivoAfastamentoService.getListByIds(motivos));
                solicitacao.setMotivosAfastamento(setMotivos);
            }
            
            solicitacao.setDataSolicitacao(new Date());

            try {
                solicitacao.setProfessor(usuarioService.getByLdap(user.getUid()));
            } catch (NamingException ex) {
                ret.put("success", false);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return ret;
            }
            if (outro != null && !outro.trim().isEmpty()) {
                solicitacao.setOutroMotivo(URLDecoder.decode(outro, "UTF-8").trim());
            }

            Collection<AulaSolicitada> aulas = new LinkedList<>();
            HashMap<Usuario, List<AulaSolicitada>> profsToAulas = new HashMap<>();
            Situacao situacao = new Situacao(Situacao.SITUACAO_SOLICITADA);
            Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> sb.append("\n").append(extratoAulas));
            for (int i = 0; i < componentes.length; i++) {
                AulaSolicitada aula = new AulaSolicitada();
                aula.setSituacao(situacao);
                aula.setSolicitacao(solicitacao);
                Componente componente = componentesService.getById(componentes[i]);
                aula.setComponente(componente);
                Usuario professor;
                try {
                    professor = usuarioService.getByLdap(professores[i]);
                } catch (NamingException ex) {
                    ret.put("success", false);
                    response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                    return ret;
                }
                aula.setProfSubstituto(professor);
                aula.setDataAula(dtAulas[i]);
                aula.setDataRecuperacao(dtRecs[i]);
                SimpleDateFormat dtHoraFormatter = new SimpleDateFormat("dd/MM/yyyy 'às' HH'h' mm'min'");
                final int ii = i;
                Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> {
                    sb.append("\n").append("Aula ").append(ii + 1).append(":");
                    sb.append(extratoAulaDetalhe
                            .replace("{0}", componente.getNome())
                            .replace("{1}", dtHoraFormatter.format(dtAulas[ii]))
                            .replace("{2}", professor.getNome())
                            .replace("{3}", dtHoraFormatter.format(dtRecs[ii])));
                });
                aula.setMailEnviado(0);
                aulas.add(aula);

                if (!profsToAulas.containsKey(professor)) {
                    profsToAulas.put(professor, new LinkedList<>());
                }
                profsToAulas.get(professor).add(aula);
            }
            if (!solicitacaoService.save(solicitacao, aulas)) {
                ret.put("success", false);
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                return ret;
            }
            Stream.of(mailToSolicitante, mailToDepEd).forEach(sb -> sb.append("\n").append(mailAcessarApp));
            System.out.println( mailToSolicitante.toString() );
            mailService.sendMailWithRetries(solicitacao.getProfessor().getMail(), extratoAssunto, mailToSolicitante.toString(), debug);
            for (int i = 0; i < 10; i++) {
                try {
                    mailService.sendMailWithRetries(deptoEnsinoMail, extratoAssunto, mailToDepEd.toString(), debug);
//                    usuarioService.getAllFromGroup(GrupoUsuarios.GRUPO_DEPTO_EDUCACAO).forEach(u -> {
//                        System.out.println("mail: " + u.getMail());
//                        mailService.sendMailWithRetries(u.getMail(), extratoAssunto, mailToDepEd.toString(), debug);
//                    });
                    break;
                } catch (Exception retry) {}
            }

            System.out.println("mandar extrato completo para: " + solicitacao.getProfessorLdap());
            response.setStatus(HttpServletResponse.SC_OK);
            ret.put("msg", successMessage);
            return ret;
        }
        ret.put("success", false);
        ret.put("msg", "Você deve estar cadastrado como PROFESSOR para fazer esta solicitação.");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return ret;
    }
    
    @RequestMapping(value = "/testar/teste.php", method = RequestMethod.GET)
    public String asd( HttpServletRequest request,
            HttpServletResponse response ) {
        return "teste";
    }
    
}
