package com.xforceplus.wapp.modules.xforceapi.service;

import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncRequest;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResponse;
import com.xforceplus.wapp.modules.xforceapi.model.RedNotificationSyncResultResponse;

/**
 * <pre>
 * 对接xforce Api 服务
 * 所有集成平台的服务都写在这，不允许写入其他地方
 * </pre>
 * 
 * @author just
 *
 */
public interface JanusApiService {

	/**
	 * 税控设备：红字信息表同步
	 * @param requestParam
	 * @return
	 */
	RedNotificationSyncResponse redNotificationSync(RedNotificationSyncRequest requestParam);
	
	/**
	 * 税控设备：红字信息表同步结果获取
	 * @return
	 */
	RedNotificationSyncResultResponse getRedNotificationSyncResult(String serialNo);
}
