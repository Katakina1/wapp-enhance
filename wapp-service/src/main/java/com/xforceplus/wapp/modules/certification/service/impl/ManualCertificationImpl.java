package com.xforceplus.wapp.modules.certification.service.impl;


import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.certification.dao.ManualCertificationDao;
import com.xforceplus.wapp.modules.certification.dao.SubmitCheckDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationExcelEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountEntity;
import com.xforceplus.wapp.modules.certification.service.DkCountService;
import com.xforceplus.wapp.modules.certification.service.ManualCertificationService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.signin.dao.HandWorkRepository;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * 手工认证
 * @author kevin.wang
 * @date 4/14/2018
 */
@Service
public class ManualCertificationImpl implements ManualCertificationService {

    @Value("${currentTaxPeriod}")
    private String taxPeriod; // 税款所属期判定日
    @Autowired
    private DkCountService dkCountService;
    @Autowired
    private HandWorkRepository handWorkRepository;
    @Autowired
    private SubmitCheckDao submitCheckDao;

    private ManualCertificationDao manualCertificationDao;
    private int  count=0;
    private ReportStatisticsEntity reportStatisticsEntity =new ReportStatisticsEntity();
    @Autowired
    public ManualCertificationImpl(ManualCertificationDao manualCertificationDao) {
        this.manualCertificationDao = manualCertificationDao;
    }

    @Override
    public List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<InvoiceCertificationEntity> entitys = manualCertificationDao.queryList(schemaLabel, map);
        return entitys;
    }

    @Override
    public ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }

        return manualCertificationDao.queryTotal(schemaLabel,map);
    }

    @Override
    public String manualCertification(String schemaLabel,String ids,String loginName,String userName,long userId){
        Boolean flag=true;
        String rzhBelongDate="";
        String gfTaxNo = "";
        int total=0;
        BigDecimal invoiceAmount= new BigDecimal(0);
        BigDecimal taxAmount= new BigDecimal(0);

        if (ids.split(",").length > 0) {
            Map<String,Object> params = new HashMap<>();
            params.put("userId",userId);
            List<TDxDkCountEntity> dkList = dkCountService.checkDksh(params);
            List<String> gfshList1 = new ArrayList<>();
            for (TDxDkCountEntity cur : dkList){
                String skssq = cur.getSkssq();
                String year = skssq.substring(0,4);
                String mon = skssq.substring(4);
                LocalDate localDate =LocalDate.of(Integer.valueOf(year),Integer.valueOf(mon),01);
                LocalDate localDate1 = localDate.plusMonths(1);
                LocalDate localDate2 = LocalDate.now();
                if ("0".equals(cur.getTjStatus()) || "3".equals(cur.getTjStatus()) ||localDate2.isBefore(localDate1)){
                    gfshList1.add(cur.getTaxno());
                }
            }

            final String[] id = ids.split(",");
            for (String anId : id) {
                RecordInvoiceDataEntity record = handWorkRepository.getInvoiceDataById(schemaLabel,Long.valueOf(anId));
                if (!gfshList1.contains(record.getGfTaxNo())){
                    if (gfTaxNo.indexOf(record.getGfTaxNo())==-1){
                        gfTaxNo = gfTaxNo + record.getGfTaxNo()+"</br>";
                    }
                    continue;
                }
                //获取税款所属期
                rzhBelongDate=getCurrentTaxPeriod(schemaLabel,anId);
                if(null==rzhBelongDate){
                    rzhBelongDate="";
                }
                flag=manualCertificationDao.manualCertification(schemaLabel,anId,loginName,userName,rzhBelongDate,record.getTaxAmount())>0;
                if(!flag){
                    continue;
                }
                ++total;
                invoiceAmount=invoiceAmount.add(record.getInvoiceAmount());
                taxAmount=taxAmount.add(record.getTaxAmount());

            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("成功提交认证:"+total+"条</br>合计金额:"+invoiceAmount.setScale(2,BigDecimal.ROUND_DOWN).toPlainString()+"</br>合计税额:"+taxAmount.setScale(2,BigDecimal.ROUND_DOWN).toPlainString()+"</br>");
        if (!"".equals(gfTaxNo)){
            sb.append("以下购方税号已申请抵扣统计不可认证：</br>"+gfTaxNo);
        }
        return sb.toString();

    }

    @Override
    public String getCurrentTaxPeriod(String schemaLabel,String id){
        String currentTaxPeriod=manualCertificationDao.getCurrentTaxPeriod(schemaLabel,id);
        SimpleDateFormat format =  new SimpleDateFormat("yyyyMM");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

        if(StringUtils.isBlank(currentTaxPeriod)){
            //日期配置文件
            if(nowDay <= Integer.parseInt(taxPeriod)){
                calendar.add(Calendar.MONTH, -1);
                currentTaxPeriod = format.format(calendar.getTime());
            }else{
                currentTaxPeriod = format.format(calendar.getTime());
            }
        }
        return currentTaxPeriod;
    }

	/**   
	 * <p>Title: queryExportList</p>   
	 * <p>Description: </p>   
	 * @param schemaLabel
	 * @param map
	 * @return   
	 * @see com.xforceplus.wapp.modules.certification.service.ManualCertificationService#queryExportList(java.lang.String, java.util.Map)
	 */  
	@Override
	public List<InvoiceCertificationExcelEntity> queryExportList(String schemaLabel, Map<String, Object> map) {
		 if(map.get("xfName")!=null){
	            map.put("xfName", map.get("xfName").toString().replace(" ",""));
	        }
	        List<InvoiceCertificationEntity> entitys = manualCertificationDao.queryExportList(schemaLabel, map);
		    List<InvoiceCertificationExcelEntity>  excelList = new LinkedList<InvoiceCertificationExcelEntity>();
		    int index = 1;
            InvoiceCertificationExcelEntity excel  = null;
		    for(InvoiceCertificationEntity entity: entitys){
                excel = new InvoiceCertificationExcelEntity();
                excel.setRownumber(entity.getRownumber());
                excel.setInvoiceCode(entity.getInvoiceCode());
                excel.setInvoiceNo(entity.getInvoiceNo());
                excel.setInvoiceDate(formatDateString(entity.getInvoiceDate()));
                String invoiceAmount = entity.getInvoiceAmount().toString();
                excel.setInvoiceAmount(invoiceAmount.substring(0,invoiceAmount.length()-2));
                String taxAmount = entity.getTaxAmount().toString();
                excel.setTaxAmount(taxAmount.substring(0,taxAmount.length()-2));
                String totalAmount = entity.getTotalAmount().toString();
                excel.setTotalAmount(totalAmount.substring(0,totalAmount.length()-2));
                excel.setVenderid(entity.getVenderid());
                excel.setCertificateNo(entity.getCertificateNo());
                excel.setJvcode(entity.getJvcode());
                excel.setCompanyCode(entity.getCompanyCode());
                excel.setFlowType(formatFlowType(entity.getFlowType()));
                excelList.add(excel);
            }
	        return excelList;
	}

    private String formatFlowType(String type){
        return null==type ? "" :
                "1".equals(type) ? "商品" :
                        "2".equals(type) ? "费用" :
                                "3".equals(type) ? "外部红票" :
                                        "4".equals(type) ? "内部红票" :
                                                "5".equals(type) ? " 供应商红票" :
                                                        "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }

    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }
}
