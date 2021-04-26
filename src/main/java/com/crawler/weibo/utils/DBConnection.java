package com.crawler.weibo.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://59.77.233.214:3306/Subsidies_zn";
    // 数据库账号密码
    static final String USER = "root";
    static final String PASS = "root";
    private static Connection conn;

    public static Connection getConn()  {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // TODO 自动生成的 catch 块
            e1.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

        return conn;
    }
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void closeConn(Connection conn) throws SQLException {
        if(conn!=null){
            conn.close();
            conn=null;
        }
    }
    public static void closeRs(ResultSet rs) throws Exception{
        if(rs!=null){
            rs.close();
            rs=null;
        }
    }
    public static ResultSet queryDQL(String sql,Connection conn, String ToolTips) {
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            System.out.println(ToolTips+"成功");
            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }
    public static int queryDML(String sql,Connection conn, String ToolTips) {
        PreparedStatement ps=null;
        try {
            //System.out.println(conn);
            ps = conn.prepareStatement(sql);
            int rs = ps.executeUpdate();
            System.out.println(ToolTips+"成功");
            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return -1;
        }
    }
}
