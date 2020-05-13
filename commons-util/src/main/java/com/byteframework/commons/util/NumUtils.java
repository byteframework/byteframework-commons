package com.byteframework.commons.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumUtils {
    /**
     * 格式化数字为百分数，将在最后添加"%",
     *
     * @param num
     * @param precision 保留小数的位数
     * @return
     */
    public static String formatPercent(double num, int precision) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(precision);
        return nf.format(num);
    }

    public static Double round(Double num, int precision) {
        BigDecimal bd = new BigDecimal(num.toString());
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static boolean isDecimal(String str) {
        if (str == null) return false;
        if (str.startsWith("-")) str = str.substring(1);
        if ("".equals(str)) return false;
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

    public static boolean isTime(String str) {
        if (str == null) return false;
        if ("".equals(str)) return false;
        if (str.startsWith("24")) {
            return str.equals("24:00");
        }
        String reg = "^(20|21|22|23|[0-1]\\d):[0-5]\\d?$";
        return str.matches(reg);
    }


    /**
     * 二次指数平滑法求预测值
     *
     * @param list    基础数据集合
     * @param per     未来第几期
     * @param modulus 平滑系数
     * @return 预测值
     */
    public static Double getDoubleExpect(List<Double> list, int per, Double modulus) {
        if (list.size() < 2 || modulus <= 0 || modulus >= 1) {
            return null;
        }
        Double modulusLeft = 1 - modulus;
        Double lastIndex = list.get(0);
        Double lastSecIndex = list.get(0);
        for (Double data : list) {
            lastIndex = modulus * data + modulusLeft * lastIndex;
            lastSecIndex = modulus * lastIndex + modulusLeft * lastSecIndex;
        }
        Double a = 2 * lastIndex - lastSecIndex;
        Double b = (modulus / modulusLeft) * (lastIndex - lastSecIndex);
        return a + b * per;
    }

    /**
     * 二次指数平滑法求预测值
     *
     * @param list    基础数据集合
     * @param per     未来第几期
     * @param modulus 平滑系数
     * @return 预测值
     */
    public static Long getLongExpect(List<Long> list, int per, Double modulus) {
        if (list.size() < 2 || modulus <= 0 || modulus >= 1) {
            return null;
        }
        Double modulusLeft = 1 - modulus;
        Long lastIndex = list.get(0);
        Long lastSecIndex = list.get(0);
        for (Long data : list) {
            lastIndex = (long) (modulus * data + modulusLeft * lastIndex);
            lastSecIndex = (long) (modulus * lastIndex + modulusLeft * lastSecIndex);
        }
        Long a = 2 * lastIndex - lastSecIndex;
        Long b = (long) ((modulus / modulusLeft) * (lastIndex - lastSecIndex));
        return a + b * per;
    }


    public static void main(String[] args) {
        //System.out.println(getDoubleExpect());
        //System.out.println(getLongExpect());
        System.out.println(isTime("00:00"));
    }

    private static Long getLongExpect() {
        List<Long> list = new ArrayList<Long>();
        list.add(2995L);
        list.add(2999L);
        list.add(2991L);
        list.add(2897L);
        list.add(2995L);
        list.add(2992L);
        list.add(2996L);
        list.add(2994L);
        list.add(2892L);
        list.add(500L);
        Collections.reverse(list);
        return getLongExpect(list, 1, 0.8);
    }

    private static Double getDoubleExpect() {
        List<Double> list = new LinkedList<Double>();
        list.add(253993d);
        list.add(289665d);
        list.add(342785d);
        list.add(384763d);
        list.add(428964d);
        list.add(470614d);
        list.add(530217d);
        list.add(620206d);
        list.add(688212d);
        list.add(746422d);
        list.add(809592d);
        list.add(791376d);
        list.add(772682d);
        list.add(806048d);
        list.add(860855d);
        list.add(996633d);
        list.add(1092883d);
        list.add(1172596d);
        list.add(1245356d);
        list.add(1326094d);
        list.add(1378717d);
        list.add(1394413d);
        list.add(1478573d);
        list.add(1534122d);
        list.add(1608150d);
        return getDoubleExpect(list, 1, 0.6);
    }

}
