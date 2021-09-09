package com.xforceplus.wapp.modules.export.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.entity.MessageEntity;

@Mapper
public interface ExportDao {
    int insertLog(@Param("entity") ExportLogEntity entity);

    void updateStartDate(Long id);

    void updateSucc(Map<String,Object> pramsMap);

    void updateFail(Map<String,Object> paramsMap);

    void insertMessage(Map map);
    
    /**
	 * 根据条件，查询消息
	 * @param schemaLabel
	 * @param parmMap
	 * @return
	 */
	List<MessageEntity> getMessageControl(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> parmMap);

	/**
	 * 根据条件，查询消息总数
	 * @param schemaLabel
	 * @param parmMap
	 * @return
	 */
	Integer getMessageControlCount(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> parmMap);

	ExportLogEntity getExportLog(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> parmMap);

	Integer allclickcommit(@Param("schemaLabel") String schemaLabel,@Param("loginname") String loginname);
	
	Integer clickcommit(@Param("schemaLabel") String schemaLabel, @Param("id") Integer id);
}
