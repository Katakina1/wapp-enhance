package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.certification.dao.ImportCertificationDao;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.certification.export.InvoiceCertificationImport;
import com.xforceplus.wapp.modules.certification.service.ImportCertificationService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入认证业务层接口
 *
 * @author Colin.hu
 * @date 4/19/2018
 */
@Service
public class ImportCertificationServiceImpl implements ImportCertificationService {

    private final static Logger LOGGER = getLogger(ImportCertificationServiceImpl.class);

    private final ImportCertificationDao importCertificationDao;

    @Autowired
    private ManualCertificationImpl manualCertification;

    @Autowired
    public ImportCertificationServiceImpl(ImportCertificationDao importCertificationDao) {
        this.importCertificationDao = importCertificationDao;
    }

    /**
     * 导入
     *
     * @param file 导入excel文件
     * @return 执行导入操作的执行结果
     */
    @Override
    public Map<String, Object> importEnjoySubsided(String schemaLabel, Long userId, MultipartFile file) {
        final InvoiceCertificationImport certificationImport = new InvoiceCertificationImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<ImportCertificationEntity> certificationEntityList = certificationImport.analysisExcel();
            if (!certificationEntityList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<ImportCertificationEntity>> entityMap = checkImportData(schemaLabel, userId, certificationEntityList);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    @Override
    public Integer submitAuth(String schemaLabel, Map<String, String> param,String userAccount,String userName) {
        final String jsonParam = param.get("jsonParam");
        final List<ImportCertificationEntity> entityList = new Gson().fromJson(jsonParam, new TypeToken<List<ImportCertificationEntity>>(){}.getType());
        //添加税款所属期
        Integer count = 0 ;
        for(ImportCertificationEntity entity:entityList){
            String id = String.valueOf(entity.getId());
            String currentTaxPeriod = manualCertification.getCurrentTaxPeriod(schemaLabel,id);
            entity.setCurrentTaxPeriod(currentTaxPeriod);
            int rows = importCertificationDao.updateAuthHandleStatusAndTaxPeriod(schemaLabel, entity,userAccount,userName);
            count = count+rows;
        }
        return count;
    }


    /**
     * 导入的数据验证
     * 1.所有导入成功的数据显示
     * 2.选择框可勾选条件：
     * ①发票状态正常
     * ②金额大于0
     * ③税额不小于0
     * ④发票类型为模板要求的类型
     * ⑤导入的发票已有底账信息
     *
     * @param certificationEntityList 导入的数据
     * @return 验证结果
     */
    private Map<String, List<ImportCertificationEntity>> checkImportData(String schemaLabel, Long userId, List<ImportCertificationEntity> certificationEntityList) {
        //返回值
        final Map<String, List<ImportCertificationEntity>> map = newHashMap();
        //导入成功的数据集
        final List<ImportCertificationEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<ImportCertificationEntity> errorEntityList = newArrayList();
        //人员下的所有税号
        List<String> taxNoList = importCertificationDao.getTaxNoList(schemaLabel, userId);
        if (taxNoList == null) {
            taxNoList = newArrayList();
        }
        final List<String> finalTaxNoList = taxNoList;
        //循环遍历查验发票信息在抵账表是否存在
        certificationEntityList.forEach(importCertificationEntity -> {
            if(checkListRecordInvoiceEntity(importCertificationEntity)) {
                final InvoiceCollectionInfo info = importCertificationDao.checkInvoiceExist(schemaLabel, importCertificationEntity.getInvoiceNo(), importCertificationEntity.getInvoiceCode());
                //发票类型
                final String invoiceType = CommonUtil.getFplx(importCertificationEntity.getInvoiceCode());
                importCertificationEntity.setInvoiceType(invoiceType);
                if (!StringUtils.isEmpty(invoiceType) && ("01".equals(invoiceType) || "03".equals(invoiceType) || "14".equals(invoiceType))) {
                    importCertificationEntity.setInvoiceTypeName(InvoiceTypeEnum.invoiceTypeMap().get(invoiceType));
                } else {
                    importCertificationEntity.setInvoiceTypeName("未知发票");
                }
                //不存在抵账
                importCertificationEntity.setRecordFlag("0");
                if (info != null) {
                    //抵账表id
                    importCertificationEntity.setId(info.getId());
                    importCertificationEntity.setInvoiceStatus(info.getInvoiceStatus());
                    importCertificationEntity.setTaxAmount(info.getTaxAmount());
                    importCertificationEntity.setCompareToResult(compareToDate(info.getInvoiceDate(), info.getRzhBelongDate()));
                    importCertificationEntity.setAuthStatus(info.getAuthStatus());
                    importCertificationEntity.setRzhYesorno(info.getRzhYesorno());
                    //开票时间
                    importCertificationEntity.setInvoiceDate(new DateTime(info.getInvoiceDate()).toString("yyyy-MM-dd"));
                    //金额
                    importCertificationEntity.setAmount(info.getInvoiceAmount());
                    //是否有效
                    importCertificationEntity.setValid(info.getValid());
                    //存在抵账
                    importCertificationEntity.setRecordFlag("1");
                    //没有税号权限
                    if (!finalTaxNoList.contains(info.getGfTaxNo())) {
                        importCertificationEntity.setRecordFlag("2");
                    }
                }
                successEntityList.add(importCertificationEntity);
            } else {
                errorEntityList.add(importCertificationEntity);
            }
        });
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }

    /**
     * 开票日期与认证归属期比较
     *
     * @param invoiceDate   开票日期
     * @param rzhBelongDate 认证归属期
     * @return true 开票日期小于或等于认证归属期
     */
    private Boolean compareToDate(Date invoiceDate, String rzhBelongDate) {

        if(StringUtils.isEmpty(rzhBelongDate)) {
            return Boolean.FALSE;
        }
        final String invoiceDateStr = new DateTime(invoiceDate).toString("yyyyMM");

        final Integer result = invoiceDateStr.compareTo(rzhBelongDate);

        if (!(result > 0)) {
            //开票日期小于或等于0
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 对导入的数据进行数据格式检测 校验数据是否符合格式要求
     *
     * @param importCertificationEntity 导入的数据实体
     * @return true校验成功 false校验失败
     */
    private Boolean checkListRecordInvoiceEntity(ImportCertificationEntity importCertificationEntity) {

        //发票代码格式校验 10或12位数字
        final Boolean codeFlag = CommonUtil.isValidNum(importCertificationEntity.getInvoiceCode(), "^(\\d{10}|\\d{12})$");
        if (!codeFlag) {
           importCertificationEntity.setNoAuthTip("1");
        }

        //发票号码格式校验 8位数字
        final Boolean numFlag = CommonUtil.isValidNum(importCertificationEntity.getInvoiceNo(), "^[\\d]{8}$");
        if (!numFlag) {
            importCertificationEntity.setNoAuthTip("2");
        }

        //金额校验 必须为数字
        final Boolean amountlag = StringUtils.isNotEmpty(importCertificationEntity.getAmount()) && CommonUtil.isNumber(importCertificationEntity.getAmount());
        if(!amountlag) {
            importCertificationEntity.setNoAuthTip("3");
        }

        //开票日期格式校验
        Boolean dateFlag = Boolean.FALSE ;
        if(StringUtils.isNotEmpty(importCertificationEntity.getInvoiceDate())) {
            dateFlag = CommonUtil.isValidDate(importCertificationEntity.getInvoiceDate(), DEFAULT_SHORT_DATE_FORMAT, "[0-9]{4}-[0-9]{2}-[0-9]{2}");
            if (!dateFlag) {
                importCertificationEntity.setNoAuthTip("4");
            }
        } else {
            importCertificationEntity.setNoAuthTip("4");
        }

        return codeFlag && numFlag && amountlag && dateFlag;
    }
}
