package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.export.InvoiceDetailExport;
import com.xforceplus.wapp.modules.fixed.service.MatchQueryService;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;


@RestController
public class MatchQueryController extends AbstractController {
    private static final Logger LOGGER= getLogger(MatchQueryController.class);
    @Autowired
    MatchQueryService matchQueryService;
    /**
     * 查询匹配信息
     * */
    @SysLog("查询匹配信息")
    @RequestMapping("modules/fixed/matchQuery/list")
    public R queruList(@RequestParam Map<String, Object> map){
        Query query =new Query(map);
        Integer result = matchQueryService.queryTotal(query);
        List<MatchQueryEntity> list = matchQueryService.querylist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page",pageUtil);
    }

    /**
     * 查询详细图片
     * */
    @SysLog("查询详细图片")
    @RequestMapping("modules/fixed/matchQuery/img")
    public R queryDetail(@RequestParam String uuid){
        List<String> imgList = matchQueryService.queryDetail(uuid);
        return R.ok().put("imgList",imgList);
    }

    /**
     * 取消匹配
     * */
    @SysLog("取消匹配")
    @RequestMapping("modules/fixed/matchQuery/cancel")
    public R cancelMatch(MatchQueryEntity entity){
        String msg = "";
        try{
            matchQueryService.cancelMatch(entity);
            msg = "取消匹配成功！";
        }catch (Exception e){
            LOGGER.debug(e.toString());
            e.printStackTrace();
            msg = "取消匹配失败！";
        }

        return R.ok().put("msg",msg);
    }
    /**
     * 点击匹配明细按钮后的查询
     * @param matchId
     * @return
     */
    @SysLog("匹配明细")
    @RequestMapping("modules/fixed/matchQuery/detail")
    public R detail(@RequestParam("matchId") Long matchId) {
        List<RecordInvoiceEntity> invoiceList = matchQueryService.getDetailInvoice(matchId);
        List<OrderEntity> orderList = matchQueryService.getDetailOrder(matchId);
        return R.ok().put("invoiceList", invoiceList).put("orderList", orderList);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询发票导出")
    @RequestMapping("export/fixed/invoiceDetailExport")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
//        Query query =new Query(params);
        //查询列表数据
        List<RecordInvoiceDetail> list = matchQueryService.exportDetailInvoice(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("invoiceDetail", list);
        //生成excel
        final InvoiceDetailExport excelView = new InvoiceDetailExport(map, "export/fixed/invoiceDetail.xlsx", "invoiceDetail");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "invoiceDetail" + excelNameSuffix);
    }
    /**
     * 点击明细按钮后的查询
     * @param value
     * @return
     */
    @SysLog("明细")
    @RequestMapping("modules/fixed/matchQuery/invoice")
    public R getInvoinceDetail(@RequestBody String value){
        final String schemaLabel = getCurrentUserSchemaLabel();
        R.error("未找到明细信息");
        InvoicesEntity invoiceEntity;
        if(StringUtils.isEmpty(value) || "null".equals(value)) {
            return R.error(1,"未查找到明细信息");
        }
        Long id=Long.parseLong(value);
        try{
            invoiceEntity=matchQueryService.getDetailInfo(schemaLabel,id);
        }catch(Exception e){
            LOGGER.error("明细 {}",e);
            return R.error(1,"未查找到明细信息");
        }

        final List<InvoicesEntity> outList = matchQueryService.getOutInfo(schemaLabel, invoiceEntity.getInvoiceCode()+invoiceEntity.getInvoiceNo());

        R r= R.ok();
        String flag=invoiceEntity.getInvoiceType();
        final String vehicleInvoice="03";//机动车销售统一发票
        if (flag.equals(vehicleInvoice)){
            DetailVehicleEntity detailVehicleEntity=new DetailVehicleEntity();
            try {
                detailVehicleEntity=matchQueryService.getVehicleDetail(schemaLabel, id);
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
        List<DetailEntity> detailEntityList=matchQueryService.getInvoiceDetail(schemaLabel, id);
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

    @SysLog("文件信息查询")
    @RequestMapping("modules/fixed/matchQuery/fileInfo")
    public R fileInfo(@RequestParam Map<String, Object> params){
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        //  Query query = new Query(params);
        // List<InvoiceEntity> Questionlist = questionOrderService.queryInvoice(schemaLabel,params);
        //ReportStatisticsEntity result = compreh ensiveInvoiceQueryService.queryTotalResult(schemaLabel,query);
        //Integer countOrder = questionOrderService.countOrders( schemaLabel,query);
        // PageUtils pageUtil = new PageUtils(Questionlist, Questionlist.size(), 0, 0);
        List<FileEntity> filelist = matchQueryService.queryFileName(schemaLabel,params);

        for (FileEntity fileEntity : filelist) {
            String filepath=fileEntity.getFilePath();
            if((filepath.toLowerCase()).endsWith(".bmp")||
                    (filepath.toLowerCase()).endsWith(".psd")||
                    (filepath.toLowerCase()).endsWith(".tiff")||
                    (filepath.toLowerCase()).endsWith(".jpg")||
                    (filepath.toLowerCase()).endsWith(".png")||
                    (filepath.toLowerCase()).endsWith(".gif")||
                    (filepath.toLowerCase()).endsWith(".jpeg")){

                fileEntity.setFileType("0");
            }
        }
        return  R.ok().put("file",filelist);
    }

    @SysLog("获取图片--资料")
    @RequestMapping("modules/fixed/matchQuery/getImageForAll")
    public void getImageForAll(@RequestParam("id") Long id, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片资料--------------------");
        //final String schemaLabel = getCurrentUserSchemaLabel();
        matchQueryService.getInvoiceImageForAll( id, getUser(), response);
    }

    @SysLog("下载文件")
    @RequestMapping("modules/fixed/matchQuery/downloadFile")
    public void downloadFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //根据id查询文件信息(文件路径和文件名)
        FileEntity fileEntity = matchQueryService.getFileInfo(id);
        try{

            String[] type=fileEntity.getFilePath().split("\\.");
            //查看图片
            matchQueryService.downloadFile(fileEntity.getFilePath(), fileEntity.getFileName(), response);
        }catch(Exception e){
            LOGGER.debug("----------------文件路径没有,不能下载--------------------");
            e.printStackTrace();
        }

    }

}
