/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import java.util.Map;
import ufsm.ctism.dao.Usuario;

/**
 *
 * @author SSI-Bruno
 */
public interface MailService {
    
    public Collection<Map<String,Object>> getAulasSolicitadasForMail();

    //public Boolean setMailEnviado(Collection<Integer> idAulas, Collection profsEmailEnviado);
    
    public void sendMailWithRetries(String to, String subject, String body, Boolean debug);
 
    public Boolean sendMail(String to, String subject, String body, Boolean debug);
    
    public Boolean sendMail(String fromMail, String fromName, String to, String subject, String body, Boolean debug);
    
}
