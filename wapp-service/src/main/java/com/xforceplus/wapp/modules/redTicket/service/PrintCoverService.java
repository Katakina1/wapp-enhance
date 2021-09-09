package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 9:29
 */
public interface PrintCoverService {


    Integer selectRedTicketListCount(Map<String,Object> map);

    List<RedTicketMatch> selectRedTicketList(Map<String,Object> map);

    RedTicketMatch getRedTicketMatch(long id);

    void exportRedTicketPdf(Map<String,Object> params, HttpServletResponse response);

    UserEntity getUserName(String userCode);
}
