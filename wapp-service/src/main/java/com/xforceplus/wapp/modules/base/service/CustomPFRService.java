package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.entity.CustomPFREntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CustomPFRService {


    /**
     * 获得自定义公告集合
     * @param map 查询条件
     * @return 公告查询集合
     */
    PagedQueryResult<CustomPFREntity> customAnnouncementList(Map<String, Object> map);

    /**
     * 获得债务数据集合
     * @param map 查询条件
     * @return 债务数据集合
     */
    PagedQueryResult<CustomPFREntity> debtList(Map<String, Object> map);

    /**
     * 批量保存债务数据
     * @param debtEntityList 债务数据
     * @return 成功数量
     */
    Map<String,Integer> saveBatchDebt(List<CustomPFREntity> debtEntityList,String usercode);

    /**
     * 删除债务数据
     */
    void deleteDebt();

    /**
     *  查询导入失败的债务数据列表
     * @param map
     * @return
     */
    List<CustomPFREntity> queryDebtFailureList(Map<String, Object> map);

    /**
     *  获取供应商已发布，未读债务数据
     * @param map
     * @return
     */
    List<CustomPFREntity> getVenderDebtList(Map<String, Object> map);

    /**
     * 发布自定义公告
     */
    void releaseCustom();
}
