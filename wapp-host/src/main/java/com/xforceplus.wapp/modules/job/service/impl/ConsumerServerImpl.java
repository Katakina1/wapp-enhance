package com.xforceplus.wapp.modules.job.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xforceplus.wapp.common.utils.DB2Conn;
import com.xforceplus.wapp.modules.job.service.ConsumerServer;
import com.xforceplus.wapp.modules.job.utils.ReceiveListener;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.posuopei.service.ReturnScreenService;
import com.google.common.collect.Lists;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ConsumerServerImpl implements ConsumerServer {
    private static Logger logger =getLogger (ConsumerServerImpl.class);
    @Autowired
    private MatchService matchService;
    @Autowired
    private DetailsService detailsService;
    @Autowired
    MatchDao matchDao;
    @Autowired
    private ReturnScreenService returnScreenService;
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;


    @Override
    public void receiveQueue(String text) {
        logger.info("text值:----------------------"+text);
        Integer invoiceCount =0;
        SubmitOutstandingReportEntity submitOutstandingReportEntitys=new SubmitOutstandingReportEntity();
        JSONArray jsonArray= JSON.parseArray(text);
      //用来判断code
        int numa=0;
        int numb=0;
        int C=0;
        int A=0;
        int B=0;
        int D=0;
        String matchno="";
        //校验
        for (int i = 0; i < jsonArray.size(); i++) {
            Object o = jsonArray.get(i);
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            JSONObject jsonObject3=jsonObject2.getJSONObject("data");
            HostReturnScreenEntity hostReturnScreenEntity = JSONObject.parseObject(jsonObject2.toJSONString(),new TypeReference<HostReturnScreenEntity>(){});
            String invDate = hostReturnScreenEntity.getData().getYY() + "-" + hostReturnScreenEntity.getData().getMM() + "-" + hostReturnScreenEntity.getData().getDD();
            if (("0").equals(hostReturnScreenEntity.getCode())) {
                //校验发票状态
                invoiceCount= checkInvocie(invoiceCount, hostReturnScreenEntity);

                if (invoiceCount == 0) {
                    numa++;
                } else {
                    numb++;
                }

            } else {
                numb++;
            }
            if("C0001".equals(hostReturnScreenEntity.getCode())){
                C++;
            }
            if("A0010".equals(hostReturnScreenEntity.getCode())){
                A++;
            }
            if("B0004".equals(hostReturnScreenEntity.getCode())||"B0001".equals(hostReturnScreenEntity.getCode())||"B0002".equals(hostReturnScreenEntity.getCode())||"B0003".equals(hostReturnScreenEntity.getCode())){
               B++;
            }
            //判断balance金额是否大于20的绝对值
            String balance=jsonObject3.getString("balance");
            if(StringUtils.isNotBlank(balance)) {
                balance=balance.trim().replace(",","");
                logger.info("balance金额:"+balance);
                if(!balance.equals("${balance}")){
                    if (new BigDecimal(balance).abs().compareTo(new BigDecimal("20")) == 1) {
                        D++;
                    }
                }
            }
        }
       //生成报告
        Map<String,Object> returnMap=new HashMap();
        returnMap= createReport( A,B,C,numa, numb, matchno,submitOutstandingReportEntitys,jsonArray,returnMap,D);
        //获取t_dx_robot_message表的请求报文与返回报文对比
        JSONObject jsonObject2 = jsonArray.getJSONObject(0);
        String messageId=jsonObject2.getString("id");
        String requestMessage=matchDao.selectRequestMessage(messageId);
        JSONArray jsonArrayRequest= JSON.parseArray(requestMessage);
        String errCode=returnMap.get("errCode").toString();
        String errDesc=returnMap.get("errDesc").toString();
        String bType=returnMap.get("bType").toString();
        RobotMessageEntity rme=new RobotMessageEntity();
        if(jsonArray.size()!=jsonArrayRequest.size()){
            if(bType.equals("Y")){
                logger.info("报告缺失！正在重新生成缺失报告");
                for (int i = jsonArray.size(); i < jsonArrayRequest.size(); i++){
                    JSONObject jsonObject = jsonArrayRequest.getJSONObject(i);
                    createReport2(errCode,errDesc,jsonObject);
                }
                rme.setStatus("3");
                rme.setMessageId(messageId);
                rme.setResponseMessage(text);
                matchDao.updateRobotMessage(rme);
            }else{
                rme.setStatus("2");
                rme.setMessageId(messageId);
                rme.setResponseMessage(text);
                matchDao.updateRobotMessage(rme);
            }
        }else{
         rme.setStatus("2");
         rme.setMessageId(messageId);
         rme.setResponseMessage(text);
         matchDao.updateRobotMessage(rme);
        }

    }


//    /**
 //     * 校验发票状态
 //     * @param conn
 //     * @param rs
 //     * @param st
 //     * @param invoiceCount
 //     * @param invDate
 //     * @param hostReturnScreenEntity
 //     */
//    public  int checkInvocie(Connection conn,ResultSet rs,PreparedStatement st,int invoiceCount,String invDate,HostReturnScreenEntity hostReturnScreenEntity){
//        String sql = "select count(1) from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+invDate+" 00:00:00') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+invDate+" 00:00:00' and B.invoice_nbr='" + "0000000" + hostReturnScreenEntity.getData().getInv() + "' and B.invoice_date='" + invDate + "' and D.PROCESS_STAT_CODE IN (1,13,10)";
//        // 创建语句执行者
//        try {
//            st = conn.prepareStatement(sql);
//            rs = st.executeQuery();
//            while (rs.next()) {
//                invoiceCount = rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            invoiceCount = 0;
//        }finally {
//            DB2Conn.closeResultSet(rs);
//            DB2Conn.closeStatement(st);
//            return invoiceCount;
//        }
//
//    }

    /**
     * 校验发票状态

     * @param invoiceCount

     * @param hostReturnScreenEntity
     */
    public  int checkInvocie(int invoiceCount,HostReturnScreenEntity hostReturnScreenEntity){
        BigDecimal invTotal=new BigDecimal(hostReturnScreenEntity.getData().getInvTotal());
        BigDecimal taxTotal=new BigDecimal(hostReturnScreenEntity.getData().getTaxTotal());
        BigDecimal invTotalPreTax=invTotal.subtract(taxTotal);
        logger.info("invTotalPreTax {}",invTotalPreTax);
        String trackTotalPreTax=hostReturnScreenEntity.getData().getError();
        logger.info("trackTotalPreTax {}",trackTotalPreTax);
        try{
            trackTotalPreTax=trackTotalPreTax.trim().replace("-","").replace(",","");
            BigDecimal trackTotal=new BigDecimal(trackTotalPreTax);
            logger.info("trackTotal {}",trackTotal);
            BigDecimal cover=invTotalPreTax.subtract(trackTotal);
            if(cover.compareTo(new BigDecimal(20))<=0&&cover.compareTo(new BigDecimal(-20))>=0){
                return 0;
            }else{
                return 1;
            }
        }catch (Exception e){
            logger.info("exception {}",e);
            return 1;
        }

    }

    /**
     * 生成报告
     */
    public Map<String,Object> createReport(int A,int B,int C,int numa,int numb,String matchno, SubmitOutstandingReportEntity submitOutstandingReportEntitys,JSONArray jsonArray,Map<String,Object> returnMap,int D){
        try {
            String errCode="999";
            String errDesc="因上条报文写屏失败！";
            String bType="";
            logger.info("解析后ArraySize:"+jsonArray.size());
            //D大于0代表改组匹配关系存在balance金额大于20，整组放到失败报告
            if(D>0){
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    JSONObject ob = jsonObject2.getJSONObject("data");
                    JSONArray array = ob.getJSONArray("comments");
                    List<LoopEntity> lo = JSONArray.parseArray(array.toJSONString(), LoopEntity.class);
                    HostReturnScreenEntity hostReturnEntity = JSONObject.parseObject(jsonObject2.toJSONString(), HostReturnScreenEntity.class);
                    hostReturnEntity.getData().setComments(lo);
                    //HostReturnScreenEntity hostReturnEntity = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class, map);
                    List<HostReturnScreenEntity> hostReturnScreenEntityList = Lists.newArrayList();
                    List<LoopEntity> loopEntityList = hostReturnEntity.getData().getComments();
                    loopEntityList.forEach(loopEntity -> {
                        // HostReturnScreenEntity hostReturn = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class);
                        HostReturnScreenEntity hostReturn = JSONObject.parseObject(jsonObject2.toJSONString(), HostReturnScreenEntity.class);
                        hostReturn.getData().setCover(loopEntity.getCover());
                        hostReturn.getData().setIfFapr(loopEntity.getIfFapr());
                        hostReturn.getData().setIfCut(loopEntity.getIfCut());
                        hostReturn.getData().setTransaction(loopEntity.getTransaction());
                        hostReturn.getData().setReceiver(loopEntity.getReceiver());
                        hostReturn.getData().setInvPreTaxAmt(loopEntity.getInvPreTaxAmt());
                        hostReturn.getData().setSeq(loopEntity.getSeq());
                        hostReturnScreenEntityList.add(hostReturn);

                    });
                    if (StringUtils.isEmpty(matchno)) {
                        List<InvoicesEntity> invList = matchService.getInvoice(hostReturnEntity.getData().getVender(), hostReturnEntity.getData().getInv(), hostReturnEntity.getData().getInvTotal());
                        InvoicesEntity ie = null;
                        try {
                            //获取不为空的matchno
                            for (InvoicesEntity in : invList) {
                                Long mno = in.getMatchno();
                                if (mno != null & mno > 0) {
                                    ie = in;
                                }
                            }
                            //根据Matchno获取MatchEntity对象
                            matchno = ie.getMatchno().toString();

                        } catch (Exception e) {
                            matchno = "-1";
                        }

                    }
                    //删除A0010、B0004失败报告数据
                    if(i==0){
                        logger.info("matchno删除开始:"+matchno);
                        submitOutstandingReportDao.deleteBatchid(matchno);
                    }
                    for (int ty = 0; ty < hostReturnScreenEntityList.size(); ty++) {
                        HostReturnScreenEntity lastHostReEntity = hostReturnScreenEntityList.get(ty);
                        lastHostReEntity.setMatchNo(matchno);
                        SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                        submitOutstandingReportEntity.setErrcode("A110");
                        submitOutstandingReportEntity.setErrdesc("差异超20,需手工处理");
                        errCode = "A110";
                        errDesc = "差异超20,需手工处理！";
                        submitOutstandingReportEntity.setPoNo(lastHostReEntity.getData().getPoNbr());
                        submitOutstandingReportEntity.setTrans(lastHostReEntity.getData().getTransaction());
                        submitOutstandingReportEntity.setVendorNo(lastHostReEntity.getData().getVender());
                        submitOutstandingReportEntity.setRece(lastHostReEntity.getData().getReceiver());
                        submitOutstandingReportEntity.setJv(lastHostReEntity.getData().getJv());
                        submitOutstandingReportEntity.setWmCost(lastHostReEntity.getData().getInvPreTaxAmt());
                        //增加字段
                        if (ty == 0) {
                            logger.info("打印发票号:" + matchno + "_" + lastHostReEntity.getData().getInv());
                            submitOutstandingReportEntity.setInvNo(lastHostReEntity.getData().getInv());
                            submitOutstandingReportEntity.setInvoiceCost(lastHostReEntity.getData().getInvTotal());
                            submitOutstandingReportEntity.setTaxAmount(lastHostReEntity.getData().getTaxTotal());
                            submitOutstandingReportEntity.setTaxRate(lastHostReEntity.getData().getTaxRate());
                            submitOutstandingReportEntity.setTaxType(StringUtils.isBlank(lastHostReEntity.getData().getTaxType()) ? "01" : "04");
                        }
                        submitOutstandingReportEntity.setBatchId(matchno);
                        submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
                    }
                }
                bType="N";
            }else{
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
              /*  Map map = new HashMap<>();
                map.put("comments", LoopEntity.class);*/
                    JSONObject ob=jsonObject2.getJSONObject("data");
                    JSONArray array=ob.getJSONArray("comments");
                    List<LoopEntity> lo=JSONArray.parseArray(array.toJSONString(),LoopEntity.class);
                    HostReturnScreenEntity hostReturnEntity = JSONObject.parseObject(jsonObject2.toJSONString(),HostReturnScreenEntity.class);
                    hostReturnEntity.getData().setComments(lo);
                    //HostReturnScreenEntity hostReturnEntity = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class, map);
                    List<HostReturnScreenEntity> hostReturnScreenEntityList = Lists.newArrayList();
                    List<LoopEntity> loopEntityList = hostReturnEntity.getData().getComments();
                    loopEntityList.forEach(loopEntity -> {
                        // HostReturnScreenEntity hostReturn = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class);
                        HostReturnScreenEntity hostReturn = JSONObject.parseObject(jsonObject2.toJSONString(),HostReturnScreenEntity.class);
                        hostReturn.getData().setCover(loopEntity.getCover());
                        hostReturn.getData().setIfFapr(loopEntity.getIfFapr());
                        hostReturn.getData().setIfCut(loopEntity.getIfCut());
                        hostReturn.getData().setTransaction(loopEntity.getTransaction());
                        hostReturn.getData().setReceiver(loopEntity.getReceiver());
                        hostReturn.getData().setInvPreTaxAmt(loopEntity.getInvPreTaxAmt());
                        hostReturn.getData().setSeq(loopEntity.getSeq());
                        hostReturnScreenEntityList.add(hostReturn);

                    });
                    if (StringUtils.isEmpty(matchno)) {
                        List<InvoicesEntity> invList = matchService.getInvoice(hostReturnEntity.getData().getVender(), hostReturnEntity.getData().getInv(), hostReturnEntity.getData().getInvTotal());
                        if(invList.size()>0){
                            matchno = invList.get(0).getMatchno().toString();
                        }else{
                            matchno = "-1";
                        }
                    }
                    //删除A0010、B0004失败报告数据
                    if(i==0){
                        logger.info("matchno删除开始:"+matchno);
                        submitOutstandingReportDao.deleteBatchid(matchno);
                    }
                    logger.info("matchno:"+matchno+"报告"+i+"生成开始------size:" + jsonArray.size());
                    for (int ty = 0; ty < hostReturnScreenEntityList.size(); ty++) {
                        HostReturnScreenEntity lastHostReEntity = hostReturnScreenEntityList.get(ty);

                        lastHostReEntity.setMatchNo(matchno);


                        if (numa == 0 && numb > 0) {
                            //code全部不为0
                            //通过ID修改Hoststatus
                            SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                            if ((A > 0 || B > 0) && C == 0) {

                                submitOutstandingReportEntity.setErrcode(lastHostReEntity.getCode());
                                if ("B0003".equals(lastHostReEntity.getCode())) {
                                    submitOutstandingReportEntity.setErrdesc("账号密码过期，请重设密码！");
                                    errCode=lastHostReEntity.getCode();
                                    errDesc="账号密码过期，请重设密码！";

                                } else {
                                    submitOutstandingReportEntity.setErrdesc("打开屏幕失败，将会自动重写，无需处理");
                                    errCode=lastHostReEntity.getCode();
                                    errDesc="打开屏幕失败，将会自动重写，无需处理";
                                }
                            } else {
                                submitOutstandingReportEntity.setErrcode("999");
                                submitOutstandingReportEntity.setErrdesc("host写屏失败");
                                errCode="999";
                                errDesc="host写屏失败";
                            }


                            submitOutstandingReportEntity.setPoNo(lastHostReEntity.getData().getPoNbr());
                            submitOutstandingReportEntity.setTrans(lastHostReEntity.getData().getTransaction());
                            submitOutstandingReportEntity.setVendorNo(lastHostReEntity.getData().getVender());
                            submitOutstandingReportEntity.setRece(lastHostReEntity.getData().getReceiver());
                            submitOutstandingReportEntity.setJv(lastHostReEntity.getData().getJv());
                            submitOutstandingReportEntity.setWmCost(lastHostReEntity.getData().getInvPreTaxAmt());

                            //增加字段
                            if (ty == 0) {
                                logger.info("打印发票号:"+matchno+"_"+lastHostReEntity.getData().getInv());
                                submitOutstandingReportEntity.setInvNo(lastHostReEntity.getData().getInv());
                                submitOutstandingReportEntity.setInvoiceCost(lastHostReEntity.getData().getInvTotal());
                                submitOutstandingReportEntity.setTaxAmount(lastHostReEntity.getData().getTaxTotal());
                                submitOutstandingReportEntity.setTaxRate(lastHostReEntity.getData().getTaxRate());
                                submitOutstandingReportEntity.setTaxType(StringUtils.isBlank(lastHostReEntity.getData().getTaxType()) ? "01" : "04");
                            }

                            //
                            submitOutstandingReportEntity.setBatchId(matchno);
                            submitOutstandingReportEntitys = submitOutstandingReportEntity;
                            if (ty == 0) {
                                if ("0".equals(lastHostReEntity.getCode())) {
                                    matchService.updateMatchHostStatus("999", matchno);
                                } else {
                                    matchService.updateMatchHostStatus(lastHostReEntity.getCode(), matchno);
                                }
                            }

                            if ((A > 0 || B > 0) && C == 0) {
                                if ("B0003".equals(lastHostReEntity.getCode())) {
                                    if (submitOutstandingReportEntitys.getErrcode().equals("999")) {
                                        //查询999的次数
                                        int count = submitOutstandingReportDao.getNineCount(submitOutstandingReportEntitys.getBatchId());
                                        if (count >= 1) {
                                            submitOutstandingReportEntitys.setErrdesc("请手工处理");
                                            errDesc="请手工处理";
                                            matchDao.updateIsDel("1",matchno);
                                        }
                                    }
                                }
                                submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntitys);
                            } else {
                                if (submitOutstandingReportEntitys.getErrcode().equals("999")) {
                                    //查询999的次数
                                    int count = submitOutstandingReportDao.getNineCount(submitOutstandingReportEntitys.getBatchId());
                                    if (count >= 1) {
                                        submitOutstandingReportEntitys.setErrdesc("请手工处理");
                                        errDesc="请手工处理";
                                        matchDao.updateIsDel("1",matchno);
                                    }
                                }
                                submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntitys);
                            }


                        } else if (numa > 0 && numb == 0) {
                            //code全部为0
                            returnScreenService.insertReturnScreen(lastHostReEntity);

                            if (ty == 0) {
                                Integer num = matchService.updateMatchHostStatus("1", matchno);
                                errCode="1";
                            }

                        } else if (numa > 0 && numb > 0) {
                            //code有0有其他
                            SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
                            submitOutstandingReportEntity.setErrcode("999");
                            submitOutstandingReportEntity.setErrdesc("host写屏失败");
                            errCode="999";
                            errDesc="host写屏失败";
                            submitOutstandingReportEntity.setPoNo(lastHostReEntity.getData().getPoNbr());
                            submitOutstandingReportEntity.setTrans(lastHostReEntity.getData().getTransaction());
                            submitOutstandingReportEntity.setVendorNo(lastHostReEntity.getData().getVender());
                            submitOutstandingReportEntity.setRece(lastHostReEntity.getData().getReceiver());
                            submitOutstandingReportEntity.setWmCost(lastHostReEntity.getData().getInvPreTaxAmt());
                            submitOutstandingReportEntity.setJv(lastHostReEntity.getData().getJv());

                            //增加字段
                            if (ty == 0) {
                                logger.info("打印发票号:"+matchno+"_"+lastHostReEntity.getData().getInv());
                                submitOutstandingReportEntity.setInvNo(lastHostReEntity.getData().getInv());
                                submitOutstandingReportEntity.setInvoiceCost(lastHostReEntity.getData().getInvTotal());
                                submitOutstandingReportEntity.setTaxAmount(lastHostReEntity.getData().getTaxTotal());
                                submitOutstandingReportEntity.setTaxRate(lastHostReEntity.getData().getTaxRate());
                                submitOutstandingReportEntity.setTaxType(StringUtils.isBlank(lastHostReEntity.getData().getTaxType()) ? "01" : "04");
                            }
                            //
                            submitOutstandingReportEntity.setBatchId(matchno);
                            submitOutstandingReportEntitys = submitOutstandingReportEntity;
                            if (ty == 0) {
                                Integer num = matchService.updateMatchHostStatus("999", matchno);
                            }
                            if (submitOutstandingReportEntitys.getErrcode().equals("999")) {
                                //查询999的次数
                                int count = submitOutstandingReportDao.getNineCount(submitOutstandingReportEntitys.getBatchId());
                                if (count >= 1) {
                                    submitOutstandingReportEntitys.setErrdesc("请手工处理");
                                    errDesc="请手工处理";
                                    matchDao.updateIsDel("1",matchno);
                                }
                            }
                            submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntitys);

                        }
                    }
                }
                bType="Y";
            }

            returnMap.put("errCode",errCode);
            returnMap.put("errDesc",errDesc);
            returnMap.put("bType",bType);
        }catch (Exception e){
            logger.info("生成报告异常:",e);
            e.printStackTrace();
        }
        logger.info("报告生成完毕-----"+matchno);
        return returnMap;
    }
    /**
     * 生成报告
     */
    public void createReport2(String errCode,String errDesc,JSONObject jsonObject2){
        String matchno="";
        SubmitOutstandingReportEntity submitOutstandingReportEntitys=new SubmitOutstandingReportEntity();
        JSONObject ob=jsonObject2.getJSONObject("data");
        JSONArray array=ob.getJSONArray("comments");
        List<LoopEntity> lo=JSONArray.parseArray(array.toJSONString(),LoopEntity.class);
        HostReturnScreenEntity hostReturnEntity = JSONObject.parseObject(jsonObject2.toJSONString(),HostReturnScreenEntity.class);
        hostReturnEntity.getData().setComments(lo);
        //HostReturnScreenEntity hostReturnEntity = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class, map);
        List<HostReturnScreenEntity> hostReturnScreenEntityList = Lists.newArrayList();
        List<LoopEntity> loopEntityList = hostReturnEntity.getData().getComments();
        loopEntityList.forEach(loopEntity -> {
            // HostReturnScreenEntity hostReturn = (HostReturnScreenEntity) JSONObject.toBean(jsonObject2, HostReturnScreenEntity.class);
            HostReturnScreenEntity hostReturn = JSONObject.parseObject(jsonObject2.toJSONString(),HostReturnScreenEntity.class);
            hostReturn.getData().setCover(loopEntity.getCover());
            hostReturn.getData().setIfFapr(loopEntity.getIfFapr());
            hostReturn.getData().setIfCut(loopEntity.getIfCut());
            hostReturn.getData().setTransaction(loopEntity.getTransaction());
            hostReturn.getData().setReceiver(loopEntity.getReceiver());
            hostReturn.getData().setInvPreTaxAmt(loopEntity.getInvPreTaxAmt());
            hostReturn.getData().setSeq(loopEntity.getSeq());
            hostReturnScreenEntityList.add(hostReturn);

        });
        if (StringUtils.isEmpty(matchno)) {
            List<InvoicesEntity> invList = matchService.getInvoice(hostReturnEntity.getData().getVender(), hostReturnEntity.getData().getInv(), hostReturnEntity.getData().getInvTotal());
            if(invList.size()>0){
                matchno = invList.get(0).getMatchno().toString();
            }else{
                matchno = "-1";
            }
        }

        for (int ty = 0; ty < hostReturnScreenEntityList.size(); ty++) {
            HostReturnScreenEntity lastHostReEntity = hostReturnScreenEntityList.get(ty);
            lastHostReEntity.setCode(errCode);
            lastHostReEntity.setMatchNo(matchno);
            SubmitOutstandingReportEntity submitOutstandingReportEntity = new SubmitOutstandingReportEntity();
            submitOutstandingReportEntity.setErrcode(errCode);
            submitOutstandingReportEntity.setPoNo(lastHostReEntity.getData().getPoNbr());
            submitOutstandingReportEntity.setTrans(lastHostReEntity.getData().getTransaction());
            submitOutstandingReportEntity.setVendorNo(lastHostReEntity.getData().getVender());
            submitOutstandingReportEntity.setRece(lastHostReEntity.getData().getReceiver());
            submitOutstandingReportEntity.setJv(lastHostReEntity.getData().getJv());
            submitOutstandingReportEntity.setWmCost(lastHostReEntity.getData().getInvPreTaxAmt());

            //增加字段
            if (ty == 0) {
                logger.info("打印发票号:" + matchno + "_" + lastHostReEntity.getData().getInv());
                submitOutstandingReportEntity.setInvNo(lastHostReEntity.getData().getInv());
                submitOutstandingReportEntity.setInvoiceCost(lastHostReEntity.getData().getInvTotal());
                submitOutstandingReportEntity.setTaxAmount(lastHostReEntity.getData().getTaxTotal());
                submitOutstandingReportEntity.setTaxRate(lastHostReEntity.getData().getTaxRate());
                submitOutstandingReportEntity.setTaxType(StringUtils.isBlank(lastHostReEntity.getData().getTaxType()) ? "01" : "04");
            }

            //
            submitOutstandingReportEntity.setBatchId(matchno);
            submitOutstandingReportEntitys = submitOutstandingReportEntity;
            if (ty == 0) {
                if ("0".equals(lastHostReEntity.getCode())) {
                    matchService.updateMatchHostStatus("999", matchno);
                } else {
                    matchService.updateMatchHostStatus(lastHostReEntity.getCode(), matchno);
                }
            }
            submitOutstandingReportEntitys.setErrcode(errCode);
            submitOutstandingReportEntitys.setErrdesc(errDesc);
            submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntitys);
        }

    }


