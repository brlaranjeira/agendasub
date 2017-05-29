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
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brlaranjeira
 */
public class JDBCUtils {
    
    private static final String BD_URL = "jdbc:mysql://bdctism/dev_agendasub";
    private static final String BD_USR = "dev_agendasub";
    private static final String BD_PW = "12345";
    private static Boolean init = Boolean.FALSE;
    
    private static Connection getConnection() {
        if (!init) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                init = Boolean.TRUE;
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        try {
            return DriverManager.getConnection(BD_URL, BD_USR, BD_PW);
        } catch (SQLException ex) {
            return null;
        }
    }
    
    private static PreparedStatement getPreparedStatement(Connection conn, String sql, Collection<Object> params, Boolean isInsert) throws SQLException {
        PreparedStatement ps = isInsert ? conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        int i = 1;
        for (Object param : params) {
            if (param != null) {
                ps.setObject(i++, param);
            } else {
                ps.setNull(i++, java.sql.Types.NULL);
            }
        }
        return ps;
    }
    
//    public static Collection<Map<String,Object>> query( String sql ) throws SQLException {
//        return query(sql,new LinkedHashSet<>());
//    }
    
    
    public static Collection<Map<String,Object>> query( String sql, Object... params ) throws SQLException {
        return query(sql,Arrays.asList(params));
    }
    
    private static Collection<Map<String,Object>> query(String sql, Collection<Object> params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = getPreparedStatement(conn, sql, params, false);
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
    
    public static Connection beginTransaction() {
        try {
            Connection c = getConnection();
            c.setAutoCommit(Boolean.FALSE);
            return c;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public static Boolean rollBack( Connection conn ) {
        try {
            conn.rollback();
            conn.close();
            return Boolean.TRUE;
        }catch (SQLException ex) {
            return Boolean.FALSE;
        }
    }
    
    public static Boolean commit( Connection conn ) {
        try {
            conn.commit();
            conn.close();
            return Boolean.TRUE;
        } catch (SQLException ex) {
            return Boolean.FALSE;
        }
    }
    
    public static Integer makeInsert(Connection conn, String sql, Object... params) throws SQLException {
        return makeInsert(conn, sql, Arrays.asList(params));
    }
    
    private static Integer makeInsert(Connection conn, String sql, Collection<Object> params) throws SQLException {
        conn = conn == null ? getConnection() : conn;
        PreparedStatement ps = getPreparedStatement(conn, sql, params, true);
        int affectedRows = ps.executeUpdate();
        if (affectedRows == 0) {
            return null;
        }
        ResultSet rs = ps.getGeneratedKeys();
        if (conn.getAutoCommit()) {
            conn.close();
        }
        return rs.next() ? rs.getInt(1) : null;
    }

    public static Integer delete(Connection conn, String sql, Object... params) throws SQLException {
        return deleteOrUpdate(conn,sql,Arrays.asList(params));
    }
    
    public static Integer update( Connection conn, String sql, Object... params ) throws SQLException {
        return deleteOrUpdate(conn, sql, Arrays.asList(params));
    }
    
    private static Integer deleteOrUpdate(Connection conn, String sql, Collection<Object> params) throws SQLException {
        conn = conn == null ? getConnection() : conn;
        PreparedStatement ps = getPreparedStatement(conn, sql, params, false);
        int affectedRows = ps.executeUpdate();
        if (conn.getAutoCommit()) {
            conn.close();
        }
        return affectedRows;
    }

    
}