package com.byteframework.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    private static Pattern ptipv4 = Pattern.compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");

    public static boolean isMobileNO(String mobile) {
        if (StringUtils.isBlank(mobile)) return false;
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(15[0-9])|(18[0,0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    public static boolean isHourMinute(String time) {
        if (StringUtils.isBlank(time)) return false;
        String rep = "(([0-1][0-9])|2[0-3]):[0-5][0-9]";
        Pattern p = Pattern.compile(rep);
        return p.matcher(time).matches();
    }

    public static boolean isMac(String mac) {
        String patternMac = "^([0-9A-F]{12})$";
        return Pattern.compile(patternMac).matcher(mac.toUpperCase()).find();
    }

    public static boolean isIpv4(String ip) {
        if (StringUtils.isBlank(ip)) return false;
        return ptipv4.matcher(ip).matches();
    }

    public static void main(String[] args) {
        System.out.println(isHourMinute("00:00"));
    }

}
