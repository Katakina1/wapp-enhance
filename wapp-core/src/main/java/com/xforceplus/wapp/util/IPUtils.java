package com.xforceplus.wapp.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 类描述：
 *
 * @ClassName IPUtils
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/26 12:40
 */
public class IPUtils {
    private static String IP = StringUtils.EMPTY;
    static {
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            IP = ip4.getHostAddress();
        } catch (UnknownHostException e) {
            IP = System.currentTimeMillis() + RandomStringUtils.random(4);
        }

    }
    public static String getLocalIp()   {
        return IP;
    }
}
