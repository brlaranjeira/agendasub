/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.ArrayList;
import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.utils.HibernateUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class SituacaoServiceImpl implements SituacaoService {

    @Override
    public Collection<Situacao> getAll() {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection ret = dbSession.createCriteria(Situacao.class).addOrder(Order.asc("id")).list();
        dbSession.close();
        return ret;
    }

    @Override
    public Situacao getById(Integer id) {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Situacao ret = (Situacao) dbSession.createCriteria(Situacao.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
        dbSession.close();
        return ret;
    }
    
}
