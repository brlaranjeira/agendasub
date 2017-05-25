/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.Ldap;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Override
    public Usuario getByLdap(String ldap) throws NamingException {
        LdapContext ctx = ufsm.ctism.utils.Ldap.getInstance().getContext();
        SearchControls sc = new SearchControls();
        sc.setReturningAttributes(new String[]{"cn", "mail"});
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setCountLimit(1);
        sc.setTimeLimit(10000);
        try {
            SearchResult result = ctx.search(Ldap.LDAP_PEOPLE_BASE, "(|(uid=" + ldap + "))", sc).next();
            return result == null ? null : new Usuario(ldap, result.getAttributes().get("cn").get().toString(), result.getAttributes().get("mail").get().toString());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Boolean userInGroup(String ldap, Integer group) throws NamingException {
        LdapContext ctx = ufsm.ctism.utils.Ldap.getInstance().getContext();
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
        NamingEnumeration<SearchResult> result = ctx.search(Ldap.LDAP_PEOPLE_BASE, "(|(uid=" + ldap + "))", userSearch);
        if (!result.hasMore()) {
            return Boolean.FALSE;
        }
        SearchResult usr = result.next();
        if (new GrupoUsuarios(Integer.parseInt(usr.getAttributes().get(groupIdAttribute).get().toString())).getId().equals(group)) {
            return Boolean.TRUE;
        }
        result = ctx.search(Ldap.LDAP_GROUP_BASE, "(|(" + groupIdAttribute + "=" + group.toString() + "))", groupSearch);
        if (result.hasMore()) {
            Attribute attribute = result.next().getAttributes().get(membersAttribute);
            if (attribute != null) {
                NamingEnumeration members = attribute.getAll();
                while (members.hasMore()) {
                    String memberLdap = members.next().toString();
                    if (memberLdap.equals(ldap)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Collection<Usuario> getAll() throws NamingException {
        Collection<Usuario> ret = new LinkedHashSet<>();
        LdapContext ctx = ufsm.ctism.utils.Ldap.getInstance().getContext();
        SearchControls sc = new SearchControls();
        sc.setReturningAttributes(new String[]{"cn", "mail", "uid"});
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setCountLimit(1);
        sc.setTimeLimit(10000);
        NamingEnumeration<SearchResult> allUsers = ctx.search(Ldap.LDAP_PEOPLE_BASE, "(|(uid=*))", sc);
        while (allUsers.hasMore()) {
            Attributes userAttributes = allUsers.next().getAttributes();
            ret.add(new Usuario(userAttributes.get("ldap").get().toString(), userAttributes.get("cn").get().toString(), userAttributes.get("mail").get().toString()));
        }
        return ret;
    }

    @Override
    public Collection<Usuario> getAllFromGroup(Integer group) throws NamingException {
        Collection<Usuario> ret = new LinkedHashSet<>();
        LdapContext ctx = ufsm.ctism.utils.Ldap.getInstance().getContext();
        SearchControls sc = new SearchControls();
        sc.setReturningAttributes(new String[]{"cn", "mail", "uid"});
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setTimeLimit(10000);
        NamingEnumeration<SearchResult> allFromGroup = ctx.search(Ldap.LDAP_PEOPLE_BASE, "(|(gidNumber=" + group + "))", sc);
        while (allFromGroup.hasMore()) {
            SearchResult user = allFromGroup.next();
            Attributes userAttributes = user.getAttributes();
            Usuario userToAdd = new Usuario(userAttributes.get("uid").get().toString(), userAttributes.get("cn").get().toString(), userAttributes.get("mail").get().toString());
            ret.add(userToAdd);
        }
//        SearchControls
//        search.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setCountLimit(1);
        sc.setReturningAttributes(new String[]{"memberUid"});
//        search.setTimeLimit(10000);
        NamingEnumeration<SearchResult> groupObject = ctx.search(Ldap.LDAP_GROUP_BASE, "(|(gidNumber=" + group + "))", sc);
        if (groupObject.hasMore()) {
            Attribute members = groupObject.next().getAttributes().get("memberUid");
            if (members != null) {
                NamingEnumeration all = members.getAll();
                while (all.hasMore()) {
                    String ldap = all.next().toString();
                    ret.add(getByLdap(ldap));
                }
            }
        }

        return ret;
    }

    @Override
    public Integer getPermissionsAndSetToModel(String usuario, Model model) {
        try {
            Integer ret = 0;

            Boolean isSsi = userInGroup(usuario, GrupoUsuarios.GRUPO_SSI);
            Boolean isDepEd = userInGroup(usuario, GrupoUsuarios.GRUPO_DEPTO_EDUCACAO);
            Boolean isProf = userInGroup(usuario, GrupoUsuarios.GRUPO_PROFESSORES);
            Boolean isEstagiario = userInGroup(usuario, GrupoUsuarios.GRUPO_ESTAGIARIOS);
            Boolean isBolsista = userInGroup(usuario, GrupoUsuarios.GRUPO_BOLSISTAS);

            ret = isSsi ? ret | GrupoUsuarios.PERMISSAO_SSI : ret;
            ret = isDepEd ? ret | GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO : ret;
            ret = isProf ? ret | GrupoUsuarios.PERMISSAO_PROFESSORES : ret;
            ret = isEstagiario ? ret | GrupoUsuarios.PERMISSAO_ESTAGIARIOS : ret;
            ret = isBolsista ? ret | GrupoUsuarios.PERMISSAO_BOLSISTAS : ret;

            if (model != null) {
                model.addAttribute("isSsi", isSsi);
                model.addAttribute("isDepEd", userInGroup(usuario, GrupoUsuarios.GRUPO_DEPTO_EDUCACAO));
                model.addAttribute("isProf", userInGroup(usuario, GrupoUsuarios.GRUPO_PROFESSORES));
                model.addAttribute("isEstagiario", userInGroup(usuario, GrupoUsuarios.GRUPO_ESTAGIARIOS));
                model.addAttribute("isBolsista", userInGroup(usuario, GrupoUsuarios.GRUPO_BOLSISTAS));
            }
            return ret;

        } catch (NamingException ex) {
            if (model != null) {
                model.addAttribute("isSsi", Boolean.FALSE);
                model.addAttribute("isDepEd", Boolean.FALSE);
                model.addAttribute("isProf", Boolean.FALSE);
                model.addAttribute("isEstagiario", Boolean.FALSE);
                model.addAttribute("isBolsista", Boolean.FALSE);
            }
            return 0;
        }
    }

    @Override
    public Boolean auth(String user, String pw) throws ServiceUnavailableException {
        return Ldap.getInstance().authLogin(user, pw);
    }

    @Override
    public Boolean hasSystemAccess(String user) throws NamingException {
        return Arrays.asList(
                GrupoUsuarios.GRUPO_DEPTO_EDUCACAO,
                GrupoUsuarios.GRUPO_ESTAGIARIOS,
                GrupoUsuarios.GRUPO_PROFESSORES,
                GrupoUsuarios.GRUPO_SSI).stream()
                .anyMatch((Integer group) -> {
                    try {
                        return userInGroup(user, group);
                    } catch (Exception skip) {
                        return Boolean.FALSE;
                    }
                });
    }

    @Override
    public Boolean changeName(Usuario usr, String name) throws ServiceUnavailableException {
        return Ldap.getInstance().changeAttribute(usr.getLdap(), "cn", name);
    }

    @Override
    public Boolean changeMail(Usuario usr, String mail) throws ServiceUnavailableException {
        return Ldap.getInstance().changeAttribute(usr.getLdap(), "mail", mail);
    }

    @Override
    public Collection<Usuario> getAllFromGroups(Integer[] groups) throws NamingException {
        Collection<Usuario> ret = new LinkedHashSet<>();
        for (Integer group : groups) {
            ret.addAll(getAllFromGroup(group));
        }
        return ret;
    }

}
