/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.service.AulaSolicitadaService;
import ufsm.ctism.service.MailService;
import ufsm.ctism.service.UsuarioService;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.SessionUser;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class SolicitacoesController {

    @Autowired
    AulaSolicitadaService aulaSolicitadaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    MailService mailService;
    
    @Value("${mail.solicita.cancela.from}")
    private String cancelaFrom;
    @Value("${mail.solicita.cancela.to}")
    private String cancelaTo;
    @Value("${mail.solicita.cancela.deped}")
    private String cancelaDepEd;
    @Value("${mail.solicita.aceita.recusa.deped}")
    private String acRecDepEd;
    @Value("${mail.acessar.app}")
    private String mailAcessarApp;
    
    /**
     * Controller para a tela de "minhas solicitações"
     *
     * @param request
     * @param response
     * @param tipo
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/minhas.htm")
    public String minhas(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "dataAula") String tipo,
            Model model) throws IOException {

        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("login.htm");
            return "login";
        }
        Collection<AulaSolicitada> aulas = aulaSolicitadaService.getBySolicitanteAndTipo(user.getUid(), tipo);
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        
        
        
        model.addAttribute("aulas", aulas);
        model.addAttribute("tipo", tipo);
        model.addAttribute("user", user);
        return "minhas";
    }

    /**
     * Controller para a tela das "solicitações para mim"
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/paramim.htm")
    public String paramim(HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {

        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("login.htm");
            return "login";
        }
        Integer permissoes = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        Boolean allow = ( permissoes & GrupoUsuarios.PERMISSAO_PROFESSORES ) > 0 || (permissoes & GrupoUsuarios.PERMISSAO_ESTAGIARIOS) > 0;
        if (allow) {
            Usuario professor;
            try {
                professor = usuarioService.getByLdap(user.getUid());
            } catch (NamingException ex) {
                response.sendRedirect("login.htm");
                return "login";
            }
            if (user == null) {
                response.sendRedirect("login.htm");
                return "login";
            }
            Collection aulas = aulaSolicitadaService.getBySubstitutoAndSituacao(new Situacao(Situacao.SITUACAO_SOLICITADA), professor.getLdap());

            Date agora = new Date();
            Collection proximas = aulaSolicitadaService.getBySubstitutoInSituacoes(
                    Arrays.asList(new Situacao(Situacao.SITUACAO_DEFERIDA), new Situacao(Situacao.SITUACAO_ACEITA)), professor.getLdap()).stream()
                    .filter((AulaSolicitada aula) -> {
                        return aula.getDataAula().compareTo(agora) >= 0;
                    }).collect(Collectors.toList());
            model.addAttribute("aulas", aulas);
            model.addAttribute("proximas", proximas);
            model.addAttribute("user", user);
            return "paramim";
        }
        response.sendRedirect("login.htm");
            return "login";
    }

    /**
     * Controller para updateAula ou negar aulas solicitadas
     *
     * @param request
     * @param response
     * @param id id da aula solicitada
     * @param aceita Boolean indicando se aceita ou não
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/ajax/responder.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> aceitar(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Integer id,
            @RequestParam Boolean aceita) throws IOException {
        
        Map<String, Object> ret = new HashMap<>();
        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ret.put("success", Boolean.FALSE);
            ret.put("msg", "Usuário não logado.");
            return ret;
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), null);
        AulaSolicitada aula = aulaSolicitadaService.getById(id);
        
        if ( (( ( ( permissions & GrupoUsuarios.PERMISSAO_PROFESSORES ) == 0) && ( ( permissions & GrupoUsuarios.PERMISSAO_ESTAGIARIOS ) == 0) ) || (!aula.getProfSubstitutoLdap().equals(user.getUid()))) ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ret.put("success", Boolean.FALSE);
            ret.put("msg", "Você não tem permissão para realizar esta ação. Por favor, contate o SSI.");
            return ret;
        }
        Situacao situacao = aceita
                ? new Situacao(Situacao.SITUACAO_ACEITA)
                : new Situacao(Situacao.SITUACAO_NEGADA);
        aula.setSituacao(situacao);
        if (aulaSolicitadaService.updateAula(aula)) {
            ret.put("success", Boolean.TRUE);
            String msg = aceita
                    ? "Solicitação aceita!\nAguardando o deferimento do Departamento de Educação."
                    : "Solicitação negada!";
            ret.put("msg", msg);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ret.put("success", Boolean.FALSE);
            ret.put("msg", "Erro interno. Por favor, contate o SSI.");
            return ret;
        }
        String mailBody = acRecDepEd
                .replace("{0}", user.getNome())
                .replace("{1}", aceita ? "aceitou" : "recusou")
                .replace("{2}", aula.getComponente().getNome())
                .replace("{3}", new SimpleDateFormat("dd/MM/YYYY', às 'HH'h 'mm'min'").format(aula.getDataAula()))
                .replace("{4}", aula.getSolicitacao().getProfessor().getNome());
        for (int i=0; i < 10; i++) {
            try {
                usuarioService.getAllFromGroup(GrupoUsuarios.GRUPO_DEPTO_EDUCACAO).forEach( u -> {
                    mailService.sendMailWithRetries(u.getMail(), "SOLICITAÇÃO DE SUBSTITUIÇÕES", mailBody, Boolean.TRUE);
                });
                break;
            } catch (NamingException retry) {}
        }
        return ret;
    }

    /**
     * Controller para tela de deferir ou indeferir solicitações (apenas super
     * user)
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/deferir.htm")
    public String deferir(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) throws IOException {
        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.sendRedirect("index.htm");
            return "index";
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), model);
        
        if ( ( ( permissions & GrupoUsuarios.PERMISSAO_SSI ) > 0 ) || ( (permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO ) > 0 ) ) {
            Situacao situacaoAceita = new Situacao(Situacao.SITUACAO_ACEITA);
            Collection aulas = aulaSolicitadaService.getBySituacao(situacaoAceita);
            aulas.stream().filter(a -> ((AulaSolicitada) a).getProfSubstituto() == null )
                    .forEach( a -> {
                        System.out.println(((AulaSolicitada)a).toString());
                    });
            model.addAttribute("aulas", aulas);
            model.addAttribute("user", user);
            return "deferir";
        }
        response.sendRedirect("index.htm");
        return "index";
    }

    /**
     * Controller para deferir ou indeferir solicitações
     *
     * @param request
     * @param response
     * @param id id da aula solicitada
     * @param aceita Boolean indicando se a solicitação será deferida ou não
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/ajax/deferir.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deferirAction(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Integer id,
            @RequestParam Boolean aceita) throws IOException {
        Map<String, Object> ret = new HashMap<>();
        SessionUser user = SessionUser.getUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ret.put("success", false);
            ret.put("msg", "Usuário não logado.");
            return ret;
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(user.getUid(), null);
        if ( ( (permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) == 0 ) && ( (permissions & GrupoUsuarios.PERMISSAO_SSI) == 0 ) ) {
            ret.put("success", false);
            ret.put("msg", "Você não tem permissão para deferir ou indeferir solicitações. Por favor, contate o SSI.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        AulaSolicitada aula = aulaSolicitadaService.getById(id);
        if (aula == null) {
            response.sendRedirect("index.htm");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ret.put("success", false);
            ret.put("msg", "Erro interno.");
            return ret;
        }

        Situacao situacaoNova = aceita
                ? new Situacao(Situacao.SITUACAO_DEFERIDA)
                : new Situacao(Situacao.SITUACAO_INDEFERIDA);
        aula.setSituacao(situacaoNova);
        if (aulaSolicitadaService.updateAula(aula)) {
            ret.put("success", Boolean.TRUE);
            ret.put("msg", (aceita ? "Deferimento" : "Indeferimento" ) + " feito com sucesso!");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ret.put("success", Boolean.FALSE);
            ret.put("msg", "Erro interno. Por favor, contate o SSI.");
        }
        return ret;
    }
    
    @RequestMapping(value = "/ajax/cancelaAula.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> cancelaAulaAction(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Integer idAula) throws NamingException, IOException {
        
        Map<String,Object> ret = new HashMap<>();
        SessionUser sessionUser = SessionUser.getUser(request);
        if (sessionUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ret.put("success", false);
            ret.put("msg", "Usuário não logado.");
            return ret;
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(sessionUser.getUid(), null);
        if ( ( (permissions & GrupoUsuarios.PERMISSAO_PROFESSORES ) == 0 ) ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ret.put("success", false);
            ret.put("msg", "Operação não permitida para este usuário.");
            return ret;
        }
        
        AulaSolicitada toDelete = aulaSolicitadaService.getById(idAula);
        
        Boolean sameUser = toDelete.getSolicitacao().getProfessorLdap().equals( sessionUser.getUid() );
        if (sameUser && usuarioService.userInGroup( sessionUser.getUid() , GrupoUsuarios.GRUPO_PROFESSORES) ) {
            Boolean success = aulaSolicitadaService.deleteAula(idAula);
//            Boolean success = Boolean.TRUE;
            ret.put( "success" , success );
            response.setStatus( success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            if (success) {
                ret.put("msg", "Aula cancelada com sucesso!");
                Usuario user = usuarioService.getByLdap(sessionUser.getUid());
                Usuario solicitado = usuarioService.getByLdap(toDelete.getProfSubstitutoLdap());
                String subject = "CANCELAMENTO DE SUBSTITUIÇÃO";
                
                String mailToUser = cancelaFrom.replace("{0}", user.getNome())
                        .replace("{1}", toDelete.getComponente().getNome())
                        .replace("{2}", usuarioService.userInGroup(solicitado.getLdap(), GrupoUsuarios.GRUPO_PROFESSORES) ? "professor" : "estagiário")
                        .replace("{3}", new SimpleDateFormat("dd/MM/YYYY', às 'HH'h' mm'min'").format(toDelete.getDataAula()))
                        + "\n" + mailAcessarApp;
                
                String mailToSolicitado = cancelaTo.replace("{0}", solicitado.getNome())
                        .replace("{1}", user.getNome())
                        .replace("{2}", toDelete.getComponente().getNome())
                        .replace("{3}", new SimpleDateFormat("dd/MM/YYYY', às 'HH'h' mm'min'").format(toDelete.getDataAula()))
                        + "\n" + mailAcessarApp;
                
                String mailToDepEd = cancelaDepEd.replace("{0}", user.getNome())
                        .replace("{1}", user.getNome())
                        .replace("{2}", toDelete.getComponente().getNome())
                        .replace("{3}", new SimpleDateFormat("dd/MM/YYYY', às 'HH'h' mm'min'").format(toDelete.getDataAula()))
                        + "\n" + mailAcessarApp;
                
                //mandar emails
                mailService.sendMail(user.getMail(), subject, mailToUser, Boolean.TRUE);
                mailService.sendMail(solicitado.getMail(), subject, mailToSolicitado, Boolean.TRUE);
                usuarioService.getAllFromGroup(GrupoUsuarios.GRUPO_DEPTO_EDUCACAO).forEach( u -> {
                    mailService.sendMailWithRetries(u.getMail(), subject, mailToDepEd, Boolean.TRUE);
                });
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ret.put( "success", false );
            ret.put( "msg" , "Aula solicitada por outro professor" );
        }
        return ret;
    }
    
    @RequestMapping(value = "/ajax/countsolicitacoes.action", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> countSolicitacoes(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String tipo ) throws NamingException {
        Map<String,Object> ret = new HashMap<>();
        SessionUser usr = SessionUser.getUser(request);
        if (usr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ret.put("msg", "Usuário não logado.");
            return ret;
        }
        Integer permissions = usuarioService.getPermissionsAndSetToModel(usr.getUid(), null);
        if ( false && (tipo.equalsIgnoreCase("paramim")) &&
                ( ((permissions & GrupoUsuarios.PERMISSAO_PROFESSORES) > 0 ) || ((permissions & GrupoUsuarios.PERMISSAO_ESTAGIARIOS) > 0) )) {
            Integer contagem = aulaSolicitadaService.getCountBySubstitutoAndSituacao(new Situacao(Situacao.SITUACAO_SOLICITADA), usr.getUid());
            ret.put("success", true);
            ret.put("cnt", contagem);
        } else if ( false && (tipo.equalsIgnoreCase("deferir")) &&
                ( ((permissions & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) > 0 ) || ((permissions & GrupoUsuarios.PERMISSAO_SSI) > 0) )) {
            Integer contagem = aulaSolicitadaService.getCountBySituacao(new Situacao(Situacao.SITUACAO_ACEITA));
            ret.put("success", true);
            ret.put("cnt", contagem);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ret.put("success", false);
            ret.put("cnt", -1);
        }
        return ret;
    }
    
    
}
