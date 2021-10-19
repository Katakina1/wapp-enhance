package com.xforceplus.wapp.modules.backFill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.apollo.msg.SealedMessage.Header;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.handle.IntegrationResultHandler;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.repository.dao.TXfElecUploadRecordDetailDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicUploadRecordDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordDetailEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;
//import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhaochao@xforceplus.com
 * @description
 * @create 2021-9-15 15:39:31 业务流程：上传-》云识别-》结果通知-》发票验真-》结果通知-》存储（记录表、发票表）
 * https://wiki.xforceplus.com/pages/viewpage.action?pageId=36934098
 **/
@Service
@Slf4j
public class DiscernService implements IntegrationResultHandler {

	private static final String REQUEST_NAME = "melete";

	/**
	 * 沃尔玛授权码
	 */
	@Value("${wapp.integration.action.discern}")
	private String action;

	/**
	 * 沃尔玛租户ID
	 */
	@Value("${wapp.integration.tenant-id}")
	private String tenantId;
	
	/**
	 * 沃尔玛租户code
	 */
	@Value("${wapp.integration.tenant-code}")
	private String tenantCode;
	
	/**
	 * 集成平台设定的客户号
	 */
	@Value("${wapp.integration.customer-no}")
	private String customerNo;
	
	/**
	 * 识别回调参数
	 * https://wiki.xforceplus.com/pages/viewpage.action?pageId=30018277
	 */
	@Value("${wapp.integration.action.discern-callbackUrl}")
	private String callbackUrl;

	@Autowired
	private HttpClientFactory httpClientFactory;

	@Autowired
	VerificationService verificationService;

	@Autowired
	private TXfElecUploadRecordDetailDao electronicUploadRecordDetailDao;

	@Autowired
	private ElectronicUploadRecordDetailService electronicUploadRecordDetailService;

	@Autowired
	private ElectronicUploadRecordDao electronicUploadRecordDao;

	@Autowired
	IDSequence iDSequence;

	private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Map<String,byte[]> discern(List<byte[]> pdfPathList) {

		if (null == pdfPathList || pdfPathList.isEmpty()) {
			log.warn("识别文件路径列表为空无法识别！");

			throw new EnhanceRuntimeException("识别文件路径列表为空无法识别");
		}

		Map<String, String> headerMap = new HashMap<String, String>();

		//云识别配置tenant_id与tenant_code
		headerMap.put("tenant-id", tenantCode);
		headerMap.put("rpcType", "http");
		String serialNo = UUID.randomUUID().toString();
		headerMap.put("serialNo", serialNo);
		headerMap.put("customerNo", customerNo);
		headerMap.put("timestamp", ft.format(new Date()));
		
		Map<String,byte[]> taskIds = new HashMap<String,byte[]>();

		for (byte[] pdfByte : pdfPathList) {
			DiscernRequest discernRequest = new DiscernRequest();

			discernRequest.setTitle("沃尔玛发票识别");
			discernRequest.setGroup(tenantCode);
			// 0-未定义；1-裸扫发票；2-A4纸发票；3-拍照；4-上传文件
			discernRequest.setScene("4");
			 discernRequest.setFile(gzip(pdfByte));
			discernRequest.setFileSuffix(".pdf");
			//属地系统需要增加callbackUrl
			//https://wiki.xforceplus.com/pages/viewpage.action?pageId=30018277
			discernRequest.setCallbackUrl(callbackUrl);
			
			discernRequest.setSerialNo(serialNo);
			discernRequest.setCustomerNo(customerNo);

			try {

				String body = JSON.toJSONString(discernRequest);

				log.info("请求集成平台action,{},headerMap:{},body:{}", action, headerMap, body);

				String result = httpClientFactory.post(action, headerMap, body, null);

				log.info("调用集成平台返回{}", result);

				DiscernResponse discernResponse = JsonUtil.fromJson(result, DiscernResponse.class);
				
				if(discernResponse.getCode().equals(1) && null != discernResponse.getResult()) {
					String taskId = discernResponse.getResult().getTaskId();
					
					taskIds.put(taskId, pdfByte);
				}else {
					log.error("pdf识别失败:{}",discernResponse.getMessage());
				}

				
				
			} catch (IOException e) {
				log.error("调用集成平台错误:{}", e);
				
//				 throw new EnhanceRuntimeException("调用集成平台错误:" + e.getMessage());

			}
		}

		return taskIds;
	}
	

