package com.xforceplus.wapp.modules.exceptionreport.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReMatchRequest;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ExceptionReportService {

    /**
     * 索赔单添加例外报告
     *
     * @param entity
     */
    void add4Claim(TXfExceptionReportEntity entity);

    /**
     * 协议单添加例外报告
     *
     * @param entity
     */
    void add4Agreement(TXfExceptionReportEntity entity);

    /**
     * EPD添加例外报告
     *
     * @param entity
     */
    void add4EPD(TXfExceptionReportEntity entity);

    Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum);

    void export(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum);
    void doExport(ExceptionReportExportDto exportDto);

    @Transactional
    void reMatchTaxCode(ReMatchRequest request);

    boolean updateStatus(String billNo, String code, int type, Long billId);
    /**
     * 例外报告导入
     * @param file
     * @return
     */
	R exceptionReportImport(MultipartFile file);
	/**
	 * 修改例外报告
	 * @param exceptionReportDto
	 * @return
	 */
	R update(ExceptionReportDto exceptionReportDto);
}
