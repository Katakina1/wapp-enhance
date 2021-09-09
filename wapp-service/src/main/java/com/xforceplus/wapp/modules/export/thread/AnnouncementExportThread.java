package com.xforceplus.wapp.modules.export.thread;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationQueryService;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.AnnouncementExcelEntity;
import com.xforceplus.wapp.modules.base.service.AnnouncementInquiryService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.export.entity.ExportLogEntity;
import com.xforceplus.wapp.modules.export.service.IExcelExportService;
import com.xforceplus.wapp.modules.export.utils.ExceptionUtil;
import com.xforceplus.wapp.modules.export.utils.FtpUtilService;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 
 * ****************************************************************************
 * 发票采集汇总导出线程
 *
 * @author(作者)：xuyongyun	
 * @date(创建日期)：2019年4月30日
 ******************************************************************************
 */
public class AnnouncementExportThread extends BaseThread {

	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 默认要导出第几页的数据
	 */
	private final int default_curr = 1;
	/**
	 * 默认每页导出多少条数据
	 */
	private final int default_size = 500;
	/**
	 * 文件名前缀
	 */
	private String fileNamePrefix = "AnnouncementList";
	/**
	 * 文件名后缀
	 */
	private String fileNameSurfix = ".xlsx";
	/**
	 * 文件名中间分隔符
	 */
	private String fileSplit = "_";
	/**
	 * excel的标题
	 */
	private String title = "公告查询";

	/**
	 * 缓存合计数量
	 */
	private static String hjsl = "0";
	/**
	 * 缓存合计金额
	 */
	private static String hjje = "0";
	/**
	 * 缓存合计税额
	 */
	private static String hjse = "0";

	private IExcelExportService exportLogService;

	private ExportLogEntity exportLog;

	private AnnouncementInquiryService announcementInquiryService;

	private FtpUtilService ftpService;
	private JSONObject massage;

	public JSONObject getMassage() {
		return massage;
	}


	public AnnouncementExportThread(IExcelExportService exportLogService,
                                    ExportLogEntity exportLog, AnnouncementInquiryService announcementInquiryService,
                                    FtpUtilService ftpService) {
		
		this.exportLogService = exportLogService;
		this.exportLog = exportLog;
		this.announcementInquiryService = announcementInquiryService;
		this.ftpService = ftpService;

	}
	
