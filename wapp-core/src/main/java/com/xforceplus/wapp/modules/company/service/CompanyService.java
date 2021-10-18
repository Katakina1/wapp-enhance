package com.xforceplus.wapp.modules.company.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.company.convert.CompanyConverter;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.taxcode.converters.TaxCodeConverter;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.service.CommClaimService;
import com.xforceplus.wapp.service.CommRedNotificationService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
@Slf4j
public class CompanyService extends ServiceImpl<TAcOrgDao, TAcOrgEntity> {
    private final CompanyConverter companyConverter;

    public CompanyService(CompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Tuple2<List<TAcOrgEntity>, Page<TAcOrgEntity>> page(Long current, Long size, String taxNo) {
        LambdaQueryChainWrapper<TAcOrgEntity> wrapper = new LambdaQueryChainWrapper<TAcOrgEntity>(baseMapper);
        if (StringUtils.isNotBlank(taxNo)) {
            wrapper.eq(TAcOrgEntity::getTaxNo, taxNo);
        }
        Page<TAcOrgEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(companyConverter.map(page.getRecords()), page);
    }

    public void update(CompanyUpdateRequest companyUpdateRequest) {
        LambdaUpdateChainWrapper<TAcOrgEntity> wrapper = new LambdaUpdateChainWrapper<TAcOrgEntity>(baseMapper);
        if (StringUtils.isNotBlank(companyUpdateRequest.getTaxNo())) {
            wrapper.eq(TAcOrgEntity::getTaxNo, companyUpdateRequest.getTaxNo());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getAccount())){
            wrapper.set(TAcOrgEntity::getAccount, companyUpdateRequest.getAccount());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getBank())){
            wrapper.set(TAcOrgEntity::getAccount, companyUpdateRequest.getBank());
        }
        if (companyUpdateRequest.getQuota()!=null){
            wrapper.set(TAcOrgEntity::getAccount, companyUpdateRequest.getQuota());
        }
        if (StringUtils.isNotBlank(companyUpdateRequest.getTaxName())){
            wrapper.set(TAcOrgEntity::getAccount, companyUpdateRequest.getTaxName());
        }
        this.update(wrapper);
    }

    /**
     * 根据orgcode获取公司信息
     * @param orgCode  --jvcode 或者供应商6d号码
     * @param orgType 5 沃尔玛公司 8供应商公司
     * @return
     */
    public TAcOrgEntity getOrgInfoByOrgCode(String orgCode,String orgType) {
        if(StringUtils.isEmpty(orgCode)){
            return null;
        }
        QueryWrapper<TAcOrgEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(orgCode)) {
            wrapper.eq(TAcOrgEntity.ORG_CODE, orgCode);
        }
        if (StringUtils.isNotBlank(orgType)) {
            wrapper.eq(TAcOrgEntity.ORG_TYPE, orgType);
        }
        return this.getOne(wrapper);
    }


    public List<TAcOrgEntity> getPurchaserOrgs(){
        LambdaQueryWrapper<TAcOrgEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.select(TAcOrgEntity::getOrgNme,TAcOrgEntity::getTaxNo,TAcOrgEntity::getOrgCode);
        wrapper.eq(TAcOrgEntity::getOrgType,5);

        return this.list(wrapper);

    }

}
