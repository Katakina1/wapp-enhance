package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.base.dao.CustomPFRDao;
import com.xforceplus.wapp.modules.base.entity.CustomPFREntity;
import com.xforceplus.wapp.modules.base.service.CustomPFRService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static com.google.common.collect.Lists.newArrayList;


@Service
public class CustomPFRServiceImpl implements CustomPFRService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomPFRServiceImpl.class);

    @Autowired
    private CustomPFRDao customPFRDao;

    @Override
    public PagedQueryResult<CustomPFREntity> customAnnouncementList(Map<String, Object> map) {
        final PagedQueryResult<CustomPFREntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = customPFRDao.getCustomAnnouncementCount(map);

        //需要返回的集合
        List<CustomPFREntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = customPFRDao.customAnnouncementList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }
//
    @Override
    public PagedQueryResult<CustomPFREntity> debtList(Map<String, Object> map) {
        final PagedQueryResult<CustomPFREntity> pagedQueryResult = new PagedQueryResult<>();
//        final ReportStatisticsEntity count = releaseAnnouncementDao.getDebtCount(map);
//
//        //需要返回的集合
//        List<DebtEntity> infoArrayList = newArrayList();
//        if (count.getTotalCount() > 0) {
//            //根据条件查询符合条件的数据集
//            infoArrayList = releaseAnnouncementDao.debtList(map);
//        }
//        pagedQueryResult.setTotalCount(count.getTotalCount());
//        pagedQueryResult.setResults(infoArrayList);
//        pagedQueryResult.setTotalAmount(count.getTotalAmount());
//        pagedQueryResult.setMdTotalAmount(count.getMdTotalAmount());
//        pagedQueryResult.setPcTotalAmount(count.getPcTotalAmount());
        return pagedQueryResult;
    }

    @Override
    @Transactional
    public Map<String,Integer> saveBatchDebt(List<CustomPFREntity> debtEntityList,String userCode) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        //失败的债务
        List<CustomPFREntity> failureList = new ArrayList<>();

        for(CustomPFREntity debtEntity : debtEntityList){
            //供应商号或协议号为空则跳过此条，继续下一个循环
            if(debtEntity == null){
                continue;
            }
            String venderId = debtEntity.getVenderId();
            //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
            if(!(venderId.length() > 20)) {
                if(StringUtils.isBlank(debtEntity.getOrderNo())) {
                    debtEntity.setFailureReason("订单号不能为空");
                    failureList.add(debtEntity);
                    continue;
                }
                if(StringUtils.isBlank(debtEntity.getGoodsNo())) {
                    debtEntity.setFailureReason("商品号不能为空");
                    failureList.add(debtEntity);
                    continue;
                }
                //供应商号如果不足6位，前面补0
                DecimalFormat g1 = new DecimalFormat("000000");
                venderId = g1.format(Integer.valueOf(venderId));
                debtEntity.setVenderId(venderId);
                debtEntity.setCreateBy(userCode);
                //MD判断供应商号和商品号是否已存在，PC判断供应商号和订单号和商品号是否已存在
                Boolean isExist = customPFRDao.queryDebtIsExist(debtEntity) > 0;
                if (!isExist) {
                    result = customPFRDao.saveDebt(debtEntity) > 0;
                } else {
                    debtEntity.setFailureReason("PFR数据已存在!");
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
        //将list切分为80一个，批量插入到数据库(sql插入字段乘以一个list大小不能超过2100)
        List<List<CustomPFREntity>> splitList = splitDebtList(failureList,80);
        //保存失败债务数据前删除该用户的错误列表
        customPFRDao.deleteDebtByCreateBy(userCode);
        for(List<CustomPFREntity> list :splitList) {
            customPFRDao.saveFailureDebt(list,userCode);
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("suc",successCount);
        map.put("fail",failureList.size());
        return map;
    }

    @Override
    public void deleteDebt() {
        customPFRDao.deleteDebt();
    }

    @Override
    public void releaseCustom() {
        customPFRDao.releaseCustom();
    }

    @Override
    public List<CustomPFREntity> queryDebtFailureList(Map<String, Object> map) {
        return customPFRDao.queryDebtFailureList(map);
    }

    @Override
    public List<CustomPFREntity> getVenderDebtList(Map<String, Object> map) {
        return null;
    }


    /**
     * 获取上传文件的名称
     *
     * @param filename 文件的原始名称，有可能包含路径
     * @return
     */
    private String getOriginalFilename(String filename) {

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf(SUBSTR_REGEX_FOR_FILE);
        int pos = winSep > unixSep ? winSep : unixSep;
        return pos != -1 ? filename.substring(pos + 1) : filename;

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
    private static  List<List<CustomPFREntity>> splitDebtList(List<CustomPFREntity> sourceList,  int  batchCount) {
        List<List<CustomPFREntity>> returnList =  new ArrayList<>();
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
