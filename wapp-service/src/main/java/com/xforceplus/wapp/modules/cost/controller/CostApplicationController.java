package com.xforceplus.wapp.modules.cost.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.interfaceBPMS.LDAPUtils;
import com.xforceplus.wapp.modules.base.entity.Staff;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.cost.service.CostApplicationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/cost/application")
public class CostApplicationController extends AbstractController {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private final static Logger LOGGER = getLogger(CostApplicationController.class);

    @Autowired
    private CostApplicationService costApplicationService;

    @SysLog("获取供应商信息")
    @RequestMapping("/getUserInfo")
    public R getUserInfo() {
        SettlementEntity userInfo = costApplicationService.getUserInfo(getUserId());
        return R.ok().put("userInfo", userInfo);
    }

    @SysLog("获取供应商信息")
    @RequestMapping("/getGys")
    public R getGys(@RequestParam String venderid) {
        SettlementEntity userInfo = costApplicationService.getUserInfo(venderid);
        return R.ok().put("userInfo", userInfo);
    }

    @SysLog("获取费用类型信息")
    @RequestMapping("/getCostTypeOption")
    public R getCostTypeOption(@RequestParam String venderid, @RequestParam String businessType) {
        //获取供应商对应的费用类型
        List<SelectionOptionEntity> costTypeOptionList = costApplicationService.getCostType(venderid, businessType);
        return R.ok().put("costTypeOptionList", costTypeOptionList);
    }

    @SysLog("获取发票信息")
    @RequestMapping("/searchInvoice")
    public R searchInvoice(@RequestParam("invoiceCode") String invoiceCode, @RequestParam("invoiceNo") String invoiceNo, @RequestParam("orgcode") String orgcode) {
        RecordInvoiceEntity invoiceInfo = costApplicationService.searchInvoice(invoiceCode, invoiceNo, orgcode);
        return R.ok().put("invoiceInfo", invoiceInfo);
    }

    @SysLog("获取税率信息")
    @RequestMapping("/getRateOptions")
    public R getRateOptions() {
        List<SelectionOptionEntity> optionList = costApplicationService.getRateOptions();
        return R.ok().put("optionList", optionList);
    }

    @SysLog("获取员工信息")
    @RequestMapping("/getStaffInfo")
    public R getStaffInfo(@RequestParam String staffEmail) {
        Staff staff=null;
        try {
            LDAPUtils ldapUtils = new LDAPUtils();
            staffEmail=staffEmail+"@walmart.com";
            staff = ldapUtils.getUserByID(staffEmail);
        } catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        return R.ok().put("staffInfo", staff);
    }

    @SysLog("获取购方信息")
    @RequestMapping("/getGfInfo")
    public R getGfInfo() {
        List<SelectionOptionEntity> optionList = costApplicationService.getGfInfo();
        return R.ok().put("optionList", optionList);
    }


    @SysLog("获取成本中心信息")
    @RequestMapping("/getDeptInfo")
    public R getDeptInfo(@RequestParam String jv) {
        List<SelectionOptionEntity> optionList = costApplicationService.getDeptInfo(jv);
        return R.ok().put("optionList", optionList);
    }

    @SysLog("上传文件")
    @RequestMapping("/uploadFile")
    public R uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) {
        //上传文件到FTP服务器临时文件夹
        String filePath = costApplicationService.uploadFile(file);
        //上传成功后数据库记录相关信息
        SettlementFileEntity fileEntity = new SettlementFileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(fileType);
        //文件路径
        fileEntity.setFilePath(filePath);
        if(filePath.isEmpty()){
            return R.error("文件上传失败");
        }
        costApplicationService.saveFile(fileEntity);
        return R.ok().put("fileEntity", fileEntity);
    }

    @SysLog("查看文件")
    @RequestMapping("/viewFile")
    public void viewFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //根据id查询文件信息(文件路径和文件名)
        SettlementFileEntity fileEntity = costApplicationService.getFileInfo(id);
        //查看图片
        costApplicationService.viewImg(fileEntity.getFilePath(), fileEntity.getFileName(), response);
    }

    @SysLog("下载文件")
    @RequestMapping("/downloadFile")
    public void downloadFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //根据id查询文件信息(文件路径和文件名)
        SettlementFileEntity fileEntity = costApplicationService.getFileInfo(id);
        //查看图片
        costApplicationService.downloadFile(fileEntity.getFilePath(), fileEntity.getFileName(), response);
    }

    @SysLog("保存费用申请信息")
    @RequestMapping("/submitAll")
    public R submitAll(@RequestBody SettlementEntity settlement) {
        //生成费用号
        settlement.setCostNo(sdf.format(new Date()));
        // 获取登陆人的Loginname
        String loginname = costApplicationService.getLoginname(getUserId());
        try{
            settlement.setLoginName(loginname);
           String result = costApplicationService.submitAll(settlement);
           if("0".equals(result)){
               return R.ok();
           }else{
               return R.error();
           }
        } catch (Exception e){
            LOGGER.error("保存费用申请时异常:{}", e);
            e.printStackTrace();
            return R.error();
        }


    }


    /**
     * 费用申请信息导入
     */
    @SysLog("费用申请信息导入")
    @PostMapping("/excelImport")
    public Map<String, Object> staffImport(@RequestParam("file") MultipartFile multipartFile) {
        Map<String,Object> map = costApplicationService.parseExcel(multipartFile);
        return map;
    }


    @SysLog("获取购方税号")
    @RequestMapping("/getGfTaxNo")
    public R getGfTaxNo(@RequestParam("jvcode") String jvcode) {
        String res = costApplicationService.getGfTaxNo(jvcode);
        return R.ok().put("gfTaxNo", res);
    }

    @SysLog("获取购方税号")
    @RequestMapping("/getGfTaxNoByDept")
    public R getGfTaxNoByDept(@RequestParam("dept") String dept) {
        String res = costApplicationService.getGfTaxNoByDept(dept);
        return R.ok().put("gfTaxNo", res);
    }
}
