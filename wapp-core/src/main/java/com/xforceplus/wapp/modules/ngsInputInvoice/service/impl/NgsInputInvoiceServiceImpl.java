package com.xforceplus.wapp.modules.ngsInputInvoice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.domain.ngs.NgsInputInvoiceQuery;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.ngsInputInvoice.dto.ExportDto;
import com.xforceplus.wapp.modules.ngsInputInvoice.dto.NgsInputInvoiceRequest;
import com.xforceplus.wapp.modules.ngsInputInvoice.service.NgsInputInvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxNgsInputInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

@Slf4j
@Service
public class NgsInputInvoiceServiceImpl extends ServiceImpl<TDxNgsInputInvoiceDao, TDxNgsInputInvoiceEntity> implements NgsInputInvoiceService {
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private TDxNgsInputInvoiceDao tdxngsInputInvoiceDao;

    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Value("${wapp.export.tmp}")
    private String tmp;

    @Override
    public Page<TDxNgsInputInvoiceEntity> paged(NgsInputInvoiceQuery vo) {
        LambdaQueryWrapper<TDxNgsInputInvoiceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getTaxPeriod()), TDxNgsInputInvoiceEntity::getTaxPeriod, vo.getTaxPeriod());
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getGfTaxNo()), TDxNgsInputInvoiceEntity::getGfTaxNo, vo.getGfTaxNo());
        queryWrapper.gt(!ObjectUtils.isEmpty(vo.getId()), TDxNgsInputInvoiceEntity::getId, vo.getId());
        Page<TDxNgsInputInvoiceEntity> pageRsult = this.page(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        return pageRsult;
    }

    /**
     * 根据ids查询数据
     * @param includes
     * @return
     */
    public List<TDxNgsInputInvoiceEntity> getByBatchIds(List<Long> includes) {
        List<List<Long>> subs = ListUtils.partition(includes , 300);
        LambdaQueryWrapper<TDxNgsInputInvoiceEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 对数据进行切分, 避免sql过长
        List<TDxNgsInputInvoiceEntity> entities = new ArrayList<>();
        for (List<Long> sub : subs) {
            queryWrapper.in(TDxNgsInputInvoiceEntity::getId, sub);
            List<TDxNgsInputInvoiceEntity> list = this.list(queryWrapper);
            entities.addAll(list);
        }
        return entities;
    }
    /**
     * @Description 查询个数
     * @return
     **/
    public Integer count(NgsInputInvoiceQuery request) {
        LambdaQueryWrapper<TDxNgsInputInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request.getTaxPeriod()), TDxNgsInputInvoiceEntity::getTaxPeriod, request.getTaxPeriod());
        wrapper.eq(StringUtils.isNotEmpty(request.getGfTaxNo()), TDxNgsInputInvoiceEntity::getGfTaxNo, request.getGfTaxNo());
        return this.count(wrapper);
    }

    /**
     * @Description 查询数据
     * @return
     **/
    public List<TDxNgsInputInvoiceEntity> queryByPage(NgsInputInvoiceQuery request){
        //获取数据
        return  tdxngsInputInvoiceDao.queryPageCustoms(request.getPageNo(),request.getPageSize(),request.getGfTaxNo(),request.getTaxPeriod());
    }

    /**
     * 导出
     * @param resultList
     * @param request
     * @return
     */
    public R inputInvoiceExport(List<TDxNgsInputInvoiceEntity> resultList, NgsInputInvoiceRequest request) {
        String fileName = "NGS进项发票运维管理数据导出";
        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), fileName);
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        FileInputStream inputStream = null;
        try {
            //创建一个sheet
            File file = FileUtils.getFile(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String title = "NGS进项发票运维管理导出成功";
            List<ExportDto> exportDtos = new ArrayList<>();
            for(TDxNgsInputInvoiceEntity entity:resultList){
                ExportDto dto = new ExportDto();
                dto.setTaxPeriod(entity.getTaxPeriod());
                dto.setCompanyCode(entity.getCompanyCode());
                dto.setJvCode(entity.getJvCode());
                dto.setInvoiceCode(entity.getInvoiceCode());
                dto.setInvoiceNo(entity.getInvoiceNo());
                dto.setScanTime(entity.getScanTime());
                dto.setVenderid(entity.getVenderid());
                dto.setVendername(entity.getVendername());
                dto.setCostCenter(entity.getCostCenter());
                dto.setTaxAmount(entity.getTaxAmount());
                dto.setTaxRate(entity.getTaxRate());
                dto.setTaxCode(entity.getTaxCode());
                dto.setAmountWithTax(entity.getAmountWithTax());
                dto.setAmountWithoutTax(entity.getAmountWithoutTax());
                dto.setVoucherNo(entity.getVoucherNo());
                dto.setPaperDrewDate(entity.getPaperDrewDate());
                dto.setNewInvoiceNo(entity.getNewInvoiceNo());
                dto.setRemark(entity.getRemark());
                dto.setInvoiceAge(entity.getInvoiceAge());
                dto.setBusinessType(entity.getBusinessType());
                dto.setScanUser(entity.getScanUser());
                dto.setIsImmovables(entity.getIsImmovables());
                dto.setLargeCategory(entity.getLargeCategory());
                dto.setInvoiceType(entity.getInvoiceType());
                dto.setUuid(entity.getUuid());
                dto.setGfTaxNo(entity.getGfTaxNo());
                dto.setHandoverPerson(entity.getHandoverPerson());
                dto.setExpenseSubject(entity.getExpenseSubject());
                dto.setInputOutAmount(entity.getInputOutAmount());
                dto.setInputOutputVoucher(entity.getInputOutputVoucher());
                dto.setKdkInputTaxAmount(entity.getKdkInputTaxAmount());
                exportDtos.add(dto);
            }
            File excl = FileUtils.getFile(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, ExportDto.class)
                    .sheet("sheet1").doWrite(exportDtos);

            //推送sftp
            inputStream = FileUtils.openInputStream(excl);
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), title, exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return R.ok();
    }
}
