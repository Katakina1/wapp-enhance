package com.xforceplus.wapp.modules.overdue.service;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.modules.overdue.converters.OverdueConverter;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.excel.OverdueImportListener;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.repository.dao.OverdueDao;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class OverdueServiceImpl extends ServiceImpl<OverdueDao, OverdueEntity> {
    private final OverdueConverter overdueConverter;
    private final DefaultSettingServiceImpl defaultSettingService;

    public OverdueServiceImpl(OverdueConverter overdueConverter, DefaultSettingServiceImpl defaultSettingService) {
        this.overdueConverter = overdueConverter;
        this.defaultSettingService = defaultSettingService;
    }

    public Tuple2<List<Overdue>, Page<?>> page(long current, long size,
                                               ServiceTypeEnum typeEnum, String sellerName, String sellerNo, String sellerTaxNo) {
        log.info("超期配置分页查询,入参：{}，{}，{}，{}", current, size, sellerName, sellerTaxNo);
        LambdaQueryChainWrapper<OverdueEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(OverdueEntity::getDeleteFlag)
                .eq(OverdueEntity::getType, typeEnum.getValue());
        if (StringUtils.isNotBlank(sellerName)) {
            wrapper.eq(OverdueEntity::getSellerName, sellerName);
        }
        if (StringUtils.isNotBlank(sellerTaxNo)) {
            wrapper.eq(OverdueEntity::getSellerTaxNo, sellerTaxNo);
        }
        if (StringUtils.isNotBlank(sellerNo)) {
            wrapper.eq(OverdueEntity::getSellerNo, sellerNo);
        }
        Page<OverdueEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("超期配置分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(overdueConverter.map(page.getRecords()), page);
    }

    public Optional<Overdue> oneOptBySellerNo(@NonNull ServiceTypeEnum typeEnum, @NonNull String sellerNo) {
        log.info("超期配置查询,入参：{}", sellerNo);
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(OverdueEntity::getDeleteFlag)
                .eq(OverdueEntity::getType, typeEnum.getValue())
                .eq(OverdueEntity::getSellerNo, sellerNo).oneOpt()
                .map(overdueConverter::map);
    }

    public Optional<Overdue> oneOptBySellerTaxNo(@NonNull ServiceTypeEnum typeEnum, @NonNull String sellerTaxNo) {
        log.info("超期配置查询,入参：{}", sellerTaxNo);
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(OverdueEntity::getDeleteFlag)
                .eq(OverdueEntity::getType, typeEnum.getValue())
                .eq(OverdueEntity::getSellerTaxNo, sellerTaxNo)
                .oneOpt().map(overdueConverter::map);
    }

    public Either<String, Integer> export(ServiceTypeEnum typeEnum, InputStream is) {
        OverdueImportListener listener = new OverdueImportListener();
        EasyExcel.read(is, OverdueDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        Set<String> taxNos = listener.getValidInvoices().stream().map(OverdueDto::getSellerTaxNo).collect(Collectors.toSet());
        Set<String> exist = new LambdaQueryChainWrapper<>(getBaseMapper()).in(OverdueEntity::getSellerTaxNo, taxNos)
                .isNull(OverdueEntity::getDeleteFlag).eq(OverdueEntity::getType, typeEnum.getValue())
                .select(OverdueEntity::getSellerTaxNo)
                .list().stream().map(OverdueEntity::getSellerTaxNo).collect(Collectors.toSet());
        List<OverdueDto> list = listener.getValidInvoices().stream()
                .peek(it -> it.setType(typeEnum.getValue()))
                .filter(it -> !exist.contains(it.getSellerTaxNo()))
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(comparing(OverdueDto::getSellerTaxNo))),
                        ArrayList::new));
        log.debug("导入数据新增数据:{}", list);
        log.info("导入数据新增条数:{}", list.size());
        boolean save = saveBatch(overdueConverter.reverse(list, 111L), 2000);
        return save ? Either.right(list.size()) : Either.right(0);
    }
}
