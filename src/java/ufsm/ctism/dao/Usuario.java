/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ufsm.ctism.dao;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.ServiceUnavailableException;
import javax.persistence.*;

/**
 * Classe que corresponde à base de usuários do LDAP
 * @author SSI-Bruno
 */
@Entity
@Table(name="CTISM_USUARIO")
public class Usuario implements Serializable {
    
    @Id
    @Column
    private String ldap;
    
    @Column
    private String nome;
    
    @Column
    private String mail;
    
    public Usuario(String ldap) {
        this.ldap = ldap;
    }

    public Usuario(String ldap, String nome, String mail) {
        this.ldap = ldap;
        this.nome = nome;
        this.mail = mail;
    }
    
    public static Usuario getByLDAP( String ldap ) {
        try {
            String[] cnmail = ufsm.ctism.utils.Ldap.getInstance().getCNAndMail(ldap);
            return new Usuario (ldap, cnmail[0], cnmail[1] );
        } catch (ServiceUnavailableException ex) {
            return null;
        }
    }
    
    /**
     * 
     * @param nome nome do professor
     */
    public void setNome(String nome) {
        this.nome=nome;
    }
    
    /**
     * 
     * @param mail email do professor
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    /**
     * 
     * @return nome do professor
     */
    public String getNome() {
        return nome;
    }

    /**
     * 
     * @return email do professor
     */
    public String getMail() {
        return mail;
    }
    public Usuario() {
    }

    /**
     * 
     * @return login do professor no ldap e identificador na tabela do banco de dados
     */
    public String getLdap() {
        return ldap;
    }

    /**
     * 
     * @param ldap login do professor no ldap e identificador na tabela do banco de dados
     */
    public void setLdap(String ldap) {
        this.ldap = ldap;
    }
    
    @Override
    public String toString() {
        return "[" + ldap + "]" + nome + " (" + mail + ")";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.ldap);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return ldap.equals(((Usuario) obj).ldap);
    }
}
