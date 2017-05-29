/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import ufsm.ctism.utils.JDBCUtils;

/**
 * Classe correspondente à tabela solicita_aula_solicitada
 * @author SSI-Bruno
 */
@Entity
@Table(name = "CTISM_SOLICITA_AULA_SOLICITADA")
public class AulaSolicitada implements Serializable {
    
    public static Integer AVISO_SOLICITANTE = 1;
    public static Integer AVISO_SUBSTITUTO = 2;
    public static Integer AVISO_DEPTO = 4;
    
    @Id
    @GeneratedValue(generator = "aulasolicitadagenerator")
    @GenericGenerator(name = "aulasolicitadagenerator", strategy = "increment")
    private Integer id;
    
    @Column(name = "id_prof_substituto")
    private String profSubstitutoLdap;
    
    @Transient
    private Usuario profSubstituto;

    @ManyToOne
    @JoinColumn (name = "id_componente", referencedColumnName = "idcomponente")
    private Componente componente;
    
    @ManyToOne
    @JoinColumn (name="id_situacao", referencedColumnName = "id")
    private Situacao situacao;
    
    @ManyToOne
    @JoinColumn (name = "id_solicitacao", referencedColumnName = "id")
    private Solicitacao solicitacao;
    
    @Column(name = "dt_aula")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAula;
    
    @Column(name = "dt_recuperacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRecuperacao;
    
    @Column(name = "mail_enviado")
    Integer mailEnviado;
    
    public AulaSolicitada(){}

    public static AulaSolicitada getById(Integer id) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE id = ?";
            Collection<Object> params = new java.util.LinkedHashSet<>();
            params.add(id);
            Collection<Map<String,Object>> col = JDBCUtils.query(sql,params);
            if (!col.iterator().hasNext()) {
                return null;
            }
            return new AulaSolicitada(col.iterator().next());
        }catch (SQLException ex) {
            return null;
        }
    }
    
    public AulaSolicitada( Map<String,Object> map ) {
        java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        this.id = (int) map.get("id");
        this.profSubstitutoLdap = map.get("id_prof_substituto").toString();
        this.profSubstituto = Usuario.getByLDAP(profSubstitutoLdap);
        this.componente = Componente.getById((int)map.get("id_componente"));
        this.situacao = Situacao.getById((int)map.get("id_situacao"));
        this.solicitacao = Solicitacao.getById((int)map.get("id_solicitacao"));
        try {
            this.dataAula = dateformat.parse(map.get("dt_aula").toString());
        } catch (ParseException ex) {
            this.dataAula = null;
        }
        try {
            this.dataRecuperacao = dateformat.parse(map.get("dt_recuperacao").toString());
        }catch (ParseException ex) {
            this.dataRecuperacao = null;
        }
        this.mailEnviado = Integer.parseInt(map.get("mail_enviado").toString());
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
     * @param profSubstituto Objeto com o professor substituto
     */
    public void setProfSubstitutoLdap(String profSubstituto) {
        this.profSubstitutoLdap = profSubstituto;
    }

   
    
    /**
     * 
     * @return Ldap do professor substituto
     */
    public String getProfSubstitutoLdap() {
        return profSubstitutoLdap;
    }
   

    /**
     * 
     * @return Objeto com o componente da aula solicitada
     */
    public Componente getComponente() {
        return componente;
    }

    /**
     * 
     * @param componente Objeto com o componente da aula solicitada
     */
    public void setComponente(Componente componente) {
        this.componente = componente;
    }

    /**
     * 
     * @return Objeto com a situação da aula solicitada
     */
    public Situacao getSituacao() {
        return situacao;
    }

    /**
     * 
     * @param situacao Objeto com a situação da aula solicitada
     */
    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    /**
     * 
     * @return Objeto da solicitação onde esta aula solicitada está contida
     */
    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    /**
     * 
     * @param solicitacao Objeto da solicitação onde esta aula solicitada está contida
     */
    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    /**
     * 
     * @return Data da aula a ser substituida
     */
    public Date getDataAula() {
        return dataAula;
    }

    /**
     * 
     * @param dataAula Data da aula a ser substituida
     */
    public void setDataAula(Date dataAula) {
        this.dataAula = dataAula;
    }

    /**
     * 
     * @return Data da recuperação da aula a ser substituida
     */
    public Date getDataRecuperacao() {
        return dataRecuperacao;
    }

    /**
     * 
     * @param dataRecuperacao Data da recuperação da aula a ser substituida
     */
    public void setDataRecuperacao(Date dataRecuperacao) {
        this.dataRecuperacao = dataRecuperacao;
    }

    /**
     * 
     * @return flag que indica se um email foi enviado para informar o professor sobre o novo estado da aula solicitada
     */
    public Integer getMailEnviado() {
        return mailEnviado;
    }

    /**
     * 
     * @param mailEnviado flag que indica se um email foi enviado para informar o professor sobre o novo estado da aula solicitada
     */
    public void setMailEnviado(Integer mailEnviado) {
        this.mailEnviado = mailEnviado;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String ret = "[" + solicitacao.getProfessor().getNome();
        ret += " -> " + profSubstitutoLdap + "] ";
        ret += componente.getNome();
        ret += "(" + format.format(dataAula) + "," + format.format(dataRecuperacao) + ")";
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((AulaSolicitada) obj).id);
    }
    
    public Usuario getProfSubstituto() {
        return profSubstituto;
    }

    public void setProfSubstituto(Usuario usuarioSubstituto) {
        this.profSubstituto = usuarioSubstituto;
        this.profSubstitutoLdap = usuarioSubstituto.getLdap();
    }
    
    
    
}
