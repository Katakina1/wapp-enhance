package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.cost.entity.InvoiceRateEntity;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import com.xforceplus.wapp.modules.job.dao.ScanMatchDao;
import com.xforceplus.wapp.modules.job.entity.SettlementEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.SignInqueryCostDao;
import com.xforceplus.wapp.modules.signin.dao.SignInqueryDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntityApi;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryCostService;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@Service
@Transactional
public class SignInqueryCostServiceImpl implements SignInInqueryCostService {

    private SignInqueryCostDao signInqueryCostDao;
    @Autowired
    private ScanMatchDao scanMatchDao;

    @Autowired
    private CostAppliction costAppliction;

    @Autowired
    private CostPushService costPushService;

    @Autowired
    public SignInqueryCostServiceImpl(SignInqueryCostDao signInqueryCostDao) {
        this.signInqueryCostDao = signInqueryCostDao;
    }


    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return signInqueryCostDao.searchGf(schemaLabel,userId);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return signInqueryCostDao.queryTotal(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return signInqueryCostDao.getRecordIncoiceList(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params) {
        return signInqueryCostDao.queryAllList(schemaLabel,params);
    }

    @Override
    public Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query) {
        return signInqueryCostDao.getSumAmount(schemaLabel,query);
    }
    @Override
    public void ScanMatch(String schemaLabel,RecordInvoiceEntityApi recordInvoiceEntity){
        if(recordInvoiceEntity.getBilltypeCode().equals("2")){
            //费用
            //获取该放票匹配关系id
            SettlementEntity entity =signInqueryCostDao.getcostNo(schemaLabel,recordInvoiceEntity.getInvoiceCode(),recordInvoiceEntity.getInvoiceNo());
            String costNo = entity.getCostNo();
            String instanceId = entity.getInstanceId();
            //获取该费用单号下的所有发票
            List<com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity> settlementInvoices = scanMatchDao.findInvoicesByCostNo(costNo);
            if(settlementInvoices!=null){
                if(settlementInvoices.size()>0){
                    //继续执行
                    //查看扫描表里的数据是否一致
                    List<com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity> scanList = scanMatchDao.findScanInvoices(costNo);
                    //比较两个list的数据是否一致
                    StringBuffer failReason = new StringBuffer();
                    List<String> invoices = new LinkedList();
                    for(com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity rie : settlementInvoices){
                        invoices.add(rie.getInvoiceCode()+rie.getInvoiceNo());
                        if(!checkIsRight(scanList,rie)){
                            failReason.append(rie.getInvoiceNo()+",");
                        }
                    }
                    if(settlementInvoices.size()==scanList.size()){
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
                        }else{
                            //扫描表中的数据和发票不一致，扫描匹配失败
                            scanMatchDao.updateCostFailReason(costNo,"2","未扫描成功发票号码："+failReason.toString());
                        }
                    }else{
                        //数量差异，提示具体少哪些发票数据
                        scanMatchDao.updateCostFailReason(costNo,"2","未扫描成功发票号码："+failReason.toString());
                    }
                }else {
                    //设置为扫描匹配失败
                    scanMatchDao.updateCostFailReason(costNo,"2","只有普通发票，需人工审核");
                }
            }
        }
    }

    @Override
    public Integer selectByuuid(String schemaLabel ,String uuid,String id,String invoiceDate ,String invoiceAmount) {
        return  signInqueryCostDao.selectInvoiceCount(schemaLabel,uuid,id,invoiceDate,invoiceAmount);
    }
    /**
     * 更新底账表扫描匹配失败
     * @param list
     * @param reason
     */
    public void updateRecordReason( List<String> list,String reason){
        scanMatchDao.updateRecordReason(list,reason);
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
    private boolean checkIsRight(List<com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity> list , com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity entity){
        if(list.size()>0){
            for(com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity invoice:list){
                if(invoice.getInvoiceCode().equals(entity.getInvoiceCode())&&invoice.getInvoiceNo().equals(entity.getInvoiceNo())&&invoice.getInvoiceType().equals(entity.getInvoiceType())){
                    return true;
                }
            }
            return false;
        }
        return false;
    }

	/**   
	 * <p>Title: deleteDateCost</p>   
	 * <p>Description: 删除操作</p>   
	 * @param shemaLable
	 * @param costNo
	 * @return   
	 * @see com.xforceplus.wapp.modules.signin.service.SignInInqueryCostService#deleteDateCost(java.lang.String, java.lang.String)
	 */  
	@Override
	public Boolean deleteDateCost(String shemaLable, String costNo) {
		//根据组关系删除扫描表中的数据，更新扫描匹配状态为未匹配
		Boolean deleteDateInvoice = deleteDateInvoice(shemaLable, costNo);
		if(deleteDateInvoice) {
			Boolean updateScanMatchStatus = updateScanMatchStatus(shemaLable, costNo, "0");
			if(updateScanMatchStatus) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

    /***
     * 删除扫描表发票
     * @param shemaLable
     * @param scanId
     * @return
     */
    @Override
    public Boolean deleteScanDate(String shemaLable, String scanId) {
        //根据组关系删除扫描表中的数据，更新扫描匹配状态为未匹配
        int deleteDateInvoice = signInqueryCostDao.deleteScanInvoice(shemaLable, scanId);
            if(deleteDateInvoice == 1) {
                return true;
            }else {
                return false;
            }
    }
    @Override
     public void underWay(String costNo){
        signInqueryCostDao.underWay(costNo);
    }


	/**   
	 * <p>Title: confirmDateCost</p>   
	 * <p>Description: 更新扫描匹配状态为匹配成功</p>   
	 * @param shemaLable
	 * @param costNo
	 * @return   
	 * @see com.xforceplus.wapp.modules.signin.service.SignInInqueryCostService#confirmDateCost(java.lang.String, java.lang.String)
	 */  
	@Override
	public Boolean confirmDateCost(String shemaLable, String costNo) {
		Boolean updateScanMatchStatus = updateScanMatchStatus(shemaLable, costNo, "1");
		if(updateScanMatchStatus) {
            com.xforceplus.wapp.modules.cost.entity.SettlementEntity entity = scanMatchDao.getSingleSettle(costNo);
            if(!"1".equals(entity.getPayModel())){
                String instanceId = entity.getInstanceId();
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
			return true;
		}else {
			return false;
		}
	}

    /***
     * 福利发票确认不进手工认证
     * @param shemaLable
     * @param costNo
     * @return
     */
    @Override
    public Boolean confirmDateCosts(String shemaLable, String costNo) {
        Boolean updateScanMatchStatus = updateScanMatchStatus(shemaLable, costNo, "1");
        if(updateScanMatchStatus) {
            com.xforceplus.wapp.modules.cost.entity.SettlementEntity entity = scanMatchDao.getSingleSettle(costNo);
            if(!"1".equals(entity.getPayModel())){
                String instanceId = entity.getInstanceId();
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
            signInqueryCostDao.updateInvoice(costNo, "1");
            return true;
        }else {
            return false;
        }
    }
	/**
	 * 根据组关系删除扫描表信息
	 * @Title: deleteDateInvoice   
	 * @Description: TODO
	 * @param: @param shemaLable
	 * @param: @param costNo
	 * @param: @return      
	 * @return: Boolean      
	 * @throws
	 */
	@Transactional
	public Boolean deleteDateInvoice(String shemaLable,String costNo) {
		try {
			signInqueryCostDao.deleteInvoiceDate(costNo);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 更新抵账表中扫描匹配状态 和 抵账表中的扫描匹配状态
	 * @Title: updateScanStatus   
	 * @Description: TODO
	 * @param: @param shemaLable
	 * @param: @param costNo
	 * @param: @return      
	 * @return: Boolean      
	 * @throws
	 */
	@Transactional
	public Boolean updateScanMatchStatus(String shemaLable,String costNo,String scanMatchStatus) {
		try {
            List<InvoiceRateEntity> invoice = signInqueryCostDao.updateRecord(costNo);
            for (InvoiceRateEntity list : invoice){
                signInqueryCostDao.updateRecordScanMatchStatus(list.getInvoiceCode()+list.getInvoiceNo(), scanMatchStatus);
            }
			//signInqueryCostDao.updateSettlementScanMatchStatus(costNo, scanMatchStatus);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    @Override
    public int selectInvoice(String costNo){
       return signInqueryCostDao.selectInvoice(costNo);
    }

    @Override
    public int checkInvoiceZP(String costNo){
        return signInqueryCostDao.checkInvoiceZP(costNo);
    }
}
