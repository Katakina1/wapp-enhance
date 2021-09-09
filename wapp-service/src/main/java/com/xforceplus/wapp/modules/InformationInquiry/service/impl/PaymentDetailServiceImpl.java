package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.InformationInquiry.dao.PaymentDetailEntityDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailExcelEntity1;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentDetailService;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;

@Service
public class PaymentDetailServiceImpl implements PaymentDetailService {

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
    @Value("${filePathConstan.vendorRemoteRootPath}")
    private String vendorRemoteRootPath;
    @Autowired
    private PaymentDetailEntityDao paymentDetailEntityDao;

    @Override
    public List<PaymentDetailEntity> findPayList(Map<String, Object> map) {
        return paymentDetailEntityDao.findPayList(map);
    }

    @Override
    public Integer paylistCount(Map<String, Object> map) {
        return paymentDetailEntityDao.paylistCount(map);
    }

    @Override
    public List<PaymentDetailExcelEntity> selectFindPayList(Map<String, Object> map) {
        List<PaymentDetailEntity> list =  paymentDetailEntityDao.findPayList(map);
        List<PaymentDetailExcelEntity> excelList = new LinkedList<PaymentDetailExcelEntity>();
        PaymentDetailExcelEntity excel = null;
        for(PaymentDetailEntity entity : list){
                excel = new PaymentDetailExcelEntity();
                excel.setVenderid(entity.getVenderid());
                excel.setOrgName(entity.getOrgName());
                excel.setReferTo(entity.getReferTo());
                excel.setDiscounts("0.00");
                excel.setShowCurrencyAmount(formatAmount(entity.getShowCurrencyAmount()+""));
                if(!"".equals(entity.getPaymentDate())&&entity.getPaymentDate()!=null){
                    excel.setPaymentDate(entity.getPaymentDate().substring(0,10));
                }
                excel.setCompanyCode(entity.getCompanyCode());
                excel.setInvoiceText(entity.getInvoiceText());
                excelList.add(excel);
        }
        return excelList;
    }

    /***
     * GF导出
     * @param map
     * @return
     */
    @Override
    public List<PaymentDetailExcelEntity1> selectFindPayListGF(Map<String, Object> map) {
        List<PaymentDetailEntity> list =  paymentDetailEntityDao.findPayList(map);
        List<PaymentDetailExcelEntity1> excelList = new LinkedList<PaymentDetailExcelEntity1>();
        PaymentDetailExcelEntity1 excel = null;
        for(PaymentDetailEntity entity : list){
            excel = new PaymentDetailExcelEntity1();
            excel.setVenderid(entity.getVenderid());
            excel.setOrgName(entity.getOrgName());
            excel.setCertificateNo(entity.getCertificateNo());
            if(!"".equals(entity.getInvoiceDate())&&entity.getInvoiceDate()!=null){
                excel.setInvoiceDate(entity.getInvoiceDate().substring(0,10));
            }

            excel.setDocumentType(entity.getDocumentType());
            if(!"".equals(entity.getPostingDate())&&entity.getPostingDate()!=null){
                excel.setPostingDate(entity.getPostingDate().substring(0,10));
            }

            excel.setCompanyCode(entity.getCompanyCode());
            excel.setCostCenter(entity.getCostCenter());
            excel.setProfitCenter(entity.getProfitCenter());
            excel.setReferTo(entity.getReferTo());
            if(!"".equals(entity.getPaymentDate())&&entity.getPaymentDate()!=null){
                excel.setPaymentDate(entity.getPaymentDate().substring(0,10));
            }
            excel.setClearanceVoucher(entity.getClearanceVoucher());
            excel.setSubject(entity.getSubject());
            excel.setUsercode(entity.getUsercode());
            excel.setShowCurrencyAmount(formatAmount(entity.getShowCurrencyAmount()+""));
            excel.setVoucherCurrency(entity.getVoucherCurrency());
            excel.setCurrencyAmount(entity.getCurrencyAmount());
            excel.setCurrency(entity.getCurrency());
            excel.setInvoiceText(entity.getInvoiceText());
            excelList.add(excel);
        }
        return excelList;
    }
    @Override
    public void upload(MultipartFile file) {
        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        //获取文件类型
        String fileType = fileName.substring(fileName.indexOf(".")+1);
        SFTPHandler handler = SFTPHandler.getHandler(vendorRemoteRootPath);
        String path="";
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            path =  handler.upload(file,tempPath);

        } catch (Exception e){
          e.printStackTrace();
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }


    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formatAmount(String d) {
        try {
            if(StringUtils.isEmpty(d)){
                return "";
            }else{
                BigDecimal b=new BigDecimal(Double.parseDouble(d));
                DecimalFormat df=new DecimalFormat("######0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                return df.format(b);
            }
        }catch (Exception e){
            return "";
        }
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
}
