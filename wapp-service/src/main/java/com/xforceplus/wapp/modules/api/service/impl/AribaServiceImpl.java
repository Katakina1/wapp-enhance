package com.xforceplus.wapp.modules.api.service.impl;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.FileTypeUtil;
import com.xforceplus.wapp.common.utils.ReturnInfoEnum;
import com.xforceplus.wapp.common.utils.ScanEditHttpClient;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.api.dao.AribaDao;
import com.xforceplus.wapp.modules.api.entity.AribaCheckEntity;
import com.xforceplus.wapp.modules.api.entity.AribaCheckReturn;
import com.xforceplus.wapp.modules.api.service.AribaService;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import com.xforceplus.wapp.modules.einvoice.util.*;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;

import com.ele.parse.entity.FPEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xforceplus.wapp.modules.Constant.DETAIL_YES_OR_NO;
import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * TODO
 *
 * @atuthor wyman
 * @date 2020-05-06 11:38
 **/
@Service("aribaService")
public class AribaServiceImpl implements AribaService {
    private final static Logger LOGGER = getLogger(AribaServiceImpl.class);
    @Autowired
    private AribaDao aribaDao;

    @Autowired
    private SystemConfig systemConfig;

    @Value("${basicAuth.loginName}")
    private String loginNmae;

    @Value("${basicAuth.userName}")
    private String userName;

    @Value("${basicAuth.userId}")
    private String userId;


    @Value("${scanElUploadUrl}")
    private String scanElUploadUrl;

    @Autowired
    private EinvoiceUploadService einvoiceUploadService;

    @Override
    public void saveRequest(String s, String auth, String sessionAuth, String response) {
        aribaDao.saveRequest(s, auth, sessionAuth, response);
    }

    @Value("${aribaCheck}")
    private String aribaCheck;




    private Date date;

