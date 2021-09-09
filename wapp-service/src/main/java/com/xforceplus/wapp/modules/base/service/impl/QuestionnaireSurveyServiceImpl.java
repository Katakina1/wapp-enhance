package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.QuestionnaireSurveyDao;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireOptionEntity;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireTopicEntity;
import com.xforceplus.wapp.modules.base.service.QuestionnaireSurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class QuestionnaireSurveyServiceImpl implements QuestionnaireSurveyService {
    @Autowired
    QuestionnaireSurveyDao questionnaireSurveyDao;
    /**
     * 查询未答问卷总数
     * */
    @Override
    public int queryTotal(QuestionnaireEntity entity) {
        return questionnaireSurveyDao.queryTotal(entity);
    }

    /**
     * 查询未答问卷
     * */
    @Override
    public List<QuestionnaireEntity> query(QuestionnaireEntity entity) {
        return questionnaireSurveyDao.query(entity);
    }
    /**
     * 查询问卷内容
     * */
    @Override
    public QuestionnaireEntity queryQuestionnaire(QuestionnaireEntity entity) {
        return questionnaireSurveyDao.queryQuestionnaire(entity);
    }
    /**
     * 保存问卷答案并记录已答状态
     * */
    @Override
    public void saveUserQuestionnaireOp(QuestionnaireEntity entity) {
        questionnaireSurveyDao.saveUserQuestionnaireOp(entity);
        questionnaireSurveyDao.recordIsAnswer(entity);
    }

}
