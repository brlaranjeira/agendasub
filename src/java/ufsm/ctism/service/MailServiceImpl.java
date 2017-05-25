/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.utils.HibernateUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class MailServiceImpl implements MailService {

    private final javax.mail.Session session;
    private final String smtpServer = "smtp1.ufsm.br";
    private final String defaultMailSender = "agendamento@ctism.ufsm.br";
    private final String defaultMailSenderName = "Agendamento Substituições";
    private final String debugMail = "ssi@ctism.ufsm.br";
    
    private MailServiceImpl() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", smtpServer);
        session = javax.mail.Session.getInstance(properties, null);
    }
    
    /**
     * 
     * @return list of maps, with keys: {MAIL (SOLICITANTE/SOLICITADO), NOME, SITUACAO_ID, SITUACAO_DESCRICAO, NUM_AULAS, LDAP (SOLICITANTE/SOLICITADO)
     */
    @Override
    public Collection<Map<String, Object>> getAulasSolicitadasForMail() {
        try {
            org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
            Collection<Situacao> situacoesSubstituto = Arrays.asList(
                    new Situacao(Situacao.SITUACAO_SOLICITADA),
                    new Situacao(Situacao.SITUACAO_DEFERIDA),
                    new Situacao(Situacao.SITUACAO_INDEFERIDA)
            );
            
            Collection<Map<String,Object>> list;
            
            list = dbSession.createCriteria(AulaSolicitada.class, "aula")
                    .createAlias("aula.situacao", "situacao")
                    .createAlias("aula.solicitacao", "solicitacao")
                    .createAlias("aula.componente", "componente")
                    .setProjection(Projections.projectionList()
                        .add(Projections.property("situacao.id"),"SITUACAO_ID")
                        .add(Projections.property("situacao.descricao"),"SITUACAO_DESCRICAO")
                        .add(Projections.property("aula.id"),"AULA_ID")
                        .add(Projections.property("aula.dataAula"),"DATA_AULA")
                        .add(Projections.property("aula.dataRecuperacao"),"DATA_RECUPERACAO")
                        .add(Projections.property("componente.nome"),"COMPONENTE")
//                        .add(Projections.count("situacao.id"),"NUM_AULAS")
                        .add(Projections.property("aula.profSubstitutoLdap"),"LDAP_SUBSTITUTO")
                        .add(Projections.property("solicitacao.professorLdap"),"LDAP_SOLICITANTE")
                        .add(Projections.property("aula.mailEnviado"),"MAIL_ENVIADO")
//                        .add(Projections.groupProperty("aula.profSubstitutoLdap"))
//                        .add(Projections.groupProperty("situacao.id"))
                    )
                    .add(Restrictions.ne("aula.mailEnviado", new Integer(AulaSolicitada.AVISO_SUBSTITUTO | AulaSolicitada.AVISO_SOLICITANTE | AulaSolicitada.AVISO_DEPTO ) ))
                    .setResultTransformer(org.hibernate.Criteria.ALIAS_TO_ENTITY_MAP)
                    .addOrder(Order.asc("situacao.id"))
                    .list();
            

            dbSession.close();
            return list;
        } catch (Throwable ex) {
            ex.printStackTrace();
            System.out.println("aa");
            return new LinkedHashSet<>();
        }
    }
    /*
    @Override
    public Boolean setMailEnviado(Collection<Integer> idAulas, Collection profsEmailEnviado) {
        Boolean ret = Boolean.TRUE;
        Collection ldaps = (Collection) profsEmailEnviado.stream()
                .map( u -> {
                    return ((Usuario) u).getLdap();
                }).collect(Collectors.toList());
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        dbSession.beginTransaction();
        try {
            String hql = "update AulaSolicitada set mailEnviado = :novo where id in (:ids) and mailEnviado = :antigo and profSubstitutoLdap in (:profs)";
            dbSession.createQuery(hql)
                    .setString("novo", "1")
                    .setParameterList("ids", idAulas)
                    .setString("antigo", "0")
                    .setParameterList("profs", ldaps)
                    .executeUpdate();
            String a = dbSession.getTransaction().toString();
            dbSession.getTransaction().commit();
        } catch (Throwable ex) {
            if (dbSession.getTransaction() != null) {
                dbSession.getTransaction().rollback();
            }
            ret = Boolean.FALSE;
        } finally {
            dbSession.close();
            return ret;
        }
    }
    */
    @Override
    public Boolean sendMail(String to, String subject, String body, Boolean debug) {
        return sendMail(defaultMailSender, defaultMailSenderName, to, subject, body, debug);
    }
    
    @Override
    public Boolean sendMail(String fromMail, String fromName, String to, String subject, String body, Boolean debug) {
        to = debug ? debugMail : to;
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromMail, fromName));
//            msg.setFrom(new InternetAddress("no_reply@journaldev.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(fromMail, false));
//            msg.setReplyTo(InternetAddress.parse("no_reply@journaldev.com", false));
            msg.setSubject( subject , "UTF-8" );
            msg.setText( body , "UTF-8" );
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            Transport.send(msg);
            return Boolean.TRUE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
//        return Boolean.TRUE;
    }
    
    @Override
    public void sendMailWithRetries(String to, String subject, String body, Boolean debug) {
        new Thread() {
            @Override
            public void run() {
                Boolean sent = false;
                Integer times = 0;
                while (!sent && times < 500) {
                    sent = sendMail(to, subject, body, debug);
                    if (!sent) {
                        try {
                            Thread.sleep(1000L * 60L * 1L);
                        } catch (InterruptedException ignore) {}
                    }
                }
            }
        }.start();
    }

    
    
}
