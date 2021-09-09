package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.signin.dao.ScannerSignDao;
import com.xforceplus.wapp.modules.signin.entity.*;
import com.xforceplus.wapp.modules.signin.entity.*;
import com.xforceplus.wapp.modules.signin.entity.*;
import com.xforceplus.wapp.modules.signin.service.ScannerSignService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.jcraft.jsch.JSchException;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/rest/invoice/sign")
public class ScannerSignController  extends AbstractController {
    @Autowired
    private ScannerSignService scannerSignService;
    @Autowired
    private ScannerSignDao scannerSignDao;
    protected static Logger log = Logger.getLogger(ScannerSignController.class);


    @Value("${pro.sftp.host}")
    private String host; // sftp IP底账
    @Value("${pro.sftp.username}")
    private String userName; // sftp 用户名
    @Value("${pro.sftp.password}")
    private String password; // sftp 密码
    @Value("${pro.sftp.default.port}")
    private String defaultPort; // sftp 默认端口号
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout; // sftp 默认超时时间
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;
    @Value("${filePathConstan.remoteImageRootPath}")
    private String remoteImageRootPath;

    @Value("${mycat.default_schema_label}")
    private String schemaLabel;
    /**
     * @Description: 有底账发票签收方法
     * @return 签收过的发票数据
     */
    @ResponseBody
    @RequestMapping(value="/signWithRecord", method= RequestMethod.POST)
    public String signWithRecord(@RequestParam(required = false) Long id, HttpServletRequest request,String invoiceType){
        List<SignedInvoiceVo> invoiceList = new ArrayList<SignedInvoiceVo>();
        try {
            String invoices = request.getParameter("invoices");
            log.info("控件传入专票信息:" + invoices);
            invoiceList = JsonUtil.fromJson(invoices, new TypeReference<ArrayList<SignedInvoiceVo>>() {
            });
            log.debug("控件invoiceList：" + invoiceList.size());
        } catch (Exception e) {
            log.error("InvoiceSignController.signWithRecord:err invoiceList", e);
        }
        InvoiceScanResultVo resultVo = signWithRecordResultVo( id,  invoiceType, invoiceList);

        try {
            return JsonUtil.toJson(resultVo, Inclusion.NON_EMPTY);
        } catch (IOException e) {
            log.error("InvoiceSignController.signWithRecord:err " + invoiceList, e);
            return "{}";
        }
    }

