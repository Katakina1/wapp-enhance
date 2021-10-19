package com.xforceplus.wapp.modules.statement.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.statement.converters.StatementConverter;
import com.xforceplus.wapp.modules.statement.models.Statement;
import com.xforceplus.wapp.modules.statement.models.StatementCount;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class StatementServiceImpl extends ServiceImpl<TXfSettlementDao, TXfSettlementEntity> {
    private final StatementConverter statementConverter;
    private final TXfBillDeductExtDao billDeductExtDao;

    public StatementServiceImpl(StatementConverter statementConverter, TXfBillDeductExtDao billDeductExtDao) {
        this.statementConverter = statementConverter;
        this.billDeductExtDao = billDeductExtDao;
    }

    public Tuple2<List<Statement>, Page<?>> page(Long current, Long size, @NonNull Integer type, List<String> settlementStatus,
                                                 String settlementNo, String purchaserNo, String invoiceType,
                                                 String businessNo, String taxRate) {
        log.info("结算单分页查询,入参,type:{},settlementStatus:{},settlementNo:{},purchaserNo:{}," +
                        "invoiceType:{},businessNo:{},taxRate:{},分页数据,current:{},size:{}",
                type, settlementStatus, settlementNo, purchaserNo, invoiceType, businessNo, taxRate, current, size);
        LambdaQueryChainWrapper<TXfSettlementEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfSettlementEntity::getSettlementType, type);
        if (CollectionUtils.isNotEmpty(settlementStatus)) {
            wrapper.in(TXfSettlementEntity::getSettlementStatus, settlementStatus);
        }
        if (StringUtils.isNotBlank(settlementNo)) {
            wrapper.eq(TXfSettlementEntity::getSettlementNo, settlementNo);
        }
        if (StringUtils.isNotBlank(purchaserNo)) {
            wrapper.eq(TXfSettlementEntity::getPurchaserNo, purchaserNo);
        }
        if (StringUtils.isNotBlank(invoiceType)) {
            wrapper.eq(TXfSettlementEntity::getInvoiceType, invoiceType);
        }
        if (StringUtils.isNotBlank(taxRate)) {
            wrapper.eq(TXfSettlementEntity::getTaxRate, taxRate);
        }
        if (StringUtils.isNotBlank(businessNo)) {
            List<String> nos = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo).eq(TXfBillDeductEntity::getBusinessNo, businessNo)
                    .list().stream().map(TXfBillDeductEntity::getRefSettlementNo)
                    .distinct().collect(Collectors.toList());
            wrapper.in(TXfSettlementEntity::getSettlementNo, nos);
        }
        Page<TXfSettlementEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("税编分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(statementConverter.map(page.getRecords()), page);
    }

    public Collection<StatementCount> count(@NonNull Integer type, String settlementNo, String purchaserNo, String invoiceType,
                                            String businessNo, String taxRate) {
        log.info("结算单tab统计,入参,type:{},settlementNo:{},purchaserNo:{},invoiceType:{},businessNo:{},taxRate:{}",
                type, settlementNo, purchaserNo, invoiceType, businessNo, taxRate);
        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();
        val lambda = wrapper.lambda();
        lambda.eq(TXfSettlementEntity::getSettlementType, type).groupBy(TXfSettlementEntity::getSettlementStatus);
        if (StringUtils.isNotBlank(settlementNo)) {
            lambda.eq(TXfSettlementEntity::getSettlementNo, settlementNo);
        }
        if (StringUtils.isNotBlank(purchaserNo)) {
            lambda.eq(TXfSettlementEntity::getPurchaserNo, purchaserNo);
        }
        if (StringUtils.isNotBlank(invoiceType)) {
            lambda.eq(TXfSettlementEntity::getInvoiceType, invoiceType);
        }
        if (StringUtils.isNotBlank(taxRate)) {
            lambda.eq(TXfSettlementEntity::getTaxRate, taxRate);
        }
        if (StringUtils.isNotBlank(businessNo)) {
            List<String> nos = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo).eq(TXfBillDeductEntity::getBusinessNo, businessNo)
                    .list().stream().map(TXfBillDeductEntity::getRefSettlementNo)
                    .distinct().collect(Collectors.toList());
            lambda.in(TXfSettlementEntity::getSettlementNo, nos);
        }
        Map<String, StatementCount> tabMap = Arrays.stream(TXfSettlementStatusEnum.values())
                .map(it -> StatementCount.builder().status(it.getCode().toString()).total(0).build())
                .collect(Collectors.toMap(StatementCount::getStatus, Function.identity()));
        wrapper.select("settlement_status as status, count(*) as total");
        getBaseMapper().selectMaps(wrapper)
                .forEach(it -> tabMap.put(it.get("status").toString(),
                        new StatementCount(it.get("status").toString(), (Integer) it.get("total"))));
        log.debug("结算单tab统计,结果:{}", tabMap.values());
        return tabMap.values();
    }
}
