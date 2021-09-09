package com.xforceplus.wapp.modules.scanRefund.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.report.entity.FpghExcelEntity;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.SctdhExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.export.CostGenerateRefundNumberAllExcel;
import com.xforceplus.wapp.modules.scanRefund.export.GenerateRefundNumberAllExcel;
import com.xforceplus.wapp.modules.scanRefund.service.GenerateRefundNumberService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/GenerateRefundNumber")
public class GenerateRefundNumberController extends AbstractController {


    @Autowired
    private GenerateRefundNumberService generateRefundNumberService;
    @Autowired
    private DetailsService detailsService;
    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    @SysLog("扫描退票查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GenerateRefundNumberEntity> list = generateRefundNumberService.queryList(schemaLabel, query);
        ReportStatisticsEntity result = generateRefundNumberService.queryTotalResult(schemaLabel, query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("/listAll")
    public void listAllExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        //Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        List<GenerateRefundNumberEntity> list = generateRefundNumberService.queryListAll(schemaLabel, params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("generateRefundNumber", list);


        try {
            List<SctdhExcelEntity> list2=generateRefundNumberService.transformExcle(list);
            ExcelUtil.writeExcel(response,list2,"生成退单号导出","sheet1", ExcelTypeEnum.XLSX,SctdhExcelEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        //生成excel
//        final GenerateRefundNumberAllExcel excelView = new GenerateRefundNumberAllExcel(map, "export/scanRefund/generateRefundNumber.xlsx", "generateRefundNumber");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "generateRefundNumber" + excelNameSuffix);
    }
    @SysLog("生成退单号")
    @RequestMapping("/uplist")
    public R bbindingnobyId(@RequestBody GenerateRefundNumberEntity generateRefundNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        //查询列表数据

        generateRefundNumberEntity.setSchemaLabel(schemaLabel);

        int a = generateRefundNumberEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            //查询最大装订册号
//            GenerateRefundNumberEntity querymaxrebateno = generateRefundNumberService.querymaxrebateno();
//            if (querymaxrebateno != null) {
//                String str2 = querymaxrebateno.getRebateNo();
//                if (!str2.equals(null) && !str2.equals("")) {
//                    str2 = str2.substring(0, 6);
//                    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
//                    String str = df.format(de);
//                    //if (str.equals(str2)) {
//                        String str3 = querymaxrebateno.getRebateNo();
//                        Long b = Long.valueOf(str3);
//                        b = b + 1;
//                        str3 = String.valueOf(b);
                        //生成退单号
                        Date de = new Date();
                        SimpleDateFormat f=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        String str3=f.format(de);
                        generateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str3);
                        GenerateRefundNumberEntity uuid = generateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
                        generateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                        //通过发票uuid查询抵账matchno并取消匹配关系
                        String matchNo=detailsService.selectMatchNo(uuid.getUuid());
                        if(StringUtils.isNotEmpty(matchNo)){
                            detailsService.matchCancel(matchNo);
                        }

//                    } else {
//                        AtomicInteger atomicNum = new AtomicInteger();
//                        int newNum = atomicNum.incrementAndGet();
//                        String newStrNum = String.format("%06d", newNum);
//                        String str4 = str + newStrNum;
//                        //生成退单号
//                        generateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
//                        GenerateRefundNumberEntity uuid = generateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
//                        generateRefundNumberService.rebatenobyuuid(uuid.getUuid());
//                        //通过发票id查询matchno并取消匹配关系
//                        String matchNo=detailsService.selectMatchNo(uuid.getUuid());
//                        if(StringUtils.isNotEmpty(matchNo)){
//                            detailsService.matchCancel(matchNo);
//                        }
//                    }
//                } else {
//                    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
//                    String str = df.format(de);
//                    AtomicInteger atomicNum = new AtomicInteger();
//                    int newNum = atomicNum.incrementAndGet();
//                    String newStrNum = String.format("%06d", newNum);
//                    String str4 = str + newStrNum;
//                    //生成退单号
//                    generateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
//                    GenerateRefundNumberEntity uuid = generateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
//                    generateRefundNumberService.rebatenobyuuid(uuid.getUuid());
//                    //通过发票id查询matchno并取消匹配关系
//                    String matchNo=detailsService.selectMatchNo(uuid.getUuid());
//                    if(StringUtils.isNotEmpty(matchNo)){
//                        detailsService.matchCancel(matchNo);
//                    }
//                }

//            } else {
//                SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
//                String str = df.format(de);
//                AtomicInteger atomicNum = new AtomicInteger();
//                int newNum = atomicNum.incrementAndGet();
//                String newStrNum = String.format("%06d", newNum);
//                String str4 = str + newStrNum;
//                //生成退单号
//                generateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
//                GenerateRefundNumberEntity uuid = generateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
//                generateRefundNumberService.rebatenobyuuid(uuid.getUuid());
//                //通过发票id查询matchno并取消匹配关系
//                String matchNo=detailsService.selectMatchNo(uuid.getUuid());
//                if(StringUtils.isNotEmpty(matchNo)){
//                    detailsService.matchCancel(matchNo);
//                }
//            }
        }
        return R.ok();
    }
}