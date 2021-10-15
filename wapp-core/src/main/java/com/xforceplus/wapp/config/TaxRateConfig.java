package com.xforceplus.wapp.config;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
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
    @Value("${taxRateSet}")
    private List<String> taxRateSet;
    private Map<BigDecimal, BigDecimal> taxRateMap = Maps.newHashMap();

    public BigDecimal getNextTaxRate(BigDecimal taxRate) {
        return taxRateMap.get(taxRate);
    }

    @PostConstruct
    public void init() {
        List<BigDecimal> res = taxRateSet.stream().map(x -> new BigDecimal(x)).sorted().collect(Collectors.toList());
        for (int i = 0; i < res.size(); i++) {
            if (i == res.size() - 1) {
                taxRateMap.put(res.get(i), null);
                break;
            }
            taxRateMap.put(res.get(i), res.get(i + 1));
        }
    }
}
