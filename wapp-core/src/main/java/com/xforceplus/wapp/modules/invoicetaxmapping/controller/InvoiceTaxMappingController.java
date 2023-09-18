package com.xforceplus.wapp.modules.invoicetaxmapping.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.modules.invoicetaxmapping.service.InvoiceTaxMappingService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@Api(tags = "税收分类编码和发票类型映射关系")
@RequestMapping(EnhanceApi.BASE_PATH + "/invoicetaxmapping")
public class InvoiceTaxMappingController {

    @Autowired
    private InvoiceTaxMappingService invoiceTaxMappingService;
    @PostMapping("/list")
    public R<Page<TInvoiceTaxMappingEntity>> listPaged(@RequestBody InvoiceTaxMappingQuery invoiceTaxMappingQuery) {
        Page<TInvoiceTaxMappingEntity> page = invoiceTaxMappingService.paged(invoiceTaxMappingQuery);
        return R.ok(page);
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public R save(@RequestBody TInvoiceTaxMappingEntity entity) {
        String isVerify = this.verify(entity);
        if (isVerify != "") {
            return R.fail(isVerify);
        }
        Map query = Maps.newHashMap();
        query.put("goodsTaxNo", entity.getGoodsTaxNo());
        Integer count = invoiceTaxMappingService.queryListCount(query);
        if (count > 0) {
            return R.fail("保存失败：税收分类编码唯一，不可重复");
        }
        final String usercode = UserUtil.getUser().getUsercode();
        entity.setLastUpdateBy(usercode);
        invoiceTaxMappingService.add(entity);
        return R.ok();
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PostMapping("/edit")
    public R edit(@RequestBody TInvoiceTaxMappingEntity entity){
        String isVerify = this.verify(entity);
        if (isVerify != "") {
            return R.fail(isVerify);
        }
        final String usercode = UserUtil.getUser().getUsercode();
        entity.setLastUpdateBy(usercode);
        entity.setLastUpdateDate(new Date());
        invoiceTaxMappingService.edit(entity);
        return R.ok();
    }

    public String verify(TInvoiceTaxMappingEntity entity){
        if (StringUtils.isEmpty(entity.getGoodsTaxNo())) {
            return "税收分类编码不能为空";
        }
        if(entity.getGoodsTaxNo().length() != 19){
            return "税收分类编码长度为19位";
        }
        if (StringUtils.isEmpty(entity.getInvoiceType())) {
            return "发票类型不能为空";
        }
        return "";
    }
    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public R list(@RequestBody int[] ids) {
        invoiceTaxMappingService.deleteMapping(ids);
        return R.ok();
    }
}
