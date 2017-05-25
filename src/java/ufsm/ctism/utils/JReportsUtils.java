/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

/**
 * Classe singleton para geração dos relatórios, utilizando Jasper Reports
 * @author SSI-Bruno
 */
public class JReportsUtils {

    private static JReportsUtils instance;
    private Session session;

    private JReportsUtils() {
//        session = HibernateUtils.getInstance().getSession();
    }

    /**
     * Método para obtenção de um objeto da classe
     * @return instância de um objeto desta classe.
     */
    public static JReportsUtils getInstance() {
        if (instance == null) {
            instance = new JReportsUtils();
        }
        return instance;
    }

    /**
     * Método para geração do PDF do relatório, a partir de um modelo e dos parâmetros
     * @param report arquivo .jasper do relatório
     * @param params mapa dos parâmetros a serem passados para a construção do relatório
     * @return o PDF gerado pelo jasper reports, ou null, caso tenha havido algum problema na geração dele.
     * @throws IOException caso o parametro report nao seja encontrado
     */
    public File getPDF(InputStream report, Map<String, Object> params) throws IOException {
        File ret = File.createTempFile("report", ".pdf");
        
        org.hibernate.Session dbSession = HibernateUtils.getInstance().getStatefullSession();
        JasperPrint print;
        try {
            print = dbSession.doReturningWork(new ReturningWork<JasperPrint>() {
                @Override
                public JasperPrint execute(Connection connection) throws SQLException {
                    try {
                        return JasperFillManager.fillReport(report, params, connection);
                    } catch (JRException ex) {
                        return null;
                    }
                }
            });
        } catch (HibernateException ex) {
            return null;
        }
        dbSession.close();
        String absPath = ret.getAbsolutePath();
        try {
            JasperExportManager.exportReportToPdfFile(print, absPath);
        } catch (JRException ex) {
            return null;
        }
        return ret;
    }

}
