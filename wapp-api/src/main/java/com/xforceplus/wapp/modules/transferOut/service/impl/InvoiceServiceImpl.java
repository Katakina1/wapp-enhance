package com.xforceplus.wapp.modules.transferOut.service.impl;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:05
*/

import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.common.validator.ValidatorUtils;
import com.xforceplus.wapp.modules.transferOut.dao.InvoiceDao;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceDao invoiceDao;

    /**
     * 查询抵账表信息 已认证 未转出
     * @param schemaLabel
     * @param map
     * @return
     */
    @Override
    public List<InvoiceEntity> transferOutQuery(String schemaLabel,Map<String, Object> map) {
        return invoiceDao.transferOutQuery(schemaLabel,map);
    }

    /**
     * 查询总数 已认证 未转出
     * @param schemaLabel
     * @param map
     * @return
     */
    @Override
    public int transferOutQueryTotal(String schemaLabel,Map<String, Object> map) {
        return invoiceDao.transferOutQueryTotal(schemaLabel,map);
    }

    /**
     * 已转出查询
     * @param schemaLabel
     * @param map
     * @return
     */
    @Override
    public List<InvoiceEntity> transferOutedQuery(String schemaLabel,Map<String, Object> map) {
        return invoiceDao.transferOutedQuery(schemaLabel,map);
    }

    /**
     * 已转出查询的信息总数
     * @param schemaLabel
     * @param map
     * @return
     */
    @Override
    public int transferOutedQueryTotal(String schemaLabel,Map<String, Object> map) {
        return invoiceDao.transferOutedQueryTotal(schemaLabel,map);
    }

    @Override
    public int setTransferOut(String schemaLabel, String ids, String outRemark, String outReason, String outTaxAmount, String outInvoiceAmout, String outStatus,String outBy) {
        InvoiceEntity invoiceEntity=null;
        int tempFlag=0;//判断是否设置成功
        if (outStatus.equals("2")){
            invoiceEntity=new InvoiceEntity();
            invoiceEntity.setOutBy(outBy);
            invoiceEntity.setId(Long.parseLong(ids));
            invoiceEntity.setOutStatus(outStatus);
            invoiceEntity.setOutRemark(outRemark);
            invoiceEntity.setOutReason(outReason);
            invoiceEntity.setOutTaxAmount(Double.parseDouble(outTaxAmount));
            invoiceEntity.setOutInvoiceAmout(Double.parseDouble(outInvoiceAmout));
            ValidatorUtils.validateEntity(invoiceEntity);
            tempFlag = invoiceDao.setTransferOut(schemaLabel,invoiceEntity);
            saveOutHis(schemaLabel, invoiceEntity);
        }else{
            final String[] id = ids.split(",");
            for (String anId : id){
                invoiceEntity=getToOutInformationAll(schemaLabel,anId);
                invoiceEntity.setOutBy(outBy);
                invoiceEntity.setId(Long.parseLong(anId));
                invoiceEntity.setOutStatus(outStatus);
                invoiceEntity.setOutRemark(outRemark);
                invoiceEntity.setOutReason(outReason);
                ValidatorUtils.validateEntity(invoiceEntity);
                tempFlag = invoiceDao.setTransferOut(schemaLabel,invoiceEntity);
                saveOutHis(schemaLabel, invoiceEntity);
                if (!(tempFlag>0)){
                    return -1;//设置失败
                }
            }
        }
        return tempFlag;
    }

    /**
     * 保存转出历史表,并更新转出金额及状态
     * @param schemaLabel
     * @param invoiceEntity
     */
    public void saveOutHis(String schemaLabel, InvoiceEntity invoiceEntity){
        //获取发票信息
        final InvoiceEntity invoice = invoiceDao.getTransferOutById(schemaLabel, invoiceEntity.getId());

        //设置发票代码,号码,uuid
        final String invoiceCode = invoice.getInvoiceCode();
        final String invoiceNo = invoice.getInvoiceNo();
        final String uuid = invoiceCode + invoiceNo;
        invoiceEntity.setInvoiceCode(invoiceCode);
        invoiceEntity.setInvoiceNo(invoiceNo);
        invoiceEntity.setUuid(uuid);

        //保存转出历史
        invoiceDao.saveOutHis(schemaLabel, invoiceEntity);

        //获取已转出的总金额税额
        final InvoiceEntity outInvoice = invoiceDao.getTotalOutAmount(schemaLabel, uuid);
        outInvoice.setId(invoiceEntity.getId());
        //比较数值,决定主表的转出状态
        if(invoice.getInvoiceAmount().equals(outInvoice.getOutInvoiceAmout()) && invoice.getTaxAmount().equals(outInvoice.getOutTaxAmount())){
            //全部转出
            outInvoice.setOutStatus("1");
        }else{
            //部分转出
            outInvoice.setOutStatus("2");
        }
        //更新主表
        invoiceDao.updateOutMain(schemaLabel, outInvoice);
    }

    /**
     * 获取税款所属期
     * @param schemaLabel
     * @param gfTaxNo
     * @return
     */
    @Override
    public String getDqskssq(String schemaLabel,String gfTaxNo) {
        return invoiceDao.getDqskssq(schemaLabel,gfTaxNo);
    }

    /**
     * 模糊查询销方名称
     * @param schemaLabel
     * @param queryString
     * @return
     */
    @Override
    public List<String> getXfName(String schemaLabel,String queryString) {
        return invoiceDao.getXfName(schemaLabel,queryString);
    }

    /**
     * 转出窗口待转出信息查询
     * @param schemaLabel
     * @param ids
     * @return
     */
    @Override
    public InvoiceEntity getToOutInformation(String schemaLabel,String  ids) {
        InvoiceEntity invoiceEntity=new InvoiceEntity();
        BigDecimal tempInvoiceAmount=new BigDecimal("0.0");
        BigDecimal tempTaxAmount=new BigDecimal("0.0");
        final String[] id = ids.split(",");
        for (String anId : id) {
            InvoiceEntity entity=invoiceDao.getToOutInformation(schemaLabel,anId);
            tempInvoiceAmount=tempInvoiceAmount.add(new BigDecimal(entity.getInvoiceAmount().toString()));
            tempTaxAmount=tempTaxAmount.add(new BigDecimal( entity.getTaxAmount().toString()));
        }
        invoiceEntity.setOutInvoiceAmout(tempInvoiceAmount.doubleValue());
        invoiceEntity.setOutTaxAmount(tempTaxAmount.doubleValue());
        return invoiceEntity;
    }

    /**
     * 转出窗口全部转出待转出信息查询
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoiceEntity getToOutInformationAll(String schemaLabel,String id) {
        InvoiceEntity invoiceEntity=new InvoiceEntity();
        InvoiceEntity temp= invoiceDao.getToOutInformation(schemaLabel,id);
        invoiceEntity.setOutInvoiceAmout(temp.getInvoiceAmount());
        invoiceEntity.setOutTaxAmount(temp.getTaxAmount());
        return invoiceEntity;
    }

    /**
     * 获取明细中抵账表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoiceEntity getDetailInfo(String schemaLabel,Long id) throws Exception{
        InvoiceEntity invoiceEntity=invoiceDao.getDetailInfo(schemaLabel,id);
        invoiceEntity.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(invoiceEntity.getTotalAmount()*100)));
        return invoiceEntity;
    }

    /**
     * 取消转出
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public Boolean cancelTransferOut(String schemaLabel,String[] id) {
        final Boolean flag = invoiceDao.cancelTransferOut(schemaLabel,id)>0;
        if(flag){
            //根据id获取取消转出的发票代码号码
            for(String idString : id){
                Long outId = Long.valueOf(idString);
                final InvoiceEntity entity = invoiceDao.getTransferOutHisById(schemaLabel, outId);

                //获取已转出的总金额税额
                final InvoiceEntity outInvoice = invoiceDao.getTotalOutAmount(schemaLabel, entity.getInvoiceCode()+entity.getInvoiceNo());
                //获取主表id
                final InvoiceEntity invoiceEntity = invoiceDao.getTransferOutByInvoice(schemaLabel, entity.getInvoiceCode(), entity.getInvoiceNo());

                outInvoice.setId(invoiceEntity.getId());

                //比较数值,决定主表的转出状态
                if(outInvoice.getOutInvoiceAmout()==0 && outInvoice.getOutTaxAmount()==0){
                    //未转出
                    outInvoice.setOutStatus("0");
                }else{
                    //部分转出
                    outInvoice.setOutStatus("2");
                }
                //更新主表
                invoiceDao.updateOutMain(schemaLabel, outInvoice);
            }
        }
        return flag;
    }
}
