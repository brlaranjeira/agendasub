/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.MotivoAfastamento;
import ufsm.ctism.utils.JDBCUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class MotivoAfastamentoServiceImpl  implements MotivoAfastamentoService {

    @Override
    public Collection<MotivoAfastamento> getAll() {
        String sql = "SELECT * FROM ctism_solicita_motivoafastamento";
        try {
            Collection<Map<String,Object>> rows = JDBCUtils.query(sql);
            Collection<MotivoAfastamento> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : rows) {
                ret.add(new MotivoAfastamento(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /* org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection ret = dbSession.createCriteria(MotivoAfastamento.class).list();
        dbSession.close();
        return ret; */
    }
    
    @Override
    public Collection<MotivoAfastamento> getListByIds(Integer[] ids) {
        String sql = "SELECT * FROM ctism_solicita_motivoafastamento WHERE id in (";
        for (int i = 0; i < ids.length; i++ ) {
            sql += (i == 0) ? " ? " : " , ? ";
        }
        sql += ")";
        try {
            Collection<Map<String, Object>> rows = JDBCUtils.query(sql, (Object[]) ids);
            Collection<MotivoAfastamento> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : rows) {
                ret.add(new MotivoAfastamento(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
            Collection ret = dbSession.createCriteria(MotivoAfastamento.class)
            .add(Restrictions.in("id", ids))
            .list();
            dbSession.close();
            return ret;*/
    }
    
    
    @Override
    public MotivoAfastamento getById(Integer id) {
        Integer[] ids = {id};
        Iterator<MotivoAfastamento> itr = getListByIds(ids).iterator();
        return itr.hasNext() ? itr.next() : null;
        //return list.iterator().next();
        /* org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        MotivoAfastamento ret = (MotivoAfastamento) dbSession.createCriteria(MotivoAfastamento.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
        dbSession.close();
        return ret; */
    }
    
}
