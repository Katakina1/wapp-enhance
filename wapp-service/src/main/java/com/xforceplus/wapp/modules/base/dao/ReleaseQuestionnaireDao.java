package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReleaseQuestionnaireDao {
    /**
     * 保存问卷
     */
    Integer saveQuestionnaire(@Param("entity")QuestionnaireEntity entity);

    /**
     * 保存题目
     */
    Integer saveTopic(@Param("entity")QuestionnaireEntity entity);

    /**
     * 保存选项
     */
    Integer saveOption(@Param("entity")QuestionnaireEntity entity);

    /**
     * 获取所有的供应商id
     */
    List<QuestionnaireEntity> getAllVender();

    /**
     * 添加公告供应商中间表信息
     */
    Integer addQuestionnaireUserMiddle(@Param("list")List<QuestionnaireEntity> list);

}
