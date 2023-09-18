package com.xforceplus.wapp.modules.common.export;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.modules.backfill.model.InvoiceFileEntity;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.backfill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.common.export.service.DownLoadSourceService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 非商业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "下载源文件")
@RequestMapping(EnhanceApi.BASE_PATH + "/downLoadSource")
public class DownLoadSourceFileController {

    @Autowired
    private DownLoadSourceService downLoadSourceService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private InvoiceFileService invoiceFileService;

    @Autowired

    private NoneBusinessService noneBusinessService;

    /**
     * 发票预览
     *
     * @return
     */
    @ApiOperation("发票预览")
    @RequestMapping(value = "/preview", method = {GET})
    public R preview(@ApiParam("id") @RequestParam() Long id) {
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(id);
        if (null == entity) {
            return R.fail("没有找到对应的发票信息");
        }
        String result;
        try {
            TXfInvoiceFileEntity tXfInvoiceFileEntity = invoiceFileService.getInvoiceFileUrl(entity.getInvoiceNo(), entity.getInvoiceCode());
            if (tXfInvoiceFileEntity != null) {
                String path = tXfInvoiceFileEntity.getPath();

                // 兼容费用上传的电子发票
                try {
                    File file = FileUtils.getFile(path);
                    result = new BASE64Encoder().encode(FileUtils.readFileToByteArray(file)).replaceAll("\n", "").replaceAll("\r", "");
                    // 目前仅支持图片和pdf文件的预览
                    if (StringUtils.equals(tXfInvoiceFileEntity.getFileSuffix().toLowerCase(), "pdf")) {
                        return R.ok("data:application/pdf;base64," + result);
                    }
                    return R.ok("data:image/jpeg;base64," + result);
                } catch (Exception e) {
                    log.error("查看费用电票上传异常:", e);
                }

                StringBuilder builder = new StringBuilder();
                if (path.startsWith("/evtaSystemIntegration")) {
                    builder.append("/u/app");
                    builder.append(path);
                    tXfInvoiceFileEntity.setPath(builder.toString());
                }
                if (path.startsWith("/u/evtaSystemIntegration")) {
                    builder.append(path.substring(0, 2));
                    builder.append("/app");
                    builder.append(path.substring(2, path.length()));
                    tXfInvoiceFileEntity.setPath(builder.toString());
                }
                if (!tXfInvoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_PDF)) {
                    result = fileService.downLoadFile(tXfInvoiceFileEntity.getPath());
                    result = result.replaceAll("\n", "").replaceAll("\r", "");
                    return R.ok("data:image/jpeg;base64," + result);
                } else {
                    result = fileService.downLoadFile(tXfInvoiceFileEntity.getPath());
                    result = result.replaceAll("\n", "").replaceAll("\r", "");
                    return R.ok("data:application/pdf;base64," + result);
                }
            } else {
                TXfNoneBusinessUploadQueryDto dto = new TXfNoneBusinessUploadQueryDto();
                dto.setInvoiceNo(entity.getInvoiceNo());
                dto.setInvoiceCode(entity.getInvoiceCode());
                dto.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
                List<TXfNoneBusinessUploadDetailDto> resultList = noneBusinessService.noPaged(dto);
                if (CollectionUtils.isEmpty(resultList)) {
                    return R.fail("没有找到对应的发票文件信息");
                }
                result = fileService.downLoadFile(resultList.get(0).getUploadPath());
                if (resultList.get(0).getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                    result = result.replaceAll("\n", "").replaceAll("\r", "");
                    return R.ok("data:image/jpeg;base64," + result);
                } else {
                    result = result.replaceAll("\n", "").replaceAll("\r", "");
                    return R.ok("data:application/pdf;base64," + result);
                }
            }

        } catch (Exception e) {
            log.error("电票预览下载文件异常:{}", e);
            return R.fail("电票预览下载文件异常");
        }

    }

    /**
     * 下载源文件
     *
     * @return
     */
    @ApiOperation("下载源文件")
    @PostMapping(value = "/batchDownLoad")
    public R<String> down(@RequestBody @ApiParam("id集合") Long[] ids) {
        try {
            List<TDxRecordInvoiceEntity> resultList = tDxRecordInvoiceDao.selectBatchIds(Arrays.asList(ids));
            if (CollectionUtils.isEmpty(resultList)) {
                return R.fail("没有找到对应的发票信息");
            }
            downLoadSourceService.down(resultList, ids);
            return R.ok("下载成功，请往消息中心查看下载结果");
        } catch (Exception e) {
            log.error("非商下载源文件异常:{}", e);
            return R.fail("下载源文件异常" + e.getMessage());
        }

    }

    /**
     * 下载源文件
     *
     * @return
     */
    @ApiOperation("下载pdf文件")
    @PostMapping(value = "/batchDownLoadPdf")
    public R<String> downPdf(@RequestBody @ApiParam("id集合") String[] idaas,
                             @RequestParam(required = false, defaultValue = "0")
                             @ApiParam("是否是非商下载，1是，0不是（默认）[如果是1传非商id，如果是0传发票代码+发票号码]") String noneBusiness) {
        try {
            if (idaas.length <= 0) {
                return R.fail("参数不能为空");
            }
            List<String> idList = new ArrayList<>(idaas.length);
            for (String id : idaas) {
                idList.add(java.net.URLEncoder.encode(id.replaceAll("\r", "%0D").replaceAll("\n", "%0A"), "UTF-8"));
            }
            List<TDxRecordInvoiceEntity> resultList = new ArrayList<>();
            List<TXfNoneBusinessUploadDetailEntity> entities = new ArrayList<>();
            if ("1".equalsIgnoreCase(noneBusiness)) {
                ArrayList<Long> list = Lists.newArrayList();
                idList.forEach(it -> list.add(Long.valueOf(it.toString())));
                entities = noneBusinessService.listByIds(list);
                if (CollectionUtils.isEmpty(entities)) {
                    return R.fail("没有找到对应的发票信息");
                }
            } else {
                resultList = new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao).in(TDxRecordInvoiceEntity::getUuid, idList).list();
                if (CollectionUtils.isEmpty(resultList)) {
                    return R.fail("没有找到对应的发票信息");
                }
            }
            downLoadSourceService.downPdf(resultList, entities, idList.toArray(new String[]{}));
            return R.ok("下载成功，请往消息中心查看下载结果");
        } catch (Exception e) {
            log.error("非商下载pdf文件异常:{}", e.getMessage(), e);
            return R.fail("下载pdf文件异常" + e.getMessage());
        }

    }


}