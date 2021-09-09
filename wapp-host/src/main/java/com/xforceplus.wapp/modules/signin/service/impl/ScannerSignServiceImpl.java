package com.xforceplus.wapp.modules.signin.service.impl;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.CHECK_BACK_INVOICE_Y;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.CHECK_INVOICE_BACK_FAIL_CODE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.CHECK_INVOICE_FAIL_CODE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.CHECK_INVOICE_SUCCESS_CODE;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.CHECK_RESULT_TIP_ERROR;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_DETAIL_NO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_DETAIL_YES;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_QS_STATUS_FAIL_ZERO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_SOURCE_SYSTEM;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_STATUS_TWO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_STATUS_ZERO;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.INVOICE_VALID_ONE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.einvoice.util.DateTimeHelper;
import com.xforceplus.wapp.modules.signin.dao.ScannerSignDao;
import com.xforceplus.wapp.modules.signin.entity.InvoiceImgQueryVo;
import com.xforceplus.wapp.modules.signin.entity.InvoiceImgSavePo;
import com.xforceplus.wapp.modules.signin.entity.InvoiceSavePo;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScanResultVo;
import com.xforceplus.wapp.modules.signin.entity.OrgTaxNoInfo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceCreateSignPo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceQueryByCodeAndNoPo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceQueryByCodeAndNoVo;
import com.xforceplus.wapp.modules.signin.entity.SignedInvoiceVo;
import com.xforceplus.wapp.modules.signin.service.ScannerSignService;
import com.google.common.collect.Lists;
import com.jcraft.jsch.JSchException;

@Service
@Transactional
public class ScannerSignServiceImpl implements ScannerSignService {
    @Autowired
    private ScannerSignDao scannerSignDao;

    @Value("${pro.sftp.host}")
    private String host; // sftp IP底账
    @Value("${pro.sftp.username}")
    private String userName; // sftp 用户名
    @Value("${pro.sftp.password}")
    private String password; // sftp 密码
    @Value("${pro.sftp.default.port}")
    private String defaultPort; // sftp 默认端口号
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout; // sftp 默认超时时间
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;
    @Value("${filePathConstan.remoteImageRootPath}")
    private String remoteImageRootPath;
    @Value("${mycat.default_schema_label}")
    private String schemaLabel;

