package com.xforceplus.wapp.modules.discountRateSetting.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.customs.convert.CheckTypeExportEnum;
import com.xforceplus.wapp.modules.customs.convert.ManageStatusExportEnum;
import com.xforceplus.wapp.modules.customs.dto.CustomsExport4Dto;
import com.xforceplus.wapp.modules.customs.dto.CustomsQueryDto;
import com.xforceplus.wapp.modules.customs.dto.CustomsValidSubmitRequest;
import com.xforceplus.wapp.modules.discountRateLog.dto.OrgDto;
import com.xforceplus.wapp.modules.discountRateSetting.dto.OrgExportDto;
import com.xforceplus.wapp.modules.discountRateSetting.dto.OrgExportRequest;
import com.xforceplus.wapp.modules.discountRateSetting.service.OrgService;
import com.xforceplus.wapp.repository.dao.TAcOrgDao;
import com.xforceplus.wapp.repository.dao.TDiscountRateSettingDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import com.xforceplus.wapp.util.ExcelExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OrgServiceImpl extends ServiceImpl<TAcOrgDao, TAcOrgEntity> implements OrgService {
    @Autowired
    private ExcelExportUtils excelExportUtils;
    @Autowired
    private TDiscountRateSettingDao tDiscountRateSettingDao;
    /**
     * 供应商限额维护列表
     * @param vo
     * @return
     */
    @Override
    public Page<TAcOrgEntity> paged(OrgDto vo) {
        LambdaQueryWrapper<TAcOrgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty(vo.getOrgId()), TAcOrgEntity::getOrgId, vo.getOrgId());
        queryWrapper.eq(!ObjectUtils.isEmpty(vo.getOrgCode()), TAcOrgEntity::getOrgCode, vo.getOrgCode());
        queryWrapper.eq(!ObjectUtils.isEmpty(vo.getOrgName()), TAcOrgEntity::getOrgName, vo.getOrgName());
        queryWrapper.eq(TAcOrgEntity::getOrgType, 8);
        queryWrapper.orderByDesc(TAcOrgEntity::getLastModifyBy);
        Page<TAcOrgEntity> pageRsult = this.page(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        return pageRsult;
    }
    /**
     * 根据ids查询数据
     * @param includes
     * @return
     */
    @Override
    public List<TAcOrgEntity> getByBatchIds(List<Long> includes) {
        List<List<Long>> subs = ListUtils.partition(includes , 300);
        LambdaQueryWrapper<TAcOrgEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 对数据进行切分, 避免sql过长
        List<TAcOrgEntity> entities = new ArrayList<>();
        for (List<Long> sub : subs) {
            queryWrapper.in(TAcOrgEntity::getOrgId, sub);
            List<TAcOrgEntity> list = this.list(queryWrapper);
            entities.addAll(list);
        }
        return entities;
    }
    public R orgExport(List<TAcOrgEntity> resultList, OrgExportRequest request, String type) {
        String fileName = "供应商开票限额"+type;
        try {
            List<OrgExportDto> exportDtos = new ArrayList<>();
            for(TAcOrgEntity entity:resultList){
                OrgExportDto dto = new OrgExportDto();
                dto.setOrgCode(entity.getDqCode());
                dto.setOrgName(entity.getOrgName());
                dto.setQuota(entity.getQuota());

                exportDtos.add(dto);
            }

            excelExportUtils.messageExportOneSheet(exportDtos, OrgExportDto.class, fileName, JSONObject.toJSONString(request), "sheet1");

        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        }
        return R.ok();
    }
    /**
     * @Description 查询个数
     * @Author fengfan
     * @return
     **/
    public Integer count(OrgDto request) {
        LambdaQueryWrapper<TAcOrgEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request.getOrgName()),TAcOrgEntity::getOrgName,request.getOrgName());
        wrapper.eq(StringUtils.isNotEmpty(request.getOrgCode()),TAcOrgEntity::getOrgCode,request.getOrgCode());
        wrapper.eq(TAcOrgEntity::getOrgType, 8);
        return this.count(wrapper);
    }
    /**
     * @Description 查询数据
     * @Author fengfan
     * @return
     **/
    public List<TAcOrgEntity> queryByPage(OrgDto request){
        //获取数据
        return  tDiscountRateSettingDao.queryPage(request.getPageNo(),request.getPageSize(),request.getOrgCode(),
                request.getOrgName());
    }

}

