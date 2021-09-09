package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.DateChineseUtils;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.dao.AnnouncementInquiryDao;
import com.xforceplus.wapp.modules.base.dao.ReleaseAnnouncementDao;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.AnnouncementExcelEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.AnnouncementInquiryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 发票借阅业务层实现
 */
@Service
@Transactional
public class AnnouncementInquiryImpl implements AnnouncementInquiryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementInquiryImpl.class);

    private final AnnouncementInquiryDao announcementInquiryDao;

    private final ReleaseAnnouncementDao releaseAnnouncementDao;
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
    @Value("${filePathConstan.remoteQuestionPaperFileTempRootPath}")
    private String remoteQuestionPaperFileTempRootPath;

    private Integer inquiryCount=0;

    private Integer unreadCount=0;

    @Autowired
    public AnnouncementInquiryImpl(AnnouncementInquiryDao announcementInquiryDao, ReleaseAnnouncementDao releaseAnnouncementDao) {
        this.announcementInquiryDao = announcementInquiryDao;
        this.releaseAnnouncementDao = releaseAnnouncementDao;
    }

    @Override
    public PagedQueryResult<AnnouncementEntity> announcementInquiryList(Map<String, Object> map) {
        final PagedQueryResult<AnnouncementEntity> pagedQueryResult = new PagedQueryResult<>();
        inquiryCount = announcementInquiryDao.getAnnouncementInquiryCount(map);
        //需要返回的集合
        List<AnnouncementEntity> infoArrayList = newArrayList();
        if (inquiryCount > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = replaceTemplateAnnounce(announcementInquiryDao.announcementInquiryList(map),map);
            if(map.get("supplierAnnoucement")==null){
                //公告查询(沃)，如果债务数据为空，不显示自定义公告
                Integer debtCount = announcementInquiryDao.getDebtCount();
                if(debtCount==0) {
                    infoArrayList.removeIf(entity -> "2".equals(entity.getAnnouncementType())||"3".equals(entity.getAnnouncementType())
                    ||"4".equals(entity.getAnnouncementType()));
                    inquiryCount=inquiryCount-3;
                }
            }
        }
        pagedQueryResult.setTotalCount(inquiryCount);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public PagedQueryResult<AnnouncementEntity> announcementUnreadList(Map<String, Object> map) {
        final PagedQueryResult<AnnouncementEntity> pagedQueryResult = new PagedQueryResult<>();
         unreadCount = announcementInquiryDao.getAnnouncementUnreadCount(map);
        //需要返回的集合
        List<AnnouncementEntity> infoArrayList = newArrayList();
        if (unreadCount > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = replaceTemplateAnnounce(announcementInquiryDao.announcementUnreadList(map),map);
        }
        pagedQueryResult.setTotalCount(unreadCount);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public PagedQueryResult<UserEntity> venderList(Map<String, Object> map) {
        final PagedQueryResult<UserEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = announcementInquiryDao.getVenderCount(map);

        //需要返回的集合
        List<UserEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = announcementInquiryDao.venderList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public void getDownLoadFile(String path, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(remoteQuestionPaperFileTempRootPath, tempPath);
        try {
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream output = response.getOutputStream();
            handler.download(path, fileName);
            File file = new File(tempPath + fileName);
            FileInputStream in = new FileInputStream(file);// 获取实体类对应Byte
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
            in.close();
            output.close();
        } catch (Exception e) {
            LOGGER.debug("----下载文件异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    @Override
    public void announceUnReadPlus(Long userId, Long announceId, String announcementType) {
        //如果为自定义公告，需要修改其他表t_dx_announcement_debt
        if ("2".equals(announcementType) || "3".equals(announcementType)) {
            announcementInquiryDao.updateCustomAnnounceHaveRead(userId,announcementType);
            //announcementInquiryDao.updateCustomAnnounceReadPlus(announceId);
        } else if("4".equals(announcementType)){
            announcementInquiryDao.updateCustomAnnounceHaveRead(userId,null);
        } else
         {
            announcementInquiryDao.updateAnnounceHaveRead(userId, announceId);
            announcementInquiryDao.updateAnnounceUnReadPlus(announceId);
        }
    }

    @Override
    public void announceAgreePlus(Long announceId,Long userId) {
        announcementInquiryDao.updateAnnounceAgreePlus(announceId);
        announcementInquiryDao.updateAnnounceAgree(userId,announceId,"1");
        announcementInquiryDao.updateAnnounceHaveRead(userId,announceId);
        announcementInquiryDao.updateAnnounceUnReadPlus(announceId);
    }

    @Override
    public void announceDisagreePlus(Long announceId,Long userId) {
        announcementInquiryDao.updateAnnounceDisagreePlus(announceId);
        announcementInquiryDao.updateAnnounceAgree(userId,announceId,"0");
        announcementInquiryDao.updateAnnounceHaveRead(userId,announceId);
        announcementInquiryDao.updateAnnounceUnReadPlus(announceId);
    }

    @Override
    public void deleteAnnounce(Long[] ids) {
        for (Long id : ids) {
            AnnouncementEntity entity = new AnnouncementEntity();
            entity.setId(id);
            AnnouncementEntity announce =announcementInquiryDao.queryAnnounce(entity);
            //删除的如果是自定义公告，删除公告时清空债务表数据
            if("2".equals(announce.getAnnouncementType())||"3".equals(announce.getAnnouncementType())||"4".equals(announce.getAnnouncementType())){
                announcementInquiryDao.emptyDebt();
                continue;
            }
            announcementInquiryDao.deleteAnnouncement(id);
            announcementInquiryDao.deleteAnnouncementVender(id);
        }
    }

    @Override
    public AnnouncementEntity queryAnnounce(AnnouncementEntity entity) {
        return announcementInquiryDao.queryAnnounce(entity);
    }

    @Override
    public void updateAnnounce(AnnouncementEntity entity) {
        announcementInquiryDao.updateAnnounce(entity);
    }

    /**
     * 替换自定义模板公告中的变量
     * @param list
     * @return
     */
    private List<AnnouncementEntity> replaceTemplateAnnounce(List<AnnouncementEntity> list,Map<String, Object> map) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        AnnouncementEntity pc = new AnnouncementEntity();
        AnnouncementEntity md = new AnnouncementEntity();
        boolean splitAnnounce = false;
        //替换自定义模板中的变量
        for (AnnouncementEntity entity : list) {
            if (StringUtils.isNotBlank(entity.getDebtType())){
                entity.setDebtType(entity.getDebtType().replace(",",""));
                if (StringUtils.containsOnly(entity.getDebtType(),"2")) {
                    //如果模板只有PC数据，则公告显示PC模板 2-PC模板
                    announcementEntity.setAnnouncementType("2");
                    announcementEntity = releaseAnnouncementDao.queryTemplate(announcementEntity);

                } else if (StringUtils.containsOnly(entity.getDebtType(),"3")) {
                    //如果模板只有MD数据，则公告显示MD模板 3-MD模板
                    announcementEntity.setAnnouncementType("3");
                    announcementEntity = releaseAnnouncementDao.queryTemplate(announcementEntity);
                } else if (entity.getDebtType().contains("2") && entity.getDebtType().contains("3")) {
                    BigDecimal totalAmount = new BigDecimal(0);
                    if (entity.getCompensationAmount() != null && entity.getPriceDifference() != null) {
                        totalAmount = totalAmount.add(entity.getCompensationAmount()).add(entity.getPriceDifference());
                        entity.setTotalAmount(totalAmount.longValue());
                    }
                    //业务要求去掉该限制
                    //如果pc加md金额超过10万，MD和PC不合并在一起，分为两个公告
                    /*if(totalAmount.compareTo(new BigDecimal(100000))==1){
                        splitAnnounce=true;
                        ++inquiryCount;
                        ++unreadCount;
                        //pc公告
                        announcementEntity.setAnnouncementType("2");
                        AnnouncementEntity pcTemplate = releaseAnnouncementDao.queryTemplate(announcementEntity);
                        pc =announcementInquiryDao.getDebtByType(entity.getVenderId(),"2",map.get("supplierAnnoucement")==null?null:map.get("supplierAnnoucement").toString());
                        String releaseDate = DateChineseUtils.getUpperDate(dateFormat.format(pc.getReleasetime()));
                        String goodsReduceDate = DateChineseUtils.getUpperDate(dateFormat.format(pc.getGoodsReduceDate()));
                        //将模板中的变量替换成实际的数据
                        String announce = pcTemplate.getAnnouncementInfo().replace("${orgName}", pc.getOrgName())
                                .replace("${venderId}", pc.getVenderId())
                                .replace("${goodsReduceDate}", goodsReduceDate)
                                .replace("${priceDifference}", pc.getPriceDifference() == null ? "" : pc.getPriceDifference().stripTrailingZeros().toPlainString())
                                .replace("${compensationAmount}", pc.getCompensationAmount() == null ? "" : pc.getCompensationAmount().stripTrailingZeros().toPlainString())
                                .replace("${releaseDate}", releaseDate);
                        pc.setAnnouncementInfo(announce);
                        pc.setId(pcTemplate.getId());
                        pc.setAnnouncementType(pcTemplate.getAnnouncementType());
                        pc.setAnnouncementTitle(pcTemplate.getAnnouncementTitle());

                        //md公告
                        announcementEntity.setAnnouncementType("3");
                        AnnouncementEntity mdTemplate = releaseAnnouncementDao.queryTemplate(announcementEntity);
                        md =announcementInquiryDao.getDebtByType(entity.getVenderId(),"3",map.get("supplierAnnoucement")==null?null:map.get("supplierAnnoucement").toString());
                        String releaseDateMd = DateChineseUtils.getUpperDate(dateFormat.format(md.getReleasetime()));
                        String goodsReduceDateMd = DateChineseUtils.getUpperDate(dateFormat.format(md.getGoodsReduceDate()));
                        //将模板中的变量替换成实际的数据
                        String announceMd = mdTemplate.getAnnouncementInfo().replace("${orgName}", md.getOrgName())
                                .replace("${venderId}", md.getVenderId())
                                .replace("${goodsReduceDate}", goodsReduceDateMd)
                                .replace("${priceDifference}", md.getPriceDifference() == null ? "" : md.getPriceDifference().stripTrailingZeros().toPlainString())
                                .replace("${compensationAmount}", md.getCompensationAmount() == null ? "" : md.getCompensationAmount().stripTrailingZeros().toPlainString())
                                .replace("${releaseDate}", releaseDateMd);
                        md.setAnnouncementInfo(announceMd);
                        md.setId(mdTemplate.getId());
                        md.setAnnouncementType(mdTemplate.getAnnouncementType());
                        md.setAnnouncementTitle(mdTemplate.getAnnouncementTitle());
                        continue;
                    }*/
                    //如果模板既有PC数据也有MD数据，则公告显示PC&MD模板 4-PC&MD模板
                    announcementEntity.setAnnouncementType("4");
                    announcementEntity = releaseAnnouncementDao.queryTemplate(announcementEntity);
                }
                BigDecimal totalAmount = new BigDecimal(0);
                if (entity.getCompensationAmount() != null && entity.getPriceDifference() != null) {
                    totalAmount = totalAmount.add(entity.getCompensationAmount()).add(entity.getPriceDifference());
                }
                String releaseDate = DateChineseUtils.getUpperDate(dateFormat.format(entity.getCustomReleasetime()));
                String goodsReduceDate ="";
                if(entity.getGoodsReduceDate()!=null){
                    goodsReduceDate = DateChineseUtils.getUpperDate(dateFormat.format(entity.getGoodsReduceDate()));
                }
                //将模板中的变量替换成实际的数据
                String announce = announcementEntity.getAnnouncementInfo().replace("${orgName}", entity.getOrgName())
                        .replace("${venderId}", entity.getVenderId())
                        .replace("${goodsReduceDate}", goodsReduceDate)
                        .replace("${totalAmount}", totalAmount.stripTrailingZeros().toPlainString())
                        .replace("${priceDifference}", entity.getPriceDifference() == null ? "" : entity.getPriceDifference().stripTrailingZeros().toPlainString())
                        .replace("${compensationAmount}", entity.getCompensationAmount() == null ? "" : entity.getCompensationAmount().stripTrailingZeros().toPlainString())
                        .replace("${releaseDate}", releaseDate);
                entity.setAnnouncementInfo(announce);
                entity.setId(announcementEntity.getId());
                entity.setAnnouncementType(announcementEntity.getAnnouncementType());
                entity.setAnnouncementTitle(announcementEntity.getAnnouncementTitle());

            }
        }
        if(splitAnnounce) {
            list.removeIf(entity -> entity.getTotalAmount() >= 100000);
            list.add(pc);
            list.add(md);
        }
        return list;
    }
    @Override
    public List<AnnouncementExcelEntity> toExcel(List<AnnouncementEntity> list){
        List<AnnouncementExcelEntity> list2=new ArrayList<>();
        int index=1;
        for (AnnouncementEntity ae:list) {
            AnnouncementExcelEntity ac=new AnnouncementExcelEntity();
            ac.setRownum(""+index++);
            ac.setAnnouncementTitle(ae.getAnnouncementTitle());
            ac.setAnnouncementType(formateAnnounceType(ae.getAnnouncementType()));
            ac.setAnnouncementInfo(ae.getAnnouncementInfo());
            ac.setReleasetime(formatDate(ae.getReleasetime()));
            ac.setSupplierAgreeNum(ae.getSupplierAgreeNum().toString());
            ac.setSupplierDisagreeNum(ae.getSupplierDisagreeNum().toString());
            ac.setSupplierReadNum(ae.getSupplierReadNum().toString());
            ac.setSupplierUnreadNum(ae.getSupplierUnreadNum().toString());
            list2.add(ac);
        }
        return list2;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateAnnounceType(String venderType){
        String value="";
        if("0".equals(venderType)){
            value="普通";
        } else if("1".equals(venderType)){
            value="培训公告";
        } else if("2".equals(venderType)){
            value="自定义公告PC";
        }else if("3".equals(venderType)){
            value="自定义公告MD";
        }else if("4".equals(venderType)){
            value="自定义公告PC&MD";
        }else if("5".equals(venderType)){
            value="自定义公告COUPON";
        }else if("6".equals(venderType)){
            value="自定义公告PFR";
        }
        return value;
    }
}
