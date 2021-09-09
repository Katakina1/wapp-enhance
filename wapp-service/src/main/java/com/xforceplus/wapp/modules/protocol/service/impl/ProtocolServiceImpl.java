package com.xforceplus.wapp.modules.protocol.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import com.xforceplus.wapp.modules.protocol.dao.ProtocolDao;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolExcelEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.protocol.service.ProtocolService;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class ProtocolServiceImpl implements ProtocolService {

    private final static Logger LOGGER = getLogger(ProtocolServiceImpl.class);


    @Autowired
    private ProtocolDao protocolDao;

    @Override
    public List<ProtocolEntity> queryList(Map<String, Object> map) {
        return protocolDao.queryList(map);
    }
    @Override
    public List<ProtocolEntity> queryListExport(Map<String, Object> map){
        return protocolDao.queryListExport(map);
    }

    @Override
    public Integer queryCount(Map<String, Object> map) {
        return protocolDao.queryCount(map);
    }
    @Override
    public Integer queryCountExport(Map<String, Object> map) {
        return protocolDao.queryCountExport(map);
    }
    @Override
    public List<ProtocolEntity> queryFailureList(Map<String, Object> map) {
        return protocolDao.queryFailureList(map);
    }

    @Override
    public Integer queryFailureCount(Map<String, Object> map) {
        return protocolDao.queryFailureCount(map);
    }

    @Override
    public List<ProtocolInvoiceDetailEntity> queryInvoiceDetailFailureList(Map<String, Object> map) {
        return protocolDao.queryInvoiceDetailFailureList(map);
    }

    @Override
    public List<ProtocolDetailEntity> queryDetailList(String venderId, String protocolNo, BigDecimal detailAmount, Date caseDate) {
        return protocolDao.queryDetailList(venderId,protocolNo,detailAmount,caseDate);
    }

    @Override
    public List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(String caseDate, String protocolNo,String venderName) {
        return protocolDao.queryInvoiceDetailList(caseDate,protocolNo,venderName);
    }

    @Override
    public Map saveBatchProtocol(List<ProtocolEntity> protocolList,String userCode,HttpServletResponse response) {

        Map result = new HashMap();
        //从excel成功读取的结果数量
        Integer successCount = 0;

        Integer total = protocolList.size();
        //从excel行数据读取结果
        Boolean isSuccess=Boolean.TRUE;
        //去重后的所有协议
        HashSet<ProtocolEntity> protocolSet = new HashSet<>();
        //校验通过的成功的协议
        List<ProtocolEntity> protocolSuccessList = new ArrayList<>();
        //校验通过的成功的协议明细
        List<ProtocolEntity> protocolDetailSuccessList = new ArrayList<>();
        //校验失败的协议
        List<ProtocolEntity> failureList = new ArrayList<>();
        List<ProtocolEntity> newList= new ArrayList<>();

        //对excel里的协议进行去重
        //去掉重复检查
        protocolList.forEach(
                p -> {
                    if (!protocolSet.add(p)) {
                        //添加重复的数据到失败list里
                        newList.add(p);
                    }
                }
        );

        //清空不用的list
        protocolList.clear();

        //对协议主信息分组，减少协议主信息是否已存在查询次数
        Map<String, List<ProtocolEntity>> protocolMap = protocolSet.stream()
                .collect(Collectors.groupingBy(d -> fetchGroupKey(d) ));
        for(Map.Entry<String, List<ProtocolEntity>> entry : protocolMap.entrySet()){
            List<ProtocolEntity> list =entry.getValue();
            //获取分组后list中的第一个值，分组后的list中的协议主信息都是一样的
            ProtocolEntity protocolEntity = list.get(0);

            String venderId = protocolEntity.getVenderId();
            String protocolNo = protocolEntity.getProtocolNo();
            //供应商号或协议号为空则跳过此条，继续下一个循环
            if (!Strings.isNullOrEmpty(protocolEntity.getProtocolNo()) ||
                    !Strings.isNullOrEmpty(protocolEntity.getVenderId())) {
                //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
                if (!(venderId.length() > 20) && !(protocolNo.length() > 255)) {
                    //供应商号如果不足6位，前面补0
                    DecimalFormat g1 = new DecimalFormat("000000");
                    try {
                        venderId = g1.format(new BigDecimal(venderId));
                        for(ProtocolEntity protocol :list){
                            protocol.setVenderId(venderId);
                        }
                    } catch (Exception e){
                        LOGGER.error(e.getMessage(),e);
                        protocolEntity.setFailureReason("供应商号码格式不正确!");
                        failureList.addAll(list);
                        continue;
                    }
                    protocolEntity.setVenderId(venderId);
                    protocolEntity.setUserCode(userCode);
                    //判断供应商号和协议号是否已存在
                    Boolean isExist = protocolDao.queryProtocolAndVenderId(protocolEntity) > 0;
                    if (!isExist) {
                        //添加协议主信息
                        protocolSuccessList.add(protocolEntity);
                        //添加协议明细(扣款原因不为空才代表有明细)
                        for(ProtocolEntity protocolEntity1:list){
                            if(StringUtils.isNotBlank(protocolEntity1.getReason())){
                                protocolDetailSuccessList.add(protocolEntity1);
                            }
                        }
                        isSuccess = Boolean.TRUE;
                    }else{
                        isSuccess = Boolean.FALSE;
                        list.forEach(entity->entity.setFailureReason("协议和协议明细已存在!"));
                        failureList.addAll(list);
                    }

                    //成功保存协议,计数器加当前组数据的大小
                    if (isSuccess) {
                        successCount+=list.size();
                    }
                } else {
                    protocolEntity.setFailureReason("供应商号长度不能超过20，协议号不能超过255");
                    failureList.add(protocolEntity);
                }
            } else{
                protocolEntity.setFailureReason("协议号、供应商号都不能为空!");
                failureList.add(protocolEntity);
            }
        }

        //对协议主信息分组，减少协议主信息是否已存在查询次数
        for(ProtocolEntity protocolEntity : newList){


            String venderId = protocolEntity.getVenderId();
            String protocolNo = protocolEntity.getProtocolNo();
            //供应商号或协议号为空则跳过此条，继续下一个循环
            if (!Strings.isNullOrEmpty(protocolEntity.getProtocolNo()) ||
                    !Strings.isNullOrEmpty(protocolEntity.getVenderId())) {
                //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
                if (!(venderId.length() > 20) && !(protocolNo.length() > 255)) {
                    //供应商号如果不足6位，前面补0
                    DecimalFormat g1 = new DecimalFormat("000000");
                    try {
                        venderId = g1.format(new BigDecimal(venderId));
                        protocolEntity.setVenderId(venderId);

                    } catch (Exception e){
                        LOGGER.error(e.getMessage(),e);
                        protocolEntity.setFailureReason("供应商号码格式不正确!");
                        failureList.addAll(newList);
                        continue;
                    }
                    if(StringUtils.isNotBlank(protocolEntity.getReason())){
                      protocolDetailSuccessList.add(protocolEntity);
                        successCount++;
                    }
                } else {
                    protocolEntity.setFailureReason("供应商号长度不能超过20，协议号不能超过255");
                    failureList.add(protocolEntity);
                }
            } else{
                protocolEntity.setFailureReason("协议号、供应商号都不能为空!");
                failureList.add(protocolEntity);
            }
        }

        List<List<ProtocolEntity>> splitProtocolList=splitList(protocolSuccessList,180);
        List<List<ProtocolEntity>> splitProtocolDetailList=splitList(protocolDetailSuccessList,180);
        //批量保存协议
        for(List<ProtocolEntity> list : splitProtocolList ){
            protocolDao.save(list);
        }
        //保存协议明细前删除该用户的错误列表
        if(failureList.size()>0) {
            protocolDao.deleteByProtocolAndUserCode(userCode);
        }
        //批量保存协议明细
        for(List<ProtocolEntity> list :splitProtocolDetailList){
            protocolDao.saveDetail(list);
        }

        //将list切分为110一个，批量插入到数据库,sql参数不能超过2100
        List<List<ProtocolEntity>> splitList =this.splitList(failureList,110);

        for(List<ProtocolEntity> list :splitList) {
            protocolDao.saveFailure(list,userCode);
        }

        result.put("success",successCount);
        result.put("failure", total-successCount);
        result.put("total", total);
        return result;
    }

    @Override
    public Integer saveBatchInvoiceDetail(List<ProtocolInvoiceDetailEntity> protocolList) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        List<ProtocolInvoiceDetailEntity> failureList = new ArrayList<>();
        for(ProtocolInvoiceDetailEntity entity :protocolList){
            if(StringUtils.isNotBlank(entity.getProtocolNo())&&StringUtils.isNotBlank(entity.getInvoiceNo())
                    &&StringUtils.isNotBlank(entity.getVenderId())) {
                //查询发票明细是否存在
                Long id = protocolDao.queryInvoiceDetailExist(entity);
                Map<String, Object> map = new HashMap<>();
                map.put("agreementCode",entity.getProtocolNo());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                String caseNewDate="";
                if(entity.getCaseDate()!=null){
                    caseNewDate= sdf.format(entity.getCaseDate());
                }
                map.put("caseDate",caseNewDate);
                map.put("invoiceAmount",entity.getInvoiceAmount());
                //查询协议是否存在,协议存在才保存发票明细,协议不存在则不保存发票明细
                Integer count = protocolDao.queryCount(map);
                if(count>0) {

                    if (id == null) {
                        //发票明细不存在，保存明细
                        result = protocolDao.saveInvoiceDetail(entity) > 0;
                    } else {
                        //发票明细存在，修改明细
                        entity.setId(id);
                        result = protocolDao.updateInvoiceDetail(entity) > 0;
                    }

                    //成功保存协议,则计数器加1
                    if (result) {
                        ++successCount;
                    }
                } else{
                    entity.setFailureReason("协议不存在，发票明细保存失败!");
                    failureList.add(entity);
                }
            } else {
                entity.setFailureReason("供应商号、发票号、Fapi发票中的协议号均不能为空!");
                failureList.add(entity);
            }
        }
        //保存失败协议：改为foreach批量插入,并在插入前清空表 ；将list切分为130一个，批量插入到数据库,sql参数不能超过2100
        List<List<ProtocolInvoiceDetailEntity>> splitList =this.otherSplitList(failureList,130);

        //先清空失败协议表数据
        protocolDao.emptyFailureInvoiceDetail();

        for(List<ProtocolInvoiceDetailEntity> list :splitList) {
            protocolDao.saveFailureInvoiceDetail(list);
        }
        return successCount;
    }

    @Override
    public void deletePorotocol(Map<String, Object> map) {
        //根据查询条件删除协议和删除明细
        Long[] protocolIds = protocolDao.queryProtocolIds(map);
        List<Long> idsList = Arrays.asList(protocolIds);
        //批量删除协议和协议明细，sql参数不能超过2100
        List<List<Long>> splitList =splitLongList(idsList,1000);
        for(List<Long> ids:splitList) {
            //根据协议的id查询协议明细的id
            Long[] protocolId = ids.stream().toArray(Long[]::new);
            //获取协议明细的id,用来删除协议明细
            Long[] protocolDetailIds = protocolDao.queryProtocolDetailIds(protocolId);
            //删除协议
            protocolDao.deleteProtocol(protocolId);
            if(protocolDetailIds.length>0) {
                //批量删除协议明细,sql参数不能超过2100
                List<List<Long>> splitDetailList =splitLongList(Arrays.asList(protocolDetailIds),1000);
                for(List<Long> detailIdsList:splitDetailList) {
                    Long[] detailIds = detailIdsList.stream().toArray(Long[]::new);
                    protocolDao.deleteProtocolDetail(detailIds);
                }
            }
        }
    }
    @Override
    public ProtocolEntity queryProtocolById(String id){
        return protocolDao.queryProtocolById(Long.valueOf(id));
    }
    @Override
    public void emptyFailureProtocol() {
        protocolDao.emptyFailureProtocol();
    }

    @Override
    public void emptyFailureInvoiceDetail() {
        protocolDao.emptyFailureInvoiceDetail();
    }

    /**
     * 分批list
     *
     * @param sourceList
     *            要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<ProtocolInvoiceDetailEntity>> otherSplitList(List<ProtocolInvoiceDetailEntity> sourceList, int  batchCount) {
        List<List<ProtocolInvoiceDetailEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

    /**
     * 分批list   失败明细
     *
     * @param sourceList
     *            要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<ProtocolEntity>> splitList(List<ProtocolEntity> sourceList, int  batchCount) {
        List<List<ProtocolEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

    /**
     * 分批list
     * @param sourceList
              要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<Long>> splitLongList(List<Long> sourceList, int  batchCount) {
        List<List<Long>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

    @Override
    public void deleteByProtocolAndUserCode(String userCode){
        protocolDao.deleteByProtocolAndUserCode(userCode);
    }

    @Override
    public List<ProtocolExcelEntity> transformExcle(List<ProtocolEntity> protocolEntities) {
        List<ProtocolExcelEntity> protocolExcelEntities=new ArrayList<>();
        for (int i=0;  i < protocolEntities.size() ; i++){
            ProtocolEntity protocolEntity=protocolEntities.get(i);

            ProtocolExcelEntity protocolExcelEntity=new ProtocolExcelEntity();
            protocolExcelEntity.setRownumber(String.valueOf(i+1));
            protocolExcelEntity.setVenderId(protocolEntity.getVenderId());
            protocolExcelEntity.setVenderName(protocolEntity.getVenderName()==null?"":protocolEntity.getVenderName());
            protocolExcelEntity.setDeptNo(protocolEntity.getDeptNo()==null?"":protocolEntity.getDeptNo());
            protocolExcelEntity.setSeq(protocolEntity.getSeq()==null?"":protocolEntity.getSeq());
            protocolExcelEntity.setProtocolNo(protocolEntity.getProtocolNo());
            protocolExcelEntity.setPayItem(protocolEntity.getPayItem()==null?"":protocolEntity.getPayItem());
            protocolExcelEntity.setPayCompany(protocolEntity.getPayCompany());
            if(protocolEntity.getAmount()!=null) {
                protocolExcelEntity.setAmount( protocolEntity.getAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            } else {
                protocolExcelEntity.setAmount("");
            }
            protocolExcelEntity.setProtocolStatus(formatProtocolStatus(protocolEntity.getProtocolStatus()));
            protocolExcelEntity.setCaseDate(formatDate(protocolEntity.getCaseDate()));
            protocolExcelEntity.setPayDate(formatDate(protocolEntity.getPayDate()));
            protocolExcelEntity.setReason(protocolEntity.getReason()==null?"":protocolEntity.getReason());
            protocolExcelEntity.setNumber(protocolEntity.getNumber()==null?"":protocolEntity.getNumber());
            protocolExcelEntity.setNumberDesc(protocolEntity.getNumberDesc()==null?"":protocolEntity.getNumberDesc());
            if(protocolEntity.getDetailAmount()!=null) {
                protocolExcelEntity.setDetailAmount(protocolEntity.getDetailAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            }
            protocolExcelEntity.setStore(protocolEntity.getStore()==null?"":protocolEntity.getStore());
            protocolExcelEntities.add(protocolExcelEntity);
        }
        return protocolExcelEntities;
    }

    @Override
    public List<ProtocolExcelEntity> selectExcelpolist(Map<String, Object> params) {

        List<ProtocolEntity> protocolEntities=protocolDao.queryListExport(params);
        List<ProtocolExcelEntity> protocolExcelEntities=new ArrayList<>();

        for (int i=0;  i < protocolEntities.size() ; i++){
            ProtocolEntity protocolEntity=protocolEntities.get(i);

            ProtocolExcelEntity protocolExcelEntity=new ProtocolExcelEntity();
            protocolExcelEntity.setRownumber(String.valueOf(i+1));
            protocolExcelEntity.setVenderId(protocolEntity.getVenderId());
            protocolExcelEntity.setVenderName(protocolEntity.getVenderName()==null?"":protocolEntity.getVenderName());
            protocolExcelEntity.setDeptNo(protocolEntity.getDeptNo()==null?"":protocolEntity.getDeptNo());
            protocolExcelEntity.setSeq(protocolEntity.getSeq()==null?"":protocolEntity.getSeq());
            protocolExcelEntity.setProtocolNo(protocolEntity.getProtocolNo());
            protocolExcelEntity.setPayItem(protocolEntity.getPayItem()==null?"":protocolEntity.getPayItem());
            protocolExcelEntity.setPayCompany(protocolEntity.getPayCompany());
            if(protocolEntity.getAmount()!=null) {
                protocolExcelEntity.setAmount( protocolEntity.getAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            } else {
                protocolExcelEntity.setAmount("");
            }
            protocolExcelEntity.setProtocolStatus(formatProtocolStatus(protocolEntity.getProtocolStatus()));
            protocolExcelEntity.setCaseDate(formatDate(protocolEntity.getCaseDate()));
            protocolExcelEntity.setPayDate(formatDate(protocolEntity.getPayDate()));
            protocolExcelEntity.setReason(protocolEntity.getReason());
            protocolExcelEntity.setNumber(protocolEntity.getNumber());
            protocolExcelEntity.setNumberDesc(protocolEntity.getNumberDesc());
            if(protocolEntity.getDetailAmount()!=null) {
                protocolExcelEntity.setDetailAmount(protocolEntity.getDetailAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            } else {
                protocolExcelEntity.setDetailAmount("");
            }
            protocolExcelEntity.setStore(protocolEntity.getStore());
            protocolExcelEntities.add(protocolExcelEntity);
        }
        return protocolExcelEntities;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatProtocolStatus(String protocolStatus) {
        if ("0".equals(protocolStatus)) {
            return "协议更改-审批完成";
        } else if ("1".equals(protocolStatus)) {
            return "协议审批完成";
        }
        return "";
    }

    /**
     * 对协议主信息进行分组，减少协议主信息是否已存在查询次数
     * @param entity
     * @return
     */
    private String fetchGroupKey(ProtocolEntity entity){
        return entity.getProtocolNo()
                + entity.getVenderId()
                + entity.getCaseDate().toString()
                + entity.getAmount().toPlainString();
    }

}
