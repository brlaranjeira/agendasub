/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe singleton com utilitários para envio de email
 *
 * @author SSI-Bruno
 */
public class MailUtils {

    private static MailUtils instance;

    private final Session session;
    private final String smtpServer = "smtp1.ufsm.br";
    private final String defaultMailSender = "agendamento@ctism.ufsm.br";
    private final String defaultMailSenderName = "Agendamento Substituições";
    private final String debugMail = "brlaranjeira@ctism.ufsm.br";

    private MailUtils() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", smtpServer);
        session = Session.getInstance(properties, null);
    }

    /**
     * Método para obtenção de um objeto da classe
     *
     * @return um objeto instanciado desta classe
     */
    public static MailUtils getInstance() {
        if (instance == null) {
            instance = new MailUtils();
        }
        return instance;
    }

    /**
     * 
     * @param to destinatário
     * @param subject assunto
     * @param body corpo da mensagem
     * @return Boolean indicando se a mensagem foi devidamente enviada
     */
//    public Boolean sendMail(String to, String subject, String body) {
//        return sendMail(to, subject, body, Boolean.FALSE);
//    }
    

    /**
     * Envia email com remetente com nome e email padrão
     *
     * @param to destinatário
     * @param subject assunto
     * @param body corpo da mensagem
     * @param debug se for debug, vai mandar email para o SSI.
     * @return Boolean indicando se a mensagem foi devidamente enviada
     */
    public Boolean sendMail(String to, String subject, String body, Boolean debug) {
        return sendMail(defaultMailSender, defaultMailSenderName, to, subject, body, debug);
    }

    /**
     * Envia email
     *
     * @param fromMail email do remetente
     * @param fromName nome do remetente
     * @param to destinatário
     * @param subject assunto
     * @param body corpo da mensagem
     * @param debug se for debug, vai mandar email para o SSI.
     * @return Boolean indicando se a mensagem foi devidamente enviada
     */
    public Boolean sendMail(String fromMail, String fromName, String to, String subject, String body, Boolean debug) {
        debug = Boolean.TRUE;
        if (debug) {
            to = debugMail;
        }
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromMail, fromName));
//            msg.setFrom(new InternetAddress("no_reply@journaldev.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(fromMail, false));
//            msg.setReplyTo(InternetAddress.parse("no_reply@journaldev.com", false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            Transport.send(msg);
            return Boolean.TRUE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }
    
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
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            }
        }.start();
    }
}
