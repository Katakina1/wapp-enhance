package com.xforceplus.wapp.interfaceBPMS;


import com.actionsoft.bpms.api.OpenApiClient;
import com.actionsoft.sdk.service.model.FormFileModel;
import com.actionsoft.sdk.service.model.TaskQueryModel;
import com.actionsoft.sdk.service.model.UploadFile;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 费用相关接口实现
 */
@Component
public class CostAppliction {
    private static final String API_SERVER = "https://bpms.cn.wal-mart.com/portal/openapi";
    private static final String ACCESS_KEY = "svcwappEVP";
    private static final String SECRET = "2tQxta7JmK!;afL:";

    //非预付款流程定义ID
    private String PROCESS_DEF_ID = "obj_b3aeb01f55264728a025d777a47044ab";
    //预付款流程定义ID
    private String PROCESS_DEF_ID_PRE = "obj_de609c1d89fe4e0c8093471dcd349b1a";
    //AWS登陆账号
    private String UID = "215828474";

    private SimpleDateFormat sdf;

    //sftp IP地址
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;

    private static final Logger logger = Logger.getLogger(CostAppliction.class);

    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    private OpenApiClient client;
    private SFTPHandler handler;

    @Autowired
    private CostPushService costPushService;

    private static CostAppliction costAppliction;

    @PostConstruct
    public void init(){
        costAppliction = this;
        costAppliction.costPushService = this.costPushService;
        costAppliction.host = this.host;
        costAppliction.userName = this.userName;
        costAppliction.password = this.password;
        costAppliction.defaultPort = this.defaultPort;
        costAppliction.defaultTimeout = this.defaultTimeout;
        costAppliction.localImageRootPath = this.localImageRootPath;

        sdf = new SimpleDateFormat("yyyy-MM-dd");

        client = new OpenApiClient(API_SERVER, ACCESS_KEY, SECRET,"json",30000,30000);
        handler = SFTPHandler.getHandler(costAppliction.localImageRootPath, costAppliction.localImageRootPath);
    }


    private static class CostApplictionInstance {
        private static final CostAppliction INSTANCE = costAppliction;
    }

    public static CostAppliction getInstance() {
        return CostApplictionInstance.INSTANCE;
    }




