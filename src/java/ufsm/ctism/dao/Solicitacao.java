/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import ufsm.ctism.utils.JDBCUtils;

/**
 * Classe correspondente à tabela solicita_solicitação.
 * Uma solicitação, feita por um professorLdap, possui um conjunto de motivos para afastamento e um conjunto de aulas solicitadas.
 * @author SSI-Bruno
 */
@Entity
@Table(name = "CTISM_SOLICITA_SOLICITACAO")
public class Solicitacao implements Serializable {

    static Solicitacao getById(Integer id) {
        Collection<Object> param = new java.util.LinkedHashSet<>();
        param.add(id);
        java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd ");
        java.text.SimpleDateFormat datetimeformat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Collection<Map<String,Object>> col = JDBCUtils.query("SELECT id_professor, datainicio, datafim, outro_motivo, data_solicitacao FROM ctism_solicita_solicitacao WHERE id = ?", param);
            for (Map<String,Object> line : col) {
                Solicitacao ret = new Solicitacao();
                ret.setProfessorLdap(line.get("id_professor").toString());
                ret.setProfessor(Usuario.getByLDAP(ret.getProfessorLdap()));
                ret.setDatainicio(dateformat.parse(line.get("datainicio").toString()));
                ret.setDatafim(dateformat.parse(line.get("datafim").toString()));
                Object outro = line.get("outro_motivo");
                ret.setOutroMotivo(outro != null ? outro.toString() : null);
                ret.setDataSolicitacao(datetimeformat.parse(line.get("data_solicitacao").toString()));
                ret.setId(id);
                return ret;
            }
            return null;
        }catch (java.sql.SQLException | java.text.ParseException ex) {
            return null;
        }
    }
    
    @Id
    @GeneratedValue(generator = "solicitacaogenerator")
    @GenericGenerator(name = "solicitacaogenerator", strategy="increment")
    @Column
    private Integer id;
    
    @Column(name = "id_professor")
    private String professorLdap;
    
    @Transient
    private Usuario professor;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datainicio;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datafim;
    
    @Column (name = "data_solicitacao")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataSolicitacao;

    

    @Column (name = "outro_motivo")
    private String outroMotivo;

    @ManyToMany
    @JoinTable( name="CTISM_SOLICITA_SOLICITACAO_HAS_MOTIVO",
                joinColumns = {@JoinColumn(name="id_solicitacao")},
                inverseJoinColumns = {@JoinColumn(name = "id_motivo")})
    Set<MotivoAfastamento> motivosAfastamento;

    public Solicitacao(Integer id) {
        this.id = id;
    }
    public Solicitacao() {}
    
    /**
     * 
     * @return data na qual a solicitação foi criada
     */
    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    /**
     * 
     * @param dataSolicitacao data na qual a solicitação foi criada
     */
    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
    
    /**
     * 
     * @return Set com os objetos dos motivos do afastamento (não incluindo a coluna outro_motivo)
     */
    public Set<MotivoAfastamento> getMotivosAfastamento() {
        return motivosAfastamento;
    }

    /**
     * 
     * @param motivosAfastamento Set com os objetos dos motivos do afastamento (não incluindo a coluna outro_motivo)
     */
    public void setMotivosAfastamento(Set<MotivoAfastamento> motivosAfastamento) {
        this.motivosAfastamento = motivosAfastamento;
    }
    
    /**
     * 
     * @return id ta linha na tabela
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id id ta linha na tabela
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return Ldap do professorLdap solicitante
     */
    public String getProfessorLdap() {
        return professorLdap;
    }

    /**
     * 
     * @param professor String contendo o ldap do professorLdap solicitante
     */
    public void setProfessorLdap(String professor) {
        this.professorLdap = professor;
    }

    /**
     * 
     * @return Data do início da solicitação
     */
    public Date getDatainicio() {
        return datainicio;
    }

    /**
     * 
     * @param datainicio Data do início da solicitação
     */
    public void setDatainicio(Date datainicio) {
        this.datainicio = datainicio;
    }

    /**
     * 
     * @return Data do final da solicitação
     */
    public Date getDatafim() {
        return datafim;
    }

    
    /**
     * 
     * @param datafim Data do final da solicitação
     */
    public void setDatafim(Date datafim) {
        this.datafim = datafim;
    }

    /**
     * 
     * @return outro motivo da solicitação (opcional)
     */
    public String getOutroMotivo() {
        return outroMotivo;
    }

    /**
     * 
     * @param outroMotivo outro motivo da solicitação (opcional)
     */
    public void setOutroMotivo(String outroMotivo) {
        this.outroMotivo = outroMotivo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((Solicitacao) obj).id);
    }

    public Usuario getProfessor() {
        return professor;
    }

    public void setProfessor(Usuario usuario) {
        this.professor = usuario;
        this.professorLdap = usuario.getLdap();
    }
    
}
