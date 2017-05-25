/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import ufsm.ctism.dao.Situacao;

/**
 *
 * @author SSI-Bruno
 */
public interface SituacaoService {
    
    public Collection<Situacao> getAll();
    
    public Situacao getById(Integer id);
    
}
