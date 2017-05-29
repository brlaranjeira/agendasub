/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.utils.JDBCUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class SituacaoServiceImpl implements SituacaoService {

    @Override
    public Collection<Situacao> getAll() {
        String sql = "SELECT * FROM ctism_solicita_situacao";
        try {
            Collection<Map<String, Object>> rows = JDBCUtils.query(sql);
            Collection<Situacao> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : rows) {
                ret.add(new Situacao(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection ret = dbSession.createCriteria(Situacao.class).addOrder(Order.asc("id")).list();
        dbSession.close();
        return ret;*/
        
    }

    @Override
    public Situacao getById(Integer id) {
        String sql = "SELECT * FROM ctism_solicita_situacao WHERE id = ?";
        try {
            Collection<Map<String, Object>> rows = JDBCUtils.query(sql, id);
            Collection<Situacao> ret = new java.util.LinkedHashSet<>();
            Iterator<Situacao> itr = ret.iterator();
            return itr.hasNext() ? itr.next() : null;
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Situacao ret = (Situacao) dbSession.createCriteria(Situacao.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
        dbSession.close();
        return ret;*/
    }
    
}
