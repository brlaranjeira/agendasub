/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ufsm.ctism.dao;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * Classe correspondente à tabela solicita_motivoafastamento
 * Os motivos padrão para afastamento estão listados nesta tabela
 * @author SSI-Bruno
 */
@Entity
@Table(name="CTISM_SOLICITA_MOTIVOAFASTAMENTO")
public class MotivoAfastamento implements Serializable {
    
    @Id
    @GeneratedValue(generator = "motivoafastamentogenerator")
    @GenericGenerator(name="motivoafastamentogenerator", strategy="increment")
    @Column
    private Integer id;
    
    @Column
    private String descricao;

    public MotivoAfastamento() {
        
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

    /**
     * 
     * @return Descrição do motivo de afastamento
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * 
     * @param descricao Descrição do motivo de afastamento
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Override
    public String toString() {
        return "[" + id + "] " + descricao;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((MotivoAfastamento) obj).id);
    }
    
    
    
}