    /**
     * 非预付款流程提交申请
     */
    public void sendCostApplication(SettlementEntity mainEntity){

        //step.1 创建流程实例
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("processDefId", PROCESS_DEF_ID);
        args.put("uid", UID);
        args.put("title", "非预付款费用推送("+mainEntity.getCostNo()+")");
        String res1 = client.exec("process.create", args);
        String instanceId = JSONObject.fromObject(res1).getJSONObject("data").getString("id");
        logger.info("-------获取实例ID：" + instanceId + "--------");


        //step.2 推送数据
        //主数据
        args.clear();
        args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
        Map<String, Object> mainData = new HashMap<String, Object>();
        mainData.put("REQUSERNO", mainEntity.getStaffNo());
        //mainData.put("FIRSTAPPROVER_USERNO", mainEntity.getStaffNo());
        mainData.put("MARTTYPE","WAPP");
        mainData.put("EPS_TYPE", "1".equals(mainEntity.getBusinessType())?"4":"5");
        mainData.put("SUPPLIER_ID",mainEntity.getVenderId());//180250
        mainData.put("SUPPLIER_NAME",mainEntity.getVenderName());
        mainData.put("BANK_NAME",mainEntity.getBankName());
        mainData.put("BANK_ACCOUNT",mainEntity.getBankAccount());
        mainData.put("PAY_DATE",sdf.format(new Date()));
        mainData.put("TOTAL_AMOUNT",mainEntity.getSettlementAmount());
        mainData.put("HAS_INVOICE","true");
        mainData.put("COSTNO",mainEntity.getCostNo());
        mainData.put("REMARK",mainEntity.getRemark());
        args.put("recordData", mainData);
        args.put("bindId", instanceId);
        String res2 = client.exec("bo.create", args);
        logger.info("-------创建BO主表数据完成:"+res2+"---------");
        JSONObject json = JSONObject.fromObject(res2);
        String boId = (String)json.get("data");
        //发票数据
        for(InvoiceRateEntity rate : mainEntity.getInvoiceRateList()){
            args.clear();
            args.put("boName", "BO_ACT_INVOICE");
            Map<String, Object> invoiceData = new HashMap<String, Object>();
            invoiceData.put("INVOICE_ID",rate.getInvoiceNo());
            invoiceData.put("INVOICE_CODE", rate.getInvoiceCode());
            invoiceData.put("CHECK_CODE", rate.getCheckCode());
            invoiceData.put("INVOICE_OBJECT_INFO","01".equals(rate.getInvoiceType())?"1":"2");
            invoiceData.put("INVOICE_AMOUNT",rate.getInvoiceAmount());
            invoiceData.put("TAX_RATE",rate.getTaxRate());
            invoiceData.put("TAX_AMOUNT",rate.getTaxAmount());
            invoiceData.put("WITHOUT_TAX_AMOUNT",rate.getInvoiceAmount().subtract(rate.getTaxAmount()));
            invoiceData.put("INVOICE_DATE",rate.getInvoiceDate());
            invoiceData.put("APPLY_AMOUNT",rate.getInvoiceAmount());
            args.put("recordData", invoiceData);
            args.put("bindId", instanceId);
            String res2_1 = client.exec("bo.create", args);
            logger.info("-------创建BO发票表数据完成:"+res2_1+"---------");
            JSONObject json_invoice = JSONObject.fromObject(res2_1);
            String InvoiceBoId = (String)json_invoice.get("data");
            //费用数据
            for(CostEntity cost : rate.getCostTableData()){
                args.clear();
                args.put("boName", "BO_ACT_EPS_VENDOR_DETAIL");
                Map<String, Object> costData = new HashMap<String, Object>();
                costData.put("INVOICE_ID",rate.getInvoiceNo());
                costData.put("TYPE",cost.getCostTypeName());
                costData.put("TYPE_NAME",cost.getCostType());
                costData.put("DEPARTMENT_NAME",costDeptGet(cost.getCostDept()));
                costData.put("DEPARTMENT_ID",cost.getCostDeptId());
                String[] times = cost.getCostTime().split("至");
                if(times.length==2) {
                    costData.put("TIME", times[0]);
                    costData.put("TIME2", times[1]);
                }else{
                    costData.put("TIME", "");
                    costData.put("TIME2", "");
                }
                costData.put("DESCRIPTION",cost.getCostUse());
                costData.put("MONEY",cost.getCostAmount());
                costData.put("TIME3",cost.getCostTime());
                args.put("recordData", costData);
                args.put("bindId", InvoiceBoId);
                String res2_1_1 = client.exec("bo.create", args);
                logger.info("-------创建BO费用表数据完成:"+res2_1_1+"---------");

                //保存bpms的费用记录id, 方便审核通过后回写信息
                String bpmsId = JSONObject.fromObject(res2_1_1).getString("data");
                costAppliction.costPushService.saveCostId(cost.getId(), instanceId, bpmsId);
            }
        }


        //step.3 启动流程
        args.clear();
        args.put("processInstId", instanceId);
        String res3 = client.exec("process.start", args);
        logger.info("-------启动流程:"+res3+"---------");


        //step.4 获取任务实例
        args.clear();
        args.put("processInstId", instanceId);
        String res4 = client.exec("process.inst.get", args);
        String taskId = JSONObject.fromObject(res4).getJSONObject("data").getString("startTaskInstId");
        logger.info("-------获取任务实例:"+taskId+"---------");


        //step.5 推送附件
        try {
            handler.openChannel(costAppliction.host, costAppliction.userName, costAppliction.password, Integer.parseInt(costAppliction.defaultPort), Integer.parseInt(costAppliction.defaultTimeout));
            for (SettlementFileEntity fileEntity : mainEntity.getFileList()) {
                FormFileModel fileModel = new FormFileModel();
                fileModel.setAppId("com.actionsoft.apps.walmart.fin.process.expensevendorpaymentflow");
                fileModel.setBoId(boId);
                fileModel.setBoItemName("ATTACHMENT");
                fileModel.setBoName("BO_ACT_EPS_VENDOR_MAIN");
                fileModel.setCreateDate(new Timestamp(System.currentTimeMillis()));
                fileModel.setCreateUser(UID);
                fileModel.setFileName(fileEntity.getFileName());
                fileModel.setProcessInstId(instanceId);
                fileModel.setRemark("");
                fileModel.setTaskInstId(taskId);

                UploadFile uploadFile = new UploadFile();
                uploadFile.setName(fileEntity.getFileName());


                handler.download(fileEntity.getFilePath(), fileEntity.getFileName());
                File file = new File(handler.getLocalImageRootPath() + fileEntity.getFileName());
                byte[] buffer = null;
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                buffer = bos.toByteArray();
                uploadFile.setContent(buffer);
                args.clear();
                args.put("formFileModel", fileModel);
                args.put("data", uploadFile);
                String res5 = client.exec("bo.file.up", args);
                logger.info("-------推送附件:" + res5 + "---------");
            }
        } catch (Exception e){
            logger.error("推送附件异常,实例ID:"+instanceId+","+e);
            e.printStackTrace();
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }

        //保存生成的流程实例ID
        costAppliction.costPushService.saveInstanceId(mainEntity.getCostNo(), instanceId);
    }


