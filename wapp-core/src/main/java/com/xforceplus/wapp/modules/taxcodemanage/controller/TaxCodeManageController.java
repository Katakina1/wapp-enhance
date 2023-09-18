package com.xforceplus.wapp.modules.taxcodemanage.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.taxcodemanage.dto.TaxCodeImportDto;
import com.xforceplus.wapp.modules.taxcodemanage.service.TaxCodeManageService;
import com.xforceplus.wapp.repository.entity.TaxCodeManageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/taxCodeManage")
public class TaxCodeManageController {
    @Autowired
    TaxCodeManageService taxCodeManageService;


    /**
     * @param taxNo
     * @param taxName
     * @return
     */
    @GetMapping("/selectAll")
    public R<List> selectAll(@RequestParam(value = "taxNo",required = false) String taxNo,
                             @RequestParam(value = "taxName",required = false) String taxName) {
        return R.ok(taxCodeManageService.selectAll(taxNo,taxName));
    }

    /**
     * 新增
     * @param taxCodeManageEntity
     * @return
     */
    @PostMapping("/addTaxCode")
    public R addTaxCode(@RequestBody TaxCodeManageEntity taxCodeManageEntity) {
        int result = taxCodeManageService.addTaxCode(taxCodeManageEntity);
        return result == 1 ? R.ok(result) : R.fail("添加失败！");
    }

    /**
     * 修改
     * @param taxCodeManageEntity
     * @return
     */
    @PostMapping("/editTaxCode")
    public R editTaxCode(@RequestBody TaxCodeManageEntity taxCodeManageEntity) {
        int result = taxCodeManageService.editTaxCode(taxCodeManageEntity);
        return result == 1 ? R.ok(result) : R.fail("修改失败！");
    }

    /**
     * 删除
     * @param taxCodeManageEntity
     * @return
     */
    @RequestMapping(value = "/deleteTaxCode", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public R deleteTaxCode(TaxCodeManageEntity taxCodeManageEntity) {
        boolean result = taxCodeManageService.deleteTaxCode(taxCodeManageEntity);
        return result ? R.ok(result) : R.fail("删除失败！");
    }


  /*  @PutMapping("/import")
    public R batchImport(@ApiParam("导入的文件") @RequestParam(required = true) MultipartFile file) throws IOException {
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
            return R.fail("文件格式不正确");
        } else if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        long start = System.currentTimeMillis();
        Either<String, Integer> result = weekDaysService.importData(file);
        return result.isRight() ? R.ok(result.get(), String.format("导入成功[%d]条数据 导入失败数据请前往消息中心查看", result.get())) : R.fail(result.getLeft());
    }*/
   /* @PostMapping("/import")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), TaxCodeImportDto.class, new TaxCodeImportListener()).sheet().doRead();
        return "success";
    }*/

    /**
     * 导出
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/export")
    public R taxCodeExport(HttpServletResponse response) throws IOException {
        List<TaxCodeManageEntity> list = taxCodeManageService.selectAll(null,null);

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String Excelname="全电税号导出";

        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(Excelname,"UTF-8") + ".xlsx");
        writeExcel(response, list);
        return R.ok("导出成功") ;
    }

    /**
     * 导出设置
     * @param response
     * @param list
     * @throws IOException
     */
    public void writeExcel(HttpServletResponse response, List<TaxCodeManageEntity> list ) throws IOException {
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        //定义表头信息
        WriteSheet writeSheet = EasyExcel.writerSheet(0, "sheet").head(TaxCodeImportDto.class).build();
        //输出内容，输出位置
        excelWriter.write(list,writeSheet);
        //关流
        excelWriter.finish();
    }
}
