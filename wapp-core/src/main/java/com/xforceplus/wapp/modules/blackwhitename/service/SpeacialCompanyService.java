package com.xforceplus.wapp.modules.blackwhitename.service;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.blackwhitename.constants.Constants;
import com.xforceplus.wapp.modules.blackwhitename.convert.SpeacialBlackCompanyConverter;
import com.xforceplus.wapp.modules.blackwhitename.convert.SpeacialCompanyConverter;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyBlackImportDto;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.blackwhitename.listener.SpeclialBlackCompanyImportListener;
import com.xforceplus.wapp.modules.blackwhitename.listener.SpeclialCompanyImportListener;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TAcOrgDao;
import com.xforceplus.wapp.repository.dao.TXfBlackWhiteCompanyDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;

/**
 * 黑白名单相关逻辑操作
 */
@Service
@Slf4j
public class SpeacialCompanyService extends ServiceImpl<TXfBlackWhiteCompanyDao, TXfBlackWhiteCompanyEntity> {
    private final SpeacialCompanyConverter companyConverter;

    @Value("${wapp.export.tmp}")
    private String tmp;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private SpeacialBlackCompanyConverter speacialBlackCompanyConverter;

    @Autowired
    ExportCommonService exportCommonService;
    
    @Autowired
    private TAcOrgDao orgDao;

    public SpeacialCompanyService(SpeacialCompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Page<TXfBlackWhiteCompanyEntity> page(Long current, Long size, String taxNo, String companyName, String type, String createTimeStart, String createTimeEnd, String supplier6d, String sapNo) {
        LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity> wrapper = new LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity>(baseMapper);
        wrapper.eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED);
        if (StringUtils.isNotEmpty(taxNo)) {
            wrapper.eq(TXfBlackWhiteCompanyEntity::getSupplierTaxNo, taxNo);
        }
        if (StringUtils.isNotEmpty(companyName)) {
            wrapper.eq(TXfBlackWhiteCompanyEntity::getCompanyName, companyName);
        }
        if (StringUtils.isNotEmpty(type)) {
            wrapper.eq(TXfBlackWhiteCompanyEntity::getSupplierType, type);
        }
        if (StringUtils.isNotEmpty(sapNo)) {
            wrapper.eq(TXfBlackWhiteCompanyEntity::getSapNo, sapNo);
        }

        if (StringUtils.isNotEmpty(createTimeStart)) {
            createTimeStart = DateUtils.date2DateTimeStart(createTimeStart);

            wrapper.ge(TXfBlackWhiteCompanyEntity::getCreateTime, createTimeStart);
        }
        if (StringUtils.isNotEmpty(supplier6d)) {
            wrapper.eq(TXfBlackWhiteCompanyEntity::getSupplier6d, supplier6d);
        }

        if (StringUtils.isNotEmpty(createTimeEnd)) {
            createTimeEnd = DateUtils.date2DateTimeEnd(createTimeEnd);
            wrapper.le(TXfBlackWhiteCompanyEntity::getCreateTime, createTimeEnd);
        }
        Page<TXfBlackWhiteCompanyEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("黑白名单信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return page;
    }

    public TXfBlackWhiteCompanyEntity getBlackListBySapNo(String sapNo, String supplierType) {
        QueryWrapper<TXfBlackWhiteCompanyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfBlackWhiteCompanyEntity.SAP_NO, sapNo);
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_TYPE, supplierType);
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_STATUS, Constants.COMPANY_STATUS_ENABLED);
        List<TXfBlackWhiteCompanyEntity> list = list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 导入黑白名单信息
     *
     * @param file
     * @return
     */
    public SpecialCompanyImportSizeDto importBlackData(InputStream excelInputStream, String type, String originalFilename) throws IOException {
        SpecialCompanyImportSizeDto sizeDto = new SpecialCompanyImportSizeDto();
        QueryWrapper wrapper = new QueryWrapper<>();
        SpeclialBlackCompanyImportListener listener = new SpeclialBlackCompanyImportListener(type);
        EasyExcel.read(excelInputStream, SpecialCompanyBlackImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(listener.getValidInvoices().size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        log.info("导入数据解析条数:{}", listener.getRows());
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
    //过滤出税号+6d去重后的数据
            List<SpecialCompanyBlackImportDto> supplierCodeList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getSupplierTaxNo()+f.getSupplier6d()))), ArrayList::new)
            );
            List<TXfBlackWhiteCompanyEntity> validList = speacialBlackCompanyConverter.reverse(supplierCodeList, UserUtil.getUserId());

