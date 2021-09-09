package com.xforceplus.wapp.modules.cost.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.cost.export.CostSettlementAllExcel;
import com.xforceplus.wapp.modules.cost.export.SettlementAllExcel;
import com.xforceplus.wapp.modules.cost.service.CostQueryService;
import com.xforceplus.wapp.modules.job.service.AdvanceService;
import com.xforceplus.wapp.modules.signin.controller.SignatureProcessingController;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

@RestController
public class CostQueryController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SignatureProcessingController.class);
    @Autowired
    private CostQueryService costQueryService;

    @Autowired
    private AdvanceService advanceService;

    @Autowired
    private BaseUserService userService;

    @SysLog("结算查询列表")
    @RequestMapping("/cost/query/list")
    public R list(@RequestParam Map<String, Object> params) {
        //获取登陆人的orgtype
        String orgtype = userService.getOrgtype(getUserId());
        //查询列表数据
        Query query = new Query(params);
        if("8".equals(orgtype)) {
            query.put("venderId", getUser().getUsercode());
        }
        String orgcode = userService.getOrgtypes(getUserId());
        if ("2333".equals(orgcode)){
            query.put("loginname", getUser().getLoginname());
        }
        List<SettlementEntity> list = costQueryService.queryList(query);
        Integer count = costQueryService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("结算明细")
    @RequestMapping("/cost/query/detail")
    public R detail(@RequestParam String costNo) {
        List<RecordInvoiceEntity> invoiceList = costQueryService.queryDetail(costNo);
        return R.ok().put("invoiceList", invoiceList);
    }

    @SysLog("结算文件明细")
    @RequestMapping("/cost/query/fileDetail")
    public R fielDetail(@RequestParam String costNo) {
        List<SettlementFileEntity> fileList = costQueryService.queryFileDetail(costNo);
        return R.ok().put("fileList", fileList);
    }

    @SysLog("获取审批状态信息")
    @RequestMapping("/cost/query/getStatusOptions")
    public R getStatusOptions() {
        List<SelectionOptionEntity> optionList = costQueryService.getStatusOptions();
        return R.ok().put("optionList", optionList);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/cost/costListAllExport/costLists")
    public void listAllExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("导出查询条件为:{}", params);
        //获取登陆人的orgtype
        String orgtype = userService.getOrgtype(getUserId());
        //查询列表数据
        if("8".equals(orgtype)) {
            params.put("venderId", getUser().getUsercode());
        }
        String orgcode = userService.getOrgtypes(getUserId());
        if ("2333".equals(orgcode)){
            params.put("loginname", getUser().getLoginname());
        }

        //查询列表数据
        List<SettlementEntity> list = costQueryService.queryAllList(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("costSettlementAll", list);
        //生成excel
        final CostSettlementAllExcel excelView = new CostSettlementAllExcel(map, "export/cost/costSettlementAll.xlsx", "costSettlementAll");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "costSettlementAll" + excelNameSuffix);
    }
    @SysLog("修改费用补录申请人")
    @RequestMapping("/cost/updateUser")
    public R updateUser(@RequestParam Map<String, Object> params) {
    costQueryService.updateUser(params.get("rzUserId").toString(),params.get("lzUserId").toString());
    return R.ok();
    }


    @SysLog("获取预付款数据")
    @RequestMapping("/cost/dataFromBPMS")
    public R dataFromBPMS(@RequestParam Map<String, Object> params) {
        boolean a= advanceService.getDataFromBPMS(params.get("epsNos").toString());
        if (a){
            return R.ok().put("msgs", "获取预付款成功！");
        }else {
            return R.ok().put("msgs", "获取预付款失败！");
        }

    }
}