	@Override
	public void run() {

			//日志表主键
			Long id = exportLog.getId();
			String userid = exportLog.getUserAccount();
		    String userCode=exportLog.getUserCode();
			String userName =  exportLog.getUserName();
			//导出条件
			String condition = exportLog.getConditions();

			String createDate = DateUtils.format(exportLog.getCreateDate());
			
			JSONObject msg = new JSONObject();
			//捕获异常，防止某一条日志导出出错，影响其它日志的导出
			try {
				exportLogService.updateStart(id);
				
				//应该保存的参数
				JSONObject prams = JSONObject.parseObject(condition);
				Map map=prams;
				//map.put("venderid",userCode);

				// 设置EXCEL名称
				//导出文件名
				StringBuilder ftpFileName = new StringBuilder();
				ftpFileName.append(userid);
				ftpFileName.append(fileSplit);
				ftpFileName.append(fileNamePrefix);
				ftpFileName.append(fileSplit);
				ftpFileName.append(DateUtils.getStringDateShort());
				ftpFileName.append(fileSplit);
				ftpFileName.append((new Date()).getTime());
				ftpFileName.append(fileNameSurfix);
				StringBuilder excelFile = new StringBuilder();
				excelFile.append(ftpService.localPathDefault);
				excelFile.append(ftpFileName.toString());

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteArrayInputStream is = null;
				try {

					ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);



					// 查询总数并 【封装相关变量 这块直接拷贝就行 不要改动】
					Integer totalRowCount =announcementInquiryService.announcementInquiryList(map).getTotalCount();

					if(totalRowCount>1000000){
						totalRowCount=1000000;
					}
					Integer pageSize = 3000;
					Integer perSheetRowCount = 500000;
					Integer writeCount = totalRowCount % pageSize == 0 ? (totalRowCount / pageSize) : (totalRowCount / pageSize + 1);

					int sheetIndex = 1;
					Sheet sheet = new Sheet(sheetIndex, 0);
					sheet.setSheetName("sheet"+sheetIndex);
					sheet.setClazz(AnnouncementExcelEntity.class);
					// 写数据 这个i的最大值直接拷贝就行了 不要改
					for (int i = 0; i < writeCount; i++) {
						// 设置SHEET名称
						// 此处查询并封装数据即可 currentPage, pageSize这个变量封装好的 不要改动
						map.put("page",i+1);
						map.put("limit",3000);
						Query query1 = new Query(map);
						List<AnnouncementEntity> list = announcementInquiryService.announcementInquiryList(query1).getResults();
						List<AnnouncementExcelEntity> list2=announcementInquiryService.toExcel(list);
						if((i+1)*pageSize>perSheetRowCount*(sheetIndex)){
							sheetIndex=sheetIndex+1;
							sheet = new Sheet(sheetIndex, 0);
							sheet.setSheetName("sheet"+sheetIndex);
							sheet.setClazz(AnnouncementExcelEntity.class);
						}
						writer.write(list2, sheet);
					}

					// 下载EXCEL
					Workbook wb=writer.getWorkbook();
					wb.write(out);
				    is = new ByteArrayInputStream(out.toByteArray());

					out.flush();

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}


				//推送sftp
				String ftpPath = ftpService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
				String ftpFilePath = ftpPath + "/" + ftpFileName.toString();
				//文件上传ftp
				ftpService.uploadFile(ftpPath, ftpFileName.toString(),is);
				//设置成功消息内容
				msg.put("title", super.getSuccTitle(title));
				msg.put("content", super.getSuccContent(createDate));
				msg.put("url", super.getUrl(id));
				msg.put("userAccount", userName);

				//记录导出完成状态
				exportLogService.updateSucc(id,ftpFilePath);
				
			}catch(Exception e) {
				String errmsg = ExceptionUtil.getExceptionDeteil(e, 4000);
				//设置失败消息内容
				msg.put("title", super.getFailTitle(title));
				msg.put("content", super.getFailContent(createDate,errmsg));
				msg.put("url", "");
				msg.put("userAccount", userName);

				log.error("", e);
				//记录导出出错状态
				exportLogService.updateFail(id, errmsg);
			}finally {
				//发送导出消息到mq
				exportLogService.insertMessage(msg);
				massage=msg;
				//推送webSocket消息
//				exportLogService.sendWebsocketMessage(userName);
				ftpService.closeChannel();
			}

	}

	
	/**
	 * 递归导出数据到excel文件，防止jvm oom
	 *	
	 * @param pramsMap 查询数据参数集合
	 * @param curr     当前第几页
	 * @param excelFile 导出的文件
	 * @throws Exception 
	 * @since           1.0
	 */

//	private boolean cicleExport(Map<String, Object> pramsMap,int curr, String excelFile) throws Exception {
//		//查询指定页数的数据
//		JSONObject pageData = fpcjService.selectByGfshAndCjrq(pramsMap,curr,default_size);
//		boolean hasNext = pageData.getBooleanValue("hasNext");
//		List<String[]> dataList = (List<String[]>) pageData.get("datalist");
//
//		boolean firstPage = false;
//		if(curr==default_curr) {
//			hjsl = pageData.getString("hjsl");
//	    	hjje = pageData.getString("hjje");
//	    	hjse = pageData.getString("hjse");
//	    	firstPage = true;
//		}
//
//		//数据不是最后一页
//		if(hasNext) {
//			int nextPage = pageData.getIntValue("nextPage");
//			//写入当前页数据
//			ExcelPoiUtil.exportListArrayToExcel(excelFile, title, titleColumns, dataList, firstPage);
//			//更新页数
//			return cicleExport(pramsMap,nextPage,excelFile);
//		}else {
//			//数据是最后一页,采集汇总需要合计行
//			String[] hjRow = {"","合计","","",hjsl,hjje,hjse};
//			dataList.add(hjRow);
//			//写入最后一页数据
//			ExcelPoiUtil.exportListArrayToExcel(excelFile, title, titleColumns, dataList, firstPage);
//			return hasNext;
//		}
//	}
}
