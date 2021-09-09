package com.xforceplus.wapp.modules.base.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class QuestionnaireEntity extends BaseEntity implements Serializable {
	//问卷id
	private Long id;
	//供应商id
	private Long userId;
	//题目id
	private Long topicId;

	 //类型(题目、选项)
	private String type;

	//题目、选项值
	private String value;

	//问卷标题
	private String questionnaireTitle;

	//发布日期
	private Date releaseDate;

	List<QuestionnaireTopicEntity> topics;

	public List<QuestionnaireTopicEntity> getTopics() {
		return topics;
	}

	public void setTopics(List<QuestionnaireTopicEntity> topics) {
		this.topics = topics;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getQuestionnaireTitle() {
		return questionnaireTitle;
	}

	public void setQuestionnaireTitle(String questionnaireTitle) {
		this.questionnaireTitle = questionnaireTitle;
	}
}
