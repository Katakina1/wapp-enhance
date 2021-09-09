package com.xforceplus.wapp.modules.posuopei.controller;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/13
 * Time:13:48
*/

import com.aisinopdf.text.pdf.S;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.export.DetailsQueryExport;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.redTicket.service.PrintCoverService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

import com.xforceplus.wapp.utils.excel.ExcelUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;

@RestController
public class DetailsController extends AbstractController {
    private static final  Logger LOGGER = LoggerFactory.getLogger(DetailsController.class);
    @Autowired
    private DetailsService detailsService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private  PrintCoverService printCoverService;

    /**
     * 点击结果明细的查询
     * @param value
     * @return
     */
    @SysLog("结果明细")
    @RequestMapping("/resultDetail")
    public R getResultDetail(@RequestBody String value){

        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("查看的结果明细id是:{}",value);
        Long id=Long.parseLong(value);
        MatchEntity entity=detailsService.getResultDetail(schemaLabel,id);
        R r= R.ok().put("entity",entity);
       return r;
    }

    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("匹配查询")
    @RequestMapping(POSUOPEI_PO_MATCH_QUERY)
    public R matchQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("param {}",param);
        Query query=new Query(param);

        PagedQueryResult<MatchEntity> pagedQueryResult=detailsService.getMatchList(query);

        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }



    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("匹配查询专用")
    @RequestMapping(POSUOPEI_PO_THEMATCH_QUERY)
    public R theMatchQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("param {}",param);
        String venderId=getUser().getUsercode();
        param.put("venderid",venderId);
        Query query=new Query(param);


        PagedQueryResult<MatchEntity> pagedQueryResult=detailsService.getTheMatchList(query);

        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }


    /**
     * 点击结果明细的查询
     * @return
     */
    @SysLog("writeScreen")
    @RequestMapping(POSUOPEI_PO_MATCH_WRITE)
    public R write(){
//        Thread thread1 = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
////                matchService.testGetconnHostPo1("2017-12-30","2017-01-25","");
//                matchService.testGetconnHostPo1("2017-12-24","2018-01-25","");
//                matchService.testGetconnHostPo2("2017-12-24","2018-01-25","");
//                matchService.testGetconnHostPo4("2017-12-24","2018-01-25","");
//            }
//        });
//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-01-24","2018-02-25","");
//                matchService.testGetconnHostPo2("2018-01-24","2018-02-25","");
//                matchService.testGetconnHostPo4("2018-01-24","2018-02-25","");
//            }
//        });
//
//        Thread thread3 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-02-24","2018-03-25","");
//                matchService.testGetconnHostPo2("2018-02-24","2018-03-25","");
//                matchService.testGetconnHostPo4("2018-02-24","2018-03-25","");
//            }
//        });
//        Thread thread4 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-03-24","2018-04-25","");
//                matchService.testGetconnHostPo2("2018-03-24","2018-04-25","");
//                matchService.testGetconnHostPo4("2018-03-24","2018-04-25","");
//            }
//        });
//        Thread thread5 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-04-24","2018-05-25","");
//                matchService.testGetconnHostPo2("2018-04-24","2018-05-25","");
//                matchService.testGetconnHostPo4("2018-04-24","2018-05-25","");
//            }
//        });
//        Thread thread6 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-05-24","2018-06-25","");
//                matchService.testGetconnHostPo2("2018-05-24","2018-06-25","");
//                matchService.testGetconnHostPo4("2018-05-24","2018-06-25","");
//            }
//        });
//        Thread thread7 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-06-24","2018-07-25","");
//                matchService.testGetconnHostPo2("2018-06-24","2018-07-25","");
//                matchService.testGetconnHostPo4("2018-06-24","2018-07-25","");
//            }
//        });
//        Thread thread8 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-07-24","2018-08-25","");
//                matchService.testGetconnHostPo2("2018-07-24","2018-08-25","");
//                matchService.testGetconnHostPo4("2018-07-24","2018-08-25","");
//            }
//        });
//        Thread thread9 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-08-24","2018-09-25","");
//                matchService.testGetconnHostPo2("2018-08-24","2018-09-25","");
//                matchService.testGetconnHostPo4("2018-08-24","2018-09-25","");
//            }
//        });
//        Thread thread10 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-09-24","2018-10-25","");
//                matchService.testGetconnHostPo2("2018-09-24","2018-10-25","");
//                matchService.testGetconnHostPo4("2018-09-24","2018-10-25","");
//            }
//        });
//        Thread thread11 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-10-24","2018-11-25","");
//                matchService.testGetconnHostPo2("2018-10-24","2018-11-25","");
//                matchService.testGetconnHostPo4("2018-10-24","2018-11-25","");
//            }
//        });
//
//        Thread thread12 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                matchService.testGetconnHostPo1("2018-11-24","2018-12-25","");
//                matchService.testGetconnHostPo2("2018-11-24","2018-12-25","");
//                matchService.testGetconnHostPo4("2018-11-24","2018-12-25","");
//            }
//        });
//        thread1.start();
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        thread2.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread3.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread4.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread5.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread6.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread7.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread8.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread9.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread10.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread11.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        thread12.start();
//
//
//
//    return R.ok("success");
     matchService.runWritrScreen();
        return R.ok("success");
    }


    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("匹配明细")
    @RequestMapping(POSUOPEI_PO_MATCH_DETAIL)
    public R getMatchDetail(@RequestBody Map<String,Object> params){
        String matchno=(String)params.get("matchno");

        LOGGER.info("票单关联号:{}",matchno);
        MatchEntity matchEntity=detailsService.getMatchDetail(matchno);

        R r= R.ok().put("invoiceList",matchEntity.getInvoiceEntityList()).put("poList",matchEntity.getPoEntityList()).put("claimList",matchEntity.getClaimEntityList());
        return r;
    }

    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("导出匹配发票明细")
    @RequestMapping("/export/invoicePoRepertoire/exportDetail")
    public void exportMatchDetail(@RequestParam Map<String,Object> params, HttpServletResponse response){
        String matchno=(String)params.get("matchno");
        LOGGER.info("票单关联号:{}",matchno);
        MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("detailsQueryExport", matchEntity.getInvoiceEntityList());
        //生成excel
        //转换Excel数据
        List<DetailInvExcelEntity> list2=detailsService.transformInvExcle(matchEntity.getInvoiceEntityList());
        try {

            ExcelUtil.writeExcel(response,list2,"匹配关系发票明细导出","sheet1", ExcelTypeEnum.XLSX,DetailInvExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }
    }
    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("导出匹配订单明细")
    @RequestMapping("/export/invoicePoRepertoire/exportDetailPo")
    public void exportPoDetail(@RequestParam Map<String,Object> params, HttpServletResponse response){
        String matchno=(String)params.get("matchno");
        LOGGER.info("票单关联号:{}",matchno);
        MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("detailsQueryExport", matchEntity.getPoEntityList());
        //生成excel
        //转换Excel数据
        List<DetailPoExcelEntity> list2=detailsService.transformPoExcle(matchEntity.getPoEntityList());
        try {

            ExcelUtil.writeExcel(response,list2,"匹配关系订单明细导出","sheet1", ExcelTypeEnum.XLSX,DetailPoExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }
    }
    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("导出匹配索赔明细")
    @RequestMapping("/export/invoicePoRepertoire/exportDetailClaim")
    public void exportClaimDetail(@RequestParam Map<String,Object> params, HttpServletResponse response){
        String matchno=(String)params.get("matchno");
        LOGGER.info("票单关联号:{}",matchno);
        MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("detailsQueryExport", matchEntity.getClaimEntityList());
        //生成excel
        //转换Excel数据
        List<DetailClaimExcelEntity> list2=detailsService.transformClaimExcle(matchEntity.getClaimEntityList());
        try {

            ExcelUtil.writeExcel(response,list2,"匹配关系发票明细导出","sheet1", ExcelTypeEnum.XLSX,DetailClaimExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }
    }
    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("相关资料明细")
    @RequestMapping(POSUOPEI_PO_MATCH_IMG)
    public R getInvoiceImg(@RequestBody Map<String,Object> params){
        String matchno=(String)params.get("matchno");

        LOGGER.info("票单关联号:{}",matchno);
        List<String> imgList=detailsService.getImg(matchno);

        R r= R.ok().put("imgList",imgList);
        return r;
    }


    /**
     * 取消匹配
     * @param params
     * @return
     */
    @SysLog("取消匹配")
    @RequestMapping(POSUOPEI_PO_MATCH_CANCEL)
    public R cancelMatch(@RequestBody Map<String,Object> params){
        String matchno=(String)params.get("matchno");
        String msg;
        LOGGER.info("票单关联号:{}",matchno);
        try{

             msg=detailsService.matchCancel(matchno);
        }catch (Exception e){
            msg="取消匹配失败！";
            e.printStackTrace();
        }

        R r= R.ok().put("msg",msg);
        return r;
    }




    /**
     * 点击明细按钮后的查询
     * @param value
     * @return
     */
    @SysLog("明细")
    @RequestMapping(POSUOPEI_PO_INVOICE_DETAIL)
    public R getInvoinceDetail(@RequestBody String value){
        final String schemaLabel = getCurrentUserSchemaLabel();
        R.error("未找到明细信息");
        InvoicesEntity invoiceEntity;
        if(StringUtils.isEmpty(value) || "null".equals(value)) {
            return R.error(1,"未查找到明细信息");
        }
        Long id=Long.parseLong(value);
        try{
            invoiceEntity=detailsService.getDetailInfo(schemaLabel,id);
        }catch(Exception e){
            LOGGER.error("明细 {}",e);
            return R.error(1,"未查找到明细信息");
        }

        final List<InvoicesEntity> outList = detailsService.getOutInfo(schemaLabel, invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());

        R r= R.ok();
        String flag=invoiceEntity.getInvoiceType();
        final String vehicleInvoice="03";//机动车销售统一发票
        if (flag.equals(vehicleInvoice)){
            DetailVehicleEntity detailVehicleEntity=new DetailVehicleEntity();
            try {
                detailVehicleEntity=detailsService.getVehicleDetail(schemaLabel, id);
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
        List<DetailEntity> detailEntityList=detailsService.getInvoiceDetail(schemaLabel, id);
        BigDecimal detailAmountTotal=new BigDecimal("0.0");
        BigDecimal taxAmountTotal=new BigDecimal("0.0");
        for (DetailEntity detailEntity:detailEntityList){
            if(null==detailEntity.getDetailAmount()||"***".equals(detailEntity.getDetailAmount())){
                detailEntity.setDetailAmount("0");
            }
            detailAmountTotal=detailAmountTotal.add(new BigDecimal(detailEntity.getDetailAmount()));
            if(null==detailEntity.getTaxAmount()||"***".equals(detailEntity.getTaxAmount())){
                detailEntity.setTaxAmount("0");
            }
            taxAmountTotal=taxAmountTotal.add(new BigDecimal(detailEntity.getTaxAmount()));
        }
        //如果明细合计金额，合计税额刚好等于票面金额2或3倍则除于2或3
        if(new BigDecimal(invoiceEntity.getInvoiceAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(detailAmountTotal)==0){
           detailAmountTotal=detailAmountTotal.divide(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        if(new BigDecimal(invoiceEntity.getInvoiceAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(3)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(detailAmountTotal)==0){
            detailAmountTotal=detailAmountTotal.divide(new BigDecimal(3)).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        if(new BigDecimal(invoiceEntity.getTaxAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(taxAmountTotal)==0){
            taxAmountTotal=taxAmountTotal.divide(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        if(new BigDecimal(invoiceEntity.getTaxAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(3)).setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(taxAmountTotal)==0){
            taxAmountTotal=taxAmountTotal.divide(new BigDecimal(3)).setScale(2,BigDecimal.ROUND_HALF_UP);
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
    /**
     * 导出数据-
     * @param
     * @return
     */
    @RequestMapping(value = "/export/invoicePoRepertoire/invoicePoListExport" )
    public void resultListExport(@RequestParam("matchnoList")String matchnoList , HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", matchnoList);
        JSONArray arr = JSONArray.fromObject(matchnoList);
        Map <String,Object> params =new HashMap<>();
        List<MatchEntity> list = new ArrayList<MatchEntity>();
        for(int i=0;i<arr.size();i++){
            String matchno = (String) arr.get(i);
            MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
            matchEntity.setMatchno(matchno);
            //获取匹配日期
            MatchEntity matchEntity1 = detailsService.selectMatchEntity(matchno);

            //String venderName = detailsService.selectVenderName(matchEntity1.getVenderid());
           // matchEntity.setVenderName(venderName);
            matchEntity.setMatchDate(matchEntity1.getMatchDate());
            matchEntity.setMatchCover(matchEntity1.getCover());
            list.add(matchEntity);
        }
        params.put("list",list);
        params.put("currentDate", now().toString("yyyy-MM-dd"));
        params.put("user",getUser());
        try {
            detailsService.exportPoPdf(params, response);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
        }

    }

    /**
     * 导出匹配查询数据-
     * @param
     * @return
     */
    @RequestMapping(value = "/export/invoicePoRepertoire/invoiceChaXunExport" )
    public void resultExport(@RequestParam("matchnoList")String matchnoList ,@RequestParam("venderName")String venderName , HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", matchnoList);
        JSONArray arr = JSONArray.fromObject(matchnoList);
        JSONArray arrName = JSONArray.fromObject(venderName);
        Map <String,Object> params =new HashMap<>();
        List<MatchEntity> list = new ArrayList<MatchEntity>();
        BigDecimal sttAmount=new BigDecimal(0);
        BigDecimal poAmount=new BigDecimal(0);
        BigDecimal claimAmount=new BigDecimal(0);
        BigDecimal cover=new BigDecimal(0);
        String venderid="";
        String name=arrName.get(0).toString();
        for(int i=0;i<arr.size();i++){
            String matchno = (String) arr.get(i);
            LOGGER.info("票单关联号:{}",matchno);
            MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
            List<InvoiceEntity> inList=matchEntity.getInvoiceEntityList();
            BigDecimal total=new BigDecimal(0);
            if(inList!=null){
                if(inList.size()>0){
                    for (InvoiceEntity in:inList) {
                        total=total.add(in.getInvoiceAmount());
                    }
                }
            }
            matchEntity.setMatchno(matchno);
            //获取匹配日期
            MatchEntity matchEntity1 = detailsService.selectMatchEntity(matchno);
            sttAmount=total;
            poAmount=matchEntity1.getPoAmount();
            claimAmount=matchEntity1.getClaimAmount();
            cover=matchEntity1.getCover();
            venderid=matchEntity1.getVenderid();
            matchEntity.setMatchDate(matchEntity1.getMatchDate());

            list.add(matchEntity);
            //  params.put("matchEntity",matchEntity);
            //R r= R.ok().put("invoiceList",matchEntity.getInvoiceEntityList()).put("poList",matchEntity.getPoEntityList()).put("claimList",matchEntity.getClaimEntityList());
        }
        params.put("list",list);
        params.put("sttAmount",sttAmount);
        params.put("poAmount",poAmount);
        params.put("claimAmount",claimAmount);
        params.put("cover",cover);
        params.put("name",name);
        params.put("currentDate", now().toString("yyyy-MM-dd"));
        params.put("currentTime",now().toString("HH:mm:ss"));
        params.put("venderid",venderid);
        params.put("user",getUser());
        try {
            detailsService.exportChaXunPdf(params, response);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
        }

    }


    /**
     * 导出数据-
     * @param
     * @return
     */
    @RequestMapping(value = "/export/invoicePoRepertoire/invoicePoListExportGF" )
    public void resultListExportGF(@RequestParam("matchnoList")String matchnoList , @RequestParam("venderid")String venderid, HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", matchnoList);
        JSONArray arr = JSONArray.fromObject(matchnoList);
        Map <String,Object> params =new HashMap<>();
        List<MatchEntity> list = new ArrayList<MatchEntity>();
        for(int i=0;i<arr.size();i++){
            String matchno = (String) arr.get(i);
            MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
            matchEntity.setMatchno(matchno);
            //获取匹配日期
            MatchEntity matchEntity1 = detailsService.selectMatchEntity(matchno);
            matchEntity.setMatchDate(matchEntity1.getMatchDate());
            //添加差异金额
            matchEntity.setMatchCover(matchEntity1.getCover());
            list.add(matchEntity);
       }
        params.put("list",list);
        params.put("currentDate", now().toString("yyyy-MM-dd"));
        UserEntity u1 = printCoverService.getUserName(venderid);
        getUser().setUsername(u1.getUsername() == null ?"":u1.getUsername());
        getUser().setUsercode(venderid);
        params.put("user",getUser());
        try {
            detailsService.exportPoPdf(params, response);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
        }

    }
    @RequestMapping(value = "/export/invoicePoRepertoire/checkCover" )
    public R checkCover(@RequestParam("matchnoList") String matchnoList){
        R r=new R();
        JSONArray arr = JSONArray.fromObject(matchnoList);
        String mno=arr.get(0).toString();
        MatchEntity match = detailsService.selectMatchEntity(mno);
        BigDecimal co=new BigDecimal(-20);
        BigDecimal cos=match.getCover();
        Integer a=match.getCover().compareTo(co.abs());
        Integer b=match.getCover().compareTo(co);
        if(a==1||b==-1){
            r.put("code",0);
        }
        else {
            r.put("code",1);
        }
        return r;
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("/export/invoicePoRepertoire/queryExport")
    public void QueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<MatchEntity> list = detailsService.queryListAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("detailsQueryExport", list);
        //生成excel
        //转换Excel数据
        List<MatchExcelEntity> list2=detailsService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"批量导入匹配导出","sheet1", ExcelTypeEnum.XLSX,MatchExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }


//        final DetailsQueryExport excelView = new DetailsQueryExport(map, "export/posuopei/pipeichaxun.xlsx", "detailsQueryExport");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "pipeichaxun" + excelNameSuffix);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出-匹配查询专用")
    @RequestMapping("/export/invoicePoRepertoire/queryExportx")
    public void QueryExportX(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        String venderId=getUser().getUsercode();
        params.put("venderid",venderId);
        List<MatchEntity> list = detailsService.queryListAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("detailsQueryExport", list);

        //转换Excel数据
        List<MatchExcelEntity> list2=detailsService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"匹配查询导出","sheet1", ExcelTypeEnum.XLSX,MatchExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }



        //生成excel
//        final DetailsQueryExport excelView = new DetailsQueryExport(map, "export/posuopei/pipeichaxun.xlsx", "detailsQueryExport");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "pipeichaxun" + excelNameSuffix);
    }
}
