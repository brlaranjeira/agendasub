/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hibernate.criterion.Restrictions;
import ufsm.ctism.dao.*;
import static ufsm.ctism.utils.SessionUtils.*;

/**
 * Classe que gerencia o usuário
 * @author SSI-Bruno
 * 
 */
public class SessionUser {
    
    private String uid;
    private String nome;
    private Set<GrupoUsuarios> grupos;

    /**
     * 
     * @param uid login do usuário no ldap
     * @param nome nome do usuário no ldap
     */
    public SessionUser(String uid, String nome) {
        this.uid = uid;
        this.nome = nome;
    }

    

    /**
     * 
     * @return login do usuário no ldap
     */
    public String getUid() {
        return uid;
    }

    /**
     * 
     * @param uid login do usuário no ldap
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 
     * @return nome do usuário no ldap
     */
    public String getNome() {
        return nome;
    }

    /**
     * 
     * @param nome nome do usuário no ldap
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    /**
     * verifica se há um usuário logado no objeto HttpSession contido no parâmetro passado.
     * @param request um objeto HttpServletRequest, que contém uma sessão com um usuário logado
     * @return o usuário logado na sessão contida no objeto HttpServletRequest passado como parâmetro ou null, caso não haja usuário logado.
     */
    public static SessionUser getUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
//        if (session.getAttribute(USER_UID) == null) {
//            return null;
//        }
        try {
            return new SessionUser(session.getAttribute(USER_UID).toString(),session.getAttribute(USER_FULLNAME).toString());
        }catch (Exception ex) {
            return null;
        }
//        Usuario usr = (Usuario) HibernateUtils.getInstance().getSession().createCriteria(Usuario.class)
//                .add(Restrictions.eq("ldap", currentUser.uid))
//                .uniqueResult();
//        if (usr == null) {
//            return null;
//        }
    }
    
}
