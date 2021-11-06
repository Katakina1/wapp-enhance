package com.xforceplus.wapp.config;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
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

    public Map<BigDecimal,BigDecimal> bulidTaxRateMap(BigDecimal taxRate) {
        Map<BigDecimal, BigDecimal> tmp = new HashMap<>();
        List<BigDecimal> tmpRes = new ArrayList<>(res.size());
        tmpRes.add(taxRate);
        for (int i = 0; i < res.size(); i++) {
            if (taxRate.compareTo(res.get(i)) == 0) {
                continue;
            }
            tmpRes.add(  res.get(i));
        }
        for (int i = 0; i < tmpRes.size(); i++) {
            if (i == tmpRes.size() - 1) {
                tmp.put(tmpRes.get(i), null);
                break;
            }
            tmp.put(tmpRes.get(i), tmpRes.get(i + 1));
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

//    public static void main(String[] ags) {
//        TaxRateConfig taxRateConfig = new TaxRateConfig();
//        taxRateConfig.init();
//        Map<BigDecimal, BigDecimal> tmp = taxRateConfig.bulidTaxRateMap(BigDecimal.valueOf(0.06));
//        System.out.println(tmp);
//        System.out.println(tmp.get(new BigDecimal("0.06")));
//        System.out.println(tmp.get(  BigDecimal.valueOf(0.13)));
//
//        tmp = taxRateConfig.bulidTaxRateMap(BigDecimal.valueOf(0.13));
//        System.out.println(tmp.get(new BigDecimal("0.06")));
//        System.out.println(tmp.get(  BigDecimal.valueOf(0.13)));
//    }
}
