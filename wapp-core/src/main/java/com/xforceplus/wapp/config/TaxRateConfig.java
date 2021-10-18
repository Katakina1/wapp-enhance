package com.xforceplus.wapp.config;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类描述：
 *
 * @ClassName TaxRateConfig
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/14 15:44
 */
@Component
public class TaxRateConfig {
    @Value("${taxRateSet:''}")
    private String taxRateStr;
    private Map<BigDecimal, BigDecimal> taxRateMap = Maps.newHashMap();

    public BigDecimal getNextTaxRate(BigDecimal taxRate) {
        return taxRateMap.get(BigDecimal.valueOf(taxRate.doubleValue()));
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(taxRateStr)) {
            taxRateStr = "0.13,0.11,0.09,0.06,0.05,0.03,0.01,0.00";
        }
        List<BigDecimal> res = Arrays.asList(taxRateStr.split(",")).stream().map(x -> new BigDecimal(x)).unordered().collect(Collectors.toList());
        for (int i = 0; i < res.size(); i++) {
            if (i == res.size() - 1) {
                taxRateMap.put(res.get(i), null);
                break;
            }
            taxRateMap.put(res.get(i), res.get(i + 1));
        }
    }
}