	/**
	 * 压缩
	 * @param file
	 */
	public static String gzip(byte[] file) {
	    
		  GZIPOutputStream gzip = null;
		  try {
		    BASE64Encoder encoder = new BASE64Encoder();
		    // 返回Base64编码过的字节数组字符串
		    String tmpStr = encoder.encode(Objects.requireNonNull(file));

		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    gzip = new GZIPOutputStream(out);
		    gzip.write(tmpStr.getBytes());
		    gzip.close();

		    return encoder.encode(Objects.requireNonNull(out.toByteArray()));
		  } catch (Exception e) {
		    log.error("字符串压缩异常:{}", e);
		  }
		  
		  return "";

		}


	/**
	 * 业务流程：上传-》云识别-》结果通知-》发票验真-》结果通知-》存储（记录表、大象记录表）
	 */
	@Override
	public boolean handle(SealedMessage sealedMessage) {

		Header header = sealedMessage.getHeader();

		SealedMessage.Payload payload = sealedMessage.getPayload();
		
		DiscernResult discernResult =null;

		try {
			log.info("PDF识别异步结果>>>>SealedMessage.header:{},payload.getObj: {}", JSON.toJSONString(header), payload.getObj());

			TypeReference<DiscernResult> typeRef = new TypeReference<DiscernResult>() {
			};

			 discernResult = JSON.parseObject(payload.getObj().toString(), typeRef);

			if (!discernResult.getDiscernStatus().equals(DiscernResult.CODE_SUCCESS)) {
				
				log.warn("发票识别异步结果》》pdf识别失败:header:{},失败消息{}", header, JSONObject.toJSONString(discernResult));
				
				return false;
			}
			
		} catch (Exception e) {
			log.error("处理识别异步结果异常:{}", e);
			
			 throw new EnhanceRuntimeException("处理识别异步结果异常:" + e.getMessage());
		}

		// 发票验真
		return verifyInvoice(discernResult);
	}

	/**
	 * 执行发票验真请求
	 * 
	 * @param discernResult
	 * @return
	 */
	private Boolean verifyInvoice(DiscernResult discernResult) {

		// 执行发票验真流程
		
		TypeReference<DiscernResultDetail> typeRef = new TypeReference<DiscernResultDetail>() {
		};

		DiscernResultDetail discernResultDetail = JSON.parseObject(discernResult.getDiscernResult(), typeRef);
		final String taskId = discernResult.getTaskId();
		final TXfElecUploadRecordDetailEntity detailEntity = electronicUploadRecordDetailService.getByDiscernTaskId(taskId);

		VerificationRequest invoice = new VerificationRequest();
		invoice.setInvoiceCode(discernResultDetail.getInvoiceCode());
		invoice.setInvoiceNo(discernResultDetail.getInvoiceNo());
		invoice.setPaperDrewDate(discernResultDetail.getInvoiceTime());
		invoice.setCheckCode(discernResultDetail.getCheckCode());
		invoice.setAmount(
				null == discernResultDetail.getTotalAmount() ? "" : discernResultDetail.getTotalAmount().toString());

		log.info("发票验真请求参数：{}", invoice);
		//
		VerificationResponse verificationResponse = verificationService.verify(invoice);
		log.info("发票验真同步返回结果：{}", JSON.toJSONString(verificationResponse));
		if (!verificationResponse.isOK()) {
			log.warn("发票代码:{},发票号码：{}，发票验真请求失败:{}", invoice.getInvoiceCode(), invoice.getInvoiceNo(),
					verificationResponse.getMessage());

			detailEntity.setStatus(false);
			detailEntity.setReason(verificationResponse.getMessage());
			this.electronicUploadRecordDetailDao.updateById(detailEntity);
			this.electronicUploadRecordDao.increaseFailureNum(detailEntity.getBatchNo());
			return true;
		}
		final String verifyTaskId = verificationResponse.getResult();
		detailEntity.setXfVerifyTaskId(verifyTaskId);
		this.electronicUploadRecordDetailDao.updateById(detailEntity);

		return true;
	}

	@Override
	public String requestName() {
		return REQUEST_NAME;
	}
}
