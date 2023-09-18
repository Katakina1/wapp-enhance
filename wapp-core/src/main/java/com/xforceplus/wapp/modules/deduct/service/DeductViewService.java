package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.export.dto.DeductViewBillExportDto;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.claim.mapstruct.DeductMapper;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.deduct.mapstruct.MatchedInvoiceMapper;
import com.xforceplus.wapp.modules.deduct.model.VendorExportAgreementBillModel;
import com.xforceplus.wapp.modules.deduct.model.VendorExportClaimBillModel;
import com.xforceplus.wapp.modules.deduct.model.VendorExportEPDBillModel;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.daoExt.BillDeductQueryExtDao;
import com.xforceplus.wapp.repository.daoExt.RecordInvoiceDetailExtDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.vo.BillDeductLeftSettlementVo;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.ExportDeductViewCallable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 18:58
 **/
@Slf4j
@Service
public class DeductViewService extends ServiceImpl<TXfBillDeductExtDao, TXfBillDeductEntity> {

    @Autowired
    private DeductMapper deductMapper;
    @Autowired
    private OverdueServiceImpl overdueService;

    @Autowired
    private AgreementBillService agreementBillService;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private MatchedInvoiceMapper matchedInvoiceMapper;
    @Autowired
    private BlueInvoiceService blueInvoiceService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private RecordInvoiceDetailExtDao recordInvoiceDetailExtDao;
    @Autowired
    private TXfPreInvoiceDao xfPreInvoiceDao;
    @Autowired
    private TXfSettlementDao xfSettlementDao;
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private BillDeductQueryExtDao billDeductQueryExtDao;
    @Autowired
    private BillRefQueryService billRefQueryService;


    private List<BigDecimal> taxRates;

    public DeductViewService(@Value("${wapp.tax-rate}") String rates){
        if (StringUtils.isBlank(rates)){
            throw new EnhanceRuntimeException("缺少配置 wapp.tax-rate");
        }

        final String[] split = rates.split("[,]");
        taxRates=new ArrayList<>();
        for (String rate : split) {
            if (StringUtils.isBlank(rate.trim())){
                throw new EnhanceRuntimeException("[wapp.tax-rate]税率配置以英文逗号隔开，切不能有空");
            }

            final BigDecimal rateDecimal = new BigDecimal(rate.trim());

            if (rateDecimal.compareTo(BigDecimal.ZERO) < 0){
                throw new EnhanceRuntimeException("[wapp.tax-rate]指定的税率不能是负数");
            }

            if (rateDecimal.compareTo(BigDecimal.ONE) > 0){
                throw new EnhanceRuntimeException("[wapp.tax-rate]指定的税率只能为小数位保留2位的小数");
            }

            taxRates.add(rateDecimal.setScale(4));
        }
    }



