package com.xforceplus.wapp.modules.job.utils;

import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoice;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoiceDetail;
import com.xforceplus.wapp.modules.job.entity.TDxVehicleSaleInvoice;
import com.xforceplus.wapp.modules.job.pojo.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TransitionUtils {
    /**
     * 实体转换
     * @param info
     * @return
     * @throws ParseException
     */
    public  static TDxRecordInvoice getInvoiceInfo(InvoiceInfo info) throws ParseException {
        TDxRecordInvoice invoice = new TDxRecordInvoice();
        invoice.setInvoiceCode(info.getInvoiceCode());
        invoice.setInvoiceNo(info.getInvoiceNo());
        invoice.setInvoiceDate(new SimpleDateFormat("yyyyMMdd").parse(info.getInvoiceDate()));
        invoice.setGfBankAndNo(info.getBuyerAcount());
        invoice.setGfAddressAndPhone(info.getBuyerAddressPhone());
        invoice.setGfName(info.getBuyerName());
        invoice.setGfTaxNo(info.getBuyerTaxNo());
        invoice.setXfBankAndNo(info.getSalerAcount());
        invoice.setXfAddressAndPhone(info.getSalerAddressPhone());
        invoice.setXfName(info.getSalerName());
        invoice.setXfTaxNo(info.getSalerTaxNo());
        invoice.setInvoiceAmount(new BigDecimal(info.getInvoiceAmount()));
        invoice.setTaxAmount(new BigDecimal(info.getTaxAmount()));
        invoice.setTotalAmount(new BigDecimal(info.getTotalAmount()));
        invoice.setRemark(info.getRemark());
        invoice.setDqskssq(info.getCurrentTaxPeriod());
        invoice.setGxjzr(info.getLegalizeEndDate());
        invoice.setGxfwq(info.getLegalizeInvoiceDateBegin());
        invoice.setGxfwz(info.getLegalizeInvoiceDateEnd());
        invoice.setOutStatus("0");
        if (info.getLegalizeDate() == null||"".equals(info.getLegalizeDate())) {
            invoice.setRzhDate(null);
        } else {
            invoice.setRzhDate(new SimpleDateFormat("yyyyMMdd").parse(info.getLegalizeDate()));
        }
        invoice.setRzhYesorno(info.getLegalizeState());
        invoice.setSfygx(info.getLegalizeState());
        if("1".equals(info.getLegalizeState())){
            invoice.setAuthStatus("4");
        }else{
            invoice.setAuthStatus("0");
        }
        invoice.setInvoiceStatus(info.getInvoiceStatus());
        invoice.setInvoiceType(info.getInvoiceType());
        if(info.getLegalizeBlongDate()==null||"".equals(info.getLegalizeBlongDate())){
            invoice.setRzhBelongDate(null);
        }
        else {
            invoice.setRzhBelongDate(info.getLegalizeBlongDate());
        }
        if (info.getSfdbts() == null||"".equals(info.getSfdbts())) {
            invoice.setSfdbts(null);
        } else {
            invoice.setSfdbts(info.getSfdbts());
        }
        if (info.getRzlx() == null||"".equals(info.getRzlx())) {
            invoice.setRzlx(null);
        } else {
            invoice.setRzlx(info.getRzlx());
        }
        if ("".equals(info.getCheckCode())) {
            invoice.setCheckCode("");
        } else {
            invoice.setCheckCode(info.getCheckCode());
        }
        // 添加认证方式字段
        if("".equals(info.getLegalizeType())){
            invoice.setRzhType("");
        }else{
            invoice.setRzhType(info.getLegalizeType());
        }
        return invoice;
    }

    /**
     *
     * @Description 实体类转换
     * @param
     * @author X Yang
     * @param
     * @date 2017年6月7日 下午2:40:03
     */
    public  static TDxRecordInvoiceDetail getInvoiceDetailInfo(InvoiceDetailInfo detailInfo) {
        TDxRecordInvoiceDetail detail = new TDxRecordInvoiceDetail();
        detail.setDetailNo(detailInfo.getDetailNo());
        detail.setGoodsName(detailInfo.getGoodsName());
        if ( isBlank(detailInfo.getSpecificationModel())) {
            detail.setModel(null);
        } else {
            detail.setModel(detailInfo.getSpecificationModel());
        }
        if ( isBlank(detailInfo.getGoodsNum())) {
            detail.setGoodsNum(null);
        } else {
            detail.setGoodsNum(detailInfo.getGoodsNum());
        }
        if ("".equals(detailInfo.getNum())){
            detail.setNum(null);
        }
        else {
            detail.setNum(detailInfo.getNum());
        }

        if("".equals(detailInfo.getUnit())){
            detail.setUnit(null);
        }
        else {
            detail.setUnit(detailInfo.getUnit());
        }

        if("".equals(detailInfo.getUnitPrice())){
            detail.setUnitPrice(null);
        }
        else {
            detail.setUnitPrice(detailInfo.getUnitPrice());
        }
        if("".equals(detailInfo.getDetailAmount())){
            detail.setDetailAmount(null);
        }
        else {
            detail.setDetailAmount(detailInfo.getDetailAmount());
        }
        if("".equals(detailInfo.getTaxRate())){
            detail.setTaxRate(null);
        }
        else {
            detail.setTaxRate(detailInfo.getTaxRate());
        }
        if("".equals(detailInfo.getTaxAmount()))
        {
            detail.setTaxAmount(null);
        }
        else {
            detail.setTaxAmount(detailInfo.getTaxAmount());
        }
        if("".equals(detailInfo.getCph())){
            detail.setCph("");
        }
        else {
            detail.setCph(detailInfo.getCph());
        }
        if ( isBlank(detailInfo.getLx())) {
            detail.setLx("");
        } else {
            detail.setLx(detailInfo.getLx());
        }
        if ( isBlank(detailInfo.getTxrqq())) {
            detail.setTxrqq("");
        } else {
            detail.setTxrqq(detailInfo.getTxrqq());
        }
        if (isBlank(detailInfo.getTxrqz())) {
            detail.setTxrqz("");
        } else {
            detail.setTxrqz(detailInfo.getTxrqz());
        }
        return detail;
    }

    public static Map getVehicleInfo(VehicleSaleInvoiceInfos info) throws ParseException {

        TDxVehicleSaleInvoice vehicleSaleInvoice = new TDxVehicleSaleInvoice();
        TDxRecordInvoice veinfo = new TDxRecordInvoice();
        DecimalFormat df = new DecimalFormat("#0.00");
        veinfo.setInvoiceCode(info.getInvoiceCode());
        veinfo.setInvoiceNo(info.getInvoiceNo());
        veinfo.setDqskssq(info.getCurrentTaxPeriod());
        veinfo.setGxjzr(info.getLegalizeEndDate());
        veinfo.setGxfwq(info.getLegalizeInvoiceDateBegin());
        veinfo.setGxfwz(info.getLegalizeInvoiceDateEnd());
        veinfo.setInvoiceType(info.getInvoiceType());
        veinfo.setGfName(info.getBuyerName());
        veinfo.setInvoiceDate(new SimpleDateFormat("yyyyMMdd").parse(info.getInvoiceDate()));
        if (isBlank(info.getBuyerIdNum())) {
            vehicleSaleInvoice.setBuyerIdNum("");
        }
        else {
            vehicleSaleInvoice.setBuyerIdNum(info.getBuyerIdNum());
        }
        veinfo.setGfTaxNo(info.getBuyerTaxNo());
        if (isBlank(info.getVehicleType())) {
            vehicleSaleInvoice.setVehicleType("");
        } else {
            vehicleSaleInvoice.setVehicleType(info.getVehicleType());
        }
        if (isBlank(info.getFactoryModel())) {
            vehicleSaleInvoice.setFactoryModel("");
        } else {
            vehicleSaleInvoice.setFactoryModel(info.getFactoryModel());
        }
        if (isBlank(info.getProductPlace())) {
            vehicleSaleInvoice.setProductPlace("");
        } else {
            vehicleSaleInvoice.setProductPlace(info.getProductPlace());
        }
        if (isBlank(info.getCertificate())) {
            vehicleSaleInvoice.setCertificate("");
        } else {
            vehicleSaleInvoice.setCertificate(info.getCertificate());
        }
        if (isBlank(info.getCertificateImprot())) {
            vehicleSaleInvoice.setCertificateImport("");
        } else {
            vehicleSaleInvoice.setCertificateImport(info.getCertificateImprot());
        }
        if (isBlank(info.getInspectionNum())) {
            vehicleSaleInvoice.setInspectionNum("");
        } else {
            vehicleSaleInvoice.setInspectionNum(info.getInspectionNum());
        }
        if (isBlank(info.getEngineNo())) {
            vehicleSaleInvoice.setEngineNo("");
        } else {
            vehicleSaleInvoice.setEngineNo(info.getEngineNo());
        }
        if (isBlank(info.getVehicleNo())) {
            vehicleSaleInvoice.setVehicleNo("");
        } else {
            vehicleSaleInvoice.setVehicleNo(info.getVehicleNo());
        }
        veinfo.setTotalAmount(new BigDecimal(info.getTotalAmount()));
        veinfo.setXfName(info.getSalerName());
        veinfo.setXfTaxNo(info.getTaxNum());
        if (isBlank(info.getPhone())) {
            vehicleSaleInvoice.setPhone("");
        } else {
            vehicleSaleInvoice.setPhone(info.getPhone());
        }
        if (isBlank(info.getBuyerBank())) {
            vehicleSaleInvoice.setBuyerBank("");
        } else {
            vehicleSaleInvoice.setBuyerBank(info.getBuyerBank());
        }
        if (isBlank(info.getBuyerAcount())){
            veinfo.setXfBankAndNo("");
        }
        else {
            veinfo.setXfBankAndNo(info.getBuyerAcount());
        }
        if(isBlank(info.getBuyerAddressPhone())){
            veinfo.setXfAddressAndPhone("");
        }
        else {
            veinfo.setXfAddressAndPhone(info.getBuyerAddressPhone());
        }
        vehicleSaleInvoice.setTaxRate(info.getTaxRate());
        veinfo.setTaxAmount(new BigDecimal(info.getTaxAmount()));
        if (isBlank(info.getTaxBureauCode())) {
            vehicleSaleInvoice.setTaxBureauCode("");
        } else {
            vehicleSaleInvoice.setTaxBureauCode(info.getTaxBureauCode());
        }
        if (isBlank(info.getTaxBureauName())) {
            vehicleSaleInvoice.setTaxBureauName("");
        } else {
            vehicleSaleInvoice.setTaxBureauName(info.getTaxBureauName());
        }
        veinfo.setInvoiceAmount(new BigDecimal(info.getInvoiceAmount()));
        if (isBlank(info.getTaxRecords())) {
            vehicleSaleInvoice.setTaxRecords("");
        } else {
            vehicleSaleInvoice.setTaxRecords(info.getTaxRecords());
        }
        if (isBlank(info.getTonnage())) {
            vehicleSaleInvoice.setTonnage("");
        } else {
            vehicleSaleInvoice.setTonnage(info.getTonnage());
        }
        if (isBlank(info.getLimitPeople())) {
            vehicleSaleInvoice.setLimitPeople("");
        } else {
            vehicleSaleInvoice.setLimitPeople(info.getLimitPeople());
        }
        veinfo.setInvoiceStatus(info.getInvoiceStatus());
        veinfo.setSfygx(info.getCheckStatus());
        veinfo.setRzhYesorno(info.getLegalizeState());
        if("1".equals(info.getLegalizeState())){
            veinfo.setAuthStatus("4");
        }
        else{
            veinfo.setAuthStatus("0");
        }
        if("".equals(info.getLegalizeDate())){
            veinfo.setRzhDate(null);
        }
        else {
            veinfo.setRzhDate(new SimpleDateFormat("yyyyMMdd").parse(info.getLegalizeDate()));
        }

        if("".equals(info.getTaxPeriod())){
            veinfo.setRzhBelongDate(null);
        }
        else {
            veinfo.setRzhBelongDate(info.getTaxPeriod());
        }

        if("".equals(info.getLegalizeType()))
        {
            veinfo.setRzhType(null);
        }
        else {
            veinfo.setRzhType(info.getLegalizeType());
        }
        if (info.getSfdbts() == null) {
            veinfo.setSfdbts("");
        } else {
            veinfo.setSfdbts(info.getSfdbts());
        }
        if (info.getRzlx() == null) {
            veinfo.setRzlx("");
        } else {
            veinfo.setRzlx(info.getRzlx());
        }
        Map map = new HashMap();
        map.put("vehicleSaleInvoice",vehicleSaleInvoice);
        map.put("veinfo",veinfo);
        return map;
    }

    /**
     *
     * @Description 实体类转换
     * @author X Yang
     * @date 2017年6月8日 下午6:41:27
     */
    public  static State getStateInfo(StateInfo info) throws ParseException {
        State state = new State();
        state.setInvoiceCode(info.getInvoiceCode());
        state.setInvoiceNo(info.getInvoiceNo());
        state.setInvoiceStatus(info.getInvoiceStatus());
        if("".equals(info.getLegalizeDate())){
            state.setLegalizeDate(null);
        }
        else {
            state.setLegalizeDate(new SimpleDateFormat("yyyyMMdd").parse(info.getLegalizeDate()));
        }

        state.setLegalizeState(info.getLegalizeState());
        if("1".equals(info.getLegalizeState())){
            state.setAuthStatus("4");
        }
        else{
            state.setAuthStatus("0");
        }
        if("".equals(info.getLegalizeBelongDate())){
            state.setLegalizeBelongDate(null);
        }
        else {
            state.setLegalizeBelongDate(info.getLegalizeBelongDate());
        }
        if("".equals(info.getLegalizeType())){
            state.setLegalizeType(null);
        }
        else{
            state.setLegalizeType(info.getLegalizeType());
        }
        // if(state.getLegalizeLx()==null){
        // info.setRzlx("");
        // }else{
        // info.setRzlx(state.getLegalizeLx().toString());
        // }
        return state;
    }

    private static Boolean isBlank(String str){
        return str == null || str.trim().length() == 0;

    }
}
