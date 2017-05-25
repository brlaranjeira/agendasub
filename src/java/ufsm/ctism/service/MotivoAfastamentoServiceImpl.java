/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.MotivoAfastamento;
import ufsm.ctism.utils.HibernateUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class MotivoAfastamentoServiceImpl  implements MotivoAfastamentoService {

    @Override
    public Collection<MotivoAfastamento> getAll() {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection ret = dbSession.createCriteria(MotivoAfastamento.class).list();
        dbSession.close();
        return ret;
    }
    
    @Override
    public Collection<MotivoAfastamento> getListByIds(Integer[] ids) {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection ret = dbSession.createCriteria(MotivoAfastamento.class)
                .add(Restrictions.in("id", ids))
                .list();
        dbSession.close();
        return ret;
    }
    
    
    @Override
    public MotivoAfastamento getById(Integer id) {
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        MotivoAfastamento ret = (MotivoAfastamento) dbSession.createCriteria(MotivoAfastamento.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
        dbSession.close();
        return ret;
    }
    
}
