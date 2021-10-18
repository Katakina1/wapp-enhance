package com.xforceplus.wapp.modules.rednotification.service;

import com.google.gson.Gson;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static org.junit.Assert.assertTrue;


public class TaxWareServiceTest extends BaseUnitTest {
    @Autowired
    TaxWareService taxWareService;

    Gson gson = new Gson();

    @Test
    public void testGetTerminal() {
        GetTerminalResponse terminal = taxWareService.getTerminal("91310113SHUIKWLU93");
        assert terminal!=null;
    }

    @Test
    public void testApplyRedInfo() {
        String reqJson = readJsonFromFile("data/applyRequest.json");
        ApplyRequest applyRequest = gson.fromJson(reqJson, ApplyRequest.class);
        TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
        assertTrue(Objects.equals(taxWareResponse.getCode(),"TXWR000000"));
    }

    /**
     * 处理红字信息结果
     */
    @Test
    public void handle() {
        String reqJson = readJsonFromFile("data/applyResult.json");
        RedMessage redMessage = gson.fromJson(reqJson, RedMessage.class);
        taxWareService.handle(redMessage);
    }

    @Test
    public void getTerminal() {
    }

    @Test
    public void applyRedInfo() {
    }

    @Test
    public void rollback() {
        String path = "data/RollBackRequst.json" ;
        String reqJson = readJsonFromFile(path);
        RevokeRequest revokeRequest= gson.fromJson(reqJson, RevokeRequest.class);
        TaxWareResponse taxWareResponse = taxWareService.rollback(revokeRequest);
        assertTrue(Objects.equals(taxWareResponse.getCode(),"TXWR000000"));
    }

    @Test
    public void generatePdf() {
    }

    @Test
    public void handleRollBack() {
    }
}