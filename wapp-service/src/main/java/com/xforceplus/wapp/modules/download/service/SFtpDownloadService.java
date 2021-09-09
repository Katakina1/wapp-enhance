package com.xforceplus.wapp.modules.download.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface SFtpDownloadService {

	void download(String schemaLabel, Map<String, Object> parmMap, HttpServletResponse response);
}
