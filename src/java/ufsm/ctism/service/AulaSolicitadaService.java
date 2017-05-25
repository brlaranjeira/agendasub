/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Calendar;
import java.util.Collection;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Situacao;

/**
 *
 * @author SSI-Bruno
 */
public interface AulaSolicitadaService {
    
    public Collection<AulaSolicitada> getAll();
    
    public Collection<AulaSolicitada> getAllBySubstituto(String ldap);
    
    public Collection<AulaSolicitada> getBySolicitanteAndTipo(String ldapSolicitante, String tipo);
    
    public Collection<AulaSolicitada> getAllBySituacao(Situacao situacao);
    
    public Integer getCountBySituacao(Situacao situacao);
    
    public Collection<AulaSolicitada> getAllInDateInterval(Calendar init, Calendar end);
    
    public Collection<AulaSolicitada> getAllInDate(Calendar dia);
    
    public Collection<AulaSolicitada> getAllWithRecInDate(Calendar dia);
    
    public Collection<AulaSolicitada> getAllWithRecInDateInterval(Calendar init, Calendar end);
    
    public Collection<AulaSolicitada> getAllAfterDate(Calendar date);
    
    public Collection<AulaSolicitada> getAllWithRecAfterDate(Calendar date);
    
    public Collection<AulaSolicitada> getAllBeforeDate(Calendar date);
    
    public Collection<AulaSolicitada> getAllWithRecBeforeDate(Calendar date);

    public Collection<AulaSolicitada> getBySubstitutoInSituacoes(Collection<Situacao> situacoes, String ldap);
    
    public Collection<AulaSolicitada> getBySubstitutoAndSituacao(Situacao situacao, String ldap);
    
    public Integer getCountBySubstitutoAndSituacao(Situacao situacao, String ldap);
    
    public Integer getCountBySubstitutoInSituacoes(Collection<Situacao> situacoes, String ldap);
    
    public Boolean updateAula(AulaSolicitada aula);

    public AulaSolicitada getById(Integer id);
    
    public Collection<AulaSolicitada> getBySituacoes(Collection<Situacao> situacoes);
    
    public Collection<AulaSolicitada> getBySituacao(Situacao situacao);

    public Boolean deleteAula(Integer idAula);
    
}