    public List<SummaryResponse> summary(DeductListRequest request, TXfDeductionBusinessTypeEnum typeEnum) {
        //结算单为待拆票 无法查询对应的业务单
        if(StringUtils.isNotBlank(request.getRefSettlementNo())) {
            QueryWrapper<TXfSettlementEntity> settlementEntityQueryWrapper = new QueryWrapper<>();
            settlementEntityQueryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getRefSettlementNo());
            settlementEntityQueryWrapper.eq(TXfSettlementEntity.SETTLEMENT_STATUS, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
            Integer settlementCount = xfSettlementDao.selectCount(settlementEntityQueryWrapper);
            if(settlementCount > 0){
                request.setRefSettlementNo("-");
            }
        }

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);

//        if (StringUtils.isNotBlank(request.getRedNotificationNo())) {
//            QueryWrapper<TXfPreInvoiceEntity> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq(TXfPreInvoiceEntity.RED_NOTIFICATION_NO, request.getRedNotificationNo());
//            queryWrapper1.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
//                    TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));
//
//            List<TXfPreInvoiceEntity> list1 = xfPreInvoiceDao.selectList(queryWrapper1);
//            List<String> settlementNoList = list1.stream().map(TXfPreInvoiceEntity::getSettlementNo).distinct().collect(Collectors.toList());
//            if(CollectionUtils.isNotEmpty(settlementNoList)) {
//                wrapper.in(TXfBillDeductEntity.REF_SETTLEMENT_NO, settlementNoList);
//            }else{
//                wrapper.in(TXfBillDeductEntity.REF_SETTLEMENT_NO, "-");
//            }
//        }
        // 红字编号查询条件处理
        if (StringUtils.isNotBlank(request.getRedNotificationNo())) {
            QueryWrapper<TXfPreInvoiceEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq(TXfPreInvoiceEntity.RED_NOTIFICATION_NO, request.getRedNotificationNo());
            queryWrapper1.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
                    TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));
            List<TXfPreInvoiceEntity> list1 = xfPreInvoiceDao.selectList(queryWrapper1);
            List<Long> preInvoiceIds = list1.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(preInvoiceIds)) {
                return toSummary(Collections.emptyList());
            }
            List<TXfDeductPreInvoiceEntity> billRefByPreInvoices = billDeductQueryExtDao.getBillRefByPreInvoiceIds(preInvoiceIds);
            List<Long> billIds = billRefByPreInvoices.stream().map(TXfDeductPreInvoiceEntity::getDeductId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(billIds)) {
                return toSummary(Collections.emptyList());
            }
            wrapper.in(TXfBillDeductEntity.ID, billIds);
        }

        wrapper.select(TXfBillDeductEntity.TAX_RATE + " as taxRate", "count(1) as count");

        final List<Map<String, Object>> map = this.getBaseMapper().selectMaps(wrapper.groupBy(TXfBillDeductEntity.TAX_RATE));
        return toSummary(map);
    }

    public PageResult<DeductListResponse> deductByPage(DeductListRequest request, TXfDeductionBusinessTypeEnum typeEnum) {
        //结算单为待拆票 无法查询对应的业务单
        if(StringUtils.isNotBlank(request.getRefSettlementNo())) {
            QueryWrapper<TXfSettlementEntity> settlementEntityQueryWrapper = new QueryWrapper<>();
            settlementEntityQueryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, request.getRefSettlementNo());
            settlementEntityQueryWrapper.eq(TXfSettlementEntity.SETTLEMENT_STATUS, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
            Integer settlementCount = xfSettlementDao.selectCount(settlementEntityQueryWrapper);
            if(settlementCount > 0){
                request.setRefSettlementNo("-");
            }
        }

        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapper(request, typeEnum);
        //结算单状态查询
        wrapper.apply(Objects.nonNull(request.getRefSettlementStatus()), "t.settlement_status = {0}", request.getRefSettlementStatus());
//        if (StringUtils.isNotBlank(request.getRedNotificationNo())) {
//            QueryWrapper<TXfPreInvoiceEntity> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq(TXfPreInvoiceEntity.RED_NOTIFICATION_NO, request.getRedNotificationNo());
//            queryWrapper1.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
//                    TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));
//
//            List<TXfPreInvoiceEntity> list1 = xfPreInvoiceDao.selectList(queryWrapper1);
//            List<String> settlementNoList = list1.stream().map(TXfPreInvoiceEntity::getSettlementNo).distinct().collect(Collectors.toList());
//            if(CollectionUtils.isNotEmpty(settlementNoList)) {
//                wrapper.in(TXfBillDeductEntity.REF_SETTLEMENT_NO, settlementNoList);
//            }else{
//                wrapper.in(TXfBillDeductEntity.REF_SETTLEMENT_NO, "-");
//            }
//        }
        // 红字编号查询条件处理
        if (StringUtils.isNotBlank(request.getRedNotificationNo())) {
            QueryWrapper<TXfPreInvoiceEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq(TXfPreInvoiceEntity.RED_NOTIFICATION_NO, request.getRedNotificationNo());
            queryWrapper1.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
                    TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));
            List<TXfPreInvoiceEntity> list1 = xfPreInvoiceDao.selectList(queryWrapper1);
            List<Long> preInvoiceIds = list1.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(preInvoiceIds)) {
                return PageResult.of(Collections.emptyList(), 0, 0, request.getSize());
            }
            List<TXfDeductPreInvoiceEntity> billRefByPreInvoices = billDeductQueryExtDao.getBillRefByPreInvoiceIds(preInvoiceIds);
            List<Long> billIds = billRefByPreInvoices.stream().map(TXfDeductPreInvoiceEntity::getDeductId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(billIds)) {
                return PageResult.of(Collections.emptyList(), 0, 0, request.getSize());
            }
            wrapper.in(TXfBillDeductEntity.ID, billIds);
        }
        Page<TXfBillDeductEntity> page = new Page<>(request.getPage(), request.getSize());
        page.addOrder(OrderItem.asc(TXfBillDeductEntity.STATUS),OrderItem.desc(TXfBillDeductEntity.ID));
        Page<BillDeductLeftSettlementVo> pageResult = getBaseMapper().selectJoinSettlement(page, wrapper);
        final List<DeductListResponse> responses = new ArrayList<>();

        Map<String, Set<String>> deductSettlementNo2RedNotificationNoMap = new HashMap<>();
        Map<String, Integer> deductSettlementNo2StatusMap = new HashMap<>();
        List<String> settlementNoList = pageResult.getRecords().stream().map(BillDeductLeftSettlementVo::getRefSettlementNo).distinct().collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(settlementNoList)) {
        	ListUtils.partition(settlementNoList, 2000).forEach(itemList -> {
        		//查询待开和待上传发票预制发票的红字信息
        		QueryWrapper<TXfPreInvoiceEntity> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.in(TXfPreInvoiceEntity.SETTLEMENT_NO, itemList);
                queryWrapper2.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(), TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));
                List<TXfPreInvoiceEntity> list2 = xfPreInvoiceDao.selectList(queryWrapper2);
                Map<String, Set<String>> temp = list2.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getSettlementNo, Collectors.mapping(TXfPreInvoiceEntity::getRedNotificationNo, Collectors.toSet())));
        		deductSettlementNo2RedNotificationNoMap.putAll(temp);
        		
        		//查询结算单的状态
                QueryWrapper<TXfSettlementEntity> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.in(TXfSettlementEntity.SETTLEMENT_NO, itemList);
                List<TXfSettlementEntity> list3 = xfSettlementDao.selectList(queryWrapper3);
                list3.forEach(settlement->{
                    deductSettlementNo2StatusMap.put(settlement.getSettlementNo(), settlement.getSettlementStatus());
                });
        	});
        }

        if (CollectionUtils.isNotEmpty(pageResult.getRecords())) {
            Map<String, Set<String>> finalDeductSettlementNo2RedNotificationNoMap = deductSettlementNo2RedNotificationNoMap;
            final List<DeductListResponse> list = pageResult.getRecords().stream().map(x -> {
                final DeductListResponse deductListResponse = deductMapper.toResponse(x);
                deductListResponse.setOverdue(checkOverdue(typeEnum, x.getSellerNo(), x.getCreateTime()) ? 1 : 0);
                if (Objects.equals(deductListResponse.getLock(), TXfDeductStatusEnum.LOCK.getCode())){
                    deductListResponse.setRefSettlementNo(null);
                }

                if (Objects.equals(deductListResponse.getStatus(),TXfDeductStatusEnum.AGREEMENT_DESTROY.getCode())
                		|| Objects.equals(deductListResponse.getStatus(),TXfDeductStatusEnum.EPD_DESTROY.getCode()) ){
                    deductListResponse.setRefSettlementNo(null);
                }
                //结算单待拆票不显示结算单号
                if(Objects.equals(deductSettlementNo2StatusMap.get(x.getRefSettlementNo()), TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode())){
                    deductListResponse.setRefSettlementNo(null);
                }
                //填充红字号
//                Set<String> redNotificationNoList = finalDeductSettlementNo2RedNotificationNoMap.get(x.getRefSettlementNo());
//                if(CollectionUtils.isNotEmpty(redNotificationNoList)){
//                    deductListResponse.setRedNotificationNo(Joiner.on(",").join(redNotificationNoList));
//                }
                return deductListResponse;
            }).collect(Collectors.toList());
            responses.addAll(list);
        }

        // 红字编号列表填充
        billRefQueryService.fullAgreementBillRedNotification(responses);

        return PageResult.of(responses, pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    public BigDecimal sumDueAndNegative(DeductListRequest request, TXfDeductionBusinessTypeEnum typeEnum){
        return sumDueAndNegative(request.getPurchaserNo(),request.getSellerNo(),typeEnum,request.getTaxRate(),request.getAgreementTaxCode());
    }

    public BigDecimal sumDueAndNegative(String purchaserNo, String sellerNo, TXfDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate,String taxCode){
        final TXfBillDeductEntity deductEntity = getSumDueAndNegativeBill(purchaserNo, sellerNo, typeEnum, taxRate,taxCode);
        return Optional.ofNullable(deductEntity).map(TXfBillDeductEntity::getAmountWithoutTax).orElse(BigDecimal.ZERO);
    }

    public TXfBillDeductEntity getSumDueAndNegativeBill(String purchaserNo, String sellerNo, TXfDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate,String taxCode){
        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapperOverDueNegativeBills(purchaserNo, sellerNo, typeEnum, taxRate,taxCode);

        wrapper.groupBy(TXfBillDeductEntity.PURCHASER_NO,TXfBillDeductEntity.SELLER_NO,TXfBillDeductEntity.TAX_RATE);

        wrapper.select("sum(amount_without_tax) as amount_without_tax,sum(amount_with_tax) as amount_with_tax,sum(tax_amount) as tax_amount ,sum(amount_with_tax) as amount_with_tax,seller_no,purchaser_no, tax_rate");

        return this.getBaseMapper().selectOne(wrapper);
    }


    public List<TXfBillDeductEntity> getOverDueNegativeBills(String purchaserNo, String sellerNo, TXfDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate,String taxCode){
        final QueryWrapper<TXfBillDeductEntity> wrapper = wrapperOverDueNegativeBills(purchaserNo, sellerNo, typeEnum, taxRate,taxCode);
//        wrapper.select(TXfBillDeductEntity.ID);
        return this.getBaseMapper().selectList(wrapper);
    }

    private QueryWrapper<TXfBillDeductEntity> wrapperOverDueNegativeBills(String purchaserNo, String sellerNo, TXfDeductionBusinessTypeEnum typeEnum, BigDecimal taxRate,String taxCode){
        DeductListRequest sumRequest=new DeductListRequest();
        sumRequest.setOverdue(null);
        sumRequest.setSellerNo(sellerNo);
        sumRequest.setPurchaserNo(purchaserNo);
        sumRequest.setTaxRate(taxRate);
        sumRequest.setAgreementTaxCode(taxCode);
        switch (typeEnum){
            case CLAIM_BILL:
                sumRequest.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
                break;
            case EPD_BILL:
                sumRequest.setStatus(TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
                break;
            case AGREEMENT_BILL:
                sumRequest.setStatus(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
                break;
        }

        sumRequest.setLockFlag(TXfDeductStatusEnum.UNLOCK.getCode());

        return doWrapper(sumRequest, typeEnum, x->{
            overDueWrapper(sellerNo,typeEnum,1,x);
            x.or(s->s.lt(TXfBillDeductEntity.AMOUNT_WITHOUT_TAX,BigDecimal.ZERO));
        });
    }

    
    
    public boolean export(DeductListRequest request) {
        final Long userId = UserUtil.getUserId();
        TXfDeductionBusinessTypeEnum typeEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class, request.getBusinessType()).get();
        DeductViewBillExportDto dto = new DeductViewBillExportDto();
        dto.setType(typeEnum);
        dto.setRequest(request);
        dto.setUserId(userId);
        dto.setLoginName(UserUtil.getLoginName());
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
        excelExportlogEntity.setUserName(dto.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);
        boolean count = this.excelExportLogService.save(excelExportlogEntity);
        dto.setLogId(excelExportlogEntity.getId());
        ExportDeductViewCallable callable = new ExportDeductViewCallable(this, dto, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
        ThreadPoolManager.submitCustomL1(callable);
        return count;
    }

	public boolean doExport(DeductViewBillExportDto exportDto) {
		boolean flag = true;
		DeductListRequest request = exportDto.getRequest();
		TXfDeductionBusinessTypeEnum typeEnum = exportDto.getType();
		// 这里的userAccount是userid
		final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
		excelExportlogEntity.setEndDate(new Date());
		excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
		TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
		// 这里的userAccount是userName
		messagecontrolEntity.setUserAccount(exportDto.getLoginName());
		messagecontrolEntity.setContent(getSuccContent());
		// 主信息
		List<DeductListResponse> queryDeductListResponse = deductByPage(request, typeEnum).getRows();
		log.info("doExport queryDeductListResponse:{}", JSON.toJSONString(queryDeductListResponse));
		if (CollectionUtils.isEmpty(queryDeductListResponse)) {
			log.info("业务单导出--未查到数据");
			return false;
		}
		final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), exportDto.getType().getDes());
		ExcelWriter excelWriter;
		ByteArrayInputStream in = null;
		String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			excelWriter = EasyExcel.write(out).excelType(ExcelTypeEnum.XLSX).build();
			WriteSheet writeSheet = EasyExcel.writerSheet(0, "业务单信息").build();
			// 创建一个sheet
            if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)){
            	List<VendorExportClaimBillModel> exportList = new LinkedList<>();
            	queryDeductListResponse.forEach(item->{
            		VendorExportClaimBillModel billModel = new VendorExportClaimBillModel();
            		BeanUtil.copyProperties(item, billModel);
            		//业务单状态
					billModel.setStatusStr(TXfDeductStatusEnum.getEnumByCode(item.getStatus()).getDesc());
					billModel.setBusinessNo(item.getBillNo());
					billModel.setRefSettlementNo(item.getRefSettlementNo());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
                writeSheet.setClazz(VendorExportClaimBillModel.class);
                excelWriter.write(exportList, writeSheet);
            }else if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum)){
            	List<VendorExportAgreementBillModel> exportList = new LinkedList<>();
				queryDeductListResponse.forEach(item->{
					VendorExportAgreementBillModel billModel = new VendorExportAgreementBillModel();
            		BeanUtil.copyProperties(item, billModel);
					billModel.setBusinessNo(item.getBillNo());
					//业务单状态
					billModel.setStatusStr(TXfDeductStatusEnum.getEnumByCode(item.getStatus()).getDesc());
					// 0 未超期，1超期
					billModel.setOverdueStr(item.getOverdue() == 0 ? "未超期" : "超期");
					//1 锁定，0未锁定
					billModel.setLockStr(item.getLock() == 1 ? "锁定" : "未锁定");
					billModel.setRefSettlementNo(item.getRefSettlementNo());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
				log.info("TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum):{}", JSON.toJSONString(exportList));
                writeSheet.setClazz(VendorExportAgreementBillModel.class);
                excelWriter.write(exportList, writeSheet);
            }else{
            	List<VendorExportEPDBillModel> exportList = new LinkedList<>();
				queryDeductListResponse.forEach(item->{
					VendorExportEPDBillModel billModel = new VendorExportEPDBillModel();
            		BeanUtil.copyProperties(item, billModel);
					billModel.setBusinessNo(item.getBillNo());
					//业务单状态
					billModel.setStatusStr(TXfDeductStatusEnum.getEnumByCode(item.getStatus()).getDesc());
					// 0 未超期，1超期
					billModel.setOverdueStr(item.getOverdue() == 0 ? "未超期" : "超期");
					//1 锁定，0未锁定
					billModel.setLockStr(item.getLock() == 1 ? "锁定" : "未锁定");
					billModel.setRefSettlementNo(item.getRefSettlementNo());
					billModel.setNum(exportList.size() + 1);
            		exportList.add(billModel);
            	});
                writeSheet.setClazz(VendorExportEPDBillModel.class);
                excelWriter.write(exportList, writeSheet);
            }
			excelWriter.finish();
			// 推送sftp
			String ftpFilePath = ftpPath + "/" + excelFileName;
			in = new ByteArrayInputStream(out.toByteArray());
			ftpUtilService.uploadFile(ftpPath, excelFileName, in);
			messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
			excelExportlogEntity.setFilepath(ftpFilePath);
			messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(exportDto.getType().getDes() + "导出失败:" + e.getMessage(), e);
			excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
			excelExportlogEntity.setErrmsg(e.getMessage());
			messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出失败");
			messagecontrolEntity.setContent(exportCommonService.getFailContent(e.getMessage()));
			flag = false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			excelExportLogService.updateById(excelExportlogEntity);
			commonMessageService.sendMessage(messagecontrolEntity);
		}
		return flag;
	}
    
    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }
    
    /**
     * 单据类型，仅支持协议单和EPD，索赔单默认返回false
     *
     * @param typeEnum 单据类型，仅支持协议单和EPD，索赔单默认返回false
     * @param sellerNo 销方编号
     * @param deductDate 扣款日期
     * @return
     */
    public boolean checkOverdue(TXfDeductionBusinessTypeEnum typeEnum, String sellerNo, Date deductDate) {
        if (typeEnum == TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
            return false;
        }
        final Date date = getOverdueDate(typeEnum,sellerNo);
        return date.after(deductDate);
    }


    /**
     * 获取超期日期，精确到天
     *
     * @param typeEnum 单据类型
     * @param sellerNo 销方编号
     * @return Date
     */
    public Date getOverdueDate(TXfDeductionBusinessTypeEnum typeEnum, String sellerNo){
        final int overdue = getOverdue(typeEnum, sellerNo);
        final DateTime dateTime = DateUtil.offsetDay(new Date(), -overdue + 1);
        return dateTime.setField(DateField.HOUR, 0)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0)
                .toJdkDate();
    }


    private int getOverdue(TXfDeductionBusinessTypeEnum typeEnum, String sellerNo) {
        ServiceTypeEnum serviceTypeEnum = null;
        switch (typeEnum) {
            case CLAIM_BILL:
                serviceTypeEnum = ServiceTypeEnum.CLAIM;
                break;
            case AGREEMENT_BILL:
                serviceTypeEnum = ServiceTypeEnum.AGREEMENT;
                break;
            case EPD_BILL:
                serviceTypeEnum = ServiceTypeEnum.EPD;
                break;
            default:
                throw new EnhanceRuntimeException("业务单据类型有误:" + typeEnum.getDes());
        }

        final Integer overdue = overdueService.oneOptBySellerNo(serviceTypeEnum, sellerNo);
        return overdue;
    }


    private List<SummaryResponse> toSummary(List<Map<String, Object>> objs) {

        Map<BigDecimal,SummaryResponse> summaries=new HashMap<>();
        for (BigDecimal taxRate : taxRates) {
            final SummaryResponse summaryResponse = new SummaryResponse(0, taxRate);
            summaries.put(taxRate,summaryResponse);
        }
        for (Map<String, Object> obj : objs) {
            final Object taxRate = obj.get("taxRate");
            final Object count = obj.get("count");
             SummaryResponse summaryResponse = null;
            if (taxRate instanceof BigDecimal){
                summaryResponse=summaries.get(((BigDecimal) taxRate).setScale(4));
            }else {
                summaryResponse=summaries.get(new BigDecimal(taxRate.toString()).setScale(4));
            }
            Optional.ofNullable(summaryResponse).ifPresent(x->x.setCount((Integer) count));
        }

        List<SummaryResponse> summaryResponses = new ArrayList<>(summaries.values());

        summaryResponses.sort(Comparator.comparing(SummaryResponse::getTaxRate));
        final SummaryResponse summaryResponse = new SummaryResponse();
        summaryResponse.setAll(true);
        summaryResponse.setTaxRate("-1");
        summaryResponse.setTaxRateText("全部");
        summaryResponse.setCount(summaryResponses.stream().map(SummaryResponse::getCount).reduce(0, Integer::sum));
        summaryResponses.add(summaryResponse);
        return summaryResponses;
    }


    private QueryWrapper<TXfBillDeductEntity> wrapper(DeductListRequest request, TXfDeductionBusinessTypeEnum typeEnum) {
        return doWrapper(request,typeEnum,x->{
//            if (typeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL){
//                // 小于0的不展示
//                x.gt(TXfBillDeductEntity.AMOUNT_WITHOUT_TAX,BigDecimal.ZERO);
//            }else {
//                x.eq("1",1);
//            }
            x.eq("1",1);
        });
    }

    /**
     * 封装查询wrapper
     * @param request 参数
     * @param typeEnum 单据类型
     * @param and 额外的and拼接
     * @return
     */
    private QueryWrapper<TXfBillDeductEntity> doWrapper(DeductListRequest request, TXfDeductionBusinessTypeEnum typeEnum, Consumer<QueryWrapper<TXfBillDeductEntity>> and) {
        QueryWrapper<TXfBillDeductEntity> wrapper = Wrappers.query(new TXfBillDeductEntity());
        wrapper.lambda().eq(StringUtils.isNotBlank(request.getBillNo()), TXfBillDeductEntity::getBusinessNo, request.getBillNo());
        wrapper.lambda().eq(StringUtils.isNotBlank(request.getPurchaserNo()), TXfBillDeductEntity::getPurchaserNo, request.getPurchaserNo());
        wrapper.lambda().eq(request.getTaxRate() != null &&
                request.getTaxRate().compareTo(new BigDecimal(-1)) != 0, TXfBillDeductEntity::getTaxRate, request.getTaxRate());

        wrapper.lambda().eq(Objects.nonNull(request.getStatus()), TXfBillDeductEntity::getStatus, request.getStatus());

        wrapper.lambda().eq(StringUtils.isNotBlank(request.getDeductInvoice()), TXfBillDeductEntity::getDeductInvoice, request.getDeductInvoice());

        wrapper.lambda().eq(StringUtils.isNotBlank(request.getSellerNo()), TXfBillDeductEntity::getSellerNo, request.getSellerNo());

        wrapper.lambda().eq(Objects.nonNull(request.getLockFlag()), TXfBillDeductEntity::getLockFlag, request.getLockFlag());

        wrapper.lambda().eq(StringUtils.isNotBlank(request.getAgreementTaxCode()), TXfBillDeductEntity::getAgreementTaxCode, request.getAgreementTaxCode());

        wrapper.lambda().eq(StringUtils.isNotBlank(request.getAgreementReasonCode()), TXfBillDeductEntity::getAgreementReasonCode, request.getAgreementReasonCode());

        wrapper.lambda().eq(StringUtils.isNotBlank(request.getRefSettlementNo()), TXfBillDeductEntity::getRefSettlementNo, request.getRefSettlementNo());


        //扣款日期>>Begin
        wrapper.ge(StringUtils.isNotBlank(request.getDeductDateBegin()), TXfBillDeductEntity.DEDUCT_DATE, request.getDeductDateBegin());

        //扣款日期>>End
        String deductDateEnd = request.getDeductDateEnd();
        if (StringUtils.isNotBlank(deductDateEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(deductDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity.DEDUCT_DATE, format);
        }
        // ===============================
        //定案、入账日期 >> begin
        final String verdictDateBegin = request.getVerdictDateBegin();
        if (StringUtils.isNotBlank(verdictDateBegin)) {
            wrapper.ge(TXfBillDeductEntity.VERDICT_DATE, verdictDateBegin);
        }
        //定案、入账日期 >> end
        String verdictDateEnd = request.getVerdictDateEnd();
        if (StringUtils.isNotBlank(verdictDateEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(verdictDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity.VERDICT_DATE, format);
        }

        // ===============================
        //创建时间、入库日期 >> begin
        final String createTimeBegin = request.getCreateTimeBegin();
        if (StringUtils.isNotBlank(createTimeBegin)) {
            wrapper.ge(TXfBillDeductEntity.CREATE_TIME, createTimeBegin);
        }

        //创建时间、入库 >> end
        String createTimeEnd = request.getCreateTimeEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            wrapper.lt(TXfBillDeductEntity.CREATE_TIME, format);
        }

        wrapper.eq(TXfBillDeductEntity.BUSINESS_TYPE, typeEnum.getValue());

        if (typeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
            //协议单和EPD才有超期配置

            //超期判断
            if (request.getOverdue() != null) {
                overDueWrapper(request.getSellerNo(),typeEnum,request.getOverdue(),wrapper);
            }
        } else {
            // 索赔单只展示 生成结算单之后的数据
            wrapper.in(TXfBillDeductEntity.STATUS,
                    TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode()
                    , TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode()
                    , TXfDeductStatusEnum.CLAIM_DESTROY.getCode()
            );
        }
        if (and!=null) {
            wrapper.and(and);
        }
        return wrapper;
    }

    private void overDueWrapper(String sellerNo, TXfDeductionBusinessTypeEnum typeEnum, Integer overDue, QueryWrapper<TXfBillDeductEntity> wrapper){
        final int overdue = getOverdue(typeEnum, sellerNo);

        final DateTime dateTime = DateUtil.offsetDay(new Date(), -overdue + 1);
        final Date date = dateTime.setField(DateField.HOUR, 0)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0)
                .toJdkDate();
        switch (overDue) {
            case 1:
                wrapper.lt(TXfBillDeductEntity.CREATE_TIME, date);
                break;
            case 0:
                wrapper.gt(TXfBillDeductEntity.CREATE_TIME, date);
                break;
        }
    }


    /**
     * @param request 列表单参数
     * @return
     */
    public PageResult<DeductListResponse> deductClaimByPage(DeductListRequest request) {
        final PageResult<DeductListResponse> result = deductByPage(request, TXfDeductionBusinessTypeEnum.CLAIM_BILL);
        final List<DeductListResponse> responses = result.getRows();

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(responses)) {
            final List<String> settlementNos = responses.stream().map(DeductListResponse::getRefSettlementNo).distinct().collect(Collectors.toList());
            //2022-08-02,统计发票的数量
            final Map<String, Integer> invoiceCount = getInvoiceCountBySettlement(settlementNos);
            //final Map<String, Integer> settlementStatus = this.settlementService.getSettlementStatus(settlementNos);
            responses.forEach(x -> {
                x.setInvoiceCount(Optional.ofNullable(invoiceCount.get(x.getRefSettlementNo())).orElse(0));
                //x.setSettlementStatus(Optional.ofNullable(settlementStatus.get(x.getRefSettlementNo())).orElse(null));
            });
        }
        return result;
    }

    private Map<String, Integer> getInvoiceCountBySettlement(List<String> settlementNos) {

        if (CollectionUtils.isEmpty(settlementNos)) {
            return Collections.emptyMap();
        }
        Map<String, Integer> result = new HashMap<>();
        
        //2022-08-02 list切割处理防止参数大于2000
        ListUtils.partition(settlementNos, 2000).forEach(itemList -> {
        	final QueryWrapper<TDxRecordInvoiceEntity> wrapper = Wrappers.<TDxRecordInvoiceEntity>query().select(TDxRecordInvoiceEntity.SETTLEMENT_NO, "count(1) as count ")
                    .in(TDxRecordInvoiceEntity.SETTLEMENT_NO, itemList).groupBy(TDxRecordInvoiceEntity.SETTLEMENT_NO);
            final List<Map<String, Object>> maps = tDxRecordInvoiceDao.selectMaps(wrapper);
            if (CollectionUtils.isNotEmpty(maps)) {
                maps.forEach(x -> {
                    final Object settlement = x.get(TDxRecordInvoiceEntity.SETTLEMENT_NO);
                    final Integer count = (Integer) x.get("count");
                    result.put(settlement.toString(), count);
                });
            }
        });
        return result;
    }


