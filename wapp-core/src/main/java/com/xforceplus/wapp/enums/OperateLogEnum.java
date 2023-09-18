package com.xforceplus.wapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Created by SunShiyong on 2021/10/25.
 * 日志操作枚举
 */
@AllArgsConstructor
@Getter
public enum OperateLogEnum {

    /**
     * 日志操作枚举
     */
    CREATE_SETTLEMENT("01", 0,"创建结算单"),
    APPLY_VERDICT("02",0,"申请不定案"),
    PASS_VERDICT("03",0,"不定案通过"),
    REJECT_VERDICT("04",0,"不定案驳回"),
    CONFIRM_SETTLEMENT("05",0,"确认结算单"),
    CANCEL_RED_NOTIFICATION_APPLY("06",0,"撤销红字信息表申请"),
    REJECT_CANCEL_RED_NOTIFICATION_APPLY("07",0,"拒绝撤销红字信息表申请"),
    AGREE_CANCEL_RED_NOTIFICATION_APPLY("08",0,"同意撤销红字信息表申请"),
    APPLY_RED_NOTIFICATION("09",0,"申请红字信息表（即重新拆票）"),
    UPLOAD_INVOICE("10",0,"上传红票"),
    BLUE_FLUSH_INVOICE("1001", 0, "蓝冲红票"),
    DELETE_INVOICE("1002", 0, "删除红票"),
    RECEIVED_INVOICE("11",0,"红票已签收"),

    CREATE_DEDUCT("12",1,"创建索赔单"),
    CLAIM_MATCH_ITEM_FAILED("1201", 1, "索赔单与索赔明细匹配失败："),
    CLAIM_MATCH_ITEM_SUCCESS("1202", 1, "索赔单已匹配索赔明细"),
    CLAIM_MATCH_ITEM_GOODS_TAX_NO_FAILED("1203", 1, "索赔明细"),
    CLAIM_MATCH_ITEM_GOODS_TAX_NO_SUCCESS("1204", 1, "索赔明细已匹配税收分类编码"),
    CLAIM_MATCH_ITEM_BLUE_INVOICE_FAILED("1205", 1, "索赔明细"),
    CLAIM_MERGE_SETTLEMENT("1205", 1, "索赔单已匹配结算单：结算单号"),
    CANCEL_DEDUCT("13",1,"索赔单已取消"),

    CREATE_AGREEMENT("14",1,"创建协议单"),
    AGREEMENT_MATCH_BLUE_INVOICE_FAILED("1401",1,"协议单"),
    AGREEMENT_MERGE_SETTLEMENT("1402", 1, "协议单已匹配结算单：结算单号"),
    LOCK_AGREEMENT("15",1,"协议单已锁定"),
    UNLOCK_AGREEMENT("16",1,"协议单已解锁"),
    CANCEL_AGREEMENT("17",1,"协议单已取消"),

    LOCK_SELLER_AGREEMENT("18",1,"供应商统一锁定"),

    UNLOCK_SELLER_AGREEMENT("19",1,"供应商统一解锁"),

    CREATE_EPD("18",1,"创建EPD单"),
    LOCK_EPD("19",1,"EPD单锁定"),
    UNLOCK_EPD("20",1,"EPD单解锁"),
    CANCEL_EPD("21",1,"EPD单已取消"),

    DESTROY_SETTLEMENT("22", 2, "结算单撤销"),
    DELETE_SETTLEMENT("2201", 2, "结算单删除"),
    SPLIT_AGAIN_SETTLEMENT("2202", 2, "结算单重新拆分"),
    SPLIT_AGAIN_SETTLEMENT_APPLY("2203", 2, "申请结算单重新拆分"),
    SPLIT_AGAIN_SETTLEMENT_APPLY_PASS("2204", 2, "申请结算单重新拆分审核：通过"),
    SPLIT_AGAIN_SETTLEMENT_APPLY_REJECT("2205", 2, "申请结算单重新拆分审核：驳回"),
    DESTROY_SETTLEMENT_APPLY("2206", 2, "申请撤销结算单"),
    DESTROY_SETTLEMENT_APPLY_PASS("2207", 2, "申请撤销结算单审核：通过"),
    DESTROY_SETTLEMENT_APPLY_REJECT("2208", 2, "申请撤销结算单审核：驳回"),

    DESTROY_SETTLEMENT_DEDUCT("2201", 2, "匹配的结算单已撤销：结算单号"),
    SETTLEMENT_CONFIRM_DEDUCT("23", 2, "结算单确认"),
    SETTLEMENT_SPLIT_PRE_INVOICE_SUCCESS("24", 2, "已匹配结算单：拆票成功，已生成预制发票"),
    SETTLEMENT_SPLIT_PRE_INVOICE_FAILED("25", 2, "已匹配结算单：拆票异常，未生成预制发票"),
    SETTLEMENT_RED_NOTIFICATION_APPLY_SUCCESS("26", 2, "已申请红字信息表"),
    SETTLEMENT_RED_NOTIFICATION_APPLY_PART_SUCCESS("2601", 2, "已部分申请红字信息表"),
    SETTLEMENT_RED_NOTIFICATION_CANCEL_SUCCESS("27", 2, "红字信息表已撤销"),
    SETTLEMENT_RED_NOTIFICATION_CANCEL_PART_SUCCESS("2701", 2, "红字信息表部分已撤销"),
    // 结算单上传发票进入部分开票状态
    SETTLEMENT_UPLOAD_PART_RED_INVOICE("28", 2, "已上传部分红票"),
    // 结算单上传发票进入已开票状态
    SETTLEMENT_UPLOAD_ALL_RED_INVOICE("29", 2, "已上传红票"),
    // 结算单删除发票进入部分开票状态
    SETTLEMENT_DELETE_PART_RED_INVOICE("30", 2, "删除部分已上传的红票"),
    // 结算单删除发票进入待开票状态
    SETTLEMENT_DELETE_ALL_RED_INVOICE("31", 2, "删除已上传的红票"),
    // 结算单蓝冲发票进入部分开票状态
    SETTLEMENT_BLUE_FLUSH_PART_RED_INVOICE("32", 2, "部分已上传的红票被蓝冲"),
    // 结算单蓝冲发票进入待开票状态
    SETTLEMENT_BLUE_FLUSH_ALL_RED_INVOICE("33", 2, "已上传的红票被蓝冲"),
    // 结算单已开票进入已完成状态
    SETTLEMENT_RED_INVOICE_DEAL_ALL("34", 2, "上传的红票已被签收"),
    ;

    private String operateCode;
    private Integer operateType;
    private String operateDesc;

    public static OperateLogEnum fromCode(String code) {
        return Arrays.stream(OperateLogEnum.values()).filter(operateLogEnum -> operateLogEnum.getOperateCode().equals(code)).findFirst().orElse(null);
    }
}
