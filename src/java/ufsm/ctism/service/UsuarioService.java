/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import org.springframework.ui.Model;
import ufsm.ctism.dao.Usuario;

/**
 *
 * @author SSI-Bruno
 */
public interface UsuarioService {
    
//    public static final Integer GRUPO_PROFESSORES = 10001;
//    public static final Integer GRUPO_DEPTO_EDUCACAO = 10006;
//    public static final Integer GRUPO_SSI = 10004;
//    public static final Integer GRUPO_BOLSISTAS = 10003;
//    public static final Integer GRUPO_ESTAGIARIOS = 10008;

    public Usuario getByLdap(String ldap) throws NamingException;

    public Boolean userInGroup(String ldap, Integer group) throws NamingException;
    
    public Collection<Usuario> getAll() throws NamingException;
    
    public Collection<Usuario> getAllFromGroup(Integer group) throws NamingException;
    
    public Collection<Usuario> getAllFromGroups(Integer[] groups) throws NamingException;

    public Boolean auth(String user, String pw) throws ServiceUnavailableException;
    
    public Boolean hasSystemAccess(String user) throws NamingException;
    
    public Integer getPermissionsAndSetToModel(String usuario , Model model);
    
    public Boolean changeName(Usuario usr, String name) throws ServiceUnavailableException;
    
    public Boolean changeMail(Usuario usr, String mail) throws ServiceUnavailableException;
    
}
