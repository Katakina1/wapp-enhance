package com.xforceplus.wapp.modules.index.dao;

import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.index.entity.IndexInvoiceCollectionModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 供应商首页
 */
@Mapper
public interface IndexMainVendorDao {
    /**
     * 公告数量
     * @param userid
     * @return
     */
    int getNoticeAllCount(Long userid);

    /**
     * 已读公告数量
     * @param userid
     * @return
     */
    int getNoticeReadCount(Long userid);

    /**
     * 匹配成功数量
     * @param usercode
     * @return
     */
    int getMatchSuccessCount(String usercode);

    /**
     * 匹配失败数量
     * @param usercode
     * @return
     */
    int getMatchFailedCount(String usercode);

    /**
     * 退票数量
     * @param usercode
     * @return
     */
    int getRefundCount(String usercode);

    /**
     * 费用数量
     * @param usercode
     * @return
     */
    int getCostCount(String usercode);

    /**
     * 已申请红票数量
     * @param usercode
     * @return
     */
    int getRedCount(String usercode);

    /**
     * 同意开红票数量
     * @param usercode
     * @return
     */
    int getAgreeRedCount(String usercode);

    /**
     * 不同意开红票数量
     * @param usercode
     * @return
     */
    int getDisagreeRedCount(String usercode);

    /**
     * 认证后异常发票数量
     * @param usercode
     * @return
     */
    int getAbnormalCount(String usercode);

    /**
     * 异常发票列表
     * @param usercode
     * @return
     */
    List<IndexInvoiceCollectionModel> getAbnormal(String usercode);

    /**
     * 获取首页收货信息
     * @return
     */
    List<SelectionOptionEntity> getReceiptInfo();
}
