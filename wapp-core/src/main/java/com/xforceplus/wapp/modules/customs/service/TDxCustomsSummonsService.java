package com.xforceplus.wapp.modules.customs.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.enums.customs.InvoiceCheckEnum;
import com.xforceplus.wapp.modules.entryaccount.dto.CustomsSummonsDto;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.common.vo.CustomsSummonsExportVo;
import com.xforceplus.wapp.common.vo.CustomsSummonsVo;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.repository.dao.TDxCustomsSummonsDao;
import com.xforceplus.wapp.repository.dao.TDxCustomsDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.ExcelExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @Author: ChenHang
 * @Date: 2023/7/4 21:07
 */
@Service
@Slf4j
public class TDxCustomsSummonsService extends ServiceImpl<TDxCustomsSummonsDao, TDxCustomsSummonsEntity> {

    @Autowired
    private TDxCustomsDao tDxCustomsDao;

    @Autowired
    private TDxCustomsSummonsDao customsSummonsDao;

    @Autowired
    private CustomsDetailService customsDetailService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    private ExcelExportUtils excelExportUtils;

    @Value("${wapp.export.tmp}")
    private String tmp;

    /**
     * 保存或更新海关缴款书传票清单
     * @param entryAccountDTO
     */
    public void saveOrUpdateCustomsSummons(EntryAccountDTO entryAccountDTO, TDxCustomsEntity tDxCustomsEntity, Map<BigDecimal, List<TDxCustomsDetailEntity>> taxRateMap) {
        log.info("保存或更新海关缴款书传票清单入参, entryAccountDTO:{}, tDxCustomsEntity:{}", JSONObject.toJSONString(entryAccountDTO), JSONObject.toJSONString(tDxCustomsEntity));

        for (BigDecimal bigDecimal : taxRateMap.keySet()) {
            List<TDxCustomsDetailEntity> detailEntities = taxRateMap.get(bigDecimal);
            // 按照税率的维度保存传票清单
            // 不含税金额
            BigDecimal invoiceAmount = new BigDecimal(0);
//            // 税额
//            BigDecimal taxAmount = new BigDecimal(0);

            for (TDxCustomsDetailEntity detailEntity : detailEntities) {
                invoiceAmount = invoiceAmount.add(detailEntity.getDutiablePrice());
//                taxAmount = taxAmount.add(detailEntity.getTaxAmount());
            }
            TDxCustomsSummonsEntity tdxCustomsSummonsEntity = TDxCustomsSummonsEntity.builder()
                    .companyCode(entryAccountDTO.getCompanyCode())
                    .jvcode(entryAccountDTO.getJvCode())
                    .invoiceNo(entryAccountDTO.getTaxDocNo())
                    // 供应商号是销方税号对应的,
                    .venderid("261332") // 固定值
                    .vendername("中央金库") // 固定值
                    .taxAmount(entryAccountDTO.getTaxAmount())
                    .taxCode(entryAccountDTO.getTaxCode())
                    .taxRate(entryAccountDTO.getTaxRate())
                    .totalAmount(invoiceAmount.add(entryAccountDTO.getTaxAmount()))
                    .certificateNo(entryAccountDTO.getAccNo())
                    .invoiceDate(tDxCustomsEntity.getPaperDrewDate())
                    .businessType("增值税海关缴款书")
                    .groupCode("AP-GFR") // 组别 固定值
                    .costSubject("进口商品付款") // 费用类科目 固定值
                    .invoiceAmount(invoiceAmount) // 根据明细的完税价格减去税款金额
                    .incomeTaxAmount(entryAccountDTO.getTaxAmount()) // 可抵扣固定资产进项税金(税额)
                    .scanUser("BMS")
                    .largeCategory("商品类")
                    .gfName(tDxCustomsEntity.getCompanyName())
                    .gfTaxNo(tDxCustomsEntity.getCompanyTaxNo())
                    .invoiceType("17")
                    .glInvoice(null)
                    .updateTime(new Date())
                    .build();

            // 业务要求如果是这两个税号则直接赋值替换(目前从RMS过来的只有这两个税号
            if (StringUtils.equals(tDxCustomsEntity.getCompanyTaxNo(), "914403007109368585")){
                //"CHC税号：914403007109368585，默认：D073
                //"CHC税号：914403007109368585，默认：WI
                tdxCustomsSummonsEntity.setCompanyCode("D073");
                tdxCustomsSummonsEntity.setJvcode("WI");
            }
            if (StringUtils.equals(tDxCustomsEntity.getCompanyTaxNo(), "91310115MA1K4RCP3U")){
                //SHC税号：91310115MA1K4RCP3U，默认：D155"
                //SHC税号：91310115MA1K4RCP3U，默认：IV"
                tdxCustomsSummonsEntity.setCompanyCode("D155");
                tdxCustomsSummonsEntity.setJvcode("IV");
            }

            // 根据海关缴款书号码和税率查询数据
            LambdaQueryWrapper<TDxCustomsSummonsEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TDxCustomsSummonsEntity::getInvoiceNo, entryAccountDTO.getTaxDocNo());
            queryWrapper.eq(TDxCustomsSummonsEntity::getTaxRate, bigDecimal);
            TDxCustomsSummonsEntity dbCustomsSummons = this.getOne(queryWrapper);
            if (ObjectUtil.isEmpty(dbCustomsSummons)) {
                tdxCustomsSummonsEntity.setCreateTime(new Date());
                tdxCustomsSummonsEntity.setIsCheck(InvoiceCheckEnum.CHECK_2.getCode().toString());
                log.info("海关缴款书传票清单保存入参:{}", JSONObject.toJSONString(tdxCustomsSummonsEntity));
                this.save(tdxCustomsSummonsEntity);
            } else {
                tdxCustomsSummonsEntity.setId(dbCustomsSummons.getId());
                log.info("海关缴款书传票清单更新入参:{}", JSONObject.toJSONString(tdxCustomsSummonsEntity));
                this.updateById(tdxCustomsSummonsEntity);
            }
        }


    }

    /**
     * 海关缴款书传票清单查询
     * @return
     */
    public PageResult<CustomsSummonsDto> queryCustomsSummonsList(CustomsSummonsVo request) {
        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);
        log.info("海关缴款书传票清单查询--处理后的请求参数{}", JSON.toJSON(request));
        //获取数据
        List<TDxCustomsSummonsEntity> customsEntities = this.queryByPage(request);
        //总个数
        int count = this.queryCount(request);
        List<CustomsSummonsDto> response = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customsEntities)){
            for(TDxCustomsSummonsEntity customsSummonsEntity : customsEntities){
                CustomsSummonsDto customsSummonsDto = copyEntity(customsSummonsEntity);
                customsSummonsDto.setGlInvoice("否");
                customsSummonsDto.setIsImmovables("否");
                response.add(customsSummonsDto);
            }
        }
        return PageResult.of(response, count, pageNo, request.getPageSize());
    }


    public List<TDxCustomsSummonsEntity> queryByPage(CustomsSummonsVo vo) {
        List<Integer> isChecks = InvoiceCheckEnum.isChecks().stream().map(InvoiceCheckEnum::getCode).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(vo.getVoucherAccountTimeStart()) && StringUtils.isNotEmpty(vo.getVoucherAccountTimeEnd())) {
            vo.setVoucherAccountTimeStart(vo.getVoucherAccountTimeStart() + " 00:00:01");
            vo.setVoucherAccountTimeEnd(vo.getVoucherAccountTimeEnd() + " 23:59:59");
        }
        return customsSummonsDao.queryByPage(
                vo.getPageNo(),vo.getPageSize(),
                vo.getInvoiceNo(), vo.getVenderid(),
                vo.getTaxPeriod(), vo.getPaperDrewDateStart(),
                vo.getPaperDrewDateEnd(), isChecks,
                vo.getCertificateNo(), vo.getContractNo(),
                vo.getVoucherAccountTimeStart(), vo.getVoucherAccountTimeEnd());
    }

    /**
     * 对象拷贝
     * @param customsSummonsEntity
     * @return
     */
    private CustomsSummonsDto copyEntity(TDxCustomsSummonsEntity customsSummonsEntity) {
        CustomsSummonsDto customsSummonsDto = new CustomsSummonsDto();
        BeanUtil.copyProperties(customsSummonsEntity, customsSummonsDto);
        return customsSummonsDto;
    }

    /**
     * 条件查询海关缴款书传票清单数量
     * @param vo
     * @return
     */
    public int queryCount(CustomsSummonsVo vo) {
        List<Integer> isChecks = InvoiceCheckEnum.isChecks().stream().map(InvoiceCheckEnum::getCode).collect(Collectors.toList());
        return customsSummonsDao.queryCount(
                vo.getInvoiceNo(), vo.getVenderid(),
                vo.getTaxPeriod(), vo.getPaperDrewDateStart(),
                vo.getPaperDrewDateEnd(), isChecks,
                vo.getCertificateNo(), vo.getContractNo(),
                vo.getVoucherAccountTimeStart(), vo.getVoucherAccountTimeEnd());
    }

    /**
     * 根据ids查询数据
     * @param includes
     * @return
     */
    public List<TDxCustomsSummonsEntity> getByBatchIds(List<Long> includes) {
        List<List<Long>> subs = ListUtils.partition(includes , 300);
        // 对数据进行切分, 避免sql过长
        List<TDxCustomsSummonsEntity> summonsEntities = new ArrayList<>();
        for (List<Long> sub : subs) {
            List<TDxCustomsSummonsEntity> list = customsSummonsDao.queryByIds(sub);
            summonsEntities.addAll(list);
        }
        return summonsEntities;
    }

    /**
     * 海关缴款书传票清单导出
     * @param resultList
     * @param request
     */
    public void export(List<TDxCustomsSummonsEntity> resultList, CustomsSummonsExportVo request) {
        String fileName = "海关缴款书传票清单";

        List<CustomsSummonsDto> exportDtos = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            CustomsSummonsDto dto = new CustomsSummonsDto();
            BeanUtil.copyProperties(resultList.get(i), dto);
            dto.setGlInvoice("否");
            dto.setIsImmovables("否");
            dto.setId((long) (i + 1));
            if (StringUtils.equals("17", dto.getInvoiceType())) {
                dto.setInvoiceType("海关缴款书");
            }
            exportDtos.add(dto);
        }

        excelExportUtils.messageExportOneSheet(exportDtos, CustomsSummonsDto.class, fileName, JSONObject.toJSONString(request), "Sheet1");

    }


}
