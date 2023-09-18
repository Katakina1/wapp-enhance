package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.exceptionreport.listener.ExceptionReportProcessListener;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemExtDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemRefExtDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.service.TransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class ClaimBillServiceTestWithoutSpring {

    @Mock
    private TXfBillDeductExtDao tXfBillDeductExtDao;

    @Mock
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;

    @Mock
    private TXfBillDeductItemExtDao tXfBillDeductItemExtDao;

    @Spy
    TaxRateConfig taxRateConfig;

    @Spy
    private TransactionalService transactionalService;

    @Mock
    private DeductItemRefBatchService deductItemRefBatchService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private OperateLogService operateLogService;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private ClaimBillService claimBillService;

    ObjectMapper objectMapper = new ObjectMapper();

    {

    }

    @BeforeEach
    public void before() {
        new ExceptionReportProcessListener(applicationContext, mock(ExceptionReportService.class));
    }
    
    @Test
    void matchClaimBill() throws IOException {

        ClassPathResource resource = new ClassPathResource("/data/ClaimBillServiceTest.json");
        JSONObject json = objectMapper.readValue(resource.getInputStream(), JSONObject.class);
        final JSONArray claimJson = json.getJSONArray("claim");
        final JSONArray itemsJson = json.getJSONArray("items");
        final List<TXfBillDeductEntity> entities = claimJson.toJavaObject(new TypeReference<List<TXfBillDeductEntity>>() {
        });
        final List<TXfBillDeductItemEntity> items = itemsJson.toJavaObject(new TypeReference<List<TXfBillDeductItemEntity>>() {
        });

        final Map<String, List<TXfBillDeductItemEntity>> collect = items.stream().collect(Collectors.groupingBy(TXfBillDeductItemEntity::getClaimNo));

        when(tXfBillDeductExtDao.queryUnMatchBill(eq(1L), any(), any(),
                eq(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()), eq(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode())
        )).thenReturn(entities);
        final long asLong = entities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
        when(tXfBillDeductExtDao.queryUnMatchBill(eq(asLong), any(), any(),
                eq(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()), eq(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode())
        )).thenReturn(null);

        when(tXfBillDeductItemRefDao.queryRefMatchAmountByBillId(any())).thenReturn(BigDecimal.ZERO);

        collect.forEach((k, v) -> {
            when(tXfBillDeductItemExtDao.queryMatchBillItem(any(), any(), any(), eq(50), eq(k))).thenReturn(v);
        });
        taxRateConfig.init();

        TAcOrgEntity seller=new TAcOrgEntity();
        when(companyService.getOrgInfoByOrgCode(any(),eq("8"))).thenReturn(seller);
        when(companyService.getOrgInfoByOrgCode(any(),eq("5"))).thenReturn(seller);

        doNothing().when(transactionalService).execute(any());
        claimBillService.matchClaimBill();
    }
}