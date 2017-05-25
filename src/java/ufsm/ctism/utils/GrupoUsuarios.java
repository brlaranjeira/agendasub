/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.util.Objects;

/**
 *
 * @author SSI-Bruno
*/
public class GrupoUsuarios {
    
    public static final Integer GRUPO_PROFESSORES = 10001;
    public static final Integer GRUPO_DEPTO_EDUCACAO = 10006;
    public static final Integer GRUPO_SSI = 10004;
    public static final Integer GRUPO_BOLSISTAS = 10003;
    public static final Integer GRUPO_ESTAGIARIOS = 10008;
    
    
    public static final Integer PERMISSAO_PROFESSORES = 1;
    public static final Integer PERMISSAO_DEPTO_EDUCACAO = 1 << 1;
    public static final Integer PERMISSAO_SSI = 1 << 2;
    public static final Integer PERMISSAO_BOLSISTAS = 1 << 3;
    public static final Integer PERMISSAO_ESTAGIARIOS = 1 << 4;
    
    private Integer id;
    
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public GrupoUsuarios(Integer id) {
        this.id = id;
    }

    public GrupoUsuarios() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GrupoUsuarios other = (GrupoUsuarios) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
