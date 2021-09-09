package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.CostEntity;
import com.xforceplus.wapp.modules.base.entity.JvEntity;
import com.xforceplus.wapp.modules.base.entity.Staff;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.export.CostTemplate;
import com.xforceplus.wapp.modules.base.export.StaffTemplate;
import com.xforceplus.wapp.modules.base.service.StaffService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redInvoiceManager.constant.Constants.IMPORT_RED_INVOICE;

/**
 * Created by tianhao.fu on 11/23/2018.
 * 员工信息管理控制层
 */
@RestController
public class StaffController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffController.class);

    @Autowired
    private StaffService staffService;


    /**
     * 员工信息查询
     */
    @PostMapping("/base/staff/query")
    public R getStaffInfos(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<Staff> list = staffService.queryList(query);
        Integer count = staffService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 供应商信息查询
     */
    @PostMapping("/base/staff/query1")
    public R getVendor1(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<UserEntity> list = staffService.queryList1(query);
        Integer count = staffService.queryCount1(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 供应商信息查询
     */
    @PostMapping("/base/staff/query2")
    public R getVendor2(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<UserEntity> list = staffService.queryList2(query);
        Integer count = staffService.queryCount2(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 供应商删除
     */
    @PostMapping("/base/staff/deleteVendor")
    public R deleteVendor(@RequestParam String ids) {
        staffService.deleteVendor(ids.split(","));
        return R.ok();
    }

    /**
     * 添加供应商
     */
    @PostMapping("/base/staff/addVendor")
    public R addVendor(@RequestParam String staff, @RequestParam String vendors) {
        staffService.addVendor(staff, vendors.split(","));
        return R.ok();
    }

    /**
     * 员工信息导入
     */
    @SysLog("员工信息导入")
    @PostMapping("/base/staff/excelImport")
    public Map<String, Object> staffImport(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("员工信息导入,params {}", multipartFile);
        Map<String,Object> map = staffService.parseExcel(multipartFile);
        return map;
    }

    /**
     * 员工信息删除
     */
    @SysLog("员工信息删除")
    @PostMapping("/base/staff/delete")
    public R deleteStaff(@RequestParam Map<String, Object> params) {
        String staffNo = params.get("staffNo")+"";
      staffService.deleteStaff(staffNo);
      return R.ok();
    }

    @SysLog("员工信息更新 ")
    @PostMapping("/base/staff/save")
    public R updateStaff(@RequestBody Staff staff) {
        staffService.updateStaff(staff);
        return R.ok();
    }

    /**
     * 员工信息模板下载
     */
    @SysLog("员工信息模板下载")
    @AuthIgnore
    @GetMapping("/export/staff/template")
    public void getTemplate(HttpServletResponse response) {
        //生成excel
        final StaffTemplate excelView = new StaffTemplate();
        excelView.write(response, "staffTemplate");
    }

    /**
     * jv信息查询
     */
    @PostMapping("/base/staff/queryJv")
    public R getJv(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<JvEntity> list = staffService.queryJv(query);
        Integer count = staffService.queryJvCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


    /**
     * 供应商信息查询
     */
    @PostMapping("/base/staff/queryJvNotAdd")
    public R getNotAddJv(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<JvEntity> list = staffService.queryNotAddJv(query);
        Integer count = staffService.queryNotAddJvCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


    /**
     * 添加jv信息
     * @param staff
     * @param
     * @return
     */
    @PostMapping("base/staff/addJvInfo")
    public R addJvInfo(@RequestParam String staff, @RequestParam String jvs) {
        staffService.addJvs(staff, jvs.split(","));
        return R.ok();
    }



    /**
     * JV删除
     */
    @PostMapping("/base/staff/deleteJv")
    public R deleteJv(@RequestParam String ids) {
        staffService.deleteJv(ids.split(","));
        return R.ok();
    }


    /**
     * 成本中心信息查询
     */
    @PostMapping("/base/staff/queryCost")
    public R getCost(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<CostEntity> list = staffService.queryCost(query);
        Integer count = staffService.queryCostCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


    @SysLog("员工信息更新 ")
    @PostMapping("base/staff/saveCost")
    public R addCost(@RequestBody CostEntity cost) {
    	int count =staffService.isExists(cost.getOrgid(),cost.getCostcode());
    	if(count==0) {
    		staffService.insertCost(cost);
    	}else {
    		return R.ok().put("error", "该jv下已存在成本中心");
    	}
        return R.ok();
    }


    /**
     * 成本中心删除
     */
    @PostMapping("/base/staff/deleteCost")
    public R deleteCost(@RequestParam String ids) {
        staffService.deleteCost(ids.split(","));
        return R.ok();
    }

    /**
     * jv与成本中心信息导入
     */
    @SysLog("jv与成本中心信息导入")
    @PostMapping("/base/staff/excelImportCbzx")
    public Map<String, Object> CbzxImport(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("jv与成本中心信息导入,params {}", multipartFile);
        Map<String,Object> map = staffService.parseCbzxExcel(multipartFile);
        return map;
    }


    /**
     * 成本中心信息模板下载
     */
    @SysLog("成本中心信息模板下载")
    @AuthIgnore
    @GetMapping("/export/costCenter/template")
    public void getCostTemplate(HttpServletResponse response) {
        //生成excel
        final CostTemplate excelView = new CostTemplate();
        excelView.write(response, "costCenterTemplate");
    }
}
