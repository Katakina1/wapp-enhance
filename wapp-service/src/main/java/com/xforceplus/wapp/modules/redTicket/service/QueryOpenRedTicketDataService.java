package com.xforceplus.wapp.modules.redTicket.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface QueryOpenRedTicketDataService {

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
    Integer getRedTicketMatchListCount(Map<String, Object> params);
    /**
     *
     * 通过序列号查询退货表的信息
     * @param params
     * @return
     */
    List<ReturnGoodsEntity> getReturnGoodsList( Map<String, Object> params);
    /**
     *
     * 通过序列号查询退货表的条数
     * @param params
     * @return
     */
    Integer getReturnGoodsListCount( Map<String, Object> params);

    /**
     *
     * 通过代号查询蓝票的信息
     * @param params
     * @return
     */
    List<InvoiceEntity> getRecordInvoiceList( Map<String, Object> params);
    /**
     *
     * 通过代号查蓝票的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceListCount( Map<String, Object> params);
    /**
     *
     * 通过序列号查询抵账表明细的信息
     * @param params
     * @return
     */
    List<InvoiceDetail> getRecordInvoiceDetailList(Map<String, Object> params);

    /**
     *
     * 通过代号查明细的条数
     * @param params
     * @return
     */
    Integer getRecordInvoiceDetailListCount( Map<String, Object> params);
    /**
     *
     * 通过序列号查询合并明细的信息
     * @param params
     * @return
     */
    List<RedTicketMatchDetail> getMergeInvoiceDetailList( Map<String, Object> params);
    /**
     *
     * 通过序列号查询合并条数
     * @param params
     * @return
     */
    Integer getMergeInvoiceDetailListCount( Map<String, Object> params);

    /**
     * 通过序列号查询退货表的信息
     * @param query
     * @return
     */
    Integer getAgreementListCount(Query query);

    /**
     * 通过序列号查询退货表的条数
     * @param query
     * @return
     */
    List<AgreementEntity> getAgreementList(Query query);

    /**
     *
     * @param params
     * @return file集合
     */
    List<FileEntity> getQueryImg(Map<String,Object> params);


    String uploadRedTicketData(MultipartFile file, UserEntity user, String fileNumber,Integer id);

    void getInvoiceImageForAll(Long dataAssociation, UserEntity user, HttpServletResponse response);

    void getDownLoadFile(Long id, UserEntity user, HttpServletResponse response);

    void getInvoiceImageForNotice(Long redNoticeAssociation, UserEntity user, HttpServletResponse response);

    List<OptionEntity> queryXL();

    List<OptionEntity> queryRedTicketType();
    String selectBusinessType(String redTicketDataSerialNumber);
    List<OpenRedExcelEntity> toExcel(List<RedTicketMatch> list);
}
