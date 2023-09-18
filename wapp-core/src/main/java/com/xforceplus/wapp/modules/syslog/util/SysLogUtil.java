package com.xforceplus.wapp.modules.syslog.util;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.enums.TXfSysLogModuleEnum;
import com.xforceplus.wapp.modules.syslog.dto.SysLogQueue;
import com.xforceplus.wapp.repository.entity.TXfSysLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 系统日志发送工具
 */
@Slf4j
public class SysLogUtil {
    public static void sendInfoLog(TXfSysLogModuleEnum moduleEnum,String businessLog){
        String traceInfo = StringUtils.EMPTY;
        String businessStatus = null;
        try {
            StackTraceElement[] stacks = new Throwable().getStackTrace();
            String clazzName = stacks[1].getClassName();
            String methodName = stacks[1].getMethodName();
            int lineNum = stacks[1].getLineNumber();
            businessStatus = "Y";
            traceInfo = new StringBuilder(clazzName.substring(clazzName.lastIndexOf(".")+1))
                    .append("#").append(methodName).append("#").append(lineNum)
                    .toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        sendLog(moduleEnum,traceInfo, StringUtils.EMPTY,businessStatus,businessLog);
    }

    public static void sendWarnLog(TXfSysLogModuleEnum moduleEnum,String businessLog){
        String traceInfo = StringUtils.EMPTY;
        String businessStatus = null;
        try {
            StackTraceElement[] stacks = new Throwable().getStackTrace();
            String clazzName = stacks[1].getClassName();
            String methodName = stacks[1].getMethodName();
            int lineNum = stacks[1].getLineNumber();
            businessStatus = "N";
            traceInfo = new StringBuilder(clazzName.substring(clazzName.lastIndexOf(".")+1))
                    .append("#").append(methodName).append("#").append(lineNum)
                    .toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        sendLog(moduleEnum,traceInfo, StringUtils.EMPTY,businessStatus,businessLog);
    }

    /**
     * 发送系统日志（不符合传参要求，将被丢弃）
     * @param moduleEnum 不超过50字符
     * @param sceneCode 不超过200字符
     * @param businessId 不超过100字符
     * @param businessStatus 不超过50字符
     * @param businessLog 不可超过1000个字符
     */
    public static void sendLog(TXfSysLogModuleEnum moduleEnum,
                               String sceneCode,
                               String businessId,
                               String businessStatus,
                               String businessLog){
        TXfSysLogEntity sysLogEntity = new TXfSysLogEntity();
        sysLogEntity.setModuleCode(moduleEnum.getValue());
        sysLogEntity.setModuleName(moduleEnum.getDes());
        sysLogEntity.setSceneCode(sceneCode);
        sysLogEntity.setThreadName(Thread.currentThread().getName());
        sysLogEntity.setBusinessId(businessId);
        sysLogEntity.setBusinessStatus(businessStatus);
        sysLogEntity.setBusinessLog(businessLog);
        sysLogEntity.setCreateTime(new Date());
        sendLog(sysLogEntity);
    }

    /**
     * 发送系统日志
     * @param sysLogEntity
     */
    public static void sendLog(TXfSysLogEntity sysLogEntity){
        sysLogEntity.setCreateTime(new Date());
        sysLogEntity.setThreadName(Thread.currentThread().getName());
        SysLogQueue.offer(sysLogEntity);
    }

    /**
     * 红字状态事件
     */
    public static void sendPreInvoiceRedNoLog(DeductRedNotificationEvent deductRedNotificationEvent, String businessStatus) {
        try {
            TXfSysLogEntity tXfSysLogEntity = new TXfSysLogEntity();
            tXfSysLogEntity.setModuleCode(TXfSysLogModuleEnum.PRE_INVOICE_RED_NO.getValue());
            tXfSysLogEntity.setModuleName(TXfSysLogModuleEnum.PRE_INVOICE_RED_NO.getDes());
            tXfSysLogEntity.setSceneCode(String.valueOf(deductRedNotificationEvent.getEvent()));
            tXfSysLogEntity.setBusinessId(String.valueOf(deductRedNotificationEvent.getBody().getPreInvoiceId()));
            tXfSysLogEntity.setBusinessExt1(deductRedNotificationEvent.getBody().getRedNotificationNo());
            tXfSysLogEntity.setBusinessLog(JSON.toJSONString(deductRedNotificationEvent));
            // 0-发送 1-接收
            tXfSysLogEntity.setBusinessStatus(businessStatus);
            sendLog(tXfSysLogEntity);
        } catch (Exception e) {
            log.error("sendSystemLog error:{},{}", JSON.toJSONString(deductRedNotificationEvent), businessStatus, e);
        }
    }

    /**
     * 获取调用点的跟踪信息，返回格式：类名#方法名#代码行号  如：SysLogUtil#main#87
     * @return
     */
    public static String getTraceInfo(){
        try {
            StackTraceElement[] stacks = new Throwable().getStackTrace();
            String clazzName = stacks[1].getClassName();
            String methodName = stacks[1].getMethodName();
            int lineNum = stacks[1].getLineNumber();
            return new StringBuffer(clazzName.substring(clazzName.lastIndexOf(".")+1))
                    .append("#").append(methodName).append("#").append(lineNum)
                    .toString();
        }catch (Exception e){
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(getTraceInfo());
    }
}
