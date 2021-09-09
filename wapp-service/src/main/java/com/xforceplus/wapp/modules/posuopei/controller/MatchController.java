package com.xforceplus.wapp.modules.posuopei.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.interfaceBPMS.Table;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.export.AddClaimImport;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class MatchController extends AbstractController {
    private final static Logger LOGGER = getLogger(MatchController.class);
    private MatchService matchService;
    @Autowired
    public MatchController(MatchService matchService){
        this.matchService=matchService;
    }

    /**
     * po查询
     * @param params
     * @return
     */
    @SysLog("po查询")
    @PostMapping(value = POSUOPEI_PO_QUERY)
    public R poQuery(@RequestBody Map<String,Object> params){
        LOGGER.info("po查询,param{}",params);

        PagedQueryResult<PoEntity> poEntityPagedQueryResult=matchService.poQueryList(params);
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),poEntityPagedQueryResult.getTotalCount(),0,0);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 索赔查询
     * @param params
     * @return
     */
    @SysLog("索赔查询")
    @PostMapping(value = POSUOPEI_CLAIM_QUERY)
    public R claimQuery(@RequestBody Map<String,Object> params){
        LOGGER.info("索赔查询,param{}",params);

        PagedQueryResult<ClaimEntity> pagedQueryResult=matchService.claimQueryList(params);
        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),0,0);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 问题单查询
     * @param params
     * @return
     */
    @SysLog("问题单查询")
    @PostMapping(value = "/modules/posuopei/questionPaper/query")
    public R questionPaperQuery(@RequestParam Map<String,Object> params){
        LOGGER.info("问题单查询,param{}",params);
        Query query=new Query(params);
        PagedQueryResult<QuestionPaperEntity> paperEntityPagedQueryResult=matchService.questionPaperQuery(query);
        PageUtils pageUtils=new PageUtils(paperEntityPagedQueryResult.getResults(),paperEntityPagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }

    /**
     * 问题单明细查询
     * @param params
     * @return
     */
    @SysLog("问题单明细查询")
    @PostMapping(value = "/modules/posuopei/questionPaper/detailQuery")
    public R questionPaperDetailQuery(@RequestParam Map<String,Object> params){
        LOGGER.info("问题单明细查询,param{}",params);

        PagedQueryResult<Object> paperEntityPagedQueryResult=matchService.questionPaperDetailQuery(params);
        PageUtils pageUtils=new PageUtils(paperEntityPagedQueryResult.getResults(),paperEntityPagedQueryResult.getResults().size(),0,0);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 问题单撤销
     * @param params
     * @return
     */
    @SysLog("问题单撤销")
    @PostMapping(value = "/modules/posuopei/questionPaper/deleteQuestionPaper")
    public R deleteQuestionPaper(@RequestBody Map<String,Object> params){
        LOGGER.info("问题单撤销,param{}",params);
        String id=String.valueOf(params.get("id"));
        matchService.deleteQuestion(Integer.valueOf(id));
        return R.ok();
    }
    /**
     * 发票录入
     * @param params
     * @return
     */
    @SysLog("发票录入")
    @PostMapping(value = POSUOPEI_INVOICE_SAVE)
    public R invoiceIn(@RequestBody Map<String,Object> params){
        String venderid= (String) params.get("venderid");
        if(StringUtils.isEmpty(venderid)){

            params.put("venderid",getUser().getUsercode());
        }else{
            params.put("venderid",venderid);
        }
        LOGGER.info("发票录入,param{}",params);
        Boolean flag=true;
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = matchService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            if(params.get("gfName").equals(org.getOrgname())){
                params.put("gfTaxno",org.getTaxno());
            }
        });
        if(StringUtils.isEmpty((String)params.get("taxRate"))){
            return R.error(488, "请选择税率");
        }
        if("04".equals(matchService.getFplx((String)params.get("invoiceCode")))){
            if(((String)params.get("checkNo"))==null){
                return R.error(488, "校验码格式错误！");
            } else if(((String)params.get("checkNo")).length()!=6){
                return R.error(488, "校验码格式错误！");
            }
        }

        if(!CommonUtil.isValidNum((String)params.get("invoiceCode"),"^(\\d{10}|\\d{12})$")) {
            return R.error(488, "发票代码格式错误");
        }else if(!CommonUtil.isValidNum((String)params.get("invoiceNo"),"^[\\d]{8}$")){
            return R.error(488, "发票号码格式错误");
        }else if(!CommonUtil.isValidNum((String)params.get("taxRate"),"^[0-9]*$")){
            if(!"1.5".equals(params.get("taxRate"))){
                return R.error(488, "税率格式错误");
            }
        }else if((!CommonUtil.isValidNum((String)params.get("invoiceAmount"),"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum((String)params.get("totalAmount"),"^[0-9]+(.[0-9]{2})?$"))||(!CommonUtil.isValidNum((String)params.get("taxAmount"),"^[0-9]+(.[0-9]{2})?$"))){
            return R.error(488, "金额格式错误,请保留两位小数");
        }else if(!("04".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))||"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode"))))){
            return R.error(488, "发票代码格式错误");
        }
        if((new BigDecimal((String)params.get("invoiceAmount")).add(new BigDecimal((String)params.get("taxAmount")))).compareTo(new BigDecimal((String)params.get("totalAmount")))!=0){
            return R.error(488, "价税合计必须为发票金额和税额之和");
        }
        BigDecimal amount=new BigDecimal((String)params.get("invoiceAmount"));
        BigDecimal rate=new BigDecimal((String) params.get("taxRate")).divide(new BigDecimal(100));;
        BigDecimal taxAmount=new BigDecimal((String)params.get("taxAmount"));
        BigDecimal rest=taxAmount.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
        if(rest.compareTo(BigDecimal.ZERO)>0){
            flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

        }else{
            flag=rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0;
        }

        if(!flag&&!("04".equals(matchService.getFplx((String)params.get("invoiceCode"))))){
            return R.error(488,"税额比对相差不能超过正负0.05元");
        }
        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=matchService.invoiceQueryList(params);
        if(poEntityPagedQueryResult.getMsg()!=null){
            return R.error(488,poEntityPagedQueryResult.getMsg());
        }
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),poEntityPagedQueryResult.getTotalCount(),0,0);
        return R.ok().put("page",pageUtils);
    }


    /**
     * 发票带出
     * @param params
     * @return
     */
    @SysLog("发票带出")
    @PostMapping(value = POSUOPEI_INVOICE_QUERY)
    public R invoiceQuery(@RequestBody Map<String,Object> params){
        Long userId=getUserId();
        String venderid= (String) params.get("venderid");
        if(StringUtils.isEmpty(venderid)){
            params.put("venderid",getUser().getUsercode());
        }else{
            params.put("venderid",venderid);
        }
        List<OrgEntity> gfNameAndTaxNoList = matchService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            if(params.get("gfName").equals(org.getOrgname())){
                params.put("gfTaxno",org.getTaxno());
            }
        });
        LOGGER.info("发票带出,param{}",params);
        Boolean flag=true;
         if(!("04".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))||"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode"))))){
            return R.error(488, "发票代码格式错误");
        }
        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=matchService.invoiceQueryOut(params);
        List<InvoiceEntity> list1=poEntityPagedQueryResult.getResults();

        if(!(poEntityPagedQueryResult.getMsg()==null)){
            return R.error(488,poEntityPagedQueryResult.getMsg());
        }
        if(list1.size()>0){
            if(null==list1.get(0).getInvoiceAmount()){
                list1.get(0).setInvoiceAmount(new BigDecimal(0));
            }
            BigDecimal amount=list1.get(0).getInvoiceAmount();
            if(null==list1.get(0).getTaxRate()){
                list1.get(0).setTaxRate(new BigDecimal(0));
            }
            BigDecimal rate=list1.get(0).getTaxRate().divide(new BigDecimal(100));
            if(null==list1.get(0).getTaxAmount()){
                list1.get(0).setTaxAmount(new BigDecimal(0));
            }
            BigDecimal taxAmount=list1.get(0).getTaxAmount();
            BigDecimal rest=taxAmount.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
            if(rest.compareTo(BigDecimal.ZERO)>0){
                flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

            }else{
                flag=(rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);
            }
            if((amount.add(taxAmount)).compareTo(list1.get(0).getTotalAmount())!=0){
                return R.error(488, "价税合计必须为发票金额和税额之和");
            }
        }


        if(!flag){
            return R.error(488,"税额比对相差不能超过正负0.05元");
        }
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),poEntityPagedQueryResult.getTotalCount(),0,0);
        return R.ok().put("page",pageUtils);
    }
    /**
     * 进入页面带出购方名称
     * @return
     */
    @SysLog("带出所属的购方名称")
    @RequestMapping(POSUOPEI_PO_GFTAXNO)
    public R getGfNameAndTaxNoXF(){
        Long userId=getUserId();
        List<OrgEntity> list = matchService.getGfNameAndTaxNo(userId);
        R r= R.ok();
        r.put("List",list);
        return r;
    }


    @SysLog("带出分区")
    @RequestMapping(POSUOPEI_GET_PARTION)
    public R getPartion(@RequestBody String theKey){
        List<OrgEntity> list = matchService.getPartion(theKey);
        R r= R.ok();
        r.put("List",list);
        return r;
    }

    @SysLog("带出城市")
    @RequestMapping(POSUOPEI_GET_CITY)
    public R getPCity(){
        List<Table> list = matchService.getCity();
        R r= R.ok();
        r.put("List",list);
        return r;
    }

    @SysLog("带出业务字典信息")
    @RequestMapping(POSUOPEI_GET_DICDETA)
    public R getDictdeta(@RequestBody(required = false) String theKey){
        List<OrgEntity> list = matchService.getDicdeta(theKey);
        R r= R.ok();
        r.put("List",list);
        return r;
    }





    /**
     * 进入页面带出购方名称
     * @return
     */
    @SysLog("带出默认信息")
    @RequestMapping(POSUOPEI_PO_DEFAULT_MESSAGE)
    public R getDefaultMessage(){
        Long userId=getUserId();
        OrgEntity orgEntity = matchService.getDefaultMessage(userId);
        R r= R.ok();
        r.put("orgEntity",orgEntity);
        if(orgEntity!=null){
            r.put("isOk","yes");
        }else{
            r.put("isOk","no");
        }
        return r;
    }

    @SysLog("匹配")
    @RequestMapping("modules/posuopei/invoice/match")
    public R  matchedData(@RequestBody MatchEntity matchEntity){

        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = matchService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            if(matchEntity.getGfName().equals(org.getOrgname())){
                matchEntity.setGfTaxNo(org.getTaxno());
            }
        });
        String benId=getUser().getUsercode();
        String msg=matchService.saveMatch(matchEntity,benId);

        return R.ok().put("msg",msg);
    }

    @SysLog("保存问题单")
    @RequestMapping("modules/posuopei/question/save")
    public R  saveQuestionPaper(@RequestBody QuestionPaperEntity questionPaperEntity){
        Boolean flag=false;
        if(questionPaperEntity.getId()!=null){
            flag= matchService.updateQuestionPaper(questionPaperEntity);
        }else{
            flag= matchService.saveQuestionPaper(questionPaperEntity);
        }
        return R.ok().put("msg","保存成功");
    }

    @SysLog("导入发票信息")
    @PostMapping("modules/invoiceImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile,@RequestParam("gfName") String gfName,@RequestParam("jvcode") String jvcode,@RequestParam("venderid") String venderid) {
        LOGGER.info("导入认证，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        if(StringUtils.isEmpty(venderid)){
            params.put("venderid",getUser().getUsercode());
        }else{
            params.put("venderid",venderid);
        }




        params.put("jvcode",jvcode);
        params.put("gfName",gfName);
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = matchService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            if(gfName.equals(org.getOrgname())){
                params.put("gfTaxno",org.getTaxno());
            }
        });



            return matchService.importInvoice(params,multipartFile);

    }

    @SysLog("导入匹配关系")
    @PostMapping("modules/matchImport") 
    public Map<String, Object> importMatch(@RequestParam("file") MultipartFile multipartFile,@RequestParam("venderid") String venderid, @RequestParam("orgtype") String orgtype) {
        LOGGER.info("导入匹配关系，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        params.put("venderid",venderid);
        if("8".equals(orgtype)){
            if(StringUtils.isEmpty(venderid)){
                params.put("venderid",getUser().getUsercode());
            }else{
                params.put("venderid",venderid);
            }
        }
        Map<String, Object> map=matchService.importMatch(params,multipartFile,orgtype);
        return map;
    }

    @SysLog("导出发票模板")
    @AuthIgnore
    @GetMapping("export/invoiceImportExport")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出发票模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/posuopei/invoiceImport.xlsx");
        excelView.write(response, "importTemplate");
    }

    @SysLog("导出匹配模板")
    @AuthIgnore
    @GetMapping("export/matchImportExport")
    public void exportMatchTemplate(HttpServletResponse response) {
        LOGGER.info("导出匹配模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/posuopei/matchImports.xlsm");
        excelView.writeXlsm(response, "matchTemplate");
    }

    @SysLog("导出匹配模板wo")
    @AuthIgnore
    @GetMapping("export/matchImportExportWo")
    public void exportMatchTemplateWo(HttpServletResponse response) {
        LOGGER.info("导出匹配模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/posuopei/matchImportsWo.xlsm");
        excelView.writeXlsm(response, "matchTemplateWo");
    }

    @SysLog("上传文件")
    @RequestMapping("modules/posuopei/question/uploadFile")
    public R uploadFile(@RequestParam("file") MultipartFile file) {
        //上传文件到FTP服务器临时文件夹
        String filePath = matchService.uploadFile(file);
        //上传成功后数据库记录相关信息
        SettlementFileEntity fileEntity = new SettlementFileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        //文件路径
        fileEntity.setFilePath(filePath);
        if(filePath.isEmpty()){
            return R.error("文件上传失败");
        }
//        matchService.saveFile(fileEntity);
        return R.ok().put("fileEntity", fileEntity);
    }

    @SysLog("查看文件")
    @RequestMapping("modules/posuopei/question/getFileList")
    public R viewFile(@RequestBody Map<String,Object> param) {

       List<SettlementFileEntity> fileEntityList=matchService.viewFile(String.valueOf(param.get("id")));
//        matchService.saveFile(fileEntity);
        return R.ok().put("fileEntityList", fileEntityList);
    }


    @SysLog("下载文件")
    @RequestMapping("modules/posuopei/question/downloadFile")
    public void downloadFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //根据id查询文件信息(文件路径和文件名)
        SettlementFileEntity fileEntity = matchService.getFileInfo(id);
        //查看图片
        matchService.downloadFile(fileEntity.getFilePath(), fileEntity.getFileName(), response);
    }

    @SysLog("问题单审批")
    @RequestMapping("modules/posuopei/question/check")
    public R check( @RequestBody Map<String,Object> param) {

        final Boolean flag=matchService.check(param);
       if(flag){
           return R.ok().put("msg","保存审核结果成功！");
       }else {
           return R.ok().put("msg","保存审核结果失败！");

       }

    }
    @SysLog("问题单撤销")
    @RequestMapping("modules/posuopei/question/chexiao")
    public R chexiao( @RequestBody Map<String,Object> param) {

        final Boolean flag=matchService.cheXiao(param);
        if(flag){
            return R.ok().put("msg","撤销成功！");
        }else {
            return R.ok().put("msg","撤销失败！");

        }

    }

    @SysLog("问题单采购同意")
    @RequestMapping("modules/posuopei/question/updateY")
    public R updateStatusY( @RequestBody Map<String,Object> param) {

        final Boolean flag=matchService.updataY(String.valueOf(param.get("id")));
        if(flag){
            return R.ok().put("msg","采购已同意！");
        }else {
            return R.ok().put("msg","采购同意失败！");

        }

    }

    @SysLog("问题单采购不同意")
    @RequestMapping("modules/posuopei/question/updateN")
    public R updateStatusN( @RequestBody Map<String,Object> param) {

        final Boolean flag=matchService.updataN(String.valueOf(param.get("id")));
        if(flag){
            return R.ok().put("msg","采购已不同意！");
        }else {
            return R.ok().put("msg","采购不同意失败！");
        }

    }

    @SysLog("导出明细模板")
    @AuthIgnore
    @GetMapping("export/exportTemplateAdd")
    public void exportTemplateAdd(HttpServletResponse response,String name) {
        LOGGER.info("导出明细模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/posuopei/"+name+".xlsx");
        excelView.write(response, name);
    }

    @SysLog("导入索赔差异明细")
    @PostMapping("modules/importAddClaim")
    public Map<String, Object> importAddClaim(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入索赔差异明细");
        Map<String, Object> map=matchService.importClaim(multipartFile);
        return map;

    }
    @SysLog("导入其他明细")
    @PostMapping("modules/importAddOther")
    public Map<String, Object> importAddOther(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入其他明细");
        Map<String, Object> map=matchService.importOther(multipartFile);
        return map;

    }
    @SysLog("导入订单单价差异明细")
    @PostMapping("modules/importAddPo")
    public Map<String, Object> importAddPo(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入订单单价差异明细");
        Map<String, Object> map=matchService.importPo(multipartFile);
        return map;

    }
    @SysLog("导入订单单价差异明细")
    @PostMapping("modules/importAddPoDiscount")
    public Map<String, Object> importAddPoDiscount(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入订单单价差异明细");
        Map<String, Object> map=matchService.importPoDiscount(multipartFile);
        return map;

    }
    @SysLog("导入收退货数量差异明细")
    @PostMapping("modules/importAddCount")
    public Map<String, Object> importAddCount(@RequestParam("file") MultipartFile multipartFile,@RequestParam("type")String type) {
        LOGGER.info("导入订单单价差异明细");
        Map<String, Object> map=matchService.importCount(multipartFile,type);
        return map;

    }
}
