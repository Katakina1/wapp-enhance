package com.xforceplus.wapp.modules.redInvoiceManager.service;


import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface RedInvoiceService {

         public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName);
}
