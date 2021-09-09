package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 问卷查询Dao
 */
@Mapper
public interface QuestionnaireWoDao {

    /**
     * 获得问卷查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getQuestionnaireCount(Map<String, Object> map);

    /**
     * 获得问卷查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<QuestionnaireEntity> questionnaireList(Map<String, Object> map);
    /**
     * 问卷供应商关联信息集合
     *
     * @param map 查询条件
     * @return 公告供应商关联信息集合
     */
    List<UserEntity> venderList(Map<String, Object> map);

    /**
     * 获得问卷供应商关联信息的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getVenderCount(Map<String, Object> map);

    /**
     * 查询问卷内容
     * */
    QuestionnaireEntity queryQuestionnaire(@Param("entity") QuestionnaireEntity entity);
}
