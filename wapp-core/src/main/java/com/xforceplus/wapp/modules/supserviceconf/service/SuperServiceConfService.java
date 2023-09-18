package com.xforceplus.wapp.modules.supserviceconf.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.SuperServiceTypeEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportSizeDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.supserviceconf.dto.*;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TAcUserDao;
import com.xforceplus.wapp.repository.dao.TAcUserExtDao;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 供应商服务配置
 */
@Service
@Slf4j
public class SuperServiceConfService extends ServiceImpl<TAcUserDao, TAcUserEntity> {

    @Autowired
    private TAcUserExtDao tAcUserExtDao;
    @Autowired
    private TAcUserDao tAcUserDao;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Value("${wapp.export.tmp}")
    private String tmp;

    /**
     * @Description 分页查询
     * @Author pengtao
     * @return
     **/
    public PageResult<SuperServiceConfDto> paged(SuperServiceConfQueryDto request) throws Exception {
        //校验请求参数
        if(Objects.isNull(request)){
            throw new EnhanceRuntimeException("供应商服务配置查询参数不允许为空");
        }

        if(StringUtils.isBlank(request.getAssertDateStart())&&StringUtils.isNotBlank(request.getAssertDateEnd())
        ||StringUtils.isNotBlank(request.getAssertDateStart())&&StringUtils.isBlank(request.getAssertDateEnd())){
            throw new EnhanceRuntimeException("生效时间开始和结束时间都需要有值");
        }

        if(StringUtils.isBlank(request.getExpireDateStart())&&StringUtils.isNotBlank(request.getExpireDateEnd())
                ||StringUtils.isNotBlank(request.getExpireDateStart())&&StringUtils.isBlank(request.getExpireDateEnd())){
            throw new EnhanceRuntimeException("失效时间开始和结束时间都需要有值");
        }

        if(StringUtils.isBlank(request.getUpdateDateStart())&&StringUtils.isNotBlank(request.getUpdateDateEnd())
                ||StringUtils.isNotBlank(request.getUpdateDateStart())&&StringUtils.isBlank(request.getUpdateDateEnd())){
            throw new EnhanceRuntimeException("更新时间开始和结束时间都需要有值");
        }

        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);
        //供应商号处理
        dealUserCode(request);

        //日期格式处理
        dateDeal(request);

