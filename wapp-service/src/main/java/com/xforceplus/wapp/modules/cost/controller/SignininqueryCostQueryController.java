package com.xforceplus.wapp.modules.cost.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.QuestionnaireExcel;
import com.xforceplus.wapp.modules.analysis.entity.InvoiceDataExcelEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.export.SettlementAllExcel;
import com.xforceplus.wapp.modules.cost.service.SignininqueryCostQueryService;
import com.xforceplus.wapp.modules.signin.controller.SignatureProcessingController;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

@RestController
public class SignininqueryCostQueryController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SignatureProcessingController.class);
    @Autowired
    private SignininqueryCostQueryService signininqueryCostQueryService;

    @Autowired
    private BaseUserService userService;

    @SysLog("结算查询列表")
    @RequestMapping("/cost/query/lists")
    public R list(@RequestParam Map<String, Object> params) {
        //获取登陆人的orgtype
        String orgtype = userService.getOrgtype(getUserId());
        //查询列表数据
        Query query = new Query(params);
        List<SettlementEntity> list = signininqueryCostQueryService.queryList(query);
        Integer count = signininqueryCostQueryService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("结算明细")
    @RequestMapping("/cost/query/details")
    public R detail(@RequestParam String costNo) {
        List<RecordInvoiceEntity> invoiceList = signininqueryCostQueryService.queryDetail(costNo);
        return R.ok().put("invoiceList", invoiceList);
    }
    @SysLog("结算对比")
    @RequestMapping("/cost/query/confirmDateContrast")
    public R details(@RequestParam String costNo) {
        List<ContrastEntity> invoiceContrastList = signininqueryCostQueryService.queryDetails(costNo);
        return R.ok().put("invoiceContrastList", invoiceContrastList);
    }
    @SysLog("结算文件明细")
    @RequestMapping("/cost/query/fileDetails")
    public R fielDetail(@RequestParam String costNo) {
        List<SettlementFileEntity> fileList = signininqueryCostQueryService.queryFileDetail(costNo);
        return R.ok().put("fileList", fileList);
    }

    @SysLog("获取审批状态信息")
    @RequestMapping("/cost/query/getStatusOptionss")
    public R getStatusOptions() {
        List<SelectionOptionEntity> optionList = signininqueryCostQueryService.getStatusOptions();
        return R.ok().put("optionList", optionList);
    }

    /**
     * 数据删除--根据uuid去更新签收状态
     *
     * @param params
     * @return
     */
    @RequestMapping("/cost/query/deleteRevoices")
    public R deleteIncoices(@RequestBody Map<String, Object> params) {
        UserEntity user = getUser();
        Boolean a = false;
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            /*String uuid = String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo"));*/
            //退票原因判断
            // 若签收失败取签收失败原因
            //若签收成功 取匹配状态 0（平台无匹配关系）  2匹配失败原因
            String costNo=String.valueOf(params.get("costNo"));
            //推送BPMS退票状态
            String instanceId = String.valueOf(params.get("instanceId"));

            String epsNo = String.valueOf(params.get("epsNo"));

            String refundReason= String.valueOf(params.get("refundReason"));

            String refundCode= String.valueOf(params.get("refundCode"));
            //属于
            String belongsTo= String.valueOf(params.get("belongsTo"));
            //费用类型  0 非预付款  1  预付款
            String payModel = String .valueOf(params.get("payModel"));

          a = signininqueryCostQueryService.deleteMsgById(schemaLabel,costNo,instanceId,epsNo,refundReason,refundCode,belongsTo,payModel);

        if (a){
            if("1".equals(payModel)) {
                //退票成功 设置预付款的金额回退，发票清空costNo
                signininqueryCostQueryService.rebackYf(costNo,epsNo);
            }
            return R.ok().put("msg", "退票成功！");
        }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return R.ok().put("msg", "退票失败！");
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/cost/listAllExport/lists")
    public void listAllExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);

        //查询列表数据
        List<SettlementExcelEntity> list = signininqueryCostQueryService.queryAllList(params);

        try {
            ExcelUtil.writeExcel(response,list,"费用扫描处理","sheet1", ExcelTypeEnum.XLSX,SettlementExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("settlementAll", list);
//        //生成excel
//        final SettlementAllExcel excelView = new SettlementAllExcel(map, "export/cost/settlementAll.xlsx", "settlementAll");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "settlementAll" + excelNameSuffix);
    }

}
