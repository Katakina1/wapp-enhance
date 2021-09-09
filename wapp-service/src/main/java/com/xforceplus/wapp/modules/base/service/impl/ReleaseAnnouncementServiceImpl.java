package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.dao.ReleaseAnnouncementDao;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserImportEntity;
import com.xforceplus.wapp.modules.base.export.UserImport;
import com.xforceplus.wapp.modules.base.service.ReleaseAnnouncementService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static com.google.common.collect.Maps.newHashMap;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class ReleaseAnnouncementServiceImpl implements ReleaseAnnouncementService {
    private static final Logger LOGGER= getLogger(ReleaseAnnouncementServiceImpl.class);


    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;

    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteAnnouncementRootPath}")
    private String remoteAnnouncementRootPath;

    @Autowired
    private  ReleaseAnnouncementDao releaseAnnouncementDao;

    @Override
    public PagedQueryResult<DebtEntity> customAnnouncementList(Map<String, Object> map) {
        final PagedQueryResult<DebtEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = releaseAnnouncementDao.getCustomAnnouncementCount(map);

        //需要返回的集合
        List<DebtEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = releaseAnnouncementDao.customAnnouncementList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public PagedQueryResult<DebtEntity> debtList(Map<String, Object> map) {
        final PagedQueryResult<DebtEntity> pagedQueryResult = new PagedQueryResult<>();
        final ReportStatisticsEntity count = releaseAnnouncementDao.getDebtCount(map);

        //需要返回的集合
        List<DebtEntity> infoArrayList = newArrayList();
        if (count.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = releaseAnnouncementDao.debtList(map);
        }
        pagedQueryResult.setTotalCount(count.getTotalCount());
        pagedQueryResult.setResults(infoArrayList);
        pagedQueryResult.setTotalAmount(count.getTotalAmount());
        pagedQueryResult.setMdTotalAmount(count.getMdTotalAmount());
        pagedQueryResult.setPcTotalAmount(count.getPcTotalAmount());
        return pagedQueryResult;
    }

    @Override
    public List<UserEntity> levelList(){
        return releaseAnnouncementDao.levelList();
    }

    @Override
    public String releaseAnnouncement(AnnouncementEntity entity){
        Boolean flag=Boolean.TRUE;

        LOGGER.debug("----------------上传公告附件开始--------------------");
        String path;
        if(entity.getAttchment()!=null) {
            SFTPHandler handler = SFTPHandler.getHandler(remoteAnnouncementRootPath);
            try {
                handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                path = handler.upload(entity.getAttchment(), tempPath);
                entity.setAnnouncementAnnex(path);
            } catch (Exception e) {
                LOGGER.debug("----------------上传公告附件异常--------------------:{}", e);
                return "上传公告附件异常";
            } finally {
                if (handler != null) {
                    handler.closeChannel();
                }
            }
        }
        UserEntity userEntity = new UserEntity();
        //根据不同的供应商选择，发布公告
        switch (entity.getUserType()){
            //全部供应商
            case "0":
                    flag = this.releaseAnnounce(entity,userEntity);
                    break;
            //根据供应商类型发布
            case "1":
                    //设置供应商类型
                    userEntity.setOrgLevel(entity.getOrgLevel());
                    flag = this.releaseAnnounce(entity,userEntity);
                    break;
            //根据上传的excel里的供应商发布
            case "2":
                    //读取excel里的供应商
                    Map<String, Object> results = this.importUser(entity.getVenderFile());
                    //如果读取成功则根据供应商发布公告
                    if(Boolean.TRUE == results.get("success")){
                        List<UserEntity> userList = (List<UserEntity>)results.get("userQueryList");
                        userEntity.setUserList(userList);
                        flag = this.releaseAnnounce(entity,userEntity);
                    } else{
                        return "读取到excel数据格式有误！";
                    }
                    break;
            //根据录入的供应商号发布
            case "3":
                    //根据录入的供应商号发布公告
                    String[] venderId = entity.getVenderId().split(",");
                    userEntity.setVenderId(venderId);
                    flag = this.releaseAnnounce(entity,userEntity);
                    break;
        }

        return flag ? "发布成功":"发布失败";

    }

    @Override
    public Map<String, Object> importUser(MultipartFile file){
        final UserImport invoiceImport = new UserImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            List<UserEntity> currentList=Lists.newArrayList();
            final List<UserImportEntity> userEntityList = invoiceImport.analysisExcel();
            if (userEntityList.size()>0) {
                userEntityList.forEach(UserImportEntity->{

                    UserEntity userEntity = new UserEntity();
                    userEntity.setUsercode(UserImportEntity.getUsercode());
                    List<UserEntity> li=releaseAnnouncementDao.userlist(userEntity);
                    if(li.size()>0){
                        currentList.add(li.get(0));

                    }
                });
                map.put("userQueryList",currentList);

                map.put("success", Boolean.TRUE);

            }else {
                LOGGER.info("读取到excel数据格式有误");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel数据格式有误！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }


        return map;
    }

    @Override
    public void saveTemplate(AnnouncementEntity entity) {
        Date now = new Date();
        entity.setReleasetime(now);
        Boolean isExist = releaseAnnouncementDao.queryTemplateExist(entity)>0;
        if(isExist){
            releaseAnnouncementDao.updateTemplate(entity);
        } else {
            releaseAnnouncementDao.addAnnouncement(entity);
        }

    }

    @Override
    public AnnouncementEntity queryTemplate(AnnouncementEntity entity) {
        return releaseAnnouncementDao.queryTemplate(entity);
    }

    @Override
    public void releaseCustom() {
        releaseAnnouncementDao.releaseCustom();
    }

    @Override
    public Integer templateIsExist() {
        return releaseAnnouncementDao.templateIsExist();
    }

    @Override
    public Integer saveBatchDebt(List<DebtEntity> debtEntityList,String userCode) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        //失败的债务
        List<DebtEntity> failureList = new ArrayList<>();

        for(DebtEntity debtEntity : debtEntityList){
            //供应商号或协议号为空则跳过此条，继续下一个循环
            String venderId = debtEntity.getVenderId();
            //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
            if(!(venderId.length() > 20)) {
                /*if(StringUtils.isBlank(debtEntity.getOrderNo())&&"2".equals(debtEntity.getDebtType())) {
                    debtEntity.setFailureReason("订单号不能为空");
                    failureList.add(debtEntity);
                    continue;
                }
                if(StringUtils.isBlank(debtEntity.getGoodsNo())&&"3".equals(debtEntity.getDebtType())) {
                    debtEntity.setFailureReason("商品号不能为空");
                    failureList.add(debtEntity);
                    continue;
                }*/

                //供应商号如果不足6位，前面补0
                DecimalFormat g1 = new DecimalFormat("000000");
                venderId = g1.format(Integer.valueOf(venderId));
                debtEntity.setVenderId(venderId);
                debtEntity.setCreateBy(userCode);
                Boolean isExist =false;
                //MD判断供应商号和商品号是否已存在，PC判断供应商号和订单号和商品号是否已存在
                // Boolean isExist = releaseAnnouncementDao.queryDebtIsExist(debtEntity) > 0;
                if (!isExist) {
                    result = releaseAnnouncementDao.saveDebt(debtEntity) > 0;
                } else {
                    debtEntity.setFailureReason("债务数据已存在。");
                    failureList.add(debtEntity);
                    result = Boolean.FALSE;
                }
                //成功保存债务数据,则计数器加1
                if (result) {
                    ++successCount;
                }
            } else{
                debtEntity.setFailureReason("供应商号长度大于了20。");
                failureList.add(debtEntity);
            }
        }
        //将list切分为80一个，批量插入到数据库(sql插入字段乘以一个list大小不能超过2100)
        List<List<DebtEntity>> splitList = splitDebtList(failureList,80);
        //保存失败债务数据前删除该用户的错误列表
        releaseAnnouncementDao.deleteDebtByCreateBy(userCode);
        for(List<DebtEntity> list :splitList) {
            releaseAnnouncementDao.saveFailureDebt(list,userCode);
        }
        return successCount;
    }

    @Override
    public void deleteDebt() {
        releaseAnnouncementDao.deleteDebt();
    }

    @Override
    public List<DebtEntity> queryDebtFailureList(Map<String, Object> map) {
        return releaseAnnouncementDao.queryDebtFailureList(map);
    }

    @Override
    public List<DebtEntity> getVenderDebtList(Map<String, Object> map) {
        return releaseAnnouncementDao.getVenderDebtList(map);
    }

    /**
     * 发布公告
     * @param entity 公告信息
     * @return 是否发布成功
     */
    private Boolean releaseAnnounce(AnnouncementEntity entity,UserEntity userEntity){
        Map<String, Object> map=new HashMap<>();
        //获取所有的供应商
        List<UserEntity> userlist = releaseAnnouncementDao.userlist(userEntity);
        //设置公告未读数量
        entity.setSupplierUnreadNum(userlist.size());
        Boolean result = releaseAnnouncementDao.addAnnouncement(entity) > 0;
        //关联供应商和公告
        for(UserEntity userEntity1 : userlist){
            userEntity1.setAnnouncementid(entity.getId());
        }
        //将list切分为200一个，批量插入到数据库
        List<List<UserEntity>> splitList =splitList(userlist,200);

        for(List<UserEntity> list:  splitList){
            releaseAnnouncementDao.addAnnouncementUserMiddle(list);
        }

        return result;
    }

    /**
     * 获取上传文件的名称
     *
     * @param filename 文件的原始名称，有可能包含路径
     * @return
     */
    private String getOriginalFilename(String filename) {

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf(SUBSTR_REGEX_FOR_FILE);
        int pos = winSep > unixSep ? winSep : unixSep;
        return pos != -1 ? filename.substring(pos + 1) : filename;

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
    private static  List<List<UserEntity>> splitList(List<UserEntity> sourceList,  int  batchCount) {
        List<List<UserEntity>> returnList =  new ArrayList<>();
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
     *
     * @param sourceList
     *            要分批的list
     * @param batchCount
     *            每批list的个数
     * @return List<List<Object>>
     */
    private static  List<List<DebtEntity>> splitDebtList(List<DebtEntity> sourceList,  int  batchCount) {
        List<List<DebtEntity>> returnList =  new ArrayList<>();
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

}
