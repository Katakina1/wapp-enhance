package com.xforceplus.wapp.modules.backfill.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.backfill.dto.TXfSellerInvoiceEntity;
import com.xforceplus.wapp.modules.backfill.dto.TXfSellerInvoiceItemEntity;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.noneBusiness.dto.TXfNoneBusinessUploadExportDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface SellerInvoiceDetailConverter {
    List<InvoiceDetail> map(List<TXfSellerInvoiceItemEntity> list);


}
