package com.xforceplus.wapp.modules.rednotification.validator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.enums.*;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.rednotification.mapstruct.ConvertHelper;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.modules.rednotification.util.RegexUtils;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import io.vavr.control.Either;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 校验红字主信息
 */
@Service
public class CheckMainService {

    @Autowired
    TaxCodeServiceImpl taxCodeServiceImpl;
    @Autowired
    CompanyService companyService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    TXfRedNotificationDao tXfRedNotificationDao;

    public String checkMainInfo(ImportInfo importInfo){

        StringBuilder errorBuilder = new StringBuilder();
        Integer applyTypeValue = importInfo.getApplyType();
        if(Objects.nonNull(applyTypeValue)) {
            Optional<RedNoApplyType> applyTypeOptional = ValueEnum.getEnumByValue(RedNoApplyType.class, applyTypeValue);
            if (!applyTypeOptional.isPresent()) {
                errorBuilder.append("申请类型为空或者不正确，必须为0—购方申请已抵扣, 1—购方申请未抵扣, 2—销方申请;");
            }
        }else {
            errorBuilder.append(" 申请类型 不能为空");
        }

        // 校验申请流水号
        if(StringUtils.isBlank(importInfo.getSellerNumber()) || importInfo.getSellerNumber().length() >50 || !RegexUtils.composedByNumberOrLetter(importInfo.getSellerNumber())){
            errorBuilder.append("申请流水号为空或者格式不正确，必须仅由长度不超过50的数字或字母组成;");
        }else {
            // 判断流水号是否存在
            LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TXfRedNotificationEntity::getSerialNo,importInfo.getSellerNumber()).eq(TXfRedNotificationEntity::getStatus,1);
            Integer count = tXfRedNotificationDao.selectCount(queryWrapper);
            if( count > 0 ){
                errorBuilder.append("申请流水号已存在;");
            }
        }



        String error1 = checkCompany(importInfo);
        errorBuilder.append(error1);


        String error2 = checkInvoiceMessage( importInfo.getOriginInvoiceCode(), importInfo.getOriginInvoiceNo(), importInfo.getInvoiceType(), importInfo.getOriginInvoiceType());
        errorBuilder.append(error2);

        String error3 = checkAmount(importInfo);
        errorBuilder.append(error3);

//        String error4 = checkRefinedOilInvoice(importInfo);
//        errorBuilder.append(error4);

        String error5 = checkPriceMethodWithoutTax(importInfo);
        errorBuilder.append(error5);

        //校验税编
        String error6 = checkGoodsTaxNo(importInfo);
        errorBuilder.append(error6);

        String errorLast = checkOtherFields(importInfo);
        errorBuilder.append(errorLast);

        return errorBuilder.toString();
    }

    private String checkCompany(ImportInfo importInfo) {
        String taxNo = importInfo.getPurchaserTaxNo();
        String purchaserName = importInfo.getPurchaserName();
        if (StringUtils.isEmpty(taxNo)){
            return "购方税号必须填写";
        }else if (StringUtils.isEmpty(purchaserName)){
            return "购方名称必须填写";
        }


        String key = "purcherseTaxNo:"+taxNo;
        Object result = redisTemplate.opsForValue().get(key);
        if ( result != null ){
            if ("false".equals(result)){
                return String.format("沃尔玛旗下未找到该购方税号:[%s]",taxNo);
            }else {
                // 判断公司名称税号
                if (!Objects.equals(result,purchaserName)){
                    return String.format("沃尔玛旗下该购方税号:[%s]对应的名称【%s】实际传入【%s】 ",taxNo,result,purchaserName);
                }
                return "" ;
            }
        }

        TAcOrgEntity company = companyService.getByTaxNo(taxNo);
        if (company != null){
            redisTemplate.opsForValue().set(key,company.getTaxName(),60, TimeUnit.SECONDS);
            // 判断公司名称税号
            if (!Objects.equals(company.getTaxName(),purchaserName)){
                return String.format("沃尔玛旗下该购方税号:[%s]对应的名称【%s】实际传入【%s】 ",taxNo,company.getTaxName(),purchaserName);
            }
            return "" ;
        }else {
            redisTemplate.opsForValue().set(key,"false",60, TimeUnit.SECONDS);
            return String.format("沃尔玛旗下未找到该购方税号:[%s]",taxNo);
        }

    }

    private String checkGoodsTaxNo(ImportInfo importInfo) {
        StringBuilder errorBuilder = new StringBuilder();
        String goodsTaxNo = importInfo.getGoodsTaxNo();
        if(goodsTaxNo.startsWith("6")){
            errorBuilder.append("税收分类编码不能以6开头;");
        }else{
            Either<String, List<TaxCodeBean>> result = taxCodeServiceImpl.searchTaxCode(goodsTaxNo, null);
            if(result.isRight()){
                List<TaxCodeBean> taxCodeBeans = result.get();
                if (taxCodeBeans.isEmpty()){
                    errorBuilder.append("税收分类编码填写错误，当前填写值为【").append(goodsTaxNo).append("】;");
                }else {
                    TaxCodeBean taxCodeBean = taxCodeBeans.get(0);
                    importInfo.setGoodsNoVer(taxCodeBean.getTaxCodeVersion());

                   String cargoName = importInfo.getGoodsName();
                    if(!StringUtils.isEmpty(cargoName) && !cargoName.matches("^\\*\\W+\\*\\W*")){
                        cargoName = new StringBuilder("*").append(taxCodeBean.getTaxShortName()).append("*").append(cargoName).toString();
                        if(cargoName.length()>200){
                            errorBuilder.append("货物及服务名称加简称长度大于200;");
                        }else{
                            importInfo.setGoodsName(cargoName);
                        }
                    }
                }
            }
        }

        return errorBuilder.toString();

    }

