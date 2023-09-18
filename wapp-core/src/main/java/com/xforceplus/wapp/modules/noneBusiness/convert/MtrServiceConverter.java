package com.xforceplus.wapp.modules.noneBusiness.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcInvoiceDetailDto;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcInvoiceMainDto;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcSaveDto;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface MtrServiceConverter {

    TXfNoneBusinessUploadDetailEntity converMtr(MtrIcSaveDto mtrIcSaveDto);

    TDxRecordInvoiceEntity converMtrInvoiceMain(MtrIcInvoiceMainDto mtrIcSaveDto);

    List<TDxRecordInvoiceDetailEntity> converMtrInvoiceDetail(List<MtrIcInvoiceDetailDto> mtrIcSaveDto);
}
