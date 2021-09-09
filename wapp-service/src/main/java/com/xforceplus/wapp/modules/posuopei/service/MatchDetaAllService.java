package com.xforceplus.wapp.modules.posuopei.service;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:05
*/


import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.posuopei.entity.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MatchDetaAllService {

    /**
     * 获取结果明细
     */
    MatchEntity getMatchDetail(String matchno);

}