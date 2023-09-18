package com.xforceplus.wapp.modules.customs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.modules.customs.dto.CustomsDetailDto;
import com.xforceplus.wapp.modules.customs.dto.CustomsQueryDto;
import com.xforceplus.wapp.repository.dao.TDxCustomsDetailDao;
import com.xforceplus.wapp.repository.entity.TDxCustomsDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ChenHang
 * @Date: 2023/7/4 15:45
 */
@Slf4j
@Service
public class CustomsDetailService extends ServiceImpl<TDxCustomsDetailDao, TDxCustomsDetailEntity> {

    /**
     * 保存或更新海关缴款书明细表数据
     * @param customsDetails
     */
    public void saveOrUpdateCustomsDetail(String taxDocNo, ArrayList<TDxCustomsDetailEntity> customsDetails) {
        // 先查询海关缴款书明细表是否有数据 有数据则删除重新添加
        LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDxCustomsDetailEntity::getCustomsNo, taxDocNo);
        List<TDxCustomsDetailEntity> tDxCustomsDetailEntities = this.list(queryWrapper);
        // 如果数据为空则直接保存
        if (CollectionUtils.isEmpty(tDxCustomsDetailEntities)) {
            this.saveBatch(customsDetails);
        }
        // 如果有数据且条数与BMS给到的条数不一致, 则删除原明细信息再保存
        if (!CollectionUtils.isEmpty(customsDetails) && !CollectionUtils.isEmpty(tDxCustomsDetailEntities) && customsDetails.size() != tDxCustomsDetailEntities.size()) {
            List<Long> ids = tDxCustomsDetailEntities.stream().map(TDxCustomsDetailEntity::getId).collect(Collectors.toList());
            this.removeByIds(ids);
            this.saveBatch(customsDetails);
        }
    }

    public PageResult<CustomsDetailDto> paged(CustomsQueryDto request) {

        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);
        //总个数
        int count = this.queryCount(request);
        //获取数据
        List<TDxCustomsDetailEntity> customsDetailEntities = this.queryByPage(request);
        List<CustomsDetailDto> response = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customsDetailEntities)){
            for(TDxCustomsDetailEntity customsEntitie : customsDetailEntities){
                response.add(copyEntity(customsEntitie));
            }
        }
        return PageResult.of(response,count,pageNo, request.getPageSize());
    }

    private List<TDxCustomsDetailEntity> queryByPage(CustomsQueryDto request) {
        LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDxCustomsDetailEntity::getCustomsNo, request.getCustomsNo());
        queryWrapper.last("ORDER by paper_drew_date desc offset " + request.getPageNo() + " rows fetch next " + request.getPageSize() + " rows only");
        return this.list(queryWrapper);
    }

    private int queryCount(CustomsQueryDto request) {
        LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDxCustomsDetailEntity::getCustomsNo, request.getCustomsNo());
        return  this.list(queryWrapper).size();
    }


    public CustomsDetailDto copyEntity(TDxCustomsDetailEntity customsDetails){
        CustomsDetailDto CustomsDetailDto = new CustomsDetailDto();
        BeanUtil.copyProperties(customsDetails,CustomsDetailDto);
        return CustomsDetailDto;
    }

    /**
     * 根据缴款书号码查询明细
     * @param customsNo
     * @param taxRate
     * @return
     */
    public List<TDxCustomsDetailEntity> getByCustomsNo(String customsNo, BigDecimal taxRate) {
        LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDxCustomsDetailEntity::getCustomsNo, customsNo);
        queryWrapper.eq(TDxCustomsDetailEntity::getTaxRate, taxRate);
        return this.list(queryWrapper);
    }
}
