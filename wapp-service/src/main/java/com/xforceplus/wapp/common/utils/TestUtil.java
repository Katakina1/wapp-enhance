package com.xforceplus.wapp.common.utils;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestUtil {
    @Test
    public void Test(){
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            // 获取连接
            conn = DB2Conn.getConnection();

            // 编写sql
            String sql = "SELECT B.PO_NBR,B.TXN_TYPE_CODE,B.VENDOR_NBR,B.RECEIVER_NBR,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,TXN_VENDOR_NAME from CNINVMAT.FINANCIAL_TXN  B  INNER JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID where C.COUNTRY_CODE='CN' ";


            // 创建语句执行者
            st= conn.prepareStatement(sql);

            //设置参数

            // 执行sql
            rs=st.executeQuery();



        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DB2Conn.colseResource(conn, st, rs);
        }

    }

}
