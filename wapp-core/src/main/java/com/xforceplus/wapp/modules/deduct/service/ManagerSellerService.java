package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.claim.dto.ManagerSellerRequest;
import com.xforceplus.wapp.modules.deduct.vo.ManagerSellerVO;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.ManagerSellerDeductDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.ManagerSellerSettingEntity;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.enums.OperateLogEnum.LOCK_SELLER_AGREEMENT;
import static com.xforceplus.wapp.enums.OperateLogEnum.UNLOCK_SELLER_AGREEMENT;
import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
@RequiredArgsConstructor
public class ManagerSellerService extends ServiceImpl<ManagerSellerDeductDao, ManagerSellerSettingEntity> {
    private final TXfBillDeductExtDao tXfBillDeductExtDao;
    private final OperateLogService operateLogService;
    private final FtpUtilService ftpUtilService;
    private final ExportCommonService exportCommonService;
    private final ExcelExportLogService excelExportLogService;
    @Value("${wapp.export.tmp}")
    private String tmp;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    public Page<ManagerSellerSettingEntity> getPage(ManagerSellerRequest request) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .like(StringUtils.isNoneBlank(request.getParams().getSellerName()),
                        ManagerSellerSettingEntity::getSellerName, request.getParams().getSellerName())
                .like(StringUtils.isNoneBlank(request.getParams().getSellerNo()),
                        ManagerSellerSettingEntity::getSellerNo, request.getParams().getSellerNo())
                .eq(StringUtils.isNoneBlank(request.getParams().getLockFlag()),
                        ManagerSellerSettingEntity::getLockFlag, request.getParams().getLockFlag())
                .page(new Page<>(request.getPageNum(), request.getPageSize()));

    }

    /**
     * 批量锁定/ 解锁
     *
     * @param ids
     * @param lockFlag
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchLock(List<Long> ids, Integer lockFlag, String user) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        new LambdaUpdateChainWrapper<>(getBaseMapper())
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .in(ManagerSellerSettingEntity::getId, ids)
                .set(ManagerSellerSettingEntity::getLockFlag, lockFlag)
                .set(ManagerSellerSettingEntity::getUpdateUser, user)
                .update();
        List<String> sellerNos = getSellerNos(ids);
        updateDeduct(sellerNos, lockFlag);
    }

    /**
     * 删除
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAndUnlock(List<Long> ids, String user) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<String> sellerNos = getSellerNos(ids);
        updateDeduct(sellerNos, TXfDeductStatusEnum.UNLOCK.getCode());
        new LambdaUpdateChainWrapper<>(getBaseMapper())
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .in(ManagerSellerSettingEntity::getId, ids)
                .set(ManagerSellerSettingEntity::getDeleteFlag, System.currentTimeMillis())
                .set(ManagerSellerSettingEntity::getUpdateUser, user)
                .update();
    }

    /**
     * 批量新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void bachInsertAndLock(@NonNull List<ManagerSellerVO> list, String user) {
        Set<String> sellerNos = list.stream().map(ManagerSellerVO::getSellerNo).collect(Collectors.toSet());
        Map<String/*sellerNo*/, ManagerSellerSettingEntity> existSellerNos = getExistTaxNos(sellerNos);
        Set<ManagerSellerSettingEntity> collect = list.stream()
                .filter(it -> StringUtils.isNotBlank(it.getSellerName()) && StringUtils.isNotBlank(it.getSellerNo()))
                .map(it -> Optional.ofNullable(existSellerNos.get(it.getSellerNo()))
                        .map(t -> existUpdate(user, it, t))
                        .orElseGet(() -> notExistCreate(user, it))
                ).collect(Collectors.toSet());
        saveOrUpdateBatch(collect);
        updateDeduct(sellerNos, TXfDeductStatusEnum.LOCK.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void importData(@NonNull List<ManagerSellerVO> list) {
        bachInsertAndLock(list, UserUtil.getUserName());
    }

    /**
     * 税号查询供应商是否加锁
     *
     * @return
     */
    public boolean findSellerNoLocked(String sellerNo) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .eq(ManagerSellerSettingEntity::getLockFlag, TXfDeductStatusEnum.LOCK.getCode())
                .eq(ManagerSellerSettingEntity::getSellerNo, sellerNo)
                .count() > 0;

    }

    public void importFail(List<ManagerSellerVO> list) {
        String exportFileName = "供应商锁定导入失败原因" + System.currentTimeMillis() + ExcelExportUtil.FILE_NAME_SUFFIX;
        File tmpFile = FileUtils.getFile(tmp);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        File sourceFile = FileUtils.getFile(tmp, exportFileName);
        EasyExcel.write(tmp + "/" + exportFileName, ManagerSellerVO.class).sheet("sheet1").doWrite(list);

        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        try {
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
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
        excelExportlogEntity.setConditions("供应商锁定");
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);
        excelExportlogEntity.setFilepath(ftpPath + "/" + exportFileName);
        excelExportLogService.save(excelExportlogEntity);
        exportDto.setLogId(excelExportlogEntity.getId());
        exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "供应商锁定导入错误信息", exportCommonService.getSuccContent());

    }

    public boolean checkImport(ManagerSellerVO vo) {
        if (StringUtils.isBlank(vo.getSellerNo())) {
            vo.setErrorMessage("供应商编号 不能为空！");
            return false;
        }
        if (StringUtils.isBlank(vo.getSellerName())) {
            vo.setErrorMessage("供应商名称 不能为空！");
            return false;
        }
        return true;
    }

    private ManagerSellerSettingEntity notExistCreate(String user, ManagerSellerVO it) {
        ManagerSellerSettingEntity entity = new ManagerSellerSettingEntity();
        entity.setLockFlag("1");
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        entity.setUpdateUser(user);
        entity.setCreateUser(user);
        entity.setSellerName(it.getSellerName());
        entity.setSellerNo(it.getSellerNo());
        return entity;
    }

    private ManagerSellerSettingEntity existUpdate(String user, ManagerSellerVO it, ManagerSellerSettingEntity t) {
        t.setLockFlag("1");
        t.setUpdateTime(new Date());
        t.setUpdateUser(user);
        t.setSellerName(it.getSellerName());
        return t;
    }


    /**
     * 同步更新待匹配的协议 加锁解锁
     *
     * @param sellerNos
     * @param lockFlag
     */
    private void updateDeduct(Collection<String> sellerNos, Integer lockFlag) {
        if (CollectionUtils.isEmpty(sellerNos)) {
            return;
        }
        new LambdaUpdateChainWrapper<>(tXfBillDeductExtDao)
                .set(TXfBillDeductEntity::getLockFlag, lockFlag)
                .set(TXfBillDeductEntity::getUpdateTime, new Date())
                .in(TXfBillDeductEntity::getSellerNo, sellerNos)
                .eq(TXfBillDeductEntity::getBusinessType, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue())
                .eq(TXfBillDeductEntity::getStatus, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode())
                .update();
        List<TXfBillDeductEntity> list = new LambdaQueryChainWrapper<>(tXfBillDeductExtDao)
                .select(TXfBillDeductEntity::getId, TXfBillDeductEntity::getBusinessType, TXfBillDeductEntity::getStatus)
                .in(TXfBillDeductEntity::getSellerNo, sellerNos)
                .eq(TXfBillDeductEntity::getBusinessType, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue())
                .eq(TXfBillDeductEntity::getStatus, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode())
                .list();
        list.forEach(it -> {
            OperateLogEnum logEnum;
            if ("1".equals(lockFlag.toString())) {
                logEnum = LOCK_SELLER_AGREEMENT;
            } else {
                logEnum = UNLOCK_SELLER_AGREEMENT;
            }
            operateLogService.addDeductLog(it.getId(), it.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(it.getStatus()), "", logEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
        });
    }

    private List<String> getSellerNos(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        List<ManagerSellerSettingEntity> list = new LambdaQueryChainWrapper<>(getBaseMapper())
                .select(ManagerSellerSettingEntity::getSellerNo)
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .in(ManagerSellerSettingEntity::getId, ids)
                .list();
        return list.stream().map(ManagerSellerSettingEntity::getSellerNo).collect(Collectors.toList());
    }


    private Map<String/*sellerNo*/, ManagerSellerSettingEntity> getExistTaxNos(Collection<String> taxNos) {
        if (CollectionUtils.isEmpty(taxNos)) {
            return Maps.newHashMap();
        }
        List<ManagerSellerSettingEntity> list = new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(ManagerSellerSettingEntity::getDeleteFlag)
                .in(ManagerSellerSettingEntity::getSellerNo, taxNos)
                .list();
        return list.stream().collect(Collectors.toMap(ManagerSellerSettingEntity::getSellerNo, Function.identity()));
    }
}