    @Override
    public AribaCheckReturn check(AribaCheckEntity aribaCheckEntity) {
        final RequestData requestData = new RequestData();
        AribaCheckReturn aribaCheckReturn = new AribaCheckReturn();
        List<AribaCheckReturn.ResultBean> resultBeaList = new ArrayList<>();
        for (AribaCheckEntity.FapiaosBean fapiaosBean : aribaCheckEntity.getFapiaos()) {
            //校验参数

            List error = checkValidator(fapiaosBean);

            AribaCheckReturn.ResultBean resultBean = new AribaCheckReturn.ResultBean();
            BeanUtils.copyProperties(fapiaosBean, resultBean);
            resultBean.setFapiaoNetAmount("");
            resultBean.setFapiaoTaxAmount("");
            resultBean.setFapiaoTotalAmount("");
            resultBean.setSupplierTaxId("");
            resultBean.setWalmartTaxId("");
            //判断是否需要进行验证
            if (!error.isEmpty()) {
                resultBean.setError(error);
            } else {
                //专票查底账，普票查验插底账，但是都要保存jv 、6位供应商号、10位供应商号
                if (fapiaosBean.getInvoiceTypeCode().equals("01")) {
                    //专票查底账
                    Map map = new HashMap();
                    map.put("invoiceCode", fapiaosBean.getFapiaoCode());
                    map.put("invoiceNo", fapiaosBean.getFapiaoNumber());
                    List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
                    ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity = new ComprehensiveInvoiceQueryEntity();
                    if (comprehensiveInvoiceQueryEntities.isEmpty()) {
                        error.add(901);
                        resultBean.setError(error);
                    } else {
                        comprehensiveInvoiceQueryEntity = comprehensiveInvoiceQueryEntities.get(0);
                        //判断是否被其他业务类型使用
                        if ("8".equals(comprehensiveInvoiceQueryEntity.getFlowType()) || StringUtils.isBlank(comprehensiveInvoiceQueryEntity.getFlowType())) {
                            //校验4要素 ，开票日期与金额
                            BigDecimal a = new BigDecimal(comprehensiveInvoiceQueryEntity.getInvoiceAmount() + "").setScale(2, BigDecimal.ROUND_DOWN);
                            BigDecimal b = new BigDecimal(fapiaosBean.getFapiaoNetAmount()).setScale(2, BigDecimal.ROUND_DOWN);
                            System.out.println("----------------------------------------");
                            System.out.println(a);
                            System.out.println(b);
                            if (a.compareTo(b) != 0) {
                                error.add(903);
                            }
                            try {
                                if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(comprehensiveInvoiceQueryEntity.getInvoiceDate()).compareTo(new SimpleDateFormat("MM/dd/yyyy").parse(fapiaosBean.getFapiaoDate())) != 0) {
                                    error.add(902);
                                }
                            } catch (Exception e) {
                                error.add(902);
                            }
                            if (!"0".equals(comprehensiveInvoiceQueryEntity.getInvoiceStatus())) {
                                error.add(924);
                            }
                            if (!error.isEmpty()) {
                                resultBean.setInvoiceType("VAT Special");
                                resultBean.setError(error);
                            } else {
                                //成功，赋值
                                resultBean.setFapiaoNetAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getInvoiceAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setFapiaoTaxAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTaxAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setFapiaoTotalAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTotalAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setSupplierTaxId(comprehensiveInvoiceQueryEntity.getXfTaxNo());
                                resultBean.setWalmartTaxId(comprehensiveInvoiceQueryEntity.getGfTaxNo());
                                resultBean.setInvoiceType("VAT Special");
                                resultBean.setError(error);
                                //插值
                                aribaDao.checkSave(fapiaosBean.getJvCode(), fapiaosBean.getLegacySupplierNumber(), fapiaosBean.getSupplierNumber(), comprehensiveInvoiceQueryEntity.getId(), fapiaosBean.getCompanyCode());
                        }
                        } else {
                            error.add(925);
                            resultBean.setError(error);
                        }
                    }
                } else {
                    final String invoiceDateQuery = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    Map map = new HashMap();
                    map.put("invoiceCode", fapiaosBean.getFapiaoCode());
                    map.put("invoiceNo", fapiaosBean.getFapiaoNumber());
                    map.put("checkCode", fapiaosBean.getValidationCode());
                    map.put("invoiceDateQuery", invoiceDateQuery);
                    List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
                    ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity = new ComprehensiveInvoiceQueryEntity();

                    if (comprehensiveInvoiceQueryEntities.isEmpty()) {} else {
                        comprehensiveInvoiceQueryEntity = comprehensiveInvoiceQueryEntities.get(0);
                        if ("8".equals(comprehensiveInvoiceQueryEntity.getFlowType())) {
                            resultBean.setFapiaoNetAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getInvoiceAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setFapiaoTaxAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTaxAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setFapiaoTotalAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTotalAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setSupplierTaxId(comprehensiveInvoiceQueryEntity.getXfTaxNo());
                            resultBean.setWalmartTaxId(comprehensiveInvoiceQueryEntity.getGfTaxNo());
                            resultBean.setInvoiceType("VAT Normal");
                            resultBean.setError(error);
                            //插值
                            aribaDao.checkSave(fapiaosBean.getJvCode(), fapiaosBean.getLegacySupplierNumber(), fapiaosBean.getSupplierNumber(), comprehensiveInvoiceQueryEntity.getId(), fapiaosBean.getCompanyCode());
                        } else {
                            error.add(925);
                            resultBean.setError(error);
                        }
                    }
                }
            }
            resultBeaList.add(resultBean);
        }
        aribaCheckReturn.setResult(resultBeaList);
        return aribaCheckReturn;
    }

    @Override
    public AribaCheckReturn signInMark(AribaCheckEntity aribaCheckEntity) {
        AribaCheckReturn aribaCheckReturn = new AribaCheckReturn();
        List<AribaCheckReturn.ResultBean> resultBeaList = new ArrayList<>();
        for (AribaCheckEntity.FapiaosBean fapiaosBean : aribaCheckEntity.getFapiaos()) {
            //校验参数
            List error = signInMarkValidator(fapiaosBean);
            AribaCheckReturn.ResultBean resultBean = new AribaCheckReturn.ResultBean();
            BeanUtils.copyProperties(fapiaosBean, resultBean);
            resultBean.setValidationCode(fapiaosBean.getValidationCode());
            if (!error.isEmpty()) {
                resultBean.setError(error);
            } else {
                Map map = new HashMap();
                map.put("invoiceCode", fapiaosBean.getFapiaoCode());
                map.put("invoiceNo", fapiaosBean.getFapiaoNumber());
                map.put("flowType", "8");
                List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
                if (comprehensiveInvoiceQueryEntities.isEmpty()) {
                    error.add(901);
                    resultBean.setError(error);
                } else {
                    ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity = comprehensiveInvoiceQueryEntities.get(0);
                    if (!"0".equals(comprehensiveInvoiceQueryEntity.getInvoiceStatus())) {
                        error.add(924);
                        resultBean.setError(error);
                    } else {
                        resultBean.setError(error);
                        int signInMarkQuery = aribaDao.signInMarkQuery(fapiaosBean.getFapiaoCode(), fapiaosBean.getFapiaoNumber());
                        if (signInMarkQuery == 0) {
                            aribaDao.signInMarkInsert(fapiaosBean.getFapiaoCode(), fapiaosBean.getFapiaoNumber());
                        }
                        aribaDao.signInMarkSave(fapiaosBean.getCustomField1(),fapiaosBean.getCustomField2(),fapiaosBean.getFapiaoCode(), fapiaosBean.getFapiaoNumber());
                    }
                }
            }
            if (resultBean.getError().isEmpty()) {
                resultBean.setStatus("OK");
            } else {
                resultBean.setStatus("ERROR");
            }
            resultBeaList.add(resultBean);

        }
        aribaCheckReturn.setResult(resultBeaList);
        return aribaCheckReturn;
    }


    @Override
    public AribaCheckReturn auth(AribaCheckEntity aribaCheckEntity) {
        AribaCheckReturn aribaCheckReturn = new AribaCheckReturn();
        List<AribaCheckReturn.ResultBean> resultBeaList = new ArrayList<>();
        for (AribaCheckEntity.FapiaosBean fapiaosBean : aribaCheckEntity.getFapiaos()) {
            //校验参数
            List error = authValidatorFapiaosBean(fapiaosBean);
            Map map = new HashMap();
            map.put("invoiceCode", fapiaosBean.getFapiaoCode());
            map.put("invoiceNo", fapiaosBean.getFapiaoNumber());
            map.put("flowType", "8");
            List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
            if (comprehensiveInvoiceQueryEntities.isEmpty()) {
                error.add(901);
            }
            AribaCheckReturn.ResultBean resultBean = new AribaCheckReturn.ResultBean();
            BeanUtils.copyProperties(fapiaosBean, resultBean);

            List<AribaCheckReturn.ResultBean.ItemsBean> resultItemsBeanList = new ArrayList<>();
            boolean authBoolean=true;
            for (AribaCheckEntity.FapiaosBean.ItemsBean itemsBean : fapiaosBean.getItems()) {
                error.addAll(authValidatorItemsBean(itemsBean));
                AribaCheckReturn.ResultBean.ItemsBean resultItemsBean = new AribaCheckReturn.ResultBean.ItemsBean();
                BeanUtils.copyProperties(itemsBean, resultItemsBean);
                if (!error.isEmpty()) {
                    resultItemsBean.setError(error);
                    resultItemsBean.setStatus("ERROR");
                    authBoolean=false;
                } else {
                    resultItemsBean.setError(error);
                    resultItemsBean.setStatus("OK");

                    int signInMarkQuery = aribaDao.authQuery(fapiaosBean, itemsBean);
                    if (signInMarkQuery != 0) {
                        aribaDao.deleteAuth(fapiaosBean, itemsBean);
                    }
                    if ("VAT Special".equals(fapiaosBean.getInvoiceType())) {
                        fapiaosBean.setInvoiceType("01");
                    } else if ("eFapiao-VAT Special".equals(fapiaosBean.getInvoiceType())) {
                        fapiaosBean.setInvoiceType("08");
                    }
                    aribaDao.authInsert(fapiaosBean, itemsBean);

                }
                resultItemsBeanList.add(resultItemsBean);
            }
            if(authBoolean) {
                aribaDao.updateRecordInvoiceConfirmStatus(fapiaosBean);
            }
            resultBean.setItems(resultItemsBeanList);
            resultBeaList.add(resultBean);
        }
        aribaCheckReturn.setResult(resultBeaList);
        return aribaCheckReturn;
    }

    public static void main(String[] args) {
        //文件下载  928
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet method = new HttpGet("下载地址url");
        try {
            HttpResponse result = httpClient.execute(method);
            HttpEntity entity = result.getEntity();
            //设置本地保存的文件
            File storeFile = new File("D:/123.pdf");
            FileOutputStream output = new FileOutputStream(storeFile);
            InputStream input = entity.getContent();
            byte b[] = new byte[1024];
            int j = 0;
            while( (j = input.read(b))!=-1){
                output.write(b,0,j);
            }
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }


    @Override
    public AribaCheckReturn upload(AribaCheckEntity aribaCheckEntity) {
        SimpleDateFormat formatA = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat formatB = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatC = new SimpleDateFormat("yyyyMMdd");
        final RequestData requestData = new RequestData();
        final ExportEntity exportEntity = new ExportEntity();

        exportEntity.setSchemaLabel(null);
        //人员id
//                exportEntity.setUserId();
        //帐号
        exportEntity.setUserAccount(loginNmae);
        //人名
        exportEntity.setUserName(userName);
        AribaCheckReturn aribaCheckReturn = new AribaCheckReturn();
        List<AribaCheckReturn.ResultBean> resultBeaList = new ArrayList<>();
        for (AribaCheckEntity.FapiaosBean fapiaosBean : aribaCheckEntity.getFapiaos()) {
            String scanId=UUID.randomUUID().toString().replace("-", "");
//            String scanId ="86776513";
            //校验参数
            List error = uploadValidator(fapiaosBean);
            AribaCheckReturn.ResultBean resultBean = new AribaCheckReturn.ResultBean();
            BeanUtils.copyProperties(fapiaosBean, resultBean);
            if (!error.isEmpty()) {
                resultBean.setError(error);
                resultBean.setSupplierNumber("");
                resultBean.setCompanyCode("");
                resultBean.setFapiaoNetAmount("");
                resultBean.setFapiaoTaxAmount("");
                resultBean.setFapiaoTotalAmount("");
                resultBean.setSupplierTaxId("");
                resultBean.setFapiaoCode("");
                resultBean.setFapiaoNumber("");
                resultBean.setWalmartTaxId("");
                resultBean.setValidationCode("");
                resultBean.setInvoiceDate("");
                resultBean.setFapiaoCurrency("");
                resultBeaList.add(resultBean);
                continue;
            } else {
                //文件下载  928
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();

                JSONObject jsonResult = null;

                HttpGet method = new HttpGet(fapiaosBean.getAttachmentLink());

                byte[] filByte = null;
                String fileType = null;
                try {
                    HttpResponse result = httpClient.execute(method);
                    HttpEntity entityResponse = result.getEntity();
                    filByte=FileTypeUtil.readInputStream(entityResponse.getContent());
                    if (filByte == null) {
                        throw new Exception("文件获取失败，get.getResponseBody()后未byte为null");
                    } else {
                        fileType = FileTypeUtil.getFileTypeByStream(filByte);
                        if (StringUtils.isBlank(fileType)) {
                            fileType = "ofd";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //下载失败
                    error.add(927);
                    //后续保存工作，然后结束当前循环
                    resultBean.setError(error);
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setFapiaoNetAmount("");
                    resultBean.setFapiaoTaxAmount("");
                    resultBean.setFapiaoTotalAmount("");
                    resultBean.setSupplierTaxId("");
                    resultBean.setFapiaoCode("");
                    resultBean.setFapiaoNumber("");
                    resultBean.setWalmartTaxId("");
                    resultBean.setValidationCode("");
                    resultBean.setInvoiceDate("");
                    resultBean.setFapiaoCurrency("");
                    resultBeaList.add(resultBean);
                    continue;
                } finally {
                    httpClient.getConnectionManager().shutdown();
                }
                //解析 926
                Map uploadFileParsingMap = uploadFileParsing(fileType, filByte,scanId);
                FPEntity fp = null;
                ElectronInvoiceEntity electronInvoiceEntity = null;
                if ("ok".equals(uploadFileParsingMap.get("code"))) {
                    fp = (FPEntity) uploadFileParsingMap.get("data");
                    //扫描图片唯一识别id
//                    String scanId = UUID.randomUUID().toString().replace("-", "");
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUsername(userName);
                    userEntity.setLoginname(loginNmae);
                    electronInvoiceEntity = getElectronInvoiceToSave(fp, scanId, userEntity);
                } else {
                    error.add(926);
                    resultBean.setError(error);
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setFapiaoNetAmount("");
                    resultBean.setFapiaoTaxAmount("");
                    resultBean.setFapiaoTotalAmount("");
                    resultBean.setSupplierTaxId("");
                    resultBean.setFapiaoCode("");
                    resultBean.setFapiaoNumber("");
                    resultBean.setWalmartTaxId("");
                    resultBean.setValidationCode("");
                    resultBean.setInvoiceDate("");
                    resultBean.setFapiaoCurrency("");
                    resultBeaList.add(resultBean);
                    continue;
                }
                //发票验证发票类型验证911
                if (!CommonUtil.getFplx(electronInvoiceEntity.getInvoiceCode()).equals(fapiaosBean.getInvoiceTypeCode())) {
                    error.add(911);
                    resultBean.setError(error);
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setFapiaoNetAmount("");
                    resultBean.setFapiaoTaxAmount("");
                    resultBean.setFapiaoTotalAmount("");
                    resultBean.setSupplierTaxId("");
                    resultBean.setFapiaoCode("");
                    resultBean.setFapiaoNumber("");
                    resultBean.setWalmartTaxId("");
                    resultBean.setValidationCode("");
                    resultBean.setInvoiceDate("");
                    resultBean.setFapiaoCurrency("");
                    resultBeaList.add(resultBean);
                    continue;
                }

                //专票查底账，普票查验插底账，但是都要保存jv 、6位供应商号、10位供应商号
                if (fapiaosBean.getInvoiceTypeCode().equals("08")) {
                    //专票查底账
                    Map map = new HashMap();
                    map.put("invoiceCode", electronInvoiceEntity.getInvoiceCode());
                    map.put("invoiceNo", electronInvoiceEntity.getInvoiceNo());
                    List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
                    ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity = new ComprehensiveInvoiceQueryEntity();
                    if (comprehensiveInvoiceQueryEntities.isEmpty()) {
                        error.add(901);
                        resultBean.setError(error);
                    } else {
                        comprehensiveInvoiceQueryEntity = comprehensiveInvoiceQueryEntities.get(0);
                        //判断是否被其他业务类型使用
                        if ("8".equals(comprehensiveInvoiceQueryEntity.getFlowType()) || StringUtils.isBlank(comprehensiveInvoiceQueryEntity.getFlowType())) {


                            if (!"0".equals(comprehensiveInvoiceQueryEntity.getInvoiceStatus())) {
                                error.add(924);
                            }
                            if (!error.isEmpty()) {
                                resultBean.setError(error);
                            } else {
                                //成功，赋值
                                resultBean.setFapiaoNetAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getInvoiceAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setFapiaoTaxAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTaxAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setFapiaoTotalAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTotalAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                                resultBean.setSupplierTaxId(comprehensiveInvoiceQueryEntity.getXfTaxNo());
                                resultBean.setFapiaoCode(comprehensiveInvoiceQueryEntity.getInvoiceCode());
                                resultBean.setFapiaoNumber(comprehensiveInvoiceQueryEntity.getInvoiceNo());
                                resultBean.setWalmartTaxId(comprehensiveInvoiceQueryEntity.getGfTaxNo());
                                resultBean.setValidationCode(comprehensiveInvoiceQueryEntity.getCheckCode().substring(comprehensiveInvoiceQueryEntity.getCheckCode().length()-6));
                                try {
                                    resultBean.setInvoiceDate(formatA.format(formatB.parse(comprehensiveInvoiceQueryEntity.getInvoiceDate())));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                resultBean.setFapiaoCurrency("CNY");
                                resultBean.setError(error);
                                //插值
                                aribaDao.checkUpdate(fapiaosBean,comprehensiveInvoiceQueryEntity);
                                comprehensiveInvoiceQueryEntity.setScanId(scanId);
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, comprehensiveInvoiceQueryEntity, fapiaosBean, Boolean.TRUE);
                                aribaDao.insertScanInvoice(invoiceScan);
                            }
                        } else {
                            error.add(925);
                            resultBean.setError(error);
                        }
                    }
                } else {

                    Map map = new HashMap();
                    map.put("invoiceCode", electronInvoiceEntity.getInvoiceCode());
                    map.put("invoiceNo", electronInvoiceEntity.getInvoiceNo());
                    List<ComprehensiveInvoiceQueryEntity> comprehensiveInvoiceQueryEntities = aribaDao.queryList(map);
                    ComprehensiveInvoiceQueryEntity comprehensiveInvoiceQueryEntity = new ComprehensiveInvoiceQueryEntity();

                    if (comprehensiveInvoiceQueryEntities.isEmpty()) {



                    } else {
                        comprehensiveInvoiceQueryEntity = comprehensiveInvoiceQueryEntities.get(0);
                        if ("8".equals(comprehensiveInvoiceQueryEntity.getFlowType())) {
                            resultBean.setFapiaoNetAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getInvoiceAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setFapiaoTaxAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTaxAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setFapiaoTotalAmount(new BigDecimal(comprehensiveInvoiceQueryEntity.getTotalAmount() + "").setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            resultBean.setSupplierTaxId(comprehensiveInvoiceQueryEntity.getXfTaxNo());
                            resultBean.setWalmartTaxId(comprehensiveInvoiceQueryEntity.getGfTaxNo());
                            resultBean.setFapiaoCode(comprehensiveInvoiceQueryEntity.getInvoiceCode());
                            resultBean.setValidationCode(comprehensiveInvoiceQueryEntity.getCheckCode().substring(comprehensiveInvoiceQueryEntity.getCheckCode().length()-6));
                            try {
                                resultBean.setInvoiceDate(formatA.format(formatB.parse(comprehensiveInvoiceQueryEntity.getInvoiceDate())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            resultBean.setFapiaoNumber(comprehensiveInvoiceQueryEntity.getInvoiceNo());
                            resultBean.setFapiaoCurrency("CNY");
                            resultBean.setError(error);
                            //插值
                            aribaDao.checkUpdate(fapiaosBean,comprehensiveInvoiceQueryEntity);
                            comprehensiveInvoiceQueryEntity.setScanId(scanId);
                            final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, comprehensiveInvoiceQueryEntity, fapiaosBean, Boolean.TRUE);
                            aribaDao.insertScanInvoice(invoiceScan);
                        } else {
                            error.add(925);
                            resultBean.setError(error);
                        }
                    }
                }
                if(error.isEmpty()){

                    byte[] bmpByte=null;
                    //图片转换
                    if(!fileType.equals("pdf")){
                        //ofd
                        OutputStream os=null;
                        File localFile = new File(systemConfig.getTempPath()+scanId+".ofd");
                        try {
                            if (!localFile.exists()) {
                                localFile.createNewFile();
                            }
                            // 输出流
                            os = new FileOutputStream(localFile);
                            os.write(filByte);
                            os.close();
                            Map map=new HashMap();
                            map.put("file",systemConfig.getTempPath()+scanId+".ofd");
                            map.put("tempPath",systemConfig.getTempPath());
                            map.put("fileName", scanId+".jpg");
//                            net.sf.json.JSONObject jsonObject=ScanEditHttpClient.httpPost(scanElUploadUrl,net.sf.json.JSONObject.fromObject(map),false);
//                            String path=jsonObject.getString("path");
                            String path="";
                            File localBmpFile = new File(path);
                            bmpByte=FileTypeUtil.File2byte(localBmpFile);
                            localFile.delete();
                            localBmpFile.delete();
                        }catch ( Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        bmpByte= PdfToImgUtil.pdfToImagePath(filByte);
                    }
                    if(bmpByte!=null) {
                        //压缩，文件上传
                        byte[] bizByte = ZipUtil.zip(bmpByte);
                        //连接远程sftp服务器
                        SFTPHandler handler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getTempPath());

                        //连接远程服务器
                        try {
                            handler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
                            String uploadImg = handler.onlyUploadImg(electronInvoiceEntity.getGfTaxNo(), new SimpleDateFormat("yyyyMMddHHmmssSSS").format(electronInvoiceEntity.getInvoiceDate()), bizByte, scanId + ".zip");
                            //保存图片记录
                            Map imgMap = buildInvoiceImgMap(uploadImg + scanId + ".zip", electronInvoiceEntity.getInvoiceCode() + electronInvoiceEntity.getInvoiceNo(), scanId);
                            imgMap.put("seqNo", "1");
                            imgMap.put("arrayName", "");
                            imgMap.put("isImage", "1");

                            //设置扫描点和票据类型
                            imgMap.put("scanPoint", "");
                            imgMap.put("billtypeCode", "8");
                            imgMap.put("userId", userId);
                            Boolean flag = aribaDao.insertInvoiceImgforCustomerOne(imgMap) > 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setSupplierNumber("");
                    resultBean.setCompanyCode("");
                    resultBean.setFapiaoNetAmount("");
                    resultBean.setFapiaoTaxAmount("");
                    resultBean.setFapiaoTotalAmount("");
                    resultBean.setSupplierTaxId("");
                    resultBean.setFapiaoCode("");
                    resultBean.setFapiaoNumber("");
                    resultBean.setWalmartTaxId("");
                    resultBean.setValidationCode("");
                    resultBean.setInvoiceDate("");
                    resultBean.setFapiaoCurrency("");
                }
                resultBean.setSupplierNumber(null);
                resultBeaList.add(resultBean);
            }
        }
        aribaCheckReturn.setResult(resultBeaList);
        return aribaCheckReturn;
    }
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SHORT_DATE_FORMAT);
    /**
     * 抵账表存在时 构建发票扫描表实体
     *
     * @param exportEntity 包含人员信息实体
     * @param dataEntity   抵账实体
     * @param
     * @param flag         抵账表是否存在 true 存在 false 不存在
     * @return 扫描实体
     */
    private InvoiceScan buildInvoiceScan(ExportEntity exportEntity, ComprehensiveInvoiceQueryEntity dataEntity,AribaCheckEntity.FapiaosBean fapiaosBean,  Boolean flag) {
        //定义返回值
        final InvoiceScan invoiceScan = new InvoiceScan();

        invoiceScan.setBarCode("");
        invoiceScan.setScanMatchStatus("1");
        invoiceScan.setIsExistStamper("1");
//        invoiceScan.setNoExistStamperNotes(entity.getNoExistStamperNotes());

        invoiceScan.setJvCode(fapiaosBean.getJvCode());
        invoiceScan.setCompanyCode(fapiaosBean.getCompanyCode());

        invoiceScan.setFlowType("8");
//        String venderId=dataEntity.getVenderid();
        invoiceScan.setVenderid(fapiaosBean.getLegacySupplierNumber());
        invoiceScan.setVenderidEdit("1");
//        invoiceScan.setId(entity.getId());
//        invoiceScan.setLocalTrmSeqNum(entity.getLocalTrmSeqNum());
        invoiceScan.setFileType("1");
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //校验码
        invoiceScan.setCheckCode(dataEntity.getCheckCode());
        //发票类型
        invoiceScan.setInvoiceType(dataEntity.getInvoiceType());
        //打印代码
        invoiceScan.setDyInvoiceCode(dataEntity.getInvoiceCode());
        //打印号码
        invoiceScan.setDyInvoiceNo(dataEntity.getInvoiceNo());
        //发票代码
        invoiceScan.setInvoiceCode(dataEntity.getInvoiceCode());
        //发票号码
        invoiceScan.setInvoiceNo(dataEntity.getInvoiceNo());
        //金额
        invoiceScan.setInvoiceAmount(bigToStr(dataEntity.getInvoiceAmount()));
        //开票时间
        invoiceScan.setInvoiceDate(dataEntity.getInvoiceDate());
        //签收方式--
        invoiceScan.setQsType(SignInEnum.QS_SCANNER.getValue());
        //'签收结果(0-签收失败 1-签收成功）'
        invoiceScan.setQsStatus("1");
        //签收描述
        invoiceScan.setNotes("签收成功");
        //是否有效（1-有效 0-无效）默认1
        invoiceScan.setValid("1");
        //uuid
        invoiceScan.setUuId(dataEntity.getInvoiceCode() + dataEntity.getInvoiceNo());
        //购方税号
        invoiceScan.setGfTaxNo(dataEntity.getGfTaxNo());
        //销方税号
        invoiceScan.setXfTaxNo(dataEntity.getXfTaxNo());
        //税额
        invoiceScan.setTaxAmount(bigToStr(dataEntity.getTaxAmount()));
        //价税合计
        invoiceScan.setTotalAmount(bigToStr(dataEntity.getTotalAmount()));
        //scanId
        invoiceScan.setScanId(dataEntity.getScanId());
        //购方名称
        invoiceScan.setGfName(dataEntity.getGfName());
        //x方名称
        invoiceScan.setXfName(dataEntity.getXfName());


//        if (flag) {
            //销方名称
//            invoiceScan.setXfName(dataEntity.getXfName());
            invoiceScan.setDeductibleTaxRate(bigToStr(dataEntity.getDeductibleTaxRate()));
            invoiceScan.setDeductibleTax(bigToStr(dataEntity.getDeductibleTax()));
//            entity.setXfName(dataEntity.getXfName());
            //购方名称
//            invoiceScan.setGfName(dataEntity.getGfName());
            //购方名称
//            entity.setGfName(dataEntity.getGfName());
            //签收时间
//            if ("1".equals(entity.getQsStatus())) {
                invoiceScan.setQsDate(simpleDateFormat.format(new Date()));
//            }

//        }
        return invoiceScan;
    }

    private String bigToStr(Object value) {
        if (value != null && StringUtils.isNotEmpty(String.valueOf(value))) {
            return String.valueOf(value);
        }
        return null;
    }
    /**
     * 构建发票图片map
     *
     * @param imagePath 图片路径
     * @param uuid      发票代码 + 发票号码
     * @param scanId    扫描id 自动生成
     * @return 发票图片map
     */
    private Map<String, String> buildInvoiceImgMap(String imagePath, String uuid, String scanId) {
        //定义返回值
        final Map<String, String> map = newHashMap();
        //图片路径
        map.put("imagePath", imagePath);
        //uuid
        map.put("uuid", uuid);
        //扫描id
        map.put("scanId", scanId);
        //返回map
        return map;
    }
    @Value("${getTaxInformation}")
    private String getTaxInformation;

    @Value("${basicAuth.getTaxInformation}")
    private String basicAuthGetTaxInformation;
    @Override
    public void check() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        //查询已收票，签收成功，或签收失败但发票状态为异常
        List<RecordInvoiceEntity> recordInvoiceEntities=aribaDao.getTaxInformation();
        AribaCheckEntity aribaCheckEntity=new AribaCheckEntity();
        List<AribaCheckEntity.FapiaosBean> fapiaosBeanList=new ArrayList<>();
        for(RecordInvoiceEntity recordInvoiceEntity:recordInvoiceEntities){
            AribaCheckEntity.FapiaosBean fapiaosBean=new AribaCheckEntity.FapiaosBean();
            fapiaosBean.setInvoiceType("01".equals(recordInvoiceEntity.getInvoiceType())?"VAT Special":"VAT Normal");
            fapiaosBean.setFapiaoCode(recordInvoiceEntity.getInvoiceCode());
            fapiaosBean.setFapiaoNumber(recordInvoiceEntity.getInvoiceNo());
            fapiaosBean.setValidationCode("01".equals(recordInvoiceEntity.getInvoiceType())?"":recordInvoiceEntity.getCheckCode().substring(recordInvoiceEntity.getCheckCode().length()-6));;
            fapiaosBean.setSupplierNumber(recordInvoiceEntity.getSupplierNumber());
//            fapiaosBean.setSupplierName(recordInvoiceEntity.getSupplierNumber());
            fapiaosBean.setStatus("1".equals(recordInvoiceEntity.getQsStatus())?"Received":"Mismatch");
            fapiaosBean.setCustomField1("");
            fapiaosBean.setCustomField2("");
            fapiaosBean.setCustomField3("");
            fapiaosBean.setCustomField4("");
            fapiaosBean.setCustomField5("");
            fapiaosBeanList.add(fapiaosBean);
        }
        if(!fapiaosBeanList.isEmpty()) {
            aribaCheckEntity.setFapiaos(fapiaosBeanList);
            //推送
            LOGGER.info("已收票推送数据：{}", JSONObject.fromObject(aribaCheckEntity).toString());
            JSONObject jsonObject = ScanEditHttpClient.httpPostAuth(getTaxInformation, JSONObject.fromObject(aribaCheckEntity), false, new String(com.xforceplus.wapp.common.utils.Base64.encode(basicAuthGetTaxInformation.getBytes())));
            LOGGER.info("已收票返回数据：{}", jsonObject.toString());
            saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),basicAuthGetTaxInformation,"已收票",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            if (jsonArray != null && jsonArray.size() != 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject result = jsonArray.getJSONObject(i);
                    if ("OK".equals(result.getString("status"))) {
                        aribaDao.updateTaxInformation(result);
                    }
                }
            }
        }
    }


    private ElectronInvoiceEntity getElectronInvoiceToSave(FPEntity fpEntity, String scanId, UserEntity user) {
        ElectronInvoiceEntity invoiceEntity = new ElectronInvoiceEntity();
        String userAccount = user.getLoginname();
        String userName = user.getUsername();
        //购方信息
        invoiceEntity.setGfName(fpEntity.getBuyer_name());
        invoiceEntity.setGfTaxNo(fpEntity.getBuyer_nsrsbh());
        invoiceEntity.setXfTaxNo(fpEntity.getSeller_nsrsbh());
        invoiceEntity.setXfName(fpEntity.getSeller_name());
        invoiceEntity.setInvoiceType(COMMON_ELECTRON_INVOICE_TYPE);
        Date kprq = null;
        try {
            kprq = DateTimeHelper.parseDateTime(fpEntity.getKprq(), INVOICE_KPRQ_FORMAT_ZH);
        } catch (Exception e) {
            kprq = DateTimeHelper.parseDateTime(fpEntity.getKprq(), INVOICE_KPRQ_FORMAT);
        }
        invoiceEntity.setInvoiceDate(kprq);
        Boolean flag = Boolean.FALSE;
        try {
            invoiceEntity.setInvoiceAmount(new BigDecimal(fpEntity.getHjje()));
        } catch (Exception e) {
            invoiceEntity.setInvoiceAmount(BigDecimal.ZERO);
            flag = true;
        }

        try {
            invoiceEntity.setTaxAmount(new BigDecimal(fpEntity.getHjse()));
        } catch (Exception e) {
            invoiceEntity.setTaxAmount(BigDecimal.ZERO);
            flag = true;
        }
        try {
            invoiceEntity.setTotalAmount(new BigDecimal(fpEntity.getJshj()));
        } catch (Exception e) {
            invoiceEntity.setTotalAmount(BigDecimal.ZERO);
            flag = true;
        }

        if (flag) {
            invoiceEntity.setNotes("发票扫描异常");
        } else {
            invoiceEntity.setNotes("未签收");
        }
        invoiceEntity.setInvoiceCode(fpEntity.getFpdm());
        invoiceEntity.setInvoiceNo(fpEntity.getFphm());
        invoiceEntity.setUuid(fpEntity.getFpdm() + fpEntity.getFphm());

        invoiceEntity.setScanId(scanId);
        invoiceEntity.setUserAccount(userAccount);
        invoiceEntity.setUserName(userName);
        invoiceEntity.setCreateDate(DateTime.now().toDate());
        invoiceEntity.setUpdateDate(DateTime.now().toDate());
        invoiceEntity.setCheckCode(fpEntity.getJym().substring(fpEntity.getJym().length() - 6));
        return invoiceEntity;
    }


    private Map uploadFileParsing(String fileType, byte[] filByte,String scanId)  {
        Map uploadFileParsing = new HashMap();
        List error = new ArrayList();
        OutputStream os=null;
        if (fileType.equals("ofd")) {
//            转图片调用
//            ofdService.toImg(new File(filByte),pathName,fileName);
            // 根据绝对路径初始化文件
            File localFile = new File(systemConfig.getTempPath()+scanId+".ofd");
            try {
                if (!localFile.exists()) {
                    localFile.createNewFile();
                }
                // 输出流
                os = new FileOutputStream(localFile);
                os.write(filByte);
                os.close();
                FPEntity fp=new FPEntity();
                uploadFileParsing.put("code", "ok");
                uploadFileParsing.put("data", fp);

            }catch (Exception e){
                e.printStackTrace();
                uploadFileParsing.put("code", "error");
                uploadFileParsing.put("data", 926);
            }



        } else if (fileType.equals("pdf")) {
            try {
                FPEntity fp = ParsePdfUtil.parseOnePdf(filByte);
                uploadFileParsing.put("code", "ok");
                uploadFileParsing.put("data", fp);
            } catch (Exception e) {
                e.printStackTrace();
                uploadFileParsing.put("code", "error");
                uploadFileParsing.put("data", 926);
            }


        } else {
            uploadFileParsing.put("code", "error");
            uploadFileParsing.put("data", 926);
        }
        return uploadFileParsing;
    }

    @Transactional
    public Boolean inspectionProcess(List<ResponseInvoice> responseInvoiceList, boolean cyYoN, String jvCode, String legacySupplierNumber, String supplierNumber, String companyCode) {

        //遍历
        responseInvoiceList.forEach(responseInvoice -> {
            //导入excel时所有需要保存主表数据
            final List<InvoiceCollectionInfo> infoList = CollectionHelper.newArrayList();
            //所有需要保存的明细数据
            final List<InvoiceDetailInfo> inspectionDetailInfoList = CollectionHelper.newArrayList();
            //构建抵账表发票明细
            final List<InvoiceDetailInfo> invoiceDetailInfoList = buildDetailList(responseInvoice);
            //集合合并
            inspectionDetailInfoList.addAll(invoiceDetailInfoList);

            //构建主表实体
            InvoiceCollectionInfo invoiceCollectionInfo = buildInvoiceCollectionInfo(responseInvoice, "");
            invoiceCollectionInfo.setNewTaxno(responseInvoice.getBuyerTaxNo());
            invoiceCollectionInfo.setVenderid(legacySupplierNumber);
            invoiceCollectionInfo.setSupplierNumber(supplierNumber);
            invoiceCollectionInfo.setJvCode(jvCode);
            invoiceCollectionInfo.setCompanyCode(companyCode);
            invoiceCollectionInfo.setFlowType("8");
            if (inspectionDetailInfoList.size() > 0) {
                //保存明细表
                //构建明细表数据
                //保存明细数据
                try {

                    if (inspectionDetailInfoList.get(0).getTaxRate() != null && ("不征税".equals(inspectionDetailInfoList.get(0).getTaxRate()) || "免税".equals(inspectionDetailInfoList.get(0).getTaxRate()) || "不征收".equals(inspectionDetailInfoList.get(0).getTaxRate()))) {
                        inspectionDetailInfoList.get(0).setTaxRate("0");
                    }
                    try {
                        BigDecimal abc = new BigDecimal(inspectionDetailInfoList.get(0).getTaxRate());
                    } catch (Exception e) {
                        inspectionDetailInfoList.get(0).setTaxRate("0");
                    }
                    String taxRate = inspectionDetailInfoList.get(0).getTaxRate();
                    for (InvoiceDetailInfo invoiceDetailInfo : inspectionDetailInfoList) {
                        if (invoiceDetailInfo.getTaxRate() != null && ("不征税".equals(inspectionDetailInfoList.get(0).getTaxRate()) || "免税".equals(invoiceDetailInfo.getTaxRate()) || "不征收".equals(invoiceDetailInfo.getTaxRate()))) {
                            invoiceDetailInfo.setTaxRate("0");
                        }
                        try {
                            BigDecimal abc = new BigDecimal(inspectionDetailInfoList.get(0).getTaxRate());
                        } catch (Exception e) {
                            inspectionDetailInfoList.get(0).setTaxRate("0");
                        }
                        //獲取所有明細的稅率是否相等
                        if (taxRate != null) {
                            if (!taxRate.equals(invoiceDetailInfo.getTaxRate())) {
                                taxRate = null;
                            }
                        }
                    }

                    aribaDao.deleteDetail(inspectionDetailInfoList.get(0));
                    List<List<InvoiceDetailInfo>> groudList = splitList(inspectionDetailInfoList, 150);
                    for (int j = 0; j < groudList.size(); j++) {
                        aribaDao.insertNoDetailedInvoice(groudList.get(j));
                    }
                    invoiceCollectionInfo.setTaxRate(taxRate);
                    if (invoiceCollectionInfo.getTaxRate().equals("0")) {
                        invoiceCollectionInfo.setLslbz("3");
                        invoiceCollectionInfo.setTaxRate("0");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //添加集合
            infoList.add(invoiceCollectionInfo);


            //保存主表
            for (InvoiceCollectionInfo invoiceCollectionInfo1 : infoList) {

                //不知道啥情况selectKey获取到的值应该是0结果却是1，没办法，先把问题解决了
                //根据uuid是否存在决定是插入还是修改


                Integer count = aribaDao.selectRecordInvoiceCount(invoiceCollectionInfo1.getUuid());
                if (null != count && count > 0) {
                    aribaDao.updateRecordInvoiceScan(invoiceCollectionInfo1, cyYoN ? 1 : 0);
                } else {
                    aribaDao.insertRecordInvoiceScan(invoiceCollectionInfo1, cyYoN ? 1 : 0);
                }

            }
        });


        return Boolean.TRUE;
    }
    private List<List<InvoiceDetailInfo>> splitList(List<InvoiceDetailInfo> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<InvoiceDetailInfo>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    /**
     * 构建抵账主体数据
     *
     * @param responseInvoice 抵账主体数据
     * @return 主体数据
     */
    private InvoiceCollectionInfo buildInvoiceCollectionInfo(ResponseInvoice responseInvoice, String buyerTaxNo) {
        //定义返回值
        final InvoiceCollectionInfo invoiceCollectionInfo = new InvoiceCollectionInfo();

        //购方税号
        invoiceCollectionInfo.setGfTaxNo(responseInvoice.getBuyerTaxNo());
        //购方名称
        invoiceCollectionInfo.setGfName(responseInvoice.getBuyerName());
        //发票类型
        invoiceCollectionInfo.setInvoiceType(responseInvoice.getInvoiceType());
        //发票代码
        invoiceCollectionInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
        //发票号码
        invoiceCollectionInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
        //价税合计
        invoiceCollectionInfo.setTotalAmount(responseInvoice.getTotalAmount());
        //金额
        invoiceCollectionInfo.setInvoiceAmount(responseInvoice.getInvoiceAmount());
        //税额
        invoiceCollectionInfo.setTaxAmount(responseInvoice.getTaxAmount());
        //销方税号
        invoiceCollectionInfo.setXfTaxNo(responseInvoice.getSalerTaxNo());
        //销方名称
        invoiceCollectionInfo.setXfName(responseInvoice.getSalerName());
        //销方地址
        invoiceCollectionInfo.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
        //销方银行帐号
        invoiceCollectionInfo.setXfBankAndNo(responseInvoice.getSalerAccount());
        //购方银行帐号
        invoiceCollectionInfo.setGfBankAndNo(responseInvoice.getBuyerAccount());
        //购方地址
        invoiceCollectionInfo.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
        SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(SHORT_DATE_FORMAT);
        //开票日期
        Date invoiceDateTime = null;
        try {
            invoiceDateTime = simpleDateFormatyyyyMMdd.parse(responseInvoice.getInvoiceDate());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        invoiceCollectionInfo.setInvoiceDate(invoiceDateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(invoiceCollectionInfo.getInvoiceDate());
//        invoiceCollectionInfo.setInvoiceDateString(dateString);
        //备注
        invoiceCollectionInfo.setRemark(responseInvoice.getRemark());
        //校验码
        invoiceCollectionInfo.setCheckCode(responseInvoice.getCheckCode());
        //有明细
        invoiceCollectionInfo.setDetailYesorno(DETAIL_YES_OR_NO);

        //uuid
        invoiceCollectionInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        //更新前的购方税号，放置不一致
        invoiceCollectionInfo.setBuyerTaxNo(buyerTaxNo);
        //发票状态
        if ("N".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("0");
        } else if ("Y".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("2");
        } else if ("0".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("0");
        } else if ("3".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("3");
        } else if ("2".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("2");
        }
        //机器编号
        invoiceCollectionInfo.setMachinecode(responseInvoice.getMachineNo());
        List<InvoiceDetail> detailList = responseInvoice.getDetailList();
        if (detailList != null) { // 超过八条数据取第二条税率
            Set set = new HashSet<String>();
            for (InvoiceDetail detail : detailList) {
                if (detail.getTaxRate() != null) {
                    set.add(detail.getTaxRate());
                }
            }
            if (detailList.size() >= 8 && set.size() == 1) {
                if (StringUtils.isNotBlank(detailList.get(1).getTaxRate())) {
                    invoiceCollectionInfo.setTaxRate(detailList.get(1).getTaxRate());
                }
            } else {
                if (set.size() == 1) {
                    if (StringUtils.isNotBlank(detailList.get(0).getTaxRate())) {
                        invoiceCollectionInfo.setTaxRate(detailList.get(0).getTaxRate());
                    }
                }
            }
        }
        //返回
        return invoiceCollectionInfo;
    }

    /**
     * 构建抵账表发票明细
     *
     * @param responseInvoice 响应实体
     * @return 抵账表发票明细
     */
    private List<InvoiceDetailInfo> buildDetailList(ResponseInvoice responseInvoice) {
        //构建返回值
        final List<InvoiceDetailInfo> invoiceDetailInfoList = CollectionHelper.newArrayList();

        //明细
        final List<InvoiceDetail> invoiceDetailList = responseInvoice.getDetailList();

        invoiceDetailList.forEach(invoiceDetail -> {
            final InvoiceDetailInfo invoiceDetailInfo = new InvoiceDetailInfo();
            //税额
            invoiceDetailInfo.setTaxAmount(invoiceDetail.getTaxAmount());
            //货物或应税劳务名称
            invoiceDetailInfo.setGoodsName(invoiceDetail.getGoodsName());
            //发票号码
            invoiceDetailInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            invoiceDetailInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
            //数量
            invoiceDetailInfo.setNum(invoiceDetail.getNum());
            //明细序号
            invoiceDetailInfo.setDetailNo(invoiceDetail.getDetailNo());
            //单价
            invoiceDetailInfo.setUnitPrice(invoiceDetail.getUnitPrice());
            //类型
            invoiceDetailInfo.setLx(invoiceDetail.getLx());
            //uuid唯一标识(发票代码+发票号码)
            invoiceDetailInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            //通行日期起
            invoiceDetailInfo.setTxrqq(invoiceDetail.getTxrqq());
            //通行日期止
            invoiceDetailInfo.setTxrqz(invoiceDetail.getTxrqz());
            //税率
            invoiceDetailInfo.setTaxRate(invoiceDetail.getTaxRate());
            //单位
            invoiceDetailInfo.setUnit(invoiceDetail.getUnit());
            //金额
            invoiceDetailInfo.setDetailAmount(invoiceDetail.getDetailAmount());
            //规格型号
            invoiceDetailInfo.setModel(invoiceDetail.getSpecificationModel());
            //车牌号
            invoiceDetailInfo.setCph(invoiceDetail.getCph());
            //放入集合
            invoiceDetailInfoList.add(invoiceDetailInfo);
        });
        return invoiceDetailInfoList;
    }


    private List checkValidator(AribaCheckEntity.FapiaosBean fapiaosBean) {
        String invoiceType = "00";
        List error = new ArrayList<>();
        //校验必填字段
        if (StringUtils.isBlank(fapiaosBean.getJvCode())) {
            error.add(909);
        }
        if (StringUtils.isBlank(fapiaosBean.getFapiaoCode())) {
            error.add(904);
        }
        if (StringUtils.isBlank(fapiaosBean.getFapiaoNumber())) {
            error.add(905);
        }
        if (StringUtils.isBlank(fapiaosBean.getCompanyCode())) {
            error.add(914);
        }
        if (StringUtils.isNotBlank(fapiaosBean.getInvoiceType())) {

            if ("VAT Special".equals(fapiaosBean.getInvoiceType())) {
                if (StringUtils.isBlank(fapiaosBean.getFapiaoNetAmount())) {
                    error.add(907);
                }
                fapiaosBean.setInvoiceTypeCode("01");
                invoiceType = "01";
            } else if ("VAT Normal".equals(fapiaosBean.getInvoiceType())) {
                if (StringUtils.isBlank(fapiaosBean.getValidationCode()) || fapiaosBean.getValidationCode().length() != 6) {
                    error.add(908);
                }
                fapiaosBean.setInvoiceTypeCode("04");
                invoiceType = "04";
            }
        }

        if (!CommonUtil.getFplx(fapiaosBean.getFapiaoCode()).equals(invoiceType)) {
            error.add(911);
        }


        try {
            date = new SimpleDateFormat("MM/dd/yyyy").parse(fapiaosBean.getFapiaoDate());
        } catch (ParseException e) {
            error.add(906);
        }


//        if (StringUtils.isBlank(fapiaosBean.getLegacySupplierNumber()) || StringUtils.isBlank(fapiaosBean.getSupplierNumber())) {
//            error.add(910);
//        }
        return error;
    }

    private List signInMarkValidator(AribaCheckEntity.FapiaosBean fapiaosBean) {
        List error = new ArrayList<>();
        if (StringUtils.isBlank(fapiaosBean.getFapiaoCode())) {
            error.add(904);
        }
        if (StringUtils.isBlank(fapiaosBean.getFapiaoNumber())) {
            error.add(905);
        }
        if (StringUtils.isBlank(fapiaosBean.getCustomField1()) || StringUtils.isBlank(fapiaosBean.getCustomField2())) {
            error.add(910);
        }
        return error;
    }

    private List authValidatorFapiaosBean(AribaCheckEntity.FapiaosBean fapiaosBean) {
        List error = new ArrayList<>();
        if (StringUtils.isBlank(fapiaosBean.getFapiaoCode())) {
            error.add(904);
        }
        if (StringUtils.isBlank(fapiaosBean.getFapiaoNumber())) {
            error.add(905);
        }
//        if (StringUtils.isBlank(fapiaosBean.getPurchasingDocumentNumber())) {
//            error.add(913);
//        }
        if (StringUtils.isBlank(fapiaosBean.getCompanyCode())) {
            error.add(914);
        }
        return error;
    }

    private List authValidatorItemsBean(AribaCheckEntity.FapiaosBean.ItemsBean itemsBean) {
        List error = new ArrayList<>();
        if (StringUtils.isBlank(itemsBean.getTaxCode())) {
            error.add(915);
        }
        if (StringUtils.isBlank(itemsBean.getTaxRate())) {
            error.add(916);
        }
        if (StringUtils.isBlank(itemsBean.getGlAccount())) {
            error.add(917);
        }
        if (StringUtils.isBlank(itemsBean.getMccCode())) {
            error.add(918);
        }
        if (StringUtils.isBlank(itemsBean.getCostCenter())) {
            error.add(919);
        }
        if (StringUtils.isBlank(itemsBean.getFapiaoTotalAmount())) {
            error.add(920);
        }
        if (StringUtils.isBlank(itemsBean.getFapiaoTaxAmount())) {
            error.add(921);
        }
        return error;
    }

    private List uploadValidator(AribaCheckEntity.FapiaosBean fapiaosBean) {
        String invoiceType = "00";
        List error = new ArrayList<>();
        //校验必填字段
        if (StringUtils.isBlank(fapiaosBean.getJvCode())) {
            error.add(909);
        }

        if (StringUtils.isBlank(fapiaosBean.getCompanyCode())) {
            error.add(914);
        }
        if (StringUtils.isNotBlank(fapiaosBean.getInvoiceType())) {

            if ("eFapiao-VAT Special".equals(fapiaosBean.getInvoiceType())) {
                fapiaosBean.setInvoiceTypeCode("08");
                invoiceType = "08";
            } else if ("eFapiao-VAT Normal".equals(fapiaosBean.getInvoiceType())) {
                fapiaosBean.setInvoiceTypeCode("10");
                invoiceType = "10";
            } else {
                error.add(911);
            }
        }

        if (StringUtils.isBlank(fapiaosBean.getLegacySupplierNumber()) || StringUtils.isBlank(fapiaosBean.getSupplierNumber())) {
            error.add(910);
        }
        if (StringUtils.isBlank(fapiaosBean.getAttachmentLink())) {
            error.add(927);
        }else{
            fapiaosBean.setAttachmentLink(StringEscapeUtils.unescapeHtml(fapiaosBean.getAttachmentLink()));
        }
        return error;
    }

}