    /**
     * 获取沃尔玛审批流程状态
     */
        public void getWalmartStatus(){

        List<SettlementEntity> list = costPushService.getMainInstanceId();
        logger.info("------------------list----------"+list.size());
        for(SettlementEntity entity : list){
            //首先,判断申请单是否被删除,若删除了,更新状态为2-审核不通过
            try{
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
            args.put("selectClause", "select * from BO_ACT_EPS_VENDOR_MAIN a join WFC_PROCESS b on a.bindId=b.ID where  b.EXT2 ='cancel' and b.ID ='"+entity.getInstanceId()+"'");
            logger.info("获取审批流程状态开始=============client"+client.toString());
            String res1 = client.exec("bo.query", args);
            logger.info("-------申请单删除状态数据:" + res1 + "---------");
            if(JSONObject.fromObject(res1).getJSONArray("data").size()>0){
                costAppliction.costPushService.updateStatus(entity.getCostNo(), "2");
                costAppliction.costPushService.cancelMatch(entity.getCostNo());
                continue;
            }

            //查询审批流程
            args.clear();
            args.put("processInstId", entity.getInstanceId());
            String res = client.exec("process.comments.get", args);
            logger.info("-------审批流程数据:" + res + "---------");

            JSONObject resObj = JSONObject.fromObject(res);

            Object o=resObj.get("msg");
            if(o!=null){
                String msg=o.toString();
                if(msg!=""){
                    if(msg.indexOf("java.lang.NullPointerException")!=-1){
                        costAppliction.costPushService.updateStatus(entity.getCostNo(), "2");
                        costAppliction.costPushService.cancelMatch(entity.getCostNo());
                    }
                }
            }
            JSONArray dataArr = resObj.getJSONArray("data");
            if(dataArr.size()>0){
                for(Iterator it = dataArr.iterator(); it.hasNext();){
                    JSONObject data = (JSONObject) it.next();
                    String activityName = data.getString("activityName");
                    String actionName = data.getString("actionName");


                    //3-submit
                    if("Submit Application".equals(activityName) && "提交".equals(actionName)){
                        if(!entity.getWalmartStatus().equals("4")) {
                            costAppliction.costPushService.updateStatus(entity.getCostNo(), "3");
                        }
                    }

                    //驳回
                    if("驳回到WAPP".equals(actionName)){
                        String msg = data.getString("msg");
                        costAppliction.costPushService.updateRebackInfo(entity.getCostNo(),msg);
                        costAppliction.costPushService.cancelMatch(entity.getCostNo());
                    }
                }
            }
            //查询是否有数据

            Map<String, Object> arg = new HashMap<String, Object>();
            arg.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
            arg.put("selectClause", "select b.* from BO_ACT_EPS_VENDOR_MAIN a join WFC_TASK b on a.BINDID=b.PROCESSINSTID where b.ACTIVITYDEFID='obj_c83c6f3e51f00001cbc116001b106fb0' and b.PROCESSINSTID='"+entity.getInstanceId()+"'");
            String re = client.exec("bo.query", arg);
            logger.info("审批流状态sql----:"+re);
            //1-审核通过
            JSONObject resObj1 = JSONObject.fromObject(re);
            Object datao=resObj1.get("data");
            if(datao!=null) {
                if (JSONObject.fromObject(re).getJSONArray("data").size() > 0) {
                    if(!entity.getWalmartStatus().equals("4")) {
                        costAppliction.costPushService.updateStatus(entity.getCostNo(), "1");
                    }
                    //审核通过的,需要从BPMS获取信息并更新
                    //主表信息
                    args.clear();
                    args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
                    args.put("querys", "[[\"bindId=\",\"" + entity.getInstanceId() + "\"]]");
                    String res2 = client.exec("bo.query", args);
                    JSONObject resData = JSONObject.fromObject(res2).getJSONArray("data").getJSONObject(0);
                    entity.setBelongsTo(resData.getString("MARTTYPE"));
                    entity.setServiceType(resData.getString("EPS_TYPE"));
                    entity.setPaymentMode(resData.getString("PAY_MODE"));
                    entity.setPayDay(resData.getString("PAY_DATE"));
                    entity.setContract(resData.getString("PO_ID"));
                    entity.setHasInvoice(resData.getString("HAS_INVOICE"));
                    entity.setUrgency(resData.getString("URGENCY_LEVEL"));
                    entity.setEpsNo(resData.getString("REQNO"));
                    entity.setRemark(resData.getString("REMARK"));
                    costAppliction.costPushService.updateMain(entity);
                    //费用表信息
                    List<CostEntity> costList = costAppliction.costPushService.getCostId(entity.getInstanceId());
                    for (CostEntity cost : costList) {
                        args.clear();
                        args.put("boName", "BO_ACT_EPS_VENDOR_DETAIL");
                        args.put("querys", "[[\"id=\",\"" + cost.getBpmsId() + "\"]]");
                        String res3 = client.exec("bo.query", args);
                        JSONObject resData3 = JSONObject.fromObject(res3).getJSONArray("data").getJSONObject(0);
                        cost.setCostTypeName(resData3.getString("TYPE"));
                        cost.setCostType(resData3.getString("TYPE_NAME"));
                        cost.setCostDept(resData3.getString("DEPARTMENT_ID"));
                        cost.setCostDeptId(resData3.getString("DEPARTMENT_NAME"));
                        cost.setCostTime(resData3.getString("TIME") + "至" + resData3.getString("TIME2"));
                        cost.setCostUse(resData3.getString("DESCRIPTION"));
                        cost.setProjectCode(resData3.getString("PROJECT_ID"));
                        costAppliction.costPushService.updateCost(cost);
                    }
                }
            }
            args.clear();
            //4-已付款状态
            String instance = entity.getInstanceId();
            args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
            String sql="SELECT m.* FROM BO_ACT_EPS_VENDOR_MAIN m left join BO_ACT_EPS_EAI_MAIN e on m.EPS_ID = e.EPS_ID where e.STATUS='3' and e.STATUS is not null and m.bindid='"+instance+"'";
            args.put("selectClause",sql);
            String res3 = client.exec("bo.query", args);
            JSONObject yfk = JSONObject.fromObject(res3);
            JSONArray yfkArr = yfk.getJSONArray("data");
            if(yfkArr.size()>0) {
                costAppliction.costPushService.updateStatus(entity.getCostNo(), "5");
                costAppliction.costPushService.updateRecord2Confirm(entity.getCostNo());
               //已付款更新抵账发票店号
                List<RecordInvoiceEntity> reList=costAppliction.costPushService.queryInvoicesByCostNos(entity.getCostNo());
                for (RecordInvoiceEntity ri:reList) {
                    List<CostEntity> costList = costAppliction.costPushService.getCostId(entity.getInstanceId());
                    for (CostEntity cost : costList) {
                        costAppliction.costPushService.getCostDeptId(cost.getCostDeptId(),ri.getInvoiceCode(),ri.getInvoiceNo());
                        return;
                    }
                }
            
            }
        }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    /**
     * 预付款补录发票流程提交申请
     */
    public void sendPrepayment(SettlementEntity mainEntity) throws Exception {
         //step.1 创建流程实例
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("processDefId", PROCESS_DEF_ID_PRE);
        String uids ="";
        if("029691".equals(mainEntity.getLoginName())){
            uids="h0h0lic";
        }else{
            uids = winid(mainEntity.getLoginName());
        }
        args.put("uid", uids);
        args.put("title", "补录发票推送("+mainEntity.getCostNo()+")");
        String res1 = client.exec("process.create", args);
        String instanceId = JSONObject.fromObject(res1).getJSONObject("data").getString("id");
        logger.info("-------获取实例ID：" + instanceId + "--------");


        //step.2 推送数据
        //主数据
        args.clear();
        args.put("uid", uids);
        args.put("boName", "BO_ACT_MAKEUP_INV");
        Map<String, Object> mainData = new HashMap<String, Object>();
        mainData.put("EPS_ID", mainEntity.getEpsId());
        mainData.put("EPS_TYPE", mainEntity.getServiceType());
        mainData.put("SUPPLIER_ID", mainEntity.getVenderId());//180250
        mainData.put("SUPPLIER_NAME", mainEntity.getVenderName());
        mainData.put("TOTAL_AMOUNT", mainEntity.getOldTotalAmount());
        mainData.put("OVERPLUS_AMOUNT", mainEntity.getSurplusAmount().add(mainEntity.getSettlementAmount()));
        mainData.put("NEW_AMOUNT", mainEntity.getSettlementAmount());
        mainData.put("OLD_BINDID", mainEntity.getOldBindId());
        mainData.put("COSTNO",mainEntity.getCostNo());
        args.put("recordData", mainData);
        args.put("bindId", instanceId);
        String res2 = client.exec("bo.create", args);
        logger.info("-------创建BO主表数据完成:" + res2 + "---------");
        JSONObject json = JSONObject.fromObject(res2);
        String boId = (String) json.get("data");
        //发票数据
        for (InvoiceRateEntity rate : mainEntity.getInvoiceRateList()) {
            args.clear();
            args.put("uid", uids);
            args.put("boName", "BO_ACT_MAKEUP_INV_SUB1");
            Map<String, Object> invoiceData = new HashMap<String, Object>();
            invoiceData.put("INV_NO", rate.getInvoiceNo());
//            invoiceData.put("INVOICE_CODE", rate.getInvoiceCode());
//            invoiceData.put("CHECK_CODE", rate.getCheckCode());
            invoiceData.put("INV_TYPE", "01".equals(rate.getInvoiceType()) ? "1" : "2");
            invoiceData.put("INV_TOTAL", rate.getInvoiceAmount());
            invoiceData.put("TAX_RATE", rate.getTaxRate());
            invoiceData.put("TAX_AMOUNT", rate.getTaxAmount());
            invoiceData.put("WITHOUT_RATE", rate.getInvoiceAmount().subtract(rate.getTaxAmount()));
            invoiceData.put("INV_DATE", rate.getInvoiceDate());
            invoiceData.put("OLD_BINDID", mainEntity.getOldBindId());
            invoiceData.put("APPLY_AMOUNT", rate.getInvoiceAmount());
            args.put("recordData", invoiceData);
            args.put("bindId", instanceId);
            String res2_1 = client.exec("bo.create", args);
            logger.info("-------创建BO发票表数据完成:" + res2_1 + "---------");
            JSONObject json_invoice = JSONObject.fromObject(res2_1);
            String InvoiceBoId = (String) json_invoice.get("data");
            //费用数据
            for (CostEntity cost : rate.getCostTableData()) {
                args.clear();
                args.put("uid", uids);
                args.put("boName", "BO_ACT_MAKEUP_VEN_MAIN");
                Map<String, Object> costData = new HashMap<String, Object>();
                costData.put("PAYMENT_TYPE", cost.getCostType());
                costData.put("PAYMENT_VAL", cost.getCostTypeName());
                costData.put("CHARGE_DEPT", cost.getCostDeptId());
                costData.put("CHARGE_VAL", costDeptGet(cost.getCostDept()));
                costData.put("PURPOSE", cost.getCostUse());
                String[] times = cost.getCostTime().split("至");
                if (times.length == 2) {
                    costData.put("TRANSACTION_DATE", times[0]);
                    costData.put("TO_DATE", times[1]);
                } else {
                    costData.put("TRANSACTION_DATE", "");
                    costData.put("TO_DATE", "");
                }
                costData.put("AMOUNT", cost.getCostAmount());
                costData.put("ADD_AMOUNT", cost.getCostAmount());
                costData.put("SURPLUS_AMOUNT", cost.getSurplusAmount());
                invoiceData.put("FEE_ID", cost.getBpmsId());
                invoiceData.put("OLD_BINDID", cost.getOldBindId());
                args.put("recordData", costData);
                args.put("bindId", InvoiceBoId);
                String res2_1_1 = client.exec("bo.create", args);
                logger.info("-------创建BO费用表数据完成:" + res2_1_1 + "---------");
            }
        }


        //step.3 启动流程
        args.clear();
        args.put("processInstId", instanceId);
        String res3 = client.exec("process.start", args);
        logger.info("-------启动流程:" + res3 + "---------");


        //step.4 获取任务实例
        args.clear();
        args.put("processInstId", instanceId);
        String res4 = client.exec("process.inst.get", args);
        String taskId = JSONObject.fromObject(res4).getJSONObject("data").getString("startTaskInstId");
        logger.info("-------获取任务实例:" + taskId + "---------");


        //step.5 推送附件
        try {
            handler.openChannel(costAppliction.host, costAppliction.userName, costAppliction.password, Integer.parseInt(costAppliction.defaultPort), Integer.parseInt(costAppliction.defaultTimeout));
            for (SettlementFileEntity fileEntity : mainEntity.getFileList()) {
                FormFileModel fileModel = new FormFileModel();
                fileModel.setAppId("com.actionsoft.apps.walmart.fin.process.inv");
                fileModel.setBoId(boId);
                fileModel.setBoItemName("NAME_FILE");
                fileModel.setBoName("BO_ACT_MAKEUP_INV");
                fileModel.setCreateDate(new Timestamp(System.currentTimeMillis()));
                fileModel.setCreateUser(UID);
                fileModel.setFileName(fileEntity.getFileName());
                fileModel.setProcessInstId(instanceId);
                fileModel.setRemark("");
                fileModel.setTaskInstId(taskId);

                UploadFile uploadFile = new UploadFile();
                uploadFile.setName(fileEntity.getFileName());


                handler.download(fileEntity.getFilePath(), fileEntity.getFileName());
                File file = new File(handler.getLocalImageRootPath() + fileEntity.getFileName());
                byte[] buffer = null;
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                buffer = bos.toByteArray();
                uploadFile.setContent(buffer);
                args.clear();
                args.put("formFileModel", fileModel);
                args.put("data", uploadFile);
                String res5 = client.exec("bo.file.up", args);
                logger.info("-------推送附件:" + res5 + "---------");

            }
        } catch (Exception e) {
            logger.error("推送附件异常,实例ID:" + instanceId + "," + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
        args.clear();
        args.put("taskInstId", taskId);
        args.put("uid",UID);
        String res = client.exec("task.complete", args);
        JSONObject  jsonnext = JSONObject.fromObject(res);
        String isok = jsonnext.getString("result");
        if("ok".equals(isok)){
            costAppliction.costPushService.updateStatus(mainEntity.getCostNo(), "6");
        }

    }
 /**
     *  调用api推送数据到BPMS发起付款流程
     */
    public void sendStatus(String instanceId,String costno){
        try {
        //通过流程实例id 获取任务id
        Map<String, Object> args = new HashMap<String, Object>();
        TaskQueryModel tqm = new TaskQueryModel();
        tqm.setProcessInstId(instanceId);
        args.put("tqm", tqm);
        String res4 = client.exec("task.query", args);
        System.out.println(res4);
        JSONArray array = JSONObject.fromObject(res4).getJSONArray("data");
        String taskId =""; //需确认是否会存在json数组情况
        if(array!=null&&array.size()>0){
            taskId = array.getJSONObject(0).getString("id");
        }
        else{
            return ;
        }
//        logger.info("-------获取任务实例:" + taskId + "---------");
        //推送下个节点
            args.clear();
            args.put("taskInstId", taskId);
            args.put("user","wapp");
            String actionName = "同意";
            Boolean isIgnoreDefaultSetting = true;
            args.put("actionName",actionName);
            args.put("isIgnoreDefaultSetting",isIgnoreDefaultSetting);
            String res = client.exec("task.comment.commit", args);
            JSONObject  json = JSONObject.fromObject(res);
            String isok = json.getString("result");
            if("ok".equals(isok)){
                args.clear();
                args.put("taskInstId", taskId);
                args.put("uid",costAppliction.UID);
                String res1 = client.exec("task.complete", args);
                JSONObject  json1 = JSONObject.fromObject(res1);
                String isok1 = json1.getString("result");
                System.out.println(res1);
                System.out.println(isok1);
                if("ok".equals(isok1)){
                    costAppliction.costPushService.updateStatus(costno, "4");
                    logger.info("推送待付款成功,实例ID:" + instanceId);
                }
            }
        }catch (Exception e){
            logger.error("推送待付款异常,实例ID:" + instanceId + "," + e);
        }
    }

    public String costDeptGet(String costDept){
        String zero = "";
        if (costDept.length() < 4) {
            int e = 4 - costDept.length();
            for (int i = 0; i < e; i++) {
                zero = zero + "0";
            }
            costDept =  zero + costDept;
        }
        return costDept;
    }


    public static String winid(String adNo) throws Exception {
        String param = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://Walmart.ChinaISD.HomeofficeTeam/HRMSService/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <hrm:GetEmployeeNO>" +
                "         <!--Optional:-->" +
                "         <hrm:ADNO>"+adNo+"</hrm:ADNO>" +
                "         <!--Optional:-->" +
                "         <hrm:LANGUAGE></hrm:LANGUAGE>" +
                "      </hrm:GetEmployeeNO>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        //String url="http://lcnnt51001.cn.wal-mart.com:8010/Web_HRMS.asmx";//测试接口
        String url2="http://ehrreport.cn.wal-mart.com/HRMSWebService/Web_HRMS.asmx";//正式接口
        String  repsont = HttpRequestUtils.doXMLCityPost(param, url2);  //请求接口
        String empNo = parseAdNoXml(repsont);
        String param2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://Walmart.ChinaISD.HomeofficeTeam/HRMSService/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <hrm:GetEmployeeWinID>" +
                "         <!--Optional:-->" +
                "         <hrm:EMPNO>"+empNo+"</hrm:EMPNO>" +
                "      </hrm:GetEmployeeWinID>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        String  repsont2 = HttpRequestUtils.doXMLCityPost(param2, url2);  //请求接口
        String winId = parseEmpNoXml(repsont2);
        if (!winId.equals("") || winId != null ){
            return winId;
        }
        return "wapp";

    }


    public static String parseAdNoXml(String repsont)  {
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetEmployeeNOResponse");
                while (itersElIterator.hasNext()) {
                    Element itemEle1 = (Element) itersElIterator.next();
                    return itemEle1.elementTextTrim("GetEmployeeNOResult"); //员工号
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String parseEmpNoXml(String repsont)  {
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetEmployeeWinIDResponse");
                while (itersElIterator.hasNext()) {
                    Element itemEle1 = (Element) itersElIterator.next();
                    return itemEle1.elementTextTrim("GetEmployeeWinIDResult"); //winid
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "wapp";
    }


}
