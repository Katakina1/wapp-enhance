package com.xforceplus.wapp.modules.taxcode.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xforceplus.wapp.config.WappHostConfig;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.modules.rednotification.util.HttpUtils;
import com.xforceplus.wapp.repository.dao.TXfTaxCodeRiversandDao;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.xforceplus.wapp.client.WappHostClient.*;

@Slf4j
@Service
public class TaxCodeRiversandServiceImpl extends ServiceImpl<TXfTaxCodeRiversandDao, TXfTaxCodeRiversandEntity> {


    @Autowired
    private WappHostConfig config;

    @Value("${riverSand.latestTime:}")
    private String latestTime;
    @Value("${riverSand.maxResetNum:1000}")
    private Integer maxResetNum;
    @Value("${riverSand.pageSize:50}")
    private Integer pageSize;


    //接口获取riversand税编信息
    public void getRiverSandTaxCode(String time, Consumer<List<TXfTaxCodeRiversandEntity>> consumer) {
        Map<String, String> headers = new HashMap<>();
        headers.put(C_NAME, config.getCName());
        headers.put(ALOHA_APP_NAME, config.getAppName());
        headers.put(ACCESS_TOKEN, config.getAppAccessToken());
        int totalPage = 1;
        int pageNum = 1;
        List<TXfTaxCodeRiversandEntity> list = Lists.newArrayList();
        Map<String, Object> requestmap = new HashMap<>();
        requestmap.put("types", new String[]{"item"});
        requestmap.put("latestTime", latestTime);
        if (StringUtils.isNotBlank(time)) {
            requestmap.put("latestTime", time);
        }
        requestmap.put("pageSize", pageSize);
        do {
            requestmap.put("pageIndex", pageNum);
            try {
                log.info("请求riversand--开始{}", new Gson().toJson(requestmap));
                String response = HttpUtils.doPostJsonSkipSsl(config.getBaseUrl() + config.getTaxCodeUrl(), new Gson().toJson(requestmap), headers);
                log.info("请求riversand--结果{}", response);
                if (StringUtils.isNotEmpty(response)) {
                    JsonObject responsejson = new JsonParser().parse(response).getAsJsonObject();
                    if ("200".equals(responsejson.get("code").getAsString()) && responsejson.get("data").getAsJsonObject().get("content").getAsJsonArray().size() > 0) {
                        totalPage = responsejson.get("data").getAsJsonObject().get("totalPages").getAsInt();
                        JsonArray content = responsejson.get("data").getAsJsonObject().get("content").getAsJsonArray();
                        for (int i = 0; i < content.size(); i++) {
                            JsonObject object = content.get(i).getAsJsonObject();
                            JsonObject attributes = object.get("attributes").getAsJsonObject();
                            if (attributes.get("taxCodeCommodityTaxCode") == null ||
                                    attributes.get("taxCodeCommodityTaxCode").isJsonNull() ||
                                    attributes.get("itemNbr") == null ||
                                    attributes.get("itemNbr").isJsonNull()) {
                                continue;
                            }
                            TXfTaxCodeRiversandEntity entity = mapEntity(object);
                            this.saveOrUpdate(entity);
                            list.add(entity);
                        }

                    }
                }
            } catch (Exception e) {
                log.error("获取riversand税编信息异常：{}", e.getMessage(), e);
            }
            if (list.size() >= maxResetNum) {
                consumer.accept(list);
                list.clear();
            }
            pageNum++;
        } while (totalPage > pageNum);
        if (list.size() > 0) {
            consumer.accept(list);
        }
        DateTime newDate = DateUtil.offsetDay(new Date(), -1);
        latestTime = DateUtil.format(newDate, "yyyy-MM-dd HH:mm:ss");
    }

    private static TXfTaxCodeRiversandEntity mapEntity(JsonObject content) {
        JsonObject attributes = content.get("attributes").getAsJsonObject();
        TXfTaxCodeRiversandEntity taxCodeRiversandEntity = new TXfTaxCodeRiversandEntity();
        taxCodeRiversandEntity.setGoodsTaxNo(attributes.get("taxCodeCommodityTaxCode") != null ? attributes.get("taxCodeCommodityTaxCode").getAsString().split(" - ")[0] : "");
        taxCodeRiversandEntity.setItemNo(attributes.get("itemNbr") != null ? attributes.get("itemNbr").getAsString() : "");
        taxCodeRiversandEntity.setItemName(attributes.has("item1Desc") && !attributes.get("item1Desc").isJsonNull() ? attributes.get("item1Desc").getAsString() : "");
        taxCodeRiversandEntity.setZeroTax(attributes.has("zeroTaxRateIdentifier") && !attributes.get("zeroTaxRateIdentifier").isJsonNull() ? attributes.get("zeroTaxRateIdentifier").getAsString() : "");
        taxCodeRiversandEntity.setTaxPre(attributes.has("preferentialFavorablePolicyIdentifier") && !attributes.get("preferentialFavorablePolicyIdentifier").isJsonNull() ? attributes.get("preferentialFavorablePolicyIdentifier").getAsString() : "");
        taxCodeRiversandEntity.setTaxPreCon((attributes.has("preferentialFavorablePolicyContent") && !attributes.get("preferentialFavorablePolicyContent").isJsonNull()) ? attributes.get("preferentialFavorablePolicyContent").getAsString() : "");
        taxCodeRiversandEntity.setTaxRate(attributes.has("vatOut") && !attributes.get("vatOut").isJsonNull() ? attributes.get("vatOut").getAsBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP) : null);
        taxCodeRiversandEntity.setUpdateTime(new Date());
        taxCodeRiversandEntity.setId(IdGenerator.generate());
        taxCodeRiversandEntity.setCreateTime(new Date());
        taxCodeRiversandEntity.setStatus("0");//默认
        taxCodeRiversandEntity.setDeleteFlag(content.has("blobIsDeleted") && !content.get("blobIsDeleted").isJsonNull() ? content.get("blobIsDeleted").getAsString() : "");
        return taxCodeRiversandEntity;
    }


}
