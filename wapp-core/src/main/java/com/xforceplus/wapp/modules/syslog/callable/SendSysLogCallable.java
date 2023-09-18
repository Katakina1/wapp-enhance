package com.xforceplus.wapp.modules.syslog.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.xforceplus.wapp.modules.syslog.dto.SysLogQueue;
import com.xforceplus.wapp.modules.syslog.service.SysLogBatchService;
import com.xforceplus.wapp.repository.entity.TXfSysLogEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志接收处理
 */
@Slf4j
public class SendSysLogCallable implements Callable<Boolean> {

	private static int batchSendSize = 200;

	private SysLogBatchService sysLogBatchService;
	private IDSequence idSequence;

	public SendSysLogCallable() {
		this.sysLogBatchService = (SysLogBatchService) SpringUtil.getBean("sysLogBatchService");
		this.idSequence = (IDSequence) SpringUtil.getBean("idSequence");
	}

	@Override
	public Boolean call() {
		try {
			List<TXfSysLogEntity> sendSysLogList;
			TXfSysLogEntity sysLogEntity;
			while (true) {
				log.info("系统日志收集程序启动.........");
				if (SysLogQueue.size() > 0) {
					sendSysLogList = new ArrayList<>();
					while (SysLogQueue.size() > 0) {
						sysLogEntity = SysLogQueue.poll();
						// 校验日志信息
						if (StringUtils.isBlank(sysLogEntity.getModuleCode())) {
							continue;
						}
						if (StringUtils.isBlank(sysLogEntity.getSceneCode())) {
							continue;
						}
						if (sysLogEntity.getModuleCode().length() > 50) {
							continue;
						}
						if (sysLogEntity.getSceneCode().length() > 200) {
							continue;
						}
						if (sysLogEntity.getBusinessLog() != null && sysLogEntity.getBusinessLog().length() > 1000) {
							continue;
						}
						sysLogEntity.setId(idSequence.nextId());
						sendSysLogList.add(sysLogEntity);
					}
					if (sendSysLogList.size() > 0) {
						log.info("系统日志收集数量：" + sendSysLogList.size());
						boolean saveOk = sysLogBatchService.saveBatch(sendSysLogList);
						log.info("系统日志收集结果：" + saveOk);
					}
					if (SysLogQueue.size() < batchSendSize) {
						Thread.sleep(10 * 1000);// 睡眠10秒
					}
				} else {
					Thread.sleep(30 * 1000);// 睡眠30秒
				}
			}
		} catch (Exception e) {
			log.error("call error:", e);
			return false;
		}
	}
}
