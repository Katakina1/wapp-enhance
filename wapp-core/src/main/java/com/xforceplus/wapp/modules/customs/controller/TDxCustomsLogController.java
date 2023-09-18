package com.xforceplus.wapp.modules.customs.controller;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.dto.customs.CustomsLogRequest;
import com.xforceplus.wapp.modules.customs.service.TDxCustomsLogService;
import com.xforceplus.wapp.repository.entity.TDxCustomsLogEntity;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 海关票勾选日志查询
 * @Author pengtao
 * @return
**/
@Slf4j
@RestController
@Api(tags = "海关缴款书日志")
@RequestMapping(EnhanceApi.BASE_PATH + "/customslog")
public class TDxCustomsLogController {
	@Autowired
	private TDxCustomsLogService tDxCustomsLogService;
	
	/**
	 * 根据查询条件查询
	 */
	@PostMapping("/list")
	public R<PageResult<TDxCustomsLogEntity>> list(@RequestBody CustomsLogRequest request){
		log.info("海关缴款书日志查询--请求参数{}", JSON.toJSON(request));
		//查询列表数据
		PageResult<TDxCustomsLogEntity> page = tDxCustomsLogService.paged(request);
		return R.ok(page);
	}
	
	
	/**
	 * 根据ID查询详情
	 */
	@GetMapping("/info/{id}")
	public R<TDxCustomsLogEntity> info(@PathVariable("id") Long id){
		TDxCustomsLogEntity tDxCustomsLog = tDxCustomsLogService.getById(id);
		return R.ok(tDxCustomsLog);
	}

}
