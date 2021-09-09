package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.service.QuestionnaireSurveyService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供方未答问卷
 * */
@RestController
public class QuestionnaireSurveyController extends AbstractController {
    @Autowired
    QuestionnaireSurveyService questionnaireSurveyService;
    @RequestMapping("/base/questionnaireSurvey/query")
    public R query(QuestionnaireEntity entity){
        //获取当前页
        final Integer page = entity.getPage();
        if(page!=null){
            //分页查询起始值
            entity.setOffset((page - 1) * entity.getLimit());
        }
        entity.setUserId(getUserId());
        List<QuestionnaireEntity> list = questionnaireSurveyService.query(entity);

        int total = questionnaireSurveyService.queryTotal(entity);
        if(page!=null) {
            PageUtils pageUtil = new PageUtils(list, total, entity.getLimit(), page);
            return R.ok().put("page", pageUtil);
        }else{
            return  R.ok().put("page", list);
        }
    }
    /**
     * 查询问卷内容
     * */
    @RequestMapping("/base/questionnaireSurvey/queryQuestionnaire")
    public R queryQuestionnaire(QuestionnaireEntity entity){
        QuestionnaireEntity questionnaireEntity = questionnaireSurveyService.queryQuestionnaire(entity);
        questionnaireEntity.setUserId(getUserId());
        return R.ok().put("entity",questionnaireEntity);
    }
    /**
     * 提交问卷
     * */
    @RequestMapping("/base/questionnaireSurvey/submitQuestionnaire")
    public R submitQuestionnaire(@RequestBody QuestionnaireEntity entity){
        //entity.setUserId(getUserId());
        questionnaireSurveyService.saveUserQuestionnaireOp(entity);
        return R.ok();
    }

}
