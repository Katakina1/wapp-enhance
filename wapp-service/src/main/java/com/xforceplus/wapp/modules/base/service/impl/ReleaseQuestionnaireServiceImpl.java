package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.ReleaseQuestionnaireDao;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.service.ReleaseQuestionnaireService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class ReleaseQuestionnaireServiceImpl implements ReleaseQuestionnaireService {

    private final static Logger LOGGER = getLogger(ReleaseQuestionnaireServiceImpl.class);
    @Autowired
    private ReleaseQuestionnaireDao releaseQuestionnaireDao;

    @Override
    public void releaseQuestionnaire(List<QuestionnaireEntity> list,String title) {
        QuestionnaireEntity entity = new QuestionnaireEntity();
        entity.setQuestionnaireTitle(title);
        //保存问卷
        releaseQuestionnaireDao.saveQuestionnaire(entity);
        //获取所有的供应商
        List<QuestionnaireEntity> userQuestionnairelist = releaseQuestionnaireDao.getAllVender();
        //关联供应商和问卷
        for(QuestionnaireEntity questionnaireEntity : userQuestionnairelist){
            questionnaireEntity.setId(entity.getId());
        }
        //将list切分为500一个
        List<List<QuestionnaireEntity>> splitList =splitList(userQuestionnairelist,500);
        //批量保存问卷和供应商关联数据
        for(List<QuestionnaireEntity> entityList:  splitList){
            releaseQuestionnaireDao.addQuestionnaireUserMiddle(entityList);
        }
        //5个分为一个list，把题目和选项放到同一个list下
        List<List<QuestionnaireEntity>> questionnaireList = splitList(list,5);
        Long topicId=null;
        for(List<QuestionnaireEntity> entityList:questionnaireList){
            for(QuestionnaireEntity questionnaireEntity: entityList){
                if("topic".equals(questionnaireEntity.getType())){
                    questionnaireEntity.setId(entity.getId());
                    //保存题目
                    releaseQuestionnaireDao.saveTopic(questionnaireEntity);
                    topicId=questionnaireEntity.getTopicId();
                    continue;
                }
                questionnaireEntity.setTopicId(topicId);
                //保存选项
                releaseQuestionnaireDao.saveOption(questionnaireEntity);
            }
        }
    }

    /**
     * 分批list
     *
     * @param sourceList
     *            要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<QuestionnaireEntity>> splitList(List<QuestionnaireEntity> sourceList,  int  batchCount) {
        List<List<QuestionnaireEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }
}
