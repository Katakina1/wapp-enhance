package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.common.utils.DB2Conn;
import com.xforceplus.wapp.modules.job.service.ConsumerServer;
import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.posuopei.service.ReturnScreenService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerServerImpl implements ConsumerServer{
	private static Logger logger = Logger.getLogger(ConsumerServerImpl.class);
    @Autowired
    private MatchService matchService;
    @Autowired
    private DetailsService detailsService;

    @Autowired
    private ReturnScreenService returnScreenService;
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;
    
    
    
    
    // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
//    @JmsListener(destination = "${activemq.queueResponse}")
    public void receiveQueue(String text) {
    	logger.info("text值:----------------------"+text);
      //  String arrayStr="[{\"id\":\"1248489390\",\"code\":\"0\",\"message\":\"SUCCESS\",\"data\":{\"DD\":\"01\",\"MM\":\"11\",\"YY\":\"2018\",\"poNbr\":\"4700275614\",\"receiver\":\"0000994752\",\"taxTotal\":\"0.0000\",\"invTotal\":\"450.0000\",\"jv\":\"WI\",\"vender\":\"888888\",\"DD1\":\"01\",\"MM1\":\"12\",\"YY1\":\"2018\",\"inv\":\"01101319\",\"invPreTaxAmt\":\"11.0000\",\"taxRate\":\"0.0000\",\"ifCut\":\"1\",\"taxTypeZ\":\" \",\"payCode\":\"0001\",\"taxType\":\"X\",\"transaction\":\"000004700275614\"},\"screen_name\":\"CICCNMP_FAPI\"},{\"id\":\"1248489391\",\"code\":\"A0010\",\"message\":\"Com Object Exception. [Cause: A command to wait for an event timed out]\",\"data\":{\"DD\":\"01\",\"MM\":\"11\",\"YY\":\"2018\",\"poNbr\":\"4700275614\",\"receiver\":\"0000994752\",\"taxTotal\":\"0.0000\",\"invTotal\":\"1210.0000\",\"jv\":\"WI\",\"vender\":\"077086\",\"DD1\":\"01\",\"MM1\":\"12\",\"YY1\":\"2018\",\"inv\":\"00000666\",\"invPreTaxAmt\":\"11.0000\",\"taxRate\":\"0.0000\",\"ifCut\":\"0\",\"taxTypeZ\":\" \",\"payCode\":\"0001\",\"taxType\":\"X\",\"transaction\":\"000004700275614\"},\"screen_name\":\"CICCNMP_FAPI\"}]";
        List<HostReturnScreenEntity> lists=new ArrayList<HostReturnScreenEntity>();
        JSONArray jsonArray=JSONArray.fromObject(text);
      //用来判断code
        int numa=0;
        int numb=0;
        String matchno="";
        for(int i=0;i<jsonArray.size();i++) {
            Object o = jsonArray.get(i);
            JSONObject jsonObject2 = JSONObject.fromObject(o);
            HostReturnScreenEntity hostReturnScreenEntity = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class);
            if(hostReturnScreenEntity.getCode().equals("0")){
                numa++;
             }else{

//                PoEntity poEntity=new PoEntity();
//                poEntity.setVenderid(hostReturnScreenEntity.getData().getVender());
//                poEntity.setPocode(hostReturnScreenEntity.getData().getPoNbr());
//                poEntity.setTractionNbr(hostReturnScreenEntity.getData().getTransaction());
//                poEntity.setTractionId(hostReturnScreenEntity.getId());
//                poEntity.setSeq(hostReturnScreenEntity.getData().getSeq());
//                poEntity.setJvcode(hostReturnScreenEntity.getData().getJv());
//                poEntity.setInvoiceno(hostReturnScreenEntity.getData().getInv());
//                poEntity.setAmountpaid(new BigDecimal(hostReturnScreenEntity.getData().getInvPreTaxAmt()));
//                SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();
//                submitOutstandingReportEntity.setErrcode("999");
//                submitOutstandingReportEntity.setErrdesc("host写屏失败");
//                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
//                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
//                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
//                submitOutstandingReportEntity.setRece("");
//                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
//                submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
//                submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
                numb++;
             }


        }


        for(int i=0;i<jsonArray.size();i++) {
            Object o = jsonArray.get(i);
            JSONObject jsonObject2 = JSONObject.fromObject(o);
            HostReturnScreenEntity hostReturnScreenEntity = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class);

            List<InvoicesEntity> invList = matchService.getInvoice(hostReturnScreenEntity.getData().getVender(), hostReturnScreenEntity.getData().getInv(),hostReturnScreenEntity.getData().getInvTotal());
            InvoicesEntity inv = null;
            if (invList != null) {
                if (invList.size() > 0){
                    for(InvoicesEntity ie:invList){
                        if(ie.getMatchno()!=null){
                            //根据Matchno获取MatchEntity对象
                            List<MatchEntity>melist=matchService.getMatch(ie.getMatchno().toString());
                            matchno=ie.getMatchno().toString();
                            //将数据插入returnScreen表
                            hostReturnScreenEntity.setMatchNo(matchno);

                            if(melist!=null){
                                if(melist.size()>0){
                                    MatchEntity me =melist.get(0);
                                    if (numa==0&&numb>0) {
                                        //code全部不为0
                                        //通过ID修改Hoststatus
                                        PoEntity poEntity=new PoEntity();
                                        poEntity.setVenderid(hostReturnScreenEntity.getData().getVender());
                                        poEntity.setPocode(hostReturnScreenEntity.getData().getPoNbr());
                                        poEntity.setTractionNbr(hostReturnScreenEntity.getData().getTransaction());
                                        poEntity.setTractionId(hostReturnScreenEntity.getId());
                                        poEntity.setSeq(hostReturnScreenEntity.getData().getSeq());
                                        poEntity.setJvcode(hostReturnScreenEntity.getData().getJv());
                                        poEntity.setInvoiceno(hostReturnScreenEntity.getData().getInv());
                                        poEntity.setAmountpaid(new BigDecimal(hostReturnScreenEntity.getData().getInvPreTaxAmt()));
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();
                                        submitOutstandingReportEntity.setErrcode("999");
                                        submitOutstandingReportEntity.setErrdesc("host写屏失败");
                                        submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                                        submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                                        submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                                        submitOutstandingReportEntity.setRece("");
                                        submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                                        submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
                                        submitOutstandingReportEntity.setBatchId(matchno);
                                        submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
                                        Integer num = matchService.updateMatchHostStatus(hostReturnScreenEntity.getCode(), me.getId().toString());
                                    } else if(numa>0&&numb==0){
                                        //code全部为0
                                        returnScreenService.insertReturnScreen(hostReturnScreenEntity);

                                        Integer num = matchService.updateMatchHostStatus("1", me.getId().toString());
                                    }else if(numa>0&&numb>0){
                                        //code有0有其他
                                        PoEntity poEntity=new PoEntity();
                                        poEntity.setVenderid(hostReturnScreenEntity.getData().getVender());
                                        poEntity.setPocode(hostReturnScreenEntity.getData().getPoNbr());
                                        poEntity.setTractionNbr(hostReturnScreenEntity.getData().getTransaction());
                                        poEntity.setTractionId(hostReturnScreenEntity.getId());
                                        poEntity.setSeq(hostReturnScreenEntity.getData().getSeq());
                                        poEntity.setJvcode(hostReturnScreenEntity.getData().getJv());
                                        poEntity.setInvoiceno(hostReturnScreenEntity.getData().getInv());
                                        poEntity.setAmountpaid(new BigDecimal(hostReturnScreenEntity.getData().getInvPreTaxAmt()));
                                        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();
                                        submitOutstandingReportEntity.setErrcode("999");
                                        submitOutstandingReportEntity.setErrdesc("host写屏失败");
                                        submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                                        submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                                        submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                                        submitOutstandingReportEntity.setRece("");
                                        submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                                        submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
                                        submitOutstandingReportEntity.setBatchId(matchno);
                                        submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
                                        Integer num = matchService.updateMatchHostStatus("999", me.getId().toString());
                                    }

                                }else{
                                	logger.info("没有该数据");
                                }
                            }
                        }
                    }
                }else{
                	logger.info("没有该数据");
                }
            }


//            //code为0，ifCut为1则更新poDetail和po金额数据
//            if(hostReturnScreenEntity.getCode().equals("0")&hostReturnScreenEntity.getData().getIfCut().equals("1")){
//
//                PoEntity poEntity=new PoEntity();
//                poEntity.setVenderid(hostReturnScreenEntity.getData().getVender());
//                poEntity.setPocode(hostReturnScreenEntity.getData().getPoNbr());
//                poEntity.setTractionNbr(hostReturnScreenEntity.getData().getTransaction());
//                poEntity.setTractionId(hostReturnScreenEntity.getId().substring(0,hostReturnScreenEntity.getId().length()-hostReturnScreenEntity.getData().getSeq().length()));
//                poEntity.setSeq(hostReturnScreenEntity.getData().getSeq());
//                poEntity.setAmountpaid(new BigDecimal(hostReturnScreenEntity.getData().getInvPreTaxAmt()));
//                poEntity.setJvcode(hostReturnScreenEntity.getData().getJv());
//                poEntity.setInvoiceno(hostReturnScreenEntity.getData().getInv());
//                poEntity.setInvoiceno(hostReturnScreenEntity.getData().getInv());
//                poEntity.setJvcode(hostReturnScreenEntity.getData().getJv());
//                poEntity.setVenderid(hostReturnScreenEntity.getData().getVender());
//                this.checkHost(poEntity)  ;
////                                        PoEntity poDetail= detailsService.selectPoDetail(hostReturnScreenEntity.getId());
////                                        BigDecimal amount=poDetail.getAmountunpaid();
////                                        BigDecimal receiptAmount=poDetail.getReceiptAmount().subtract(poDetail.getAmountunpaid());
////                                        String pocode=poDetail.getPocode();
////                                        detailsService.updatePodetail(receiptAmount,new BigDecimal(0),poDetail.getId());
////                                        PoEntity po=detailsService.selectPo(pocode);
////                                        receiptAmount=po.getReceiptAmount().subtract(amount);
////                                        BigDecimal poAmount=po.getAmountunpaid().subtract(amount);
////                                        detailsService.updatePo(receiptAmount,poAmount,po.getId());
//            }
        }
    }
    public  void checkHost(PoEntity poEntity){
        Connection conn = null;
        try {
            conn = DB2Conn.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement st1 = null;
        ResultSet rs1 = null;
        String sql2= "select   a.* from (SELECT   OC.* , Row_Number() OVER ( ORDER BY process_status_ts DESC ) rnum  FROM (SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT,B.process_status_ts from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"')  OC ) a where a.rnum =1";

//        String sql2 = "SELECT  B.PROCESS_STAT_CODE,A.TXN_COST_AMT from CNINVMAT.TXN_PROCESS_LOG  B left join  CNINVMAT.FINANCIAL_TXN  A on A.TRANSACTION_id=B.TRANSACTION_id and A.TXN_SEQ_NBR=B.TXN_SEQ_NBR WHERE B.TRANSACTION_id='"+poEntity.getTractionId()+"'  and B.TXN_SEQ_NBR='"+poEntity.getSeq()+"'";
        SubmitOutstandingReportEntity submitOutstandingReportEntity=new SubmitOutstandingReportEntity();

        try {
            st1=conn.prepareStatement(sql2);
            rs1=st1.executeQuery();
            BigDecimal cost=new BigDecimal(0);
            while(rs1.next()){

                try{

                    cost=rs1.getBigDecimal(2);
                }catch (Exception e){

                    continue;
                }finally {
                    continue;
                }

            }

            if(cost.compareTo(new BigDecimal(0).subtract(poEntity.getAmountpaid()))!=0){
                System.out.println(cost);
                System.out.println(poEntity.getAmountpaid());
                submitOutstandingReportEntity.setErrcode("998");
                submitOutstandingReportEntity.setErrdesc("host匹配失败");
                submitOutstandingReportEntity.setPoNo(poEntity.getPocode());
                submitOutstandingReportEntity.setTrans(poEntity.getTractionNbr());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setRece("");
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
                submitOutstandingReportEntity.setVendorNo(poEntity.getVenderid());
                submitOutstandingReportEntity.setInvNo(poEntity.getInvoiceno());
                submitOutstandingReportEntity.setJv(poEntity.getJvcode());
                submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}