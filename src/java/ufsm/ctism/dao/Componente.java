/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ufsm.ctism.dao;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * Classe correspondente à tabela ctism_componente
 * @author SSI-Bruno
 */
@Entity
@Table(name = "CTISM_COMPONENTE")
public class Componente implements Serializable {
    
    @Id
    @GeneratedValue(generator="componenteincrement")
    @GenericGenerator(name="componenteincrement",strategy="increment")
    @Column(name="idcomponente")
    private Integer id;
    
    @Column
    private String nome;
    
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
