package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import com.xforceplus.wapp.modules.base.entity.UniversalTaxRateEntity;
import com.xforceplus.wapp.modules.base.export.CommodityTemplate;
import com.xforceplus.wapp.modules.base.export.KnowCenterExcel;
import com.xforceplus.wapp.modules.base.service.UniversalTaxRateService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class UniversalTaxRateController extends AbstractController {
    private final static Logger LOGGER = getLogger(UniversalTaxRateController.class);

    @Autowired
    private UniversalTaxRateService universalTaxRateService;

    @SysLog("供应商列表查询")
    @RequestMapping("/modules/base/getVendor")
    public R getVendor(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        Integer count = universalTaxRateService.queryCount(query);
        List<UniversalTaxRateEntity> list = universalTaxRateService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("商品列表查询")
    @RequestMapping("/modules/base/getCommodity")
    public R getCommodity(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        Integer count = universalTaxRateService.queryCommodityCount(query);
        List<UniversalTaxRateEntity> list = universalTaxRateService.queryCommodity(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 批量导入
     */
    @SysLog("供应商零税率导入")
    @PostMapping("/modules/base/commodityImport")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("开红票资料导入,params {}", multipartFile);
        Map<String,Object> map = universalTaxRateService.parseExcel(multipartFile);
        return map;
    }

    /**
     * 导入模板下载
     */
    @SysLog("供应商零税率导入模板下载")
    @AuthIgnore
    @GetMapping("export/commodityTemplate")
    public void getInvoiceCheck(HttpServletResponse response) {
        LOGGER.info("供应商零税率导入模板下载,params {}");
        //生成excel
        final CommodityTemplate excelView = new CommodityTemplate();
        excelView.write(response, "commodityTemplate");
    }

    /**
     * 保存商品信息
     */
    @SysLog("商品信息保存")
    @RequestMapping("base/saveCommodity")
    public R saveCommodity(@RequestBody UniversalTaxRateEntity universalTaxRateEntity) {



        return R.ok(universalTaxRateService.saveCommodity(universalTaxRateEntity));
    }

    /**
     * 删除商品信息(批量)
     */
    @SysLog("商品信息删除")
    @RequestMapping("base/deleteCommodity")
    public R delete(@RequestBody Long[] ids) {

        universalTaxRateService.deleteCommodity(ids);

        return R.ok();
    }

    /**
     * 删除商品信息(批量)
     */
    @SysLog("供应商信息删除")
    @RequestMapping("base/vendorNbr")
    public R deletes(@RequestBody Map<String,Object> params) {
        String vendorNbr= params.get("vendorNbr").toString();
        universalTaxRateService.deleteVendor(vendorNbr);

        return R.ok();
    }

}
