/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.util.Arrays;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * Classe singleton para consultas ao ldap
 *
 * @author SSI-Bruno
 */
public class Ldap {

    private final static String ldapAddress = "";
    private final static String ldapPort = "389";
    private final static String ldapServer = "ldap://" + ldapAddress + ":" + ldapPort;
    private final static String ldapAdmin = "cn=admin,dc=intranet,dc=ctism,dc=ufsm,dc=br";
    private final static String ldapAdminCredentials = "";
    public final static String LDAP_PEOPLE_BASE = "ou=people,dc=intranet,dc=ctism,dc=ufsm,dc=br";
    public final static String LDAP_GROUP_BASE = "ou=groups,dc=intranet,dc=ctism,dc=ufsm,dc=br";
    private static LdapContext adminContext;
    private static final java.util.Hashtable environment = new java.util.Hashtable<>();
    private static Ldap instance = null;

    private Ldap() throws ServiceUnavailableException {
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
//        environment.put(Context.REFERRAL, "follow");

        environment.put(Context.PROVIDER_URL, ldapServer);
        environment.put(Context.SECURITY_PRINCIPAL, ldapAdmin);
        environment.put(Context.SECURITY_CREDENTIALS, ldapAdminCredentials);

        try {
            adminContext = new InitialLdapContext(environment, null);
            environment.remove(Context.SECURITY_PRINCIPAL);
            environment.remove(Context.SECURITY_CREDENTIALS);
            if (adminContext == null) {
                throw new ServiceUnavailableException();
            }
        } catch (NamingException ex) {
            throw new ServiceUnavailableException();
        }
    }

    /**
     * Método para obtenção de um objeto da classe
     *
     * @return instância de um objeto desta classe.
     * @throws ServiceUnavailableException se o ldap não estiver disponível
     */
    public static Ldap getInstance() throws ServiceUnavailableException {

        if (instance == null) {
            instance = new Ldap();
        }
        return instance;
    }

    /**
     * autentica um usuário e senha no ldap
     *
     * @param usr usuário do ldap
     * @param pw senha do usuário
     * @return um Boolean indicando se o login foi aceito
     * @throws ServiceUnavailableException quando ocorre algum erro com o LDAP
     */
    public Boolean authLogin(String usr, String pw) throws ServiceUnavailableException {
        SearchControls sc = new SearchControls();
        String[] attributeFilter = {"cn"};
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setCountLimit(1);
        sc.setTimeLimit(10000);
        try {
            NamingEnumeration<SearchResult> searchResults = adminContext.search(LDAP_PEOPLE_BASE, "(|(uid=" + usr + "))", sc);
            if (!searchResults.hasMore()) {
                return Boolean.FALSE;
            }
            String cn = searchResults.next().getAttributes().get("cn").get().toString();
            environment.put(Context.SECURITY_PRINCIPAL, "cn=" + cn + "," + LDAP_PEOPLE_BASE);
            environment.put(Context.SECURITY_CREDENTIALS, pw);
            try {
                InitialLdapContext context = new InitialLdapContext(environment, null); //testar autenticacao
                environment.remove(Context.SECURITY_PRINCIPAL);
                environment.remove(Context.SECURITY_CREDENTIALS);
                if (context == null) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            } catch (Exception ex) {
                return Boolean.FALSE;
            }
        } catch (NamingException ex) {
            throw new ServiceUnavailableException();
        }
    }
    
    public Boolean changeAttribute(String usr, String attr, String value) {
        String usrCN = getCNAndMail(usr)[0];
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(LdapContext.REPLACE_ATTRIBUTE, new BasicAttribute(attr, value));
        try {
            adminContext.modifyAttributes("cn=" + usrCN + ",ou=people,dc=intranet,dc=ctism,dc=ufsm,dc=br", mods);
            return Boolean.TRUE;
        } catch (NamingException ex) {
            return Boolean.FALSE;
        }
    }

    /**
     * obtém nome e email de um usuário do ldap
     *
     * @param usr login do usuário
     * @return vetor de 2 strings, contendo nome e email do usuário,
     * respectivamente
     */
    public String[] getCNAndMail(String usr) {
        String[] ret = new String[2];
        SearchControls sc = new SearchControls();
        String[] attributeFilter = {"cn", "mail"};
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setCountLimit(1);
        sc.setTimeLimit(10000);
        try {
            NamingEnumeration<SearchResult> searchResults = adminContext.search(LDAP_PEOPLE_BASE, "(|(uid=" + usr + "))", sc);
            if (!searchResults.hasMore()) {
                return null;
            }
            Attributes result = searchResults.next().getAttributes();
            ret[0] = result.get("cn").get().toString();
            ret[1] = result.get("mail").get().toString();
        }catch (Exception ex) {
            
        }

        return ret;
    }

    public Boolean userInGroup(String ldap, GrupoUsuarios grupo) throws NamingException {
        SearchControls userSearch = new SearchControls();
        SearchControls groupSearch = new SearchControls();
        String groupIdAttribute = "gidNumber";
        String membersAttribute = "memberUid";
        Arrays.asList(userSearch, groupSearch).stream().forEach((search) -> {
            search.setSearchScope(SearchControls.SUBTREE_SCOPE);
            search.setCountLimit(1);
            search.setTimeLimit(10000);
        });
        userSearch.setReturningAttributes(new String[]{groupIdAttribute});
        groupSearch.setReturningAttributes(new String[]{membersAttribute});
        NamingEnumeration<SearchResult> result = adminContext.search(LDAP_PEOPLE_BASE, "(|(uid=" + ldap + "))", userSearch);
        if (!result.hasMore()) {
            return false;
        }
        if (new GrupoUsuarios(Integer.parseInt(result.next().getAttributes().get(groupIdAttribute).get().toString())).equals(grupo)) {
            return true;
        }
        result = adminContext.search(LDAP_GROUP_BASE, "(|(" + groupIdAttribute + "=" + grupo.getId().toString() + "))", groupSearch);

        if (result.hasMore()) {
            NamingEnumeration members = result.next().getAttributes().get(membersAttribute).getAll();
            while (members.hasMore()) {
                String memberLdap = members.next().toString();
                if (memberLdap.equals(ldap)) {
                    return true;
                }
            }
        }
        return false;
    }

    public LdapContext getContext() {
        return adminContext;
    }

}
