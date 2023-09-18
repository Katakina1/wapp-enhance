package com.xforceplus.wapp.modules.xforceapi.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.repository.dao.TDxNgsInputInvoiceDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationService;
import com.xforceplus.wapp.modules.xforceapi.dao.XfRednotificationSyncDao;
import com.xforceplus.wapp.modules.xforceapi.dao.XfRednotificationSyncReqDao;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSync;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSyncReq;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncRequest;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResponse;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResultResponse;
import com.xforceplus.wapp.modules.xforceapi.service.JanusApiService;
import com.xforceplus.wapp.modules.xforceapi.service.RedNotificationSyncService;
import org.springframework.util.ObjectUtils;

@Service
public class RedNotificationSyncServiceImpl  extends ServiceImpl<XfRednotificationSyncDao, TXfRednotificationSync> implements RedNotificationSyncService{

	private final static Logger log = LoggerFactory.getLogger(RedNotificationSyncServiceImpl.class);
	
	@Autowired
	private JanusApiService janusApiService;
	@Autowired
	private XfRednotificationSyncDao rednotificationSyncDao;
	@Autowired
	private XfRednotificationSyncReqDao rednotificationSyncReqDao;
	@Autowired
	private RedNotificationService redNotificationService;
	//配置的自动回去的税盘编码
	@Value("${wapp.rednotification.authSync.deviceUn:8TKSQZTM|JIOCPEUF}")
	private String autoSyncTerminalUnConfig;
	
	@Override
	public Response<Object> redNotificationSync(String terminalUn, String deviceUn, Date startDate, Date endDate) {
		log.info("redNotificationSync terminalUn:{}, deviceUn:{}, startDate:{}, endDate:{}", terminalUn, deviceUn, DateUtils.format(startDate), DateUtils.format(endDate));
		if(StringUtils.isBlank(terminalUn) && StringUtils.isBlank(deviceUn)) {
			return Response.failed("terminalUn和deviceUn 不能同时为空");
		}
		if(startDate == null || endDate == null) {
			return Response.failed("起始时间和结束时间不能为空");
		}
		if(endDate.getTime() - startDate.getTime() < 0) {
			return Response.failed("起始时间不能比结束时间大");
		}
		if(endDate.getTime() - startDate.getTime() >= 5 * 24 * 60 * 60 * 1000) {
			return Response.failed("起始时间和结束时间间隔不能超过5天");
		}
		RedNotificationSyncRequest requestParam = new RedNotificationSyncRequest();
		String serialNo = UUID.randomUUID().toString().replaceAll("-", "");
		requestParam.setSerialNo( serialNo);
		requestParam.setTerminalUn(terminalUn);
		requestParam.setDeviceUn(deviceUn);
		requestParam.setStartDate(DateUtils.format(startDate, DateUtils.DATE_PATTERN_EN));
		requestParam.setEndDate(DateUtils.format(endDate, DateUtils.DATE_PATTERN_EN));
		RedNotificationSyncResponse response = janusApiService.redNotificationSync(requestParam);
		boolean syncReuslt = insertRednotificationSyncReq(requestParam, response);
		return syncReuslt && response.isOk() ? Response.ok("获取请求成功") : Response.failed(response.getMessage());
	}

	@Override
	public Response<Object> getRedNotificationSyncResult(String serialNo) {
		RedNotificationSyncResultResponse response = janusApiService.getRedNotificationSyncResult(serialNo);
		AtomicInteger successNum = new AtomicInteger(0);
		if(response.isOk()) {
			response.getResult().getRedNotificationList().forEach(item->{
				insertRednotificationSync(response.getResult().getSerialNo(), item);
				successNum.addAndGet(1);
			});
		}
		return response.isOk() ? Response.ok("获取结果成功，总记录数:" + successNum.get()) : Response.failed(response.getMessage());
	}
	
	
	@Override
	@Scheduled(cron = "32 23 4 * * ?")
	public Response<Object> redNotificationSyncTask() {
		String [] deviceUnList = autoSyncTerminalUnConfig.split("|");
		for (String deviceUn : deviceUnList) {
			Date currentDate = DateUtils.getNowDateShort();
			this.redNotificationSync("", deviceUn,  DateUtils.addDate(currentDate, -3), DateUtils.addDate(currentDate, -1));
		}
		return Response.ok("触发同步任务：" + autoSyncTerminalUnConfig);
	}

