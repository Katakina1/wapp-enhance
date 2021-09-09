

package com.xforceplus.wapp.modules.job.service.impl;


import com.actionsoft.bpms.api.OpenApiClient;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.job.dao.AdvanceDao;
import com.xforceplus.wapp.modules.job.pojo.RecordInvoice;
import com.xforceplus.wapp.modules.job.pojo.advance.DetailData;
import com.xforceplus.wapp.modules.job.pojo.advance.InvoiceData;
import com.xforceplus.wapp.modules.job.pojo.advance.MainData;
import com.xforceplus.wapp.modules.job.pojo.advance.RecordInvoiceData;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.job.service.AdvanceService;
import com.xforceplus.wapp.modules.signin.service.impl.ScannerSignServiceImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 采购问题处理类
 */


@PropertySource(value = {"classpath:config.properties"})
@Service("advanceService")
public class AdvanceServiceImpl
 implements AdvanceService
{
        private static Logger logger = LoggerFactory.getLogger(AdvanceServiceImpl.class);
        private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        @Autowired
        private AdvanceDao advanceDao;
        @Autowired
        private CostAppliction costAppliction;
        @Value("${cg.interface.url}")
        private String apiServer;
        @Value("${cg.apiMethod}")
        private String apiMethod;
        @Value("${cg.accessKey}")
        private String accessKey;
        @Value("${cg.secret}")
        private String secret;
        private String requestno;
        @Value("${filePathConstan.localImageRootPath}")
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
        //设置获取时间，开始时间，结束时间
        private String beginTime = "";
        private String endTime = "";
        //设置带票付款获取时间，开始时间，结束时间
        private String beginTimes = "";
        private String endTimes = "";

        @PostConstruct
        public void init(){
                client = new OpenApiClient(apiServer, accessKey, secret);
                handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        }



/**
         * 获取预付款数据
         */


        @Override
        public void getDataFromBPMS() {
                // 获取时间段  , 从数据字典里获取
                List<DictPo> list  = advanceDao.findDict();
                getTime(list);
                // 获取数据
                Map<String, Object> args = new HashMap<String, Object>();
                //step.3 启动流程
                args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
                String sql="select m.* from BO_ACT_EPS_VENDOR_MAIN m left join BO_ACT_EPS_EAI_MAIN e on m.BINDID=e.BINDID left join (select i.EPS_ID,sum(NEW_AMOUNT) as totalamount from BO_ACT_MAKEUP_INV_SUB1 s left join BO_ACT_MAKEUP_INV i on s.bindid=i.bindid left join BO_ACT_EPS_EAI_MAIN e on i.BINDID=e.BINDID left join wfc_process p on i.bindid=p.id where (e.STATUS!=4 or e.STATUS is null) and (p.ext2 ='running' or p.ext2 ='completed') group by i.EPS_ID) a on a.EPS_ID=m.EPS_ID where e.STATUS='3' and m.TOTAL_AMOUNT>isnull(a.totalamount,0) AND m.HAS_INVOICE = 'false'";
               String addSql = "and e.RECEIVETIME>="+"'"+beginTime+"' and e.RECEIVETIME <="+"'"+endTime+"'";
                //String addSql="";
                String sqlall = sql+addSql;
                args.put("selectClause",sqlall);
                String res3 = client.exec("bo.query", args); //返回数据json
                JSONObject obj = JSONObject.fromObject(res3);
                String data = obj.getString("data");
                String result = obj.getString("result");
                boolean suc = (boolean)obj.get("success");
                if(suc&&"ok".equals(result)){
                        JSONArray main = JSONArray.fromObject(data);
                        List<MainData> mainDataList = JSONArray.toList(main,new MainData(),new JsonConfig());
                        if(mainDataList.size()>0){
                                logger.info("---------------预付款获取开始-------------------");
                                for(MainData mainData:mainDataList){

                                    //供应商号加0
                                    String no = mainData.getSUPPLIER_ID();
                                    String zero = "";
                                    if (no.length() < 6) {
                                        int e = 6 - no.length();
                                        for (int i = 0; i < e; i++) {
                                            zero = zero + "0";
                                        }
                                        no = zero + no;
                                    }
                                    mainData.setSUPPLIER_ID(no);

                                        //插入费用主数据
                                        String bindid = mainData.getBINDID();
                                        int count = advanceDao.findByBindID(bindid);//判断是否已经存在，如果存在更新，不存在则插入
                                        if(count>0){
                                                //执行更新
                                                advanceDao.updateAdvance(mainData);
                                        }else{
                                                //执行插入
                                                advanceDao.insertAdvance(mainData);
                                                //创建winID用户
                                                int counts = advanceDao.findByUser(mainData.getAD_ID());
                                                if(counts==0) {
                                                        advanceDao.insertUser(mainData.getAD_ID());
                                                        int userId = advanceDao.selectUserId(mainData.getAD_ID());
                                                        advanceDao.insertRole(userId);
                                                }
                                        }
                                        //请求费用明细数据
                                        args.clear();
                                        args.put("boName", "BO_ACT_EPS_VENDOR_DETAIL");
                                        args.put("querys","[[\"BINDID =\",\""+mainData.getBINDID()+"\"]]");
                                        String detailResponse = client.exec("bo.query", args); //返回明细数据
                                        JSONObject detailObj = JSONObject.fromObject(detailResponse);
                                        String detail = detailObj.getString("data");
                                        String detailResult = detailObj.getString("result");
                                        boolean detaiSuc = (boolean)detailObj.get("success");
                                        if(detaiSuc&&"ok".equals(detailResult)){
                                                JSONArray detailJSON = JSONArray.fromObject(detail);
                                                List<DetailData> detailDataList = JSONArray.toList(detailJSON,new DetailData(),new JsonConfig());
                                                List<List<DetailData>> detailList = groupList(detailDataList);
                                                //查询明细是否有值
                                                int detailCount = advanceDao.findDetail(bindid);
                                                if(detailCount>0){
                                                        advanceDao.deleteDetail(bindid);
                                                }
                                                for (int i = 0; i < detailList.size(); i++){
                                                    advanceDao.insertDetail(detailList.get(i));
                                                }

                                        }
                                }
                                logger.info("---------------预付款获取结束-------------------");
                        }
                }
        }

        private void getTime(List<DictPo> list) {
                if(list!=null&&list.size()!=0){

                        for (DictPo enttiy :list) {
                                if ("beginTime".equals(enttiy.getDictname())) {
                                        beginTime = enttiy.getDictcode();
                                }
                                if ("endTime".equals(enttiy.getDictname())) {
                                        endTime = enttiy.getDictcode();
                                }
                                if ("status".equals(enttiy.getDictname())){
                                        if("1".equals(enttiy.getDictcode())){
                                                //获取前一天的时间
                                                Date dNow = new Date();   //当前时间
                                                Date dBefore = new Date();

                                                Calendar calendar = Calendar.getInstance(); //得到日历
                                                calendar.setTime(dNow);//把当前时间赋给日历
                                                calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                                                dNow = calendar.getTime();   //得到前一天的时间
                                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                                                beginTime = sdf.format(dNow);    //格式化前一天
                                                endTime= sdf.format(dBefore);    //格式化前一天
                                        }
                                }
                        }
                }else{
                        //获取前一天的时间
                        Date dNow = new Date();   //当前时间
                        Date dBefore = new Date();

                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dNow);//把当前时间赋给日历
                        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                        dNow = calendar.getTime();   //得到前一天的时间
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                        beginTime = sdf.format(dNow);    //格式化前一天
                        endTime= sdf.format(dBefore);    //格式化前一天
                }


        }



        private void getTimes(List<DictPo> list) {
                if(list!=null&&list.size()!=0){

                        for (DictPo enttiy :list) {
                                if ("beginTimes".equals(enttiy.getDictname())) {
                                        beginTimes = enttiy.getDictcode();
                                }
                                if ("endTimes".equals(enttiy.getDictname())) {
                                        endTimes = enttiy.getDictcode();
                                }
                                if ("dpfkStatus".equals(enttiy.getDictname())){
                                        if("1".equals(enttiy.getDictcode())){
                                                //获取前一天的时间
                                                Date dNow = new Date();   //获取当天区间
                                                Date dBefore = new Date();//当前时间

                                                Calendar calendar = Calendar.getInstance(); //得到日历
                                                calendar.setTime(dNow);//把当前时间赋给日历
                                                calendar.add(Calendar.DATE, 1);  //设置为前一天
                                                dNow = calendar.getTime();   //得到前一天的时间
                                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                                                beginTime = sdf.format(dBefore);    //格式化前一天
                                                endTime= sdf.format(dNow);    //格式化前一天
                                        }
                                }
                        }
                }else{
                        //获取前一天的时间
                        Date dNow = new Date();   //获取当天区间
                        Date dBefore = new Date();//当前时间

                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dNow);//把当前时间赋给日历
                        calendar.add(Calendar.DATE, 1);  //设置为前一天
                        dNow = calendar.getTime();   //得到前一天的时间
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                        beginTime = sdf.format(dBefore);    //格式化前一天
                        endTime= sdf.format(dNow);    //格式化前一天
                }


        }


