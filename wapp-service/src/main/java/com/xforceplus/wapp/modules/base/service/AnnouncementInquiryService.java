package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.AnnouncementExcelEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 公告查询业务层接口
 */
public interface AnnouncementInquiryService {

    /**
     * 获得公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
   PagedQueryResult<AnnouncementEntity> announcementInquiryList(Map<String, Object> map);

    /**
     * 未读公告查询集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    PagedQueryResult<AnnouncementEntity> announcementUnreadList(Map<String, Object> map);

    /**
     * 公告供应商关联信息集合
     *
     * @param map 查询条件
     * @return 公告供应商关联信息集合
     */
    PagedQueryResult<UserEntity> venderList(Map<String, Object> map);

    /**
     * 公告附件下载
     * @param path 文件ftp路径
     * @param response
     */
    void getDownLoadFile(String path, HttpServletResponse response);

    /**
     * 点击公告，公告阅读数量+1,状态修改已阅读
     * @param userId 用户id
     * @param announceId 公告id
     */
    void announceUnReadPlus(Long userId, Long announceId,String announcementType);

    /**
     * 培训公告点击同意，同意数量+1
     * @param announceId 公告id
     */
    void announceAgreePlus(Long announceId,Long userId);

    /**
     * 培训公告点击不同意，不同意数量+1
     * @param announceId 公告id
     */
    void announceDisagreePlus(Long announceId,Long userId);

    /**
     * 删除公告
     */
    void deleteAnnounce( Long[] id);

    /**
     * 查询公告内容
     * @param entity
     * @return 数量
     */
    AnnouncementEntity queryAnnounce(AnnouncementEntity entity);

    /**
     * 修改公告内容
     * @param entity
     */
    void updateAnnounce(AnnouncementEntity entity);
    List<AnnouncementExcelEntity> toExcel(List<AnnouncementEntity> list);
}
