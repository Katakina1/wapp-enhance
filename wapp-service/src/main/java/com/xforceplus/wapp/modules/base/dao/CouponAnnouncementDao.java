package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.CouponEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CouponAnnouncementDao {

    /**
     * 获得自定义公告查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getCouponAnnouncementCount(Map<String, Object> map);

    /**
     * 获得自定义公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<CouponEntity> couponAnnouncementList(Map<String, Object> map);

    /**
     *下拉供应商类型查询
     */
    List<UserEntity> levelList();

    /**
     * 查询供应商列表
     */
    List<UserEntity> userlist(@Param("entity") UserEntity userEntity);

    /**
     * 添加公告信息
     */
    Integer addAnnouncement(@Param("entity") AnnouncementEntity params);

    /**
     * 添加公告供应商中间表信息
     */
    Integer addAnnouncementUserMiddle(@Param("list") List<UserEntity> list);

    /**
     * 获取供应商id
     */
    Integer getUserid(@Param("user") UserEntity user);

    /**
     * 保存自定义公告
     */
    void saveTemplate(@Param("entity") AnnouncementEntity entity);

    /**
     * 查询自定义模板是否已存在
     * @param entity
     * @return 数量
     */
    Integer queryTemplateExist(@Param("entity") AnnouncementEntity entity);

    /**
     * 查询自定义模板内容
     * @param entity
     * @return 数量
     */
    AnnouncementEntity queryTemplate(@Param("entity") AnnouncementEntity entity);

    /**
     * 修改自定义模板
     * @param entity
     */
    void updateTemplate(@Param("entity") AnnouncementEntity entity);

    /**
     * 查询PC,MD,PC&MD三个是否都存在
     * @return 数量
     */
    Integer templateIsExist();

    /**
     * 发布自定义公告，所有的数据都发布
     */
    void releaseCustom();

    /**
     * 	查询债务数据是否已存在
     */
    int queryDebtIsExist(@Param("entity") CouponEntity entity);

    /**
     * 	保存债务数据
     */
    int saveDebt(@Param("entity") CouponEntity entity);

    /**
     * 	保存导入失败债务数据
     */
    int saveFailureDebt(@Param("list") List<CouponEntity> entity, @Param("userCode") String userCode);

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
    List<CouponEntity> queryDebtFailureList(Map<String, Object> map);

}
