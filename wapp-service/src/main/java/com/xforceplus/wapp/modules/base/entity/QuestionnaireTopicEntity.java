package com.xforceplus.wapp.modules.base.entity;

import java.util.List;

public class QuestionnaireTopicEntity {
    //题目id
    private Long id;
    //题目标题
    private String topicTitle;
    //问卷标题
    private Long questionnaireId;
    //用户提交的答案编号
    private Long topicOp;

    List<QuestionnaireOptionEntity> options;

//    private QuestionnaireEntity questionnaireEntity;

//    public QuestionnaireEntity getQuestionnaireEntity() {
//        return questionnaireEntity;
//    }
//
//    public void setQuestionnaireEntity(QuestionnaireEntity questionnaireEntity) {
//        this.questionnaireEntity = questionnaireEntity;
//    }

    public Long getTopicOp() {
        return topicOp;
    }

    public void setTopicOp(Long topicOp) {
        this.topicOp = topicOp;
    }

    public List<QuestionnaireOptionEntity> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionnaireOptionEntity> options) {
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }
}