    /**
     * 10位随机数
     * @return
     */
    @Override
    public String sjnum() {
        int a[] = new int[10];
        String sjnum = "";
        for(int i=0;i<a.length;i ++) {
            a[i] = (int)(10*(Math.random()));
            sjnum +=a[i];
        }
        return sjnum;
    }
    /**
     * 使用电子底帐进行发票签收
     * @param invoiceVo
     * @param id
     * @return
     */
    @Override
    public int signUseRecord(SignedInvoiceVo invoiceVo, Long id){

        // 设置uuid
        invoiceVo.setUuid(new StringBuilder(invoiceVo.getInvoiceCode()).append(invoiceVo.getInvoiceNo()).toString());

        UserEntity sysUserEntity=(UserEntity) SecurityUtils.getSubject().getPrincipal();
        // 用户姓名
        invoiceVo.setUserName(sysUserEntity.getUsername());
        // 设置用户账号
        invoiceVo.setUserAccount(String.valueOf(sysUserEntity.getLoginname()));


        //扫描购方税号与下拉框选择购方名称税号不匹配
        OrgTaxNoInfo taxCode = null;
        try {
            //String gfName = invoiceVo.getGfName().replace("(", "（").replace(")", "）");
            taxCode = scannerSignDao.getOrgTaxGfName(invoiceVo.getGfTaxNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(taxCode == null){
            invoiceVo.setInvoiceStatus("0");
            invoiceVo.setGfName("");
            if(invoiceVo.getNotes()==null){
                invoiceVo.setNotes("购方税号错误,");
            }else {
                invoiceVo.setNotes(invoiceVo.getNotes() + ",购方税号错误,");
            }
            try {
                int row1 = saveInvoice(invoiceVo, id);
                if(1 != row1) throw new Exception(" Save Invoice Data Failed !");
            } catch (Exception e) {
                invoiceVo.setNotes("重复扫描");
                return -3;
            }
            // 返回未签收
            return -2;
        }else{
            invoiceVo.setGfName(taxCode.getCompanyName());
        }


        // 签收信息，存放签收操作结果
        StringBuilder note = new StringBuilder();

        // 获取电子底账
        RecordInvoiceQueryByCodeAndNoVo record = new RecordInvoiceQueryByCodeAndNoVo();


        try {
            record = queryRecordInvoiceByCodeAndNo(_2RecordSignPo(invoiceVo));
            //设置消防名称
            invoiceVo.setXfName(record.getXfName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 判断是否有底账
        if(null == record){// 无底账，不签收
            boolean isSuccess = true;
            if("F".equals(invoiceVo.getInvoiceCode().substring(0,1))){
                note.append("发票代码错误,");
                isSuccess = false;
            }
            if("F".equals(invoiceVo.getInvoiceNo().substring(0, 1))){
                note.append("发票号码错误,");
                isSuccess = false;
            }
            if("".equals(invoiceVo.getInvoiceDate()) ||invoiceVo.getInvoiceDate()==null){
                note.append("开票日期错误,");
                isSuccess = false;
            }
            if("".equals(invoiceVo.getXfTaxNo()) ||invoiceVo.getXfTaxNo()==null){
                note.append("销方税号错误,");
                isSuccess = false;
            }
            if("".equals(invoiceVo.getInvoiceAmount()) ||invoiceVo.getInvoiceAmount()==null){
                note.append("开票金额错误,");
                isSuccess = false;
            }
            //判断销方税号开票类型
//			List<String> taxNoCacheList = CacheStatic.getTaxNoCacheList();
//			if (taxNoCacheList.contains(invoiceVo.getXfTaxNo())) {
//				note.append("该销方税号只能开普票,");
//				isSuccess = false;
//			}
            if(isSuccess==false){
                invoiceVo.setNotes(StringUtils.substringBeforeLast(note.toString(), ","));
            }

            invoiceVo.setInvoiceStatus("0");
            if(invoiceVo.getNotes()==null){
                invoiceVo.setNotes("签收失败");
            }else {
                invoiceVo.setNotes(invoiceVo.getNotes()+",签收失败");
            }
            if(invoiceVo.getQsDate() != null){ //将已签收的发票修改为未签收的时候签收时间置为null
                invoiceVo.setQsDate(null);
            }

            // 入库
            try {
                int row = this.saveInvoice(invoiceVo, id);
                if(1 != row) throw new Exception(" Save Invoice Data Failed !");
            } catch (Exception e) {
                invoiceVo.setNotes("重复扫描");
                e.printStackTrace();
//				this.invoiceScanService.delImg(invoiceVo.getScanId());//删除入库失败的发票
                return -3;
            }
            // 返回未签收
            return -2;
        }
        // 有底账，进行签收
        // 判断是否异常
        if(0 != record.getInvoiceStatus()){ // 异常,签收失败
            invoiceVo.setInvoiceStatus("0");
            invoiceVo.setNotes(note.append("异常发票").toString());
            // 入库
            try {
                int row = this.saveInvoice(invoiceVo, id);
                if(1 != row) throw new Exception(" Save Invoice Data Failed !");
            } catch (Exception e) {
                invoiceVo.setNotes("重复扫描");
                e.printStackTrace();
//				this.invoiceScanService.delImg(invoiceVo.getScanId());//删除入库失败的发票
                return -3;
            }
            // 返回未签收
            return -2;
        }

        ////////////////////
        // 正常,继续签收
        /*
         * 对各字段进行签收
         * 包括： 发票代码／发票号码开票日期／购方税号／销方税号／金额／税额／价税合计
         * 签收结果：若以上字段均成功，则签收成功；
         * 		   反之签收失败，并将所有错误字段记录在签收信息中
         */
        boolean isSuccess = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String	date2 = sdf.format(record.getInvoiceDate());
        //当没扫描到结果（undefined）或和发票表中的数据不一致时，都会签收失败
        if(invoiceVo.getInvoiceDate()==null||(invoiceVo.getInvoiceDate()!=null&&false == invoiceVo.getInvoiceDate().replaceAll("-", "").equals(date2.replaceAll("-", "")))){
            note.append("开票日期错误,");
            isSuccess = false;
        }
        if(invoiceVo.getGfName()==null||(invoiceVo.getGfName()!=null&&false == invoiceVo.getGfName().indexOf(record.getGfName())>=0)){
            note.append("购方名称错误,");
            isSuccess = false;
        }
        if(invoiceVo.getGfTaxNo()==null||(invoiceVo.getGfTaxNo()!=null&&false == invoiceVo.getGfTaxNo().equals(record.getGfTaxNo()))){
            note.append("购方税号错误,");
            isSuccess = false;
        }
        if(invoiceVo.getXfTaxNo()==null||(invoiceVo.getXfTaxNo()!=null&&false == invoiceVo.getXfTaxNo().equals(record.getXfTaxNo()))){ //
            note.append("销方税号错误,");
            isSuccess = false;
        }
//		List<String> taxNoCacheList = CacheStatic.getTaxNoCacheList();
//		if (taxNoCacheList.contains(invoiceVo.getXfTaxNo())) {
//			note.append("该销方税号只能开普票,");
//			isSuccess = false;
//		}
        if(invoiceVo.getInvoiceAmount()==null||(invoiceVo.getInvoiceAmount()!=null&& new BigDecimal(invoiceVo.getInvoiceAmount()).doubleValue()!=record.getInvoiceAmount())){
            note.append("金额错误,");
            isSuccess = false;
        }
        if(invoiceVo.getTaxAmount()==null||(invoiceVo.getTaxAmount()!=null&& new BigDecimal(invoiceVo.getTaxAmount()).doubleValue()!= record.getTaxAmount())){
            note.append("税额错误,");
            isSuccess = false;
        }
        if(invoiceVo.getTotalAmount()==null||(invoiceVo.getTotalAmount()!=null&&Double.parseDouble(invoiceVo.getTotalAmount())!=(record.getTotalAmount()))){
            note.append("价税合计错误,");
            isSuccess = false;
        }
        // 签收结束
        ////////////////////

        int row = 0;  // 处理结果
        // 判断签收结果，更新签收状态，设置签收信息
        if(false == isSuccess){  // 签收失败
            invoiceVo.setInvoiceStatus("0");
            invoiceVo.setNotes(note.deleteCharAt(note.lastIndexOf(",")).toString());
            // 入库
            try {
                row = this.saveInvoice(invoiceVo, id);
                if(1 != row) throw new Exception(" Save Invoice Data Failed !");
            } catch (Exception e) {
                invoiceVo.setNotes("重复扫描");
//				this.invoiceScanService.delImg(invoiceVo.getScanId());//删除入库失败的发票
                return -3;
            }
            return -2;
        }

        // 签收成功
        invoiceVo.setInvoiceStatus("1");

        invoiceVo.setNotes(note.append("签收成功").toString());
        //添加签收时间
        if(invoiceVo.getQsDate() == null) invoiceVo.setQsDate(new Date());
        // 入库
        try {
            row = this.saveInvoice(invoiceVo, id);
            if(1 != row) throw new Exception(" Save Invoice Data Failed !");
            //更新底账库签收状态
           scannerSignDao.updateRecordInvoiceState(invoiceVo.getUuid());
        } catch (Exception e) {
            invoiceVo.setNotes("重复扫描");

//			this.invoiceScanService.delImg(invoiceVo.getScanId());//删除入库失败的发票
            return -3;
        }
        return row;
    }
    
    /**
     * 使用电子底帐进行发票签收
     * @param invoiceVo
     * @param id
     * @return
     */
    @Override
    public int signUseRecord(SignedInvoiceVo invoiceVo){

        // 设置uuid
        invoiceVo.setUuid(new StringBuilder(invoiceVo.getInvoiceCode()).append(invoiceVo.getInvoiceNo()).toString());

        UserEntity sysUserEntity=(UserEntity) SecurityUtils.getSubject().getPrincipal();
        // 用户姓名
        invoiceVo.setUserName(sysUserEntity.getUsername());
        // 设置用户账号
        invoiceVo.setUserAccount(String.valueOf(sysUserEntity.getLoginname()));

        // 签收信息，存放签收操作结果
        StringBuilder note = new StringBuilder();

        // 正常,继续签收
        /*
         * 对各字段进行签收
         * 包括： 发票代码／发票号码开票日期／购方税号／销方税号／金额／税额／价税合计
         * 签收结果：若以上字段均成功，则签收成功；
         * 		   反之签收失败，并将所有错误字段记录在签收信息中
         */
       
        int row = 0;  // 处理结果
      
        // 签收成功
        invoiceVo.setInvoiceStatus("1");

        invoiceVo.setNotes(note.append("签收成功").toString());
        //添加签收时间
        if(invoiceVo.getQsDate() == null) invoiceVo.setQsDate(new Date());
        // 入库
        try {
            row = this.saveInvoiceOnly(invoiceVo);
            if(1 != row && 0 != row) throw new Exception(" Save Invoice Data Failed !");
            //更新底账库签收状态
           scannerSignDao.updateRecordInvoiceState(invoiceVo.getUuid());
        } catch (Exception e) {
//			this.invoiceScanService.delImg(invoiceVo.getScanId());//删除入库失败的发票
            return -3;
        }
        return row;
    }

    @Override
    public void saveImg(InvoiceImgSavePo savePo) {
        this.scannerSignDao.saveImg(schemaLabel,savePo);
    }


    protected int saveInvoice(SignedInvoiceVo invoiceVo, Long id) {
        // 回写底账签收状态

        int row = 0;
        // 扫描数据入库
        if(null != id){
            try {
                deleteInvoice(id);
                row =1;
            }catch (Exception e){
                e.printStackTrace();
            }
            if(1 != row){
                return row;
            }
        }
        InvoiceSavePo savePo = vo2SavePo(invoiceVo);
        if(scannerSignDao.selectByUuid(savePo.getUuid())==null){
            row = this.scannerSignDao.saveInvoice(savePo);
        }
        if(0 < row){
            invoiceVo.setId(savePo.getId());
            row = 1;
        }
        return row;
    }
    
    protected int saveInvoiceOnly(SignedInvoiceVo invoiceVo) {
        // 回写底账签收状态

        int row = 0;
        // 扫描数据入库
        InvoiceSavePo savePo = vo2SavePo(invoiceVo);
        if(scannerSignDao.selectByUuid(savePo.getUuid())==null){
            row = this.scannerSignDao.saveInvoice(savePo); 
        }
        
        invoiceVo.setId(savePo.getId());
        return row;
    }

    public int deleteInvoice(Long id){
        return this.scannerSignDao.deleteInvoice(id);
    }
    protected InvoiceSavePo vo2SavePo(SignedInvoiceVo vo) {
        InvoiceSavePo po = new InvoiceSavePo();
        po.setInsideOutside(vo.getInsideOutside());
        po.setNotes(vo.getNotes());
        po.setInvoiceCode(vo.getInvoiceCode());
        po.setInvoiceNo(vo.getInvoiceNo());
        po.setGfTaxNo(vo.getGfTaxNo());
        po.setGfName(vo.getGfName());
        if(!"".equals(vo.getInvoiceAmount()) && vo.getInvoiceAmount() != null){
            po.setInvoiceAmount(new BigDecimal(vo.getInvoiceAmount()));
        }
        if(!"".equals(vo.getTaxAmount()) && vo.getTaxAmount() != null){
            po.setTaxAmount(new BigDecimal(vo.getTaxAmount()));
        }
        if(!"".equals(vo.getTotalAmount()) && vo.getTotalAmount() != null){
            po.setTotalAmount(new BigDecimal(vo.getTotalAmount()));
        }
        po.setXfTaxNo(vo.getXfTaxNo());
        po.setCheckCode(vo.getCheckCode());
        
        Timestamp  ts=new Timestamp(new Date().getTime());
        po.setCreateDate(ts.toString());
       
        
        po.setXfName(vo.getXfName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            po.setInvoiceDate(sdf.parse(vo.getInvoiceDate().replaceAll("-", "")));
        } catch (Exception e) {
            po.setInvoiceDate(null);
        }
        po.setInvoiceSerialNo(vo.getInvoiceSerialNo());
        //po.setCreateDate(vo.getScandate());
        po.setInvoiceStatus(vo.getInvoiceStatus());
        po.setInvoiceType(vo.getInvoiceType());
        po.setUserName(vo.getUserName());
        po.setUuid(vo.getUuid());
        po.setQsDate(vo.getQsDate());
        po.setScanId(vo.getScanId());
        po.setUserAccount(vo.getUserAccount());
        po.setValid("1");
        
        return po;
    }

    public  RecordInvoiceQueryByCodeAndNoVo
    queryRecordInvoiceByCodeAndNo(RecordInvoiceQueryByCodeAndNoPo param) {
        if(null == param){
            return null;
        }
        List<RecordInvoiceQueryByCodeAndNoVo> vos ;
        vos = scannerSignDao.queryByCodeAndNo(param);
        return vos.size() > 0 ? vos.get(0) : null;
    }

    protected RecordInvoiceQueryByCodeAndNoPo _2RecordSignPo(SignedInvoiceVo invoiceVo){
        RecordInvoiceQueryByCodeAndNoPo signPo = new RecordInvoiceQueryByCodeAndNoPo();
        signPo.setInvoiceCode(invoiceVo.getInvoiceCode());
        signPo.setInvoiceNo(invoiceVo.getInvoiceNo());
        signPo.setDate(invoiceVo.getQsDate());
        return signPo;
    }
    private static String[] fpdmlist={"144031539110","131001570151","133011501118","111001571071"};
    /**
     * 根据发票代码获取发票类型
     * @param fpdm
     * @return
     */
    public static String getFplx(String fpdm) {
        String fplx = "";
        if (fpdm.trim().length() == 12) {
            String fplxflag = fpdm.substring(7, 8);
            for (int i = 0; i < fpdmlist.length; i++) {
                if (fpdm.equals(fpdmlist[i])) {
                    fplx = "10";
                    break;
                }
            }
            if ("0".equals(fpdm.substring(0, 1)) && "11".equals(fpdm.substring(10, 12))) {
                fplx = "10";
            }
            if ("0".equals(fpdm.substring(0, 1)) && "12".equals(fpdm.substring(10, 12))) {
                fplx = "14";
            }
            if ("0".equals(fpdm.substring(0, 1)) && ("06".equals(fpdm.substring(10, 12)) || "07".equals(fpdm.substring(10, 12)))) {
                //判断是否为卷式发票  第1位为0且第11-12位为06或07
                fplx = "11";
            }
            if ("2".equals(fplxflag) && !"0".equals(fpdm.substring(0, 1))) {
                fplx = "03";
            }
            if ("0".equals(fpdm.substring(0, 1)) && ("04".equals(fpdm.substring(10, 12)) || "05".equals(fpdm.substring(10, 12)))) {
                fplx = "04";
            }
        } else if (fpdm.trim().length() == 10) {
            String fplxflag = fpdm.substring(7, 8);
            if ("1".equals(fplxflag) || "5".equals(fplxflag)) {
                fplx = "01";
            } else if ("6".equals(fplxflag) || "3".equals(fplxflag)) {
                fplx = "04";
            } else if ("7".equals(fplxflag) || "2".equals(fplxflag)) {
                fplx = "02";
            }
        }
        return fplx;
    }


    /**
     * 通过查验签收回来的数据获取底账表实体信息
     *
     * @param responseInvoice 查验签收回来的数据
     * @return 底账表数据信息
     */
    private com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice getRRecordInvoiceFromResponse(ResponseInvoice responseInvoice) {
        com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice recordInvoice = null;
        if (responseInvoice != null && CHECK_INVOICE_SUCCESS_CODE.equals(responseInvoice.getResultCode())) {
            //如果查验成功
            recordInvoice = new com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice();
            //查验返回结果信息
            recordInvoice.setResultCode(responseInvoice.getResultCode());
            recordInvoice.setResultTip(responseInvoice.getResultTip());

            recordInvoice.setInvoiceCode(responseInvoice.getInvoiceCode());
            recordInvoice.setInvoiceNo(responseInvoice.getInvoiceNo());
            recordInvoice.setInvoiceType(responseInvoice.getInvoiceType());
            recordInvoice.setInvoiceDate(DateTimeHelper.parseDate(responseInvoice.getInvoiceDate()));
            recordInvoice.setGfTaxNo(responseInvoice.getBuyerTaxNo());
            recordInvoice.setGfName(responseInvoice.getBuyerName());
            recordInvoice.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
            recordInvoice.setGfBankAndNo(responseInvoice.getBuyerAccount());
            recordInvoice.setXfTaxNo(responseInvoice.getSalerTaxNo());
            recordInvoice.setXfName(responseInvoice.getSalerName());
            recordInvoice.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
            recordInvoice.setXfBankAndNo(responseInvoice.getSalerAccount());
            recordInvoice.setInvoiceStatus(CHECK_BACK_INVOICE_Y.equals(responseInvoice.getIsCancelled()) ? INVOICE_STATUS_TWO : INVOICE_STATUS_ZERO);
            if (!"".equals(responseInvoice.getInvoiceAmount()) && responseInvoice.getInvoiceAmount() != null && !(" ").equals(responseInvoice.getInvoiceAmount())) {
                recordInvoice.setInvoiceAmount(new BigDecimal(responseInvoice.getInvoiceAmount()));
            }
            if (!"".equals(responseInvoice.getTaxAmount()) && responseInvoice.getTaxAmount() != null && !(" ").equals(responseInvoice.getTaxAmount())) {
                recordInvoice.setTaxAmount(new BigDecimal(responseInvoice.getTaxAmount()));
            }
            if (!"".equals(responseInvoice.getTotalAmount()) && responseInvoice.getTotalAmount() != null && !(" ").equals(responseInvoice.getTotalAmount())) {
                recordInvoice.setTotalAmount(new BigDecimal(responseInvoice.getTotalAmount()));
            }
            recordInvoice.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            recordInvoice.setRemark(responseInvoice.getRemark());
            recordInvoice.setCheckCode(responseInvoice.getCheckCode());
            recordInvoice.setDetailYesorno(responseInvoice.getDetailList().isEmpty() ? INVOICE_DETAIL_NO : INVOICE_DETAIL_YES);
            recordInvoice.setDetailList(this.getInvoiceDetails(responseInvoice.getDetailList(),
                    responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo(), responseInvoice.getInvoiceCode(),
                    responseInvoice.getInvoiceNo()));
            recordInvoice.setValid(INVOICE_VALID_ONE);
            recordInvoice.setSourceSystem(INVOICE_SOURCE_SYSTEM);
            recordInvoice.setQsType("1");
        } else {
            //如果查验失败
            recordInvoice = new com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice();
            //查验返回结果信息
            recordInvoice.setResultCode(responseInvoice != null ? responseInvoice.getResultCode() : CHECK_INVOICE_BACK_FAIL_CODE);
            recordInvoice.setResultTip(responseInvoice != null ? responseInvoice.getResultTip() : CHECK_RESULT_TIP_ERROR);
        }

        return recordInvoice;
    }

    /**
     * 获取查验签收回来的商品明细
     *
     * @param list 查验回来的数据
     * @return 整理好的商品明细信息
     */
    private List<RecordInvoiceDetail> getInvoiceDetails(List<InvoiceDetail> list, String uuid, String invoiceCode, String invoiceNo) {
        List<RecordInvoiceDetail> details = Lists.newArrayList();
        RecordInvoiceDetail invoiceDetail;
        for (InvoiceDetail detail : list) {
            invoiceDetail = new RecordInvoiceDetail();
            invoiceDetail.setUuid(uuid);
            invoiceDetail.setInvoiceCode(invoiceCode);
            invoiceDetail.setInvoiceNo(invoiceNo);
            invoiceDetail.setDetailNo(detail.getDetailNo());
            invoiceDetail.setGoodsName(detail.getGoodsName());
            invoiceDetail.setModel(detail.getSpecificationModel());
            invoiceDetail.setUnit(detail.getUnit());
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setUnitPrice(detail.getUnitPrice());
            invoiceDetail.setDetailAmount(detail.getDetailAmount());
            invoiceDetail.setTaxRate(detail.getTaxRate());
            invoiceDetail.setTaxAmount(detail.getTaxAmount());
            invoiceDetail.setCph(detail.getCph());
            invoiceDetail.setLx(detail.getLx());
            invoiceDetail.setTxrqq(detail.getTxrqq());
            invoiceDetail.setTxrqz(detail.getTxrqz());
            details.add(invoiceDetail);
        }
        return details;
    }

    /**
     * 根据扫描结果选择性插入到底账表、扫描表中
     * @return
     * @throws Exception
     */

    @Override
    public InvoiceScanResultVo insertFromScan(List<SignedInvoiceVo> signedInvoiceVos , Long id){
        InvoiceScanResultVo resultVo = new InvoiceScanResultVo();
        if(null == signedInvoiceVos || signedInvoiceVos.size() < 0){
            return null;
        }
        List<com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice> recordInvoiceList=new ArrayList<>();
        //查验
        for (SignedInvoiceVo signedInvoiceVo: signedInvoiceVos) {
            resultVo.setScanNum(resultVo.getScanNum() + 1); // 扫描票数量+1
            InvoiceSavePo invoiceSavePo = scannerSignDao.selectByUuid(signedInvoiceVo.getUuid());
            if(invoiceSavePo!=null&&id==null) {
                signedInvoiceVo.setNotes("重复扫描");
                signedInvoiceVo.setInvoiceStatus("0");
                resultVo.setRkFailNum(resultVo.getRkFailNum()+1);
            }else{
                //判断识别是否有误
                if(null==signedInvoiceVo.getNotes()) {
                    //判断重复扫描

                        //获取发票类型
                        String fplx = getFplx(signedInvoiceVo.getInvoiceCode());
                        if (fplx.equals("04")) {
                            //普通电子发票查验
                            //整理带查验签收发票的请求数据
                            RequestData requestData = new RequestData();
                            requestData.setCheckCode(signedInvoiceVo.getCheckCode());
                            requestData.setInvoiceAmount(signedInvoiceVo.getInvoiceAmount() != null ?
                                    signedInvoiceVo.getInvoiceAmount() : "");
                            requestData.setInvoiceNo(signedInvoiceVo.getInvoiceNo());
                            requestData.setInvoiceCode(signedInvoiceVo.getInvoiceCode());

                            requestData.setInvoiceDate(signedInvoiceVo.getInvoiceDate());
                            requestData.setInvoiceType(signedInvoiceVo.getInvoiceType() != null ? signedInvoiceVo.getInvoiceType() : "04");
                            requestData.setBuyerTaxNo(signedInvoiceVo.getGfTaxNo());
                            //发送数据差异签收
                            ResponseInvoice responseInvoice = null;//invoiceCheckService.sendRequest(requestData);
                            com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice recordInvoice = getRRecordInvoiceFromResponse(responseInvoice);
                            if (CHECK_INVOICE_SUCCESS_CODE.equals(recordInvoice.getResultCode())) {
                                if ("".equals(recordInvoice.getGfTaxNo()) || recordInvoice.getGfTaxNo() == null || (" ").equals(recordInvoice.getGfTaxNo())) {
                                    recordInvoice.setGfTaxNo(DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);
                                }
                                if (CHECK_INVOICE_FAIL_CODE.equals(recordInvoice.getInvoiceStatus())) {
                                    //若发票作废在更新保存的信息
                                    signedInvoiceVo.setNotes("签收失败");
                                    signedInvoiceVo.setInvoiceStatus("0");
                                    recordInvoice.setQsStatus(INVOICE_QS_STATUS_FAIL_ZERO);
                                    resultVo.setQsFailNum(resultVo.getQsFailNum()+1);
                                } else {
                                    signedInvoiceVo.setNotes("签收成功");
                                    signedInvoiceVo.setInvoiceStatus("1");
                                    signedInvoiceVo.setGfTaxNo(recordInvoice.getGfTaxNo());
                                    signedInvoiceVo.setXfTaxNo(recordInvoice.getXfTaxNo());
                                    signedInvoiceVo.setTotalAmount(recordInvoice.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                                    signedInvoiceVo.setTaxAmount(recordInvoice.getTaxAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                                    signedInvoiceVo.setInvoiceAmount(recordInvoice.getInvoiceAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                                    signedInvoiceVo.setInvoiceDate(DateTimeHelper.formatDat(recordInvoice.getInvoiceDate()));
                                    recordInvoice.setQsStatus("1");
                                    recordInvoice.setRemark("签收成功");
                                    resultVo.setSuccessNum(resultVo.getSuccessNum()+1);
                                    //保存底账信息
                                    //this.saveRecordInvoice( recordInvoice);
                                    scannerSignDao.saveRecordInvoice(recordInvoice);
                                    //保存商品明细信息
                                    if (recordInvoice.getDetailList() != null && !recordInvoice.getDetailList().isEmpty()) {
                                        scannerSignDao.saveRecordInvoiceDetail(recordInvoice.getDetailList());
                                    }
                                }
                                //保存扫描信息到扫描表
                                try {
                                    saveInvoice(signedInvoiceVo, id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //如果返回的底账信息为空，则表示查验失败
                                signedInvoiceVo.setNotes("签收失败");
                                signedInvoiceVo.setInvoiceStatus("0");
                                resultVo.setQsFailNum(resultVo.getQsFailNum()+1);
                                //保存扫描信息到扫描表
                                try {
                                    saveInvoice(signedInvoiceVo, id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                }else{
                    if(!signedInvoiceVo.getNotes().equals("选择类型与实际扫描发票类型不一致")) {
                        //入库
                        signedInvoiceVo.setInvoiceStatus("0");
                        resultVo.setQsFailNum(resultVo.getQsFailNum() + 1);
                        //保存扫描信息到扫描表
                        try {
                            saveInvoice(signedInvoiceVo, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        resultVo.setQsFailNum(resultVo.getQsFailNum() + 1);
                        signedInvoiceVo.setInvoiceStatus("0");
                    }
                }
            }


            //修改返回值
            if (signedInvoiceVo.getInvoiceStatus().equals("1")) {
                signedInvoiceVo.setInvoiceStatus("已签收");
            } else if (signedInvoiceVo.getInvoiceStatus().equals("0")) {
                signedInvoiceVo.setInvoiceStatus("未签收");
            }

        }
        resultVo.setInvoiceList(signedInvoiceVos);

        return resultVo;

    }

    private List<RecordInvoiceCreateSignPo> adapterToRecordInvoiceCreateSignPo(
            List<SignedInvoiceVo> invoices) {
        List<RecordInvoiceCreateSignPo> res = new ArrayList<RecordInvoiceCreateSignPo>();
        RecordInvoiceCreateSignPo po;
        for (SignedInvoiceVo o : invoices) {
            po = new RecordInvoiceCreateSignPo();
            po.setInvoiceType(o.getInvoiceType());
            po.setInvoiceCode(o.getInvoiceCode());
            po.setInvoiceNo(o.getInvoiceNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                po.setInvoiceDate(sdf.parse(o.getInvoiceDate().replaceAll("-", "")));
            } catch (Exception e) {
                po.setInvoiceDate(null);
            }
            po.setGfTaxNo(o.getGfTaxNo());
            po.setGfName(o.getGfName());
            po.setXfTaxNo(o.getXfTaxNo());
            if(!"".equals(o.getInvoiceAmount()) && o.getInvoiceAmount() != null){
                po.setInvoiceAmount(new BigDecimal(o.getInvoiceAmount()));
            }
            if(!"".equals(o.getTaxAmount()) && o.getTaxAmount() != null){
                po.setTaxAmount(new BigDecimal(o.getTaxAmount()));
            }
            if(!"".equals(o.getTotalAmount()) && o.getTotalAmount() != null){
                po.setTotalAmount(new BigDecimal(o.getTotalAmount()));
            }
            po.setUuid(o.getUuid());
            po.setQsDate(o.getQsDate());
            po.setScpz(o.getScpz());
            po.setCreateDate(o.getScandate());
            po.setScanPath(o.getScanPath());
            po.setNotes(o.getNotes());
            res.add(po);
        }
        return res;
    }

    public void delImg(String scanId) {
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            // 删除sftp文件服务器上的zip压缩图片
            InvoiceImgQueryVo image = scannerSignDao.getImg(schemaLabel,scanId);
            if (image != null) {
                try {
                    imageHandler.deleteRemote(image.getImagePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scannerSignDao.deleteInvoiceImg(schemaLabel,image.getScanId());// 删除图片路径
            }
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        } catch (JSchException e1) {
            e1.printStackTrace();
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }
    }
    @Override
    public void invoiceDelete(String uuId)  {
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
        } catch (JSchException e) {
            e.printStackTrace();
        }
        InvoiceSavePo po = scannerSignDao.selectByInvoice(uuId);// 查询扫描表信息
        //查询扫描表
        //获取发票类型

        //专票删除扫描表  修改底账库     普票全删
        //获取签收状态
        //已签收  入库删除表
        if(null!=po){
            if(po.getInvoiceStatus().equals("1")){
                if(scannerSignDao.findDelByUuid(uuId)!=null){
                    //删除
                    scannerSignDao.delDel(uuId);
                }
                //入库
                scannerSignDao.savedel(uuId);//入库删除表
            }
            if(po.getInvoiceType().equals("01")){//专票
                scannerSignDao.invoiceDelete(uuId);// 删除扫描表发票
                scannerSignDao.updateRecordInvoiceHandleState(uuId);// 已签收的发票删除后将底账标的签收时间,扫描路径id,扫描名称制空
            }else if(po.getInvoiceType().equals("04")){//普票
                scannerSignDao.invoiceDelete(uuId);// 删除扫描表发票
                scannerSignDao.deleteRecordInvoiceById(uuId);// 删除底账表发票
            }

            InvoiceImgQueryVo image = scannerSignDao.getImg(schemaLabel,po.getScanId());
            if (image != null) {
                try {
                    imageHandler.deleteRemote(image.getImagePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scannerSignDao.deleteInvoiceImg(schemaLabel,image.getScanId());// 删除图片路径
            }

        }




        if (imageHandler != null) {
            imageHandler.closeChannel();
        }


    }
}
