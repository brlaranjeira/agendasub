/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.dao;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

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