//    private String checkRefinedOilInvoice( ImportInfo importInfo){
//
//        String petroleumReason = importInfo.getOliApplyReason();
//        if(!StringUtils.isEmpty(petroleumReason)) {
//            if (!ValueEnum.getEnumByValue(PetroleumReason.class, petroleumReason).isPresent()) {
//                return "品油发票必须填写正确的成品油申请原因: 0-成品油涉及销售数量变更, 1-成品油仅涉及销售金额变更";
//            }
//            PetroleumReason petroleumReason1 = ValueEnum.getEnumByValue(PetroleumReason.class, petroleumReason).get();
//            importInfo.setSpecialInvoiceFlag(petroleumReason1.getValue());
//
//        }
//
//        return "";
//    }


    private String checkAmount(ImportInfo importInfo){

        StringBuilder errorBuilder = new StringBuilder();

        BigDecimal amountWithTax = importInfo.getAmountWithTax();
        if(Objects.isNull(amountWithTax)){
            errorBuilder.append("价税合计不能为空;");
        }else{
            if(amountWithTax.compareTo(BigDecimal.ZERO) >= 0){
                errorBuilder.append("价税合计必须小于零;");
            }
        }

        BigDecimal amountWithoutTax = importInfo.getAmountWithoutTax();
        if(Objects.isNull(amountWithoutTax)){
            errorBuilder.append("合计金额不能为空;");
        }else{
            if(amountWithoutTax.compareTo(BigDecimal.ZERO) >= 0){
                errorBuilder.append("合计金额必须小于零;");
            }
        }

        BigDecimal taxAmount = importInfo.getTaxAmount();
        if(Objects.isNull(taxAmount)){
            errorBuilder.append("合计税额不能为空;");
        }else{
            if(taxAmount.compareTo(BigDecimal.ZERO) > 0){
                errorBuilder.append("合计税额必须小于等于零;");
            }
        }

        if(Objects.nonNull(amountWithTax) && Objects.nonNull(amountWithoutTax) && Objects.nonNull(taxAmount)){

            amountWithTax = amountWithTax.setScale(2, BigDecimal.ROUND_HALF_UP);
            importInfo.setAmountWithTax(amountWithTax);

            amountWithoutTax = amountWithoutTax.setScale(2, BigDecimal.ROUND_HALF_UP);
            importInfo.setAmountWithoutTax(amountWithoutTax);

            taxAmount = taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
            importInfo.setTaxAmount(taxAmount);

            if(amountWithoutTax.add(taxAmount).compareTo(amountWithTax) != 0){
                errorBuilder.append("合计金额与合计税额之和不等于价税合计;");
            }
        }

        return errorBuilder.toString();
    }

    private String checkInvoiceMessage( String invoiceCode, String invoiceNo, String invoiceType, String originInvoiceType){

        StringBuilder errorBuilder = new StringBuilder();

        if(StringUtils.isNotBlank(invoiceCode) && !RegexUtils.isInvoiceCode(invoiceCode)){
            errorBuilder.append("发票代码格式不正确，必须为10或12位数字;");
        }

        if(StringUtils.isNotBlank(invoiceNo) && !RegexUtils.isInvoiceNo(invoiceNo)){
            errorBuilder.append("发票号码格式不正确，必须为8位数字;");
        }

        if(StringUtils.isBlank(invoiceType) || !isSpecialInvoice(invoiceType)){
            errorBuilder.append("发票类型为空或者不正确，必须为s-增值税专用发票或者se-增值税专用电子发票;");
        }

        if(StringUtils.isNotBlank(originInvoiceType) && !isSpecialInvoice(originInvoiceType)){
            errorBuilder.append("原发票类型不正确，必须为s-增值税专用发票或者se-增值税专用电子发票;");
        }

        return errorBuilder.toString();
    }

    private boolean isSpecialInvoice(String str){

        Optional<InvoiceType> invoiceTypeOptional = ValueEnum.getEnumByValue(InvoiceType.class, str);

        InvoiceType invoiceType;
        if(invoiceTypeOptional.isPresent() && ((invoiceType = invoiceTypeOptional.get()) == InvoiceType.SPECIAL || invoiceType == InvoiceType.SPECIAL_ELECTRONIC)){
            return true;
        }
        return false;
    }



    private String checkPriceMethodWithoutTax(ImportInfo importInfo){

        BigDecimal amountWithoutTax = importInfo.getAmountWithoutTax();

        BigDecimal quantity = null ;
        if (importInfo.getNum() !=null){
            quantity = new BigDecimal(importInfo.getNum());
        }

        BigDecimal unitPrice = null;
        if (importInfo.getUnitPrice() !=null){
            unitPrice = new BigDecimal(importInfo.getUnitPrice());
        }


        if(Objects.nonNull(amountWithoutTax)){
            if(Objects.isNull(quantity) ^ Objects.isNull(unitPrice)){
                if(Objects.isNull(quantity)){
                    quantity = amountWithoutTax.divide(unitPrice, 6 ,BigDecimal.ROUND_HALF_UP);
                    importInfo.setNum(quantity.toString());
                }else{
                    unitPrice = amountWithoutTax.divide(quantity, 15, BigDecimal.ROUND_HALF_UP);
                    importInfo.setUnitPrice(unitPrice.toPlainString());
                }
            }

            BigDecimal deduction = Optional.ofNullable(importInfo.getDeduction()).orElse(BigDecimal.ZERO);

            if(deduction.compareTo(amountWithoutTax) < 0){
                return "扣除额必须大于等于不含税金额;";
            }else{

                //计算公式 https://wiki.xforceplus.com/pages/viewpage.action?pageId=73403822
                if (StringUtils.isEmpty(importInfo.getTaxRate())){
                    return "税率必须填写";
                }
                BigDecimal taxRate = ConvertHelper.handleTaxRate(importInfo.getTaxRate());
                if (taxRate == null){
                    return "税率填写不正确"+importInfo.getTaxRate();
                }

                BigDecimal amountWithTax = importInfo.getAmountWithTax();
                BigDecimal taxAmount = importInfo.getTaxAmount();
                if(Objects.isNull(amountWithTax)){
                    amountWithTax = Optional.ofNullable(importInfo.getTaxAmount())
                            .filter(r -> {
                                BigDecimal calTaxAmount = amountWithoutTax.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
                                return calTaxAmount.compareTo(r.setScale(2, RoundingMode.HALF_UP)) != 0 && calTaxAmount.subtract(r).setScale(2, RoundingMode.HALF_UP).abs().compareTo(new BigDecimal("0.01")) <= 0;
                            })
                            .map(r -> amountWithoutTax.add(r).setScale(2, RoundingMode.HALF_UP))
                            .orElseGet(() -> amountWithoutTax.multiply(taxRate).add(amountWithoutTax).subtract(deduction.multiply(taxRate)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    importInfo.setAmountWithTax(amountWithTax);
                }

                if(Objects.isNull(taxAmount)){
                    taxAmount = amountWithTax.subtract(amountWithoutTax).setScale(2, BigDecimal.ROUND_HALF_UP);
                    importInfo.setTaxAmount(taxAmount);
                }

            }
        }

        StringBuilder errorBuilder = new StringBuilder();
        return errorBuilder.toString();

    }


    private String checkOtherFields(ImportInfo importInfo){
        StringBuilder errorBuilder = new StringBuilder();

        String taxPre = importInfo.getTaxPre();

        if (StringUtils.isEmpty(taxPre)){
            errorBuilder.append("请填写优惠政策标识;");
        } else{
            if (!"0".equals(taxPre) && !"1".equals(taxPre)){
                errorBuilder.append("优惠政策标识只能是0和1;");
            }
        }

        return errorBuilder.toString();

    }


}
