package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;

import java.util.Map;

/**
 * 问卷调查业务层接口
 */
public interface QuestionnaireWoService {

    /**
     * 获得问卷查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
   PagedQueryResult<QuestionnaireEntity> questionnaireList(Map<String, Object> map);

    /**
     * 问卷供应商关联信息集合
     *
     * @param map 查询条件
     * @return 问卷供应商关联信息集合
     */
    PagedQueryResult<UserEntity> venderList(Map<String, Object> map);

    /**
     * 查询问卷内容
     * */
    QuestionnaireEntity queryQuestionnaire(QuestionnaireEntity entity);
}