	@Override
	@Scheduled(cron = "32 23 11,22 * * ?")
	public Response<Object> getRedNotificationSyncResultTask() {
		Date currentDate = DateUtils.getNowDateShort();
		List<TXfRednotificationSyncReq> list = this.getRedNotificationByNoResult(DateUtils.addDate(currentDate, -2), DateUtils.addDate(currentDate, 1));
		if(list == null || list.size() == 0) {
			return Response.ok("无需要跑的任务！");
		}
		list.forEach(item ->{
			this.getRedNotificationSyncResult(item.getRespSerialNo());
			//休眠10秒，防止太快请求
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		return Response.ok("触发同步任务：" + list.size());
	}
	
	
	/**
	 * 根据时间段，查询没有获取通过结果的单据
	 * @param createDate
	 * @param endDate
	 * @return
	 */
	private List<TXfRednotificationSyncReq> getRedNotificationByNoResult(Date createDate, Date endDate){
		LambdaQueryWrapper<TXfRednotificationSyncReq> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TXfRednotificationSyncReq::getReqResult, "TXWR000000");
		wrapper.between(TXfRednotificationSyncReq::getCreatedTime, createDate, endDate);
		wrapper.notIn(TXfRednotificationSyncReq::getRespSerialNo, "TXWR000000");
		return rednotificationSyncReqDao.selectList(wrapper);
	}
	
	/**
	 * 根据请求流水号查询，用来处理结果
	 * @param respSerialNo
	 * @return
	 */
	private List<TXfRednotificationSyncReq> getRedReqByRespSerialNo(String respSerialNo){
		LambdaQueryWrapper<TXfRednotificationSyncReq> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TXfRednotificationSyncReq::getRespSerialNo, respSerialNo);
		return rednotificationSyncReqDao.selectList(wrapper);
	}
	
