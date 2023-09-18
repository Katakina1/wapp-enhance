package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportStatusEnum;
import com.xforceplus.wapp.enums.query.AgreementBillQueryTabEnum;
import com.xforceplus.wapp.enums.query.DeductBillQueryTabEnum;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListNewRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductTabResponse;
import com.xforceplus.wapp.repository.daoExt.BillDeductQueryExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import com.xforceplus.wapp.repository.entity.TXfDeductPreInvoiceEntity;
import com.xforceplus.wapp.util.StopWatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Describe: 沃尔玛侧-业务单查询（索赔、协议）
 *
 * @Author xiezhongyong
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class BillQueryService {

    @Autowired
    private DeductService deductService;
    @Autowired
    private BillDeductQueryExtDao billDeductQueryExtDao;
    @Autowired
    private BillRefQueryHistoryDataService billRefQueryHistoryDataService;
    /**
     * 列外报告处理 待匹配 状态的结算单，该列展示例外原因（Code=S002、S004、S006、S007)
     */
    private List<String> includeExceptionCodes = Arrays.asList(
            ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE.getCode(),
            ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL.getCode(),
            ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL.getCode(),
            ExceptionReportCodeEnum.VENDOR_NO_FAIL.getCode()
    );
    /**
     * 需要设置红字编号的tab
     */
    private List<String> setRedInfoTabs = Arrays.asList(
            DeductBillQueryTabEnum.WAIT_MAKE_INVOICE.code(),
            DeductBillQueryTabEnum.PART_MAKE_INVOICE.code(),
            DeductBillQueryTabEnum.COMPLETE_MAKE_INVOICE.code(),
//            DeductBillQueryTabEnum.WAIT_AUDIT.code(),
            AgreementBillQueryTabEnum.WAIT_MAKE_INVOICE.code(),
            AgreementBillQueryTabEnum.PART_MAKE_INVOICE.code(),
            AgreementBillQueryTabEnum.COMPLETE_MAKE_INVOICE.code()
//            AgreementBillQueryTabEnum.WAIT_AUDIT.code()
    );


    /**
     * 业务单列表 TAB
     *
     * @param request
     * @return PageResult
     */
    public List<QueryDeductTabResponse> queryPageTab(QueryDeductListNewRequest request) {
        List<QueryDeductTabResponse> tabResponses = new ArrayList<>();

        // 通用参数设置
        setCommonParam(request);

        // 默认查询索赔单
        List<IQueryTab> queryTabs = Arrays.asList(DeductBillQueryTabEnum.values());

        // 业务单据类型;1:索赔;2:协议
        Integer businessType = Integer.parseInt(request.getBusinessType());
        if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(businessType)) {
            queryTabs = Arrays.asList(AgreementBillQueryTabEnum.values());
        }

        for (IQueryTab queryTab : queryTabs) {

            if (DeductBillQueryTabEnum.ALL.code().equals(queryTab.code())) {
                // 全部使用当前所有的tab count 累加即可，这里排除的目的是少执行一条count sql
                continue;
            }

            // 数量统计
            int count = billDeductQueryExtDao.countBill(
                    request.getIdList(),
                    request.getBusinessNo(),
                    businessType,
                    request.getSellerNo(),
                    request.getSellerName(),
                    request.getDeductStartDate(),
                    request.getDeductEndDate(),
                    request.getPurchaserNo(),
                    request.getCreateTimeEnd(),
                    request.getCreateTimeBegin(),
                    request.getRefSettlementNo(),
                    request.getRedNotificationNo(),
                    request.getTaxRate(),
                    queryTab,
                    request.getRedNotificationStatus(),
                    request.getExceptionReportCodes(),
                    request.getItemTaxRate());

            tabResponses.add(QueryDeductTabResponse.builder().count(count).key(queryTab.code()).desc(queryTab.message()).build());
        }
        // 全部 tab 添加
        tabResponses.add(QueryDeductTabResponse.builder().key(DeductBillQueryTabEnum.ALL.code()).
                count(tabResponses.stream().mapToInt(QueryDeductTabResponse::getCount).sum()).
                desc(DeductBillQueryTabEnum.ALL.message()).build());

        return tabResponses;
    }


    /**
     * 业务单列表
     *
     * @param request
     * @return PageResult
     */
    public PageResult<QueryDeductListResponse> queryPageList(QueryDeductListNewRequest request) {
        Integer offset = (request.getPageNo() - 1) * request.getPageSize();
        Integer next = request.getPageSize();

        // 通用参数设置
        setCommonParam(request);

        // 默认查询索赔单 待匹配
        IQueryTab billQueryTab = null;

        // 业务单据类型;1:索赔;2:协议
        Integer businessType = Integer.parseInt(request.getBusinessType());
        if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(businessType)) {
            billQueryTab = DeductBillQueryTabEnum.fromCode(request.getKey());
        } else if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(businessType)) {
            billQueryTab = AgreementBillQueryTabEnum.fromCode(request.getKey());
        }
        if (null == billQueryTab) {
            billQueryTab = DeductBillQueryTabEnum.WAIT_MATCH;
        }


        // 数量统计
        int count = 0;
        if (request.getTotalFalg()) {
            count = billDeductQueryExtDao.countBill(
                    request.getIdList(),
                    request.getBusinessNo(),
                    businessType,
                    request.getSellerNo(),
                    request.getSellerName(),
                    request.getDeductStartDate(),
                    request.getDeductEndDate(),
                    request.getPurchaserNo(),
                    request.getCreateTimeEnd(),
                    request.getCreateTimeBegin(),
                    request.getRefSettlementNo(),
                    request.getRedNotificationNo(),
                    request.getTaxRate(),
                    billQueryTab,
                    request.getRedNotificationStatus(),
                    request.getExceptionReportCodes(),
                    request.getItemTaxRate());

            if (0 == count) {

                // 无数据不需要执行后面的查询
                return PageResult.of(new ArrayList<>(), count, request.getPageNo(), request.getPageSize());
            }
        }
        StopWatchUtils.start("数量统计-DATA");
        List<TXfBillDeductExtEntity> tXfBillDeductEntities = billDeductQueryExtDao.listBill(
                offset,
                next,
                request.getIdList(),
                request.getBusinessNo(),
                businessType,
                request.getSellerNo(),
                request.getSellerName(),
                request.getDeductStartDate(),
                request.getDeductEndDate(),
                request.getPurchaserNo(),
                request.getCreateTimeEnd(),
                request.getCreateTimeBegin(),
                request.getRefSettlementNo(),
                request.getRedNotificationNo(),
                request.getTaxRate(),
                billQueryTab,
                request.getRedNotificationStatus(),
                request.getExceptionReportCodes(),
                request.getItemTaxRate());

        StopWatchUtils.stop();
        List<QueryDeductListResponse> responseList = convertDeductResponse(tXfBillDeductEntities);
        StopWatchUtils.start("填充待拆票原因");
        //填充待拆票原因(只有待审核需要)
        if (/*DeductBillQueryTabEnum.WAIT_AUDIT.code().equals(billQueryTab.code()) ||*/

//                AgreementBillQueryTabEnum.WAIT_AUDIT.code().equals(billQueryTab.code()) ||
            // 全部（索赔+协议）
                DeductBillQueryTabEnum.ALL.code().equals(billQueryTab.code())) {
            deductService.fillSettlementRemark(responseList);
        }
        StopWatchUtils.stop();

        // 响应数据 tab key 设置
        for (QueryDeductListResponse data : responseList) {
            // 默认使用当前传入 Tab
            data.setQueryTab(BillQueryTabStatusConvert.getTabResponse(billQueryTab));
            if (DeductBillQueryTabEnum.ALL.code().equals(billQueryTab.code())) {
                // 全部需要计算列表 tab, 其它使用查询入参billQueryTab
                data.setQueryTab(BillQueryTabStatusConvert.getTabResponse(BillQueryTabStatusConvert.getQueryTab(businessType, data.getStatus(), data.getSettlementStatus())));
            }


            // 列外报告处理 待匹配 状态的业务单，该列展示例外原因（Code=S002、S004、S006、S007）
            if (!ExceptionReportStatusEnum.ABNORMAL.getType().equals(data.getExceptionStatus()) || !includeExceptionCodes.contains(data.getExceptionCode())) {
                data.setExceptionDescription(StringUtils.EMPTY);
            }
        }
        StopWatchUtils.start("红字信息填充");
        // 红字信息填充(未区分tab,匹配不到数据为空)
        fullRedNotification(responseList);
        StopWatchUtils.stop();
        StopWatchUtils.start("历史红字信息填充");
        // 历史红字信息填充
        billRefQueryHistoryDataService.fullBillRedNotification2(responseList, setRedInfoTabs);
        int pages = count % request.getPageSize() == 0 ? (count / request.getPageSize()) :
                (count / request.getPageSize() + 1);
        StopWatchUtils.stop();
        return PageResult.of(responseList, count, pages, request.getPageSize());
    }

    /**
     * 通用参数设置
     *
     * @param request
     */
    private void setCommonParam(QueryDeductListNewRequest request) {
        //创建时间、入库 >> end
        String createTimeEnd = request.getCreateTimeEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            request.setCreateTimeEnd(format);
        }

    }

    /**
     * 红字信息填充
     *
     * @param list
     */
    public void fullRedNotification(List<? extends QueryDeductBaseResponse> list) {
        List<? extends QueryDeductBaseResponse> responseList =
                list.stream().filter(v -> null == v.getQueryTab() || setRedInfoTabs.contains(v.getQueryTab().getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(responseList)) {
            return;
        }
        List<Long> billIds =
                responseList.stream().map(QueryDeductBaseResponse::getId).distinct().collect(Collectors.toList());
        // PS： 因为已撤销的预制发票 t_xf_deduct_pre_invoice 表deleted 状态未更新，需要关联预制发票表查询
        List<TXfDeductPreInvoiceEntity> refList = billDeductQueryExtDao.getBillRefByBillIds(billIds);
        Map<Long, List<TXfDeductPreInvoiceEntity>> refMaps =
                refList.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getDeductId));
        for (QueryDeductBaseResponse bill : responseList) {
            List<TXfDeductPreInvoiceEntity> refs = refMaps.get(bill.getId());
            if (CollectionUtils.isEmpty(refs)) {
                continue;
            }
            // 红字编号
            bill.setRedNotificationNos(refs.stream().map(TXfDeductPreInvoiceEntity::getRedNotificationNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
            // 红字状态
            bill.setRedNotificationStatus(refs.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

        }

    }

    public List<QueryDeductListResponse> convertDeductResponse(List<TXfBillDeductExtEntity> tXfBillDeductEntities) throws BeansException {
        List<QueryDeductListResponse> list = new ArrayList<>();
        for (TXfBillDeductExtEntity item : tXfBillDeductEntities) {
            QueryDeductListResponse queryDeductListResponse = new QueryDeductListResponse();
            queryDeductListResponse.setInvoiceType(item.getInvoiceType());
            queryDeductListResponse.setId(item.getId());
            queryDeductListResponse.setRefSettlementNo(item.getRefSettlementNo());
            queryDeductListResponse.setBusinessNo(item.getBusinessNo());
            queryDeductListResponse.setBusinessType(item.getBusinessType());
            queryDeductListResponse.setSellerNo(item.getSellerNo());
            queryDeductListResponse.setSellerName(item.getSellerName());
            queryDeductListResponse.setDeductDate(item.getDeductDate());
            queryDeductListResponse.setPurchaserNo(item.getPurchaserNo());
            queryDeductListResponse.setPurchaserName(item.getPurchaserName());
            queryDeductListResponse.setAgreementMemo(item.getAgreementMemo());
            queryDeductListResponse.setAgreementDocumentType(item.getAgreementDocumentType());
            queryDeductListResponse.setAgreementDocumentNumber(item.getAgreementDocumentNumber());
            queryDeductListResponse.setAgreementReasonCode(item.getAgreementReasonCode());
            queryDeductListResponse.setAgreementReference(item.getAgreementReference());
            queryDeductListResponse.setAgreementTaxCode(item.getAgreementTaxCode());
            queryDeductListResponse.setAmountWithTax(item.getAmountWithTax());
            queryDeductListResponse.setAmountWithoutTax(item.getAmountWithoutTax());
            queryDeductListResponse.setTaxAmount(item.getTaxAmount());
            queryDeductListResponse.setTaxRate(item.getTaxRate());
            queryDeductListResponse.setVerdictDate(item.getVerdictDate());
            log.info("verdictDate8:{}",item.getVerdictDate());
            queryDeductListResponse.setBatchNo(item.getBatchNo());
            queryDeductListResponse.setSettlementStatus(item.getSettlementStatus());
            queryDeductListResponse.setLockFlag(item.getLockFlag());
            queryDeductListResponse.setStatus(item.getStatus());
            queryDeductListResponse.setRemark(item.getRemark());
            queryDeductListResponse.setDeductInvoice(item.getDeductInvoice());
            queryDeductListResponse.setItemWithoutAmount(item.getItemWithoutAmount());
            queryDeductListResponse.setItemWithAmount(item.getItemWithAmount());
            queryDeductListResponse.setItemTaxAmount(item.getItemTaxAmount());
            queryDeductListResponse.setCreateTime(item.getCreateTime());
            queryDeductListResponse.setUpdateTime(item.getUpdateTime());
            queryDeductListResponse.setSettlementRemark(item.getSettlementRemark());
            queryDeductListResponse.setExceptionStatus(item.getExceptionStatus());
            queryDeductListResponse.setExceptionCode(item.getExceptionCode());
            queryDeductListResponse.setExceptionDescription(item.getExceptionDescription());
            queryDeductListResponse.setMakeInvoiceStatus(item.getMakeInvoiceStatus());
            list.add(queryDeductListResponse);
        }
        return list;
    }


}