/**
 * 获取BPMS系统中已经审核通过的从非预付款数据
 * 从bpms发起的带票付款
 * */


        public void getDPFKFromBPMS(){

                // 获取时间段  , 从数据字典里获取
                List<DictPo> list  = advanceDao.findDict();
                getTimes(list);
                List<DictPo> lists  = advanceDao.findDpfk();
                logger.info("---------------bpms带票付款获取开始日期"+beginTime+"---结束日期:"+endTime+"----------------");
                // 获取数据
                Map<String, Object> args = new HashMap<String, Object>();
                //step.3 启动流程
                args.put("boName", "BO_ACT_EPS_VENDOR_MAIN");
                String sql="SELECT V.* FROM BO_ACT_EPS_VENDOR_MAIN V LEFT JOIN WFC_TASK T ON V.BINDID=T.PROCESSINSTID WHERE ACTIVITYDEFID='obj_c83c6f3e51f00001cbc116001b106fb0' AND (V.COSTNO IS NULL  OR V.COSTNO = '')";
                String addSql = "and T.BEGINTIME>="+"'"+beginTime+"' and T.BEGINTIME <="+"'"+endTime+"'";
//                String addSql = "";
                args.put("selectClause",sql+addSql);
                String res3 = client.exec("bo.query", args); //返回数据json
                JSONObject obj = JSONObject.fromObject(res3);
                String data = obj.getString("data");
                String result = obj.getString("result");
                boolean suc = (boolean)obj.get("success");
                if(suc&&"ok".equals(result)){
                        JSONArray main = JSONArray.fromObject(data);
                        List<MainData> mainDataList = JSONArray.toList(main,new MainData(),new JsonConfig());
                        if(mainDataList.size()>0) {
                                logger.info("---------------bpms带票付款获取成功-------------------");
                                for (MainData mainData : mainDataList) {
                                        //插入费用主数据
                                        //生成费用号
                                        String costno = sdf.format(new Date());
                                        String bindid = mainData.getBINDID();
                                        //供应商号加0
                                        String no = mainData.getSUPPLIER_ID();
                                        String zero = "";
                                        if (no.length() < 6) {
                                                int e = 6 - no.length();
                                                for (int i = 0; i < e; i++) {
                                                        zero = zero + "0";
                                                }
                                                no = zero + no;
                                        }
                                        mainData.setSUPPLIER_ID(no);
                                        int count = advanceDao.findCost(bindid);//判断是否已经存在，如果存在更新，不存在则插入
                                        if (count == 0) {
                                                //执行更新 --由于是审核完的数据，不会修改
                                                //执行插入
                                                advanceDao.insertCostMain(mainData, costno, "bpms");
                                        //请求费用发票数据
                                        args.clear();
                                        args.put("boName", "BO_ACT_INVOICE");
                                        args.put("querys", "[[\"BINDID =\",\"" + mainData.getBINDID() + "\"]]");
                                        String invoiceRespose = client.exec("bo.query", args); //返回明细数据
                                        JSONObject invoiceObj = JSONObject.fromObject(invoiceRespose);
                                        String invoicedata = invoiceObj.getString("data");
                                        String inoviceResult = invoiceObj.getString("result");
                                        boolean invoiceSuc = (boolean) invoiceObj.get("success");
                                        if (invoiceSuc && "ok".equals(inoviceResult)) {
                                                JSONArray detailJSON = JSONArray.fromObject(invoicedata);
                                                List<InvoiceData> invoiceList = JSONArray.toList(detailJSON, new InvoiceData(), new JsonConfig());
                                                Map<String, RecordInvoiceData> map = new HashMap();

                                                for (InvoiceData entity : invoiceList) {
                                                        RecordInvoiceData invoiceData = new RecordInvoiceData();
                                                        //!!!!!!!需整合发票信息，存抵账表，目前资料不全，暂不处理
                                                        //判断是否是多税率的
                                                        String invoiceNo = entity.getINVOICE_ID();
                                                        //校验发票类型，判断是否需要比对抵账表
                                                        String invoiceType = ScannerSignServiceImpl.getFplx(entity.getINVOICE_CODE());
                                                        invoiceData.setInvoiceType(invoiceType);
                                                        invoiceData.setVendorid(mainData.getSUPPLIER_ID());
                                                        invoiceData.setCheckCode(entity.getCHECK_CODE());
                                                        invoiceData.setInvoiceDate(entity.getINVOICE_DATE());
                                                        String invoiceno = invoiceNo;
                                                        if (invoiceNo != null && invoiceType != null) {
                                                                String invoiceCode = entity.getINVOICE_CODE();
                                                                invoiceData.setTaxAmount(new BigDecimal(entity.getTAX_AMOUNT()));
                                                                invoiceData.setTotalAmount(new BigDecimal(entity.getINVOICE_AMOUNT()));
                                                                invoiceData.setInvoiceCode(invoiceCode);
                                                                invoiceData.setInvoiceAmount(new BigDecimal(entity.getWITHOUT_TAX_AMOUNT()));
                                                               if (invoiceNo.length() > 8) {//目前发票号码invoiceno 如果拆分后面补00 ，01
                                                                       invoiceno = invoiceNo.substring(0, 8);
                                                               }
                                                                        invoiceData.setInvoiceNo(invoiceno);
                                                                        invoiceData.setUuid(invoiceData.getInvoiceCode() + invoiceno);
                                                                        if (map.containsKey(invoiceCode + invoiceno)) {
                                                                                RecordInvoiceData invoice = map.get(invoiceCode + invoiceno);
                                                                                if (entity.getWITHOUT_TAX_AMOUNT() != null) {
                                                                                        BigDecimal big = invoice.getInvoiceAmount().add(new BigDecimal(entity.getWITHOUT_TAX_AMOUNT()));//发票金额
                                                                                        invoice.setInvoiceAmount(big);
                                                                                }
                                                                                if (entity.getTAX_AMOUNT() != null) {
                                                                                        BigDecimal taxamount = invoice.getTaxAmount().add(new BigDecimal(entity.getTAX_AMOUNT()));//税额
                                                                                        invoice.setTaxAmount(taxamount);
                                                                                }
                                                                                if (entity.getAPPLY_AMOUNT() != null) {
                                                                                        BigDecimal totalamount = invoice.getTotalAmount().add(new BigDecimal(entity.getAPPLY_AMOUNT()));//价税合计
                                                                                        invoice.setTotalAmount(totalamount);
                                                                                }
                                                                                map.put(invoiceCode + invoiceno, invoice);
                                                                        } else {
                                                                                map.put(invoiceCode + invoiceno, invoiceData);
                                                                        }
//                                                                } else {
//                                                                        invoiceData.setInvoiceNo(invoiceNo);
//                                                                        invoiceData.setUuid(invoiceCode + invoiceNo);
//                                                                        map.put(invoiceData.getUuid(), invoiceData);
//                                                                }
                                                        }
                                                        String detailBindId = entity.getID();
//                                                       int invoicecount = advanceDao.findInoviceByNo(invoiceNo);
//                                                       if(invoicecount==0) {
                                                        int s = advanceDao.insertInvoice(entity, costno);
                                                        //得到发票存储id
                                                        String id = entity.getId();
                                                        //请求费用明细数据
                                                        args.clear();
                                                        args.put("boName", "BO_ACT_EPS_VENDOR_DETAIL");
                                                        args.put("querys", "[[\"BINDID =\",\"" + detailBindId + "\"]]");
                                                        String detailResponse = client.exec("bo.query", args); //返回明细数据
                                                        JSONObject detailObj = JSONObject.fromObject(detailResponse);
                                                        String detail = detailObj.getString("data");
                                                        String detailResult = detailObj.getString("result");
                                                        boolean detaiSuc = (boolean) detailObj.get("success");
                                                        if (detaiSuc && "ok".equals(detailResult)) {
                                                                JSONArray detailJson1 = JSONArray.fromObject(detail);
                                                                List<DetailData> detailDataList = JSONArray.toList(detailJson1, new DetailData(), new JsonConfig());
                                                                //查询明细是否有值
                                                                int detailCount = advanceDao.findCostDetail(bindid);
                                                                if (detailCount > 0) {
                                                                        advanceDao.deleteCostDetail(bindid);
                                                                }
                                                                advanceDao.insertBpmsDetail(detailDataList, id);
                                                                getDPFK(lists,detailDataList.get(0).getTYPE_NAME(),mainData.getBINDID(),costno);
//                                                                if(detailDataList.get(0).getTYPE_NAME().equals("5088") || detailDataList.get(0).getTYPE_NAME().equals("5089") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("5090") ||detailDataList.get(0).getTYPE_NAME().equals("5091") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("5092") ||detailDataList.get(0).getTYPE_NAME().equals("5093") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("5094") ||detailDataList.get(0).getTYPE_NAME().equals("5109") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("4091") ||detailDataList.get(0).getTYPE_NAME().equals("5113") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("5048") ||detailDataList.get(0).getTYPE_NAME().equals("5133") ||
//                                                                        detailDataList.get(0).getTYPE_NAME().equals("5078")){
//                                                                        costAppliction.sendStatus(mainData.getBINDID(),costno);
//                                                                }


                                                        }

//                                                       }


                                                }
                                                //插入发票数据，底账数据
                                                if (map != null && map.size() > 0) {
                                                        Set set = map.keySet();
                                                        Iterator iter = set.iterator();
                                                        while (iter.hasNext()) {
                                                                String uuid = (String) iter.next();
                                                                RecordInvoice record = advanceDao.findRecordInvoice(uuid);
                                                                RecordInvoiceData inoviceData = map.get(uuid);
                                                                if (record != null) {
                                                                    //不从BPMS取供应商号
                                                                    //advanceDao.updateVenderId(no,uuid);
                                                                        inoviceData.setGfTaxNo(record.getGfTaxNo());
                                                                        inoviceData.setGfName(record.getGfName());
                                                                } else {
                                                                        if ("01".equals(inoviceData.getInvoiceType()) || "04".equals(inoviceData.getInvoiceType())) {
                                                                                advanceDao.insertRecordInvoice(inoviceData);
                                                                        }
                                                                }
                                                                advanceDao.insertInvoiceMain(inoviceData, costno);
                                                                advanceDao.insertBindInvoice(inoviceData, costno);
                                                        }
                                                }
                                        }

                                }
                        }
                                logger.info("---------------bpms带票付款获取结束-------------------");
                        }

                }

       }


    /*
     * List分割
     */
    public static List<List<DetailData>> groupList(List<DetailData> list) {
        List<List<DetailData>> listGroup = new ArrayList<List<DetailData>>();
        int listSize = list.size();
        //子集合的长度
        int toIndex = 100;
        for (int i = 0; i < list.size(); i += 100) {
            if (i + 100 > listSize) {
                toIndex = listSize - i;
            }
            List<DetailData> newList = list.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }

        /***
         * 解析ADVANCE_DPFK费用类型
         * @param list
         */
        private void getDPFK(List<DictPo> list,String costType,String instanceId,String costno) {
                if (list != null && list.size() != 0) {
                        for (DictPo enttiy : list) {
                                if(costType.equals(enttiy.getDictcode())){
                                        costAppliction.sendStatus(instanceId,costno);
                                    logger.info("---------------发送bpms数据成功,费用类型:"+enttiy.getDictcode()+"-------------------");
                                }
                        }

                }
        }
}

