package com.xforceplus.wapp.htmladapter;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.htmladapter.dto.XmlToPdf;
import com.xforceplus.wapp.htmladapter.service.impl.PdfBoxGenerator;
import com.xforceplus.wapp.modules.backfill.dto.AnalysisXmlResult;
import com.xforceplus.wapp.modules.backfill.mapstruct.XmlToPdfMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Controller
@RequestMapping(value = EnhanceApi.BASE_PATH)
public class TestPdfController {
    @Autowired
    private PdfBoxGenerator pdfBoxGenerator;
    @Autowired
    private XmlToPdfMapper xmlToPdfMapper;

    @SneakyThrows
    @RequestMapping(value = "/htmlToPdf")
    public R getInvoiceInfo(@RequestBody AnalysisXmlResult invoiceInfo, HttpServletResponse response) {
        XmlToPdf map = xmlToPdfMapper.map(invoiceInfo);
        byte[] bytes = pdfBoxGenerator.generatePdfFileByHtmlAndData(map);
        response.setHeader("Content-Disposition", "attachment;fileName=1.pdf");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        return R.ok("result",  "");
    }
}
