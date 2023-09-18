package com.xforceplus.wapp.modules.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.modules.audit.enums.AuditStatusEnum;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.InvoiceAudit;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.repository.dao.InvoiceAuditDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Admin
* @description 针对表【t_xf_invoice_audit(发票审核页面)】的数据库操作Service实现
* @createDate 2022-05-25 15:06:26
*/
@Service
@AllArgsConstructor
public class InvoiceAuditServiceImpl extends ServiceImpl<InvoiceAuditDao, InvoiceAudit>
    implements InvoiceAuditService{

    private final TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Override
    public Tuple2<Long, List<InvoiceAudit>> search(String invoiceNo, String invoiceCode,
                                                   String settlementNo, String auditStatus,
                                                   Integer pages, Integer size) {
        Page<InvoiceAudit> page = new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(StringUtils.isNotBlank(invoiceNo), InvoiceAudit::getInvoiceNo, invoiceNo)
                .eq(StringUtils.isNotBlank(invoiceCode), InvoiceAudit::getInvoiceCode, invoiceCode)
                .eq(StringUtils.isNotBlank(auditStatus), InvoiceAudit::getAuditStatus, auditStatus)
                .eq(StringUtils.isNotBlank(settlementNo), InvoiceAudit::getSettlementNo, settlementNo)
                .isNull(InvoiceAudit::getDeleteFlag)
                .ne(InvoiceAudit::getAutoFlag, 1)
                .orderByDesc(InvoiceAudit::getUpdateTime)
                .page(new Page<>(pages, size));
        return Tuple.of(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean add(String settlementNo, @NotNull @Size List<String> uuid, String remark, Integer autoFlag) {
        List<TDxRecordInvoiceEntity> invoiceEntities = new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao)
                .in(TDxRecordInvoiceEntity::getUuid, uuid)
                .list();
        if (CollectionUtils.isEmpty(invoiceEntities) || invoiceEntities.size() != uuid.size()) {
            throw new EnhanceRuntimeException("未查到对应的发票信息");
        }
        
        List<InvoiceAudit> insert = invoiceEntities.stream().map(it -> getInvoiceAudit(settlementNo, remark, autoFlag, it)).collect(Collectors.toList());
        saveBatch(insert);
        return true;
    }

    @Override
    public boolean saveOrUpdate(String settlementNo, @NotNull @Size List<String> uuids, String remark, Integer autoFlag) {
        List<TDxRecordInvoiceEntity> invoiceEntities = new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao)
                .in(TDxRecordInvoiceEntity::getUuid, uuids)
                .list();
        if (CollectionUtils.isEmpty(invoiceEntities) || invoiceEntities.size() != uuids.size()) {
            throw new EnhanceRuntimeException("未查到对应的发票信息");
        }

        invoiceEntities.forEach(recordInvoiceEntity -> {
            InvoiceAudit invoiceAudit = getInvoiceAudit(settlementNo, remark, autoFlag, recordInvoiceEntity);
            try {
                save(invoiceAudit);
            } catch (Exception e) {
                log.error("save error:", e);
                LambdaQueryWrapper<InvoiceAudit> wrapper = Wrappers.lambdaQuery(InvoiceAudit.class).eq(InvoiceAudit::getInvoiceUuid, recordInvoiceEntity.getUuid());
                update(invoiceAudit, wrapper);
            }
        });
        return true;
    }

    private InvoiceAudit getInvoiceAudit(String settlementNo, String remark, Integer autoFlag, TDxRecordInvoiceEntity recordInvoiceEntity) {
        InvoiceAudit invoiceAudit = new InvoiceAudit();
        invoiceAudit.setInvoiceUuid(recordInvoiceEntity.getUuid());
        invoiceAudit.setSettlementNo(settlementNo);
        invoiceAudit.setInvoiceCode(recordInvoiceEntity.getInvoiceCode());
        invoiceAudit.setInvoiceNo(recordInvoiceEntity.getInvoiceNo());
        invoiceAudit.setAuditStatus(AuditStatusEnum.NOT_AUDIT.getValue());
        invoiceAudit.setAutoFlag(autoFlag);
        invoiceAudit.setRemark(remark);
        invoiceAudit.setCreateUser(UserUtil.getUserName());
        invoiceAudit.setUpdateUser(UserUtil.getUserName());
        return invoiceAudit;
    }

    @Override
    public boolean audit(@NotNull @Size List<String> uuids, String auditStatus, String auditRemark) {
        if (!ValueEnum.isValid(AuditStatusEnum.class, auditStatus)) {
            throw new EnhanceRuntimeException("审核状态不正确");
        }
        return new LambdaUpdateChainWrapper<>(getBaseMapper())
                .set(InvoiceAudit::getAuditStatus, auditStatus )
                .set(InvoiceAudit::getAuditRemark, auditRemark)
                .set(InvoiceAudit::getUpdateTime, new Date())
                .set(InvoiceAudit::getAuditUser, UserUtil.getUserName())
                .in(InvoiceAudit::getInvoiceUuid, uuids)
                .isNull(InvoiceAudit::getDeleteFlag)
                .update();
    }

    @Override
    public boolean delete(List<String> uuids) {
        Integer count = new LambdaQueryChainWrapper<>(getBaseMapper())
                .in(InvoiceAudit::getInvoiceUuid, uuids)
                .ne(InvoiceAudit::getAuditStatus, AuditStatusEnum.NOT_AUDIT.getValue())
                .isNull(InvoiceAudit::getDeleteFlag)
                .count();
        if (count > 0) {
            throw new EnhanceRuntimeException("已审批的数据不能删除");
        }
        return new LambdaUpdateChainWrapper<>(getBaseMapper())
                .set(InvoiceAudit::getDeleteFlag, System.currentTimeMillis())
                .in(InvoiceAudit::getInvoiceUuid, uuids)
                .isNull(InvoiceAudit::getDeleteFlag)
                .update();
    }

    @Override
    public boolean passAudit(String uuid) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(StringUtils.isNotBlank(uuid), InvoiceAudit::getInvoiceUuid, uuid)
                .eq(InvoiceAudit::getAuditStatus, AuditStatusEnum.AUDIT_PASS.getValue())
                .isNull(InvoiceAudit::getDeleteFlag)
                .oneOpt().isPresent();
    }

    @Override
    public List<InvoiceAudit> search(Set<String> uuids) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .in(InvoiceAudit::getInvoiceUuid, uuids)
                .isNull(InvoiceAudit::getDeleteFlag)
                .list();
    }

    @Override
    public List<InvoiceAudit> search(Date begin, Date end) {
        Asserts.isNull(begin, "开始时间不能为空");
        Asserts.isNull(end, "结束时间不能为空");
        Asserts.isFalse(end.after(begin), "结束时间必须在开始时间之后");

        LambdaQueryWrapper<InvoiceAudit> wrapper = Wrappers.lambdaQuery(InvoiceAudit.class)
                .ge(InvoiceAudit::getCreateTime, begin)
                .le(InvoiceAudit::getCreateTime, end)
                .eq(InvoiceAudit::getAuditStatus, AuditStatusEnum.NOT_AUDIT.getValue())
                .eq(InvoiceAudit::getAutoFlag, NumberUtils.INTEGER_ONE);
        return list(wrapper);
    }
}




