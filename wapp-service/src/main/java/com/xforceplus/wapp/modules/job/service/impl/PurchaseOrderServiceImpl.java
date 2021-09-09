package com.xforceplus.wapp.modules.job.service.impl;


import com.actionsoft.bpms.api.OpenApiClient;
import com.actionsoft.sdk.service.model.FormFileModel;
import com.actionsoft.sdk.service.model.UploadFile;
import com.actionsoft.sdk.service.response.process.ProcessInstResponse;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.job.dao.PurchaseOrderDao;
import com.xforceplus.wapp.modules.job.pojo.question.FilePo;
import com.xforceplus.wapp.modules.job.pojo.question.QuestionDetail;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.job.service.PurchaseOrderService;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 采购问题处理类
 */
@PropertySource(value = {"classpath:config.properties"})
@Service("purchaseOrderService")
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);
    @Autowired
    private PurchaseOrderDao puchaseOrderDao;
    @Value("${cg.interface.url}")
    private String apiServer;
    @Value("${cg.apiMethod}")
    private String apiMethod;
    @Value("${cg.accessKey}")
    private String accessKey;
    @Value("${cg.secret}")
    private String secret;
    
    private String requestno;
    @Value("${filePathConstan.remoteQuestionPaperFileTempRootPath}")
    private String localImageRootPath;
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


    private String processid = "";
    private String uid = "";
    private OpenApiClient client;
    private SFTPHandler handler;


    @PostConstruct
    public void init(){
        client = new OpenApiClient(apiServer, accessKey, secret);
        handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
    }

    /**
     *   采购问题单调用接口推送数据
     */
    public void executePush(){
        logger.info("-----------------推送BPMS采购问题单开始-------------");
        String processId = "";
        String taskId ="";
        int error = 0; //如果有错误，不进行状态更新
        //查询出符合推送的采购问题单
        List<QuestionPaperEntity> qlist = puchaseOrderDao.selectOrders();
        if(qlist.size()>0){
            //遍历查询出子集数据
            for (QuestionPaperEntity entity: qlist) {
                error = sendSingle(error, entity);
            }

        }
        logger.info("-----------------推送BPMS采购问题单结束-------------");
    }

    public int sendSingle(int error, QuestionPaperEntity entity) {
        String processId;
        Integer id = entity.getId();
        //创建流程实例ID
        processId = getProcessId();
        //执行对接接口的第二步
        String boId = sendMainData(entity,processId);//推送主数据返回ID
        if(!"".equals(boId)) {

            //通过id查询子数据
            List<QuestionDetail> detaillist = puchaseOrderDao.selectQuestionDetails(id);
            for(QuestionDetail detail :detaillist){
                Map map = entity2Map(detail,entity.getQuestionType());
                String detailboid = sendDetailData(map, processId);
                System.out.println("----detailboid-----"+detailboid);
                if("".equals(detailboid)){
                    error++;
                }
            }
            //从ftp获取附件信息， 传递附件信息
            List<FilePo> fileList = puchaseOrderDao.findFilePath(id);
            Map<String, Object> args = new HashMap<String, Object>();
            //step.3 启动流程
            args.clear();
            args.put("processInstId", processId);
            String res3 = client.exec("process.start", args);
            logger.info("-------启动流程:"+res3+"---------");


            //step.4 获取任务实例
            args.clear();
            args.put("processInstId", processId);
            ProcessInstResponse res4 = client.exec("process.inst.get", args, ProcessInstResponse.class);
            String  taskId = res4.getData().getStartTaskInstId();
            logger.info("-------获取任务实例:"+taskId+"---------");


            //step.5 推送附件
            try {
                handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                for (FilePo filePo :fileList) {
                    FormFileModel fileModel = new FormFileModel();
                    fileModel.setAppId("com.actionsoft.apps.walmart.fin.process.discrepancyrequest");
                    fileModel.setBoId(boId);
                    fileModel.setBoItemName("ATTACHMENT");
                    fileModel.setBoName("BO_ACT_DISCREPANCYREQUEST");
                    fileModel.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    fileModel.setCreateUser(uid);
                    fileModel.setFileName(filePo.getFileName());
                    fileModel.setProcessInstId(processId);
                    fileModel.setRemark(filePo.getRemark());
                    fileModel.setTaskInstId(taskId);

                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setName(filePo.getFileName());


                    handler.download(filePo.getSrc(), filePo.getFileName());
                    File file = new File(handler.getLocalImageRootPath() + filePo.getFileName());
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
                    try {
                        String res5 = client.exec("bo.file.up", args);
                        logger.info("-------推送附件:" + res5 + "---------");
                    }catch (Exception e){
                        String  res=  client.exec("bo.file.up", args);
                        logger.info("-------重新推送附件:" + res + "---------");
                    }
                }
            } catch (Exception e){
                logger.error("推送附件异常,实例ID:"+processId+","+e);
                e.printStackTrace();
            } finally {
                if (handler != null) {
                    handler.closeChannel();
                }
            }
            //更新数据库状态
            if(error==0) {
                puchaseOrderDao.updateStatus(id, processId);
            }
        }
        return error;
    }

    /**
     * 批量推送明细数据
     * @param mapList
     * @param processId
     * @return
     */
    public String sendDetailData(Map map, String processId) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("boName", "BO_ACT_DISCREPANCYDETAIL");
        args.put("recordData", map);
        args.put("bindId", processId);
        OpenApiClient client= new OpenApiClient(apiServer, accessKey, secret);
        String res2 = client.exec("bo.create", args);
        JSONObject json = JSONObject.fromObject(res2);
        String boId = (String)json.get("data");
        String result = (String)json.get("result");
        Boolean success = (Boolean)json.get("success");
        if(success&&"ok".equals(result)){
            return boId;
        }
        return "";
    }

    /**
     * 明细类转为map
     * @param detail
     * @param questionType
     * @return
     */
    private Map entity2Map(QuestionDetail detail, String questionType) {

        Map<String,String> map = new HashMap();
        map.put("PONO",detail.getPoCode());
        map.put("CLAIMSNO",detail.getClaimno());
        map.put("RECEIVENO",detail.getReceiptid());
        map.put("PRODUCTNO",detail.getGoodsNo());
        if(detail.getSystemAmount()!=null) {
            map.put("UNITSALEPRICE", detail.getSystemAmount().toString());
        }
        if(detail.getVendorAmount()!=null) {
            map.put("UNITVENDORPRICE", detail.getVendorAmount().toString());
        }
        if(detail.getNumbers()!=null) {
            map.put("QUANTITY", detail.getNumbers()+"");
        }
        if(detail.getDifferenceAmount()!=null) {
            if("2001".equals(questionType)||"2002".equals(questionType)||"2004".equals(questionType)||"2008".equals(questionType)){
                map.put("BALANCE",detail.getDifferenceAmount().toString());
            }else if("2003".equals(questionType)||"2007".equals(questionType)){
                map.put("NEWBALANCE",detail.getDifferenceAmount().toString());
            }
        }
        if(detail.getVendorNumber()!=null){
            map.put("VENDORSUPPLYQTY",detail.getVendorNumber()+"");
        }
        if(detail.getSystemNumber()!=null){
            map.put("SYSTEMRECQTY",detail.getSystemNumber()+"");
        }
//                        if(detail.getNumberDifference()!=null){
//
//                                map.put("NEWBALANCE",detail.getNumberDifference()+"");
//                        }

        return map;
    }

    /**
     * 推送主数据信息 -- 返回id
     * @param entity
     * @param processId
     * @return
     */
    private String sendMainData(QuestionPaperEntity entity, String processId) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("boName", "BO_ACT_DISCREPANCYREQUEST");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("DEPARTMENTNO",entity.getDepartment());
