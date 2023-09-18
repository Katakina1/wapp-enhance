package com.xforceplus.wapp.modules.backfill.service;//package com.xforceplus.wapp.modules.backfill.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import com.xforceplus.apollo.msg.SealedMessage;
//import com.xforceplus.wapp.common.utils.*;
//import com.xforceplus.wapp.common.utils.Base64;
//import com.xforceplus.wapp.constants.Constants;
//import com.xforceplus.wapp.handle.IntegrationResultHandler;
//import com.xforceplus.wapp.modules.backfill.convert.SellerInvoiceDetailConverter;
//import com.xforceplus.wapp.modules.backfill.dto.SellerInvoicePushDto;
//import com.xforceplus.wapp.modules.backfill.dto.TXfSellerInvoiceEntity;
//import com.xforceplus.wapp.modules.backfill.dto.TXfSellerInvoiceItemEntity;
//import com.xforceplus.wapp.modules.backfill.model.*;
//import com.xforceplus.wapp.modules.company.service.CompanyService;
//import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
//import com.xforceplus.wapp.modules.sys.util.UserUtil;
//import com.xforceplus.wapp.repository.daoExt.ElectronicInvoiceDao;
//import com.xforceplus.wapp.repository.daoExt.MatchDao;
//import com.xforceplus.wapp.repository.daoExt.XfRecordInvoiceDao;
//import com.xforceplus.wapp.repository.entity.InvoiceEntity;
//import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
//import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
//import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Supplier;
//
//@Service
//@Slf4j
//public class SellerInvoicePushService implements IntegrationResultHandler {
//
//    private static final String REQUEST_NAME = "sellerInvoicePush";
//    @Autowired
//    private CompanyService companyService;
//    @Autowired
//    private ElectronicInvoiceDao electronicInvoiceDao;
//
//    @Autowired
//    private XfRecordInvoiceDao recordInvoiceDao;
//
//    @Autowired
//    private MatchDao matchDao;
//    @Autowired
//    private SellerInvoiceDetailConverter sellerInvoiceDetailConverter;
//
//    @Autowired
//    private NoneBusinessService noneBusinessService;
//
//    @Autowired
//    @Lazy
//    private VerificationService verificationService;
//    @Autowired
//    private FileService fileService;
//
//    @Autowired
//    private BackFillService backFillService;
//
//    @Override
//    public boolean handle(SealedMessage sealedMessage) {
//
//        SealedMessage.Header header = sealedMessage.getHeader();
//
//        SealedMessage.Payload payload = sealedMessage.getPayload();
//        log.info("发票下发异步结果>>>>SealedMessage.header:{},payload.getObj: {}", JSON.toJSONString(header), payload.getObj());
//        TypeReference<SellerInvoicePushDto> typeRef = new TypeReference<SellerInvoicePushDto>() {
//        };
//        JSONObject json = (JSONObject) JSONObject.parse((String) payload.getObj());
//        JSONArray arry = json.getJSONArray("sellerInvoiceDetails");
//        for (int i = 0; i < arry.size(); i++) {
//            JSONObject obj1 = (JSONObject) arry.get(i);
//            obj1.put("id", null);
//        }
//        SellerInvoicePushDto sellerInvoicePushDto = JSON.parseObject(json.toJSONString(), typeRef);
//        if (Objects.isNull(sellerInvoicePushDto.getSellerInvoiceMain())) {
//            log.error("发票下发头信息为空");
//            return false;
//        }
//        TXfSellerInvoiceEntity tXfSellerInvoiceEntity = sellerInvoicePushDto.getSellerInvoiceMain();
//        List<TXfSellerInvoiceItemEntity> detaiList = sellerInvoicePushDto.getSellerInvoiceDetails();
//        TAcOrgEntity purEntity = companyService.getOrgInfoByTaxNo(tXfSellerInvoiceEntity.getPurchaserTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
//        TAcOrgEntity sellerEntity = companyService.getOrgInfoByTaxNo(tXfSellerInvoiceEntity.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_SUPPLIER);
//        TAcOrgEntity sellerWalEntity = companyService.getOrgInfoByTaxNo(tXfSellerInvoiceEntity.getSellerTaxNo(), com.xforceplus.wapp.modules.blackwhitename.constants.Constants.COMPANY_TYPE_WALMART);
//        Map<String, Object> map = new HashMap<>();
//        if (null != sellerEntity) {
//            map.put("venderid", sellerEntity.getOrgCode());
//            map.put("xfName", sellerEntity.getOrgName());
//        }
//        if (null != purEntity) {
//            map.put("jvcode", purEntity.getOrgCode());
//            map.put("gfName", purEntity.getOrgName());
//        }
//        //如果购销方都是沃尔玛的 jv开给chc的票，同步到非商模块
//        if (sellerWalEntity != null && purEntity != null) {
//            TXfNoneBusinessUploadDetailEntity entity = new TXfNoneBusinessUploadDetailEntity();
//            entity.setInvoiceDate(tXfSellerInvoiceEntity.getPaperDrewDate());
//            entity.setInvoiceCode(tXfSellerInvoiceEntity.getInvoiceCode());
//            entity.setBatchNo(DateUtils.getNo(5));
//            entity.setInvoiceNo(tXfSellerInvoiceEntity.getInvoiceNo());
//            entity.setOfdStatus(Constants.VERIFY_NONE_BUSINESS_SUCCESSE);
//            entity.setVerifyStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
//            entity.setCreateUser("System");
//            entity.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
//            entity.setBussinessType("3");
//            entity.setInvoiceType("5");
//            entity.setCreateTime(new Date());
//            if (!StringUtils.isEmpty(tXfSellerInvoiceEntity.getEUrl())) {
//                String uploadFile = null;
//                try {
////                    int lastIndexOf = tXfSellerInvoiceEntity.getEUrl().lastIndexOf(".");
////                    String suffix = tXfSellerInvoiceEntity.getEUrl().substring(lastIndexOf);
//                    String base64 = verificationService.getBase64ByUrl(tXfSellerInvoiceEntity.getEUrl());
////                    entity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
//                    //发送验签
//                    OfdResponse response = backFillService.signOfd(Base64.decode(base64), entity.getBussinessNo());
//                    if (response.isOk()) {
//                        entity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
//                        entity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
//                        entity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_SUCCESSE);
//                        uploadFile = fileService.uploadFile(Base64.decode(base64), UUID.randomUUID().toString().replace("-", "") + ".ofd", "noneBusiness");
//                        UploadFileResult uploadResult1 = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
//                        entity.setSourceUploadId(uploadResult1.getData().getUploadId());
//                        entity.setSourceUploadPath(uploadResult1.getData().getUploadPath());
//                        final InvoiceMain invoiceMain = response.getResult().getInvoiceMain();
//                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(response.getResult().getImageUrl())) {
//                            String base64Jpg = verificationService.getBase64ByUrl(response.getResult().getImageUrl());
//                            uploadFile = fileService.uploadFile(Base64.decode(base64Jpg), UUID.randomUUID().toString().replace("-", "") + ".jpeg", "noneBusiness");
//                            UploadFileResult uploadFileImageResult1 = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
//                            entity.setUploadId(uploadFileImageResult1.getData().getUploadId());
//                            entity.setUploadPath(uploadFileImageResult1.getData().getUploadPath());
//                        }
//                    } else {
//                        entity.setFileType(String.valueOf(Constants.FILE_TYPE_PDF));
//                        uploadFile = fileService.uploadFile(Base64.decode(base64), UUID.randomUUID().toString().replace("-", "") + ".pdf", "noneBusiness");
//                        UploadFileResult uploadResult1 = JsonUtil.fromJson(uploadFile, UploadFileResult.class);
//                        entity.setUploadId(uploadResult1.getData().getUploadId());
//                        entity.setUploadPath(uploadResult1.getData().getUploadPath());
//                        entity.setSourceUploadId(uploadResult1.getData().getUploadId());
//                        entity.setSourceUploadPath(uploadResult1.getData().getUploadPath());
//                    }
//
//                } catch (IOException e) {
//                    log.error("发票同步上传文件服务器失败:{}", e);
//                }
//
//
//            }
//            noneBusinessService.save(entity);
//        }
//        map.put("invoiceNo", tXfSellerInvoiceEntity.getInvoiceNo());
//        map.put("invoiceCode", tXfSellerInvoiceEntity.getInvoiceCode());
//        map.put("invoiceAmount", tXfSellerInvoiceEntity.getAmountWithoutTax());
//        map.put("invoiceDate", tXfSellerInvoiceEntity.getPaperDrewDate());
//        map.put("totalAmount", tXfSellerInvoiceEntity.getAmountWithTax());
//        map.put("taxAmount", tXfSellerInvoiceEntity.getTaxAmount());
//        map.put("taxRate", detaiList.get(0).getTaxRate());
//        map.put("invoiceType", InvoiceUtil.getInvoiceType(tXfSellerInvoiceEntity.getInvoiceType(), tXfSellerInvoiceEntity.getInvoiceCode()));
//        map.put("gfTaxno", tXfSellerInvoiceEntity.getPurchaserTaxNo());
//        map.put("checkNo", tXfSellerInvoiceEntity.getCheckCode());
//        map.put("xfName", tXfSellerInvoiceEntity.getSellerName());
//        map.put("xfTaxNo", tXfSellerInvoiceEntity.getSellerTaxNo());
//        map.put("cipherText", tXfSellerInvoiceEntity.getCipherText());
////        map.put("goodsListFlag", tXfSellerInvoiceEntity.get);
//        map.put("invoiceStatus", InvoiceUtil.getInvoiceStatus(tXfSellerInvoiceEntity.getStatus()));
//        String uuid = tXfSellerInvoiceEntity.getInvoiceCode() + "" + tXfSellerInvoiceEntity.getInvoiceNo();
//        map.put("uuid", uuid);
//        List<Supplier<Boolean>> successSuppliers = new ArrayList<>();
//        map.put("uuid", uuid);
//        List<InvoiceEntity> list1 = matchDao.ifExist(map);
//        if (CollectionUtils.isEmpty(list1)) {
//            if ("04".equals(CommonUtil.getFplx((String) tXfSellerInvoiceEntity.getInvoiceCode()))) {
//                electronicInvoiceDao.saveInvoicePP(map);
//            } else {
//                this.electronicInvoiceDao.saveInvoice(map);
//            }
//            // 新增发票情况下才会将明细入库
//            successSuppliers.add(() -> {
//                saveOrUpdateRecordInvoiceDetail(sellerInvoiceDetailConverter.map(detaiList), tXfSellerInvoiceEntity.getInvoiceNo(), tXfSellerInvoiceEntity.getInvoiceCode());
//                return true;
//            });
//        } else {
//            map.put("id", list1.get(0).getId());
//            if ("04".equals(CommonUtil.getFplx((String) tXfSellerInvoiceEntity.getInvoiceCode()))) {
//                matchDao.allUpdatePP(map);
//            } else {
//                matchDao.allUpdate(map);
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 保存明细
//     *
//     * @param details
//     * @param invoiceNo
//     * @param invoiceCode
//     */
//    private void saveOrUpdateRecordInvoiceDetail(List<InvoiceDetail> details, String invoiceNo, String invoiceCode) {
//        if (!CollectionUtils.isEmpty(details)) {
//            List<TDxRecordInvoiceDetailEntity> recordDetails = new ArrayList<>();
//            for (int i = 0; i < details.size(); i++) {
//                final InvoiceDetail x = details.get(i);
//                TDxRecordInvoiceDetailEntity detail = new TDxRecordInvoiceDetailEntity();
//                detail.setInvoiceNo(invoiceNo);
//                detail.setInvoiceCode(invoiceCode);
//                detail.setUuid(invoiceCode + invoiceNo);
//                detail.setDetailNo(String.valueOf(i + 1));
//                detail.setDetailAmount(x.getAmountWithoutTax());
//                detail.setGoodsName(x.getCargoName());
//                detail.setTaxAmount(x.getTaxAmount());
//                detail.setTaxRate(x.getTaxRate());
//                detail.setNum(x.getQuantity());
//                detail.setUnit(x.getQuantityUnit());
//                detail.setUnitPrice(x.getUnitPrice());
//                detail.setModel(x.getItemSpec());
//                recordDetails.add(detail);
//            }
//            recordInvoiceDao.saveRecordInvoiceDetail(recordDetails);
//        }
//    }
//
//    @Override
//    public String requestName() {
//        return REQUEST_NAME;
//    }
//}