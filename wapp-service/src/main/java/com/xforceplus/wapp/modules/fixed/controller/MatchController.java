package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.service.UserService;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.fixed.dao.FixedMatchDao;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.MatchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController("fixedMatchController")
@RequestMapping("/fixed/match")
public class MatchController extends AbstractController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private FixedMatchDao matchDao;

    @SysLog("发票匹配-订单列表")
    @RequestMapping("/orderList")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        params.put("venderid", getUser().getUsercode());
        List<OrderEntity> list = matchService.queryOrderList(params);
        return R.ok().put("orderList", list);
    }

    @SysLog("获取税率信息")
    @RequestMapping("/getRate")
    public R getRate() {
        List<SelectionOptionEntity> optionList = matchService.getRate();
        return R.ok().put("optionList", optionList);
    }

    @SysLog("获取普票发票信息")
    @RequestMapping("/searchInvoice")
    public R searchInvoice(@RequestParam("invoiceCode") String invoiceCode, @RequestParam("invoiceNo") String invoiceNo) {
        InvoiceEntity invoiceInfo = matchService.searchInvoice(invoiceCode, invoiceNo);
        return R.ok().put("invoiceInfo", invoiceInfo);
    }

    @SysLog("获取专票发票信息")
    @RequestMapping("/searchInvoiceQuery")
    public R searchInvoiceQuery(@RequestParam("invoiceQueryDate1") String invoiceQueryDate1,@RequestParam("invoiceQueryDate2") String invoiceQueryDate2, @RequestParam("invoiceNo") String invoiceNo,
            @RequestParam("gfTaxNo") String gfTaxNo,@RequestParam("orgid") String orgid) {
        getUserId();
        List<InvoiceEntity> invoiceInfo = matchService.searchInvoiceQuery(invoiceQueryDate1,invoiceQueryDate2, invoiceNo,gfTaxNo,orgid);
        return R.ok().put("invoiceInfo", invoiceInfo);
    }

    @SysLog("保存匹配信息")
    @RequestMapping("/submitAll")
    public R submitAll(@RequestBody MatchQueryEntity match) {
        try{
            Long userId=getUserId();
            OrganizationEntity organizationEntity= matchDao.getXf(userId);
            match.setXfName(organizationEntity.getTaxname());
            match.setXfTaxNo(organizationEntity.getTaxno());
            matchService.submitAll(match);
        } catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
        return R.ok();
    }

    @SysLog("上传文件")
    @RequestMapping("/uploadFile")
    public R uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) {
        //上传文件到FTP服务器临时文件夹
        String filePath = matchService.uploadFile(file);
        //上传成功后数据库记录相关信息
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        //文件路径
        fileEntity.setFilePath(filePath);
        fileEntity.setFileType(fileType);
        if(filePath.isEmpty()){
            return R.error("文件上传失败");
        }
        matchService.saveFile(fileEntity);
        return R.ok().put("fileEntity", fileEntity);
    }



}
