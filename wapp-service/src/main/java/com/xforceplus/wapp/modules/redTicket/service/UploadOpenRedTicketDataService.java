package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/12 11:49
 */
public interface UploadOpenRedTicketDataService {
    Integer getRedTicketMatchListCount(Map<String, Object> map);

    List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map);

    List<FileEntity> queryRedDatalist(Map<String,Object> para);

    int deleteRedData(Map<String,Object> para);
}
