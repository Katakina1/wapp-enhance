package com.xforceplus.wapp.modules.xforceapi.service;

import java.util.Date;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSync;
import com.xforceplus.wapp.modules.xforceapi.entity.TXfRednotificationSyncReq;

/**
 * <pre>
 * 同步红字信息表功能服务
 * </pre>
 * 
 * @author just
 *
 */
public interface RedNotificationSyncService extends IService<TXfRednotificationSync> {

	/**
	 * <pre>
	 * 定时任务定时同步
	 * 每天每个税盘同步前两天的数据
	 * </pre>
	 * @return
	 */
	Response<Object> redNotificationSyncTask();
	
	/**
	 * 红字信息表同步
	 * 
	 * @param terminalUn
	 * @param deviceUn
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Response<Object> redNotificationSync(String terminalUn, String deviceUn, Date startDate, Date endDate);
	
	/**
	 * <pre>
	 * 定时任务获取结果
	 * 当请求时间大于6个小时以上的才自动同步
	 * </pre>
	 * @param serialNo
	 * @return
	 */
	Response<Object> getRedNotificationSyncResultTask();

	/**
	 * 获取同步结果
	 * 
	 * @param serialNo
	 * @return
	 */
	Response<Object> getRedNotificationSyncResult(String serialNo);
	Page<TXfRednotificationSync> paged(TXfRednotificationSyncReq vo);
}