        log.info("供应商服务配置查询--处理后的请求参数{}", JSON.toJSON(request));
        //总个数
        int count = queryCount(request);
        //获取数据
        List<TAcUserEntity> userEntities = queryByPage(request);
        List<SuperServiceConfDto> response = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userEntities)){
            for(TAcUserEntity userEntity:userEntities){
                response.add(copyEntity(userEntity));
            }
        }
        return PageResult.of(response,count,pageNo, request.getPageSize());
    }

    /**
     * @Description 根据供应商号查询数据
     * @Author pengtao
     * @return
    **/
    public List<TAcUserEntity> queryByUserCodes(List<String> request){
        QueryWrapper<TAcUserEntity> wrapper = new QueryWrapper<>();
        wrapper.in(CollectionUtils.isNotEmpty(request),TAcUserEntity.USERCODE,request);
        return tAcUserDao.selectList(wrapper);
    }

    /**
     * @Description 查询个数
     * @Author pengtao
     * @return
     **/
    public Integer count(SuperServiceConfQueryDto request) {
        QueryWrapper<TAcUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request.getUserCode()),TAcUserEntity.USERCODE,request.getUserCode());
        wrapper.eq(StringUtils.isNotEmpty(request.getAssertDateStart()),TAcUserEntity.ASSERTDATE,request.getAssertDateStart());
        wrapper.eq(StringUtils.isNotEmpty(request.getExpireDateStart()),TAcUserEntity.EXPIREDATE,request.getExpireDateStart());
        wrapper.eq(Objects.nonNull(request.getServiceType()),TAcUserEntity.SERVICETYPE,request.getServiceType());
        return this.count(wrapper);
    }

    /**
     * @Description 查询数量
     * @Author pengtao
     * @return
    **/
    public Integer queryCount(SuperServiceConfQueryDto request){
        return  tAcUserExtDao.countSupSerConf(request.getUserCode(),
                request.getUserName(),request.getTaxNo(),request.getServiceType(),request.getAssertDateStart(),
                request.getExpireDateStart(),request.getUpdateDateStart(),request.getAssertDateEnd(),
                request.getExpireDateEnd(),request.getUpdateDateEnd(),request.getUserId());
    }

    /**
     * @Description 查询数据
     * @Author pengtao
     * @return
     **/
    public List<TAcUserEntity> queryByPage(SuperServiceConfQueryDto request){
        //获取数据
        return  tAcUserExtDao.queryPageSupSerConf(request.getPageNo(),request.getPageSize(),request.getUserCode(),
                request.getUserName(),request.getTaxNo(),request.getServiceType(),request.getAssertDateStart(),
                request.getExpireDateStart(),request.getUpdateDateStart(),request.getAssertDateEnd(),
                request.getExpireDateEnd(),request.getUpdateDateEnd(),request.getUserId());
    }
    /**
     * @Description 实体转换
     * @Author pengtao
     * @return
     **/
    public SuperServiceConfDto copyEntity(TAcUserEntity userEntitiey){
        SuperServiceConfDto superServiceConfDto = new SuperServiceConfDto();
        superServiceConfDto.setUserId(userEntitiey.getUserid());
        superServiceConfDto.setOrgId(userEntitiey.getOrgid());
        superServiceConfDto.setTaxNo(userEntitiey.getExtf0());
        superServiceConfDto.setDiscountRate(userEntitiey.getExtf1());
        superServiceConfDto.setServiceType(userEntitiey.getServiceType());
        superServiceConfDto.setUserCode(userEntitiey.getUsercode());
        superServiceConfDto.setUserName(userEntitiey.getUsername());
        superServiceConfDto.setAssertDate(userEntitiey.getAssertDate());
        superServiceConfDto.setExpireDate(userEntitiey.getExpireDate());
        superServiceConfDto.setUpdateDate(userEntitiey.getUpdateDate());
        return superServiceConfDto;
    }

    /**
     * @Description 请求日期处理
     * @param
     * @Date
     * @Author pengtao
     * @return
     **/
    public void dateDeal(SuperServiceConfQueryDto request){
        //生效日期，失效日期，更新日期
        if (StringUtils.isNotBlank(request.getAssertDate())) {
            //excel导入更新处理
            if(request.getAssertDate().contains("/")){
                request.setAssertDate(request.getAssertDate().replaceAll("/","-"));
            }
            String format = DateUtils.addDayToYYYYMMDD(request.getAssertDate(), 0);
            request.setAssertDate(format);
        }

        if (StringUtils.isNotBlank(request.getExpireDate())) {
            if(request.getExpireDate().contains("/")){
                request.setAssertDate(request.getExpireDate().replaceAll("/","-"));
            }
            String format = DateUtils.addDayToYYYYMMDD(request.getExpireDateStart(), 0);
            request.setExpireDateStart(format);
        }

        if (StringUtils.isNotBlank(request.getAssertDateStart())) {
            //excel导入更新处理
            if(request.getAssertDateStart().contains("/")){
                request.setAssertDateStart(request.getAssertDateStart().replaceAll("/","-"));
            }
            String format = DateUtils.addDayToYYYYMMDD(request.getAssertDateStart(), 0);
            request.setAssertDateStart(format);
        }

        if (StringUtils.isNotBlank(request.getExpireDateStart())) {
            if(request.getExpireDateStart().contains("/")){
                request.setAssertDateStart(request.getExpireDateStart().replaceAll("/","-"));
            }
            String format = DateUtils.addDayToYYYYMMDD(request.getExpireDateStart(), 0);
            request.setExpireDateStart(format);
        }

        //更新时间
        if(StringUtils.isNotBlank(request.getUpdateDateStart())){
            String format = DateUtils.addDayToYYYYMMDD(request.getUpdateDateStart(), 0);
            request.setUpdateDateStart(format);
        }

        //各个查询时间结束日期处理
        if(StringUtils.isNotBlank(request.getAssertDateEnd())){
            String format = DateUtils.addDayToYYYYMMDD(request.getAssertDateEnd(), 0);
            request.setAssertDateEnd(format);
        }

        if(StringUtils.isNotBlank(request.getExpireDateEnd())){
            String format = DateUtils.addDayToYYYYMMDD(request.getExpireDateEnd(), 0);
            request.setExpireDateEnd(format);
        }

        if(StringUtils.isNotBlank(request.getUpdateDateEnd())){
            String format = DateUtils.addDayToYYYYMMDD(request.getUpdateDateEnd(), 0);
            request.setUpdateDateEnd(format);
        }
    }

    /**
     * @Description 供应商号查询处理
     * @Author pengtao
     * @return
    **/
    public void dealUserCode(SuperServiceConfQueryDto request) {
        //供应商号处理
        if(StringUtils.isNotBlank(request.getUserCode())){
            if(countUserCode(request.getUserCode())>9){
                throw new EnhanceRuntimeException("供应商号查询数量最大为10个");
            }
            String[] itemNoSplit = request.getUserCode().split(",");
            StringBuffer strBuf = new StringBuffer("");
            for(String str:itemNoSplit){
                if(!str.startsWith("'")&&!str.endsWith("'")){
                    strBuf.append("'").append(str.trim()).append("',");
                }
            }
            if(strBuf.length()>1){
                strBuf.deleteCharAt(strBuf.length()-1);
            }
            request.setUserCode(strBuf.toString());
        }
    }

    /**
     * @Description userCode限定10个
     * @Author pengtao
     * @return
    **/
    public int countUserCode(String userCode){
        int cnt = 0;
        if(userCode.contains(",")){
            cnt = userCode.length()-userCode.replaceAll(",","").length();
        }
        return cnt;
    }

    public String dealUserCodeStr(List<String> request){
        //供应商号处理
        String strUserCode = "";
        if(CollectionUtils.isNotEmpty(request)){
            StringBuffer strBuf = new StringBuffer("");
            for(String str:request){
                if(!str.startsWith("'")&&!str.endsWith("'")){
                    strBuf.append("'").append(str.trim()).append("',");
                }
            }

            if(strBuf.length()>1){
                strUserCode = strBuf.deleteCharAt(strBuf.length()-1).toString();
            }
        }
        return strUserCode;
    }

    /**
     * @Description 更新供应商配置
     * @param
     * @Date
     * @Author pengtao
     * @return
    **/
    public R update(SuperServiceConfQueryDto request) {
        //校验请求参数
        if(Objects.isNull(request)){
            return R.fail("供应商服务配置更新请求参数有误");
        }
        if(StringUtils.isBlank(request.getUserCode())){
            return R.fail("供应商号不允许为空");
        }

        if(Objects.isNull(request.getServiceType())){
            return R.fail("服务类型不允许为空");
        }

        if(request.getServiceType()==1){
            if(StringUtils.isBlank(request.getAssertDate())){
                return R.fail("生效时间不允许为空");
            }
            if(StringUtils.isBlank(request.getExpireDate())){
                return R.fail("失效时间不允许为空");
            }
        }

        //日期格式处理
        dateDeal(request);
        request.setUpdateDateStart(DateUtils.getStringDateShort());
        log.info("供应商服务配置更新参数:{}",JSON.toJSON(request));
        int status = tAcUserExtDao.updateSupSerConf(request.getUserCode(),request.getUserName(),request.getServiceType(),request.getAssertDate(),request.getExpireDate(),request.getUpdateDateStart());
        log.info("供应商服务配置更新个数:{}",status);
        if(status!=-1){
            return R.ok("更新成功");
        }
        return R.fail("更新失败");
    }

    /**
     * @Description 校验请求参数
     * @Author pengtao
     * @return
    **/
    public Boolean checkRequest(SuperServiceConfQueryDto request){
        if(Objects.isNull(request)){
            return Boolean.TRUE;
        }

        if(Objects.isNull(request.getUserCode())){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    /**
     * @Description 导入更新
     * @param  
     * @Date   
     * @Author pengtao
     * @return 
    **/
    public R importFile(MultipartFile file) throws Exception {
        SuperSerConfImportListener listener = new SuperSerConfImportListener();
        int update = 0;
        try {
            EasyExcel.read(file.getInputStream(), SuperServiceConfImportDto.class, listener).sheet().doRead();
            if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
                return R.fail("未解析到数据");
            }

            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
                for (int i = 0; i < listener.getInvalidInvoices().size(); i++) {
                    builder.append("供应商号:" + listener.getInvalidInvoices().get(i).getUserCode() );
                    builder.append(listener.getInvalidInvoices().get(i).getErrorMsg());
                }
                return R.fail(builder.toString());
            }

            log.info("导入更新供应商配置数据:{}",JSON.toJSON(listener.getValidInvoices()));
            List<String> serviceTypes = listener.getValidInvoices().stream().map(SuperServiceConfImportDto::getServiceType).distinct().collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(serviceTypes)&&serviceTypes.contains(-1)){
                return R.fail("服务类型转换失败，请检查参数后重试!");
            }
            for(SuperServiceConfImportDto dto:listener.getValidInvoices()){
                SuperServiceConfQueryDto request = new SuperServiceConfQueryDto();
                String userCode = dto.getUserCode();
                request.setUserCode(userCode);
                dealUserCode(request);
                request.setPageNo(0);
                List<TAcUserEntity> tAcUserEntitys = queryByPage(request);
                if(CollectionUtils.isNotEmpty(tAcUserEntitys)){
                    request.setAssertDateStart(dto.getAssertDate());
                    request.setExpireDateStart(dto.getExpireDate());
                    //日期格式处理
                    dateDeal(request);
                    request.setUserCode(userCode);
                    request.setUpdateDateStart(DateUtils.getStringDateShort());
                    request.setServiceType(SuperServiceTypeEnum.getValue(dto.getServiceType()));
                    int status = tAcUserExtDao.updateSupSerConf(request.getUserCode(),request.getUserName(),request.getServiceType(),request.getAssertDateStart(),request.getExpireDateStart(),request.getUpdateDateStart());
                    update+=status;
                }
            }
        } catch (IOException e) {
            log.error("读取excel异常:{}", e);
            return R.fail("读取excel异常");
        }

        return R.ok("message", "导入成功 " + update + " 条数据");
    }

    /**
     * @Description 供应商服务导入变更，异常提示导出excel
     * @Author pengtao
     * @return
     **/
    public SpecialCompanyImportSizeDto uploadImportData(MultipartFile file) throws Exception {
        SpecialCompanyImportSizeDto sizeDto = new SpecialCompanyImportSizeDto();
        SuperSerConfImportListener listener = new SuperSerConfImportListener();
        EasyExcel.read(file.getInputStream(), SuperServiceConfImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            sizeDto.setErrorMsg("未解析到数据");
            return sizeDto;
        }
        log.info("导入更新供应商配置数据:{}",JSON.toJSON(listener.getValidInvoices()));
        List<SuperServiceConfQueryDto> updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
            //过滤出6d+服务类型去重后的数据
            List<SuperServiceConfImportDto> superServiceConfList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getUserCode() + f.getServiceType()))), ArrayList::new)
            );

            for (SuperServiceConfImportDto dto : superServiceConfList) {
                SuperServiceConfQueryDto request = new SuperServiceConfQueryDto();
                String userCode = dto.getUserCode();
                request.setUserCode(userCode);
                dealUserCode(request);
                request.setPageNo(0);
                List<TAcUserEntity> tAcUserEntitys = queryByPage(request);
                if (CollectionUtils.isEmpty(tAcUserEntitys)) {
                    dto.setErrorMsg("未找到对应的供应商服务配置数据");
                    listener.getInvalidInvoices().add(dto);
                    continue;
                }
                if (StringUtils.isNotBlank(dto.getServiceType())) {
                    if (!Arrays.asList(SuperServiceTypeEnum.NORMAL.getDesc(),SuperServiceTypeEnum.VIP.getDesc()).contains(dto.getServiceType())) {
                        dto.setErrorMsg("服务类型存在错误");
                        listener.getInvalidInvoices().add(dto);
                        continue;
                    }

                    if (SuperServiceTypeEnum.VIP.getDesc().equals(dto.getServiceType()) && StringUtils.isAnyBlank(dto.getAssertDate(), dto.getExpireDate())) {
                            dto.setErrorMsg("服务类型为VIP时生效日期和失效日期不允许为空");
                        listener.getInvalidInvoices().add(dto);
                        continue;
                    }
                }

                request.setAssertDateStart(dto.getAssertDate());
                request.setExpireDateStart(dto.getExpireDate());
                //日期格式处理
                dateDeal(request);
                request.setUserCode(userCode);
                request.setUpdateDateStart(DateUtils.getStringDateShort());
                request.setServiceType(SuperServiceTypeEnum.getValue(dto.getServiceType()));
                int status = tAcUserExtDao.updateSupSerConf(request.getUserCode(), request.getUserName(), request.getServiceType(), request.getAssertDateStart(), request.getExpireDateStart(), request.getUpdateDateStart());
                log.info("供应商服务配置导入更新条数:{},供应商号:{}", status, request.getUserCode());
                updateList.add(request);
            }
        }

        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = FileUtils.getFile(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = FileUtils.getFile(tmp, file.getOriginalFilename());
            EasyExcel.write(tmp + "/" + file.getOriginalFilename(), SuperServiceConfImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

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
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "供应商服务配置上传导入错误信息", exportCommonService.getSuccContent());
        }
        sizeDto.setImportCount(listener.getRows());
        sizeDto.setValidCDount(updateList.size());
        sizeDto.setUnValidCount(listener.getInvalidInvoices().size());
        return sizeDto;
    }

    public R export(List<TAcUserEntity> resultList, SupSerConfValidSubmitRequest request) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "供应商服务配置数据导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        FileInputStream inputStream = null;
        try {
            //创建一个sheet
            File file = FileUtils.getFile(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            List<SuperServiceConfExportDto> exportDtos = new ArrayList<>();
            for(TAcUserEntity tAcUserEntity:resultList){
                SuperServiceConfExportDto dto = new SuperServiceConfExportDto();
                dto.setUserCode(tAcUserEntity.getUsercode());
                dto.setUserName(tAcUserEntity.getUsername());
                if(StringUtils.isNotBlank(tAcUserEntity.getAssertDate())){
                    dto.setAssertDate(tAcUserEntity.getAssertDate().substring(0,tAcUserEntity.getAssertDate().indexOf(" ")));
                }
                if(StringUtils.isNotBlank(tAcUserEntity.getExpireDate())){
                    dto.setExpireDate(tAcUserEntity.getExpireDate().substring(0,tAcUserEntity.getExpireDate().indexOf(" ")));
                }
                if(StringUtils.isNotBlank(tAcUserEntity.getUpdateDate())){
                    dto.setUpdateDate(tAcUserEntity.getUpdateDate().substring(0,tAcUserEntity.getUpdateDate().indexOf(" ")));
                }
                //服务类型获取中文
                if(Objects.nonNull(tAcUserEntity.getServiceType())){
                    dto.setServiceType(SuperServiceTypeEnum.fromValue(tAcUserEntity.getServiceType()).getDesc());
                }

                exportDtos.add(dto);
            }
            File excl = FileUtils.getFile(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, SuperServiceConfExportDto.class)
                    .sheet("sheet1").doWrite(exportDtos);
            //推送sftp
            inputStream = FileUtils.openInputStream(excl);
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "供应商服务配置数据导出成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return R.ok();
    }
}
