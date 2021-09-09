package com.xforceplus.wapp.modules.posuopei.service.impl;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.DB2Conn;

import com.xforceplus.wapp.modules.job.utils.JMSProducer;
import com.xforceplus.wapp.modules.posuopei.dao.DetailsDao;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;

import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.google.common.collect.Lists;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;

import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

@Service("matchService")
public class MatchServiceImpl implements MatchService {
    private static final Logger LOGGER= getLogger(MatchServiceImpl.class);

    /**
     * MQ队列名称
     * */
    @Value("${activemq.queue}")
    private String queueName;

    @Value("${activemq.queue_two}")
    private String queueName2;

    @Value("${activemq.queue_three}")
    private String queueName3;

    @Value("${activemq.queue_four}")
    private String queueName4;

    @Value("${activemq.queue_five}")
    private String queueName5;

    @Value("${activemq.queue_six}")
    private String queueName6;

    @Value("${activemq.queue_seven}")
    private String queueName7;

    @Value("${activemq.queue_eight}")
    private String queueName8;

    @Autowired
    MatchDao matchDao;
    @Autowired
    DetailsDao detailsDao;
    @Autowired
    private JMSProducer producer;
//    @Autowired
//    private JMSProducer producer1;
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;

    @Override
    public List<InvoiceEntity> ifExist(Map<String, Object> map) {
        return matchDao.ifExist(map);
    }

    @Override
    public Boolean match(MatchEntity matchEntity) {
        return false;
    }

    @Override
    public List<OrgEntity> getDicdeta(String theKey) {
        return matchDao.getDicdeta(theKey);
    }

