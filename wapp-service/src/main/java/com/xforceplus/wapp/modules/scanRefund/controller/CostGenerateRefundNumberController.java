package com.xforceplus.wapp.modules.scanRefund.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.analysis.entity.InvoiceDataExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.export.CostGenerateRefundNumberAllExcel;
import com.xforceplus.wapp.modules.scanRefund.service.CostGenerateRefundNumberService;
import com.xforceplus.wapp.modules.signin.controller.SignatureProcessingController;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("")
public class CostGenerateRefundNumberController extends AbstractController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SignatureProcessingController.class);
    @Autowired
    private CostGenerateRefundNumberService costgenerateRefundNumberService;

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    @SysLog("扫描退票查询列表")
    @RequestMapping("/scanRefund/CostGenerateRefundNumber/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GenerateRefundNumberEntity> list = costgenerateRefundNumberService.queryList(schemaLabel, query);
        ReportStatisticsEntity result = costgenerateRefundNumberService.queryTotalResult(schemaLabel, query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @SysLog("生成退单号")
    @RequestMapping("/scanRefund/CostGenerateRefundNumber/uplist")
    public R bbindingnobyId(@RequestBody GenerateRefundNumberEntity generateRefundNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        //查询列表数据

        generateRefundNumberEntity.setSchemaLabel(schemaLabel);

        int a = generateRefundNumberEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            Date de = new Date();
            //查询最大装订册号
            GenerateRefundNumberEntity querymaxrebateno = costgenerateRefundNumberService.querymaxrebateno();
            if (querymaxrebateno != null) {
                String str2 = querymaxrebateno.getRebateNo();
                if (!str2.equals(null) && !str2.equals("")) {
                    str2 = str2.substring(0, 6);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                    String str = df.format(de);
                    if (str.equals(str2)) {
                        String str3 = querymaxrebateno.getRebateNo();
                        Long b = Long.valueOf(str3);
                        b = b + 1;
                        str3 = String.valueOf(b);
                        //生成退单号
                        costgenerateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str3);
                        GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
                        costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());

                    } else {
                        AtomicInteger atomicNum = new AtomicInteger();
                        int newNum = atomicNum.incrementAndGet();
                        String newStrNum = String.format("%06d", newNum);
                        String str4 = str + newStrNum;
                        //生成退单号
                        costgenerateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
                        GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
                        costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                    }
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                    String str = df.format(de);
                    AtomicInteger atomicNum = new AtomicInteger();
                    int newNum = atomicNum.incrementAndGet();
                    String newStrNum = String.format("%06d", newNum);
                    String str4 = str + newStrNum;
                    //生成退单号
                    costgenerateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
                    GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
                    costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                }

            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                String str = df.format(de);
                AtomicInteger atomicNum = new AtomicInteger();
                int newNum = atomicNum.incrementAndGet();
                String newStrNum = String.format("%06d", newNum);
                String str4 = str + newStrNum;
                //生成退单号
                costgenerateRefundNumberService.rebatenobyId(schemaLabel, generateRefundNumberEntity.getIds()[i], str4);
                GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(generateRefundNumberEntity.getIds()[i]);
                costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
            }
        }
        return R.ok();
    }

    @SysLog("查询eps")
    @RequestMapping("/scanRefund/CostGenerateRefundNumber/epsDetails")
    public R detail(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<GenerateRefundNumberEntity> epsList = costgenerateRefundNumberService.epsDetaList(schemaLabel, params);
        return R.ok().put("epsList", epsList);
    }

    @SysLog("生成退单号")
    @RequestMapping("/scanRefund/CostGenerateRefundNumber/epsuplist")
    public R epsbbindingnobyId(@RequestBody GenerateRefundNumberEntity generateRefundNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        generateRefundNumberEntity.setSchemaLabel(schemaLabel);

        int eps = generateRefundNumberEntity.getEpsNos().length;
        for (int e = 0;e < eps ;e++){
            List<GenerateRefundNumberEntity> list =  costgenerateRefundNumberService.queryepsno(generateRefundNumberEntity.getEpsNos()[e]);
            for (GenerateRefundNumberEntity lists :list ){


                Date de = new Date();
                //查询最大装订册号
                GenerateRefundNumberEntity querymaxrebateno = costgenerateRefundNumberService.querymaxrebateno();
                if (querymaxrebateno != null) {
                    String str2 = querymaxrebateno.getRebateNo();
                    if (!str2.equals(null) && !str2.equals("")) {
                        str2 = str2.substring(0, 6);
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                        String str = df.format(de);
                        if (str.equals(str2)) {
                            String str3 = querymaxrebateno.getRebateNo();
                            Long b = Long.valueOf(str3);
                            b = b + 1;
                            str3 = String.valueOf(b);
                            //生成退单号
                            costgenerateRefundNumberService.rebatenobyId(schemaLabel, lists.getId(), str3);
                            GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(lists.getId());
                            costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());

                        } else {
                            AtomicInteger atomicNum = new AtomicInteger();
                            int newNum = atomicNum.incrementAndGet();
                            String newStrNum = String.format("%06d", newNum);
                            String str4 = str + newStrNum;
                            //生成退单号
                            costgenerateRefundNumberService.rebatenobyId(schemaLabel, lists.getId(), str4);
                            GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(lists.getId());
                            costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                        }
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                        String str = df.format(de);
                        AtomicInteger atomicNum = new AtomicInteger();
                        int newNum = atomicNum.incrementAndGet();
                        String newStrNum = String.format("%06d", newNum);
                        String str4 = str + newStrNum;
                        //生成退单号
                        costgenerateRefundNumberService.rebatenobyId(schemaLabel, lists.getId(), str4);
                        GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(lists.getId());
                        costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                    }

                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                    String str = df.format(de);
                    AtomicInteger atomicNum = new AtomicInteger();
                    int newNum = atomicNum.incrementAndGet();
                    String newStrNum = String.format("%06d", newNum);
                    String str4 = str + newStrNum;
                    //生成退单号
                    costgenerateRefundNumberService.rebatenobyId(schemaLabel, lists.getId(), str4);
                    GenerateRefundNumberEntity uuid = costgenerateRefundNumberService.queryuuid1(lists.getId());
                    costgenerateRefundNumberService.rebatenobyuuid(uuid.getUuid());
                }
            }
            }

        //查询列表数据


        return R.ok();
    }


    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/scanRefund/ListAllExports/List")
    public void listAllExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("导出查询条件为:{}", params);

        //查询列表数据
        List<GenerateRefundNumberExcelEntity> list = costgenerateRefundNumberService.queryListAlls(schemaLabel,params);
        try {
            ExcelUtil.writeExcel(response,list,"费用生成退单号","sheet1", ExcelTypeEnum.XLSX,GenerateRefundNumberExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("costGenerateRefundNumber", list);
//        //生成excel
//        final CostGenerateRefundNumberAllExcel excelView = new CostGenerateRefundNumberAllExcel(map, "export/scanRefund/costGenerateRefundNumber.xlsx", "costGenerateRefundNumber");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "costGenerateRefundNumber" + excelNameSuffix);
    }
}