package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;

import java.util.List;

public interface QuestionnaireSurveyService {
    /**
     * 查询未答问卷总数
     * */
   int queryTotal(QuestionnaireEntity entity);
    /**
     * 查询未答问卷
     * */
    List<QuestionnaireEntity> query(QuestionnaireEntity entity);
    /**
     * 查询问卷内容
     * */
    QuestionnaireEntity queryQuestionnaire(QuestionnaireEntity entity);
    /**
     * 保存问卷答案并记录已答状态
     * */
    void saveUserQuestionnaireOp(QuestionnaireEntity entity);
}
