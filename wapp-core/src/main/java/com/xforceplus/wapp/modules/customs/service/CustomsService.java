package com.xforceplus.wapp.modules.customs.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.customs.*;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.customs.convert.BillStatusEnum;
import com.xforceplus.wapp.modules.customs.convert.CheckPurposeExportEnum;
import com.xforceplus.wapp.modules.customs.convert.CheckTypeExportEnum;
import com.xforceplus.wapp.modules.customs.convert.ManageStatusExportEnum;
import com.xforceplus.wapp.modules.customs.dto.*;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.ExcelExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 海关缴款书
 */
@Service
@Slf4j
public class CustomsService extends ServiceImpl<TDxCustomsDao, TDxCustomsEntity> {

    @Autowired
    private TDxCustomsDao tDxCustomsDao;

    @Autowired
    private TDxCustomsDetailDao tDxCustomsDetailDao;

    @Autowired
    private TDxCustomsLogDao tDxCustomsLogDao;

    @Autowired
    private TDxCustomsTaskDao tDxCustomsTaskDao;

    @Autowired
    private TDxCustomsExtDao tDxCustomsExtDao;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private ExcelExportUtils excelExportUtils;

    @Value("${wapp.export.tmp}")
    private String tmp;

    //全选最大数量
    private final Integer MAX_PAGE_SIZE = 5000;

    /**
     * @Description 分页查询
     * @Author pengtao
     * @return
     **/
    public PageResult<CustomsDto> paged(CustomsQueryDto request) {
        //校验请求参数
        if(Objects.isNull(request)){
            throw new EnhanceRuntimeException("海关缴款书查询参数不允许为空");
        }
        //日期处理
        dealDate(request);

        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);
        //海关缴款书号处理
        dealCustomsNo(request);
        //勾选状态处理
        dealIsCheck(request);

