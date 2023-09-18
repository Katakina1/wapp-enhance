package com.xforceplus.wapp.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.rednotification.util.HttpUtils;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xforceplus.apollo.client.http.HttpClientFactory;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class JanusClient {
    private final HttpClientFactory httpClientFactory;
    @Value("${wapp.integration.action.tax-code}")
    private String taxCodeAction;

    @Value("${wapp.integration.action.send-tax-code}")
    private String sendTaxCodeAction;

    @Value("${wapp.integration.sign.tax-code}")
    private String taxCodeSign;

    @Value("${wapp.integration.sign.send-tax-code}")
    private String sendTaxCodeSign;

    @Value("${wapp.integration.action.sync-tax-code}")
    private String syncTaxCodeAction;

    @Value("${wapp.integration.tenant-id:1203939049971830784}")
    public String tenantId;

    @Value("${wapp.integration.tenant-code}")
    private String tenantCode;

    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Value("${wapp.integration.host.http}")
    private String janusPath;

    @Value("${wapp.integration.authentication}")
    public String authentication;


    private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private final Map<String, String> defaultHeader = new HashMap<>();
    private final Gson gson = new Gson();
    private final DefaultIdentifierGenerator generator;

    public JanusClient(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
        this.generator = new DefaultIdentifierGenerator();
    }

    @PostConstruct
    public void init() {
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("tenant-id", tenantId);
        defaultHeader.put("tenantId", tenantId);
        defaultHeader.put("tenantCode", tenantId);
        defaultHeader.put("accept-encoding", "");
    }

    /**
     * 参考Jira PRJCENTER-7793
     * @param taxCode
     * @param keyWord
     * @return
     */
    public Either<String, List<TaxCodeBean>> searchTaxCode(String taxCode, String keyWord) {
        if (StringUtils.isBlank(taxCode) && StringUtils.isBlank(keyWord)) {
            return Either.left("参数不能全为空");
        }
        try {
            String serialNo = generator.nextId(null).toString();
            Map<String,String> header=new HashMap<>(defaultHeader);
            header.put("uiaSign", taxCodeSign);
            header.put("serialNo", serialNo);
            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            if (StringUtils.isNotBlank(taxCode)) {
                paramMeterMap.put("code", taxCode);
            }
            if (StringUtils.isNotBlank(keyWord)) {
//                paramMeterMap.put("taxCodeKeyWord", keyWord);
            	paramMeterMap.put("shortName", keyWord);
            }
            paramMeterMap.put("appId", "walmart");
            paramMeterMap.put("node", 1);
            log.info("获取中台税编,集成流水号[serialNo]:{},taxCodeAction:{},param:{},header:{}", serialNo, taxCodeAction,paramMeterMap, JSON.toJSONString(header));
            final String get = httpClientFactory.get(taxCodeAction, paramMeterMap, header);
            log.info("获取中台税编结果:{}", get);
            TaxCodeRsp taxCodeRsp = gson.fromJson(get, TaxCodeRsp.class);
            if (Objects.nonNull(taxCodeRsp) && "TWTXZZ100".equalsIgnoreCase(taxCodeRsp.getCode())) {
                List<TaxCodeBean> codeBeans = taxCodeRsp.getResult().stream().map(TaxCodeRsp.ResultBean::getData)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                //2022-07-07 过滤
                if(StringUtils.isBlank(taxCode) && StringUtils.isNotBlank(keyWord)) {
                	codeBeans = codeBeans.stream().filter(item->StringUtils.equalsIgnoreCase(keyWord, item.getTaxShortName())).collect(Collectors.toList());
                }
                return Either.right(codeBeans);
            }
            log.error("获取中台税编错误:{}", get);
            return Either.left("获取中台税编错误");
        } catch (IOException e) {
            log.error("获取税编结果异常:" + e.getMessage(), e);
            return Either.left("获取税编结果异常");
        }
    }

    /**
     * @Description 税编同步更新到3.0平台
     * @return
    **/
    public R sendTaxCode(TXfTaxCodeRiversandEntity tXfTaxCodeRiversandEntity){
        if(tXfTaxCodeRiversandEntity ==null || StringUtils.isBlank(tXfTaxCodeRiversandEntity.getItemNo()) || StringUtils.isBlank(tXfTaxCodeRiversandEntity.getGoodsTaxNo())){
            return R.fail("商品编号或税收分类编码为空");
        }
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("systemOrig", "walmart_wapp");

            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("tenant-id", tenantCode);
            headerMap.put("rpcType", "http");
            String serialNo = UUID.randomUUID().toString();
            headerMap.put("serialNo", serialNo);
            headerMap.put("customerNo", customerNo);
            headerMap.put("timestamp", ft.format(new Date()));
            headerMap.put("uiaSign", sendTaxCodeSign);
            headerMap.put("Authentication", authentication);
            headerMap.put("Action", sendTaxCodeAction);
            headerMap.put("systemOrig", "walmart_wapp");

            HashMap<String, Object> paramMeterMap = Maps.newHashMap();
            paramMeterMap.put("goodsTaxNo",tXfTaxCodeRiversandEntity.getGoodsTaxNo());
//            paramMeterMap.put("",tXfTaxCodeRiversandEntity.getItemNo());
            paramMeterMap.put("taxConvertCode",tXfTaxCodeRiversandEntity.getItemNo());
            if (StringUtils.isNotBlank(tXfTaxCodeRiversandEntity.getItemName())) {
                paramMeterMap.put("itemName",tXfTaxCodeRiversandEntity.getItemName());
            }
            if (StringUtils.isNotBlank(tXfTaxCodeRiversandEntity.getTaxPre())) {
                paramMeterMap.put("taxPre",tXfTaxCodeRiversandEntity.getTaxPre());
            }
            if (StringUtils.isNotBlank(tXfTaxCodeRiversandEntity.getTaxPreCon())) {
                paramMeterMap.put("taxPreCon",tXfTaxCodeRiversandEntity.getTaxPreCon());
            }
            if (tXfTaxCodeRiversandEntity.getTaxRate() !=null) {
                paramMeterMap.put("taxRate",tXfTaxCodeRiversandEntity.getTaxRate());
            }

            if (StringUtils.isNotBlank(tXfTaxCodeRiversandEntity.getZeroTax())) {
                paramMeterMap.put("zeroTax",tXfTaxCodeRiversandEntity.getZeroTax());
            }
            paramMeterMap.put("tenantName", "沃尔玛");
            paramMeterMap.put("tenantCode", "Walmart");
            List<Map> list=new ArrayList<>();
            list.add(paramMeterMap);
            log.info("上传3.0平台税编,集成流水号[serialNo]:{},taxCodeAction:{},header:{},param:{}", serialNo, sendTaxCodeAction,headerMap, gson.toJson(paramMeterMap));
            final String post=HttpUtils.doPostJson(janusPath,gson.toJson(list),headerMap,params);
            log.info("上传3.0平台税编结果:{}", post);
            R taxCodeRsp = gson.fromJson(post, R.class);
            return taxCodeRsp;
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
    }

    public R syncTaxCode(TaxCodeEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.getItemNo())) {
            return R.fail("商品编号或税收分类编码为空");
        }
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("serialNo", entity.getItemNo());
            headerMap.put("Authentication", authentication);
            headerMap.put("Action", syncTaxCodeAction);

            TaxCodeSyncBean bean = new TaxCodeSyncBean();
            bean.setQuantityUnit(Objects.toString(entity.getQuantityUnit(), StringUtils.EMPTY));
            bean.setItemSpec(Objects.toString(entity.getItemSpec(), StringUtils.EMPTY));
            bean.setItemName(Objects.toString(entity.getItemName(), StringUtils.EMPTY));
            //必填
            bean.setTaxConvertCode(entity.getItemNo());
            //必填
            bean.setGoodsTaxNo(entity.getGoodsTaxNo());
            bean.setTaxPre(Objects.toString(entity.getTaxPre(), StringUtils.EMPTY));
            bean.setTaxPreCon(Objects.toString(entity.getTaxPreCon(), StringUtils.EMPTY));
            bean.setTaxRate(entity.getTaxRate().toPlainString());
            bean.setZeroTax(Objects.toString(entity.getZeroTax(), StringUtils.EMPTY));
            //必填
            bean.setTenantCode("Walmart");
            bean.setItemCode(Objects.toString(entity.getItemCode(), StringUtils.EMPTY));
            //必填
            bean.setStandardItemName(entity.getStandardItemName());
            //必填
            bean.setTenantName("Walmart");

            log.info("上传3.0平台税编,集成流水号[serialNo]:{},taxCodeAction:{},header:{},param:{}", entity.getItemNo(), syncTaxCodeAction, headerMap, gson.toJson(bean));
            final String post = HttpUtils.doPostJson(janusPath, gson.toJson(Lists.newArrayList(bean)), headerMap);
            log.info("上传3.0平台税编结果:{}", post);
            R taxCodeRsp = gson.fromJson(post, R.class);
            return taxCodeRsp;
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * @Description RiverSand税编同步3.0
     * @Author pengtao
     * @return
    **/
    public R riverSandSyncTaxCode(TXfTaxCodeRiversandEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.getItemNo())) {
            return R.fail("商品编号或税收分类编码为空");
        }
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("serialNo", entity.getItemNo());
            headerMap.put("Authentication", authentication);
            headerMap.put("Action", syncTaxCodeAction);

            TaxCodeSyncBean bean = new TaxCodeSyncBean();
            bean.setQuantityUnit(Objects.toString(entity.getQuantityUnit(), StringUtils.EMPTY));
            bean.setItemSpec(Objects.toString(entity.getItemSpec(), StringUtils.EMPTY));
            bean.setItemName(Objects.toString(entity.getItemName(), StringUtils.EMPTY));
            //必填
            bean.setTaxConvertCode(entity.getItemNo());
            //必填
            bean.setGoodsTaxNo(entity.getGoodsTaxNo());
            bean.setTaxPre(Objects.toString(entity.getTaxPre(), StringUtils.EMPTY));
            bean.setTaxPreCon(Objects.toString(entity.getTaxPreCon(), StringUtils.EMPTY));
            bean.setTaxRate(entity.getTaxRate().toPlainString());
            bean.setZeroTax(Objects.toString(entity.getZeroTax(), StringUtils.EMPTY));
            //必填
            bean.setTenantCode("Walmart");
            bean.setItemCode(Objects.toString(entity.getItemCode(), StringUtils.EMPTY));
            //必填
            bean.setStandardItemName("");
            //必填
            bean.setTenantName("Walmart");

            log.info("上传3.0平台税编,集成流水号[serialNo]:{},taxCodeAction:{},header:{},param:{}", entity.getItemNo(), syncTaxCodeAction, headerMap, gson.toJson(bean));
            final String post = HttpUtils.doPostJson(janusPath, gson.toJson(Lists.newArrayList(bean)), headerMap);
            log.info("上传3.0平台税编结果:{}", post);
            R taxCodeRsp = gson.fromJson(post, R.class);
            return taxCodeRsp;
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * @Description RiverSand税编同步3.0 批量
     * @Author pengtao
     * @return
     **/
    public R riverSandSyncTaxCodeList(List<TXfTaxCodeRiversandEntity> entities) {
        Map<String, String> headerMap = new HashMap<>();
        try {
            headerMap.put("Authentication", authentication);
            headerMap.put("Action", syncTaxCodeAction);
            List<TaxCodeSyncBean> requestList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(entities)){
                headerMap.put("serialNo", entities.stream().findFirst().get().getItemNo());
                entities.forEach(entity ->{
                    TaxCodeSyncBean bean = new TaxCodeSyncBean();
                    bean.setQuantityUnit(Objects.toString(entity.getQuantityUnit(), StringUtils.EMPTY));
                    bean.setItemSpec(Objects.toString(entity.getItemSpec(), StringUtils.EMPTY));
                    bean.setItemName(Objects.toString(entity.getItemName(), StringUtils.EMPTY));
                    //必填
                    bean.setTaxConvertCode(entity.getItemNo());
                    //必填
                    bean.setGoodsTaxNo(entity.getGoodsTaxNo());
                    bean.setTaxPre(Objects.toString(entity.getTaxPre(), StringUtils.EMPTY));
                    bean.setTaxPreCon(Objects.toString(entity.getTaxPreCon(), StringUtils.EMPTY));
                    bean.setTaxRate(entity.getTaxRate().toPlainString());
                    bean.setZeroTax(Objects.toString(entity.getZeroTax(), StringUtils.EMPTY));
                    //必填
                    bean.setTenantCode("Walmart");
                    bean.setItemCode(Objects.toString(entity.getItemCode(), StringUtils.EMPTY));
                    //必填
                    bean.setStandardItemName("");
                    //必填
                    bean.setTenantName("Walmart");
                    requestList.add(bean);
                });
        }else{
            return R.fail("传参有误请检查后重试");
        }
        log.info("上传3.0平台税编,集成流水号[serialNo]:{},taxCodeAction:{},header:{},param:{}", entities.stream().findFirst().get().getItemNo(), syncTaxCodeAction, headerMap, gson.toJson(requestList));
        final String post = HttpUtils.doPostJson(janusPath, gson.toJson(requestList), headerMap);
        log.info("上传3.0平台税编结果:{}", post);
        R taxCodeRsp = gson.fromJson(post, R.class);
        return taxCodeRsp;
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }
}
