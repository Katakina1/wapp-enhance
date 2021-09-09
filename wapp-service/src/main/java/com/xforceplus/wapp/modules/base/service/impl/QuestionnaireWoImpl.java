package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.dao.QuestionnaireWoDao;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.QuestionnaireWoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 问卷调查(沃)业务层实现
 */
@Service
@Transactional
public class QuestionnaireWoImpl implements QuestionnaireWoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireWoImpl.class);

    private final QuestionnaireWoDao questionnaireWoDao;


    @Autowired
    public QuestionnaireWoImpl(QuestionnaireWoDao questionnaireWoDao) {
        this.questionnaireWoDao = questionnaireWoDao;
    }

    @Override
    public PagedQueryResult<QuestionnaireEntity> questionnaireList(Map<String, Object> map) {
        final PagedQueryResult<QuestionnaireEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = questionnaireWoDao.getQuestionnaireCount(map);
        //需要返回的集合
        List<QuestionnaireEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = questionnaireWoDao.questionnaireList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public PagedQueryResult<UserEntity> venderList(Map<String, Object> map) {
        final PagedQueryResult<UserEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = questionnaireWoDao.getVenderCount(map);

        //需要返回的集合
        List<UserEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = questionnaireWoDao.venderList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public QuestionnaireEntity queryQuestionnaire(QuestionnaireEntity entity) {
        return questionnaireWoDao.queryQuestionnaire(entity);
    }
}
