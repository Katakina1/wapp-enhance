package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportStatusEnum;
import com.xforceplus.wapp.enums.query.AgreementBillQueryTabEnum;
import com.xforceplus.wapp.enums.query.DeductBillQueryTabEnum;
import com.xforceplus.wapp.enums.query.SettlementQueryTabEnum;
import com.xforceplus.wapp.modules.deduct.dto.*;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.daoExt.SellerBillDeductQueryExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import com.xforceplus.wapp.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Describe: 供应商侧-业务单查询（索赔）
 * PS： 目前看上去和沃尔玛侧的业务单查询区别不大，拆开的原因是后续改动影响范围小
 *
 * @Author xiezhongyong
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class SellerBillQueryService {

    @Autowired
    private SellerBillDeductQueryExtDao sellerBillDeductQueryExtDao;

    @Autowired
    private BillQueryService billQueryService;


    /**
     * 列外报告处理 待匹配 状态的结算单，该列展示例外原因（Code=S002、S004、S006)
     */
    private List<String> includeExceptionCodes = Arrays.asList(
            ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE.getCode(),
            ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL.getCode(),
            ExceptionReportCodeEnum.PART_MATCH_CLAIM_DETAIL.getCode()
    );

    /**
     * 业务单列表 TAB
     *
     * @param request
     * @return PageResult
     */
    public List<QueryDeductTabResponse> queryPageTab(QuerySellerDeductListRequest request) {
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

        // 结算单状态转换
        List<Integer> settlementStatusList = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getSettlementTabKey())) {
            SettlementQueryTabEnum settlementQueryTabEnum = SettlementQueryTabEnum.fromCode(request.getSettlementTabKey());
            settlementStatusList = settlementQueryTabEnum.queryParams();
        }

        for (IQueryTab queryTab : queryTabs) {

            if (DeductBillQueryTabEnum.ALL.code().equals(queryTab.code())) {
                // 全部使用当前所有的tab count 累加即可，这里排除的目的是少执行一条count sql
                continue;
            }

            // 数量统计
            int count = sellerBillDeductQueryExtDao.countBill(
                    request.getIdList(),
                    request.getBusinessNo(),
                    businessType,
                    UserUtil.getUser().getUsercode(),
                    request.getDeductDateStart(),
                    request.getDeductDateEnd(),
                    request.getDeductInvoice(),
                    request.getVerdictDateStart(),
                    request.getVerdictDateEnd(),
                    request.getPurchaserNo(),
                    request.getCreateTimeStart(),
                    request.getCreateTimeEnd(),
                    request.getRefSettlementNo(),
                    request.getRedNotificationNo(),
                    request.getTaxRate(),
                    queryTab,
                    request.getRedNotificationStatus(),
                    request.getExceptionReportCodes(),
                    settlementStatusList);

            tabResponses.add(QueryDeductTabResponse.builder().count(count).key(queryTab.code()).desc(queryTab.message()).build());
        }
        // 全部 tab 添加
        tabResponses.add(QueryDeductTabResponse.builder().key(SettlementQueryTabEnum.ALL.code()).
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
    public PageResult<QuerySellerDeductListResponse> queryPageList(QuerySellerDeductListRequest request) {
        Integer offset = (request.getPageNo() - 1) * request.getPageSize();
        Integer next = request.getPageSize();

        // 通用参数设置
        setCommonParam(request);

        // 默认查询索赔单 待匹配
        IQueryTab billQueryTab = null;

        // 业务单据类型;1:索赔;2:协议(当业务单状态settlementStatus 为空时查询全部)
        Integer businessType = Integer.parseInt(request.getBusinessType());
        if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(businessType)) {
            // 索赔
            billQueryTab = DeductBillQueryTabEnum.ALL;
            if (StringUtils.isNotBlank(request.getBusinessTabKey())) {
                billQueryTab = DeductBillQueryTabEnum.fromCode(request.getBusinessTabKey());
            }
        } else if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(businessType)) {
            // 协议
            billQueryTab = AgreementBillQueryTabEnum.ALL;
            if (StringUtils.isNotBlank(request.getBusinessTabKey())) {
                billQueryTab = AgreementBillQueryTabEnum.fromCode(request.getBusinessTabKey());
            }
        }
        if (null == billQueryTab) {
            billQueryTab = DeductBillQueryTabEnum.ALL;
        }

        // 结算单状态转换
        List<Integer> settlementStatusList = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getSettlementTabKey())) {
            SettlementQueryTabEnum settlementQueryTabEnum = SettlementQueryTabEnum.fromCode(request.getSettlementTabKey());
            settlementStatusList = settlementQueryTabEnum.queryParams();
        }

        // 数量统计
        int count = sellerBillDeductQueryExtDao.countBill(
                request.getIdList(),
                request.getBusinessNo(),
                businessType,
                request.getSellerNo(),
                request.getDeductDateStart(),
                request.getDeductDateEnd(),
                request.getDeductInvoice(),
                request.getVerdictDateStart(),
                request.getVerdictDateEnd(),
                request.getPurchaserNo(),
                request.getCreateTimeStart(),
                request.getCreateTimeEnd(),
                request.getRefSettlementNo(),
                request.getRedNotificationNo(),
                request.getTaxRate(),
                billQueryTab,
                request.getRedNotificationStatus(),
                request.getExceptionReportCodes(),
                settlementStatusList);

        if (0 == count) {
            // 无数据不需要执行后面的查询
            return PageResult.of(new ArrayList<>(), count, request.getPageNo(), request.getPageSize());
        }

        List<TXfBillDeductExtEntity> tXfBillDeductEntities = sellerBillDeductQueryExtDao.listBill(
                offset,
                next,
                request.getIdList(),
                request.getBusinessNo(),
                businessType,
                request.getSellerNo(),
                request.getDeductDateStart(),
                request.getDeductDateEnd(),
                request.getDeductInvoice(),
                request.getVerdictDateStart(),
                request.getVerdictDateEnd(),
                request.getPurchaserNo(),
                request.getCreateTimeStart(),
                request.getCreateTimeEnd(),
                request.getRefSettlementNo(),
                request.getRedNotificationNo(),
                request.getTaxRate(),
                billQueryTab,
                request.getRedNotificationStatus(),
                request.getExceptionReportCodes(),
                settlementStatusList);

        List<QuerySellerDeductListResponse> responseList = BeanUtils.copyList(tXfBillDeductEntities, QuerySellerDeductListResponse.class);


        // 响应数据 tab key 设置
        for (QuerySellerDeductListResponse data : responseList) {
            // 默认使用当前传入 Tab
            data.setQueryTab(BillQueryTabStatusConvert.getTabResponse(billQueryTab));
            if (DeductBillQueryTabEnum.ALL.code().equals(billQueryTab.code())) {
                // 全部需要计算列表 tab, 其它使用查询入参billQueryTab
                data.setQueryTab(BillQueryTabStatusConvert.getTabResponse(BillQueryTabStatusConvert.getQueryTab(businessType, data.getStatus(), data.getSettlementStatus())));
            }
            SettlementQueryTabEnum settlementQueryTabEnum = SettlementQueryTabEnum.fromSettlementStatus(data.getSettlementStatus());
            // 结算单tab 状态转换
            data.setSettlementQueryTab(null == settlementQueryTabEnum ? null : new QueryDeductBaseResponse.QueryTabResp(settlementQueryTabEnum.code(), settlementQueryTabEnum.message()));


            // 列外报告处理 待匹配 状态的业务单，该列展示例外原因（Code=S002、S004、S006）
            if (!ExceptionReportStatusEnum.ABNORMAL.getType().equals(data.getExceptionStatus()) || !includeExceptionCodes.contains(data.getExceptionCode())) {
                data.setExceptionDescription(StringUtils.EMPTY);
            }
        }

        // 红字信息填充(未区分tab,匹配不到数据为空)
        billQueryService.fullRedNotification(responseList);

        int pages = count % request.getPageSize() == 0 ? (count / request.getPageSize()) : (count / request.getPageSize() + 1);
        return PageResult.of(responseList, count, pages, request.getPageSize());
    }

    /**
     * 通用参数设置
     *
     * @param request
     */
    private void setCommonParam(QuerySellerDeductListRequest request) {
        //创建时间、入库 >> end
        String createTimeEnd = request.getCreateTimeEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            request.setCreateTimeEnd(format);
        }
    }


}
