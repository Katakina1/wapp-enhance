package com.xforceplus.wapp.modules.export.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.InformationInquiry.service.*;
import com.xforceplus.wapp.modules.analysis.service.DataInvoiceSubmitService;
import com.xforceplus.wapp.modules.analysis.service.MaterialInvoiceSubmitDetailService;
import com.xforceplus.wapp.modules.base.service.AnnouncementInquiryService;
import com.xforceplus.wapp.modules.base.service.AribaBillTypeService;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.KnowCenterService;
import com.xforceplus.wapp.modules.businessData.service.ClaimService;
import com.xforceplus.wapp.modules.businessData.service.PoService;
import com.xforceplus.wapp.modules.certification.service.CertificationQueryService;
import com.xforceplus.wapp.modules.certification.service.ManualCertificationService;
import com.xforceplus.wapp.modules.collect.service.AbnormalInvoiceCollectionService;
import com.xforceplus.wapp.modules.collect.service.InvoiceCollectionService;
import com.xforceplus.wapp.modules.collect.service.NoDetailedInvoiceService;
import com.xforceplus.wapp.modules.cost.service.CostQueryService;
import com.xforceplus.wapp.modules.cost.service.SignininqueryCostQueryService;
import com.xforceplus.wapp.modules.export.Enum.ExcelServiceTypeEnum;
import com.xforceplus.wapp.modules.export.dao.ExportDao;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.service.IExcelExportService;
import com.xforceplus.wapp.modules.export.thread.*;
import com.xforceplus.wapp.modules.export.utils.FtpUtilService;
import com.xforceplus.wapp.modules.invoiceBorrow.service.InvoiceBorrowService;
import com.xforceplus.wapp.modules.job.service.HostService;
import com.xforceplus.wapp.modules.job.utils.JMSExprotRequestProducer;
import com.xforceplus.wapp.modules.pack.service.GenerateBindNumberService;
import com.xforceplus.wapp.modules.pack.service.InputPackNumberService;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.protocol.service.ProtocolService;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InvoiceListService;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.redTicket.service.ExamineAndUploadRedNoticeService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.service.BatchSystemMatchQueryService;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InvoiceProcessingStatusReportService;
import com.xforceplus.wapp.modules.report.service.SupplierIssueInvoiceQuantityandRatioService;
import com.xforceplus.wapp.modules.scanRefund.service.*;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryService;
import com.xforceplus.wapp.websocket.WebSocketServer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * ****************************************************************************
 * excel导出的业务逻辑实现类
 *
 * @author(作者)：xuyongyun @date(创建日期)：2019年4月30日
 ******************************************************************************
 */
