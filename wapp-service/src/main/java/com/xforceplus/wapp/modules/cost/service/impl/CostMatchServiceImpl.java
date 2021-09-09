package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.modules.cost.dao.CostApplicationDao;
import com.xforceplus.wapp.modules.cost.dao.CostMatchDao;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RateEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementMatchEntity;
import com.xforceplus.wapp.modules.cost.exception.DuplicatedException;
import com.xforceplus.wapp.modules.cost.export.CostMatchImport;
import com.xforceplus.wapp.modules.cost.service.CostMatchService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class CostMatchServiceImpl implements CostMatchService {

    private final static Logger LOGGER = getLogger(CostMatchServiceImpl.class);

    @Autowired
    private CostMatchDao costMatchDao;

    @Autowired
    private CostApplicationDao costApplicationDao;

    @Override
    public List<SettlementMatchEntity> queryList(Map<String, Object> map) {
        List<SettlementMatchEntity> settlement= costMatchDao.queryList(map);

        for (SettlementMatchEntity lists : settlement){
            String costDeptIds = costMatchDao.costDeptIds(lists.getCostNo());
            lists.setCostDeptIds(costDeptIds);
        }
        return settlement;
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return costMatchDao.queryCount(map);
    }

    @Override
    public List<CostEntity> queryDetail(String costNo) {
        return costMatchDao.queryDetail(costNo);
    }

    @Override
    public List<CostEntity> querySelectDetail(String[] costNoArray) {
        return costMatchDao.querySelectDetail(costNoArray);
    }

    @Override
    public int selectInvoice(RecordInvoiceEntity entity) {
        return costMatchDao.selectInvoice(entity);
    }

    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final CostMatchImport costImport= new CostMatchImport(multipartFile, costApplicationDao);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<List<String>> list = costImport.analysisExcel();
            //存发票信息
            List<RecordInvoiceEntity> invoiceList = newArrayList();

            for(List<String> data : list){
                boolean isMultiple = false;

                for(RecordInvoiceEntity invoice : invoiceList){
                    if((invoice.getInvoiceCode()+invoice.getInvoiceNo()).equals(data.get(1)+data.get(2))){
                        CostEntity cost = getCostInfo(Long.parseLong(data.get(9)));
                        invoice.getRateTableData().get(0).getCostTableData().add(cost);
                        isMultiple = true;
                        break;
                    }
                }
                if(!isMultiple){
                    RecordInvoiceEntity invoiceEntity = new RecordInvoiceEntity();
                    invoiceEntity.setGfTaxNo(data.get(0));
                    invoiceEntity.setInvoiceCode(data.get(1));
                    invoiceEntity.setInvoiceNo(data.get(2));
                    invoiceEntity.setInvoiceAmount(new BigDecimal(data.get(3)));
                    invoiceEntity.setTaxAmount(new BigDecimal(data.get(5)));
                    invoiceEntity.setTotalAmount(new BigDecimal(data.get(6)));
                    invoiceEntity.setInvoiceDate(data.get(7));
                    invoiceEntity.setCheckCode(data.get(8));

                    List<RateEntity> rateList = newArrayList();
                    RateEntity rateEntity = new RateEntity();
                    rateEntity.setInvoiceAmount(invoiceEntity.getTotalAmount());
                    rateEntity.setTaxRate(data.get(4));
                    rateEntity.setTaxAmount(invoiceEntity.getTaxAmount());

                    List<CostEntity> costList = newArrayList();
                    CostEntity costEntity = getCostInfo(Long.parseLong(data.get(9)));
                    costList.add(costEntity);

                    rateEntity.setCostTableData(costList);
                    rateList.add(rateEntity);
                    invoiceEntity.setRateTableData(rateList);
                    invoiceList.add(invoiceEntity);
                }
            }

            map.put("success", Boolean.TRUE);
            map.put("dataList",invoiceList);
        } catch (DuplicatedException re) {
            LOGGER.error("发票重复使用:{}", re);
            map.put("success", Boolean.FALSE);
            map.put("reason", re.getMessage());
        } catch (Exception e) {
            LOGGER.error("解析excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "解析excel文件异常！");
        }
        return map;
    }

    /**
     * 根据主键获取费用行信息
     * @param id
     * @return
     */
    private CostEntity getCostInfo(Long id){
        return costMatchDao.getCostInfo(id);
    }
}
