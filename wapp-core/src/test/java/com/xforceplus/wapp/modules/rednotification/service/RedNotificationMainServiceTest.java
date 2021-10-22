package com.xforceplus.wapp.modules.rednotification.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;


public class RedNotificationMainServiceTest extends BaseUnitTest {

    @Autowired
    RedNotificationMainService redNotificationMainService;

    @Test
    public void add() throws IOException {
        String json = readJsonFromFile("data/AddRedNotificationRequest.json");
        AddRedNotificationRequest request = JsonUtil.fromJson(json, AddRedNotificationRequest.class);
        redNotificationMainService.add(request);
    }

    @Test
    public void getTerminals() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("abc120");
        queryModel.setIsAllSelected(true);
        Response<GetTerminalResult> terminals = redNotificationMainService.getTerminals(queryModel);
        assertTrue(terminals.getCode()==1);
    }

    @Test
    public void getFilterData() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("abc120");
        queryModel.setIsAllSelected(true);
        List<TXfRedNotificationEntity> filterData = redNotificationMainService.getFilterData(queryModel);
        assertTrue(!CollectionUtils.isEmpty(filterData));
    }

    @Test
    public void applyByPage() {
    }

    @Test
    public void summary() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("abc120");
        queryModel.setIsAllSelected(true);
        Response<SummaryResult> response = redNotificationMainService.summary(queryModel);
        assertTrue(response.getCode() ==1);
    }

    @Test
    public void listData() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("abc120");
        queryModel.setIsAllSelected(true);
        Response<PageResult<RedNotificationMain>> response = redNotificationMainService.listData(queryModel);
        assertTrue(response.getCode() ==1);
        assertTrue(!CollectionUtils.isEmpty(response.getResult().getRows()));

    }

    @Test
    public void detail() {
        Response<RedNotificationInfo> response = redNotificationMainService.detail(53354550456321L);
        assertTrue(response.getCode() ==1);
    }

    @Test
    public void rollback() {
    }
}