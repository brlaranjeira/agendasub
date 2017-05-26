/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 *
 * @author brlaranjeira
 */
public class JDBCUtils {
    
    private static final String bdurl = "jdbc:mysql://bdctism/dev_agendasub";
    private static final String bdusr = "dev_agendasub";
    private static final String bdpw = "12345";
    private static Boolean init = Boolean.FALSE;
    
    private static Connection getConnection() {
        if (!init) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        try {
            return DriverManager.getConnection(bdurl, bdusr, bdpw);
        } catch (SQLException ex) {
            return null;
        }
    }
    
    private static PreparedStatement getPreparedStatement(Connection conn, String sql, Collection<Object> params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = 1;
        for (Object param : params) {
            ps.setObject(i++, param);
        }
        return ps;
    }
    
    public static Collection<Map<String,Object>> query( String sql ) throws SQLException {
        return query(sql,new LinkedHashSet<>());
    }
    
    public static Collection<Map<String,Object>> query(String sql, Collection<Object> params) throws SQLException {
        Connection conn = JDBCUtils.getConnection();
        PreparedStatement ps = JDBCUtils.getPreparedStatement(conn, sql, params);
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        Collection col = new LinkedHashSet<>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < colCount; i++) {
                String name = md.getColumnLabel(i+1);
                Object value = rs.getObject(i+1);
                map.put(name, value);
            }
            col.add(map);
        }
        conn.close();
        return col;
    }
}
