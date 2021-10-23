package com.xforceplus.wapp.modules.company.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.company.dto.CompanyUpdateRequest;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

/**
 * 索赔单业务逻辑
 */
@Slf4j
@RestController
@Api(tags = "抬头信息管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @ApiOperation("抬头信息分页查询")
    @GetMapping("/list/paged")
    public R<PageResult<TAcOrgEntity>> getOverdue(@ApiParam("页数") @RequestParam(required = false, defaultValue = "1") Long current,
                                                  @ApiParam("条数") @RequestParam(required = false, defaultValue = "10") Long size,
                                                  @ApiParam("税号") @RequestParam(required = false) String taxNo) {
        long start = System.currentTimeMillis();
        val page = companyService.page(current, size, taxNo);
        log.info("抬头信息分页查询,耗时:{}ms", System.currentTimeMillis() - start);
        return R.ok(PageResult.of(page._1, page._2.getTotal(), page._2.getPages(), page._2.getSize()));
    }


    @ApiOperation("根据税号抬头信息修改")
    @PostMapping("/updateByTaxNo")
    public R updateByTaxNo(@RequestBody CompanyUpdateRequest companyUpdateRequest) {
        if(StringUtils.isEmpty(companyUpdateRequest.getTaxNo())){
            return  R.fail("税号不能为空");
        }
        companyService.update(companyUpdateRequest);
        return R.ok("修改成功");
    }

    @ApiOperation("根据税号获取抬头信息")
    @GetMapping("/getByTaxNo")
    public R<TAcOrgEntity> getByTaxNo(@ApiParam("税号") @RequestParam(required = true) String taxNo) {

        TAcOrgEntity result = companyService.getByTaxNo(taxNo);
        if (result == null) {
            return R.fail("未查询到对应的抬头信息");
        }
        return R.ok(result);
    }

    @ApiOperation("抬头信息导入")
    @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file
    ) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = companyService.importData(file);
        log.info("抬头信息导入,耗时:{}ms", System.currentTimeMillis() - start);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据", result.get())) : R.fail(result.getLeft());
    }


    @ApiOperation("购方机构列表")
    @GetMapping("purchasers")
    public R purchaserOrg() {
        final List<TAcOrgEntity> purchaserOrgs = companyService.getPurchaserOrgs();
        return R.ok(Collections.singletonMap("orgs", purchaserOrgs));
    }

    @ApiOperation(value = "获取抬头模板")
    @GetMapping(value = "/template")
    public void template(HttpServletResponse res, HttpServletRequest req) {
        try {
            String name = "抬头信息导入模板";
            String fileName = name + ".xlsx";
            ServletOutputStream out;
            res.setContentType("multipart/form-data");
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            String filePath = getClass().getResource("/excl/" + fileName).getPath();
            String userAgent = req.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                fileName = new String((fileName).getBytes("UTF-8"), "ISO-8859-1");
            }
            filePath = URLDecoder.decode(filePath, "UTF-8");
            res.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            FileInputStream inputStream = null;

            inputStream = new FileInputStream(filePath);

            out = res.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                // 4.写到输出流(out)中
                out.write(buffer, 0, b);
            }
            inputStream.close();

            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
