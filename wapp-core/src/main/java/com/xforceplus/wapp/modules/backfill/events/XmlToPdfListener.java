package com.xforceplus.wapp.modules.backfill.events;


import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.htmladapter.dto.XmlToPdf;
import com.xforceplus.wapp.htmladapter.service.PdfGenerator;
import com.xforceplus.wapp.modules.backfill.dto.AnalysisXmlResult;
import com.xforceplus.wapp.modules.backfill.mapstruct.XmlToPdfMapper;
import com.xforceplus.wapp.modules.backfill.model.InvoiceFileEntity;
import com.xforceplus.wapp.modules.backfill.model.UploadFileResult;
import com.xforceplus.wapp.modules.backfill.service.ElectronicUploadRecordDetailService;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.repository.daoExt.InvoiceFileDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlToPdfListener {
    private final XmlToPdfMapper xmlToPdfMapper;
    private final PdfGenerator pdfGenerator;
    private final FileService fileService;
    private final IDSequence idSequence;
    private final InvoiceFileDao invoiceFileDao;
    private final ElectronicUploadRecordDetailService electronicUploadRecordDetailService;
    private final NoneBusinessService noneBusinessService;

    @EventListener
    public void xmlToPdf(XmlToPdfEvent event) {
        log.info("xmlToPdf入参：{}", event);
        XmlToPdfEvent.UploadCallBack uploadCallBack = new XmlToPdfEvent.UploadCallBack();
        XmlToPdf map = xmlToPdfMapper.map(event.getResult());
        byte[] bytes = pdfGenerator.generatePdfFileByHtmlAndData(map);
        if (Objects.isNull(bytes)) {
            log.error("xml生成pdf失败，字节流为空");
            uploadCallBack.setErrMsg("xml生成pdf失败");
//            event.getUploadCallBack().accept(uploadCallBack);
            return;
        }
        String uploadResult;
        try {
            uploadResult = fileService.uploadFile(bytes, UUID.randomUUID() + ".pdf", event.getVendorId());
        } catch (IOException e) {
            log.error("上传pdf文件失败，{}", e.getMessage(), e);
            uploadCallBack.setErrMsg("上传pdf文件失败");
//            event.getUploadCallBack().accept(uploadCallBack);
            return;
        }
        UploadFileResult result = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
        if (result == null || result.getData() == null) {
            log.error("上传pdf文件失败，数据为空，{}", result);
            uploadCallBack.setErrMsg("上传pdf文件失败");
//            event.getUploadCallBack().accept(uploadCallBack);
            return;
        }
        log.info("xmlToPdf转换成功，地址：{}", result.getData().getUploadPath());
        uploadCallBack.setUploadId(result.getData().getUploadId());
        uploadCallBack.setUploadPath(result.getData().getUploadPath());
        AnalysisXmlResult.InvoiceMainDTO invoiceMain = event.getResult().getInvoiceMain();



        TXfElecUploadRecordDetailEntity entity = electronicUploadRecordDetailService.getByUploadId(event.getUploadFileId());
        if (Objects.nonNull(entity)) {
            log.info("xmlToPdf 保存ElecUpload，{}", entity);
            final List<TXfInvoiceFileEntity> files = invoiceFileDao.getByInvoiceAndTypes(invoiceMain.getInvoiceNo(), "", Collections.singletonList(1));

            if (CollectionUtils.isNotEmpty(files)) {
                TXfInvoiceFileEntity save = files.get(0);
                save.setPath(result.getData().getUploadPath());
                save.setType(InvoiceFileEntity.TYPE_OF_PDF);
                log.info("xmlToPdf 更新invoiceFile，{}", save);
                invoiceFileDao.update(save);
            } else {
                TXfInvoiceFileEntity file = new TXfInvoiceFileEntity();
                file.setInvoiceNo(invoiceMain.getInvoiceNo());
                file.setInvoiceCode("");
                file.setStatus(1);
                file.setId(idSequence.nextId());
                file.setStorage(0);
                file.setOrigin(0);
                file.setCreateUser("venderId:" + event.getVendorId());
                file.setPath(result.getData().getUploadPath());
                file.setType(InvoiceFileEntity.TYPE_OF_PDF);
                file.setCreateTime(new Date());
                log.info("xmlToPdf 保存invoiceFile，{}", file);
                invoiceFileDao.save(file);
            }
        } else {
            TXfNoneBusinessUploadDetailEntity nEntity = noneBusinessService.getBySourceUploadId(event.getUploadFileId());
            log.info("xmlToPdf 查询NoneBus，{}", nEntity);
            if (Objects.nonNull(nEntity)) {
                nEntity.setUploadId(result.getData().getUploadId());
                nEntity.setUploadPath(result.getData().getUploadPath());
                log.info("xmlToPdf 保存NoneBus，{}", nEntity);
                noneBusinessService.updateById(nEntity);
            }
        }
    }
}
