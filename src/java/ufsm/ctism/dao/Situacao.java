/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import ufsm.ctism.utils.JDBCUtils;

/**
 * Classe correspondente à tabela solicita_situacao
 * Cada aula solicitada se encontra em uma das situações previstas nesta tabela.
 * @author SSI-Bruno
 */
@Entity
@Table(name = "CTISM_SOLICITA_SITUACAO")
public class Situacao implements Serializable {
    
    public static final Integer SITUACAO_SOLICITADA = 1;
    public static final Integer SITUACAO_ACEITA = 2;
    public static final Integer SITUACAO_NEGADA = 3;
    public static final Integer SITUACAO_DEFERIDA = 4;
    public static final Integer SITUACAO_INDEFERIDA = 5;

    static Situacao getById(Integer id) {
        try {
            Collection<Map<String,Object>> col = JDBCUtils.query("SELECT descricao FROM ctism_solicita_situacao WHERE id = ?", id );
            for (Map<String,Object> line : col) {
                Situacao ret = new Situacao();
                ret.setDescricao(line.get("descricao").toString());
                ret.setId(id);
                return ret;
            }
            return null;
        }catch (java.sql.SQLException ex) {
            return null;
        }
    }
    
    @Id
    @GeneratedValue(generator = "situacaogenerator")
    @GenericGenerator(name = "situacaogenerator", strategy = "increment")
    private Integer id;
    
    @Column
    private String descricao;

    public Situacao(){}
    public Situacao(Integer id) {
        this.id = id;
    }

    public Situacao(Map<String, Object> map) {
        this.id = (int) map.get("id");
        this.descricao = map.get("descricao").toString();
    }
    
    /**
     * 
     * @return Descrição da situação
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * 
     * @param descricao Descrição da situação
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

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
        return id.equals(((Situacao)obj).id);
    }
    
    
}
