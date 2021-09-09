package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.dao.CouponAnnouncementDao;
import com.xforceplus.wapp.modules.base.entity.CouponEntity;
import com.xforceplus.wapp.modules.base.service.CouponAnnouncementService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class CouponAnnouncementServiceImpl implements CouponAnnouncementService {
    private static final Logger LOGGER= getLogger(CouponAnnouncementServiceImpl.class);

    @Autowired
    private CouponAnnouncementDao couponAnnouncementDao;

    @Override
    public PagedQueryResult<CouponEntity> couponAnnouncementList(Map<String, Object> map) {
        final PagedQueryResult<CouponEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = couponAnnouncementDao.getCouponAnnouncementCount(map);

        //需要返回的集合
        List<CouponEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = couponAnnouncementDao.couponAnnouncementList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public void releaseCustom() {
        couponAnnouncementDao.releaseCustom();
    }


    @Override
    public Integer saveBatchCoupon(List<CouponEntity> debtEntityList,String userCode) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        //失败的债务
        List<CouponEntity> failureList = new ArrayList<>();

        for(CouponEntity debtEntity : debtEntityList){
            //供应商号或协议号为空则跳过此条，继续下一个循环
            String venderId = debtEntity.getSixD();
            //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
            if(!(venderId.length() > 20)) {
                if(debtEntity.getCaseDate()==null) {
                    debtEntity.setFailureReason("现金房定案日期不能为空");
                    failureList.add(debtEntity);
                    continue;
                }

                //供应商号如果不足6位，前面补0
                DecimalFormat g1 = new DecimalFormat("000000");
                venderId = g1.format(Integer.valueOf(venderId));
                debtEntity.setSixD(venderId);
                debtEntity.setCreateBy(userCode);
                //MD判断供应商号和商品号是否已存在，PC判断供应商号和订单号和商品号是否已存在
                Boolean isExist = couponAnnouncementDao.queryDebtIsExist(debtEntity) > 0;
                if (!isExist) {
                    result = couponAnnouncementDao.saveDebt(debtEntity) > 0;
                } else {
                    debtEntity.setFailureReason("Coupon数据已存在。");
                    failureList.add(debtEntity);
                    result = Boolean.FALSE;
                }
                //成功保存债务数据,则计数器加1
                if (result) {
                    ++successCount;
                }
            } else{
                debtEntity.setFailureReason("供应商号长度大于了20。");
                failureList.add(debtEntity);
            }
        }
        //将list切分为100一个，批量插入到数据库(sql插入字段乘以一个list大小不能超过2100)
        List<List<CouponEntity>> splitList = splitDebtList(failureList,100);
        //保存失败债务数据前删除该用户的错误列表
        couponAnnouncementDao.deleteDebtByCreateBy(userCode);
        for(List<CouponEntity> list :splitList) {
            couponAnnouncementDao.saveFailureDebt(list,userCode);
        }
        return successCount;
    }

    @Override
    public void deleteDebt() {
        couponAnnouncementDao.deleteDebt();
    }

    @Override
    public List<CouponEntity> queryDebtFailureList(Map<String, Object> map) {
        return couponAnnouncementDao.queryDebtFailureList(map);
    }


    /**
     * 分批list
     *
     * @param sourceList
     *            要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<CouponEntity>> splitDebtList(List<CouponEntity> sourceList,  int  batchCount) {
        List<List<CouponEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

}
