/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import ufsm.ctism.dao.Componente;

/**
 *
 * @author SSI-Bruno
 */
public interface ComponentesService {
    
    public Collection<Componente> getAll();
    
    public Componente getById(Integer id);
    
}
