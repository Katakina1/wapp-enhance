package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Mapper
public interface KnowCenterDao {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<KnowledgeFileEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 保存上传文件的信息
     * @param knowledgeFileEntity 文件信息
     */
    void saveKnowFile(KnowledgeFileEntity knowledgeFileEntity);

    /**
     * 删除上传的文件信息
     * @param id 文件id
     * */
    void deleteOne(@Param("id") Integer id);

}
