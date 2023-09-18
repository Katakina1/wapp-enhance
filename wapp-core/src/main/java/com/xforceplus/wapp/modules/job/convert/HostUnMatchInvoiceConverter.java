package com.xforceplus.wapp.modules.job.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.job.dto.HostUnMatchInvoiceExportDto;
import com.xforceplus.wapp.modules.noneBusiness.dto.TXfNoneBusinessUploadExportDto;
import com.xforceplus.wapp.modules.noneBusiness.dto.TXfNoneBusinessUploadImportDto;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface HostUnMatchInvoiceConverter {


    List<HostUnMatchInvoiceExportDto> exportMap(List<TDxRecordInvoiceEntity> list);


}
