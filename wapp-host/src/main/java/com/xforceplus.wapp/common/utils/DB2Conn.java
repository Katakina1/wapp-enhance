package com.xforceplus.wapp.common.utils;

import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
public class DB2Conn {
    private static String driver="com.ibm.db2.jcc.DB2Driver";

    //private static String url="jdbc:db2://DSN4DRDA:443/DSN4";
    private static String url="jdbc:db2://DSN4DRDA.wal-mart.com:443/DSN4";
    private static String user="DB2ISDCN";
    private static String password="XPWGQTN6";
    private static final Logger LOGGER= getLogger(DB2Conn.class);
    private DB2Conn(){}

    static {
        /**
         * 驱动注册
         */
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            LOGGER.info("DB2CONN {}" ,e);
            throw new ExceptionInInitializerError(e);
        }

    }

    /**
     * 获取 Connetion
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        Connection conn =  DriverManager.getConnection(url, user, password);
//        conn.setReadOnly(true);
//        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        return conn;
    }

    /**
     * 释放资源
     * @param conn
     * @param st
     * @param rs
     */
    public static void colseResource(Connection conn,Statement st,ResultSet rs) {
        closeResultSet(rs);
        closeStatement(st);
        closeConnection(conn);
    }

    /**
     * 释放连接 Connection
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if(conn !=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        conn = null;
    }

    /**
     * 释放语句执行者 Statement
     * @param st
     */
    public static void closeStatement(Statement st) {
        if(st !=null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        st = null;
    }

    /**
     * 释放结果集 ResultSet
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if(rs !=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        rs = null;
    }

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<PoEntity> list= Lists.newArrayList();
        try {
            // 获取连接
            conn = DB2Conn.getConnection();

            // 编写sql
//            String sql = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_id,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'2018-11-01 00:00:00') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where C.COUNTRY_CODE='CN' and B.TRANSACTION_date >'2018-11-01' AND B.TXN_TYPE_CODE in(1,2,4) and PO_NBR !=0000000000 AND D.PROCESS_STAT_CODE in(14,12,11,15,19,9,99,999) order by B.TRANSACTION_date DESC";
            String sql9 = "SELECT  count(*) from CNINVMAT.FINANCIAL_TXN B WHERE B.POST_DATE>'2016-12-24'";

//            String sql = "SELECT B.PO_NBR,B.TXN_TYPE_CODE,B.VENDOR_NBR,B.RECEIVER_NBR,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,TXN_VENDOR_NAME from CNINVMAT.FINANCIAL_TXN  B  INNER JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID where C.COUNTRY_CODE='CN' AND B.PO_NBR='2350414767' ";


            // 创建语句执行者
            st= conn.prepareStatement(sql9);

            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs=st.executeQuery();
            System.out.println("连接成功");
            System.out.println(new Date().toString());
            while(rs.next()){
                Long count = rs.getLong(1);
                System.out.println(count);


//                PoEntity po= new PoEntity(pocode,venderid,tractionNo.substring(tractionNo.length()-10,tractionNo.length()),amount,poType,postDate,taxRate,hoststatus,invoiceId,tractionNo,tractionDate,jvcode,"",tractionId);

//                    list.add(po);
            }

        } catch (Exception e) {
            LOGGER.info("{}",e);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);

        }


    }

//    public static void main(String[] args) {
//        SortedMap<String, Charset> mm = Charset.availableCharsets();
//
//        Set<String> set = mm.keySet();
//
//        Iterator<String> it = set.iterator();
//
//        while (it.hasNext()) {
//            System.out.println("JDK支持的字符集为:" + it.next());
//        }
//
//        int size = Charset.availableCharsets().size();
//
//        System.out.println("====================================");
//
//        System.out.println("JDK支持的字符集的个数==" + size);
//
//        System.out.println("平台默认的字符集为:" + Charset.defaultCharset());
//
//    }



}

