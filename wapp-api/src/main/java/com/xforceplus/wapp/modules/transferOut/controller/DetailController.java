package com.xforceplus.wapp.modules.transferOut.controller;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/13
 * Time:13:48
*/

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.entity.OrgEntity;
import com.xforceplus.wapp.modules.transferOut.service.DetailService;
import com.xforceplus.wapp.modules.transferOut.service.InvoiceService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/transferOut/detailQuery")
public class DetailController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(DetailController.class);
    @Autowired
    private DetailService detailService;

    @Autowired
    private InvoiceService invoiceService;

    /**
     * 进入页面带出购方名称和税号
     * @return
     */
    @SysLog("通过Useid带出所属的购方名称以及税号")
    @RequestMapping("/gfNameAndTaxNo")
    public R getGfNameAndTaxNo(){
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long userId=getUserId();
        List<OrgEntity> list = detailService.getGfNameAndTaxNo(schemaLabel,userId);
        List<String> gfNameList= Lists.newArrayList();
        List<String> gfTaxNoList=Lists.newArrayList();
        int i=0;
        for (OrgEntity orgEntity :list){
            gfNameList.add(orgEntity.getOrgname());
            gfTaxNoList.add(orgEntity.getTaxno());
            i++;
        }
        R r=R.ok();
        r.put("gfNameList",gfNameList);
        r.put("gfTaxNoList",gfTaxNoList);
        return r;
    }

    /**
     * 点击明细按钮后的查询
     * @param value
     * @return
     */
    @SysLog("明细")
    @RequestMapping("/invoiceDetail")
    public R getInvoinceDetail(@RequestBody String value){
        final String schemaLabel = getCurrentUserSchemaLabel();
        R.error("未找到明细信息");
        InvoiceEntity invoiceEntity=new InvoiceEntity();
        if(StringUtils.isEmpty(value) || "null".equals(value)) {
            return R.error(1,"未查找到明细信息");
        }
        Long id=Long.parseLong(value);
        try{
            invoiceEntity=invoiceService.getDetailInfo(schemaLabel,id);
        }catch(Exception e){
            return R.error(1,"未查找到明细信息");
        }

        final List<InvoiceEntity> outList = detailService.getOutInfo(schemaLabel, invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());

        R r=R.ok();
        String flag=invoiceEntity.getInvoiceType();
        final String vehicleInvoice="03";//机动车销售统一发票
        if (flag.equals(vehicleInvoice)){
            DetailVehicleEntity detailVehicleEntity=new DetailVehicleEntity();
            try {
                detailVehicleEntity=detailService.getVehicleDetail(schemaLabel, id);
            }catch (Exception e){
                LOGGER.error(e.toString());
            }


            String account="";
            String phone="";
            if (checkBankAccount(invoiceEntity.getXfBankAndNo())!=null){
                account=checkBankAccount(invoiceEntity.getXfBankAndNo());
            }
            if (checkCellphone(invoiceEntity.getXfAddressAndPhone())!=null){
                phone=checkCellphone(invoiceEntity.getXfAddressAndPhone());
            }else{
                phone=checkTelephone(invoiceEntity.getXfAddressAndPhone());
            }
            String bank = invoiceEntity.getXfBankAndNo().replace(account,"");
            String address=invoiceEntity.getXfAddressAndPhone().replace(phone,"");
            r.put("bank",bank).put("account", account ).put("address", address).put("phone", phone);
            r.put("invoiceEntity",invoiceEntity);
            r.put("outList", outList);
            r.put("detailVehicleEntity",detailVehicleEntity);
            return r;
        }
        List<DetailEntity> detailEntityList=detailService.getInvoiceDetail(schemaLabel, id);
        BigDecimal detailAmountTotal=new BigDecimal("0.0");
        BigDecimal taxAmountTotal=new BigDecimal("0.0");
        for (DetailEntity detailEntity:detailEntityList){
            detailAmountTotal=detailAmountTotal.add(new BigDecimal(detailEntity.getDetailAmount()));
            taxAmountTotal=taxAmountTotal.add(new BigDecimal(detailEntity.getTaxAmount()));
        }


        r.put("detailEntityList",detailEntityList);
        r.put("invoiceEntity", invoiceEntity);
        r.put("outList", outList);
        r.put("detailAmountTotal",detailAmountTotal);
        r.put("taxAmountTotal",taxAmountTotal);
        return  r;
    }

    /**
     * 查询符合的手机号码
     * @param str
     */
    public static String checkCellphone(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("((1[0-9]))\\d{9}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
           return matcher.group();
        }
        return null;
    }

    /**
     * 查询符合的固定电话
     * @param str
     */
    public static String checkTelephone(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }

    /**
     * 查询符合的银行帐号
     * @param str
     */
    public static String checkBankAccount(String str){
        if(str==null){
            return "";
        }
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("\\d{16,19}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }
}
