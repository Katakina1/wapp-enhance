package com.xforceplus.wapp.modules.invoice.controller;

import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/invoice")
public class InvoiceController {

    @Autowired
    InvoiceServiceImpl invoiceService;

    @ApiOperation(value = "发票详情", notes = "", response = Response.class, tags = {"发票池",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @GetMapping(value = "/detail")
    public Response<InvoiceDto> detail(@RequestParam(value="id") Long id){
        return invoiceService.detail(id);
    }
}