//            List<String> supplier6dList = validList.stream().map(s->s.getSupplier6d()).distinct().collect(Collectors.toList());
//            QueryWrapper<TAcOrgEntity> q = new QueryWrapper<>();
//            q.in(TAcOrgEntity.ORG_CODE,supplier6dList);
//            int count = orgDao.selectCount(q);
//            if(count != supplier6dList.size()){
//                sizeDto.setErrorMsg("供应商6D编号无法与机构orgcode匹配，请修改正确供应商6D编号");
//                return sizeDto;
//            }
            
            validList.stream().forEach(e -> {
                e.setSupplierType(type);
                TXfBlackWhiteCompanyEntity obj = getOne(
                        new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                                .lambda()
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierType, type)
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierTaxNo, e.getSupplierTaxNo())
                                .eq(TXfBlackWhiteCompanyEntity::getSupplier6d, e.getSupplier6d())
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
                );
                if (obj != null) {
                    e.setId(obj.getId());
                }

                e.setCreateUser(UserUtil.getLoginName());
                e.setSupplierStatus(Constants.COMPANY_STATUS_ENABLED);
                e.setUpdateUser(UserUtil.getLoginName());
            });
            boolean save = saveOrUpdateBatch(validList);
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, originalFilename);
            EasyExcel.write(tmp + "/" + originalFilename, SpecialCompanyBlackImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String exportFileName = "导入失败原因" + String.valueOf(System.currentTimeMillis()) + ExcelExportUtil.FILE_NAME_SUFFIX;
            String ftpFilePath = ftpPath + "/" + exportFileName;
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, exportFileName, inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(type));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpFilePath);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "黑名单导入错误信息", exportCommonService.getSuccContent());

        }
        return sizeDto;
    }

    public SpecialCompanyImportSizeDto importWhiteData(InputStream excelInputStream, String type, String originalFilename) throws IOException {
        QueryWrapper wrapper = new QueryWrapper<>();
        SpeclialCompanyImportListener listener = new SpeclialCompanyImportListener(type);
        SpecialCompanyImportSizeDto sizeDto = new SpecialCompanyImportSizeDto();
        EasyExcel.read(excelInputStream, SpecialCompanyImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(listener.getValidInvoices().size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
            List<SpecialCompanyImportDto> supplierCodeList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getSupplierTaxNo()+f.getSupplier6d()))), ArrayList::new)
            );
            List<TXfBlackWhiteCompanyEntity> validList = companyConverter.reverse(supplierCodeList, UserUtil.getUserId());

//            List<String> supplier6dList = validList.stream().map(s->s.getSupplier6d()).distinct().collect(Collectors.toList());
//            QueryWrapper<TAcOrgEntity> q = new QueryWrapper<>();
//            q.in(TAcOrgEntity.ORG_CODE,supplier6dList);
//            int count = orgDao.selectCount(q);
//            if(count != supplier6dList.size()){
//                sizeDto.setErrorMsg("供应商6D编号无法与机构orgcode匹配，请修改正确供应商6D编号");
//                return sizeDto;
//            }

            validList.stream().forEach(e -> {
                e.setSupplierType(type);
                TXfBlackWhiteCompanyEntity obj = getOne(
                        new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                                .lambda()
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierType, type)
                                .eq(TXfBlackWhiteCompanyEntity::getSupplier6d, e.getSupplier6d())
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierTaxNo, e.getSupplierTaxNo())
                                .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
                );
                if (obj != null) {
                    e.setId(obj.getId());
                }

                e.setCreateUser(UserUtil.getLoginName());
                e.setSupplierStatus(Constants.COMPANY_STATUS_ENABLED);
                e.setUpdateUser(UserUtil.getLoginName());
            });
            boolean save = saveOrUpdateBatch(validList);
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, originalFilename);
            EasyExcel.write(tmp + "/" + originalFilename, SpecialCompanyImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String exportFileName = "导入失败原因" + String.valueOf(System.currentTimeMillis()) + ExcelExportUtil.FILE_NAME_SUFFIX;
            String ftpFilePath = ftpPath + "/" + exportFileName;
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, exportFileName, inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(type));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + exportFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "白名单导入错误信息", exportCommonService.getSuccContent());

        }
        return sizeDto;
    }


    /**
     * 判断供应商是否在黑白名单中
     *
     * @param supplierType {@link String} 0-黑名单 1-白名单
     * @param memo         供应商6D
     * @return
     */
    public boolean hitBlackOrWhiteBy6D(String supplierType, String memo) {
        return 0 < count(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        .eq(TXfBlackWhiteCompanyEntity::getSupplier6d, memo)
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
        );
    }

    public Map<String, TXfBlackWhiteCompanyEntity> hitBlackOrWhiteBy6D(String supplierType, List<String> memoList) {
        List<TXfBlackWhiteCompanyEntity> list = list(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        .in(TXfBlackWhiteCompanyEntity::getSupplier6d, memoList)
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
        );
        Map<String, TXfBlackWhiteCompanyEntity> map = Maps.newHashMap();
        list.forEach(t -> {
            map.put(t.getSupplier6d(), t);
        });
        return map;
    }

    public Map<String, TXfBlackWhiteCompanyEntity> hitBlackOrWhiteBySapNo(String supplierType, List<String> sapNoList) {
        List<TXfBlackWhiteCompanyEntity> list = list(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        .in(TXfBlackWhiteCompanyEntity::getSapNo, sapNoList)
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
        );
        Map<String, TXfBlackWhiteCompanyEntity> map = Maps.newHashMap();
        list.forEach(t -> {
            map.put(t.getSapNo(), t);
        });
        return map;
    }

    public boolean hitBlackOrWhiteBySapNo(String supplierType, String sapNo) {
        return 0 < count(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        .eq(TXfBlackWhiteCompanyEntity::getSapNo, sapNo)
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierStatus, Constants.COMPANY_STATUS_ENABLED)
        );
    }

    /**
     * 批量删除
     *
     * @param id
     */
    public void deleteById(Long[] id) {
        List<TXfBlackWhiteCompanyEntity> list = new ArrayList<>();
        for (int i = 0; i < id.length; i++) {
            TXfBlackWhiteCompanyEntity entity = new TXfBlackWhiteCompanyEntity();
            entity.setId(id[i]);
            entity.setSupplierStatus(Constants.COMPANY_STATUS_DELETE);
            list.add(entity);
        }
        this.updateBatchById(list);

    }
}
