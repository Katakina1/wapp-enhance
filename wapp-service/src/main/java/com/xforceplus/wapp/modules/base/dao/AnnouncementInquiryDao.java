package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公告查询Dao
 */
@Mapper
public interface AnnouncementInquiryDao {

    /**
     * 获得公告查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getAnnouncementInquiryCount(Map<String, Object> map);

    /**
     * 获得公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<AnnouncementEntity> announcementInquiryList(Map<String, Object> map);

    /**
     * 获得未读公告查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getAnnouncementUnreadCount(Map<String, Object> map);


    /**
     * 获得未读公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<AnnouncementEntity> announcementUnreadList(Map<String, Object> map);

    /**
     * 获得未读自定义公告查询数据的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getCustomAnnouncementUnreadCount(Map<String, Object> map);


    /**
     * 获得未读自定义公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    List<DebtEntity> customAnnouncementUnreadList(Map<String, Object> map);

    /**
     * 公告供应商关联信息集合
     *
     * @param map 查询条件
     * @return 公告供应商关联信息集合
     */
    List<UserEntity> venderList(Map<String, Object> map);

    /**
     * 获得公告供应商关联信息的总行数
     * @param map 查询条件
     * @return 总数
     */
    Integer getVenderCount(Map<String, Object> map);

    /**
     * 点击公告，公告中间表状态修改为已阅读
     * @param userId 用户id
     * @param announceId 公告id
     */
    void updateAnnounceHaveRead(@Param("userId") Long userId,@Param("announceId") Long announceId);

    /**
     * 点击公告，公告中间表状态修改为已阅读
     * @param userId 用户id
     * @param announceId 公告id
     */
    void updateAnnounceAgree(@Param("userId") Long userId,@Param("announceId") Long announceId,@Param("isAgree")String isAgree);

    /**
     * 点击公告，公告阅读数量+1
     * @param announceId 公告id
     */
    void updateAnnounceUnReadPlus(@Param("announceId") Long announceId);

    /**
     * 培训公告点同意，同意数量+1
     * @param announceId 公告id
     */
    void updateAnnounceAgreePlus(@Param("announceId") Long announceId);

    /**
     * 培训公告点不同意，不同意数量+1
     * @param announceId 公告id
     */
    void updateAnnounceDisagreePlus(@Param("announceId") Long announceId);

    /**
     * 删除公告
     * @param announceId 公告Id
     */
    void deleteAnnouncement(@Param("id") Long announceId);

    /**
     * 删除公告关联的供应商中间表
     * @param announceId
     */
    void deleteAnnouncementVender(@Param("id") Long announceId);

    /**
     * 点击公告，债务数据状态修改为已阅读
     * @param userId 用户id
     */
    void updateCustomAnnounceHaveRead(@Param("userId") Long userId,@Param("announcementType") String announcementType);

    /**
     * 点击公告，公告阅读数量+1
     * @param announceId 公告id
     */
    void updateCustomAnnounceReadPlus(@Param("announceId") Long announceId);

    /**
     * 查询公告内容
     * @param entity
     * @return
     */
    AnnouncementEntity queryAnnounce(@Param("entity")AnnouncementEntity entity);

    /**
     * 修改公告内容
     * @param entity
     */
    void updateAnnounce(@Param("entity")AnnouncementEntity entity);

    /**
     * 清空债务表中所有数据
     */
    void emptyDebt();

    /**
     * 根据债务类型和供应商获取债务数据
     * @param venderId
     * @param debtType
     * @return
     */
    AnnouncementEntity getDebtByType(@Param("venderId")String venderId,@Param("debtType")String debtType,@Param("supplierAnnoucement")String supplierAnnoucement);

    /**
     * 获取债务数据数量
     * @return 数量
     */
    Integer getDebtCount();
}
