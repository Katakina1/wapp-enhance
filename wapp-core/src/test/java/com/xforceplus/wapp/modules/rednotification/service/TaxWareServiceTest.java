package com.xforceplus.wapp.modules.rednotification.service;

import com.google.gson.Gson;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.modules.rednotification.model.taxware.ApplyRequest;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import com.xforceplus.wapp.modules.rednotification.model.taxware.TaxWareResponse;
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
        GetTerminalResponse terminal = taxWareService.getTerminal("91420111271850146W");
        assert terminal!=null;
    }

    @Test
    public void testApplyRedInfo() {
        String reqJson = readJsonFromFile("data/applyRequest.json");
        ApplyRequest applyRequest = gson.fromJson(reqJson, ApplyRequest.class);
        TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
        assertTrue(Objects.equals(taxWareResponse.getCode(),"TXWR000000"));
    }
}