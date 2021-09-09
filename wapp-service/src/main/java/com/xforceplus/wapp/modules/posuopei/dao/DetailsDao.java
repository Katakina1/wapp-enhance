package com.xforceplus.wapp.modules.posuopei.dao;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:16:55
*/

import com.aisinopdf.text.I;
import com.xforceplus.wapp.modules.posuopei.entity.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DetailsDao {

    List<DetailEntity> getInvoiceDetail(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    List<InvoicesEntity> getOutInfo(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    DetailVehicleEntity getVehicleDetail(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    MatchEntity getResultDetail(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    List<InvoicesEntity> getMatchDetailList(@Param("schemaLabel") String schemaLabel, @Param("macthId") Long macthId);


    InvoicesEntity getDetailInfo(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    List<MatchEntity> getMatchList(Map<String,Object> params);
    Integer getMatchCount(Map<String,Object> params);

    List<MatchEntity> getTheMatchList(Map<String,Object> params);
    Integer getTheMatchCount(Map<String,Object> params);
    Integer upDateWriteStatus();

    Integer cancelClaim(@Param("matchno") String matchno);
//    Integer cancelPoFather(@Param("pocode") String  pocode, @Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus);
    Integer cancelPo(@Param("id") Integer id, @Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus);
    Integer cancelInvoice(@Param("matchno") String matchno);
    Integer cancelMatch(@Param("matchno") String matchno);
    Integer deleMatch(@Param("matchno") String matchno);
    Integer deletePo(@Param("id") Integer id, @Param("changeAmount")BigDecimal changeAmount,@Param("matchStatus") String matchStatus);
    List<PoEntity> getPoJiLu(@Param("matchno") String matchno);
    Integer ifChangePoFatherStatus(@Param("pocode") String  pocode);
    Integer ifBFPP(@Param("id") Integer id);

    List<String> getImg(@Param("matchno") String matchno);


    MatchEntity selectMatchEntity(@Param("matchno")String matchno);
    PoEntity selectPoDetail(@Param("receiptid")String receiptid);

    String selectVenderName(@Param("venderid")String venderid);

    Integer canBeCancel(@Param("matchno")String matchno);

    List<InvoicesEntity> selectHostStatus(@Param("matchno")String matchno);

    List<MatchEntity> queryListAll(Map<String,Object> params);

    String selectMatchNo(@Param("uuid")String uuid);
}
