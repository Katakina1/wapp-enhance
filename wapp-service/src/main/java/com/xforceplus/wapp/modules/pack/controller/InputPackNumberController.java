package com.xforceplus.wapp.modules.pack.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadExcel;
import com.xforceplus.wapp.modules.pack.entity.InputPackNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.InputPackNumberExcelEntity;
import com.xforceplus.wapp.modules.pack.service.InputPackNumberService;
import com.xforceplus.wapp.modules.redTicket.entity.ReturnGoodsEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/pack/inputPackNumber")
public class InputPackNumberController extends AbstractController {



    @Autowired
    private InputPackNumberService inputPackNumberService;
    private static final Logger LOGGER = getLogger(InputPackNumberController.class);

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
        List<InputPackNumberEntity> list = inputPackNumberService.queryList(schemaLabel,query);
        int result = inputPackNumberService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    @SysLog("录入装箱号")
    @RequestMapping("/uplist")
    public R bbindingnobyId(@RequestBody InputPackNumberEntity inputPackNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        final int count = inputPackNumberService.querypackingno(schemaLabel,inputPackNumberEntity.getPackingNo());
//        if(count>0){
//              return R.error(1,"装箱号已经存在！");
//        }
//        查询列表数据
        inputPackNumberEntity.setSchemaLabel(schemaLabel);

        //录入装箱号
        inputPackNumberService.inputpackingno(schemaLabel, inputPackNumberEntity.getBbindingNos(),inputPackNumberEntity.getPackingNo());

        return R.ok();

    }

    @SysLog("查询导出")
    @RequestMapping("/exportExcel")
    public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        List<InputPackNumberEntity> inputPackNumberEntity = inputPackNumberService.getListAll(schemaLabel,params);

        //转换Excel数据
        List<InputPackNumberExcelEntity> list2=inputPackNumberService.transformExcle(inputPackNumberEntity);
        try {

            ExcelUtil.writeExcel(response,list2,"装箱导出","sheet1", ExcelTypeEnum.XLSX,InputPackNumberExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }


    }

    @RequestMapping("/bindingnolist")
    @SysLog("通过装订册号查询扫描列表")
    public R bindingNoListByNumber(@RequestParam Map<String, Object> params) {

        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //退货查列表数据
        Integer resultReturn = inputPackNumberService.getBindingnoListCount(query);
        List<InputPackNumberEntity> inputPackNumberEntity = inputPackNumberService.getBindingnoList(query);

        PageUtils pageUtil1 = new PageUtils(inputPackNumberEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }
    @SysLog("装箱号导入")
    @PostMapping(value = "/enterPackageNumber")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("装箱号导入,params {}", multipartFile);
        Map<String,Object> map = inputPackNumberService.parseExcel(multipartFile);
        return map;
    }
}
