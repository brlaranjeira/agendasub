/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ufsm.ctism.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import ufsm.ctism.utils.JDBCUtils;

/**
 * Classe correspondente à tabela ctism_componente
 * @author SSI-Bruno
 */
@Entity
@Table(name = "CTISM_COMPONENTE")
public class Componente implements Serializable {

    public static Componente getById(Integer id) {
        try {
            Collection<Map<String,Object>> col = JDBCUtils.query("SELECT nome FROM ctism_componente WHERE idcomponente = ?", id );
            for (Map<String,Object> line : col) {
                Componente ret = new Componente();
                ret.setNome(line.get("nome").toString());
                ret.setId(id);
                return ret;
            }
            return null;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    @Id
    @GeneratedValue(generator="componenteincrement")
    @GenericGenerator(name="componenteincrement",strategy="increment")
    @Column(name="idcomponente")
    private Integer id;
    
    @Column
    private String nome;
    
    public Componente(Map<String,Object> map) {
        this.id = (int)map.get("idcomponente");
        this.nome = map.get("nome").toString();
    }
    
    public Componente(){}

    /**
     * 
     * @return id da linha na tabela
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id id da linha na tabela
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return nome do componente
     */
    public String getNome() {
        return nome;
    }

    /**
     * 
     * @param nome nome do componente
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Override
    public String toString() {
        return "[" + id + "] " + nome;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((Componente) obj).id);
    }
    
    
    
}
