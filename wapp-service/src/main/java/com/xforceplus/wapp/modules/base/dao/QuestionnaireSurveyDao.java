package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionnaireSurveyDao {
    /**
     * 查询未答问卷总数
     * */
    int queryTotal(@Param("entity") QuestionnaireEntity entity);
    /**
     * 查询未答问卷
     * */
    List<QuestionnaireEntity> query(@Param("entity") QuestionnaireEntity entity);
    /**
     * 查询问卷内容
     * */
    QuestionnaireEntity queryQuestionnaire(@Param("entity") QuestionnaireEntity entity);
    /**
     * 保存问卷答案
     * */
    void saveUserQuestionnaireOp(@Param("entity") QuestionnaireEntity entity);
    /**
     * 记录问卷已读
     * */
    void recordIsAnswer(@Param("entity") QuestionnaireEntity entity);

}
