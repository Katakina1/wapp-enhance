package com.xforceplus.wapp.modules.einvoice.service.impl;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.einvoice.dao.EinvoiceQueryDao;
import com.xforceplus.wapp.modules.einvoice.entity.EinvoiceQueryEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.export.ElectronInvoiceExcel;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceQueryService;
import com.xforceplus.wapp.modules.einvoice.util.DateTimeHelper;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO;

/**
 * @author marvin
 * 电票查询业务层接口实现
 */
@Service("einvoiceQueryService")
@Transactional
public class EinvoiceQueryServiceImpl implements EinvoiceQueryService {
    private final EinvoiceQueryDao einvoiceQueryDao;

    @Autowired
    public EinvoiceQueryServiceImpl(EinvoiceQueryDao einvoiceQueryDao) {

        this.einvoiceQueryDao = einvoiceQueryDao;
    }

    @Override
    public R queryInvoiceMsg(String schemaLabel, EinvoiceQueryEntity queryEntity, Long userId) {
        //获取当前页面
        final Integer page = queryEntity.getPage();

        //分页查询起始值
        queryEntity.setOffset((page - 1) * queryEntity.getLimit());

        queryEntity.setQsStartDate(DateTimeHelper.formatDate(new Date(Long.valueOf(queryEntity.getQsStartDate()))));
        queryEntity.setQsEndDate(DateTimeHelper.formatDate(new Date(Long.valueOf(queryEntity.getQsEndDate()))));

        List<String> taxNos = einvoiceQueryDao.selectGfTaxNo(schemaLabel, userId);
        taxNos.add(DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);

        queryEntity.setTaxNos(taxNos);

        List<ElectronInvoiceEntity> invoiceList = this.queryList(schemaLabel, queryEntity);

        ReportStatisticsEntity result = this.queryTotal(schemaLabel, queryEntity);

        PageUtils pageEntity =  new PageUtils(invoiceList, result.getTotalCount(), queryEntity.getLimit(), page);
        return R.ok().put("page",pageEntity).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @Override
    public List<ElectronInvoiceEntity> queryList(String schemaLabel, EinvoiceQueryEntity queryEntity) {
        return einvoiceQueryDao.queryInvoiceList(schemaLabel, queryEntity);
    }

    @Override
    public ReportStatisticsEntity queryTotal(String schemaLabel, EinvoiceQueryEntity queryEntity) {
        return einvoiceQueryDao.queryInvoiceListCount(schemaLabel, queryEntity);
    }

    @Override
    public void exportElectronInvoice(String schemaLabel, Map<String, Object> requestData, HttpServletResponse response, Long userId) {
        EinvoiceQueryEntity queryEntity = new EinvoiceQueryEntity();
        queryEntity.setGfTaxNo((String) requestData.get("gfTaxNo"));
        queryEntity.setInvoiceNo((String) requestData.get("invoiceNo"));
        queryEntity.setQsStartDate(DateTimeHelper.formatDate(new Date(Long.valueOf((String) requestData.get("qsStartDate")))));
        queryEntity.setQsEndDate(DateTimeHelper.formatDate(new Date(Long.valueOf((String) requestData.get("qsEndDate")))));
        queryEntity.setUserId(userId);
        List<String> taxNos = einvoiceQueryDao.selectGfTaxNo(schemaLabel, userId);
        taxNos.add(DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);

        queryEntity.setTaxNos(taxNos);

        List<ElectronInvoiceEntity> list = einvoiceQueryDao.queryInvoiceListForExport(schemaLabel, queryEntity);

        ElectronInvoiceExcel excel = new ElectronInvoiceExcel(list);
        excel.write(response, "电票导出");
    }
}
