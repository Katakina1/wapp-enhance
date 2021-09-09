package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import com.xforceplus.wapp.modules.job.dao.ScanMatchDao;
import com.xforceplus.wapp.modules.job.entity.SettlementEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.SignInqueryDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntityApi;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@Service
@Transactional
public class SignInqueryServiceImpl implements SignInInqueryService {

    private SignInqueryDao signInqueryDao;
    @Autowired
    private ScanMatchDao scanMatchDao;

    @Autowired
    private CostAppliction costAppliction;

    @Autowired
    private CostPushService costPushService;

    @Autowired
    public SignInqueryServiceImpl(SignInqueryDao signInqueryDao) {
        this.signInqueryDao = signInqueryDao;
    }


    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return signInqueryDao.searchGf(schemaLabel,userId);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return signInqueryDao.queryTotal(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return signInqueryDao.getRecordIncoiceList(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params) {
        return signInqueryDao.queryAllList(schemaLabel,params);
    }

    @Override
    public Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query) {
        return signInqueryDao.getSumAmount(schemaLabel,query);
    }
    @Override
    public void ScanMatch(String schemaLabel,RecordInvoiceEntityApi recordInvoiceEntity){
        if(recordInvoiceEntity.getBilltypeCode().equals("1")){
            //商品
            //获取该放票匹配关系id
            Integer matchid =signInqueryDao.getMatchid(schemaLabel,recordInvoiceEntity.getInvoiceCode()+recordInvoiceEntity.getInvoiceNo());
            //根据匹配id 查询发票数据集合
            List<String> invoices = scanMatchDao.findInvoicesById(matchid);
            if(invoices.size()>0) { //如果有发票数据进行校验
                Map<String,String> map = checkInvoice(invoices);
                String status= map.get("status");
                if("2".equals(status)){
                    String reason = map.get("reason");
                    scanMatchDao.updatePoScanFailReason(matchid,status,reason);
                }else {
                    scanMatchDao.updatePoMatchById(matchid, status);
                }
                if("1".equals(status)){
                    //如果扫描匹配成功，需要在底账表中更新发票的扫描匹配状态
                    scanMatchDao.updateRecordInvoice(invoices,status);
                }
            }

        }else if(recordInvoiceEntity.getBilltypeCode().equals("2")){
            //费用
            //获取该放票匹配关系id
            SettlementEntity entity =signInqueryDao.getcostNo(schemaLabel,recordInvoiceEntity.getInvoiceCode(),recordInvoiceEntity.getInvoiceNo());
            String costNo = entity.getCostNo();
            String instanceId = entity.getInstanceId();
            List<String> invoices = scanMatchDao.getInvoiceByCostNo(costNo);
            if(invoices.size()>0) { //如果有发票数据进行校验
                Map<String,String> map = checkInvoice(invoices);
                String status= map.get("status");
                if("2".equals(status)){
                    String reason = map.get("reason");
                    scanMatchDao.updateCostFailReason(costNo,status,reason);
                }else {
                    scanMatchDao.updateCostMatchById(costNo, status);
                }
                if("1".equals(status)){
                    //如果扫描匹配成功，需要在底账表中更新发票的扫描匹配状态
                    scanMatchDao.updateRecordInvoice(invoices,status);
                    if(!"1".equals(entity.getPayModel())){
                        costAppliction.sendStatus(instanceId,costNo);
                    }else{
                        List<com.xforceplus.wapp.modules.cost.entity.SettlementEntity> costList = costPushService.getPushData(costNo);
                        com.xforceplus.wapp.modules.cost.entity.SettlementEntity settle = costList.get(0);
                        try {
                            costAppliction.sendPrepayment(settle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }else if(recordInvoiceEntity.getBilltypeCode().equals("3")){
            //外红
            //获取红票数据表
            List<RedTicketMatch>  list =  signInqueryDao.selectOuterRedInvoice(schemaLabel,recordInvoiceEntity.getInvoiceCode(),recordInvoiceEntity.getInvoiceNo());
            List<String> redNotice = new LinkedList<String>();
            if(list!=null){
                if(list.size()>0){
                    for(RedTicketMatch entity:list){
                        scanMatchDao.updateOutMatch(entity.getId());
                        String red = entity.getRedNoticeNumber();
                        redNotice.add(red);
                    }
                    scanMatchDao.updateRecordInvoiceByRedNotice(redNotice);
                }
            }
        }else if(recordInvoiceEntity.getBilltypeCode().equals("4")){
            //内红
            //获取匹配序列号
            String serialNumber =signInqueryDao.getSerialNumber(schemaLabel,recordInvoiceEntity.getInvoiceCode(),recordInvoiceEntity.getInvoiceNo());
            //根据序列号查询发票列表
            List<String> invoices  = scanMatchDao.selectInvoicesBySerialNumber(serialNumber);
            String reason = "";
            if(invoices.size()>0) { //如果有发票数据进行校验
                Map  map = checkInvoice(invoices);
                String status= map.get("status")+"";
                //获取内红匹配关系
                RedInvoiceData data=signInqueryDao.getRedInvoiceData(schemaLabel,serialNumber);
                data.setScanMatchStatus(status);
                if("2".equals(status)){
                    data.setScanFailReason(map.get("reason")+"");
                    scanMatchDao.updateStatusAndReason(data);
                }else {
                    scanMatchDao.updateMatchInfo(data);
                }
                if("1".equals(status)){
                    //如果扫描匹配成功，需要在底账表中更新发票的扫描匹配状态
                    scanMatchDao.updateRecordInvoice(invoices,status);
                }
            }
        }
    }

    @Override
    public Integer selectByuuid(String schemaLabel,String uuid,String id,String invoiceDate ,String invoiceAmount){
        return  signInqueryDao.selectInvoiceCount(schemaLabel,uuid,id,invoiceDate,invoiceAmount);
    }

    public Map<String,String> checkInvoice(List<String> list){
        // 统计list中底账表是否还有未签收的发票数据
        List<String> count =  scanMatchDao.getCountNotSign(list);
        Map<String,String> map = new HashMap();
        if (count.size() >0){
            if(count.size()==list.size()){
                map.put("status","0");
                return map; // 都是未签收发票  设置为未扫描匹配
            }else{
                StringBuffer sb = new StringBuffer();
                for(String invoiceNo : count){
                    sb.append(invoiceNo+",");
                }
                map.put("status","2");
                map.put("reason",sb.toString());
                return map; // 有部分已经签收，而且还有未签收的，设置为扫描匹配失败
            }
        }
        else{
            map.put("status","1");
            return map; // 没有未签收发票   设置为扫描匹配成功
        }


    }


    @Override
    public  List<RecordInvoiceExcelEntity> transformExcle(List<RecordInvoiceEntity> invoiceEntityList ) {
        List<RecordInvoiceExcelEntity> list2 = new ArrayList<>();
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < invoiceEntityList.size(); i++) {
            RecordInvoiceEntity recordInvoiceEntity = invoiceEntityList.get(i);
            RecordInvoiceExcelEntity entity1 = new RecordInvoiceExcelEntity();
            //序号
            entity1.setCell0( recordInvoiceEntity.getRownumber());
            //扫描日期
            entity1.setCell1( dateFormat2.format(recordInvoiceEntity.getCreateDate()));
            //扫描流水号
            entity1.setCell2( recordInvoiceEntity.getScanId());
            //文件类型
            entity1.setCell3( fileTypeResult(recordInvoiceEntity.getFileType()));
            //JV
            entity1.setCell4( recordInvoiceEntity.getJvCode());
            //公司代码
            entity1.setCell5( recordInvoiceEntity.getCompanyCode());
            //扫描描述
            entity1.setCell6( recordInvoiceEntity.getNotes());
            //供应商号
            entity1.setCell7( recordInvoiceEntity.getVenderid());

            //发票代码
            entity1.setCell8( recordInvoiceEntity.getInvoiceCode());
            //发票号码
            entity1.setCell9( recordInvoiceEntity.getInvoiceNo());
            //发票类型
            entity1.setCell10( invResult(recordInvoiceEntity.getInvoiceType()));
            //开票日期
            try {
                entity1.setCell11( dateFormat2.format(recordInvoiceEntity.getInvoiceDate()));
            } catch (Exception e) {
                entity1.setCell11( "");
            }
            //金额
            entity1.setCell12( formatBigDecimal(recordInvoiceEntity.getInvoiceAmount()));
            //税额
            entity1.setCell13( formatBigDecimal(recordInvoiceEntity.getTaxAmount()));
            //认证结果
            entity1.setCell14( rzhResult(recordInvoiceEntity.getRzhYesorno()));
            //认证日期
            entity1.setCell15( recordInvoiceEntity.getRzhDate() != null ? dateFormat2.format(recordInvoiceEntity.getRzhDate()) : "");
            //业务类型
            entity1.setCell16( flowTypeResult(recordInvoiceEntity.getFlowType()));
            //发票匹配状态
            entity1.setCell17( ScanMatchStatusResult(recordInvoiceEntity));
            //匹配失败原因
            entity1.setCell18(recordInvoiceEntity.getScanFailReason());
            //签收状态
            entity1.setCell19( qsResult(recordInvoiceEntity.getQsStatus()));
           //急票龄
            entity1.setJpl(formatJpl(recordInvoiceEntity.getJpl()));
            entity1.setBz(recordInvoiceEntity.getRemark());
            entity1.setGl(formatGl(recordInvoiceEntity.getGl()));
            list2.add(entity1);
        }
        return list2;
    }
    @Override
    public void updateGl(String schemaLabel, Map<String, Object> params){
        signInqueryDao.updateGl(schemaLabel,params);
    }
    private String ScanMatchStatusResult(RecordInvoiceEntity scanMatchStatus) {
        if("1".equals(scanMatchStatus.getFileType())){
            if("1".equals(scanMatchStatus.getScanMatchStatus())){
                return "匹配成功";
            }else if("2".equals(scanMatchStatus.getScanMatchStatus())){
                return "匹配失败";
            }else  {
                return "未匹配";
            }
        }else{
            return "—— ——";
        }
    }
    private String formatJpl(Integer jpl){
       if(jpl!=null){
           if(jpl>=330){
               return "是";
           }else{
               return "否";
           }
       }else{
           return "——";
       }

    }
    private String formatGl(String jpl){
        if(jpl!=null){
            if(jpl.equals("1")){
                return "SGA";
            }else if(jpl.equals("0")){
                return "否";
            }else if(jpl.equals("2")){
                return "IC";
            }else if(jpl.equals("3")){
                return "EC";
            }else if(jpl.equals("4")){
                return "RE";
            }else if(jpl.equals("5")){
                return "SR";
            }
        }else{
            return "——";
        }
        return "——";
    }
    private String flowTypeResult(String flowType) {
        if("1".equals(flowType)){
            return "商品";
        }else
        if("2".equals(flowType)){
            return "费用";
        }if("3".equals(flowType)){
            return "外红";
        }if("4".equals(flowType)){
            return "内红";
        }if("5".equals(flowType)){
            return "供应商红票";
        }if("6".equals(flowType)){
            return "租赁";
        }
        if("7".equals(flowType)) {
            return "直接认证";
        }
        if("8".equals(flowType)){
            return "Ariba";
        }
        return "";
    }

    private String formatBigDecimal(BigDecimal val){
        if(val!=null){
            return String.valueOf(new DecimalFormat("#,##0.00").format(val));
        }
        return null;
    }
    private String rzhResult(String code){
        String value=null;
        if("1".equals(code)){
            value="已认证";
        }else{
            value="未认证";
        }
        return value;
    }


    private String fileTypeResult(String code){
        String value=null;
        if("1".equals(code)){
            value="发票";
        }else if("2".equals(code)){
            value="附件";
        }else if("3".equals(code)){
            value="封面";
        }else{
            value=null;
        }
        return value;
    }
    private String qsResult(String code){
        String value=null;
        if("0".equals(code)){
            value="签收失败";
        }else if("1".equals(code)){
            value="签收成功";
        }else{
            value=null;
        }
        return value;
    }
    //0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传
    private String qsType(String code){
        String val=null;
        if(code!=null){
            switch (code){
                case"4" : val="手工签收"; break;
                case"2": val="app签收"; break;
                case"0": val="扫码签收"; break;
                case"1": val="扫描仪签收"; break;
                case"3": val="导入签收"; break;
                case"5": val="pdf上传"; break;
                default: val=null; break;
            }
            return val;
        }
        return null;
    }
    private String invResult(String code){
        String value=null;
        if("01".equals(code)){
            value="专票";
        }else if("04".equals(code)){
            value="普票";
        }else if("11".equals(code)){
            value="卷票";
        }else{
            value=null;
        }
        return value;
    }
}
