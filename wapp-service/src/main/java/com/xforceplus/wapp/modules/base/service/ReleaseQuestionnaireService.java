package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;

import java.util.List;

public interface ReleaseQuestionnaireService {
    /**
     * 发布问卷
     * @return
     */
    void releaseQuestionnaire(List<QuestionnaireEntity> list,String title);
}
