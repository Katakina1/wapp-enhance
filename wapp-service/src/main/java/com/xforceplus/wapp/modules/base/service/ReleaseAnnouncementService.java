package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReleaseAnnouncementService {

    /**
     * 获得自定义公告集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    PagedQueryResult<DebtEntity> customAnnouncementList(Map<String, Object> map);

    /**
     * 获得债务数据集合
     * @param map 查询条件
     * @return 债务数据集合
     */
    PagedQueryResult<DebtEntity> debtList(Map<String, Object> map);
    /**
     *下拉供应商类型查询
     */
    List<UserEntity> levelList();
    /**
     * 发布公告
     */
    String releaseAnnouncement(AnnouncementEntity params);
    /**
     * 导入供应商
     */
    Map<String, Object> importUser(MultipartFile file);

    /**
     * 保存自定义模板
     * @param entity
     */
    void saveTemplate(AnnouncementEntity entity);

    /**
     * 查询自定义模板内容
     * @param entity
     * @return 数量
     */
    AnnouncementEntity queryTemplate(AnnouncementEntity entity);

    /**
     * 发布自定义公告
     */
    void releaseCustom();

    /**
     * 查询PC,MD,PC&MD三个是否都存在
     * @return
     */
    Integer templateIsExist();

    /**
     * 批量保存债务数据
     * @param debtEntityList 债务数据
     * @return 成功数量
     */
    Integer saveBatchDebt(List<DebtEntity> debtEntityList,String usercode);

    /**
     * 删除债务数据
     */
    void deleteDebt();

    /**
     *  查询导入失败的债务数据列表
     * @param map
     * @return
     */
    List<DebtEntity> queryDebtFailureList(Map<String, Object> map);

    /**
     *  获取供应商已发布，未读债务数据
     * @param map
     * @return
     */
    List<DebtEntity> getVenderDebtList(Map<String, Object> map);
}
