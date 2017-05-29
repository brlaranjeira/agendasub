/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Solicitacao;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.utils.HibernateUtils;
import ufsm.ctism.utils.JDBCUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class SolicitacaoServiceImpl implements SolicitacaoService {

    @Autowired
    UsuarioService usuarioService;
    
    @Override
    public Collection<Solicitacao> getAll() {
        String sql = "SELECT * FROM ctism_solicita_solicitacao";
        try {
            Collection<Map<String, Object>> rows = JDBCUtils.query(sql);
            Collection<Solicitacao> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : rows) {
                ret.add(new Solicitacao(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
            dbSession.createCriteria(Solicitacao.class).addOrder(Order.desc("datainicio"));
            Collection<Solicitacao> ret = dbSession.createCriteria(Solicitacao.class)
            .addOrder(Order.desc("datainicio")).list();
            dbSession.close();
            ret.forEach( solicitacao -> {
            try {
            solicitacao.setProfessor(usuarioService.getByLdap(solicitacao.getProfessorLdap()));
            } catch (NamingException skip) {}
            });
            return ret;*/
    }

    @Override
    public Collection<Solicitacao> getAllBySolicitante(String ldap) {
        String sql = "SELECT * FROM ctism_solicita_solicitacao WHERE id_professor = ? ORDER BY datainicio DESC";
        try {
            Collection<Map<String, Object>> rows = JDBCUtils.query(sql, ldap);
            Collection<Solicitacao> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : rows) {
                ret.add(new Solicitacao(map));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
        /* org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection<Solicitacao> ret = dbSession.createCriteria(Solicitacao.class)
                .add(Restrictions.eq("professor",ldap))
                .addOrder(Order.desc("datainicio")).list();
        dbSession.close();
        ret.forEach((Solicitacao solicitacao) -> {
            try {
                solicitacao.setProfessor(usuarioService.getByLdap(ldap));
            } catch (NamingException skip) {}
        });
        return ret; */
    }
    
    @Override
    public Integer getNumAulas(Integer solicitacaoId) {
        String sql = "SELECT COUNT(*) AS CNT FROM ctism_solicita_solicitacao WHERE id = ?";
        try {
            Collection<Map<String, Object>> col = JDBCUtils.query( sql , solicitacaoId );
            return (int) col.iterator().next().get("CNT");
        }catch (SQLException ex) {
            return null;
        }
        /* org.hibernate.StatelessSession session = HibernateUtils.getInstance().getStatelessSession();
        Long ret = (Long) session.createCriteria(AulaSolicitada.class)
                .add(Restrictions.eq("solicitacao", new Solicitacao(solicitacaoId)))
                .setProjection(Projections.rowCount()).uniqueResult();
        session.close();
        return ret.intValue(); */
    }

    @Override
    public boolean save(Solicitacao solicitacao, Collection<AulaSolicitada> aulas) {
        String ldap = "?";
        Boolean ret = Boolean.TRUE;
//        try {
            ldap = solicitacao.getProfessorLdap();
//            Usuario solicitante = usuarioService.getByLdap(ldap);
            Usuario solicitante = solicitacao.getProfessor();
            if (solicitante == null) {
                throw new IllegalArgumentException("nao encontrado usuario de ldap \"" + ldap + "\"");
            }
//        } catch (NamingException ex) {
//            throw new IllegalArgumentException("nao encontrado usuario de ldap \"" + ldap + "\"");
//        }
        for (AulaSolicitada aula : aulas) {
            ldap = aula.getProfSubstitutoLdap();
            try {
                if (usuarioService.getByLdap(ldap) == null) {
                    throw new IllegalArgumentException("nao encontrado usuario de ldap \"" + ldap + "\"");
                }
            } catch (NamingException ex) {
                throw new IllegalArgumentException("nao encontrado usuario de ldap \"" + ldap + "\"");
            }
        }
        //ids do ldap estao ok, agora salvamos
        org.hibernate.Session dbSession = HibernateUtils.getInstance().getStatefullSession();
        try {
            dbSession.beginTransaction();
            dbSession.save(solicitacao);
            aulas.forEach((AulaSolicitada aula) -> dbSession.save(aula));
        } catch (Exception ex) {
            ret = Boolean.FALSE;
            Transaction t = dbSession.getTransaction();
            if (t != null) {
                t.rollback();
            }
        } finally {
            try {
                Transaction t = dbSession.getTransaction();
                if (t != null && t.isActive()) {
                    t.commit();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ret = Boolean.FALSE;
            }
        }
        return ret;
    }
    
    @Override
    public boolean deleteSolicitacao(Integer solicitacaoId) {
        org.hibernate.StatelessSession session = HibernateUtils.getInstance().getStatelessSession();
        session.beginTransaction();
        boolean ret = true;
        try {
            String hql = "delete from Solicitacao where id = :solid";
            Query qry = session.createQuery(hql).setInteger(":solid", solicitacaoId);
            qry.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex) {
            ret = false;
            Transaction t = session.getTransaction();
            if (t != null) {
                t.rollback();
            }
        } finally {
            session.close();
        }
        return ret;
    }
    
    
    
}
