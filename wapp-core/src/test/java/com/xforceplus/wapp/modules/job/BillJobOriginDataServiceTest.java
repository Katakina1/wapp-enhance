package com.xforceplus.wapp.modules.job;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.job.service.BillJobOriginDataService;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import com.xforceplus.wapp.repository.vo.OriginClaimBillVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/09/08 9:36
 **/
public class BillJobOriginDataServiceTest extends BaseUnitTest {

    @Autowired
    private BillJobOriginDataService billJobOriginDataService;

    @Test
    public void testClaimInfo() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DateUtils.DATE_TIME_PATTERN);
        Date startDate = df.parse("2022-05-01 00:00:00");
        Date endDate = df.parse("2022-05-30 23:59:59");
        PageResult<TXfOriginClaimBillEntity> tXfOriginClaimBillEntityPageResult =
                billJobOriginDataService.claimInfo("", startDate, endDate, 1, 10);


        PageResult<OriginClaimBillVo> tXfOriginClaimBillEntityPageResult2 =
                billJobOriginDataService.claimInfo("", startDate, endDate, null, 1, 10);


        System.err.println(JSON.toJSONString(tXfOriginClaimBillEntityPageResult));
        System.err.println(JSON.toJSONString(tXfOriginClaimBillEntityPageResult2));
    }
}
