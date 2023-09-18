package com.xforceplus.wapp.modules.syscode.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.syscode.SysIdEnum;
import com.xforceplus.wapp.modules.syscode.dto.SysCodeDTO;
import com.xforceplus.wapp.repository.dao.TXfSysCodeDao;
import com.xforceplus.wapp.repository.entity.TXfSysCodeEntity;
import com.xforceplus.wapp.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/10/25 10:42
 **/
@Service
@Slf4j
public class SysCodeService extends ServiceImpl<TXfSysCodeDao, TXfSysCodeEntity> {

    /**
     * 新增
     */
    public boolean add(SysCodeDTO dto) {
        TXfSysCodeEntity sysCodeEntity = new TXfSysCodeEntity();
        BeanUtils.copyProperties(dto, sysCodeEntity);
        sysCodeEntity.setId(null);
        return this.save(sysCodeEntity);
    }

    /**
     * 根据id更新
     */
    public boolean update(SysCodeDTO dto) {
        Asserts.isNull(dto.getId(), "id不能为空");

        TXfSysCodeEntity tXfSysCodeEntity = new TXfSysCodeEntity();
        BeanUtils.copyProperties(dto, tXfSysCodeEntity);
        return this.updateById(tXfSysCodeEntity);
    }

    /**
     * 根据id和code查询一条记录
     */
    public SysCodeDTO getOneBy(String sysId, String sysCode) {
        LambdaQueryWrapper<TXfSysCodeEntity> queryWrapper = Wrappers.lambdaQuery(TXfSysCodeEntity.class)
                .eq(TXfSysCodeEntity::getSysId, sysId)
                .eq(TXfSysCodeEntity::getSysCode, sysCode)
                .orderByAsc(TXfSysCodeEntity::getSeqNum);
        List<TXfSysCodeEntity> list =
                this.list(queryWrapper);
        return Optional.ofNullable(list).orElse(Lists.newArrayList()).stream().findFirst().map(entity -> {
            SysCodeDTO sysCodeDTO = new SysCodeDTO();
            BeanUtils.copyProperties(entity, sysCodeDTO);
            return sysCodeDTO;
        }).orElse(null);

    }

    public boolean getDestroySettlementFlag(String type) {
        LambdaQueryWrapper<TXfSysCodeEntity> queryWrapper = Wrappers.lambdaQuery(TXfSysCodeEntity.class)
                .eq(TXfSysCodeEntity::getSysId, SysIdEnum.DESTROY_SETTLEMENT_SELLER.getCode())
                .eq(TXfSysCodeEntity::getSysCode, type)
                .orderByAsc(TXfSysCodeEntity::getSeqNum);
        List<TXfSysCodeEntity> list = this.list(queryWrapper);
        return Optional.ofNullable(list).orElse(Lists.newArrayList())
                .stream().findFirst().map(entity -> Constants.ONE_STR.equals(entity.getSysName())).orElse(false);
    }
}
