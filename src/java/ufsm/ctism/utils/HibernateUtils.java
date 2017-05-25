/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ufsm.ctism.utils;

import java.util.Properties;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Classe singleton para utilização do Hibernate
 * @author SSI-Bruno
 */
public class HibernateUtils {
    
    private final org.hibernate.SessionFactory sessionFactory;
    private static HibernateUtils instance;

    
    private HibernateUtils() {
        Configuration configuration = new Configuration();
        configuration.configure();
        Properties properties = configuration.getProperties();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        StandardServiceRegistry serviceRegistry = builder.applySettings(properties).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    
    /**
     * Abre uma sessão stateless no banco de dados
     * @return sessão aberta com o banco
     */
    public org.hibernate.StatelessSession getStatelessSession() {
        return sessionFactory.openStatelessSession();
    }
    
    /**
     * Abre uma sessão statefull no banco de dados
     * @return sessão aberta com o banco
     */
    public org.hibernate.Session getStatefullSession() {
        return sessionFactory.openSession();
    }
    
    /**
     * Método para obtenção de um objeto da classe
     * @return instância de um objeto desta classe.
     */
    public static HibernateUtils getInstance() {
        if (instance == null) {
            instance = new HibernateUtils();
        }
        return instance;
    }
    
}
