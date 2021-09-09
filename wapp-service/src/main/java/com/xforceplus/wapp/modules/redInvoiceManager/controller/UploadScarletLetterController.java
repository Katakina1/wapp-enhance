package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import org.slf4j.Logger;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/redInvoice/uploadScarletLetter")
public class UploadScarletLetterController extends AbstractController {



    @Autowired
    private UploadScarletLetterService uploadScarletLetterService;
    private static final Logger LOGGER = getLogger(UploadScarletLetterController.class);

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("红票资料查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        UploadScarletLetterEntity list1 = uploadScarletLetterService.getTypeById(schemaLabel,query);
        if(list1.getOrgType().equals("5")&&list1.getTaxNo().equals("0")){
        List<UploadScarletLetterEntity> list = uploadScarletLetterService.queryListByStore(schemaLabel,query);
        ReportStatisticsEntity result = uploadScarletLetterService.queryTotalResultByStore(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());
        }else if(list1.getOrgType().equals("5")||list1.getOrgType().equals("2")){
            List<UploadScarletLetterEntity> list = uploadScarletLetterService.queryList(schemaLabel,query);
            ReportStatisticsEntity result = uploadScarletLetterService.queryTotalResult(schemaLabel,query);

            PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

            return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

        }
        return R.error();
    }

    @SysLog("红字通知单上传")
//    @RequestMapping(value = "/uploadRed", method = {RequestMethod.POST})
    @RequestMapping("/uploadRed")
    public R uploadRedTicketData(@RequestParam("file") MultipartFile file,@RequestParam("serialNumber") String serialNumber,@RequestParam("id") String id) {
        LOGGER.info("查询条件为:{}", id);
        return R.ok().put("msg", uploadScarletLetterService.uploadRedTicketRed( file, getUser(), serialNumber,Integer.valueOf(id)));
    }

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("上传文件查询列表")
    @RequestMapping("/filelist")
    public R filelist(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        List<UploadScarletLetterEntity> list = uploadScarletLetterService.getfileName(query);
        ReportStatisticsEntity result = uploadScarletLetterService.getfileNameCount(query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());
    }

    /**
     * 删除文件
     * @param para
     * @return
     */
    @SysLog("删除文件")
    @RequestMapping("/delete")
    public R delete(@RequestParam Map<String, Object> para) {

        String filename = para.get("localFileName").toString();

        int i =uploadScarletLetterService.deleteRedData(para);
//         uploadScarletLetterService.delete(filename);
         String str = filename.substring(13,29);
         String str1 = filename.substring(0,12);
        uploadScarletLetterService.delete1(str);
        Integer getfilecount = uploadScarletLetterService.getfileCount1(str1);
        if(getfilecount == 0){
            uploadScarletLetterService.updateStatus2(str1);
        }
        return R.ok();
    }

    /**
     * 查询机构类型
     * @param params
     * @return
     */
    @SysLog("查询机构类型")
    @RequestMapping("/Orgid")
    public R orgList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        UploadScarletLetterEntity list = uploadScarletLetterService.getTypeById(schemaLabel,query);
        return R.ok().put("orgType",list.getOrgType()).put("taxNo",list.getTaxNo());

    }
}