        log.info("海关缴款书勾选查询--处理后的请求参数{}", JSON.toJSON(request));
        //总个数
        int count = queryCount(request);
        //获取数据
        List<TDxCustomsEntity> customsEntities = this.queryByPage(request);
        List<CustomsDto> response = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(customsEntities)){
            for(TDxCustomsEntity customsEntitie:customsEntities){
                CustomsDto customsDto = copyEntity(customsEntitie);
                // 查询海关缴款书明细, 获取主信息与明细信息的税额差
                // 设置税额差 税额差是主表的税额减去缴款书明细税额合计差额
                customsDto.setTaxAmountDifference(Objects.nonNull(customsEntitie.getTaxAmountDifference())?
                        customsEntitie.getTaxAmountDifference().toString():null);

                //WALMART-3328 勾选失败原因，失败的状态下才展示
                if(Arrays.asList(CheckTypeExportEnum.CHECK_TYPE_5.getCode(),CheckTypeExportEnum.CHECK_TYPE_6.getCode(),
                        CheckTypeExportEnum.CHECK_TYPE__1.getCode()).contains(customsEntitie.getIsCheck())){
                    customsDto.setAuthRemark(customsEntitie.getAuthRemark());
                }else{
                    customsDto.setAuthRemark("");
                }
                response.add(customsDto);
            }
        }
        return PageResult.of(response,count,pageNo, request.getPageSize());
    }

    /**
     * @Description 根据海关缴款书号码查询数据
     * @Author pengtao
     * @return
    **/
    public List<TDxCustomsEntity> queryByCustomsNo(List<String> request){
        QueryWrapper<TDxCustomsEntity> wrapper = new QueryWrapper<>();
        wrapper.in(CollectionUtils.isNotEmpty(request),TDxCustomsEntity.CUSTOMSNO,request);
        return tDxCustomsDao.selectList(wrapper);
    }

    /**
     * @Description 根据海关缴款书号码查询数据
     * @Author pengtao
     * @return
     **/
    public List<TDxCustomsEntity> queryByCustomsNo(String request){
        QueryWrapper<TDxCustomsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request),TDxCustomsEntity.CUSTOMSNO,request);
        return tDxCustomsDao.selectList(wrapper);
    }

    /**
     * @Description 查询个数
     * @Author pengtao
     * @return
     **/
    public Integer count(CustomsQueryDto request) {
        LambdaQueryWrapper<TDxCustomsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request.getCustomsNo()),TDxCustomsEntity::getCustomsNo,request.getCustomsNo());
        wrapper.eq(StringUtils.isNotEmpty(request.getCompanyTaxNo()),TDxCustomsEntity::getCompanyTaxNo,request.getCompanyTaxNo());
        wrapper.eq(StringUtils.isNotEmpty(request.getCompanyName()),TDxCustomsEntity::getCompanyName,request.getCompanyName());
        wrapper.eq(StringUtils.isNotEmpty(request.getVoucherAccountTimeStart()), TDxCustomsEntity::getVoucherAccountTime, request.getVoucherAccountTimeStart() + " 00:00:01");
        wrapper.eq(StringUtils.isNotEmpty(request.getVoucherAccountTimeEnd()),TDxCustomsEntity::getVoucherAccountTime, request.getVoucherAccountTimeEnd() + " 23:59:59");
        return this.count(wrapper);
    }

    /**
     * @Description 查询数量
     * @Author pengtao
     * @return
    **/
    public Integer queryCount(CustomsQueryDto request){
        return  tDxCustomsExtDao.countCustoms(request.getCustomsNo(),request.getManageStatus(),
                request.getCompanyTaxNo(),request.getCompanyName(),request.getIsCheck(),
                request.getPaperDrewDateStart(),request.getPaperDrewDateEnd(),
                request.getCheckTimeStart(),request.getCheckTimeEnd(),request.getTaxPeriod(),
                request.getVoucherNo(),request.getAccountStatus(),request.getConfirmStatus(),request.getBillStatus());
    }

    /**
     * @Description 查询认证状态数量
     * @Author pengtao
     * @return
     **/
    public List<QueryCustomsTabResponse> queryAuthCount(){
        List<QueryCustomsTabResponse> tabList = new ArrayList<>();

        List<Map<String, Object>> listMaps = tDxCustomsExtDao.queryAuthCount();
        Map<Integer,Integer> statusMap = new HashMap<>();
        for (Map<String, Object> listMap : listMaps) {
            statusMap.put(Integer.parseInt(listMap.get("is_check").toString()), Integer.parseInt(listMap.get("num").toString()));
        }

        List<CustomsQueryTabEnum> queryCheckEnums = Arrays.asList(CustomsQueryTabEnum.values());

        for (CustomsQueryTabEnum tabEnum : queryCheckEnums) {
            //跳过不需要展示的
            if (Arrays.asList(CustomsQueryTabEnum.ALL,CustomsQueryTabEnum.QUERY_CHECK_1,CustomsQueryTabEnum.QUERY_CHECK_6,
                    CustomsQueryTabEnum.QUERY_CHECK_8).contains(tabEnum)) {
                continue;
            }

            int tabCount = 0;
            for (Integer queryParam : tabEnum.queryParams()) {
                tabCount += statusMap.getOrDefault(queryParam, 0);
            }
            tabList.add(QueryCustomsTabResponse.builder().key(tabEnum.code()).num(tabCount).desc(tabEnum.message()).build());
        }

        // 全部 tab 添加，暂不展示
//        tabList.add(QueryCustomsTabResponse.builder().key(CustomsQueryTabEnum.ALL.code()).
//                num(tabList.stream().mapToInt(QueryCustomsTabResponse::getNum).sum()).
//                desc(SettlementQueryTabEnum.ALL.message()).build());

        return tabList;
    }

    /**
     * @Description 入账状态首页统计
     * @Author pengtao
     * @return
    **/
    public List<QueryCustomsTabResponse> queryEntryCount() {
        List<QueryCustomsTabResponse> tabList = new ArrayList<>();

        List<Map<String, Object>> listMaps = tDxCustomsExtDao.queryEntryCount();
        Map<Object, Integer> statusMap = new HashMap<>();
        for (Map<String, Object> listMap : listMaps) {
            if(Objects.nonNull(listMap.get("account_status"))){
                statusMap.put(listMap.get("account_status").toString(), Integer.parseInt(listMap.get("num").toString()));
            }
        }

        List<CustomsQueryAccountTabEnum> queryCheckEnums = Arrays.asList(CustomsQueryAccountTabEnum.values());

        for (CustomsQueryAccountTabEnum tabEnum : queryCheckEnums) {
            //跳过不需要展示的
            if (Arrays.asList(CustomsQueryAccountTabEnum.ALL,CustomsQueryAccountTabEnum.QUERY_ACCOUNT_06).contains(tabEnum)) {
                continue;
            }

            int tabCount = 0;
            for (String queryParam : tabEnum.queryParams()) {
                tabCount += statusMap.getOrDefault(queryParam, 0);
            }
            tabList.add(QueryCustomsTabResponse.builder().key(tabEnum.code()).num(tabCount).desc(tabEnum.message()).build());
        }

        return tabList;
    }

    /**
     * @Description 查询数据
     * @Author pengtao
     * @return
     **/
    public List<TDxCustomsEntity> queryByPage(CustomsQueryDto request){
        //获取数据
        return  tDxCustomsExtDao.queryPageCustoms(request.getPageNo(),request.getPageSize(),request.getCustomsNo(),
                request.getManageStatus(), request.getCompanyTaxNo(),request.getCompanyName(),request.getIsCheck(),
                request.getPaperDrewDateStart(),request.getPaperDrewDateEnd(),
                request.getCheckTimeStart(),request.getCheckTimeEnd(),request.getTaxPeriod(),
                request.getVoucherNo(),request.getAccountStatus(), request.getConfirmStatus(),request.getBillStatus());
    }


    /**
     * @Description 实体转换
     * @Author pengtao
     * @return
     **/
    public CustomsDto copyEntity(TDxCustomsEntity customsEntity){
        CustomsDto customsDto = new CustomsDto();
        BeanUtil.copyProperties(customsEntity,customsDto);
        return customsDto;
    }

    /**
     * @Description 请求日期处理
     * @Author pengtao
     * @return
     **/
    public void dealDate(CustomsQueryDto request){
        //填发日期校验
        if(StringUtils.isBlank(request.getPaperDrewDateStart())&&StringUtils.isNotBlank(request.getPaperDrewDateEnd())
                ||StringUtils.isNotBlank(request.getPaperDrewDateStart())&&StringUtils.isBlank(request.getPaperDrewDateEnd())){
            throw new EnhanceRuntimeException("填开日期开始和结束时间都需要有值");
        }else{
            //日期格式处理，去掉-
            request.setPaperDrewDateStart(request.getPaperDrewDateStart().replace("-",""));
            request.setPaperDrewDateEnd(request.getPaperDrewDateEnd().replace("-",""));
        }
        //勾选日期校验
        if(StringUtils.isBlank(request.getCheckTimeStart())&&StringUtils.isNotBlank(request.getCheckTimeEnd())
                ||StringUtils.isNotBlank(request.getCheckTimeStart())&&StringUtils.isBlank(request.getCheckTimeEnd())){
            throw new EnhanceRuntimeException("勾选日期开始和结束时间都需要有值");
        }

        //撤销勾选日期校验
        if(StringUtils.isBlank(request.getUnCheckTimeStart())&&StringUtils.isNotBlank(request.getUnCheckTimeEnd())
                ||StringUtils.isNotBlank(request.getUnCheckTimeStart())&&StringUtils.isBlank(request.getUnCheckTimeEnd())){
            throw new EnhanceRuntimeException("撤销勾选日期开始和结束时间都需要有值");
        }
    }

    /**
     * @Description 海关缴款书号码查询处理
     * @Author pengtao
     * @return
    **/
    public void dealCustomsNo(CustomsQueryDto request) {
        //海关缴款书号码处理
        if(StringUtils.isNotBlank(request.getCustomsNo())){
            /*if(countUserCode(request.getCustomsNo())>9){
                throw new EnhanceRuntimeException("海关缴款书号码查询数量最大为10个");
            }*/
            String[] itemNoSplit = request.getCustomsNo().split(",");
            StringBuffer strBuf = new StringBuffer("");
            for(String str:itemNoSplit){
                if(!str.startsWith("'")&&!str.endsWith("'")){
                    strBuf.append("'").append(str.trim()).append("',");
                }
            }
            if(strBuf.length()>1){
                strBuf.deleteCharAt(strBuf.length()-1);
            }
            request.setCustomsNo(strBuf.toString());
        }
    }

    /**
     * @Description 勾选状态处理
     * @Author pengtao
     * @return
    **/
    public void dealIsCheck(CustomsQueryDto request) {

        if(StringUtils.equals("all",request.getIsCheck())){
            //按照全选状态处理
            List<String> checkEnum = Arrays.asList(InvoiceCheckEnum.CHECK_0.getValue().toString(),
                    InvoiceCheckEnum.CHECK_2.getValue().toString(), InvoiceCheckEnum.CHECK_3.getValue().toString(),
                    InvoiceCheckEnum.CHECK_4.getValue().toString(),InvoiceCheckEnum.CHECK_5.getValue().toString(),
                    InvoiceCheckEnum.CHECK_9.getValue().toString(),InvoiceCheckEnum.CHECK_N1.getValue().toString(),
                    //WALMART-3277 4,6,8状态联查
                    InvoiceCheckEnum.CHECK_6.getValue().toString(),InvoiceCheckEnum.CHECK_8.getValue().toString()
            );
            request.setIsCheck(checkEnum.stream().collect(Collectors.joining(",")));
            dealIsCheck(request);
        }
        //海关票勾选状态处理
        else if(StringUtils.isNotBlank(request.getIsCheck())){
            //WALMART-3277 4,6,8状态联查
            if(StringUtils.equals(InvoiceCheckEnum.CHECK_4.getValue().toString(),request.getIsCheck())){
                request.setIsCheck(request.getIsCheck()+","+InvoiceCheckEnum.CHECK_6.getValue().toString()+
                        ","+InvoiceCheckEnum.CHECK_8.getValue().toString());
            }

            String[] isCheckSplit = request.getIsCheck().split(",");
            StringBuffer strBuf = new StringBuffer("");
            for(String str:isCheckSplit){
                if(!str.startsWith("'")&&!str.endsWith("'")){
                    strBuf.append("'").append(str.trim()).append("',");
                }
            }
            if(strBuf.length()>1){
                strBuf.deleteCharAt(strBuf.length()-1);
            }
            request.setIsCheck(strBuf.toString());
        } else {
            //按照全选状态处理
            List<String> checkEnum = Arrays.asList(InvoiceCheckEnum.CHECK_0.getValue().toString(),
                    InvoiceCheckEnum.CHECK_2.getValue().toString(), InvoiceCheckEnum.CHECK_3.getValue().toString(),
                    InvoiceCheckEnum.CHECK_4.getValue().toString(),InvoiceCheckEnum.CHECK_5.getValue().toString(),
                    InvoiceCheckEnum.CHECK_9.getValue().toString(),InvoiceCheckEnum.CHECK_N1.getValue().toString(),
                    //WALMART-3277 4,6,8状态联查
                    InvoiceCheckEnum.CHECK_6.getValue().toString(),InvoiceCheckEnum.CHECK_8.getValue().toString()
                    );
            request.setIsCheck(checkEnum.stream().collect(Collectors.joining(",")));
            dealIsCheck(request);
        }
    }

    /**
     * @Description 限定10个
     * @Author pengtao
     * @return
    **/
    public int countUserCode(String customsNo){
        int cnt = 0;
        if(customsNo.contains(",")){
            cnt = customsNo.length()-customsNo.replaceAll(",","").length();
        }
        return cnt;
    }

    /**
     * @Description 更新海关缴款书
     * @param
     * @Date
     * @Author pengtao
     * @return
    **/
    public R updateCustoms(CustomsUpdateRequest request) {
        log.info("海关缴款书更新参数:{}",JSON.toJSON(request));
        //校验请求参数
        if(Objects.isNull(request)){
            return R.fail("海关缴款书更新请求参数有误");
        }
        TDxCustomsEntity tDxCustomsEntity = tDxCustomsExtDao.selectById(request.getId());
        if(Objects.isNull(tDxCustomsEntity)){
            return R.fail("未查询到对应的海关缴款书数据");
        }

        if(StringUtils.isEmpty(request.getType())){
            return R.fail("操作类型不能为空");
        }
        TDxCustomsEntity entity = new TDxCustomsEntity();
        if(StringUtils.equals(request.getType(), OpTypeEnum.OP_TYPE_1.getCode().toString())) {
            if (Objects.nonNull(request.getEffectiveTaxAmount())) {
                entity.setEffectiveTaxAmount(request.getEffectiveTaxAmount());
            } else {
                return R.fail("有效税款金额不能为空");
            }
        }
        if(StringUtils.equals(request.getType(),OpTypeEnum.OP_TYPE_2.getCode().toString())) {
            if (Objects.nonNull(request.getTaxPeriod())) {
                entity.setTaxPeriod(request.getTaxPeriod());
            } else {
                return R.fail("所属期不能为空");
            }
        }

        if(StringUtils.equals(request.getType(),OpTypeEnum.OP_TYPE_3.getCode().toString())) {

            if (Objects.nonNull(request.getVoucherNo())) {
                entity.setVoucherNo(request.getVoucherNo());
            } else {
                return R.fail("凭证号不能为空");
            }
            //WALMART-3325 【-1,0,1,3,4,6,8】状态下不允许更新凭证号
            if(Arrays.asList(InvoiceCheckEnum.CHECK_N1.getCode().toString(),InvoiceCheckEnum.CHECK_0.getCode().toString(),
                    InvoiceCheckEnum.CHECK_1.getCode().toString(), InvoiceCheckEnum.CHECK_3.getCode().toString(),
                    InvoiceCheckEnum.CHECK_4.getCode().toString(), InvoiceCheckEnum.CHECK_6.getCode().toString(),
                    InvoiceCheckEnum.CHECK_8.getCode().toString())
                    .contains(tDxCustomsEntity.getIsCheck())){
//                String errorMsg = InvoiceCheckEnum.getInvoiceCheckEnum(Integer.parseInt(tDxCustomsEntity.getIsCheck())).getDesc();
                return R.fail("已勾选/撤销中/勾选中状态下不允许更新凭证号");
            }

            //修改凭证入账日期
            if (Objects.nonNull(request.getVoucherAccountTime())) {
                entity.setVoucherAccountTime(request.getVoucherAccountTime());
            } else {
                return R.fail("凭证入账日期不能为空");
            }
        }

        entity.setId(request.getId());
        int status = tDxCustomsExtDao.updateById(entity);
        log.info("海关缴款书更新状态:{}",status);
        if(status!=-1){
            return R.ok("更新成功");
        }
        return R.fail("更新失败");
    }

    /**
     * @Description 海关缴款书导入变更，异常提示导出excel
     * @Author pengtao
     * @return
     **/
    public CustomsImportSizeDto uploadImportData(MultipartFile file) throws Exception {
        CustomsImportSizeDto sizeDto = new CustomsImportSizeDto();
        CustomsImportListener listener = new CustomsImportListener();
        EasyExcel.read(file.getInputStream(), CustomsImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }
        log.info("导入更新海关缴款书数据:{}",JSON.toJSON(listener.getValidInvoices()));
        List<CustomsQueryDto> updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {

            for (CustomsImportDto dto : listener.getValidInvoices()) {
                CustomsQueryDto request = new CustomsQueryDto();
                request.setPageNo(0);
                request.setCustomsNo(dto.getCustomsNo());
                //缴款书号码处理
                dealCustomsNo(request);
                List<TDxCustomsEntity> entities = queryByPage(request);
                if (CollectionUtils.isEmpty(entities)) {
                    dto.setErrorMsg("未找到对应的海关缴款书数据");
                    listener.getInvalidInvoices().add(dto);
                    continue;
                }

                TDxCustomsEntity entity = new TDxCustomsEntity();
                entity.setId(entities.stream().findFirst().get().getId());

                //WALMART-3325 【-1,0,1,3,4,6,8】状态下不允许更新凭证号
                String isCheck = entities.stream().findFirst().get().getIsCheck();
                if(StringUtils.isNotBlank(isCheck)){
                    if(Arrays.asList(InvoiceCheckEnum.CHECK_N1.getCode().toString(),InvoiceCheckEnum.CHECK_0.getCode().toString(),
                            InvoiceCheckEnum.CHECK_1.getCode().toString(), InvoiceCheckEnum.CHECK_3.getCode().toString(),
                            InvoiceCheckEnum.CHECK_4.getCode().toString(), InvoiceCheckEnum.CHECK_6.getCode().toString(),
                            InvoiceCheckEnum.CHECK_8.getCode().toString()).contains(isCheck)){
//                        String errorMsg = InvoiceCheckEnum.getInvoiceCheckEnum(Integer.parseInt(isCheck)).getDesc();
//                        dto.setErrorMsg(errorMsg+"状态下不允许更新凭证号");
                        dto.setErrorMsg("已勾选/撤销中/勾选中状态下不允许更新凭证号");

                        listener.getInvalidInvoices().add(dto);
                        continue;
                    }
                }

                entity.setVoucherNo(dto.getVoucherNo());
                //凭证入账日期
                entity.setVoucherAccountTime(DateUtils.strToDate(dto.getVoucherAccountTime()));
                entity.setUpdateTime(new Date());
                int status = tDxCustomsExtDao.updateById(entity);
                log.info("海关缴款书导入更新条数:{},海关缴款书号码:{}", status, request.getCustomsNo());
                updateList.add(request);
            }
        }

        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, file.getOriginalFilename());
            EasyExcel.write(tmp + "/" + file.getOriginalFilename(), CustomsImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String exportFileName = "导入失败原因" + String.valueOf(System.currentTimeMillis()) + ExcelExportUtil.FILE_NAME_SUFFIX;
            String ftpFilePath = ftpPath + "/" + exportFileName;
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, exportFileName, inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setConditions(file.getOriginalFilename());
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpFilePath);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "海关缴款书凭证号导入错误信息", exportCommonService.getSuccContent());
        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(updateList.size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        return sizeDto;
    }

    /**
     * @Description 导出
     * @param resultList  要导出的数据
     * @param request 请求参数
     * @param type 勾选/入账
     * @return 
    **/
    public R export(List<TDxCustomsEntity> resultList, CustomsValidSubmitRequest request,String type) {
        String fileName = "海关缴款书"+type;
        try {
            List<CustomsExportDto> exportDtos = new ArrayList<>();
            for(TDxCustomsEntity entity:resultList){
                CustomsExportDto dto = new CustomsExportDto();
                dto.setCustomsNo(entity.getCustomsNo());
                dto.setCompanyName(entity.getCompanyName());
                dto.setCompanyTaxNo(entity.getCompanyTaxNo());

                //勾选状态
                if(Objects.nonNull(entity.getIsCheck())){
                    dto.setIsCheck(CheckTypeExportEnum.getValue(entity.getIsCheck()));
                    //勾选失败原因，失败的状态下才展示
                    if(Arrays.asList(CheckTypeExportEnum.CHECK_TYPE_5.getCode(),CheckTypeExportEnum.CHECK_TYPE_6.getCode(),
                            CheckTypeExportEnum.CHECK_TYPE__1.getCode()).contains(entity.getIsCheck())){
                        dto.setAuthRemark(entity.getAuthRemark());
                    }
                }

                //抵扣用途
                if(Objects.nonNull(entity.getCheckPurpose())){
                    dto.setCheckPurpose(CheckPurposeExportEnum.getValue(entity.getCheckPurpose()));
                }
                //税款金额
                dto.setTaxAmount(entity.getTaxAmount().toString());
                //有效抵扣税款金额
                dto.setEffectiveTaxAmount(entity.getEffectiveTaxAmount().toPlainString());

                //填开日期格式调整为yyyy-MM-dd
                if(StringUtils.isNotBlank(entity.getPaperDrewDate())){
                    dto.setPaperDrewDate(DateUtils.toFormatDate(entity.getPaperDrewDate()));
                }

                //勾选日期转yyyy-MM-dd
                if(Objects.nonNull(entity.getCheckTime())){
                    dto.setCheckTime(DateUtils.format(entity.getCheckTime()));
                }

                //撤销勾选日期转yyyy-MM-dd
                if(Objects.nonNull(entity.getUnCheckTime())){
                    dto.setUnCheckTime(DateUtils.format(entity.getUnCheckTime()));
                }

                //所属期yyyy-MM
                if(StringUtils.isNotBlank(entity.getTaxPeriod())){
                    dto.setTaxPeriod(DateUtils.toFormatDateMM(entity.getTaxPeriod()));
                }

                //管理状态
                if(Objects.nonNull(entity.getManageStatus())){
                    dto.setManageStatus(ManageStatusExportEnum.getValue(entity.getManageStatus()));
                }

                //用途
                if(Objects.nonNull(entity.getCheckPurpose())){
                    dto.setCheckPurpose(CheckPurposeExportEnum.getValue(entity.getCheckPurpose()));
                }

                exportDtos.add(dto);
            }

            excelExportUtils.messageExportOneSheet(exportDtos, CustomsExportDto.class, fileName, JSONObject.toJSONString(request), "sheet1");
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        }
        return R.ok();
    }


    /**
     * @Description 导出
     * @param resultList  要导出的数据
     * @param request 请求参数
     * @return
     **/
    public R entryExport(List<TDxCustomsEntity> resultList, CustomsValidSubmitRequest request) {
        String fileName = "海关缴款书入账";
        try {
            List<CustomsEntryExportDto> exportDtos = new ArrayList<>();
            for(TDxCustomsEntity entity:resultList){
                CustomsEntryExportDto dto = new CustomsEntryExportDto();
                dto.setCustomsNo(entity.getCustomsNo());
                dto.setCompanyName(entity.getCompanyName());
                dto.setCompanyTaxNo(entity.getCompanyTaxNo());

                //勾选状态
                if(Objects.nonNull(entity.getIsCheck())){
                    dto.setIsCheck(CheckTypeExportEnum.getValue(entity.getIsCheck()));
                    //勾选失败原因，失败的状态下才展示
                    if(Arrays.asList(CheckTypeExportEnum.CHECK_TYPE_5.getCode(),CheckTypeExportEnum.CHECK_TYPE_6.getCode(),
                            CheckTypeExportEnum.CHECK_TYPE__1.getCode()).contains(entity.getIsCheck())){
                        dto.setAuthRemark(entity.getAuthRemark());
                    }
                }

                //抵扣用途
                if(Objects.nonNull(entity.getCheckPurpose())){
                    dto.setCheckPurpose(CheckPurposeExportEnum.getValue(entity.getCheckPurpose()));
                }
                //税款金额
                dto.setTaxAmount(entity.getTaxAmount().toString());
                //有效抵扣税款金额
                dto.setEffectiveTaxAmount(entity.getEffectiveTaxAmount().toPlainString());

                //填开日期格式调整为yyyy-MM-dd
                if(StringUtils.isNotBlank(entity.getPaperDrewDate())){
                    dto.setPaperDrewDate(DateUtils.toFormatDate(entity.getPaperDrewDate()));
                }

                //勾选日期转yyyy-MM-dd
                if(Objects.nonNull(entity.getCheckTime())){
                    dto.setCheckTime(DateUtils.format(entity.getCheckTime()));
                }

                //撤销勾选日期转yyyy-MM-dd
                if(Objects.nonNull(entity.getUnCheckTime())){
                    dto.setUnCheckTime(DateUtils.format(entity.getUnCheckTime()));
                }

                //所属期yyyy-MM
                if(StringUtils.isNotBlank(entity.getTaxPeriod())){
                    dto.setTaxPeriod(DateUtils.toFormatDateMM(entity.getTaxPeriod()));
                }

                //管理状态
                if(Objects.nonNull(entity.getManageStatus())){
                    dto.setManageStatus(ManageStatusExportEnum.getValue(entity.getManageStatus()));
                }

                //用途
                if(Objects.nonNull(entity.getCheckPurpose())){
                    dto.setCheckPurpose(CheckPurposeExportEnum.getValue(entity.getCheckPurpose()));
                }

                //入账导出主信息补充
                if(Objects.nonNull(entity.getVoucherAccountTime())){
                    dto.setVoucherAccountTime(DateUtils.format(entity.getVoucherAccountTime()));
                }
                dto.setVoucherNo(entity.getVoucherNo());

                //入账状态
                if(Objects.nonNull(entity.getAccountStatus())){
                    dto.setAccountStatus(AccountStatusEnum.getValue(entity.getAccountStatus()));
                }

                dto.setContractNo(entity.getContractNo());

                dto.setCustomsDocNo(entity.getCustomsDocNo());

                //对比状态
                if(Objects.nonNull(entity.getBillStatus())){
                    dto.setBillStatus(BillStatusEnum.getValue(entity.getBillStatus()));
                }

                dto.setAuthRemark(entity.getAuthRemark());
                if(Objects.nonNull(entity.getTaxAmountDifference())){
                    dto.setTaxAmountDifference(entity.getTaxAmountDifference().toPlainString());
                }
                dto.setAbnormalInfo(entity.getAbnormalInfo());
                exportDtos.add(dto);
            }
            List<String> customsNos = resultList.stream().map(TDxCustomsEntity::getCustomsNo).distinct().collect(Collectors.toList());
            List<CustomsEntryDetailsExportDto> detailsExportDtos = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(customsNos)){
                //数据超过SQL执行需要分组
                List<List<String>> customsNoSubs = ListUtils.partition(customsNos,300);
                for(List<String> customsNoSub:customsNoSubs){
                    LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.in(TDxCustomsDetailEntity::getCustomsNo,customsNoSub);
                    List<TDxCustomsDetailEntity> details = tDxCustomsDetailDao.selectList(queryWrapper);
                    if(CollectionUtils.isNotEmpty(details)){
                        for(TDxCustomsDetailEntity detailEntity:details){
                            CustomsEntryDetailsExportDto detailsDto = new CustomsEntryDetailsExportDto();
                            BeanUtil.copyProperties(detailEntity,detailsDto);
                            //金额保留2位小数
                            detailsDto.setTaxAmount(decimalFormat(detailEntity.getTaxAmount()));

                            detailsDto.setDutiablePrice(decimalFormat(detailEntity.getDutiablePrice()));
                            //税率保留整数位
                            detailsDto.setTaxRate(decimalTaxRateFormat(detailEntity.getTaxRate()));

                            detailsExportDtos.add(detailsDto);
                        }
                    }
                }
            }

            ArrayList<List> lists = new ArrayList<>();
            lists.add(exportDtos);
            lists.add(detailsExportDtos);
            ArrayList<Class> clazzs = new ArrayList<>();
            clazzs.add(CustomsEntryExportDto.class);
            clazzs.add(CustomsEntryDetailsExportDto.class);
            excelExportUtils.messageExportMoreSheet(lists, clazzs, fileName, JSONObject.toJSONString(request), "主信息", "明细信息");

        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        }
        return R.ok();
    }

    /**
     * @Description 金额保留2位
     * @Author pengtao
     * @return
     **/
    public static String decimalFormat(BigDecimal amount){
        BigDecimal value = new BigDecimal(0);
        if(null!=amount){
            value = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(value);
    }

    /**
     * @Description 金额保留整数位
     * @Author pengtao
     * @return
     **/
    public static String decimalTaxRateFormat(BigDecimal amount){
        BigDecimal value = new BigDecimal(0);
        if(null!=amount){
            value = amount.setScale(0,BigDecimal.ROUND_HALF_UP);
        }
        // 不足小数补0
        DecimalFormat decimalFormat = new DecimalFormat("#");
        return decimalFormat.format(value);
    }

    /**
     * @Description 异常导出
     * @param resultList  要导出的数据
     * @param request 请求参数
     * @param type 勾选/入账
     * @return
     **/
    public R abnormalExport(List<TDxCustomsEntity> resultList, CustomsValidSubmitRequest request,String type) {
        String fileName = "海关缴款书"+type;
        try {
            List<CustomsExport4Dto> exportDtos = new ArrayList<>();
            for(TDxCustomsEntity entity:resultList){
                CustomsExport4Dto dto = new CustomsExport4Dto();
                dto.setCustomsNo(entity.getCustomsNo());
                dto.setCompanyName(entity.getCompanyName());
                dto.setCompanyTaxNo(entity.getCompanyTaxNo());
                //填开日期格式调整为yyyy-MM-dd
                if(StringUtils.isNotBlank(entity.getPaperDrewDate())){
                    dto.setPaperDrewDate(DateUtils.toFormatDate(entity.getPaperDrewDate()));
                }
                dto.setTaxAmount(entity.getTaxAmount().toString());
                dto.setEffectiveTaxAmount(entity.getEffectiveTaxAmount().toPlainString());
                //勾选状态
                if(Objects.nonNull(entity.getIsCheck())){
                    dto.setIsCheck(CheckTypeExportEnum.getValue(entity.getIsCheck()));
                }
                //管理状态
                if(Objects.nonNull(entity.getManageStatus())){
                    dto.setManageStatus(ManageStatusExportEnum.getValue(entity.getManageStatus()));
                }

                exportDtos.add(dto);
            }

            excelExportUtils.messageExportOneSheet(exportDtos, CustomsExport4Dto.class, fileName, JSONObject.toJSONString(request), "sheet1");

        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        }
        return R.ok();
    }

    /**
     * @Description 根据Ids查询
     * @Author pengtao
     * @return
     **/
    public List<CustomsExportDto> getByIds(List<Long> ids) {
        List<TDxCustomsEntity> entities = tDxCustomsDao.selectBatchIds(ids);
        List<CustomsExportDto> resultList = new ArrayList<>();
        CustomsExportDto customsExportDto;
        if(CollectionUtils.isNotEmpty(entities)){
            for (TDxCustomsEntity entity:entities){
                customsExportDto = new CustomsExportDto();
                customsExportDto.setCustomsNo(entity.getCustomsNo());
                customsExportDto.setPaperDrewDate(entity.getPaperDrewDate());
                customsExportDto.setIsCheck(entity.getIsCheck());
                customsExportDto.setTaxAmount(entity.getTaxAmount().toString());
                customsExportDto.setEffectiveTaxAmount(entity.getEffectiveTaxAmount().toPlainString());
                customsExportDto.setCheckPurpose(entity.getCheckPurpose());
                resultList.add(customsExportDto);
            }
        }
        return resultList;
    }

    /**
     * 根据ids查询数据
     * @param includes
     * @return
     */
    public List<TDxCustomsEntity> getByBatchIds(List<Long> includes) {
        List<List<Long>> subs = ListUtils.partition(includes , 300);
        LambdaQueryWrapper<TDxCustomsEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 对数据进行切分, 避免sql过长
        List<TDxCustomsEntity> entities = new ArrayList<>();
        for (List<Long> sub : subs) {
            queryWrapper.in(TDxCustomsEntity::getId, sub);
            List<TDxCustomsEntity> list = this.list(queryWrapper);
            entities.addAll(list);
        }
        return entities;
    }

    public R saveCustoms(CustomsSaveRequest request) {
        log.info("海关缴款书手工录入参数:{}",JSON.toJSON(request));
        //校验请求参数
        if(Objects.isNull(request)){
            return R.fail("海关缴款书手工录入请求参数有误");
        }

        //保存前校验是否重复
        LambdaQueryWrapper<TDxCustomsEntity> queryWrapper = Wrappers.lambdaQuery(TDxCustomsEntity.class)
                .eq(TDxCustomsEntity::getCustomsNo,request.getCustomsNo());
        List<TDxCustomsEntity> entities = tDxCustomsExtDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(entities)){
            return R.fail("已存在海关缴款书号相同的数据,请重新填写");
        }
        TDxCustomsEntity entity = new TDxCustomsEntity();
        //手工录入
        entity.setSourceType(SourceTypeEnum.SOURCE_TYPE_1.getCode().toString());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        //默认未勾选
        entity.setIsCheck(InvoiceCheckEnum.CHECK_2.getCode().toString());
        //默认未入账
        entity.setAccountStatus(AccountStatusEnum.ACCOUNT_00.getCode());
        BeanUtil.copyProperties(request,entity);
        int status = tDxCustomsExtDao.insert(entity);

        log.info("海关缴款书保存状态:{}",status);
        if(status!=-1){
            return R.ok("保存成功");
        }
        return R.fail("保存失败");
    }

    /**
     * @Description 发起勾选请求，撤销请求
     * @Author pengtao
     * @return
    **/
    public R check(CustomsCheckRequest request) {

        List<TDxCustomsEntity> entities = new ArrayList<>();
        //全选时读取查询参数
        if(StringUtils.equals("1",request.getIsAllSelected())){
            if(Objects.isNull(request.getQuery())){
                return R.fail("全选时,查询参数必传");
            }

            //查询数据
            if(StringUtils.equals("1",request.getIsAllSelected())){
                request.getQuery().setPageSize(MAX_PAGE_SIZE);
            }
            PageResult<CustomsDto> list = paged(request.getQuery());
            if(CollectionUtils.isNotEmpty(list.getRows())){
              List<CustomsDto>  customsDtos = list.getRows();
              for(CustomsDto dto:customsDtos){
                  TDxCustomsEntity entity = new TDxCustomsEntity();
                  BeanUtil.copyProperties(dto,entity);
                  entities.add(entity);
              }
            }
        }else{
            entities = tDxCustomsDao.selectBatchIds(request.getIds());
        }


        if(CollectionUtils.isEmpty(entities)){
            return R.fail("未查询到对应的数据");
        }

        if(entities.size()>MAX_PAGE_SIZE){
            return R.fail("勾选数量最多为5000条");
        }

        List<String> isChecks = entities.stream().map(TDxCustomsEntity::getIsCheck).distinct().collect(Collectors.toList());
        //抵扣勾选
        if(StringUtils.equals(InvoiceChecPurposeEnum.PURPOSE_1.getCode().toString(),request.getAuthUse())){
            List<String> invoiceChecks = Arrays.asList(InvoiceCheckEnum.CHECK_0.getCode().toString(),
                    InvoiceCheckEnum.CHECK_3.getCode().toString(), InvoiceCheckEnum.CHECK_4.getCode().toString());
            for(String check:invoiceChecks){
                if(isChecks.contains(check)){
                    return R.fail("当前状态不可操作");
                }
            }
            //撤销勾选
        }else if(StringUtils.equals(InvoiceChecPurposeEnum.PURPOSE_10.getCode().toString(),request.getAuthUse())){
            List<String> invoiceChecks = Arrays.asList(InvoiceCheckEnum.CHECK_0.getCode().toString(),
                    InvoiceCheckEnum.CHECK_2.getCode().toString(), InvoiceCheckEnum.CHECK_3.getCode().toString(),
                    InvoiceCheckEnum.CHECK_5.getCode().toString());
            for(String check:invoiceChecks){
                if(isChecks.contains(check)){
                    return R.fail("当前状态不可操作");
                }
            }
        }

        String msg = "勾选请求成功";
        for(TDxCustomsEntity entity:entities){
            CustomsTaxCheckRequest checkRequest = transCustomsEntity(entity,request.getTaxPeriod(),request.getAuthUse());
            log.info("海关缴款书发起勾选请求:{}",JSON.toJSON(checkRequest));

            //保存请求日志
            this.saveCustomsLog(checkRequest);
            //保存定时任务
            this.saveCustomsTask(checkRequest);
            //更新状态为勾选中，公共服务通过定时任务获取定时任务数据发起勾选请求

            if(StringUtils.equals(request.getAuthUse(),InvoiceChecPurposeEnum.PURPOSE_1.getCode().toString())){
                entity.setIsCheck(InvoiceCheckEnum.CHECK_3.getCode().toString());
            }else if(StringUtils.equals(request.getAuthUse(),InvoiceChecPurposeEnum.PURPOSE_10.getCode().toString())){
                entity.setIsCheck(InvoiceCheckEnum.CHECK_0.getCode().toString());
                msg = "撤销勾选成功";
            }

            tDxCustomsDao.updateById(entity);
        }

        return R.ok(msg);
    }

    /**
     * @Description 生成勾选请求
     * @Author pengtao
     * @return
     **/
    public CustomsTaxCheckRequest transCustomsEntity(TDxCustomsEntity entity,String taxPeriod,String authUse){
        CustomsTaxCheckRequest checkRequest = new CustomsTaxCheckRequest();
        checkRequest.setCustomsId(entity.getId());
        checkRequest.setCustomsPaymentNo(entity.getCustomsNo());
        checkRequest.setDateIssued(entity.getPaperDrewDate());
        checkRequest.setBb("4");
        checkRequest.setTaxNo(entity.getCompanyTaxNo());
        checkRequest.setEffectiveTaxAmount(entity.getEffectiveTaxAmount().toPlainString());
        checkRequest.setTaxPeriod(taxPeriod);
        //1-抵扣勾选 10-撤销抵扣勾选  3-退税勾选 30-退税撤销勾选（确认后无法撤销）
        checkRequest.setAuthUse(authUse);
        //1-模拟勾选成功 2-模拟勾选失败
        checkRequest.setDebug("1");
        return checkRequest;
    }

    /**
     * @Description 生成入账请求
     * @Author pengtao
     * @return
     **/
    public CustomsTaxEntryRequest transCustomsEntry(TDxCustomsEntity entity,String entryStatus){
        CustomsTaxEntryRequest entryRequest = new CustomsTaxEntryRequest();
        entryRequest.setCustomsId(entity.getId());
        entryRequest.setCustomsPaymentNo(entity.getCustomsNo());
        entryRequest.setBb("4");
        entryRequest.setTaxNo(entity.getCompanyTaxNo());
        entryRequest.setEntryStatus(entryStatus);
        entryRequest.setDebug("1");
        return entryRequest;
    }


    public void saveCustomsLog(CustomsTaxCheckRequest checkRequest){
        TDxCustomsLogEntity logEntity = new TDxCustomsLogEntity();
        logEntity.setCustomsId(checkRequest.getCustomsId());
        logEntity.setCustomsNo(checkRequest.getCustomsPaymentNo());
        logEntity.setCheckTime(new Date());
        logEntity.setType(checkRequest.getAuthUse());
        logEntity.setUserId(UserUtil.getUserId());
        logEntity.setUserName(UserUtil.getUserName());
        tDxCustomsLogDao.insert(logEntity);
    }

    public void saveCustomsEntryLog(CustomsTaxEntryRequest request){
        TDxCustomsLogEntity logEntity = new TDxCustomsLogEntity();
        logEntity.setCustomsId(request.getCustomsId());
        logEntity.setCustomsNo(request.getCustomsPaymentNo());
        logEntity.setType(request.getEntryStatus());
        logEntity.setUserId(UserUtil.getUserId());
        logEntity.setUserName(UserUtil.getUserName());
        tDxCustomsLogDao.insert(logEntity);
    }

    /**
     * @Description 生成定时任务
     * @Author pengtao
     * @return
    **/
    public void saveCustomsTask(CustomsTaxCheckRequest checkRequest){
        TDxCustomsTaskEntity taskEntity = new TDxCustomsTaskEntity();
        taskEntity.setCustomsId(checkRequest.getCustomsId());
        taskEntity.setCustomsNo(checkRequest.getCustomsPaymentNo());
        taskEntity.setAuthUse(checkRequest.getAuthUse());
        taskEntity.setBuyerTaxNo(checkRequest.getTaxNo());
        taskEntity.setDateIssued(checkRequest.getDateIssued());
        taskEntity.setEffectiveTaxAmount(checkRequest.getEffectiveTaxAmount());
        taskEntity.setTaxPeriod(checkRequest.getTaxPeriod());
        taskEntity.setSendMsg(JSON.toJSONString(checkRequest));
        taskEntity.setStatus(0);
        taskEntity.setNum(1);
        tDxCustomsTaskDao.insert(taskEntity);
    }


    /**
     * @Description 生成入账定时任务
     * @Author pengtao
     * @return
     **/
    public void saveCustomsEntryTask(CustomsTaxEntryRequest request){
        TDxCustomsTaskEntity taskEntity = new TDxCustomsTaskEntity();
        taskEntity.setCustomsId(request.getCustomsId());
        taskEntity.setCustomsNo(request.getCustomsPaymentNo());
        taskEntity.setAuthUse(request.getEntryStatus());
        taskEntity.setBuyerTaxNo(request.getTaxNo());
        taskEntity.setSendMsg(JSON.toJSONString(request));
        taskEntity.setStatus(0);
        taskEntity.setNum(1);
        tDxCustomsTaskDao.insert(taskEntity);
    }

    /**
     * @Description 发起入账请求
     * @Author pengtao
     * @return
     **/
    public R entry(CustomsEntryRequest request) {
        List<TDxCustomsEntity> entities = new ArrayList<>();
        //全选时读取查询参数
        if(StringUtils.equals("1",request.getIsAllSelected())){
            if(Objects.isNull(request.getQuery())){
                return R.fail("全选时,查询参数必传");
            }

            //查询数据
            if(StringUtils.equals("1",request.getIsAllSelected())){
                request.getQuery().setPageSize(MAX_PAGE_SIZE);
            }
            PageResult<CustomsDto> list = paged(request.getQuery());
            if(CollectionUtils.isNotEmpty(list.getRows())){
                List<CustomsDto>  customsDtos = list.getRows();
                for(CustomsDto dto:customsDtos){
                    TDxCustomsEntity entity = new TDxCustomsEntity();
                    BeanUtil.copyProperties(dto,entity);
                    entities.add(entity);
                }
            }
        }else{
            entities = tDxCustomsDao.selectBatchIds(request.getIds());
        }

        if(CollectionUtils.isEmpty(entities)){
            return R.fail("未查询到对应的数据");
        }

        if(entities.size()>MAX_PAGE_SIZE){
            return R.fail("勾选数量最多为5000条");
        }

        String msg = "入账请求成功";
        for(TDxCustomsEntity entity:entities){
            CustomsTaxEntryRequest entryRequest = this.transCustomsEntry(entity,request.getEntryStatus());
            log.info("海关缴款书发起入账请求:{}",JSON.toJSON(entryRequest));

            //保存请求日志
            this.saveCustomsEntryLog(entryRequest);
            //保存定时任务
//            this.saveCustomsEntryTask(entryRequest);

            //更新状态为入账中，公共服务通过定时任务获取定时任务数据发起入账请求
            if(Arrays.asList(EntryStatusEnum.ACCOUNT_02.getCode(),
                    EntryStatusEnum.ACCOUNT_03.getCode()).contains(request.getEntryStatus())){
                entity.setAccountStatus(AccountStatusEnum.ACCOUNT_01.getCode());
            }else{
                entity.setAccountStatus(AccountStatusEnum.ACCOUNT_05.getCode());
                msg = "撤销入账成功";
            }
            tDxCustomsDao.updateById(entity);
        }
        return R.ok(msg);
    }

    /**
     * 获取需要比对的海关缴款书数据
     * @return
     */
    public List<TDxCustomsEntity> getTaskCustoms() {
        LambdaQueryWrapper<TDxCustomsEntity> query = new LambdaQueryWrapper<>();
        query.eq(TDxCustomsEntity::getAccountStatus, AccountStatusEnum.ACCOUNT_00.getCode());
        query.and(wapper -> wapper.eq(TDxCustomsEntity::getPushBmsStatus, "0").or().isNull(TDxCustomsEntity::getPushBmsStatus));
        return tDxCustomsDao.selectList(query);
    }

    private BigDecimal getDetailTaxAmount(String customsNo) {
        LambdaQueryWrapper<TDxCustomsDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDxCustomsDetailEntity::getCustomsNo, customsNo);
        List<TDxCustomsDetailEntity> detailEntities = tDxCustomsDetailDao.selectList(queryWrapper);
        BigDecimal bmsDetailTaxAmt = new BigDecimal(0);
        for (TDxCustomsDetailEntity detailEntity : detailEntities) {
            BigDecimal taxAmount = detailEntity.getTaxAmount();
            bmsDetailTaxAmt = bmsDetailTaxAmt.add(taxAmount);
        }
        return bmsDetailTaxAmt;
    }

    /**
     * @Description 海关缴款书全选金额统计
     * @Author pengtao
     * @return
    **/
    public CustomsAmountDto getCheckAmount(CustomsQueryDto request) {
        //全选忽略分页
        if(StringUtils.equals("1",request.getIsAllSelected())){
            request.setPageSize(MAX_PAGE_SIZE);
        }
        PageResult<CustomsDto> list = paged(request);
        CustomsAmountDto customsAmountDto = new CustomsAmountDto();
        if(CollectionUtils.isNotEmpty(list.getRows())){
            //税额合计
            BigDecimal taxAmountTotal = list.getRows().stream()
                    .map(item -> BigDecimal.ZERO.add(new BigDecimal(StringUtils.isBlank(item.getTaxAmount())?"0":item.getTaxAmount())))
                            .reduce(BigDecimal.ZERO,(x,y)->x.add(y));
            //有效抵扣税额合计
            BigDecimal effTaxAmountTotal = list.getRows().stream()
                    .map(item -> BigDecimal.ZERO.add(new BigDecimal(StringUtils.isBlank(item.getEffectiveTaxAmount())?"0":item.getEffectiveTaxAmount())))
                    .reduce(BigDecimal.ZERO,(x,y)->x.add(y));
            customsAmountDto.setTaxAmountTotal(taxAmountTotal);
            customsAmountDto.setEffTaxAmountTotal(effTaxAmountTotal);
            customsAmountDto.setCount(list.getRows().size());
        }
        return customsAmountDto;
    }

    /**
     * @Description 入账查询
     * @Author pengtao
     * @return
    **/
    public PageResult<CustomsDto> entryPaged(CustomsQueryDto request) {
        //校验请求参数
        if(Objects.isNull(request)){
            throw new EnhanceRuntimeException("海关缴款书查询参数不允许为空");
        }
        //填发日期校验
        if(StringUtils.isBlank(request.getPaperDrewDateStart())&&StringUtils.isNotBlank(request.getPaperDrewDateEnd())
                ||StringUtils.isNotBlank(request.getPaperDrewDateStart())&&StringUtils.isBlank(request.getPaperDrewDateEnd())){
            throw new EnhanceRuntimeException("填开日期开始和结束时间都需要有值");
        }
        //勾选日期校验
        if(StringUtils.isBlank(request.getCheckTimeStart())&&StringUtils.isNotBlank(request.getCheckTimeEnd())
                ||StringUtils.isNotBlank(request.getCheckTimeStart())&&StringUtils.isBlank(request.getCheckTimeEnd())){
            throw new EnhanceRuntimeException("勾选日期开始和结束时间都需要有值");
        }

        //入账中的勾选状态全选处理
        if(StringUtils.equals("all",request.getIsCheck())){
            request.setIsCheck("");
        } else if(StringUtils.equals(InvoiceCheckEnum.CHECK_4.getValue().toString(),request.getIsCheck())){
            //勾选状态处理
            List<String> isChecks = Arrays.asList(InvoiceCheckEnum.CHECK_4.getValue().toString(),InvoiceCheckEnum.CHECK_6.getValue().toString(),
                    InvoiceCheckEnum.CHECK_8.getValue().toString());

            request.setIsCheck(isChecks.stream().collect(Collectors.joining(",")));
        }

        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);
        //海关缴款书号码处理
        dealCustomsNo(request);
        //日期处理
        dealDate(request);

        log.info("海关缴款书入账查询--处理后的请求参数{}", JSON.toJSON(request));
        //总个数
        int count = this.queryEntryCount(request);
        //获取数据
        List<TDxCustomsEntity> customsEntities = this.queryEntryByPage(request);
        List<CustomsDto> response = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(customsEntities)){
            for(TDxCustomsEntity customsEntitie:customsEntities){
                CustomsDto customsDto = copyEntity(customsEntitie);
                // 查询海关缴款书明细, 获取主信息与明细信息的税额差
                // 设置税额差 税额差是主表的税额减去缴款书明细税额合计差额
                customsDto.setTaxAmountDifference(Objects.nonNull(customsEntitie.getTaxAmountDifference())?
                        customsEntitie.getTaxAmountDifference().toString():null);
                response.add(customsDto);

                //WALMART-3328 勾选失败原因，失败的状态下才展示
                if(Arrays.asList(CheckTypeExportEnum.CHECK_TYPE_5.getCode(),CheckTypeExportEnum.CHECK_TYPE_6.getCode(),
                        CheckTypeExportEnum.CHECK_TYPE__1.getCode()).contains(customsEntitie.getIsCheck())){
                    customsDto.setAuthRemark(customsEntitie.getAuthRemark());
                }else{
                    customsDto.setAuthRemark("");
                }
            }
        }
        return PageResult.of(response,count,pageNo, request.getPageSize());
    }

    /**
     * @Description 入账查询数量
     * @Author pengtao
     * @return
    **/
    public int queryEntryCount(CustomsQueryDto request) {
        return  tDxCustomsExtDao.countEntryCustoms(request.getCustomsNo(),request.getManageStatus(),
                request.getCompanyTaxNo(),request.getCompanyName(),request.getIsCheck(),
                request.getPaperDrewDateStart(),request.getPaperDrewDateEnd(),
                request.getCheckTimeStart(),request.getCheckTimeEnd(),request.getTaxPeriod(),
                request.getVoucherNo(),request.getAccountStatus(),request.getConfirmStatus(),
                request.getBillStatus(),request.getUnCheckTimeStart(),request.getUnCheckTimeEnd(),
                request.getContractNo(),request.getCustomsDocNo(),
                request.getVoucherAccountTimeStart(), request.getVoucherAccountTimeEnd()
        );
    }

    /**
     * @Description 入账查询数据
     * @Author pengtao
     * @return
    **/
    public List<TDxCustomsEntity> queryEntryByPage(CustomsQueryDto request) {
        return  tDxCustomsExtDao.queryEntryPageCustoms(request.getPageNo(),request.getPageSize(),request.getCustomsNo(),
                request.getManageStatus(), request.getCompanyTaxNo(),request.getCompanyName(),request.getIsCheck(),
                request.getPaperDrewDateStart(),request.getPaperDrewDateEnd(),
                request.getCheckTimeStart(),request.getCheckTimeEnd(),request.getTaxPeriod(),
                request.getVoucherNo(),request.getAccountStatus(), request.getConfirmStatus(),
                request.getBillStatus(),request.getUnCheckTimeStart(),request.getUnCheckTimeEnd(),
                request.getContractNo(),request.getCustomsDocNo(),
                request.getVoucherAccountTimeStart(), request.getVoucherAccountTimeEnd());
    }
}
