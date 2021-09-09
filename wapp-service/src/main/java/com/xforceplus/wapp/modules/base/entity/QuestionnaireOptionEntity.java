package com.xforceplus.wapp.modules.base.entity;

public class QuestionnaireOptionEntity {
    //id
    private Long id;
    //题目id
    private Long topicId;
    //选项内容
    private String optionName;

//    private QuestionnaireTopicEntity questionnaireTopicEntity;
//
//    public QuestionnaireTopicEntity getQuestionnaireTopicEntity() {
//        return questionnaireTopicEntity;
//    }
//
//    public void setQuestionnaireTopicEntity(QuestionnaireTopicEntity questionnaireTopicEntity) {
//        this.questionnaireTopicEntity = questionnaireTopicEntity;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
