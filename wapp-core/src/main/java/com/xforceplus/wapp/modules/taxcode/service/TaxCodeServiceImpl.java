package com.xforceplus.wapp.modules.taxcode.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.client.JanusClient;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.billdeduct.service.BillDeductItemServiceImpl;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementItemServiceImpl;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.converters.*;
import com.xforceplus.wapp.modules.taxcode.dto.*;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.models.TaxCodeLog;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeAuditServiceImpl;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeRiversandServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfTaxCodeRiversandExtDao;
import com.xforceplus.wapp.repository.dao.TaxCodeDao;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeAuditEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.util.ExcelExportUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaxCodeServiceImpl extends ServiceImpl<TaxCodeDao, TaxCodeEntity> {
    private final TaxCodeConverter taxCodeConverter;
    private final TaxCodeAuditServiceImpl taxCodeAuditService;
    private final JanusClient janusClient;
    private final SettlementService settlementService;
    private final CommonMessageService commonMessageService;
    private final BillDeductItemServiceImpl billDeductItemService;
    @Lazy
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Lazy
    @Autowired
    private SettlementItemServiceImpl settlementItemService;
    private final RedisTemplate<String, String> redisTemplate;
    @Autowired
    private TXfTaxCodeRiversandExtDao tXfTaxCodeRiversandExtDao;

    @Autowired
    private TaxCodeRiversandServiceImpl taxCodeRiversandService;

    @Autowired
    private ExcelExportUtils excelExportUtils;

    public Tuple2<List<TaxCode>, Page<TaxCodeEntity>> page(Long current, Long size, String goodsTaxNo, String itemName, String itemNo, String medianCategoryCode) {
        log.debug("税编分页查询,入参,goodsTaxNo:{},itemName:{},itemNo:{},medianCategoryCode:{},分页数据,current:{},size:{}",
                goodsTaxNo, itemName, itemNo, medianCategoryCode, current, size);
        LambdaQueryChainWrapper<TaxCodeEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag);
        if (StringUtils.isNotBlank(goodsTaxNo)) {
            wrapper.eq(TaxCodeEntity::getGoodsTaxNo, goodsTaxNo);
        }
        if (StringUtils.isNotBlank(itemName)) {
            if (itemName.length() < 2) {
                throw new EnhanceRuntimeException("商品名称至少输入开头两位字符");
            }
            wrapper.like(TaxCodeEntity::getItemName, itemName + "%");
        }
        if (StringUtils.isNotBlank(itemNo)) {
            wrapper.eq(TaxCodeEntity::getItemNo, itemNo);
        }
        if (StringUtils.isNotBlank(medianCategoryCode)) {
            wrapper.eq(TaxCodeEntity::getMedianCategoryCode, medianCategoryCode);
        }
        Page<TaxCodeEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("税编分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(taxCodeConverter.map(page.getRecords()), page);
    }

    public Optional<TaxCodeDto> getTaxCodeByItemNo(@NonNull String itemNo) {
        log.debug("通过itemNo[{}]查询税编", itemNo);
        Function<TaxCodeEntity, String> getTaxShortName = (it) -> searchTaxCode(it.getGoodsTaxNo(), null)
                .map(m -> m.get(0).getTaxShortName())
                .getOrNull();
        Function<String, TaxCodeDto> findTaxCode = (no) -> new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag)
                .eq(TaxCodeEntity::getItemNo, no)
                .oneOpt().map(it -> taxCodeConverter.map(it, getTaxShortName.apply(it)))
                .orElse(null);
        TaxCodeDto taxCode = findTaxCode.apply(itemNo);
//        if (Objects.isNull(taxCode)) {
//            try {
//                taxCode = wappDb2Client.getItemNo(itemNo).map(findTaxCode).orElse(null);
//            } catch (Exception e) {
//                log.error("查询税编异常，itemNo：{}。", itemNo, e);
//            }
//        }
        log.info("通过itemNo[{}]查询税编,结果:{}", itemNo, taxCode);
        return Optional.ofNullable(taxCode);
    }

    /**
     * <pre>
     * .税转编和税编大类不能同时为空
     * .优先从redis中获取税编信息（redis缓存时间为2小时）
     * .redis中不存在，将从税编中台获取信息
     * </pre>
     *
     * @param taxCode 税收编码
     * @param keyWord 搜搜关键字
     * @return 成功集合/失败原因
     */
    public Either<String, List<TaxCodeBean>> searchTaxCode(@Nullable String taxCode, @Nullable String keyWord) {
        String key = String.format("taxCode:%s-keyWord:%s", Objects.toString(taxCode, StringUtils.EMPTY), Objects.toString(keyWord, StringUtils.EMPTY));
        String cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            log.info("searchTaxCode cache:{}", cache);
            List<TaxCodeBean> taxCodeBeans = JsonUtil.fromJsonList(cache, TaxCodeBean.class);
            return Either.right(taxCodeBeans);
        } else {
            Either<String, List<TaxCodeBean>> result = janusClient.searchTaxCode(taxCode, keyWord);
            if (result.isRight()) {
                redisTemplate.opsForValue().set(key, JsonUtil.toJsonStr(result.get()), 2, TimeUnit.HOURS);
            }
            return result;
        }
    }

    /**
     * <pre>
     * 1：根据税率，税编，税收大类查询税编信息
     * .如果税率为空，不对税编进行校验，如果传入税编，将过滤税率
     * </pre>
     *
     * @param taxRate
     * @param taxCode
     * @param keyWord
     * @return
     */
    public Either<String, List<TaxCodeBean>> searchTaxCode(@Nullable String taxRate, @Nullable String taxCode, @Nullable String keyWord) {
        Either<String, List<TaxCodeBean>> either = searchTaxCode(taxCode, keyWord);
        if (StringUtils.isBlank(taxRate)) {
            return either;
        }
        return either.map(it -> it.stream().filter(tc -> {
            tc.setTaxRate(taxRate);
            return tc.getTaxRateList().contains(taxRate);
        }).collect(Collectors.toList()));
    }

    /**
     * 根据税编简称及关键词查询税编列表
     *
     * @param taxRate
     * @param shortName
     * @param keyWord
     * @return
     */
    public Either<String, List<TaxCodeBean>> taxCodeQuery(String taxRate, String shortName, String keyWord) {
        Either<String, List<TaxCodeBean>> either = searchTaxCode(null, shortName);
        return either.map(it -> it.stream().filter(tc -> {
            //按税率过滤
            if (StringUtils.isNotBlank(taxRate)) {
                tc.setTaxRate(taxRate);
                if (!tc.getTaxRateList().contains(taxRate)) {
                    return false;
                }
            }
            //按关键词过滤
            if (StringUtils.isNotBlank(keyWord)) {
                if (!tc.getTaxCode().contains(keyWord) && !tc.getTaxName().contains(keyWord)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaxCode(TaxCode taxCode, UserEntity user, boolean isSeller) {
        log.info("登录人信息：{}", user);

        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag)
                .eq(TaxCodeEntity::getId, taxCode.getId())
                .oneOpt()
                .map(it -> {
                    TaxCodeEntity map = taxCodeConverter.map(taxCode);
                    map.setId(it.getId());
                    map.setUpdateTime(new Date());
                    map.setUpdateUser(Long.valueOf(user.getUserid()));

                    TaxCodeAuditEntity entity = new TaxCodeAuditEntity();
                    entity.setSellerNo(isSeller ? user.getLoginname() : null);
                    entity.setSellerName(isSeller ?  user.getUsername() : "非供应商修改");
                    entity.setItemNo(it.getItemNo());
                    entity.setItemName(it.getItemName());
                    TaxCodeEntity before = taxCodeConverter.cleanValue(it);
                    entity.setBefore(JSON.toJSONString(before));
                    entity.setAfter(JSON.toJSONString(map));
                    entity.setAuditStatus(isSeller ? 0 : 1);
                    entity.setAuditTime(isSeller ? null : new Date());
                    entity.setAuditOpinion(isSeller ? null : "无需审核");
                    entity.setSendStatus(0);
                    entity.setCreateUser(user.getLoginname());
                    entity.setUpdateUser(user.getLoginname());
                    taxCodeAuditService.save(entity);

                    //如果是供应商不需要修改，如果不是则直接修改
                    if (!isSeller) {
                        updateById(map);
                        updateTaxCodeForSettlementAndPreInvoice(entity);
                    }
                    return true;
                }).orElse(false);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean auditTaxCode(String id, Integer auditStatus, String opinion, UserEntity user) {
        TaxCodeAuditEntity entity = taxCodeAuditService.getById(id);
        if (null != entity) {
            TaxCodeEntity before = JSON.parseObject(entity.getBefore(), TaxCodeEntity.class);

            //更新审核状态
            entity.setAuditStatus(auditStatus);
            entity.setAuditOpinion(opinion);
            entity.setAuditTime(new Date());
            entity.setUpdateUser(user.getUserid().toString());
            taxCodeAuditService.updateById(entity);

            String content = "商品" + entity.getItemName() + "商品税编" + before.getGoodsTaxNo() + "修改申请，已审核通过。";
            if (auditStatus == 1) {
                String after = entity.getAfter();
                //修改TaxCode
                TaxCodeEntity taxCodeEntity = JSON.parseObject(after, TaxCodeEntity.class);
                updateById(taxCodeEntity);
                updateTaxCodeForSettlementAndPreInvoice(entity);
            } else {
                content = "商品" + entity.getItemName() + "商品税编" + before.getGoodsTaxNo() + "，审核不通过，原因：" + opinion;
            }
            //小铃铛消息通知
            TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
            //这里的userAccount是userName
            messagecontrolEntity.setUserAccount(entity.getCreateUser());
            messagecontrolEntity.setContent(content);
            messagecontrolEntity.setTitle("税编修改审核通知");
            commonMessageService.sendMessage(messagecontrolEntity);
        }
        return true;
    }

    public Page<TaxCodeLog> taxCodeLog(String itemNo, String itemName, String sellerName, String sellerNo, Integer auditStatus, Integer sendStatus, String begin, String end, String auditBegin, String auditEnd, Integer current, Integer size) {
        Page<TaxCodeAuditEntity> entities = taxCodeAuditService.query(itemNo, itemName, sellerName, sellerNo, auditStatus, sendStatus, begin, end, auditBegin, auditEnd, current, size);
        List<TaxCodeLog> records = entities.getRecords().parallelStream().map(it -> {
            TaxCodeLog taxCodeLog = taxCodeConverter.map(it);
            taxCodeLog.setBefore(JSON.parseObject(it.getBefore(), TaxCodeEntity.class));
            taxCodeLog.setAfter(JSON.parseObject(it.getAfter(), TaxCodeEntity.class));
            return taxCodeLog;
        }).collect(Collectors.toList());
        Page<TaxCodeLog> page = new Page<>(current, size, entities.getTotal());
        page.setRecords(records);
        return page;
    }

    public String taxCodeSync(Long id) {
        TaxCodeAuditEntity entity = taxCodeAuditService.getById(id);
        if (null != entity) {
            String after = entity.getAfter();
            //修改TaxCode
            TaxCodeEntity taxCodeEntity = JSON.parseObject(after, TaxCodeEntity.class);
            TaxCodeEntity byId = getById(taxCodeEntity.getId());
            R r = janusClient.syncTaxCode(byId);
            if ("1".equals(r.getCode()) && r.getMessage() != null && r.getMessage().contains("成功1条")) {
                entity.setSendStatus(1);
            } else {
                entity.setSendStatus(3);
            }
            taxCodeAuditService.updateById(entity);
            return r.getMessage();
        }
        return "未查询到数据！";
    }


    /**
     * @Description riversand税编手动同步
     * @Author pengtao
     * @return
    **/
    public R reTaxCodeSync(Long id)  {
        log.info("开始riversand税编同步,id:{}",id);
        LambdaQueryWrapper<TXfTaxCodeRiversandEntity> query = new LambdaQueryWrapper<>();
        query.eq(TXfTaxCodeRiversandEntity::getId, id);
        TXfTaxCodeRiversandEntity entity = tXfTaxCodeRiversandExtDao.selectOne(query);
        if (null != entity) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("调用riversand税编同步异常:",e);
            }
            R r = janusClient.riverSandSyncTaxCode(entity);
            UpdateWrapper<TXfTaxCodeRiversandEntity> refWrapper = new UpdateWrapper<>();
            refWrapper.eq(TXfTaxCodeRiversandEntity.ITEM_NO,entity.getItemNo());
            if("1".equals(r.getCode())){
                refWrapper.set(TXfTaxCodeRiversandEntity.STATUS,StatusEnum.STATUS_3.getCode());
            }else{
                if (r.getMessage().contains("存在")){
                    refWrapper.set(TXfTaxCodeRiversandEntity.STATUS,StatusEnum.STATUS_4.getCode());
                }else{
                    //原逻辑更新为-1，现在更新为5，失败原因后期可通过运维查询日志获取
                    refWrapper.set(TXfTaxCodeRiversandEntity.STATUS,StatusEnum.STATUS_5.getCode());
                }
            }
            refWrapper.set(TXfTaxCodeRiversandEntity.UPDATE_TIME,new Date());
            refWrapper.set(TXfTaxCodeRiversandEntity.UPDATE_USER,UserUtil.getUserId().toString());
            taxCodeRiversandService.update(refWrapper);//更新状态
            log.info("riversand的税编同步结果:{}",JSON.toJSON(r));
            return r;
        }
        return R.fail("未查询到数据！");
    }


    /**
     * @Description riversand税编手动同步
     * @Author pengtao
     * @return
     **/
    public R reTaxCodeSyncList(List<TXfTaxCodeRiversandEntity> entities)  {
        log.info("开始riversand税编同步,entities:{}",JSON.toJSON(entities));
        String successMsg = "同步成功";
        String failMsg = "同步失败";
        AtomicInteger successNum = new AtomicInteger();
        AtomicInteger failNum = new AtomicInteger();
        if(CollectionUtils.isNotEmpty(entities)){
            //分组
            List<List<TXfTaxCodeRiversandEntity>> entityListPar = ListUtils.partition(entities,20);
            for(List<TXfTaxCodeRiversandEntity> entityList:entityListPar){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("调用riversand税编同步异常:",e);
                }
                List<String> itemNos = entityList.stream().map(TXfTaxCodeRiversandEntity::getItemNo).distinct().collect(Collectors.toList());
                //税编批量同步
                R r = janusClient.riverSandSyncTaxCodeList(entityList);
                log.info("riversand的税编同步结果:{},itemNos:{}",JSON.toJSON(r),itemNos);
                if(StringUtils.equals("1",r.getCode())){
                    //批量更新为已同步
                    updateStatus(entityList,StatusEnum.STATUS_3.getCode());
                    entityList.forEach(x ->{ successNum.getAndIncrement(); });
                }else {
                    if (StringUtils.contains(r.getMessage(),"存在")){
                        updateStatus(entityList,StatusEnum.STATUS_4.getCode());
                    }else{
                        //原逻辑更新为-1，现在更新为5，失败原因后期可通过运维查询日志获取
                        updateStatus(entityList,StatusEnum.STATUS_5.getCode());
                    }
                    entityList.forEach(x ->{ failNum.getAndIncrement(); });
                }
            }
            return R.ok(true,successMsg+successNum+"个,"+failMsg+failNum+"个");
        }
        return R.fail("未查询到数据！");
    }

    public void updateStatus(List<TXfTaxCodeRiversandEntity> entities,String status){
        List<String> itemNos = entities.stream().map(TXfTaxCodeRiversandEntity::getItemNo).distinct().collect(Collectors.toList());
        UpdateWrapper<TXfTaxCodeRiversandEntity> refWrapper = new UpdateWrapper<>();
        refWrapper.in(TXfTaxCodeRiversandEntity.ITEM_NO,itemNos);
        refWrapper.set(TXfTaxCodeRiversandEntity.STATUS,status);
        refWrapper.set(TXfTaxCodeRiversandEntity.UPDATE_TIME,new Date());
        refWrapper.set(TXfTaxCodeRiversandEntity.UPDATE_USER,UserUtil.getUserId().toString());
        taxCodeRiversandService.update(refWrapper);//更新状态
    }

    public void updateTaxCodeForSettlementAndPreInvoice(TaxCodeAuditEntity entity) {
        TaxCodeEntity before = JSON.parseObject(entity.getBefore(), TaxCodeEntity.class);
        TaxCodeEntity after = JSON.parseObject(entity.getAfter(), TaxCodeEntity.class);
        if (!Objects.equals(before.getGoodsTaxNo(), after.getGoodsTaxNo())) {
            billDeductItemService.updateItem(entity.getItemNo(), entity.getSellerNo(), after.getGoodsTaxNo());
            settlementItemService.updateItem(entity.getItemNo(), entity.getSellerNo(), after.getGoodsTaxNo());
            preinvoiceService.updateItem(entity.getItemNo(), entity.getSellerNo(), after.getGoodsTaxNo());
        }
    }

    public List<String> matchTaxCode(String taxCode) {
        ArrayList<String> list = new ArrayList<>();
        list.add(taxCode);
        return list;
    }

    //全选最大数量
    private final Integer MAX_PAGE_SIZE = 5000;

    /**
     * @Description 分页查询
     * @Author pengtao
     * @return
     **/
    public PageResult<RiversandtTcDto> paged(RiversandTcQueryDto request) {
        //校验请求参数
        if(Objects.isNull(request)){
            throw new EnhanceRuntimeException("查询参数不允许为空");
        }
        //日期处理
        dealDate(request);
        //状态处理
        dealSyncStatus(request);

        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);

        log.info("riversand处理后的请求参数{}", JSON.toJSON(request));
        //总个数
        int count = queryCount(request);
        //获取数据
        List<TXfTaxCodeRiversandEntity> customsEntities = queryByPage(request);
        List<RiversandtTcDto> response = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(customsEntities)){
            for(TXfTaxCodeRiversandEntity customsEntitie:customsEntities){
                RiversandtTcDto customsDto = copyEntity(customsEntitie);
                //状态处理
                customsDto.setStatus(dealStatus(customsDto.getStatus()));
                response.add(customsDto);
            }
        }
        return PageResult.of(response,count,pageNo, request.getPageSize());
    }

    /**
     * @Description 请求日期处理
     * @Author pengtao
     * @return
     **/
    public void dealDate(RiversandTcQueryDto request){
        //日期校验
        if(StringUtils.isBlank(request.getCreateTimeStart())&&StringUtils.isNotBlank(request.getCreateTimeEnd())
                ||StringUtils.isNotBlank(request.getCreateTimeStart())&&StringUtils.isBlank(request.getCreateTimeEnd())){
            throw new EnhanceRuntimeException("新增日期开始和结束时间都需要有值");
        }else if(StringUtils.isBlank(request.getCreateTimeStart())&&StringUtils.isBlank(request.getCreateTimeEnd())){
            //时间为空默认为查询最近一年,结束日期需要加1天，数据库按照时分秒为0查询
            request.setCreateTimeEnd(DateUtils.dateToStrYM(DateUtils.addDate(new Date(),1)));
            request.setCreateTimeStart(DateUtils.getLastYearYM());
        }else{
            //日期格式处理，去掉-
            request.setCreateTimeStart(request.getCreateTimeStart().replace("-",""));
            request.setCreateTimeEnd(request.getCreateTimeEnd().replace("-",""));
            //结束日期加1天
            request.setCreateTimeEnd(DateUtils.dateToStrYM(DateUtils.addDate(DateUtils.strToDatePeriod(request.getCreateTimeEnd()),1)));
        }
    }


    /**
     * @Description 同步状态处理
     * @Author pengtao
     * @return
     **/
    public String dealStatus(String  status){
        if(StringUtils.isNotBlank(status)){
            if(Arrays.asList(StatusEnum.STATUS_1.getCode(),StatusEnum.STATUS_2.getCode(),StatusEnum.STATUS_3.getCode(),
                    StatusEnum.STATUS_4.getCode(),StatusEnum.STATUS_N1.getCode()).contains(status)){
                //已同步
                return SyncStatusEnum.STATUS_1.getCode();
            }else{
                return SyncStatusEnum.STATUS_0.getCode();
            }
        }
        return status;
    }

    /**
     * @Description 实体转换
     * @Author pengtao
     * @return
     **/
    public RiversandtTcDto copyEntity(TXfTaxCodeRiversandEntity customsEntity){
        RiversandtTcDto riversandtTcDto = new RiversandtTcDto();
        BeanUtil.copyProperties(customsEntity,riversandtTcDto);
        return riversandtTcDto;
    }

    /**
     * @Description 查询数量
     * @Author pengtao
     * @return
     **/
    public Integer queryCount(RiversandTcQueryDto request){
        return  tXfTaxCodeRiversandExtDao.count(request.getItemNo(),request.getStatus(),
                request.getCreateTimeStart(),request.getCreateTimeEnd());
    }

    /**
     * @Description 分页查询
     * @Author pengtao
     * @return
    **/
    public List<TXfTaxCodeRiversandEntity> queryByPage(RiversandTcQueryDto request) {
        return tXfTaxCodeRiversandExtDao.queryByPage(request.getPageNo(),request.getPageSize(),request.getItemNo(),
                request.getStatus(),request.getCreateTimeStart(),request.getCreateTimeEnd());
    }

    /**
     * @Description 导出
     * @Author pengtao
     * @return
    **/
    public R export(List<TXfTaxCodeRiversandEntity> resultList, RiversandTcValidSubmitRequest request) {
        String fileName = "Riversand新增税编";
        try {
            List<RiversandTcExportDto> exportDtos = new ArrayList<>();
            for(TXfTaxCodeRiversandEntity entity:resultList){
                RiversandTcExportDto dto = new RiversandTcExportDto();
                BeanUtil.copyProperties(entity,dto);

                if(Objects.nonNull(dto.getStatus())){
                    //状态处理
                    dto.setStatus(SyncStatusEnum.getValue(dealStatus(dto.getStatus())));
                }
                //新增日期格式调整为yyyy-MM-dd
                if(Objects.nonNull(entity.getCreateTime())){
                    dto.setCreateTime(DateUtils.format(entity.getCreateTime()));
                }
                //零税率标志
                if(Objects.nonNull(entity.getZeroTax())){
                    dto.setZeroTax(ZeroTaxEnum.getValue(entity.getZeroTax()));
                }
                //优惠政策标识
                if(Objects.nonNull(entity.getTaxPre())){
                    dto.setTaxPre(TaxPreEnum.getValue(entity.getTaxPre()));
                }
                exportDtos.add(dto);
            }

            excelExportUtils.messageExportOneSheet(exportDtos, RiversandTcExportDto.class, fileName, JSONObject.toJSONString(request), "sheet1");
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        }
        return R.ok();
    }

    /**
     * @Description 税编同步状态处理
     * @Author pengtao
     * @return
     **/
    public void dealSyncStatus(RiversandTcQueryDto request) {
        //海关缴款书号码处理
        String status = "0";
        if(StringUtils.isNotBlank(request.getStatus())){
            if(StringUtils.equals(SyncStatusEnum.STATUS_0.getCode(),request.getStatus())){
                status = StringUtils.join(Arrays.asList(StatusEnum.STATUS_0.getCode(),StatusEnum.STATUS_5.getCode()),",");
                request.setStatus(status);
            }else if(StringUtils.equals(SyncStatusEnum.STATUS_1.getCode(),request.getStatus())){
                status = StringUtils.join(Arrays.asList(StatusEnum.STATUS_N1.getCode(),StatusEnum.STATUS_1.getCode(),StatusEnum.STATUS_2.getCode(),
                        StatusEnum.STATUS_3.getCode(),StatusEnum.STATUS_4.getCode()),",");
                request.setStatus(status);
            }
            //补充单引号
            String[] itemNoSplit = request.getStatus().split(",");
            StringBuffer strBuf = new StringBuffer("");
            for(String str:itemNoSplit){
                if(!str.startsWith("'")&&!str.endsWith("'")){
                    strBuf.append("'").append(str.trim()).append("',");
                }
            }
            if(strBuf.length()>1){
                strBuf.deleteCharAt(strBuf.length()-1);
            }
            request.setStatus(strBuf.toString());
        }
    }
}
