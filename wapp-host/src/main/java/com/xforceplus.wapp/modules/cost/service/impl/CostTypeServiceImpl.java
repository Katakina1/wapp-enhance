package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostTypeDao;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.service.CostTypeService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CostTypeServiceImpl implements CostTypeService {

    @Autowired
    private CostTypeDao costTypeDao;

    @Override
    public List<CostEntity> queryList(Map<String, Object> map) {
        return costTypeDao.queryList(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return costTypeDao.queryCount(map);
    }

    @Override
    public void save(CostEntity entity) {
        DecimalFormat g1=new DecimalFormat("000000");
        String venderId = g1.format(Integer.valueOf(entity.getVenderId()));
        entity.setVenderId(venderId);
        costTypeDao.save(entity);
    }

    @Override
    public Integer saveBatch(List<CostEntity> costList) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        for(CostEntity costEntity : costList){
            //供应商号或费用类型或费用名称为空则跳过此条，继续下一个循环
            String venderId = costEntity.getVenderId();
            String costType = costEntity.getCostType().toString();
            String costTypeName = costEntity.getCostTypeName();
            if (!(Strings.isNullOrEmpty(venderId)) && !(Strings.isNullOrEmpty(costType)) && !(Strings.isNullOrEmpty(costTypeName))) {
                //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
                if(!(venderId.length() > 20) && !(costType.length() > 255)&& !(costTypeName.length() > 255)) {
                     //供应商号如果不足6位，前面补0
                     DecimalFormat g1=new DecimalFormat("000000");
                     venderId = g1.format(Integer.valueOf(venderId));
                     costEntity.setVenderId(venderId);
                     //判断供应商号和费用类型是否已存在
                    Boolean isExist = costTypeDao.queryCostTypeAndVenderId(costEntity)>0;
                    if(isExist){
                        //如果存在删除再重新保存
                        costTypeDao.deleteByCostAndVenderId(venderId,costEntity.getCostType());
                        result = costTypeDao.save(costEntity)>0;
                    } else{
                        result = costTypeDao.save(costEntity)>0;
                    }

                    //成功保存费用类型,则计数器加1
                    if(result) {
                        ++successCount;
                    }

                }
            }

        }
        return successCount;
    }

    @Override
    public void update(CostEntity entity) {
        costTypeDao.update(entity);
    }

    @Override
    public void delete(Long id) {
        costTypeDao.delete(id);
    }

    @Override
    public int queryCostType(CostEntity entity) {
        return costTypeDao.queryCostType(entity);
    }

}
