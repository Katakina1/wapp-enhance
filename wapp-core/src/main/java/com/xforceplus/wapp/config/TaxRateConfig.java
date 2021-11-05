package com.xforceplus.wapp.config;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
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
    private List<BigDecimal > res  ;
    public BigDecimal getNextTaxRate(BigDecimal taxRate) {
        return taxRateMap.get(BigDecimal.valueOf(taxRate.doubleValue()));
    }

    public Map<BigDecimal,BigDecimal> bulidTaxRateMap(BigDecimal taxRate) {
        Map<BigDecimal, BigDecimal> tmp = new HashMap<>();
        for (int i = 0; i < res.size(); i++) {
            if (i == 0) {
                tmp.put(taxRate, res.get(i));
            }
            if (i == res.size() - 1) {
                tmp.put(res.get(i), null);
                break;
            }
            if (taxRate.compareTo(res.get(i)) == 0) {
                continue;
            }
            tmp.put(res.get(i), res.get(i + 1));
        }
        return tmp;
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(taxRateStr)) {
            taxRateStr = "0.13,0.11,0.09,0.06,0.05,0.03,0.01,0.00";
        }
        res = Arrays.asList(taxRateStr.split(",")).stream().map(x -> new BigDecimal(x)).unordered().collect(Collectors.toList());
    }
//
//    public static void main(String[] ags) {
//        TaxRateConfig taxRateConfig = new TaxRateConfig();
//        taxRateConfig.init();
//        Map<BigDecimal, BigDecimal> tmp = taxRateConfig.bulidTaxRateMap(BigDecimal.valueOf(0.06));
//        System.out.println(tmp);
//    }
}
