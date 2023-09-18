package com.xforceplus.wapp.modules.entryaccount;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.ClientFactoryMockConfig;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.customs.convert.BillStatusEnum;
import com.xforceplus.wapp.modules.customs.service.CustomsService;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountResultDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result.BMSResultDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result.QueryTaxBillResult;
import com.xforceplus.wapp.modules.entryaccount.service.EntryAccountService;
import com.xforceplus.wapp.modules.entryaccount.service.impl.EntryAccountServiceImpl;
import com.xforceplus.wapp.modules.entryaccount.util.SignUtil;
import com.xforceplus.wapp.repository.entity.TDxCustomsDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: ChenHang
 * @Date: 2023/6/30 10:45
 */
@Slf4j
public class EntryAccountServiceTest /*extends BaseUnitTest*/{

    @Autowired
    private CustomsService customsService;

    @Autowired
    private EntryAccountServiceImpl entryAccountService;


    @Test
    public void test02() {
        List<EntryAccountDTO> list = new ArrayList<>();
        EntryAccountDTO entryAccountDTO = new EntryAccountDTO();
        entryAccountDTO.setPostDate(new Date());
        entryAccountDTO.setCompanyCode("xxxx");
        entryAccountDTO.setJvCode("PT");
        entryAccountDTO.setTaxAmount(new BigDecimal("10.00"));
        entryAccountDTO.setTaxCode("S6");
        entryAccountDTO.setTaxRate(new BigDecimal("5"));
        entryAccountDTO.setAccNo("凭证号aaaa");
        entryAccountDTO.setBusinessSource("S001");
        entryAccountDTO.setInvoiceCode("044002200204");
        entryAccountDTO.setInvoiceNo("00129027");
        String string = JSONObject.toJSONString(entryAccountDTO);
        System.out.println("string = " + string);
        list.add(entryAccountDTO);

//        Map<String, List<EntryAccountResultDTO>> map = entryAccountService.entryAccount(list);
    }

    @Test
    public void test01() {
        String s = null;
        String s1 = "20230630";
        String s2 = DateUtils.strToStrDate3(s);
        System.out.println("s2 = " + s2);
        if (!StringUtils.equals(s1, DateUtils.strToStrDate3(s))) {
            log.info("海关缴款书号开票日期:{}, 与BMS获取的填发日期不一致:{}", s1, s);
            // 设置海关票比对状态为比对失败
        }
    }


    public static void main(String[] args) throws Exception {

        BigDecimal bigDecimal = new BigDecimal("0.1600");
        String string = bigDecimal.toString();

        // 模拟入参
        Date date = new Date();
        date = DateUtils.toDate("2023-09-05 11:11:11", DateUtils.DATE_TIME_PATTERN);
        EntryAccountDTO entryAccountDTO = new EntryAccountDTO();
        entryAccountDTO.setBusinessSource("S002");
        entryAccountDTO.setTaxDocNo("202306100000000000001H");
        entryAccountDTO.setInvType("2");
        entryAccountDTO.setAccNo("TEST0001");
        entryAccountDTO.setPostDate(date);
        entryAccountDTO.setInvType("2");
        entryAccountDTO.setJvCode("PT");
        entryAccountDTO.setInvoiceCode(null);
        entryAccountDTO.setInvoiceNo(null);
        entryAccountDTO.setCostCenter("8001");
        entryAccountDTO.setCompanyCode("DO73");
        entryAccountDTO.setTaxCode("01");
        entryAccountDTO.setTaxRate(new BigDecimal("6.0000"));
        entryAccountDTO.setTaxAmount(new BigDecimal("12.1100"));

        System.out.println("entryAccountDTO = " + JSONObject.toJSONString(entryAccountDTO));
        Map<String, String> map = (Map<String, String>) JSONObject.parseObject(JSONObject.toJSONString(entryAccountDTO), Map.class);
        map.put("postDate", DateUtils.dateToStr(entryAccountDTO.getPostDate()));
        map.put("taxRate", entryAccountDTO.getTaxRate().toString());
        map.put("taxAmount", entryAccountDTO.getTaxAmount().toString());
        String sign = SignUtil.generateSignature(map);
        entryAccountDTO.setSign(sign);
        // 模拟入参结束

        // 校验入参签名
        Map<String, String> signMap = JSONObject.parseObject(JSONObject.toJSONString(entryAccountDTO), Map.class);
        signMap.put("postDate", DateUtils.dateToStr(entryAccountDTO.getPostDate()));
        signMap.put("taxRate", entryAccountDTO.getTaxRate().toString());
        signMap.put("taxAmount", entryAccountDTO.getTaxAmount().toString());
        String endSign = SignUtil.generateSignature(signMap);
        System.out.println("StringUtils.equals(sign, endSign) = " + StringUtils.equals(entryAccountDTO.getSign(), endSign));

    }

}
