package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.dao.UniversalTaxRateDao;
import com.xforceplus.wapp.modules.base.entity.UniversalTaxRateEntity;
import com.xforceplus.wapp.modules.base.export.CommodityImport;
import com.xforceplus.wapp.modules.base.service.UniversalTaxRateService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class UniversalTaxRateServiceImpl implements UniversalTaxRateService {

    private final static Logger LOGGER = getLogger(UniversalTaxRateServiceImpl.class);

    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;

    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;

    /**
     * 远程文件存放路径
     */
    //@Value("${filePathConstan.remoteKnowFileRootPath}")
    private String remoteKnowFileRootPath;

    @Autowired
    private UniversalTaxRateDao universalTaxRateDao;

    @Override
    public List<UniversalTaxRateEntity> queryList(Map<String, Object> map) {
        return universalTaxRateDao.queryList(map);
    }

    @Override
    public List<UniversalTaxRateEntity> queryCommodity(Map<String, Object> map) {
        return universalTaxRateDao.queryCommodity(map);
    }

    @Override
    public Integer queryCommodityCount(Map<String, Object> map) {
        return universalTaxRateDao.queryCommodityCount(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return universalTaxRateDao.queryCount(map);
    }

    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final CommodityImport commodityImport = new CommodityImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<UniversalTaxRateEntity>universalTaxRateList = commodityImport.analysisExcel();
            if (!universalTaxRateList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<UniversalTaxRateEntity>> entityMap = universalTaxImportData(universalTaxRateList);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
                map.put("succeeds",entityMap.get("reasons"));


            } else {
                LOGGER.info("读取到excel模板错误");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel模板错误！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;

    }


    private Map<String, List<UniversalTaxRateEntity>> universalTaxImportData(List<UniversalTaxRateEntity> universalTaxList){
        //返回值
        final Map<String, List<UniversalTaxRateEntity>> map = newHashMap();
        //导入成功的数据集
        final List<UniversalTaxRateEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<UniversalTaxRateEntity> errorEntityList = newArrayList();

        universalTaxList.forEach(universalTaxData -> {
            String vendorNbr = universalTaxData.getVendorNbr();
            String deptId = universalTaxData.getDeptId();
            String itemNbr = universalTaxData.getItemNbr();
            String vendorName = universalTaxData.getVendorName();
            String notes = universalTaxData.getNotes();
            String inputTax = universalTaxData.getInputTax();


            if (!vendorNbr.trim().isEmpty() && !deptId.isEmpty() && !itemNbr.isEmpty() && !vendorName.isEmpty()&& !notes.isEmpty()&&!inputTax.isEmpty()) {
                if( vendorNbr.matches("^[0-9]{1,6}$")){
                    if(vendorNbr.trim().length()<6){
                        int c=6-vendorNbr.trim().length();
                        for(int i=0;i<c;i++){
                            vendorNbr = "0"+vendorNbr;
                        }
                        universalTaxData.setVendorNbr(vendorNbr);
                    }
                    successEntityList.add(universalTaxData);
                }

            } else {
                errorEntityList.add(universalTaxData);
            }
        });
        if(errorEntityList.size()==0){

            //如果都校验通过，保存入库
            for(UniversalTaxRateEntity tax: successEntityList){
                if(universalTaxRateDao.selectCont(tax)>0){
                    errorEntityList.add(tax);
                    universalTaxRateDao.updateUniversalTaxRate(tax);

                }else {
                    universalTaxRateDao.insertUniversalTaxRate(tax);
                }

            }

        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);

        return map;
    }


    @Override
    public Map<String, Object> saveCommodity(UniversalTaxRateEntity universalTaxRateEntity) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);

        try {

            int i = universalTaxRateDao.getCount(universalTaxRateEntity);
            if(i==0){
                final int r = universalTaxRateDao.saveCommodity(universalTaxRateEntity);
                if (r == 1) {
                    LOGGER.info("保存商品信息 {} 成功!", universalTaxRateEntity.toString());
                } else {
                    LOGGER.info("保存商品信息 {} 失败!", universalTaxRateEntity.toString());
                    map.put("code", 2);
                    map.put("msg",  "保存商品信息失败");
                }
            }else {
                map.put("code", 4);
                map.put("msg",  "保存商品号已经存在");
            }

        } catch (Exception e) {
            LOGGER.error("保存商品信息异常！cause:{} universalTaxRateEntity:{}", e.getCause(), universalTaxRateEntity.toString(), e);
            map.put("code", 3);
            map.put("msg",  "保存商品信息异常");
        }
        return map;
    }

    @Override
    public int deleteCommodity(Long []ids) {
            int r = 0 ;
        try {
            r = universalTaxRateDao.deleteCommodity(ids);
        } catch (Exception e) {
            LOGGER.error("删除商品信息异常! ids:{} cause:{}", ids, e.getCause(), e);
        }
        return r;
    }

    @Override
    public int deleteVendor(String vendorNbr){
        int b = 0;
        try {
            b = universalTaxRateDao.deleteVendor(vendorNbr);
        }catch (Exception e) {
            LOGGER.error("删除供应商信息异常! vendorNbr:{} cause:{}", vendorNbr, e.getCause(), e);
        }
        return b;
    }
}
