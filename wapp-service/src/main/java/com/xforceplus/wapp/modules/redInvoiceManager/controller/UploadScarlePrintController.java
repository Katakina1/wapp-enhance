package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.ClaimInquiryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.PoInquiryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.ClaimInquiryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.export.UploadScarleExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导出专用controller
 */
@RestController
@RequestMapping("/export")
public class UploadScarlePrintController extends AbstractController {

    @Autowired
    private UploadScarletLetterService uploadScarletLetterService;

    private static final Logger LOGGER = getLogger(UploadScarlePrintController.class);
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("/uploadInvoiceExport")
    public void uploadInvoiceExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);

        params.put("userID", getUserId());
        //查询列表数据
        List<UploadScarletLetterEntity> list = uploadScarletLetterService.queryListAll(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("uploadScarletLetterList", list);
        //生成excel
        final UploadScarleExcel excelView = new UploadScarleExcel(map, "export/redInvoice/UploadScarletLetterList.xlsx", "uploadScarletLetterList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "uploadScarletLetterList" + excelNameSuffix);
    }

}
