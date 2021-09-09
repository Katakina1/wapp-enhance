package com.xforceplus.wapp.modules.fixed.service.impl;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.fixed.dao.FixedMatchDao;
import com.xforceplus.wapp.modules.fixed.entity.*;
import com.xforceplus.wapp.modules.fixed.service.MatchService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Service("fixedMatchService")
public class MatchServiceImpl implements MatchService {

    private final static Logger LOGGER = getLogger(MatchServiceImpl.class);

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
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteFixedRootPath}")
    private String remoteRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteFixedTempRootPath}")
    private String remoteTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localFixedRootPath}")
    private String localRootPath;

    @Autowired
    private FixedMatchDao matchDao;

    @Override
    public List<OrderEntity> queryOrderList(Map<String, Object> params) {
        return matchDao.queryOrderList(params);
    }

    @Override
    public List<SelectionOptionEntity> getRate() {
        return matchDao.getRate();
    }

    @Override
    public InvoiceEntity searchInvoice(String invoiceCode, String invoiceNo) {
        return matchDao.searchInvoice(invoiceCode, invoiceNo);
    }
    @Override
    public List<InvoiceEntity> searchInvoiceQuery(String invoiceQueryDate1, String invoiceQueryDate2, String invoiceNo,String gfTaxNo,String orgid) {
        return matchDao.searchInvoiceQuery(invoiceQueryDate1,invoiceQueryDate2, invoiceNo,gfTaxNo,orgid);
    }


    @Override
    @Transactional
    public void submitAll(MatchQueryEntity match) {
        //获取JV信息
        MatchQueryEntity jvinfo = matchDao.getJVInfo(match.getJvcode());
        match.setJvname(jvinfo.getJvname());
        match.setGfTaxNo(jvinfo.getGfTaxNo());
        match.setGfName(jvinfo.getGfName());
        //保存匹配表
        matchDao.saveMatch(match);
        Long matchId = match.getId();
        //获取订单信息,发票信息
        List<OrderEntity> orderList = match.getOrderList();
        List<InvoiceEntity> invoiceList = match.getInvoiceList();
        //关联表信息实体
        List<LinkEntity> linkList = newArrayList();
        String companyCode="";
        //创建订单关联信息
        for(OrderEntity order : orderList){
            companyCode=order.getCompanyCode();
            LinkEntity link = new LinkEntity();
            matchDao.updateOrder(order.getId());
            link.setMatchId(matchId);
            link.setDocType("1");
            link.setDocId(order.getId());
            linkList.add(link);
        }

        //保存或更新发票,同时创建发票关联信息
        for(InvoiceEntity invoice : invoiceList){
            if("0".equals(match.getSettlementMethod())){
                invoice.setMatching("1");
            }else{
                invoice.setMatching("2");
            }
            invoice.setCompanyCode(companyCode);
            invoice.setVenderid(match.getVenderid());
            invoice.setVenderName(match.getVenderName());
            invoice.setGfName(match.getGfName());
            invoice.setGfTaxNo(jvinfo.getGfTaxNo());
            invoice.setJvcode(match.getJvcode());
            invoice.setJvname(match.getJvname());
            invoice.setXfName(match.getXfName());
            invoice.setXfTaxNo(match.getXfTaxNo());
            LinkEntity link = new LinkEntity();
            if(null!=invoice.getId()){
                //判断来源是普票查验或专票
                if(invoice.getInvoiceType().equals("01")||(invoice.getInvoiceType().equals("04")&&invoice.getSourceSystem().equals("1"))){
                    //普票查验或专票
                    matchDao.updateInvoice(invoice);
                }else{
                    //普票录入修改
                    matchDao.updateInvoiceByEntering(invoice);
                }

            }else{
                matchDao.saveInvoice(invoice);
            }
            link.setMatchId(matchId);
            link.setDocType("2");
            link.setDocId(invoice.getId());
            linkList.add(link);
        }
        matchDao.saveLink(linkList);

        //问题单文件
        List<FileEntity> fileList = match.getFileList();
        if(fileList!=null && !fileList.isEmpty()){
            for(FileEntity file : fileList){
                //移动文件(临时->正式),返回正式路径
                String path = moveFile(file.getFilePath());
                //更新路径,关联匹配号
                matchDao.updateFile(path, matchId, file.getId());
            }
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        LOGGER.debug("----------------上传问题单文件开始--------------------");
        String filePath = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            filePath = handler.uploadDate(file);
        } catch (Exception e){
            LOGGER.debug("----------------上传问题单文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------上传问题单文件完成--------------------");
        return filePath;
    }

    @Override
    public Integer saveFile(FileEntity entity) {
        return matchDao.saveFile(entity);
    }



    /**
     * 将文件从临时目录移动至正式目录
     * @param filePath 临时目录路径
     * @return
     */
    private String moveFile(String filePath){
        LOGGER.debug("----------------移动问题单文件开始--------------------");
        String path = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteRootPath, remoteTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            path = handler.move(filePath);
        } catch (Exception e){
            LOGGER.debug("----------------移动问题单文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------移动问题单文件完成--------------------");
        return path;
    }
}
