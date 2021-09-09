package com.xforceplus.wapp.modules.export.Enum;

/**
 *
 * ****************************************************************************
 * excel导出业务类型
 *
 * @author(作者)：xuyongyun
 * @date(创建日期)：2019年4月30日
 ******************************************************************************
 */
public enum ExcelServiceTypeEnum {
	/**
	 * 扫描处理
	 */
	SCAN_HANDLE(1,8071),
	/**
	 * 扫描处理报告
	 */
	SCAN_REPORT(2,8071),
	/**
	 * HOST匹配成功报告
	 */
	HOST_SUCC(3,8071),
	/**
	 * HOST匹配失败报告
	 */
	HOST_FAIL(4,8071),
	/**
	 * 匹配查询
	 */
	MATCHING_QUERY(5,5),
	/**
	 * 发票退票查询（供）
	 */
	INVOICE_REFUND(6,6),
	/**
	 * 协议查询(供)protocolXF.html
	 */
	PROTOCOL_QUERY(7,7),
	/**
	 * 扣款发票查询（销）paymentInvoiceQuery.html
	 */
	DEDUCTION_INVOICE(8,8),
	/**
	 * 订单查询（供）/poQuery.html
	 */
	PO_QUERY(9,9),
	/**
	 * 销方索赔导出
	 */
	CLAIM_EXPORT(10,10),

	/**
	 * 销方发票导出
	 */
	INVOICE_EXPORT(11,11),
	/**
	 * 销方付款信息导出
	 */
	PAYMENT_EXPORT(12,12),
	/***
	 * 结算查询导出
	 */
	COST_QUERY(13,13),
	/***
	 * 直接认证
	 */
    DIRECT_AUTH(14,8071),
	/***
	 * 税务传票清单
	 */
	AUTH_LIST(15,8071),
	/***
	 * 手工认证
	 */
	MANUAL_AUTH(16,8071),
	/***
	 * 发票认证报告
	 */
	AUTH_REPORT(17,8071),
	/***
	 * 认证查询
	 */
	AUTH_QUERY(18,8071),
	/***
	 * 发票归还
	 */
	INVOICE_RETURN(19,8071),
	/***
	 * 借阅记录查询
	 */
	BORROW_RECORD(20,8071),

	/***
	 * 录入装箱号
	 */
	PACK_NUMDER(21,8071),
	/***
	 *费用打印退单号
	 */
	COST_GENERATE(22,8071),
	/***
	 * 订单查询(购)
	 */
	PO_INQUIRY(23,8071),
	/***
	 * 索赔查询
	 */
	CLAIML_QUIRY(24,8071),
	/***
	 * 付款信息查询
	 */
	PAYMENT_QUIRY(25,8071),
	/***
	 * 付款信息查询
	 */
	PROTOCPL(26,8071),
	/***
	 * 发票综合查询
	 */
	INVOICE_LIST(27,8071),
    /***
     * 发票退票查询导出
     */
    REBATENO_LIST(28,8071),
    /***
     * 匹配查询导出
     */
    MATCH_LIST(29,8071),
	/***
	 * 打印退单封面查询导出
	 */
	INVOICE_TICKETS(30,8071),
	/***
	 * 退单导出
	 */
	GENERATE_LIST(31,8071),

	/***
	 * 问题单导出
	 */
	QUESTION_LIST(32,8071),
	/***
	 * 审核红票资料导出
	 */
	EXAMINE_LIST(33,8071),
	/***
	 * 批量导入匹配关系导出
	 */
	PLMATCH_LIST(34,8071),
	/***
	 * 查询开红票资料导出
	 */
	REDINVOICE_LIST(35,35),
	/***
	 * 批量导入红票信息导出
	 */
	IMPORTREDINVOICE_LIST(36,8071),
	/***
	 * 上传红字通知单导出
	 */
	UPREDINVOICE_LIST(37,8071),
	/***
	 * 数据发票统计导出
	 */
	DATAINVOICE_LIST(38,8071),
	/***
	 * 供应商问题发票统计和比率
	 */
	ISSUEINVOICE_LIST(39,8071),
	/***
	 * 实物发票提交明细
	 */
	MATTERINVOICE_LIST(40,8071),
	/***
	 * 商品退换票
	 */
	RETURNINVOICE_LIST(41,8071),
	/***
	 * 供应商信息查询
	 */
	VENDER_LIST(42,8071),
	/***
	 * 异常报告
	 */
	EXCEPTION_LIST(43,8071),
	/***
	 * 结算查询
	 */
	SETTLE_LIST(44,8071),
    /***
     * 费用扫描处理导出
     */
    COSTQUERY_LIST(45,8071),
	/***
	 * 费用扫描报告导出
	 */
	SIGN_QUERY(46,8071),
	/***
	 * 发票处理状态报告导出
	 */
	INVOICE_STATUS(47,8071),
	/***
	 * 扣款发票上传导出
	 */
	INVOICE_UPLOAD(48,8071),
    /***
     * 知识中心
     */
    KNOWCENTER_LIST(49,8071),
	/***
	 * 查询开红票资料（外部红票）
	 */
	OPENRED_LIST(50,8071),
	/***
	 * 实物发票提交统计
	 */
	SHIWU_LIST(51,8071),
	/***
	 * 未补明细发票列表
	 */
	NODETAIL_LIST(52,8071),
	/***
	 * 公告查询
	 */
	GONGGAO_LIST(53,8071),
	/***
	 * 采集列表
	 */
	CAIJI_LIST(54,8071),

	/***
	 * 费用打印封面
	 */
	COST_TICKETS(55,8071),

	/***
	 * 写屏维护导出
	 */
	SCREE_HOST(56,8071),

	/***
	 * 采购问题单导出
	 */
	CGWTD_CX(57,8071),
	/***
	 *红票信息购方导出
	 */
	RED_GFIN(58,8071),
	/***
	 * 红票信息销方导出
	 */
	RED_XFIN(59,59),

	ARIBA_AUTH_LIST(60,8071),
	ARIBA_BILL_LIST(61,8071),
	BIND_NUMBER(62,8071);
	private int serviceType;
	private int index;

	private ExcelServiceTypeEnum(int serviceType,int index) {
		this.serviceType = serviceType;
		this.index = index;
	}
	public static int getIndex(Integer value) {
		ExcelServiceTypeEnum[] businessModeEnums = values();
		for (ExcelServiceTypeEnum businessModeEnum : businessModeEnums) {
			if (businessModeEnum.getServiceType()==value) {
				return businessModeEnum.getIndex();
			}
		}
		return 0;
	}
	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