/*    @Transactional
    public TXfSettlementEntity makeSettlement(MakeSettlementRequest request, TXfDeductionBusinessTypeEnum type) {
        if (CollectionUtils.isEmpty(request.getInvoiceIds())) {
            throw new EnhanceRuntimeException("请至少选择一张业务单据");
        }
        final BigDecimal amount = checkAndGetTotalAmount(request, type);

        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.obtainInvoiceByIds(amount, request.getInvoiceIds());
        final List<TXfBillDeductEntity> bills = getOverDueNegativeBills(request.getPurchaserNo(), request.getSellerNo(), type, request.getTaxRate());
        final List<Long> collect = bills.parallelStream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
        List<Long> ids=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(collect)){
            ids.addAll(collect);
        }
        if (CollectionUtils.isNotEmpty(request.getBillIds())){
            ids.addAll(request.getBillIds());
        }
        return agreementBillService.mergeSettlementByManual(ids,type,matchRes);
    }*/
/*
    public List<MatchedInvoiceListResponse> getMatchedInvoice(PreMakeSettlementRequest request, TXfDeductionBusinessTypeEnum typeEnum){


        final BigDecimal amount = checkAndGetTotalAmount(request, typeEnum);

        final TAcOrgEntity purchaserOrg = deductService.queryOrgInfo(request.getPurchaserNo(), false);
        if (Objects.isNull(purchaserOrg)){
            throw new EnhanceRuntimeException("扣款公司代码:["+ request.getPurchaserNo()+"]不存在");
        }
        final TAcOrgEntity sellerOrg = deductService.queryOrgInfo(request.getSellerNo(), true);

        if (Objects.isNull(sellerOrg)){
            throw new EnhanceRuntimeException("供应商编号:["+ request.getSellerNo()+"]不存在");
        }
        BigDecimal taxRate = request.getTaxRate().movePointRight(2);

        taxRate=AgreementBillService.convertTaxRate(typeEnum,taxRate);

        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.obtainAvailableInvoicesWithoutItems(amount, null,
                sellerOrg.getTaxNo(), purchaserOrg.getTaxNo(),taxRate , false, false, typeEnum);

        final List<MatchedInvoiceListResponse> responses = this.matchedInvoiceMapper.toMatchInvoice(matchRes);
        if (CollectionUtils.isNotEmpty(responses)){
            for (MatchedInvoiceListResponse respons : responses) {
                final List<TDxRecordInvoiceDetailEntity> details = recordInvoiceDetailExtDao.selectTopGoodsName(5, respons.getInvoiceCode() + respons.getInvoiceNo());
                final String goodsName = details.stream().map(TDxRecordInvoiceDetailEntity::getGoodsName).collect(Collectors.joining(","));
                respons.setGoodsName(goodsName);
            }
        }
        return responses;
    }*/
