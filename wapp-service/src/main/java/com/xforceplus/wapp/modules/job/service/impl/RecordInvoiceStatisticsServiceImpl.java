package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.modules.job.dao.RecordInvoiceStatisticsDao;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoiceStatistics;
import com.xforceplus.wapp.modules.job.pojo.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.job.service.RecordInvoiceStatisticsService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
public class RecordInvoiceStatisticsServiceImpl implements RecordInvoiceStatisticsService {
	private static final Logger logger = Logger.getLogger(RecordInvoiceStatisticsServiceImpl.class);
	
	@Autowired
	private RecordInvoiceStatisticsDao recordInvoiceStatisticsDao;
	
	@Transactional(rollbackFor = Exception.class)
	public void countTaxRate(List<InvoiceDetailInfo> recordInvoiceDetail,
			String invoiceCode, String invoiceNo,String linkName) {
		try {
			// 1,查看有几种税率
			Set<String>  set = new HashSet<String>();
			for (InvoiceDetailInfo rs : recordInvoiceDetail) {
				set.add(rs.getTaxRate());
			}
			// 2,计算每种税率金额合计
			for (String rate : set) {
				TDxRecordInvoiceStatistics rs = new TDxRecordInvoiceStatistics();
				BigDecimal typeAmount = new BigDecimal("0");
				BigDecimal typeTaxAmount = new BigDecimal("0");
				for (InvoiceDetailInfo mx : recordInvoiceDetail) {
					if (rate .equals( mx.getTaxRate())&& !mx.getGoodsName().equals("原价合计") && !mx.getGoodsName().equals("折扣额合计")&& !mx.getGoodsName().equals("(详见销货清单)")&&(StringUtils.isNotBlank(mx.getTaxRate()))) {
						typeAmount = typeAmount.add(new BigDecimal(mx.getDetailAmount()));
						typeTaxAmount = typeTaxAmount.add(new BigDecimal(mx.getTaxAmount()));
					}
				}
				rs.setInvoiceCode(invoiceCode);
				rs.setInvoiceNo(invoiceNo);
				try {
					rs.setTaxRate(new BigDecimal(rate));
				}catch (Exception e){
					rs.setTaxRate( new BigDecimal("0"));
				}
				rs.setDetailAmount(typeAmount);
				rs.setTaxAmount(typeTaxAmount);
				rs.setTotalAmount(typeAmount.add(typeTaxAmount));
				// 存入税率表
				recordInvoiceStatisticsDao.saveStatistics(rs,linkName);
			}
		} catch (Exception e) {
			logger.error("RecordInvoiceStatisticsService.countTaxRate:err",e);
		}
	}
		

}