    /**
     * @Description: 无底账发票签收方法
     * @param request
     *            录入的发票数据
     * @return 签收过的发票数据
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "/signWithoutRecord", method = RequestMethod.POST)
    public String signWithoutRecord(Model model, HttpServletRequest request, String invoiceType, @RequestParam(required = false) Long id) {

        List<SignedInvoiceVo> invoiceList = new ArrayList<SignedInvoiceVo>();
        try {
            String invoices = request.getParameter("invoices");
            log.info("控件传入普票信息:" + invoices);
            invoiceList = JsonUtil.fromJson(invoices, new TypeReference<ArrayList<SignedInvoiceVo>>() {
            });
            InvoiceScanResultVo resultVo = this.signWithoutRecordResultVo(invoiceType, id, invoiceList);
            return JsonUtil.toJson(resultVo, Inclusion.NON_EMPTY);
        } catch (IOException e) {
            log.error("InvoiceSignController.signWithoutRecord:err " + invoiceList, e);
            return "{}";
        }
    }

    @RequestMapping(value="/invoiceDelete.json",method=RequestMethod.POST)
    @ResponseBody
    public void invoiceDelete(String uuId){
        try {
            scannerSignService.invoiceDelete(uuId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 有底账逻辑接口
     *

     * @param invoiceList
     * @return
     */
    public InvoiceScanResultVo signWithRecordResultVo( @RequestParam(required = false) Long id, String invoiceType, List<SignedInvoiceVo> invoiceList) {
        // 查找扫描路径
        InvoiceScanResultVo resultVo = new InvoiceScanResultVo();
        resultVo.setScanNum(0);
        resultVo.setSuccessNum(0);
        resultVo.setQsFailNum(0);
        resultVo.setWqsNum(0);
        resultVo.setRkFailNum(0);
//		if (null != gfName)
//			resultVo.setGfName(gfName);

        // 获取扫描时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date scanDate = new Date();
        String scanDateStr = sdf.format(scanDate);

        // 签收结果容器,用于页面展示
        List<SignedInvoiceVo> resultList = new ArrayList<SignedInvoiceVo>();

        resultVo.setInvoiceList(resultList);

        // 遍历签收
        for (SignedInvoiceVo invoiceVo : invoiceList) {
            log.debug("控件invoiceList：" + invoiceList.size());
            resultVo.setScanNum(resultVo.getScanNum() + 1); // 扫描票数量+1

            // 格式转换金额
            try {
                new BigDecimal(invoiceVo.getInvoiceAmount());
            } catch (Exception e) {
                invoiceVo.setInvoiceAmount("0");
            }
            // 格式转换税额
            try {
                new BigDecimal(invoiceVo.getTaxAmount());
            } catch (Exception e) {
                invoiceVo.setTaxAmount("0");
            }
            // 设置随机数
            if ("".equals(invoiceVo.getInvoiceCode()) || invoiceVo.getInvoiceCode() == null) {
                invoiceVo.setInvoiceCode("F" + scannerSignService.sjnum());
            }
            if ("".equals(invoiceVo.getInvoiceNo()) || invoiceVo.getInvoiceNo() == null) {
                invoiceVo.setInvoiceNo("F" + scannerSignService.sjnum());
            }
            // 设置购方名称
//			if (null == invoiceVo.getGfName()) {
//				invoiceVo.setGfName(gfName);
//			}
            // 获取扫描流水号
//            String serialNo = CacheStatic.getNextSerialNo(scanDateStr.replace("-", ""));
            // 设置扫描流水号
//            if (null == invoiceVo.getInvoiceSerialNo()) {
//                invoiceVo.setInvoiceSerialNo(serialNo);
//            }
            // 设置扫描时间
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (null == invoiceVo.getScandate()) {
                invoiceVo.setScandate(sdf.format(scanDate));
            }
            // 设置发票时间
            if (6 == invoiceVo.getInvoiceDate().length()) {
                invoiceVo.setInvoiceDate("20" + invoiceVo.getInvoiceDate());
            }
            // 计算价税合计
            if (invoiceVo.getInvoiceAmount() == null || "".equals(invoiceVo.getInvoiceAmount()) || invoiceVo.getTaxAmount() == null || "".equals(invoiceVo.getTaxAmount())) {
                invoiceVo.setTotalAmount(null);
            } else {
                invoiceVo.setTotalAmount((new BigDecimal(invoiceVo.getInvoiceAmount()).add(new BigDecimal(invoiceVo.getTaxAmount()))).toString());
            }


                invoiceVo.setInsideOutside(0);
                // 设置发票种类
                if(CommonUtil.getFplx(invoiceVo.getInvoiceCode()).equals("01")) {
                    invoiceVo.setInvoiceType("01");
                    invoiceVo.setInvoiceStatus("0");
                    int row = this.scannerSignService.signUseRecord(invoiceVo, id);
                    //修改返回值
                    if (invoiceVo.getInvoiceStatus().equals("1")){
                        invoiceVo.setInvoiceStatus("已签收");
                    }else if (invoiceVo.getInvoiceStatus().equals("0")){
                        invoiceVo.setInvoiceStatus("未签收");
                    }
                    if (1 == row) {
                        resultVo.setSuccessNum(resultVo.getSuccessNum() + 1);
                    } else if (-1 == row) {
                        resultVo.setWqsNum(resultVo.getQsFailNum() + 1);
                    } else if (-2 == row) {
                        resultVo.setQsFailNum(resultVo.getQsFailNum() + 1);
                    } else {
                        resultVo.setRkFailNum(resultVo.getRkFailNum() + 1);
                    }
                }else{
                    resultVo.setQsFailNum(resultVo.getQsFailNum() + 1);
                    invoiceVo.setInvoiceStatus("未签收");
                    invoiceVo.setNotes("选择类型与实际扫描发票类型不一致");
                }


            // 是否核对底账
            resultList.add(invoiceVo);

        }
        return resultVo;
    }
    /**
     * 无底账逻辑接
     * @param id
     * @param invoiceList
     * @return
     */
    @SuppressWarnings("unused")
    public InvoiceScanResultVo signWithoutRecordResultVo(String invoiceType,  @RequestParam(required = false) Long id, List<SignedInvoiceVo> invoiceList) {
        // 获取扫描时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date scanDate = new Date();
        InvoiceScanResultVo resultVo = new InvoiceScanResultVo();
        resultVo.setScanNum(0);
        resultVo.setSuccessNum(0);
        resultVo.setQsFailNum(0);
        resultVo.setWqsNum(0);
        resultVo.setRkFailNum(0);

        boolean flag = false;
        for (SignedInvoiceVo invoiceVo : invoiceList) {
            StringBuilder note = new StringBuilder();
            //resultVo.setScanNum(resultVo.getScanNum() + 1); // 扫描票数量+1
            boolean isSuccess = true;

            // 设置扫描路径
            // ScanPaths paths =
            // scanPathVo(invoiceVo.getInvoiceCode()+invoiceVo.getInvoiceNo());
            // if (paths.getId() != null) {
            // invoiceVo.setScanPathId(paths.getId());
            // invoiceVo.setScanPath(paths.getScanPath());
            // scanPathId = paths.getId();
            // }
            if ("".equals(invoiceVo.getInvoiceCode()) || invoiceVo.getInvoiceCode() == null) {
                note.append("发票代码错误,");
                invoiceVo.setInvoiceCode("F" + scannerSignService.sjnum());
                isSuccess = false;
            }
            if ("".equals(invoiceVo.getInvoiceNo()) || invoiceVo.getInvoiceNo() == null) {
                note.append("发票号码错误,");
                invoiceVo.setInvoiceNo("F" + scannerSignService.sjnum());
                isSuccess = false;
            }
            if ("".equals(invoiceVo.getInvoiceDate()) || invoiceVo.getInvoiceDate() == null) {
                note.append("开票日期错误,");
                isSuccess = false;
            }
            if ("".equals(invoiceVo.getXfTaxNo()) || invoiceVo.getXfTaxNo() == null) {
                note.append("销方税号错误,");
                isSuccess = false;
            }
            if ("".equals(invoiceVo.getTaxAmount()) || invoiceVo.getTaxAmount() == null) {
                invoiceVo.setTaxAmount("0");
            }
            if ("".equals(invoiceVo.getInvoiceAmount()) || invoiceVo.getInvoiceAmount() == null) {
                note.append("开票金额错误,");
                isSuccess = false;
            }
            if (isSuccess == false) {
                invoiceVo.setNotes(StringUtils.substringBeforeLast(note.toString(), ","));
            }


            // 格式转换金额
            try {
                new BigDecimal(invoiceVo.getInvoiceAmount());
            } catch (Exception e) {
                invoiceVo.setInvoiceAmount("0");
            }
            // 格式转换税额
            try {
                new BigDecimal(invoiceVo.getTaxAmount());
            } catch (Exception e) {
                invoiceVo.setTaxAmount("0");
            }

//            // 设置签收状态
//            invoiceVo.setInvoiceStatus("1");
            // 设置扫描时间
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (null == invoiceVo.getScandate()) {
                invoiceVo.setScandate(sdf.format(scanDate));
            }
            UserEntity sysUserEntity=(UserEntity) SecurityUtils.getSubject().getPrincipal();
            // 用户姓名
            invoiceVo.setUserName(sysUserEntity.getUsername());
            // 设置用户账号
            invoiceVo.setUserAccount(String.valueOf(sysUserEntity.getLoginname()));
            // 设置uuid
            invoiceVo.setUuid(invoiceVo.getInvoiceCode() + "" + invoiceVo.getInvoiceNo());
            // 设置发票时间
            if (6 == invoiceVo.getInvoiceDate().length()) {
                invoiceVo.setInvoiceDate("20" + invoiceVo.getInvoiceDate());
            }
            // 计算价税合计
            if (invoiceVo.getInvoiceAmount() == null || "".equals(invoiceVo.getInvoiceAmount())) {
                invoiceVo.setTotalAmount(null);
                if (invoiceVo.getNotes() == null) {
                    invoiceVo.setNotes("价税合计错误,");
                } else {
                    invoiceVo.setNotes(invoiceVo.getNotes() + ",价税合计错误");
                }
            } else {
                invoiceVo.setTotalAmount((new BigDecimal(invoiceVo.getInvoiceAmount()).add(new BigDecimal(invoiceVo.getTaxAmount()))).toString());
            }
            // 发票代码合法性判断
            if (!CommonUtil.getFplx(invoiceVo.getInvoiceCode()).equals("04")) { // 发票代码错误
                invoiceVo.setNotes("选择类型与实际扫描发票类型不一致");
                continue;
            }
            invoiceVo.setInvoiceType("04");

            // 判断是否扫描识别的购方税号是否存在
            OrgTaxNoInfo gfTaxNo = scannerSignDao.getOrgTaxGfName(invoiceVo.getGfTaxNo());
            if (gfTaxNo == null) {
                invoiceVo.setGfName("");
                if (invoiceVo.getNotes() == null) {
                    invoiceVo.setNotes("购方税号错误");
                } else {
                    invoiceVo.setNotes(invoiceVo.getNotes() + "，购方税号错误");
                }
            }else{
                invoiceVo.setGfName(gfTaxNo.getCompanyName());
            }
            invoiceVo.setInvoiceType("04");

//            // 设置签收状态
//            invoiceVo.setInvoiceStatus("1");
//            if (invoiceVo.getNotes() == null) {
//                invoiceVo.setNotes("签收成功");
//            } else {
//                invoiceVo.setNotes(invoiceVo.getNotes() + "," + "签收成功");
//            }
//            // 设置签收时间
//            if (invoiceVo.getQsDate() == null) {
//                invoiceVo.setQsDate(scanDate);
//            }
            // 是否核对底账
            invoiceVo.setInsideOutside(1);
        }
        try {
            resultVo=scannerSignService.insertFromScan(invoiceList,  id);


//            resultVo.setRkFailNum(invoiceList.get(invoiceList.size() - 1).getFailNum());
        } catch (Exception e) {
            log.error("扫描生成底账出错!", e);
            //resultVo.setInvoiceList(invoiceList);
        }


        return resultVo;
    }