@Service
public class ExcelExportServiceImpl implements IExcelExportService {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	IExcelExportService excelExportService;
	@Autowired
	ExportDao exportDao;
	@Autowired
	FtpUtilService ftpService;
	@Autowired
	SignInInqueryService signInInqueryService;
	@Autowired
	private QuestionnaireService questionnaireService;
	@Autowired
	private BatchSystemMatchQueryService batchSystemMatchQueryService;
	@Autowired
	private SignForQueryService signForQueryService;
	@Autowired
	private DetailsService detailsService;
	@Autowired
	private RebatenoForQueryXiaoService rebatenoForQueryXiaoService;
	@Autowired
    private ProtocolService protocolService;
    @Autowired
    private PaymentInvoiceQueryService paymentInvoiceQueryService;
    @Autowired
    private PoService poService;
	@Autowired
	private ClaimService claimService;
	@Autowired
	private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;
	@Autowired
	private PaymentDetailService paymentDetailService;
	@Autowired
	private WebSocketServer webSocketServer;
	@Autowired
	private JMSExprotRequestProducer jmsExprotRequestProducer;
	@Autowired
	private CostQueryService costQueryService;
    @Autowired
    private BaseUserService baseUserService;
    @Autowired
	private DirectAuthService scanConfirmService;
	@Autowired
    private  AuthenticationQueryService authenticationQueryService;
	@Autowired
    private ManualCertificationService manualCertificationService;
	@Autowired
	private AuthenticationResultQueryService authenticationResultQueryService;
    @Autowired
    private CertificationQueryService certificationQueryService;
	@Autowired
	private InvoiceBorrowService invoiceBorrowService;
	@Autowired
	private InputPackNumberService inputPackNumberService;
    @Autowired
    private CostGenerateRefundNumberService costgenerateRefundNumberService;
    @Autowired
	private PoInquiryService poInquiryService;
    @Autowired
    private ClaimInquiryService claimInquiryService;
    @Autowired
	private RebatenoForQueryService rebatenoForQueryService;
	@Autowired
	private PrintRefundInformationService printRefundInformationService;
	@Autowired
	private GenerateRefundNumberService generateRefundNumberService;
    @Autowired
    private ScanningService scanningService;
	@Autowired
	private SignininqueryCostQueryService signininqueryCostQueryService;
	@Autowired
    private SignForQueryChargeService signForQueryChargeService;
    @Autowired
    private InvoiceProcessingStatusReportService invoiceProcessingStatusReportService;
    @Autowired
    private PaymentInvoiceUploadService paymentInvoiceUploadService;
	@Autowired
	private ExamineAndUploadRedNoticeService examineAndUploadRedNoticeService;
	@Autowired
	private QueryOpenRedTicketDataService queryOpenRedTicketDataService;
	@Autowired
	private KnowCenterService knowCenterService;
    @Autowired
	private AbnormalInvoiceCollectionService abnormalInvoiceCollectionService;
	@Autowired
	private SupplierInformationSearchService supplierInformationSearchService;
    @Autowired
    private MaterialInvoiceSubmitDetailService materialInvoiceSubmitDetailService;
    @Autowired
	private SupplierIssueInvoiceQuantityandRatioService supplierIssueInvoiceQuantityandRatioService;
	@Autowired
	private DataInvoiceSubmitService dataInvoiceSubmitService;
	@Autowired
	private ScanConfirmService scanConfirmService1;
	@Autowired
	private UploadScarletLetterService uploadScarletLetterService;
	@Autowired
	private InputRedTicketInformationService inputRedTicketInformationService;
	@Autowired
	private InvoiceListService invoiceListService;
	@Autowired
	private InvoiceCollectionService invoiceCollectionService;
	@Autowired
	private NoDetailedInvoiceService noDetailedInvoiceService;
	@Autowired
	private AnnouncementInquiryService announcementInquiryService;
	@Autowired
	private CostPrintRefundInformationService costPrintRefundInformationService;
    @Autowired
    private HostService hostService;
    @Autowired
	private MatchService matchService;
    @Autowired
    private RedInvoiceUploadService redInvoiceUploadService;
	@Autowired
	private AribaBillTypeService aribaBillTypeService;
	@Autowired
	private GenerateBindNumberService generateBindNumberService;
    @Value("${activemq.export_success_queue_one}")
	private String activemqExportSuccessQueueOne;
	@Value("${activemq.export_success_queue_two}")
	private String activemqExportSuccessQueueTwo;
	@Value("${activemq.export_success_queue_gfone}")
	private String exportSuccessQueuegfone;
	@Value("${activemq.export_success_queue_gftwo}")
	private String exportSuccessQueuegftwo;
	@Value("${activemq.export_success_queue_xfone}")
	private String exportSuccessQueuexfone;
	@Value("${activemq.export_success_queue_xftwo}")
	private String exportSuccessQueuexftwo;
	@Override
	public void exportExcel(String message) {
		JSONObject jsonObject = JSON.parseObject(message);
		ExportLogEntity tDxExcelExportlog = JSON.parseObject(jsonObject.getJSONObject("message").toJSONString(), ExportLogEntity.class);

		JSONObject exportStutaMessage=null;

			if (ExcelServiceTypeEnum.SCAN_HANDLE.getServiceType() == tDxExcelExportlog.getServiceType()) {
				// 发票采集导出
				ScanHandleExportThread abc=new ScanHandleExportThread(excelExportService, tDxExcelExportlog, signInInqueryService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if (ExcelServiceTypeEnum.HOST_FAIL.getServiceType() == tDxExcelExportlog.getServiceType()) {
				// Host失败
				QuestionnaireExportThread abc=new QuestionnaireExportThread(excelExportService, tDxExcelExportlog, questionnaireService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if(ExcelServiceTypeEnum.HOST_SUCC.getServiceType() == tDxExcelExportlog.getServiceType()) {
				// Host成功
				BatchSystemMatchQueryExportThread abc=new BatchSystemMatchQueryExportThread(excelExportService, tDxExcelExportlog, batchSystemMatchQueryService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if(ExcelServiceTypeEnum.SCAN_REPORT.getServiceType() == tDxExcelExportlog.getServiceType()) {
				//掃描處理報告
				SignForQueryExportThread abc=new SignForQueryExportThread(excelExportService, tDxExcelExportlog, signForQueryService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if(ExcelServiceTypeEnum.MATCHING_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
				//匹配查询（销）
				MatchingQueryExportThread abc=new MatchingQueryExportThread(excelExportService, tDxExcelExportlog, detailsService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if(ExcelServiceTypeEnum.INVOICE_REFUND.getServiceType() == tDxExcelExportlog.getServiceType()) {
				//发票退票查询
		     	InvoixeRefundExportThread abc=new InvoixeRefundExportThread(excelExportService, tDxExcelExportlog, rebatenoForQueryXiaoService, ftpService);
				abc.run();
				exportStutaMessage=abc.getMassage();
			}if(ExcelServiceTypeEnum.PROTOCOL_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
                //协议查询
                ProtocolQueryExportThread abc=new ProtocolQueryExportThread(excelExportService, tDxExcelExportlog, protocolService, ftpService);
                abc.run();
                exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.DEDUCTION_INVOICE.getServiceType() == tDxExcelExportlog.getServiceType()) {
                //协议查询
                DeductionInvoiceExportThread abc=new DeductionInvoiceExportThread(excelExportService, tDxExcelExportlog, paymentInvoiceQueryService, ftpService);
                abc.run();
                exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.PO_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
                //订单查询查询
                POQueyrExportThread abc=new POQueyrExportThread(excelExportService, tDxExcelExportlog, poService, ftpService);
                abc.run();
                exportStutaMessage=abc.getMassage();
            }
			if(ExcelServiceTypeEnum.CLAIM_EXPORT.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//销方索赔导出
			ClaimQueryExportThread abc=new ClaimQueryExportThread(excelExportService, tDxExcelExportlog, claimService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.INVOICE_EXPORT.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//销方发票导出
			InvoiceQueryExportThread abc=new InvoiceQueryExportThread(excelExportService, tDxExcelExportlog, comprehensiveInvoiceQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
	     	}if(ExcelServiceTypeEnum.PAYMENT_EXPORT.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//销方付款信息导出
			PaymentDetailExportThread abc=new PaymentDetailExportThread(excelExportService, tDxExcelExportlog, paymentDetailService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.COST_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//结算查询
			CostQueryExportThread abc=new CostQueryExportThread(excelExportService, tDxExcelExportlog, costQueryService, ftpService,baseUserService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.DIRECT_AUTH.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//直接认证
			DirectAuthExportThread abc=new DirectAuthExportThread(excelExportService, tDxExcelExportlog, scanConfirmService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.AUTH_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//税务传票清单
			AuthenticationQueryExportThread abc=new AuthenticationQueryExportThread(excelExportService, tDxExcelExportlog, authenticationQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.MANUAL_AUTH.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //手工认证
            ManualCertificationExportThread abc=new ManualCertificationExportThread(excelExportService, tDxExcelExportlog, manualCertificationService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.AUTH_REPORT.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//发票认证报告
			AuthenticationResultExportThread abc=new AuthenticationResultExportThread(excelExportService, tDxExcelExportlog, authenticationResultQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.AUTH_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//认证查询
			QueryCertificationExportThread abc=new QueryCertificationExportThread(excelExportService, tDxExcelExportlog, certificationQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.INVOICE_RETURN.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//发票归还
			InvoiceReturnExportThread abc=new InvoiceReturnExportThread(excelExportService, tDxExcelExportlog, invoiceBorrowService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.BORROW_RECORD.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//发票借阅记录
			InvoiceRecordExportThread abc=new InvoiceRecordExportThread(excelExportService, tDxExcelExportlog, invoiceBorrowService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.PACK_NUMDER.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//录入装箱号
			InputPackNumberExportThread abc=new InputPackNumberExportThread(excelExportService, tDxExcelExportlog, inputPackNumberService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.COST_GENERATE.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //费用生成退单号
            CostGenerateRefundNumberExportThread abc=new CostGenerateRefundNumberExportThread(excelExportService, tDxExcelExportlog, costgenerateRefundNumberService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.PO_INQUIRY.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//订单查询(购)
			PoInquiryExportThread abc=new PoInquiryExportThread(excelExportService, tDxExcelExportlog, poInquiryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.CLAIML_QUIRY.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //索赔查询（购）
            ClaimInQuiryExportThread abc=new ClaimInQuiryExportThread(excelExportService, tDxExcelExportlog, claimInquiryService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.PAYMENT_QUIRY.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//购方付款信息查询
			PaymentDetailGFExportThread abc=new PaymentDetailGFExportThread(excelExportService, tDxExcelExportlog, paymentDetailService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.PROTOCPL.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //协议查询(购)
            ProtocolExportThread abc=new ProtocolExportThread(excelExportService, tDxExcelExportlog, protocolService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.INVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//发票综合查询
			InvoiceQueryListExportThread abc=new InvoiceQueryListExportThread(excelExportService, tDxExcelExportlog, comprehensiveInvoiceQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.REBATENO_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//发票退票查询
			RebatenoForExportThread abc=new RebatenoForExportThread(excelExportService, tDxExcelExportlog, rebatenoForQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.MATCH_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//购方匹配查询
			MatchingQueryGFExportThread abc=new MatchingQueryGFExportThread(excelExportService, tDxExcelExportlog, detailsService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.INVOICE_TICKETS.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//打印退单封面查询导出
			PrintRefundInformationExportThread abc=new PrintRefundInformationExportThread(excelExportService, tDxExcelExportlog, printRefundInformationService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.GENERATE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//退单导出
			GenerateRefundNumberExportThread abc=new GenerateRefundNumberExportThread(excelExportService, tDxExcelExportlog, generateRefundNumberService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.QUESTION_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //问题单导出
            QuestionListExportThread abc=new QuestionListExportThread(excelExportService, tDxExcelExportlog, scanningService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.COSTQUERY_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//费用扫描处理导出
			SignininqueryCostQueryExportThread abc=new SignininqueryCostQueryExportThread(excelExportService, tDxExcelExportlog, signininqueryCostQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.SIGN_QUERY.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //费用扫描处理导出
            SignForQueryChargeServiceExportThread abc=new SignForQueryChargeServiceExportThread(excelExportService, tDxExcelExportlog, signForQueryChargeService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.INVOICE_STATUS.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //发票处理状态报告导出
            InvoiceProcessingStatusReportExportThread abc=new InvoiceProcessingStatusReportExportThread(excelExportService, tDxExcelExportlog, invoiceProcessingStatusReportService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
           }if(ExcelServiceTypeEnum.INVOICE_UPLOAD.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //扣款发票上传导出
            PaymentInvoiceUploadExportThread abc=new PaymentInvoiceUploadExportThread(excelExportService, tDxExcelExportlog, paymentInvoiceUploadService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.EXAMINE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//审核红票资料
			ExamineExportThread abc=new ExamineExportThread(excelExportService, tDxExcelExportlog, examineAndUploadRedNoticeService,queryOpenRedTicketDataService,ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.SETTLE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//结算查询（购）
			CostQueryGFExportThread abc=new CostQueryGFExportThread(excelExportService, tDxExcelExportlog, costQueryService, ftpService,baseUserService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.KNOWCENTER_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//结算查询
			KnowCenterQueryExportThread abc=new KnowCenterQueryExportThread(excelExportService, tDxExcelExportlog, knowCenterService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.EXCEPTION_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//异常报告
			ExcetionQueryExportThread abc=new ExcetionQueryExportThread(excelExportService, tDxExcelExportlog, abnormalInvoiceCollectionService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.VENDER_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//供应商信息查询
			SupplierInformationQueryExportThread abc=new SupplierInformationQueryExportThread(excelExportService, tDxExcelExportlog, supplierInformationSearchService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.MATTERINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//实物发票提交明细
			MaterialInvoiceDetailQueryExportThread abc=new MaterialInvoiceDetailQueryExportThread(excelExportService, tDxExcelExportlog, materialInvoiceSubmitDetailService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.RETURNINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//商品退换票
			ScanConfirmQueryExportThread abc=new ScanConfirmQueryExportThread(excelExportService, tDxExcelExportlog, scanConfirmService1, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.ISSUEINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//供应商为题发票数量及比率
			InvoiceQuestionExportThread abc=new InvoiceQuestionExportThread(excelExportService, tDxExcelExportlog, supplierIssueInvoiceQuantityandRatioService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.DATAINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//数据发票提交统计
			DataInvoiceSubmitQueryExportThread abc=new DataInvoiceSubmitQueryExportThread(excelExportService, tDxExcelExportlog, dataInvoiceSubmitService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.UPREDINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//上传红字通知单
			UploadScarletterQueryExportThread abc=new UploadScarletterQueryExportThread(excelExportService, tDxExcelExportlog, uploadScarletLetterService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.IMPORTREDINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//批量导入红票信息
			RedTicketExportThread abc=new RedTicketExportThread(excelExportService, tDxExcelExportlog, inputRedTicketInformationService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.REDINVOICE_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//查询开红票资料(内部红票)
			RedInvoiceListExportThread abc=new RedInvoiceListExportThread(excelExportService, tDxExcelExportlog, uploadScarletLetterService,invoiceListService, ftpService);
			abc.run();
            exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.OPENRED_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //查询开红票资料(外部红票)
            OpenRedExportThread abc=new OpenRedExportThread(excelExportService, tDxExcelExportlog, queryOpenRedTicketDataService,ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.SHIWU_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //实物发票提交统计
            MaterialInvoiceSubmitQueryExportThread abc=new MaterialInvoiceSubmitQueryExportThread(excelExportService, tDxExcelExportlog, dataInvoiceSubmitService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.CAIJI_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
            //采集列表
			InvoiceCollectionExportThread abc=new InvoiceCollectionExportThread(excelExportService, tDxExcelExportlog, invoiceCollectionService, ftpService);
            abc.run();
            exportStutaMessage=abc.getMassage();
            }if(ExcelServiceTypeEnum.NODETAIL_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//未补明细发票列表
			NodetailedInvoiceExportThread abc=new NodetailedInvoiceExportThread(excelExportService, tDxExcelExportlog, noDetailedInvoiceService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.GONGGAO_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//公告查询沃
			AnnouncementExportThread abc=new AnnouncementExportThread(excelExportService, tDxExcelExportlog, announcementInquiryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		    }if(ExcelServiceTypeEnum.COST_TICKETS.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//打印退单封面查询导出
			PrintRefundInformationCostExportThread abc=new PrintRefundInformationCostExportThread(excelExportService, tDxExcelExportlog, costPrintRefundInformationService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.SCREE_HOST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//写屏维护导出
			ScreeHostExportThread abc=new ScreeHostExportThread(excelExportService, tDxExcelExportlog, hostService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.CGWTD_CX.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//采购问题单到处
			CgwtdExportThread abc=new CgwtdExportThread(excelExportService, tDxExcelExportlog, matchService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.RED_GFIN.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//红票信息购方查询导出
			RedInvoiceUploadExportThread abc=new RedInvoiceUploadExportThread(excelExportService, tDxExcelExportlog, redInvoiceUploadService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.RED_XFIN.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//红票信息销方查询导出
			RedInvoiceQueryExportThread abc=new RedInvoiceQueryExportThread(excelExportService, tDxExcelExportlog, redInvoiceUploadService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.ARIBA_AUTH_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//Ariba税务传票清单
			AribaAuthenticationQueryExportThread abc=new AribaAuthenticationQueryExportThread(excelExportService, tDxExcelExportlog, authenticationQueryService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.ARIBA_BILL_LIST.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//Ariba业务类型
			AribaBillTypeExportThread abc=new AribaBillTypeExportThread(excelExportService, tDxExcelExportlog, aribaBillTypeService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }if(ExcelServiceTypeEnum.BIND_NUMBER.getServiceType() == tDxExcelExportlog.getServiceType()) {
			//装订成册
			BindNumberExportThread abc=new BindNumberExportThread(excelExportService, tDxExcelExportlog,generateBindNumberService, ftpService);
			abc.run();
			exportStutaMessage=abc.getMassage();
		   }



			if(exportStutaMessage!=null){
				//生产导出完成的消息
				Map map=new HashMap();
                log.debug("推送消息开始");
				map.put("message",exportStutaMessage.toJSONString());
				Destination destination=null;
				if(jsonObject.getString("activemqProducerStatusCode").equals("gfone")){
					//如果是从导出申请one身份的生产消息，则正产到导出成功one队列上
					destination= new ActiveMQQueue(exportSuccessQueuegfone);
				}else if(jsonObject.getString("activemqProducerStatusCode").equals("gftwo")){
					destination= new ActiveMQQueue(exportSuccessQueuegftwo);
				}else if(jsonObject.getString("activemqProducerStatusCode").equals("xfone")){
					destination= new ActiveMQQueue(exportSuccessQueuexfone);
				}else if(jsonObject.getString("activemqProducerStatusCode").equals("xftwo")){
					destination= new ActiveMQQueue(exportSuccessQueuexftwo);
				}
				try {
					jmsExprotRequestProducer.sendMessage(destination, JSON.toJSONString(map));
                    log.debug("推送消息成功");
				}catch (Exception e){
					e.printStackTrace();
				}
			}

	}


	@Override
	public ExportLogEntity excelExportApply(Map<String, Object> paramsMap) {
		ExportLogEntity entity = new ExportLogEntity();
		entity.setUserAccount(paramsMap.get("userId").toString());
		entity.setUserName(paramsMap.get("userName").toString());
		entity.setServiceType(Integer.valueOf(paramsMap.get("serviceType").toString()));
		entity.setConditions(paramsMap.get("conditions").toString());
		//保存导出日志表
		int count = exportDao.insertLog(entity);
		return entity;
	}

	@Override
	public void updateStart(Long id) {
		exportDao.updateStartDate(id);
	}

	@Override
	public void updateSucc(Long id, String ftpFilePath) {
		Map<String,Object> pramsMap = new HashMap<String,Object>(4);
		pramsMap.put("id", id);
		pramsMap.put("excelFile", ftpFilePath);
		exportDao.updateSucc(pramsMap);
	}

	@Override
	public void updateFail(Long id, String errmsg) {
		Map<String,Object> paramsMap = new HashMap<String,Object>(4);
		paramsMap.put("id", id);
		paramsMap.put("errmsg", errmsg);
		exportDao.updateFail(paramsMap);
	}

	@Override
	public void insertMessage(JSONObject msg) {
		exportDao.insertMessage(msg);
	}


	@Override
	public void sendWebsocketMessage(String username) {
		if (!StringUtils.isEmpty(username)) {
			Map<String, Object> parmMap = new HashMap<String, Object>();
			parmMap.put("username", username);
			parmMap.put("operationStatus", "0");
			int count = exportDao.getMessageControlCount(null, parmMap);

			JSONObject result = new JSONObject();
			result.put("messageCount", count);
			webSocketServer.AppointSending(WebSocketServer.webSocketUserInfo.get(username), result.toString());
		}
	}
}
