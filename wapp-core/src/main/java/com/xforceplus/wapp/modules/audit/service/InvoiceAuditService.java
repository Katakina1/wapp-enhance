package com.xforceplus.wapp.modules.audit.service;

import com.xforceplus.wapp.repository.entity.InvoiceAudit;
import com.baomidou.mybatisplus.extension.service.IService;
import io.vavr.Tuple2;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Admin
 * @description 针对表【t_xf_invoice_audit(发票审核页面)】的数据库操作Service
 * @createDate 2022-05-25 15:06:26
 */
public interface InvoiceAuditService extends IService<InvoiceAudit> {

    Tuple2<Long, List<InvoiceAudit>> search(String invoiceNo, String invoiceCode, String settlementNo, String auditStatus, Integer pages, Integer size);

    List<InvoiceAudit> search(Set<String> uuids);

    List<InvoiceAudit> search(Date begin, Date end);

    boolean add(String settlementNo, @NotNull @Size List<String> uuids, String remark, Integer autoFlag);

    boolean saveOrUpdate(String settlementNo, @NotNull @Size List<String> uuids, String remark, Integer autoFlag);

    boolean audit(@NotNull @Size List<String> uuids, String auditStatus, String auditRemark);

    boolean delete(List<String> uuids);

    boolean passAudit(String uuid);
}
