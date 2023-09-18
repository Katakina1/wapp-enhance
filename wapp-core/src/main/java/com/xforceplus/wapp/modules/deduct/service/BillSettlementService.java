package com.xforceplus.wapp.modules.deduct.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xforceplus.wapp.repository.dao.TXfBillSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillSettlementService extends ServiceImpl<TXfBillSettlementDao, TXfBillSettlementEntity> {
	
	@Autowired
	private TXfBillSettlementDao billSettlementDao;

	/**
	 * insert
	 * @param settlementEntity
	 * @return
	 */
	public boolean addBillSettlement(TXfBillSettlementEntity settlementEntity) {
		if(settlementEntity == null) {
			throw new NullPointerException("TXfBillSettlementEntity is null");
		}
		settlementEntity.setCreateTime(new Date());
		settlementEntity.setUpdateTime(new Date());
		return billSettlementDao.insert(settlementEntity) > 0;
	}
	
	public TXfBillSettlementEntity bulidBillSettlementEntity(TXfSettlementEntity tXfSettlementEntity, TXfBillDeductEntity item) {
		TXfBillSettlementEntity settlementEntity = new TXfBillSettlementEntity();
		settlementEntity.setBillId(item.getId());
		settlementEntity.setBusinessNo(item.getBusinessNo());
		settlementEntity.setBusinessType(item.getBusinessType());
		settlementEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
		settlementEntity.setStatus(0);
		settlementEntity.setBiilStatus(item.getStatus());
		settlementEntity.setSettlmentStatus(tXfSettlementEntity.getSettlementStatus());
		settlementEntity.setSellerNo(tXfSettlementEntity.getSellerNo());
		settlementEntity.setSellerName(tXfSettlementEntity.getSellerName());
		settlementEntity.setPurchaserNo(tXfSettlementEntity.getPurchaserNo());
		settlementEntity.setPurchaserName(tXfSettlementEntity.getPurchaserName());
		settlementEntity.setBiilAmountWithoutTax(item.getAmountWithoutTax());
		settlementEntity.setBiilTaxAmount(item.getTaxAmount());
		settlementEntity.setSettlmentAmountWithoutTax(tXfSettlementEntity.getAmountWithoutTax());
		settlementEntity.setSettlmentTaxAmount(tXfSettlementEntity.getTaxAmount());
		return settlementEntity;
	}

	/**
	 * batch insert
	 * @param list
	 * @return
	 */
	public boolean addBillSettlementBatch(List<TXfBillSettlementEntity> list) {
		if (list == null) {
			throw new NullPointerException("list is null");
		}
		AtomicInteger atomicInteger = new AtomicInteger(0);
		list.forEach(item -> {
			atomicInteger.getAndAdd(billSettlementDao.insert(item));
		});
		log.info("addBillSettlementBatch list size:{},save size:{}", list.size(), atomicInteger.get());
		return atomicInteger.get() > 0;
	}
	
	/**
	 * 1、作废业务单和结算单关系，作废后，无法恢复
	 * 
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	public boolean cancelBillSettlementStatus(String businessNo, String settlementNo, Integer businessType) {
		log.info("cancelBillSettlementStatus businessNo:{},settlementNo:{},businessType:{}", businessNo, settlementNo, businessType);
		if(StringUtils.isBlank(businessNo)) {
			throw new NullPointerException("businessNo is null");
		}
		if(StringUtils.isBlank(settlementNo)) {
			throw new NullPointerException("settlementNo is null");
		}
		return billSettlementDao.updateByBusinessNoAndSettlementNo(businessNo, settlementNo, businessType, 1) > 0;
	}
	
	/**
	 * 1、根据businessNo查询
	 * @param businessNo
	 * @param businessType
	 * @return
	 */
	public List<TXfBillSettlementEntity> queryByBusinessNo(String businessNo, Integer businessType){
		return billSettlementDao.queryByBusinessNo(businessNo, businessType);
	}
	
	/**
	 * 1、根据结算单号查询
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	public List<TXfBillSettlementEntity> queryBySettlementNo(String settlementNo, Integer businessType){
		return billSettlementDao.queryBySettlementNo(settlementNo, businessType);
	}
	
	/**
	 * 1、跟进业务单类型，结算单号，业务单号查询，唯一
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	public TXfBillSettlementEntity queryByBusinessNoAndSettlementNo(String businessNo, String settlementNo, Integer businessType) {
		List<TXfBillSettlementEntity> list = billSettlementDao.queryByBusinessNoAndSettlementNo(businessNo, settlementNo, businessType);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
}
