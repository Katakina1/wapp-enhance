package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import com.xforceplus.wapp.modules.job.dao.ScanMatchDao;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoice;
import com.xforceplus.wapp.modules.job.pojo.InvoicePo;
import com.xforceplus.wapp.modules.job.pojo.RecordInvoice;
import com.xforceplus.wapp.modules.job.service.ScanMatchService;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ScanMatchServiceImpl implements ScanMatchService {
    @Autowired
    private ScanMatchDao scanMatchDao;

    @Autowired
    private CostAppliction costAppliction;

    @Autowired
    private CostPushService costPushService;

    /**
     * 查询需要走扫描匹配校验操作的数据
     */
    @Override
    public void innerRedInvoice() {
        //获取符合条件的数据集合   amount_match_yesorno = 0  scan_match_status in 0 2
           List<RedInvoiceData> list =  scanMatchDao.getAllDatas();
           if(list!=null){
               if(list.size()>0){
                    for(RedInvoiceData data : list){
                        //获取序列号
                        String serialNumber = data.getSerialNumber();
                        //根据序列号查询发票列表
                        List<String> invoices  = scanMatchDao.selectInvoicesBySerialNumber(serialNumber);
                        String reason = "";
                        if(invoices.size()>0) { //如果有发票数据进行校验
                            Map  map = checkInvoice(invoices);
                            String status= map.get("status")+"";
                            data.setScanMatchStatus(status);
                            if("2".equals(status)){
                                reason = map.get("reason")+"";
                                data.setScanFailReason(reason);
                                scanMatchDao.updateStatusAndReason(data);
                                scanMatchDao.updateRecordReason(invoices,reason);
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
           }
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


    /**
     * 查询外部红票的未扫描匹配，扫描匹配失败的数据
     */
    @Override
    public void outerRedInvoice() {
         List<RedTicketMatch>  list =  scanMatchDao.selectOuterRedInvoice();
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
    }

    /**
     * 查询po索赔匹配信息数据，未扫描匹配，扫描匹配失败的数据
     */
    @Override
    public void posuopeiInvoice() {
            //获取符合匹配信息的数据    部分匹配， 完全匹配，差异匹配
           List<MatchEntity>  list = scanMatchDao.findpoMatchDatas();
        if(list!=null){
            if(list.size()>0){
                for(MatchEntity entity : list){
                   Integer id =  entity.getId();
                    //根据匹配id 查询发票数据集合
                    List<String> invoices = scanMatchDao.findInvoicesById(id);
                    if(invoices.size()>0) { //如果有发票数据进行校验
                        Map<String,String> map = checkInvoice(invoices);
                        String status= map.get("status");
                        if("2".equals(status)){
                            String reason = map.get("reason");
                            scanMatchDao.updatePoScanFailReason(id,status,reason);
                        }else {
                            scanMatchDao.updatePoMatchById(id, status);
                        }
                        if("1".equals(status)){
                            //如果扫描匹配成功，需要在底账表中更新发票的扫描匹配状态
                            scanMatchDao.updateRecordInvoice(invoices,status);
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询预付款费用匹配信息，未扫描匹配，扫描匹配失败的数据
     */
    @Override
    public void costInvoice() {
        // 获取费用的匹配数据，还没有扫描匹配成功的
        List<SettlementEntity> list =  scanMatchDao.findNotScanCost();
        if(list!=null){
            if(list.size()>0){
                for(SettlementEntity entity : list){
                    String costNo = entity.getCostNo();
                    String instanceId = entity.getInstanceId();
                    String venderId = entity.getVenderId();
                    List<RecordInvoiceEntity> scanListAll = scanMatchDao.findScanInvoices(costNo);
                    //获取该费用单号下的所有发票
                    List<RecordInvoiceEntity> settlementInvoices = scanMatchDao.findInvoicesByCostNo(costNo);
                    if(settlementInvoices!=null){
                        if(settlementInvoices.size()>0){
                            //继续执行
                            //查看扫描表里的数据是否一致
                            List<RecordInvoiceEntity> scanList = scanMatchDao.findScanInvoices(costNo);
                            //比较两个list的数据是否一致
                            StringBuffer failReason = new StringBuffer();
                            List<String> invoices = new LinkedList();

                            //比较供应商数据是否一致
                            StringBuffer venderFailReason = new StringBuffer();
                            List<String> venderList = new LinkedList();

                            for(RecordInvoiceEntity vender : scanList) {
                                if((vender.getInvoiceType().equals("01")||vender.getInvoiceType().equals("04"))){

                                if(!venderId.equals(vender.getVenderid())){
                                    venderFailReason.append(vender.getInvoiceNo()+",");
                                }
                                }
                            }
                            for(RecordInvoiceEntity rie : settlementInvoices){
                                venderList.add(rie.getVenderid());
                                invoices.add(rie.getInvoiceCode()+rie.getInvoiceNo());
                                if(!checkIsRight(scanList,rie)){
                                    failReason.append(rie.getInvoiceNo()+",");
                                }

                            }
                            if(settlementInvoices.size()==scanList.size()){
                                if(venderFailReason.length()==0){

                                if(failReason.length()==0){
                                    //数据一致 -- 继续执行，校验是否已经签收成功
                                    if(invoices.size()>0) { //如果有发票数据进行校验
                                        Map<String,String> map = checkInvoice(invoices);
                                        String status= map.get("status");
                                        if("2".equals(status)){
                                            String reason = map.get("reason");
                                            scanMatchDao.updateCostFailReason(costNo,status,reason);
                                            updateRecordReason(invoices,reason);
                                        }
                                        scanMatchDao.updateCostMatchById(costNo, status);
                                        if("1".equals(status)){
                                            //如果扫描匹配成功，需要在底账表中更新发票的扫描匹配状态
                                            scanMatchDao.updateRecordInvoice(invoices,status);
                                            if(!"1".equals(entity.getPayModel())){
                                                costAppliction.sendStatus(instanceId,costNo);
                                            }else{
                                                List<SettlementEntity> costList = costPushService.getPushData(costNo);
                                                SettlementEntity settle = costList.get(0);
                                                try {
                                                    costAppliction.sendPrepayment(settle);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    //扫描表中的数据和发票不一致，扫描匹配失败
                                    scanMatchDao.updateCostFailReason(costNo,"2","未扫描成功发票号码："+failReason.toString());
                                    for (RecordInvoiceEntity scanLists : scanListAll){
                                        scanMatchDao.updateCostScan(scanLists.getInvoiceCode()+scanLists.getInvoiceNo(),"2","未扫描成功发票号码："+failReason.toString());
                                    }

                                }
                            }else{
                                //扫描表中的数据和发票不一致，扫描匹配失败
                                scanMatchDao.updateCostFailReason(costNo,"2","供应商未匹配成功发票号码："+venderFailReason.toString());
                                    for (RecordInvoiceEntity scanLists : scanListAll){
                                        scanMatchDao.updateCostScan(scanLists.getInvoiceCode()+scanLists.getInvoiceNo(),"2","供应商未匹配成功发票号码："+venderFailReason.toString());
                                    }

                            }

                            }else{
                                //数量差异，提示具体少哪些发票数据
                                scanMatchDao.updateCostFailReason(costNo,"2","未扫描成功发票号码："+failReason.toString());
                                for (RecordInvoiceEntity scanLists : scanListAll){
                                    scanMatchDao.updateCostScan(scanLists.getInvoiceCode()+scanLists.getInvoiceNo(),"2","未扫描成功发票号码："+failReason.toString());
                                }

                            }
                        }else {
                            //设置为扫描匹配失败
                            scanMatchDao.updateCostFailReason(costNo,"2","只有普通发票或附件，需人工审核");
                            for (RecordInvoiceEntity scanLists : scanListAll){
                                scanMatchDao.updateCostScan(scanLists.getInvoiceCode()+scanLists.getInvoiceNo(),"2","只有普通发票或附件，需人工审核");
                            }

                        }
                    }
                }
            }
        }
    }


    /**
     * 更新底账表扫描匹配失败
     * @param list
     * @param reason
     */
    public void updateRecordReason( List<String> list,String reason){
        scanMatchDao.updateRecordReason(list,reason);
    }


    private boolean checkIsRight(List<RecordInvoiceEntity> list ,RecordInvoiceEntity entity){
        if(list.size()>0){
            for(RecordInvoiceEntity invoice:list){
                if(invoice.getInvoiceCode().equals(entity.getInvoiceCode()) && invoice.getInvoiceNo().equals(entity.getInvoiceNo()) && invoice.getInvoiceType().equals(entity.getInvoiceType()) &&
                        invoice.getTaxAmount().equals(entity.getTaxAmount()) && invoice.getInvoiceAmount().equals(entity.getInvoiceAmount())
                        && invoice.getInvoiceDate().equals(entity.getInvoiceDate())){
                    return true;
                }
            }
            return false;
        }
        return false;
    }

//    private boolean venderCheckIsRight(List<RecordInvoiceEntity> list ,RecordInvoiceEntity entity){
//        if(list.size()>0){
//            for(RecordInvoiceEntity invoice:list){
//                if(invoice.getVenderid().equals(entity.getVenderid())&&(invoice.getInvoiceType().equals("01")||invoice.getInvoiceType().equals("04"))){
//                    return true;
//                }else if (invoice.getInvoiceType().equals("02")){
//                    return true;
//                }
//            }
//            return false;
//        }
//        return false;
//    }
}
