package com.xforceplus.wapp.modules.rednotification.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import com.xforceplus.wapp.modules.rednotification.model.GetTerminalResult;
import com.xforceplus.wapp.modules.rednotification.model.QueryModel;
import com.xforceplus.wapp.modules.rednotification.model.Response;
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


public class RedNotificationMainServiceTest extends BaseUnitTest {

    @Autowired
    RedNotificationMainService redNotificationMainService;

    @Test
    public void add() throws IOException {
        String json = readJsonFromFile("data/AddRedNotificationRequest.json");
        AddRedNotificationRequest request = JsonUtil.fromJson(json, AddRedNotificationRequest.class);
        String add = redNotificationMainService.add(request);
    }

    @Test
    public void getTerminals() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("123");
        Response<GetTerminalResult> terminals = redNotificationMainService.getTerminals(queryModel);
        assertTrue(terminals.getCode()==1);
    }

    @Test
    public void getFilterData() {
        QueryModel queryModel = new QueryModel();
        queryModel.setBillNo("123");
        List<TXfRedNotificationEntity> filterData = redNotificationMainService.getFilterData(queryModel);
        assertTrue(CollectionUtils.isEmpty(filterData));
    }

    @Test
    public void applyByPage() {
    }

    @Test
    public void summary() {
    }

    @Test
    public void listData() {
    }

    @Test
    public void detail() {
    }

    @Test
    public void rollback() {
    }
}