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
         final int first = name.indexOf("*");
         final int length = name.length();
         if (first > -1 && length > first+1) {
             int end = name.indexOf("*", first + 1);
             if (end > -1 && length > end) {
                 List<String> res = new ArrayList<>();
                 final String shortName = name.substring(first + 1, end);
                 final String itemName = name.substring(end+1);
                 res.add(shortName);
                 res.add(itemName);
                 return res;
              }
         }
        return Collections.EMPTY_LIST;
    }
}
