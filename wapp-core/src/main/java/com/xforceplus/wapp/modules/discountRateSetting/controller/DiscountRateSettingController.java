package com.xforceplus.wapp.modules.discountRateSetting.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.discountRateSetting.service.DiscountRateSettingService;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "折让比率设置")
@RequestMapping(EnhanceApi.BASE_PATH + "/discountRateSetting")
public class DiscountRateSettingController {
    @Autowired
    private DiscountRateSettingService discountRateSettingService;
    @Autowired
    private DiscountRateLogService discountRateLogService;

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
     * 修改折扣率
     * @param orgEntity
     * @return
     */
    @PostMapping("/updateDiscountRate")
    public R setDiscountRate(@RequestBody TAcOrgEntity orgEntity){
        final String usercode = UserUtil.getUser().getUsercode();
        Long nowDiscountRate = discountRateSettingService.selectNowDiscountRate(orgEntity.getOrgId());
        discountRateSettingService.editDiscountRate(orgEntity);
        //修改成功后记录日志
        OrgLogEntity obj = new OrgLogEntity();
        obj.setOrgid(orgEntity.getOrgId());
        obj.setUpdateUser(usercode);
        obj.setUpdateBefore(nowDiscountRate);
        Long DiscountRate = orgEntity.getDiscountRate().longValue();
        obj.setUpdateAfter(DiscountRate);
        discountRateLogService.addDiscountRateLog(obj);
        return R.ok();
    }
}
