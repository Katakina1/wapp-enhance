package com.xforceplus.wapp.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类描述：
 *
 * @ClassName ItemNameUtils
 * @Description TODO
 * @Author ZZW
 * @Date 2021/11/16 20:07
 */
public class ItemNameUtils {
     public static List<String> splitItemName(String name) {
        if (StringUtils.isEmpty(name)) {
            return Collections.EMPTY_LIST;
        }

        List<String> res = new ArrayList<>();
        String[] str = name.split("\\*");
        if (str.length == 3) {
            res.add(str[1]);
            res.add(str[2]);
            return res;
        }
        return Collections.EMPTY_LIST;
    }
}
