package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.entity.CouponEntity;

import java.util.List;
import java.util.Map;

public interface CouponAnnouncementService {

    /**
     * 获得自定义公告集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    PagedQueryResult<CouponEntity> couponAnnouncementList(Map<String, Object> map);

    /**
     * 发布自定义公告
     */
    void releaseCustom();

    /**
     * 批量保存债务数据
     * @param debtEntityList 债务数据
     * @return 成功数量
     */
    Integer saveBatchCoupon(List<CouponEntity> debtEntityList, String usercode);

    /**
     * 删除债务数据
     */
    void deleteDebt();

    /**
     *  查询导入失败的债务数据列表
     * @param map
     * @return
     */
    List<CouponEntity> queryDebtFailureList(Map<String, Object> map);

}
