package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.TaxCodeDao;
import com.xforceplus.wapp.modules.base.entity.TaxCodeEntity;
import com.xforceplus.wapp.modules.base.service.TaxCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxCodeServiceImpl implements TaxCodeService {
    @Autowired
    private TaxCodeDao taxCodeDao;
    @Override
    public int insert(List<TaxCodeEntity> list) {
        taxCodeDao.emptyTable();
        final List<List<TaxCodeEntity>> splitList = splitList(list,500);
        int sum = 0;
        for (List<TaxCodeEntity> inlist : splitList) {
            sum += taxCodeDao.insert(inlist);
        }

        return sum;
    }

    @Override
    public List<TaxCodeEntity> queryList(TaxCodeEntity entity) {
        return taxCodeDao.queryList(entity);
    }

    @Override
    public int queryTotal() {
        return taxCodeDao.queryTotal();
    }

    /**
     * 分批次导入数据
     * */
    private static  List<List<TaxCodeEntity>> splitList(List<TaxCodeEntity> sourceList, int  batchCount) {
        List<List<TaxCodeEntity>> returnList =  new ArrayList<>();
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
