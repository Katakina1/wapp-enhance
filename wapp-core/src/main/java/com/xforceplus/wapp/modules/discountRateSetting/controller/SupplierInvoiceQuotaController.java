package com.xforceplus.wapp.modules.discountRateSetting.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.discountRateLog.dto.OrgDto;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.discountRateLog.service.SupplierInvoiceQuotaLogService;
import com.xforceplus.wapp.modules.discountRateSetting.service.DiscountRateSettingService;
import com.xforceplus.wapp.modules.discountRateSetting.service.SupplierInvoiceQuotaService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "供应商开票限额设置")
@RequestMapping(EnhanceApi.BASE_PATH + "/SupplierInvoiceQuota")
public class SupplierInvoiceQuotaController {
    @Autowired
    private SupplierInvoiceQuotaService supplierInvoiceQuotaService;
    @Autowired
    private SupplierInvoiceQuotaLogService supplierInvoiceQuotaLogService;

    @Autowired
    private DiscountRateSettingService discountRateSettingService;

    /**
     * 查询当前用户对应的供应商信息
     * @return
     */
    @PostMapping("/selectOrg")
    public R<TAcOrgEntity> selectOrg(){
        final String usercode = UserUtil.getUser().getUsercode();
        int orgId = discountRateSettingService.findOrgByUserCode(usercode);
        TAcOrgEntity org = discountRateSettingService.selectOrg(orgId);
        return R.ok(org);
    }

    /**
     * 修改限额
     * @param orgEntity
     * @return
     */
    @PostMapping("/updateQuota")
    public R setQuota(@RequestBody TAcOrgEntity orgEntity){
        final String usercode = UserUtil.getUser().getUsercode();
        Long nowQuota = supplierInvoiceQuotaService.selectNowQuota(orgEntity.getOrgId());
        supplierInvoiceQuotaService.editQuota(orgEntity);
        //修改成功后记录日志
        OrgQuotaLogEntity obj = new OrgQuotaLogEntity();
        obj.setOrgid(orgEntity.getOrgId());
        obj.setUpdateUser(usercode);
        obj.setUpdateBefore(nowQuota);
        Long Quota = orgEntity.getQuota().longValue();
        obj.setUpdateAfter(Quota);
        supplierInvoiceQuotaLogService.addQuotaLog(obj);
        return R.ok();
    }
    /**
     * 查询修改限额日志
     * @param vo
     * @return
     */
    @PostMapping("/selectQuotaLog")
    public R selectQuotaLog(@RequestBody OrgQuotaLogEntity vo){
        List<OrgQuotaLogEntity> list = supplierInvoiceQuotaService.selectQuotaLog(vo.getOrgid());
        return R.ok(list);
    }

}
