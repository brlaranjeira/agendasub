/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.dao.Situacao;
import ufsm.ctism.dao.Usuario;
import ufsm.ctism.utils.HibernateUtils;
import ufsm.ctism.utils.JDBCUtils;

/**
 *
 * @author SSI-Bruno
 */
@Service
public class AulaSolicitadaServiceImpl implements AulaSolicitadaService {
    
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    SolicitacaoService solicitacaoService;
    
    @Override
    public Collection<AulaSolicitada> getAllBySubstituto(String ldap) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE id_prof_substituto = ? ORDER BY dt_aula DESC";
            Collection<Map<String,Object>> aulas = JDBCUtils.query( sql , ldap );
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> aula : aulas) {
                ret.add(new AulaSolicitada(aula));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class,"aula")
                .add(Restrictions.eq("profSubstituto", ldap))
                .addOrder(Order.desc("aula.dataAula"))
                .setCacheable(false)
                .list();
        dbSession.close();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            } catch (NamingException skip) {}
        });
        return ret;*/
    }

    @Override
    public Collection<AulaSolicitada> getAll() {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada";
            Collection<Map<String,Object>> aulas = JDBCUtils.query( sql );
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> aula : aulas) {
                ret.add(new AulaSolicitada(aula));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
    }
    
    @Override
    public Collection<AulaSolicitada> getAllBySituacao(Situacao situacao) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE id_situacao = ? ORDER BY dt_recuperacao ASC";
            Collection<Map<String,Object>> aulas = JDBCUtils.query( sql , situacao.getId() );
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> aula : aulas) {
                ret.add(new AulaSolicitada(aula));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
    }
    
    @Override
    public Integer getCountBySituacao(Situacao situacao) {
        try {
            String sql = "SELECT COUNT(*) AS CNT FROM ctism_solicita_aula_solicitada WHERE id_situacao = ?";
            Collection<Map<String, Object>> col = JDBCUtils.query(sql, situacao.getId());
            return (int) col.iterator().next().get("CNT");
        } catch (SQLException ex) {
            return null;
        }
        /*
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Number ret = (Number) dbSession.createCriteria(AulaSolicitada.class,"aula")
                .createAlias("aula.situacao", "situacao")
                .add(Restrictions.eq("situacao",situacao))
                .addOrder(Order.asc("dataRecuperacao"))
                .setCacheable(false)
                .setProjection(Projections.rowCount())
                .uniqueResult();
        dbSession.close();
        return ret.intValue();*/
    }
    
    private Collection<AulaSolicitada> getAllWithXInDateInterval(String x, Calendar init, Calendar end) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE " + x + " >= ? AND " + x + " < ? ORDER BY id_prof_substituto ASC, " + x + " DESC";
            Collection<Map<String, Object>> col = JDBCUtils.query(sql, init.getTime(), end.getTime());
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : col) {
                ret.add(new AulaSolicitada(map));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        System.out.println("init: " + init.getTime());
        System.out.println("end: " + end.getTime());
        List<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.ge(x, init.getTime()))
                .add(Restrictions.lt(x, end.getTime()))
                .add(Restrictions.between(x, init.getTime(),end.getTime() ))
                .addOrder(Order.asc(x))
                .setCacheable(false)
