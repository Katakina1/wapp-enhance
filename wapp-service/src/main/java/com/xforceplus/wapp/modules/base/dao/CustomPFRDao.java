package com.xforceplus.wapp.modules.base.dao;


import com.xforceplus.wapp.modules.base.entity.CustomPFREntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustomPFRDao {

    /**
     * 获得自定义公告查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getCustomAnnouncementCount(Map<String, Object> map);

    /**
     * 获得自定义公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<CustomPFREntity> customAnnouncementList(Map<String, Object> map);

    /**
     * 获得债务数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    ReportStatisticsEntity getDebtCount(Map<String, Object> map);

    /**
     * 获得债务数据集合
     * @param map 查询条件
     * @return 债务数据集合
     */
    List<CustomPFREntity> debtList(Map<String, Object> map);


    /**
     * 发布自定义公告，所有的数据都发布
     */
    void releaseCustom();

    /**
     * 	查询债务数据是否已存在
     */
    int queryDebtIsExist( @Param("entity") CustomPFREntity entity);

    /**
     * 	保存债务数据
     */
    int saveDebt( @Param("entity") CustomPFREntity entity);

    /**
     * 	保存导入失败债务数据
     */
    int saveFailureDebt( @Param("list") List<CustomPFREntity> entity,@Param("userCode")String userCode);

    /**
     * 删除债务数据
     */
    void deleteDebt();

    void deleteDebtByCreateBy(@Param("userCode") String userCode);

    /**
     * 查询导入失败的债务数据列表
     * @param map
     * @return
     */
    List<CustomPFREntity> queryDebtFailureList(Map<String, Object> map);

}
