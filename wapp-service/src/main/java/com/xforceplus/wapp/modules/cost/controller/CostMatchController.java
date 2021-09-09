package com.xforceplus.wapp.modules.cost.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementMatchEntity;
import com.xforceplus.wapp.modules.cost.service.CostMatchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cost/match")
public class CostMatchController extends AbstractController {

    @Autowired
    private CostMatchService costMatchService;

    @Autowired
    private BaseUserService userService;

    @SysLog("费用匹配列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        String venderid = (String)params.get("venderId");
        if(venderid==null || venderid.length()==0){
            params.put("venderId", getUser().getUsercode());
        }
        Query query = new Query(params);
        List<SettlementMatchEntity> list = costMatchService.queryList(query);
        Integer count = costMatchService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("费用匹配明细列表")
    @RequestMapping("/detail")
    public R detail(@RequestParam String costNo) {
        List<CostEntity> detailList = costMatchService.queryDetail(costNo);
        return R.ok().put("detailList", detailList);
    }

    @SysLog("费用匹配选择明细列表")
    @RequestMapping("/selectDetail")
    public R selectDetail(@RequestParam String costNos) {
        String[] costNoArray = costNos.split(",");
        List<CostEntity> detailList = costMatchService.querySelectDetail(costNoArray);
        return R.ok().put("detailList", detailList);
    }

    @SysLog("验证发票是否重复")
    @RequestMapping("/checkInvoice")
    public boolean checkInvoice(@RequestBody RecordInvoiceEntity invoice) {
        return costMatchService.selectInvoice(invoice)>0;
    }

    @SysLog("获取Orgtype")
    @RequestMapping("/getOrgtype")
    public R getOrgtype() {
        String orgtype = userService.getOrgtype(getUserId());
        return R.ok().put("orgtype", orgtype);
    }

    /**
     * 费用匹配信息导入
     */
    @SysLog("费用匹配信息导入")
    @PostMapping("/excelImport")
    public Map<String, Object> batchImport(@RequestParam("file") MultipartFile multipartFile) {
        Map<String,Object> map = costMatchService.parseExcel(multipartFile);
        return map;
    }
}
