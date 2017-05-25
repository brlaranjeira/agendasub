/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Solicitacao;

/**
 *
 * @author SSI-Bruno
 */
public interface SolicitacaoService {
    
    public Collection<Solicitacao> getAll();
    
    public Collection<Solicitacao> getAllBySolicitante(String ldap);
    
//    public Collection<Solicitacao> getAllBySubstituto(String ldap);
    
    public boolean save(Solicitacao solicitacao, Collection<AulaSolicitada> aulas) throws IllegalArgumentException;

    public boolean deleteSolicitacao(Integer solicitacaoId);
    
    public Integer getNumAulas(Integer solicitacaoId);
    
    
    
}