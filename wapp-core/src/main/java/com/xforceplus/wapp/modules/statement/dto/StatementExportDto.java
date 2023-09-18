package com.xforceplus.wapp.modules.statement.dto;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;

import lombok.Data;

/**
 * 1、结算单导入DTO
 * @author lenovo
 *
 */
@Data
public class StatementExportDto {

	/**
	 * 查询参数
	 */
	private StatementRequest request;
	/**
	 * 日志ID 
	 */
	private Long logId;
	/**
	 * 用户信息
	 */
	private UserEntity userEntity;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 *用户名称
	 */
	private String loginName;
}
