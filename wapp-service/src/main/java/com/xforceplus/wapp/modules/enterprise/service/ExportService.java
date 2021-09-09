package com.xforceplus.wapp.modules.enterprise.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.entity.MessageEntity;

public interface ExportService {

	/**
	 * 根据条件，查询消息
	 * @param schemaLabel
	 * @param parmMap
	 * @return
	 */
	public List<MessageEntity> getMessageControl(String schemaLabel, Map<String, Object> parmMap);

	/**
	 * 根据条件，查询消息总数
	 * @param schemaLabel
	 * @param parmMap
	 * @return
	 */
	public Integer getMessageControlCount(String schemaLabel, Map<String, Object> parmMap);
	
	public ExportLogEntity getExportLog(String schemaLabel, Map<String, Object> parmMap);
	
	public int allclickcommit(String schemaLabel, String loginname);
	
	Integer clickcommit(String schemaLabel, Integer id);
	
}
