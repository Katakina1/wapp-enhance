package com.xforceplus.wapp.common.utils;


import java.sql.*;

/**
 * @author raymond.yan
 */


public class JDBCUtils {

    private static final String DBDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";// 驱动类类名

    private static final String DBNAME = "NewVendorMaster";// 数据库名
    //  jdbc:sqlserver://localhost:1433;DatabaseName=school","sa","tjw19951105"
    private static final String DBURL = "jdbc:sqlserver://PCNNT51007SQL;instanceName=ni1;DatabaseName="+DBNAME;// 连接URL

    private static final String DBUSER = "SVCCNWAPP";// 数据库用户名

    private static final String DBPASSWORD = "Za^OAdryR%nOL9aja0Lw";// 数据库密码

    private static Connection conn = null;

    private static PreparedStatement ps = null;

    private static ResultSet rs = null;

    /*
     * 获取数据库连接
     */
    public static Connection getConnection() {

        try {

            Class.forName(DBDRIVER);// 注册驱动

            conn = DriverManager.getConnection(DBURL,DBUSER,
                    DBPASSWORD);// 获得连接对象
            System.out.println("成功加载SQL Server驱动程序");
        } catch (ClassNotFoundException e) {// 捕获驱动类无法找到异常

            System.out.println("找不到SQL Server驱动程序");
            System.out.println(e.toString());
            e.printStackTrace();

        } catch (SQLException e) {// 捕获SQL异常

            e.printStackTrace();
        }

        return conn;

    }

    public static ResultSet select(String sql) throws Exception {

        try {

            conn = getConnection();

            ps = (PreparedStatement) conn.prepareStatement(sql);

            rs = ps.executeQuery();

            return rs;

        } catch (SQLException sqle) {

            throw new SQLException("select data Exception: "
                    + sqle.getMessage());

        } catch (Exception e) {

            throw new Exception("System error: " + e.getMessage());

        }

    }

    /*
     * 增删改均调用这个方法
     */
    public static void updata(String sql) throws Exception {

        try {

            conn = getConnection();

            ps = (PreparedStatement) conn.prepareStatement(sql);

            ps.executeUpdate();

        } catch (SQLException sqle) {

            throw new SQLException("insert data Exception: "
                    + sqle.getMessage());

        } finally {

            try {

                if (ps != null) {

                    ps.close();

                }

            } catch (Exception e) {

                throw new Exception("ps close exception: " + e.getMessage());

            }

            try {

                if (conn != null) {

                    conn.close();

                }

            } catch (Exception e) {

                throw new Exception("conn close exception: " + e.getMessage());

            }

        }

    }
}