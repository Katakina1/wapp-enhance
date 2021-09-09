package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.export.AnnouncementExcel;
import com.xforceplus.wapp.modules.base.export.TrainVenderExcel;
import com.xforceplus.wapp.modules.base.service.AnnouncementInquiryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class AnnouncementInquiryController extends AbstractController {

    private final static Logger LOGGER = getLogger(AnnouncementInquiryController.class);

    private final AnnouncementInquiryService announcementInquiryService;

    @Autowired
    public AnnouncementInquiryController(AnnouncementInquiryService announcementInquiryService) {
        this.announcementInquiryService = announcementInquiryService;
    }

    @SysLog("公告列表查询")
    @RequestMapping("announcementInquiry/list")
    public R announcementInquiryList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());
        params.put("usercode", getUser().getUsercode());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<AnnouncementEntity> infoPagedQueryResult = announcementInquiryService.announcementInquiryList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("公告附件下载")
    @RequestMapping(value = "export/downLoadFile")
    public void downLoadFile(@RequestParam("path") String path, HttpServletResponse response) {
        announcementInquiryService.getDownLoadFile(path,response);

    }

    @SysLog("未读公告列表查询")
    @RequestMapping("announcementInquiry/unreadList")
    public R announcementUnreadList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("usercode", getUser().getUsercode());
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<AnnouncementEntity> infoPagedQueryResult = announcementInquiryService.announcementUnreadList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("供应商关联信息列表查询")
    @RequestMapping("announcementInquiry/venderList")
    public R venderList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);


        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<UserEntity> infoPagedQueryResult = announcementInquiryService.venderList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("点击公告，阅读数量+1")
    @RequestMapping(value = "announcementInquiry/readPlus")
    public R announcementReadPlus(@RequestParam("announceId")Long announceId,@RequestParam("announcementType")String announcementType) {
        announcementInquiryService.announceUnReadPlus(getUserId(),announceId,announcementType);
        return R.ok();
    }

    @SysLog("培训公告，同意+1")
    @RequestMapping(value = "announcementInquiry/agreePlus")
    public R agreePlus(@RequestParam("announceId")Long announceId) {
        announcementInquiryService.announceAgreePlus(announceId,getUserId());
        return R.ok();
    }

    @SysLog("培训公告，不同意+1")
    @RequestMapping(value = "announcementInquiry/disagreePlus")
    public R disagreePlus(@RequestParam("announceId")Long announceId) {
        announcementInquiryService.announceDisagreePlus(announceId,getUserId());
        return R.ok();
    }

    @SysLog("公告删除")
    @RequestMapping("announcement/delete")
    public R delete(@RequestBody Long[] ids) {
        announcementInquiryService.deleteAnnounce(ids);
        return R.ok();
    }

    @SysLog("查询公告内容")
    @RequestMapping("announcementInquiry/queryAnnounce")
    public R queryTemplate(@RequestBody AnnouncementEntity entity) {
        AnnouncementEntity announcementInfo = announcementInquiryService.queryAnnounce(entity);
        return R.ok().put("announcementInfo",announcementInfo.getAnnouncementInfo());
    }

    @SysLog("修改公告")
    @RequestMapping("announcementInquiry/updateAnnounce.ignoreHtmlFilter")
    public R updateAnnounce(@RequestBody AnnouncementEntity entity) {
        announcementInquiryService.updateAnnounce(entity);
        return R.ok();
    }

    @SysLog("公告导出")
    @AuthIgnore
    @RequestMapping("export/annoucement")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final Map<String, List<AnnouncementEntity>> map = newHashMapWithExpectedSize(1);
        map.put("announcementList", announcementInquiryService.announcementInquiryList(params).getResults());
        //生成excel
        final AnnouncementExcel excelView = new AnnouncementExcel(map, "export/base/announcementList.xlsx", "announcementList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "announcementList" + excelName);
    }

    @SysLog("培训公告关联的供应商导出")
    @AuthIgnore
    @RequestMapping("export/trainVender")
    public void exportTrainVender(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);
        String announcementTitle = params.get("announcementTitle").toString();
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("trainAnnounceVenderList", announcementInquiryService.venderList(params).getResults());
        map.put("announcementTitle", announcementTitle);
        //生成excel
        final TrainVenderExcel excelView = new TrainVenderExcel(map, "export/base/trainAnnounceVenderList.xlsx", "trainAnnounceVenderList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "trainAnnounceVenderList" + excelName);
    }
}
