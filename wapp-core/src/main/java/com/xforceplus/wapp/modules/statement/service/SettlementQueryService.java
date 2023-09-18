package com.xforceplus.wapp.modules.statement.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.query.SettlementQueryTabEnum;
import com.xforceplus.wapp.modules.statement.dto.AgreementExportListDto;
import com.xforceplus.wapp.modules.statement.dto.ClaimExportListDto;
import com.xforceplus.wapp.modules.statement.dto.QuerySettlementListRequest;
import com.xforceplus.wapp.modules.statement.dto.QuerySettlementTabResponse;
import com.xforceplus.wapp.modules.statement.vo.SettlementExportVo;
import com.xforceplus.wapp.modules.statement.models.Settlement;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.util.ExcelExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Describe: 结算单查询
 * PS: 和之前的 StatementServiceImpl 存在部分参数变化及tab汇总变化，新起一个查询类是因为不想影响之前的EPD的业务，本次只改动 索赔+协议
 *
 * @Author xiezhongyong
 * @Date 2022-09-12
 */
@Slf4j
@Service
public class SettlementQueryService {

    @Autowired
    private TXfBillDeductExtDao billDeductExtDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private ExcelExportUtils excelExportUtils;
    /**
     * 结算单 查询
     * 1、供应商侧结算单查询
     * 2、沃尔玛侧结算单查询
     *
     * @param user
     * @param request
     * @return
     */
    public PageResult<Settlement> listSettlement(UserEntity user, QuerySettlementListRequest request) {

        log.info("结算单查询(listSettlement)入参，user：{}， request：{}", user, JsonUtil.toJsonStr(request));

        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();

        // 结算单状态列表（不同的tab key 查询的状态不一样）
        SettlementQueryTabEnum queryTabEnum = SettlementQueryTabEnum.fromCode(request.getKey());
        if (null != queryTabEnum && CollectionUtils.isNotEmpty(queryTabEnum.queryParams())) {
            wrapper.in(TXfSettlementEntity.SETTLEMENT_STATUS, queryTabEnum.queryParams());
        }
        // 简单参数设置
        setSimpleParam(user, request, wrapper);

        // 通过业务单查询到 结算单进行匹配（TODO：此处like 后期量大会影响效率）
        if (StringUtils.isNotBlank(request.getBusinessNo())) {
            QueryWrapper<TXfBillDeductEntity> billWrapper = new QueryWrapper();
            billWrapper.likeRight(TXfBillDeductEntity.BUSINESS_NO, request.getBusinessNo());
            List<TXfBillDeductEntity> billList = billDeductExtDao.selectList(billWrapper);
            List<String> settlementNoList = billList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(settlementNoList)) {
                // 未匹配到任何数据直接返回空
                return PageResult.of(new ArrayList<>(), 0, request.getPageNo(), request.getPageSize());
            }
            wrapper.in(TXfSettlementEntity.SETTLEMENT_NO, settlementNoList);
        }
        Page<TXfSettlementEntity> respPage = tXfSettlementDao.selectPage(new Page(request.getPageNo(), request.getPageSize()), wrapper);
        log.debug("结算单查询(listSettlement),总条数:{},分页数据:{}", respPage.getTotal(), respPage.getRecords());

        List<Settlement> responseList = new ArrayList<>();
        BeanUtil.copyList(respPage.getRecords(), responseList, Settlement.class);


