package com.xforceplus.wapp.modules.overdue.service;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.overdue.converters.OverdueConverter;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.excel.OverdueImportListener;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.repository.dao.OverdueDao;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class OverdueServiceImpl extends ServiceImpl<OverdueDao, OverdueEntity> {
    private final OverdueConverter overdueConverter;

    public OverdueServiceImpl(OverdueConverter overdueConverter) {
        this.overdueConverter = overdueConverter;
    }

    public Tuple2<List<Overdue>, Long> page(long current, long size, String sellerName, String sellerTaxNo) {
        log.info("超期配置分页查询,入参：{}，{}，{}，{}", current, size, sellerName, sellerTaxNo);
        LambdaQueryChainWrapper<OverdueEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(OverdueEntity::getDeleteFlag);
        if (StringUtils.isNotBlank(sellerName)) {
            wrapper.eq(OverdueEntity::getSellerName, sellerName);
        }
        if (StringUtils.isNotBlank(sellerTaxNo)) {
            wrapper.eq(OverdueEntity::getSellerTaxNo, sellerTaxNo);
        }
        Page<OverdueEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("超期配置分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(overdueConverter.map(page.getRecords()), page.getTotal());
    }

    public Either<String, Integer> export(InputStream is) {
        OverdueImportListener listener = new OverdueImportListener();
        EasyExcel.read(is, OverdueDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        Set<String> taxNos = listener.getValidInvoices().stream().map(OverdueDto::getSellerTaxNo).collect(Collectors.toSet());
        Set<String> exist = new LambdaQueryChainWrapper<>(getBaseMapper()).in(OverdueEntity::getSellerTaxNo, taxNos)
                .isNull(OverdueEntity::getDeleteFlag).select(OverdueEntity::getSellerTaxNo)
                .list().stream().map(OverdueEntity::getSellerTaxNo).collect(Collectors.toSet());
        List<OverdueDto> list = listener.getValidInvoices().stream().filter(it -> !exist.contains(it.getSellerTaxNo()))
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(comparing(OverdueDto::getSellerTaxNo))),
                        ArrayList::new));
        boolean save = saveBatch(overdueConverter.reverse(list, 111L), 2000);
        return save ? Either.right(list.size()) : Either.right(0);
    }
}