    @ResponseBody
    @RequestMapping(value = "/uploadImg")
    public String uploadImg(MultipartFile file, String code, String invCode, String gfTaxNO , String scanId) {
        InputStream ins = null;
        OutputStream os = null;
//		String gfTaxNo = null;
        log.info("上传图片进来了  发票代码:" + code + "发票号码:" + invCode + "图片" + file);
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            // 获得session中保留的信息
//			OrgTaxNoInfo gfTax = orgTaxNoService.getOrgTaxNoId(Long.parseLong(gfId));// 查询下拉框所选购方税号
//			gfTaxNo = gfTax.getTaxCode();
            ins = file.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(ins);
            // 获取扫描时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date scanDate = new Date();
            String scanDateStr = sdf.format(scanDate);
            StringBuffer fileName = new StringBuffer();
            // String xh = UUID.randomUUID().toString().replace("-", "");
            // //获取唯一码
            // fileName.append(scanDateStr+"_").append(code).append(invCode).append(".bmp").toString();
            fileName.append(scanDateStr + "_").append(scanId).append(".bmp").toString();
            // 输出的文件流保存到本地文件
            sdf = new SimpleDateFormat("yyyy");
            String pathDate = sdf.format(scanDate);

            os = new FileOutputStream(imageHandler.getLocalImageRootPath() + File.separator + fileName);
            // 开始读取
            os.write(bytes);
            try {
                if (null != os) {
                    os.close();
                }
                if (null != ins) {
                    ins.close();
                }
            } catch (IOException e) {
                log.error("InvoiceSignController.uploadImg:err " + ins, e);
            }
            log.info("开始压缩:" + fileName);
            ZipUtil.zip(imageHandler.getLocalImageRootPath() + fileName.toString().replace(".bmp", ".zip"), new File(imageHandler.getLocalImageRootPath() + fileName));
            String path = imageHandler.uploadScanImage(pathDate, fileName.toString().replace(".bmp", ".zip"));// 图片上传ftp
            InvoiceImgSavePo savePo = new InvoiceImgSavePo();
            // savePo.setImage(bytes);
             savePo.setUuid(code+invCode);
            // StringBuilder(code).append(invCode).toString());
            savePo.setScanId(scanId);
            savePo.setImagePath(path + fileName.toString().replace(".bmp", ".zip"));
            scannerSignService.saveImg(savePo);
            FileUtil fu = new FileUtil();
            fu.deleteFile(imageHandler.getLocalImageRootPath() + fileName); // 上传成功后删除源图片
            fu.deleteFile(imageHandler.getLocalImageRootPath() + fileName.toString().replace(".bmp", ".zip")); // 上传成功后删除压缩包
            return "success";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("InvoiceSignController.uploadImg:err " + ins, e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8 关闭通道
            imageHandler.closeChannel();
        }
        return "fail";

    }