    @Override
    public Boolean checkInvoiceMessage(String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount) {
        Boolean flag=true;

        if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
            flag=false;
        }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
            flag=false;
        }else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$") && "1.5".equals(taxRate) == false){
            flag=false;
        }else if((!CommonUtil.isValidNum(invoiceAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(totalAmount,"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum(taxAmount,"^[0-9]+(.[0-9]{2})?$"))){
            flag=false;
        }else if((!CommonUtil.isValidNum(invoiceDate,"/^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/"))){
            flag=false;
        }

        if(flag){
            BigDecimal amount=new BigDecimal(invoiceAmount);
            BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
            BigDecimal taxAmount1=new BigDecimal(taxAmount);
            BigDecimal rest=taxAmount1.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
            if(rest.compareTo(BigDecimal.ZERO)>0){
                flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);
            }else{
                flag=rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0;
            }
        }
        return flag;
    }

    @Override
    public String getFplx(String fpdm) {
        String fplx = "";
        if (fpdm.length() == 12) {
            String zero=fpdm.substring(0,1);
            String lastTwo=fpdm.substring(10,12);
            if("0".equals(zero) && ("04".equals(lastTwo) || "05".equals(lastTwo))){
                fplx="04";
            }
        } else if (fpdm.length() == 10) {
            String fplxflag = fpdm.substring(7, 8);
            if ("6".equals(fplxflag) || "3".equals(fplxflag)) {
                fplx = "04";
            }
        }
        return fplx;
    }

    @Override
    public Integer insertPoList(List<PoEntity> list) {
        List<PoEntity> poEntityList1=Lists.newArrayList();
        Integer s=0;

        for (Integer c = 0; c < list.size(); c++) {
            if (s == 100) {
                //批量插入订单数据
                Integer count=1;
                try {
                    count = this.insertPoListTraction(poEntityList1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (count > 0) {
                    poEntityList1.clear();
                    s = 0;
                }
            }   //批量插入订单数据
            poEntityList1.add(list.get(c));
            s++;
        }

        if(poEntityList1.size()>0){
            try {
                return this.insertPoListTraction(poEntityList1);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }
    }

    @Override
    public Integer insertPoListCopy(List<PoEntity> list) {
        List<PoEntity> poEntityList1=Lists.newArrayList();
        Integer s=0;
        for(Integer c=0;c<list.size();c++){
            if(s==100){
                //批量插入订单数据
                Integer count=1;
                try {
                    count=this.insertPoListCopy1(poEntityList1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(count>0){
                    poEntityList1.clear();
                    s=0;
                }

            }   //批量插入订单数据
            poEntityList1.add(list.get(c));
            s++;
        }
        if(poEntityList1.size()>0){
            try {
                return this.insertPoListCopy1(poEntityList1);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }
    }

    @Override
    public  void connHostPo1(String date1 ,int i){
        Boolean flag=true;
        int k=0;
        Connection conn = null;
        PreparedStatement st1 = null;
        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();
        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            //po
            //type=1,hoststatus=u,pocode= trans_nbr,receipt=receiver_nbr
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=1   and D.process_status_ts>'"+date1+"'  and B.POST_DATE>'2016-12-25' order by B.TRANSACTION_date DESC";

            // 创建语句执行者
            st1=conn.prepareStatement(sql1);

            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs1=st1.executeQuery();
            matchDao.insertTaskLog("connHostPo1","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setListPoType1(rs1);
            this.getHostData(list1,"1","u",i);
            matchDao.insertTaskLog("connHostPo1","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostPo1","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }

        connHostPo2(date1,0);
        connHostPo4(date1,0);
    }

    @Override
    public  void connHostPo2(String date1,int i){
        Boolean flag=true;
        int k=0;
        Connection conn = null;
        PreparedStatement st2 = null;
        ResultSet rs2 = null;

        List<PoEntity> list2= Lists.newArrayList();
        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            //po
            String sql2 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=2 AND B.PO_NBR !=0000000000  and D.process_status_ts>'"+date1+"'  and B.POST_DATE>'2016-12-25' order by B.TRANSACTION_date DESC";
            // 创建语句执行者
            st2=conn.prepareStatement(sql2);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs2=st2.executeQuery();
            System.out.println("连接成功");
            System.out.println(new Date().toString());
            matchDao.insertTaskLog("connHostPo2","start",date1);
            list2=this.setList(rs2);
            this.getHostData(list2,"2","u",i);
            matchDao.insertTaskLog("connHostPo2","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostPo2","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }
    }

    @Override
    public void connHostPo4(String date1,int i){
        Boolean flag=true;
        int k=0;
        Connection conn = null;
        PreparedStatement st1 = null;
        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();

        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=4 AND B.PO_NBR !=0000000000  and D.process_status_ts>'"+date1+"'  and B.POST_DATE>'2016-12-25' order by B.TRANSACTION_date DESC";
            // 创建语句执行者
            st1=conn.prepareStatement(sql1);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs1=st1.executeQuery();
            matchDao.insertTaskLog("connHostPo4","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());

            list1=this.setList(rs1);

            this.getHostData(list1,"4","u",i);
            matchDao.insertTaskLog("connHostPo4","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostPo4","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
            //删除订单表超两年货款转收入的信息，订单号为700888、756888以及538520
            matchDao.delPo();
        }
    }

    @Override
    public  void connHostClaimType2(String date1,int i){
        Boolean flag=true;
        Connection conn = null;
        PreparedStatement st2 = null;
        ResultSet rs2 = null;
        List<PoEntity> list2= Lists.newArrayList();

        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            String sql2 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=2 AND B.PO_NBR=0000000000 and D.process_status_ts>'"+date1+"'  and B.POST_DATE>'2016-12-25'  order by B.TRANSACTION_date DESC";
            // 创建语句执行者
            st2=conn.prepareStatement(sql2);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs2=st2.executeQuery();
            matchDao.insertTaskLog("connHostClaimType2","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());
            list2=this.setList(rs2);
            this.getHostData(list2,"5","u",i);
            matchDao.insertTaskLog("connHostClaimType2","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostClaimType2","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }

        this.connHostClaimType3( date1, 0);
    }

    @Override
    public  void connHostClaimType3(String date1,int i){
        Boolean flag=true;
        int k=0;
        Connection conn = null;
        PreparedStatement st1 = null;
        ResultSet rs1 = null;

        List<PoEntity> list1= Lists.newArrayList();
        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            //calim
            //type=3,hoststatus=u,claimno=transaction_nbr
            String sql1 = "SELECT  B.PO_NBR,B.TXN_TYPE_CODE ,B.VENDOR_NBR,B.RECEIVER_NBR ,B.TXN_COST_AMT,B.POST_DATE,A.INVOICE_TAX_RATE,C.FI4_REPORT_CODE,B.TXN_VENDOR_NAME,B.TRANSACTION_NBR,B.invoice_NBR,B.TRANSACTION_date,B.TRANSACTION_id,D.PROCESS_STAT_CODE,B.DUE_DATE,B.TXN_SEQ_NBR,B.vendor_dept_nbr,B.STORE_NBR from CNINVMAT.FINANCIAL_TXN  B  LEFT JOIN   CNINVMAT.INVOICE_TAX  A ON A.INVOICE_ID=B.INVOICE_ID INNER JOIN CNAPCNTL.AP_COMPANY_REPORT C on C.AP_COMPANY_ID=B.AP_COMPANY_ID LEFT JOIN (select   a.* from (SELECT   OC.* , Row_Number() OVER (partition by TRANSACTION_id,TXN_SEQ_NBR ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.TXN_PROCESS_LOG  OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.TRANSACTION_id=B.TRANSACTION_id AND D.TXN_SEQ_NBR=B.TXN_SEQ_NBR where C.COUNTRY_CODE='CN' AND B.TXN_TYPE_CODE=3 and D.process_status_ts>'"+date1+"'  and B.POST_DATE>'2016-12-25' order by B.TRANSACTION_date DESC";
            // 创建语句执行者
            st1=conn.prepareStatement(sql1);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs1=st1.executeQuery();
            matchDao.insertTaskLog("connHostClaimType3","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());
            list1=this.setList(rs1);
            this.getHostData(list1,"3","u",i);
            matchDao.insertTaskLog("connHostClaimType3","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostClaimType3","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }
    }

    public  String connHostAgainOne(String date1,String invoiceNo,String vendorId){
        Boolean flag=true;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        vendorId = vendorId.replaceAll("^(0+)", "");
        String hostStatus=null;
        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            String sql = "select B.invoice_nbr,B.invoice_date,D.PROCESS_STAT_CODE,B.vendor_nbr,D.process_status_ts from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+date1+"' and B.invoice_nbr = '0000000"+invoiceNo+"' and B.vendor_nbr ='"+vendorId+"'";
            // 创建语句执行者
            st= conn.prepareStatement(sql);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs=st.executeQuery();
            matchDao.insertTaskLog("connHostAgain","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());

            while (rs.next()){



                hostStatus=rs.getString(3);
                return hostStatus;
//                this.hostDeleteInvoice( invoiceNo,  hostStatus,  invoiceDate, vendor,  hostDate);
            }
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }
        if(!flag) {
            return null;

        }else {
            return hostStatus;
        }
    }



    @Override
    public  void connHostAgain(String date1){
        Boolean flag=true;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            // 编写sql
            String sql = "select B.invoice_nbr,B.invoice_date,D.PROCESS_STAT_CODE,B.vendor_nbr,D.process_status_ts from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+date1+"'";
            // 创建语句执行者
            st= conn.prepareStatement(sql);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs=st.executeQuery();
            matchDao.insertTaskLog("connHostAgain","start",date1);
            System.out.println("连接成功");
            System.out.println(new Date().toString());
            String invoiceNo=null;
            String invoiceDate=null;
            String hostStatus=null;
            String vendor=null;
            java.sql.Date hostDate=null;
            while (rs.next()){
                invoiceNo=rs.getString(1).trim();
                invoiceNo=invoiceNo.substring(invoiceNo.length()-8,invoiceNo.length());
                invoiceDate=rs.getString(2)+" 00:00:00";
                hostStatus=rs.getString(3);
                vendor=rs.getString(4);
                hostDate=rs.getDate(5);
                vendor=vendor.substring(0,vendor.length()-3);
                int length=vendor.length();
                if(6-length>0){
                    for(int i=0;i<6-length;i++){
                        vendor="0"+vendor;
                    }
                }
                this.hostDeleteInvoice( invoiceNo,  hostStatus,  invoiceDate, vendor,  hostDate);
            }
            matchDao.insertTaskLog("connHostAgain","end",date1);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("connHostAgain","exception",date1);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }

        if(!flag) {
            connHostAgain(date1);
        }
    }

    @Override
    public  void getHostClaimDetail(Integer day){
        Boolean flag=true;
        Connection conn = null;
        PreparedStatement st = null;

        ResultSet rs = null;
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        currentTime = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = formatter.format(currentTime);
        try {
            // 获取连接
            conn = DB2Conn.getConnection();
            //GET CLAIM DETATIL
            // 获取连接
            // 编写sql
            String sql1 = "SELECT STORE_NBR,FINAL_DATE,CONTROL_NBR AS CLAIM_NBR,INT(T2.VENDOR_NBR||RIGHT(100+T2.VENDOR_DEPT_NBR,2)||T2.VENDOR_SEQ_NBR) VNDR_NBR,T1.ITEM_NBR,T1.UPC_NBR,T2.DEPT_NBR,T2.ITEM1_DESC AS CN_DESC,T2.VENDOR_STOCK_ID,COST_AMT AS VNPK_COST,COST_MULT AS VNPK_QTY,(COST_AMT/COST_MULT) AS UNIT_COST,ITEM_QTY,(COST_AMT*ITEM_QTY/COST_MULT) AS LINE_COST,CASE RATE_GROUP_CODE WHEN '1' THEN '0%' WHEN '2' THEN '10%' WHEN '3' THEN '16%' END AS TAX_RATE,SUBSTR(T2.FINELINE_NBR,1,2) AS CATEGORY_NBR FROM CNBKROOM.BKRM_PROC_XMIT_DTL T1,CNITEM.ITEM T2,CNINTLTR.ITEM_TAX_RATE T3 WHERE TRANSACTION_TYPE=10 AND FINAL_DATE = '" + date2 + "' AND T1.ITEM_NBR=T2.ITEM_NBR AND T1.ITEM_NBR=T3.ITEM_NBR AND TAX_TYPE='IVA' ORDER BY 1,2,3";
            // 创建语句执行者
            st = conn.prepareStatement(sql1);
            //设置参数
            System.out.println("连接成功");
            // 执行sql
            rs = st.executeQuery();
            matchDao.insertTaskLog("getHostClaimDetail", "start", date2);

            while (rs.next()) {
                String storeNbr = rs.getString(1);
                java.sql.Date finalDate = rs.getDate(2);
                String returnGoodsCode = rs.getString(3);
                String vndrNbr = rs.getString(4);
                String itemNbr = rs.getString(5);
                String upcNbr = rs.getString(6);
                String deptNbr = rs.getString(7);
                String goodsName = "";
                try {
                    goodsName = rs.getString(8);
                } catch (Exception e) {
                    goodsName = "-4420";
                }

                String vendorStockId = rs.getString(9);
                BigDecimal vnpkCost = rs.getBigDecimal(10);
                int vnpkQty = rs.getInt(11);
                BigDecimal goodsPrice = rs.getBigDecimal(12);
                int goodsNumber = rs.getInt(13);
                BigDecimal goodsAmount = rs.getBigDecimal(14);
                String taxRate = rs.getString(15);
                String gategoryNbr = rs.getString(16);
                ClaimDetailEntity claimDetailEntity = new ClaimDetailEntity(storeNbr, finalDate, returnGoodsCode, vndrNbr, itemNbr, upcNbr, deptNbr, goodsName, vendorStockId, vnpkCost, vnpkQty, goodsPrice, goodsNumber, goodsAmount, taxRate, gategoryNbr);
                this.insertClaimDetail(claimDetailEntity);
            }
            matchDao.insertTaskLog("getHostClaimDetail", "end", date2);
        } catch (Exception e) {
            flag=false;
            LOGGER.info("{}",e);
            matchDao.insertTaskLog("getHostClaimDetail","exception",date2);
        }finally {
            System.out.println("close connect");
            System.out.println(new Date().toString());
            DB2Conn.closeConnection(conn);
        }

        if(!flag) {
            getHostClaimDetail(day);
        }
    }

    @Override
    public  JSONArray writeScreen(MatchEntity matchEntity){
        //用于记录
        List<HostWriterScreenEntity> hostWriterScreenEntityList=Lists.newArrayList();
        SimpleDateFormat s=new SimpleDateFormat("ddMMyyyy");
        BigDecimal tempAmount=new BigDecimal(0);
//        List<InvoiceEntity> invoiceList = matchDao.invoiceList(matchEntity.getMatchno());

//        matchEntity.setInvoiceEntityList(invoiceList);
//        matchEntity.setPoEntityList(matchDao.hostPoList(matchEntity.getMatchno()));
//        matchEntity.setClaimEntityList(matchDao.claimList(matchEntity.getMatchno()));
        List<PoEntity> poEntityList=Lists.newArrayList();
        List<PoEntity> poEntities=matchEntity.getPoEntityList();
        poEntityList.addAll(poEntities);
        BigDecimal claimTotal=new BigDecimal(0);
        for(int i=0;i<matchEntity.getClaimEntityList().size();i++) {
            ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(i);
            claimTotal = claimTotal.add(claimEntity.getClaimAmount());
        }
        Date dueDate=matchEntity.getDueDate();
        LOGGER.info("-------------------------dueDate {} :",dueDate);
        if(dueDate==null){
            dueDate=new Date();
            LOGGER.info("---------------------new dueDate {}:",dueDate);
        }
        Calendar thisdate = Calendar.getInstance();
        thisdate.setTime(new Date());

        //step2 遍历发票
        for(int i=0;i<matchEntity.getInvoiceEntityList().size();i++) {
            InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
            BigDecimal InSubClaim=new BigDecimal(0);
            List<LoopEntity> loopEntities=Lists.newArrayList();

            HostWriterScreenEntity hostWriterScreenEntity=new HostWriterScreenEntity();
            WriterScreenDataEntity writerScreenDataEntity = new WriterScreenDataEntity();
            writerScreenDataEntity.setJv(invoiceEntity.getJvcode());
            writerScreenDataEntity.setVender(matchEntity.getVenderid());
            writerScreenDataEntity.setInv(invoiceEntity.getInvoiceNo());
//            writerScreenDataEntity.setError("${error}");


            Date date = null;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(invoiceEntity.getInvoiceDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar now = Calendar.getInstance();
            now.setTime(date);
            String year = String.valueOf(now.get(Calendar.YEAR));
            String month = String.valueOf(now.get(Calendar.MONTH) + 1); // 0-based!
            if (month.length() == 1) {
                month = "0" + month;
            }
            String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
            if (day.length() == 1) {
                day = "0" + day;
            }
            Calendar now1 = Calendar.getInstance();
            now1.setTime(dueDate);
            if (month.length() == 1) {
                month = "0" + month;
            }
            now1.add(Calendar.DAY_OF_MONTH,-2);
            String day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));
            String year1= String.valueOf(now1.get(Calendar.YEAR));
            String month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
            if(now1.compareTo(thisdate)<0){
                Calendar cc=Calendar.getInstance();
                cc.setTime(new Date());
                cc.add(Calendar.DAY_OF_MONTH,+3);
                now1=cc;
                year1= String.valueOf(now1.get(Calendar.YEAR));
                month1 = String.valueOf(now1.get(Calendar.MONTH) + 1); // 0-based!
                if (month.length() == 1) {
                    month = "0" + month;
                }
                day1 = String.valueOf(now1.get(Calendar.DAY_OF_MONTH));
            }
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (day1.length() == 1) {
                day1 = "0" + day1;
            }
            if (month1.length() == 1) {
                month1 = "0" + month1;
            }
            writerScreenDataEntity.setYY1(year1);
            writerScreenDataEntity.setMM1(month1);
            writerScreenDataEntity.setDD1(day1);
            writerScreenDataEntity.setYY(year);
            writerScreenDataEntity.setMM(month);
            writerScreenDataEntity.setDD(day);


            String taxTotal=String.valueOf(invoiceEntity.getTaxAmount());
            if(taxTotal.length()>2){
                writerScreenDataEntity.setTaxTotal(taxTotal.substring(0,taxTotal.length()-2));
            }else{
                writerScreenDataEntity.setTaxTotal(taxTotal);
            }
            String taxRate=String.valueOf(invoiceEntity.getTaxRate());
            if(taxRate.length()>2){
                writerScreenDataEntity.setTaxRate(taxRate.substring(0,taxRate.length()-2));
            }else{
                writerScreenDataEntity.setTaxRate(taxRate);
            }
            if ("01".equals(invoiceEntity.getInvoiceType())) {
                writerScreenDataEntity.setTaxTypeZ("X");
                writerScreenDataEntity.setTaxType(" ");
            } else if ("04".equals(invoiceEntity.getInvoiceType())) {
                writerScreenDataEntity.setTaxType("X");
                writerScreenDataEntity.setTaxTypeZ(" ");
            }
            String totalAmount=String.valueOf(invoiceEntity.getTotalAmount());
            if(totalAmount.length()>2){
                writerScreenDataEntity.setInvTotal(totalAmount.substring(0,totalAmount.length()-2));
            }else{
                writerScreenDataEntity.setInvTotal(totalAmount);
            }
            writerScreenDataEntity.setPoNbr(matchEntity.getPoEntityList().get(0).getPocode());
            writerScreenDataEntity.setPayCode("1");
            writerScreenDataEntity.setError("${error}");
            writerScreenDataEntity.setBalance("${balance}");

            hostWriterScreenEntity.setLogin_store_id("");
            hostWriterScreenEntity.setScreen_name("CICCNMP_FAPI");
            hostWriterScreenEntity.setSystem_name("");
            hostWriterScreenEntity.setId(matchEntity.getPoEntityList().get(0).getTractionIdSeq());

            if(i==0) {

                //先结索赔
                for (int j = 0; j < matchEntity.getClaimEntityList().size(); j++) {
                    LoopEntity loopEntity=new LoopEntity();
                    ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(j);


                    loopEntity.setSeq(claimEntity.getSeq());

                    loopEntity.setD1(day1);
                    loopEntity.setM1(month1);
                    loopEntity.setY1(year1);

                    loopEntity.setIfFapr("0");
                    loopEntity.setCover("");
                    loopEntity.setTransaction(claimEntity.getClaimno());
                    loopEntity.setReceiver("0000000000");
                    loopEntity.setCpn("0000000000");

                    String invPreTaxAmt=String.valueOf(claimEntity.getClaimAmount());
                    if(invPreTaxAmt.length()>2){
                        loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                    }else{
                        loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                    }
                    loopEntity.setA(invPreTaxAmt);
                    loopEntity.setIfCut("0");
                    loopEntity.setE("");
//                    hostWriterScreenEntity.setData(writerScreenDataEntity);

                    LOGGER.info("索赔 {}",claimEntity.getClaimno());

                    loopEntities.add(loopEntity);
//                    hostWriterScreenEntityList.add(hostWriterScreenEntity);
                }
                //发票和索赔总金额 InSubClaim
                InSubClaim=invoiceEntity.getInvoiceAmount().subtract(claimTotal);
            }else{
                //发票金额 InSubClaim
                InSubClaim=invoiceEntity.getInvoiceAmount();
            }

            //如果InSubClaim余额大于0，再借订单
            if((InSubClaim.compareTo(BigDecimal.ZERO))>0) {
                for (int k = 0; k < poEntities.size(); k++) {
                    LoopEntity loopEntity=new LoopEntity();
                    PoEntity poEntity = poEntities.get(k);
                    loopEntity.setSeq(poEntity.getSeq());

                    loopEntity.setD1(day1);
                    loopEntity.setM1(month1);
                    loopEntity.setY1(year1);
                    loopEntity.setE("");
                    loopEntity.setIfFapr("0");
                    loopEntity.setCover("");

                        loopEntity.setTransaction(poEntity.getTractionNbr());
                        loopEntity.setReceiver(poEntity.getReceiptid());
                        loopEntity.setCpn(poEntity.getPocode());
                        String a=String.valueOf(poEntity.getAmountpaid());
                        a=a.substring(0,a.length()-2);
                        loopEntity.setA(a);
                        //加上收货日期
                        String d=s.format(poEntity.getReceiptdate());
                        loopEntity.setD(d);
                    Boolean flag = true;
                    if(poEntityList.size()==0){
                        String invPreTaxAmt=String.valueOf(InSubClaim);
                        if(invPreTaxAmt.length()>2){
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                        }else{
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                        }
                        loopEntity.setIfCut("0");

                        loopEntity.setA("0.00");
                        if(matchEntity.getCover().compareTo(new BigDecimal(20))>0||matchEntity.getCover().compareTo(new BigDecimal(-20))<0) {
                            loopEntity.setIfFapr("1");
                            loopEntity.setCover(String.valueOf(new BigDecimal("0.00").subtract(InSubClaim)));
                        }
                    }else if (InSubClaim.subtract(poEntity.getAmountpaid()).compareTo(new BigDecimal(0)) > 0) {
                        if(poEntityList.size()-1==0){
                             String invPreTaxAmt=String.valueOf(InSubClaim);
                            if(invPreTaxAmt.length()>2){
                                loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                            }else{
                                loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                            }
                            loopEntity.setIfCut("1");
                            if(matchEntity.getCover().compareTo(new BigDecimal(20))>0||matchEntity.getCover().compareTo(new BigDecimal(-20))<0) {
                                loopEntity.setIfFapr("1");
                                loopEntity.setCover(String.valueOf(poEntity.getAmountpaid().subtract(InSubClaim)));
                            }
                        }else{
                            String invPreTaxAmt=String.valueOf(poEntity.getAmountpaid());
                            if(invPreTaxAmt.length()>2){
                                loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                            }else{
                                loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                            }
                            InSubClaim = InSubClaim.subtract(poEntity.getAmountpaid());
                            loopEntity.setIfCut("0");

                        }

                        poEntityList.remove(0);
                    }else if(InSubClaim.subtract(poEntity.getAmountpaid()).compareTo(new BigDecimal(0)) == 0){
                        String invPreTaxAmt=String.valueOf(poEntity.getAmountpaid());
                        if(invPreTaxAmt.length()>2){
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                        }else{
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                        }
                        InSubClaim = InSubClaim.subtract(poEntity.getAmountpaid());
                        loopEntity.setIfCut("0");
                        poEntityList.remove(0);
                        flag = false;
                    }else {
                        String invPreTaxAmt=String.valueOf(InSubClaim);
                        if(invPreTaxAmt.length()>2){
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt.substring(0,invPreTaxAmt.length()-2));
                        }else{
                            loopEntity.setInvPreTaxAmt(invPreTaxAmt);
                        }
                        loopEntity.setIfCut("1");
                        tempAmount = poEntity.getAmountpaid().subtract(InSubClaim);
                        poEntity.setAmountpaid(tempAmount);
                        poEntityList.set(0,poEntity);
                        flag = false;
                    }
//                    hostWriterScreenEntity.setData(writerScreenDataEntity);
//                    hostWriterScreenEntity.setLogin_store_id("");
//                    hostWriterScreenEntity.setScreen_name("CICCNMP_FAPI");
//                    hostWriterScreenEntity.setSystem_name("");
//                    hostWriterScreenEntity.setId(poEntity.getTractionIdSeq());
                    LOGGER.info("订单 {}",poEntity.getReceiptid());
                    loopEntities.add(loopEntity);
//                    hostWriterScreenEntityList.add(hostWriterScreenEntity);
                    if (!flag) {
                        poEntities.clear();
                        poEntities.addAll(poEntityList);
                        break;
                    }
                }
            }
            loopEntities.get(loopEntities.size()-1).setE("1");
            writerScreenDataEntity.setComments(loopEntities);
            hostWriterScreenEntity.setData(writerScreenDataEntity);
            hostWriterScreenEntityList.add(hostWriterScreenEntity);
        }
//        if(hostWriterScreenEntityList.size()>0){
//
//            if((matchEntity.getCover().compareTo(new BigDecimal(0)))<0){
//                hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().get(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().size()-1).setInvPreTaxAmt(String.valueOf(new BigDecimal(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().get(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().size()-1).getInvPreTaxAmt()).subtract(matchEntity.getCover())));
//            }
//            if(matchEntity.getCover().compareTo(new BigDecimal(20))>0||matchEntity.getCover().compareTo(new BigDecimal(-20))<0){
//                hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().get(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().size()-1).setIfFapr("1");
//                hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().get(hostWriterScreenEntityList.get(hostWriterScreenEntityList.size()-1).getData().getComments().size()-1).setCover(String.valueOf(matchEntity.getCover()));
//            }
//        }
        return  this.List2Json(hostWriterScreenEntityList);
    }

    @Override
    public void runWritrScreen(Long[] ids) {
        // 写屏开始，记录操作日志
        matchDao.insertTaskLog("runWritrScreen", "start", "");

        // 检测写屏开关
        List<OrgEntity> orgList = matchDao.getPartion("write");
        if (orgList.size() <= 0 || "OPEN".equals(orgList.get(0).getDictcode()) == false) {
            matchDao.insertTaskLog("runWritrScreen", "abort", "");
            LOGGER.info("-------------------------写屏开关未开启--------------------");
//            return;
        }
       matchDao.updateWriteScreenType("off");
        // 开始写屏
        LOGGER.info("-------------------------写屏定时任务开启--------------------");

        try {
            // 获取已匹配的数据集合
            List<MatchEntity> matchEntitylist = Lists.newArrayList();
            if (ids == null || ids.length == 0) {
                matchEntitylist = matchDao.getMatchLists();
            } else {
                matchEntitylist = matchDao.getChooseMatchLists(ids);
            }
            LOGGER.info("matchEntitylist {}" ,matchEntitylist.size());
            // feng liu
            List<MatchEntity> list1 = Lists.newArrayList();

            List<MatchEntity> list2 = Lists.newArrayList();
            distriList(matchEntitylist,list1,list2);
            Destination destination1 = new ActiveMQQueue(queueName);
            Destination destination2 = new ActiveMQQueue(queueName2);
            Destination destination3 = new ActiveMQQueue(queueName3);
            Destination destination4 = new ActiveMQQueue(queueName4);
            Destination destination5 = new ActiveMQQueue(queueName5);
            Destination destination6 = new ActiveMQQueue(queueName6);
            Destination destination7 = new ActiveMQQueue(queueName7);
            Destination destination8 = new ActiveMQQueue(queueName8);
            LOGGER.info("run thread");
        Thread thread1 = new Thread(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("run thread1");
                if(list1.size()>0){
                    LOGGER.info("list1 size {}",list1.size());
                    sendJson(list1,destination1,destination3,destination5,destination7);
                }

            }
        });

            Thread thread2 = new Thread(new Runnable() {

                @Override
                public void run() {
                    LOGGER.info("run thread2");
                    if(list2.size()>0){
                        LOGGER.info("list2 size {}",list2.size());
                        sendJson(list2,destination2,destination4,destination6,destination8);

                    }
                }
            });

            thread1.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread2.start();




        }catch (Exception e) {
            matchDao.updateWriteScreenType("OPEN");
            LOGGER.info("mqException", e);
            matchDao.insertTaskLog("runWritrScreen", "exception", "");
        } finally {
            LOGGER.info("close connect");
            LOGGER.info(new Date().toString());
        }

        matchDao.updateWriteScreenType("OPEN");
        matchDao.insertTaskLog("runWritrScreen", "end", "");
        LOGGER.info("-------------------------写屏定时任务结束--------------------");
    }

    @Override
    public void runWritrScreen1(Long[] ids) {
        // 写屏开始，记录操作日志
        matchDao.insertTaskLog("runWritrScreen", "start", "");

        // 检测写屏开关
        List<OrgEntity> orgList = matchDao.getPartion("write");

        if (orgList.size() <= 0 || "OPEN".equals(orgList.get(0).getDictcode()) == false) {
            matchDao.insertTaskLog("runWritrScreen", "abort", "");
            LOGGER.info("-------------------------写屏开关未开启--------------------");
            return;
        }

        // 开始写屏
        LOGGER.info("-------------------------写屏定时任务开启--------------------");

        try {
            // 获取已匹配的数据集合
            List<MatchEntity> matchEntitylist = Lists.newArrayList();

                matchEntitylist = matchDao.getChooseMatchLists(ids);

            // 消息队列
            Destination destination1 = new ActiveMQQueue(queueName);
            final String[] vender = {matchEntitylist.get(0).getVenderid()};
            matchEntitylist.forEach((MatchEntity matchEntity) -> {

                try {
                    // 更新状态为8，中间状态
                    this.upDateHostStatus(matchEntity.getMatchno(),"8");

                    // 写屏前数据检查
                    // 获取host数据，判断金额和状态，状态不为0时取消匹配（除208外）
                    // 输出例外报告数据

                    // 根据票单关联号获取 PO单 数据
                    matchEntity.setPoEntityList(matchDao.hostPoList(matchEntity.getMatchno()));
                    // 根据票单关联号获取 索赔 数据
                    matchEntity.setClaimEntityList(matchDao.claimList(matchEntity.getMatchno()));
                    // 根据票单关联号获取 发票 数据
                    List<InvoiceEntity>invoiceList=matchDao.invoiceList(matchEntity.getMatchno());
                    invoiceList.forEach(invoiceEntity -> {
                        if("04".equals(CommonUtil.getFplx(invoiceEntity.getInvoiceCode())) ){
                            invoiceEntity.setInvoiceAmount(invoiceEntity.getDkinvoiceAmount());
                            if(invoiceEntity.getDeductibleTax()==null||invoiceEntity.getDeductibleTax().compareTo(BigDecimal.ZERO)<=0){
                                invoiceEntity.setTaxAmount(new BigDecimal(0));
                            }else{
                                invoiceEntity.setTaxAmount(invoiceEntity.getDeductibleTax());
                            }

                            if(invoiceEntity.getDeductibleTaxRate()==null||invoiceEntity.getDeductibleTaxRate().compareTo(BigDecimal.ZERO)<=0){
                                invoiceEntity.setTaxRate(new BigDecimal(0));
                            }else{
                                invoiceEntity.setTaxRate(invoiceEntity.getDeductibleTaxRate());
                            }
                        }
                    });
                    matchEntity.setInvoiceEntityList(invoiceList);
                     //判断匹配时差异金额和现在计算出的差异金额是否一致，如果不一致生成错误报告
                    Boolean isAmount = checkMatchAmount(matchEntity);
                    if (isAmount) {
                        this.upDateHostStatus(matchEntity.getMatchno(),"10");
                        List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                        list.forEach(invoiceEntity -> {
                            SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                            submitOutstandingReportEntity.setErrcode("001");
                            submitOutstandingReportEntity.setErrdesc("供应商提交匹配关系与写屏时匹配关系不符");
                            submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                            submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                            submitOutstandingReportEntity.setPoNo("");
                            submitOutstandingReportEntity.setTrans("");
                            submitOutstandingReportEntity.setRece("");
                            //增加字段
                            submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                            submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                            submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                            submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                            submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());
                            //
                            submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                            submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                        });
                    }else{
                        List<SubmitOutstandingReportEntity> soreList = this.checkWriteScreen(matchEntity);

                        List<SubmitOutstandingReportEntity> soreDistList = Lists.newArrayList();
                        if (soreList.size() == 0) {
                            LOGGER.info("-------sendQueueBegin-------");

                            LOGGER.info("-------sendQueue1-------");
                            try{
                                producer.sendMessage(destination1, String.valueOf(this.writeScreen(matchEntity)));
                                // 更新状态为9，中间状态
                                this.upDateHostStatus(matchEntity.getMatchno(),"9");
                            }catch (IndexOutOfBoundsException ioobe){
                                // 更新状态为10，fpduokai
                                this.upDateHostStatus(matchEntity.getMatchno(),"10");
                                List<InvoiceEntity> list= matchEntity.getInvoiceEntityList();
                                list.forEach(invoiceEntity -> {
                                    SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();
                                    submitOutstandingReportEntity.setErrcode("202");
                                    submitOutstandingReportEntity.setErrdesc("发票金额多开");
                                    submitOutstandingReportEntity.setPoNo("");
                                    submitOutstandingReportEntity.setTrans("");
                                    submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                                    submitOutstandingReportEntity.setRece("");
                                    submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());

                                    //增加字段

                                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                                    submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                                    submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                                    submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                                    submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                                    //
                                    submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                                    submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                                });
                            }
                            LOGGER.info("-------sendQueueSuccess-------");
                        } else {
                            // 处理例外报告
                            if (matchEntity.getInvoiceEntityList().size() > 0) {
                                for (int i = 0; i < matchEntity.getInvoiceEntityList().size(); i++) {
                                    InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
                                    if("04".equals(CommonUtil.getFplx(invoiceEntity.getInvoiceCode())) ){
                                        invoiceEntity.setInvoiceAmount(invoiceEntity.getDkinvoiceAmount());
                                        if(invoiceEntity.getDeductibleTax()==null||invoiceEntity.getDeductibleTax().compareTo(BigDecimal.ZERO)<=0){
                                            invoiceEntity.setTaxAmount(new BigDecimal(0));
                                        }else{
                                            invoiceEntity.setTaxAmount(invoiceEntity.getDeductibleTax());
                                        }

                                        if(invoiceEntity.getDeductibleTaxRate()==null||invoiceEntity.getDeductibleTaxRate().compareTo(BigDecimal.ZERO)<=0){
                                            invoiceEntity.setTaxRate(new BigDecimal(0));
                                        }else{
                                            invoiceEntity.setTaxRate(invoiceEntity.getDeductibleTaxRate());
                                        }
                                    }
                                    soreDistList.add(
                                            new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), invoiceEntity.getInvoiceNo(), String.valueOf(invoiceEntity.getTotalAmount()), "", matchEntity.getMatchno(), "", "", "", "", "", "", invoiceEntity.getInvoiceDate(),String.valueOf(invoiceEntity.getTaxAmount()),String.valueOf(invoiceEntity.getTaxRate()),invoiceEntity.getInvoiceType()));
                                }
                                for (int j = 0; j < matchEntity.getPoEntityList().size(); j++) {
                                    PoEntity poEntity = matchEntity.getPoEntityList().get(j);
                                    if (j < soreDistList.size()) {
                                        for (int k = 0; k < soreList.size(); k++) {
                                            SubmitOutstandingReportEntity submitOutstandingReportEntity = soreList.get(k);
                                            if (String.valueOf(submitOutstandingReportEntity.getId()).equals(poEntity.getTractionIdSeq())) {
                                                soreDistList.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                                soreDistList.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                                soreDistList.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                                soreDistList.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                                soreDistList.get(j).setErrcode(String.valueOf(submitOutstandingReportEntity.getErrcode()));
                                                soreDistList.get(j).setErrdesc(String.valueOf(submitOutstandingReportEntity.getErrdesc()));
                                            } else {
                                                soreDistList.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                                soreDistList.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                                soreDistList.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                                soreDistList.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                            }
                                        }
                                    } else {
                                        Boolean flag = true;
                                        for (int s = 0; s < soreList.size(); s++) {
                                            SubmitOutstandingReportEntity submitOutstandingReportEntity1 = soreList.get(s);
                                            if (String.valueOf(submitOutstandingReportEntity1.getId()).equals(poEntity.getTractionIdSeq())) {
                                                soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), submitOutstandingReportEntity1.getErrcode(), submitOutstandingReportEntity1.getErrdesc(), ""));
                                                flag = false;
                                            }
                                        }
                                        if (flag) {
                                            soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), "", "", ""));
                                        }
                                    }
                                }
                                for (int v = 0; v < matchEntity.getClaimEntityList().size(); v++) {
                                    ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(v);
                                    if (v < soreDistList.size() - matchEntity.getPoEntityList().size()) {
                                        for (int k = 0; k < soreList.size(); k++) {
                                            SubmitOutstandingReportEntity submitOutstandingReportEntity2 = soreList.get(k);
                                            if (String.valueOf(submitOutstandingReportEntity2.getId()).equals(claimEntity.getTractionIdSeq())) {
                                                soreDistList.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                                soreDistList.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));
                                                soreDistList.get(v).setErrcode(String.valueOf(submitOutstandingReportEntity2.getErrcode()));
                                                soreDistList.get(v).setErrdesc(String.valueOf(submitOutstandingReportEntity2.getErrdesc()));
                                            } else {
                                                soreDistList.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                                soreDistList.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));
                                            }
                                        }
                                    } else {
                                        Boolean flag = true;
                                        for (int s = 0; s < soreList.size(); s++) {
                                            SubmitOutstandingReportEntity submitOutstandingReportEntity3 = soreList.get(s);
                                            if (String.valueOf(submitOutstandingReportEntity3.getId()).equals(claimEntity.getTractionIdSeq())) {
                                                soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", submitOutstandingReportEntity3.getErrcode(), submitOutstandingReportEntity3.getErrdesc(), ""));
                                                flag = false;
                                            }
                                        }
                                        if (flag) {
                                            soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", "", "", ""));
                                        }
                                    }
                                }
                            }
                        }
                        if (soreDistList.size() > 0) {
                            this.insertSubmitOutstandingReport(soreDistList);
                        }
                    }
                }catch(Exception e){
                    try{
                        this.upDateHostStatus(matchEntity.getMatchno(),"999");
//                        this.revertDateHostStatus(matchEntity.getMatchno());
                    }catch (Exception e2){
                        // 等待 10秒
                        try {
                            Thread.sleep(10000);
                            this.upDateHostStatus(matchEntity.getMatchno(),"999");
//                            this.revertDateHostStatus(matchEntity.getMatchno());
                        } catch (Exception e1) {
                            LOGGER.info("hoststatus 状态回滚时锁表",e);
                        }
                    }finally {

                        LOGGER.info("生成json异常",e);
                        e.printStackTrace();
                        LOGGER.info("生成json异常,matchno为：",matchEntity.getMatchno());
                    }

                }
            });
        }catch (Exception e) {
            LOGGER.info("mqException", e);

            matchDao.insertTaskLog("runWritrScreen", "exception", "");
        } finally {
            LOGGER.info("close connect");
            LOGGER.info(new Date().toString());
        }

        matchDao.insertTaskLog("runWritrScreen", "end", "");
        LOGGER.info("-------------------------写屏定时任务结束--------------------");
    }

    @Override
    public List<SubmitOutstandingReportEntity> checkWriteScreen(MatchEntity matchEntity) {




        // 需要进入例外报告的发票数据
        List<SubmitOutstandingReportEntity> soreList=Lists.newArrayList();

        // 获取一个初始化连接
        Connection initConn = null;
		try {
			initConn = DB2Conn.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 获取实际处理使用的连接
		Connection handlerConn = initConn;
        final Boolean[] flag = {true};

//        matchEntity.getInvoiceEntityList().forEach(invoiceEntity -> {
//            try {
//                SubmitOutstandingReportEntity submitOutstandingReportEntity = checkHost(matchEntity, invoiceEntity, handlerConn,flag);
//                if(!"0".equals(submitOutstandingReportEntity.getErrcode())){// 状态不为0，进入例外报告
//                    flag[0] =false;
//                    soreList.add(submitOutstandingReportEntity);
//                    // 状态不为208，取消匹配关系
//                    if("208".equals(submitOutstandingReportEntity.getErrcode()) == false){
//
//                        this.beforeWriteScreenClaimCheck(matchEntity.getMatchno());  // 取消匹配关系
//                    }
//                }
//            }catch(Exception e) {
//                LOGGER.error("处理发票： matchEntity id is " + (matchEntity.getId() == null ? "null" : matchEntity.getId()));
//                e.printStackTrace();
//            }
//
//        });





        // 遍历处理 索赔 数据
        matchEntity.getClaimEntityList().forEach(claimEntity -> {
        	try {
	            SubmitOutstandingReportEntity submitOutstandingReportEntity = checkHost(matchEntity, claimEntity, handlerConn,flag);
	            if(!"0".equals(submitOutstandingReportEntity.getErrcode())){// 状态不为0，进入例外报告
                    flag[0] =false;
	                soreList.add(submitOutstandingReportEntity);
	                // 状态不为208，取消匹配关系
	                if("208".equals(submitOutstandingReportEntity.getErrcode()) == false){

	                    this.beforeWriteScreenClaimCheck(matchEntity.getMatchno());  // 取消匹配关系
	                }
	            }
        	}catch(Exception e) {
        		LOGGER.error("处理索赔： matchEntity id is " + (matchEntity.getId() == null ? "null" : matchEntity.getId()));
        		e.printStackTrace();
        	}
        });

        // 遍历处理 PO单 数据
        matchEntity.getPoEntityList().forEach(poEntity -> {
        	try {
	            SubmitOutstandingReportEntity submitOutstandingReportEntity = checkHost(matchEntity,poEntity, handlerConn,flag);
	            if(!"0".equals(submitOutstandingReportEntity.getErrcode())){  // 状态不为0，进入例外报告

	                soreList.add(submitOutstandingReportEntity);
                    flag[0] =false;
	                // 状态不为208，取消匹配关系
	                if(("208".equals(submitOutstandingReportEntity.getErrcode()) == false) &&("409".equals(submitOutstandingReportEntity.getErrcode()) == false)){

                        this.beforeWriteScreenPoCheck(poEntity,matchEntity.getMatchno());
	                }
	            }
            }catch(Exception e) {
        		LOGGER.error("处理PO： matchEntity id is " + (matchEntity.getId() == null ? "null" : matchEntity.getId()));
        		e.printStackTrace();
            }
        });

        if(null != initConn) {
            DB2Conn.closeConnection(initConn);
        }

        return soreList;
    }



    public  SubmitOutstandingReportEntity checkHost(MatchEntity matchEntity, InvoiceEntity invoiceEntity, Connection conn,Boolean [] flag) {
        // 创建例外报告对象
        SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
        // 初始化 host状态
        String hostStatus = "";  // DB2的状态，
        if (flag[0]) {


            PreparedStatement preparedStmt = null;
            ResultSet resultSet = null;

            try {
                String invoiceNo=invoiceEntity.getInvoiceNo();
                String vendorid=invoiceEntity.getVenderid();
                Date currentTime = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentTime);
                calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf("-1"));
                currentTime = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = formatter.format(currentTime);
                String date1=date2+" 00:00:00";

                // 获取索赔数据的SQL
                String selectSql = "select B.invoice_nbr,B.invoice_date,D.PROCESS_STAT_CODE,B.vendor_nbr,D.process_status_ts from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+date1+"' and B.invoice_nbr = '0000000"+invoiceNo+"' and B.vendor_nbr ='"+vendorid+"'";

                try {
                    preparedStmt = conn.prepareStatement(selectSql);// 预编译
                    resultSet = preparedStmt.executeQuery();// 执行
                } catch (Exception e) {
                    // 循环计数器
                    int count = 0;
                    // 重置是否成功
                    boolean resetResult = false;
                    do {
                        // 每次重试，间隔1秒
                        Thread.sleep(1000);
                        resetResult = resetDB2Resource(conn, preparedStmt, resultSet, selectSql);
                    } while (resetResult == false && count++ < 3);

                    // 如果重置失败，抛出异常
                    if (resetResult == false) {
                        throw e;
                    }
                }

                if (resultSet.next()) {
                    hostStatus=resultSet.getString(3);
                }
            } catch (Exception e) {
                hostStatus = "";
                LOGGER.info("invoice", e);
            } finally {
                if (null != resultSet) {
                    DB2Conn.closeResultSet(resultSet);
                }
                if (null != preparedStmt) {
                    DB2Conn.closeStatement(preparedStmt);
                }
            }

            /***
             * 判断状态是否需要进入例外报告，并准备相关数据
             */
            if (StringUtils.isEmpty(hostStatus)) {  // 取数据时发生异常
                submitOutstandingReportEntity.setErrcode("208");
                submitOutstandingReportEntity.setErrdesc("对比发票状态时数据库连接失败");
                submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                //
                submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
            } else if ("10".equals(hostStatus)) {  // 未匹配
                submitOutstandingReportEntity.setErrcode("503");
                submitOutstandingReportEntity.setErrdesc("Inv Status 栏位值 为Unmatched");
                submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                //
                submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());

            }  else {  // 可以写屏
                submitOutstandingReportEntity.setErrcode("0");
            }

        }else{
            submitOutstandingReportEntity.setErrcode("0");
        }

        return submitOutstandingReportEntity;
    }

    public  SubmitOutstandingReportEntity checkHost(MatchEntity matchEntity, ClaimEntity claimEntity, Connection conn,Boolean [] flag) {
        // 创建例外报告对象
        SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
        // 初始化 host索赔状态和host索赔金额
        String hostStatus = "";  // DB2的状态， 8是冻结状态 1是未结状态 其他是已结状态
        BigDecimal hostCost = new BigDecimal(0);
        if (flag[0]) {


        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try {
            // 获取索赔数据的SQL
            String selectSql = "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='" + claimEntity.getTractionId() + "'  and B.TXN_SEQ_NBR='" + claimEntity.getSeq() + "')  OC ) a where a.rnum =1";

            try {
                preparedStmt = conn.prepareStatement(selectSql);// 预编译
                resultSet = preparedStmt.executeQuery();// 执行
            } catch (Exception e) {
                // 循环计数器
                int count = 0;
                // 重置是否成功
                boolean resetResult = false;
                do {
                    // 每次重试，间隔1秒
                    Thread.sleep(1000);
                    resetResult = resetDB2Resource(conn, preparedStmt, resultSet, selectSql);
                } while (resetResult == false && count++ < 3);

                // 如果重置失败，抛出异常
                if (resetResult == false) {
                    throw e;
                }
            }

            if (resultSet.next()) {
                hostStatus = resultSet.getString(1);// 获取状态
                hostCost = resultSet.getBigDecimal(2);// 获取金额
            }
        } catch (Exception e) {
            hostStatus = "";
            LOGGER.info("claim", e);
        } finally {
            if (null != resultSet) {
                DB2Conn.closeResultSet(resultSet);
            }
            if (null != preparedStmt) {
                DB2Conn.closeStatement(preparedStmt);
            }
        }

        /***
         * 判断状态是否需要进入例外报告，并准备相关数据
         */
        if (StringUtils.isEmpty(hostStatus)) {  // 取数据时发生异常
            submitOutstandingReportEntity.setErrcode("208");
            submitOutstandingReportEntity.setErrdesc("对比索赔状态时数据库连接失败");
            submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
            submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));
            submitOutstandingReportEntity.setTrans(claimEntity.getTractionId());
            submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
            submitOutstandingReportEntity.setRece("");
        } else if ("8".equals(hostStatus)) {  // 冻结
            submitOutstandingReportEntity.setErrcode("204");
            submitOutstandingReportEntity.setErrdesc("货款冻结");
            submitOutstandingReportEntity.setTrans(claimEntity.getClaimno());
            submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
            submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
            submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));
            submitOutstandingReportEntity.setRece("");
        } else if ("1".equals(hostStatus)) {  // 等于1，未结，可能需要写屏
            if ((hostCost.compareTo(new BigDecimal(0).subtract(claimEntity.getClaimAmount())) != 0)) {  // 判断host金额和匹配金额是否相等，相当于索赔金额多开（双方标准），不写屏
                submitOutstandingReportEntity.setErrcode("401");
                submitOutstandingReportEntity.setErrdesc("索赔金额多开");
                submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setTrans(claimEntity.getTractionId());
                submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
                submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
                submitOutstandingReportEntity.setRece("");
            } else {  // 可以写屏
                submitOutstandingReportEntity.setErrcode("0");
            }
        } else {  // 不为空，不是8，不是1，已结
            submitOutstandingReportEntity.setErrcode("203");
            submitOutstandingReportEntity.setErrdesc("索赔已结");
            submitOutstandingReportEntity.setTrans(claimEntity.getClaimno());
            submitOutstandingReportEntity.setVendorNo(claimEntity.getVenderid());
            submitOutstandingReportEntity.setJv(claimEntity.getJvcode());
            submitOutstandingReportEntity.setId(Long.valueOf(claimEntity.getTractionIdSeq()));
            submitOutstandingReportEntity.setRece("");
        }
    }else{
            submitOutstandingReportEntity.setErrcode("0");
        }

        return submitOutstandingReportEntity;
    }

    @Transactional
    public  SubmitOutstandingReportEntity checkHost(MatchEntity matchEntity, PoEntity poEntity, Connection conn,Boolean [] flag){
        SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
        if(flag[0]) {


            PreparedStatement preparedStmt = null;
            ResultSet resultSet = null;

            String payterm = null;

            try {
//            String selectCommentSql = "SELECT t1.comment_text  from cnpurord.po_comment t1 left join cninvmat.financial_txn t2 on t2.purchase_order_id=t1.purchase_order_id  where t2.transaction_id='" + poEntity.getTractionId() + "'  AND t2.txn_seq_nbr='" + poEntity.getSeq() + "' and t1.COMMENT_TYPE_CODE='100'";
                String selectCommentSql = "SELECT T2.COMMENT_TEXT  FROM CNPURORD.PO_NBR_XREF T1,CNPURORD.PO_COMMENT T2,cninvmat.financial_txn T3 WHERE T1.PURCHASE_ORDER_ID = T2.PURCHASE_ORDER_ID AND T1.PARTITION_NBR=T2.PARTITION_NBR AND T3.PO_NBR=t1.P1A_KEY AND t3.transaction_id='" + poEntity.getTractionId() + "' AND t3.txn_seq_nbr='" + poEntity.getSeq() + "' and t2.COMMENT_TYPE_CODE='100'";
                try {
                    preparedStmt = conn.prepareStatement(selectCommentSql);// 预编译
                    resultSet = preparedStmt.executeQuery();// 执行
                } catch (Exception e) {
                    // 循环计数器
                    int count = 0;
                    // 重置是否成功
                    boolean resetResult = false;
                    do {
                        // 每次重试，间隔1秒
                        Thread.sleep(1000);
                        resetResult = resetDB2Resource(conn, preparedStmt, resultSet, selectCommentSql);
                    } while (resetResult == false && count++ < 3);

                    // 如果重置失败，抛出异常
                    if (resetResult == false) {
                        throw e;
                    }
                }

                if (resultSet.next()) {
                    // 获取信息
                    payterm = resultSet.getString(1).trim();
                    LOGGER.info("payterm" + payterm);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("Exception", e);
                submitOutstandingReportEntity.setErrcode("208");
                submitOutstandingReportEntity.setErrdesc("获取DueDate时数据库连接失败！");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setRece("");
                return submitOutstandingReportEntity;
            } finally {
                if (null != resultSet) {
                    DB2Conn.closeResultSet(resultSet);
                }
                if (null != preparedStmt) {
                    DB2Conn.closeStatement(preparedStmt);
                }
            }

            Date postDate = poEntity.getReceiptdate();// 收货日期
            Date dueDate = null;
            try {
                //获取字符串中最后一个字符
                String lastIndex = payterm.substring(payterm.length() - 1, payterm.length());

                // 获取字符串里面的数字，方便转换成日期
                String paytermNumStr = "== " + payterm + " ==";
                payterm = getNumber(payterm);

                // 判断日期
                Integer nextDays = 0;
                if (!StringUtils.isEmpty(payterm)) {
                    nextDays = Integer.valueOf(payterm);
                    LOGGER.info("----------------------------Net:" + nextDays);
                } else {
                    LOGGER.info("----------------------------Net:没有获取到数字");
                    LOGGER.info("订单DueDate问题:" + paytermNumStr + "没有获取到日期");
                    submitOutstandingReportEntity.setErrcode("409");
                    submitOutstandingReportEntity.setErrdesc("订单DueDate问题");
                    submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                    submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                    submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                    submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                    submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                    submitOutstandingReportEntity.setRece("");
                    return submitOutstandingReportEntity;
                }

                // 收货日期
                Calendar postTime = Calendar.getInstance();
                postTime.setTime(postDate);
                //获取收货日
                Integer day = postTime.get(Calendar.DAY_OF_MONTH);
                LOGGER.info("----------------------------postdate:" + day);

                // 当前日期
                Calendar distDate = Calendar.getInstance();

                if ("m".equals(lastIndex)) {
                    LOGGER.info("---------------------------- payterm.toLowerCase().endsWith(\"eom\") is true");
                    if (day > 24) {// 24号以后（不含24号）  ,设置为下个月
                        postTime.add(Calendar.MONTH, 1);
                        distDate.set(postTime.get(Calendar.YEAR), postTime.get(Calendar.MONTH), 24);
                        distDate.add(Calendar.DAY_OF_MONTH, nextDays);
                    } else {// 24号之前（含24号）   ,设置为当前月
                        distDate.set(postTime.get(Calendar.YEAR), postTime.get(Calendar.MONTH), 24);
                        distDate.add(Calendar.DAY_OF_MONTH, nextDays);
                    }
                } else {
                    LOGGER.info("---------------------------- payterm.toLowerCase().endsWith(\"eom\") is false");
                    distDate.set(postTime.get(Calendar.YEAR), postTime.get(Calendar.MONTH), postTime.get(Calendar.DAY_OF_MONTH));
                    distDate.add(Calendar.DAY_OF_MONTH, nextDays);
                }

                dueDate = distDate.getTime();
            } catch (Exception e) {
                LOGGER.info("", e);
                LOGGER.info("订单DueDate问题:" + e.getMessage());
                dueDate = null;
                submitOutstandingReportEntity.setErrcode("409");
                submitOutstandingReportEntity.setErrdesc("订单DueDate问题");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setRece("");
                return submitOutstandingReportEntity;
            }

            if (dueDate != null) {
//                matchDao.updatePoDueDate(dueDate, poEntity.getTractionId() + poEntity.getSeq());
                if(matchEntity.getDueDate()==null){
                    matchEntity.setDueDate(dueDate);
                }else{
                    if(matchEntity.getDueDate().compareTo(dueDate)<0){
                        matchEntity.setDueDate(dueDate);
                    }
                }
            }

            String hostStatus = "";
            BigDecimal hostCost = new BigDecimal(0);
            try {
                String selectSql = "select a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='" + poEntity.getTractionId() + "'  and B.TXN_SEQ_NBR='" + poEntity.getSeq() + "')  OC ) a where a.rnum =1";

                try {
                    preparedStmt = conn.prepareStatement(selectSql);// 预编译
                    resultSet = preparedStmt.executeQuery();// 执行
                } catch (Exception e) {
                    // 循环计数器
                    int count = 0;
                    // 重置是否成功
                    boolean resetResult = false;
                    do {
                        // 每次重试，间隔1秒
                        Thread.sleep(1000);
                        resetResult = resetDB2Resource(conn, preparedStmt, resultSet, selectSql);
                    } while (resetResult == false && count++ < 3);

                    // 如果重置失败，抛出异常
                    if (resetResult == false) {
                        throw e;
                    }
                }

                if (resultSet.next()) {
                    hostStatus = resultSet.getString(1);
                    hostCost = resultSet.getBigDecimal(2);
                }
            } catch (Exception e) {
                hostStatus = "";
                e.printStackTrace();
            } finally {
                if (null != resultSet) {
                    DB2Conn.closeResultSet(resultSet);
                }
                if (null != resultSet) {
                    DB2Conn.closeStatement(preparedStmt);
                }
            }

            if (StringUtils.isEmpty(hostStatus)) {
                submitOutstandingReportEntity.setErrcode("208");
                submitOutstandingReportEntity.setErrdesc("对比订单状态时数据库连接失败!");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setRece("");
            } else if ("8".equals(hostStatus)) {
                submitOutstandingReportEntity.setErrcode("204");
                submitOutstandingReportEntity.setErrdesc("货款冻结");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setRece(poEntity.getReceiptid());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                poEntity.setHoststatus(hostStatus);
                poEntity.setAmountpaid(hostCost);
            } else if ("1".equals(hostStatus) == false) {
                submitOutstandingReportEntity.setErrcode("205");
                submitOutstandingReportEntity.setErrdesc("订单已结");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));
                poEntity.setAmountpaid(hostCost);
                poEntity.setHoststatus(hostStatus);
                submitOutstandingReportEntity.setRece("");
            } else {
                if ((hostCost.compareTo(new BigDecimal(0).subtract(poEntity.getAmountpaid())) != 0)) {
                    submitOutstandingReportEntity.setErrcode("402");
                    submitOutstandingReportEntity.setErrdesc("订单金额多开");
                    submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                    submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                    submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                    submitOutstandingReportEntity.setId(Long.valueOf(poEntity.getTractionIdSeq()));

                    submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                    submitOutstandingReportEntity.setRece("");
                    poEntity.setAmountpaid(hostCost);
                    poEntity.setHoststatus(hostStatus);
                } else {
                    submitOutstandingReportEntity.setErrcode("0");
                }
            }
        }else{
            submitOutstandingReportEntity.setErrcode("0");
        }


        return submitOutstandingReportEntity;
    }
    public Boolean checkMatchAmount(MatchEntity matchEntity){
        Boolean isAmount=false;
        List<HostWriterScreenEntity> hostWriterScreenEntityList=Lists.newArrayList();
        List<PoEntity> poEntityList=Lists.newArrayList();
        List<PoEntity> poEntities=matchEntity.getPoEntityList();
        poEntityList.addAll(poEntities);
        BigDecimal claimTotal=new BigDecimal(0);
        BigDecimal InvoiceTotal=new BigDecimal(0);
        for(int i=0;i<matchEntity.getClaimEntityList().size();i++) {
            ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(i);
            claimTotal = claimTotal.add(claimEntity.getClaimAmount());
        }
        LOGGER.info("索赔金额为:"+claimTotal);
        //step2 遍历发票
        for(int i=0;i<matchEntity.getInvoiceEntityList().size();i++) {
            InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
            InvoiceTotal=InvoiceTotal.add(invoiceEntity.getInvoiceAmount());
        }
            BigDecimal InSubClaim=new BigDecimal(0);
                //发票和索赔总金额 InSubClaim
                InSubClaim=InvoiceTotal.subtract(claimTotal);
                LOGGER.info("发票金额为:"+InSubClaim);
            //如果InSubClaim余额大于0，再借订单
            if((InSubClaim.compareTo(BigDecimal.ZERO))>0) {
                for (int k = 0; k < poEntities.size(); k++) {
                    PoEntity poEntity = poEntities.get(k);
                    if(poEntity.getAmountunpaid().compareTo(new BigDecimal(0))==1){
                        LOGGER.info("未结金额为:"+poEntity.getAmountunpaid());
                    }
                         InSubClaim = InSubClaim.subtract(poEntity.getAmountpaid().subtract(poEntity.getAmountunpaid()));
                    LOGGER.info("已减发票金额为:"+InSubClaim);
                }
            }
        BigDecimal cover=matchEntity.getCover().setScale(2,BigDecimal.ROUND_DOWN).abs();
        BigDecimal insub=InSubClaim.setScale(2,BigDecimal.ROUND_DOWN).abs();
        LOGGER.info("写屏差异金额为:"+insub);
        LOGGER.info("匹配差异金额为:"+cover);
        LOGGER.info("两者差异金额为:"+cover.subtract(insub));
            if(cover.subtract(insub).setScale(2,BigDecimal.ROUND_DOWN).abs().compareTo(new BigDecimal(20))==1){
                isAmount=true;
            }

        return isAmount;
    }
    public  SubmitOutstandingReportEntity checkHost(InvoiceEntity invoiceEntity,Connection conn){
        PreparedStatement st1 = null;
        ResultSet rs1 = null;
        String sql2= "SELECT\n" +
                "\tCOUNT (1)\n" +
                "FROM\n" +
                "\tCNINVMAT.INVOICE B\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\ta.*\n" +
                "\tFROM\n" +
                "\t\t(\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\tOC.*, Row_Number () OVER (\n" +
                "\t\t\t\t\tpartition BY invoice_id\n" +
                "\t\t\t\t\tORDER BY\n" +
                "\t\t\t\t\t\tprocess_status_ts DESC\n" +
                "\t\t\t\t) rnum\n" +
                "\t\t\tFROM\n" +
                "\t\t\t\tCNINVMAT.INVC_PROCESS_LOG OC\n" +
                "\t\t) a\n" +
                "\tWHERE\n" +
                "\t\ta.rnum = 1\n" +
                ") AS D ON D.INVOICE_ID = B.INVOICE_ID\n" +
                "WHERE\n" +
                "\tB.invoice_nbr = ''\n" +
                "AND B.invoice_date = ''\n" +
                "AND B.vendor_nbr = ''\n" +
                "AND D.PROCESS_STAT_CODE IN (0, 1, 10)";
//        String sql2= "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"')  OC ) a where a.rnum =1";

//        String sql2 = "SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"'";
        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();

        try {
            st1=conn.prepareStatement(sql2);
            rs1=st1.executeQuery();
            String hostStatus="0";
            BigDecimal cost=new BigDecimal(0);
            while(rs1.next()){
                try{
                    hostStatus=rs1.getString(1);
                    cost=rs1.getBigDecimal(2);
                }catch (Exception e){
                    hostStatus="";
                    continue;
                }finally {
                    continue;
                }

            }
            if("1".equals(hostStatus)||"0".equals(hostStatus)||"10".equals(hostStatus)){

                submitOutstandingReportEntity.setErrcode("0");

            }
            else {
                submitOutstandingReportEntity.setErrcode("402");
                submitOutstandingReportEntity.setErrdesc("发票已匹配");
                submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submitOutstandingReportEntity;
    }
    public List<PoEntity> setList(ResultSet rs) throws Exception {
        Integer count=0;
        List<PoEntity> list= Lists.newArrayList();
        while(rs.next()){

//            String pocode = rs.getString(1);


//            String poType = rs.getString(2);
            String venderid = rs.getString(3);
            int length=venderid.length();
            if(6-length>0){
                for(int i=0;i<6-length;i++){
                    venderid="0"+venderid;
                }
            }
//            String receipt=rs.getString(4);
//            BigDecimal amount = new BigDecimal(0).subtract(rs.getBigDecimal(5));
//            java.sql.Date postDate = rs.getDate(6);
//            BigDecimal taxRate = rs.getBigDecimal(7);
//            String jvcode = rs.getString(8);
            String vendername;
            try {
                vendername= rs.getString(9);
            }catch (Exception e){
                vendername="4420";
            }
//            String tractionNo=rs.getString(10);
//            String invoiceId=rs.getString(11);
//            java.sql.Date tractionDate=rs.getDate(12);
            String tractionid=rs.getString(13);
//            String hoststatus=rs.getString(14);
            java.sql.Date dueDate=rs.getDate(15);
            String seq=rs.getString(16);
//            String dept=rs.getString(17);
//            String storeNbr=rs.getString(18);
            String tractionIdSeq=tractionid+seq;
            PoEntity po= new PoEntity(rs.getString(1),venderid,rs.getString(4),new BigDecimal(0).subtract(rs.getBigDecimal(5)),rs.getString(2), rs.getDate(6),rs.getBigDecimal(7),rs.getString(14),rs.getString(11),rs.getString(10),rs.getDate(12),rs.getString(8),vendername,tractionid,rs.getDate(15),rs.getString(17),seq,tractionIdSeq);
            po.setStoreNbr(rs.getString(18));
//            po.setDueDate(dueDate);
            list.add(po);
//            count++;
//            if(count==100){
//                this.getHostData(list,"4","u");
//                count=0;
//                list.clear();
//            }

        }
        return list;
    }
    public List<PoEntity> setListPoType1(ResultSet rs) throws Exception {
        List<PoEntity> list= Lists.newArrayList();
        Integer count=0;
        while(rs.next()){


            String venderid = rs.getString(3);
            int length=venderid.length();
            if(6-length>0){
                for(int i=0;i<6-length;i++){
                    venderid="0"+venderid;
                }
            }

            String vendername;
            try {
                vendername= rs.getString(9);
            }catch (Exception e){
                vendername="4420";
            }



            String tractionid=rs.getString(13);



            String seq=rs.getString(16);

            String tractionIdSeq=tractionid+seq;
            PoEntity po= new PoEntity(rs.getString(1),venderid,rs.getString(4),new BigDecimal(0).subtract(rs.getBigDecimal(5)),rs.getString(2), rs.getDate(6),rs.getBigDecimal(7),rs.getString(14),rs.getString(11),rs.getString(10),rs.getDate(12),rs.getString(8),vendername,tractionid,rs.getDate(15),rs.getString(17),seq,tractionIdSeq);
            po.setStoreNbr(rs.getString(18));

            list.add(po);

        }
        return list;
    }


    public void getHostData(List<PoEntity> list,String type,String hostStatus,int i) throws Exception {
        try {
            if("1".equals(type)||"4".equals(type)||"2".equals(type)){

                if("u".equals(hostStatus)){
                    //清空临时表
                    Boolean deletCount=true;
                    try{
                        this.deletePoCopy();
                    }catch (Exception e){
                        e.printStackTrace();
                        deletCount=false;
                        matchDao.insertTaskLog("connHostPo"+type,"deletePoCopyException","");
                        this.deletePoCopy();
                    }
                    if(deletCount){
                        try{
                            this.insertPoListCopy(list);
                        }catch (Exception e){
                            e.printStackTrace();
                            matchDao.insertTaskLog("connHostPo"+type,"insertPoListCopyException","");

                        }

                        matchDao.transferProcPo();
                        matchDao.adjustment();
                    }



                }

            }else if("3".equals(type)||"5".equals(type)) {

                if ("u".equals(hostStatus)) {
                    Boolean deletCount=true;
                    try{
                        this.deleteClaimCopy();
                    }catch (Exception e){
                        e.printStackTrace();
                        deletCount=false;
                        matchDao.insertTaskLog("connHostClaimType"+type,"deleteClaimCopyException","");
                        this.deleteClaimCopy();
                    }

                    if(deletCount) {


                        //将数据插入临时表
                        try {
                            this.insertClaimListCopy(list);
                        } catch (Exception e) {
                            matchDao.insertTaskLog("connHostClaimType" + type, "insertClaimListCopyException", "");
                            e.printStackTrace();
                        }
                        matchDao.transferProcClaim();
                    }


                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            if("3".equals(type) ||"2".equals(type)){
                matchDao.insertTaskLog(type,String.valueOf(i),"");
                getHostData(list,type,hostStatus,i);
            }else{
                throw new Exception();
            }



        }

    }






    @Transactional
    public Boolean matchDelete(String matchno) {

        try{
            final Boolean flag=detailsDao.deleMatch(matchno)>0;
            if(flag){
                detailsDao.cancelClaim(matchno);
                detailsDao.cancelInvoice(matchno);
                List<PoEntity> list=detailsDao.getPoJiLu(matchno);

                for(int k=0;k<list.size();k++){
                    PoEntity poEntity=list.get(k);


                    detailsDao.deletePo(poEntity.getId(),poEntity.getChangeAmount(),"6");

                }
            }else {
                return flag;
            }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }
        return true;
    }

    @Transactional
    public String matchCancel(String matchno) {
        String msg="取消匹配成功！";
        try{
            final Boolean flag=detailsDao.cancelMatch(matchno)>0;
            if(flag){
                detailsDao.cancelClaim(matchno);
                detailsDao.cancelInvoice(matchno);
                List<PoEntity> list=detailsDao.getPoJiLu(matchno);
                BigDecimal changeTotal=new BigDecimal(0);
                for(int k=0;k<list.size();k++){
                    PoEntity poEntity=list.get(k);
                    detailsDao.cancelPo(poEntity.getId(),poEntity.getChangeAmount(),"6");

//
                }
            }else {
                msg="该条匹配无法取消！";
            }
        }catch (Exception e){
            LOGGER.info("取消匹配 {}",e);
            throw new RuntimeException();
        }
        return msg;
    }
    public  JSONArray List2Json(List<HostWriterScreenEntity> list){
        JSONArray json = JSONArray.fromObject(list);
        LOGGER.info("json {}",json);
        //插入请求报文至t_dx_robot_message
            JSONObject object=json.getJSONObject(0);
            String messageId=object.getString("id");
            String requestMessage=json.toString();
            String status="1";
            RobotMessageEntity rm=new RobotMessageEntity();
            rm.setMessageId(messageId);
            rm.setRequestMessage(requestMessage);
            rm.setStatus(status);
            matchDao.insertRobotMessage(rm);
        return json;
    }

    @Transactional
    @Override
    public List<InvoicesEntity> getInvoice(String venderid,String invoice_no,String invoice_amount){
        List<InvoicesEntity> lists=matchDao.getInvoice(venderid,invoice_no,invoice_amount);
        return  lists;
    }

    @Transactional
    @Override
    public List<MatchEntity> getMatch(String matchno){
        return matchDao.getMatch(matchno);
    }

    @Transactional
    @Override
    public Integer updateMatchHostStatus(String host_status,String id){
        return matchDao.updateMatchHostStatus(host_status,id);
    }

    public static String getNumber(String payterm) {
        String str2 = "";
        if (payterm != null && !"".equals(payterm)) {
            for (int i = 0; i < payterm.length(); i++) {
                if (payterm.charAt(i) >= 48 && payterm.charAt(i) <= 57) {
                    str2 += payterm.charAt(i);
                }

            }
        }
        return str2;
    }


    @Override
    public Integer upDatePoList(List<PoEntity> list, Integer i){

        List<PoEntity> poEntityList1=Lists.newArrayList();
        Integer s=0;
        try{


            for(;i<list.size();i++){
                if(s==100){
                    //批量更新订单数据
                    Integer count=this.updatePoListTraction(poEntityList1);
                    if(count>0){
                        poEntityList1.clear();
                        s=0;
                    }
                }   //批量更新订单数据
                poEntityList1.add(list.get(i));
                s++;

            }
        }catch (Exception e){
            if(i<list.size()){
                upDatePoList(list, i);
            }
        }
        try{
            if(poEntityList1.size()>0){
                this.updatePoListTraction(poEntityList1);
            }
        }catch (Exception e){
            this.updatePoListTraction(poEntityList1);
        }


        return  i;

    }

    @Override
    @Transactional
    public Integer upDatePoListMatched(List<PoEntity> list, Integer i){
        List<PoEntity> poEntityList1=Lists.newArrayList();
        Integer s=0;
        try{


            for(;i<list.size();i++){
                if(s==100){
                    //批量更新订单数据
                    Integer count=this.hostUpdatePoStatusTraction(poEntityList1);
                    if(count>0){
                        poEntityList1.clear();
                        s=0;
                    }
                }   //批量更新订单数据
                poEntityList1.add(list.get(i));
                s++;

            }
        }catch (Exception e){
            if(i<list.size()){
                upDatePoListMatched(list, i);
            }
        }
        try{
            if(poEntityList1.size()>0){
                this.hostUpdatePoStatusTraction(poEntityList1);
            }
        }catch (Exception e){
            this.hostUpdatePoStatusTraction(poEntityList1);
        }
        return  i;
    }


    @Override
    @Transactional
    public Integer upDateClaimList(List<ClaimEntity> list, Integer i){
        List<ClaimEntity> claimEntities=Lists.newArrayList();
        Integer s=0;
        try{


            for(;i<list.size();i++){
                if(s==100){
                    //批量更新订单数据
                    Integer count=this.updateClaimList(claimEntities);
                    if(count>0){
                        claimEntities.clear();
                        s=0;
                    }
                }   //批量更新订单数据
                claimEntities.add(list.get(i));
                s++;

            }
        }catch (Exception e){
            if(i<list.size()){
                upDateClaimList(list, i);
            }
        }
        try{
            if(claimEntities.size()>0){
                this.updateClaimList(claimEntities);
            }
        }catch (Exception e){
            this.updateClaimList(claimEntities);
        }
        return  i;
    }


    @Override
    public  void  test() {
      matchDao.adjustment();
    }
    @Transactional
    public Integer insertPoListTraction( List<PoEntity> list){
        return matchDao.insertPoList(list);
    }

    @Transactional
    public Integer insertPoListCopy1( List<PoEntity> list){
        return matchDao.insertPoListCopy(list);
    }


    @Transactional
    public Integer updatePoListTraction( List<PoEntity> list){
        return matchDao.hostUpdatePoTraction(list);
    }

    @Transactional
    public Integer hostUpdatePoStatusTraction( List<PoEntity> list){
        return matchDao.hostUpdatePoStatusTraction(list);
    }

    @Transactional
    public Integer updateClaimList( List<ClaimEntity> list){
        return matchDao.updateClaimList(list);
    }

    @Transactional
    public Integer insertClaimListTraction( List<ClaimEntity> list){
        return matchDao.insertClaimList(list);
    }

    @Transactional
    public Integer insertClaimCopyList( List<PoEntity> list){
        return matchDao.insertClaimCopyList(list);
    }

    @Transactional
    public Boolean ClaimListTraction(PoEntity poEntity){
        Boolean f1 = matchDao.ifAddClaimExist(poEntity) > 0;

        //f2:是否插入成功
        Boolean f2 = false;
        if (!f1) {
            f2 = matchDao.insertClaimFather(poEntity) > 0;
        }else{
            f2=matchDao.hostUpdateClaim(poEntity)>0;
        }
        return f2;
    }

    @Transactional
    public  Boolean hostDeleteInvoice(String invoiceNo, String hostStatus, String invoiceDate,String vendor, Date hostDate){
        if(hostStatus.equals("13")){
            String matchno= matchDao.getInvoiceMatchno(invoiceNo,hostStatus,invoiceDate,vendor);

            if(!StringUtils.isEmpty(matchno)){
                Boolean in=matchDao.getMatchHostStatus(matchno)>0;
                if(in){
                    this.matchDelete(matchno);
                }

            }
        }
        Boolean flag= matchDao.upDateInvoiceHostStatus(invoiceNo,hostStatus,invoiceDate,vendor,hostDate)>=0;

        return flag;
    }


    @Transactional
    public void insertSubmitOutstandingReport(List<SubmitOutstandingReportEntity> list){
        list.forEach(en -> {
            submitOutstandingReportDao.insertSubmitOutstandingReport(en);
        });
    }

    @Transactional
    Integer upDateHostStatus(String matchno,String hostStatus){
        Integer count=matchDao.upDateHostStatus(matchno,hostStatus);
        return count;
    }

    @Transactional
    Integer revertDateHostStatus(String matchno){
        Integer count=matchDao.revertDateHostStatus(matchno);
        return count;
    }

    @Transactional
    public void beforeWriteScreenPoCheck(PoEntity poEntity,String matchno){
        this.matchCancel(matchno);
        matchDao.cancelInvoiceMatch(matchno);
//        matchDao.hostUpdatePoAmount(poEntity);
    }

    @Transactional
    public void beforeWriteScreenClaimCheck(String matchno){
        this.matchCancel(matchno);
        matchDao.cancelInvoiceMatch(matchno);
    }

    @Transactional
    Integer insertClaimDetail(ClaimDetailEntity claimDetailEntity){
        return matchDao.insertClaimDetail(claimDetailEntity);
    }

    @Transactional
    Integer deletePoCopy(){
        return matchDao.deletePoCopy();
    }

    @Transactional
    Integer deleteClaimCopy(){
        return matchDao.deleteClaimCopy();
    }


    public Integer insertClaimListCopy(List<PoEntity> list) {
        List<PoEntity> claimList=Lists.newArrayList();
        Integer s=0;
        for(Integer c=0;c<list.size();c++){
            if(s==100){
                //批量插入订单数据
                Integer count=1;
                try {
                    count=this.insertClaimCopyList(claimList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(count>0){
                    claimList.clear();
                    s=0;
                }

            }   //批量插入索赔数据
            claimList.add(list.get(c));
            s++;

        }
        if(claimList.size()>0){
            try {
                return this.insertClaimCopyList(claimList);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }

    }

    public Integer insertClaimList(List<ClaimEntity> list) {
        List<ClaimEntity> claimEntityList1=Lists.newArrayList();
        Integer s=0;

        for (Integer c = 0; c < list.size(); c++) {
            if (s == 100) {
                //批量插入订单数据
                Integer count=1;
                try {
                    count = this.insertClaimListTraction(claimEntityList1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (count > 0) {
                    claimEntityList1.clear();
                    s = 0;
                }
            }   //批量插入订单数据
            claimEntityList1.add(list.get(c));
            s++;

        }

        if(claimEntityList1.size()>0){
            try {
                return this.insertClaimListTraction(claimEntityList1);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }

        }else{
            return 0;
        }
    }
    
    public boolean resetDB2Resource(Connection conn, PreparedStatement preparedStmt, ResultSet resultSet, String excuteSql) {
    	try {
    		// 释放当前资源
    		if(null != resultSet) {
                DB2Conn.closeResultSet(resultSet);
            }
    		if(null != preparedStmt) {
                DB2Conn.closeStatement(preparedStmt);
            }
    		if(null != conn) {
                DB2Conn.closeConnection(conn);
            }
    		
    		// 重新申请资源
    		conn = DB2Conn.getConnection();
    		preparedStmt = conn.prepareStatement(excuteSql);
    		resultSet = preparedStmt.executeQuery();
    		
    		return true;
    	}catch(Exception e) {
    		return false;
    	}
    }

    private  void distriList( List<MatchEntity> list1, List<MatchEntity> list2,List<MatchEntity> list3){

        if(list1.size()>=2){
            int cov=list1.size()/2;

            for(int vis=0;vis<list1.size();vis++){
                if(vis<cov){
                    list2.add(list1.get(vis));
                }else{
                    list3.add(list1.get(vis));
                }
            }
        }
    }


    private void sendJson(List<MatchEntity> matchEntitylist,Destination destination1,Destination destination2,Destination destination3,Destination destination4){
        final String[] vender = {matchEntitylist.get(0).getVenderid()};
        int [] ty=new int[]{1};
        matchEntitylist.forEach((MatchEntity matchEntity) -> {

            try {



                // 更新状态为8，中间状态
                this.upDateHostStatus(matchEntity.getMatchno(), "8");
                // 根据票单关联号获取 PO单 数据
                matchEntity.setPoEntityList(matchDao.hostPoList(matchEntity.getMatchno()));
                // 根据票单关联号获取 索赔 数据
                matchEntity.setClaimEntityList(matchDao.claimList(matchEntity.getMatchno()));
                // 根据票单关联号获取 发票 数据
                List<InvoiceEntity>invoiceList=matchDao.invoiceList(matchEntity.getMatchno());
                invoiceList.forEach(invoiceEntity -> {
                    if("04".equals(CommonUtil.getFplx(invoiceEntity.getInvoiceCode())) ){
                        invoiceEntity.setInvoiceAmount(invoiceEntity.getDkinvoiceAmount());
                        if(invoiceEntity.getDeductibleTax()==null||invoiceEntity.getDeductibleTax().compareTo(BigDecimal.ZERO)<=0){
                            invoiceEntity.setTaxAmount(new BigDecimal(0));
                        }else{
                            invoiceEntity.setTaxAmount(invoiceEntity.getDeductibleTax());
                        }

                        if(invoiceEntity.getDeductibleTaxRate()==null||invoiceEntity.getDeductibleTaxRate().compareTo(BigDecimal.ZERO)<=0){
                            invoiceEntity.setTaxRate(new BigDecimal(0));
                        }else{
                            invoiceEntity.setTaxRate(invoiceEntity.getDeductibleTaxRate());
                        }
                    }
                });
                matchEntity.setInvoiceEntityList(invoiceList);
                int sum=matchEntity.getClaimEntityList().size()+matchEntity.getPoEntityList().size();
                if(sum<500) {
                    //判断匹配时差异金额和现在计算出的差异金额是否一致，如果不一致生成错误报告
                    Boolean isAmount = checkMatchAmount(matchEntity);
                    if (isAmount) {
                        this.upDateHostStatus(matchEntity.getMatchno(),"10");
                        List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                        list.forEach(invoiceEntity -> {
                            SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                            submitOutstandingReportEntity.setErrcode("001");
                            submitOutstandingReportEntity.setErrdesc("供应商提交匹配关系与写屏时匹配关系不符");
                            submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                            submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                            submitOutstandingReportEntity.setPoNo("");
                            submitOutstandingReportEntity.setTrans("");
                            submitOutstandingReportEntity.setRece("");
                            //增加字段
                            submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                            submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                            submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                            submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                            submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());
                            //
                            submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                            submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                        });
                    } else {
                    // 写屏前数据检查
                    // 获取host数据，判断金额和状态，状态不为0时取消匹配（除208外）
                    // 输出例外报告数
                    List<SubmitOutstandingReportEntity> soreList = this.checkWriteScreen(matchEntity);
                    List<SubmitOutstandingReportEntity> soreDistList = Lists.newArrayList();
                    if (soreList.size() == 0) {
                        LOGGER.info("-------sendQueueBegin-------");
                        if (vender[0].equals(matchEntity.getVenderid())) {

                        } else {
                            if (ty[0] < 4) {
                                ty[0] = ty[0] + 1;

                            } else {
                                ty[0] = 1;
                            }
                            vender[0] = matchEntity.getVenderid();
                        }
                        if (ty[0] == 1) {
                            LOGGER.info("-------sendQueue1-------");
                            try {
                                producer.sendMessage(destination1, String.valueOf(this.writeScreen(matchEntity)));
                                // 更新状态为9，中间状态
                                this.upDateHostStatus(matchEntity.getMatchno(), "9");
                            } catch (IndexOutOfBoundsException ioobe) {
                                // 更新状态为10，fpduokai
                                ioobe.printStackTrace();
                                this.upDateHostStatus(matchEntity.getMatchno(), "10");
                                List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                                list.forEach(invoiceEntity -> {
                                    SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                                    submitOutstandingReportEntity.setErrcode("001");
                                    submitOutstandingReportEntity.setErrdesc("发票金额多开");
                                    submitOutstandingReportEntity.setPoNo("");
                                    submitOutstandingReportEntity.setTrans("");
                                    submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                                    submitOutstandingReportEntity.setRece("");
                                    submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                                    //增加字段
                                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                                    submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                                    submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                                    submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                                    submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());
                                    //
                                    submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                                    submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
                                });
                            }


                        } else if (ty[0] == 2) {
                            LOGGER.info("-------sendQueue2-------");
                            try {
                                producer.sendMessage(destination2, String.valueOf(this.writeScreen(matchEntity)));
                                // 更新状态为9，中间状态
                                this.upDateHostStatus(matchEntity.getMatchno(), "9");
                            } catch (IndexOutOfBoundsException ioobe) {
                                // 更新状态为10，fpduokai
                                this.upDateHostStatus(matchEntity.getMatchno(), "10");
                                List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                                list.forEach(invoiceEntity -> {
                                    SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                                    submitOutstandingReportEntity.setErrcode("001");
                                    submitOutstandingReportEntity.setErrdesc("发票金额多开");
                                    submitOutstandingReportEntity.setPoNo("");
                                    submitOutstandingReportEntity.setTrans("");
                                    submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                                    submitOutstandingReportEntity.setRece("");
                                    submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());

                                    //增加字段

                                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                                    submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                                    submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                                    submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                                    submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                                    //
                                    submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                                    submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                                });

                            }

                        } else if (ty[0] == 3) {
                            LOGGER.info("-------sendQueue3-------");
                            try {
                                producer.sendMessage(destination3, String.valueOf(this.writeScreen(matchEntity)));
                                // 更新状态为9，中间状态
                                this.upDateHostStatus(matchEntity.getMatchno(), "9");
                            } catch (IndexOutOfBoundsException ioobe) {
                                // 更新状态为10，fpduokai
                                this.upDateHostStatus(matchEntity.getMatchno(), "10");
                                List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                                list.forEach(invoiceEntity -> {
                                    SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                                    submitOutstandingReportEntity.setErrcode("001");
                                    submitOutstandingReportEntity.setErrdesc("发票金额多开");
                                    submitOutstandingReportEntity.setPoNo("");
                                    submitOutstandingReportEntity.setTrans("");
                                    submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                                    submitOutstandingReportEntity.setRece("");
                                    submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());

                                    //增加字段

                                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                                    submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                                    submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                                    submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                                    submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                                    //
                                    submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                                    submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                                });

                            }
                        } else if (ty[0] == 4) {
                            LOGGER.info("-------sendQueue4-------");
                            try {
                                producer.sendMessage(destination4, String.valueOf(this.writeScreen(matchEntity)));
                                // 更新状态为9，中间状态
                                this.upDateHostStatus(matchEntity.getMatchno(), "9");
                            } catch (IndexOutOfBoundsException ioobe) {
                                // 更新状态为10，fpduokai
                                this.upDateHostStatus(matchEntity.getMatchno(), "10");
                                List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                                list.forEach(invoiceEntity -> {
                                    SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                                    submitOutstandingReportEntity.setErrcode("001");
                                    submitOutstandingReportEntity.setErrdesc("发票金额多开");
                                    submitOutstandingReportEntity.setPoNo("");
                                    submitOutstandingReportEntity.setTrans("");
                                    submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                                    submitOutstandingReportEntity.setRece("");
                                    submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());

                                    //增加字段

                                    submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                                    submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                                    submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                                    submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                                    submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                                    //
                                    submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                                    submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                                });

                            }
                        }
                        LOGGER.info("-------sendQueueSuccess-------");
                    } else {
                        // 处理例外报告
                        LOGGER.info("处理例外报告");
                        if (matchEntity.getInvoiceEntityList().size() > 0) {
                            for (int i = 0; i < matchEntity.getInvoiceEntityList().size(); i++) {
                                InvoiceEntity invoiceEntity = matchEntity.getInvoiceEntityList().get(i);
                                if ("04".equals(CommonUtil.getFplx(invoiceEntity.getInvoiceCode()))) {
                                    invoiceEntity.setInvoiceAmount(invoiceEntity.getDkinvoiceAmount());
                                    if (invoiceEntity.getDeductibleTax() == null || invoiceEntity.getDeductibleTax().compareTo(BigDecimal.ZERO) <= 0) {
                                        invoiceEntity.setTaxAmount(new BigDecimal(0));
                                    } else {
                                        invoiceEntity.setTaxAmount(invoiceEntity.getDeductibleTax());
                                    }

                                    if (invoiceEntity.getDeductibleTaxRate() == null || invoiceEntity.getDeductibleTaxRate().compareTo(BigDecimal.ZERO) <= 0) {
                                        invoiceEntity.setTaxRate(new BigDecimal(0));
                                    } else {
                                        invoiceEntity.setTaxRate(invoiceEntity.getDeductibleTaxRate());
                                    }
                                }
                                soreDistList.add(
                                        new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), invoiceEntity.getInvoiceNo(), String.valueOf(invoiceEntity.getTotalAmount()), "", matchEntity.getMatchno(), "", "", "", "", "", "", invoiceEntity.getInvoiceDate(), String.valueOf(invoiceEntity.getTaxAmount()), String.valueOf(invoiceEntity.getTaxRate()), invoiceEntity.getInvoiceType()));
                            }
                            for (int j = 0; j < matchEntity.getPoEntityList().size(); j++) {
                                PoEntity poEntity = matchEntity.getPoEntityList().get(j);
                                if (j < soreDistList.size()) {
                                    for (int k = 0; k < soreList.size(); k++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity = soreList.get(k);
                                        if (String.valueOf(submitOutstandingReportEntity.getId()).equals(poEntity.getTractionIdSeq())) {
                                            soreDistList.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                            soreDistList.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                            soreDistList.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                            soreDistList.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                            soreDistList.get(j).setErrcode(String.valueOf(submitOutstandingReportEntity.getErrcode()));
                                            soreDistList.get(j).setErrdesc(String.valueOf(submitOutstandingReportEntity.getErrdesc()));
                                        } else {
                                            soreDistList.get(j).setWmCost(String.valueOf(poEntity.getAmountpaid()));
                                            soreDistList.get(j).setPoNo(String.valueOf(poEntity.getPocode()));
                                            soreDistList.get(j).setTrans(String.valueOf(poEntity.getTractionNbr()));
                                            soreDistList.get(j).setRece(String.valueOf(poEntity.getReceiptid()));
                                        }
                                    }
                                } else {
                                    Boolean flag = true;
                                    for (int s = 0; s < soreList.size(); s++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity1 = soreList.get(s);
                                        if (String.valueOf(submitOutstandingReportEntity1.getId()).equals(poEntity.getTractionIdSeq())) {
                                            soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), submitOutstandingReportEntity1.getErrcode(), submitOutstandingReportEntity1.getErrdesc(), ""));
                                            flag = false;
                                        }
                                    }
                                    if (flag) {
                                        soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(poEntity.getAmountpaid()), matchEntity.getMatchno(), String.valueOf(poEntity.getPocode()), String.valueOf(poEntity.getTractionNbr()), String.valueOf(poEntity.getReceiptid()), "", "", ""));
                                    }
                                }
                            }
                            for (int v = 0; v < matchEntity.getClaimEntityList().size(); v++) {
                                ClaimEntity claimEntity = matchEntity.getClaimEntityList().get(v);
                                if (v < soreDistList.size() - matchEntity.getPoEntityList().size()) {
                                    for (int k = 0; k < soreList.size(); k++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity2 = soreList.get(k);
                                        if (String.valueOf(submitOutstandingReportEntity2.getId()).equals(claimEntity.getTractionIdSeq())) {
                                            soreDistList.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                            soreDistList.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));
                                            soreDistList.get(v).setErrcode(String.valueOf(submitOutstandingReportEntity2.getErrcode()));
                                            soreDistList.get(v).setErrdesc(String.valueOf(submitOutstandingReportEntity2.getErrdesc()));
                                        } else {
                                            soreDistList.get(v).setWmCost(String.valueOf(claimEntity.getClaimAmount()));
                                            soreDistList.get(v).setTrans(String.valueOf(claimEntity.getClaimno()));
                                        }
                                    }
                                } else {
                                    Boolean flag = true;
                                    for (int s = 0; s < soreList.size(); s++) {
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity3 = soreList.get(s);
                                        if (String.valueOf(submitOutstandingReportEntity3.getId()).equals(claimEntity.getTractionIdSeq())) {
                                            soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", submitOutstandingReportEntity3.getErrcode(), submitOutstandingReportEntity3.getErrdesc(), ""));
                                            flag = false;
                                        }
                                    }
                                    if (flag) {
                                        soreDistList.add(new SubmitOutstandingReportEntity("", "", soreList.get(0).getJv(), soreList.get(0).getVendorNo(), "", "", String.valueOf(claimEntity.getClaimAmount()), matchEntity.getMatchno(), "", String.valueOf(claimEntity.getClaimno()), "", "", "", ""));
                                    }
                                }
                            }
                        }
                    }
                    if (soreDistList.size() > 0) {
                        this.insertSubmitOutstandingReport(soreDistList);
                    }
                }
                }else{
                    // 更新状态为10，fpduokai
                    this.upDateHostStatus(matchEntity.getMatchno(), "12");
                    List<InvoiceEntity> list = matchEntity.getInvoiceEntityList();
                    list.forEach(invoiceEntity -> {
                        SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                        submitOutstandingReportEntity.setErrcode("206");
                        submitOutstandingReportEntity.setErrdesc("该发票所结订单和索赔总和超过500条");


                        submitOutstandingReportEntity.setJv(invoiceEntity.getJvcode());
                        submitOutstandingReportEntity.setVendorNo(invoiceEntity.getVenderid());
                        submitOutstandingReportEntity.setPoNo("");
                        submitOutstandingReportEntity.setTrans("");
                        submitOutstandingReportEntity.setRece("");


                        //增加字段

                        submitOutstandingReportEntity.setInvNo(invoiceEntity.getInvoiceNo());
                        submitOutstandingReportEntity.setInvoiceCost(String.valueOf(invoiceEntity.getTotalAmount()));
                        submitOutstandingReportEntity.setTaxAmount(String.valueOf(invoiceEntity.getTaxAmount()));
                        submitOutstandingReportEntity.setTaxRate(String.valueOf(invoiceEntity.getTaxRate()));
                        submitOutstandingReportEntity.setTaxType(invoiceEntity.getInvoiceType());


                        //
                        submitOutstandingReportEntity.setBatchId(matchEntity.getMatchno());
                        submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);

                    });
                }
            }catch(Exception e){
                try{
                    this.revertDateHostStatus(matchEntity.getMatchno());
                }catch (Exception e2){
                    // 等待 10秒
                    try {
                        Thread.sleep(10000);
                        this.revertDateHostStatus(matchEntity.getMatchno());
                    } catch (Exception e1) {
                        LOGGER.info("hoststatus 状态回滚时锁表",e);
                    }
                }finally {

                    LOGGER.info("生成json异常",e);
                    e.printStackTrace();
                    LOGGER.info("生成json异常,matchno为：",matchEntity.getMatchno());
                }

            }
        });
    }
    @Override
    public int updateIsDel(String isdel,String matchno){
        return matchDao.updateIsDel(isdel,matchno);
    }
 }
