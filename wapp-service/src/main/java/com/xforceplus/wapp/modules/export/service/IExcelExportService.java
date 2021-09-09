package com.xforceplus.wapp.modules.export.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * ****************************************************************************
 * excel导出业务处理接口
 *
 * @author(作者)：xuyongyun	
 * @date(创建日期)：2019年4月30日
 ******************************************************************************
 */
public interface IExcelExportService {


	/**
	 * 导出excel入口
	 *
	 *	
	 * @since           1.0
	 */
	void exportExcel(String message);

	ExportLogEntity excelExportApply(Map<String,Object> pramsMap);

	void updateStart(Long id);

    void updateSucc(Long id, String ftpFilePath);

	void updateFail(Long id, String errmsg);

	void insertMessage(JSONObject msg);
	
	void sendWebsocketMessage(String username);
}
