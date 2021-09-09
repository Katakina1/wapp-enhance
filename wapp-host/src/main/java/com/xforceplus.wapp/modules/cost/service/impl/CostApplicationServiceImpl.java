package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.cost.dao.CostApplicationDao;
import com.xforceplus.wapp.modules.cost.dao.CostMatchDao;
import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.cost.export.CostImport;
import com.xforceplus.wapp.modules.cost.service.CostApplicationService;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class CostApplicationServiceImpl implements CostApplicationService {

    private final static Logger LOGGER = getLogger(CostApplicationServiceImpl.class);

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
    @Value("${filePathConstan.remoteCostFileRootPath}")
    private String remoteCostFileRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteCostFileTempRootPath}")
    private String remoteCostFileTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    @Autowired
    private CostApplicationDao costApplicationDao;

    @Autowired
    private CostMatchDao costMatchDao;

    @Autowired
    private CostPushService costPushService;

    @Override
    public SettlementEntity getUserInfo(Long id) {
        return costApplicationDao.getUserInfo(id);
    }

    @Override
    public SettlementEntity getUserInfo(String venderid) {
        return costApplicationDao.getUserInfoByCode(venderid);
    }

    @Override
    public List<SelectionOptionEntity> getCostType(String venderId, String businessType) {
        return costApplicationDao.getCostType(venderId, businessType);
    }

    @Override
    public RecordInvoiceEntity searchInvoice(String invoiceCode, String invoiceNo, String orgcode) {
        RecordInvoiceEntity res = costApplicationDao.searchInvoice(invoiceCode, invoiceNo);
        RecordInvoiceEntity taxInfo = costApplicationDao.getGf(orgcode);
        res.setJvcode(taxInfo.getJvcode());
        return res;
    }

    @Override
    public List<SelectionOptionEntity> getGfInfo() {
        return costApplicationDao.getGfInfo();
    }

    @Override
    public List<SelectionOptionEntity> getGfInfoByStaff(String staff) {
        return costApplicationDao.getGfInfoByStaff(staff);
    }

    @Override
    public List<SelectionOptionEntity> getRateOptions() {
        return costApplicationDao.getRateOptions();
    }

    @Override
    public List<SelectionOptionEntity> getDeptInfo(String staffNo) {
        return costApplicationDao.getDeptInfo(staffNo);
    }

    @Override
    public List<SelectionOptionEntity> getEmail(String vendorNo) {
        return costApplicationDao.getEmail(vendorNo);
    }

    @Override
    public Integer saveFile(SettlementFileEntity fileEntity) {
        return costApplicationDao.saveFile(fileEntity);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        LOGGER.debug("----------------上传费用文件开始--------------------");
        String filePath = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteCostFileTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            filePath = handler.upload(file);
        } catch (Exception e){
            LOGGER.debug("----------------上传费用文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------上传费用文件完成--------------------");
        return filePath;
    }

    @Override
    public SettlementFileEntity getFileInfo(Long id) {
        return costApplicationDao.getFileInfo(id);
    }

    @Override
    public void viewImg(String filePath, String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            //文件后缀
            String suffix = filePath.substring(filePath.lastIndexOf('.')+1);
            response.reset();
            //设置相应图片类型的响应头
            if("png".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/png");
            }
            if("pdf".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","application/pdf");
            }
            if("jpeg".equalsIgnoreCase(suffix) || "jpe".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/jpeg");
            }
            if("gif".equalsIgnoreCase(suffix)){
                response.setHeader("Content-Type","image/gif");
            }
            OutputStream output = response.getOutputStream();
            handler.download(filePath, fileName);
            File file = new File(handler.getLocalImageRootPath()+fileName);
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
            LOGGER.debug("----获取图片异常---" + e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    @Override
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        try {
            fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream output = response.getOutputStream();
            handler.download(filePath, fileName);
            File file = new File(handler.getLocalImageRootPath()+fileName);
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
    public void submitAll(SettlementEntity settlement) throws Exception {
        //存主信息
        costApplicationDao.saveSettlement(settlement);
        //费用号
        String costNo = settlement.getCostNo();
        //存发票
        for(RecordInvoiceEntity invoice : settlement.getInvoiceList()){
            String invoiceCode = invoice.getInvoiceCode();//代码
            String invoiceNo = invoice.getInvoiceNo();//号码
            invoice.setVenderid(settlement.getVenderId());//设置供应商号

            //增值税票存底账, 非增票不存, 但都要存记录数据的发票表
            if("1".equals(invoice.getInvoiceKind()) || "2".equals(invoice.getInvoiceKind())) {
                if (invoice.getIsExist()) {
                    //底账已有发票,更新费用号和费用发票标识
                    costApplicationDao.updateSettlementInvoice(costNo, invoice.getCoverAmount(), invoiceCode, invoiceNo);
                } else {
                    RecordInvoiceEntity gfInfo = costApplicationDao.getGf(invoice.getGfTaxNo());
                    invoice.setJvcode(gfInfo.getJvcode());
                    invoice.setJvname(gfInfo.getJvname());
                    invoice.setCompanyCode(gfInfo.getCompanyCode());
                    //再次查询底账是否有记录, 此处有记录,则说明来源是录入的, 覆盖原记录
                    RecordInvoiceEntity entity = costApplicationDao.searchInvoice(invoiceCode, invoiceNo);
                    if(entity!=null){
                        costApplicationDao.updateRecordInvoice(invoice, costNo);
                    }

                    costApplicationDao.saveSettlementInvoice(invoice, costNo);
                }
            }

            costApplicationDao.saveSettlementInvoice2(invoice, costNo);

            //存税率
            //查询税率表中已有相同的发票号码数据条数
            int i = costApplicationDao.getRateCount(invoiceNo);
            for(RateEntity rate : invoice.getRateTableData()){
                String newInvoiceNo = invoiceNo;
                //未一次性冲完的
                if(invoice.getCoverAmount().compareTo(invoice.getTotalAmount())<0 || invoice.getRateTableData().size()>1){
                    newInvoiceNo = invoiceNo+getInvoiceNoSuffix(++i);
                }
                costApplicationDao.saveRate(rate, costNo, invoiceCode, newInvoiceNo);
                Long rateId = rate.getId();

                //存费用
                for(CostEntity cost : rate.getCostTableData()) {
                    costApplicationDao.saveCost(cost, rateId);
                    //匹配过来的,计算冲销金额等,并更新
                    if("1".equals(settlement.getWalmartStatus())){
                        costMatchDao.updateCostAmount(cost);
                        costMatchDao.updateSettlementAmount(cost);
                    }
                }
            }
        }

        //存文件
        for(SettlementFileEntity file : settlement.getFileList()){
            //移动文件(临时->正式),返回正式路径
            String path = moveFile(file.getFilePath());
            //更新路径,关联费用号
            costApplicationDao.updateFile(path, costNo, file.getId());
        }

        if("0".equals(settlement.getWalmartStatus())){
            //非预付款
            //推送到BPMS, 为了数据准确性, 重新从数据库获取
            CostAppliction ca = CostAppliction.getInstance();
            settlement = costPushService.getPushData(costNo).get(0);
            ca.sendCostApplication(settlement);
        }

    }

    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final CostImport costImport= new CostImport(multipartFile, costApplicationDao);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<List<String>> list = costImport.analysisExcel();

            //存发票的uuid
            List<String> invoiceUuidList = newArrayList();
            //存税率的uuid
            List<String> rateUuidList = newArrayList();
            //存发票信息
            List<RecordInvoiceEntity> invoiceList = newArrayList();
            //存税率信息
            List<RateEntity> rateListAll = newArrayList();
            //存费用信息
            for(List<String> entity : list){
                String invoiceUuid = entity.get(0)+entity.get(7).substring(0,8);
                String rateUuid = entity.get(0)+entity.get(7);
                if(!invoiceUuidList.contains(invoiceUuid)){
                    invoiceUuidList.add(invoiceUuid);
                    RecordInvoiceEntity invoice = new RecordInvoiceEntity();
                    invoice.setInvoiceCode(entity.get(0));
                    invoice.setInvoiceNo(entity.get(7).substring(0,8));
                    invoice.setInvoiceDate(entity.get(11));
                    invoice.setCheckCode(entity.get(1));
                    invoice.setInvoiceAmount(new BigDecimal(entity.get(2)));
                    invoice.setTaxAmount(new BigDecimal(entity.get(3)));
                    invoice.setTotalAmount(new BigDecimal(entity.get(4)));
                    invoice.setGfTaxNo(entity.get(19));
                    invoiceList.add(invoice);
                }
                if(!rateUuidList.contains(rateUuid)){
                    rateUuidList.add(rateUuid);
                    RateEntity rate = new RateEntity();
                    rate.setInvoiceAmount(new BigDecimal(entity.get(8)));
                    rate.setTaxRate(entity.get(6).split("_")[0]);
                    rate.setTaxAmount(new BigDecimal(entity.get(9)));
                    rateListAll.add(rate);
                }
            }

            //费用列放入税率结构中
            for(int a=0;a<rateListAll.size();a++){
                RateEntity rate = rateListAll.get(a);
                String rateUuid = rateUuidList.get(a);
                //费用列表
                List<CostEntity> costList = newArrayList();
                for(List<String> entity : list){
                    if(rateUuid.equals(entity.get(0)+entity.get(7))){
                        CostEntity cost = new CostEntity();
                        cost.setCostType(entity.get(12).split("_")[0]);
                        cost.setCostTypeName(entity.get(12).split("_")[1]);
                        cost.setCostDeptId(entity.get(13));
                        cost.setCostTime(entity.get(14)+"至"+entity.get(15));
                        cost.setCostUse(entity.get(16));
                        cost.setCostAmount(new BigDecimal(entity.get(17)));
                        cost.setProjectCode(entity.get(18));
                        costList.add(cost);
                    }
                }
                rate.setCostTableData(costList);
            }

            //税率列放入发票结构中
            for(int b=0;b<invoiceList.size();b++){
                RecordInvoiceEntity invoice = invoiceList.get(b);
                List<RateEntity> rateList = newArrayList();
                for(int a=0;a<rateListAll.size();a++) {
                    String rateUuid = rateUuidList.get(a);
                    if(rateUuid.startsWith(invoice.getInvoiceCode()+invoice.getInvoiceNo())){
                        rateList.add(rateListAll.get(a));
                    }
                }
                invoice.setRateTableData(rateList);
            }

            map.put("success", Boolean.TRUE);
            map.put("dataList",invoiceList);
        } catch (RuntimeException re) {
            LOGGER.error("发票重复使用:{}", re);
            map.put("success", Boolean.FALSE);
            map.put("reason", re.getMessage());
        } catch (Exception e) {
            LOGGER.error("解析excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "解析excel文件异常！");
        }
        return map;
    }

    @Override
    public String getGfTaxNo(String jvcode) {
        return costApplicationDao.getGf(jvcode).getJvcode();
    }

    @Override
    public String getGfTaxNoByDept(String dept) {
        return costApplicationDao.getGfTaxNoByDept(dept);
    }

    @Override
    public String getGfTaxNoByStaffNo(String staffNo) {
        return costApplicationDao.getGfTaxNoByStaffNo(staffNo);
    }

    /**
     * 将文件从临时目录移动至正式目录
     * @param filePath 临时目录路径
     * @return
     */
    private String moveFile(String filePath){
        LOGGER.debug("----------------移动费用文件开始--------------------");
        String path = "";
        SFTPHandler handler = SFTPHandler.getHandler(remoteCostFileRootPath, remoteCostFileTempRootPath);
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            path = handler.move(filePath);
        } catch (Exception e){
            LOGGER.debug("----------------移动费用文件异常--------------------:{}" , e);
        } finally {
            handler.closeChannel();
        }
        LOGGER.debug("----------------移动费用文件完成--------------------");
        return path;
    }

    private String getInvoiceNoSuffix(int i){
        if(i<10){
            return "0"+i;
        }else{
            return ""+i;
        }
    }
}
