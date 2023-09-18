package com.xforceplus.wapp.modules.entryaccount.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.utils.TaxRateUtils;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.common.vo.InvoiceSummonsExportVo;
import com.xforceplus.wapp.common.vo.InvoiceSummonsVo;
import com.xforceplus.wapp.enums.AuthStatusEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.entryaccount.dto.TDxSummonsRMSDto;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.common.vo.ExprotVo;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TAcOrgDao;
import com.xforceplus.wapp.repository.dao.TAcUserDao;
import com.xforceplus.wapp.repository.dao.TDxSummonsRMSDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.ExcelExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 对接RMS入账功能, 非商专票入账才会生成传票清单
 * @Author: ChenHang
 * @Date: 2023/7/17 16:00
 */
@Service
@Slf4j
public class SummonsRMSService extends ServiceImpl<TDxSummonsRMSDao, TDxSummonsRMSEntity> {

    @Autowired
    private TDxRecordInvoiceDetailService recordInvoiceDetailService;


    @Autowired
    private TDxSummonsRMSDao summonsRMSDao;

    @Autowired
    private TAcUserDao tAcUserDao;

    @Autowired
    private ExcelExportUtils excelExportUtils;

    /**
     * 生成或更新RMS过来的非商传票清单
     * @param entryAccountDTO
     */
    public void saveOrUpdateSummons(EntryAccountDTO entryAccountDTO, TDxRecordInvoiceEntity tDxRecordInvoiceEntity, List<TDxRecordInvoiceDetailEntity> detailEntityList) {
        log.info("对接RMS非商入账传票清单入参, entryAccountDTO:{}, tDxRecordInvoiceEntity:{}, detailEntityList:{}", JSONObject.toJSONString(entryAccountDTO), JSONObject.toJSONString(tDxRecordInvoiceEntity), JSONObject.toJSONString(detailEntityList));
        String uuid = entryAccountDTO.getInvoiceCode() + entryAccountDTO.getInvoiceNo();
        // 根据明细的税率为维度区分明细并统计金额, 根据税率将明细进行分组
        Map<String, List<TDxRecordInvoiceDetailEntity>> taxRateMap = detailEntityList.stream().collect(Collectors.groupingBy(TDxRecordInvoiceDetailEntity::getTaxRate));

        // 根据税号查询供应商id usercode即为供应商id
        TAcUserEntity acUserEntity = tAcUserDao.getByTaxNo(tDxRecordInvoiceEntity.getXfTaxNo());
        String venderid = null;
        if (null != acUserEntity) {
            venderid = acUserEntity.getUsercode();
        }

        for (String taxRate : taxRateMap.keySet()) {
            List<TDxRecordInvoiceDetailEntity> invoiceDetailEntities = taxRateMap.get(taxRate);
            // 大税率指非0.xx的税率
            String bigTaxRate = TaxRateUtils.strTaxRateToStr(taxRate);

            BigDecimal dtoTaxRate = entryAccountDTO.getTaxRate();
            if (dtoTaxRate.compareTo(new BigDecimal(bigTaxRate)) != 0) {
                continue;
            }

            // 按照税率的维度保存传票清单
            // 税额
            BigDecimal taxAmount = new BigDecimal(0);
            // 不含税金额
            BigDecimal invoiceAmount = new BigDecimal(0);
            for (TDxRecordInvoiceDetailEntity detailEntity : invoiceDetailEntities) {
                String goodsName = detailEntity.getGoodsName();
                if (StringUtils.equals(goodsName, "(详见销货清单)") || StringUtils.equals(goodsName, "（详见销货清单）") ||
                        StringUtils.equals(goodsName, "(详见销货清单）") || StringUtils.equals(goodsName, "（详见销货清单)") ||
                        StringUtils.equals(goodsName, "原价合计") || StringUtils.equals(goodsName, "折扣额合计")) {
                    continue;
                }
                taxAmount = taxAmount.add(new BigDecimal(detailEntity.getTaxAmount()));
                invoiceAmount = invoiceAmount.add(new BigDecimal(detailEntity.getDetailAmount()));
            }
            TDxSummonsRMSEntity tDxSummonsRMSEntity = new TDxSummonsRMSEntity();
            tDxSummonsRMSEntity.setInvoiceNo(entryAccountDTO.getInvoiceNo());
            tDxSummonsRMSEntity.setInvoiceCode(entryAccountDTO.getInvoiceCode());
            tDxSummonsRMSEntity.setUuid(uuid);
            tDxSummonsRMSEntity.setCompanyCode(entryAccountDTO.getCompanyCode());
            tDxSummonsRMSEntity.setJvcode(entryAccountDTO.getJvCode());
            tDxSummonsRMSEntity.setScanUser("BMS");
            // 扫描时间
            tDxSummonsRMSEntity.setScanTime(new Date());
//            tDxSummonsRMSEntity.setTaxPeriod(tDxRecordInvoiceEntity.getRzhBelongDate());
//            tDxSummonsRMSEntity.setVenderid(venderid);
            tDxSummonsRMSEntity.setVenderid(entryAccountDTO.getVenderid());
            tDxSummonsRMSEntity.setVendername(tDxRecordInvoiceEntity.getVendername());
            tDxSummonsRMSEntity.setStroe("stroe#");
            tDxSummonsRMSEntity.setTaxCode(entryAccountDTO.getTaxCode());
            tDxSummonsRMSEntity.setInvoiceType(tDxRecordInvoiceEntity.getInvoiceType());
            tDxSummonsRMSEntity.setTaxRate(new BigDecimal(bigTaxRate));
            tDxSummonsRMSEntity.setTaxAmount(taxAmount);
            tDxSummonsRMSEntity.setCertificateNo(entryAccountDTO.getAccNo());
            tDxSummonsRMSEntity.setInvoiceDate(DateUtils.format(tDxRecordInvoiceEntity.getInvoiceDate(), DateUtils.DATE_PATTERN_EN));
            tDxSummonsRMSEntity.setBusinessType("BMS");
            tDxSummonsRMSEntity.setRemark(tDxRecordInvoiceEntity.getRemark());
            tDxSummonsRMSEntity.setLargeCategory(entryAccountDTO.getLargeCategory());
            tDxSummonsRMSEntity.setGfName(tDxRecordInvoiceEntity.getGfName());
            tDxSummonsRMSEntity.setGfTaxNo(tDxRecordInvoiceEntity.getGfTaxNo());
            tDxSummonsRMSEntity.setEpsNo(null);
            tDxSummonsRMSEntity.setGlInvoice(null);

//            tDxSummonsRMSEntity.setTotalAmount(taxAmount.add(invoiceAmount));
            tDxSummonsRMSEntity.setTotalAmount(entryAccountDTO.getTotalAmount());

            tDxSummonsRMSEntity.setCertificateTime(entryAccountDTO.getPostDate());
            tDxSummonsRMSEntity.setIsImmovables("否");
//            tDxSummonsRMSEntity.setInvoiceAmount(invoiceAmount);
            tDxSummonsRMSEntity.setInvoiceAmount(entryAccountDTO.getInvoiceAmount());


            // 根据税率维度保存传票清单
            LambdaQueryWrapper<TDxSummonsRMSEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StringUtils.isNotEmpty(entryAccountDTO.getInvoiceNo()), TDxSummonsRMSEntity::getUuid, entryAccountDTO.getInvoiceNo());
            queryWrapper.eq(StringUtils.isNotEmpty(entryAccountDTO.getInvoiceCode()), TDxSummonsRMSEntity::getUuid, entryAccountDTO.getInvoiceCode());
            queryWrapper.eq(TDxSummonsRMSEntity::getTaxRate, bigTaxRate);
            TDxSummonsRMSEntity dbSummonsRMS = this.getOne(queryWrapper);
            if (ObjectUtil.isEmpty(dbSummonsRMS)) {
                tDxSummonsRMSEntity.setCreateTime(new Date());
                tDxSummonsRMSEntity.setUpdateTime(new Date());
                this.save(tDxSummonsRMSEntity);
            } else {
                tDxSummonsRMSEntity.setId(dbSummonsRMS.getId());
                this.updateById(tDxSummonsRMSEntity);
            }
        }

    }

    /**
     * RMS非商发票入账查询
     * @param vo
     * @return
     */
    public Page<TDxSummonsRMSEntity> invoiceSummonsList(InvoiceSummonsVo vo) {
        vo.setAuthStatus(AuthStatusEnum.AUTH_STATUS_SUCCESS.getCode());
        vo.setPageNo((vo.getPageNo() - 1) * vo.getPageSize());

        Integer total = this.queryCount(vo);
        List<TDxSummonsRMSEntity> records = summonsRMSDao.invoiceSummonsList(vo);
        Page<TDxSummonsRMSEntity> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    public List<TDxSummonsRMSEntity> getByBatchIds(List<Long> includes) {
        List<List<Long>> subs = ListUtils.partition(includes , 300);
        // 对数据进行切分, 避免sql过长
        List<TDxSummonsRMSEntity> summonsEntities = new ArrayList<>();
        for (List<Long> sub : subs) {
            List<TDxSummonsRMSEntity> list = this.listByIds(sub);
            summonsEntities.addAll(list);
        }
        return summonsEntities;
    }

    public void export(List<TDxSummonsRMSEntity> resultList, InvoiceSummonsExportVo request) {
        String fileName = "RMS非商入账传票清单";

        List<TDxSummonsRMSDto> exportDtos = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            TDxSummonsRMSDto dto = new TDxSummonsRMSDto();
            BeanUtil.copyProperties(resultList.get(i), dto);
            dto.setId((long) (i + 1));
            exportDtos.add(dto);
        }
        excelExportUtils.messageExportOneSheet(exportDtos, TDxSummonsRMSDto.class, fileName, JSONObject.toJSONString(request), "Sheet1");

    }

    public Integer queryCount(InvoiceSummonsVo vo) {
        int count = summonsRMSDao.queryCount(vo);
        return count;
    }
}
