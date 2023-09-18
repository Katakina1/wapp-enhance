package com.xforceplus.wapp.modules.backfill.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.invoice.InvoiceAuthStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.audit.enums.AuditStatusEnum;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.modules.backfill.mapstruct.RecordInvoiceMapper;
import com.xforceplus.wapp.modules.backfill.model.HostInvoiceModel;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backfill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.util.BeanUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;
import static com.xforceplus.wapp.modules.sys.util.UserUtil.getUserId;

/**
 * Created by SunShiyong on 2021/10/16.
 * 底账数据服务
 */
@Service
@Slf4j
public class RecordInvoiceService extends ServiceImpl<TDxRecordInvoiceDao, TDxRecordInvoiceEntity> {
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;

    @Autowired
    private TDxRecordInvoiceDetailDao recordInvoiceDetailsDao;

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;

    @Autowired
    private TXfSettlementDao tXfSettlementDao;

    @Autowired
    private TXfBlueRelationDao tXfBlueRelationDao;
    @Autowired
    private InvoiceAuditService invoiceAuditService;
    @Autowired
    private OperateLogService operateLogService;
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private RecordInvoiceMapper recordInvoiceMapper;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Value("${wapp.export.tmp}")
    private String tmp;

    /**
     * 正式发票列表
     *
     * @param
     * @return PageResult
     */
    public PageResult<RecordInvoiceResponse> queryPageList(long pageNo, long pageSize, String settlementNo, String invoiceColor, String invoiceStatus, String venderid) {
        Page<TDxRecordInvoiceEntity> page = new Page<>(pageNo, pageSize);
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus, venderid, invoiceColor, true);
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page, wrapper);
        List<RecordInvoiceResponse> response = new ArrayList<>();
        RecordInvoiceResponse recordInvoiceResponse = null;
        //添加换票审核状态字段
        Map<String, InvoiceAudit> uidAndAuditStatusMap = Maps.newHashMap();
        if ("0".equalsIgnoreCase(invoiceColor)
                && InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode().equalsIgnoreCase(invoiceStatus)
                && CollectionUtils.isNotEmpty(pageResult.getRecords())) {
            Set<String> uuids = pageResult.getRecords().stream().map(TDxRecordInvoiceEntity::getUuid).collect(Collectors.toSet());
            List<InvoiceAudit> search = invoiceAuditService.search(uuids);
            uidAndAuditStatusMap = search.stream().collect(Collectors.toMap(InvoiceAudit::getInvoiceUuid, Function.identity()));
        }
        for (TDxRecordInvoiceEntity recordInvoice : pageResult.getRecords()) {
            recordInvoiceResponse = new RecordInvoiceResponse();
            BeanUtil.copyProperties(recordInvoice, recordInvoiceResponse);
            if (recordInvoice.getTaxRate() != null) {
                BigDecimal taxRate = recordInvoice.getTaxRate().divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
                recordInvoiceResponse.setTaxRate(taxRate.toPlainString());
                recordInvoiceResponse.setRedNotificationNo(recordInvoice.getRedNoticeNumber());
            }
            InvoiceAudit orDefault = uidAndAuditStatusMap.getOrDefault(recordInvoice.getUuid(), new InvoiceAudit());
            // 发票显示状态处理
            recordInvoiceResponse.setInvoiceShowStatus(getInvoiceShowStatus(recordInvoiceResponse, orDefault).getCode());
            if (BigDecimal.ZERO.compareTo(recordInvoice.getTotalAmount()) < 0) {
                // 蓝票审核记录无需返回
                orDefault = new InvoiceAudit();
            }
            recordInvoiceResponse.setAuditStatus(orDefault.getAuditStatus());
            recordInvoiceResponse.setAuditRemark(orDefault.getAuditRemark());
            recordInvoiceResponse.setAuditSubmitRemark(orDefault.getRemark());
            recordInvoiceResponse.setAuditSubmitTime(orDefault.getCreateTime());
            recordInvoiceResponse.setAuditTime(AuditStatusEnum.NOT_AUDIT.getValue().equals(orDefault.getAuditStatus()) ? null : orDefault.getUpdateTime());
            response.add(recordInvoiceResponse);
        }
        return PageResult.of(response, pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    /**
     * 结算单正式发票列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param settlementNo
     * @param venderid
     * @return
     */
    public PageResult<RecordInvoiceResponse> queryPageRecordInvoiceList(long pageNo, long pageSize, String settlementNo, String venderid) {
        Page<TDxRecordInvoiceEntity> page = new Page<>(pageNo, pageSize);
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(TDxRecordInvoiceEntity.ID);
        wrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue());
        wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENT_NO, settlementNo);
        // 正常、蓝冲
        wrapper.in(TDxRecordInvoiceEntity.INVOICE_STATUS, Arrays.asList(InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode(),
                InvoiceStatusEnum.INVOICE_STATUS_SEND_BLUE.getCode(), InvoiceStatusEnum.ANTU_STATUS_EXCEPTION.getCode()));
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page, wrapper);
        List<RecordInvoiceResponse> responseList = new ArrayList<>();

        Set<String> uuids = pageResult.getRecords().stream().map(TDxRecordInvoiceEntity::getUuid).collect(Collectors.toSet());
        List<InvoiceAudit> search = CollectionUtils.isEmpty(uuids) ? Lists.newArrayList() : invoiceAuditService.search(uuids);
        Map<String, InvoiceAudit> uidAndAuditStatusMap = search.stream().collect(Collectors.toMap(InvoiceAudit::getInvoiceUuid, Function.identity(),
                (v1, v2) -> {
                    if (v2.getCreateTime().compareTo(v1.getCreateTime()) > 0) {
                        return v2;
                    }
                    return v1;
                }));

        for (TDxRecordInvoiceEntity record : pageResult.getRecords()) {
            RecordInvoiceResponse recordInvoice = BeanUtils.copyProperties(record, RecordInvoiceResponse.class);
            recordInvoice.setRedNotificationNo(record.getRedNoticeNumber());
            // 审核数据处理
            InvoiceAudit orDefault = uidAndAuditStatusMap.getOrDefault(recordInvoice.getUuid(), new InvoiceAudit());
            // 发票显示状态处理
            recordInvoice.setInvoiceShowStatus(getInvoiceShowStatus(recordInvoice, orDefault).getCode());
            if (BigDecimal.ZERO.compareTo(recordInvoice.getTotalAmount()) < 0) {
                // 蓝票审核记录无需返回
                orDefault = new InvoiceAudit();
            }
            recordInvoice.setAuditStatus(orDefault.getAuditStatus());
            recordInvoice.setAuditRemark(orDefault.getAuditRemark());
            recordInvoice.setAuditSubmitRemark(orDefault.getRemark());
            recordInvoice.setAuditSubmitTime(orDefault.getCreateTime());
            // 表中更新时间会有默认值，但是在待审核状态时，不应该展示审核时间
            recordInvoice.setAuditTime(AuditStatusEnum.NOT_AUDIT.getValue().equals(orDefault.getAuditStatus()) ? null : orDefault.getUpdateTime());
            responseList.add(recordInvoice);
        }


        return PageResult.of(responseList, pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    /**
     * 发票显示状态 0-正常红票;1-正常蓝票;2-蓝冲待审核;3-红票待蓝冲;4-蓝票待审核;5-红票已蓝冲
     * <p>
     * 发票状态	t_dx_record_invoice（底账发票表）	t_xf_invoice_audit（发票审核表）
     * 正常红票	金额小于0，invoice_status=0	—
     * 正常蓝票	金额大于0，invoice_status=0	审核通过(audit_status=1)
     * 蓝冲待审核	正常红票	待审核(audit_status=0)
     * 红票待蓝冲	正常红票	审核通过
     * 蓝票待审核	正常蓝票	待审核
     * 红票已蓝冲	红票，invoice_status=5	审核通过
     */
    private InvoiceShowStatusEnum getInvoiceShowStatus(RecordInvoiceResponse recordInvoice, InvoiceAudit invoiceAudit) {

        // 异常发票
        if(InvoiceStatusEnum.ANTU_STATUS_EXCEPTION.getCode().equals(recordInvoice.getInvoiceStatus())){
            return InvoiceShowStatusEnum.ABNORMAL;
        }
        if(InvoiceStatusEnum.INVOICE_STATUS_SEND_BLUE.getCode().equals(recordInvoice.getInvoiceStatus())) {
            // 红票已蓝冲
            return InvoiceShowStatusEnum.RED_BLUE_OFFSET;
        }

        // 正常红票	金额小于0，invoice_status=0
        if (InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode().equals(recordInvoice.getInvoiceStatus()) && BigDecimal.ZERO.compareTo(recordInvoice.getTotalAmount()) == 1) {

            // 正常红票+待审核(audit_status=0)
            if (null != invoiceAudit.getId() && AuditStatusEnum.NOT_AUDIT.getValue().equals(invoiceAudit.getAuditStatus())) {
                // 蓝冲待审核
                return InvoiceShowStatusEnum.BLUE_OFFSET_AUTID;
            }
            // 正常红票+审核通过
            if (null != invoiceAudit.getId() && AuditStatusEnum.AUDIT_PASS.getValue().equals(invoiceAudit.getAuditStatus())) {
                // 红票待蓝冲
                return InvoiceShowStatusEnum.RED_WAIT_OFFSET;
            }
            // 正常红票
            return InvoiceShowStatusEnum.NORMAL_RED;
        }
        // 正常蓝票	金额大于0，invoice_status=0
        if (InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode().equals(recordInvoice.getInvoiceStatus()) && recordInvoice.getTotalAmount().compareTo(BigDecimal.ZERO) == 1) {
            // 正常蓝票+待审核
            if (null != invoiceAudit.getId() && AuditStatusEnum.NOT_AUDIT.getValue().equals(invoiceAudit.getAuditStatus())) {
                // 蓝票待审核
                return InvoiceShowStatusEnum.BLUE_AUTID;
            }
            return InvoiceShowStatusEnum.NORMAL_BLUE;
        }
        // 红票已蓝冲	红票，invoice_status=5
        if (null != invoiceAudit.getId() && AuditStatusEnum.AUDIT_PASS.getValue().equals(invoiceAudit.getAuditStatus())) {
            // 红票已蓝冲
            return InvoiceShowStatusEnum.RED_BLUE_OFFSET;
        }

        // 异常兜底
        return InvoiceShowStatusEnum.NORMAL_RED;


    }


    /**
     * 正式发票详情
     *
     * @param
     * @return InvoiceDetailResponse
     */
    public InvoiceDetailResponse getInvoiceById(Long id) {
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectById(id);
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        if (invoiceEntity != null) {
            List<InvoiceDetail> invoiceDetails = queryInvoiceDetailByUuid(invoiceEntity.getUuid());
            response.setItems(invoiceDetails);
            BeanUtil.copyProperties(invoiceEntity, response);
            this.convertMain(invoiceEntity, response);
        }
        return response;
    }

    public List<InvoiceDetail> queryInvoiceDetailByUuid(String uuid) {
        QueryWrapper<TDxRecordInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceDetailEntity.UUID, uuid);
        List<TDxRecordInvoiceDetailEntity> tDxRecordInvoiceDetailEntities = recordInvoiceDetailsDao.selectList(wrapper);
        List<InvoiceDetail> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tDxRecordInvoiceDetailEntities)) {
            InvoiceDetail invoiceDetail;
            for (TDxRecordInvoiceDetailEntity tDxRecordInvoiceDetailEntity : tDxRecordInvoiceDetailEntities) {
                invoiceDetail = new InvoiceDetail();
                this.convertItem(tDxRecordInvoiceDetailEntity, invoiceDetail);
                list.add(invoiceDetail);
            }
        }
        return list;
    }

    private static final String NEGATIVE_SYMBOL = "-";

    /**
     * 根据uuid获取该发票的所有正数明细
     * <p>
     * by Kenny Wong
     *
     * @param uuid
     * @return
     */
    public List<TDxRecordInvoiceDetailEntity> getInvoiceDetailByUuid(String uuid) {
        QueryWrapper<TDxRecordInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceDetailEntity.UUID, uuid);
        //TODO  负数的不能参与匹配，是否可以考虑使用大于 0 ？
        wrapper.ne(TDxRecordInvoiceDetailEntity.DETAIL_AMOUNT, "0");
        // by Kenny Wong 按照明细序号排序，保证每次返回的结果顺序一致
        wrapper.orderByAsc(TDxRecordInvoiceDetailEntity.DETAIL_NO);
        return Optional.ofNullable(recordInvoiceDetailsDao.selectList(wrapper))
                .orElse(Collections.emptyList())
                .stream()
                .filter(v -> !v.getDetailAmount().startsWith(NEGATIVE_SYMBOL))
                .collect(Collectors.toList());
    }


    public Integer getCountBySettlementNo(String settlementNo, String invoiceStatus, String venderid) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus, venderid, null, false);
        return tDxRecordInvoiceDao.selectCount(wrapper);
    }

    /**
     * 发票删除 - 新版
     *
     * @param invoiceId 发票id
     */
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteInvoiceV2(Long invoiceId) {
        // 1.查询发票信息
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectById(invoiceId);
        Asserts.isNull(invoiceEntity, "未找到发票信息");

        // true-红票 false-蓝票
        boolean isRed = invoiceEntity.getInvoiceAmount().compareTo(BigDecimal.ZERO) < 0;
        // 2.判断发票状态
        if (isRed) {
            // 2.2.已蓝冲红票无法删除
            Asserts.isTrue(InvoiceStatusEnum.INVOICE_STATUS_SEND_BLUE.getCode().equals(invoiceEntity.getInvoiceStatus()), "红票已蓝冲，不能删除");
            // 2.3.发票蓝冲审核中无法删除
            List<InvoiceAudit> invoiceAudits = invoiceAuditService.search(Sets.newHashSet(invoiceEntity.getUuid()));
            if (CollectionUtil.isNotEmpty(invoiceAudits)) {
                Asserts.isTrue(AuditStatusEnum.NOT_AUDIT.getValue().equals(invoiceAudits.get(0).getAuditStatus()), "已申请蓝冲，不能删除");
                Asserts.isTrue(AuditStatusEnum.AUDIT_PASS.getValue().equals(invoiceAudits.get(0).getAuditStatus()), "已申请蓝冲，请上传蓝票");
            }
            // 2.4.正常红字电票和跨月红字纸票无法删除 需要发起蓝冲操作  返回特定code 2
            if (InvoiceTypeEnum.isElectronic(invoiceEntity.getInvoiceType())) {
                return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵", R.INTERMEDIATE_STATE);
            } else if (!DateUtils.isCurrentMonth(invoiceEntity.getInvoiceDate())) {
                return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵", R.INTERMEDIATE_STATE);
            }
        } else {
            if (!InvoiceStatusEnum.ANTU_STATUS_EXCEPTION.getCode().equals(invoiceEntity.getInvoiceStatus())) {
                // 2.1.直接已认证蓝票无法删除
                Asserts.isTrue(InvoiceAuthStatusEnum.SUCCESS_AUTH.code().equals(invoiceEntity.getAuthStatus())
                        && InvoiceExchangeTypeEnum.ZJRZ.getCode().equalsIgnoreCase(invoiceEntity.getFlowType()), "发票购方已认证，不能删除");
                // 2.2.发票审核中无法删除
                List<InvoiceAudit> invoiceAudits = invoiceAuditService.search(Sets.newHashSet(invoiceEntity.getUuid()));
                if (CollectionUtil.isNotEmpty(invoiceAudits)) {
                    Asserts.isTrue(AuditStatusEnum.NOT_AUDIT.getValue().equals(invoiceAudits.get(0).getAuditStatus()), "发票购方审核中，不能删除");
                }
            }
        }

        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, invoiceEntity.getSettlementNo());
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
        // 原结算单状态
        Integer originalSettlementStatus = tXfSettlementEntity.getSettlementStatus();

        // 3.删除发票
        final Date now = new Date();
        String settlementNo = invoiceEntity.getSettlementNo();
        if (isRed) {
            // 3.1.红票删除  更新底账表状态 更新扫描表状态 预制发票置为待上传发票状态 修改结算单状态
            TDxRecordInvoiceEntity recordInvoiceEntityU = new TDxRecordInvoiceEntity();
            recordInvoiceEntityU.setId(invoiceEntity.getId());
            recordInvoiceEntityU.setIsDel(IsDealEnum.YES.getValue());
            recordInvoiceEntityU.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
            recordInvoiceEntityU.setSettlementNo("");
            recordInvoiceEntityU.setStatusUpdateDate(now);
            Asserts.isFalse(this.updateById(recordInvoiceEntityU), "删除失败，未找到发票");

            if (StringUtils.isNotBlank(invoiceEntity.getUuid())) {
                TDxInvoiceEntity dxInvoiceEntityU = new TDxInvoiceEntity();
                dxInvoiceEntityU.setIsdel(IsDealEnum.YES.getValue());
                dxInvoiceEntityU.setUpdateDate(now);
                dxInvoiceEntityU.setDelDate(now);
                dxInvoiceEntityU.setRefundReason("deleteInvoice delete");
                UpdateWrapper<TDxInvoiceEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(TDxInvoiceEntity.UUID, invoiceEntity.getUuid());
                int update = tDxInvoiceDao.update(dxInvoiceEntityU, updateWrapper);
                log.info("删除扫描数据:{}", update);
            }

            // 修改预制发票状态为待上传并制空字段
            UpdateWrapper<TXfPreInvoiceEntity> preWrapper = new UpdateWrapper<>();
            preWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE, invoiceEntity.getInvoiceCode());
            preWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO, invoiceEntity.getInvoiceNo());
            preWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, settlementNo);
            TXfPreInvoiceEntity preInvoiceEntity = tXfPreInvoiceDao.selectOne(preWrapper);
            Asserts.isNull(preInvoiceEntity, "删除失败，未找到对应预制发票");

            TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            tXfPreInvoiceEntity.setId(preInvoiceEntity.getId());
            tXfPreInvoiceEntity.setInvoiceCode("");
            tXfPreInvoiceEntity.setInvoiceNo("");
            tXfPreInvoiceEntity.setMachineCode("");
            tXfPreInvoiceEntity.setPaperDrewDate("");
            tXfPreInvoiceEntity.setCheckCode("");
            tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
            tXfPreInvoiceEntity.setUpdateTime(now);
            Asserts.isFalse(1 == tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity), "删除失败，未找到对应预制发票");
            commonMessageService.sendInvoiceDeleteMessage(Lists.newArrayList(preInvoiceEntity.getId()));

            // 修改结算单状态
            Asserts.isFalse(updateSettlement(settlementNo, originalSettlementStatus), "删除失败，未找到对应结算单");
        } else {
            // 3.2.蓝票删除 特殊逻辑 删除审核记录 释放红票
            TXfBlueRelationEntity blueRelation = blueInvoiceRelationService.getByBlueInfo(invoiceEntity.getInvoiceNo(), invoiceEntity.getInvoiceCode());
            Asserts.isNull(blueRelation, "不存在红蓝关系");

            List<TXfBlueRelationEntity> redBlueRelationList = blueInvoiceRelationService.getByRedInfo(blueRelation.getRedInvoiceNo(), blueRelation.getRedInvoiceCode());
            Asserts.isTrue(CollectionUtil.isEmpty(redBlueRelationList), "不存在红蓝关系");

            // 删除红蓝关联关系 红票下所有的一起删除
            blueInvoiceRelationService.deleteByRedInvoice(blueRelation.getRedInvoiceNo(), blueRelation.getRedInvoiceCode());


            List<String> uuidList = redBlueRelationList.stream().map(entity -> entity.getBlueInvoiceCode() + entity.getBlueInvoiceNo()).collect(Collectors.toList());
            // 删除结算单号
            UpdateWrapper<TDxRecordInvoiceEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in(TDxRecordInvoiceEntity.UUID, uuidList);
            TDxRecordInvoiceEntity recordInvoiceEntityU = new TDxRecordInvoiceEntity();
            recordInvoiceEntityU.setSettlementNo("");
            //recordInvoiceEntityU.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
            recordInvoiceEntityU.setStatusUpdateDate(now);
            recordInvoiceEntityU.setIsDel(IsDealEnum.YES.getValue());
            tDxRecordInvoiceDao.update(recordInvoiceEntityU, updateWrapper);

        }

        addLogByDeleteInvoice(tXfSettlementEntity.getId(), originalSettlementStatus);

        return R.ok("", "删除成功");
    }

    /**
     * 删除发票成功添加日志
     *
     * @param settlementId             结算单id
     * @param originalSettlementStatus 原结算单状态
     */
    private void addLogByDeleteInvoice(Long settlementId, Integer originalSettlementStatus) {
        // 添加日志
        TXfSettlementEntity tXfSettlementEntityNow = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(tXfSettlementEntityNow.getId(), OperateLogEnum.DELETE_INVOICE,
                Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntityNow.getSettlementStatus())).map(TXfSettlementStatusEnum::getDesc).orElse(""), "", getUserId(), UserUtil.getUserName());

        OperateLogEnum deductOpLogEnum = null;
        if (TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(originalSettlementStatus)
                && TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode().equals(tXfSettlementEntityNow.getSettlementStatus())) {
            // 由已开票变成部分开票
            deductOpLogEnum = OperateLogEnum.SETTLEMENT_DELETE_PART_RED_INVOICE;
        } else if ((TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(originalSettlementStatus) || TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode().equals(originalSettlementStatus))
                && TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(tXfSettlementEntityNow.getSettlementStatus())) {
            // 由部分开票/已开票变成待开票
            deductOpLogEnum = OperateLogEnum.SETTLEMENT_DELETE_ALL_RED_INVOICE;
        }
        operateLogService.addDeductLog(tXfSettlementEntityNow.getSettlementNo(), tXfSettlementEntityNow.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(originalSettlementStatus), deductOpLogEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
    }


    /**
     * 删除红票
     *
     * @param id
     * @return R
     */
    @Deprecated
    @Transactional
    public R deleteInvoice(Long id) {
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(id);
        if (entity == null) {
            return R.fail("根据id未找到发票");
        }
        if (entity.getInvoiceAmount().compareTo(BigDecimal.ZERO) > 0) {
            return R.fail("蓝票不允许删除");
        }
        if (InvoiceTypeEnum.isElectronic(entity.getInvoiceType())) {
            return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵");
        }
        if (!DateUtils.isCurrentMonth(entity.getInvoiceDate())) {
            return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵");
        }
        Date updateDate = new Date();
        String settlementNo = entity.getSettlementNo();
        entity.setIsDel(IsDealEnum.YES.getValue());
        entity.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
        entity.setSettlementNo("");
        entity.setStatusUpdateDate(updateDate);
        int count = tDxRecordInvoiceDao.updateById(entity);
        if (count < 1) {
            throw new EnhanceRuntimeException("删除失败,未找到发票");
        }

        if (StringUtils.isNotBlank(entity.getUuid())) {
            TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
            tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
            tDxInvoiceEntity.setUpdateDate(updateDate);
            tDxInvoiceEntity.setDelDate(updateDate);
            tDxInvoiceEntity.setRefundReason("deleteInvoice detele");
            UpdateWrapper<TDxInvoiceEntity> wrapper = new UpdateWrapper<>();
            wrapper.eq(TDxInvoiceEntity.UUID, entity.getUuid());
            int count1 = tDxInvoiceDao.update(tDxInvoiceEntity, wrapper);
            if (count1 < 1) {
                throw new EnhanceRuntimeException("删除失败,未找到扫描发票");
            }
        }

        //修改预制发票状态为待上传并制空字段
        UpdateWrapper<TXfPreInvoiceEntity> preWrapper = new UpdateWrapper<>();
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE, entity.getInvoiceCode());
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO, entity.getInvoiceNo());
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setInvoiceCode("");
        tXfPreInvoiceEntity.setInvoiceNo("");
        tXfPreInvoiceEntity.setMachineCode("");
        tXfPreInvoiceEntity.setPaperDrewDate("");
        tXfPreInvoiceEntity.setCheckCode("");
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfPreInvoiceEntity.setUpdateTime(updateDate);
        int count2 = tXfPreInvoiceDao.update(tXfPreInvoiceEntity, preWrapper);
        if (count2 < 1) {
            throw new EnhanceRuntimeException("删除失败,未找到对应预制发票");
        }
        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, settlementNo);
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
        // 原结算单状态
        Integer originalSettlementStatus = tXfSettlementEntity.getSettlementStatus();
        //修改结算单状态
        if (!updateSettlement(settlementNo, originalSettlementStatus)) {
            throw new EnhanceRuntimeException("删除失败，未找到对应结算单");
        }
        addLogByDeleteInvoice(tXfSettlementEntity.getId(), originalSettlementStatus);
        return R.ok("删除成功");
    }

    public boolean updateSettlement(String settlementNo, Integer originSettlementStatus) {
        QueryWrapper<TXfPreInvoiceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, settlementNo);
        queryWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoices = tXfPreInvoiceDao.selectList(queryWrapper);
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setUpdateTime(new Date());
        if (CollectionUtils.isEmpty(tXfPreInvoices)) {
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        } else {
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
        }
        if (!tXfSettlementEntity.getSettlementStatus().equals(originSettlementStatus)) {
            tXfSettlementEntity.setApproveStatus(SettlementApproveStatusEnum.DEFAULT.getCode());
        }
        UpdateWrapper<TXfSettlementEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, settlementNo);
        return tXfSettlementDao.update(tXfSettlementEntity, updateWrapper) > 0;
    }

    /**
     * 发票列表
     *
     * @param uuid
     * @return R
     */
    public InvoiceDetailResponse queryInvoiceByUuid(String uuid) {
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.UUID, uuid);
//        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectOne(wrapper);
        List<TDxRecordInvoiceEntity> invoiceEntityList = tDxRecordInvoiceDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(invoiceEntityList)) {
            return null;
        }
        TDxRecordInvoiceEntity entity = invoiceEntityList.get(0);
        List<InvoiceDetail> list = queryInvoiceDetailByUuid(uuid);
        response.setItems(list);
        BeanUtil.copyProperties(entity, response);
        this.convertMain(entity, response);
        return response;
    }

    /**
     * 根据发票代码+号码查询关联发票信息
     *
     * @param invoiceCode
     * @param invoiceNo
     * @param queryType   0: 通过红票查询蓝票，1：通过蓝票查询红票
     * @return
     */
    public List<InvoiceDetailResponse> queryRefInvoice(String invoiceCode, String invoiceNo, Integer queryType) {
        QueryWrapper<TXfBlueRelationEntity> wrapper = new QueryWrapper<>();
        if (0 == queryType) {
            wrapper.eq(TXfBlueRelationEntity.RED_INVOICE_CODE, invoiceCode);
            wrapper.eq(TXfBlueRelationEntity.RED_INVOICE_NO, invoiceNo);
        } else {
            wrapper.eq(TXfBlueRelationEntity.BLUE_INVOICE_CODE, invoiceCode);
            wrapper.eq(TXfBlueRelationEntity.BLUE_INVOICE_NO, invoiceNo);
        }
        List<TXfBlueRelationEntity> tXfBlueRelationEntities = tXfBlueRelationDao.selectList(wrapper);
        List<InvoiceDetailResponse> response = new ArrayList<>();
        InvoiceDetailResponse invoice;
        for (TXfBlueRelationEntity tXfBlueRelationEntity : tXfBlueRelationEntities) {
            String uuid = tXfBlueRelationEntity.getBlueInvoiceCode() + tXfBlueRelationEntity.getBlueInvoiceNo();

            if (0 != queryType) {
                uuid = tXfBlueRelationEntity.getRedInvoiceCode() + tXfBlueRelationEntity.getRedInvoiceNo();
            }

            QueryWrapper<TDxRecordInvoiceEntity> blueWrapper = new QueryWrapper<>();
            blueWrapper.eq(TDxRecordInvoiceEntity.UUID, uuid);
            TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(blueWrapper);
            if (invoiceEntity != null) {
                invoice = new InvoiceDetailResponse();
                List<InvoiceDetail> list = queryInvoiceDetailByUuid(uuid);
                invoice.setItems(list);
                BeanUtil.copyProperties(invoiceEntity, invoice);
                this.convertMain(invoiceEntity, invoice);
                response.add(invoice);
            }
        }
        return response;
    }

    /**
     * 发票列表
     *
     * @param settlementNo,
     * @param invoiceStatus
     * @param venderid
     * @param invoiceColor
     * @return List
     */
    public List<InvoiceDetailResponse> queryInvoicesBySettlementNo(String settlementNo, String invoiceStatus, String invoiceColor, String venderid) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus, venderid, invoiceColor, true);
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(wrapper);
        List<InvoiceDetailResponse> response = new ArrayList<>();
        InvoiceDetailResponse invoice;
        for (TDxRecordInvoiceEntity invoiceEntity : tDxRecordInvoiceEntities) {
            invoice = new InvoiceDetailResponse();
            List<InvoiceDetail> list = queryInvoiceDetailByUuid(invoiceEntity.getUuid());
            invoice.setItems(list);
            BeanUtil.copyProperties(invoiceEntity, invoice);
            this.convertMain(invoiceEntity, invoice);
            response.add(invoice);
        }
        return response;
    }

    private QueryWrapper<TDxRecordInvoiceEntity> getQueryWrapper(String settlementNo, String invoiceStatus, String venderid, String invoiceColor, boolean isOrder) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        if (isOrder) {
            wrapper.orderByDesc(TDxRecordInvoiceEntity.ID);
        }
        wrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue());
        if (StringUtils.isNotEmpty(venderid)) {
            wrapper.eq(TDxRecordInvoiceEntity.VENDERID, venderid);
        }
        if (StringUtils.isNotEmpty(settlementNo)) {
            wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENT_NO, settlementNo);
        }
        if (StringUtils.isNotEmpty(invoiceStatus)) {
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS, invoiceStatus);
        }
        if (StringUtils.isNotEmpty(invoiceColor)) {
            if (invoiceColor.equals("0")) {
                wrapper.le(TDxRecordInvoiceEntity.INVOICE_AMOUNT, 0);
            } else {
                wrapper.ge(TDxRecordInvoiceEntity.INVOICE_AMOUNT, 0);
            }
        }
        return wrapper;

    }


    public boolean blue4RedInvoice(String redInvoiceNo, String redInvoiceCode) {
        TDxRecordInvoiceEntity entity = new TDxRecordInvoiceEntity();
        entity.setInvoiceStatus(InvoiceStatusEnum.INVOICE_STATUS_SEND_BLUE.getCode());
        entity.setStatusUpdateDate(new Date());
        LambdaUpdateWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity::getUuid, redInvoiceCode + redInvoiceNo);
        tDxRecordInvoiceDao.update(entity, wrapper);
        return true;

    }


    public void convertMain(TDxRecordInvoiceEntity entity, InvoiceDetailResponse invoice) {
        invoice.setPurchaserAddressAndPhone(entity.getGfAddressAndPhone());
        invoice.setPurchaserBankAndNo(entity.getGfBankAndNo());
        invoice.setPurchaserName(entity.getGfName());
        invoice.setPurchaserTaxNo(entity.getGfTaxNo());
        invoice.setSellerAddressAndPhone(entity.getXfAddressAndPhone());
        invoice.setSellerBankAndNo(entity.getXfBankAndNo());
        invoice.setSellerName(entity.getXfName());
        invoice.setSellerTaxNo(entity.getXfTaxNo());
        invoice.setSellerNo(entity.getVenderid());
        invoice.setPaperDrewDate(entity.getInvoiceDate());
        invoice.setAmountWithoutTax(entity.getInvoiceAmount());
        invoice.setAmountWithTax(entity.getTotalAmount());
        invoice.setRedNotificationNo(entity.getRedNoticeNumber());
        invoice.setMachineCode(entity.getMachinecode());
        if (entity.getTaxRate() != null) {
            BigDecimal taxRate = entity.getTaxRate().divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            invoice.setTaxRate(taxRate.toPlainString());
        }
        //判断销货清单，当明细大于8条时 值为1
        if (CollectionUtils.isNotEmpty(invoice.getItems()) && invoice.getItems().size() > 8) {
            invoice.setGoodsListFlag("1");
        }
    }

    public void convertItem(TDxRecordInvoiceDetailEntity entity, InvoiceDetail invoiceDetail) {
        invoiceDetail.setAmountWithoutTax(entity.getDetailAmount());
        invoiceDetail.setId(entity.getId());
        if (StringUtils.isNotEmpty(entity.getDetailAmount()) && StringUtils.isNotEmpty(entity.getTaxAmount())) {
            //处理发票明细金额带逗号问题
            if (entity.getTaxAmount().contains(",")) {
                entity.setTaxAmount(entity.getTaxAmount().replaceAll(",", ""));
            }
            BigDecimal amountWithTax = new BigDecimal(entity.getDetailAmount()).add(new BigDecimal(entity.getTaxAmount()));
            invoiceDetail.setAmountWithTax(amountWithTax.toPlainString());
        }
        invoiceDetail.setTaxAmount(entity.getTaxAmount());
        invoiceDetail.setCargoName(entity.getGoodsName());
        invoiceDetail.setItemSpec(entity.getModel());
        invoiceDetail.setQuantity(entity.getNum());
        invoiceDetail.setQuantityUnit(entity.getUnit());
        invoiceDetail.setUnitPrice(entity.getUnitPrice());
        if (StringUtils.isNotEmpty(entity.getTaxRate())) {
            BigDecimal taxRate = new BigDecimal(entity.getTaxRate()).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            invoiceDetail.setTaxRate(taxRate.toPlainString());
        }
    }

    public Tuple2<Long, List<HostInvoiceModel>> queryHostInvoice(List<Long> ids, Integer pageNum, Integer pageSize) {
        return queryHostInvoice(null, null, null, null, null, null, ids, pageNum, pageSize);
    }

    public Tuple2<Long, List<HostInvoiceModel>> queryHostInvoice(String sellerName, String sellerNo, String hostInv,
                                                                 String invoiceNo, String invoiceDateStart, String invoiceDateEnd,
                                                                 Integer pageNum, Integer pageSize) {
        return queryHostInvoice(sellerName, sellerNo, hostInv,
                invoiceNo, invoiceDateStart, invoiceDateEnd,null, pageNum, pageSize);
    }

    public Tuple2<Long, List<HostInvoiceModel>> queryHostInvoice(String sellerName, String sellerNo, String hostInv,
                                                                 String invoiceNo, String invoiceDateStart, String invoiceDateEnd,
                                                                 Collection<Long> ids,
                                                                 Integer pageNum, Integer pageSize) {

        Page<TDxRecordInvoiceEntity> page = new LambdaQueryChainWrapper<>(getBaseMapper())
                .in(TDxRecordInvoiceEntity::getInvoiceType,
                        Arrays.asList(InvoiceTypeEnum.QC_INVOICE.getValue(), InvoiceTypeEnum.QS_INVOICE.getValue()))
                .in(CollectionUtils.isNotEmpty(ids), TDxRecordInvoiceEntity::getId, ids)
                .eq(StringUtils.isNotBlank(sellerName), TDxRecordInvoiceEntity::getXfName, sellerName)
                .eq(StringUtils.isNotBlank(sellerNo), TDxRecordInvoiceEntity::getVenderid, sellerNo)
                .eq(StringUtils.isNotBlank(hostInv), TDxRecordInvoiceEntity::getHostInv, hostInv)
                .eq(StringUtils.isNotBlank(invoiceNo), TDxRecordInvoiceEntity::getInvoiceNo, invoiceNo)
                .ge(StringUtils.isNotBlank(invoiceDateStart), TDxRecordInvoiceEntity::getInvoiceDate, invoiceDateStart)
                .le(StringUtils.isNotBlank(invoiceDateEnd), TDxRecordInvoiceEntity::getInvoiceDate, invoiceDateEnd)
                .page(new Page<>(pageNum, pageSize));
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return Tuple.of(page.getTotal(), new ArrayList<>());
        }
        List<String> uuids = page.getRecords().stream().map(TDxRecordInvoiceEntity::getUuid).distinct().collect(Collectors.toList());
        List<TDxRecordInvoiceDetailEntity> list = new LambdaQueryChainWrapper<>(recordInvoiceDetailsDao).in(TDxRecordInvoiceDetailEntity::getUuid, uuids)
                .list();
        Map<String, String> taxRateMap = list.stream().collect(Collectors
                .toMap(TDxRecordInvoiceDetailEntity::getInvoiceNo, TDxRecordInvoiceDetailEntity::getTaxRate, (old, now) -> old));

        return Tuple.of(page.getTotal(), recordInvoiceMapper.map(page.getRecords(), taxRateMap));
    }

    public String exportHostInvoice(List<Long> ids) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "HOST发票查询导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String path = tmp + ftpPath + "/" + excelFileName;
        //创建一个sheet
        File file = FileUtils.getFile(tmp + ftpPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File excl = FileUtils.getFile(file, excelFileName);
        FileInputStream inputStream = null;
        try {
            Integer num = 0;
            Integer size = 500;
            ExcelWriter sheet = EasyExcel.write(path, HostInvoiceModel.class).build();
            Tuple2<Long, List<HostInvoiceModel>> hostInvoice = queryHostInvoice(ids, num, size);
            hostInvoice._2.forEach(it-> it.setTaxRate(mapTaxRate(it.getTaxRate())));
            sheet.write(hostInvoice._2, EasyExcel.writerSheet("sheet").build());
            Long total = hostInvoice._1;
            while (total > ((long) (num + 1) * size)) {
                num++;
                hostInvoice = queryHostInvoice(ids, num, size);
                hostInvoice._2.forEach(it-> it.setTaxRate(mapTaxRate(it.getTaxRate())));
                sheet.write(hostInvoice._2, EasyExcel.writerSheet("sheet").build());
            }
            sheet.finish();

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
            excelExportlogEntity.setConditions(JSON.toJSONString(ids));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "HOST发票查询导出成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return "导出异常";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public static String mapTaxRate(String taxRate) {
        if (!NumberUtils.isNumber(taxRate)) {
            return taxRate;
        }
        return taxRate.split("\\.")[0] + "%";
    }

    /**
     * 根据uuid查询抵账表数据
     * @return
     */
    public List<TDxRecordInvoiceEntity> queryRecordInvByUuid(String uuid) {
        return tDxRecordInvoiceDao.queryRecordInvByUuid(uuid);
    }

    /**
     * flowType = 9(RMS过来的发票)
     * qsStatus = 1(签收成功)
     * aribaConfirmStatus = 0
     * 获取需推送至BMS的数据
     * @return
     */
    public List<TDxRecordInvoiceEntity> getQsToBms(String flowType, String qsStatus) {
        // 根据 ariba_confirm_status 判断是否推送成功
        LambdaQueryWrapper<TDxRecordInvoiceEntity> queryWrapper = new LambdaQueryWrapper<TDxRecordInvoiceEntity>();
        queryWrapper.eq(TDxRecordInvoiceEntity::getFlowType, flowType);
        queryWrapper.eq(TDxRecordInvoiceEntity::getQsStatus, qsStatus);
        queryWrapper.and(wapper -> wapper.in(TDxRecordInvoiceEntity::getAribaConfirmStatus, "0", "").or().isNull(TDxRecordInvoiceEntity::getAribaConfirmStatus));
        return this.list(queryWrapper);
    }
}