        List<String> settlementNoList = responseList.stream().map(Settlement::getSettlementNo).distinct().collect(Collectors.toList());
        // 查询结算单关联的业务单编号
        Map<String, Set<String>> deductSettlementNo2BusinessNoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(settlementNoList)) {
            List<TXfBillDeductEntity> deductEntityList = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo, TXfBillDeductEntity::getBusinessNo)
                    .in(TXfBillDeductEntity::getRefSettlementNo, settlementNoList)
                    .list().stream().collect(Collectors.toList());
            deductSettlementNo2BusinessNoMap = deductEntityList.stream().collect(Collectors.groupingBy(TXfBillDeductEntity::getRefSettlementNo,
                    Collectors.mapping(TXfBillDeductEntity::getBusinessNo, Collectors.toSet())));
        }
        Map<String, Set<String>> redNotificationNosMap = new HashMap<>();
        // 查询预制发票表关联的红字信息表id
        if (CollectionUtils.isNotEmpty(settlementNoList)) {
            List<TXfPreInvoiceEntity> preInvoiceEntities = new LambdaQueryChainWrapper<>(tXfPreInvoiceDao)
                    .select(TXfPreInvoiceEntity::getSettlementNo, TXfPreInvoiceEntity::getRedNotificationNo)
                    .in(TXfPreInvoiceEntity::getSettlementNo, settlementNoList)
                    .list().stream().collect(Collectors.toList());
            redNotificationNosMap = preInvoiceEntities.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getSettlementNo,
                    Collectors.mapping(TXfPreInvoiceEntity::getRedNotificationNo, Collectors.toSet())));

        }
        log.info("结算单关联的业务单 {}, 结算单关联的预制发票:{}", deductSettlementNo2BusinessNoMap, redNotificationNosMap);
        Map<String, Set<String>> finalDeductSettlementNo2BusinessNoMap = deductSettlementNo2BusinessNoMap;
        Map<String, Set<String>> finalRedNotificationNosMap = redNotificationNosMap;
        responseList.forEach(settlement -> {
            Set<String> businessNoSet = finalDeductSettlementNo2BusinessNoMap.get(settlement.getSettlementNo());
            if (CollectionUtils.isNotEmpty(businessNoSet)) {
                settlement.setBusinessNo(Joiner.on(",").join(businessNoSet));
            }
            Set<String> redNotificationNoSet = finalRedNotificationNosMap.get(settlement.getSettlementNo());
            if (CollectionUtils.isNotEmpty(redNotificationNoSet)) {
                settlement.setRedNotificationNo(Joiner.on(",").join(redNotificationNoSet));
            }
            setQueryTab(settlement);
        });

        return PageResult.of(responseList, respPage.getTotal(), request.getPageNo(), request.getPageSize());
    }

    /**
     * 结算单 tab 统计
     * 1、供应商侧结算单查询
     * 2、沃尔玛侧结算单查询
     *
     * @param user
     * @param request
     * @return
     */
    public List<QuerySettlementTabResponse> tabCount(UserEntity user, QuerySettlementListRequest request) {

        log.info("结算单tab查询(tabCount)入参，user：{}， request：{}", user, JsonUtil.toJsonStr(request));

        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();

        // 简单参数设置
        setSimpleParam(user, request, wrapper);

        // 通过业务单查询到 结算单进行匹配（TODO：此处like 后期量大会影响效率）
        if (StringUtils.isNotBlank(request.getBusinessNo())) {
            QueryWrapper<TXfBillDeductEntity> billWrapper = new QueryWrapper();
            billWrapper.likeRight(TXfBillDeductEntity.BUSINESS_NO, request.getBusinessNo());
            List<TXfBillDeductEntity> billList = billDeductExtDao.selectList(billWrapper);
            List<String> settlementNoList = billList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(settlementNoList)) {
                return convertTabResponse(Integer.parseInt(request.getBusinessType()), new ArrayList<>());
            }
            wrapper.in(TXfSettlementEntity.SETTLEMENT_NO, settlementNoList);
        }

        // 通过状态分组 
        wrapper.select("settlement_status as status, count(*) as total");
        wrapper.groupBy(TXfSettlementEntity.SETTLEMENT_STATUS);

        List<Map<String, Object>> listMaps = tXfSettlementDao.selectMaps(wrapper);
        log.debug("结算单tab查询(tabCount),sql查询结果:{}", JsonUtil.toJsonStr(listMaps));

        return convertTabResponse(Integer.parseInt(request.getBusinessType()), listMaps);
    }

    /**
     * 简单查询参数设值
     *
     * @param user
     * @param request
     * @param wrapper
     */
    private void setSimpleParam(UserEntity user, QuerySettlementListRequest request, QueryWrapper<TXfSettlementEntity> wrapper) {

        // 业务单据类型;1:索赔;2:协议
        wrapper.eq(TXfSettlementEntity.BUSINESS_TYPE, Integer.parseInt(request.getBusinessType()));

        if (user != null) {
            // 供应商侧需要进行数据过滤
            wrapper.eq(TXfSettlementEntity.SELLER_NO, UserUtil.getUser().getUsercode());
        }
        if (Objects.nonNull(request.getSettlementStatus())) {
            wrapper.eq(TXfSettlementEntity.SETTLEMENT_STATUS, request.getSettlementStatus());
        }
        if (StringUtils.isNotBlank(request.getSettlementNo())) {
            wrapper.likeRight(TXfSettlementEntity.SETTLEMENT_NO, request.getSettlementNo());
        }
        if (StringUtils.isNotBlank(request.getPurchaserNo())) {
            wrapper.eq(TXfSettlementEntity.PURCHASER_NO, request.getPurchaserNo());
        }
        if (StringUtils.isNotBlank(request.getInvoiceType())) {
            wrapper.eq(TXfSettlementEntity.INVOICE_TYPE, request.getInvoiceType());
        }
        if (Objects.nonNull(request.getTaxRate())) {
            wrapper.eq(TXfSettlementEntity.TAX_RATE, request.getTaxRate());
        }
    }

    /**
     * tab 响应转换
     *
     * @param businessType
     * @param listMaps
     * @return
     */
    private List<QuerySettlementTabResponse> convertTabResponse(Integer businessType, List<Map<String, Object>> listMaps) {

        List<QuerySettlementTabResponse> tabList = new ArrayList<>();

        Map<Integer, Integer> statusMap = new HashMap<>();
        for (Map<String, Object> listMap : listMaps) {
            statusMap.put(Integer.parseInt(listMap.get("status").toString()), Integer.parseInt(listMap.get("total").toString()));
        }

        List<SettlementQueryTabEnum> settlementQueryTabEnums = Arrays.asList(SettlementQueryTabEnum.values());

        for (SettlementQueryTabEnum tabEnum : settlementQueryTabEnums) {

            // 全部使用当前所有的tab count 累加即可，这里排除的目的是少执行一条count sql（考虑枚举后期顺序调整，所以放循环排除）
            if (tabEnum == SettlementQueryTabEnum.ALL) {
                continue;
            }

            // tab 包含多个状态需要累加
            int tabCount = 0;
            for (Integer queryParam : tabEnum.queryParams()) {
                tabCount += statusMap.getOrDefault(queryParam, 0);
            }
            tabList.add(QuerySettlementTabResponse.builder().key(tabEnum.code()).count(tabCount).desc(tabEnum.message()).build());
        }

        // 全部 tab 添加
        tabList.add(QuerySettlementTabResponse.builder().key(SettlementQueryTabEnum.ALL.code()).
                count(tabList.stream().mapToInt(QuerySettlementTabResponse::getCount).sum()).
                desc(SettlementQueryTabEnum.ALL.message()).build());

        return tabList;
    }

    /**
     * 设置响应数据queryTab
     *
     * @param settlement
     */
    public void setQueryTab(Settlement settlement) {
        for (SettlementQueryTabEnum value : SettlementQueryTabEnum.values()) {
            if (SettlementQueryTabEnum.ALL == value) {
                continue;
            }
            List<Integer> statusList = value.queryParams();
            if (statusList.contains(settlement.getSettlementStatus())) {
                settlement.setQueryTab(new Settlement.QueryTabResp(value.code(), value.message()));
                continue;
            }
        }
    }

    /**
     * 根据主键ID查询
     * @param ids
     * @return
     */
    public List<Settlement> getByBatchIds(List<Long> ids) {
        List<TXfSettlementEntity> tXfSettlementEntities = tXfSettlementDao.selectBatchIds(ids);
        List<Settlement> responseList = new ArrayList<>();
        BeanUtil.copyList(tXfSettlementEntities, responseList, Settlement.class);


        List<String> settlementNoList = responseList.stream().map(Settlement::getSettlementNo).distinct().collect(Collectors.toList());
        // 查询结算单关联的业务单编号
        Map<String, Set<String>> deductSettlementNo2BusinessNoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(settlementNoList)) {
            List<TXfBillDeductEntity> deductEntityList = new LambdaQueryChainWrapper<>(billDeductExtDao)
                    .select(TXfBillDeductEntity::getRefSettlementNo, TXfBillDeductEntity::getBusinessNo)
                    .in(TXfBillDeductEntity::getRefSettlementNo, settlementNoList)
                    .list().stream().collect(Collectors.toList());
            deductSettlementNo2BusinessNoMap = deductEntityList.stream().collect(Collectors.groupingBy(TXfBillDeductEntity::getRefSettlementNo,
                    Collectors.mapping(TXfBillDeductEntity::getBusinessNo, Collectors.toSet())));
        }
        Map<String, Set<String>> redNotificationNosMap = new HashMap<>();
        // 查询预制发票表关联的红字信息表id
        if (CollectionUtils.isNotEmpty(settlementNoList)) {
            List<TXfPreInvoiceEntity> preInvoiceEntities = new LambdaQueryChainWrapper<>(tXfPreInvoiceDao)
                    .select(TXfPreInvoiceEntity::getSettlementNo, TXfPreInvoiceEntity::getRedNotificationNo)
                    .in(TXfPreInvoiceEntity::getSettlementNo, settlementNoList)
                    .list().stream().collect(Collectors.toList());
            redNotificationNosMap = preInvoiceEntities.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getSettlementNo,
                    Collectors.mapping(TXfPreInvoiceEntity::getRedNotificationNo, Collectors.toSet())));

        }
        Map<String, Set<String>> finalDeductSettlementNo2BusinessNoMap = deductSettlementNo2BusinessNoMap;
        Map<String, Set<String>> finalRedNotificationNosMap = redNotificationNosMap;
        responseList.forEach(settlement -> {
            Set<String> businessNoSet = finalDeductSettlementNo2BusinessNoMap.get(settlement.getSettlementNo());
            if (CollectionUtils.isNotEmpty(businessNoSet)) {
                settlement.setBusinessNo(Joiner.on(",").join(businessNoSet));
            }
            Set<String> redNotificationNoSet = finalRedNotificationNosMap.get(settlement.getSettlementNo());
            if (CollectionUtils.isNotEmpty(redNotificationNoSet)) {
                settlement.setRedNotificationNo(Joiner.on(",").join(redNotificationNoSet));
            }
            setQueryTab(settlement);
        });
        return responseList;
    }


    public void export(List<Settlement> resultList, SettlementExportVo request) {
        String fileName = "红字信息表";

        // 索赔导出
        if (StringUtils.equals(request.getBusinessType(), "1")) {
            List<ClaimExportListDto> exportDtos = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                ClaimExportListDto dto = new ClaimExportListDto();
                BeanUtil.copyProperties(resultList.get(i), dto);
                dto.setSettlementStatusStr(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(dto.getSettlementStatus()).getDesc());
                exportDtos.add(dto);
            }
            fileName = fileName + "-索赔";
            excelExportUtils.messageExportOneSheet(exportDtos, ClaimExportListDto.class, fileName, JSONObject.toJSONString(request), "Sheet1");
        }
        // 协议导出
        if (StringUtils.equals(request.getBusinessType(), "2")) {
            List<AgreementExportListDto> exportDtos = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                AgreementExportListDto dto = new AgreementExportListDto();
                BeanUtil.copyProperties(resultList.get(i), dto);
                dto.setSettlementStatusStr(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(dto.getSettlementStatus()).getDesc());
                exportDtos.add(dto);
            }
            fileName = fileName + "-协议";
            excelExportUtils.messageExportOneSheet(exportDtos, AgreementExportListDto.class, fileName, JSONObject.toJSONString(request), "Sheet1");
        }

    }

    public long queryCount(UserEntity user, QuerySettlementListRequest request) {
        QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();

        // 结算单状态列表（不同的tab key 查询的状态不一样）
        SettlementQueryTabEnum queryTabEnum = SettlementQueryTabEnum.fromCode(request.getKey());
        if (null != queryTabEnum && CollectionUtils.isNotEmpty(queryTabEnum.queryParams())) {
            wrapper.in(TXfSettlementEntity.SETTLEMENT_STATUS, queryTabEnum.queryParams());
        }
        // 简单参数设置
        setSimpleParam(user, request, wrapper);
        if (StringUtils.isNotBlank(request.getBusinessNo())) {
            QueryWrapper<TXfBillDeductEntity> billWrapper = new QueryWrapper();
            billWrapper.likeRight(TXfBillDeductEntity.BUSINESS_NO, request.getBusinessNo());
            List<TXfBillDeductEntity> billList = billDeductExtDao.selectList(billWrapper);
            List<String> settlementNoList = billList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(settlementNoList)) {
                // 未匹配到任何数据直接返回空
                return 0;
            }
            wrapper.in(TXfSettlementEntity.SETTLEMENT_NO, settlementNoList);
        }
        List<TXfSettlementEntity> list = tXfSettlementDao.selectList(wrapper);
        return list.size();
    }


}