//                .addOrder(Order.asc("profSubstitutoLdap"))
                .list();
        dbSession.close();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            } catch (Exception skip) {}
        });
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getAllInDateInterval(Calendar init, Calendar end) {
        return getAllWithXInDateInterval("dt_aula", init, end);
    }
    
    @Override
    public Collection<AulaSolicitada> getAllWithRecInDateInterval(Calendar init, Calendar end) {
        return getAllWithXInDateInterval("dt_recuperacao", init, end);
    }
    
    private Collection<AulaSolicitada> getAllWithXInDate(String x, Calendar date) {
        //System.out.println("[" + x + "] " + new SimpleDateFormat("dd-MM-YYYY").format(new Date(date.getTimeInMillis())));
        Calendar dateCpy = (Calendar) date.clone();
        //calendar indica algum momemto do dia, precisamos do inicio e do final
        dateCpy.set(Calendar.HOUR_OF_DAY, 0);
        dateCpy.set(Calendar.MINUTE, 0);
        dateCpy.set(Calendar.SECOND, 0);
        Calendar end = (Calendar) dateCpy.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        return getAllWithXInDateInterval(x, dateCpy, end);
        /*org.hibernate.StatelessSession session = HibernateUtils.getInstance().getStatelessSession();
        Collection<AulaSolicitada> aulas = session.createCriteria(AulaSolicitada.class)
                .add(Restrictions.gt(x, dateCpy.getTime()))
                .add(Restrictions.lt(x, end.getTime()))
                .addOrder(Order.asc(x))
                .setCacheable(false)
                .list();
        session.close();
        return aulas;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getAllInDate(Calendar date) {
        return getAllWithXInDate("dt_aula", date);
    }

    @Override
    public Collection<AulaSolicitada> getAllWithRecInDate(Calendar dia) {
        return getAllWithXInDate("dt_recuperacao", dia);
    }
    
    private Collection<AulaSolicitada> getAllWithXAfterDate(String x, Calendar date) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE " + x + " > ? ORDER BY id_prof_substituto ASC, " + x + " DESC";
            Collection<Map<String, Object>> col = JDBCUtils.query(sql, date.getTime());
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : col) {
                ret.add(new AulaSolicitada(map));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        List<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.gt(x, date.getTime()))
                .addOrder(Order.asc("profSubstituto"))
                .addOrder(Order.asc(x))
                .setCacheable(false)
                .list();
        dbSession.close();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            } catch (NamingException skip) {}
        });
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getAllAfterDate(Calendar date) {
        return getAllWithXAfterDate("dt_aula",date);
    }
    
    @Override
    public Collection<AulaSolicitada> getAllWithRecAfterDate(Calendar date) {
        return getAllWithXAfterDate("dt_recuperacao",date);
    }
    
    private Collection<AulaSolicitada> getAllWithXBeforeDate(String x,Calendar date) {
        try {
            String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE " + x + " < ? ORDER BY id_prof_substituto ASC, " + x + " DESC";
            Collection<Map<String, Object>> col = JDBCUtils.query(sql, date.getTime());
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : col) {
                ret.add(new AulaSolicitada(map));
            }
            return ret;
        }catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        List<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.lt(x, date.getTime()))
                .addOrder(Order.asc("profSubstituto"))
                .addOrder(Order.asc(x))
                .setCacheable(false)
                .list();
        dbSession.close();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            } catch (NamingException skip) {}
        });
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getAllBeforeDate(Calendar date) {
        return getAllWithXBeforeDate("dt_aula", date);
    }
    
    @Override
    public Collection<AulaSolicitada> getAllWithRecBeforeDate(Calendar date) {
        return getAllWithXBeforeDate("dt_recuperacao", date);
    }

    //variavel 'tipo' diz se eh data da aula ou da recuperacao ou ambas
    @Override
    public Collection<AulaSolicitada> getBySolicitanteAndTipo(String ldapSolicitante, String tipo) {
        
        String sql = "SELECT aula.* FROM ctism_solicita_aula_solicitada aula LEFT JOIN ctism_solicita_solicitacao sol ON aula.id_solicitacao = sol.id WHERE sol.id_professor = ? ";
        Collection<Map<String, Object>> col;
        if (!tipo.equalsIgnoreCase("todas")) {
            tipo = tipo.equalsIgnoreCase("dataAula") ? "dt_aula" : "dt_recuperacao";
            sql += " AND " + tipo + " > ? ORDER BY " + tipo + " ASC";
            try {
                col = JDBCUtils.query(sql, ldapSolicitante, new Date());
            } catch (SQLException ex) {
                return null;
            }
        } else {
            sql += " ORDER BY dt_aula ASC";
            try {
                col = JDBCUtils.query(sql, ldapSolicitante);
            }catch (SQLException ex) {
                return null;
            }
        }
        Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
        for (Map<String, Object> map : col) {
            ret.add(new AulaSolicitada(map));
        }
        return ret;
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        
        Criteria qry = dbSession.createCriteria(AulaSolicitada.class,"aula")
                .createAlias("aula.solicitacao", "solicitacao")
                .add(Restrictions.eq("solicitacao.professorLdap", ldapSolicitante))
                .setCacheable(false);
        if (!tipo.equalsIgnoreCase("todas")) {
            qry.add(Restrictions.gt("aula."+tipo, new Date()));
            qry.addOrder(Order.asc("aula."+tipo));
        } else {
            qry.addOrder(Order.asc("aula.dataAula"));
        }
        Collection<AulaSolicitada> ret = qry.list();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            } catch (NullPointerException | NamingException skip) {}
        });
        dbSession.close();
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getBySubstitutoInSituacoes(Collection<Situacao> situacoes, String ldap) {
        Collection<Object> params = new java.util.LinkedHashSet<>();
        params.add(ldap);
        String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE id_prof_substituto = ? AND id_situacao IN (";
        Boolean primeiro = Boolean.TRUE;
        for (Situacao situacao : situacoes) {
            sql += (primeiro) ? " ? " : " , ? ";
            primeiro = Boolean.FALSE;
            params.add(situacao.getId());
        }
        sql += ") ORDER BY dt_aula ASC";
        try {
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            Collection<Map<String, Object>> col = JDBCUtils.query(sql,params.toArray(new Object[params.size()]));
            for (Map<String, Object> map : col) {
                ret.add(new AulaSolicitada(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /*
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.in("situacao", situacoes))
                .add(Restrictions.eq("profSubstitutoLdap", ldap))
                .addOrder(Order.asc("dataAula"))
                .setCacheable(false)
                .list();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                aula.setProfSubstituto(usuarioService.getByLdap(aula.getProfSubstitutoLdap()));
            }catch (NamingException skip) {}
            try {
                if (aula.getSolicitacao().getProfessor() == null) {
                    aula.getSolicitacao().setProfessor(usuarioService.getByLdap(aula.getSolicitacao().getProfessorLdap()));
                }
            }catch (NamingException skip) {}
        });
        dbSession.close();
        return ret;
        */
    }
    
    
    @Override
    public Collection<AulaSolicitada> getBySubstitutoAndSituacao(Situacao situacao, String ldap){
        return getBySubstitutoInSituacoes(Arrays.asList(situacao), ldap);
    }
    
    @Override
    public Integer getCountBySubstitutoInSituacoes(Collection<Situacao> situacoes, String ldap) {
        Collection<Object> params = new java.util.LinkedHashSet<>();
        params.add(ldap);
        String sql = "SELECT COUNT(*) AS CNT FROM ctism_solicita_aula_solicitada WHERE id_prof_substituto = ? AND id_situacao IN (";
        Boolean primeiro = Boolean.TRUE;
        for (Situacao situacao : situacoes) {
            sql += (primeiro) ? " ? " : " , ? ";
            primeiro = Boolean.FALSE;
            params.add(situacao.getId());
        }
        sql += ")";
        try {
            Collection<Map<String, Object>> col = JDBCUtils.query(sql,params.toArray(new Object[params.size()]));
            return (int) col.iterator().next().get("CNT");
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Number ret = (Number) dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.in("situacao", situacoes))
                .add(Restrictions.eq("profSubstitutoLdap", ldap))
                .addOrder(Order.asc("dataAula"))
                .setCacheable(false)
                .setProjection(Projections.rowCount())
                .uniqueResult();
        dbSession.close();
        return ret.intValue();*/
    }
    
    @Override
    public Integer getCountBySubstitutoAndSituacao(Situacao situacao, String ldap) {
        return getCountBySubstitutoInSituacoes(Arrays.asList(situacao),ldap);
    }
    
    

    @Override
    public AulaSolicitada getById (Integer id) {
        return AulaSolicitada.getById(id);
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        AulaSolicitada ret = (AulaSolicitada) dbSession.createCriteria(AulaSolicitada.class)
                .add(Restrictions.eq("id",id))
                .uniqueResult();
        try {
            ret.setProfSubstituto(usuarioService.getByLdap(ret.getProfSubstitutoLdap()));
        } catch (NullPointerException | NamingException skip) {}
        try {
            ret.getSolicitacao().setProfessor(usuarioService.getByLdap(ret.getSolicitacao().getProfessorLdap()));
        } catch (NullPointerException | NamingException skip) {}
        dbSession.close();
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getBySituacoes (Collection<Situacao> situacoes) {
        Collection<Object> params = new java.util.LinkedHashSet<>();
        String sql = "SELECT * FROM ctism_solicita_aula_solicitada WHERE id_situacao IN (";
        Boolean primeiro = Boolean.TRUE;
        for (Situacao situacao : situacoes) {
            sql += (primeiro) ? " ? " : " , ? ";
            primeiro = Boolean.FALSE;
            params.add(situacao.getId());
        }
        sql += ")";
        try {
            Collection<AulaSolicitada> ret = new java.util.LinkedHashSet<>();
            Collection<Map<String, Object>> col = JDBCUtils.query(sql,params.toArray(new Object[params.size()]));
            for (Map<String, Object> map : col) {
                ret.add(new AulaSolicitada(map));
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
        /*org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        Collection<AulaSolicitada> ret = dbSession.createCriteria(AulaSolicitada.class,"aula")
                .add(Restrictions.in("situacao", situacoes))
                .createAlias("aula.solicitacao", "solicitacao")
                .setCacheable(false)
                .setFetchMode("solicitacao.motivosAfastamento", FetchMode.JOIN)
                .list();
        ret.forEach((AulaSolicitada aula) -> {
            try {
                String subLdap = aula.getProfSubstitutoLdap();
                Usuario toSet = usuarioService.getByLdap(subLdap);
                aula.setProfSubstituto(toSet);
            } catch (NullPointerException | NamingException skip) {
                skip.printStackTrace();
            }
            try {
                if (aula.getSolicitacao().getProfessor() == null) {
                    aula.getSolicitacao().setProfessor(usuarioService.getByLdap(aula.getSolicitacao().getProfessorLdap()));
                }
            } catch (NullPointerException | NamingException skip) {
                skip.printStackTrace();
            }    
        });
        dbSession.close();
        return ret;*/
    }
    
    @Override
    public Collection<AulaSolicitada> getBySituacao(Situacao situacao) {
        return getBySituacoes(Arrays.asList(situacao));
    }
    
    @Override
    public Boolean deleteAula(Integer idAula) {
        String sql = "DELETE FROM ctism_solicita_aula_solicitada WHERE id = ?";
        try {
            return JDBCUtils.delete(null, sql, idAula) > 0;
        }catch (SQLException ex) {
            return Boolean.FALSE;
        }
        /*
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        dbSession.beginTransaction();
        try {
            String hqlQuery = "delete from AulaSolicitada where id = :id";
            Query query = dbSession.createQuery(hqlQuery)
                    .setInteger("id", idAula);
            query.executeUpdate();
            dbSession.getTransaction().commit();
        */
            /**
             * CASO A SOLICITACAO TENHA FICADO SEM NENHUMA AULA SOLICITADA
             * A ENTIDADE SOLICITACAO EH APAGADA VIA TRIGGER
             */
        /*    
        } catch (Exception ex) {
            Transaction t = dbSession.getTransaction();
            if (t != null) {
                t.rollback();
            }
            return Boolean.FALSE;
        } finally {
            dbSession.close();
        }
        return Boolean.TRUE;
        */
    }
    
    @Override
    public Boolean updateAula(AulaSolicitada aula) {
        String sql = "UPDATE ctism_solicita_aula_solicitada SET id_prof_substituto = ?, id_componente = ?, id_situacao = ?, id_solicitacao = ?, dt_aula = ?, dt_recuperacao = ?, mail_enviado = ? WHERE id = ?";
        
        try {
            Integer affectedRows = JDBCUtils.update(null, sql, aula.getProfSubstitutoLdap(), aula.getComponente().getId(), aula.getSituacao().getId(), aula.getSolicitacao().getId(), aula.getDataAula(), aula.getDataRecuperacao(), aula.getMailEnviado(), aula.getId());
            return affectedRows > 0;
        } catch (SQLException ex) {
            return Boolean.FALSE;
        }
        /**
         * id_prof_substituto = ?, 
         * id_componente = ?,
         * id_situacao = ?,
         * id_solicitacao = ?,
         * dt_aula = ?,
         * dt_recuperacao = ?,
         * mail_enviado = ?
         * WHERE id = ?
         */
        
        /**
         * aula.getProfSubstitutoLdap(),
         * aula.getComponente().getId(),
         * aula.getSituacao().getId(),
         * aula.getSolicitacao().getId(),
         * aula.getDataAula(),
         * aula.getDataRecuperacao(),
         * aula.getMailEnviado(),
         * aula.getId()
         */
        
        /*Boolean ret = Boolean.TRUE;
        org.hibernate.StatelessSession dbSession = HibernateUtils.getInstance().getStatelessSession();
        try {
            dbSession.beginTransaction();
            dbSession.update(aula);
            dbSession.getTransaction().commit();
        } catch (Exception ex) {
            ret = Boolean.FALSE;
            Transaction t = dbSession.getTransaction();
            if (t != null && t.isActive()) {
                t.rollback();
            }
        } finally {
            dbSession.close();
        }
        return ret;
        */
    }
    
}