//                data.put("ENGLISHNAME","test1");
//                data.put("CHINESENAME","泰斯特依");
        data.put("DIVISIONNO",entity.getPartition());
        data.put("JV",entity.getJvcode());
        data.put("BUYERCITY",entity.getCity());
        data.put("VENDORNO",entity.getUsercode());
        data.put("VENDORNAME",entity.getUsername());
        data.put("VENDORCONTACTTEL",entity.getTelephone());
//                data.put("DISCREPANCYNO",entity.getQuestionType());
        data.put("DISCREPANCYNO",entity.getQuestionType()); //问题类型
        data.put("URGENTFLAG","1");
        data.put("BUYERAPPROVER",entity.getPurchaser());
//                data.put("APPROVETEMPLATENOSAVE",uid);
        data.put("REQSTATUS","Cancelled");
        data.put("REQUSERNO",requestno); //申请人工号
        //通过分区调用bpms接口获取问题类型编号信息
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("boName","BO_ACT_DISCREPANCYTYPE");
        map.put("selectClause","select REFERENCENO from BO_ACT_DISCREPANCYTYPE WHERE INFONO='"+1002+"'");
        String res1= client.exec("bo.query",map);
        JSONObject resData = JSONObject.fromObject(res1).getJSONArray("data").getJSONObject(0);
        data.put("DISCREPANCYSNO",resData.getString("REFERENCENO")); //问题类型编号
        data.put("REASONTEMPLATESNO",entity.getQuestionType()); //问题原因编号
        data.put("REASONCONTENT",entity.getProblemCause());//问题原因模板需要
        data.put("SOTRENO",entity.getStoreNbr());//门店信息
        data.put("REASONTEMPLATENO",entity.getProblemStream()+entity.getDescription());//问题描述
        data.put("INVOICENO",entity.getInvoiceNo());
        data.put("INVOICEDATE",entity.getInvoiceDate());
        data.put("BUYERNAMECN",entity.getPurchaser());
        data.put("REQUESTERDEPARTMENTNO",entity.getDepartment());
        args.put("recordData", data);
        args.put("bindId", processId);
        OpenApiClient client= new OpenApiClient(apiServer, accessKey, secret);
        String res2 = client.exec("bo.create", args); //调用接口
        JSONObject json = JSONObject.fromObject(res2);
        String boId = (String)json.get("data");
        String result = (String)json.get("result");
        Boolean success = (Boolean)json.get("success");
        if(success&&"ok".equals(result)){
            return boId;
        }
        return "";
    }

    /**
     * 创建采购问题单接口流程实例id
     * @return
     */
    private String getProcessId() {
        List<DictPo> list = puchaseOrderDao.selectParam();
        for(DictPo po : list){
            if("process_id".equals(po.getDictcode())){
                processid= po.getDictname();
            }
            if("uid".equals(po.getDictcode())){
                uid= po.getDictname();
            }
            if("requestno".equals(po.getDictcode())){
                requestno=po.getDictname();
            }
        }
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("processDefId", processid);
        args.put("uid", uid);
        args.put("title", "采购问题单");
        OpenApiClient client= new OpenApiClient(apiServer, accessKey, secret);
        ProcessInstResponse r = client.exec(apiMethod, args, ProcessInstResponse.class);
        String instanceId = r.getData().getId();
        return instanceId;
    }

}
