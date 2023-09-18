package com.xforceplus.wapp.modules.ngsInputInvoice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.evat.common.domain.ngs.NgsInputInvoiceQuery;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
public interface NgsInputInvoiceService extends IService<TDxNgsInputInvoiceEntity> {
    Page<TDxNgsInputInvoiceEntity> paged(NgsInputInvoiceQuery vo);
}
