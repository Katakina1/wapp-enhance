package com.xforceplus.wapp.modules.export.controller;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.export.Enum.ExcelServiceTypeEnum;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.service.IExcelExportService;
import com.xforceplus.wapp.modules.job.utils.JMSExprotRequestProducer;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.xforceplus.wapp.common.utils.R;

import javax.jms.Destination;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * ****************************************************************************
 * excel服务接口
 *
 * @author(作者)：xuyongyun
 * @date(创建日期)：2019年5月5日
 ******************************************************************************
 */
@RestController
@RequestMapping("excel")
public class ExcelController extends AbstractController {
	private static Logger log = LoggerFactory.getLogger(ExcelController.class);

	@Autowired
	IExcelExportService excelExportService;

	@Autowired
	private JMSExprotRequestProducer jmsExprotRequestProducer;
	@Value("${activemq.producer_status}")
	private String activemqProducerStatus;
	@Value("${activemq.producer_status_code}")
	private String activemqProducerStatusCode;
	@Value("${activemq.exprot_equest_queue_buyer}")
	private String activemqExprotEquestEueueBuyer;
	@Value("${activemq.exprot_equest_queue_sellers}")
	private String activemqExprotEquestEueueSellers;

	@Value("${activemq.exprot_rquest_queue_gfone}")
	private String exprotRquestQueuegfone;
	@Value("${activemq.exprot_rquest_queue_gftwo}")
	private String exprotRquestQueuegftwo;
	@Value("${activemq.exprot_rquest_queue_xfone}")
	private String exprotRquestQueuexfone;
	@Value("${activemq.exprot_rquest_queue_xftwo}")
	private String exprotRquestQueuexftwo;
	/**
	 * excel导出申请接口
	 *
	 * @param serviceType 业务类型
	 * @param condition   导出条件
	 * @return
	 * @since           1.0
	 */
	@PostMapping("/apply")
	@ResponseBody
	public ResponseEntity<?> excelExportApply(String serviceType,String condition) {


		//入参统一在入口处理
		Map<String,Object> pramsMap = new HashMap<String,Object>(16);
		pramsMap.put("userAccount", getUserId());
		pramsMap.put("userId", getUserId());
		pramsMap.put("userName", getLoginName());
		pramsMap.put("userCode", getUser().getUsercode());
		pramsMap.put("costQuery",getUser().getLoginname());

		if(StringUtils.isNotBlank(serviceType)) {
			pramsMap.put("serviceType", serviceType);
		}else {
			return ResponseEntity.ok(R.error("业务类型不能为空！"));
		}
		if(StringUtils.isNotBlank(condition)) {
			pramsMap.put("conditions", condition);

		}else {
			return ResponseEntity.ok(R.error("导出条件不能为空！"));
		}

		log.debug("pramsMap----" + pramsMap);
		try {
			//保存导出日志记录
			ExportLogEntity tdxExcelExprortlog =excelExportService.excelExportApply(pramsMap);
			pramsMap.put("id", tdxExcelExprortlog.getId());
			pramsMap.put("createDate", new Date());

			//编辑消息对象
			Map map=new HashMap();
			map.put("activemqStatus",activemqProducerStatus);//队列身份，这里是导出申请的生产者
			map.put("activemqProducerStatusCode",activemqProducerStatusCode);//队列身份，这里是导出申请的生产者
			map.put("message",pramsMap);
			if (activemqProducerStatusCode.equals("gfone")) {
				Destination destination = new ActiveMQQueue(exprotRquestQueuegfone);
				jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
			}else if(activemqProducerStatusCode.equals("gftwo")) {
				Destination destination = new ActiveMQQueue(exprotRquestQueuegftwo);
				jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
			}else if(activemqProducerStatusCode.equals("xfone")) {
				Destination destination = new ActiveMQQueue(exprotRquestQueuexfone);
				jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
			}else if(activemqProducerStatusCode.equals("xftwo")) {
				Destination destination = new ActiveMQQueue(exprotRquestQueuegftwo);
				jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
			}

//			if(ExcelServiceTypeEnum.getIndex(Integer.parseInt( serviceType))==8071) {
//                Destination destination = new ActiveMQQueue(activemqExprotEquestEueueBuyer);
//                jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
//            }else{
//                Destination destination = new ActiveMQQueue(activemqExprotEquestEueueSellers);
//                jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
//            }

			//调用方法另起线程执行
//			excelExportService.exportExcel(jsonObject.toString());
			return ResponseEntity.ok(R.ok().put("data", "申请导出成功，请在消息列表中下载导出文件。"));
		}catch(Exception e) {
			e.printStackTrace();
			log.error("", e);
			return ResponseEntity.ok(R.error("处理失败，请稍后重试！"));
		}


	}

	/**
	 *  给jsonObject base64加密成字符串
	 * @param json
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private  String responseResult(JSONObject json) throws UnsupportedEncodingException {
		String jsonString = null;
		if(json!=null) {
			log.debug("返回结果：" + json.toJSONString());
			jsonString = Base64.getEncoder().encodeToString(json.toJSONString().getBytes("UTF-8"));
		}
		return jsonString;
	}


}