	/**
	 * 保存同步红字信息表记录
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean insertRednotificationSyncReq(RedNotificationSyncRequest request, RedNotificationSyncResponse response) {
		log.info("insertRednotificationSyncReq param:{}, response:{}", JSON.toJSONString(request), JSON.toJSONString(response));
		TXfRednotificationSyncReq redNotificationSyncReq = new TXfRednotificationSyncReq();
		BeanUtils.copyProperties(request, redNotificationSyncReq);
		redNotificationSyncReq.setCreatedTime(new Date());
		redNotificationSyncReq.setUpdatedTime(redNotificationSyncReq.getCreatedTime());
		redNotificationSyncReq.setRespSerialNo(response.isOk() ? response.getResult().getSerialNo() : "");
		redNotificationSyncReq.setReqResult(response.getCode());
		return rednotificationSyncReqDao.insert(redNotificationSyncReq) > 0;
	}

	/**
	 * <pre>
	 * 保存同步的红字信息表信息
	 * </pre>
	 * @param serialNo
	 * @param redNotification
	 * @return
	 */
	private boolean insertRednotificationSync(String serialNo, RedNotificationSyncResultResponse.RedNotification redNotification) {
		log.info("insertRednotificationSync param:{}", JSON.toJSONString(redNotification));
		TXfRednotificationSync rednotificationSync = new TXfRednotificationSync();
		BeanUtils.copyProperties(redNotification, rednotificationSync);
		rednotificationSync.setSerialNo(serialNo);
		//处理平台异常字段场景
		if(StringUtils.isBlank(rednotificationSync.getPurchaserTaxCode())) {
			rednotificationSync.setPurchaserTaxCode(redNotification.getPurchaseTaxCode());
		}
		if(redNotification.getAmount() != null) {
			rednotificationSync.setAmountWithTax(redNotification.getAmount().getAmountWithTax());
			rednotificationSync.setAmountWithoutTax(redNotification.getAmount().getAmountWithoutTax());
			rednotificationSync.setTaxAmount(redNotification.getAmount().getTaxAmount());
		}
		rednotificationSync.setCreatedTime(new Date());
		rednotificationSync.setUpdatedTime(rednotificationSync.getCreatedTime());
		//查询红字信息表中的数据
		RedNotificationMain redNotificationMain = redNotificationService.getByRedNotification(rednotificationSync.getRedNotificationNo());
		if(redNotificationMain != null) {//开启校验
			//校验销方信息
			StringBuilder taxNoMsg = new StringBuilder();
			if(!StringUtils.equalsIgnoreCase(redNotificationMain.getSellerTaxNo(), rednotificationSync.getSellerTaxCode())) {
				taxNoMsg.append("税号不一致,红字：" ).append(redNotificationMain.getSellerTaxNo()).append(";");
			}
			if(!StringUtils.equalsIgnoreCase(redNotificationMain.getSellerName(), rednotificationSync.getSellerName())) {
				taxNoMsg.append("名称不一致,红字：" ).append(redNotificationMain.getSellerName()).append(";");
			}
			if(taxNoMsg.length() >= 1) {
				rednotificationSync.setTaxNoResult("fail");
				rednotificationSync.setTaxNoResultMsg(taxNoMsg.toString());
			}
			//校验金额
			StringBuilder amountMsg = new StringBuilder();
			if(rednotificationSync.getAmountWithTax().compareTo(redNotificationMain.getAmountWithTax()) != 0) {
				amountMsg.append("含税不一致,红字：" ).append(redNotificationMain.getAmountWithTax()).append(";");
			}
			if(rednotificationSync.getAmountWithoutTax().compareTo(redNotificationMain.getAmountWithoutTax()) != 0) {
				amountMsg.append("不含税不一致,红字：" ).append(redNotificationMain.getAmountWithoutTax()).append(";");
			}
			if(rednotificationSync.getTaxAmount().compareTo(redNotificationMain.getTaxAmount()) != 0) {
				amountMsg.append("税额不一致,红字：" ).append(redNotificationMain.getTaxAmount()).append(";");
			}
			if(amountMsg.length() >= 1) {
				rednotificationSync.setAmountResult("fail");
				rednotificationSync.setAmountResultMsg(amountMsg.toString());
			}
			rednotificationSync.setBillNo(redNotificationMain.getBillNo());
		}else {
			rednotificationSync.setBillNo("");
		}
		//更新请求的同步请求的状态
		List<TXfRednotificationSyncReq> reqList = this.getRedReqByRespSerialNo(serialNo);
		if (reqList != null) {
			reqList.forEach(item -> {
				item.setRespResult(serialNo);
				item.setRespResultMsg("获取结果成功");
				item.setUpdatedTime(new Date());
				updateRedReqResult(item);
			});
		}
		//判断红字信息表是否存在
		TXfRednotificationSync dbRednotification = queryTXfRednotificationSyncByRednotificationNo(rednotificationSync.getRedNotificationNo());
		if(dbRednotification != null) {
			rednotificationSync.setCreatedTime(null);
			rednotificationSync.setUpdatedTime(new Date());
			rednotificationSync.setId(dbRednotification.getId());
			return rednotificationSyncDao.updateById(rednotificationSync) > 0;
		}
		return rednotificationSyncDao.insert(rednotificationSync) > 0;
	}
	
	private boolean updateRedReqResult(TXfRednotificationSyncReq rednotificationSyncReq) {
		return rednotificationSyncReqDao.updateById(rednotificationSyncReq) > 0;
	}
	
	/**
	 * 根据红字信息表查询信息是否存在
	 * @param rednotificationNo
	 * @return
	 */
	public TXfRednotificationSync queryTXfRednotificationSyncByRednotificationNo(String rednotificationNo) {
		LambdaQueryWrapper<TXfRednotificationSync> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TXfRednotificationSync::getRedNotificationNo, rednotificationNo);
		List<TXfRednotificationSync> list = rednotificationSyncDao.selectList(wrapper);
		return (list == null || list.size() == 0) ? null : list.get(0); 
	}

	/**
	 * 分页查询
	 * @param vo
	 * @return
	 */
	@Override
	public Page<TXfRednotificationSync> paged(TXfRednotificationSyncReq vo) {
		LambdaQueryWrapper<TXfRednotificationSync> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(StringUtils.isNotEmpty(vo.getSerialNo()), TXfRednotificationSync::getSerialNo, vo.getSerialNo());

		Page<TXfRednotificationSync> pageRsult = this.page(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
		return pageRsult;
	}
}
