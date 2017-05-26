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
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.utils.HibernateUtils;
import ufsm.ctism.utils.JDBCUtils;

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
            String sql = "SELECT sit.id AS SITUACAO_ID, sit.descricao AS SITUACAO_DESCRICAO, aula.id AS AULA_ID, aula.dt_aula AS DATA_AULA, aula.dt_recuperacao AS DATA_RECUPERACAO, comp.nome AS COMPONENTE, aula.id_prof_substituto AS LDAP_SUBSTITUTO, sol.id_professor AS LDAP_SOLICITANTE, aula.mail_enviado AS MAIL_ENVIADO FROM CTISM_SOLICITA_AULA_SOLICITADA aula INNER JOIN CTISM_COMPONENTE comp ON aula.id_componente = comp.idcomponente INNER JOIN CTISM_SOLICITA_SITUACAO sit ON aula.id_situacao = sit.id INNER JOIN CTISM_SOLICITA_SOLICITACAO sol ON aula.id_solicitacao = sol.id WHERE aula.mail_enviado <> ? ORDER BY sit.id ASC";
            Collection<Object> al = new ArrayList<>();
            al.add( AulaSolicitada.AVISO_SUBSTITUTO | AulaSolicitada.AVISO_SOLICITANTE | AulaSolicitada.AVISO_DEPTO );
            Collection<Map<String,Object>> list = JDBCUtils.query(sql, al);
            return list;
        } catch (Throwable ex) {
            ex.printStackTrace();
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
