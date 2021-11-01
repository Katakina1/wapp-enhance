package com.xforceplus.wapp.modules.rednotification.log;

/**
 * 操作枚举类
 */
public enum LogOperateType {
    /**
     * 描述：日志操作类型定义
     */
    QUERY(1, "查询"),ADD(2, "新增"), MODIFY(3, "修改"),DELETE(4, "删除"),
    UPLOAD(5, "上传"), DOWNLOAD(6, "下载"), IMPORT(7, "导入"), EXPORT(8, "导出"),
    OTHER(9,"其它操作");

    private final int code;
    private final String msg;

    LogOperateType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 枚举类型转换，由于构造函数获取了枚举的子类enums，让遍历更加高效快捷
     * @param code 数据库中存储的自定义code属性
     * @return code对应的枚举类
     */
    public static LogOperateType locateEnum(int code) {
        for(LogOperateType status : LogOperateType.values()) {
            if(status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的枚举类型：" + code);
    }
}
