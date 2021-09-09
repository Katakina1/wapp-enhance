package com.xforceplus.wapp.modules.pack.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.QueryFlowTypeEntity;
import com.xforceplus.wapp.modules.pack.service.GenerateBindNumberService;
import com.xforceplus.wapp.modules.redTicket.service.GenerateRedTicketInformationService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
//import com.itextpdf.text.log.SysoLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicInteger;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/pack/GenerateBindNumber")
public class GenerateBindNumberController extends AbstractController {



    @Autowired
    private GenerateBindNumberService generateBindNumberService;
    private static final Logger LOGGER = getLogger(GenerateBindNumberController.class);
    @Autowired
    private GenerateRedTicketInformationService generateRedTicketInformationService;

    @RequestMapping("/gfOrg/list/query")
    @SysLog("查询机构代码")
    public R generateRedTicketData(@RequestBody OrganizationEntity organizationEntity) {
        LOGGER.info("查询机构代码,param{}",organizationEntity);
        OrganizationEntity list=generateRedTicketInformationService.queryGfCode(organizationEntity.getTaxno());
        return R.ok().put("list", list);
    }
    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描打包查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GenerateBindNumberEntity> list = generateBindNumberService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = generateBindNumberService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @SysLog("生成装订册号")
    @RequestMapping("/uplist")
    public R bbindingnobyId(@RequestBody GenerateBindNumberEntity generateBindNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        generateBindNumberEntity.setSchemaLabel(schemaLabel);

        int a = generateBindNumberEntity.getIds().length;

        AtomicInteger atomicNum = new AtomicInteger();
        int newNum = atomicNum.incrementAndGet();
        Date de = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String str = df.format(de);
        String str3="";
        //查询最大装订册号
        GenerateBindNumberEntity querymaxbbindingno = generateBindNumberService.querymaxbbindingno();
        if(querymaxbbindingno!=null) {
            String str2 = querymaxbbindingno.getBbindingNo();
            if (!str2.equals(null) && !str2.equals("")) {
                str2 = str2.substring(0, 6);
                if (str.equals(str2)) {
                    str3 = querymaxbbindingno.getBbindingNo();
                    int b = Integer.valueOf(str3);
                    b = b + 1;
                    str3 = String.valueOf(b);
                } else {
                    String newStrNum = String.format("%03d", newNum);
                    str3 = str + newStrNum;
                }
            }else{
                String newStrNum = String.format("%03d", newNum);
                str3 = str + newStrNum;
            }
        }else{
            String newStrNum = String.format("%03d", newNum);
            str3 = str + newStrNum;
        }
        for (int i = 0; i < a; i++) {
            generateBindNumberService.bbindingnobyId(schemaLabel, generateBindNumberEntity.getIds()[i], str3);
            // 每50使用同一条装订号自增一次
            if((i+1)%50==0) {
                int b = Integer.valueOf(str3);
                b = b + 1;
                str3 = String.valueOf(b);
            }

        }

     /*   for (int i = 0; i < a; i++) {
            Date de = new Date();
            //查询最大装订册号
            GenerateBindNumberEntity querymaxbbindingno = generateBindNumberService.querymaxbbindingno();
            if(querymaxbbindingno!=null) {
                String str2 = querymaxbbindingno.getBbindingNo();
                if (!str2.equals(null) && !str2.equals("")) {
                    str2 = str2.substring(0, 6);
                    SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
                    String str = df.format(de);
                    if (str.equals(str2)) {
                        String str3 = querymaxbbindingno.getBbindingNo();
                        int b = Integer.valueOf(str3);
                        b = b + 1;
                        str3 = String.valueOf(b);
                        //生成装订册号
                        generateBindNumberService.bbindingnobyId(schemaLabel, generateBindNumberEntity.getIds()[i], str3);

                    } else {
                        AtomicInteger atomicNum = new AtomicInteger();
                        int newNum = atomicNum.incrementAndGet();
                        String newStrNum = String.format("%03d", newNum);
                        String str4 = str + newStrNum;
                        //生成装订册号
                        generateBindNumberService.bbindingnobyId(schemaLabel, generateBindNumberEntity.getIds()[i], str4);

                    }
                }else {
                        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
                        String str = df.format(de);
                        AtomicInteger atomicNum = new AtomicInteger();
                        int newNum = atomicNum.incrementAndGet();
                        String newStrNum = String.format("%03d", newNum);
                        String str4 = str + newStrNum;
                        //生成装订册号
                        generateBindNumberService.bbindingnobyId(schemaLabel, generateBindNumberEntity.getIds()[i], str4);
                    }
            }else {
                SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
                String str = df.format(de);
                AtomicInteger atomicNum = new AtomicInteger();
                int newNum = atomicNum.incrementAndGet();
                String newStrNum = String.format("%03d", newNum);
                String str4 = str + newStrNum;
                //生成装订册号
                generateBindNumberService.bbindingnobyId(schemaLabel, generateBindNumberEntity.getIds()[i], str4);
            }
        }*/
        return R.ok();


    }

    @RequestMapping("/alllist")
    @SysLog("查询明细列表")
    public R allList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn1 = generateBindNumberService.getRecordInvoiceListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity1 = generateBindNumberService.getRecordInvoiceList(query);

        //PO查列表数据
        Integer resultReturn2 = generateBindNumberService.getPOListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity2 = generateBindNumberService.getPOList(query);

        //索赔查列表数据
        Integer resultReturn3 = generateBindNumberService.getClaimListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity3 = generateBindNumberService.getClaimList(query);

        PageUtils pageUtil1 = new PageUtils(generateBindNumberEntity1, resultReturn1, query.getLimit(), query.getPage());
        PageUtils pageUtil2 = new PageUtils(generateBindNumberEntity2, resultReturn2, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(generateBindNumberEntity3, resultReturn3, query.getLimit(),query.getPage());


        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2).put("page3", pageUtil3);

    }
    @RequestMapping("/recordinvoicelist")
    @SysLog("查询抵账信息列表")
    public R returnRecordInvoice(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = generateBindNumberService.getRecordInvoiceListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity = generateBindNumberService.getRecordInvoiceList(query);

        PageUtils pageUtil1 = new PageUtils(generateBindNumberEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }

    @RequestMapping("/POlist")
    @SysLog("查询PO信息列表")
    public R returnPOList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //PO查列表数据
        Integer resultReturn = generateBindNumberService.getPOListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity = generateBindNumberService.getPOList(query);

        PageUtils pageUtil1 = new PageUtils(generateBindNumberEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }


    @RequestMapping("/claimlist")
    @SysLog("查询索赔信息列表")
    public R returnClaimList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //索赔查列表数据
        Integer resultReturn = generateBindNumberService.getClaimListCount(query);
        List<GenerateBindNumberEntity> generateBindNumberEntity = generateBindNumberService.getClaimList(query);

        PageUtils pageUtil1 = new PageUtils(generateBindNumberEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }

    @RequestMapping("/searchFlowType")
    public R searchFlowType() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<QueryFlowTypeEntity> optionList = generateBindNumberService.searchFlowType();
        return R.ok().put("optionList", optionList);
    }
    @SysLog("装订册号导入")
    @PostMapping(value = "/enterPackageNumber")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("退票资料导入,params {}", multipartFile);
        Map<String,Object> map = generateBindNumberService.parseExcel(multipartFile);
        return map;
    }

}
