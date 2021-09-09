package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/22 13:56
 */

public interface UploadRedNoticeService {

    /**
     * 获取查询开红票分页数据对象
     * @param map 参数
     * @return 分页对象
     */
    List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map);
    /**
     * 获取查询开红票分页数据记录数
     * @param params 参数
     * @return
     */
    Integer getRedTicketMatchListCount(@Param("map") Map<String, Object> params);

    List<FileEntity> queryRedNoticelist(Map<String,Object> para);

    int deleteRedData(Map<String,Object> para);
}
