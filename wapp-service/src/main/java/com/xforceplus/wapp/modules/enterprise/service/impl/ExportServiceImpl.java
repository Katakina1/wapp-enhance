package com.xforceplus.wapp.modules.enterprise.service.impl;

import com.xforceplus.wapp.modules.enterprise.service.ExportService;
import com.xforceplus.wapp.modules.export.dao.ExportDao;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExportServiceImpl implements ExportService {

	@Autowired
	private ExportDao exportDao;
	
	@Override
	public List<MessageEntity> getMessageControl(String schemaLabel, Map<String, Object> parmMap) {
		return exportDao.getMessageControl(schemaLabel, parmMap);
	}

	@Override
	public Integer getMessageControlCount(String schemaLabel, Map<String, Object> parmMap) {
		return exportDao.getMessageControlCount(schemaLabel, parmMap);
	}

	@Override
	public ExportLogEntity getExportLog(String schemaLabel, Map<String, Object> parmMap) {
		return exportDao.getExportLog(schemaLabel, parmMap);
	}

	@Override
	public int allclickcommit(String schemaLabel, String loginname) {
		return exportDao.allclickcommit(schemaLabel, loginname);
	}

	@Override
	public Integer clickcommit(String schemaLabel, Integer id) {
		return exportDao.clickcommit(schemaLabel, id);
	}
}
