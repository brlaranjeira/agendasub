/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import ufsm.ctism.dao.MotivoAfastamento;

/**
 *
 * @author SSI-Bruno
 */
public interface MotivoAfastamentoService {
    
    public Collection<MotivoAfastamento> getAll();
    
    public Collection<MotivoAfastamento> getListByIds(Integer[] ids);
    
    public MotivoAfastamento getById(Integer id);
    
}