//    public  String connHostAgain(String date1,String invoiceNo,String vendorId){
//        Boolean flag=true;
//        Connection conn = null;
//        PreparedStatement st = null;
//        ResultSet rs = null;
//        vendorId = vendorId.replaceAll("^(0+)", "");
//        String hostStatus=null;
//        try {
//            // 获取连接
//            conn = DB2Conn.getConnection();
//            // 编写sql
//            String sql = "select B.invoice_nbr,B.invoice_date,D.PROCESS_STAT_CODE,B.vendor_nbr,D.process_status_ts from CNINVMAT.INVOICE B LEFT JOIN (select a.*  from (SELECT   OC.* , Row_Number() OVER (partition by invoice_id ORDER BY process_status_ts DESC ) rnum  FROM CNINVMAT.INVC_PROCESS_LOG OC WHERE OC.process_status_ts>'"+date1+"') a where a.rnum =1)AS D ON D.INVOICE_ID=B.INVOICE_ID where D.process_status_ts>'"+date1+"' and B.invoice_nbr = '0000000"+invoiceNo+"' and B.vendor_nbr ='"+vendorId+"'";
//            // 创建语句执行者
//            st= conn.prepareStatement(sql);
//            //设置参数
//            System.out.println("连接成功");
//            // 执行sql
//            rs=st.executeQuery();
//            matchDao.insertTaskLog("connHostAgain","start",date1);
//            System.out.println("连接成功");
//            System.out.println(new Date().toString());
//
//            while (rs.next()){
//
//
//
//                hostStatus=rs.getString(3);
//                return hostStatus;
////                this.hostDeleteInvoice( invoiceNo,  hostStatus,  invoiceDate, vendor,  hostDate);
//            }
//        } catch (Exception e) {
//            flag=false;
//            logger.info("{}",e);
//        }finally {
//            System.out.println("close connect");
//            System.out.println(new Date().toString());
//            DB2Conn.closeConnection(conn);
//        }
//
//        if(!flag) {
//            return null;
//        }else {
//            return hostStatus;
//        }
//    }
}
