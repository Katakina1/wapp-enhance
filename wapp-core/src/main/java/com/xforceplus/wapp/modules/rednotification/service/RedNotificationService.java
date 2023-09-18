package com.xforceplus.wapp.modules.rednotification.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.enums.InvoiceOrigin;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationDeleteRequest;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationInfo;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationItem;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDao;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;

@Service
public class RedNotificationService extends ServiceImpl<TXfRedNotificationDao, TXfRedNotificationEntity> {
	@Autowired
	private RedNotificationMainMapper redNotificationMainMapper;
	@Autowired
	private RedNotificationItemService redNotificationItemService;
	// 红字权限控制🥱
	@Value("${wapp.rednotification.authUser:admin,test01,aqli,j0z01jq}")
	private String redAuthUser;

	/**
	 * 根据红字信息表编号查询红字信息表信息
	 * @param redNotificationNo
	 * @param isGetDetails 是否查询明细
	 * @return
	 */
	public Response<RedNotificationInfo> queryByRedNotificationInfo(String redNotificationNo, boolean isGetDetails) {
		RedNotificationInfo redNotificationInfo = null;
		RedNotificationMain redNotificationMain = getByRedNotification(redNotificationNo);
		if (redNotificationMain != null) {
			redNotificationInfo = new RedNotificationInfo();
			if (isGetDetails) {//是否查询明细
				redNotificationInfo.setRedNotificationItemList(getDetailsByApplyId(redNotificationMain.getId()));
			}
			redNotificationInfo.setRednotificationMain(redNotificationMain);
		}
		return Response.ok("成功", redNotificationInfo);
	}

	/**
	 * 根据红字信息表编号查询红字信息表主信息
	 * 
	 * @param redNotificationNo
	 * @return
	 */
	public RedNotificationMain getByRedNotification(String rednotificationNo) {
		LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TXfRedNotificationEntity::getRedNotificationNo, rednotificationNo);
		List<TXfRedNotificationEntity> list = getBaseMapper().selectList(queryWrapper);
		if (list == null || list.size() == 0) {
			return null;
		}
		RedNotificationMain redNotificationMain = redNotificationMainMapper.entityToMainInfo(list.get(0));
		if (StringUtils.isEmpty(redNotificationMain.getInvoiceDate())) {
			redNotificationMain.setInvoiceDate(DateUtils.getCurentIssueDate());
		}
		return redNotificationMain;
	}

	/**
	 * 根据红字信息表主信息ID，查询红字信息表明细信息
	 * 
	 * @param applyId
	 * @return
	 */
	public List<RedNotificationItem> getDetailsByApplyId(Long applyId) {
		LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, applyId);
		List<TXfRedNotificationDetailEntity> list = redNotificationItemService.getBaseMapper().selectList(queryWrapper);
		if (list == null || list.size() == 0) {
			return null;
		}
		return redNotificationMainMapper.entityToItemInfoList(list);
	}

	/**
	 * 根据红字信息表主信息查询红字信息表所有信息 包含主信息和明细信息
	 * 
	 * @param id
	 * @return
	 */
	public Response<RedNotificationInfo> detail(Long id) {
		TXfRedNotificationEntity tXfRedNotificationEntity = getBaseMapper().selectById(id);
		RedNotificationInfo redNotificationInfo = null;
		if (tXfRedNotificationEntity != null) {
			redNotificationInfo = new RedNotificationInfo();
			RedNotificationMain redNotificationMain = redNotificationMainMapper.entityToMainInfo(tXfRedNotificationEntity);
			if (StringUtils.isEmpty(redNotificationMain.getInvoiceDate())) {
				redNotificationMain.setInvoiceDate(DateUtils.getCurentIssueDate());
			}
			LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
			List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationItemService.getBaseMapper().selectList(queryWrapper);
			List<RedNotificationItem> redNotificationItems = redNotificationMainMapper.entityToItemInfoList(tXfRedNotificationDetailEntities);
			redNotificationInfo.setRedNotificationItemList(redNotificationItems);
			redNotificationInfo.setRednotificationMain(redNotificationMain);
		}
		return Response.ok("成功", redNotificationInfo);
	}

	/**
	 * 物理删除红字信息表信息
	 * 
	 * @param request
	 * @param username
	 * @param loginname
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Response<String> deleteById(RedNotificationDeleteRequest request, String username, String loginname) {
		// 判断用户信息是否有配置
		List<String> redAuthUserList = Arrays.asList(redAuthUser.split(","));
		if (!(redAuthUserList.contains(username) || redAuthUserList.contains(loginname))) {
			return Response.failed("无权限操作");
		}
		AtomicInteger totalDelete = new AtomicInteger(0);
		// 根据ID循环删除，先删除主表，再删除明细表
		if (request != null && request.getRedId() != null) {
			for (String id : request.getRedId()) {
				TXfRedNotificationEntity tXfRedNotificationEntity = getBaseMapper().selectById(id);
				if (!Objects.equals(tXfRedNotificationEntity.getApplyingStatus(),
						RedNoApplyingStatus.WAIT_TO_APPLY.getValue())) {
					throw new EnhanceRuntimeException("结算单[" + tXfRedNotificationEntity.getBillNo() + "]已申请，不允许操作");
				}
				if (!Objects.equals(tXfRedNotificationEntity.getInvoiceOrigin(), InvoiceOrigin.IMPORT.getValue())) {
					throw new EnhanceRuntimeException("结算单[" + tXfRedNotificationEntity.getBillNo() + "]类型错误，不允许操作");
				}
				if (tXfRedNotificationEntity != null && (getBaseMapper().deleteById(id) > 0)) {
					LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
					queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
					redNotificationItemService.getBaseMapper().delete(queryWrapper);
					totalDelete.addAndGet(1);
				}
			}
		}
		return Response.ok("删除成功数量：" + totalDelete.get());
	}

}