    /**
     * 查看图片
     * @param scanId 唯一识别码
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/getImg")
    @ResponseBody
    public void getImg(HttpServletRequest request,String scanId,HttpServletResponse response){
        try {
            SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
            InvoiceImgQueryVo img = null;
            try {
                img = scannerSignDao.getImg(schemaLabel, scanId);
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                if (null != img) {
                    UserEntity sysUserEntity=(UserEntity) SecurityUtils.getSubject().getPrincipal();
                    String userAccount = sysUserEntity.getUserid()+"";
                    imageHandler.download(img.getImagePath(), userAccount + ".zip");
                    response.setContentType("image/png");
                    String name = scanId;
                    response.reset();
                    response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    OutputStream output = response.getOutputStream();
                    byte[] zipFile = ZipUtil.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + ".zip");

                    ByteArrayInputStream in = new ByteArrayInputStream(zipFile);// 获取实体类对应Byte
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    output.flush();
                    if (in != null)
                        in.close();
                    if (output != null)
                        output.close();
                } else {
                    scanId = "0000";
                    InvoiceImgQueryVo img1 = scannerSignDao.getImg(schemaLabel,scanId);
                    response.setContentType("image/png");
                    String name = scanId;
                    response.reset();
                    response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    OutputStream output = response.getOutputStream();
                    ByteArrayInputStream in = new ByteArrayInputStream(img1.getImage());// 获取实体类对应Byte
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    if (in != null)
                        in.close();
                    if (output != null)
                        output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageHandler != null) {
                    imageHandler.closeChannel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取底账库数据
    @RequestMapping(value="/getRecordInvoice")
    @ResponseBody
    public Map getRecordInvoice(String invoiceCode,String invoiceNo){
        Map map=new HashMap();
        RecordInvoiceQueryByCodeAndNoPo recordInvoiceQueryByCodeAndNoPo=new RecordInvoiceQueryByCodeAndNoPo();
        recordInvoiceQueryByCodeAndNoPo.setInvoiceCode(invoiceCode);
        recordInvoiceQueryByCodeAndNoPo.setInvoiceNo(invoiceNo);
        List<RecordInvoiceQueryByCodeAndNoVo> recordInvoiceList=scannerSignDao.queryByCodeAndNo(recordInvoiceQueryByCodeAndNoPo);
        if(null!=recordInvoiceList&&recordInvoiceList.size()==1){
            map.put("code","200");
            map.put("msg",recordInvoiceList.get(0));
        }else{
            map.put("code","500");
            map.put("msg",new RecordInvoiceQueryByCodeAndNoVo());
        }
        return map;
    }

}