/*
    public BigDecimal checkAndGetTotalAmount(PreMakeSettlementRequest request, TXfDeductionBusinessTypeEnum typeEnum){
        final List<Long> billId = request.getBillIds();
        BigDecimal amount=BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(billId)) {
            final QueryWrapper<TXfBillDeductEntity> wrapper = Wrappers.query();
            wrapper.select(
                    "sum(amount_without_tax) amount_without_tax"
                    ,TXfBillDeductEntity.TAX_RATE
                    ,TXfBillDeductEntity.SELLER_NO
                    ,TXfBillDeductEntity.PURCHASER_NO
            );
            TXfDeductStatusEnum statusEnum;
            switch (typeEnum){
                case AGREEMENT_BILL:
                    statusEnum= TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT;
                    break;
                case EPD_BILL:
                    statusEnum= TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT;
                    break;
                default:throw new EnhanceRuntimeException("手动合并结算单仅支持协议单和EPD");
            }
            wrapper.in(TXfBillDeductEntity.ID, billId)
                    .eq(TXfBillDeductEntity.BUSINESS_TYPE,typeEnum.getValue())
                    .eq(TXfBillDeductEntity.STATUS,statusEnum.getCode())
                    .eq(TXfBillDeductEntity.LOCK_FLAG, TXfDeductStatusEnum.UNLOCK.getCode())
                    .groupBy(TXfBillDeductEntity.SELLER_NO,TXfBillDeductEntity.PURCHASER_NO,TXfBillDeductEntity.TAX_RATE)
            ;
            final List<TXfBillDeductEntity> entities = getBaseMapper().selectList(wrapper);
            if (CollectionUtils.isNotEmpty(entities)) {
                if (entities.size() > 1) {
                    throw new EnhanceRuntimeException("您选择了存在多税率或者多购销方的单据，不能完成此操作");
                }
            } else {
                throw new EnhanceRuntimeException("您选择的单据不存在，或已被使用/锁定，请刷新重试");
            }
            TXfBillDeductEntity entity  = entities.get(0);
            if (!Objects.equals(entity.getPurchaserNo(),request.getPurchaserNo())){
                throw new EnhanceRuntimeException("单据扣款公司代码与参数不一致");
            }

            if (entity.getTaxRate().compareTo(request.getTaxRate())!=0){
                throw new EnhanceRuntimeException("单据税率与参数不一致");
            }

            amount=amount.add(entity.getAmountWithoutTax());
        }

        final BigDecimal decimal = sumDueAndNegative(request.getPurchaserNo(), request.getSellerNo(), typeEnum,request.getTaxRate());
        log.info("amount:{},decimal:{}",amount, decimal);
        amount = amount.add(decimal);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EnhanceRuntimeException("负数和超期单据总额小于0，生成结算单的单据总额必须大于0，请返回重新选择正数单据");
        }
        return amount;
    }*/
}
