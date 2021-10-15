package com.xforceplus.wapp.modules.overdue.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.DefaultSettingEnum;
import com.xforceplus.wapp.repository.dao.DefaultSettingDao;
import com.xforceplus.wapp.repository.entity.DefaultSettingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class DefaultSettingServiceImpl extends ServiceImpl<DefaultSettingDao, DefaultSettingEntity> {
    public String getOverdueDay(DefaultSettingEnum settingEnum) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(DefaultSettingEntity::getCode, settingEnum.getValue())
                .select(DefaultSettingEntity::getValue).oneOpt().map(DefaultSettingEntity::getValue)
                .orElse("30");
    }

    public Boolean updateOverdueDay(DefaultSettingEnum settingEnum, String day) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(DefaultSettingEntity::getCode, settingEnum.getValue())
                .oneOpt().map(it -> new LambdaUpdateChainWrapper<>(getBaseMapper())
                        .set(DefaultSettingEntity::getValue, day)
                        .eq(DefaultSettingEntity::getId, it.getId()).update())
                .orElse(Boolean.FALSE);
    }
}
