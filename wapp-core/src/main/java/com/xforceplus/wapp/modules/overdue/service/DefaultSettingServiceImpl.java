package com.xforceplus.wapp.modules.overdue.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.DefaultSettingEnum;
import com.xforceplus.wapp.modules.overdue.dto.RedSwitchDto;
import com.xforceplus.wapp.repository.dao.DefaultSettingDao;
import com.xforceplus.wapp.repository.entity.DefaultSettingEntity;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class DefaultSettingServiceImpl extends ServiceImpl<DefaultSettingDao, DefaultSettingEntity> {
    public Integer getOverdueDay(DefaultSettingEnum settingEnum) {
        List<Integer> values = Arrays.asList(DefaultSettingEnum.AGREEMENT_OVERDUE_DEFAULT_DAY.value,
                DefaultSettingEnum.CLAIM_OVERDUE_DEFAULT_DAY.value,
                DefaultSettingEnum.EPD_OVERDUE_DEFAULT_DAY.value);
        Assert.isTrue(values.contains(settingEnum.getValue()), "参数不正确");
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(DefaultSettingEntity::getCode, settingEnum.getCode())
                .select(DefaultSettingEntity::getValue).oneOpt().map(it -> Integer.valueOf(it.getValue()))
                .orElse(30);
    }

    public Boolean updateOverdueDay(DefaultSettingEnum settingEnum, Integer day, String user) {
        List<Integer> values = Arrays.asList(DefaultSettingEnum.AGREEMENT_OVERDUE_DEFAULT_DAY.value,
                DefaultSettingEnum.CLAIM_OVERDUE_DEFAULT_DAY.value,
                DefaultSettingEnum.EPD_OVERDUE_DEFAULT_DAY.value);
        Assert.isTrue(values.contains(settingEnum.getValue()), "参数不正确");
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(DefaultSettingEntity::getCode, settingEnum.getCode())
                .oneOpt().map(it -> new LambdaUpdateChainWrapper<>(getBaseMapper())
                        .set(DefaultSettingEntity::getValue, day)
                        .eq(DefaultSettingEntity::getId, it.getId()).update())
                .orElseGet(() -> save(DefaultSettingEntity.builder().code(settingEnum.getCode())
                        .meaning(settingEnum.getMessage()).value(day.toString()).createUser(user).updateUser(user).build()));
    }

    public Boolean updateOrSaveRedSwitch(List<RedSwitchDto> dto, String user) {
        List<RedSwitchDto> update = dto.stream().filter(it -> Objects.nonNull(it.getId())).collect(Collectors.toList());
        List<DefaultSettingEntity> updateModel = update.parallelStream().map(it -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String value = String.format("%s,%s", format.format(it.getStart()), format.format(it.getEnd()));
            return DefaultSettingEntity.builder().id(it.getId()).value(value).updateUser(user).build();
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updateModel)) {
            updateBatchById(updateModel);
        }
        List<RedSwitchDto> insert = dto.stream().filter(it -> Objects.isNull(it.getId())).collect(Collectors.toList());
        List<DefaultSettingEntity> insertModel = insert.parallelStream().map(it -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String value = String.format("%s,%s", format.format(it.getStart()), format.format(it.getEnd()));
            return DefaultSettingEntity.builder()
                    .code(DefaultSettingEnum.RED_INFORMATION_SWITCH.getCode())
                    .meaning(DefaultSettingEnum.RED_INFORMATION_SWITCH.getMessage())
                    .value(value).createUser(user).updateUser(user).build();
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insertModel)) {
            saveBatch(insertModel);
        }
        return true;
    }

    public List<Tuple3<Long, Date, Date>> getRedSwitch() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(DefaultSettingEntity::getCode, DefaultSettingEnum.RED_INFORMATION_SWITCH)
                .orderByDesc(DefaultSettingEntity::getUpdateTime)
                .list().stream().map(it -> {
                    if (StringUtils.isBlank(it.getValue())) {
                        return null;
                    }
                    String[] split = it.getValue().split(",");
                    try {
                        Date start = format.parse(split[0]);
                        Date end = format.parse(split[1]);
                        return Tuple.of(it.getId(), start, end);
                    } catch (ParseException e) {
                        log.error("日期格式类型转换异常：", e);
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public boolean isHanging() {
        Date date = new Date();
        return getRedSwitch().stream()
                .anyMatch(it -> {
                    int start = DateUtils.truncatedCompareTo(date, it._2, Calendar.DATE);
                    int end = DateUtils.truncatedCompareTo(date, it._3, Calendar.DATE);
                    return start >= 0 && end <= 0;
                });
    }

    public Boolean deleteRedSwitch(List<Long> ids) {
        return removeByIds(ids);
    }
}
