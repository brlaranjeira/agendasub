/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.Componente;
import ufsm.ctism.utils.HibernateUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class ComponentesServiceImpl implements ComponentesService {
    @Override
    public Collection<Componente> getAll() {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        List ret = dbSession.createCriteria(Componente.class).addOrder(Order.asc("nome"))
//                .setCacheMode(CacheMode.NORMAL)
                .list();
        dbSession.close();
        return ret;
    }
    
    @Override
    public Componente getById(Integer id) {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Componente ret = (Componente) dbSession.createCriteria(Componente.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        dbSession.close();
        return ret;
    }
    
}
