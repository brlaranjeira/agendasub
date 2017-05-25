/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.service.AulaSolicitadaService;
import ufsm.ctism.service.MailService;
import ufsm.ctism.service.UsuarioService;

/**
 *
 * @author SSI-Bruno
 */
public class MailsController {

    @Value("${app.url}")
    private String appURL;
    @Value("${mail.lembrete.assunto}")
    private String lembreteAssunto;
    @Value("${mail.lembrete.head}")
    private String lembreteHead;
    @Value("${mail.lembrete.aula}")
    private String lembreteAula;
    @Value("${mail.novidades.head}")
    private String novidadesHead;
    @Value("${mail.novidades.solicitadas}")
    private String novidadesSolicitadas;
    @Value("${mail.novidades.modificadas}")
    private String novidadesModificadas;
    @Value("${depto.ensino.mail}")
    private String deptoEnsinoMail;
    @Value("${mail.acessar.app}")
    private String mailAcessarApp;
    @Value("${mail.ola}")
    private String mailOla;
    @Value("${mode.debug}")
    private Boolean debug;
    @Value("${mail.aula.detalhes}")
    private String aulaDetalhes;

    @Autowired
    AulaSolicitadaService aulaSolicitadaService;
    @Autowired
    MailService mailService;
    @Autowired
    UsuarioService usuarioService;

//    @Scheduled(cron = "0 22 11 * * ?")
    @Scheduled(cron = "0 30 12 * * ?") //executado uma vez por dia, as 12h 30min
    public void lembretesVespera() {

        Calendar ini = Calendar.getInstance();
        ini.setTime(new Date());
        ini.add(Calendar.DATE, 1);
        ini.set(Calendar.HOUR_OF_DAY, 0);
        ini.set(Calendar.MINUTE, 0);
        ini.set(Calendar.SECOND, 0);
        ini.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(ini.getTimeInMillis());
        end.add(Calendar.DATE, 1);

        /**
         * mapas
         */
        Map<String, List<AulaSolicitada>> profsToAulasSub = new HashMap<>();
        Map<String, List<AulaSolicitada>> profsToAulasRec = new HashMap<>();
        aulaSolicitadaService.getAllInDateInterval(ini, end).stream().forEach((aula) -> {
            String ldap = aula.getProfSubstitutoLdap();
            if (!profsToAulasSub.containsKey(ldap)) {
                profsToAulasSub.put(ldap, new LinkedList<>());
            }
            profsToAulasSub.get(ldap).add(aula);
        });
        aulaSolicitadaService.getAllWithRecInDateInterval(ini, end).stream().forEach((aula) -> {
            String ldap = aula.getSolicitacao().getProfessorLdap();
            if (!profsToAulasRec.containsKey(ldap)) {
                profsToAulasRec.put(ldap, new LinkedList<>());
            }
            profsToAulasRec.get(ldap).add(aula);
        });
        /**
         * emails
         */
        profsToAulasSub.values().forEach(aulasProf -> {
            Usuario prof = aulasProf.get(0).getProfSubstituto();
            StringBuilder body = new StringBuilder().append(mailOla.replace("{0}", prof.getNome()) + "\n");
            body.append(lembreteHead
                    .replace("{0}", Integer.toString(aulasProf.size()))
                    .replace("{1}", "substituir"));
            aulasProf.forEach(aula -> {
                body.append(lembreteAula
                        .replace("{0}", new SimpleDateFormat("HH'h' mm'min'").format(aula.getDataAula()))
                        .replace("{1}", aula.getComponente().getNome()) + "\n");
            });
            body.append(mailAcessarApp);
            mailService.sendMailWithRetries(prof.getMail(), lembreteAssunto.replace("{0}", "substituir"), body.toString(), debug);
        });
        profsToAulasRec.values().stream().forEach(aulasProf -> {
            Usuario prof = aulasProf.get(0).getSolicitacao().getProfessor();
            StringBuilder body = new StringBuilder().append(mailOla.replace("{0}", prof.getNome()) + "\n");
            body.append(lembreteHead
                    .replace("{0}", Integer.toString(aulasProf.size()))
                    .replace("{1}", "recuperar"));
            aulasProf.stream().forEach((aula) -> {
                body.append(lembreteAula
                        .replace("{0}", new SimpleDateFormat("HH'h' mm'min'").format(aula.getDataAula()))
                        .replace("{1}", aula.getComponente().getNome()) + "\n");
            });
            body.append(mailAcessarApp);
            mailService.sendMailWithRetries(prof.getMail(), lembreteAssunto.replace("{0}", "recuperar"), body.toString(), debug);
        });
    }

//    @Scheduled(cron = "0,15,30,45 * * * * ?")
//    @Scheduled(cron = "0 0,5,10,15,20,25,30,35,40,45,50,55 * * * ?") //executado a cada 5 minutos
    @Scheduled(cron = "0 * * * * ?") //executado a cada 5 minutos
    public void avisoNovidades() {

//        aulaSolicitadaService.getAllIds();
        /**
         * FORMA LISTA PARA EMAILS
         */
        Collection<Integer> idAulas = aulaSolicitadaService.getAll().stream()
                .map(aula -> aula.getId())
                .collect(Collectors.toList());

        Collection<Map<String, Object>> listAulas = mailService.getAulasSolicitadasForMail();
        if (listAulas.isEmpty()) {
            return;
        }
        Map<String, List<Map<String, Object>>> profsToRowList = new HashMap<>();
        Collection<Map<String, Object>> aulasMailDepto = new LinkedHashSet<>();
        listAulas.forEach(row -> {
            Integer mailEnviado = Integer.parseInt(row.get("MAIL_ENVIADO").toString());
            if ((mailEnviado & ufsm.ctism.dao.AulaSolicitada.AVISO_SOLICITANTE) == 0) {
                String ldapSolicitante = row.get("LDAP_SOLICITANTE").toString();
                profsToRowList.putIfAbsent(ldapSolicitante, new LinkedList<Map<String, Object>>());
                profsToRowList.get(ldapSolicitante).add(row);
            }
            if ((mailEnviado & ufsm.ctism.dao.AulaSolicitada.AVISO_SUBSTITUTO) == 0) {
                String ldapSubstituto = row.get("LDAP_SUBSTITUTO").toString();
                profsToRowList.putIfAbsent( ldapSubstituto , new LinkedList<Map<String, Object>>() );
                profsToRowList.get(ldapSubstituto).add(row);
            }
            if ( ( mailEnviado & ufsm.ctism.dao.AulaSolicitada.AVISO_DEPTO ) == 0 ) {
                aulasMailDepto.add( row );
            }
        });
        Map<Integer, AulaSolicitada> idToAulaSolicitada = new HashMap<>();

        //FUNCAO QUE ADICIONA AS AULAS EM UMA STRINGBUILDER
        BiConsumer<StringBuilder, Collection<Map<String, Object>>> mailWriter = (sb, array) -> {
            String situacao = array.iterator().next().get("SITUACAO_DESCRICAO").toString();
            sb.append("\n" + novidadesModificadas
                .replace("{0}", (new Integer(array.size())).toString())
                .replace("{1}", situacao))
              .append('\n');
            for (Map<String, Object> curr : array) {
                String solicitante, substituto;
                try {
                    solicitante = usuarioService.getByLdap(curr.get("LDAP_SOLICITANTE").toString()).getNome();
                } catch (NamingException ex) {
                    solicitante = curr.get("LDAP_SOLICITANTE").toString();
                }
                try {
                    substituto = usuarioService.getByLdap(curr.get("LDAP_SUBSTITUTO").toString()).getNome();
                } catch (NamingException ex) {
                    substituto = curr.get("LDAP_SUBSTITUTO").toString();
                }
                java.text.DateFormat df = new SimpleDateFormat("dd/MM/yyyy 'às' HH'h' mm'min'");
                String componente = curr.get("COMPONENTE").toString();
                Date recDate = new Date(((java.sql.Timestamp) curr.get("DATA_RECUPERACAO")).getTime());
                Date aulaDate = new Date(((java.sql.Timestamp) curr.get("DATA_AULA")).getTime());

                sb.append("******************************\n")
                    .append("\t" + aulaDetalhes
                    .replace("{0}", curr.get("COMPONENTE").toString())
                    .replace("{1}", df.format(aulaDate))
                    .replace("{2}", solicitante)
                    .replace("{3}", substituto)
                    .replace("{4}", df.format(recDate))
                    .replace("{5}", situacao)
                    .replaceAll("\n", "\n\t")
                ).append('\n');
            }
            array.clear();
        };
        
        /**
         * ITERA PELOS USUARIOS QUE TEM EMAIL PARA RECEBER
         */
        profsToRowList.entrySet().stream().forEach(entry -> {
            try {
                /**
                 * INFORMACOES DO USUARIO
                 */
                List<Map<String, Object>> aulas = entry.getValue();
                String ldapStr = entry.getKey();
                Usuario ldapUser = usuarioService.getByLdap(ldapStr);
                if (ldapUser == null) {
                    for (Map<String, Object> aula : aulas) {
                        Integer id = Integer.parseInt(aula.get("AULA_ID").toString());
                        idToAulaSolicitada.putIfAbsent(id, aulaSolicitadaService.getById(id));
                        AulaSolicitada aulaObj = idToAulaSolicitada.get(id);
                        if (ldapStr.equals(aula.get("LDAP_SOLICITANTE"))) {
                            aulaObj.setMailEnviado(aulaObj.getMailEnviado() | AulaSolicitada.AVISO_SOLICITANTE);
                        }
                        if (ldapStr.equals(aula.get("LDAP_SUBSTITUTO"))) {
                            aulaObj.setMailEnviado(aulaObj.getMailEnviado() | AulaSolicitada.AVISO_SUBSTITUTO);
                        }
                    }
                }
                String to = ldapUser.getMail();
                String nome = ldapUser.getNome();
                StringBuilder body = new StringBuilder();

                /**
                 * ESCRITA DO EMAIL
                 */
                body.append(mailOla.replace("{0}", nome) + "\n");
                body.append(novidadesHead + "\n");

                Map<String, Object> aulaAnterior = null;
                Collection<Map<String, Object>> aulasSituacao = new ArrayList<>();

                
                
                for (Map<String, Object> aula : aulas) {
                    if (aulaAnterior != null && !aula.get("SITUACAO_ID").equals(aulaAnterior.get("SITUACAO_ID"))) {//MUDOU SITUACAO
                        mailWriter.accept(body, aulasSituacao);
                    }
                    aulasSituacao.add(aula);
                    aulaAnterior = aula;
                }
                mailWriter.accept(body, aulasSituacao);
                
                body.append("\n").append(mailAcessarApp);
                
                Boolean mailSent = mailService.sendMail(to, novidadesHead, body.toString(), debug);
                if (mailSent) {
                    for (Map<String, Object> aula : aulas) {
                        Integer id = Integer.parseInt(aula.get("AULA_ID").toString());
                        idToAulaSolicitada.putIfAbsent(id, aulaSolicitadaService.getById(id));
                        AulaSolicitada aulaObj = idToAulaSolicitada.get(id);
                        if (aula.get("LDAP_SUBSTITUTO").equals(ldapStr)) {
                            aulaObj.setMailEnviado(aulaObj.getMailEnviado() | ufsm.ctism.dao.AulaSolicitada.AVISO_SUBSTITUTO);
                        } else {
                            aulaObj.setMailEnviado(aulaObj.getMailEnviado() | ufsm.ctism.dao.AulaSolicitada.AVISO_SOLICITANTE);
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println(t.getCause());
            }

        });

        Map<String,Object> aulaAnterior = null;
        Collection<Map<String,Object>> aulas = new LinkedList<>();
        StringBuilder body = new StringBuilder();
        body.append(novidadesHead).append('\n');
        for (Map<String, Object> aula : aulasMailDepto) {
            if (aulaAnterior != null && !aula.get("SITUACAO_ID").equals(aulaAnterior.get("SITUACAO_ID"))) {
                mailWriter.accept(body, aulas);
            }
            aulas.add(aula);
            aulaAnterior = aula;
        }
        mailWriter.accept(body, aulas);
        body.append( "\n" + mailAcessarApp );
        Boolean mailSent = mailService.sendMail(deptoEnsinoMail, "Há novidades nas solicitações de substituição de aulas", body.toString(), debug);
        if (mailSent) {
            for (Map<String, Object> aula : aulasMailDepto) {
                Integer id = Integer.parseInt(aula.get("AULA_ID").toString());
                idToAulaSolicitada.putIfAbsent(id, aulaSolicitadaService.getById(id));
                AulaSolicitada aulaObj = idToAulaSolicitada.get(id);
                aulaObj.setMailEnviado( aulaObj.getMailEnviado() | AulaSolicitada.AVISO_DEPTO );
            }
        }
        idToAulaSolicitada.values().forEach( aula -> aulaSolicitadaService.updateAula(aula) );
    }}

